package com.mess.ad.sdk.interfaces.listener

interface IFloatAdListener {

    fun onIconClose()

    fun onIconShowFailed(code: Int, msg: String?)

    fun onIconClick()
}