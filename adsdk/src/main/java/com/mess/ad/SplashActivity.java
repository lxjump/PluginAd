package com.mess.ad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.FrameLayout;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends Activity {

    private FrameLayout mainLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 创建 FrameLayout
        mainLayout = new FrameLayout(this);
        mainLayout.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        setContentView(mainLayout);
//        AdManager.initSplash(SplashActivity.this, mainLayout);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

//                AdManager.loadSplash();
            }
        }, 3000);

    }

    @Override
    protected void onResume() {
        super.onResume();
//        AdManager.loadSplash();
        AppManager.runGame(this);
    }
}