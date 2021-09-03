package com.daqsoft.servicemodule.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.daqsoft.android.scenic.servicemodule.R;

import static com.dueeeke.videoplayer.util.PlayerUtils.dp2px;

/**
 * desc :自定义竖直方向圆点
 * @author 江云仙
 * @date 2020/4/14 9:14
 */
public class LoadingPointView extends View {
    //灰色圆点
    private Paint mGrayPaint;
    //半径
    private int mRadius;
    public LoadingPointView(Context context) {
        this(context, null);
    }

    public LoadingPointView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingPointView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initParmas(context);
    }

    private void initParmas(Context context) {
        mGrayPaint = new Paint();
        mGrayPaint.setAntiAlias(true);
        mGrayPaint.setStyle(Paint.Style.FILL);
        mGrayPaint.setColor(ContextCompat.getColor(context, R.color.color_d1d1d1));
        mRadius = dp2px(context, 3);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < 6; i++) {
            //修改圆心x轴坐标，来画出多个圆点
            canvas.drawCircle(getWidth() / 2,getWidth() / 2 + (mRadius * i * 4)+ 6 * i, mRadius, mGrayPaint);
        }
    }
}
