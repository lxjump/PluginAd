package com.mess.loader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ContentProvider;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;

import com.mess.loader.abi.ProcessAbiDetector;
import com.mess.loader.hook.ams.AMSHookManager;
import com.mess.loader.hook.contentprovider.ContentProviderHookManager;
import com.mess.loader.hook.resource.ResourceHookManager;
import com.mess.loader.hook.service.ServiceHookManager;
import com.mess.loader.utils.FileUtils;
import com.mess.loader.utils.LogUtils;
import com.mess.loader.utils.PluginUtils;
import com.mess.loader.utils.ReflectUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;

public class Loader {

    private final static String TAG = Loader.class.getSimpleName();
    private static boolean sHaveLoaded = false;

    private static ClassLoader pluginClassLoader = null;

    private static Context pluginContext;

    private final String sdkManagerName = "com.mess.ad.SDKManager";

    private Application application;

    private Object sdkManagerInstance;

    private String pluginPath;

    private static Loader loaderInstance;

    private static List<ActivityInfo> activityInfos;

    private Loader() {

    }

    public synchronized static Loader getLoaderInstance() {
        if (loaderInstance == null) {
            loaderInstance = new Loader();
        }
        return loaderInstance;
    }

    public static ClassLoader getPluginClassLoader() {
        return pluginClassLoader;
    }

    public static void setPluginClassLoader(ClassLoader pluginClassLoader) {
        Loader.pluginClassLoader = pluginClassLoader;
    }

    public static Context getPluginContext() {
        return pluginContext;
    }

    public static void setPluginContext(Context pluginContext) {
        Loader.pluginContext = pluginContext;
    }

