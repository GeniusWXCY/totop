package com.genius.totop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;

import com.genius.totop.R;
import com.genius.totop.manager.CacheDataManager;
import com.genius.totop.model.CacheData;
import com.genius.totop.model.Category;
import com.genius.totop.model.DataRes;
import com.genius.totop.model.db.CacheDataDB;
import com.genius.totop.utils.ThreadPoolUtils;

import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends FragmentActivity{

    private static final String TAG = "WelcomeActivity";
    private boolean isTimeout = false;
    private boolean isFinishInit = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);


        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(isFinishInit){
                    executeNext();
                }
                isTimeout = true;
            }
        };

        new Timer().schedule(task, 2000);
        operateCacheData();
    }

    private void executeNext(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        WelcomeActivity.this.finish();
    }

    private void operateCacheData(){
        //从db中获取数据
        final CacheDataDB cacheDataDB = CacheDataManager.findFromLocal();

        if(cacheDataDB == null){

            //取常量进行初始化
            CacheDataManager.initData(WelcomeActivity.this);

            //TODO 进度条
            //mProgressBar.setVisibility(View.VISIBLE);

            //发起网络请求，并将数据缓存起来
            ThreadPoolUtils.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    try{
                        DataRes<CacheData> cacheDatas = CacheDataManager.findCacheDatas();
                        DataRes<Category> categorys = CacheDataManager.findCategorys();
                        //取网络数据进行初始化
                        CacheDataManager.initData(cacheDatas,categorys);

                        CacheDataManager.updateLocal(cacheDataDB, cacheDatas,categorys);
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.e(TAG,e.toString());
                    }
                    //mProgressBar.setVisibility(View.GONE);
                    isFinishInit = true;
                    if (isTimeout){
                        executeNext();
                    }
                }
            });

        }else{
            //取本地数据进行初始化
            CacheDataManager.initData(cacheDataDB);
            //TODO 更新本地数据
            isFinishInit = true;
            if (isTimeout){
                executeNext();
            }
        }
    }
}
