package com.mess.loader.utils;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import java.io.Serializable;
import java.lang.reflect.Field;

public class IntentUtils {

    public static void printIntent(Intent intent) {
        if (intent == null) {
            LogUtils.d("Intent is null");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Intent Details:\n");

        // Action
        sb.append("Action: ").append(intent.getAction()).append("\n");

        // Data
        if (intent.getData() != null) {
            sb.append("Data: ").append(intent.getData().toString()).append("\n");
        }

        // Extras (Bundle)
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            sb.append("Extras:\n");
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                sb.append("  ")
                        .append(key)
                        .append(" = ")
                        .append(parseValue(value)) // 解析值的类型
                        .append("\n");
            }
        } else {
            sb.append("Extras: null\n");
        }

        LogUtils.d(sb.toString());
    }


    // 解析嵌套 Bundle
    private static String parseBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        sb.append("Bundle{");
        for (String key : bundle.keySet()) {
            Object value = bundle.get(key);
            sb.append(key).append("=").append(parseValue(value)).append(", ");
        }
        sb.append("}");
        return sb.toString();
    }

    private static String parseValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Parcelable) {
            return value.getClass().getName() + "@" + Integer.toHexString(value.hashCode());
        } else if (value instanceof Bundle) {
            return parseBundle((Bundle) value);
        } else if (value instanceof Serializable) {
            return parseSerializable((Serializable) value); // 处理 Serializable 对象
        } else {
            return value.toString();
        }
    }

    // 解析 Serializable 对象
    private static String parseSerializable(Serializable serializable) {
        StringBuilder sb = new StringBuilder();
        Class<?> clazz = serializable.getClass();
        sb.append(clazz.getSimpleName()).append("{");

        try {
            // 通过反射获取所有字段（包括私有字段）
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true); // 允许访问私有字段
                Object fieldValue = field.get(serializable);
                sb.append(field.getName())
                        .append("=")
                        .append(fieldValue != null ? fieldValue.toString() : "null")
                        .append(", ");
            }
            sb.append("}");
        } catch (IllegalAccessException e) {
            // 若反射失败，直接调用 toString()
            return clazz.getName() + "@" + Integer.toHexString(serializable.hashCode());
        }

        return sb.toString();
    }
}
