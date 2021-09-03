package com.daqsoft.usermodule.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.daqsoft.usermodule.R;

import java.lang.reflect.Field;

public class CircleProgress extends View {

    private  Context context;
    //进度最大值
    private int maxNum;
    //起始角度
    private int startAngle;
    //扫描过的角度
    private int sweepAngle;
    //进度条宽度
    private int progressWidth;
    //外圈宽度
    private int outCircleWidth;
    //主题色
    private int themeColor;
    //进度条颜色
    private int progressColor;
    //外圈颜色
    private int innerColor;
    //内圈颜色
    private int outColor;
    //内圈半径
    private int innerCircleRadius;
    //外圈半径
    private int outCircleRadius;
    //控件宽高
    private int mWidth,mHeight;
    //当前进度值
    private int currentNum;

    //画内外圈，进度条
    private Paint paint;
    //画文本
    private Paint txtPaint;
    //画刻度
    private Paint scalePaint;

    //刻度
    private int[] scales ={350,500,550,650,1000};
    //刻度文本
    private String[] scaleTxts ={"较差","中等","良好","优秀","极好"};

    private String contentTxt="";
    private RectF innerRectF;
    private RectF progressRectF;
    private RectF outRectF;

    public CircleProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initAttr(attrs);
        initPaint();
    }

    private void initAttr(AttributeSet attrs){
        TypedArray array = context.obtainStyledAttributes(attrs,R.styleable.CircleProgress);
        maxNum  = array.getInt(R.styleable.CircleProgress_progressMax,1000);
        startAngle = array.getInt(R.styleable.CircleProgress_startAngle,160);
        sweepAngle = array.getInt(R.styleable.CircleProgress_sweepAngle,220);
        progressWidth = dp2px(array.getInt(R.styleable.CircleProgress_progressWidth,8));
        outCircleWidth = dp2px(array.getInt(R.styleable.CircleProgress_outCircleWidth,2));
        themeColor = array.getColor(R.styleable.CircleProgress_themeColor,0xff36cd46);
        progressColor = array.getColor(R.styleable.CircleProgress_progressColor,0xffffffff);
        innerColor =array.getColor(R.styleable.CircleProgress_innerCircleColor,0x40ffffff);
        outColor =array.getColor(R.styleable.CircleProgress_outCircleColor,0x40ffffff);
        innerCircleRadius =array.getInt(R.styleable.CircleProgress_innerCircleRadius,0);
        outCircleRadius = array.getInt(R.styleable.CircleProgress_outCircleWidth,0);
        array.recycle();
//        setBackgroundColor(themeColor);
    }

    private void initPaint(){
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(progressColor);
        txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setColor(progressColor);
        scalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scalePaint.setColor(themeColor);
    }

    public void setCurrentNumAnim(int num) {
        float duration = (float)Math.abs(num-currentNum)/maxNum *1500 +500;
        ObjectAnimator anim = ObjectAnimator.ofInt(this,"currentNum",num);
        anim.setDuration((long)Math.min(duration,2000));
        anim.start();
    }

    public void setCurrentNum(int currentNum) {
        this.currentNum = currentNum;
        invalidate();
    }

    public void setScales(int[] scales,String[] scaleTxts) {
        if(scales.length>0)
        this.scales = scales;
        if(scaleTxts.length>0)
            this.scaleTxts = scaleTxts;
        invalidate();
    }


    public void setContentTxt(String contentTxt) {
        this.contentTxt = contentTxt;
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
            mWidth = dp2px(300);
        }

        if(hMode == MeasureSpec.EXACTLY){
            mHeight = hSize;
        }else{
            mHeight = dp2px(200);
        }
        setMeasuredDimension(mWidth,mHeight);
        if(innerCircleRadius == 0 || outCircleRadius == 0){
            innerCircleRadius =  getMeasuredWidth()/4;
            outCircleRadius = innerCircleRadius +dp2px(7);
        }

        innerRectF = new RectF(-innerCircleRadius,-innerCircleRadius,innerCircleRadius,innerCircleRadius);
        outRectF = new RectF(-outCircleRadius,-outCircleRadius,outCircleRadius,outCircleRadius);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(mWidth/2,mHeight/2+mHeight/4);
        drawCircle(canvas);//画内外圈
        drawText(canvas);//画文本
        drawProgress(canvas);//画进度
        drawScale(canvas);//画刻度
    }


    /**
     * 画内外圈
     * @param canvas
     */
    private void drawCircle(Canvas canvas){
        canvas.save();
        //画内圈
        paint.setColor(innerColor);
        paint.setStrokeWidth(progressWidth);
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawArc(innerRectF,startAngle,sweepAngle,false,paint);

        //外圈
        paint.setStrokeWidth(outCircleWidth);
        paint.setColor(outColor);
        canvas.drawArc(outRectF,startAngle,sweepAngle,false,paint);
        canvas.restore();
    }

    /**
     * 画文本
     * @param canvas
     */
    private void drawText(Canvas canvas){
        canvas.save();
        txtPaint.setStyle(Paint.Style.FILL);
        txtPaint.setTextSize(innerCircleRadius/2);
        canvas.drawText(currentNum+"",-txtPaint.measureText(currentNum+"")/2,-40,txtPaint);
        txtPaint.setTextSize(innerCircleRadius/8);
        String content = "信用";
        for(int i=0;i<scales.length;i++){
            if(currentNum<=scales[i]){
                content+=scaleTxts[i];
                break;
            }
        }
        Rect r = new Rect();
        txtPaint.getTextBounds(content,0,content.length(),r);
        canvas.drawText(content,-r.width()/2,r.height()-20,txtPaint);
        txtPaint.setTextSize(innerCircleRadius/10);
        txtPaint.getTextBounds(contentTxt,0,contentTxt.length(),r);
        canvas.drawText(contentTxt,-r.width()/2,r.height()+30,txtPaint);
        canvas.restore();
    }

    /**
     * 画进度
     * @param canvas
     */
    private void drawProgress(Canvas canvas){
        canvas.save();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(progressWidth);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(progressColor);
        int sweep;
        if(currentNum<=maxNum){
            sweep = (int)(currentNum/(float)maxNum * sweepAngle - 1);
        }else{
            sweep = sweepAngle;
        }
        if(sweep>0){
            canvas.drawArc(innerRectF,startAngle,sweep,false,paint);
        }
        canvas.restore();
    }

    /**
     * 画刻度
     * @param canvas
     */
    private void drawScale(Canvas canvas){
        canvas.save();
        canvas.rotate(-270+startAngle);
        float angle = 0f;
        scalePaint.setStrokeWidth(dp2px(2));
        scalePaint.setColor(themeColor);
        for(int i=0;i<scales.length;i++){
            if(i==0){
                angle = ((float) scales[0]/(float) maxNum)*sweepAngle;
            }else{
                angle = ((float) (scales[i]-scales[i-1])/(float)maxNum)*sweepAngle;
            }
            canvas.rotate(angle/2);
            drawScaleText(canvas,scaleTxts[i]+"",paint);
            canvas.rotate(angle/2);
            if(i!=scales.length-1){
                canvas.drawLine(0,-innerCircleRadius-progressWidth/2-dp2px(1),0,-innerCircleRadius+progressWidth/2+dp2px(2),scalePaint);
            }
        }
        canvas.restore();
    }

    /**
     * 画刻度文本
     * @param canvas
     */
    private void drawScaleText(Canvas canvas,String txt,Paint paint){
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(sp2px(8));
        paint.setColor(progressColor);
        float width = paint.measureText(txt);
        canvas.drawText(txt,-width/2,-innerCircleRadius+dp2px(15),paint);
        paint.setStyle(Paint.Style.STROKE);
    }

    private int dp2px(int dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,getResources().getDisplayMetrics());
    }


    private int sp2px(int sp){

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,sp,getResources().getDisplayMetrics());
    }
}
