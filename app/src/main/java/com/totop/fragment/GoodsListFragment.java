package com.totop.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
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
import butterknife.OnClick;
import cn.trinea.android.common.util.ListUtils;

public class GoodsListFragment extends Fragment {

    @InjectView(R.id.listView_goods)PullToRefreshListView mPullRefreshListView;

    List<Goods> mList = new ArrayList<Goods>();

    private Context mContext;
    private GoodsAdapter mGoodsAdapter;
    private OnFragmentSettingListener mListener;
    /**
     * 当前页数
     */
    private int currentNo = 1;
    /**
     * 1 价格属性（默认值） 2 对象属性
     */
    private int currentSortType = 1;

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
        mGoodsAdapter = new GoodsAdapter(mContext,mList);

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
                new GetDataTask().execute(1,currentSortType);
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                currentNo++;
                new GetDataTask().execute(currentNo,currentSortType);
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
        new GetDataTask().execute(currentNo,currentSortType);
        return view;
    }

    private class GetDataTask extends AsyncTask<Integer, Void, DataRes<Goods>> {

        @Override
        protected DataRes<Goods> doInBackground(Integer... params) {
            //TODO 进度条
            return GoodsManager.findGoods(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(DataRes<Goods> result) {

            if(result.success){

                List<Goods> list = result.data;
                if(isRefresh){ //刷新数据
                    isRefresh = false;
                    List<Goods> tempList = new ArrayList<Goods>();
                    for(Goods goods: list){
                        if (!mList.contains(goods)){
                            tempList.add(goods);
                        }
                    }
                    if(tempList.size() > 0){
                        mList.addAll(0,tempList);
                        mGoodsAdapter.notifyDataSetChanged();
                    }
                    mPullRefreshListView.onRefreshComplete();
                }else{
                    //去重复
                    ListUtils.addDistinctList(mList,result.data);
                    mGoodsAdapter.notifyDataSetChanged();
                    mPullRefreshListView.onRefreshComplete();

                    //是否有下一页
                    if(mList.size() >= result.total){
                        //mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
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
}
