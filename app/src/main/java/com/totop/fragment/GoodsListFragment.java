package com.totop.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.totop.App;
import com.totop.activity.GoodsDetailActivity;
import com.totop.activity.R;
import com.totop.manager.GoodsManager;
import com.totop.model.DataRes;
import com.totop.model.Goods;
import com.totop.view.adapter.GoodsAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import cn.trinea.android.common.util.ListUtils;

public class GoodsListFragment extends Fragment {

    @InjectView(R.id.listView_goods)PullToRefreshListView mPullRefreshListView;

    private Context mContext;
    private GoodsAdapter mGoodsAdapter;
    private OnFragmentSettingListener mListener;
    /**
     * 当前页数
     */
    private int currentPageNo = 1;

    /** 上新-价格 */
    SparseArray<List<Goods>> priceNewSparseArray = new SparseArray<List<Goods>>();
    /** 上新-对象 */
    SparseArray<List<Goods>> objectNewSparseArray = new SparseArray<List<Goods>>();
    /** 人气-价格 */
    SparseArray<List<Goods>> priceHotSparseArray = new SparseArray<List<Goods>>();
    /** 人气-对象 */
    SparseArray<List<Goods>> objectHotSparseArray = new SparseArray<List<Goods>>();

    /**
     * 当前排序方式 0 按时间排序 1 按照热度排序
     */
    private int currentSortType = GoodsManager.SORT_BY_NEW;
    /**
     * 当前模式：价格模式或对象模式
     */
    private String currentModeType = GoodsManager.MODE_PRICE;
    /**
     * 当前模式的值
     */
    private int currentModeValue = 1;
    /**
     * 当前产品集合
     */
    List<Goods> currentList = new ArrayList<Goods>();

    SparseArray<List<Goods>> currentSparseArray = priceNewSparseArray;

    private boolean isRefresh = false;

    public static GoodsListFragment newInstance() {
        GoodsListFragment fragment = new GoodsListFragment();
        return fragment;
    }

