package com.genius.totop.utils;

import com.genius.totop.network.NetApi;

import retrofit.RestAdapter;

public class NetApiUtils {

    public static NetApi service = null;

    static{
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.HOST)
                .build();

        service = restAdapter.create(NetApi.class);
    }
}
