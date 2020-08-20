package com.martin.cmpt.x5Webview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.martin.core.ui.views.X5WebView;

public class X5WebviewActivity extends FragmentActivity {
    X5WebView x5WebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.x5webview_activity_main);
        x5WebView = findViewById(R.id.x5_webview);
        x5WebView.loadUrl("https://www.baidu.com");
        x5WebView.addJavascriptInterface(new JSCallFunction(), "jsBridge");
    }

    public static void launchSelf(Context context) {
        Intent intent = new Intent(context, X5WebviewActivity.class);
        context.startActivity(intent);
    }
}