    public GoodsListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goods_list, container, false);

        ButterKnife.inject(this,view);
        mContext = getActivity();
        mGoodsAdapter = new GoodsAdapter(mContext, currentList);

        mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

                // 获取刷新时间，设置刷新时间格式
                String label = DateUtils.formatDateTime(App.getContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("最后加载时间:" + label);

                //TODO 如何处理刷新的时机
                isRefresh = true;
                new GetDataTask().execute();
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                currentPageNo++;
                new GetDataTask().execute();
            }
        });

        mPullRefreshListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                //Toast.makeText(mContext, "已经没有数据了!", Toast.LENGTH_SHORT).show();
            }
        });

        ListView mListView = mPullRefreshListView.getRefreshableView();
        mListView.setAdapter(mGoodsAdapter);
        mListView.setItemsCanFocus(true);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GoodsAdapter.ViewHolder viewHolder = (GoodsAdapter.ViewHolder) view.getTag();
                Intent intent = new Intent(mContext,GoodsDetailActivity.class);
                intent.putExtra(GoodsDetailActivity.EXTRA_IMAGE_URL,viewHolder.goods.link);
                startActivity(intent);
            }
        });
        //加载数据
        new GetDataTask().execute();
        return view;
    }

    private class GetDataTask extends AsyncTask<Integer, Void, DataRes<Goods>> {

        @Override
        protected DataRes<Goods> doInBackground(Integer... params) {
            //int pageNo,int sortType,String typeKey,int typeValue
            int pageNo = currentPageNo;
            if(isRefresh){
                pageNo = 1;
            }
            //TODO 进度条
            return GoodsManager.findGoods(pageNo,currentSortType,currentModeType,currentModeValue);
        }

        @Override
        protected void onPostExecute(DataRes<Goods> result) {
            mPullRefreshListView.onRefreshComplete();
            if(result.success){

                List<Goods> list = result.data;

                //以下逻辑缓存数据--TODO 切换时执行此逻辑
                List<Goods> cacheList = currentSparseArray.get(currentModeValue);
                if (cacheList == null){
                    cacheList = new ArrayList<Goods>();
                    currentSparseArray.put(currentModeValue,cacheList);
                }

                if(isRefresh){ //刷新数据
                    isRefresh = false;
                    List<Goods> tempList = new ArrayList<Goods>();
                    for(Goods goods: list){
                        if (!cacheList.contains(goods)){
                            tempList.add(goods);
                        }
                    }
                    if(tempList.size() > 0){
                        cacheList.addAll(0, tempList);
                        currentList.clear();
                        currentList.addAll(cacheList);
                        mGoodsAdapter.notifyDataSetChanged();
                    }
                }else{
                    if(list != null){
                        //去重复
                        ListUtils.addDistinctList(cacheList,result.data);
                        currentList.clear();
                        currentList.addAll(cacheList);
                        mGoodsAdapter.notifyDataSetChanged();
                    }

                    //是否有下一页
                    if( list == null || list.size() < GoodsManager.PAGE_COUNT){
                        mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    }
                }
            }else{
                Toast.makeText(App.getContext(),"网络请求失败，请稍后再试",Toast.LENGTH_SHORT).show();
            }
            //TODO 关闭进度条
            super.onPostExecute(result);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (OnFragmentSettingListener) activity;
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    @OnClick(R.id.button_setting)
    void setting(){
        if(mListener != null){
            mListener.toggle();
        }
    }

    @OnCheckedChanged(R.id.radiobutton_new_goods)
    void changeNewGoods(){
        Log.e("Genius","changeNewGoods");
        currentSortType = GoodsManager.SORT_BY_NEW;
        if (currentModeType == GoodsManager.MODE_PRICE){
            currentSparseArray = priceNewSparseArray;
        }else{
            currentSparseArray = objectNewSparseArray;
        }

        changeView();
    }
    @OnCheckedChanged(R.id.radiobutton_hot_goods)
    void changeHotGoods(){
        Log.e("Genius","changeHotGoods");
        currentSortType = GoodsManager.SORT_BY_HOT;
        if (currentModeType == GoodsManager.MODE_PRICE){
            currentSparseArray = priceHotSparseArray;
        }else{
            currentSparseArray = objectHotSparseArray;
        }
        changeView();
    }

    //TODO 切换价格/对象的事件
    @OnCheckedChanged(R.id.layout_bottom_radio)
    void changePriceOrObjectd(RadioButton rb,boolean flag){
        switch (rb.getId()){
            case R.id.radio_level_one:
                currentModeValue = 1;
                break;
            case R.id.radio_level_two:
                currentModeValue = 2;
                break;
            case R.id.radio_level_three:
                currentModeValue = 3;
                break;
            case R.id.radio_level_four:
                currentModeValue = 4;
                break;
        }
        changeView();
    }

    //TODO 切换模式的事件
    void changeMode(){

        if(currentModeType == GoodsManager.MODE_OBJECT){
            //价格模式
            currentModeType = GoodsManager.MODE_PRICE;
            currentModeValue = 1;//赋初始值
            if(currentSortType == GoodsManager.SORT_BY_HOT){
                currentSparseArray = priceHotSparseArray;
            }else{
                currentSparseArray = priceNewSparseArray;
            }
        }else {
            currentModeType = GoodsManager.MODE_OBJECT;
            currentModeValue = 1;//赋初始值
            if(currentSortType == GoodsManager.SORT_BY_HOT){
                currentSparseArray = objectHotSparseArray;
            }else{
                currentSparseArray = objectNewSparseArray;
            }
        }
        changeView();

    }

    private void changeView(){
        List<Goods> cacheList = currentSparseArray.get(currentModeValue);
        if (cacheList == null){
            new GetDataTask().execute();
        }else{
            currentList.clear();
            currentList.addAll(cacheList);
            mGoodsAdapter.notifyDataSetChanged();
        }
    }
}
