package com.totop.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

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
                new GetDataTask().execute();
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

        new GetDataTask().execute();
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

    private class GetDataTask extends AsyncTask<Integer, Void, DataRes<Goods>>{
        @Override
        protected void onPreExecute() {
            //ÅÐ¶ÏÊÇ·ñÓÐÍøÂçÁ¬½Ó
            mProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected DataRes<Goods> doInBackground(Integer... params) {

            try {
                //TODO ËÑË÷½Ó¿Ú
                return GoodsManager.findGoods(1, 1, GoodsManager.MODE_PRICE, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(DataRes<Goods> result) {

            if(result != null && result.success){
                List<Goods> list = result.data;
                currentList.addAll(list);
            }else{
                //ÍøÂçÇëÇóÊ§°Ü
                toggleErrorView(true);
            }
            super.onPostExecute(result);
            mProgressBar.setVisibility(View.GONE);
        }
    }

}
