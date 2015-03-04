package com.totop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.totop.activity.R;
import com.totop.bean.Goods;

import java.util.List;

public class GoodsAdapter extends BaseAdapter{

    private List<Goods> list;
    private LayoutInflater mInflater;
    private Context mContext;

    public GoodsAdapter(Context context,List<Goods> list){
        this.list = list;
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if(convertView == null){

            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_goods_list,null);

            convertView.setTag(holder);

        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    public final class ViewHolder{

    }
}
