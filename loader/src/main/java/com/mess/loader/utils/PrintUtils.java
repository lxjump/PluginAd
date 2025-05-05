package com.mess.loader.utils;

import android.content.Intent;
import android.util.Log;

public class PrintUtils {

    public static void printAllIntentExtras(Intent intent) {
        Log.d("JustDebug", "printAllIntentExtras intent=" + intent);
        if (intent == null) {
            Log.d("JustDebug", "printAllIntentExtras intent is null");
            return;
        }

        Log.d("JustDebug", "printAllIntentExtras intent uri=" + intent.toUri(0));
    }

}
