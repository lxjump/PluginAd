package com.mess.ad;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.lang.reflect.Method;

public class PluginContextWrapper extends ContextWrapper {
    private final ClassLoader pluginClassLoader;
    private final Resources pluginResources;

    public PluginContextWrapper(Context base, String apkPath, ClassLoader classLoader) {
        super(base);
        this.pluginClassLoader = classLoader;
        this.pluginResources = createPluginResources(base, apkPath);
    }

    @Override
    public Context getBaseContext() {
        return super.getBaseContext();
    }

    // ✅ 重写 getClassLoader()，确保插件代码正确加载
    @Override
    public ClassLoader getClassLoader() {
        return pluginClassLoader != null ? pluginClassLoader : super.getClassLoader();
    }

    // ✅ 重写 getResources()，确保插件加载自己的资源，而不是宿主的资源
    @Override
    public Resources getResources() {
        return pluginResources != null ? pluginResources : super.getResources();
    }

    // ✅ 重写 getAssets()，确保插件可以访问自己的 assets
    @Override
    public AssetManager getAssets() {
        return pluginResources != null ? pluginResources.getAssets() : super.getAssets();
    }

    // ✅ 加载插件 APK 的 Resources
    private static Resources createPluginResources(Context base, String apkPath) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPathMethod = assetManager.getClass().getDeclaredMethod("addAssetPath", String.class);
            addAssetPathMethod.invoke(assetManager, apkPath);
            return new Resources(assetManager, base.getResources().getDisplayMetrics(), base.getResources().getConfiguration());
        } catch (Exception e) {
            e.printStackTrace();
            return base.getResources();
        }
    }
}

