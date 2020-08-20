package com.martin.core.base;

import android.app.Application;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.martin.core.config.AppConfig;
import com.tencent.smtt.sdk.QbSdk;

/**
 * Created by DingJinZhu on 2020/1/21.
 * Description:
 */
public abstract class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppConfig.init(this);//初始化系统context，变量等
        initARouter();
        initX5WebCore();
    }

    private void initX5WebCore() {
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(),  cb);
    }

    private void initARouter() {
        if (AppConfig.isDebug()) {//debug mode
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);
    }

    public void exitApp() {//退出app时释放相关资源
        AppConfig.shutDown();
    }
}
