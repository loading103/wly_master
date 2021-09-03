package com.daqsoft.baselib.widgets.click;

import android.view.View;

public abstract class NoDoubleClickListener implements View.OnClickListener {

    public final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;
    private String moduleName;

    public NoDoubleClickListener() {

    }

    public NoDoubleClickListener(String module) {
        moduleName = module;
    }

    @Override
    public void onClick(View v) {
        long curClickTime = System.currentTimeMillis();
        if (curClickTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = curClickTime;
            onNoDoubleClick(v);
        }
    }

    public abstract void onNoDoubleClick(View v);
}