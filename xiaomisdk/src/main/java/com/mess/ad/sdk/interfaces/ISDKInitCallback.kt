package com.mess.ad.sdk.interfaces

/**
 * sdk 初始化回调(登录、广告sdk splash banner float reward interstitial)
 */
interface ISDKInitCallback {

    fun onSuccess()

    fun onFailed(message: String)

}