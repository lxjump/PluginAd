package com.mess.loader.hook.ams;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import com.mess.loader.Loader;
import com.mess.loader.hook.service.ServiceHookManager;
import com.mess.loader.utils.LogUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class IActivityManagerHandler implements InvocationHandler {
    private final Object rawIActivityManager;
    private final Context context;

    private final static String TAG = IActivityManagerHandler.class.getSimpleName();

    public IActivityManagerHandler(Context context,Object rawIActivityManager) {
        this.context=context;
        this.rawIActivityManager = rawIActivityManager;

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //替换掉交给AMS的intent对象，将里面的TargetActivity的暂时替换成已经声明好的替身StubActivity
        LogUtils.d("invoke method " + method.getName());
        Intent intent = AMSHookManager.Utils.filter(args);
        switch (method.getName()) {
            case "startActivity":
                Log.d(TAG, " startActivity");
                if (intent != null) {
                    ComponentName targetComp = intent.getComponent();
                    if (targetComp != null) {
                        String targetClass = targetComp.getClassName();
                        if (Loader.isPluginActivity(targetClass)) {
                            LogUtils.d("是插件 Activity，替换为 StubActivity → " + targetClass);
                            AMSHookManager.Utils.replaceActivityIntent(args);
                        } else {
                            LogUtils.d("非插件 Activity，跳过处理 → " + targetClass);
                        }
                    } else {
                        LogUtils.w("intent 没有明确的 component，可能是隐式启动，跳过");
                    }
                }

                return method.invoke(rawIActivityManager, args);
            case "startService":
//                AMSHookManager.Utils.replaceServiceIntent(args);
                return method.invoke(rawIActivityManager, args);
            case "stopService":
                // 判断是否停止插件中的服务。
                if (!context.getPackageName().equals(intent.getComponent().getPackageName())) {
                    return ServiceHookManager.stopService(intent);
                } else {
                    return method.invoke(rawIActivityManager, args);
                }
            default:
                return method.invoke(rawIActivityManager, args);
        }
    }


}
