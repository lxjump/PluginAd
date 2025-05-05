package com.mess.ad;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;

import com.mess.ad.Utils.AppUtils;

public class AppManager {

    public static void runGame(Activity activity) {
        String metaValue = AppUtils.getMetaValue(activity, "main_activity");
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(activity.getPackageName(), metaValue));
        activity.startActivity(intent);
        activity.finish();

    }

}
