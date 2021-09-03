package com.daqsoft.travelCultureModule.search.view;



import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;


public class MyTextView extends View {

    //字体颜色
    private int MyTextColor = Color.parseColor("#000000");
    //要绘制的文字
    private String MyTextString = "四川美食协会美食";
    //默认文字大小
    private int MyTextSize = 35;
    //画笔
    private Paint mPaint;
    //画笔
    private Paint mPaint2;
    ArrayList<String> content=new ArrayList<>();
    public MyTextView(Context context) {
        this(context, null);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        String key="美食";
        //实例化画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(MyTextColor);
        mPaint.setTextSize(MyTextSize);
        mPaint2 = new Paint();
        mPaint2.setAntiAlias(true);
        mPaint2.setColor(Color.parseColor("#36CD64"));
        mPaint2.setTextSize(MyTextSize);
        String[] result=MyTextString.split(key);
        int length=0;
        for(int i=0;i<result.length;i++){
            length=length+result[i].length()+key.length();
            if(length>MyTextString.length()){
                content.add(result[i]);
            }else{
                content.add(result[i]+key);
            }
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取宽高模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //获取宽高
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.AT_MOST) {
            //测量文字宽度
            Rect rect = new Rect();
            mPaint.getTextBounds(MyTextString, 0, MyTextString.length(), rect);
            widthSize = rect.width() + getPaddingLeft() + getPaddingRight();
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            //测量文字的高度
            Rect rect = new Rect();
            mPaint.getTextBounds(MyTextString, 0, MyTextString.length(), rect);
            heightSize = rect.height() + getPaddingTop() + getPaddingBottom();
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int x = getPaddingLeft();
        //计算文字基线
        Paint.FontMetricsInt metricsInt = mPaint.getFontMetricsInt();
        Log.e("MyTextView-bottom",metricsInt.bottom+"");
        Log.e("MyTextView-top",metricsInt.top+"");
        //需要注意metricsInt.bottom是正值  metricsInt.top是负值
        int dy = (metricsInt.bottom - metricsInt.top) / 2 - metricsInt.bottom;
        int y = getHeight() / 2 + dy;
        String[] value=MyTextString.split("美食");
        for(int i=0;i<content.size();i++){
            canvas.drawText(MyTextString, x, y, mPaint);
        }

    }

    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

}
