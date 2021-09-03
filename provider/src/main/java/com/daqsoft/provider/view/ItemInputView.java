package com.daqsoft.provider.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daqsoft.provider.R;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

/**
 * @Description item输入样式
 * @ClassName ItemInputView
 * @Author PuHua
 * @Time 2019/6/11 13:45
 * @Version 1.0
 */
public class ItemInputView extends RelativeLayout {
    private String mLabel;
    private String mContent = "";
    private int leftLabelColor;
    private int rightHintColor;
    private int rightContentColor;
    private int contentPosition;
    private int leftLabelSize;
    private int rightContentSize;
    private int maxElems;

    public int getMaxElems() {
        return maxElems;
    }

    public void setMaxElems(int maxElems) {
        this.maxElems = maxElems;
    }

    private int shortMaxElement = 100;

    public String getmLabel() {
        return mLabel;
    }

    public void setmLabel(String mLabel) {
        this.mLabel = mLabel;
        if (tvLabel != null) {
            tvLabel.setText(mLabel);
            if (isNeed) {
                SpannableString spannableString = new SpannableString("*" + mLabel);
                spannableString.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvLabel.setText(spannableString);
            } else {
                tvLabel.setText(mLabel);
            }

        }
    }


    private String etHint;
    private boolean isNeed;
    private int inputLines;

    private List<String> pictureList;
    /**
     * 展示文字
     */
    TextView tvContent;

    public TextView getTvContent() {
        return tvContent;
    }

    public void setTvContent(TextView tvContent) {
        this.tvContent = tvContent;
    }

    private int contentGravity;
    /**
     * 输入文字
     */
    public EditText etContent;

    TextView tvLabel;


    CompositeDisposable compositeDisposable;


    public ItemInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initItem(context, attrs);
    }

    private OnKeyChanged keyChanged;

    public void setKeyChanged(OnKeyChanged keyChanged) {
        this.keyChanged = keyChanged;
    }

    public interface OnKeyChanged {
        void keyChanged(String key);
    }

    private void initItem(Context context, AttributeSet attrs) {
        int sp = getResources().getDimensionPixelSize(R.dimen.sp_14);
        int dp = getResources().getDimensionPixelSize(R.dimen.dp_16);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemView);
        mLabel = typedArray.getString(R.styleable.ItemView_leftLabel);
        mContent = typedArray.getString(R.styleable.ItemView_rightContent);
        leftLabelColor = typedArray.getColor(R.styleable.ItemView_leftLabelColor, context.getResources().getColor(R.color.txt_gray));
        rightHintColor = typedArray.getColor(R.styleable.ItemView_rightHintColor,
                context.getResources().getColor(R.color.txt_gray));
        rightContentColor = typedArray.getColor(R.styleable.ItemView_rightContentColor, context.getResources().getColor(R.color.txt_black));
        contentPosition = typedArray.getInt(R.styleable.ItemView_rightContentPosition, 0);
        rightContentSize = (int) typedArray.getDimension(R.styleable.ItemView_rightContentSize, sp);
        etHint = typedArray.getString(R.styleable.ItemView_etHint);
        isNeed = typedArray.getBoolean(R.styleable.ItemView_isNeed, true);
        inputLines = typedArray.getInt(R.styleable.ItemView_inputLines, 2);
        maxElems = typedArray.getInt(R.styleable.ItemView_maxElms, 1000);
        contentGravity = typedArray.getInt(R.styleable.ItemView_contentGravity, 1);

        tvLabel = new TextView(context);
        tvLabel.setId(R.id.item_left_label);

        if (isNeed) {
            SpannableString spannableString = new SpannableString(mLabel + "*");
            spannableString.setSpan(new ForegroundColorSpan(Color.RED), mLabel.length(), spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvLabel.setText(spannableString);
        } else {
            tvLabel.setText(mLabel);
        }
        tvLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, sp);
        tvLabel.setTextColor(leftLabelColor);
        ///////////////////////////////////
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        View view = new View(context);
        view.setBackgroundColor(context.getResources().getColor(R.color.dividing_line));
        LayoutParams p = new LayoutParams(LayoutParams.WRAP_CONTENT, 1);
