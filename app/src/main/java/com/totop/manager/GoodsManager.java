package com.totop.manager;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.totop.model.DataRes;
import com.totop.model.Goods;
import com.totop.model.db.GoodsDB;
import com.totop.network.NetApi;
import com.totop.utils.Constants;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;

public class GoodsManager {
    /**
     * 每页显示个数
     */
    public static final int PAGE_COUNT = 10;
    /**
     * 价格模式
     */
    public static final String MODE_PRICE = "price";
    /**
     * 对象模式
     */
    public static final String MODE_OBJECT = "object";

    public static final int SORT_BY_NEW = 0;
    public static final int SORT_BY_HOT = 1;
    /**
     * 最近浏览的最大个数
     */
    public static final int MAX_HISTORY_COUNT = 20;

    private static NetApi service = null;

    static{
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.HOST)
                .build();

        service = restAdapter.create(NetApi.class);
    }

    public static void findGoods(int pageNo,int pageCount,int sortType,String typeKey,int typeValue, Callback<DataRes<Goods>> response){

        if(MODE_PRICE.equals(typeKey)){
            service.findGoodsByPrice(pageNo, pageCount, sortType, typeValue,response);
        }else if(MODE_OBJECT.equals(typeKey)){
            service.findGoodsByObject(pageNo,pageCount,sortType,typeValue,response);
        }
    }

    public static void findGoods(int pageNo,int sortType,String typeKey,int typeValue,Callback<DataRes<Goods>> response){
        findGoods(pageNo,PAGE_COUNT,sortType,typeKey,typeValue,response);
    }

    /**
     * 保存了浏览记录
     * @param goods
     */
    public static void saveHistory(Goods goods){
        GoodsDB goodsDB = goods.transfer();
        goodsDB.time = System.currentTimeMillis();
        List<GoodsDB> list = new Select().from(GoodsDB.class).orderBy("time desc").execute();
        //判断是否超过最大数
        if(list.size() >= MAX_HISTORY_COUNT){
            ActiveAndroid.beginTransaction();

            try {
                //删除最后一条
                GoodsDB.delete(GoodsDB.class,list.get(list.size()-1).getId());
                //保存当条数据
                goodsDB.save();
                ActiveAndroid.setTransactionSuccessful();
            } finally {
                ActiveAndroid.endTransaction();
            }
        }else {
            goodsDB.save();
        }
    }

    /**
     * 查询所有浏览记录
     * @return
     */
    public static List<GoodsDB> findHistory(){
        return new Select().from(GoodsDB.class).execute();
    }
}
