package com.martin.cmpt.x5Webview.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.martin.core.config.AppConfig;
import com.martin.core.ui.views.X5WebView;
import com.martin.core.utils.MiscUtils;
import com.tencent.smtt.sdk.ValueCallback;

import java.lang.ref.WeakReference;

/**
 * Created by DingJinZhu on 2020/8/14.
 * Description:
 */
public class JSCallBack {
    private final static String TAG = JSCallBack.class.getSimpleName();
    private WeakReference<X5WebView> webViewRef;
    private final String callbackId;

    public JSCallBack(String callbackId, X5WebView webView) {
        this.callbackId = callbackId;
        if (null != webView) {
            this.webViewRef = new WeakReference<>(webView);
        }
    }

    /**
     * webview 调用 JS 方法
     * @param js
     */
    public void callJS(String js) {
        if (null == webViewRef || null == webViewRef.get()) {
            Log.e(TAG, "webview is null");
            return;
        }
        Context context = webViewRef.get().getContext();
        if (null == context || MiscUtils.isDestroy((Activity) context)) {
            Log.e(TAG, "webview's context is null");
        } else {
            AppConfig.postOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webViewRef.get().evaluateJavascript(js, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            Log.i(TAG, "receive js value is :" + value);
                        }
                    });
                }
            });
        }
    }

    public void postJSMsg() {

    }
}
