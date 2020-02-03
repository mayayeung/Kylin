package com.martin.kylin;

import com.martin.core.base.BaseApplication;

/**
 * Created by DingJinZhu on 2020/2/3.
 * Description:
 */
public class MyApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}
