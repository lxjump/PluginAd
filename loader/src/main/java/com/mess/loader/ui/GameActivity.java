package com.mess.loader.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.mess.loader.Loader;
import com.mess.loader.hook.ams.AMSHookManager;
import com.mess.pluginad.R;


public class GameActivity extends Activity implements View.OnClickListener {

    private static String TAG = GameActivity.class.getName();
    private Button banner;
    private Button interstitial;
    private Button reward;
    private Button floatIcon;
    private Button login;
    private Button startActivity;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // hook  ams
        String subPackageName = getPackageName();
        AMSHookManager.init(newBase,subPackageName);
    }

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);
        Loader.getLoaderInstance().callInitSDK(this);
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
        startActivity = findViewById(R.id.button6);
        startActivity.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        Log.d(TAG, "v = " + v.toString());
        if (v == banner) {
            Log.d(TAG, "show Banner");
            Loader.getLoaderInstance().callShowBanner();
        } else if (v == interstitial) {
            Log.d(TAG, "show interstitial");
            Loader.getLoaderInstance().callShowFullAd();
        } else if (v == reward) {
            Log.d(TAG, "show reward");
            Loader.getLoaderInstance().callShowRewardAd();
        } else if (v == floatIcon) {
            Log.d(TAG, "show floatIcon");
            Loader.getLoaderInstance().callShowFloatIcon();
        } else if (v == startActivity) {
            Log.d(TAG, "startActivity");
            Loader.getLoaderInstance().callStartActivityTest(this);
        }
    }
}
