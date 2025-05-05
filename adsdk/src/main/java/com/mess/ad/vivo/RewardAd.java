package com.mess.ad.vivo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.widget.EditText;

import com.mess.ad.Utils.LogUtils;
import com.mess.ad.constant.AdIds;
import com.mess.ad.sdk.listener.IRewardListener;
import com.vivo.mobilead.unified.base.AdParams;
import com.vivo.mobilead.unified.base.VivoAdError;
import com.vivo.mobilead.unified.base.callback.MediaListener;
import com.vivo.mobilead.unified.reward.UnifiedVivoRewardVideoAd;
import com.vivo.mobilead.unified.reward.UnifiedVivoRewardVideoAdListener;

import java.lang.ref.WeakReference;

public class RewardAd {

    private static final String TAG = RewardAd.class.getSimpleName();
    private EditText etFloorPrice;
    private UnifiedVivoRewardVideoAd vivoRewardVideoAd;
    private boolean isLoadAndShow;
    private AdParams.Builder builder;

    private boolean playingRewardAd = false;

    private WeakReference<Activity> curActivity;

    @SuppressLint("StaticFieldLeak")
    private static RewardAd instance;

    private IRewardListener rewardListener;

    private RewardAd() {}

    public static RewardAd getInstance() {
        if (instance == null) {
            instance = new RewardAd();
        }
        return instance;
    }

    public void initRewardAd(Activity activity) {
        curActivity = new WeakReference<>(activity);
    }

    private void initAdParams() {

        String posId = AdIds.VIDEO_POSITION_ID;
        builder = new AdParams.Builder(posId);
    }

    public void loadRewardAd(IRewardListener rewardListener) {
        if (isPlayingRewardAd()) {
            LogUtils.d("正在播放激励广告");
            return;
        }
        initAdParams();
        this.rewardListener = rewardListener;
        vivoRewardVideoAd = new UnifiedVivoRewardVideoAd(curActivity.get(), builder.build(), rewardVideoAdListener);
        vivoRewardVideoAd.setMediaListener(mediaListener);
        vivoRewardVideoAd.loadAd();
    }

    private void showRewardAd() {
        LogUtils.d("showRewardAd");
        vivoRewardVideoAd.showAd(curActivity.get());
    }

    private final UnifiedVivoRewardVideoAdListener rewardVideoAdListener = new UnifiedVivoRewardVideoAdListener() {
        @Override
        public void onAdReady() {
            //价格大于0时可以比价，比价结束后再展示
            LogUtils.d("onAdReady");
            //此处可以调用showAd展示视频了，也可以等待视频缓存好，即onVideoCached后再展示视频
            showRewardAd();
//            setPlayingRewardAd(true);
        }

        @Override
        public void onAdFailed(VivoAdError vivoAdError) {
            LogUtils.d("onAdFailed: " + vivoAdError.toString());
            if (rewardListener != null) {
                rewardListener.onAdShowFailed(vivoAdError.getCode(), vivoAdError.getMsg());
            }
            setPlayingRewardAd(false);
        }

        @Override
        public void onAdClick() {
            LogUtils.d("onAdClick");
            if (rewardListener != null) {
                rewardListener.onAdClick();
            }
        }

        @Override
        public void onAdShow() {
            LogUtils.d("onAdShow");
            if (rewardListener != null) {
                rewardListener.onAdShow();
            }
        }

        @Override
        public void onAdClose() {
            LogUtils.d("onAdClose");
            setPlayingRewardAd(false);
            if (rewardListener != null) {
                rewardListener.onAdClose();
            }
        }

        @Override
        public void onRewardVerify() {
            LogUtils.d("onRewardVerify");
            if (rewardListener != null) {
                rewardListener.onAdReward();
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

    public boolean isPlayingRewardAd() {
        return playingRewardAd;
    }

    public void setPlayingRewardAd(boolean playingRewardAd) {
        this.playingRewardAd = playingRewardAd;
    }
}
