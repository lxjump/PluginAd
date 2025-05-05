package com.mess.ad.vivo;

import android.app.Activity;
import android.util.Log;

import com.mess.ad.Utils.LogUtils;
import com.mess.ad.constant.AdIds;
import com.mess.ad.sdk.bean.AdInfo;
import com.mess.ad.sdk.listener.IInterstitialListener;
import com.vivo.mobilead.unified.base.AdParams;
import com.vivo.mobilead.unified.base.VivoAdError;
import com.vivo.mobilead.unified.base.callback.MediaListener;
import com.vivo.mobilead.unified.interstitial.UnifiedVivoInterstitialAd;
import com.vivo.mobilead.unified.interstitial.UnifiedVivoInterstitialAdListener;

import java.lang.ref.WeakReference;

public class InterstitialAd {

    private static final String TAG = InterstitialAd.class.getSimpleName();
    private AdParams videoAdParams;
    private AdParams imageAdParams;
    private UnifiedVivoInterstitialAd vivoInterstitialAd;
    //记录加载的是图片还是视频
    private int materialType;

    private static InterstitialAd instance;

    private WeakReference<Activity> curActivity;

    private AdInfo curInfo;

    private IInterstitialListener interstitialListener;


    private InterstitialAd() {

    }

    public static InterstitialAd getInstance() {
        if (instance == null) {
            instance = new InterstitialAd();
        }
        return instance;
    }

    public void init(Activity activity) {
        curActivity = new WeakReference<>(activity);
    }

    private void initInterAdParams() {


        String imagePosId = AdIds.INTERSTITIAL_POSITION_ID;

        AdParams.Builder imageBuilder = new AdParams.Builder(imagePosId);
        // 设置底价
        imageAdParams = imageBuilder.build();
    }


    public void loadInterstitialAd(IInterstitialListener interstitialListener) {
        initInterAdParams();
        this.interstitialListener = interstitialListener;
        vivoInterstitialAd = new UnifiedVivoInterstitialAd(curActivity.get(), imageAdParams, interstitialAdListener);
        vivoInterstitialAd.setMediaListener(mediaListener);
        vivoInterstitialAd.loadAd();
    }


    private void showInterstitialAd() {
        if (vivoInterstitialAd != null) {
            vivoInterstitialAd.showAd();
        }
    }

    private void addAdInfo(AdInfo adInfo) {
        if (adInfo == null) {
            return;
        }
        adInfo.setAdIndex((adInfo.getAdIndex() + 1) % adInfo.getAdIds().size());
    }


    private UnifiedVivoInterstitialAdListener interstitialAdListener = new UnifiedVivoInterstitialAdListener() {
        @Override
        public void onAdReady() {
            LogUtils.d("onAdReady");
            showInterstitialAd();

        }

        @Override
        public void onAdFailed(VivoAdError error) {
            LogUtils.d("onAdFailed: " + error.toString());
            if (interstitialListener != null) {
                interstitialListener.onInsertShowFailed(error.getCode(), error.getMsg());
            }
            if (error.getCode() == 4012) {
                Log.e(TAG, "插屏广告id:" + " onAdFailed:" + error.toString());
            } else {
                Log.e(TAG, "插屏广告 onAdFailed:" + error.toString());
            }
            Log.e(TAG, "vivo插屏加载失败:" + error.toString());
            addAdInfo(curInfo);
        }

        @Override
        public void onAdClick() {
            LogUtils.d("onAdClick");
            if (interstitialListener != null) {
                interstitialListener.onInsertClick();
            }
        }

        @Override
        public void onAdShow() {
            LogUtils.d("onAdShow");
            if (interstitialListener != null) {
                interstitialListener.onInsertShow();
            }
        }

        @Override
        public void onAdClose() {
            LogUtils.d("onAdClose");
            if (interstitialListener != null) {
                interstitialListener.onInsertClose();
            }
        }
    };

    private MediaListener mediaListener = new MediaListener() {
        @Override
        public void onVideoStart() {
            LogUtils.d("onVideoStart");
        }

        @Override
        public void onVideoPause() {
            LogUtils.d("onVideoPause");
        }

        @Override
        public void onVideoPlay() {
            LogUtils.d("onVideoPlay");
        }

        @Override
        public void onVideoError(VivoAdError error) {
            LogUtils.d("onVideoError: " + error.toString());
        }

        @Override
        public void onVideoCompletion() {
            LogUtils.d("onVideoCompletion");
        }

        @Override
        public void onVideoCached() {
            LogUtils.d("onVideoCached");
        }
    };


}
