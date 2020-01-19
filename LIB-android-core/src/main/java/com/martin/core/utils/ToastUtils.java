package com.martin.core.utils;

import android.widget.Toast;
import com.martin.core.config.AppConfig;
import java.lang.ref.WeakReference;

public class ToastUtils {
    private ToastUtils() {}

    private static WeakReference<Toast> toast;

    public static synchronized void showToastOnce(final String message) {
        AppConfig.postOnUiThread(new Runnable() {

            public void run() {
                if(toast==null) {
                    toast = new WeakReference<Toast>(Toast.makeText(AppConfig.getContext(), message, Toast.LENGTH_SHORT));
                }else {
                    if(toast.get()==null){
                        toast = new WeakReference<Toast>(Toast.makeText(AppConfig.getContext(), message, Toast.LENGTH_SHORT));
                    }else{
                        toast.get().setText(message);
                    }
                }

                if(toast.get()!=null)
                    toast.get().show();
            }
        });
    }

    public static void hideToast() {
        if (toast != null && toast.get() != null) {
            toast.get().cancel();
        }
    }

}
