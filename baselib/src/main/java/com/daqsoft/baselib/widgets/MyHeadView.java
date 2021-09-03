package com.daqsoft.baselib.widgets;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.daqsoft.baselib.base.BaseApplication;
import com.daqsoft.baselib.utils.SPUtils;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

import daqsoft.com.baselib.R;

public class MyHeadView extends LinearLayout implements RefreshHeader {

    private LottieAnimationView lottie;
    private TextView tip;
    private ImageView ivLoading;
    private AnimatorUtil animatorUtil;
    public MyHeadView(Context context) {
        super(context);
        initView(context);
    }
    public MyHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView(context);
    }
    public MyHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context);
    }
    private void initView(Context context) {
        setGravity(Gravity.CENTER);
        View     view =null;

        if(BaseApplication.appArea=="sc" ||BaseApplication.appArea=="xj"){
            view = LayoutInflater.from(context).inflate(R.layout.layout_refresh_head_panda, this);
            lottie  = view.findViewById(R.id.lottie);
            if(BaseApplication.appArea=="sc"){
                lottie.setAnimation("lottie_animation_refresh_header_panda.json");
                lottie.setImageAssetsFolder("refresh");
            }
            else{
                lottie.setAnimation("load_more.json");
                lottie.setImageAssetsFolder("images");
            }
        }else{
            animatorUtil=new  AnimatorUtil(context);
            view = LayoutInflater.from(context).inflate(R.layout.layout_refresh_head_ws, this);
            ivLoading  = view.findViewById(R.id.im_loading);
            animatorUtil.FrameAnimation(ivLoading);
        }
        tip = (TextView)view.findViewById(R.id.tips);
        String tips = SPUtils.getInstance().getString("main_tips", "加载中...");
        tip.setText(tips);
        tip.setVisibility(VISIBLE);
    }
    @NonNull
    public View getView() {
        return this;//真实的视图就是自己，不能返回null
    }
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;//指定为平移，不能null
    }
    @Override
    public void onStartAnimator(RefreshLayout layout, int headHeight, int maxDragHeight) {
    }
    @Override
    public int onFinish(RefreshLayout layout, boolean success) {
        if(lottie!=null){
            lottie.cancelAnimation();
            lottie.clearAnimation();
        }
        if(ivLoading!=null){
            ivLoading.clearAnimation();
        }
        clearFocus();
        return 200;//延迟500毫秒之后再弹回
    }
    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        switch (newState) {
            case None:
            case PullDownToRefresh:
                if(lottie!=null){
                    lottie.playAnimation();
                }
                break;
            case Refreshing:
                break;
            case ReleaseToRefresh:
                break;
        }
    }


    /**
     * 色设置提示文字
     */
    public void setTips(String   text){
        if(!TextUtils.isEmpty(text)){
            tip.setText(text);
        }
    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }
    @Override
    public void onInitialized(RefreshKernel kernel, int height, int maxDragHeight) {
    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {

    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {
    }
    @Override
    public void setPrimaryColors(@ColorInt int ... colors){
    }
}