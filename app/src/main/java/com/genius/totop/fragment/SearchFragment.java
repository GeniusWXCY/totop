package com.genius.totop.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.genius.totop.R;
import com.genius.totop.activity.GoodsDetailActivity;
import com.genius.totop.manager.GoodsManager;
import com.genius.totop.model.DatasRes;
import com.genius.totop.model.Goods;
import com.genius.totop.view.adapter.GoodsAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.trinea.android.common.util.ListUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import tr.xip.errorview.ErrorView;

public class SearchFragment extends Fragment {

    @InjectView(R.id.progressBar) ProgressBar mProgressBar;
    @InjectView(R.id.error_view) ErrorView mErrorView;
    @InjectView(R.id.empty_view)View mEmptyView;
    @InjectView(R.id.text_empty_desc)TextView emptyDesc;
    @InjectView(R.id.listView_goods)PullToRefreshListView mPullRefreshListView;

    private Context mContext;
    private GoodsAdapter mGoodsAdapter;
    private List<Goods> currentList = new ArrayList<Goods>();

    private int currentPageNo = 1;
    private String serarchText;

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.inject(this, view);
        mContext = getActivity();
        mGoodsAdapter = new GoodsAdapter(mContext, currentList);

        mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);

        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                currentPageNo++;
                executeData();
            }
        });

        ListView mListView = mPullRefreshListView.getRefreshableView();
        mListView.setAdapter(mGoodsAdapter);

        mErrorView.setOnRetryListener(new ErrorView.RetryListener() {

            @Override
            public void onRetry() {
                toggleErrorView(false);
                executeData();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GoodsAdapter.ViewHolder viewHolder = (GoodsAdapter.ViewHolder) view.getTag();
                Intent intent = new Intent(mContext, GoodsDetailActivity.class);
                intent.putExtra(GoodsDetailActivity.EXTRA_IMAGE_URL, viewHolder.goods.link);
                intent.putExtra(GoodsDetailActivity.EXTRA_GOODS_SOURCE, viewHolder.goods.sourceName);
                startActivity(intent);
            }
        });

        emptyDesc.setText(getString(R.string.str_search_empty));

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

    private void executeData(){

        toggleErrorView(false);
        mEmptyView.setVisibility(View.GONE);
        mPullRefreshListView.setVisibility(View.VISIBLE);

        //TODO 判断是否有网络连接
        mProgressBar.setVisibility(View.VISIBLE);

        GoodsManager.search(currentPageNo, GoodsManager.PAGE_COUNT, serarchText, new Callback<DatasRes<Goods>>() {
            @Override
            public void success(DatasRes<Goods> goodsDatasRes, Response response) {
                mPullRefreshListView.onRefreshComplete();
                if (goodsDatasRes != null) {
                    List<Goods> list = goodsDatasRes.data;
                    if (list.isEmpty()) {
                        mEmptyView.setVisibility(View.VISIBLE);
                        mPullRefreshListView.setVisibility(View.GONE);
                    } else {
                        ListUtils.addDistinctList(currentList, list);
                        mGoodsAdapter.notifyDataSetChanged();
                    }

                    //是否有下一页
                    if (list.size() < GoodsManager.PAGE_COUNT) {
                        mPullRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
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

    public void search(String text){
        serarchText = text;
        executeData();
    }

}
