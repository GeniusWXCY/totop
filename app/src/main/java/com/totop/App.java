package com.totop;

import android.app.Application;
import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.totop.utils.UILHelper;

public class App extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        UILHelper.initImageLoader(sContext);
        ActiveAndroid.initialize(this);
    }

    public static Context getContext() {
        return sContext;
    }

}
