package com.totop.network;

import com.totop.model.DataRes;
import com.totop.model.Goods;
import com.totop.utils.Constants;

import java.util.List;

import retrofit.RestAdapter;

public class NetApi {

    /**
     * 每页显示个数
     */
    private static final int PAGE_COUNT = 10;
    private static INetApi service = null;

    static{
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.HOST)
                .build();

        service = restAdapter.create(INetApi.class);
    }

    public static DataRes<Goods> findGoods(int pageNo,int pageCount,int sortType){
        return service.findGoods(pageNo, pageCount, sortType);
    }

    public static DataRes<Goods> findGoods(int pageNo,int sortType){
        return service.findGoods(pageNo,PAGE_COUNT,sortType);
    }
}
