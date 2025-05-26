package com.mess.loader.hook.ams;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.mess.loader.Loader;
import com.mess.loader.hook.activity.StubActivity;
import com.mess.loader.hook.service.ProxyService;
import com.mess.loader.utils.IntentUtils;
import com.mess.loader.utils.LogUtils;
import com.mess.loader.utils.PrintUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.List;


public class AMSHookManager {

    public final static int LAUNCH_ACTIVITY = 100;
    public static final int EXECUTE_TRANSACTION = 159;

    private static String targetPackageName;
    public static final String KEY_RAW_INTENT = "raw_intent";
    private static boolean isInIt = false;
    public static final String TAG = "AMSHookManager";

    public static boolean isIsInIt() {
        return isInIt;
    }

    /**
     * 初始化操作
     *
     * @param packageName
     */
    public static void init(Context context, String packageName) {
        try {
            targetPackageName = packageName;
            hookIActivityManager(context);
            hookActivityThreadHandler();
            isInIt = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * hook 掉IActivityManager，使用自己的代理对象和ams通讯
     */
    @SuppressLint({"PrivateApi", "DiscouragedPrivateApi"})
    private static void hookIActivityManager(Context context) throws Exception {
        Field ActivityManagerSingletonFiled;

        Class<?> iActivityManagerInterface;
        // android Q 以上版本,需要替换的是ActivityTaskManager的实力
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // 10.0以上是ActivityTaskManager中的IActivityTaskManagerSingleton
            LogUtils.d("android Q");
            Class<?> activityTaskManagerClass = Class.forName("android.app.ActivityTaskManager");
            ActivityManagerSingletonFiled = activityTaskManagerClass.getDeclaredField("IActivityTaskManagerSingleton");
            iActivityManagerInterface = Class.forName("android.app.IActivityTaskManager");
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0以上发生改变
            LogUtils.d("android O");
            Class<?> ActivityManagerClass = Class.forName("android.app.ActivityManager");
            ActivityManagerSingletonFiled = ActivityManagerClass.getDeclaredField("IActivityManagerSingleton");
            iActivityManagerInterface = Class.forName("android.app.IActivityManager");
        } else {
            LogUtils.d("< android 8");
            Class<?> ActivityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");
            ActivityManagerSingletonFiled = ActivityManagerNativeClass.getDeclaredField("gDefault");
            iActivityManagerInterface = Class.forName("android.app.IActivityManager");
        }
        ActivityManagerSingletonFiled.setAccessible(true);
        Object ActivityManagerSingleton = ActivityManagerSingletonFiled.get(null);
        // ActivityManagerSingleton是一个 android.util.Singleton对象; 我们取出这个单例里面的字段
        Class<?> SingletonClass = Class.forName("android.util.Singleton");
        Field mInstanceField = SingletonClass.getDeclaredField("mInstance");
        mInstanceField.setAccessible(true);
        //获取到ActivityManager通讯代理对象，即IActivityManager对象
        Object rawIActivityManager = mInstanceField.get(ActivityManagerSingleton);
        //动态代理，创建代理对象
        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{iActivityManagerInterface},
                new IActivityManagerHandler(context, rawIActivityManager));
        //换成自己的IActivityManager对象
        LogUtils.d("11111111111");
        mInstanceField.set(ActivityManagerSingleton, proxy);
    }

