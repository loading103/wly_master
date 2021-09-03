package com.daqsoft.thetravelcloudwithculture.home.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.daqsoft.baselib.base.BaseApplication;
import com.daqsoft.baselib.utils.Utils;
import com.daqsoft.provider.bean.CommonTemlate;
import com.daqsoft.provider.view.BottomNavigationItemView;

import com.daqsoft.provider.bean.HomeMenu;
import com.daqsoft.thetravelcloudwithculture.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 可选择内部控件的linearlayout
 * @ClassName SelectLinearLayout
 * @Author PuHua
 * @Time 2019/12/8 17:04
 * @Version 1.0
 */
public class SelectLinearLayout extends LinearLayout {

    private Context mContext;
    /**
     * 存放当前所有子项
     */
//    private List<NavigationItemView> navigationItemViews = new ArrayList<>();
    private List<BottomNavigationItemView> navigationItemViews = new ArrayList<>();

    /**
     * 已选中的下标
     */
    private int currentSelectPostion = 0;

    public void setCurrentSelectPostion(int currentSelectPostion) {
        this.currentSelectPostion = currentSelectPostion;
        changeTab();
    }

    public OnPageSelect getOnPageSelect() {
        return onPageSelect;
    }


    public void setOnPageSelect(OnPageSelect onPageSelect) {
        this.onPageSelect = onPageSelect;
    }

    /**
     * 监听页面选中
     */
    private OnPageSelect onPageSelect;

    public SelectLinearLayout(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public SelectLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public SelectLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        this.setOrientation(HORIZONTAL);
//        setClipChildren(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        for (int i = 0; i < getChildCount(); i++) {
//            if (getChildAt(i) instanceof NavigationItemView) {
//                final NavigationItemView navigationItemView = (NavigationItemView) getChildAt(i);
//                final int finalI = i;
//                navigationItemView.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (!navigationItemView.isSelect()) {
//                            navigationItemViews.get(currentSelectPostion).select(false);
//                            navigationItemView.select(true);
//                            currentSelectPostion = finalI;
//
//                            if (onPageSelect != null) {
//                                onPageSelect.onPageSelect(finalI);
//                            }
//                        }
//                    }
//                });
//                if (navigationItemView.isSelect()) {
//                    currentSelectPostion = i;
//                }
//                navigationItemViews.add(navigationItemView);
//            }
//            if (getChildAt(i) instanceof BottomNavigationItemView) {
//                final BottomNavigationItemView navigationItemView = (BottomNavigationItemView) getChildAt(i);
//                final int finalI = i;
//                navigationItemView.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (!navigationItemView.getSelectFlag()) {
//                            navigationItemViews.get(currentSelectPostion).setSelectFlag(false);
//                            navigationItemView.setSelectFlag(true);
//                            currentSelectPostion = finalI;
//
//                            if (onPageSelect != null) {
//                                onPageSelect.onPageSelect(finalI);
//                            }
//                        }
//                    }
//                });
//                if (navigationItemView.getSelectFlag()) {
//                    currentSelectPostion = i;
//                }
//                navigationItemViews.add(navigationItemView);
//            }
//        }
    }

    private void changeTab() {
        navigationItemViews.clear();
        for (int i = 0; i < getChildCount(); i++) {
//            if (getChildAt(i) instanceof NavigationItemView) {
//                final NavigationItemView navigationItemView = (NavigationItemView) getChildAt(i);
//                LinearLayout.LayoutParams params = null;
//                if (i == currentSelectPostion) {
//                    navigationItemView.setSelect(true);
//                    params = new LinearLayout.LayoutParams(0, (int) Utils.dip2px(mContext, 64), 1);
//                    params.gravity = Gravity.BOTTOM;
//                } else {
//                    navigationItemView.setSelect(false);
//                    params = new LinearLayout.LayoutParams(0, (int) Utils.dip2px(mContext, 50), 1);
//                    params.gravity = Gravity.BOTTOM;
//                }
//                navigationItemView.setLayoutParams(params);
//                navigationItemViews.add(navigationItemView);
//            }
            if (getChildAt(i) instanceof BottomNavigationItemView) {
                final BottomNavigationItemView navigationItemView = (BottomNavigationItemView) getChildAt(i);
                LinearLayout.LayoutParams params = null;
                if (i == currentSelectPostion) {
                    navigationItemView.setSelectFlag(true);
                    params = new LinearLayout.LayoutParams(0, (int) BaseApplication.context.getResources().getDimension(R.dimen.dp_50), 1);
                    params.gravity = Gravity.CENTER;
                } else {
                    navigationItemView.setSelectFlag(false);
                    params = new LinearLayout.LayoutParams(0, (int) Utils.dip2px(mContext, 50), 1);
                    params.gravity = Gravity.CENTER;
                }
                navigationItemView.setLayoutParams(params);
                navigationItemViews.add(navigationItemView);
            }
        }
    }

