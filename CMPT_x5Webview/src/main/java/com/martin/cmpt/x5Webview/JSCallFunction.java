package com.martin.cmpt.x5Webview;

import android.webkit.JavascriptInterface;

import com.martin.cmpt.x5Webview.utils.JSCallBack;
import com.martin.core.ui.views.X5WebView;
import com.martin.core.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by DingJinZhu on 2020/8/20.
 * Description:
 */
public class JSCallFunction {
    X5WebView x5WebView;

    public JSCallFunction(X5WebView x5WebView) {
        this.x5WebView = x5WebView;
    }

    /**
     * toast
     *
     * @param msg
     */
    @JavascriptInterface
    public void showToast(String msg) {
        ToastUtils.showToastOnce(msg);
        try {
            JSONObject obj = new JSONObject(msg);
            String param = obj.optString("param");
            String callback = obj.optString("callback");
            String jsCode = String.format("window.receive('%s')", param);
            new JSCallBack(null, x5WebView).callJS(jsCode);
        } catch (JSONException e) {
            e.printStackTrace();
            String jsCode = String.format("window.receive('%s')", msg);
            new JSCallBack(null, x5WebView).callJS(jsCode);
        }

    }

}
