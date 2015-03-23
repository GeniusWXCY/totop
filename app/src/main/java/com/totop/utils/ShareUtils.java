package com.totop.utils;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Administrator on 2015/3/8.
 */
public class ShareUtils {

    public static void share(Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_TEXT, "功能待完善！！");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, "9块9包邮"));
    }
}
