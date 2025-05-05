package com.mess.ad.vivo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mess.ad.AppManager;
import com.mess.ad.Utils.AppUtils;
import com.mess.ad.Utils.IdentifierGetter;
import com.mess.ad.Utils.LogUtils;
import com.mess.ad.Utils.PhoneInfoGetter;
import com.mess.ad.constant.AdIds;
import com.mess.ad.constant.IDConfig;
import com.mess.ad.sdk.bean.AdInfo;
import com.vivo.mobilead.unified.base.AdParams;
import com.vivo.mobilead.unified.base.VivoAdError;
import com.vivo.mobilead.unified.splash.UnifiedVivoSplashAd;
import com.vivo.mobilead.unified.splash.UnifiedVivoSplashAdListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SplashAd {

    private static String TAG = SplashAd.class.getName();

    private View adView;
    private ViewGroup containerView;
    private AdParams adParams;
    private WeakReference<Activity> curActivity;

    private UnifiedVivoSplashAd splashAd;

    @SuppressLint("StaticFieldLeak")
    private static SplashAd instance = null;
    private List<String> mNeedRequestPMSList = new ArrayList<>();
    private boolean mCanJump;

    public static SplashAd getInstance() {
        if (instance == null) {
            instance = new SplashAd();
        }
        return instance;
    }

    public void initSplashAd(Activity activity, ViewGroup containerView) {
////        this.curActivity = activity;
//        this.containerView = containerView;
//        String splashId = Config.splash2;
//        AdParams.Builder builder = new AdParams.Builder(splashId);
////        builder.setWxAppid("105825573");
//        // 拉取广告的超时时长：即开屏广告从请求到展示所花的最大时长（并不是指广告曝光时长）取值范围[3000, 5000]
//        builder.setFetchTimeout(3000);
//        /**
//         * 可以根据需要配置横竖屏
//         **/
//        builder.setSplashOrientation(SplashAdParams.ORIENTATION_PORTRAIT);
//        adParams = builder.build();
    }

    public void initSplash(Activity activity) {
        String appName = "";
        curActivity = new WeakReference<>(activity);
        View inflate = ((LayoutInflater) curActivity.get().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(IdentifierGetter.getLayoutIdentifier(this.curActivity.get(), "lpt_splash_bottom_area"), (ViewGroup) null);
        ImageView imageView = (ImageView) inflate.findViewById(IdentifierGetter.getIDIdentifier(curActivity.get(), "lpt_icon_iv"));
        TextView textView = (TextView) inflate.findViewById(IdentifierGetter.getIDIdentifier(curActivity.get(), "lpt_tv_app_name"));
        TextView textView2 = (TextView) inflate.findViewById(IdentifierGetter.getIDIdentifier(curActivity.get(), "lpt_tv_app_desc"));
        try {
            PackageManager packageManager = activity.getPackageManager();
            appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(activity.getPackageName(), 0));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.toString());
            appName = "";
        }
        textView.setText(appName);
        textView2.setText("app_desc");
        try {
            PackageManager packageManager2 = this.curActivity.get().getPackageManager();
            imageView.setImageDrawable(packageManager2.getApplicationIcon(packageManager2.getApplicationInfo(this.curActivity.get().getPackageName(), 0)));
        } catch (PackageManager.NameNotFoundException e2) {
            Log.e(TAG, e2.toString());
        }
        fetchSplashAd();
    }

    private void checkAndRequestPermissions() {
        if (ActivityCompat.checkSelfPermission(this.curActivity.get(), "android.permission.READ_PHONE_STATE") != 0) {
            this.mNeedRequestPMSList.add("android.permission.READ_PHONE_STATE");
        }
        if (ActivityCompat.checkSelfPermission(this.curActivity.get(), "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            this.mNeedRequestPMSList.add("android.permission.WRITE_EXTERNAL_STORAGE");
        }
        if (ActivityCompat.checkSelfPermission(this.curActivity.get(), "android.permission.WRITE_CALENDAR") != 0) {
            this.mNeedRequestPMSList.add("android.permission.WRITE_CALENDAR");
        }
        if (ActivityCompat.checkSelfPermission(this.curActivity.get(), "android.permission.ACCESS_FINE_LOCATION") != 0) {
            this.mNeedRequestPMSList.add("android.permission.ACCESS_FINE_LOCATION");
        }
        if (this.mNeedRequestPMSList.isEmpty()) {
            return;
        }
        String[] strArr = new String[this.mNeedRequestPMSList.size()];
        this.mNeedRequestPMSList.toArray(strArr);
        ActivityCompat.requestPermissions(this.curActivity.get(), strArr, 100);
    }

    private void fetchSplashAd() {
        LogUtils.d("fetchSplashAd");
        List<String> adIds;
        loadSplash(AdIds.SPLASH_POSITION_ID);
        runGame();
    }

    public void runGame() {
        String metaValue = AppUtils.getMetaValue(this.curActivity.get(), "main_activity");
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(this.curActivity.get().getPackageName(), metaValue));
        this.curActivity.get().startActivity(intent);
        this.curActivity.get().finish();
    }


    private void loadSplash(String str) {
        AdParams.Builder builder = new AdParams.Builder(str);
        builder.setFetchTimeout(5000);
        builder.setSplashOrientation(PhoneInfoGetter.getOrientation(this.curActivity.get()) == 0 ? 2 : 1);
        new UnifiedVivoSplashAd(this.curActivity.get(), unifiedVivoSplashAdListener, builder.build()).loadAd();
    }



    public void loadAd() {
        splashAd = new UnifiedVivoSplashAd(curActivity.get(), unifiedVivoSplashAdListener, adParams);
        splashAd.loadAd();
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (hasNecessaryPMSGranted()) {
            fetchSplashAd();
        } else {
            runGame();
        }
    }

    private boolean hasNecessaryPMSGranted() {
        return Build.VERSION.SDK_INT >= 29 || ActivityCompat.checkSelfPermission(this.curActivity.get(), "android.permission.READ_PHONE_STATE") == 0;
    }


    public void pause() {
        this.mCanJump = false;
    }

    public void resume() {
        this.mCanJump = true;
    }

    private final UnifiedVivoSplashAdListener unifiedVivoSplashAdListener = new UnifiedVivoSplashAdListener() {
        @Override
        public void onAdShow() {
            LogUtils.d("onAdShow");
        }

        @Override
        public void onAdFailed(@NonNull VivoAdError vivoAdError) {
            LogUtils.d("onAdFailed: " + vivoAdError.getMsg());
            AppManager.runGame(curActivity.get());
        }

        @Override
        public void onAdReady(@NonNull View adView) {
            LogUtils.d("onAdReady");
            SplashAd.this.adView = adView;
//            if (splashAd.getPrice() > 0 || !TextUtils.isEmpty(splashAd.getPriceLevel())) {
//            }       if (splashAd.getPrice() > 0 || !TextUtils.isEmpty(splashAd.getPriceLevel())) {
//            }
        }

        @Override
        public void onAdClick() {
            LogUtils.d("onAdClick");
        }

        @Override
        public void onAdSkip() {
            LogUtils.d("onAdSkip");
            if (adView != null) {
                adView.setVisibility(View.GONE);
                containerView.removeView(adView);
                containerView.setVisibility(View.GONE);
                adView = null;
                AppManager.runGame(curActivity.get());
            }
        }

        @Override
        public void onAdTimeOver() {
            LogUtils.d("onAdTimeOver");
            if (adView != null) {
                adView.setVisibility(View.GONE);
                containerView.removeView(adView);
                containerView.setVisibility(View.GONE);
                adView = null;
            }
            new Handler().postDelayed(new Runnable() {
                @Override // java.lang.Runnable
                public void run() {
                    AppManager.runGame(curActivity.get());
                }
            }, 1500L);

        }
    };


}
