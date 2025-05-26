package com.mess.loader.hook.resource;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.widget.EditText;

import com.mess.loader.utils.LogUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class ResourceHookManager {
    private static final String TAG = ResourceHookManager.class.getSimpleName();
    private static Resources multiResources;

    public static void init(Context context, String apkFilePath) {
//        preloadResource(context, apkFilePath);
        preloadResource_backup(context, apkFilePath);
    }

    private synchronized static void preloadResource(Context context, String apkFilePath) {
        try {
            AssetManager assetManager = AssetManagerCompat.createWithPath(context, apkFilePath);
            // 创建合并后的 Resources 对象
            Resources resources = new Resources(
                    assetManager,
                    context.getResources().getDisplayMetrics(),
                    context.getResources().getConfiguration()
            );
            replaceSystemResources(context, resources, apkFilePath);

            multiResources = resources;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 系统资源替换逻辑（全版本通用）
     */
    private static void replaceSystemResources(Context context, Resources resources, String apkPath)
            throws Exception {
        // 替换 ContextImpl 中的资源
        @SuppressLint("PrivateApi") Class<?> contextImplClass = Class.forName("android.app.ContextImpl");
        @SuppressLint("DiscouragedPrivateApi") Field mResourcesField = contextImplClass.getDeclaredField("mResources");
        mResourcesField.setAccessible(true);
        mResourcesField.set(context, resources);

        // 替换 LoadedApk 中的资源
        @SuppressLint("DiscouragedPrivateApi") Field loadedApkField = contextImplClass.getDeclaredField("mPackageInfo");
        loadedApkField.setAccessible(true);
        Object loadApk = loadedApkField.get(context);

        assert loadApk != null;
        Class<?> loadApkClass = loadApk.getClass();
        Field resField = loadApkClass.getDeclaredField("mResources");
        resField.setAccessible(true);
        resField.set(loadApk, resources);

//        // 处理 Android 9.0+ 的特殊情况
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            try {
//                // 获取 mSplitResDirs 字段
//                Field mSplitResDirsField = loadApkClass.getDeclaredField("mSplitResDirs");
//                mSplitResDirsField.setAccessible(true);
//
//                // 获取当前值（可能为 null）
//                String[] existingSplitResDirs = (String[]) mSplitResDirsField.get(loadApk);
//
//                // 转换为 List 并处理 null
//                List<String> splitResDirsList = new ArrayList<>();
//                if (existingSplitResDirs != null) {
//                    splitResDirsList = new ArrayList<>(Arrays.asList(existingSplitResDirs));
//                }
//
//                // 添加新路径
//                splitResDirsList.add(apkPath);
//
//                // 转换回 String[] 并设置回字段
//                String[] updatedSplitResDirs = splitResDirsList.toArray(new String[0]);
//                mSplitResDirsField.set(loadApk, updatedSplitResDirs);
//            } catch (NoSuchFieldException e) {
//                LogUtils.e("mSplitResDirs 字段不存在: " + e);
//            }
//        }
//
//        // 替换 ActivityThread 资源管理器
//        Object resourcesManager = getResourcesManager();
//
//        updateResourcesManager(resourcesManager, resources);
    }

    public static Object getResourcesManager() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ 通过静态方法获取
            @SuppressLint("PrivateApi") Class<?> resourcesManagerClass = Class.forName("android.app.ResourcesManager");
            @SuppressLint("DiscouragedPrivateApi") Method getInstanceMethod = resourcesManagerClass.getDeclaredMethod("getInstance");
            return getInstanceMethod.invoke(null);
        } else {
            // Android 9 及以下通过 ActivityThread 获取
            @SuppressLint("PrivateApi") Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            @SuppressLint("DiscouragedPrivateApi") Field sCurrentActivityThread = activityThreadClass.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThread.setAccessible(true);
            Object activityThread = sCurrentActivityThread.get(null);

            @SuppressLint("DiscouragedPrivateApi") Field resourcesManagerField = activityThreadClass.getDeclaredField("mResourcesManager");
            resourcesManagerField.setAccessible(true);
            return resourcesManagerField.get(activityThread);
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private static void updateResourcesManager(Object manager, Resources newRes)
            throws ReflectiveOperationException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            // 处理 7.0+ 版本
            Field resImplsField = manager.getClass().getDeclaredField("mResourceImpls");
            resImplsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<Object, WeakReference<Object>> resImpls =
                    (Map<Object, WeakReference<Object>>) resImplsField.get(manager);

            // 获取新 ResourcesImpl 对象
            @SuppressLint("DiscouragedPrivateApi") Field resImplField = Resources.class.getDeclaredField("mResourcesImpl");
            resImplField.setAccessible(true);
            Object newImpl = resImplField.get(newRes);

            // 替换实现对象
            assert resImpls != null;
            for (Map.Entry<Object, WeakReference<Object>> entry : resImpls.entrySet()) {
                Object key = entry.getKey();
                resImpls.put(key, new WeakReference<>(newImpl));
            }

        } else {
            // 处理 7.0 以下版本
            Field activeResourcesField = manager.getClass().getDeclaredField("mActiveResources");
            activeResourcesField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<Object, WeakReference<Resources>> activeResources =
                    (Map<Object, WeakReference<Resources>>) activeResourcesField.get(manager);

            for (Map.Entry<Object, WeakReference<Resources>> entry : activeResources.entrySet()) {
                Object key = entry.getKey();
                activeResources.put(key, new WeakReference<>(newRes));
            }
        }
    }

    private synchronized static void preloadResource_backup(Context context, String apkFilePath) {
        try {
            // 先创建AssetManager
            Class<? extends AssetManager> AssetManagerClass = AssetManager.class;
            AssetManager assetManager = AssetManagerClass.newInstance();
            // 将插件资源和宿主资源通过 addAssetPath方法添加进去
            @SuppressLint("DiscouragedPrivateApi") Method addAssetPathMethod = AssetManagerClass.getDeclaredMethod("addAssetPath", String.class);
            addAssetPathMethod.setAccessible(true);
            String hostResourcePath = context.getPackageResourcePath();
            int result_1 = (int) addAssetPathMethod.invoke(assetManager, hostResourcePath);
            int result_2 = (int) addAssetPathMethod.invoke(assetManager, apkFilePath);
            // 接下来创建，合并资源后的Resource
            Resources resources = new Resources(assetManager, context.getResources().getDisplayMetrics(), context.getResources().getConfiguration());
            // 替换 ContextImpl 中Resource对象
            Class<?> contextImplClass = context.getClass();
            Field resourcesField1 = contextImplClass.getDeclaredField("mResources");
            resourcesField1.setAccessible(true);
            resourcesField1.set(context, resources);
            // 先获取到LoadApk对象
            Field loadedApkField = contextImplClass.getDeclaredField("mPackageInfo");
            loadedApkField.setAccessible(true);
            Object loadApk = loadedApkField.get(context);


            Class<?> loadApkClass = loadApk.getClass();
            // 替换掉LoadApk中的Resource对象。
            Field resourcesField2 = loadApkClass.getDeclaredField("mResources");
            resourcesField2.setAccessible(true);
            resourcesField2.set(loadApk, resources);


//            //获取到ActivityThread
//            @SuppressLint("PrivateApi") Class<?> ActivityThreadClass = Class.forName("android.app.ActivityThread");
//            @SuppressLint("DiscouragedPrivateApi") Field sCurrentActivityThreadField = ActivityThreadClass.getDeclaredField("sCurrentActivityThread");
//            sCurrentActivityThreadField.setAccessible(true);
//            Object ActivityThread = sCurrentActivityThreadField.get(null);
//            // 获取到ResourceManager对象
//            @SuppressLint("SoonBlockedPrivateApi")
//            Field ResourcesManagerField = ActivityThreadClass.getDeclaredField("mResourcesManager");
//            ResourcesManagerField.setAccessible(true);
//            Object resourcesManager = ResourcesManagerField.get(ActivityThread);
//            // 替换掉ResourceManager中resource对象
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
//                Class<?> resourcesManagerClass = resourcesManager.getClass();
//                Field mActiveResourcesField = resourcesManagerClass.getDeclaredField("mActiveResources");
//                mActiveResourcesField.setAccessible(true);
//                Map<Object, WeakReference<Resources>> map = (Map<Object, WeakReference<Resources>>) mActiveResourcesField.get(resourcesManager);
//                Object key = map.keySet().iterator().next();
//                map.put(key, new WeakReference<>(resources));
//            } else {
//                // still hook Android N Resources, even though it's unnecessary, then nobody will be strange.
//                Class<?> resourcesManagerClass = resourcesManager.getClass();
//                Field mResourceImplsField = resourcesManagerClass.getDeclaredField("mResourceImpls");
//                mResourceImplsField.setAccessible(true);
//                Map map = (Map) mResourceImplsField.get(resourcesManager);
//                Object key = map.keySet().iterator().next();
//                Field mResourcesImplField = Resources.class.getDeclaredField("mResourcesImpl");
//                mResourcesImplField.setAccessible(true);
//                Object resourcesImpl = mResourcesImplField.get(resources);
//                map.put(key, new WeakReference<>(resourcesImpl));
//                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
//                    // 在android 9.0 以上 创建Activity会单独创建Resource,并没有使用LoadApk中Resource.
//                    // 因此考虑将插件资源放到LoadApk中mSplitResDirs数组中
//                    try {
//                        Field mSplitResDirsField = loadApkClass.getDeclaredField("mSplitResDirs");
//                        mSplitResDirsField.setAccessible(true);
//                        String[] mSplitResDirs = (String[]) mSplitResDirsField.get(loadApk);
//                        String[] temp = new String[]{apkFilePath};
//                        mSplitResDirsField.set(loadApk, temp);
//                    } catch (Exception e) {
//                        LogUtils.e(e.toString());
//                    }
//                }
//            }
            multiResources = resources;
        } catch (Exception e) {
            LogUtils.d(e.toString());
        }
    }

    public static int getDrawableId(String resName, String packageName) {
        int imgId = multiResources.getIdentifier(resName, "mipmap", packageName);
        if (imgId == 0) {
            imgId = multiResources.getIdentifier(resName, "drawable", packageName);
        }
        return imgId;
    }


    public static Resources getMultiResources() {
        return multiResources;
    }
}
