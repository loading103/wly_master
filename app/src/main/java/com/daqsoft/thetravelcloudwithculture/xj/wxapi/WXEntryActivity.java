package com.daqsoft.thetravelcloudwithculture.xj.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.daqsoft.baselib.utils.ToastUtils;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;

public class WXEntryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("WXEntryActivity", "onCreate");
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("WXEntryActivity", "onNewIntent");
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        SendAuth.Resp resp = new SendAuth.Resp(intent.getExtras());
            Log.e("----------分享回调",new Gson().toJson(resp));
        if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
                // 这是分享
//                ToastUtils.showMessage("分享成功");

        }else if(resp.errCode == BaseResp.ErrCode.ERR_USER_CANCEL){
                // 这是分享
//                ToastUtils.showMessage("分享取消");
        }
        finish();
    }
}