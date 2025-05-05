package com.mess.ad;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.webkit.WebView;

import com.mess.ad.Utils.LogUtils;
import com.mess.ad.sdk.listener.IInitSDKListener;
import com.mess.ad.sdk.listener.ITpInitSDKListener;
import com.mess.ad.vivo.UnionSDK;

import java.lang.ref.WeakReference;

public class SDKManager {

    private final static String TAG = SDKManager.class.getSimpleName();

    private static SDKManager sdkManager;
    private AdManager adManager;

    private WeakReference<Application> weakApplication;

    private SDKManager() {
        adManager = new AdManager();
    }

    public static SDKManager getSdkManager() {
        if (sdkManager == null) {
            sdkManager = new SDKManager();
        }
        Log.d(TAG, "获取SDKManager实例");
        return sdkManager;
    }

    public AdManager getAdManager() {
        return adManager;
    }

    public void setAdManager(AdManager adManager) {
        this.adManager = adManager;
    }


    public void initApplication(Application application, IInitSDKListener iInitSDKListener) {
        LogUtils.setDEBUG(true);
        LogUtils.setTAG("AdSDKPlugin");
        Log.d(TAG, "初始化SDKManager application");
        this.weakApplication = new WeakReference<>(application);
        UnionSDK.getInstance().privateAgreed(application.getBaseContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = Application.getProcessName();
            if (!application.getPackageName().equals(processName)) {
                WebView.setDataDirectorySuffix(processName);
            }
        }
    }

    public void initConfig(Activity activity, ITpInitSDKListener tpInitSDKListener) {
        Log.d(TAG, "initConfig");
    }


    public void initSDK(Activity activity) {
        Log.d(TAG, "SDKManager initSDK");
        this.adManager.initAd(weakApplication.get(), new IInitSDKListener() {
            @Override
            public void onInitFailed(int code, String msg) {
                Log.d(TAG, "onInitFailed" + code + ", msg = " + msg);
            }

            @Override
            public void onInitSuccess() {
                Log.d(TAG, "onInitSuccess");
//                UnionSDK.getInstance().login(activity, new ILoginListener() {
//                    @Override
//                    public void onLoginFailed(int i, String str) {
//                        Log.d(TAG, "failed code = " + i + " msg = " + str);
//                    }
//
//                    @Override
//                    public void onLoginSuccess() {
//                        Log.d(TAG, "vivo sdk Loging success");
//                        SDKManager.getSdkManager().getAdManager().initShowAd();
//                    }
//                });
            }
        });

        adManager.initAllTypeAd(activity);
    }

    public void bindBackClickListener(Activity activity) {

    }

    public void checkStatus(Context context, final IInitSDKListener iInitSDKListener) {
        // 配置逻辑先注释，后面实现
//        if (!Config.getInstance().isReady()) {
//            Config.getInstance().init(context, new IInitSDKListener() { // from class: laputalib.sdk.vivoNew.FormProxy.11
//                @Override // laputalib.sdk.m.listener.IInitSDKListener
//                public void onInitFailed(int i, String str) {
//                    IInitSDKListener iInitSDKListener2 = iInitSDKListener;
//                    if (iInitSDKListener2 != null) {
//                        iInitSDKListener2.onInitFailed(-4, "SDK初始化失败了");
//                    }
//                }
//
//                @Override // laputalib.sdk.m.listener.IInitSDKListener
//                public void onInitSuccess() {
//                    FormProxy formProxy = FormProxy.this;
//                    formProxy.initAd((Context) formProxy.weakApplication.get(), iInitSDKListener);
//                }
//            });
//        } else
        // ToDo 通过服务器获取配置
//        if (!this.mInit) {
//            initAd(this.weakApplication.get(), iInitSDKListener);
//        } else if (iInitSDKListener != null) {
//            iInitSDKListener.onInitSuccess();
//        }



    }
    public void startActivityTest(Activity activity) {
        Intent intent = new Intent(activity, TestActivity.class);
        activity.startActivity(intent);
    }
}
