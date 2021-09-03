package com.daqsoft.baselib.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import daqsoft.com.baselib.R;


/**
 * @Description 圆角imageView
 * @ClassName ArcImageView
 * @Author luoyi
 * @Time 2020/4/2 19:04
 * @Version 1.1
 */
public class ArcImageView extends AppCompatImageView {

    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLORDRAWABLE_DIMENSION = 2;
    /**
     * 矩形
     */
    public static int SHAPE_REC = 1;
    /**
     * 圆形
     */
    public static int SHAPE_CIRCLE = 2;
    /**
     * 椭圆
     */
    public static int SHAPE_OVAL = 3;
    private final Matrix mShaderMatrix = new Matrix();
    /**
     * 边框大小,默认为0，即无边框
     */
    private float mBorderSize = 0;
    /**
     * 边框颜色，默认为白色
     */
    private int mBorderColor = Color.WHITE;
    /**
     * 形状，默认为直接矩形
     */
    private int mShape = SHAPE_REC;
    /**
     * 矩形的圆角半径,默认为0，即直角矩形
     */
    private float mRoundRadius = 0;
    private float mRoundRadiusLeftTop, mRoundRadiusLeftBottom, mRoundRadiusRightTop, mRoundRadiusRightBottom;
    private Paint mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * ImageView的矩形区域
     */
    private RectF mViewRect = new RectF();
    /**
     * 边框的矩形区域
     */
    private RectF mBorderRect = new RectF();
    private Paint mBitmapPaint = new Paint();
    private BitmapShader mBitmapShader;
    private Bitmap mBitmap;
    private Path mPath = new Path();

    public ArcImageView(Context context) {
        this(context, null);
    }

