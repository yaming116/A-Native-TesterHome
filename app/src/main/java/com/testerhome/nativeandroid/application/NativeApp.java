package com.testerhome.nativeandroid.application;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.squareup.okhttp.OkHttpClient;

import im.fir.sdk.FIR;

/**
 * Created by Bin Li on 2015/9/15.
 */
public class NativeApp extends Application {

    @Override
    public void onCreate() {
        FIR.init(this);
        super.onCreate();

        // initialize fresco with OK HTTP
        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(this, new OkHttpClient())
                .build();
        Fresco.initialize(this, config);
    }

}
