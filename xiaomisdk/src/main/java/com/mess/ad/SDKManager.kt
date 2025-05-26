package com.mess.ad

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.webkit.WebView
import com.mess.ad.Utils.LogUtils
import com.mess.ad.sdk.AdManager
import com.mess.ad.sdk.interfaces.ISDKInitCallback
import java.lang.ref.WeakReference

class SDKManager private constructor() {
    val TAG: String = SDKManager::class.java.simpleName

    var sdkManager: SDKManager? = null
    private var adManager: AdManager? = null

    private var weakApplication: WeakReference<Application>? = null

    init {
        adManager = AdManager()
    }

    fun getSdkManager(): SDKManager {
        if (sdkManager == null) {
            sdkManager = SDKManager()
        }
        Log.d(TAG, "获取SDKManager实例")
        return sdkManager as SDKManager
    }

    fun getAdManager(): AdManager? {
        return adManager
    }

    fun setAdManager(adManager: AdManager?) {
        this.adManager = adManager
    }


    fun initApplication(application: Application, callback: ISDKInitCallback) {
        LogUtils.setDEBUG(true)
        LogUtils.setTAG("AdSDKPlugin")
        Log.d(TAG, "初始化SDKManager application")
        this.weakApplication = WeakReference(application)
    }



    fun initSDK(activity: Activity?) {
        Log.d(TAG, "SDKManager initSDK")
        adManager.initAd(weakApplication!!.get(), object : IInitSDKListener() {
            override fun onInitFailed(code: Int, msg: String) {
                Log.d(TAG, "onInitFailed$code, msg = $msg")
            }

            override fun onInitSuccess() {
                Log.d(TAG, "onInitSuccess")
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
        })

        adManager.initAllTypeAd(activity)
    }

    fun bindBackClickListener(activity: Activity?) {
    }

    fun checkStatus(context: Context?, iInitSDKListener: IInitSDKListener?) {
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

    fun startActivityTest(activity: Activity) {
        val intent = Intent(activity, TestActivity::class.java)
        activity.startActivity(intent)
    }
}