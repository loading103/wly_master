package com.daqsoft.provider.view.web;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.daqsoft.provider.R;


public class WebViewProgressBar extends View {
    private int progress = 1;
    private final static int HEIGHT = 5;
    private Paint paint;
    private final static int colors[] = new int[]{};

    public WebViewProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WebViewProgressBar(Context context) {
        super(context);
        //        LinearGradient shader = new LinearGradient(
        //                0, 0,
        //                100, HEIGHT,
        //                colors,
        //                null,
        //                Shader.TileMode.MIRROR);
        paint = new Paint(Paint.DITHER_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(HEIGHT);
        paint.setAntiAlias(true);
        paint.setColor(context.getResources().getColor(R.color.colorPrimary));
        //        paint.setShader(shader);
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth() * progress / 100, HEIGHT, paint);
    }
}