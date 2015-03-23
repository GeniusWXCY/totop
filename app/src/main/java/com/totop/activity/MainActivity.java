package com.totop.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.totop.DataFactory;
import com.totop.view.adapter.GoodsAdapter;
import com.totop.model.Goods;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends FragmentActivity implements OnMenuItemClickListener,OnMenuItemLongClickListener {

    @InjectView(R.id.listView_goods)PullToRefreshListView mPullRefreshListView;

    List<Goods> mList = new ArrayList<Goods>();

    private FragmentManager mFragmentManager;
    private DialogFragment mMenuDialogFragment;
    private Context mContext;
    private GoodsAdapter mGoodsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);
        mContext = this;
        mList = DataFactory.make();
        mGoodsAdapter = new GoodsAdapter(this,mList);

        mFragmentManager = getSupportFragmentManager();
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance((int) getResources().getDimension(R.dimen.tool_bar_height), getMenuObjects());

        mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

                // 获取刷新时间，设置刷新时间格式
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("最后加载时间:" + label);

                new GetDataTask().execute();
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

                mGoodsAdapter.notifyDataSetChanged();
                mPullRefreshListView.onRefreshComplete();
                //没有数据后
                mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            }
        });

        mPullRefreshListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                Toast.makeText(mContext, "已经没有数据了!", Toast.LENGTH_SHORT).show();
            }
        });

        ListView mListView = mPullRefreshListView.getRefreshableView();
        mListView.setAdapter(mGoodsAdapter);
        mListView.setItemsCanFocus(true);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GoodsAdapter.ViewHolder viewHolder = (GoodsAdapter.ViewHolder) view.getTag();
                Intent intent = new Intent(MainActivity.this,GoodsDetailActivity.class);
                intent.putExtra(GoodsDetailActivity.EXTRA_IMAGE_URL,viewHolder.goods.url);
                startActivity(intent);
            }
        });
    }

    private List<MenuObject> getMenuObjects() {
        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setResource(R.drawable.ic_menu_close);

        MenuObject all = new MenuObject("全部");
        all.setResource(R.drawable.ic_menu_all);

        MenuObject yifu = new MenuObject("衣服");
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.ic_menu_yifu);
        yifu.setBitmap(b);

        MenuObject xiezi = new MenuObject("鞋子");
        BitmapDrawable bd = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_menu_xiezi));
        xiezi.setDrawable(bd);

        MenuObject baobao = new MenuObject("包包");
        baobao.setResource(R.drawable.ic_menu_baobao);

        MenuObject meizhuang = new MenuObject("美妆");
        meizhuang.setResource(R.drawable.ic_menu_meizhuang);

        MenuObject other = new MenuObject("其他");
        other.setResource(R.drawable.ic_menu_other);

        menuObjects.add(close);
        menuObjects.add(all);
        menuObjects.add(yifu);
        menuObjects.add(xiezi);
        menuObjects.add(baobao);
        menuObjects.add(meizhuang);
        menuObjects.add(other);
        return menuObjects;
    }

    @Override
    public void onMenuItemClick(View view, int position) {
        Toast.makeText(this, "Clicked on position: " + position, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.button_left_open) void bkSearch(){
        if (mFragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
            mMenuDialogFragment.show(mFragmentManager, ContextMenuDialogFragment.TAG);
        }
    }

    @Override
    public void onMenuItemLongClick(View view, int i) {

    }

    private class GetDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // Simulates a background job.
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            mList.add(mList.get(0));
            mGoodsAdapter.notifyDataSetChanged();
            mPullRefreshListView.onRefreshComplete();
            super.onPostExecute(result);
        }
    }
}
