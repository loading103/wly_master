package com.daqsoft.thetravelcloudwithculture.home.view.gallery;

import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import androidx.viewpager.widget.ViewPager;

import com.daqsoft.thetravelcloudwithculture.R;

/**
 * @des viewPager的动画
 * @author PuHua
 * @Date 2019/12/9 13:54
 */
public class BranchScalePageTransformer implements ViewPager.PageTransformer {

    private static final float MAX_SCALE = 1.0f;
    private static final float MIN_SCALE = 1.0f;



    @Override
    public void transformPage(View view, float position) {
        if (position < -1) {
            position = -1;
            view.setAlpha(0);
        } else if (position <= 0) {
            view.setAlpha(1+position);
        }
        else if (position > 1) {
            position = 1;
        }

        float tempScale = position < 0 ? 1 + position : 1 - position;
        float slope = (MAX_SCALE - MIN_SCALE) / 1;
        float scaleValue = MIN_SCALE + tempScale * slope;

       ImageView imageView =  view.findViewById(R.id.image);
        float pivotY = imageView.getHeight();
        imageView.setPivotY(pivotY);
        imageView.setScaleY(scaleValue);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            view.getParent().requestLayout();
        }
    }
}
