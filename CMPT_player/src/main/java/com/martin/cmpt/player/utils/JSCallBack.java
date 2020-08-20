package com.martin.cmpt.player.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import java.lang.ref.WeakReference;

/**
 * Created by DingJinZhu on 2020/8/14.
 * Description:
 */
public class JSCallBack {
    private final static String TAG = JSCallBack.class.getSimpleName();
    private WeakReference<WebView> webViewRef;
    private final String callbackId;

    public JSCallBack(String callbackId, WebView webView) {
        this.callbackId = callbackId;
        if (null != webView) {
            this.webViewRef = new WeakReference<>(webView);
        }
    }

    /**
     * webview 调用 JS 方法
     * @param js
     */
    private void callJS(String js) {
        if (null == webViewRef || null == webViewRef.get()) {
            Log.e(TAG, "webview is null");
            return;
        }
        Context context = webViewRef.get().getContext();
        if (null == context || MiscUtils.isDestroy((Activity) context)) {
            Log.e(TAG, "webview's context is null");
        } else {
            webViewRef.get().evaluateJavascript(js, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    Log.i(TAG, "receive js value is :" + value);
                }
            });
        }
    }

    public void postJSMsg() {

    }
}
