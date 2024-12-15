package com.martin.core.utils;

import android.view.View;


import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import rx.Observer;


/**
 * Created by lishuxun on 2018/6/4.
 */

public class ClickUtils {
    public static void clickView(View view, final ClickCallBack callBack) {
        RxView.clicks(view).throttleFirst(1500, TimeUnit.MICROSECONDS).subscribe(new Observer<Void>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Void aVoid) {
                if (callBack != null) {
                    callBack.doWhat();
                }
            }
        });
    }

    public static void clickView(View view, long duration, final ClickCallBack callBack) {
        RxView.clicks(view).throttleFirst(duration, TimeUnit.MICROSECONDS).subscribe(new Observer<Void>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Void aVoid) {
                if (callBack != null) {
                    callBack.doWhat();
                }
            }
        });
    }

    public interface ClickCallBack {
        void doWhat();
    }

    public static void main(String[] args) {
        String url = "https://api/user/api/user/get-user-info?user_id=e4f935e2f8701lee832d8a95840554a2";
        String[] params = url.split("&", 2);
        System.out.println(params[0]);

        if (params.length > 1 && params[0].contains("user_id")) {
            String uid = params[0].split("user_id=")[1].trim();
            System.out.println(uid+"_abc");
        }

    }

}
