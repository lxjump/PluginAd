package com.mess.ad.Utils;

import com.mess.ad.sdk.HttpCallback;

import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtils {

    private static OkHttpClient okHttpClient;

    static {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        }
    }

    public static void doGet(String str, HttpCallback httpCallback) {
        if (httpCallback == null) {
            return;
        }
        okHttpClient.newCall(new Request.Builder().url(str).build()).enqueue(httpCallback);
    }

    public static void doPost(String str, Map<String, String> map, HttpCallback httpCallback) {
        if (httpCallback == null) {
            return;
        }
        FormBody.Builder builder = new FormBody.Builder();
        if (map != null) {
            for (String str2 : map.keySet()) {
                builder.add(str2, map.get(str2));
            }
        }
        okHttpClient.newCall(new Request.Builder().post(builder.build()).url(str).build()).enqueue(httpCallback);
    }

}
