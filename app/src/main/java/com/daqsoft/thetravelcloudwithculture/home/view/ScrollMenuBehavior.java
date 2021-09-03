package com.daqsoft.thetravelcloudwithculture.home.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.google.android.material.appbar.AppBarLayout;

import java.lang.reflect.Field;

/**
 * @Description 自定义的首页上拉时的Behavior
 * @ClassName TransferHeaderBehavior
 * @Author PuHua
 * @Time 2019/12/6 15:24
 * @Version 1.0
 */
public class ScrollMenuBehavior extends AppBarLayout.ScrollingViewBehavior {
    private OverScroller mScroller;


    public ScrollMenuBehavior(Context context, AttributeSet set) {
        super(context, set);
        getParentScroller(context);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return super.layoutDependsOn(parent, child, dependency);

    }

    /**
     * 反射获得滑动属性。
     *
     * @param context
     */
    private void getParentScroller(Context context) {
        if (mScroller != null) return;
        mScroller = new OverScroller(context);
        try {
            // 父类AppBarLayout.Behavior父类的父类HeaderBehavior
            Class<?> reflex_class = getClass().getSuperclass().getSuperclass();
            Field fieldScroller = reflex_class.getDeclaredField("mScroller");
            fieldScroller.setAccessible(true);
            fieldScroller.set(this, mScroller);
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        if (child instanceof TopLineChangeScrollView){
            TopLineChangeScrollView topLineChangeScrollView = (TopLineChangeScrollView) child;

            topLineChangeScrollView.change();

        }

        return super.onDependentViewChanged(parent, child, dependency);



    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
        if (mScroller != null) {
            // 当recyclerView 做好滑动准备的时候 直接干掉Appbar的滑动
            if (mScroller.computeScrollOffset()) {
                mScroller.abortAnimation();
            }
        }
        // recyclerview的惯性比较大 ,会顶在头部一会儿, 到头直接干掉它的滑动
        if (type == ViewCompat.TYPE_NON_TOUCH && getTopAndBottomOffset() == 0) {
            ViewCompat.stopNestedScroll(target, type);
        }
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
    }

    @Override
    public boolean onTouchEvent(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                break;
        }
        return super.onTouchEvent(parent, child, ev);
    }


}
