package com.daqsoft.provider.scrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import androidx.core.widget.NestedScrollView;


public class DqScrollView extends NestedScrollView {

    private OnScrollListener listener;

    public void setOnScrollListener(OnScrollListener listener) {
        this.listener = listener;
    }
    public DqScrollView(Context context) {
        super(context);
    }

    public DqScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DqScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    //设置接口
    public interface OnScrollListener{
        void onScrollY(int scrollY);
    }

    //重写原生onScrollChanged方法，将参数传递给接口，由接口传递出去
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(listener != null){

            //这里我只传了垂直滑动的距离
            listener.onScrollY(t);
        }
    }

}
