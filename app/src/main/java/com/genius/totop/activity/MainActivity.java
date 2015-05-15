package com.genius.totop.activity;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.Toast;

import com.genius.totop.BuildConfig;
import com.genius.totop.R;
import com.genius.totop.fragment.ContactUsFragment;
import com.genius.totop.fragment.GoodsListFragment;
import com.genius.totop.fragment.HelpFragment;
import com.genius.totop.fragment.OnHomeFragmentListener;
import com.genius.totop.manager.VersionManager;
import com.genius.totop.model.Item;
import com.genius.totop.utils.Constants;
import com.genius.totop.utils.EventCode;
import com.genius.totop.utils.UMengShareUtils;
import com.qq.e.appwall.GdtAppwall;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import cn.trinea.android.common.util.DownloadManagerPro;
import de.greenrobot.event.EventBus;


public class MainActivity extends BaseMenuActivity implements OnHomeFragmentListener {

    private FragmentTransaction mFragmentTransaction;
    private FragmentManager mFragmentManager;

    private String mCurrentFragmentTag;

    private static final String STATE_CURRENT_FRAGMENT = "com.genius.totop.activity.MainActivity";
    private long exitTime = 0;
    private GdtAppwall appwall;
    private CompleteReceiver  completeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentManager = getSupportFragmentManager();
        if (savedInstanceState != null) {
            mCurrentFragmentTag = savedInstanceState.getString(STATE_CURRENT_FRAGMENT);
        }else{
            openHome();
        }

        mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
        //TODO 以下代码的作用？
        mMenuDrawer.setOnDrawerStateChangeListener(new MenuDrawer.OnDrawerStateChangeListener() {
            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                if (newState == MenuDrawer.STATE_CLOSED) {
                    commitTransactions();
                }
            }

            @Override
            public void onDrawerSlide(float openRatio, int offsetPixels) {
                // Do nothing
            }
        });
        VersionManager.getInstance(this).checkVersion(false);

        //友盟分享相关
        UMengShareUtils.addCustomPlatforms(this);
        appwall = new GdtAppwall(this, Constants.GDT_APP_ID,Constants.GDT_POST_ID, BuildConfig.DEBUG);

        completeReceiver = new CompleteReceiver();
        registerReceiver(completeReceiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        //发送指令，退出欢迎界面
        EventBus.getDefault().post(EventCode.COMMAND_FINISH_WELCOME);

    }

    private void openHome(){
        mCurrentFragmentTag = ((Item) mAdapter.getItem(0)).mTitle;
        attachFragment(mMenuDrawer.getContentContainer().getId(),getFragment(mCurrentFragmentTag),mCurrentFragmentTag);
        commitTransactions();
    }

    @Override
    protected void onMenuItemClicked(int position, Item item) {

        String title = item.mTitle;

        if(!title.equals(mCurrentFragmentTag)){
            if(title.equals(getString(R.string.menu_share))){
                UMengShareUtils.share(this);
            }else if(title.equals(getString(R.string.menu_version))){
                VersionManager.getInstance(this).checkVersion(true);
            }else if(title.equals(getString(R.string.menu_hot))){
                appwall.doShowAppWall();
                Toast.makeText(this,"热门",Toast.LENGTH_SHORT).show();
            }else {
                if (mCurrentFragmentTag != null){
                    detachFragment(getFragment(mCurrentFragmentTag));
                }
                attachFragment(mMenuDrawer.getContentContainer().getId(), getFragment(title), title);
                mCurrentFragmentTag = title;
            }
        }
        mMenuDrawer.closeMenu();
    }

    @Override
    protected int getDragMode() {
        return MenuDrawer.MENU_DRAG_WINDOW;
    }

    @Override
    protected Position getDrawerPosition() {
        return Position.LEFT;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_CURRENT_FRAGMENT, mCurrentFragmentTag);
    }

    private void commitTransactions() {
        if (mFragmentTransaction != null && !mFragmentTransaction.isEmpty()) {
            mFragmentTransaction.commit();
            mFragmentTransaction = null;
        }
    }

    private FragmentTransaction ensureTransaction() {
        if (mFragmentTransaction == null) {
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        }

        return mFragmentTransaction;
    }

    private void attachFragment(int layout, Fragment f, String tag) {
        if (f != null) {
            if (f.isDetached()) {
                ensureTransaction();
                mFragmentTransaction.attach(f);
            } else if (!f.isAdded()) {
                ensureTransaction();
                mFragmentTransaction.add(layout, f, tag);
            }
        }
    }

    private void detachFragment(Fragment f) {
        if (f != null && !f.isDetached()) {
            ensureTransaction();
            mFragmentTransaction.detach(f);
        }
    }

    private Fragment getFragment(String tag) {
        Fragment f = mFragmentManager.findFragmentByTag(tag);
        if (f == null) {
            if(tag.equals(getString(R.string.menu_home))){
                f = GoodsListFragment.newInstance();
            }else if(tag.equals(getString(R.string.menu_contact))){
                f = ContactUsFragment.newInstance();
            }else if(tag.equals(getString(R.string.menu_help))){
                f = HelpFragment.newInstance();
            }
        }
        return f;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            final int drawerState = mMenuDrawer.getDrawerState();
            if (drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING) {
                mMenuDrawer.closeMenu();
            }else{
                //首页
                String title = getString(R.string.menu_home);
                if(!title.equals(mCurrentFragmentTag)){
                    back();
                }else{
                    exit();
                }
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }

    @Override
    public void toggle() {
        mMenuDrawer.toggleMenu();
    }

    @Override
    public void back(){
        if (mCurrentFragmentTag != null){
            detachFragment(getFragment(mCurrentFragmentTag));
        }
        openHome();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(completeReceiver);
    }

    class CompleteReceiver extends BroadcastReceiver {

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onReceive(Context context, Intent intent) {
            // get complete download id
            long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if(completeDownloadId == Constants.DOWNLOAD_ID){

                Uri downloadFileUri = null;
                DownloadManager downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
                    downloadFileUri = downloadManager.getUriForDownloadedFile(completeDownloadId);
                }else{//兼容低版本的下载安装--TODO 未测试
                    DownloadManagerPro downloadManagerPro = new DownloadManagerPro(downloadManager);
                    String fileName = downloadManagerPro.getFileName(completeDownloadId);
                    downloadFileUri = Uri.parse(fileName);
                }

                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(install);
            }
        }
    };
}
