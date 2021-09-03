package com.daqsoft.baselib.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

/**
 * 解决adjustResize + 透明状态栏问题的外层ViewGroup
 *
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-8-8
 * @since JDK 1.8.0_191
 */
public class ImmerseGroup extends FrameLayout {
    public ImmerseGroup(Context context) {
        super(context);
    }

    public ImmerseGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImmerseGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), 0);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}