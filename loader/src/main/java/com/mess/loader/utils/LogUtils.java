package com.mess.loader.utils;

import android.annotation.SuppressLint;
import android.util.Log;

public class LogUtils {
    private static String TAG = "LogUtils"; // 默认 TAG
    private static boolean DEBUG = true; // 可根据 BuildConfig.DEBUG 控制
    public static void setTAG(String TAG) {
        LogUtils.TAG = TAG;
    }

    public static void setDEBUG(boolean debug) {
        LogUtils.DEBUG = debug;
    }

    public static void d(String message) {
        if (!DEBUG) return;
        StackTraceElement caller = getCallerStackTrace();
        if (caller != null) {
            String tag = caller.getClassName().substring(caller.getClassName().lastIndexOf(".") + 1);
            Log.d(tag, getCallerStackMsg(caller, message));
        } else {
            Log.d(TAG, message);
        }
    }

    public static void e(String message) {
        if (!DEBUG) return;
        StackTraceElement caller = getCallerStackTrace();
        if (caller != null) {
            String tag = caller.getClassName().substring(caller.getClassName().lastIndexOf(".") + 1);
            Log.e(tag, getCallerStackMsg(caller, message));
        } else {
            Log.e(TAG, message);
        }
    }

    public static void i(String message) {
        if (!DEBUG) return;
        StackTraceElement caller = getCallerStackTrace();
        if (caller != null) {
            String tag = caller.getClassName().substring(caller.getClassName().lastIndexOf(".") + 1);
            Log.i(tag, getCallerStackMsg(caller, message));
        } else {
            Log.i(TAG, message);
        }
    }

    public static void w(String message) {
        if (!DEBUG) return;
        StackTraceElement caller = getCallerStackTrace();
        if (caller != null) {
            String tag = caller.getClassName().substring(caller.getClassName().lastIndexOf(".") + 1);
            Log.w(tag, getCallerStackMsg(caller, message));
        } else {
            Log.w(TAG, message);
        }
    }

    private static String getCallerStackMsg(StackTraceElement caller, String message) {
        String tag = caller.getClassName().substring(caller.getClassName().lastIndexOf(".") + 1);
        @SuppressLint("DefaultLocale")
        String logMessage = String.format("(%s:%d) %s#%s → %s",
                caller.getFileName(), caller.getLineNumber(),
                tag, caller.getMethodName(), message);
        return logMessage;
    }

    private static StackTraceElement getCallerStackTrace() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stackTrace.length; i++) {
            if (stackTrace[i].getClassName().equals(LogUtils.class.getName()) && i + 2 < stackTrace.length) {
                return stackTrace[i + 2]; // 获取 LogUtils.d() 的调用者
            }
        }
        return null;
    }
}

