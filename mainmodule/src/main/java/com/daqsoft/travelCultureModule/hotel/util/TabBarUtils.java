package com.daqsoft.travelCultureModule.hotel.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daqsoft.mainmodule.R;
import com.google.android.material.appbar.AppBarLayout;

import java.util.List;

public class TabBarUtils {

    //顶部菜单布局
    private LinearLayout tabMenuView;
    //弹出菜单父布局
    private FrameLayout popupMenuViews;

    private LinearLayout appbar;

    int current_tab_position = -1;

    private Context context;

    //分割线颜色
    private int dividerColor = 0xffcccccc;
    //tab选中颜色
    private int textSelectedColor = 0xff36cd64;
    //tab未选中颜色
    private int textUnselectedColor = 0xff111111;
    //遮罩颜色
    private int maskColor = 0x88888888;
    //tab字体大小
    private int menuTextSize = 14;

    //tab选中图标
    private int menuSelectedIcon = R.mipmap.scenic_details_arrow_up;
    //tab未选中图标
    private int menuUnselectedIcon = R.mipmap.scenic_details_arrow_down;


    public void bindLayout(LinearLayout tab, FrameLayout frame, List<View> popupViews,Context context,LinearLayout appbar){
        this.context = context;
        tabMenuView = tab;
        this.appbar = appbar;
        textSelectedColor = context.getResources().getColor(R.color.colorPrimary);
//        tabMenuView.getChildAt(0).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switchMenu(tabMenuView.getChildAt(0));
//            }
//        });
        tabMenuView.getChildAt(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMenu(tabMenuView.getChildAt(2));
            }
        });
        tabMenuView.getChildAt(4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMenu(tabMenuView.getChildAt(4));
            }
        });

        popupMenuViews = frame;
        for (int i = 0; i < popupViews.size(); i++) {
            if (i==1){
                popupViews.get(i).setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) context.getResources().getDimension(R.dimen.dp_320)));
            }else{
                popupViews.get(i).setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            popupMenuViews.addView(popupViews.get(i), i);
        }
        popupMenuViews.setVisibility(View.GONE);
    }

    /**
     * 切换菜单
     *
     * @param target
     */
    private void switchMenu(View target) {
//        System.out.println(current_tab_position);
        for (int i = 0; i < tabMenuView.getChildCount(); i = i + 2) {
            if (target == tabMenuView.getChildAt(i)) {
                if (current_tab_position == i) {
                    closeMenu();
                } else {
                    if (current_tab_position == -1) {
                        popupMenuViews.setVisibility(View.VISIBLE);
//                        popupMenuViews.setAnimation(AnimationUtils.loadAnimation(context, R.anim.dd_menu_in));
                        popupMenuViews.getChildAt(i / 2).setVisibility(View.VISIBLE);
                    } else {
                        popupMenuViews.getChildAt(i / 2).setVisibility(View.VISIBLE);
                    }
                    current_tab_position = i;
                    ((TextView) tabMenuView.getChildAt(i)).setTextColor(textSelectedColor);
                    ((TextView) tabMenuView.getChildAt(i)).setCompoundDrawablesWithIntrinsicBounds(null, null,
                            context.getResources().getDrawable(menuSelectedIcon), null);
                    AppBarLayout.LayoutParams lpdd =  (AppBarLayout.LayoutParams) appbar.getLayoutParams();
                    lpdd.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP_MARGINS);
                    appbar.setLayoutParams(lpdd);
                }
            } else {
                ((TextView) tabMenuView.getChildAt(i)).setTextColor(textUnselectedColor);
                ((TextView) tabMenuView.getChildAt(i)).setCompoundDrawablesWithIntrinsicBounds(null, null,
                        context.getResources().getDrawable(menuUnselectedIcon), null);
                popupMenuViews.getChildAt(i / 2).setVisibility(View.GONE);
            }
        }
    }

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        if (current_tab_position != -1) {
            ((TextView) tabMenuView.getChildAt(current_tab_position)).setTextColor(textUnselectedColor);
            ((TextView) tabMenuView.getChildAt(current_tab_position)).setCompoundDrawablesWithIntrinsicBounds(null, null,
                    context.getResources().getDrawable(menuUnselectedIcon), null);
            popupMenuViews.setVisibility(View.GONE);
//            popupMenuViews.setAnimation(AnimationUtils.loadAnimation(context, R.anim.dd_menu_out));
            current_tab_position = -1;
        }
        AppBarLayout.LayoutParams lpdd =  (AppBarLayout.LayoutParams) appbar.getLayoutParams();
        lpdd.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL|AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        appbar.setLayoutParams(lpdd);

    }

    /**
     * 改变tab文字
     *
     * @param text
     */
    public void setTabText(String text) {
        if (current_tab_position != -1) {
            ((TextView) tabMenuView.getChildAt(current_tab_position)).setText(text);
        }
    }

    /**
     * DropDownMenu是否处于可见状态
     *
     * @return
     */
    public boolean isShowing() {
        return current_tab_position != -1;
    }

}
