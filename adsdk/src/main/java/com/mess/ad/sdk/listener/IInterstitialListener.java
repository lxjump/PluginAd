package com.mess.ad.sdk.listener;

public interface IInterstitialListener {

    void onInsertClick();

    void onInsertClose();

    void onInsertShow();

    void onInsertShowFailed(int code, String str);

}
