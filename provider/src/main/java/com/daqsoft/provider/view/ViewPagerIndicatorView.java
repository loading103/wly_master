package com.daqsoft.provider.view;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.viewpager.widget.ViewPager;

import com.daqsoft.baselib.utils.Utils;
import com.daqsoft.provider.R;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @Description 简单的viewPager指示器
 * 使用请参考模板
 * @ClassName ViewPagerIndicatorView
 * @Author luoyi
 * @Time 2020/4/2 21:04
 * @version 1.2
 */

public class ViewPagerIndicatorView extends View {
    private static final String TAG = "ViewPagerIndicatorView";
    private static final boolean ISDEBUG = false;
    private static final int DEFAULT_RADIUS = 10;
    private static final int DEFAULT_DURATION = 1000;
    private static final int DEFAULT_COLOR = Color.GREEN;
    // 定圆半径
    private float mRadius;
    // 颜色
    private int mLoadingColor;
    // 动画时间
    private int mDuration;
    //n个定圆的X圆心
    private int[] mCirclesX;
    private int mBoderWidth;
    // 三个定圆的Y圆心
    private int mCirClesY;
    // 动圆的X圆心
    private float mFloatX;

    // 定圆圆环的paint和path
    private Paint mPaint;

    /**
     * 背景画笔
     */
    private Paint backPaint;

    public int currentStep = 0;
    /**
     * 指示器总数
     */
    private int total;
    /**
     * 间隔
     */
    private float dis;
    /**
     * 将要跳转
     */
    private int stepTo = 1;

    private Context mContext;

    /**
     * 是否圆形
     */
    private Boolean  circleFlag = false;


    public ViewPagerIndicatorView(Context context) {
        this(context, null);
        mContext = context;
    }

    public ViewPagerIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
    }

    public ViewPagerIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initUI(context, attrs);
    }

    private void initUI(Context context, AttributeSet attrs) {

        mPaint = new Paint();
        backPaint = new Paint();

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicatorView);
        total = ta.getInt(R.styleable.ViewPagerIndicatorView_total, 1);
        mLoadingColor = ta.getColor(R.styleable.ViewPagerIndicatorView_loadingcolor, DEFAULT_COLOR);
        mRadius = ta.getDimension(R.styleable.ViewPagerIndicatorView_loadingradius, DEFAULT_RADIUS);
        mDuration = ta.getInt(R.styleable.ViewPagerIndicatorView_loadingduration, DEFAULT_DURATION);
        currentStep = ta.getInt(R.styleable.ViewPagerIndicatorView_current_step, 0);
        circleFlag = ta.getBoolean(R.styleable.ViewPagerIndicatorView_circle_flag, false);


        mPaint.setColor(mLoadingColor);
        //抗锯齿
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        backPaint.setColor(context.getResources().getColor(R.color.c2bf));
        mRadius = Utils.dip2px(mContext, 3);
        dis = Utils.dip2px(mContext, 6);
        mBoderWidth = (int) (mRadius * 4);
        ta.recycle();
    }

    int mWidth;
    int mHeight;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        }
    }

    private void init() {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawProgress(canvas);
    }


    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
        init();
        postInvalidate();
    }


    /**
     * 绘制进度
     *
     * @param canvas 画布
     */
    private void drawProgress(Canvas canvas) {
        mCirclesX = new int[total];
        mWidth = getWidth();
        mHeight = getHeight();
        mCirClesY = mHeight / 2;
        float start = (mWidth - (total - 1) * dis - (total - 1) * mRadius * 2 - mBoderWidth) / 2;

        if (circleFlag){
            for (int i = 0; i < total; i++) {
                mCirclesX[i] = (int) (start + i * dis + 2 * i * mRadius);
            }
        }else {
            for (int i = 0; i < total; i++) {
                if (i < currentStep) {
                    mCirclesX[i] = (int) (start + i * dis + (2 * i + 1) * mRadius);
                } else if (i == currentStep) {
                    mCirclesX[i] = (int) (start + i * dis + 2 * i * mRadius);
                } else {
                    mCirclesX[i] = (int) (start + i * dis + 2 * i * mRadius + mBoderWidth);
                }
            }
        }
        for (int i = 0; i < total; i++) {
            if (i == currentStep) {
                if (circleFlag){
                    canvas.drawCircle(mCirclesX[i], mCirClesY, mRadius, mPaint);
                }else {
                    canvas.drawRoundRect(mCirclesX[i], mCirClesY - mRadius, mCirclesX[i] + mBoderWidth, mCirClesY + mRadius, 10, 10, mPaint);
                }
            } else {
                canvas.drawCircle(mCirclesX[i], mCirClesY, mRadius, backPaint);
            }
        }

    }

    /**
     * 设置当前选中像
     *
     * @param step
     */
    public void setSteps(final int step) {
        currentStep = step;
        postInvalidate();
    }

    private void log(String string) {
        if (ISDEBUG) {
            Log.d(TAG, string);
        }
    }

    public void binViewPager(@Nullable ViewPager viewPager) {
        if (viewPager != null) {
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    setSteps(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }
}
