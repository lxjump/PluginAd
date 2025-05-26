package com.mess.loader;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.content.res.Resources;

public class PluginContext extends ContextWrapper {
    private final Resources pluginResources;
    private final AssetManager pluginAssetManager;
    private final ClassLoader pluginClassLoader;

    public PluginContext(Context base, Resources pluginResources, AssetManager pluginAssetManager, ClassLoader pluginClassLoader) {
        super(base);
        this.pluginResources = pluginResources;
        this.pluginAssetManager = pluginAssetManager;
        this.pluginClassLoader = pluginClassLoader;
    }

    @Override
    public Resources getResources() {
        return pluginResources;
    }

    @Override
    public AssetManager getAssets() {
        return pluginAssetManager;
    }

    @Override
    public ClassLoader getClassLoader() {
        return pluginClassLoader;
    }

    @Override
    public String getPackageName() {
        return "com.mess.ad"; // 插件包名
    }
}
