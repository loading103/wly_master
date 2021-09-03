package com.daqsoft.provider.mapview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import androidx.core.widget.NestedScrollView;

public class MapContainer extends RelativeLayout {
    // 定义一个SsrollView 将它与Activity的scrollView绑定；
    private NestedScrollView scrollView;

    //三个构造
    public MapContainer(Context context) {
        this(context, null);
    }

    public MapContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //通过这个方法将它与Activity的ScrollView绑定
    public void setScrollView(NestedScrollView scrollView) {
        this.scrollView = scrollView;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //事件分发
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            //手指按下阻止ScrollVIew向下发送事件
            scrollView.requestDisallowInterceptTouchEvent(false);
        } else {
            scrollView.requestDisallowInterceptTouchEvent(true);
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
