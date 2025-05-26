package com.mess.loader.hook.ams;

import static com.mess.loader.hook.ams.AMSHookManager.KEY_RAW_INTENT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.ContextThemeWrapper;

import com.mess.loader.Loader;
import com.mess.loader.utils.LogUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class PluginInstrumentation extends Instrumentation {

    private final static String TAG = PluginInstrumentation.class.getSimpleName();

    private Instrumentation mInstrumentation;
    private PackageManager mPackageManager;

    public PluginInstrumentation(Instrumentation mInstrumentation, PackageManager mPackageManager) {
        this.mInstrumentation = mInstrumentation;
        this.mPackageManager = mPackageManager;
    }



//    @Override
//    public Activity newActivity(Class<?> clazz, Context context, IBinder token, Application application, Intent intent, ActivityInfo info, CharSequence title, Activity parent, String id, Object lastNonConfigurationInstance) throws IllegalAccessException, InstantiationException {
//        Activity activity = mInstrumentation.newActivity(clazz, className, intent);
//
//        // 替换 Activity 的 Context
//        try {
//            Field contextField = Activity.class.getDeclaredField("mContext");
//            contextField.setAccessible(true);
//            contextField.set(activity, pluginContext);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return activity;
//    }

//    public ActivityResult execStartActivity(
//            Context who, IBinder contextThread, IBinder token, Activity target,
//            Intent intent, int requestCode, Bundle options) {
//        LogUtils.d( "\n打印调用startActivity相关参数: \n" + "who = [" + who + "], " +
//                "\ncontextThread = [" + contextThread + "], \ntoken = [" + token + "], " +
//                "\ntarget = [" + target + "], \nintent = [" + intent +
//                "], \nrequestCode = [" + requestCode + "], \noptions = [" + options + "]");
//
//
//        LogUtils.d( "------------hook  success------------->");
//        LogUtils.d( "这里可以做你在打开StartActivity方法之前的事情");
//        LogUtils.d( "------------hook  success------------->");
//        LogUtils.d( "");
//
//        @SuppressLint("QueryPermissionsNeeded")
//        List<ResolveInfo> infos = mPackageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL);
//        for (ResolveInfo info : infos) {
//            LogUtils.d( "info " + info.toString());
//        }
//        if (infos.isEmpty()) {
//            //发现是插件的activity，即没有在AndroidManifest.xml中注册的Activity
//            LogUtils.d("没有在AndroidManifest.xml中注册的Activity");
//            LogUtils.d( "旧 intent = " + intent.toString());
////            intent = AMSHookManager.Utils.createProxyIntent(intent);
////            LogUtils.d( "新 intent = " + intent.toString()  + ", ddd " + Objects.requireNonNull(intent.getParcelableExtra(KEY_RAW_INTENT)).toString());
//        }
//        try {
////            @SuppressLint("DiscouragedPrivateApi") Method execMethod = Instrumentation.class.getDeclaredMethod("execStartActivity",
////                    Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class, int.class, Bundle.class);
//            @SuppressLint("DiscouragedPrivateApi")
//            Method execStartActivity = Instrumentation.class.getDeclaredMethod(
//                    "execStartActivity",
//                    Context.class, IBinder.class, IBinder.class, Activity.class,
//                    Intent.class, int.class, Bundle.class
//            );
//            execStartActivity.setAccessible(true);
//            LogUtils.d( "new Intent = " + intent.toString());
//            return (ActivityResult) execStartActivity.invoke(mInstrumentation, who, contextThread, token,
//                    target, intent, requestCode, options);
//        } catch (Exception e) {
//            LogUtils.e("execStartActivity exception e = " + e.toString());
//        }
//        return null;
//    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException,
            IllegalAccessException, ClassNotFoundException {
        LogUtils.d("newActivity className " + className);
        Intent rawIntent = AMSHookManager.Utils.recoverActivityIntent(intent);
        LogUtils.d("newActivity rawIntent = " + rawIntent);
        LogUtils.d("newActivity intent = " + intent);
        if (Loader.isPluginActivity(className) && !className.contains("GameActivity")) {
            //恢复插件的要启动的Activity组件
//            Activity activity = mInstrumentation.newActivity(cl, className, intent);
            Activity activity = mInstrumentation.newActivity(cl, className, intent);
            // 替换 Activity 的 Context


            return activity;
        }
        // 如果是宿主 Activity，直接走原始 ClassLoader
        if (isHostActivity(Loader.getContextManager().getBaseContext(), className)) {
            LogUtils.d("→ Host Activity: " + className);
            return mInstrumentation.newActivity(cl, className, intent);
        }

        // 是第三方 App 的 Activity，不要处理它（避免 ClassNotFound）
        LogUtils.e("→ External Activity Detected, skipping: " + className);
        // 让系统自己处理：直接抛出 ClassNotFound（系统能从目标 App 加载）
//        throw new ClassNotFoundException("Skip loading external activity: " + className);
//        return mInstrumentation.newActivity(cl, className, intent);
        return super.newActivity(cl, className, intent);
    }

    private List<String> cachedHostActivities = null;

    private boolean isHostActivity(Context context, String className) {
        if (cachedHostActivities == null) {
            cachedHostActivities = new ArrayList<>();
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                        context.getPackageName(),
                        PackageManager.GET_ACTIVITIES
                );
                if (packageInfo.activities != null) {
                    for (ActivityInfo activityInfo : packageInfo.activities) {
                        cachedHostActivities.add(activityInfo.name);
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                LogUtils.e("Failed to load host activities: " + e.getMessage());
            }
        }
        return cachedHostActivities.contains(className);
    }


    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        dealReplaceActivityContext(activity);
        super.callActivityOnCreate(activity, icicle);
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle, PersistableBundle persistentState) {
        dealReplaceActivityContext(activity);
        super.callActivityOnCreate(activity, icicle, persistentState);
    }

    private void dealReplaceActivityContext(Activity activity) {
        String className = activity.getClass().getName();
        LogUtils.d("className = " + className);
        if (Loader.isPluginActivity(className) && !className.contains("GameActivity")) {
            replaceActivityContext(activity, Loader.getContextManager().getPluginContext());
        }
    }

    private void replaceActivityContext(Activity activity, Context newContext) {
        LogUtils.d("replaceActivityContext");
        try {
            // 获取 ContextWrapper 的 mBase 字段
            Field baseContextField = ContextWrapper.class.getDeclaredField("mBase");
            baseContextField.setAccessible(true);
            // 替换 mBase 字段为插件的 Context
            LogUtils.d("before replace mBase");
            baseContextField.set(activity, newContext);
            fixActivityResources(activity, newContext);
            LogUtils.d("after replace mBase");
        } catch (Exception e) {
            LogUtils.e(e.toString());
        }
    }

    private void fixActivityResources(Activity activity, Context pluginContext) {
        try {
            Resources pluginRes = pluginContext.getResources();

            // 替换 mResources 字段
            @SuppressLint("DiscouragedPrivateApi") Field mResourcesField = ContextThemeWrapper.class.getDeclaredField("mResources");
            mResourcesField.setAccessible(true);
            mResourcesField.set(activity, pluginRes);

            // 创建并替换 mTheme 字段
            Resources.Theme pluginTheme = pluginRes.newTheme();
            pluginTheme.setTo(pluginRes.newTheme());

            @SuppressLint("SoonBlockedPrivateApi") Field mThemeField = ContextThemeWrapper.class.getDeclaredField("mTheme");
            mThemeField.setAccessible(true);
            mThemeField.set(activity, pluginTheme);

            LogUtils.d("已替换 Activity mResources 和 mTheme");

        } catch (Exception e) {
            LogUtils.e("fixActivityResources error: " + e);
        }
    }


    private boolean inAndroidManifest(String clazzName) {

        return false;
    }

    public List<ActivityInfo> getActivitiesFromInstalledApk(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            // 获取 PackageInfo
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);

            // 获取 Activity 信息
            assert packageInfo.activities != null;
            return Arrays.asList(packageInfo.activities);
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e(e.toString());
        }
        return Collections.emptyList();
    }


    private boolean isPluginActivity(Intent intent) {
        ComponentName component = intent.getComponent();
        if (component == null) {
            return false;
        }
        String clazzName = intent.getComponent().getClassName();
        return isClassAvailable(clazzName);
    }

    private boolean isClassAvailable(String clazzName) {
        try {
            Class.forName(clazzName, false, Loader.getPluginClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }


}
