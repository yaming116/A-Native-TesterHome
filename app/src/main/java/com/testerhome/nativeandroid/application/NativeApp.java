package com.testerhome.nativeandroid.application;

import android.app.Application;
import android.os.Build;
import android.util.Log;
import android.webkit.WebView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.testerhome.nativeandroid.BuildConfig;
import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.auth.TesterHomeAccountService;
import com.testerhome.nativeandroid.models.OAuth;
import com.testerhome.nativeandroid.models.TesterUser;
import com.testerhome.nativeandroid.oauth2.AuthenticationService;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Bin Li on 2015/9/15.
 */
public class NativeApp extends Application {

    private static NativeApp instance;

    public static String TAG = "NativeApp";
    public static long timerTime = 82800;
    private Timer timer = null;

    public static NativeApp getInstance() {
        if (null == instance) {
            instance = new NativeApp();
        }
        return instance;
    }

    public void startTimer() {
        TesterUser testerUser = TesterHomeAccountService.getInstance(this).getActiveAccountInfo();
        Log.d(TAG, testerUser.getCreate_at() + "");
        Log.d(TAG, testerUser.getExpireDate() + "");
        Log.d(TAG, System.currentTimeMillis() + "");
        long leftTime = timerTime - (System.currentTimeMillis() / 1000 - testerUser.getCreate_at());
        if (leftTime < 0) {
            leftTime = 0;
        }

        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                TesterUser testerUser = TesterHomeAccountService.getInstance(instance).getActiveAccountInfo();
                if (testerUser.getRefresh_token() != null) {
                    String url = AuthenticationService.ACCESS_TOKEN_URL;

                    OkHttpClient okHttpClient = new OkHttpClient();

                    RequestBody formBody = new FormEncodingBuilder()
                            .add("client_id", AuthenticationService.getAuthorize_client_id())
                            .add("grant_type", "refresh_token")
                            .add("client_secret", "3a20127eb087257ad7196098bfd8240746a66b0550d039eb2c1901c025e7cbea")
                            .add("refresh_token", testerUser.getRefresh_token())
                            .build();

                    Request request = new Request.Builder()
                            .url(url)
                            .post(formBody)
                            .build();

                    Response response;
                    try {
                        response = okHttpClient.newCall(request).execute();

                        if (response.isSuccessful()) {
                            String responseStr = response.body().string();
                            Log.d(TAG, responseStr);
                            Gson gson = new Gson();
                            OAuth oAuth = gson.fromJson(responseStr, OAuth.class);
                            if (oAuth.getExpires_in() > 0 && oAuth.getAccess_token() != null) {
                                TesterHomeAccountService.getInstance(instance).updateAccountToken(oAuth);
                            }
                        } else {
                            Log.e("Tokenm", "error:" + response.message());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        timer.schedule(timerTask, leftTime * 1000);
    }


    public void cancelTimerTask() {
        Log.d(TAG, "i have cancel");
        if (timer != null) {
            timer.cancel();
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();

        // initialize fresco with OK HTTP

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.networkInterceptors().add(new StethoInterceptor());
        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(this, okHttpClient)
                .build();
        Fresco.initialize(this, config);

        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        mTracker = getDefaultTracker();
        mTracker.enableExceptionReporting(true);
        mTracker.enableAutoActivityTracking(true);

        startTimer();

        sRefWatcher = LeakCanary.install(this);

        Stetho.initializeWithDefaults(this);
    }

    private static RefWatcher sRefWatcher;

    public static RefWatcher getRefWatcher() {
        return sRefWatcher;
    }

    private Tracker mTracker;

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     *
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }
}
