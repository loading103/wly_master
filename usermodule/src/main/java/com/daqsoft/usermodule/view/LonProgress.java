package com.daqsoft.usermodule.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import com.daqsoft.usermodule.R;

public class LonProgress extends View {


    private static final String TAG = "LonProgress";
    private Context context;
    //进度条宽度
    private int progressWidth;
    //背景条宽度
    private int backgroundWidth;
    //进度条宽度
    private int progressHeight;
    //背景条宽度
    private int backgroundHeight;
    //内层背景颜色
    private int innerBgColor;
    //外层背景颜色
    private int outBgColor;
    //刻度文本颜色
    private int scaleTextColor;
    //当前文本颜色
    private int currentTextColor;
    //进度条最大值
    private int progressMax;
    //当前值
    private int currentNum = 506;

    //刻度值
    private int[] scales ={0,350,500,550,650,1000};
    //刻度文本
    private String[] scaleTexts = {"较差","中等","良好","优秀","极好"};

    private Paint paint;
    private Paint textPaint;

    private int mWidth;
    private int mHeight;
    private int [] progressColors = {0xff46dc85,0xff61e297,0xff89edb3,0xffbdfad7,0xffffffff};

    //进度结束时x坐标
    private float endX=0;

    private String scaleLevel = "良好";

