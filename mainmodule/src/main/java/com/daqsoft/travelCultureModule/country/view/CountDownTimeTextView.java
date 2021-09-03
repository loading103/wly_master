package com.daqsoft.travelCultureModule.country.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.daqsoft.mainmodule.R;


/**
 *
 */

public class CountDownTimeTextView extends AppCompatTextView {
    private static final String SPLIT = ":";
    private static final String BLANK = "";
    private static final int RADIUS = 4;
    static final long MS_IN_A_DAY = 1000 * 60 * 60 * 24;
    static final long MS_IN_AN_HOUR = 1000 * 60 * 60;
    static final long MS_IN_A_MINUTE = 1000 * 60;
    static final long MS_IN_A_SECOND = 1000;
    public static final String DEFAULT_TIME = "00:00:00";

    private String simTime = "";
    private CountDownTimer timer;

    private Paint borderPaint;
    private boolean border = false;

    public CountDownTimeTextView(Context context) {
        super(context);
        init(context, null);
    }

    public CountDownTimeTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CountDownTimeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CountDownTimeTextView);
            if (ta.hasValue(R.styleable.CountDownTimeTextView_border)) {
                border = ta.getBoolean(R.styleable.CountDownTimeTextView_border, false);
                borderPaint = new Paint();
                borderPaint.setDither(true);
                borderPaint.setAntiAlias(true);
                borderPaint.setStyle(Paint.Style.STROKE);
                borderPaint.setStrokeWidth(1f);
                borderPaint.setColor(Color.parseColor("#999999"));
            }
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        SaveState ss = new SaveState(parcelable);
        ss.text = simTime;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SaveState ss = (SaveState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setText(ss.text);
    }

    static class SaveState extends BaseSavedState {
        private String text;

        SaveState(Parcel source) {
            super(source);
            text = source.readString();
        }

        SaveState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(text);
        }

        public static final Creator<SaveState> CREATOR = new Creator<SaveState>() {
            @Override
            public SaveState createFromParcel(Parcel source) {
                return new SaveState(source);
            }

            @Override
            public SaveState[] newArray(int size) {
                return new SaveState[size];
            }
        };
    }

    public void setTime(long time, final OnFinishListener listener) {
        setTime("", time, listener);
    }

    public void setTime(final String tip, long time, final OnFinishListener listener) {
        if (timer != null) {
            timer.cancel();
        }
        setText(tip + DEFAULT_TIME);
        timer = new CountDownTimer(time, 1) {
            @Override
            public void onTick(long l) {
                if (l >= 60 * 60 * 1000) { //超过1小时
                    simTime = getTimeFromLong(l);
                } else {
                    simTime = getTimeFromLong(l);
//                    simTime = getMillisecondsTimes(l);
                }
                setText(tip + simTime);
            }

            @Override
            public void onFinish() {
                setText(tip + DEFAULT_TIME);
                listener.onFinish();
            }
        };

    }

    private String getMillisecondsTimes(long l) {
        l = l % MS_IN_AN_HOUR;
        long minutes = l / MS_IN_A_MINUTE; //分
        l = l % MS_IN_A_MINUTE;
        long seconds = l / MS_IN_A_SECOND; //秒
        l = l % MS_IN_A_SECOND;
        long milliseconds = l / 10; //毫秒

        StringBuilder sb = new StringBuilder();
        if (minutes >= 10) {
            sb.append(BLANK).append(minutes).append(SPLIT);
        } else {
            sb.append(BLANK + "0").append(minutes).append(SPLIT);
        }

        if (seconds >= 10) {
            sb.append(seconds).append(SPLIT);
        } else {
            sb.append("0").append(seconds).append(SPLIT);
        }

        if (milliseconds >= 10) {
            sb.append(milliseconds).append(BLANK);
        } else {
            sb.append("0").append(milliseconds).append(BLANK);
        }
        return sb.toString();

    }

    public String getTimeFromLong(long diff) {
        long day = diff / MS_IN_A_DAY;
        diff = diff % MS_IN_A_DAY;
        long numHours = diff / MS_IN_AN_HOUR;
        diff = diff % MS_IN_AN_HOUR;
        long numMinutes = diff / MS_IN_A_MINUTE;
        diff = diff % MS_IN_A_MINUTE;
        long numSeconds = diff / MS_IN_A_SECOND;

        StringBuilder buf = new StringBuilder();
        if (day > 0) {
            buf.append(BLANK).append(day).append("天").append(SPLIT);
        }
        if (numHours >= 10) { //hour
            buf./*append(BLANK).*/append(numHours).append(SPLIT);
        } else if (numHours >= 0) {
            buf./*append(BLANK + "0").*/append(numHours).append(SPLIT);
        }

        if (numMinutes >= 10) { //minutes
            buf.append(numMinutes).append(SPLIT);
        } else if (numMinutes >= 0) {
            buf.append("0").append(numMinutes).append(SPLIT);
        }

        if (numSeconds >= 10) { //seconds
            buf.append(numSeconds).append(BLANK);
        } else if (numSeconds >= 0) {
            buf.append("0").append(numSeconds).append(BLANK);
        }

        return buf.toString();
    }

    public void start() {
        timer.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (border) {
            String text = getText().toString();
            String[] result = text.split(":");
            @SuppressLint("DrawAllocation") float[] length = new float[result.length];
            float splitWidth = getPaint().measureText(":");
            float blankWidth = getPaint().measureText("  ");

            if (result.length > 0) {
                float height = getHeight() - getPaddingBottom();
                for (int i = 0; i < result.length; i++) {
                    float value = getPaint().measureText(result[i]);
                    length[i] = value;
                }

                if (result.length == 3) {
                    @SuppressLint("DrawAllocation") RectF r1 = new RectF((int) (getPaddingLeft() + blankWidth / 2), getPaddingTop(), (int) (getPaddingLeft() + length[0] - blankWidth / 2), (int) (getPaddingTop() + height));
                    @SuppressLint("DrawAllocation") RectF r2 = new RectF((int) (r1.right + blankWidth + splitWidth), getPaddingTop(), (int) (r1.right + splitWidth + length[1]), (int) (getPaddingTop() + height));
                    @SuppressLint("DrawAllocation") RectF r3 = new RectF((int) (r2.right + blankWidth + splitWidth), getPaddingTop(), (int) (r2.right + splitWidth + length[2]), (int) (getPaddingTop() + height));
                    canvas.drawRoundRect(r1, RADIUS, RADIUS, borderPaint);
                    canvas.drawRoundRect(r2, RADIUS, RADIUS, borderPaint);
                    canvas.drawRoundRect(r3, RADIUS, RADIUS, borderPaint);
                }
            }
        }
    }

    public interface OnFinishListener {
        void onFinish();
    }
}
