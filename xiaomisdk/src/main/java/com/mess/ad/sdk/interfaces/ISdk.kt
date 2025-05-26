package com.mess.ad.sdk.interfaces

import android.app.Application
import android.content.Context

interface ISdk {

    fun initSDK(application: Application, callback: ISDKInitCallback)

    fun login(listener: IAccountListener)

    fun logout()

    fun initAdSDK(context: Context)

    fun initBanner(callback: ISDKInitCallback)
    fun showBanner(callback: IADShowCallback)

    fun initInterstitialAd(callback: ISDKInitCallback)
    fun showInterstitialAd(callback: IADShowCallback)

    fun initRewardAd(callback: ISDKInitCallback)
    fun showRewardAd(callback: IADShowCallback)

    fun initSplashAd(callback: ISDKInitCallback)
    fun showSplashAd(callback: IADShowCallback)

    fun initFloatAd(callback: ISDKInitCallback)
    fun showFloatAd(callback: IADShowCallback)

}