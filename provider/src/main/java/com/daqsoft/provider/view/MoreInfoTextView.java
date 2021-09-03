package com.daqsoft.provider.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * @author luoyi
 * @des  更多信息textview
 * @Date 2020/4/26 21:48
 */
public class MoreInfoTextView extends AppCompatTextView {

    public MoreInfoTextView(Context context) {
        super(context);
    }

    public MoreInfoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoreInfoTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public void setText(String content,String moreInfo){

    }
}
