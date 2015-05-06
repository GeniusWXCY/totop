package com.genius.totop.manager;

import android.content.Context;

import com.activeandroid.query.Select;
import com.genius.totop.model.CacheData;
import com.genius.totop.model.Type;
import com.genius.totop.model.db.CacheDataDB;
import com.genius.totop.utils.Constants;
import com.genius.totop.utils.NetApiUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.totop.genius.R;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CacheDataManager {

    public static void findCacheData(Callback<CacheData> response) {
        NetApiUtils.service.findCacheData(response);
    }

    public static CacheData findCacheDataLocal(Context context) {

        CacheData cacheData = new CacheData();
        cacheData.help = cacheData.new Help();
        cacheData.price = cacheData.new TypeWrap();
        cacheData.object = cacheData.new TypeWrap();
        cacheData.share = cacheData.new Share();

        cacheData.help.desc = Constants.helpDesc;
        cacheData.share.url = Constants.shareUrl;
        cacheData.price.types = new ArrayList<>();
        cacheData.object.types = new ArrayList<>();

        List<CacheDataDB> cacheDatas = new Select().from(CacheDataDB.class).execute();
        if (cacheDatas.isEmpty()) {
            String[] priceArray = context.getResources().getStringArray(R.array.type);
            String[] objectArray = context.getResources().getStringArray(R.array.object);
            for (int i = 0; i < priceArray.length; i++) {
                cacheData.price.types.add(new Type(priceArray[i], (i + 1) + ""));
            }

            for (int i = 0; i < objectArray.length; i++) {
                cacheData.object.types.add(new Type(priceArray[i], (i + 5) + ""));
            }

        } else {
            CacheDataDB cacheDataDB = cacheDatas.get(0);
            cacheData.help.desc = cacheDataDB.helpDesc;
            cacheData.share.url = cacheDataDB.shareUrl;
            cacheData.price.types = new Gson().fromJson(cacheDataDB.price, new TypeToken<List<Type>>() {
            }.getType());
            cacheData.object.types = new Gson().fromJson(cacheDataDB.object, new TypeToken<List<Type>>() {
            }.getType());
        }

        return cacheData;
    }

    public static void update() {
        findCacheData(new Callback<CacheData>() {
            @Override
            public void success(CacheData cacheData, Response response) {
                long shareTime = cacheData.share.time;
                long helpTime = cacheData.help.time;
                long objectTime = cacheData.object.time;
                long priceTime = cacheData.price.time;

                List<CacheDataDB> cacheDataDBs = new Select().from(CacheDataDB.class).execute();
                if(!cacheDataDBs.isEmpty()){
                    CacheDataDB cacheDataDB = cacheDataDBs.get(0);
                    cacheDataDB.shareTime = shareTime;
                    cacheDataDB.shareUrl = cacheData.share.url;
                    cacheDataDB.helpDesc = cacheData.help.desc;
                    cacheDataDB.helpTime = cacheData.help.time;
                    cacheDataDB.price = new Gson().toJson(cacheData.price.types);
                    cacheDataDB.priceTime = cacheData.price.time;
                    cacheDataDB.object = new Gson().toJson(cacheData.object.types);
                    cacheDataDB.objectTime = cacheData.object.time;
                    //TODO 判断是否更新
                    cacheDataDB.save();
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
