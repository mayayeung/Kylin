package com.martin.kylin;

import android.util.Log;

import com.martin.core.base.BaseApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by DingJinZhu on 2020/2/3.
 * Description:
 */
public class MyApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("djz", "process name :" + getCurProcessName());
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

    private String getCurProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
