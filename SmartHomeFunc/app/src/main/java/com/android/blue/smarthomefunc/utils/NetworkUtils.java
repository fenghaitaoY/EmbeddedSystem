package com.android.blue.smarthomefunc.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络工具类
 *
 * Created by root on 1/26/18.
 */

public class NetworkUtils {

    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager mConnectManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mConnectManager != null){
            NetworkInfo[] allNetworkInfo = mConnectManager.getAllNetworkInfo();
            if (allNetworkInfo != null){
                for (NetworkInfo networkInfo : allNetworkInfo){
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isActiveNetworkMobile(Context context){
        ConnectivityManager mConnectManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mConnectManager != null){
            NetworkInfo networkInfo = mConnectManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE){
                return true;
            }
        }
        return false;
    }

    public static boolean isActiveNetworkWifi(Context context){
        ConnectivityManager mConnectManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mConnectManager != null){
            NetworkInfo networkInfo = mConnectManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
                return true;
            }
        }
        return false;
    }
}
