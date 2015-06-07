package com.genius.totop.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
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

import com.easyandroidanimations.library.FlipVerticalAnimation;
import com.genius.totop.App;
import com.genius.totop.R;
import com.genius.totop.activity.GoodsDetailActivity;
import com.genius.totop.activity.HistoryActivity;
import com.genius.totop.activity.SearchActivity;
import com.genius.totop.manager.CacheDataManager;
import com.genius.totop.manager.GoodsManager;
import com.genius.totop.model.DatasRes;
import com.genius.totop.model.Goods;
import com.genius.totop.model.Type;
import com.genius.totop.view.adapter.GoodsAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.melnykov.fab.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import cn.trinea.android.common.util.ListUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import tr.xip.errorview.ErrorView;

public class GoodsListFragment extends Fragment {

    @InjectView(R.id.listView_goods)PullToRefreshListView mPullRefreshListView;
    @InjectView(R.id.layout_bottom_radio) RadioGroup mRadioGroup;
    @InjectView(R.id.progressBar) ProgressBar mProgressBar;
    @InjectView(R.id.error_view) ErrorView mErrorView;
    @InjectView(R.id.empty_view)View mEmptyView;
    @InjectView(R.id.fab_top) FloatingActionButton mFabTop;
    @InjectView(R.id.fab_type) FloatingActionButton mFabType;
    @InjectView(R.id.layout_bottom_bar) View mBottomBar;

    public static final String TAG = "GoodsListFragment";

    private Context mContext;
    private GoodsAdapter mGoodsAdapter;
    private OnHomeFragmentListener mListener;

    /** 上新-价格 */
    SparseArray<List<Goods>> mPriceNewSparseArray = new SparseArray<List<Goods>>();
    /** 上新-对象 */
    SparseArray<List<Goods>> mObjectNewSparseArray = new SparseArray<List<Goods>>();
    /** 人气-价格 */
    SparseArray<List<Goods>> mPriceHotSparseArray = new SparseArray<List<Goods>>();
    /** 人气-对象 */
    SparseArray<List<Goods>> mObjectHotSparseArray = new SparseArray<List<Goods>>();

    /**
     * 当前排序方式 0 按时间排序 1 按照热度排序
     */
    private int mCurrentSortType = GoodsManager.SORT_BY_NEW;
    /**
     * 当前模式：价格模式或对象模式
     */
    private String mCurrentModeType = GoodsManager.MODE_PRICE;
    /**
     * 当前模式的值:初始值为第一价位
     */
    private int mCurrentModeValue = CacheDataManager.mCategory.price.types.get(0).id;
    /**
     * 当前产品集合
     */
    private List<Goods> mCurrentList = new ArrayList<Goods>();
    /**
     * 当前产品列表
     */
    private SparseArray<List<Goods>> mCurrentSparseArray = mPriceNewSparseArray;

    /**
     * 以下两个对象为当前产品的更新时间--用于刷新数据，刷新接口传递该数据，服务端返回时间大于该值的数据。每次刷新操作后，更新改值
     */
    private SparseArray<Long> mNewUpdateTimeSparssArray = new SparseArray<>();
    private SparseArray<Long> mHotUpdateTimeSparssArray = new SparseArray<>();

    /**
     * 以下两个对象为当前产品的加载时间--用于获取第N页时候，传递该数据，服务端返回时间小于该值的数据
     */
    private SparseArray<Long> mNewLoadTimeSparssArray = new SparseArray<>();
    private SparseArray<Long> mHotLoadTimeSparssArray = new SparseArray<>();

    private SparseArray<Integer> mNewPageNoSparseArray = new SparseArray<>();
    private SparseArray<Integer> mHotPageNoSparseArray = new SparseArray<>();

    public static GoodsListFragment newInstance() {
        return new GoodsListFragment();
    }

