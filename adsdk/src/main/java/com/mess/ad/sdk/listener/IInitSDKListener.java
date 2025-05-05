package com.mess.ad.sdk.listener;

public interface IInitSDKListener {

    void onInitFailed(int code, String msg);

    void onInitSuccess();

}
