package com.testerhome.nativeandroid.networks;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;

import java.io.IOException;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

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
        okHttpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

//                if (NetworkUtils.isNetworkAvailable(ctx)) {
//                    Log.e("cache", "cache for 10");
//                    int maxAge = 600; // read from cache for 10 minute
//                    chain.request().addHeader("Cache-Control", "public, max-age=" + maxAge);
//                } else {
//                    Log.e("cache", "cache for 4 weeks");
//                    int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
//                    chain.request().addHeader("Cache-Control",
//                            "public, only-if-cached, max-stale=" + maxStale);
//                }
                Response response = chain.proceed(chain.request());

                // Do anything with response here

                return response;
            }
        });
        try{
            int cacheSize = 10 * 1024 * 1024;
            Cache cache = new Cache(ctx.getCacheDir(), cacheSize);
            okHttpClient.setCache(cache);
        }catch (Exception ex){
            Log.e("cache error", "can not create cache!");
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(endpoint)
//                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(service);
    }
}
