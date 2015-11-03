package com.testerhome.nativeandroid.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.testerhome.nativeandroid.Config;
import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.views.base.BackBaseActivity;

public class SendToWXActivity extends BackBaseActivity {

    private String mTitle;
    private String mBody;

    private IWXAPI api;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean resolved = resolveIntent(getIntent());
        if (!resolved) {
            finish();
            return;
        }

        api = WXAPIFactory.createWXAPI(this, Config.APP_ID, true);
        api.registerApp(Config.APP_ID);

        prepareUI();

    }

    private boolean resolveIntent(Intent intent) {
        if (Intent.ACTION_SEND.equals(intent.getAction()) &&
                "text/plain".equals(intent.getType())) {
            mTitle = intent.getStringExtra(Intent.EXTRA_TITLE);
            mBody = intent.getStringExtra(Intent.EXTRA_TEXT);
            return true;
        }
        return false;
    }

    private void prepareUI() {
        Log.e("share info", mBody);
        // mTextMessageBody.setText(mBody);

        if (!api.isWXAppInstalled()) {
            Toast.makeText(SendToWXActivity.this, "您还未安装微信客户端",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        WXWebpageObject webpageObject = new WXWebpageObject(mBody);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = webpageObject;
        msg.title = mTitle;

        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        msg.setThumbImage(thumb);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;

        api.sendReq(req);

        this.finish();
    }
}
