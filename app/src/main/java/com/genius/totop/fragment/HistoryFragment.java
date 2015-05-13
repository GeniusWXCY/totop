package com.genius.totop.fragment;

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
import android.widget.TextView;

import com.genius.totop.activity.GoodsDetailActivity;
import com.genius.totop.manager.GoodsManager;
import com.genius.totop.model.Goods;
import com.genius.totop.model.db.GoodsDB;
import com.genius.totop.view.adapter.GoodsAdapter;
import com.totop.genius.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class HistoryFragment extends Fragment {

    @InjectView(R.id.listView_goods)ListView mListView;
    @InjectView(R.id.progressBar) ProgressBar mProgressBar;
    @InjectView(R.id.empty_view)View mEmptyView;
    @InjectView(R.id.text_empty_desc)TextView emptyDesc;

    private Context mContext;
    private GoodsAdapter mGoodsAdapter;
    private List<Goods> currentList = new ArrayList<Goods>();

    public HistoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.inject(this, view);
        mContext = getActivity();
        mGoodsAdapter = new GoodsAdapter(mContext, currentList);
        mListView.setAdapter(mGoodsAdapter);

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
        emptyDesc.setText(getString(R.string.str_history_empty));

        new GetDataTask().execute();

        return view;
    }

    private class GetDataTask extends AsyncTask<Integer, Void, List<GoodsDB>>{
        @Override
        protected void onPreExecute() {

            mEmptyView.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected List<GoodsDB> doInBackground(Integer... params) {
            return GoodsManager.findHistory();
        }

        @Override
        protected void onPostExecute(List<GoodsDB> result) {

            if(result != null ){
                if(result.isEmpty()){
                    mEmptyView.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                }else {
                    currentList.addAll(transfer(result));
                    mGoodsAdapter.notifyDataSetChanged();
                }
            }else{
                mListView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
            }
            super.onPostExecute(result);

            mProgressBar.setVisibility(View.GONE);
        }

    }


    private List<Goods> transfer(List<GoodsDB> list){
        List<Goods> result = new ArrayList<Goods>();
        for (GoodsDB goodsDB : list){
            result.add(goodsDB.transfer());
        }
        return result;
    }
}
