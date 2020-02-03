package com.martin.core.config;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by dingjinzhu on 2019/03/01.
 * 获取系统传入的application，创建全局线程池
 */

public class AppConfig {
    private static Application application;
    private static Handler handler;//主线程的handler
    private static ExecutorService es;
    private static boolean debug = false;

    public static void init(Application application) {
        AppConfig.application = application;
        debug = application.getApplicationInfo() != null
                && (application.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        handler = new Handler(Looper.getMainLooper());
        es = Executors.newFixedThreadPool(5);
    }

    public static boolean isDebug() {
        return debug;
    }

    public static Application getContext() {
        return application;
    }

    public static void postOnUiThread(Runnable task) {
        handler.post(task);
    }

    public static void postDelayOnUiThread(Runnable task, long delayMills) {
        handler.postDelayed(task, delayMills);
    }

    public static void removeTask(Runnable task) {
        handler.removeCallbacks(task);
    }

    public static void execute(Runnable task) {
        if (es != null && !es.isShutdown()) {
            es.execute(task);
        }
    }

    public static <T> Future<T> submit(Callable<T> call){
        if (es != null && !es.isShutdown()) {
            return es.submit(call);
        }
        return null;
    }

    public static void shutDown() {
        if (es != null && !es.isShutdown()) {
            es.shutdown();
        }
    }
}
