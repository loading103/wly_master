package com.daqsoft.thetravelcloudwithculture.home.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.daqsoft.baselib.utils.Utils;
import com.daqsoft.thetravelcloudwithculture.R;

/**
 * @Description 自定义的首页上拉是的Behavior
 * @ClassName TransferHeaderBehavior
 * @Author PuHua
 * @Time 2019/12/6 15:24
 * @Version 1.0
 */
public class TransferHeaderBehavior extends CoordinatorLayout.Behavior<LinearLayout> {

    /**
     * 处于中心时候原始X轴
     */
    private int mOriginalHeaderX = 0;
    /**
     * 处于中心时候原始Y轴
     */
    private int mOriginalHeaderY = 0;

    /**标题栏的高度*/
    private int childHeight = 0;
    // 计算toolbar从开始移动到最后的百分比
    float percent=-1;

    private Context mContext;

    public TransferHeaderBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, LinearLayout child, View dependency) {
        return dependency instanceof TopLineChangeScrollView;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, LinearLayout child, View dependency) {

        // 初始化高度
        if (childHeight == 0) {
            childHeight = child.getBottom() * 2;
        }
        TopLineChangeScrollView topLineChangeScrollView = (TopLineChangeScrollView) dependency;
        TextView tvSearch = child.findViewById(R.id.tv_search);
        TextView weather = child.findViewById(R.id.tv_weather);
        RecyclerView recyclerView = child.findViewById(R.id.icons);
        TextView backTop = child.findViewById(R.id.back_top);


        if (percent==-1){
            // 初始位置时全透明
            percent = 0;
            tvSearch.setText(mContext.getResources().getString(R.string.home_search_hint));
            tvSearch.setBackground(mContext.getResources().getDrawable(R.drawable.home_back_white_round_large));
            weather.setVisibility(View.VISIBLE);
            backTop.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }else{
            if (dependency.getY() >childHeight*2){
                // 到起始位置时全透明
                percent =0;
                tvSearch.setText(mContext.getResources().getString(R.string.home_search_hint));
                tvSearch.setBackground(mContext.getResources().getDrawable(R.drawable.home_back_white_round_large));
                weather.setVisibility(View.VISIBLE);
                backTop.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }else{

                tvSearch.setText(mContext.getResources().getString(R.string.home_search_hint_dis));
                tvSearch.setBackground(mContext.getResources().getDrawable(R.drawable.home_back_gray_round_large));
                if (dependency.getY()>childHeight){
                    // 根据上拉高度设置透明度
                    percent =  childHeight/dependency.getY();
                    weather.setVisibility(View.VISIBLE);
                    backTop.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                }else {
                    // 上诉其他位置之外不透明
                    weather.setVisibility(View.GONE);
                    backTop.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    percent = 1;
                }
            }
        }

        // 计算alpha通道值
        float alpha = percent * 255;

        //设置背景颜色
        child.setBackgroundColor(Color.argb((int) alpha, 255, 255, 255));

        return true;
    }
}
