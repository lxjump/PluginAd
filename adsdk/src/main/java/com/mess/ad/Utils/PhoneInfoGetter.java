package com.mess.ad.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.webkit.WebView;

import com.qq.e.comm.pi.NUADI;
import com.vivo.httpdns.BuildConfig;
import com.vivo.httpdns.k.b1800;
import java.util.Locale;
import java.util.UUID;

/* loaded from: classes3.dex */
public class PhoneInfoGetter {
    private static final String FILE_NAME = "pgame";
    private static final String TAG = "phoneInfo";

    public static boolean isSimAvaliable(Context context) {
        if (context == null) {
            return false;
        }

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) {
            return false;
        }

        // 获取 SIM 卡的状态
        int simState = telephonyManager.getSimState();

        // 检查 SIM 卡状态是否为可用状态
        return simState == TelephonyManager.SIM_STATE_READY;
    }

    public static String getManufacture() {
        return (Build.MANUFACTURER + Build.MODEL).trim();
    }

    public static String getSysVersion() {
        return Build.VERSION.RELEASE;
    }

    public static float getDisplayDensity(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.density;
    }

    public static String getLanguage() {
        return Locale.getDefault().toString();
    }

    /**
     * 获取 SIM 卡序列号
     *
     * @param context 应用上下文
     * @return SIM 卡序列号或 null（无 SIM 或无权限）
     */
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    public static String getSimSerialNumber(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) {
            return null;
        }

        // 检查 Android 版本和权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return "Permission restricted on Android 10+";
        }

        return telephonyManager.getSimSerialNumber();
    }

    /**
     * 获取设备 MAC 地址
     *
     * @param context 应用上下文
     * @return MAC 地址或 null
     */
    public static String getMAC(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo != null) {
                    return wifiInfo.getMacAddress(); // 获取 MAC 地址
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取设备的国家信息
     *
     * @param context 应用上下文
     * @return 国家代码 (ISO 3166-1 alpha-2 格式)
     */
    public static String getCountry(Context context) {
        // 优先获取 SIM 卡的国家信息
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            String countryIso = telephonyManager.getSimCountryIso();
            if (countryIso != null && !countryIso.isEmpty()) {
                return countryIso.toUpperCase(Locale.US);
            }
        }

        // 如果 SIM 卡不可用，获取设备的区域设置
        return Locale.getDefault().getCountry().toUpperCase(Locale.US);
    }

    public static String getPLMN(Context context) {
        // 获取 TelephonyManager 实例
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) {
            return null;
        }

        // 获取 SIM 卡运营商的 PLMN
        String simOperator = telephonyManager.getSimOperator();
        if (simOperator != null && !simOperator.isEmpty()) {
            return simOperator; // 返回 SIM 卡的 MCC+MNC
        }

        // 如果 SIM 卡不可用，获取网络运营商的 PLMN
        String networkOperator = telephonyManager.getNetworkOperator();
        if (networkOperator != null && !networkOperator.isEmpty()) {
            return networkOperator; // 返回当前网络 MCC+MNC
        }

        return null; // 无法获取 PLMN
    }

    public static final boolean isSDAvaliable() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static final String getSDRootDIR() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static String getDeviceId(Context context) {
        String mac = getMAC(context);
        if (android.text.TextUtils.isEmpty(mac)) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return "";
            }
            String simSerialNumber = getSimSerialNumber(context);
            return !android.text.TextUtils.isEmpty(simSerialNumber) ? simSerialNumber : getUUID(context);
        }
        return mac;
    }

    public static String getUUID(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, 0);
        String string = sharedPreferences.getString("uuid", "");
        if (android.text.TextUtils.isEmpty(string)) {
            String uuid = UUID.randomUUID().toString();
            sharedPreferences.edit().putString("uuid", uuid);
            return uuid;
        }
        return string;
    }

    public static boolean isNetWork(Context context) {
        try {
            NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                return activeNetworkInfo.isConnected();
            }
            return false;
        } catch (Throwable th) {
            th.printStackTrace();
            return false;
        }
    }

    public static String getCurrentNetType(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            return BuildConfig.APPLICATION_ID;
        }
        if (activeNetworkInfo.getType() == 1) {
            return "wifi";
        }
        if (activeNetworkInfo.getType() == 0) {
            int subtype = activeNetworkInfo.getSubtype();
            return (subtype == 4 || subtype == 1 || subtype == 2) ? "2g" : (subtype == 3 || subtype == 8 || subtype == 6 || subtype == 5 || subtype == 12) ? "3g" : subtype == 13 ? "4g" : "";
        }
        return "";
    }

    public static String getLac(Context context) {
        GsmCellLocation gsmCellLocation;
        int cid;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return "";
        }
        CellLocation cellLocation = telephonyManager.getCellLocation();
        return String.valueOf((telephonyManager.getPhoneType() != 1 || !(cellLocation instanceof GsmCellLocation) || (cid = (gsmCellLocation = (GsmCellLocation) cellLocation).getCid()) <= 0 || cid == 65535) ? 0 : gsmCellLocation.getLac());
    }

    public static int getWidth(Context context) {
        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
    }

    public static int getHeight(Context context) {
        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
    }

    public static int getOrientation(Activity activity) {
        int rotation = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        if (rotation != 0) {
            if (rotation != 1) {
                return rotation != 2 ? 8 : 9;
            }
            return 0;
        }
        return 1;
    }

    public static String getAndroidId(Context context) {
        return Settings.System.getString(context.getContentResolver(), "android_id");
    }

    public static String getBssId(Context context) {
        String bssid;
        WifiInfo connectionInfo = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
        return (connectionInfo == null || (bssid = connectionInfo.getBSSID()) == null) ? "" : bssid;
    }

    public static String getSSId(Context context) {
        WifiInfo connectionInfo = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
        if (connectionInfo != null) {
            String ssid = connectionInfo.getSSID();
            return android.text.TextUtils.isEmpty(ssid) ? "" : (ssid.length() > 2 && ssid.charAt(0) == '\"' && ssid.charAt(ssid.length() - 1) == '\"') ? ssid.substring(1, ssid.length() - 1) : ssid;
        }
        return "";
    }

    public static String getDpr(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return String.valueOf(displayMetrics.densityDpi);
    }

    public static String getUserAgent(Context context) {
        WebView webView = new WebView(context);
        webView.layout(0, 0, 0, 0);
        return webView.getSettings().getUserAgentString();
    }

    public static String isPad(Context context) {
        if ((context.getResources().getConfiguration().screenLayout & 15) >= 3) {
            return String.valueOf(1);
        }
        return String.valueOf(0);
    }

    public static String getDeviceId(Context context, String str) {
        String str2;
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        try {
            str2 = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getMacAddress();
        } catch (Exception e) {
            e.printStackTrace();
            sb.append("id");
            sb.append(getUUID(context));
            str2 = "";
        }
        if (StringUtil.isNotEmpty(str2)) {
            sb.append("wifi");
            sb.append(str2);
            return sb.toString();
        }
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();
        if (StringUtil.isNotEmpty(deviceId)) {
            sb.append("imei");
            sb.append(deviceId);
            return sb.toString();
        }
        String simSerialNumber = telephonyManager.getSimSerialNumber();
        if (StringUtil.isNotEmpty(simSerialNumber)) {
            sb.append("sn");
            sb.append(simSerialNumber);
            return sb.toString();
        }
        String uuid = getUUID(context);
        if (StringUtil.isNotEmpty(uuid)) {
            sb.append("id");
            sb.append(uuid);
            return sb.toString();
        }
        return sb.toString();
    }

    public static String getAppName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getApplicationInfo().packageName, 0);
            return (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
}