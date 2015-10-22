package com.testerhome.nativeandroid.application;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.util.Log;

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
        checkFIRPermission();
        super.onCreate();

        // initialize fresco with OK HTTP
        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(this, new OkHttpClient())
                .build();
        Fresco.initialize(this, config);
    }

    private void checkFIRPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int hasReadPhoneStatePermission = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
            if (hasReadPhoneStatePermission != PackageManager.PERMISSION_GRANTED) {
                Log.e("bugHD", "bugHD init fail!");
                return;
            }
        }

        FIR.init(this);
        Log.e("bugHD", "bugHD init!");
    }

}
