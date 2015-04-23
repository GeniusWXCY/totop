package com.totop.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.melnykov.fab.FloatingActionButton;
import com.totop.App;
import com.totop.activity.GoodsDetailActivity;
import com.totop.activity.HistoryActivity;
import com.totop.activity.R;
import com.totop.activity.SearchActivity;
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
import tr.xip.errorview.ErrorView;

public class GoodsListFragment extends Fragment {

    @InjectView(R.id.listView_goods)PullToRefreshListView mPullRefreshListView;
    @InjectView(R.id.layout_bottom_radio) RadioGroup mRadioGroup;
    @InjectView(R.id.progressBar) ProgressBar mProgressBar;
    @InjectView(R.id.error_view) ErrorView mErrorView;
    @InjectView(R.id.empty_view)View mEmptyView;
    @InjectView(R.id.fab_top) FloatingActionButton mFabTop;
    @InjectView(R.id.fab_type) FloatingActionButton mFabType;


    private Context mContext;
    private GoodsAdapter mGoodsAdapter;
    private OnHomeFragmentListener mListener;
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
    private int currentModeValue = MODE_VALUE_PRICE_INIT;
    /**
     * 当前产品集合
     */
    private List<Goods> currentList = new ArrayList<Goods>();

    private SparseArray<List<Goods>> currentSparseArray = priceNewSparseArray;

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

