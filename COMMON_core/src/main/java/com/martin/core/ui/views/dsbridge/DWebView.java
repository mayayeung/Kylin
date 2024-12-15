package com.martin.core.ui.views.dsbridge;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.JavascriptInterface;

import androidx.annotation.Keep;

import com.martin.core.ui.views.X5WebView;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import static android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW;

/**
 * Created by DingJinZhu on 2020/8/24.
 * Description:
 */
public class DWebView extends X5WebView {
    private static final String BRIDGE_NAME = "_dsbridge";
    private static final String LOG_TAG = "dsBridge";
    private static boolean isDebug = false;
    private String APP_CACHE_DIRNAME;
    private WebChromeClient webChromeClient;
    private Map<String, Object> javaScriptNamespaceInterfaces = new HashMap();
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private JavascriptCloseWindowListener javascriptCloseWindowListener = null;
    private ArrayList<CallInfo> callInfoList;
    private InnerJavascriptInterface innerJavascriptInterface = new InnerJavascriptInterface();
    private Map<Integer, OnReturnValue> handlerMap = new HashMap<>();
    private int callID = 0;

    public DWebView(Context context) {
        super(context, null);
        init();
    }

    public DWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        APP_CACHE_DIRNAME = getContext().getFilesDir().getAbsolutePath() + "/webcache";
        WebSettings settings = getSettings();
        settings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
            settings.setMixedContentMode(MIXED_CONTENT_ALWAYS_ALLOW);
        }
        settings.setAllowFileAccess(false);
        settings.setAppCacheEnabled(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setAppCachePath(APP_CACHE_DIRNAME);
        settings.setUseWideViewPort(true);
        setWebChromeClient(mWebChromeClient);
        addInternalJavascriptObject();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            super.addJavascriptInterface(innerJavascriptInterface, BRIDGE_NAME);
        } else {
            // add bridge tag in lower android version
            settings.setUserAgentString(settings.getUserAgentString() + " _dsbridge");
        }
    }

    class InnerJavascriptInterface {

        private void PrintDebugInfo(String error) {
            Log.d(LOG_TAG, error);
            if (isDebug) {
                evaluateJavascript(String.format("alert('%s')", "DEBUG ERR MSG:\\n" + error.replaceAll("\\'", "\\\\'")));
            }
        }

        @Keep
        @JavascriptInterface
        public String call(String methodName, String argStr) {
            String error = "Js bridge  called, but can't find a corresponded " +
                    "JavascriptInterface object , please check your code!";
            String[] nameStr = parseNamespace(methodName.trim());
            methodName = nameStr[1];
            Object jsb = javaScriptNamespaceInterfaces.get(nameStr[0]);
            JSONObject ret = new JSONObject();
            try {
                ret.put("code", -1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (jsb == null) {
                PrintDebugInfo(error);
                return ret.toString();
            }
            Object arg = null;
            Method method = null;
            String callback = null;

            try {
                JSONObject args = new JSONObject(argStr);
                if (args.has("_dscbstub")) {
                    callback = args.getString("_dscbstub");
                }
                if (args.has("data")) {
                    arg = args.get("data");
                }

            } catch (JSONException e) {
                error = String.format("The argument of \"%s\" must be a JSON object string!", methodName);
                PrintDebugInfo(error);
                e.printStackTrace();
                return ret.toString();
            }


            Class<?> cls = jsb.getClass();
            boolean asyn = false;
            try {
                method = cls.getMethod(methodName,
                        new Class[]{Object.class, CompletionHandler.class});
                asyn = true;
            } catch (Exception e) {
                try {
                    method = cls.getMethod(methodName, new Class[]{Object.class});
                } catch (Exception ex) {

                }
            }

            if (method == null) {
                error = "Not find method \"" + methodName + "\" implementation! please check if the  signature or namespace of the method is right ";
                PrintDebugInfo(error);
                return ret.toString();
            }

            JavascriptInterface annotation = method.getAnnotation(JavascriptInterface.class);
            if (annotation == null) {
                error = "Method " + methodName + " is not invoked, since  " +
                        "it is not declared with JavascriptInterface annotation! ";
                PrintDebugInfo(error);
                return ret.toString();
            }

            Object retData;
            method.setAccessible(true);
            try {
                if (asyn) {
                    final String cb = callback;
                    method.invoke(jsb, arg, new CompletionHandler() {

                        @Override
                        public void complete(Object retValue) {
                            complete(retValue, true);
                        }

                        @Override
                        public void complete() {
                            complete(null, true);
                        }

                        @Override
                        public void setProgressData(Object value) {
                            complete(value, false);
                        }

                        private void complete(Object retValue, boolean complete) {
                            try {
                                JSONObject ret = new JSONObject();
                                ret.put("code", 0);
                                ret.put("data", retValue);
                                //retValue = URLEncoder.encode(ret.toString(), "UTF-8").replaceAll("\\+", "%20");
                                if (cb != null) {
                                    //String script = String.format("%s(JSON.parse(decodeURIComponent(\"%s\")).data);", cb, retValue);
                                    String script = String.format("%s(%s.data);", cb, ret.toString());
                                    if (complete) {
                                        script += "delete window." + cb;
                                    }
                                    evaluateJavascript(script);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    retData = method.invoke(jsb, arg);
                    ret.put("code", 0);
                    ret.put("data", retData);
                    return ret.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
                error = String.format("Call failed：The parameter of \"%s\" in Java is invalid.", methodName);
                PrintDebugInfo(error);
                return ret.toString();
            }
            return ret.toString();
        }

    }

    private WebChromeClient mWebChromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (webChromeClient != null) {
                webChromeClient.onProgressChanged(view, newProgress);
            } else {
                super.onProgressChanged(view, newProgress);
            }
        }

    };

    /**
     * Set debug mode. if in debug mode, some errors will be prompted by a dialog
     * and the exception caused by the native handlers will not be captured.
     *
     * @param enabled
     */
    public static void setWebContentsDebuggingEnabled(boolean enabled) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(enabled);
        }
        isDebug = enabled;
    }


    /**
     * Add a java object which implemented the javascript interfaces to dsBridge with namespace.
     * Remove the object using {@link #removeJavascriptObject(String) removeJavascriptObject(String)}
     *
     * @param object
     * @param namespace if empty, the object have no namespace.
     */
    public void addJavascriptObject(Object object, String namespace) {
        if (namespace == null) {
            namespace = "";
        }
        if (object != null) {
            javaScriptNamespaceInterfaces.put(namespace, object);
        }
    }

    /**
     * remove the javascript object with supplied namespace.
     *
     * @param namespace
     */
    public void removeJavascriptObject(String namespace) {
        if (namespace == null) {
            namespace = "";
        }
        javaScriptNamespaceInterfaces.remove(namespace);

    }

    /**
     * This method can be called in any thread, and if it is not called in the main thread,
     * it will be automatically distributed to the main thread.
     *
     * @param script
     */
    public void evaluateJavascript(final String script) {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                _evaluateJavascript(script);
            }
        });
    }

    /**
     * This method can be called in any thread, and if it is not called in the main thread,
     * it will be automatically distributed to the main thread.
     *
     * @param url
     */
    @Override
    public void loadUrl(final String url) {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                callInfoList = new ArrayList<>();
                DWebView.super.loadUrl(url);
            }
        });
    }

    /**
     * This method can be called in any thread, and if it is not called in the main thread,
     * it will be automatically distributed to the main thread.
     *
     * @param url
     * @param additionalHttpHeaders
     */
    @Override
    public void loadUrl(final String url, final Map<String, String> additionalHttpHeaders) {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                callInfoList = new ArrayList<>();
                DWebView.super.loadUrl(url, additionalHttpHeaders);
            }
        });
    }

    @Override
    public void reload() {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                callInfoList = new ArrayList<>();
                DWebView.super.reload();
            }
        });
    }


    private void _evaluateJavascript(String script) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            DWebView.super.evaluateJavascript(script, null);
        } else {
            super.loadUrl("javascript:" + script);
        }
    }

    private void addInternalJavascriptObject() {
        addJavascriptObject(new Object() {

            @JavascriptInterface
            public boolean hasNativeMethod(Object args) throws JSONException {

                JSONObject jsonObject = (JSONObject) args;
                String methodName = jsonObject.getString("name").trim();
                String type = jsonObject.getString("type").trim();
                String[] nameStr = parseNamespace(methodName);
                Object jsb = javaScriptNamespaceInterfaces.get(nameStr[0]);
                if (jsb != null) {
                    Class<?> cls = jsb.getClass();
                    boolean asyn = false;
                    Method method = null;
                    try {
                        method = cls.getMethod(nameStr[1],
                                new Class[]{Object.class, CompletionHandler.class});
                        asyn = true;
                    } catch (Exception e) {
                        try {
                            method = cls.getMethod(nameStr[1], new Class[]{Object.class});
                        } catch (Exception ex) {

                        }
                    }
                    if (method != null) {
                        JavascriptInterface annotation = method.getAnnotation(JavascriptInterface.class);
                        if (annotation != null) {
                            if ("all".equals(type) || (asyn && "asyn".equals(type) || (!asyn && "syn".equals(type)))) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            }

            @JavascriptInterface
            public String closePage(Object object) throws JSONException {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (javascriptCloseWindowListener == null
                                || javascriptCloseWindowListener.onClose()) {
                            Context context = getContext();
                            if (context instanceof Activity) {
                                ((Activity) getContext()).onBackPressed();
                            }
                        }
                    }
                });
                return null;
            }

            @JavascriptInterface
            public void dsinit(Object jsonObject) {
                DWebView.this.dispatchStartupQueue();
            }

            @JavascriptInterface
            public void returnValue(final Object obj) throws JSONException {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = (JSONObject) obj;
                        Object data = null;
                        try {
                            int id = jsonObject.getInt("id");
                            boolean isCompleted = jsonObject.getBoolean("complete");
                            OnReturnValue handler = handlerMap.get(id);
                            if (jsonObject.has("data")) {
                                data = jsonObject.get("data");
                            }
                            if (handler != null) {
                                handler.onValue(data);
                                if (isCompleted) {
                                    handlerMap.remove(id);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

        }, "_dsb");
    }

    private String[] parseNamespace(String method) {
        int pos = method.lastIndexOf('.');
        String namespace = "";
        if (pos != -1) {
            namespace = method.substring(0, pos);
            method = method.substring(pos + 1);
        }
        return new String[]{namespace, method};
    }

    private void runOnMainThread(Runnable runnable) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            runnable.run();
            return;
        }
        mainHandler.post(runnable);
    }

    public interface JavascriptCloseWindowListener {
        /**
         * @return If true, close the current activity, otherwise, do nothing.
         */
        boolean onClose();
    }

    private synchronized void dispatchStartupQueue() {
        if (callInfoList != null) {
            for (CallInfo info : callInfoList) {
                dispatchJavascriptCall(info);
            }
            callInfoList = null;
        }
    }

    private class CallInfo {
        public CallInfo(String handlerName, int id, Object[] args) {
            if (args == null) args = new Object[0];
            data = new JSONArray(Arrays.asList(args)).toString();
            callbackId = id;
            method = handlerName;
        }

        @Override
        public String toString() {
            JSONObject jo = new JSONObject();
            try {
                jo.put("method", method);
                jo.put("callbackId", callbackId);
                jo.put("data", data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jo.toString();
        }

        public String data = null;
        public int callbackId;
        public String method;
    }


    private void dispatchJavascriptCall(CallInfo info) {
        evaluateJavascript(String.format("window._handleMessageFromNative(%s)", info.toString()));
    }

    public synchronized <T> void callHandler(String method, Object[] args, final OnReturnValue<T> handler) {

        CallInfo callInfo = new CallInfo(method, callID++, args);
        if (handler != null) {
            handlerMap.put(callInfo.callbackId, handler);
        }

        if (callInfoList != null) {
            callInfoList.add(callInfo);
        } else {
            dispatchJavascriptCall(callInfo);
        }

    }

    public void callHandler(String method, Object[] args) {
        callHandler(method, args, null);
    }

    public <T> void callHandler(String method, OnReturnValue<T> handler) {
        callHandler(method, null, handler);
    }

    /**
     * Test whether the handler exist in javascript
     *
     * @param handlerName
     * @param existCallback
     */
    public void hasJavascriptMethod(String handlerName, OnReturnValue<Boolean> existCallback) {
        callHandler("_hasJavascriptMethod", new Object[]{handlerName}, existCallback);
    }

}
