package com.mess.ad;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.mess.ad.Utils.LogUtils;

import org.w3c.dom.Text;

public class TestActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        LogUtils.d("getApplicationContext = " + getApplicationContext());
        LogUtils.d("getBaseContext = " + getBaseContext());
        textView.setText(getApplicationContext().toString());
        setContentView(textView);

    }
}
