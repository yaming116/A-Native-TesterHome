package com.testerhome.nativeandroid.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.testerhome.nativeandroid.views.base.BackBaseActivity;

/**
 * Created by vclub on 15/11/2.
 */
public class SendToWXActivity extends BackBaseActivity {

    private String mTitle;
    private String mBody;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean resolved = resolveIntent(getIntent());
        if (!resolved){
            finish();
            return;
        }

        prepareUI();

    }

    private boolean resolveIntent(Intent intent){
        if (Intent.ACTION_SEND.equals(intent.getAction()) &&
                "text/plain".equals(intent.getType())){
            mTitle = intent.getStringExtra(Intent.EXTRA_TITLE);
            mBody = intent.getStringExtra(Intent.EXTRA_TEXT);
            return true;
        }
        return false;
    }

    private void prepareUI() {
        Log.e("share info", mBody);
        // mTextMessageBody.setText(mBody);

        WXWebpageObject webpageObject = new WXWebpageObject(mBody);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = webpageObject;
        msg.description = "测试";

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;

        WXAPIFactory.createWXAPI(this, "wxecf3c70cafae1f8c", true).sendReq(req);

        this.finish();
    }
}
