package com.genius.totop.manager;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.genius.totop.model.DataRes;
import com.genius.totop.model.Goods;
import com.genius.totop.model.db.GoodsDB;
import com.genius.totop.utils.NetApiUtils;

import java.util.List;

import retrofit.Callback;

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

    public static void findGoods(int pageNo,int pageCount,int sortType,String typeKey,int typeValue, Callback<DataRes<Goods>> response){

        if(MODE_PRICE.equals(typeKey)){
            NetApiUtils.service.findGoodsByPrice(pageNo, pageCount, sortType, typeValue,response);
        }else if(MODE_OBJECT.equals(typeKey)){
            NetApiUtils.service.findGoodsByObject(pageNo,pageCount,sortType,typeValue,response);
        }
    }

    public static void findGoods(int pageNo,int sortType,String typeKey,int typeValue,Callback<DataRes<Goods>> response){
        findGoods(pageNo,PAGE_COUNT,sortType,typeKey,typeValue,response);
    }

    public static void search(int pageNo,int pageCount ,String key,Callback<DataRes<Goods>> response){
        NetApiUtils.service.search(pageNo,pageCount,key,response);
    }

    /**
     * 保存了浏览记录
     * @param goods
     */
    public static void saveHistory(Goods goods){
        GoodsDB goodsDB = goods.transfer();
        goodsDB.time = System.currentTimeMillis();

        //判断是否存在浏览记录
        if(isExistHistory(goods)){
            new Update(GoodsDB.class).set("time="+System.currentTimeMillis()).where("goodsid=" + goods.id).execute();
            return;
        }

        List<GoodsDB> list = findHistory();

        //判断是否超过最大数
        if(list.size() >= MAX_HISTORY_COUNT){
            ActiveAndroid.beginTransaction();

            try {
                //重复的定义
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
        return new Select().from(GoodsDB.class).orderBy("time desc").execute();
    }

    /**
     * 判断是否存在浏览记录
     * @param goods
     * @return
     */
    public static boolean isExistHistory(Goods goods){
        //判断是否存在浏览记录
        List<Model> existList = new Select().from(GoodsDB.class).where("goodsid=" + goods.id).execute();
        return !existList.isEmpty();
    }
}
