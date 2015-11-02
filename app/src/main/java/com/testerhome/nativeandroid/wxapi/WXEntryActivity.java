package com.testerhome.nativeandroid.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

/**
 * Created by vclub on 15/11/2.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        switch (baseReq.getType()){
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:

                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:

                break;
            default:
                break;
        }
        finish();
    }

    @Override
    public void onResp(BaseResp baseResp) {
        String result;

        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = "errcode_success";
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "errcode_cancel";
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "errcode_deny";
                break;
            default:
                result = "errcode_unknown";
                break;
        }

        Log.v("weixin", "result" + result);
    }
}
