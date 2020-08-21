package com.martin.core.utils;

import android.app.Activity;
import android.os.Build;

/**
 * Created by DingJinZhu on 20190522
 * Description:混合的工具提供类
 */
public class MiscUtils {


    public static boolean isDestroy(Activity mActivity) {
        if (mActivity== null || mActivity.isFinishing() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && mActivity.isDestroyed())) {
            return true;
        } else {
            return false;
        }
    }


}
