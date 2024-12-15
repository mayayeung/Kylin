package com.martin.cmpt.x5Webview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.martin.core.ui.views.NetErrorView;
import com.martin.core.ui.views.dsbridge.DWebView;
import com.martin.core.utils.NetWorkUtils;
import com.martin.core.utils.StatusBarUtils;
import com.martin.core.utils.TextUtils;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class X5WebviewActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = X5WebviewActivity.class.getSimpleName();
    private ProgressBar mPageLoadingProgressBar = null;
    private NetErrorView netErrorView;
    DWebView webView;
    TextView title;
    EditText urlEt;
    Button urlGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.x5webview_activity_main);
        /*if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

            int option2 = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(option2);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }*/

        /*StatusBarUtils.setTranslucentStatus(this);
        if (!StatusBarUtils.setStatusBarDarkTheme(this, true)) {

        } else {
            StatusBarUtils.setStatusBarColor(this, 0x11000000);
            StatusBarUtils.setRootViewFitsSystemWindows(this, false);
        }*/

//        StatusBarUtils.setTranslucentStatus(this);
        initNativeView();
        initWebview();
    }



    private static boolean isImmersive = false;

    private void initNativeView() {
        findViewById(R.id.addValue).setOnClickListener(this);
        findViewById(R.id.append).setOnClickListener(this);
        urlEt = findViewById(R.id.url_et);
        urlGo = findViewById(R.id.url_go);
        urlGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isImmersive = !isImmersive;
                String url = urlEt.getText().toString();
                if (!TextUtils.isEmpty(url)) {
                    webView.loadUrl(url);
                }
                StatusBarUtils.setTranslucentStatus(X5WebviewActivity.this);
//                StatusBarUtils.setAppImmersive(X5WebviewActivity.this, isImmersive, !isImmersive, true, Color.GREEN);
            }
        });
    }

    private void initWebview() {
        title = findViewById(R.id.title);
        mPageLoadingProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
        netErrorView = findViewById(R.id.netErrorView);
        webView = findViewById(R.id.web_view);

        title.setOnClickListener(this);
        netErrorView.setOnRefreshListener(() -> webView.reload());

        webView.setWebViewClient(new WebViewClient() {
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
                return false;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
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

        webView.setWebContentsDebuggingEnabled(true);
        webView.addJavascriptObject(new JsApi(), null);
        webView.loadUrl("file:///android_asset/dsbridge.html");
        webView.loadUrl("https://www.wuhaneduyun.cn/");
    }

    public static void launchSelf(Context context) {
        Intent intent = new Intent(context, X5WebviewActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*
            case R.id.addValue:
                webView.callHandler("addValue", new Object[]{3, 4}, new OnReturnValue<Integer>() {
                    @Override
                    public void onValue(Integer retValue) {
                        ToastUtils.showToastOnce(retValue.toString());
                    }
                });
                break;
            case R.id.append:
                webView.callHandler("append", new Object[]{"I", "and", "you"}, new OnReturnValue<String>() {
                    @Override
                    public void onValue(String retValue) {
                        ToastUtils.showToastOnce(retValue);
                    }
                });
                break;
                */
        }
    }
}
