package com.mess.ad;

import static com.mess.ad.vivo.CustomBannerAd.SHOW_BOTTOM_BANNER_MSG;

import android.app.Activity;
import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.mess.ad.Utils.AppUtils;
import com.mess.ad.Utils.LogUtils;
import com.mess.ad.constant.AdIds;
import com.mess.ad.sdk.listener.IBannerListener;
import com.mess.ad.sdk.listener.IFullVideoListener;
import com.mess.ad.sdk.listener.IIconListener;
import com.mess.ad.sdk.listener.IInitSDKListener;
import com.mess.ad.sdk.listener.IInterstitialListener;
import com.mess.ad.sdk.listener.IRewardListener;
import com.mess.ad.vivo.CustomBannerAd;
import com.mess.ad.vivo.FullAd;
import com.mess.ad.vivo.IconAd;
import com.mess.ad.vivo.InterstitialAd;
import com.mess.ad.vivo.RewardAd;
import com.mess.ad.vivo.SplashAd;
import com.vivo.mobilead.manager.VInitCallback;
import com.vivo.mobilead.manager.VivoAdManager;
import com.vivo.mobilead.model.VAdConfig;
import com.vivo.mobilead.model.VCustomController;
import com.vivo.mobilead.model.VLocation;
import com.vivo.mobilead.unified.base.VivoAdError;

import java.lang.ref.WeakReference;

public class AdManager {

    private final static String TAG = AdManager.class.getSimpleName();

    private WeakReference<Activity> curActivity;

    public static final Handler mHandler = new Handler(Looper.getMainLooper());


    public AdManager(){

    }

    public WeakReference<Activity> getCurActivity() {
        return curActivity;
    }

    public void initAd(Application application, IInitSDKListener initSDKListener) {
        VAdConfig adConfig = new VAdConfig.Builder()
                .setMediaId(AdIds.MEDIA_ID)
                //是否开启日志输出，请确保不要在正式环境开启
                .setDebug(true)
                //隐私数据获取配置，请根据 App 情况认真配置，避免产生收入问题或合规风险
                .setCustomController(new VCustomController() {
                    @Override
                    public boolean isCanUseLocation() {//是否允许获取位置信息，默认允许
                        return false;
                    }

                    @Override
                    public VLocation getLocation() {
                        //若不允许获取位置信息，亦可主动传给 SDK 位置信息
                        return null;
                    }

                    @Override
                    public boolean isCanUsePhoneState() {//是否允许获取 imei 信息，默认允许
                        return true;
                    }

                    @Override
                    public String getImei() {//若不允许获取 imei 信息，亦可主动传给 SDK imei 信息
                        return null;
                    }

                    @Override
                    public boolean isCanUseWriteExternal() {//是否允许使用公共存储空间，默认允许
                        return true;
                    }

                    @Override
                    public boolean isCanPersonalRecommend() { //是否允许推荐个性化广告，默认允许
                        return true;
                    }

                    @Override
                    public boolean isCanUseImsi() { //是否允许获取 imsi，默认不允许
                        return false;
                    }

                    @Override
                    public boolean isCanUseApplist() { //是否允许获取应用列表，默认允许
                        return false;
                    }

                    @Override
                    public boolean isCanUseAndroidId() {//是否允许获取 AndroidId，默认允许
                        return true;
                    }

                    @Override
                    public boolean isCanUseMac() {//是否允许获取 mac，默认不允许
                        return false;
                    }

                    @Override
                    public boolean isCanUseIp() {//是否允许获取 ip，默认允许
                        return true;
                    }
                }).build();
        VivoAdManager.getInstance().setAgreePrivacyStrategy(true);
        VivoAdManager.getInstance().init(application, adConfig, new VInitCallback() { // from class: com.vivo.mobilead.demo.App.2
            @Override // com.vivo.mobilead.manager.VInitCallback
            public void failed(VivoAdError vivoAdError) {
                LogUtils.d("vivo ad init failed");
                initSDKListener.onInitFailed(vivoAdError.getCode(), vivoAdError.getMsg());
            }

            @Override // com.vivo.mobilead.manager.VInitCallback
            public void suceess() {
                LogUtils.d("vivo ad init success");
                initSDKListener.onInitSuccess();

            }
        });
    }

