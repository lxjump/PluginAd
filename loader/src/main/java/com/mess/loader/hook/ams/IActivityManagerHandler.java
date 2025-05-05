package com.mess.loader.hook.ams;

import android.content.Context;
import android.content.Intent;
import android.util.Log;


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
        switch (method.getName()) {
            case "startActivity":
                Log.d(TAG, " startActivity");
                AMSHookManager.Utils.replaceActivityIntent(args);
                return method.invoke(rawIActivityManager, args);
            case "startService":
//                AMSHookManager.Utils.replaceServiceIntent(args);
                return method.invoke(rawIActivityManager, args);
            case "stopService":
                // 判断是否停止插件中的服务。
                Intent intent = AMSHookManager.Utils.filter(args);
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
