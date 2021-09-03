package com.daqsoft.provider.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;

import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.daqsoft.provider.R;


/**
 * @Description 底部控制选择器item
 * @ClassName NavigationItemView
 * @Author PuHua
 * @Time 2019/12/3 15:43
 * @Version 1.0
 */
public class NavigationItemView extends View {
    /**
     * 背景线条布局
     */
    Path backLinePath;
    /**
     * 背景颜色路径
     */
    Path backgroundPath;
    /**
     * 背景线条画笔
     */
    Paint backLinePaint;
    /**
     * 背景颜色画笔
     */
    Paint backgroundPaint;

    int mWidth;
    int mHeight;
    /**
     * 图片的中心点坐标
     */
    int centerX;
    int centerY;

    int topDis;

    int bigImageR;

    float backR;

    int baseLineH;

    int maxR;


    int originImageR;

    private Context mContext;

    int strokeW;

    public boolean isSelect() {
        return isSelect;
    }

    /**
     * 顶部凸起  默认不突起
     */
    boolean raised = false;
    /**
     * 设置是否需要凸起
     * @param raised
     */
    public void setRaised(boolean raised){
        this.raised = raised;
    }

    public void setSelect(boolean select) {

        if (raised && select) {
            backR = (float) bigImageR + topDis;
            topPointH = (int) ((float) bigImageR - originImageR);
        } else {
            backR = (float) originImageR + topDis;
            topPointH = 0;
        }
        isSelect = select;
    }

    /**
     * 是否选中
     */
    private boolean isSelect = true;
    /**
     * 顶部控制点
     */
    private int topPointH = 0;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * 底部的文字
     */
    private String text = "";
    /**
     * 文本的画笔
     */
    private Paint textPaint;
    /**
     * 两种不同状态的图片的id
     */
    private Bitmap bigImage, smallImage;
    /**
     * 大小图的地址
     */
    private String bigImageUrl, smallImageUrl;


    public NavigationItemView(Context context) {
        super(context);
        mContext = context;
        initView(null);
    }

    public NavigationItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        initView(attrs);
    }

    public NavigationItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView(attrs);
    }

    public void setBigImageUrl(String bigImageUrl) {
        getBigBitmapByUrl(bigImageUrl);
        this.bigImageUrl = bigImageUrl;
    }

    public void setSmallImageUrl(String smallImageUrl) {
        getSmallBitmapByUrl(smallImageUrl);
        this.smallImageUrl = smallImageUrl;
    }

    void initView(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.NavigationItemView);
            isSelect = ta.getBoolean(R.styleable.NavigationItemView_select, false);
            text = ta.getString(R.styleable.NavigationItemView_text);
            setSelected(isSelect);
        }


//        bigImage = ta.getDrawable(R.styleable.NavigationItemView_bigImage);
//        smallImage = ta.getDrawable(R.styleable.NavigationItemView_smallImage);

