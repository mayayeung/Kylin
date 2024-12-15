package com.martin.kylin.config;

import com.martin.kylin.BuildConfig;

/**
 * Created by DingJinZhu on 2024/5/21.
 * Description:
 */
public class Constant {


    public static boolean firstApp(){
        return BuildConfig.FLAVOR.contains("online");
    }

}
