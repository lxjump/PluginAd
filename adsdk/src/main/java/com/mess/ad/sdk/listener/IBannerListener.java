package com.mess.ad.sdk.listener;

public interface IBannerListener {

    void clickBanner();

    void closeBanner();

    void failedBanner();

    void readyBanner();

    void showBanner();

    void skipBanner();

}
