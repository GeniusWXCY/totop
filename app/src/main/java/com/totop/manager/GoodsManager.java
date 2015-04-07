package com.totop.manager;

import com.totop.model.DataRes;
import com.totop.model.Goods;
import com.totop.network.NetApi;
import com.totop.utils.Constants;

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
    private static NetApi service = null;

    static{
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.HOST)
                .build();

        service = restAdapter.create(NetApi.class);
    }

    public static DataRes<Goods> findGoods(int pageNo,int pageCount,int sortType,String typeKey,int typeValue){

        if(MODE_PRICE.equals(typeKey)){
            return service.findGoodsByPrice(pageNo, pageCount, sortType, typeValue);
        }else if(MODE_OBJECT.equals(typeKey)){
            return service.findGoodsByObject(pageNo,pageCount,sortType,typeValue);
        }
        return null;
    }

    public static DataRes<Goods> findGoods(int pageNo,int sortType,String typeKey,int typeValue){
        return findGoods(pageNo,PAGE_COUNT,sortType,typeKey,typeValue);
    }
}
