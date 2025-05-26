package com.mess.ad.sdk.interfaces.listener

interface IInterstitialListener {

    fun onInsertClick()

    fun onInsertClose()

    fun onInsertShow()

    fun onInsertShowFailed(code: Int, str: String?)

}