package com.genius.totop.manager;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.genius.totop.model.DatasRes;
import com.genius.totop.model.Goods;
import com.genius.totop.model.SimpleLocation;
import com.genius.totop.model.db.GoodsDB;
import com.genius.totop.utils.Constants;
import com.genius.totop.utils.NetApiUtils;
import com.genius.totop.utils.ThreadPoolUtils;
import com.google.gson.Gson;

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

    public static void findGoods(int pageNo,int pageCount,int sortType,String typeKey,int typeValue, Callback<DatasRes<Goods>> response){

        if(MODE_PRICE.equals(typeKey)){
            NetApiUtils.service.findGoodsByPrice(pageNo, pageCount, sortType, typeValue,response);
        }else if(MODE_OBJECT.equals(typeKey)){
            NetApiUtils.service.findGoodsByObject(pageNo,pageCount,sortType,typeValue,response);
        }
    }

    public static void findGoods(int pageNo,int sortType,String typeKey,int typeValue,Callback<DatasRes<Goods>> response){
        findGoods(pageNo,PAGE_COUNT,sortType,typeKey,typeValue,response);
    }

    /**
     * 搜索
     * @param pageNo
     * @param pageCount
     * @param key
     * @param response
     */
    public static void search(int pageNo,int pageCount ,String key,Callback<DatasRes<Goods>> response){
        NetApiUtils.service.search(pageNo, pageCount, key, response);
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

    /**
     * 提交浏览记录到服务端
     * @param imei
     * @param area
     * @param id
     */
    public static void postVisit(String imei, String area, String id) throws Exception {
        NetApiUtils.service.postVisit(imei,area,id);
    }

    /**
     * 浏览商品时保存浏览记录到本地，并提交信息到服务端
     * @param goods
     */
    public static void executeAfterView(final Goods goods, final Context context){
        ThreadPoolUtils.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                GoodsManager.saveHistory(goods);
                try {
                    //TODO 测试
                    Location location = getLocation(context);
                    SimpleLocation simpleLocation = new SimpleLocation(location.getLatitude(),location.getLongitude());
                    String json = new Gson().toJson(simpleLocation).toString();
                    postVisit(Constants.IMEI, json, goods.id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static Location getLocation(Context context){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);
        String locationProvider;
        if(providers.contains(LocationManager.GPS_PROVIDER)){
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        }else if(providers.contains(LocationManager.NETWORK_PROVIDER)){
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        }else{
            return null;
        }
        //获取Location
        Location location = locationManager.getLastKnownLocation(locationProvider);
        return location;
    }
}