    public GoodsListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goods_list, container, false);

        ButterKnife.inject(this,view);
        mContext = getActivity();
        initCurrentData();
        mGoodsAdapter = new GoodsAdapter(mContext, mCurrentList, mCurrentSortType);

        mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

                // 获取刷新时间，设置刷新时间格式
                String label = DateUtils.formatDateTime(App.getContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("最后加载时间:" + label);
                executeRefreshData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //获取当前页
                executeLoadData();
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
                intent.putExtra(GoodsDetailActivity.EXTRA_ICON_URL, viewHolder.goods.icon);
                startActivity(intent);
                GoodsManager.executeAfterView(viewHolder.goods,mContext);
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
                executeLoadData();
            }
        });

        //设置透明度
        mFabTop.getBackground().setAlpha(200);
        mFabType.getBackground().setAlpha(200);

        //加载数据
        loadView();
        return view;
    }

    /**
     * 将上新/人气的每个type对应的页码和加载时间，更新时间初始化
     */
    private void initCurrentData() {

        try {
            List<Type> priceTypes = CacheDataManager.mCategory.price.types;
            List<Type> objectTypes = CacheDataManager.mCategory.object.types;

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            //初始时间设置为100年以后
            Date date  = df.parse("2115-01-01");
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            long timestamp = cal.getTimeInMillis();


            for(Type type : priceTypes){
                mNewPageNoSparseArray.put(type.id, 1);
                mHotPageNoSparseArray.put(type.id, 1);

                mHotLoadTimeSparssArray.put(type.id,timestamp);
                mHotUpdateTimeSparssArray.put(type.id,timestamp);

                mNewLoadTimeSparssArray.put(type.id,timestamp);
                mNewUpdateTimeSparssArray.put(type.id,timestamp);
            }

            for (Type type : objectTypes){
                mNewPageNoSparseArray.put(type.id, 1);
                mHotPageNoSparseArray.put(type.id, 1);

                mHotLoadTimeSparssArray.put(type.id,timestamp);
                mHotUpdateTimeSparssArray.put(type.id,timestamp);

                mNewLoadTimeSparssArray.put(type.id,timestamp);
                mNewUpdateTimeSparssArray.put(type.id,timestamp);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
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

    public void executeLoadData(){

        //判断是否有网络连接
        mProgressBar.setVisibility(View.VISIBLE);

        final int pageNo = getCurrentPageNoSparseArray().get(mCurrentModeValue);

        long loadTime = getCurrentLoadTimeSparseArray().get(mCurrentModeValue);

        Log.i(TAG,"当前页码：" + pageNo);
        Log.i(TAG,"当前模式：" + mCurrentModeValue);
        Log.i(TAG, "当前loadTime：" + loadTime);

        GoodsManager.findGoods(pageNo, mCurrentSortType, mCurrentModeType, mCurrentModeValue, loadTime,new Callback<DatasRes<Goods>>() {
            @Override
            public void success(DatasRes<Goods> goodsDatasRes, Response response) {

                mPullRefreshListView.onRefreshComplete();

                if(goodsDatasRes != null) {

                    List<Goods> list = goodsDatasRes.data;

                    //如果为第一页，赋值updateTime和loadTime
                    if(pageNo == 1){
                        getCurrentUpdateTimeSparseArray().put(mCurrentModeValue,goodsDatasRes.serverTime);
                        getCurrentLoadTimeSparseArray().put(mCurrentModeValue,goodsDatasRes.serverTime);
                    }

                    //以下逻辑缓存数据
                    List<Goods> cacheList = mCurrentSparseArray.get(mCurrentModeValue);
                    if (cacheList == null) {
                        cacheList = new ArrayList<Goods>();
                        mCurrentSparseArray.put(mCurrentModeValue, cacheList);
                    }

                    if (list != null) {
                        //去重复
                        ListUtils.addDistinctList(cacheList, goodsDatasRes.data);
                        mCurrentList.clear();
                        mCurrentList.addAll(cacheList);

                        if (mCurrentList.isEmpty()) {
                            mEmptyView.setVisibility(View.VISIBLE);
                            mPullRefreshListView.setVisibility(View.GONE);
                        }

                        mGoodsAdapter.notifyDataSetChanged();
                    }

                    //是否有下一页
                    if (list == null || list.size() < GoodsManager.PAGE_COUNT) {
                        mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    }else{
                        getCurrentPageNoSparseArray().put(mCurrentModeValue, pageNo + 1);
                    }
                }
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {
                toggleErrorView(true);
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    public void executeRefreshData(){

        mProgressBar.setVisibility(View.VISIBLE);

        final int pageNo = getCurrentPageNoSparseArray().get(mCurrentModeValue);

        long updateTime = getCurrentUpdateTimeSparseArray().get(mCurrentModeValue);

        GoodsManager.refreshGoods(pageNo, mCurrentSortType, mCurrentModeType, mCurrentModeValue, updateTime,new Callback<DatasRes<Goods>>() {

            @Override
            public void success(DatasRes<Goods> goodsDatasRes, Response response) {

                mPullRefreshListView.onRefreshComplete();

                if(goodsDatasRes != null) {

                    List<Goods> list = goodsDatasRes.data;

                    //更新updateTime
                    getCurrentUpdateTimeSparseArray().put(mCurrentModeValue,goodsDatasRes.serverTime);

                    //以下逻辑缓存数据
                    List<Goods> cacheList = mCurrentSparseArray.get(mCurrentModeValue);

                    List<Goods> tempList = new ArrayList<Goods>();
                    for (Goods goods : list) {
                        if (!cacheList.contains(goods)) {
                            tempList.add(goods);
                        }
                    }
                    if (tempList.size() > 0) {
                        cacheList.addAll(0, tempList);
                        //如果是人气，要根据人气值进行降序 --TODO 未测试
                        if(mCurrentSortType == GoodsManager.SORT_BY_HOT){
                            Collections.sort(cacheList, new Comparator<Goods>() {
                                @Override
                                public int compare(Goods lhs, Goods rhs) {
                                    if(Float.valueOf(lhs.heat) - Float.valueOf(rhs.heat) > 0 ){
                                        return 1;
                                    }else {
                                        return -1;
                                    }
                                }
                            });
                        }
                        mCurrentList.clear();
                        mCurrentList.addAll(cacheList);
                        mGoodsAdapter.notifyDataSetChanged();
                    }
                }
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {
                toggleErrorView(true);
                mProgressBar.setVisibility(View.GONE);
            }
        });
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

            //每次切换后将列表设置为可加载更多
            mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

            mCurrentSortType = GoodsManager.SORT_BY_NEW;
            mGoodsAdapter.setCurrentSortType(mCurrentSortType);
            if (mCurrentModeType == GoodsManager.MODE_PRICE){
                mCurrentSparseArray = mPriceNewSparseArray;
            }else{
                mCurrentSparseArray = mObjectNewSparseArray;
            }

            loadView();
        }
    }
    @OnCheckedChanged(R.id.radiobutton_hot_goods)
    void changeHotGoods(RadioButton rb , boolean isCheck){
        if(isCheck){

            //每次切换后将列表设置为可加载更多
            mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

            mCurrentSortType = GoodsManager.SORT_BY_HOT;
            mGoodsAdapter.setCurrentSortType(mCurrentSortType);
            if (mCurrentModeType == GoodsManager.MODE_PRICE){
                mCurrentSparseArray = mPriceHotSparseArray;
            }else{
                mCurrentSparseArray = mObjectHotSparseArray;
            }
            loadView();
        }
    }

    /**
     * 切换价格(对象)的事件
     */
    @OnCheckedChanged({R.id.radio_level_one,R.id.radio_level_two,R.id.radio_level_three,R.id.radio_level_four})
    void changePriceOrObjectd(RadioButton rb,boolean isCheck){

        if(isCheck){

            //每次切换后将列表设置为可加载更多
            mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

            List<Type> priceTypes = CacheDataManager.mCategory.price.types;
            List<Type> objectTypes = CacheDataManager.mCategory.object.types;

            switch (rb.getId()){
                case R.id.radio_level_one:
                    mCurrentModeValue = mCurrentModeType == GoodsManager.MODE_PRICE?priceTypes.get(0).id:objectTypes.get(0).id;
                    break;
                case R.id.radio_level_two:
                    mCurrentModeValue = mCurrentModeType == GoodsManager.MODE_PRICE?priceTypes.get(1).id:objectTypes.get(1).id;
                    break;
                case R.id.radio_level_three:
                    mCurrentModeValue = mCurrentModeType == GoodsManager.MODE_PRICE?priceTypes.get(2).id:objectTypes.get(2).id;
                    break;
                case R.id.radio_level_four:
                    mCurrentModeValue = mCurrentModeType == GoodsManager.MODE_PRICE?priceTypes.get(3).id:objectTypes.get(3).id;
                    break;
            }
            loadView();
        }
    }

    private void loadView(){
        toggleErrorView(false);
        mEmptyView.setVisibility(View.GONE);
        mPullRefreshListView.setVisibility(View.VISIBLE);
        List<Goods> cacheList = mCurrentSparseArray.get(mCurrentModeValue);
        if (cacheList == null || cacheList.isEmpty()){
            executeLoadData();
        }else{
            mCurrentList.clear();
            mCurrentList.addAll(cacheList);
            mGoodsAdapter.notifyDataSetChanged();
        }
    }

    private void changeCategoryBar(){

        RadioButton radioButton1 = (RadioButton) mRadioGroup.findViewById(R.id.radio_level_one);
        RadioButton radioButton2 = (RadioButton) mRadioGroup.findViewById(R.id.radio_level_two);
        RadioButton radioButton3 = (RadioButton) mRadioGroup.findViewById(R.id.radio_level_three);
        RadioButton radioButton4 = (RadioButton) mRadioGroup.findViewById(R.id.radio_level_four);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            //动画效果
            new FlipVerticalAnimation(mBottomBar).animate();
        }

        if(mCurrentModeType == GoodsManager.MODE_OBJECT){
            List<Type> types = CacheDataManager.mCategory.object.types;
            radioButton1.setText(types.get(0).name);
            radioButton2.setText(types.get(1).name);
            radioButton3.setText(types.get(2).name);
            radioButton4.setText(types.get(3).name);
        }else{
            List<Type> types = CacheDataManager.mCategory.price.types;
            radioButton1.setText(types.get(0).name);
            radioButton2.setText(types.get(1).name);
            radioButton3.setText(types.get(2).name);
            radioButton4.setText(types.get(3).name);
        }
        mRadioGroup.clearCheck();
        radioButton1.setChecked(true);
    }

    @OnClick(R.id.fab_type)
    public void changeType(ImageButton view){

        if(mCurrentModeType == GoodsManager.MODE_OBJECT){
            //切换成价格模式
            mCurrentModeType = GoodsManager.MODE_PRICE;
            mCurrentModeValue = CacheDataManager.mCategory.price.types.get(0).id;//赋初始值
            if(mCurrentSortType == GoodsManager.SORT_BY_HOT){
                mCurrentSparseArray = mPriceHotSparseArray;
            }else{
                mCurrentSparseArray = mPriceNewSparseArray;
            }
            view.setImageResource(R.drawable.fab_money);
        }else {
            mCurrentModeType = GoodsManager.MODE_OBJECT;
            mCurrentModeValue = CacheDataManager.mCategory.object.types.get(0).id;//赋初始值
            if(mCurrentSortType == GoodsManager.SORT_BY_HOT){
                mCurrentSparseArray = mObjectHotSparseArray;
            }else{
                mCurrentSparseArray = mObjectNewSparseArray;
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


    /**
     * 获取当前页码集合
     * @return
     */
    private SparseArray<Integer> getCurrentPageNoSparseArray(){
        if(mCurrentSortType == GoodsManager.SORT_BY_HOT){
            return mHotPageNoSparseArray;
        }else{
            return mNewPageNoSparseArray;
        }
    }

    private SparseArray<Long> getCurrentUpdateTimeSparseArray(){
        if(mCurrentSortType == GoodsManager.SORT_BY_HOT){
            return mHotUpdateTimeSparssArray;
        }else{
            return mNewUpdateTimeSparssArray;
        }
    }

    private SparseArray<Long> getCurrentLoadTimeSparseArray(){
        if(mCurrentSortType == GoodsManager.SORT_BY_HOT){
            return mHotLoadTimeSparssArray;
        }else{
            return mNewLoadTimeSparssArray;
        }
    }
}
