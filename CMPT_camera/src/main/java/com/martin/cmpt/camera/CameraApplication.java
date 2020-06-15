package com.martin.cmpt.camera;

import com.martin.core.base.BaseApplication;

import me.pqpo.smartcropperlib.SmartCropper;

/**
 * Created by DingJinZhu on 2020/5/28.
 * Description:
 */
public class CameraApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        SmartCropper.buildImageDetector(this);
    }
}
