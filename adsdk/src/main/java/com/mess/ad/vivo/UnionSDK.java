package com.mess.ad.vivo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.mess.ad.Utils.AppUtils;
import com.mess.ad.Utils.LogUtils;
import com.mess.ad.sdk.listener.IInitSDKListener;
import com.mess.ad.sdk.listener.ILoginListener;
import com.vivo.unionsdk.open.VivoAccountCallback;
import com.vivo.unionsdk.open.VivoExitCallback;
import com.vivo.unionsdk.open.VivoRealNameInfoCallback;
import com.vivo.unionsdk.open.VivoUnionSDK;

public class UnionSDK {

    private static final String TAG = UnionSDK.class.getSimpleName();

    private static UnionSDK unionSDK;
    private UnionSDK() {

    }

    public static UnionSDK getInstance() {
        if(unionSDK == null) {
            unionSDK = new UnionSDK();
        }
        return unionSDK;
    }

    public void privateAgreed(Context context) {
        VivoUnionSDK.onPrivacyAgreed(context);
    }

    public void login(final Activity activity, final ILoginListener iLoginProxyListener) {
        VivoUnionSDK.registerAccountCallback(activity, new VivoAccountCallback() { // from class: laputalib.sdk.vivoNew.FormProxy.4.1
            @Override // com.vivo.unionsdk.open.VivoAccountCallback
            public void onVivoAccountLogin(String str, String str2, String str3) {
                LogUtils.d("vivo账户登录成功:" + str);
                iLoginProxyListener.onLoginSuccess();
                VivoUnionSDK.getRealNameInfo(activity, new VivoRealNameInfoCallback() { // from class: laputalib.sdk.vivoNew.FormProxy.4.1.1
                    @Override // com.vivo.unionsdk.open.VivoRealNameInfoCallback
                    public void onGetRealNameInfoFailed() {
                    }

                    @Override // com.vivo.unionsdk.open.VivoRealNameInfoCallback
                    public void onGetRealNameInfoSucc(boolean z, int i) {
                    }
                });
            }

            @Override // com.vivo.unionsdk.open.VivoAccountCallback
            public void onVivoAccountLogout(int i) {
                iLoginProxyListener.onLoginFailed(-1, "vivo登录退出");
                Log.e(TAG, "登录vivo账户退出");
                showExitDialog(activity, "登录退出,无法继续为您提供游戏,即将退出游戏.", iLoginProxyListener);
            }

            @Override // com.vivo.unionsdk.open.VivoAccountCallback
            public void onVivoAccountLoginCancel() {
                iLoginProxyListener.onLoginFailed(-1, "vivo登录取消");
                showExitDialog(activity, "取消登录,无法继续为您提供游戏,即将退出游戏.", iLoginProxyListener);
            }
        });
        VivoUnionSDK.login(activity);
    }

    public void showExitDialog(final Activity activity, final String str, final ILoginListener iLoginListener) {
        activity.runOnUiThread(new Runnable() { // from class: laputalib.sdk.vivoNew.FormProxy.5
            @Override // java.lang.Runnable
            public void run() {
                AlertDialog create = new AlertDialog.Builder(activity).setTitle("提示").setCancelable(false).setMessage(str).setNegativeButton("退出游戏", new DialogInterface.OnClickListener() { // from class: laputalib.sdk.vivoNew.FormProxy.5.2
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        AppUtils.exitApp(activity);
                    }
                }).setPositiveButton("返回登录", new DialogInterface.OnClickListener() { // from class: laputalib.sdk.vivoNew.FormProxy.5.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        login(activity, iLoginListener);
                    }
                }).create();
                create.setCanceledOnTouchOutside(false);
                create.show();
            }
        });
    }

    public void addGameCenter() {

    }

    public void doExitGame(Activity activity) {
        VivoUnionSDK.exit(activity, new VivoExitCallback() {
            @Override
            public void onExitCancel() {
                //退出取消
            }

            @Override
            public void onExitConfirm() {
                //退出确认
                activity.finish();
            }
        });
    }


}
