package com.mess.ad.vivo;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.mess.ad.Utils.LogUtils;
import com.mess.ad.constant.AdIds;
import com.mess.ad.sdk.listener.IIconListener;
import com.vivo.mobilead.unified.base.AdParams;
import com.vivo.mobilead.unified.base.VivoAdError;
import com.vivo.mobilead.unified.icon.UnifiedVivoFloatIconAd;
import com.vivo.mobilead.unified.icon.UnifiedVivoFloatIconAdListener;

import java.lang.ref.WeakReference;

public class IconAd {

    private static final String TAG = IconAd.class.getSimpleName();
    private UnifiedVivoFloatIconAd vivoFloatIconAd;

    private WeakReference<Activity> curActivity;

    private IIconListener iconListener;

    private static IconAd instance;

    private IconAd() {

    }

    public static IconAd getInstance() {
        if (instance == null) {
            instance = new IconAd();
        }
        return instance;
    }

    public void initIconAd(Activity activity) {
        curActivity = new WeakReference<>(activity);

    }

    protected void initAdParams() {

    }

    public void loadIconAd(IIconListener iconListener) {
        this.iconListener = iconListener;
        String posId = AdIds.FLOAT_ICON;

        AdParams adParams = new AdParams.Builder(posId).build();
        vivoFloatIconAd = new UnifiedVivoFloatIconAd(curActivity.get(), adParams, floatIconAdListener);
        vivoFloatIconAd.loadAd();
    }

    protected void showIconAd() {
        if (vivoFloatIconAd != null) {
            vivoFloatIconAd.showAd(curActivity.get());
        }
    }

    private final UnifiedVivoFloatIconAdListener floatIconAdListener = new UnifiedVivoFloatIconAdListener() {
        @Override
        public void onAdShow() {
            LogUtils.d("onAdShow");
        }

        @Override
        public void onAdFailed(@NonNull VivoAdError vivoAdError) {
            LogUtils.d("onAdFailed: " + vivoAdError.toString());
            if (iconListener != null) {
                iconListener.onIconShowFailed(vivoAdError.getCode(), vivoAdError.getMsg());
            }
        }

        @Override
        public void onAdReady() {
            LogUtils.d("onAdReady");
            showIconAd();
        }

        @Override
        public void onAdClick() {
            LogUtils.d("onAdClick");
            if (iconListener != null) {
                iconListener.onIconClick();
            }
        }

        @Override
        public void onAdClose() {
            LogUtils.d("onAdClose");
            if (iconListener != null) {
                iconListener.onIconClose();
            }
        }
    };
}
