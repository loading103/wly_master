package com.daqsoft.travelCultureModule.hotel.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 可以控制NestedScrollingChild2接口的Recyclerview
 *
 * @author EthanCo
 * @since 2017/12/8
 */
public class MAppBarReclyerView extends RecyclerView {
    boolean isNestedEnable = false;

    public MAppBarReclyerView(Context context) {
        super(context);
    }

    public MAppBarReclyerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MAppBarReclyerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean isNestedEnable() {
        return isNestedEnable;
    }

    public void setNestedEnable(boolean nestedEnable) {
        isNestedEnable = nestedEnable;
    }

    @Override
    public boolean startNestedScroll(int axes, int type) {
        if (isNestedEnable) {
            return super.startNestedScroll(axes, type);
        } else {
            return false;
        }
    }

    @Override
    public void stopNestedScroll(int type) {
        if (isNestedEnable) {
            super.stopNestedScroll(type);
        }
    }

    @Override
    public boolean hasNestedScrollingParent(int type) {
        if (isNestedEnable) {
            return super.hasNestedScrollingParent(type);
        } else {
            return false;
        }
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow, int type) {
        if (isNestedEnable) {
            return super.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
        } else {
            return false;
        }
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow, int type) {
        if (isNestedEnable) {
            return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
        } else {
            return false;
        }
    }
}