    public LonProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initAttr(attrs);
        initPaint();
    }

    private void initAttr(AttributeSet attrs){
        TypedArray typedValue = context.obtainStyledAttributes(attrs,R.styleable.LonProgress);
        progressWidth = typedValue.getInt(R.styleable.LonProgress_progressWidths,dp2px(0));
        backgroundWidth = typedValue.getInt(R.styleable.LonProgress_backgroundWidth,dp2px(0));
        progressHeight = typedValue.getInt(R.styleable.LonProgress_progressHeight,dp2px(20));
        backgroundHeight = typedValue.getInt(R.styleable.LonProgress_backgroundHeight,dp2px(25));
        innerBgColor = typedValue.getColor(R.styleable.LonProgress_innerBgColor,0x30000000);
        outBgColor = typedValue.getColor(R.styleable.LonProgress_outBgColor,0x20000000);
        scaleTextColor =typedValue.getColor(R.styleable.LonProgress_scaleTextColor,0xffffffff);
        currentTextColor = typedValue.getColor(R.styleable.LonProgress_currentTextColor,0xff36cd46);
        progressMax = typedValue.getInt(R.styleable.LonProgress_progressMaxs,1000);
    }

    private void initPaint(){
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    }

    public void setCurrentNum(int currentNum) {
        this.currentNum = currentNum;
        invalidate();
    }

    public void setScales(int[] scales,String[] scaleTxts) {
        if(scales.length>0)
            this.scales = scales;
        if(scaleTxts.length>0)
            this.scaleTexts = scaleTxts;
        invalidate();
    }
    public void setScaleTextColor(int scaleTextColor) {
        this.scaleTextColor = scaleTextColor;
    }

    public void setScaleLevel(String scaleLevel) {
        this.scaleLevel = scaleLevel;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);

        if(wMode == MeasureSpec.EXACTLY){
            mWidth = wSize;
        }else{
            mWidth = dp2px(600);
        }

        if(hMode == MeasureSpec.EXACTLY){
            mHeight = hSize;
        }else{
            mHeight = dp2px(200);
        }
        setMeasuredDimension(mWidth,mHeight);
        if(progressWidth == 0 || backgroundWidth == 0){
            backgroundWidth = mWidth - getPaddingLeft() - getPaddingRight();
            progressWidth = backgroundWidth - dp2px(2);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(getPaddingLeft(),mHeight/2);//平移中心点
        drawBackground(canvas);//画背景
        drawProgress(canvas);//画进度
        drawScale(canvas);//画刻度
        drawScaleText(canvas);//画刻度文本
        canvas.restore();
    }

    /**
     * 画背景
     * @param canvas
     */
    private void drawBackground(Canvas canvas){
        canvas.save();
        //外圈
        paint.setStrokeWidth(dp2px(30));
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(outBgColor);
        canvas.drawLine(0,0,backgroundWidth,0,paint);
        //内圈
        paint.setStrokeWidth(dp2px(20));
        paint.setColor(innerBgColor);
        canvas.drawLine(dp2px(1),0,progressWidth,0,paint);
        canvas.restore();
    }

    /**
     * 画进度
     * @param canvas
     */
    private void drawProgress(Canvas canvas){
        canvas.save();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        RectF rectF;
        //每个刻度宽度
        float scaleWidth = (float) progressWidth/scaleTexts.length +10;
        float pxToNum = 0f;//每个分值对应的像素
        float halfCircleRadius = dp2px(10); //半圆对应的半径
        int tempNum = currentNum;
        if(currentNum>0){
            rectF = new RectF(-dp2px(10),-dp2px(10),dp2px(10),dp2px(10));
            for(int i= 1;i<scales.length;i++){
                paint.setColor(progressColors[i-1]);
                pxToNum = scaleWidth/(scales[i] - scales[i-1]);
                tempNum =currentNum - scales[i];
                if(i == 1){
                    Log.e(TAG,"================画左边半圆");
                  canvas.drawArc(rectF,90,180,false,paint);
              }
                float left = 0;
                if(i == 1){
                    left = 0;
                } else if(i == 2){
                    left = scaleWidth - halfCircleRadius;
                }else{
                    left = (i-1)* scaleWidth - halfCircleRadius;
                }
                if(currentNum * pxToNum - halfCircleRadius < halfCircleRadius){
                    Log.e(TAG,"================画右边半圆");
                    endX = dp2px(10);
                    canvas.drawArc(rectF,-90,180,false,paint);
                    return;
                }else if(tempNum <= 0){
                    tempNum =currentNum - scales[i -1];
                    //画矩形
                    Log.e(TAG,"================画矩形1");
                    float right = left+tempNum* pxToNum - halfCircleRadius;
                    rectF = new RectF(left,-dp2px(10),right,dp2px(10));
                    Log.e(TAG,"================left:"+left+",right:"+right+",width:"+scaleWidth);
                    Log.e(TAG,"================progress:"+tempNum+",pxToNum:"+pxToNum+",i:"+i);
                    canvas.drawRect(rectF,paint);
                    rectF = new RectF(left+tempNum * pxToNum - 2*halfCircleRadius,-dp2px(10),left+tempNum * pxToNum,dp2px(10));
                    endX = left+tempNum * pxToNum ;
                    Log.e(TAG,"================endx:"+endX);

                    // 画右边半圆
                    canvas.drawArc(rectF,-90,180,false,paint);
                    break;
                }else{
                    //画矩形
                    Log.e(TAG,"================画矩形2");
                    Log.e(TAG,"================progress:"+tempNum+",pxToNum:"+pxToNum+",i:"+scales[i]);
                    Log.e(TAG,"================left:"+left+",right:"+scales[i]* pxToNum);
                    float right = left+scaleWidth;
                    if(i == 1)
                     right = left+scaleWidth - halfCircleRadius;
                    rectF = new RectF(left,-dp2px(10),right,dp2px(10));
                    canvas.drawRect(rectF,paint);
                }
            }
        }

        canvas.restore();
    }

    /**
     * 画刻度
     * @param canvas
     */
    private void drawScale(Canvas canvas){
        canvas.save();
        textPaint.setTextSize(sp2px(12));
        paint.setStyle(Paint.Style.FILL);
        textPaint.setColor(scaleTextColor);
        int count = scaleTexts.length;
        float scaleWidth = (float) progressWidth/(float) count;
        for(int i=0;i<scales.length;i++){
            float width = textPaint.measureText(scales[i]+"");
            if(i == 0){
                canvas.drawText(scales[i]+"",-width-dp2px(10),dp2px(30),textPaint);
            }else if(i == scales.length - 1){
                canvas.drawText(scales[i]+"",i*scaleWidth,dp2px(30),textPaint);
            }else{
                canvas.drawText(scales[i]+"",i*scaleWidth-width,dp2px(30),textPaint);
            }
        }
        paint.setStyle(Paint.Style.STROKE);
        canvas.restore();
    }

    /**
     * 画刻度文本
     * @param canvas
     */
    private void drawScaleText(Canvas canvas){
        canvas.save();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(scaleTextColor);
        int count = scaleTexts.length;
        float scaleWidth = (float) progressWidth/(float) count;
        Rect rect = new Rect();
        float lastRight = 0f;
        for(int i=0;i<scaleTexts.length;i++){
            String scaleText = scaleTexts[i];
            float width = textPaint.measureText(scaleText);
            textPaint.getTextBounds(scaleText,0,scaleText.length(),rect);
            float left = i*scaleWidth+scaleWidth/2 - width;
            if(scaleLevel == scaleText){
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.mine_integrity_text_box);
                canvas.drawBitmap(bitmap,null,new RectF(
                        endX-dp2px(4) ,
                        -dp2px(25)-rect.height()-dp2px(4),
                        endX+width+dp2px(10),
                        -dp2px(25)+rect.height()-dp2px(2)),paint);
                textPaint.setColor(currentTextColor);
                textPaint.setTextSize(sp2px(12));
                canvas.drawText(scaleText+"",endX,-dp2px(25),textPaint);
                lastRight = endX +width+dp2px(10);
            }else{
                textPaint.setColor(scaleTextColor);
                textPaint.setTextSize(sp2px(10));
                float tempLeft = left;
                if(lastRight != 0&& lastRight > left){
                    tempLeft += width/2 + 10;
                }
                canvas.drawText(scaleText+"",tempLeft,-dp2px(25),textPaint);
            }
        }
        paint.setStyle(Paint.Style.STROKE);
        canvas.restore();
    }


    private int dp2px(int dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,getResources().getDisplayMetrics());
    }


    private int sp2px(int sp){

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,sp,getResources().getDisplayMetrics());
    }
}
