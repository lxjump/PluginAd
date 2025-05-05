package com.mess.ad;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.mess.ad.Utils.LogUtils;
import com.vivo.mobilead.manager.VInitCallback;
import com.vivo.mobilead.manager.VivoAdManager;
import com.vivo.mobilead.model.VAdConfig;
import com.vivo.mobilead.model.VCustomController;
import com.vivo.mobilead.model.VLocation;
import com.vivo.mobilead.unified.base.VivoAdError;

public class App extends Application {

    private static App instance;

    @Override // android.content.ContextWrapper
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
    }

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        LogUtils.d("onCreate");
        instance = this;
//        SDKManager.getSdkManager().initApplication(this, null);
    }

    public static Context getPluginContext() {
        return instance;
    }

}
