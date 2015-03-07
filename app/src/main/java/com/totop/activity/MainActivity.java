package com.totop.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.totop.DataFactory;
import com.totop.adapter.GoodsAdapter;
import com.totop.bean.Goods;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;


public class MainActivity extends FragmentActivity implements OnMenuItemClickListener,OnMenuItemLongClickListener {

    @InjectView(R.id.listView_goods)ListView mListView;

    List<Goods> mList = new ArrayList<Goods>();

    private FragmentManager fragmentManager;
    private DialogFragment mMenuDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        mListView = (ListView) findViewById(R.id.listView_goods);
        fragmentManager = getSupportFragmentManager();
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance((int) getResources().getDimension(R.dimen.tool_bar_height), getMenuObjects());

        mList = DataFactory.make();

        mListView.setAdapter(new GoodsAdapter(this,mList));
        mListView.setItemsCanFocus(true);
        /*mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,GoodsDetailActivity.class);
                startActivity(intent);
            }
        });*/
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
        if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
            mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
        }
    }

    @OnItemClick(R.id.listView_goods) void bkItemClick(AdapterView<?> parent, View view, int position, long id){

        GoodsAdapter.ViewHolder viewHolder = (GoodsAdapter.ViewHolder) view.getTag();
        Intent intent = new Intent(MainActivity.this,GoodsDetailActivity.class);
        intent.putExtra(GoodsDetailActivity.EXTRA_IMAGE_URL,viewHolder.goods.url);
        startActivity(intent);
    }

    @Override
    public void onMenuItemLongClick(View view, int i) {

    }
}
