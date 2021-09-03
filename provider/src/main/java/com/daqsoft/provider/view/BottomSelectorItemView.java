package com.daqsoft.provider.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

/**
 * @Description 底部导航控件
 * @ClassName BottomSelectorItemView
 * @Author PuHua
 * @Time 2019/12/3 14:13
 * @Version 1.0
 */
public class BottomSelectorItemView extends LinearLayout {

    Path backPath;

    Paint backPaint;

    int mWidth;
    int mHeight;

    int centerx,centery;



    public BottomSelectorItemView(Context context) {
        super(context);
    }

    public BottomSelectorItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomSelectorItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    private void init(Context context, AttributeSet attrs){

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        } else {
            mWidth = getPaddingLeft() + 480 + getPaddingRight();
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        } else {
            mHeight = getPaddingTop() + 100 + getPaddingBottom();
        }
        setMeasuredDimension(mWidth,mHeight);
        Log.d("TAG","width: " + mWidth + " h: " + mHeight);

        centerx = mWidth/2;
        centery = mHeight/2;


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
