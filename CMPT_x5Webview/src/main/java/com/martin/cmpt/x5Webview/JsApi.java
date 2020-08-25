package com.martin.cmpt.x5Webview;

import android.webkit.JavascriptInterface;
import com.martin.core.ui.views.dsbridge.CompletionHandler;


/**
 * Created by du on 16/12/31.
 */

public class JsApi {
    @JavascriptInterface
    public String testSyn(Object msg)  {
        return msg + "［syn call］";
    }

    @JavascriptInterface
    public void testAsyn(Object msg, CompletionHandler<String> handler){
        handler.complete(msg+" [ asyn call]");
    }


}