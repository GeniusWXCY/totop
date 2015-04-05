package com.totop.network;

import com.totop.model.DataRes;
import com.totop.model.Goods;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Query;

public interface INetApi {

    @GET("/interface/api.do?sk=1000")
    DataRes<Goods> findGoods(@Query("pi") int pageNo, @Query("ps") int pageCount, @Query("sort") int sortType);
}
