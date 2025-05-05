package com.mess.ad.sdk.listener;

public interface IFullVideoListener {

    void onAdClick();

    void onAdClickSkip();

    void onAdClose();

    void onAdReward();

    void onAdShow();

    void onAdShowFailed(int code, String str);

}
