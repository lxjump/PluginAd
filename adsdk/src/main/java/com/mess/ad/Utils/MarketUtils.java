package com.mess.ad.Utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

public class MarketUtils {

    // 跳转到小米应用市场（显式Intent）
    public static void openXiaomiMarket(Context context, String targetPackageName) {
        try {
            // 小米应用市场的包名和Activity类名
            String xiaomiMarketPackage = "com.xiaomi.market";
            String xiaomiMarketActivity = "com.xiaomi.market.ui.JoinActivity"; // 或 JoinActivity

            // 构造显式Intent
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(xiaomiMarketPackage, xiaomiMarketActivity));
            intent.putExtra("packageName", targetPackageName); // 小米市场的参数
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // 检查是否存在小米应用市场
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                // 回退到通用市场链接
                openGenericMarket(context, targetPackageName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            openGenericMarket(context, targetPackageName);
        }
    }

    // 通用跳转到应用市场（隐式Intent + 网页降级）
    public static void openGenericMarket(Context context, String packageName) {
        try {
            // 构造市场链接
            Uri uri = Uri.parse("market://details?id=" + packageName);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // 检查是否有应用能处理此Intent
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                // 降级到网页版
                openWebMarket(context, packageName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            openWebMarket(context, packageName);
        }
    }

    // 降级到网页版应用商店
    private static void openWebMarket(Context context, String packageName) {
        try {
            Uri webUri = Uri.parse("https://app.mi.com/details?id=" + packageName);
            Intent intent = new Intent(Intent.ACTION_VIEW, webUri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 判断是否为小米设备
    public static boolean isXiaomiDevice() {
        String manufacturer = Build.MANUFACTURER;
        String brand = Build.BRAND;
        return "xiaomi".equalsIgnoreCase(manufacturer) || "xiaomi".equalsIgnoreCase(brand);
    }

    // 综合调用入口
    public static void openMarket(Context context, String packageName) {
        if (isXiaomiDevice()) {
            openXiaomiMarket(context, packageName);
        } else {
            openGenericMarket(context, packageName);
        }
    }
}