package com.testerhome.nativeandroid.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by vclub on 15/10/24.
 */
public class NetworkUtils {

    public static boolean isNetworkAvailable(Context ctx){
        ConnectivityManager connectivityManager
                = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
