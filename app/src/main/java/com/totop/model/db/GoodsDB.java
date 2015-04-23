package com.totop.model.db;

import com.activeandroid.Model;

public class GoodsDB extends Model{

    public String id;
    /**
     * 标题
     */
    public String name;
    /**
     * 描述
     */
    public String description;
    /**
     * 现价
     */
    public float currentPrice;
    /**
     * 原价
     */
    public float originalPrice;
    /**
     * 来源 0淘宝 1天猫 2京东
     */
    public int source;
    /**
     * 图片地址
     */
    public String icon;
    /**
     * 已买个数
     */
    public int salesvolume;
    /**
     * 商品地址
     */
    public String link;

    public String priceType;
    public String objectType;
    public String heat;
    public String sourceName;

    /**
     * 浏览时间
     */
    public long time;
}
