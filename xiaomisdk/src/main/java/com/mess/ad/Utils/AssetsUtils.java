package com.mess.ad.Utils;


import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AssetsUtils {


    public static String getFromAssets(Context context, String str) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open(str)));
            StringBuilder sb = new StringBuilder();
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    sb.append(readLine);
                } else {
                    return sb.toString();
                }
            }
        } catch (Exception e) {
            LogUtils.d("exception = " + e.toString());
            return null;
        }
    }

    public static byte[] getFromAssetsAes(Context context, String str) {
        try {
            InputStream open = context.getResources().getAssets().open(str);
            byte[] bArr = new byte[open.available()];
            open.read(bArr);
            return bArr;
        } catch (Exception e) {
            LogUtils.d("exception = " + e.toString());
            return null;
        }
    }
}


