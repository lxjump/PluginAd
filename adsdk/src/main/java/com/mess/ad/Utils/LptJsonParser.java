package com.mess.ad.Utils;

import com.vivo.unionsdk.utils.LOG;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LptJsonParser {

    private final static String TAG = LptJsonParser.class.getSimpleName();

    public static Boolean getBoolean(JSONObject jSONObject, String str) {
        if (jSONObject != null && !TextUtils.isEmpty(str) && !jSONObject.isNull(str)) {
            try {
                return Boolean.valueOf(jSONObject.getBoolean(str));
            } catch (JSONException unused) {
                return Boolean.FALSE;
            }
        }
        return Boolean.FALSE;
    }

    public static float getFloat(JSONObject jSONObject, String str) {
        String string = getString(jSONObject, str);
        if (TextUtils.isEmpty(string)) {
            return 0.0f;
        }
        try {
            return Float.parseFloat(string);
        } catch (NumberFormatException e) {
            LOG.e(TAG, e.toString());
            return 0.0f;
        }
    }

    public static int getInt(JSONObject jSONObject, String str) {
        String string = getString(jSONObject, str);
        if (TextUtils.isEmpty(string)) {
            return 0;
        }
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException unused) {
            return 0;
        }
    }

    public static JSONArray getJSONArray(JSONObject jSONObject, String str) {
        if (jSONObject != null && !TextUtils.isEmpty(str) && !jSONObject.isNull(str)) {
            try {
                return jSONObject.getJSONArray(str);
            } catch (JSONException e) {
                LOG.e(TAG, e.toString());
            }
        }
        return null;
    }

    public static JSONObject getJSONObject(JSONObject jSONObject, String str) {
        if (jSONObject != null && !TextUtils.isEmpty(str) && !jSONObject.isNull(str)) {
            try {
                return jSONObject.getJSONObject(str);
            } catch (JSONException unused) {
            }
        }
        return null;
    }

    public static JSONObject getJsonObject(JSONObject jSONObject, String str) {
        if (jSONObject != null && !TextUtils.isEmpty(str) && !jSONObject.isNull(str)) {
            try {
                return jSONObject.getJSONObject(str);
            } catch (JSONException e) {
                LOG.e(TAG, e.toString());
            }
        }
        return null;
    }

    public static long getLong(JSONObject jSONObject, String str) {
        String string = getString(jSONObject, str);
        if (TextUtils.isEmpty(string)) {
            return 0L;
        }
        try {
            return Long.parseLong(string);
        } catch (NumberFormatException e) {
            LOG.e(TAG, e.toString());
            return 0L;
        }
    }

    public static String getString(JSONObject jSONObject, String str) {
        if (jSONObject != null && !TextUtils.isEmpty(str) && !jSONObject.isNull(str)) {
            try {
                return jSONObject.getString(str);
            } catch (JSONException unused) {
            }
        }
        return null;
    }

    public static Map<String, String> jsonToStringMap(String str) {
        JSONObject jSONObject;
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            jSONObject = new JSONObject(str);
        } catch (JSONException unused) {
            jSONObject = null;
        }
        if (jSONObject == null) {
            return null;
        }
        Iterator<String> keys = jSONObject.keys();
        HashMap hashMap = new HashMap();
        while (keys.hasNext()) {
            String next = keys.next();
            String string = getString(jSONObject, next);
            if (string == null) {
                string = "";
            }
            hashMap.put(next, string);
        }
        return hashMap;
    }

    public static String stringMapToJson(Map<String, String> map) {
        if (map == null || map.size() <= 0) {
            return null;
        }
        JSONObject jSONObject = new JSONObject();
        for (String str : map.keySet()) {
            if (!TextUtils.isEmpty(str)) {
                String str2 = map.get(str);
                if (str2 == null) {
                    str2 = "";
                }
                try {
                    jSONObject.put(str, str2);
                } catch (JSONException unused) {
                }
            }
        }
        return jSONObject.toString();
    }

    private static List<Object> toList(JSONArray jSONArray) {
        if (jSONArray == null) {
            return null;
        }
        try {
            int length = jSONArray.length();
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < length; i++) {
                Object obj = null;

                obj = jSONArray.get(i);

                if (obj instanceof JSONArray) {
                    obj = toList((JSONArray) obj);
                } else if (obj instanceof JSONObject) {
                    obj = toMap((JSONObject) obj);
                }
                arrayList.add(obj);
                return arrayList;
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private static Map<String, Object> toMap(JSONObject jSONObject) throws JSONException {
        Iterator<String> keys = jSONObject.keys();
        HashMap hashMap = new HashMap();
        while (keys.hasNext()) {
            String next = keys.next();
            Object obj = jSONObject.get(next);
            if (obj instanceof JSONArray) {
                obj = toList((JSONArray) obj);
            } else if (obj instanceof JSONObject) {
                obj = toMap((JSONObject) obj);
            }
            hashMap.put(next, obj);
        }
        return hashMap;
    }


}