//        getBigBitmapByUrl(bigImageUrl);
//        getSmallBitmapByUrl(smallImageUrl);


        mHeight = dip2px(mContext, 64);

        originImageR = dip2px(mContext, 10);

        bigImageR = dip2px(mContext, 18);

        topDis = dip2px(mContext, 6);

        baseLineH = dip2px(mContext, 12);

        maxR = bigImageR + topDis;

        backR = originImageR + topDis;
        topPointH = baseLineH;

        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.parseColor("#000000"));
        textPaint.setTextSize(dip2px(mContext, 13));
        textPaint.setAntiAlias(true);

        backLinePaint = new Paint();
        strokeW = dip2px(mContext, (int) 0.5f);
        backLinePaint.setStyle(Paint.Style.STROKE);
        backLinePaint.setColor(Color.parseColor("#e2e2e2"));
        backLinePaint.setStrokeWidth(strokeW);

        backLinePath = new Path();
        backgroundPath = new Path();
        backgroundPaint = new Paint();
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(Color.parseColor("#ffffff"));
        backLinePaint.setAntiAlias(true);

        // 默认为选中
        if (raised && isSelect) {
            backR = (float) bigImageR + topDis;
            topPointH = (int) ((float) bigImageR - originImageR);
        } else {
            backR = (float) originImageR + topDis;
            topPointH = 0;
        }

    }

    /**
     * drawable转bitmap
     *
     * @param context
     * @param drawable
     * @return
     */
    public Bitmap getBitmapFormDrawable(Context context, Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE
                        ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        //设置绘画的边界，此处表示完整绘制
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {;
            mWidth = widthSize;
        } else {
            mWidth = getPaddingLeft() + 480 + getPaddingRight();
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        } else {
            mHeight = getPaddingTop() + dip2px(mContext, 64) + getPaddingBottom();
        }

        mHeight = getPaddingTop() + dip2px(mContext, 64) + getPaddingBottom();

        setMeasuredDimension(mWidth, mHeight);
        Log.d("TAG", "width: " + mWidth + " h: " + mHeight);
        Log.d("TAG", "baseLineH: " + baseLineH + " h: " + mHeight);
        centerX = mWidth / 2;
        centerY = mHeight - dip2px(mContext, 20) - bigImageR;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.d("TAG", "backR=" + backR);

        float smallerCR = (backR / 2);
        float start = (float) (smallerCR / Math.sqrt(3));
        // 小弧度的高点位置（y）
        float top = baseLineH - baseLineH / 8;

        // 起始控制点
        float startX = centerX - backR - start * 2;
        // 左边控制点
        float controlLeft = centerX - backR + start;
        // 右边控制点
        float controlRight = centerX + backR - start;
        // 终止控制点
        float endX = centerX + backR + start * 2;
        // 大背景控制点（y）
        float bigTopControlY = 0 - topPointH * 1.5f;

        backLinePath.moveTo(0, baseLineH);
        backgroundPath.moveTo(0, baseLineH);

        backLinePath.lineTo(startX, baseLineH);
        backgroundPath.lineTo(startX, baseLineH);

        if (backR == originImageR + topDis) {
            backLinePath.lineTo(mWidth, baseLineH);
            backgroundPath.lineTo(mWidth, baseLineH);
        } else {
            // 画线条
            backLinePath.cubicTo(startX, baseLineH, controlLeft, baseLineH, controlLeft, top);
            backLinePath.cubicTo(controlLeft, top, centerX, bigTopControlY, controlRight, top);
            backLinePath.cubicTo(controlRight, top, controlRight, baseLineH, endX, baseLineH);
            backLinePath.lineTo(mWidth, baseLineH);
            // 画背景颜色
            backgroundPath.cubicTo(startX, baseLineH, controlLeft, baseLineH, controlLeft, top);
            backgroundPath.cubicTo(controlLeft, top, centerX, bigTopControlY, controlRight, top);
            backgroundPath.cubicTo(controlRight, top, controlRight, baseLineH, endX, baseLineH);
            backgroundPath.lineTo(mWidth, baseLineH);
        }

        backgroundPath.lineTo(mWidth, mHeight);
        backgroundPath.lineTo(0, mHeight);
        backgroundPath.lineTo(0, baseLineH);

        canvas.drawPath(backgroundPath, backgroundPaint);

        canvas.drawPath(backLinePath, backLinePaint);

        drawText(canvas);

        drawImage(canvas);
        backLinePath.reset();
        backgroundPath.reset();
    }

    private void drawText(Canvas canvas) {
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        canvas.drawText(text, centerX - bounds.width() / 2, mHeight - dip2px(mContext, 6), textPaint);
    }

    private void drawImage(Canvas canvas) {
        float r = backR - topDis;


        if (isSelect) {
            if (bigImage != null) {
                Bitmap bitmap = bigImage/*getBitmapFormDrawable(mContext, bigImage)*/;
                int w = (int) (2 * r);
                Bitmap newBitmap = resetBitmapWH(bitmap, w, w);
                canvas.drawBitmap(newBitmap, centerX - r, centerY - r, textPaint);
//                bitmap.recycle();
                setSelected(true);
            }
        } else {
            Bitmap bitmap = null;
            setSelected(false);

            if (backR == originImageR + topDis) {
                if (smallImage != null) {
                    bitmap = smallImage/*getBitmapFormDrawable(mContext, smallImage)*/;
                    canvas.drawBitmap(bitmap, centerX - r, centerY - r, textPaint);
                }

            } else {
                if (bigImage != null) {
                    bitmap = bigImage;
                    int w = (int) (2 * r);
                    Bitmap newBitmap = resetBitmapWH(bitmap, w, w);
                    canvas.drawBitmap(newBitmap, centerX - r, centerY - r, textPaint);
//                    bitmap.recycle();
                }

            }
        }

    }

    /**
     * 获取大图
     *
     * @param url
     */
    public void getBigBitmapByUrl(String url) {

        Glide.with(this)
                .asBitmap()
                .load(url)
//                .centerCrop()
                .override(dip2px(getContext(), 20), dip2px(getContext(), 20))
                .into(new CustomTarget<Bitmap>() {

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        bigImage = resource;
                        invalidate();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    /**
     * 获取大图
     *
     * @param url
     */
    public void getSmallBitmapByUrl(String url) {

        Glide.with(this)
                .asBitmap()
                .load(url)
//                .centerCrop()
                .override(dip2px(getContext(), 20), dip2px(getContext(), 20))
                .into(new CustomTarget<Bitmap>() {

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        smallImage = resource;
                        invalidate();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    /**
     * 调用方法
     *
     * @param select
     */
    public void select(boolean select) {

        isSelect = select;
        ValueAnimator valueAnimator;
        if (isSelect) {
            valueAnimator = ValueAnimator.ofFloat(originImageR, (float) bigImageR);
        } else {
            valueAnimator = ValueAnimator.ofFloat(bigImageR, (float) originImageR);

        }
        valueAnimator.setInterpolator(new AnticipateOvershootInterpolator());
        valueAnimator.setDuration(500);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                if (!raised) {
                    backR = (float) originImageR + topDis;
                    topPointH = 0;
                } else {
                    backR = (float) animation.getAnimatedValue() + topDis;
                    topPointH = (int) ((float) animation.getAnimatedValue() - originImageR);
                }

                postInvalidate();

            }
        });
        valueAnimator.start();
    }


    /**
     * 重置图片的大小
     *
     * @param bitMap
     * @param newWidth
     * @param newHeight
     * @return
     */
    private Bitmap resetBitmapWH(Bitmap bitMap, int newWidth, int newHeight) {
        int width = bitMap.getWidth();
        int height = bitMap.getHeight();

        if (newWidth == 0 && newHeight == 0) {
            return bitMap;
        }
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();

        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        bitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height, matrix, true);
        return bitMap;
    }

    /**
     * @param context
     * @param dpValue
     * @return
     */
    public int dip2px(Context context, int dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
