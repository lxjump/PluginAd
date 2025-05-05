package com.mess.loader.hook.resource;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.mess.loader.utils.LogUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class AssetManagerCompat {
    private static final int SDK_VERSION = Build.VERSION.SDK_INT;

    public static AssetManager createWithPath(Context context, String apkPath)
            throws Exception {
        if (SDK_VERSION >= Build.VERSION_CODES.O) {
            // 使用反射调用 Android 8.0+ 的新 API
            return createWithApkAssets(context, apkPath);
        } else {
            // 传统反射方式 (API 21-25)
            return createLegacyAssetManager(apkPath, context);
        }
    }

    /**
     * 新 API 实现（需要双重反射）
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static AssetManager createWithApkAssets(Context context, String apkPath)
            throws Exception {
        try {
            // 第1层反射：获取 Builder 类
            @SuppressLint("PrivateApi") Class<?> builderClass = Class.forName("android.content.res.AssetManager$Builder");
            LogUtils.d("before Builder init");
            Constructor<?> constructor = builderClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object builder = constructor.newInstance();
            invokeSetNoInit(builderClass, builder);
            LogUtils.d("after Builder init");

            // 第2层反射：ApkAssets 加载
            @SuppressLint("PrivateApi") Class<?> apkAssetsClass = Class.forName("android.content.res.ApkAssets");
            @SuppressLint("SoonBlockedPrivateApi") Method loadFromPath = apkAssetsClass.getDeclaredMethod("loadFromPath", String.class);

            // 加载外部 APK
            Object externalApk = loadFromPath.invoke(null, apkPath);
            Method addApkAssets = builderClass.getMethod("addApkAssets", apkAssetsClass);
            addApkAssets.invoke(builder, externalApk);

            // 加载系统资源（防止覆盖）
            Object systemApk = loadFromPath.invoke(null, context.getApplicationInfo().sourceDir);
            addApkAssets.invoke(builder, systemApk);

            // 构建最终 AssetManager
            Method buildMethod = builderClass.getMethod("build");
            return (AssetManager) buildMethod.invoke(builder);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            // 如果新 API 反射失败，尝试回退旧方法
            return createLegacyAssetManager(apkPath, context);
        }
    }

    /**
     * 传统反射实现 (API 21+)
     */
    private static AssetManager createLegacyAssetManager(String apkPath, Context context)
            throws Exception {
        // 1. 创建空 AssetManager 实例
        Constructor<?> constructor = AssetManager.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        AssetManager am = (AssetManager) constructor.newInstance();

        // 2. 获取 addAssetPath 方法
        @SuppressLint("DiscouragedPrivateApi") Method addAssetPath = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
        addAssetPath.setAccessible(true);

        // 3. 添加外部路径
        addAssetPath.invoke(am, apkPath);

        // 4. 添加系统路径（可选）
        addAssetPath.invoke(am, context.getApplicationInfo().sourceDir);

        return am;
    }

    /**
            * 调用 setNoInit() 方法
     */
    private static void invokeSetNoInit(Class<?> builderClass, Object builder)
            throws Exception {
        try {
            Method setNoInit = builderClass.getMethod("setNoInit");
            setNoInit.invoke(builder);
        } catch (NoSuchMethodException e) {
            // 处理 Android 12 以下版本差异
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                Method setCompatNoInit = builderClass.getMethod("setCompatNoInit");
                setCompatNoInit.invoke(builder);
            } else {
                throw e;
            }
        }
    }

    /**
     * 创建合并资源的 Resources 对象
     */
    public static Resources createResources(Context context, AssetManager am) {
        Resources superRes = context.getResources();
        return new Resources(am, superRes.getDisplayMetrics(), superRes.getConfiguration());
    }
}
