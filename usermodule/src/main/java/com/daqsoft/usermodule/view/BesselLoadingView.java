package com.daqsoft.usermodule.view;

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

import com.daqsoft.usermodule.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @Description 该控件未进度展示控件，实现了简单的动画效果，
 * 使用请参考模板
 * @ClassName BesselProcessView
 * @Author PuHua
 * @Time 2019/11/9 11:43
 */

public class BesselLoadingView extends View {
    private static final String TAG = "BesselLoadingView";
    private static final boolean ISDEBUG = false;
    private static final int DEFAULT_RADIUS = 80;
    private static final int DEFAULT_DURATION = 1500;
    private static final int DEFAULT_COLOR = 0xffffff;
    //定圆半径
    private float mRadius;
    //动圆半径
    private float mRadiusFloat;
    //颜色
    private int mLoadingColor;
    //动画时间
    private int mDuration;
    //三个定圆的X圆心
    private int[] mCirclesX;
    //三个定圆的Y圆心
    private int mCirClesY;
    //动圆的X圆心
    private float mFloatX;
    //定圆和动圆圆心相距多少时开始贝塞尔曲线
    private int mMinDistance;

    // 定圆圆环的paint和path
    private Paint mPaint;
    private Path mPath;

    private Paint mDefaultPaint;
    private Path mDefaullPath;

    private Path linePath;

    private Paint textPaint;

    private Paint baseLinePaint;

    private Path baseLinePath;
    /**
     * 三角路径
     */
    private Path tranglePath;

    private ArrayList<String> strings = new ArrayList<>();

