package com.martin.core.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;

/**
 * 线程工具，用于执行线程等
 */
public final class ThreadUtil {
    public static final ThreadUtil INST = new ThreadUtil();

    private ThreadUtil(){
    }

    /**
     * 在线程中执行
     * @param runnable 要执行的runnable
     */
    public void excute(Runnable runnable) {
        ExecutorService executorService = getExecutorService();
        if (executorService != null && !executorService.isShutdown()) {
            // 优先使用线程池，提高效率
            executorService.execute(runnable);
        } else {
            // 线程池获取失败，则直接使用线程
            new Thread(runnable).start();
        }
    }

    /**
     * 在主线程中执行
     * @param runnable 要执行的runnable
     */
    public void excuteInMainThread(Runnable runnable){
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    /**
     * 获取缓存线程池
     * @return 缓存线程池服务
     */
    public ExecutorService getExecutorService(){
        return ThreadUtils.getCachedPool();
    }

    public void shutDown(){
        ExecutorService executorService = getExecutorService();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }

    }
}
