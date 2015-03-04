package com.totop.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.totop.adapter.GoodsAdapter;
import com.totop.bean.Goods;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    ListView mListView;
    List<Goods> mList = new ArrayList<Goods>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        mListView = (ListView) findViewById(R.id.listView_goods);
        mListView.setAdapter(new GoodsAdapter(this,mList));
    }


    private void initData(){
        Goods goods1 = new Goods();
        Goods goods2 = new Goods();
        mList.add(goods1);
        mList.add(goods2);
    }
}
