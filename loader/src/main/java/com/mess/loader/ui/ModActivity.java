package com.mess.loader.ui;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.mess.loader.Loader;
import com.mess.loader.utils.LogUtils;

public class ModActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtils.d("ModActivity::onCreate");
        this.getWindow().setDimAmount(0f);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        super.onCreate(savedInstanceState);

        View contentView = new RelativeLayout(this);
        contentView.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        contentView.setBackgroundColor(Color.BLACK);
        this.setContentView(contentView);
        Loader.getLoaderInstance().loadMod(this);
    }


    @Override
    public void onBackPressed() {
        LogUtils.d("ModActivity::onBackPressed");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Loader.getLoaderInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
}
