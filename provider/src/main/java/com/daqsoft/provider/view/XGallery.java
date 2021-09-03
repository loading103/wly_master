package com.daqsoft.provider.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.daqsoft.baselib.base.BaseApplication;
import com.daqsoft.baselib.utils.Utils;
import com.daqsoft.provider.R;

import timber.log.Timber;

/**
 * @author PuHua
 * @des 仿制gallery
 * @Date 2019/12/9 13:55
 */
public class XGallery extends FrameLayout implements View.OnTouchListener {

    private ViewPager mViewPager;
    private ViewPager.PageTransformer mPageTransformer;

    private int mTouchSlop;
    private int mTapTimeout;

    private int mViewPagerWidth;
    private long mStartDownTime = -1;
    private boolean isClickMode;

    private int gravity = 0;
    private PointF mStartPoint = new PointF();

    public XGallery(@NonNull Context context) {
        super(context);
        init(context, null, 0);
    }

    public XGallery(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public XGallery(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XGallery, defStyleAttr, 0);
        int itemWidth = a.getDimensionPixelOffset(R.styleable.XGallery_xGallery_itemWidth, LayoutParams.MATCH_PARENT);
        int itemHeight = a.getDimensionPixelOffset(R.styleable.XGallery_xGallery_itemHeight, LayoutParams.MATCH_PARENT);
        gravity = a.getInt(R.styleable.XGallery_xGallery_gravity, 0);
        a.recycle();

        mViewPager = new ViewPager(context);
        mViewPager.setClipChildren(false);
        mViewPager.setOverScrollMode(OVER_SCROLL_NEVER);
        mViewPager.setHorizontalScrollBarEnabled(false);
        mViewPager.setOffscreenPageLimit(2);

        setPageTransformer(new BottomScalePageTransformer());

        LayoutParams params = new LayoutParams(itemWidth, itemHeight);
        if (gravity == 0) {
            params.gravity = Gravity.LEFT;
            params.leftMargin = (int) Utils.dip2px(BaseApplication.context, 20);
        } else {
            params.gravity = Gravity.CENTER;
        }
        addView(mViewPager, params);

        setClipChildren(false);
        setOnTouchListener(this);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mTapTimeout = ViewConfiguration.getTapTimeout();
        mViewPagerWidth = itemWidth;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mViewPager.getAdapter() == null || mViewPager.getAdapter().getCount() == 0) {
            return true;
        }

        final float x = event.getX();
        final float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isClickMode = true;
                mStartDownTime = SystemClock.uptimeMillis();
                mStartPoint.set(event.getX(), event.getY());
                break;

            case MotionEvent.ACTION_MOVE:
                if (Math.abs(x - mStartPoint.x) > mTouchSlop || Math.abs(y - mStartPoint.y) > mTouchSlop) {
                    isClickMode = false;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (isClickMode && SystemClock.uptimeMillis() - mStartDownTime <= mTapTimeout) {
                    performClickItem(mStartPoint.x);
                }
                isClickMode = false;
                mStartDownTime = -1;
                break;

            default:
                break;
        }
        return mViewPager.dispatchTouchEvent(event);
    }

    /**
     * 处理Item的点击事件
     */
    private void performClickItem(float x) {
        float centerDistance = x - getWidth() / 2.f;
        boolean isRightSide = centerDistance >= 0;
        int space = (int) (Math.abs(centerDistance) + mViewPagerWidth / 2.f) / mViewPagerWidth;
        int position = mViewPager.getCurrentItem() + (isRightSide ? space : -space);
        if (position < 0 || position >= mViewPager.getAdapter().getCount()) {
            return;
        }

        int currentItem = mViewPager.getCurrentItem();
        if (currentItem == position) {
            // 没有改变
            return;
        }

        mViewPager.setCurrentItem(position, true);
        if (mOnGalleryPageSelectListener != null) {
            mOnGalleryPageSelectListener.onGalleryPageSelected(position);
        }
    }

    /**
     * 设置Item的切换动画。
     *
     * @param transformer 切换动画
     */
    public void setPageTransformer(ViewPager.PageTransformer transformer) {
        mPageTransformer = transformer;
        mViewPager.setPageTransformer(true, transformer);
    }

    /**
     * 设置ViewPager额外显示的最大页数。
     *
     * @param limit 页数
     */
    public void setPageOffscreenLimit(int limit) {
        mViewPager.setOffscreenPageLimit(limit);
    }

    /**
     * 设置Adapter。
     *
     * @param adapter PagerAdapter
     */
    public void setAdapter(PagerAdapter adapter) {
        mViewPager.setAdapter(adapter);
    }

    /**
     * 设置当前选中的Item。
     *
     * @param itemPosition Item的位置
     * @param smoothScroll 是否平缓滚动到指定的位置
     */
    public void setSelection(int itemPosition, boolean smoothScroll) {
        mViewPager.setCurrentItem(itemPosition, smoothScroll);
    }

    /**
     * 获取当前选中的项的下标
     *
     * @return
     */
    public int getCurrentPosition() {
        return mViewPager.getCurrentItem();
    }

    public void setCurrentPosition(int position){
        mViewPager.setCurrentItem(position);
    }

    public void setOffscreenPageLimit(int limit) {
        mViewPager.setOffscreenPageLimit(limit);
    }

    /**
     * 添加页面切换的监听事件。
     *
     * @param listener ViewPager页面切换的监听事件
     */
    public void addOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mViewPager.addOnPageChangeListener(listener);
    }

    private OnGalleryPageSelectListener mOnGalleryPageSelectListener;

    /**
     * 设置选中的监听事件。
     *
     * @param listener 选中的监听事件
     */
    public void setOnGalleryPageSelectListener(OnGalleryPageSelectListener listener) {
        this.mOnGalleryPageSelectListener = listener;
    }

    public interface OnGalleryPageSelectListener {

        void onGalleryPageSelected(int position);
    }

}
