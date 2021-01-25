package com.martin.cmpt.hook;

import com.martin.core.base.BaseApplication;

/**
 * Created by DingJinZhu on 2020/5/28.
 * Description:
 */
public class HookApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        HookUtil hookUtil = new HookUtil();
        hookUtil.hookStartActivity(this);
        hookUtil.hookHookMh(this);
    }
}
