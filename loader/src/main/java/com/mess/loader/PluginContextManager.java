package com.mess.loader;

import android.content.Context;
import android.util.Log;

import com.mess.loader.utils.LogUtils;

public class PluginContextManager {
    private final PluginContext pluginContext;
    private final Context baseContext;

    public PluginContextManager(PluginContext pluginContext, Context baseContext) {
        LogUtils.d("baseContext = " + baseContext);
        LogUtils.d("pluginContext = " + pluginContext);
        this.pluginContext = pluginContext;
        this.baseContext = baseContext;
    }

    public PluginContext getPluginContext() {
        return pluginContext;
    }

    public Context getBaseContext() {
        LogUtils.d("baseContext = " + baseContext);
        return baseContext;
    }
}

