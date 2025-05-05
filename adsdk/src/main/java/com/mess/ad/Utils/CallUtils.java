package com.mess.ad.Utils;

import android.util.Log;

public class CallUtils {

    private final static String TAG = CallUtils.class.getSimpleName();

    public static String call(int i) {
        return new Throwable().getStackTrace()[i].getClassName();
    }

    public static void printCall() {
        StackTraceElement[] stackTrace;
        for (StackTraceElement stackTraceElement : new Throwable().getStackTrace()) {
            Log.e(TAG, "CallUtils info : " + stackTraceElement.getClassName() + "->" + stackTraceElement.getMethodName());
        }
    }
}
