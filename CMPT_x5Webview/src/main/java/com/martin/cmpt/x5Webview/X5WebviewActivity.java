package com.martin.cmpt.x5Webview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.martin.cmpt.x5Webview.utils.JSCallBack;
import com.martin.core.ui.views.NetErrorView;
import com.martin.core.ui.views.X5WebView;
import com.martin.core.utils.NetWorkUtils;
import com.martin.core.utils.ToastUtils;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class X5WebviewActivity extends FragmentActivity implements View.OnClickListener {
    private static final String TAG = X5WebviewActivity.class.getSimpleName();
    private ProgressBar mPageLoadingProgressBar = null;
    private NetErrorView netErrorView;
    X5WebView x5WebView;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.x5webview_activity_main);
        initWebview();
    }

    private void initWebview() {
        title = findViewById(R.id.title);
        mPageLoadingProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
        netErrorView = findViewById(R.id.netErrorView);
        x5WebView = findViewById(R.id.x5_webview);

        title.setOnClickListener(this);
        netErrorView.setOnRefreshListener(() -> x5WebView.reload());

        x5WebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                super.onPageStarted(webView, s, bitmap);
                netErrorView.hide();
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
            }

            @Override
            public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
                super.onReceivedError(webView, webResourceRequest, webResourceError);
                if (!NetWorkUtils.isAvailable(getApplicationContext())) {
                    netErrorView.show();
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                Uri parse = Uri.parse(url);
                String scheme = parse.getScheme();
                if (HybridConfig.SCHEME.equals(scheme)) {
                    String host = parse.getHost();
                    String param = parse.getQueryParameter(HybridConfig.GET_PARAM);
                    String callback = parse.getQueryParameter(HybridConfig.GET_CALLBACK);
                    Log.e(TAG, "host---" + host + "  param---" + param + "  callback---" + callback);
                }
                return false;
            }
        });

        x5WebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webView, int newProgress) {
                mPageLoadingProgressBar.setProgress(newProgress);
                if (mPageLoadingProgressBar != null && newProgress != 100) {
                    mPageLoadingProgressBar.setVisibility(View.VISIBLE);
                } else if (mPageLoadingProgressBar != null) {
                    mPageLoadingProgressBar.setVisibility(View.GONE);
                }
            }
        });

        x5WebView.addJavascriptInterface(new JSCallFunction(x5WebView), "jsBridge");
        x5WebView.loadUrl("file:///android_asset/ui.html");
    }

    public static void launchSelf(Context context) {
        Intent intent = new Intent(context, X5WebviewActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
//        x5WebView.loadUrl("javascript:invokeFunc()");
        new JSCallBack(null, x5WebView).callJS("javascript:nativeToast('abcdefghijklmn')");
    }
}
