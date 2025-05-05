package com.mess.ad.sdk.action;

public interface IBannerActionListener {

    void clickBanner();

    void closeBanner();

    void failedBanner();

    void readyBanner();

    void showBanner();

    void skipBanner();

}
