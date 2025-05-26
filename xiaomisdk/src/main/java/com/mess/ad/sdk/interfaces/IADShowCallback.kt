package com.mess.ad.sdk.interfaces

interface IADShowCallback {

    fun onAdSuccess()

    fun onAdFailed()

    /**
     * 广告缓存回调
     */
    fun onAdCached()

    fun onAdReady()

    fun onAdClosed()

    fun onAdClick()

    /**
     * 激励、插屏广告奖励发放回调
     */
    fun onRewardVerify()

}