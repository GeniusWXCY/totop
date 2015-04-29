package com.genius.totop;

import com.genius.totop.model.Goods;

import java.util.ArrayList;
import java.util.List;

public class DataFactory {

    public static List<Goods> make(){
        List<Goods> list = new ArrayList<Goods>();

        Goods goods1 = new Goods();
        Goods goods2 = new Goods();

        goods1.id = "1";
        goods1.name = "超薄防勾丝袜连裤袜";
        goods1.description = "小编说：弹性好、透明度好、手感更加细腻，防勾丝。";
        goods1.currentPrice = 4.9f;
        goods1.originalPrice = 9.8f;
        goods1.salesvolume = 888;
        goods1.icon = "http://gw.alicdn.com/bao/uploaded/i4/TB19LyNGFXXXXasXXXXXXXXXXXX_!!0-item_pic.jpg_320x320q50s150.jpg";
        goods1.link = "http://h5.m.taobao.com/awp/core/detail.htm?id=41889677311&ali_refid=a3_430125_1006:1104442269:N:%CB%BF%CD%E0%C7%EF%B6%AC+%B1%A1%BF%EE:29026874c798a3551491576d4b91050b&ali_trackid=1_29026874c798a3551491576d4b91050b&spm=0.0.0.0.";

        goods2.id = "2";
        goods2.name = "冰箱除味剂竹炭包";
        goods2.description = "小编说：吸附能力强，用于冰箱除味效果佳，吸附有毒气体及异味，可以重复使用。";
        goods2.currentPrice = 14.9f;
        goods2.originalPrice = 19.8f;
        goods2.salesvolume = 1236;
        goods2.icon = "http://gw.alicdn.com/bao/uploaded/i3/TB1bFDzGpXXXXa6XpXXXXXXXXXX_!!0-item_pic.jpg_320x320q50s150.jpg";
        goods2.link = "http://h5.m.taobao.com/awp/core/detail.htm?id=19826705312&wp_m=double_goods_7377765681&wp_pk=shop/index_40656291_74101&wp_app=weapp&from=inshop";

        list.add(goods1);
        list.add(goods2);

        return list;
    }
}
