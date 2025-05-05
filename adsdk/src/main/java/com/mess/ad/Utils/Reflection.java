package com.mess.ad.Utils;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class Reflection {

    private final static String TAG = Reflection.class.getSimpleName();

    public static Object invokeStaticMethod(String str, String str2, Class[] clsArr, Object[] objArr) {
        try {
            return Class.forName(str).getMethod(str2, clsArr).invoke(null, objArr);
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException |
                 NoSuchMethodException | SecurityException |
                 InvocationTargetException  e) {
            Log.e(TAG, "发生异常", e);
            return null;
        }
    }

    public static Object invokeInstanceMethod(Object obj, String str, Class[] clsArr, Object[] objArr) {
        if (obj == null) {
            return null;
        }
        try {
            Method declaredMethod = obj.getClass().getDeclaredMethod(str, clsArr);
            declaredMethod.setAccessible(true);
            return declaredMethod.invoke(obj, objArr);
        } catch (Exception e) {
            Log.e(TAG, "发生异常", e);
            return null;
        }
    }

    public static Object invokeMethod(Class<?> cls, Object obj, String str, Object[] objArr, Class<?>... clsArr) {
        try {
            Method declaredMethod = cls.getDeclaredMethod(str, clsArr);
            declaredMethod.setAccessible(true);
            return declaredMethod.invoke(obj, objArr);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException |
                 InvocationTargetException e) {
            Log.e(TAG, "发生异常", e);
            return null;
        }
    }

    public static Object invokeMethod(String str, Object obj, String str2, Object[] objArr, Class<?>... clsArr) {
        if (clsArr == null) {
            try {
                clsArr = new Class[0];
                if (objArr == null) {
                    objArr = new Object[0];
                }
                return invokeMethod(Class.forName(str), obj, str2, objArr, clsArr);
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "发生异常", e);
                return null;
            }
        }
        return null;
    }

    public static Object invokeMethod(String str, String str2, Object obj, Class[] clsArr, Object[] objArr) {
        try {
            return Class.forName(str).getMethod(str2, clsArr).invoke(obj, objArr);
        } catch (ClassNotFoundException | IllegalAccessException
                 | IllegalArgumentException | NoSuchMethodException | SecurityException
                 | InvocationTargetException e) {
            Log.e(TAG, "发生异常", e);
            return null;
        }
    }

    public static Object getFieldValue(Class<?> cls, Object obj, String str) {
        try {
            Field declaredField = cls.getDeclaredField(str);
            declaredField.setAccessible(true);
            return declaredField.get(obj);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            Log.e(TAG, "发生异常", e);
            return null;
        }
    }

    public static void setFieldOjbect(String str, String str2, Object obj, Object obj2) {
        try {
            Field declaredField = Class.forName(str).getDeclaredField(str2);
            declaredField.setAccessible(true);
            declaredField.set(obj, obj2);
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            Log.e(TAG, "发生异常", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getFieldObject(String str, Object obj, String str2) {
        try {
            Field declaredField = Class.forName(str).getDeclaredField(str2);
            declaredField.setAccessible(true);
            return declaredField.get(obj);
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            Log.e(TAG, "发生异常", e);
            return null;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getFieldValue(String str, Object obj, String str2) {
        try {
            return getFieldValue(Class.forName(str), obj, str2);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "发生异常", e);
            return null;
        }
    }

    public static boolean setFieldValue(Class<?> cls, Object obj, String str, Object obj2) {
        try {
            Field declaredField = cls.getDeclaredField(str);
            declaredField.setAccessible(true);
            declaredField.set(obj, obj2);
            return true;
        } catch (IllegalAccessException | NoSuchFieldException e) {
            Log.e(TAG, "发生异常", e);
            return false;
        }
    }

    public static boolean setFieldValue(String str, Object obj, String str2, Object obj2) {
        try {
            setFieldValue(Class.forName(str), obj, str2, obj2);
            return true;
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "发生异常", e);
            return false;
        }
    }

    public static Object newInstance(String str) {
        try {
            return Class.forName(str).newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            Log.e(TAG, "发生异常", e);
            return null;
        }
    }

    public static Field findField(Object obj, String str) throws NoSuchFieldException {
        for (Class<?> cls = obj.getClass(); cls != null; cls = cls.getSuperclass()) {
            try {
                Field declaredField = cls.getDeclaredField(str);
                if (!declaredField.isAccessible()) {
                    declaredField.setAccessible(true);
                }
                return declaredField;
            } catch (NoSuchFieldException unused) {
                Log.d(TAG, unused.toString());
            }
        }
        throw new NoSuchFieldException("Field " + str + " not found in " + obj.getClass());
    }

    public static Method findMethod(Object obj, String str, Class<?>... clsArr) throws NoSuchMethodException {
        for (Class<?> cls = obj.getClass(); cls != null; cls = cls.getSuperclass()) {
            try {
                Method declaredMethod = cls.getDeclaredMethod(str, clsArr);
                if (!declaredMethod.isAccessible()) {
                    declaredMethod.setAccessible(true);
                }
                return declaredMethod;
            } catch (NoSuchMethodException unused) {
                Log.e(TAG, "发生异常", unused);
            }
        }
        throw new NoSuchMethodException("Method " + str + " with parameters " + Arrays.asList(clsArr) + " not found in " + obj.getClass());
    }


}
