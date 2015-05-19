package com.genius.totop;

import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;

import com.activeandroid.ActiveAndroid;
import com.genius.totop.utils.Constants;
import com.genius.totop.utils.UILHelper;

public class App extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        UILHelper.initImageLoader(sContext);
        ActiveAndroid.initialize(this);

        Constants.IMEI =  ((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
                .getDeviceId();
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
