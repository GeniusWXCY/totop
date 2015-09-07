package com.genius.totop.manager;

import android.content.Context;

import com.activeandroid.query.Select;
import com.genius.totop.R;
import com.genius.totop.model.CacheData;
import com.genius.totop.model.Category;
import com.genius.totop.model.DataRes;
import com.genius.totop.model.Type;
import com.genius.totop.model.db.CacheDataDB;
import com.genius.totop.utils.Constants;
import com.genius.totop.utils.EncyUtils;
import com.genius.totop.utils.NetApiUtils;
import com.genius.totop.utils.ThreadPoolUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;


public class CacheDataManager {

    /**
     * 帮助信息、分享地址等缓存数据
     */
    public static final CacheData mCacheData = new CacheData();
    /**
     * 对象/价格等类别对象
     */
    public static Category mCategory = new Category();

    private static final int CATEGORY_NUM = 4;

    public static void initData(Context context){
        mCacheData.url = Constants.shareUrl;
        mCacheData.content = Constants.helpDesc;

        mCategory.price = mCategory.new TypeWrap();
        mCategory.object = mCategory.new TypeWrap();

        mCategory.price.types = new ArrayList<>();
        mCategory.object.types = new ArrayList<>();

        String[] priceArray = context.getResources().getStringArray(R.array.type);
        String[] objectArray = context.getResources().getStringArray(R.array.object);

        int length1 = CATEGORY_NUM > priceArray.length ? CATEGORY_NUM : priceArray.length;
        int length2 = CATEGORY_NUM > objectArray.length ? CATEGORY_NUM : objectArray.length;

        for (int i = 0; i < length1; i++) {
            mCategory.price.types.add(new Type( (i + 1),priceArray[i],"1"));
        }

        for (int i = 0; i < length2; i++) {
            mCategory.object.types.add(new Type((i + 5),objectArray[i], "2"));
        }
    }

    /**
     * 获取帮助信息和分享地址
     */
    public static DataRes<CacheData> findCacheDatas() throws Exception {
        return NetApiUtils.service.findCacheDatas(EncyUtils.ency(System.currentTimeMillis()));
    }

    /**
     * 获取对象和价格类别
     */
    public static DataRes<Category> findCategorys() throws Exception {
        return NetApiUtils.service.findCategorys(EncyUtils.ency(System.currentTimeMillis()));
    }

    /**
     * 异步获取帮助信息和分享地址
     */
    public static void findCacheDatas(Callback<DataRes<CacheData>> response) {
        NetApiUtils.service.findCacheDatas(EncyUtils.ency(System.currentTimeMillis()),response);
    }

    /**
     * 异步获取对象和价格类别
     */
    public static void findCategorys(Callback<DataRes<Category>> response){
        NetApiUtils.service.findCategorys(EncyUtils.ency(System.currentTimeMillis()),response);
    }

    /**
     * 根据网络请求结果，将数据缓存至内存中
     * @param cacheDatas
     * @param categorys
     */
    public static void initData(DataRes<CacheData> cacheDatas, DataRes<Category> categorys) {
        if(cacheDatas != null && cacheDatas.success){
            CacheData tempCacheData = cacheDatas.data;
            if(tempCacheData != null){
                mCacheData.url = tempCacheData.url;
                mCacheData.content = tempCacheData.content;
            }
        }
        if (categorys != null && categorys.success){
            Category tempCateGory = categorys.data;
            if(tempCateGory != null){
                mCategory.object = tempCateGory.object;
                mCategory.price = tempCateGory.price;
            }
        }
    }

    /**
     * 将本地数据缓存至内存中
     * @param cacheDataDB
     */
    public static void initData(CacheDataDB cacheDataDB) {

        mCategory.price = mCategory.new TypeWrap();
        mCategory.object = mCategory.new TypeWrap();
        mCategory.price.types = new Gson().fromJson(cacheDataDB.price, new TypeToken<List<Type>>() {
            }.getType());
        mCategory.object.types = new Gson().fromJson(cacheDataDB.object, new TypeToken<List<Type>>() {
            }.getType());
        mCacheData.url = cacheDataDB.shareUrl;
        mCacheData.content = cacheDataDB.helpDesc;
    }

    /**
     * 获取本地数据
     * @return
     */
    public static CacheDataDB findFromLocal(){
        List<CacheDataDB> cacheDataDBs = new Select().from(CacheDataDB.class).execute();
        if(cacheDataDBs!= null && !cacheDataDBs.isEmpty()){
            return cacheDataDBs.get(0);
        }else{
            return null;
        }
    }

    /**
     * 异步更新本地数据
     */
    public static void updateLocal(final CacheDataDB cacheDataDB) {

        ThreadPoolUtils.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                DataRes<CacheData> cacheDatas = null;
                try {
                    cacheDatas = findCacheDatas();
                    DataRes<Category> categorys = findCategorys();
                    updateLocal(cacheDataDB,cacheDatas,categorys);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 同步更新本地数据
     * @param cacheDataDB
     * @param cacheDatas
     * @param categorys
     */
    public static void updateLocal(CacheDataDB cacheDataDB,DataRes<CacheData> cacheDatas,DataRes<Category> categorys){

        if (cacheDataDB == null) {
            cacheDataDB = new CacheDataDB();
        }

        //保存本地时间
        long currentTime = System.currentTimeMillis();

        if(categorys != null){
            cacheDataDB.object = new Gson().toJson(categorys.data.object.types);
            cacheDataDB.price = new Gson().toJson(categorys.data.price.types);
            cacheDataDB.objectTime = currentTime;
            cacheDataDB.priceTime = currentTime;
        }

        if(cacheDatas != null){
            cacheDataDB.helpDesc = cacheDatas.data.content;
            cacheDataDB.shareUrl = cacheDatas.data.url;
            cacheDataDB.modifyTime = currentTime;
        }

        cacheDataDB.save();
    }
}
