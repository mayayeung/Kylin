package com.martin.core.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class NetWorkUtils {

    public static boolean isAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable();
    }

    /**
     * @param context
     * @return 0、手机网络 1、wifi -1、无网络
     */
    public static int networkType(Context context) {
        int mNetworkType = -1;
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable()) {
            mNetworkType = networkInfo.getType();
        }

        return mNetworkType;
    }

    /**
     * info.getSubtype()取值列表如下： NETWORK_TYPE_CDMA 网络类型为CDMA NETWORK_TYPE_EDGE
     * 网络类型为EDGE NETWORK_TYPE_EVDO_0 网络类型为EVDO0 NETWORK_TYPE_EVDO_A 网络类型为EVDOA
     * NETWORK_TYPE_GPRS 网络类型为GPRS NETWORK_TYPE_HSDPA 网络类型为HSDPA
     * NETWORK_TYPE_HSPA 网络类型为HSPA NETWORK_TYPE_HSUPA 网络类型为HSUPA
     * NETWORK_TYPE_UMTS 网络类型为UMTS
     * <p>
     * 联通的3G为UMTS或HSDPA，移动和联通的2G为GPRS或EDGE，电信的2G为CDMA，电信 的3G为EVDO
     */

    public static int getNetSpeedBytes() {
        String line;
        String[] segs;
        int received = 0;
        int i;
        int tmp = 0;
        boolean isNum;
        try {
            FileReader fr = new FileReader("/proc/net/dev");
            BufferedReader in = new BufferedReader(fr, 500);
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("rmnet") || line.startsWith("eth") || line.startsWith("wlan")) {
                    segs = line.split(":")[1].split(" ");
                    for (i = 0; i < segs.length; i++) {
                        isNum = true;
                        try {
                            tmp = Integer.parseInt(segs[i]);
                        } catch (Exception e) {
                            isNum = false;
                        }
                        if (isNum == true) {
                            received = received + tmp;
                            break;
                        }
                    }
                }
            }
            in.close();
        } catch (IOException e) {
            return -1;
        }
        return received;
    }

    //每隔5分钟取一次网络类型，每次取会耗时
    public static long lastTime = 0;
    public static String lastNetworkTypeName;
    public static long INTERVER = 5 * 60 * 1000;

    public static String getNetworkTypeName(Context context, boolean fromCache) {
        if (!fromCache) {
            return getNetworkTypeName(context);
        }
        if (TextUtils.isEmpty(lastNetworkTypeName) || System.currentTimeMillis() - lastTime > INTERVER) {
            lastTime = System.currentTimeMillis();
            if (context != null) {
                ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectMgr != null) {
                    NetworkInfo info = connectMgr.getActiveNetworkInfo();
                    if (info != null) {
                        switch (info.getType()) {
                            case ConnectivityManager.TYPE_WIFI:
                                return "WIFI";
                            case ConnectivityManager.TYPE_MOBILE:
                                return getNetworkTypeName(info.getSubtype());
                        }
                    }
                }
            }
            return getNetworkTypeName(TelephonyManager.NETWORK_TYPE_UNKNOWN);
        } else {
            return lastNetworkTypeName;
        }
    }

    public static String getNetworkTypeName(Context context) {
        if (context != null) {
            ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectMgr != null) {
                NetworkInfo info = connectMgr.getActiveNetworkInfo();
                if (info != null) {
                    switch (info.getType()) {
                        case ConnectivityManager.TYPE_WIFI:
                            return "WIFI";
                        case ConnectivityManager.TYPE_MOBILE:
                            return getNetworkTypeName(info.getSubtype());
                    }
                }
            }
        }
        return getNetworkTypeName(TelephonyManager.NETWORK_TYPE_UNKNOWN);

    }

    public static String getNetworkTypeName(int type) {
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GPRS";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "EDGE";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "UMTS";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return "HSUPA";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "HSPA";
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return "CDMA";
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return "CDMA - EvDo rev. 0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return "CDMA - EvDo rev. A";
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return "CDMA - EvDo rev. B";
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return "CDMA - 1xRTT";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "LTE";
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return "CDMA - eHRPD";
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "iDEN";
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "HSPA+";
            default:
                return "UNKNOWN";
        }
    }
}
