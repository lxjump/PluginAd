package com.mess.loader.hook.contentprovider;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;

import com.mess.loader.utils.LogUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class ContentProviderHookManager {
    private static List<ProviderInfo> providerInfoList = new LinkedList<>();

    public static void init(Application context, String apkFilePath) {
        preloadParseContentProvider(apkFilePath);
        // 便于classloader加载，修改
        String packageName = context.getBaseContext().getPackageName();
        for (ProviderInfo providerInfo : providerInfoList) {
            providerInfo.applicationInfo.packageName = packageName;
        }
        installContentProvider(context);
    }

    /**
     * 将ContentProvider安装到进程中
     */
    private static void installContentProvider(Context context) {
        try {
            //获取到ActivityThread
            @SuppressLint("PrivateApi")
            Class<?> ActivityThreadClass = Class.forName("android.app.ActivityThread");
            @SuppressLint("DiscouragedPrivateApi")
            Field sCurrentActivityThreadField = ActivityThreadClass.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThreadField.setAccessible(true);
            Object ActivityThread = sCurrentActivityThreadField.get(null);
            // 调用 installContentProviders()
            LogUtils.d("调用 installContentProviders()");
            @SuppressLint("DiscouragedPrivateApi")
            Method installContentProvidersMethod = ActivityThreadClass.getDeclaredMethod("installContentProviders", Context.class, List.class);
            installContentProvidersMethod.setAccessible(true);
            installContentProvidersMethod.invoke(ActivityThread, context, providerInfoList);
        } catch (Exception e) {
            LogUtils.d("exception = " + e.toString());
//            throw new RuntimeException(e);
        }

    }

    /**
     * 解析插件中的service
     *
     * @param apkFilePath
     */
    private static void preloadParseContentProvider(String apkFilePath) {
        try {
            // 先获取PackageParser对象
            @SuppressLint("PrivateApi")
            Class<?> packageParserClass = Class.forName("android.content.pm.PackageParser");
            Object packageParser = packageParserClass.newInstance();
            //接着获取PackageParser.Package
            @SuppressLint("DiscouragedPrivateApi")
            Method parsePackageMethod = packageParserClass.getDeclaredMethod("parsePackage", File.class, int.class);
            parsePackageMethod.setAccessible(true);
            Object packageParser$package = parsePackageMethod.invoke(packageParser, new File(apkFilePath), PackageManager.GET_RECEIVERS);
            // 接着获取到Package中的receivers列表
            assert packageParser$package != null;
            Class<?> packageParser$package_Class = packageParser$package.getClass();
            Field providersField = packageParser$package_Class.getDeclaredField("providers");
            providersField.setAccessible(true);
            List providersList = (List) providersField.get(packageParser$package);
            @SuppressLint("PrivateApi")
            Class<?> packageParser$Provider_Class = Class.forName("android.content.pm.PackageParser$Provider");
            // 获取 name
            @SuppressLint("DiscouragedPrivateApi")
            Field infoField = packageParser$Provider_Class.getDeclaredField("info");
            infoField.setAccessible(true);
            for (Object provider : providersList) {
                ProviderInfo info = (ProviderInfo) infoField.get(provider);
                providerInfoList.add(info);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
