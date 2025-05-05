package com.mess.loader.hook.ams;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.mess.loader.utils.LogUtils;


public class ActivityThreadHandlerCallback implements Handler.Callback {

    private final Handler activityThreadHandler;

    public ActivityThreadHandlerCallback(Handler activityThreadHandler) {
        this.activityThreadHandler = activityThreadHandler;
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        try {
//            ActivityHookHelper.recoverActivityIntent(msg);
            AMSHookManager.Utils.recoverActivityIntent(msg);
        } catch (Exception e) {
            LogUtils.e(e.toString());
        }
        activityThreadHandler.handleMessage(msg);
        return true;
    }
}
