package com.daqsoft.baselib.widgets.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.airbnb.lottie.LottieAnimationView;
import com.daqsoft.baselib.base.BaseApplication;

import java.util.Random;

import daqsoft.com.baselib.R;

/**
 * @author ly
 * @version V1.0
 * @Title: CommonLoadingDialog
 * @Description: 加载弹窗
 * @date 2015/9/16 16:32
 */
public class CommonLoadingDialog extends AlertDialog {


    private LottieAnimationView lav_loading;

    public CommonLoadingDialog(Context context) {
        super(context, R.style.CommonLoadingDialog);
        init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_common_dialog);
        lav_loading = findViewById(R.id.lav_loading);
        if(BaseApplication.appArea.equals("ws")){
            int number = new Random().nextInt(2);
            Log.e("-number-------",number+"");
            if(number==1){
                lav_loading.setAnimation("loading.json");
            }else {
                lav_loading.setAnimation("wsloading2.json");
            }
        }
    }

    private void init() {
        setCancelable(true);
    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception e) {

        }

    }
}
