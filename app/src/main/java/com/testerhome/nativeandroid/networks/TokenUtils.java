package com.testerhome.nativeandroid.networks;

import android.content.Context;

import com.testerhome.nativeandroid.auth.TesterHomeAccountService;
import com.testerhome.nativeandroid.oauth2.AuthenticationService;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Bin Li on 2016/4/27.
 */
public class TokenUtils {

    public static void RefreshToken(Context context, String refresh_token){
        RestAdapterUtils.getRestAPI(context)
                .refreshToken(AuthenticationService.getAuthorize_client_id(),
                        "refresh_token",
                        "3a20127eb087257ad7196098bfd8240746a66b0550d039eb2c1901c025e7cbea",
                        refresh_token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(oAuth -> {
                            TesterHomeAccountService.getInstance(context).updateAccountToken(oAuth);
                        },
                        throwable -> {
                            // can't get new token
                        },
                        () -> {
                            // on complete
                        });
    }
}
