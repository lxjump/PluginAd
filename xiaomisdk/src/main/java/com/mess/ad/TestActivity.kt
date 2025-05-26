package com.mess.ad

import android.app.Activity
import android.os.Bundle
import android.widget.Button

import com.mess.ad.Utils.IdentifierGetter
import com.mess.ad.Utils.MarketUtils

class TestActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(IdentifierGetter.getLayoutIdentifier(this, "test_layout"))
        val button = findViewById<Button>(IdentifierGetter.getIDIdentifier(this, "btn_jump"))
        button.setOnClickListener {
            MarketUtils.openXiaomiMarket(this, "com.xiaomi.market")
        }
    }
}