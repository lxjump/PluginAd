package com.mess.loader;


import android.app.Application;
import android.content.Context;

import com.mess.loader.utils.LogUtils;


public class PluginApplication extends Application {

    private final String TAG = "Mess";

    @Override
    public void onCreate() {
        LogUtils.d("ModApplication::onCreate");
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context context) {
        LogUtils.setTAG(TAG);
        LogUtils.setDEBUG(true);
        LogUtils.d("ModApplication::attachBaseContext");
        super.attachBaseContext(context);
        Loader.getLoaderInstance().loadMod(context, this);

    }
}
