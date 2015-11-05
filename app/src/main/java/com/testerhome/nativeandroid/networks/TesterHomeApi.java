package com.testerhome.nativeandroid.networks;

import com.testerhome.nativeandroid.Config;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by Bin Li on 2015/9/16.
 */
public class TesterHomeApi {

    private static TesterHomeApi instance;
    private TopicsService topicsService;

    public static TesterHomeApi getInstance() {
        if (instance == null){
            instance = new TesterHomeApi();
        }
        return instance;
    }

    private TesterHomeApi(){
        Retrofit retrofit = buildRestAdapter();
        this.topicsService = retrofit.create(TopicsService.class);
    }

    private Retrofit buildRestAdapter() {
        return new Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public TopicsService getTopicsService() {
        return topicsService;
    }

}
