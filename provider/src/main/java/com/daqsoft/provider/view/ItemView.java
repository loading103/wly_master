package com.daqsoft.provider.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daqsoft.provider.R;

import io.reactivex.disposables.CompositeDisposable;


/**
 * @Description item布局
 * @ClassName ItemView
 * @Author PuHua
 * @Time 2019/6/11 13:45
 * @Version 1.0
 */
public class ItemView extends RelativeLayout {
    private String mLabel;
    private String mContent;
    private int leftLabelColor;
    private int rightContentColor;
    private int rightContentMaxLength;
    private int contentPosition;
    private int leftLabelSize;
    private int rightContentSize;
    private boolean isNeed;
    //    private int inputType;
    private int labelPadding;
    private int drawableLeft;
    private int labelType;
    private boolean showArrow;
    private boolean showDivider;
    private int contentGravity;
    private float labelWidth;

    /**
     * 右边或下边展示的文字
     */
    TextView tvContent;

    CompositeDisposable compositeDisposable;

    TextView tvLabel;
    /**
     * 展示图片时的暂无字样提示
     */
    TextView tvTemp;
    int sp = getResources().getDimensionPixelSize(R.dimen.sp_16);
    int dp = getResources().getDimensionPixelSize(R.dimen.dp_16);

    private View lineView;
    private LayoutParams lineParams;

    public ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initItem(context, attrs);
    }

    private Activity activity;


    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    private void initItem(Context context, AttributeSet attrs) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemView);
        mLabel = typedArray.getString(R.styleable.ItemView_leftLabel);
        mContent = typedArray.getString(R.styleable.ItemView_rightContent);
        leftLabelColor = typedArray.getColor(R.styleable.ItemView_leftLabelColor, context.getResources().getColor(R.color.txt_black));
        rightContentColor = typedArray.getColor(R.styleable.ItemView_rightContentColor, context.getResources().getColor(R.color.txt_black));
        rightContentMaxLength = typedArray.getInt(R.styleable.ItemView_rightContentMaxLength,-1);
        contentPosition = typedArray.getInt(R.styleable.ItemView_rightContentPosition, 0);
        rightContentSize = (int) typedArray.getDimension(R.styleable.ItemView_rightContentSize, sp);
        isNeed = typedArray.getBoolean(R.styleable.ItemView_isNeed, false);
//        inputType = typedArray.getInt(R.styleable.ItemView_inputType, 1);
        leftLabelSize = (int) typedArray.getDimension(R.styleable.ItemView_labelSize, sp);
        labelPadding = (int) typedArray.getDimension(R.styleable.ItemView_labelPadding, dp);
        drawableLeft = typedArray.getResourceId(R.styleable.ItemView_drawableLeft, 0);
        labelType = typedArray.getInt(R.styleable.ItemView_labelType, 1);
        showArrow = typedArray.getBoolean(R.styleable.ItemView_showArrow, false);
        showDivider = typedArray.getBoolean(R.styleable.ItemView_showDivider, true);
        contentGravity = typedArray.getInt(R.styleable.ItemView_contentGravity, 1);
        labelWidth = typedArray.getDimension(R.styleable.ItemView_labelWidth, 0.0f);

        tvLabel = new TextView(context);
        if (drawableLeft != 0) {
            tvLabel.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(drawableLeft), null, null, null);
            tvLabel.setCompoundDrawablePadding(dp / 2);
        }

        if (labelType == 0) {
            // 加粗
            tvLabel.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }
        tvLabel.setId(R.id.item_left_label);
        if (isNeed) {
            SpannableString spannableString = new SpannableString("*" + mLabel);
            spannableString.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvLabel.setText(spannableString);
        } else {
            tvLabel.setText(mLabel);
        }