    public void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        try {
            final String entryClassName = "com.gamemod.PluginEntrance";
            final String entryMethodName = "onRequestPermissionsResult";
            final Class<?>[] argClassTypeList = new Class[]{Activity.class, int.class, String[].class, int[].class};
            final Class<?> pluginEntryClass = pluginClassLoader.loadClass(entryClassName);
            if (pluginEntryClass != null) {
                Method method = pluginEntryClass.getMethod(entryMethodName, argClassTypeList);
                if (Modifier.isStatic(method.getModifiers())) {
                    method.setAccessible(true);
                    method.invoke(null, activity, requestCode, permissions, grantResults);
                }
            }
        } catch (Exception e) {
            LogUtils.e(String.format("Loader onRequestPermissionsResult error:%s", e));
        }
    }

    public void loadMod(Context baseContext, Application app) {
        loadMod(baseContext, app, null);
        //加载插件资源
        ResourceHookManager.init(baseContext, pluginPath);
        // hook service ，解析多进程的service 。多进程，会重复走onCreate()
        ServiceHookManager.init(baseContext, pluginPath);
        // hook ContentProvider(加载ContentProvider是在application 的onCreate()之前)
        ContentProviderHookManager.init(app, pluginPath);
        AMSHookManager.hookInstrumentation(baseContext);
    }

    public void loadMod(Activity activity) {
        loadMod(activity.getBaseContext(), activity.getApplication(), activity);
    }


    public void loadMod(Context baseContext, Application app, Activity activity) {
        try {
            Boolean result = attach(baseContext, app, activity);
            LogUtils.d(String.format("Loader attach result: %s", result));
        } catch (Exception e) {
            LogUtils.e(String.format("Loader attach error:%s", e));
            throw new RuntimeException(e);
        }
    }

    private void initPluginApp(ClassLoader pluginClassLoader) {
        try {
            String pluginAppName = "com.mess.ad.App";
            Class<?> pluginAppClass = pluginClassLoader.loadClass(pluginAppName);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
//        PluginUtils.invokeMethod(pluginClassLoader, pluginAppName, "initApplication", sdkManagerInstance,
//                argClassTypeList, application, listener);
    }

    private boolean callInitApplication(ClassLoader pluginClassLoader, Object sdkManagerInstance) {
        try {
            // 加载 IInitSDKListener 接口
            Class<?> listenerInterface = pluginClassLoader.loadClass("com.mess.ad.sdk.listener.IInitSDKListener");
            final Class<?>[] argClassTypeList = new Class[]{
                    Application.class,
                    listenerInterface
            };

            // 创建 IInitSDKListener 实现
            Object listener = java.lang.reflect.Proxy.newProxyInstance(
                    pluginClassLoader,
                    new Class[]{listenerInterface},
                    (proxy, method, args) -> {
                        if ("onInitSuccess".equals(method.getName())) {
                            LogUtils.d("Plugin initialized: " + args[0]);
                        } else if ("onInitFailed".equals(method.getName())) {
                            LogUtils.d(("Plugin initialization failed: code = " + args[0] + " msg = " + args[1]));
                        }
                        return null;
                    }
            );
            PluginUtils.invokeMethod(pluginClassLoader, sdkManagerName, "initApplication", sdkManagerInstance,
                    argClassTypeList, application, listener);
            return true;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean attach(final Context baseContext, final Application app, Activity activity) throws Exception {
        LogUtils.d("Loader::attach");
        application = app;
        if (sHaveLoaded) {
            LogUtils.e("can only  been attached once!");
            return false;
        } else {
            sHaveLoaded = true;
        }

        final File modDir = baseContext.getDir("lpt", Context.MODE_PRIVATE);
        if (FileUtils.isDirNotExists(modDir)) {
            throw new IllegalStateException("create dir error.");
        }

        final File installationDir = new File(modDir, "plugin");
        boolean ok = FileUtils.createOrExistsDir(installationDir);
        if (!ok) {
            throw new IllegalStateException("create dir error.");
        }

        final File odexDir = new File(installationDir, "odex");
        ok = FileUtils.createOrExistsDir(odexDir);
        if (!ok) {
            throw new IllegalStateException("create dir error.");
        }

        final File librarySearchDir = new File(installationDir, "lib");
        ok = FileUtils.createOrExistsDir(librarySearchDir);
        if (!ok) {
            throw new IllegalStateException("create dir error.");
        }

        final File pluginApk = new File(installationDir, "sdk.apk");
        pluginPath = pluginApk.getAbsolutePath();
        if (installPluginApkDebug(baseContext, pluginApk) || installPluginApk(baseContext, pluginApk)) {
//            final ClassLoader classLoader = load(pluginApk, odexDir, librarySearchDir);
            String soPath = installationDir + "/lib/";
            // 解压插件 so 到 指定目录
            extractSoFiles(pluginApk.getAbsolutePath(), soPath);
            pluginApk.setReadOnly();
            final ClassLoader classLoader = new PluginClassLoader(pluginApk.getAbsolutePath(), odexDir.getAbsolutePath(), librarySearchDir.getAbsolutePath(), baseContext.getClassLoader());
            ClassLoader mergedClassLoader = new BaseClassLoader(baseContext.getClassLoader(), classLoader);
            Thread.currentThread().setContextClassLoader(mergedClassLoader);
            activityInfos = getActivitiesFromApkFile(app,pluginApk.getAbsolutePath());
            createPluginContext(baseContext, pluginApk.getAbsolutePath());
//            final Method initApplication = reflectSDKManager(classLoader);
            final PackageInfo packageInfo = baseContext.getPackageManager().getPackageArchiveInfo(pluginApk.getAbsolutePath(), 0);
            if (packageInfo != null) {
                Class<?> listenerInterface = classLoader.loadClass("com.mess.ad.sdk.listener.IInitSDKListener");
//              获取插件中SDKManager的实例
                sdkManagerInstance = PluginUtils.invokeMethod(classLoader, sdkManagerName, "getSdkManager", null,
                        null, (Object[]) null);
                pluginClassLoader = classLoader;
                patch(app);
                return callInitApplication(classLoader, sdkManagerInstance);
            }
        } else {
            throw new IllegalStateException("plugin apk not install");
        }
        return false;
    }

    private void createPluginContext(Context hostContext, String pluginPath) {
        try {
            // 创建插件的 AssetManager
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPathMethod = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
            addAssetPathMethod.setAccessible(true);
            addAssetPathMethod.invoke(assetManager, pluginPath);

            // 创建插件的 Resources
            Resources pluginResources = new Resources(
                    assetManager,
                    hostContext.getResources().getDisplayMetrics(),
                    hostContext.getResources().getConfiguration()
            );
            pluginContext = new PluginContext(hostContext, pluginResources, assetManager, pluginClassLoader);


            Log.d("PluginContext", "插件 Context 创建成功: " + pluginContext);

            LogUtils.d("插件 Context 创建成功: " + pluginContext);
        } catch (Exception e) {
            throw new RuntimeException("创建插件 Context 失败", e);
        }


    }


    // 重新反射获取当前application,可修复第一代加壳导致的无图标问题
    // PluginGate.initialize 必须在real application oncreate 调用之后调用
    @SuppressLint("PrivateApi")
    private Application applicationByReflect() throws Exception {
        final Class<?> activityThread = Class.forName("android.app.ActivityThread");
        final Object thread = activityThread.getMethod("currentActivityThread").invoke(null);
        final Object app = activityThread.getMethod("getApplication").invoke(thread);
        return (Application) app;
    }


    private BaseDexClassLoader load(final File installedPluginApk,
                                    final File odexDir,
                                    final File librarySearchDir) {
        final ClassLoader bootstrapClassloader = Activity.class.getClassLoader();
        // 修复在Android 14上加载mod.apk失败问题
        installedPluginApk.setReadOnly();
        return new DexClassLoader(
                installedPluginApk.getAbsolutePath(),
                odexDir.getAbsolutePath(),
                librarySearchDir.getAbsolutePath(),
                bootstrapClassloader
        );
    }

    private boolean installPluginApkDebug(final Context context, final File destination) {
        final File debugPluginApk = new File("/data/local/tmp/mess" + "/sdk.apk");
        destination.setWritable(true); // 需设置为可写，否则更新版本时有问题
        if (FileUtils.checkFileExists(debugPluginApk)) {
            if (FileUtils.checkFileExists(destination) && FileUtils.checkSameFile(destination, debugPluginApk)) {
                showDebugTips(context);
                return true;
            } else {
                boolean ok = FileUtils.copyOrMoveFile(debugPluginApk, destination, null, false);
                if (ok) {
                    showDebugTips(context);
                }
                return ok;
            }
        }
        return false;
    }


    private boolean installPluginApk(final Context context, final File destination) {
        final String assetPath = "mess/sdk.apk";
        boolean needCopy = true;
        destination.setWritable(true); // 需设置为可写，否则更新版本时有问题
        if (destination.exists() && destination.isFile()) {
            final long size = FileUtils.getAssetsFileLength(context, assetPath);
            needCopy = size != destination.length();
        }
        if (needCopy) {
            return FileUtils.copyFileFromAssets(
                    context,
                    assetPath,
                    destination.getAbsolutePath()
            );
        } else {
            return true;
        }
    }

    private void showDebugTips(Context context) {
        Toast.makeText(context, "测试sdk plugin", Toast.LENGTH_LONG).show();
    }

    public void loadPluginProvider(String providerClassName, String authority) {
        try {

            // 加载插件的 VivoUnionProvider 类
            Class<?> providerClass = pluginClassLoader.loadClass(providerClassName);

            // 创建 ContentProvider 实例
            ContentProvider pluginProvider = (ContentProvider) providerClass.newInstance();

            // 初始化 ContentProvider
            pluginProvider.attachInfo(application.getBaseContext(), null);
            pluginProvider.onCreate();

            // 注册插件的 ContentProvider
            ProxyContentProvider.registerPluginProvider(authority, pluginProvider);

            Log.d(TAG, "Plugin ContentProvider loaded and registered: " + providerClassName);
        } catch (Exception e) {
            Log.e(TAG, "Failed to load plugin ContentProvider", e);
        }
    }

    public void callInitSDK(Activity activity) {
        Log.d(TAG, "activity cl = " + activity.getClassLoader().toString());
        final Class<?>[] argClassTypeList = new Class[]{
                Activity.class,
        };
        PluginUtils.invokeMethod(pluginClassLoader, sdkManagerName, "initSDK", sdkManagerInstance, argClassTypeList, activity);
    }

    /**
     * 解压插件中的 .so 文件到宿主的 nativeLibraryDir
     *
     * @param apkPath      插件 APK 文件路径
     * @param nativeLibDir 宿主的 nativeLibraryDir
     */
    public void extractSoFiles(String apkPath, String nativeLibDir) {
        String abi = ProcessAbiDetector.getAbi(application.getBaseContext()).getArch();
        Log.d(TAG, "abi = " + abi);
        try (ZipFile zipFile = new ZipFile(apkPath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();

                // 过滤 .so 文件
                if (name.startsWith("lib/" + abi) && name.endsWith(".so")) {
                    File outputFile = new File(nativeLibDir, name.substring(name.lastIndexOf('/')));
                    Log.d(TAG, "name  = " + name);
                    Log.d(TAG, "nativeLibDir  = " + nativeLibDir);
                    Log.d(TAG, "outputFile  = " + outputFile.getAbsolutePath());
                    if (!Objects.requireNonNull(outputFile.getParentFile()).exists()) {
                        outputFile.getParentFile().mkdirs();
                    }

                    // 解压文件
                    try (InputStream in = zipFile.getInputStream(entry);
                         FileOutputStream out = new FileOutputStream(outputFile)) {
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = in.read(buffer)) > 0) {
                            out.write(buffer, 0, len);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void callShowBanner() {
        Log.d(TAG, "callShowBanner");
        // 获取插件AdManager实例
        Object adManagerInstance = PluginUtils.invokeMethod(pluginClassLoader, sdkManagerName, "getAdManager",
                sdkManagerInstance, null, (Object[]) null);
        String adManagerName = "com.mess.ad.AdManager";
        String loadBannerMethodName = "loadBanner";
        Object result = PluginUtils.invokeMethod(pluginClassLoader, adManagerName, loadBannerMethodName,
                adManagerInstance, null, (Object[]) null);
    }


    public void callShowFloatIcon() {

        try {
            Log.d(TAG, "callShowFloatIcon");
            // 获取插件AdManager实例
            Object adManagerInstance = PluginUtils.invokeMethod(pluginClassLoader, sdkManagerName, "getAdManager",
                    sdkManagerInstance, null, (Object[]) null);
            String adManagerName = "com.mess.ad.AdManager";
            String iconAdListener = "com.mess.ad.sdk.listener.IIconListener";
            Class<?> listenerInterface = pluginClassLoader.loadClass(iconAdListener);
            String loadBannerMethodName = "loadIconAd";
            final Class<?>[] argClassTypeList = new Class[]{
                    listenerInterface
            };
            Object listener = java.lang.reflect.Proxy.newProxyInstance(
                    pluginClassLoader,
                    new Class[]{listenerInterface},
                    (proxy, method, args) -> {
                        if ("onIconClose".equals(method.getName())) {
                            LogUtils.d("Plugin onIconClose: " + args[0]);
                        } else if ("onIconClick".equals(method.getName())) {
                            Log.d(TAG, "onIconClick");
                        } else if ("onIconShowFailed".equals(method.getName())) {
                            LogUtils.d(("Plugin onIconShowFailed failed: code = " + args[0] + " msg = " + args[1]));
                        }
                        return null;
                    }
            );
            Object result = PluginUtils.invokeMethod(pluginClassLoader, adManagerName, loadBannerMethodName,
                    adManagerInstance, argClassTypeList, listener);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public void callShowRewardAd() {

        try {
            Log.d(TAG, "callShowRewardAd");
            // 获取插件AdManager实例
            Object adManagerInstance = PluginUtils.invokeMethod(pluginClassLoader, sdkManagerName, "getAdManager",
                    sdkManagerInstance, null, (Object[]) null);
            String adManagerName = "com.mess.ad.AdManager";
            String rewardListener = "com.mess.ad.sdk.listener.IRewardListener";
            Class<?> listenerInterface = pluginClassLoader.loadClass(rewardListener);
            String loadRewardAd = "loadRewardAd";
            final Class<?>[] argClassTypeList = new Class[]{
                    listenerInterface
            };
            Object listener = java.lang.reflect.Proxy.newProxyInstance(
                    pluginClassLoader,
                    new Class[]{listenerInterface},
                    (proxy, method, args) -> {
                        if ("onAdShow".equals(method.getName())) {
                            LogUtils.d("Plugin onIconClose: " + args[0]);
                        } else if ("onAdReward".equals(method.getName())) {
                            Log.d(TAG, "onIconClick");
                        } else if ("onAdShowFailed".equals(method.getName())) {
                            LogUtils.d(("Plugin onIconShowFailed failed: code = " + args[0] + " msg = " + args[1]));
                        }
                        return null;
                    }
            );
            Object result = PluginUtils.invokeMethod(pluginClassLoader, adManagerName, loadRewardAd,
                    adManagerInstance, argClassTypeList, listener);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public void callShowFullAd() {

        try {
            Log.d(TAG, "callShowFullAd");
            // 获取插件AdManager实例
            Object adManagerInstance = PluginUtils.invokeMethod(pluginClassLoader, sdkManagerName, "getAdManager",
                    sdkManagerInstance, null, (Object[]) null);
            String adManagerName = "com.mess.ad.AdManager";
            String fullListener = "com.mess.ad.sdk.listener.IInterstitialListener";
            Class<?> listenerInterface = pluginClassLoader.loadClass(fullListener);
            String loadInterstitialAd = "loadInterstitialAd";
            final Class<?>[] argClassTypeList = new Class[]{
                    listenerInterface
            };
            Object listener = java.lang.reflect.Proxy.newProxyInstance(
                    pluginClassLoader,
                    new Class[]{listenerInterface},
                    (proxy, method, args) -> {
                        if ("onAdShow".equals(method.getName())) {
                            LogUtils.d("Plugin onIconClose: " + args[0]);
                        } else if ("onAdReward".equals(method.getName())) {
                            Log.d(TAG, "onIconClick");
                        } else if ("onAdShowFailed".equals(method.getName())) {
                            LogUtils.d(("Plugin onIconShowFailed failed: code = " + args[0] + " msg = " + args[1]));
                        }
                        return null;
                    }
            );
            Object result = PluginUtils.invokeMethod(pluginClassLoader, adManagerName, loadInterstitialAd,
                    adManagerInstance, argClassTypeList, listener);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public void patch(Application application) {
        try {
            // 获取Application的BaseContext （来自ContextWrapper）
            Context oBase = application.getBaseContext();
            if (oBase == null) {
                Log.e(TAG, "pclu.p: nf mb. ap cl=" + application.getClass());
                return;
            }

            // 获取mBase.mPackageInfo
            // 1. ApplicationContext - Android 2.1
            // 2. ContextImpl - Android 2.2 and higher
            // 3. AppContextImpl - Android 2.2 and higher
            Object oPackageInfo = ReflectUtils.readField(oBase, "mPackageInfo");
            if (oPackageInfo == null) {
                Log.e(TAG, "pclu.p: nf mpi. mb cl=" + oBase.getClass());
                return;
            }

            // mPackageInfo的类型主要有两种：
            // 1. android.app.ActivityThread$PackageInfo - Android 2.1 - 2.3
            // 2. android.app.LoadedApk - Android 2.3.3 and higher
            Log.d(TAG, "patch: mBase cl=" + oBase.getClass() + "; mPackageInfo cl=" + oPackageInfo.getClass());

            // 获取mPackageInfo.mClassLoader
            ClassLoader oClassLoader = (ClassLoader) ReflectUtils.readField(oPackageInfo, "mClassLoader");
            if (oClassLoader == null) {
                Log.e(TAG, "pclu.p: nf mpi. mb cl=" + oBase.getClass() + "; mpi cl=" + oPackageInfo.getClass());
                return;
            }

            // 外界可自定义ClassLoader的实现，但一定要基于RePluginClassLoader类
//            ClassLoader cl = RePlugin.getConfig().getCallbacks().createClassLoader(oClassLoader.getParent(), oClassLoader);
            ClassLoader cl = new BaseClassLoader(application.getClassLoader(), pluginClassLoader);
            // 将新的ClassLoader写入mPackageInfo.mClassLoader
            ReflectUtils.writeField(oPackageInfo, "mClassLoader", cl);

            // 设置线程上下文中的ClassLoader为RePluginClassLoader
            // 防止在个别Java库用到了Thread.currentThread().getContextClassLoader()时，“用了原来的PathClassLoader”，或为空指针
            Thread.currentThread().setContextClassLoader(cl);

            Log.d(TAG, "patch: patch mClassLoader ok");
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void callStartActivityTest(Activity activity) {
        Log.d(TAG, "callStartActivityTest cl = " + activity.getClassLoader().toString());
        final Class<?>[] argClassTypeList = new Class[]{
                Activity.class,
        };
        PluginUtils.invokeMethod(pluginClassLoader, sdkManagerName, "startActivityTest", sdkManagerInstance, argClassTypeList, activity);

    }

    public List<ActivityInfo> getActivitiesFromApkFile(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);

        if (packageInfo != null) {
            return Arrays.asList(packageInfo.activities);
        }
        return Collections.emptyList();
    }

    public static boolean isPluginActivity(String clazzName) {
        LogUtils.d("isPluginActivity size " + activityInfos.size() + ", className = " + clazzName);
        for (ActivityInfo activityInfo: activityInfos) {
            LogUtils.d("getName " + activityInfo.name);
            if (activityInfo.name.contains(clazzName)) {
                LogUtils.d("是插件Activity");
                return true;
            }
        }
        return false;
    }
}
