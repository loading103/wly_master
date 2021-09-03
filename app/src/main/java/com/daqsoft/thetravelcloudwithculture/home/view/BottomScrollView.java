package com.daqsoft.thetravelcloudwithculture.home.view;


import android.content.Context;

import android.util.AttributeSet;

import android.widget.ScrollView;

import androidx.core.widget.NestedScrollView;


public class BottomScrollView extends NestedScrollView {

    private OnScrollToBottomListener onScrollToBottom;

    public BottomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomScrollView(Context context) {

        super(context);

    }



    @Override

    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX,

                                  boolean clampedY) {

        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);

        if(scrollY != 0 && null != onScrollToBottom){

            onScrollToBottom.onScrollBottomListener(clampedY);

        }

    }



    public void setOnScrollToBottomLintener(OnScrollToBottomListener listener){
        onScrollToBottom = listener;

    }



    public interface OnScrollToBottomListener{

        public void onScrollBottomListener(boolean isBottom);

    }

}