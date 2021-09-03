package com.daqsoft.provider.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.daqsoft.provider.ProjectConfig;
import com.daqsoft.provider.R;
import com.daqsoft.provider.view.BaseDialog;

import java.util.ArrayList;


/**
 * @Description 分享工具
 * @ClassName ShareUtils
 * @Author PuHua
 * @Time 2020/1/3 16:43
 * @Version 1.0
 */
public class ShareUtils {
    Activity mContext;
    BaseDialog mShareDialog;

    /**
     * 三方登录
     */
    ShareUtils(Activity context) {
        mContext = context;
    }

    void init() {

    }

    /**
     * 初始化弹框
     */
    private void ininShareDialog() {
        mShareDialog = new BaseDialog(mContext);
    }
}
