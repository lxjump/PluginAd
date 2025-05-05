package com.mess.ad.sdk.ad;

import android.app.Activity;

import com.mess.ad.sdk.listener.IBannerListener;

public interface IBannerAd {

    void initBannerAd(Activity activity);

    void hideBanner();

    void setBannerListener(IBannerListener bannerListener);

    void loadBannerAd();

    void initBannerContainer(Activity activity);

}
