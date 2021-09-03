package com.daqsoft.thetravelcloudwithculture.home.view;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;

import com.daqsoft.baselib.base.BaseApplication;
import com.daqsoft.baselib.utils.Utils;

import timber.log.Timber;

/**
 * 用一句话来描述功能
 *
 * @author 黄熙
 * @version 1.0.0
 * @date 2020/4/9 0009
 * @since JDK 1.8
 */
public class HorizontalStackTransformerWithRotation implements ViewPager.PageTransformer {
    private static final float CENTER_PAGE_SCALE = 1f;
    private int offscreenPageLimit;
    private ViewPager boundViewPager;

    public HorizontalStackTransformerWithRotation(@NonNull ViewPager boundViewPager) {
        this.boundViewPager = boundViewPager;
        this.offscreenPageLimit = boundViewPager.getOffscreenPageLimit();
    }

    @Override
    public void transformPage(@NonNull View view, float position) {
        int pagerWidth = boundViewPager.getWidth();
        float horizontalOffsetBase = 0f;
        horizontalOffsetBase = (pagerWidth - pagerWidth * CENTER_PAGE_SCALE) / 2 / offscreenPageLimit + Utils.dip2px(BaseApplication.context, 30);
        if (position >= offscreenPageLimit || position <= -1) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }

        if (position >= 0) {
            float translationX = (horizontalOffsetBase - view.getWidth()) * position;
            view.setTranslationX(translationX);
        }
        if (position > -1 && position < 0) {
            float rotation = position * 30;
            view.setRotation(rotation);
            view.setAlpha((position * position * position + 1));
        } else if (position > offscreenPageLimit - 1) {
            view.setAlpha((float) (1 - position + Math.floor(position)));
        } else {
            view.setRotation(0);
            view.setAlpha(1);
        }
        if (position == 0) {
            view.setScaleX(CENTER_PAGE_SCALE);
            view.setScaleY(CENTER_PAGE_SCALE);
        } else {
            float scaleFactor = Math.min(CENTER_PAGE_SCALE - position * 0.1f, CENTER_PAGE_SCALE);
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
        }

        // test code: view初始化时，设置了tag
        String tag = (String) view.getTag();
        ViewCompat.setElevation(view, (offscreenPageLimit - position) * 5);
    }
}
