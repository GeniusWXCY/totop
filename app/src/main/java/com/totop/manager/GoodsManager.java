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
    public static final int PAGE_COUNT = 5;
    private static NetApi service = null;

    static{
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.HOST)
                .build();

        service = restAdapter.create(NetApi.class);
    }

    public static DataRes<Goods> findGoods(int pageNo,int pageCount,int sortType){
        return service.findGoods(pageNo, pageCount, sortType);
    }

    public static DataRes<Goods> findGoods(int pageNo,int sortType){
        return service.findGoods(pageNo,PAGE_COUNT,sortType);
    }
}
