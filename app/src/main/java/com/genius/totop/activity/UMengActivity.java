package com.genius.totop.activity;

import android.support.v4.app.FragmentActivity;

import com.umeng.analytics.MobclickAgent;

public class UMengActivity extends FragmentActivity {
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