    /**
     * 添加菜单项
     *
     * @param menus
     */
    public void addMenus(List<HomeMenu> menus) {
        for (int i = 0; i < menus.size(); i++) {
//            HomeMenu menu = menus.get(i);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, (int) Utils.dip2px(mContext, 50), 1);
//            params.gravity = Gravity.BOTTOM;
//            NavigationItemView navigationItemView = new NavigationItemView(mContext);
//            navigationItemView.setBigImageUrl(menu.getSelectIcon());
//            navigationItemView.setSmallImageUrl(menu.getUnSelectIcon());
//            navigationItemView.setText(menu.getName());
//            if (i == 0) {
//                navigationItemView.setSelect(true);
//            } else {
//                navigationItemView.setSelect(false);
//            }
//            addView(navigationItemView, params);

            HomeMenu menu = menus.get(i);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,(int) BaseApplication.context.getResources().getDimension(R.dimen.dp_50), 1);
            params.gravity = Gravity.CENTER;
            BottomNavigationItemView navigationItemView = new BottomNavigationItemView(mContext);

            String url = "";
            if (menu.getGif() == null || menu.getGif().isEmpty()){
                url = menu.getSelectIcon();
            }else {
                url = menu.getGif();
            }
            navigationItemView.setSelectedImage(url);
            navigationItemView.setUnselectedImage(menu.getUnSelectIcon());
            navigationItemView.setText(menu.getName());
            int finalI = i;
            navigationItemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!navigationItemView.getSelectFlag()) {
                        navigationItemViews.get(currentSelectPostion).setSelectFlag(false);
                        navigationItemView.setSelectFlag(true);
                        currentSelectPostion = finalI;

                        if (onPageSelect != null) {
                            onPageSelect.onPageSelect(finalI);
                        }
                    }
                }
            });
            if (i == 0) {
                navigationItemView.setSelectFlag(true);
            } else {
                navigationItemView.setSelectFlag(false);
            }
            addView(navigationItemView, params);
            navigationItemViews.add(navigationItemView);
        }
        postInvalidate();
    }
    /**
     * 添加菜单项
     *
     * @param menus
     */
    public void addNewMenus(List<CommonTemlate> menus) {
        for (int i = 0; i < menus.size(); i++) {
//            HomeMenu menu = menus.get(i);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, (int) Utils.dip2px(mContext, 50), 1);
//            params.gravity = Gravity.BOTTOM;
//            NavigationItemView navigationItemView = new NavigationItemView(mContext);
//            navigationItemView.setBigImageUrl(menu.getSelectIcon());
//            navigationItemView.setSmallImageUrl(menu.getUnSelectIcon());
//            navigationItemView.setText(menu.getName());
//            if (i == 0) {
//                navigationItemView.setSelect(true);
//            } else {
//                navigationItemView.setSelect(false);
//            }
//            addView(navigationItemView, params);

            CommonTemlate menu = menus.get(i);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,(int) BaseApplication.context.getResources().getDimension(R.dimen.dp_50), 1);
            params.gravity = Gravity.CENTER;
            BottomNavigationItemView navigationItemView = new BottomNavigationItemView(mContext);

            String url = menu.getSelectIcon();
//            if (menu.getGif() == null || menu.getGif().isEmpty()){
//                url = menu.getSelectIcon();
//            }else {
//                url = menu.getGif();
//            }
            navigationItemView.setSelectedImage(url);
            navigationItemView.setUnselectedImage(menu.getUnSelectIcon());
            navigationItemView.setText(menu.getName());
            int finalI = i;
            navigationItemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!navigationItemView.getSelectFlag()) {
                        navigationItemViews.get(currentSelectPostion).setSelectFlag(false);
                        navigationItemView.setSelectFlag(true);
                        currentSelectPostion = finalI;

                        if (onPageSelect != null) {
                            onPageSelect.onPageSelect(finalI);
                        }
                    }
                }
            });
            if (i == 0) {
                navigationItemView.setSelectFlag(true);
            } else {
                navigationItemView.setSelectFlag(false);
            }
            addView(navigationItemView, params);
            navigationItemViews.add(navigationItemView);
        }
        postInvalidate();
    }
    public interface OnPageSelect {
        void onPageSelect(int pos);
    }
}