//        p.leftMargin = dp;
//        p.rightMargin = dp;

        if (contentPosition == 0) {

            etContent = new EditText(context);
            etContent.setId(R.id.item_bottom_content);
            etContent.setHint(etHint);
            etContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, rightContentSize);
            etContent.setTextColor(rightContentColor);
            etContent.setHintTextColor(rightHintColor);
            etContent.setBackground(null);
            if (contentGravity == 0) {
                etContent.setGravity(Gravity.LEFT);
            } else {
                etContent.setGravity(Gravity.RIGHT);
//                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            }

//                etContent.setGravity(Gravity.RIGHT);
            etContent.setMaxEms(shortMaxElement);

            etContent.setPadding(0, dp, 0, dp);
            etContent.addTextChangedListener(new TextWatcher() {
                private CharSequence temp;
                private int selectionStart;
                private int selectionEnd;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    temp = s;
                }

                @Override
                public void afterTextChanged(Editable s) {
                    selectionStart = etContent.getSelectionStart();
                    selectionEnd = etContent.getSelectionEnd();
                    if (temp.length() > shortMaxElement) {
                        if (selectionStart == 0) {
                            etContent.setSelection(etContent.getText().length());
                        } else {
                            s.delete(shortMaxElement, selectionEnd);
                            etContent.setText(s);
                            etContent.setSelection(etContent.getText().length());
                        }
                    }

                    mContent = s.toString();

                    if (keyChanged != null) {
                        keyChanged.keyChanged(mContent);
                    }
                }
            });
            lp.rightMargin = dp;
            addView(etContent, lp);

            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            p.addRule(RelativeLayout.BELOW, R.id.item_left_label);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp.addRule(RelativeLayout.RIGHT_OF, R.id.item_left_label);
            tvLabel.setPadding(dp, dp, dp, dp);
            addView(tvLabel, layoutParams);
            addView(view, p);
        } else if (contentPosition == 1) {
            // 上下模式

            // 上下输入文字模式
            etContent = new EditText(context);
            etContent.setId(R.id.item_bottom_content);
            etContent.setHint(etHint);
            etContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, rightContentSize);
            etContent.setTextColor(rightContentColor);
            etContent.setHintTextColor(leftLabelColor);
            etContent.setPadding(dp, dp, dp, dp);
//                etContent.setLines(inputLines);
            etContent.setMaxEms(maxElems);
//                etContent.setMinLines(6);
            etContent.setGravity(Gravity.LEFT | Gravity.TOP);
//                etContent.setBackground(context.getResources().getDrawable(R.drawable.shape_white_5));
            etContent.addTextChangedListener(new TextWatcher() {
                private CharSequence temp;
                private int selectionStart;
                private int selectionEnd;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    temp = s;
                }

                @Override
                public void afterTextChanged(Editable s) {
                    selectionStart = etContent.getSelectionStart();
                    selectionEnd = etContent.getSelectionEnd();
                    if (temp.length() > maxElems) {
                        if (selectionStart == 0) {
                            etContent.setSelection(etContent.getText().length());
                        } else {
                            s.delete(maxElems, selectionEnd);
                            etContent.setText(s);
                            etContent.setSelection(etContent.getText().length());

                        }

                    }
                    if (keyChanged != null) {


                        mContent = s.toString();

                        keyChanged.keyChanged(mContent);
                    }
                }
            });
            lp.leftMargin = dp;
            lp.rightMargin = dp;
            lp.width = LayoutParams.MATCH_PARENT;
            addView(etContent, lp);

            addView(tvLabel, layoutParams);
//            addView(view, p);
            tvLabel.setPadding(dp, dp, dp, getResources().getDimensionPixelSize(R.dimen.dp_4));
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp.addRule(RelativeLayout.BELOW, R.id.item_left_label);
            p.addRule(RelativeLayout.BELOW, R.id.item_right_content);
        }


        setBackgroundColor(Color.WHITE);
        typedArray.recycle();
    }


    public String getContent() {
        if (tvContent != null) {
            mContent = tvContent.getText().toString();
        }

        if (etContent != null) {
            mContent = etContent.getText().toString();
        }

        return mContent;
    }

    public void setContent(String mContent) {
        this.mContent = mContent;
        if (tvContent != null) {
            tvContent.setText(mContent);
        }

        if (etContent != null) {
            etContent.setText(mContent);
        }
    }

}
