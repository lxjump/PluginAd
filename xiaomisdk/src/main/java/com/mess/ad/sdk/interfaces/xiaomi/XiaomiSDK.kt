package com.mess.ad.sdk.interfaces.xiaomi

import android.app.Application
import android.content.Context
import com.mess.ad.sdk.interfaces.IADShowCallback
import com.mess.ad.sdk.interfaces.IAccountListener
import com.mess.ad.sdk.interfaces.ISDKInitCallback
import com.mess.ad.sdk.interfaces.ISdk

class XiaomiSDK : ISdk {

    override fun initSDK(application: Application, callback: ISDKInitCallback) {
        TODO("Not yet implemented")
    }

    override fun login(listener: IAccountListener) {
        TODO("Not yet implemented")
    }

    override fun logout() {
        TODO("Not yet implemented")
    }

    override fun initAdSDK(context: Context) {
        TODO("Not yet implemented")
    }

    override fun initBanner(callback: ISDKInitCallback) {
        TODO("Not yet implemented")
    }

    override fun showBanner(callback: IADShowCallback) {
        TODO("Not yet implemented")
    }

    override fun initInterstitialAd(callback: ISDKInitCallback) {
        TODO("Not yet implemented")
    }

    override fun showInterstitialAd(callback: IADShowCallback) {
        TODO("Not yet implemented")
    }

    override fun initRewardAd(callback: ISDKInitCallback) {
        TODO("Not yet implemented")
    }

    override fun showRewardAd(callback: IADShowCallback) {
        TODO("Not yet implemented")
    }

    override fun initSplashAd(callback: ISDKInitCallback) {
        TODO("Not yet implemented")
    }

    override fun showSplashAd(callback: IADShowCallback) {
        TODO("Not yet implemented")
    }

    override fun initFloatAd(callback: ISDKInitCallback) {
        TODO("Not yet implemented")
    }

    override fun showFloatAd(callback: IADShowCallback) {
        TODO("Not yet implemented")
    }

}