package com.mess.ad.Utils;

import android.util.Log;

import com.mess.ad.sdk.HttpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class CityUtils {

    private final static String TAG = CityUtils.class.getSimpleName();

    public interface ICityInfo {
        void onFailure();

        void onSuccess(CityInfo cityInfo);
    }

    public static void loadMyCity(final ICityInfo iCityInfo) {
        HttpUtils.doGet("https://api.bilibili.com/x/web-interface/zone?jsonp=jsonp", new HttpCallback() {
           // from class: laputalib.sdk.config.CityUtils.1
            @Override // x_xxoo3.Callback
            public void onFailure(Call call, IOException iOException) {
                iCityInfo.onFailure();
            }

            @Override // x_xxoo3.Callback
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject jSONObject;
                try {
                    JSONObject jSONObject2 = new JSONObject(response.body().string());
                    if (jSONObject2.has("data") && (jSONObject = jSONObject2.getJSONObject("data")) != null && jSONObject.has("country") && jSONObject.has("province") && jSONObject.has("city")) {
                        iCityInfo.onSuccess(new CityInfo(jSONObject.getString("country"), jSONObject.getString("province"), jSONObject.getString("city")));
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "定位数据解析失败：" + e.getMessage());
                    iCityInfo.onFailure();
                }
            }
        });
    }

    /* loaded from: classes3.dex */
    public static class CityInfo {
        private String city;
        private String country;
        private String province;

        public CityInfo(String str, String str2, String str3) {
            this.country = str;
            this.province = str2;
            this.city = str3;
        }

        public String getCountry() {
            return this.country;
        }

        public String getProvince() {
            return this.province;
        }

        public String getCity() {
            return this.city;
        }
    }


}
