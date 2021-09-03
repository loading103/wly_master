package com.daqsoft.provider.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;

import com.daqsoft.provider.R;

import java.util.Objects;


public class ClearEditText extends AppCompatEditText implements View.OnFocusChangeListener, TextWatcher {

    //右边的删除按钮
    private Drawable mClearDrawable;

    private CharSequence temp;
    private int selectionStart;
    private int selectionEnd;
    private int maxElement = 1000;

    public int getMaxElement() {
        return maxElement;
    }

    public void setMaxElement(int maxElement) {
        this.maxElement = maxElement;
    }

    public ClearEditText(Context context) {
        this(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mClearDrawable = getResources().getDrawable(R.mipmap.button_close);
        //设置删除按钮的边界
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
        //默认隐藏删除按钮
        setClearIcon(false);
//        setSingleLine();
        //监听EditText焦点变化，以根据text长度控制删除按钮的显示、隐藏
        setOnFocusChangeListener(this);
        //监听文本内容变化
        addTextChangedListener(this);
    }

    /**
     * 控制EditText右边制删除按钮的显示、隐藏
     */
    private void setClearIcon(boolean isShow) {
        Drawable rightDrawable = isShow ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1],
                rightDrawable, getCompoundDrawables()[3]);
    }

    /**
     * 有焦点，并文本长度大于0则显示删除按钮
     *
     * @param v
     * @param hasFocus
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            setClearIcon(getText().length() > 0);
        } else {
            setClearIcon(false);
        }
    }

    /**
     * 文本内容变化时回调
     * 当文本长度大于0时显示删除按钮， 否则隐藏
     *
     * @param text
     * @param start
     * @param lengthBefore
     * @param lengthAfter
     */
    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        setClearIcon(text.length() > 0);
        temp = text;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        selectionStart = getSelectionStart();
        selectionEnd = getSelectionEnd();
        if (temp.length() > maxElement) {
            if (selectionStart==0){
                setSelection(Objects.requireNonNull(getText()).length());
            }else{
                s.delete(maxElement, selectionEnd);
                setText(s);
                setSelection(getText().length());
            }
        }

        if (afterTextChangedListenr != null) {
            afterTextChangedListenr.onAfterTextChanged(s.toString());
        }
    }

    /**
     * 通过手指的触摸位置模式删除按钮的点击事件
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getCompoundDrawables()[2] != null) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                boolean xTouchable = event.getX() > (getWidth() - getPaddingRight() - mClearDrawable.getIntrinsicWidth())
                        && (event.getX() < (getWidth() - getPaddingRight()));

                boolean yTouchable = event.getY() > (getHeight() - mClearDrawable.getIntrinsicHeight()) / 2
                        && event.getY() < (getHeight() + mClearDrawable.getIntrinsicHeight()) / 2;

                //清除文本
                if (xTouchable && yTouchable) {
                    setText("");
                }
            }
        }
        return super.onTouchEvent(event);
    }


    /**
     * EditText抖动
     *
     * @param counts
     * @return
     */
    public void startShake(int counts) {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(500);
        startAnimation(translateAnimation);
    }

    @Override
    public void setCompoundDrawablePadding(int pad) {
        super.setCompoundDrawablePadding(pad);
    }

    public AfterTextChangedListenr afterTextChangedListenr;
    public interface AfterTextChangedListenr{
        void onAfterTextChanged(String text);
    }
}
