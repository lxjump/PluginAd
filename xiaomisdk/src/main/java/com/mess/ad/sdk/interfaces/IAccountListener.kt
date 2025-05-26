package com.mess.ad.sdk.interfaces

interface IAccountListener {

    fun onLoginSuccess()

    fun onLoginFailed(message: String)

    fun onLoginCancel(message: String)

    fun onLogout()

}