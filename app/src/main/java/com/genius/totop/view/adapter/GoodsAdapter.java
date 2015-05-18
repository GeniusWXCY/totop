package com.genius.totop.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.genius.totop.R;
import com.genius.totop.manager.GoodsManager;
import com.genius.totop.model.Goods;
import com.genius.totop.utils.UILHelper;
import com.genius.totop.utils.UMengShareUtils;
import com.genius.totop.view.widget.ColoredRatingBar;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GoodsAdapter extends BaseAdapter{

    private List<Goods> list;
    private LayoutInflater mInflater;
    private Context mContext;
    /**
     * 当前排序方式-默认按照人气值排序
     */
    private int currentSortType = GoodsManager.SORT_BY_HOT;

    public GoodsAdapter(Context context,List<Goods> list,int currentSortType){
        this(context,list);
        this.currentSortType = currentSortType;
    }

    public GoodsAdapter(Context context,List<Goods> list){
        this.list = list;
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setCurrentSortType(int currentSortType){
        this.currentSortType = currentSortType;
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

        Goods goods = list.get(position);
        ViewHolder holder = null;

        if(convertView == null){
            convertView = mInflater.inflate(R.layout.item_goods_list,null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        UILHelper.displayImage(goods.icon, holder.goodsPicImage);
        holder.titleText.setText(goods.name);

        if(currentSortType == GoodsManager.SORT_BY_NEW){//新鲜度
            //TODO 没有新鲜度的字段
            holder.ratingBar.setRating(Float.valueOf(Math.random() * 5 + ""));
            holder.ratingBar.setVisibility(View.VISIBLE);
            holder.hotImage.setVisibility(View.GONE);
            holder.hotLabelText.setText(mContext.getString(R.string.str_new_text));
        }else{//人气
            holder.ratingBar.setVisibility(View.GONE);
            holder.hotImage.setVisibility(View.VISIBLE);
            holder.hotLabelText.setText(mContext.getString(R.string.str_heat_text));
            float heat = Float.valueOf(goods.heat);

            double temp = Math.random();
            if(temp > 0.8){
                holder.hotImage.setBackgroundResource(R.drawable.hotter);
            }else if(temp>0.5){
                holder.hotImage.setBackgroundResource(R.drawable.hotter);
            }else{
                holder.hotImage.setBackgroundResource(R.drawable.hot);
            }
        }

        holder.priceText.setText(String.valueOf(goods.currentPrice));
        holder.originalPriceText.setText(String.valueOf(goods.originalPrice));
        holder.originalPriceText.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.sourceTexct.setText(goods.sourceName);
        holder.goods = goods;
        holder.shareText.setTag(goods.icon);
        holder.shareText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UMengShareUtils.share((Activity) mContext, (String) v.getTag());
            }
        });

        return convertView;
    }

    public static class ViewHolder {
        @InjectView(R.id.imageview_goods_pic)ImageView goodsPicImage;
        @InjectView(R.id.textview_title)TextView titleText;
        @InjectView(R.id.textview_goods_hot_label)TextView hotLabelText;
        @InjectView(R.id.ratingbar_refresh)ColoredRatingBar ratingBar;
        @InjectView(R.id.imagebutton_new)ImageView hotImage;
        @InjectView(R.id.text_price)TextView priceText;
        @InjectView(R.id.text_original_price)TextView originalPriceText;
        @InjectView(R.id.text_source)TextView sourceTexct;
        @InjectView(R.id.text_share)TextView shareText;
        public Goods goods;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
