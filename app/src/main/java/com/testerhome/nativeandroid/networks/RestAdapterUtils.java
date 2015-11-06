package com.testerhome.nativeandroid.networks;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;
import com.testerhome.nativeandroid.utils.NetworkUtils;

import java.io.IOException;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by vclub on 15/10/24.
 */
public class RestAdapterUtils {

    /**
     * support offline cache
     *
     * @param endpoint
     * @param service
     * @param ctx
     * @param <T>
     * @return
     */
    public static <T> T getRestAPI(String endpoint, Class<T> service, final Context ctx) {

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                String cacheStr;
                if (NetworkUtils.isNetworkAvailable(ctx)) {
                    int maxAge = 60; // read from cache for 1 minute
                    cacheStr = "public, max-age=" + maxAge;
                } else {
                    int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
                    cacheStr = "public, only-if-cached, max-stale=" + maxStale;
                }
                Response response = chain.proceed(chain.request());
                return response.newBuilder()
                        .header("Cache-Control", cacheStr)
                        .build();
            }
        });
        try {
            int cacheSize = 10 * 1024 * 1024;
            Cache cache = new Cache(ctx.getCacheDir(), cacheSize);
            okHttpClient.setCache(cache);
        } catch (Exception ex) {
            Log.e("cache error", "can not create cache!");
        }

        okHttpClient.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, max-age=60")
                        .build();
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(endpoint)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(service);
    }
}
