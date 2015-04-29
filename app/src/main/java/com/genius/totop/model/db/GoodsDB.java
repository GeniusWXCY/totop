package com.genius.totop.model.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.genius.totop.model.Goods;

@Table(name="goods")
public class GoodsDB extends Model implements Transferable<Goods>{

    @Column
    public String goodsid;
    /**
     * 标题
     */
    @Column
    public String name;
    /**
     * 描述
     */
    @Column
    public String description;
    /**
     * 现价
     */
    @Column
    public float currentPrice;
    /**
     * 原价
     */
    @Column
    public float originalPrice;
    /**
     * 来源 0淘宝 1天猫 2京东
     */
    @Column
    public int source;
    /**
     * 图片地址
     */
    @Column
    public String icon;
    /**
     * 已买个数
     */
    @Column
    public int salesvolume;
    /**
     * 商品地址
     */
    @Column
    public String link;

    @Column
    public String priceType;
    @Column
    public String objectType;
    @Column
    public String heat;
    @Column
    public String sourceName;

    /**
     * 浏览时间
     */
    @Column
    public long time;

    @Override
    public Goods transfer() {
        Goods goods = new Goods();
        goods.id = this.goodsid;
        goods.name = this.name;
        goods.currentPrice = this.currentPrice;
        goods.originalPrice = this.originalPrice;
        goods.source = this.source;
        goods.icon = this.icon;
        goods.salesvolume = this.salesvolume;
        goods.link = this.link;
        goods.priceType = this.priceType;
        goods.objectType = this.objectType;
        goods.heat = this.heat;
        goods.sourceName = this.sourceName;
        return goods;
    }
}
