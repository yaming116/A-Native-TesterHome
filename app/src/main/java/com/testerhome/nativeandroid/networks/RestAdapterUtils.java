package com.testerhome.nativeandroid.networks;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.testerhome.nativeandroid.BuildConfig;
import com.testerhome.nativeandroid.utils.NetworkUtils;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by vclub on 15/10/24.
 */
public class RestAdapterUtils {

    /**
     * support offline cache
     * @param endpoint
     * @param service
     * @param ctx
     * @param <T>
     * @return
     */
    public static <T> T getRestAPI(String endpoint, Class<T> service, final Context ctx) {

        OkHttpClient okHttpClient = new OkHttpClient();
        try{
            int cacheSize = 10 * 1024 * 1024;
            Cache cache = new Cache(ctx.getCacheDir(), cacheSize);
            okHttpClient.setCache(cache);
        }catch (Exception ex){
            Log.e("cache error", "can not create cache!");
        }

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(BuildConfig.DEBUG? RestAdapter.LogLevel.FULL: RestAdapter.LogLevel.NONE)
                .setEndpoint(endpoint)
                .setClient(new OkClient(okHttpClient))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        if (NetworkUtils.isNetworkAvailable(ctx)) {
                            Log.e("cache", "cache for 10");
                            int maxAge = 600; // read from cache for 10 minute
                            request.addHeader("Cache-Control", "public, max-age=" + maxAge);
                        } else {
                            Log.e("cache", "cache for 4 weeks");
                            int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
                            request.addHeader("Cache-Control",
                                    "public, only-if-cached, max-stale=" + maxStale);
                        }
                    }
                })
                .build();

        return restAdapter.create(service);
    }
}
