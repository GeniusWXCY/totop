package com.genius.totop;

import android.app.Application;
import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.genius.totop.utils.UILHelper;

public class App extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        UILHelper.initImageLoader(sContext);
        ActiveAndroid.initialize(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }
    public static Context getContext() {
        return sContext;
    }

}
