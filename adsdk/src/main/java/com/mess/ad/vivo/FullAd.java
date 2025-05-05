package com.mess.ad.vivo;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.mess.ad.Utils.LogUtils;
import com.mess.ad.constant.AdIds;
import com.mess.ad.sdk.bean.AdInfo;
import com.mess.ad.sdk.listener.IFullVideoListener;
import com.vivo.mobilead.unified.base.AdParams;
import com.vivo.mobilead.unified.base.VivoAdError;
import com.vivo.mobilead.unified.base.callback.MediaListener;
import com.vivo.mobilead.unified.interstitial.UnifiedVivoInterstitialAd;
import com.vivo.mobilead.unified.interstitial.UnifiedVivoInterstitialAdListener;

import java.lang.ref.WeakReference;

public class FullAd {

    private static final String TAG = FullAd.class.getSimpleName();

    private static final int LOAD_FULL_VIDEO_MSG = 393216;

    private AdParams videoAdParams;
    private AdParams imageAdParams;
    private UnifiedVivoInterstitialAd vivoInterstitialAd;
    //记录加载的是图片还是视频
    private int materialType;

    private static FullAd instance;

    private WeakReference<Activity> curActivity;

    private AdInfo curInfo;

    private IFullVideoListener fullVideoListener;

    private final Handler mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() { // from class: laputalib.sdk.vivoNew.ProxyFull.1
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            if (message.what != LOAD_FULL_VIDEO_MSG) {
                return true;
            }
            return true;
        }
    });


    private FullAd() {

    }

    public static FullAd getInstance() {
        if (instance == null) {
            instance = new FullAd();
        }
        return instance;
    }

    public void init(Activity activity) {
        curActivity = new WeakReference<>(activity);
    }

    private void initFullAdParams() {

        String fullPosId = AdIds.VIDEO_INTERSTITIAL_POSITION_ID;
        AdParams.Builder videoBuilder = new AdParams.Builder(fullPosId);
        // 设置底价
        videoAdParams = videoBuilder.build();
    }


    public void loadFullAd(IFullVideoListener fullVideoListener) {
        LogUtils.d("showFullAd");
        initFullAdParams();
        this.fullVideoListener = fullVideoListener;
        materialType = 2;
        vivoInterstitialAd = new UnifiedVivoInterstitialAd(curActivity.get(), videoAdParams, interstitialAdListener);
        vivoInterstitialAd.setMediaListener(mediaListener);
        vivoInterstitialAd.loadVideoAd();
    }

    private void showFullAd() {
        if (vivoInterstitialAd != null) {
            vivoInterstitialAd.showVideoAd(curActivity.get());
        }
    }


    private void addAdInfo(AdInfo adInfo) {
        if (adInfo == null) {
            return;
        }
        adInfo.setAdIndex((adInfo.getAdIndex() + 1) % adInfo.getAdIds().size());
    }


    private final UnifiedVivoInterstitialAdListener interstitialAdListener = new UnifiedVivoInterstitialAdListener() {
        @Override
        public void onAdReady() {
            LogUtils.d("onAdReady");
            showFullAd();
        }

        @Override
        public void onAdFailed(VivoAdError error) {
            LogUtils.d("onAdFailed: " + error.toString());
            if (fullVideoListener != null) {
                fullVideoListener.onAdShowFailed(error.getCode(), error.getMsg());
            }
            if (error.getCode() == 4012) {
                LogUtils.e("插屏广告id:" + " onAdFailed:" + error.toString());
            } else {
                LogUtils.e("插屏广告 onAdFailed:" + error.toString());
            }
            LogUtils.e("vivo插屏加载失败:" + error.toString());
            addAdInfo(curInfo);
        }

        @Override
        public void onAdClick() {
            LogUtils.d("onAdClick");
            if (fullVideoListener != null) {
                fullVideoListener.onAdClick();
            }
        }

        @Override
        public void onAdShow() {
            LogUtils.d("onAdShow");
            if (fullVideoListener != null) {
                fullVideoListener.onAdShow();
            }
        }

        @Override
        public void onAdClose() {
            LogUtils.d("onAdClose");
            if (fullVideoListener != null) {
                fullVideoListener.onAdClose();
            }
        }
    };

    private final MediaListener mediaListener = new MediaListener() {
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
