package com.daqsoft.thetravelcloudwithculture.home.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.daqsoft.baselib.utils.Utils;

/**
 * @Description 顶部背景曲线可变的的RecyclerView
 * @ClassName TopLineChangeRecyclerView
 * @Author PuHua
 * @Time 2019/12/4 17:46
 * @Version 1.0
 */
public class TopLineChangeRecyclerView extends RecyclerView {

    /**
     * 顶部曲线路径
     */
    Path path;
    /**
     * 顶部曲线画笔
     */

    Paint paint;
    /**
     * 曲线高度，不是实际高度
     */
    private int pathHeight = 0;
    private int mWidth;
    private int mHeight;


    public TopLineChangeRecyclerView(@NonNull Context context) {
        super(context);
    }

    public TopLineChangeRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TopLineChangeRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        int widthMode = MeasureSpec.getMode(widthSpec);
        int widthSize = MeasureSpec.getSize(widthSpec);
        int heightMode = MeasureSpec.getMode(heightSpec);
        int heightSize = MeasureSpec.getSize(heightSpec);

        mWidth = widthSize;
        mHeight = heightSize;

        setMeasuredDimension(mWidth,mHeight);
    }

    private void init(AttributeSet attrs){

        path = new Path();
        paint = new Paint();

        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        pathHeight = (int) Utils.dip2px(getContext(),25);

    }

    @Override
    public void onDraw(Canvas c) {
        path.reset();
        path.moveTo(0,pathHeight);
        path.quadTo(0,0,mWidth/3,0);
        path.quadTo(mWidth/3,0,mWidth*2/3,0);
        path.quadTo(mWidth,0,mWidth,pathHeight);
        path.lineTo(mWidth,mHeight);
        path.lineTo(0,mHeight);
        path.lineTo(0,pathHeight);
        path.close();
        c.drawPath(path,paint);
        super.onDraw(c);



    }
}