    public ArrayList<String> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }

    private ArrayList<String> labels = new ArrayList<>();

    public int currentStep = 0;

    public BesselLoadingView(Context context) {
        this(context, null);
    }

    public BesselLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BesselLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI(context, attrs);
    }

    private void initUI(Context context, AttributeSet attrs) {

        baseLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        baseLinePath = new Path();

        baseLinePaint.setPathEffect(new DashPathEffect(new float[]{5, 4}, 0));
        baseLinePaint.setColor(Color.WHITE);

        baseLinePaint.setStyle(Paint.Style.STROKE);

        // 默认背景的
        mDefaultPaint = new Paint();
        mDefaultPaint.setStyle(Paint.Style.STROKE);
        mDefaullPath = new Path();
        mCirclesX = new int[3];
        mDefaultPaint.setColor(Color.WHITE);
        mDefaultPaint.setAntiAlias(true);

        //路径
        mPaint = new Paint();
        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.STROKE);
        tranglePath = new Path();
        mPath = new Path();
        linePath = new Path();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BesselLoadingView);
        mLoadingColor = ta.getColor(R.styleable.BesselLoadingView_loadingcolor, DEFAULT_COLOR);
        mRadius = ta.getDimension(R.styleable.BesselLoadingView_loadingradius, DEFAULT_RADIUS);
        mDuration = ta.getInt(R.styleable.BesselLoadingView_loadingduration, DEFAULT_DURATION);
        currentStep = ta.getInt(R.styleable.BesselLoadingView_current_step, 0);
        String labelId = ta.getString(R.styleable.BesselLoadingView_labels);
        if (labelId != null) {

            String[] ss = labelId.split(",");
            labels.addAll(Arrays.asList(ss));
        }
        mPaint.setColor(mLoadingColor);
        //抗锯齿
        mPaint.setAntiAlias(true);

        mRadiusFloat = mRadius * 0.1f;
        strings.add("01");
        strings.add("02");
        strings.add("03");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mWidth;
        int mHeight;

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

        setMeasuredDimension(mWidth, mHeight);
        log("width: " + mWidth + " h: " + mHeight);
        //计算x方向三个圆心
        for (int i = 0; i < 3; i++) {
            mCirclesX[i] = mWidth * (i * 2 + 1) / 6;
        }

        //计算三个圆心Y坐标
        mCirClesY = mHeight / 2;
        //三个初始圆的半径
        mRadius = mHeight / 3;
        mRadiusFloat = mRadius * 0.1f;
        log("mCirclesX: " + mCirclesX[0] + "," + mCirclesX[1] + "," + mCirclesX[2] + "  Y: " + mCirClesY);

        if (mRadius >= mHeight / 6) {
            log("圆的半径大于间隙了,自动缩小");
            mRadius = mHeight / 6;
            mRadiusFloat = mRadius * 0.1f;
        }
        baseLinePaint.setStrokeWidth(mRadius/10);
        mDefaultPaint.setStrokeWidth(mRadius / 10);
        mMinDistance = (int) (mRadius/10);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(mCirclesX[0] - mRadius, (float) (mCirclesX[currentStep] + mRadius * 0.9));
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(mDuration);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                mFloatX = (float) animation.getAnimatedValue();
                postInvalidate();


            }
        });
        valueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBesselLine(canvas);
    }


    /**
     * 绘制进度
     *
     * @param canvas
     */
    @SuppressLint("ResourceAsColor")
    private void drawBesselLine(Canvas canvas) {
        // 画三角
        float dis = mFloatX-(mCirclesX[currentStep] +mRadius);

        if (dis>=0&&dis<=mMinDistance){
            tranglePath.moveTo((float) (mCirclesX[currentStep]+Math.sqrt(Math.pow(mRadius,2)-Math.pow(mFloatX/2,2))),mCirClesY-mFloatX/2);
            tranglePath.lineTo(mCirclesX[currentStep]+mRadius+mFloatX,mCirClesY-mFloatX/2);
            tranglePath.lineTo((float) (mCirclesX[currentStep]+Math.sqrt(Math.pow(mRadius,2)-Math.pow(mFloatX/2,2))),mCirClesY+mFloatX/2);
            tranglePath.close();
            canvas.drawPath(tranglePath,mDefaultPaint);
        }

        // 画虚线
        baseLinePath.moveTo(mCirclesX[0] + mRadius, mCirClesY);
        baseLinePath.lineTo(mCirclesX[1] - mRadius, mCirClesY);
        baseLinePath.moveTo(mCirclesX[1] + mRadius, mCirClesY);
        baseLinePath.lineTo(mCirclesX[2] - mRadius, mCirClesY);
        canvas.drawPath(baseLinePath, baseLinePaint);
        // 画小圆圈（根据当前进度）
        canvas.drawCircle(mFloatX, mCirClesY, mRadiusFloat, mPaint);
        // 画实线（根据当前进度）
        linePath.moveTo(mCirclesX[0] - mRadius, mCirClesY);
        linePath.lineTo(mFloatX, mCirClesY);
        canvas.drawPath(linePath, mDefaultPaint);
        // 根据当前进度画背景和里面的文字
        for (int i = 0; i < 3; i++) {
            if (i > currentStep) {
                // 当前是未到的进度，默认样式
                canvas.drawCircle(mCirclesX[i], mCirClesY, mRadius, mDefaultPaint);
                textPaint.setTextAlign(Paint.Align.CENTER);
                textPaint.setTextSize(48);
                textPaint.setColor(Color.WHITE);
                canvas.drawText(strings.get(i), mCirclesX[i], mCirClesY + 12, textPaint);
            } else {
                // 以下相当于实现了个简单的进度动画效果，展示当前进度
                if (mFloatX >= mCirclesX[i] - mRadius) {
                    // 当小圆圈画到当前进度是再更新当前进度
                    canvas.drawCircle(mCirclesX[i], mCirClesY, mRadius, mPaint);
                    textPaint.setTextAlign(Paint.Align.CENTER);
                    textPaint.setTextSize(48);
                    textPaint.setColor(R.color.colorPrimary);
                    canvas.drawText(strings.get(i), mCirclesX[i], mCirClesY + 12, textPaint);
                } else {
                    // 若小圆圈还未画到当前进度时候还是默认背景
                    canvas.drawCircle(mCirclesX[i], mCirClesY, mRadius, mDefaultPaint);
                    textPaint.setTextAlign(Paint.Align.CENTER);
                    textPaint.setTextSize(48);
                    textPaint.setColor(Color.WHITE);
                    canvas.drawText(strings.get(i), mCirclesX[i], mCirClesY + 12, textPaint);
                }
            }
            // 画进度下边的文字
            if (labels.size() > 0) {
                textPaint.setTextAlign(Paint.Align.CENTER);
                textPaint.setTextSize(48);
                textPaint.setColor(Color.WHITE);
                canvas.drawText(labels.get(i), mCirclesX[i], mCirClesY + mRadius * 2, textPaint);
            }
        }


    }


    public void setSteps(int step){
        if (step>currentStep){
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(mCirclesX[currentStep] - mRadius, (float) (mCirclesX[step] + mRadius * 0.9));
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.setDuration(mDuration);
            valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
//                if (mFloatX<=mCirclesX[currentStep]){
                    mFloatX = (float) animation.getAnimatedValue();
                    postInvalidate();
//                }
                }
            });
            valueAnimator.start();
            currentStep = step;
        }

    }

    private void log(String string) {
        if (ISDEBUG) {
            Log.d(TAG, string);
        }
    }
}
