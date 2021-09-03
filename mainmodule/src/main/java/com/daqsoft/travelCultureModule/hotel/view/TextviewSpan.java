package com.daqsoft.travelCultureModule.hotel.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;

/**
 * 一串文字分成两段，且底部对齐，且字体大小不一致的控件
 * Created by xxx on 2019/7/30
 */
public class TextviewSpan extends View {

    private int mWidth;//控件宽度

    private int mHeight;//控件高度

    private Paint mLeftPaint;//左边字符串画笔

    private Paint mRightPaint;//右边字符串画笔

    private int mTextColor = Color.BLACK;//文字颜色

    private String mLeftText = "";//左边文字

    private String mRightText = "";//右边文字

    private String text3 = "";

    private int mRightTextSize = 12;//dp

    private int mLeftTextSize = 20;//dp

    public TextviewSpan(Context context) {
        this(context, null);
    }

    public TextviewSpan(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextviewSpan(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mRightPaint = new Paint();
        mRightPaint.setColor(mTextColor);
        mRightPaint.setStyle(Paint.Style.FILL);
        mRightPaint.setAntiAlias(true);
        mRightPaint.setTextSize(dip2px(mRightTextSize));

        mLeftPaint = new Paint();
        mLeftPaint.setColor(mTextColor);
        mLeftPaint.setFakeBoldText(true);
        mLeftPaint.setStyle(Paint.Style.FILL);
        mLeftPaint.setTextSize(dip2px(mLeftTextSize));

        updateSize();
    }

    /** 设置字体大小，单位sp */
    public void setTextSize(int leftTextSize, int rightTextSize) {
        mLeftTextSize = leftTextSize;
        mRightTextSize = rightTextSize;
        mRightPaint.setTextSize(dip2px(mRightTextSize));
        mLeftPaint.setTextSize(dip2px(mLeftTextSize));
        updateSize();
        requestLayout();
    }

    public void setTextColor(@ColorInt int textColor) {
        mTextColor = textColor;
        mLeftPaint.setColor(mTextColor);
        mRightPaint.setColor(mTextColor);
        invalidate();
    }

    public void setText(String leftText, String rightText,String text3) {
        if (leftText == null || rightText == null) {
            return;
        }
        this.mLeftText = leftText;
        this.mRightText = rightText;
        this.text3 = text3;
        updateSize();
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth, mHeight);
        invalidate();
    }

    private void updateSize() {
        float leftTextWidth = getFontlength(mLeftPaint, mLeftText);//返回字符串宽度
        float rightTextWidth = getFontlength(mRightPaint, mRightText);//返回字符串宽度
        mWidth = (int) (leftTextWidth + rightTextWidth);
        float leftTextHeight = getFontHeight(mLeftPaint);
        float rightTextHeight = getFontHeight(mRightPaint);
        mHeight = (int) (leftTextHeight > rightTextHeight ? leftTextHeight : rightTextHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setBackgroundColor(Color.parseColor("#00000000"));
        try {
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));//抗锯齿
            float leftTextWidth = getFontlength(mLeftPaint, mLeftText);
            canvas.drawText(mLeftText, 0, mHeight, mLeftPaint);
            canvas.drawText(mRightText, leftTextWidth, mHeight, mRightPaint);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // 关闭硬件加速，防止异常unsupported operation exception
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    private float getFontlength(Paint paint, String str) {
        return paint.measureText(str);
    }

    /** 返回指定笔的文字高度 */
    private float getFontHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        //return fm.descent - fm.ascent;
        return Math.abs(fm.ascent);//展示的是金额数字，没有descent，不用按照文字高度计算规则算
    }

    /** 返回指定笔离文字顶部的基准距离 */
    private float getFontLeading(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.leading - fm.ascent;
    }

    private int dip2px(float dipValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}

