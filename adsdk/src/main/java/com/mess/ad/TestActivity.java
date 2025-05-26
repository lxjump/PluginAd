package com.mess.ad;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mess.ad.Utils.IdentifierGetter;
import com.mess.ad.Utils.LogUtils;
import com.mess.ad.Utils.MarketUtils;

import org.w3c.dom.Text;

public class TestActivity extends Activity {

    private static String TAG = TestActivity.class.getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "context = " + this.getBaseContext());
        Log.d(TAG, "context = " + this.getApplicationContext());
        Log.d(TAG, "context = " + this);
        Log.d(TAG, "package name = " + this.getPackageName());
        LogUtils.d("context = " + this.getBaseContext());
        LogUtils.d("context = " + this.getApplicationContext());
        LogUtils.d("context = " + this);
        setContentView(IdentifierGetter.getLayoutIdentifier(this, "test_layout"));
        Button jump = findViewById(IdentifierGetter.getIDIdentifier(this, "btn_jump"));
        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarketUtils.openGenericMarket(TestActivity.this, "com.xiaomi.market");
            }
        });
    }


}