    /**
     * android 9.0 以上采用Instrumentation方式来，加载插件中Activity
     *
     * @param context
     * @throws Exception
     */
    public static void hookInstrumentation(Context context) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
            try {
                //获取到ActivityThread
                @SuppressLint("PrivateApi")
                Class<?> ActivityThreadClass = Class.forName("android.app.ActivityThread");
                @SuppressLint("DiscouragedPrivateApi")
                Field sCurrentActivityThreadField = ActivityThreadClass.getDeclaredField("sCurrentActivityThread");
                sCurrentActivityThreadField.setAccessible(true);
                Object ActivityThread = sCurrentActivityThreadField.get(null);
                @SuppressLint("DiscouragedPrivateApi")
                Field mInstrumentationField = ActivityThreadClass.getDeclaredField("mInstrumentation");
                mInstrumentationField.setAccessible(true);
                Instrumentation instrumentation = (Instrumentation) mInstrumentationField.get(ActivityThread);
                Instrumentation proxyInstrumentation = new PluginInstrumentation(instrumentation, context.getPackageManager());
                mInstrumentationField.set(ActivityThread, proxyInstrumentation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * hook ActivityThread中 handler拦截处理
     * ,恢复要开启的activity
     */
    private static void hookActivityThreadHandler() throws Exception {
        //获取到ActivityThread
        @SuppressLint("PrivateApi")
        Class<?> ActivityThreadClass = Class.forName("android.app.ActivityThread");
        @SuppressLint("DiscouragedPrivateApi")
        Field sCurrentActivityThreadField = ActivityThreadClass.getDeclaredField("sCurrentActivityThread");
        sCurrentActivityThreadField.setAccessible(true);
        Object ActivityThread = sCurrentActivityThreadField.get(null);
        //获取到ActivityThread中的handler
        @SuppressLint("DiscouragedPrivateApi")
        Field mHField = ActivityThreadClass.getDeclaredField("mH");
        mHField.setAccessible(true);
        Handler mHandler = (Handler) mHField.get(ActivityThread);
        //给handler添加callback监听器，拦截
        @SuppressLint("DiscouragedPrivateApi")
        Field mCallBackField = Handler.class.getDeclaredField("mCallback");
        mCallBackField.setAccessible(true);
        mCallBackField.set(mHandler, new ActivityThreadHandlerCallback(mHandler));
    }

    public static String getTargetPackageName() {
        return targetPackageName;
    }

    public static final class Utils {


        public static Intent filter(Object[] args) {
            Intent intent = null;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    intent = (Intent) args[i];
                    break;
                }
            }
            return intent;
        }

        /**
         * 替换成代替的activity,绕过ams检查
         *
         * @param args
         */
        public static void replaceActivityIntent(Object[] args) {
            Log.d("AMSHookManager", "replaceActivityIntent");
            Intent rawIntent;
            int index = 0;

            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    index = i;
                    break;
                }
            }
            //真实启动的Intent
            LogUtils.d("args index = " + args[index].toString());
            rawIntent = (Intent) args[index];
            args[index] = createProxyIntent(rawIntent);
        }

        public static Intent createProxyIntent(Intent rawIntent) {
            LogUtils.d("构建一个替代的Activity对应的intent");
            //构建一个替代的Activity对应的intent
            Intent subIntent = new Intent();
            ComponentName componentName = new ComponentName(targetPackageName, StubActivity.class.getName());
            subIntent.setComponent(componentName);
            //将真实启动的Intent作为参数附带上
            subIntent.putExtra(KEY_RAW_INTENT, rawIntent);
            LogUtils.d("新的intent " + subIntent.toString());
            LogUtils.d("新的intent Extra " + subIntent.getParcelableExtra(KEY_RAW_INTENT).toString());
            return subIntent;
        }


        /**
         * 替换成ProxyService
         *
         * @param args
         */
        public static void replaceServiceIntent(Object[] args) {
            Intent rawIntent;
            int index = 0;
            for (int i = 0; i < args.length; ++i) {
                if (args[i] instanceof Intent) {
                    index = i;
                    break;
                }
            }
            rawIntent = (Intent) args[index];
            // 构建一个ProxyService的intent
            Intent subIntent = new Intent();
            subIntent.setClassName(targetPackageName, ProxyService.class.getName());
            // 将信息存储在intent中
            subIntent.putExtra(KEY_RAW_INTENT, rawIntent);
            args[index] = subIntent;
        }


        /**
         * 恢复成要启动的activity
         *
         * @param message
         */
        public static void recoverActivityIntent(Message message) {
            LogUtils.d("准备恢复启动的activity what = " + message.what);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (message.what == EXECUTE_TRANSACTION) {
                    try {
                        LogUtils.d("恢复启动的activity obj = " + message.obj.getClass());
                        dealReplaceActivity(message.obj);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                if (message.what == LAUNCH_ACTIVITY) {
                    try {
                        LogUtils.d("恢复启动的activity");
                        Class<?> ActivityClientRecordClass = message.obj.getClass();
                        Field intentField = ActivityClientRecordClass.getDeclaredField("intent");
                        intentField.setAccessible(true);
                        Intent subIntent = (Intent) intentField.get(message.obj);
                        assert subIntent != null;
                        modifyIntentForPlugin(subIntent);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        public static Intent modifyIntentForPlugin(Intent intent) {
            LogUtils.d("modifyIntentForPlugin intent = " + intent.toString());
            //真实启动的Intent
            Intent rawIntent = intent.getParcelableExtra(KEY_RAW_INTENT);
            LogUtils.d("rawIntent = " + rawIntent.toString());
            assert rawIntent != null;
            LogUtils.d("modifyIntentForPlugin 真实的intent = " + rawIntent.toString());
            //把正式启动的intent设置进去
//            intent.setComponent(rawIntent.getComponent());
            LogUtils.d("modifyIntentForPlugin 设置后 intent = " + intent.toString());
            // 设置intent的classloader,避免intent中存在插件 Parcelable 或 Serializable序列化的对象,导致找不到类的问题
            rawIntent.setExtrasClassLoader(Loader.getPluginClassLoader());
            IntentUtils.printIntent(rawIntent);
            return rawIntent;
        }

        public static Intent recoverActivityIntent(Intent subIntent) {
            //真实启动的Intent
            return subIntent.getParcelableExtra(KEY_RAW_INTENT);
        }

        public static void dealReplaceActivity(Object clientTransaction) {
            try {
                LogUtils.d("dealReplaceActivity");
                Field mActivityCallbacksField = clientTransaction.getClass().getDeclaredField("mActivityCallbacks");
                mActivityCallbacksField.setAccessible(true);
                List<?> activityCallbacks = (List<?>) mActivityCallbacksField.get(clientTransaction);
                if (activityCallbacks == null) {
                    return;
                }
                // 遍历 activityCallbacks，找到 LaunchActivityItem
                LogUtils.d("callbacks size = " + activityCallbacks.size());
                for (Object callback : activityCallbacks) {
                    LogUtils.d(" call class name " + callback.getClass().getName());
                    if ("android.app.servertransaction.LaunchActivityItem".equals(callback.getClass().getName())) {
                        // 找到 LaunchActivityItem
                        // 进一步处理 LaunchActivityItem
//                        Intent intent = getIntent(callback);
                        Field mIntentField = callback.getClass().getDeclaredField("mIntent");
                        mIntentField.setAccessible(true);
                        Intent intent = (Intent) mIntentField.get(callback);

                        // 打印或修改 Intent
                        LogUtils.d("拦截到 Intent: " + intent);
                        Intent rawIntent = (Intent) mIntentField.get(callback);

//                        if (rawIntent != null) {
//                            modifyIntentIfNeeded(rawIntent);
//                        }
//                        break; // 只处理第一个LaunchActivityItem
                        PrintUtils.printAllIntentExtras(intent);

                        assert intent != null;
                        Intent newIntent = modifyIntentForPlugin(intent);
                        mIntentField.set(callback, newIntent);
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        }

        private static void modifyIntentIfNeeded(Intent rawIntent) {
            // 这里根据你的逻辑替换Intent，示例：
            Intent targetIntent = rawIntent.getParcelableExtra(KEY_RAW_INTENT);
            if (targetIntent != null) {
                LogUtils.d("发现需要恢复的真实Intent: " + targetIntent.getComponent());
//                IntentUtils.printIntent(targetIntent);
                LogUtils.d("********************************************************");
                IntentUtils.printIntent(rawIntent);
//                rawIntent.setComponent(targetIntent.getComponent());
                rawIntent = targetIntent;
            }
        }

        public static Intent getIntent(Object launchActivityItem) {
            // 获取 LaunchActivityItem 的 mIntent 字段
            try {
                Field mIntentField = launchActivityItem.getClass().getDeclaredField("mIntent");
                mIntentField.setAccessible(true);
                Intent intent = (Intent) mIntentField.get(launchActivityItem);

                // 打印或修改 Intent
                LogUtils.d("拦截到 Intent: " + intent);
//                PrintUtils.printAllIntentExtras(intent);
                return intent;
            } catch (NoSuchFieldException | IllegalAccessException e) {
                LogUtils.d(e.toString());
                throw new RuntimeException(e);
            }
        }


        public static Intent findIntentInArgs(Object[] args) {
            for (Object arg : args) {
                if (arg instanceof Intent) {
                    return (Intent) arg;
                }
            }
            return null;
        }
    }


}