    public ArcImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mBorderSize);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setAntiAlias(true);
        mBitmapPaint.setAntiAlias(true);
        if (mShape == SHAPE_CIRCLE) {
            // 固定为CENTER_CROP，其他不生效
            super.setScaleType(ScaleType.CENTER_CROP);
        }


    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setupBitmapShader();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mBitmap = getBitmapFromDrawable(drawable);
        setupBitmapShader();
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType != ScaleType.CENTER_CROP) {
            //throw new IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType));
        }
    }

    private void init(AttributeSet attrs) {


        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ArcImageView, 0, 0);
        for (int i = 0; i < ta.getIndexCount(); i++) {
            int attr = ta.getIndex(i);
            if (attr == R.styleable.ArcImageView_is_cover_src) {
//                isCoverSrc = ta.getBoolean(attr, isCoverSrc);
            } else if (attr == R.styleable.ArcImageView_is_circle) {
                Boolean isCircle = ta.getBoolean(attr, false);
                if (isCircle) {
                    mShape = SHAPE_CIRCLE;
                }
            } else if (attr == R.styleable.ArcImageView_border_width) {
                mBorderSize = ta.getDimensionPixelSize(attr, (int) mBorderSize);
            } else if (attr == R.styleable.ArcImageView_border_color) {
                mBorderColor = ta.getColor(attr, mBorderColor);
            } else if (attr == R.styleable.ArcImageView_inner_border_width) {
                mBorderSize = ta.getDimensionPixelSize(attr, (int) mBorderSize);
            } else if (attr == R.styleable.ArcImageView_inner_border_color) {
                mBorderColor = ta.getColor(attr,  mBorderColor);
            } else if (attr == R.styleable.ArcImageView_corner_radius) {
                mRoundRadius = ta.getDimensionPixelSize(attr, (int) mRoundRadius);
            } else if (attr == R.styleable.ArcImageView_corner_top_left_radius) {
                mRoundRadiusLeftTop = ta.getDimensionPixelSize(attr, (int) mRoundRadiusLeftTop);
            } else if (attr == R.styleable.ArcImageView_corner_top_right_radius) {
                mRoundRadiusRightTop = ta.getDimensionPixelSize(attr, (int) mRoundRadiusRightTop);
            } else if (attr == R.styleable.ArcImageView_corner_bottom_left_radius) {
                mRoundRadiusLeftBottom = ta.getDimensionPixelSize(attr, (int) mRoundRadiusLeftBottom);
            } else if (attr == R.styleable.ArcImageView_corner_bottom_right_radius) {
                mRoundRadiusRightBottom = ta.getDimensionPixelSize(attr, (int) mRoundRadiusRightBottom);
            } else if (attr == R.styleable.ArcImageView_mask_color) {
//                maskColor = ta.getColor(attr, maskColor);
            } else if (attr == R.styleable.ArcImageView_back_color) {
//                backColor = ta.getColor(attr,backColor);
            }
        }
        if (mRoundRadius != 0) {
            mRoundRadiusRightTop = mRoundRadius;
            mRoundRadiusRightBottom = mRoundRadius;
            mRoundRadiusLeftBottom = mRoundRadius;
            mRoundRadiusLeftTop = mRoundRadius;
        }
        ta.recycle();
    }

    /**
     * 对于普通的view,在执行到onDraw()时，背景图已绘制完成
     * <p>
     * 对于ViewGroup,当它没有背景时直接调用的是dispatchDraw()方法, 而绕过了draw()方法，
     * 当它有背景的时候就调用draw()方法，而draw()方法里包含了dispatchDraw()方法的调用，
     */
    @Override
    public void onDraw(Canvas canvas) {

        if (mBitmap != null) {
            if (mShape == SHAPE_CIRCLE) {
                canvas.drawCircle(mViewRect.right / 2, mViewRect.bottom / 2,
                        Math.min(mViewRect.right, mViewRect.bottom) / 2, mBitmapPaint);
            } else if (mShape == SHAPE_OVAL) {
                canvas.drawOval(mViewRect, mBitmapPaint);
            } else {
//                canvas.drawRoundRect(mViewRect, mRoundRadius, mRoundRadius, mBitmapPaint);
                mPath.reset();
                mPath.addRoundRect(mViewRect, new float[]{
                        mRoundRadiusLeftTop, mRoundRadiusLeftTop,
                        mRoundRadiusRightTop, mRoundRadiusRightTop,
                        mRoundRadiusRightBottom, mRoundRadiusRightBottom,
                        mRoundRadiusLeftBottom, mRoundRadiusLeftBottom,
                }, Path.Direction.CW);
                canvas.drawPath(mPath, mBitmapPaint);

            }
        }

        if (mBorderSize > 0) { // 绘制边框
            if (mShape == SHAPE_CIRCLE) {
                canvas.drawCircle(mViewRect.right / 2, mViewRect.bottom / 2,
                        Math.min(mViewRect.right, mViewRect.bottom) / 2 - mBorderSize / 2, mBorderPaint);
            } else if (mShape == SHAPE_OVAL) {
                canvas.drawOval(mBorderRect, mBorderPaint);
            } else {
//                canvas.drawRoundRect(mBorderRect, mRoundRadius, mRoundRadius, mBorderPaint);
                mPath.reset();
                mPath.addRoundRect(mBorderRect, new float[]{
                        mRoundRadiusLeftTop, mRoundRadiusLeftTop,
                        mRoundRadiusRightTop, mRoundRadiusRightTop,
                        mRoundRadiusRightBottom, mRoundRadiusRightBottom,
                        mRoundRadiusLeftBottom, mRoundRadiusLeftBottom,
                }, Path.Direction.CW);
                canvas.drawPath(mPath, mBorderPaint);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initRect();
        setupBitmapShader();
    }

    // 不能在onLayout()调用invalidate()，否则导致绘制异常。（setupBitmapShader()中调用了invalidate()）
    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
//        initRect();
//        setupBitmapShader();
    }

    private void setupBitmapShader() {
        // super(context, attrs, defStyle)调用setImageDrawable时,成员变量还未被正确初始化
        if (mBitmapPaint == null) {
            return;
        }
        if (mBitmap == null) {
            invalidate();
            return;
        }
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mBitmapPaint.setShader(mBitmapShader);

        // 固定为CENTER_CROP,使图片在view中居中并裁剪
//        mShaderMatrix.set(null);

        if (mShape == SHAPE_CIRCLE) {
            // 缩放到高或宽　与view的高或宽　匹配
            float scale = Math.max(getWidth() * 1f / mBitmap.getWidth(), getHeight() * 1f / mBitmap.getHeight());
            // 由于BitmapShader默认是从画布的左上角开始绘制，所以把其平移到画布中间，即居中
            float dx = (getWidth() - mBitmap.getWidth() * scale) / 2;
            float dy = (getHeight() - mBitmap.getHeight() * scale) / 2;
            mShaderMatrix.setScale(scale, scale);
            mShaderMatrix.postTranslate(dx, dy);
            mBitmapShader.setLocalMatrix(mShaderMatrix);
        } else {
            // 参照imageview源码，实现默认center_crop效果
            float scale;
            float dx = 0, dy = 0;
            int dwidth= mBitmap.getWidth();
            int dheight=mBitmap.getHeight();
            int vheight = getHeight();
            int vwidth= getWidth();

            if (dwidth * vheight > vwidth * dheight) {
                scale = (float) vheight / (float) dheight;
                dx = (vwidth - dwidth * scale) * 0.5f;
            } else {
                scale = (float) vwidth / (float) dwidth;
                dy = (vheight - dheight * scale) * 0.5f;
            }

            mShaderMatrix.setScale(scale, scale);
            mShaderMatrix.postTranslate(dx, dy);
            mBitmapShader.setLocalMatrix(mShaderMatrix);
        }
        invalidate();
    }

    //　设置图片的绘制区域
    private void initRect() {

        mViewRect.top = 0;
        mViewRect.left = 0;
        mViewRect.right = getWidth(); // 宽度
        mViewRect.bottom = getHeight(); // 高度

        // 边框的矩形区域不能等于ImageView的矩形区域，否则边框的宽度只显示了一半
        mBorderRect.top = mBorderSize / 2;
        mBorderRect.left = mBorderSize / 2;
        mBorderRect.right = getWidth() - mBorderSize / 2;
        mBorderRect.bottom = getHeight() - mBorderSize / 2;
    }

    public int getShape() {
        return mShape;
    }

    public void setShape(int shape) {
        mShape = shape;
    }

    public float getBorderSize() {
        return mBorderSize;
    }

    public void setBorderSize(int mBorderSize) {
        this.mBorderSize = mBorderSize;
        mBorderPaint.setStrokeWidth(mBorderSize);
        initRect();
        invalidate();
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(int mBorderColor) {
        this.mBorderColor = mBorderColor;
        mBorderPaint.setColor(mBorderColor);
        invalidate();
    }

    public float getRoundRadius() {
        return mRoundRadius;
    }

    public void setRoundRadius(float mRoundRadius) {
        this.mRoundRadius = mRoundRadius;
        invalidate();
    }

    public void setRoundRadiis(float roundRadiusLeftBottom, float roundRadiusLeftTop, float roundRadiusRightBottom, float roundRadiusRightTop) {
        mRoundRadiusLeftBottom = roundRadiusLeftBottom;
        mRoundRadiusLeftTop = roundRadiusLeftTop;
        mRoundRadiusRightBottom = roundRadiusRightBottom;
        mRoundRadiusRightTop = roundRadiusRightTop;
        invalidate();
    }

    public float[] getRoundRadiis() {
        return new float[]{mRoundRadiusLeftBottom, mRoundRadiusLeftTop, mRoundRadiusRightBottom, mRoundRadiusRightTop};
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        try {
            Bitmap bitmap;
            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                Bitmap.Config config =
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565;
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),config);
            }
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setCornerRadius(float i){
        mRoundRadiusLeftBottom = i;
        mRoundRadiusLeftTop = i;
        mRoundRadiusRightTop = i;
        mRoundRadiusRightBottom = i;
    }

    public void setCornerBottomRightRadius(float i) {
        mRoundRadiusRightBottom = i;
    }

    public void setCornerTopRightRadius(float i) {
        mRoundRadiusRightTop = i;
    }
    public void setCornerBottomLeftRadius(float i) {
        mRoundRadiusLeftBottom = i;
    }
    public void setCornerTopLeftRadius(float i) {
        mRoundRadiusLeftTop = i;
    }
}