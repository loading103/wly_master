package com.daqsoft.provider.view.scrollview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

import androidx.core.widget.NestedScrollView;

/**
 * @author ly
 * @version V1.0
 * @Description: 自定义实现scrollView
 * @date 2016/1/12 15:29
 */
public class FadActionScrollview extends NestedScrollView {
    private OnScrollListener onScrollListener;
    private ScrollBottomListener scrollBottomListener;

    public FadActionScrollview(Context context) {
        super(context);
    }

    public FadActionScrollview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FadActionScrollview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private boolean mDisableEdgeEffects = true;

    /**
     * @author Cyril Mottier
     */
    public interface OnScrollChangedListener {
        void onScrollChanged(NestedScrollView who, int l, int t, int oldl, int oldt);
    }

    private OnScrollChangedListener mOnScrollChangedListener;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (mOnScrollChangedListener != null) {
            super.onScrollChanged(l, t, oldl, oldt);
            mOnScrollChangedListener.onScrollChanged(this, l, t, oldl, oldt);
        }
        if (onScrollListener != null) {
            onScrollListener.onScroll(t);
        }
        if (scrollBottomListener != null) {
            if (t + getHeight() >= (computeVerticalScrollRange() - 100)) {
                //ScrollView滑动到底部了
                scrollBottomListener.scrollBottom();
            }
        }
    }

    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        mOnScrollChangedListener = listener;
    }

    public void setScrollBottomListener(ScrollBottomListener scrollBottomListener) {
        this.scrollBottomListener = scrollBottomListener;
    }

    @Override
    protected float getTopFadingEdgeStrength() {
        // http://stackoverflow.com/a/6894270/244576
        if (mDisableEdgeEffects && Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return 0.0f;
        }
        return super.getTopFadingEdgeStrength();
    }

    @Override
    protected float getBottomFadingEdgeStrength() {
        // http://stackoverflow.com/a/6894270/244576
        if (mDisableEdgeEffects && Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return 0.0f;
        }
        return super.getBottomFadingEdgeStrength();
    }

    public void setOnScrollLIstener(OnScrollListener scrollListener) {
        this.onScrollListener = scrollListener;
    }

    public interface OnScrollListener {
        public void onScroll(int scrollY);
    }

    public interface ScrollBottomListener {
        public void scrollBottom();
    }
}
