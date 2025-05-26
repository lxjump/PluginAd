package com.mess.ad.sdk.interfaces.listener

interface IRewardListener {

    fun onAdClick()

    fun onAdClickSkip()

    fun onAdClose()

    fun onAdReward()

    fun onAdShow()

    fun onAdShowFailed(code: Int, str: String?)
}