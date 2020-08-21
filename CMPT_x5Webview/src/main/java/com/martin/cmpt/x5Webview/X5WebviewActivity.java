package com.martin.cmpt.x5Webview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.martin.cmpt.x5Webview.utils.JSCallBack;
import com.martin.core.ui.views.X5WebView;
import com.martin.core.utils.ToastUtils;
import com.tencent.smtt.sdk.ValueCallback;

public class X5WebviewActivity extends FragmentActivity implements View.OnClickListener {
    X5WebView x5WebView;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.x5webview_activity_main);
        title = findViewById(R.id.title);
        title.setOnClickListener(this);
        x5WebView = findViewById(R.id.x5_webview);
        x5WebView.loadUrl("file:///android_asset/ui.html");
        x5WebView.addJavascriptInterface(new JSCallFunction(), "jsBridge");
    }

    public static void launchSelf(Context context) {
        Intent intent = new Intent(context, X5WebviewActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
//        ToastUtils.showToastOnce("do click");
        x5WebView.loadUrl("javascript:(function(){alert(\"aaa\");})()");
//        new JSCallBack(null, x5WebView).callJS("javascript:invokeFunc()");
/*
        x5WebView.evaluateJavascript("invokeFunc()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {

            }
        });*/

    }
}
