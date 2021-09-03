package com.daqsoft.travelCultureModule.country.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

/**
 * desc :自定义scrollview滑动事件
 * @author 江云仙
 * @date 2020/4/13 11:43
 */
public class CountryNestScrollView extends NestedScrollView {

        public CountryNestScrollView(@NonNull Context context) {
            super(context);
        }

        public CountryNestScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        public CountryNestScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        protected void onScrollChanged(int l, int t, int oldl, int oldt) {
            super.onScrollChanged(l, t, oldl, oldt);
            if (onScrollListener != null){
                onScrollListener.onScrollChanged(l,t,oldl,oldt);
            }
//        Log.e("onScrollChanged","l=="+l+"  t=="+t+"  oldl=="+oldl+"   oldt=="+oldt);
        }

        @Override
        protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
            super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
            if (onScrollListener != null){
                onScrollListener.onOverScrolled(scrollX,scrollY,clampedX,clampedY);
            }
//        Log.e("onOverScrolled","scrollX=="+scrollX+"  scrollY=="+scrollY+"  clampedX=="+clampedX+"   clampedY=="+clampedY);
        }


        public void setOnScrollListener(OnScrollListener onScrollListener){
            this.onScrollListener = onScrollListener;
        }
        private OnScrollListener onScrollListener;
        public interface OnScrollListener{
            void onScrollChanged(int l, int t, int oldl, int oldt);
            void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY);
        }
        //重写这个方法，并且在方法里面请求所有的父控件都不要拦截他的事件
        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            getParent().requestDisallowInterceptTouchEvent(true);
            return super.dispatchTouchEvent(ev);
        }

}
