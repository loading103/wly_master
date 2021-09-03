package com.daqsoft.provider.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

/**
 * @author luoyi
 * @des 指示器
 * @Date 2020/4/24 13:35
 */

public class ViewPagerIndicatorLineView extends LinearLayout {
    /**
     * 指示器总数
     */
    private int total;
    private ArrayList<ImageView> mPointViews = new ArrayList<ImageView>();
    private int[] page_indicatorId = new int[2];

    public ViewPagerIndicatorLineView(Context context) {
        super(context);
    }

    public ViewPagerIndicatorLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewPagerIndicatorLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ViewPagerIndicatorLineView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total, int[] pageIndicatorId) {
        this.total = total;
        removeAllViews();
        mPointViews.clear();
        page_indicatorId = pageIndicatorId;
        addPageIndicator(pageIndicatorId);
    }

    private void addPageIndicator(int[] pageIndicatorId) {
        for (int count = 0; count < total; count++) {
            // 翻页指示的点
            ImageView pointView = new ImageView(getContext());
            pointView.setPadding(5, 0, 5, 0);
            if (count == 0) {
                pointView.setImageResource(pageIndicatorId[1]);
            } else {
                pointView.setImageResource(pageIndicatorId[0]);
            }
            mPointViews.add(pointView);
            addView(pointView);
        }
    }

    private void setSteps(int position) {
        for (int i = 0; i < mPointViews.size(); i++) {
            if (i == position) {
                mPointViews.get(i).setImageResource(page_indicatorId[1]);
            } else {
                mPointViews.get(i).setImageResource(page_indicatorId[0]);
            }
        }
    }

    public void binViewPager(@org.jetbrains.annotations.Nullable ViewPager viewPager) {
        if (viewPager != null) {
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    setSteps(position);
                }


                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }

}
