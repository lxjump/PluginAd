package com.mess.ad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.mess.ad.Utils.LogUtils;
import com.mess.ad.sdk.listener.IBannerListener;
import com.mess.ad.sdk.listener.IIconListener;
import com.mess.ad.sdk.listener.IInterstitialListener;

/**
 * 直接运行测试用的
 */
public class GameActivity extends Activity implements View.OnClickListener {

    private static String TAG = GameActivity.class.getName();
    private Button banner;
    private Button interstitial;
    private Button reward;
    private Button floatIcon;
    private Button login;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);
        SDKManager.getSdkManager().initSDK(this);
        banner = findViewById(R.id.button);
        banner.setOnClickListener(this);
        interstitial = findViewById(R.id.button2);
        interstitial.setOnClickListener(this);
        reward = findViewById(R.id.button3);
        reward.setOnClickListener(this);
        floatIcon = findViewById(R.id.button4);
        floatIcon.setOnClickListener(this);
        login = findViewById(R.id.button5);
        login.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        LogUtils.d("v = " + v.toString());
        if (v == banner) {
            LogUtils.d("show Banner");
            SDKManager.getSdkManager().getAdManager().loadBanner(new IBannerListener() {
                @Override
                public void clickBanner() {

                }

                @Override
                public void closeBanner() {

                }

                @Override
                public void failedBanner() {

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
        } else if (v == interstitial) {
            LogUtils.d("show interstitial");
            SDKManager.getSdkManager().getAdManager().loadInterstitialAd(new IInterstitialListener() {
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
                public void onInsertShowFailed(int i, String str) {

                }
            });
        } else if (v == reward) {
            LogUtils.d("show reward");
            SDKManager.getSdkManager().getAdManager().loadRewardAd();
        } else if (v == floatIcon) {
            LogUtils.d("show floatIcon");
            SDKManager.getSdkManager().getAdManager().loadIconAd(new IIconListener() {
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
    }
}
