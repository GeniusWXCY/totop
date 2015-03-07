package com.totop.bean;

public class Goods {

    public static final int SOURCE_TAOBAO = 0;
    public static final int SOURCE_TMALL = 1;
    public static final int SOURCE_JD = 2;

    public String id;
    /**
     * 标题
     */
    public String title;
    /**
     * 描述
     */
    public String description;
    /**
     * 现价
     */
    public float currentprice;
    /**
     * 原价
     */
    public float originalprice;
    /**
     * 来源 0淘宝 1天猫 2京东
     */
    public int source;
    /**
     * 剩下展示时间
     */
    public int lefttime;
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
    public String url;

    //TODO 性别，排序，热度,分类，跳转地址
}
