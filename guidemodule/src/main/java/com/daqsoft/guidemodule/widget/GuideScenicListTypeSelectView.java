package com.daqsoft.guidemodule.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;

import com.daqsoft.guidemodule.scenicList.GuideScenicListViewModel;
import com.daqsoft.guidemodule.R;
import com.daqsoft.guidemodule.databinding.GuideLayoutScenicTypeSelectBinding;
import com.daqsoft.provider.bean.ResourceTypeLabel;
import com.daqsoft.provider.bean.ValueKeyBean;
import com.daqsoft.provider.service.GaoDeLocation;
import com.daqsoft.provider.view.ListPopupWindow;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

/**
 * @Description Guide景区列表条件选择器
 * @ClassName GuideScenicListTypeSelectView
 * @Author Wongxd
 * @Time 2020/4/3 9:41
 */
public class GuideScenicListTypeSelectView extends LinearLayout {


    private FragmentActivity fragmentActivity;

    private GuideLayoutScenicTypeSelectBinding mBinding;

    private String selfLat = "", selfLon = "";

    private boolean useHot = false, useDistance = false;

    public void setModel(GuideScenicListViewModel model, FragmentActivity activity) {
        this.model = model;
        this.fragmentActivity = activity;
    }

    private GuideScenicListViewModel model;

    private GuideSecondSelectPopupWindow secondSelectPopupWindow = null;

    public void setOnTypeSelectListener(OnTypeSelectListener mOnTypeSelectListener) {
        this.mOnTypeSelectListener = mOnTypeSelectListener;
    }

    private OnTypeSelectListener mOnTypeSelectListener;

    public GuideScenicListTypeSelectView(Context context) {
        super(context);
        init(context);
    }

    public GuideScenicListTypeSelectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GuideScenicListTypeSelectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressLint("CheckResult")
    public void init(final Context context) {


        mBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.guide_layout_scenic_type_select,
                null,
                false);
        addView(mBinding.getRoot());


        secondSelectPopupWindow = GuideSecondSelectPopupWindow.getInstance(context, true,
                new GuideSecondSelectPopupWindow.WindowDataBack() {
                    @Override
                    public void select(HashMap<String, String> item) {
                        if (mOnTypeSelectListener != null) {
                            mOnTypeSelectListener.onTypesSelected(item, useHot, useDistance);
                        }
                    }
                });


        RxView.clicks(mBinding.llHot)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {

                        useDistance = false;
                        mBinding.ivDistance.setRotation(0f);

                        if (useHot) {
                            mBinding.ivHot.setRotation(0f);
                        } else {
                            mBinding.ivHot.setRotation(180f);
                        }
                        useHot = !useHot;

                        if (mOnTypeSelectListener != null) {
                            mOnTypeSelectListener.onHotAndDistanceChanged(useHot, useDistance);
                        }

                        if (secondSelectPopupWindow != null) {
                            secondSelectPopupWindow.dismiss();
                        }
                    }
                });

        RxView.clicks(mBinding.tvType)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object resourceTypeLabel) throws Exception {
                        if (secondSelectPopupWindow != null) {
                            secondSelectPopupWindow.show(mBinding.tvType);
                            // 设置箭头向上
                        }
                    }
                });


        RxView.clicks(mBinding.llDistance)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object n) throws Exception {
                        useHot = false;
                        mBinding.ivHot.setRotation(0f);

                        if (useDistance) {
                            mBinding.ivDistance.setRotation(0f);
                        } else {
                            mBinding.ivDistance.setRotation(180f);
                        }
                        useDistance = !useDistance;

                        if (mOnTypeSelectListener != null) {
                            mOnTypeSelectListener.onHotAndDistanceChanged(useHot, useDistance);
                        }

                        if (secondSelectPopupWindow != null) {
                            secondSelectPopupWindow.dismiss();
                        }
                    }
                });

    }


    public void getData(ArrayList<ResourceTypeLabel> firstData,
                        ArrayList<ResourceTypeLabel> secondData) {
        secondSelectPopupWindow.setFirstData(firstData);
        secondSelectPopupWindow.addSecencData(secondData);
        secondSelectPopupWindow.setSecendData();

        model.getSelectLabels().observe(fragmentActivity, new Observer<List<List<ResourceTypeLabel>>>() {
            @Override
            public void onChanged(List<List<ResourceTypeLabel>> lists) {
                secondSelectPopupWindow.addAll(lists);
            }
        });

    }


    public interface OnTypeSelectListener {

        /**
         * 距离  热门 状态 改变
         */
        void onHotAndDistanceChanged(boolean hot, boolean distance);


        /**
         * 分类选中
         *
         * @param item
         */
        void onTypesSelected(HashMap<String, String> item, boolean hot, boolean distance);
    }
}

