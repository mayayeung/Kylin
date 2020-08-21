package com.martin.cmpt.x5Webview;

import android.webkit.JavascriptInterface;

import com.martin.core.utils.ToastUtils;

/**
 * Created by DingJinZhu on 2020/8/20.
 * Description:
 */
public class JSCallFunction {

    /**
     * toast
     *
     * @param toast
     */
    @JavascriptInterface
    public void showToast(String toast) {
        ToastUtils.showToastOnce(toast);
    }

}
