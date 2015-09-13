package com.genius.totop.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}