                //TODO 如何处理刷新的时机--刷新数据用另外的接口
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
                Intent intent = new Intent(mContext, GoodsDetailActivity.class);
                intent.putExtra(GoodsDetailActivity.EXTRA_IMAGE_URL, viewHolder.goods.link);
                intent.putExtra(GoodsDetailActivity.EXTRA_GOODS_SOURCE,viewHolder.goods.sourceName);
                startActivity(intent);
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem == 0){
                    //滑到顶部
                    mFabTop.hide();
                }else{
                    mFabTop.show();
                }
            }
        });

        mErrorView.setOnRetryListener(new ErrorView.RetryListener() {

            @Override
            public void onRetry() {
                toggleErrorView(false);
                new GetDataTask().execute();
            }
        });

        //设置透明度
        mFabTop.getBackground().setAlpha(200);
        mFabType.getBackground().setAlpha(200);

        //加载数据
        loadView();
        return view;
    }

    private void toggleErrorView(boolean isShow){
        if(isShow){
            mErrorView.setVisibility(View.VISIBLE);
            mPullRefreshListView.setVisibility(View.GONE);
        }else{
            mErrorView.setVisibility(View.GONE);
            mPullRefreshListView.setVisibility(View.VISIBLE);
        }
    }

    private class GetDataTask extends AsyncTask<Integer, Void, DataRes<Goods>> {

        @Override
        protected void onPreExecute() {
            //判断是否有网络连接
            mProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected DataRes<Goods> doInBackground(Integer... params) {
            //int pageNo,int sortType,String typeKey,int typeValue
            int pageNo = currentPageNo;
            if(isRefresh){
                pageNo = 1;
            }
            //TODO 异常处理 参考开源
            try {
                return GoodsManager.findGoods(pageNo,currentSortType,currentModeType,currentModeValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(DataRes<Goods> result) {

            mPullRefreshListView.onRefreshComplete();
            if(result != null && result.success){

                List<Goods> list = result.data;

                //以下逻辑缓存数据
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

                        if(currentList.isEmpty()){
                            mEmptyView.setVisibility(View.VISIBLE);
                            mPullRefreshListView.setVisibility(View.GONE);
                        }

                        mGoodsAdapter.notifyDataSetChanged();
                    }

                    //是否有下一页
                    if( list == null || list.size() < GoodsManager.PAGE_COUNT){
                        mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    }
                }
            }else{
                //网络请求失败
                toggleErrorView(true);
            }
            super.onPostExecute(result);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (OnHomeFragmentListener) activity;
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

    @OnClick(R.id.button_search)
    void search(){
        Intent intent = new Intent(mContext, SearchActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.button_history)
    void history(){
        Intent intent = new Intent(mContext, HistoryActivity.class);
        startActivity(intent);
    }

    @OnCheckedChanged(R.id.radiobutton_new_goods)
    void changeNewGoods(RadioButton rb , boolean isCheck){
        if(isCheck){
            currentSortType = GoodsManager.SORT_BY_NEW;
            if (currentModeType == GoodsManager.MODE_PRICE){
                currentSparseArray = priceNewSparseArray;
            }else{
                currentSparseArray = objectNewSparseArray;
            }

            loadView();
        }
    }
    @OnCheckedChanged(R.id.radiobutton_hot_goods)
    void changeHotGoods(RadioButton rb , boolean isCheck){
        if(isCheck){
            currentSortType = GoodsManager.SORT_BY_HOT;
            if (currentModeType == GoodsManager.MODE_PRICE){
                currentSparseArray = priceHotSparseArray;
            }else{
                currentSparseArray = objectHotSparseArray;
            }
            loadView();
        }
    }

    //TODO
    public static final int MODE_VALUE_PRICE_INIT = 1;
    public static final int MODE_VALUE_OBJECT_INIT = 5;

    /**
     * 切换价格(对象)的事件
     */
    @OnCheckedChanged({R.id.radio_level_one,R.id.radio_level_two,R.id.radio_level_three,R.id.radio_level_four})
    void changePriceOrObjectd(RadioButton rb,boolean isCheck){
        if(isCheck){
            switch (rb.getId()){
                case R.id.radio_level_one:
                    currentModeValue = currentModeType == GoodsManager.MODE_PRICE?1:5;
                    break;
                case R.id.radio_level_two:
                    currentModeValue = currentModeType == GoodsManager.MODE_PRICE?2:6;
                    break;
                case R.id.radio_level_three:
                    currentModeValue = currentModeType == GoodsManager.MODE_PRICE?3:7;
                    break;
                case R.id.radio_level_four:
                    currentModeValue = currentModeType == GoodsManager.MODE_PRICE?4:8;
                    break;
            }
            loadView();
        }
    }

    private void loadView(){
        toggleErrorView(false);
        mEmptyView.setVisibility(View.GONE);
        mPullRefreshListView.setVisibility(View.VISIBLE);
        List<Goods> cacheList = currentSparseArray.get(currentModeValue);
        if (cacheList == null || cacheList.isEmpty()){
            new GetDataTask().execute();
        }else{
            currentList.clear();
            currentList.addAll(cacheList);
            mGoodsAdapter.notifyDataSetChanged();
        }
    }

    private void changeCategoryBar(){

        RadioButton radioButton1 = (RadioButton) mRadioGroup.findViewById(R.id.radio_level_one);
        RadioButton radioButton2 = (RadioButton) mRadioGroup.findViewById(R.id.radio_level_two);
        RadioButton radioButton3 = (RadioButton) mRadioGroup.findViewById(R.id.radio_level_three);
        RadioButton radioButton4 = (RadioButton) mRadioGroup.findViewById(R.id.radio_level_four);

        //TODO 动画效果
        if(currentModeType == GoodsManager.MODE_OBJECT){
            radioButton1.setText("老人");
            radioButton2.setText("小孩");
            radioButton3.setText("女人");
            radioButton4.setText("男人");
        }else{
            radioButton1.setText("9块9");
            radioButton2.setText("19块9");
            radioButton3.setText("29块9");
            radioButton4.setText("39块9");
        }
        mRadioGroup.clearCheck();
        radioButton1.setChecked(true);
    }

    @OnClick(R.id.fab_type)
    public void changeType(ImageButton view){

        if(currentModeType == GoodsManager.MODE_OBJECT){
            //切换成价格模式
            currentModeType = GoodsManager.MODE_PRICE;
            currentModeValue = MODE_VALUE_PRICE_INIT;//赋初始值
            if(currentSortType == GoodsManager.SORT_BY_HOT){
                currentSparseArray = priceHotSparseArray;
            }else{
                currentSparseArray = priceNewSparseArray;
            }
            view.setImageResource(R.drawable.fab_money);
        }else {
            currentModeType = GoodsManager.MODE_OBJECT;
            currentModeValue = MODE_VALUE_OBJECT_INIT;//赋初始值
            if(currentSortType == GoodsManager.SORT_BY_HOT){
                currentSparseArray = objectHotSparseArray;
            }else{
                currentSparseArray = objectNewSparseArray;
            }
            view.setImageResource(R.drawable.fab_group);
        }
        //切换价格-对象的栏目,默认选中第一个栏目
        changeCategoryBar();

    }

    @OnClick(R.id.fab_top)
    public void moveToTop(ImageButton view){
        mPullRefreshListView.getRefreshableView().setSelection(0);
    }

}
