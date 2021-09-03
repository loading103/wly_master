package com.daqsoft.provider.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import com.daqsoft.baselib.base.BaseApplication;
import com.daqsoft.baselib.utils.SPUtils;
import com.daqsoft.provider.SPKey;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;

import cn.jpush.android.api.JPushInterface;

public class JpushAliasUtils {

    public static int  ALIAS_NUMBER=1;
    public static int  TAG_NUMBER=2;
    /**
     * 设置Tag(初次设置，有时候会设置失败，在onTagOperatorResult接收极光的方法里面检测，失败就重新注册)
     */
    public  static  void setTag(Context context){
        Set<String> set= new HashSet<>();
        set.add("android");
        JPushInterface.setTags(context,TAG_NUMBER,set);
    }

    /**
     * 设置Alias(初次设置，有时候会设置失败，在onTagOperatorResult接收极光的方法里面检测，失败就重新注册)
     */
    public static  void setAlias(Context context){
        String phone = SPUtils.getInstance().getInt(SPKey.VIPID)+"";
        if(TextUtils.isEmpty(phone)){
            return;
        }
        Log.e("getRegistrationID----","getRegistrationID------"+JPushInterface.getRegistrationID(BaseApplication.context));
        JPushInterface.setAlias(context,ALIAS_NUMBER,phone);
    }

    /**
     * 清除Alias
     */
    public static  void clearAlias(Context context){
        JPushInterface.deleteAlias(context,ALIAS_NUMBER);
    }


}