//        tvLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, sp);
        tvLabel.setTextColor(leftLabelColor);
        tvLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, leftLabelSize);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        if (labelWidth != 0) {
            float scale = context.getResources().getDisplayMetrics().density;

            layoutParams.width = (int) (labelWidth * scale + 0.5f);
        }

        tvContent = new TextView(context);
        tvContent.setCompoundDrawablePadding(10);
        tvContent.setId(R.id.item_right_content);
        tvContent.setText(mContent);
        tvContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, rightContentSize);
        tvContent.setTextColor(rightContentColor);
        if(rightContentMaxLength != -1){
            tvContent.setMaxEms(rightContentMaxLength);
            tvContent.setSingleLine(true);
            tvContent.setEllipsize(TextUtils.TruncateAt.END);
        }
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        lineView = new View(context);
        lineView.setBackgroundColor(context.getResources().getColor(R.color.dividing_line));
        lineParams = new LayoutParams(LayoutParams.WRAP_CONTENT, 1);
        lineParams.topMargin = labelPadding;

        if (contentPosition == 0) {
            // 左右模式
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lineParams.addRule(RelativeLayout.BELOW, R.id.item_right_content);

            lp.addRule(RelativeLayout.RIGHT_OF, R.id.item_left_label);
            tvLabel.setPadding(0, 0, labelPadding, 0);
//            tvLabel.setLeft();
            tvContent.setPadding(labelPadding, 0, 0, 0);
            if (contentGravity == 0) {
                tvContent.setGravity(Gravity.LEFT);
            } else {
                tvContent.setGravity(Gravity.RIGHT);
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            }

            if (showArrow) {
                lp.width = LayoutParams.MATCH_PARENT;
                Drawable drawable = context.getResources().getDrawable(R.mipmap.choose);
                tvContent.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                tvContent.setCompoundDrawablePadding(dp);
            }
            // 根据行数显示左右对齐方式
            tvContent.post(new Runnable() {
                @Override
                public void run() {
                    if (tvContent.getLineCount() > 1)
                        // 多余一行左对齐
                        tvContent.setGravity(Gravity.LEFT);
                    else
                        // 一行右对齐
                        if (contentGravity == 0) {
                            tvContent.setGravity(Gravity.LEFT);
                        } else {
                            tvContent.setGravity(Gravity.RIGHT);
                        }
                }
            });
            addView(tvLabel, layoutParams);
            addView(tvContent, lp);
            if (showDivider) {
                addView(lineView, lineParams);
            }
        } else if (contentPosition == 1) {
            // 上下模式

            tvLabel.setPadding(labelPadding, labelPadding, labelPadding, getResources().getDimensionPixelSize(R.dimen.dp_4));
            tvContent.setPadding(labelPadding, getResources().getDimensionPixelSize(R.dimen.dp_4), labelPadding, labelPadding);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp.addRule(RelativeLayout.BELOW, R.id.item_left_label);
            lineParams.addRule(RelativeLayout.BELOW, R.id.item_right_content);
            addView(tvLabel, layoutParams);
            addView(tvContent, lp);
        }

        setBackgroundColor(Color.WHITE);
        typedArray.recycle();
    }

    /**
     * 设置右边展示文字 默认为暂无
     *
     * @param content
     */
    public void setContent(String content) {
        this.mContent = content;
        if (tvContent != null) {
            if (!TextUtils.isEmpty(content)) {
                if (mContent.equals("null")) {
                    tvContent.setHint("暂无");
                    tvContent.setHintTextColor(getResources().getColor(R.color.txt_gray));
                } else {
                    tvContent.setText(mContent);
                }
            } else {
                tvContent.setHint("暂无");
                tvContent.setHintTextColor(getResources().getColor(R.color.txt_gray));
            }
            if (contentPosition == 0) {
                // 左右展示模式时，根据行数显示左右对齐方式
                tvContent.post(new Runnable() {
                    @Override
                    public void run() {
                        if (tvContent.getLineCount() > 1)
                            // 多余一行左对齐
                            tvContent.setGravity(Gravity.LEFT);
                        else
                            // 一行右对齐
                            tvContent.setGravity(Gravity.RIGHT);
                    }
                });
            }

        }
    }

    public void setContentIcon(Drawable drawable){
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvContent.setCompoundDrawables(drawable, null, null, null);
        }
    }

    /**
     * 设置右边展示文字+单位 默认为暂无
     *
     * @param content
     */
    public void setContentWithUnit(String content, String unit) {
        this.mContent = content;
        if (tvContent != null) {
            if (!TextUtils.isEmpty(content)) {
                if (mContent.equals("null")) {
                    tvContent.setHint("暂无");
                    tvContent.setHintTextColor(getResources().getColor(R.color.txt_gray));
                } else {
                    tvContent.setText(mContent + unit);
                }
            } else {
                tvContent.setHint("暂无");
                tvContent.setHintTextColor(getResources().getColor(R.color.txt_gray));
            }
        }
    }

    /**
     * 设置右边显示的文字并自定义默认文字
     *
     * @param content
     * @param defaultString
     */
    public void setContent(String content, String defaultString) {
        this.mContent = content;
        if (tvContent != null) {
            if (!TextUtils.isEmpty(content)) {
                if (mContent.equals("null")) {
                    tvContent.setHint(defaultString);
                    tvContent.setHintTextColor(getResources().getColor(R.color.txt_gray));
                } else {
                    tvContent.setText(mContent);
                }
            } else {
                tvContent.setHint(defaultString);
                tvContent.setHintTextColor(getResources().getColor(R.color.txt_gray));
            }
        }
    }

    public void setContent(int content, String defaultString) {
        this.mContent = String.valueOf(content);
        if (tvContent != null) {
            if (!TextUtils.isEmpty(mContent)) {
                if (mContent.equals("null")) {
                    tvContent.setHint(defaultString);
                    tvContent.setHintTextColor(getResources().getColor(R.color.txt_gray));
                } else {
                    tvContent.setText(mContent);
                }

            } else {
                tvContent.setHint(defaultString);
                tvContent.setHintTextColor(getResources().getColor(R.color.txt_gray));
            }
        }
    }

    public void setLabel(String mLabel) {
        this.tvLabel.setText(mLabel);

    }

    public void hidArrow(){
        tvContent.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    public void showDivider(){
        addView(lineView, lineParams);
    }

    public String getContent() {
        return this.mContent;
    }

    public void compositeDisposableClear() {
        if (compositeDisposable != null)
            compositeDisposable.clear();
    }

    public TextView getTvContent() {
        return tvContent;
    }
    public TextView getTvLabel(){
        return tvLabel;
    }
}