    public void initAllTypeAd(Activity activity) {
        curActivity = new WeakReference<>(activity);
        initRewardAd(activity);
        initInterstitialAd(activity);
        initFullAd(activity);
        initIconAd(activity);
    }

    public void initShowAd() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtils.d("开始显示banner、icon ad等");
                loadBanner();
                loadIconAd();
                startTimingInsertAd(curActivity.get());
                startTimingFullAd(curActivity.get());
                startTimingRewardAd(curActivity.get());
            }
        }, 2500L);
    }

    public void initSplash(Activity activity) {
        SplashAd.getInstance().initSplash(activity);
    }

    public void loadSplash() {
        SplashAd.getInstance().loadAd();
    }

    public void startTimingInsertAd(final Activity activity) {
        Log.d(TAG, "startTimingInsertAd");
        // 自动播放激励视频起始时间和时间间隔,正常要服务器下发配置控制这个时间
        long showTimingStart = 1000;
        final long showTimingInterval = 2000;

        Log.i(TAG, "定时插屏广告开始时间：" + (showTimingStart / 1000) + "  间隔：" + (showTimingInterval / 1000));
        if (showTimingStart > 0) {
            mHandler.postDelayed(new Runnable() { // from class: laputalib.sdk.BusinessAd.3
                @Override // java.lang.Runnable
                public void run() {
                    if (AppUtils.isRunningForeground(activity)) {
                        Log.i(TAG, "应用是在前台,展示定时插屏广告");
                        loadInterstitialAd();
                    } else {
                        Log.e(TAG, "应用不在前台,不展示定时插屏广告");
                    }
                    mHandler.postDelayed(this, showTimingInterval);
                }
            }, showTimingStart);
        }
    }

    public void startTimingFullAd(final Activity activity) {
        Log.d(TAG, "startTimingFullAd");

        // 自动播放激励视频起始时间和时间间隔,正常要服务器下发配置控制这个时间
        long showTimingStart = 1000;
        final long showTimingInterval = 2000;
        Log.i(TAG, "定时插屏广告开始时间：" + (showTimingStart / 1000) + "  间隔：" + (showTimingInterval / 1000));
        if (showTimingStart > 0) {
            mHandler.postDelayed(new Runnable() { // from class: laputalib.sdk.BusinessAd.3
                @Override // java.lang.Runnable
                public void run() {
                    if (AppUtils.isRunningForeground(activity)) {
                        Log.i(TAG, "应用是在前台,展示定时插屏广告");
                        loadFullAd();
                    } else {
                        Log.e(TAG, "应用不在前台,不展示定时插屏广告");
                    }
                    mHandler.postDelayed(this, showTimingInterval);
                }
            }, showTimingStart);
        }
    }

    public void startTimingRewardAd(final Activity activity) {
        Log.d(TAG, "startTimingRewardAd");
        // 自动播放激励视频起始时间和时间间隔,正常要服务器下发配置控制这个时间
        long showTimingStart = 1000;
        final long showTimingInterval = 2000;
        Log.i(TAG, "定时插屏广告开始时间：" + (showTimingStart / 1000) + "  间隔：" + (showTimingInterval / 1000));
        if (showTimingStart > 0) {
            mHandler.postDelayed(new Runnable() { // from class: laputalib.sdk.BusinessAd.3
                @Override // java.lang.Runnable
                public void run() {
                    if (AppUtils.isRunningForeground(activity)) {
                        Log.i(TAG, "应用是在前台,展示定时插屏广告");
                        if (RewardAd.getInstance().isPlayingRewardAd()) {
                            Log.d(TAG, "当前播放着激励广告,不展示定时激励广告");
                            mHandler.postDelayed(this, showTimingStart);
                            return;
                        }
                        loadRewardAd();
                    } else {
                        Log.e(TAG, "应用不在前台,不展示定时插屏广告");
                    }
                    mHandler.postDelayed(this, showTimingInterval);
                }
            }, showTimingStart);
        }
    }


    public void loadBanner() {
        CustomBannerAd customBannerAd = new CustomBannerAd();
        customBannerAd.initBannerAd(curActivity.get());
        customBannerAd.setBannerListener(new IBannerListener() {
            @Override
            public void clickBanner() {

            }

            @Override
            public void closeBanner() {
                Log.d(TAG, "关闭banner,触发handler,间隔时间后再次显示");
                customBannerAd.refreshBottomPollBanner(SHOW_BOTTOM_BANNER_MSG);
            }

            @Override
            public void failedBanner() {
                Log.d(TAG, "banner显示失败,触发handler,间隔时间后再次显示");
                customBannerAd.refreshBottomPollBanner(SHOW_BOTTOM_BANNER_MSG);
            }

            @Override
            public void readyBanner() {

            }

            @Override
            public void showBanner() {

            }

            @Override
            public void skipBanner() {

            }
        });
        customBannerAd.refreshBottomPollBanner(SHOW_BOTTOM_BANNER_MSG);
    }

    public void loadBanner(IBannerListener bannerListener) {
        LogUtils.d("loadBanner");
        CustomBannerAd customBannerAd = new CustomBannerAd();
        customBannerAd.initBannerAd(curActivity.get());
        customBannerAd.setBannerListener(bannerListener);
        customBannerAd.refreshBottomPollBanner(SHOW_BOTTOM_BANNER_MSG);

    }

    public void initInterstitialAd(Activity activity) {
        InterstitialAd.getInstance().init(activity);
    }

    public void loadInterstitialAd() {
        InterstitialAd.getInstance().loadInterstitialAd(new IInterstitialListener() {
            @Override
            public void onInsertClick() {

            }

            @Override
            public void onInsertClose() {

            }

            @Override
            public void onInsertShow() {

            }

            @Override
            public void onInsertShowFailed(int code, String str) {

            }
        });
    }

    public void loadInterstitialAd(IInterstitialListener interstitialListener) {
        InterstitialAd.getInstance().loadInterstitialAd(interstitialListener);
    }

    public void initFullAd(Activity activity) {
        FullAd.getInstance().init(activity);
    }

    public void loadFullAd() {
        FullAd.getInstance().loadFullAd(new IFullVideoListener() {

            @Override
            public void onAdClick() {

            }

            @Override
            public void onAdClickSkip() {

            }

            @Override
            public void onAdClose() {

            }

            @Override
            public void onAdReward() {

            }

            @Override
            public void onAdShow() {

            }

            @Override
            public void onAdShowFailed(int code, String str) {
                if (code == -2) {
                    // 执行播放失败操作，一般是再次调起播放
                }
            }
        });
    }

    public void loadFullAd(IFullVideoListener fullVideoListener) {
        FullAd.getInstance().loadFullAd(fullVideoListener);
    }

    public void initRewardAd(Activity activity) {
        RewardAd.getInstance().initRewardAd(activity);
    }

    public void loadRewardAd() {
        final boolean[] rewarded = {false};
        RewardAd.getInstance().loadRewardAd(new IRewardListener() {
            @Override
            public void onAdClick() {

            }

            @Override
            public void onAdClickSkip() {

            }

            @Override
            public void onAdClose() {
                LogUtils.d( "激励视频关闭回调，rewarded = " + rewarded[0]);
                if (rewarded[0]) {
                    LogUtils.d("执行发放奖励");
                } else {
                    LogUtils.d("播放视频失败，不发放奖励");
                }
            }

            @Override
            public void onAdReward() {
                LogUtils.d("onAdReward");
                rewarded[0] = true;
            }

            @Override
            public void onAdShow() {

            }

            @Override
            public void onAdShowFailed(int code, String str) {
                LogUtils.d("onAdReward code = " + code + " msg = " + str);
                if (code == 4014) {
                    curActivity.get().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(curActivity.get(), "暂无广告,无法获取激励", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    public void loadRewardAd(IRewardListener rewardListener) {
        RewardAd.getInstance().loadRewardAd(rewardListener);
    }

    public void initIconAd(Activity activity) {
        IconAd.getInstance().initIconAd(activity);
    }

    public void loadIconAd() {
        IconAd.getInstance().loadIconAd(new IIconListener() {
            @Override
            public void onIconClose() {

            }

            @Override
            public void onIconShowFailed(int code, String msg) {

            }

            @Override
            public void onIconClick() {

            }
        });
    }

    public void loadIconAd(IIconListener iconListener) {
        IconAd.getInstance().loadIconAd(iconListener);
    }

}
