package com.daqsoft.provider.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daqsoft.provider.R;

/**
 * 自定义输入框搜索控件
 *
 * @author 黄熙
 * @version 1.0.0
 * @date 2019/12/18 0018
 * @since JDK 1.8
 */
public class EditSearchView extends LinearLayout implements TextWatcher, TextView.OnEditorActionListener {
    /**
     * 输入框
     */
    private EditText etSearch;
    /**
     * 搜索回调监听对象
     */
    private EditSearchListener listener;


    public EditSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public EditSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    /**
     * 回调对象赋值
     *
     * @param editSearchListener 回调对象
     * @return
     */
    public EditSearchView setEditSearchListener(EditSearchListener editSearchListener) {
        this.listener = editSearchListener;
        return this;
    }

    /**
     * 初始化
     *
     * @param context 上下文
     */
    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.editSearchViewStyle);
        View view = LayoutInflater.from(context).inflate(R.layout.include_edit_search, this);
        etSearch = view.findViewById(R.id.et_search);
        etSearch.setHint(typedArray.getString(R.styleable.editSearchViewStyle_txt_hint));
        etSearch.addTextChangedListener(this);
        etSearch.setOnEditorActionListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public boolean onEditorAction(TextView textView, int keyAction, KeyEvent keyEvent) {
        if (keyAction == EditorInfo.IME_ACTION_SEARCH) {
            if (listener != null) {
                listener.search(textView.getText().toString().trim());
            }
            return true;
        }
        return false;
    }


    /**
     * 搜索框回调函数
     */
    public interface EditSearchListener {
        void search(String content);
    }

}
