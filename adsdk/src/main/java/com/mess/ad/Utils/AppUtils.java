package com.mess.ad.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import java.util.List;

public class AppUtils {

    public static Drawable getAppIcon(Context context) {
        return null;
    }

    public static String getAppName(Context context) {
        return "";
    }

    public static boolean isRunningForeground(Context context) {
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses();
        if (runningAppProcesses == null) {
            return false;
        }
        String str = context.getApplicationInfo().packageName;
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
            if (str.equalsIgnoreCase(runningAppProcessInfo.processName) && runningAppProcessInfo.importance == 100) {
                return true;
            }
        }
        return false;
    }

    public static void exitApp(Context context) {
        for (ActivityManager.AppTask appTask : ((ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE)).getAppTasks()) {
            appTask.finishAndRemoveTask();
        }
        System.exit(0);
    }

    public static String getMetaValue(Context context, String str) {
        try {
            return context.getPackageManager().getApplicationInfo(context.getPackageName(), 128).metaData.getString(str);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "tp8";
        }
    }

    public static boolean getMetaBool(Context context, String str) {
        try {
            return context.getPackageManager().getApplicationInfo(context.getPackageName(), 128).metaData.getBoolean(str);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isScreenLand(Activity activity) {
        Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(point);
        return point.x > point.y;
    }



}
