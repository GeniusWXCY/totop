package com.totop.model;

public class Goods {

    public static final int SOURCE_TAOBAO = 0;
    public static final int SOURCE_TMALL = 1;
    public static final int SOURCE_JD = 2;

    public String id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Goods goods = (Goods) o;

        if (!id.equals(goods.id)) return false;
        if (!name.equals(goods.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

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
}
