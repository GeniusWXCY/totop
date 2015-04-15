package com.totop.view.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.totop.activity.R;
import com.totop.model.Goods;
import com.totop.utils.ShareUtils;
import com.totop.utils.UILHelper;
import com.totop.view.widget.ColoredRatingBar;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GoodsAdapter extends BaseAdapter{

    private List<Goods> list;
    private LayoutInflater mInflater;
    private Context mContext;
    private int currentSortType;

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

        if(Float.valueOf(goods.heat) - 0.5 < 0){//新鲜度
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
        holder.originalPriceText.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG);
        holder.sourceTexct.setText(goods.sourceName);
        holder.goods = goods;

        holder.shareText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtils.share(mContext);
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
