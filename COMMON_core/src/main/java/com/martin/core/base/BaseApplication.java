package com.martin.core.base;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.martin.core.config.AppConfig;

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
