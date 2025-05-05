package com.mess.ad.vivo;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.mess.ad.Utils.AppUtils;
import com.mess.ad.Utils.CityUtils;
import com.mess.ad.Utils.DisplayUtil;
import com.mess.ad.Utils.LogUtils;
import com.mess.ad.Utils.PhoneInfoGetter;
import com.mess.ad.Utils.TextUtils;
import com.mess.ad.constant.AdIds;
import com.mess.ad.sdk.ad.IBannerAd;
import com.mess.ad.sdk.bean.AdInfo;
import com.mess.ad.sdk.listener.IBannerListener;
import com.vivo.mobilead.unified.banner.UnifiedVivoBannerAd;
import com.vivo.mobilead.unified.banner.UnifiedVivoBannerAdListener;
import com.vivo.mobilead.unified.base.AdParams;
import com.vivo.mobilead.unified.base.VivoAdError;

import java.lang.ref.WeakReference;

public class CustomBannerAd implements IBannerAd {

    private final static String TAG = CustomBannerAd.class.getName();

    public static final int SHOW_BOTTOM_BANNER_MSG = 196609;
    public static final int SHOW_BOTTOM_NATIVE_BANNER_MSG = 196610;
    public static final int SHOW_TOP_BANNER_MSG = 196611;
    public static final int SHOW_TOP_NATIVE_BANNER_MSG = 196612;

    private WeakReference<Activity> curActivity;

    private FrameLayout bannerBottomContainer;

    private UnifiedVivoBannerAd vivoBannerAd;

    private IBannerListener bannerListener;

    private AdInfo bannerAdInfo;

    public CustomBannerAd() {

    }

    @Override
    public void initBannerAd(Activity activity) {
        LogUtils.d("initBannerAd");
        curActivity = new WeakReference<>(activity);
        initAdParams();
        initBannerContainer(activity);
    }

    @Override
    public void hideBanner() {

    }

    private void initAdParams() {

    }

    @Override
    public void initBannerContainer(Activity activity) {
        LogUtils.d("initBannerContainer");
        FrameLayout frameLayout = activity.findViewById(android.R.id.content);
        bannerBottomContainer = new FrameLayout(activity);
        LogUtils.d("initBannerContainer " + PhoneInfoGetter.getOrientation(activity));
        FrameLayout.LayoutParams relLayoutBottomParams = null;
        if (PhoneInfoGetter.getOrientation(activity) == 0) {
            relLayoutBottomParams = new FrameLayout.LayoutParams(DisplayUtil.dip2px(activity, 320.0f), -2);
        } else {
            relLayoutBottomParams = new FrameLayout.LayoutParams(-2, -2);
        }
        if (AppUtils.isScreenLand(activity)) {
            Double.isNaN(activity.getResources().getDisplayMetrics().widthPixels);
        } else {
            Double.isNaN(activity.getResources().getDisplayMetrics().widthPixels);
        }
        relLayoutBottomParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        this.bannerBottomContainer.setLayoutParams(relLayoutBottomParams);
        frameLayout.addView(this.bannerBottomContainer);
    }

    @Override
    public void setBannerListener(IBannerListener bannerListener) {
        this.bannerListener = bannerListener;
    }

    @Override
    public void loadBannerAd() {
        //此处即可调用showAd展示广告
        LogUtils.d("loadBannerAd");
        String posId = AdIds.BANNER_POSITION_ID;
        if (TextUtils.isEmpty(posId)) {
//            iconAdInfo.setAdIndex(iconAdInfo.getAdIndex() + 1);
            addAdInfo(bannerAdInfo);
            return;
        }
        AdParams.Builder builder = new AdParams.Builder(posId);
        builder.setRefreshIntervalSeconds(30);
        AdParams adParams = builder.build();
        if (vivoBannerAd != null) {
            vivoBannerAd.destroy();
        }
        vivoBannerAd = new UnifiedVivoBannerAd(curActivity.get(), adParams, unifiedVivoBannerAdListener);
        vivoBannerAd.loadAd();
    }

    private void showAd(View adView) {
        if (adView != null && bannerBottomContainer != null) {
            bannerBottomContainer.removeAllViews();
            bannerBottomContainer.addView(adView);
        } else {
            LogUtils.d("Ad view is not ready or container is not initialized.");
        }
    }

    private void addAdInfo(AdInfo adInfo) {
        adInfo.setAdIndex((adInfo.getAdIndex() + 1) % adInfo.getAdIds().size());
    }

    private final Handler bannerHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() { // from class: laputalib.sdk.business.BannerAdBusiness.1
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            if (AppUtils.isRunningForeground(curActivity.get())) {
                switch (message.what) {
                    case SHOW_BOTTOM_BANNER_MSG /* 196609 */:
                        hideBottomAllBanner();
                        showBottomBanner(SHOW_BOTTOM_BANNER_MSG);
                        break;
                    case SHOW_BOTTOM_NATIVE_BANNER_MSG /* 196610 */:
                        hideBottomAllBanner();
                        showBottomBanner(SHOW_BOTTOM_NATIVE_BANNER_MSG);
                        break;
                }
                return true;
            } else if (bannerAdInfo != null) {
                bannerHandler.sendEmptyMessageDelayed(SHOW_BOTTOM_BANNER_MSG, bannerAdInfo.getShowInterval());
                return true;
            } else {
                bannerHandler.sendEmptyMessageDelayed(SHOW_BOTTOM_BANNER_MSG, 40000L);
                return true;
            }
        }
    });

    private void hideBottomAllBanner() {
        if (this.bannerBottomContainer != null) {
            this.bannerBottomContainer.removeAllViews();
        }
    }

    public void showBottomBanner(int i) {
        refreshBottomPollBanner(SHOW_BOTTOM_BANNER_MSG);
        loadBannerAd();
    }

    public void refreshBottomPollBanner(int what) {
        if (bannerAdInfo == null) {
            removeBottomBannerMsg();
            this.bannerHandler.sendEmptyMessageDelayed(what, 30000L);
            return;
        }
        long showInterval = bannerAdInfo.getShowInterval();
        LogUtils.d("下方Banner刷新间隔时间：" + (showInterval / 1000));
        FrameLayout frameLayout = this.bannerBottomContainer;
        if (frameLayout != null && frameLayout.getParent() != null) {
            ((FrameLayout) this.bannerBottomContainer.getParent()).removeView(this.bannerBottomContainer);
        }
        removeBottomBannerMsg();
        this.bannerHandler.sendEmptyMessageDelayed(what, showInterval);
    }


    public void removeBottomBannerMsg() {
        this.bannerHandler.removeMessages(SHOW_BOTTOM_BANNER_MSG);
    }

    private final UnifiedVivoBannerAdListener unifiedVivoBannerAdListener = new UnifiedVivoBannerAdListener() {
        @Override
        public void onAdShow() {
            LogUtils.d("onAdShow");
        }

        @Override
        public void onAdFailed(@NonNull VivoAdError vivoAdError) {
            LogUtils.d("onAdFailed, error = " +vivoAdError.getMsg() );
            addAdInfo(bannerAdInfo);
        }

        @Override
        public void onAdReady(@NonNull View adView) {
            LogUtils.d("onAdReady");
//            BannerAd.this.adView = adView;
            //此处即可调用showAd展示广告
            showAd(adView);
        }

        @Override
        public void onAdClick() {
            LogUtils.d("onAdClick");
            bannerListener.clickBanner();
        }

        @Override
        public void onAdClose() {
            LogUtils.d("onAdClose");
            bannerListener.closeBanner();
        }
    };


}
