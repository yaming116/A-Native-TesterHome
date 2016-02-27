package com.testerhome.nativeandroid.networks;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;
import com.testerhome.nativeandroid.Config;
import com.testerhome.nativeandroid.utils.NetworkUtils;

import java.io.IOException;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

/**
 * Created by vclub on 15/10/24.
 */
public class RestAdapterUtils {

    /**
     * support offline cache
     *
     * @return
     */
    public static TopicsService getRestAPI(Context ctx) {

        final boolean isOnline = NetworkUtils.isNetworkAvailable(ctx);

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                String cacheStr;
                if (isOnline) {
                    cacheStr = "public, max-age=60";
                } else {
                    cacheStr = "public, only-if-cached, max-stale=" + (60 * 60 * 24 * 28);
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
                .baseUrl(Config.BASE_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(TopicsService.class);
    }
}
