package com.daqsoft.thetravelcloudwithculture.home.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.daqsoft.baselib.utils.Utils;
import com.daqsoft.thetravelcloudwithculture.R;
import com.trello.rxlifecycle3.android.ActivityEvent;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @Description NestedScrollView
 * @ClassName CustomView
 * @Author PuHua
 * @Time 2019/12/4 17:05
 * @Version 1.0
 */

public class TopLineChangeScrollView extends NestedScrollView {

    public int RADUCEHEIGHT;

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

    /**
     * 上拉提示符号
     */
    Path upPath1, upPath2;
    /**
     * 上拉提示画笔
     */
    Paint upPaint1, upPaint2;
    private int pathHeight = 0;
    private int mWidth;
    private int mHeight;
    private Context mContext;

    private Canvas mCavas;
    /**
     * 控制上面的箭头动画，是显示上面两个还是显示下边两个
     */
    boolean up = true;

    /**
     * 判断当前是在上拉还是下拉
     */
    boolean isUp = false;
    /**
     * 是否隐藏箭头
     */
    boolean hideArrow = false;
    /**
     * 子控件的距上边距
     */
    int childTopMargin ;

    public TopLineChangeScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public TopLineChangeScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    // 初始化
    private void init() {
        path = new Path();
        paint = new Paint();

        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        RADUCEHEIGHT = pathHeight = (int) Utils.dip2px(getContext(), 30);

        upPath1 = new Path();
        upPaint2 = new Paint();
        upPaint1 = new Paint();
        upPath2 = new Path();

        upPaint1.setAntiAlias(true);
        upPaint1.setStyle(Paint.Style.FILL);
        upPaint1.setColor(mContext.getResources().getColor(R.color.txt_gray));


        //定时画顶部
        Disposable d = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        int tempColor = upPaint1.getColor();
                        upPaint1.setColor(upPaint2.getColor());

                        upPaint2.setColor(tempColor);

                        up = !up;

                        TopLineChangeScrollView.this.postInvalidate();

                    }
                });

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

        setMeasuredDimension(mWidth, mHeight);


    }

    @Override
    public void onDraw(Canvas c) {


        mCavas = c;
        // 画顶部
        path.reset();
        path.moveTo(0, pathHeight);
        path.lineTo(0, pathHeight);
        path.quadTo(0, 0, mWidth / 3, 0);
        path.lineTo(mWidth * 2 / 3, 0);
        path.quadTo(mWidth, 0, mWidth, pathHeight);
        path.lineTo(mWidth, mHeight);
        path.lineTo(0, mHeight);
        path.lineTo(0, pathHeight);
        path.close();
        c.drawPath(path, paint);
        drawArrow();
        super.onDraw(c);
    }

    /**
     * 画箭头
     */
    public void drawArrow() {
        if (mCavas != null) {
            upPath1.reset();
            upPath1.moveTo(mWidth / 2 - Utils.dip2px(mContext, 8), pathHeight - Utils.dip2px(mContext, 1));

            upPath1.lineTo(mWidth / 2, pathHeight - Utils.dip2px(mContext, 10));

            upPath1.lineTo(mWidth / 2 + Utils.dip2px(mContext, 8), pathHeight - Utils.dip2px(mContext, 1));

            upPath1.quadTo(mWidth / 2 + Utils.dip2px(mContext, 8f), pathHeight + Utils.dip2px(mContext, 1f),
                    mWidth / 2 + Utils.dip2px(mContext, 6f), pathHeight);

            upPath1.lineTo(mWidth / 2, pathHeight - Utils.dip2px(mContext, 6));
            upPath1.lineTo(mWidth / 2 - Utils.dip2px(mContext, 6), pathHeight);
            upPath1.quadTo(mWidth / 2 - Utils.dip2px(mContext, 8f), pathHeight + Utils.dip2px(mContext, 1f),
                    mWidth / 2 - Utils.dip2px(mContext, 8f), pathHeight - Utils.dip2px(mContext, 1));

            View view = getChildAt(0);
            NestedScrollView.LayoutParams params = (LayoutParams) view.getLayoutParams();
            if (!hideArrow){

//                params.topMargin = childTopMargin;
                upPath1.offset(0, Utils.dip2px(mContext, 5f));
                if (!up) {
                    upPaint1.setColor(mContext.getResources().getColor(R.color.txt_gray));
                    mCavas.drawPath(upPath1, upPaint1);
                }
                upPath1.offset(0, Utils.dip2px(mContext, -5f));
                mCavas.drawPath(upPath1, upPaint1);
                if (up) {
                    upPaint1.setColor(mContext.getResources().getColor(R.color.d4));
                    upPath1.offset(0, Utils.dip2px(mContext, -6f));
                    mCavas.drawPath(upPath1, upPaint1);
                }
            }else{

//                params.topMargin = (int) Utils.dip2px(mContext, 10f);
            }
//            view.setLayoutParams(params);
        }

    }

    /**
     * 控件在屏幕上的位置
     */
    float posH;
    /**
     * 背景变化完成的最后位置
     */
    public float endH;

    public float startH = -1;

    /**
     * 改变背景
     */
    public void change() {
        if (posH < getY()) {
            isUp = false;
        } else {
            isUp = true;
        }
        posH = getY();

        if (startH == -1) {
            startH = posH;
            View view = getChildAt(0);
            NestedScrollView.LayoutParams params = (LayoutParams) view.getLayoutParams();
            childTopMargin = params.topMargin;
            Log.d("TAG","childTopMargin="+childTopMargin);
        }
        if (isUp) {
            if (startH - posH >= endH) {
                // 变平
                pathHeight = 0;
                hideArrow = true;
            }
        } else {
            if (startH - posH < endH) {
                // 形状圆弧
                pathHeight = RADUCEHEIGHT;
                hideArrow = false;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);

    }
}