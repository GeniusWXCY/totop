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

import com.totop.genius.R;
import com.genius.totop.activity.GoodsDetailActivity;
import com.genius.totop.manager.GoodsManager;
import com.genius.totop.model.DataRes;
import com.genius.totop.model.Goods;
import com.genius.totop.view.adapter.GoodsAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import tr.xip.errorview.ErrorView;

public class SearchFragment extends Fragment {

    @InjectView(R.id.listView_goods)ListView mListView;
    @InjectView(R.id.progressBar) ProgressBar mProgressBar;
    @InjectView(R.id.error_view) ErrorView mErrorView;
    @InjectView(R.id.empty_view)View mEmptyView;

    private Context mContext;
    private GoodsAdapter mGoodsAdapter;
    private List<Goods> currentList = new ArrayList<Goods>();

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.inject(this, view);
        mContext = getActivity();
        mGoodsAdapter = new GoodsAdapter(mContext, currentList);
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

        return view;
    }

    private void toggleErrorView(boolean isShow){
        if(isShow){
            mErrorView.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }else{
            mErrorView.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }
    }

    private void executeData(){

        toggleErrorView(false);
        mEmptyView.setVisibility(View.GONE);
        mListView.setVisibility(View.VISIBLE);

        //TODO 判断是否有网络连接
        mProgressBar.setVisibility(View.VISIBLE);

        GoodsManager.findGoods(1, 1, GoodsManager.MODE_PRICE, 1, new Callback<DataRes<Goods>>() {
            @Override
            public void success(DataRes<Goods> goodsDataRes, Response response) {
                if (goodsDataRes != null) {
                    List<Goods> list = goodsDataRes.data;
                    if (list.isEmpty()) {
                        mEmptyView.setVisibility(View.VISIBLE);
                        mListView.setVisibility(View.GONE);
                    } else {
                        currentList.addAll(list);
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


    public void search(String text){
        executeData();
    }

}
