package com.mess.ad.sdk.listener;

public interface IIconListener {

    void onIconClose();

    void onIconShowFailed(int code, String msg);

    void onIconClick();

}
