package com.daqsoft.travelCultureModule.hotActivity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;

import com.daqsoft.mainmodule.R;

/**
 * @Description 活动详情的Behavior
 * @ClassName TabHeaderBehavior
 * @Author PuHua
 * @Time 2019/12/6 15:24
 * @Version 1.0
 */
public class VolunteerTabHeaderBehavior extends CoordinatorLayout.Behavior<ConstraintLayout> {


    private Context mContext;
    View locationView;

//    View ll_detail_notice;
//
//    View ll_comment;

    View llRecommend;

    View desView;
    View vIntroduce;
    View vComment;
    View vDetail;
    View vRecommend;

    View indicator;

    private int childHeight = 0;

    public VolunteerTabHeaderBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ConstraintLayout child, View dependency) {
        locationView = parent.findViewById(R.id.ll_place);

//        ll_detail_notice = parent.findViewById(R.id.ll_detail_notice);
//
//        ll_comment = parent.findViewById(R.id.ll_comment);

        llRecommend = parent.findViewById(R.id.ll_recommend);

        indicator = child.findViewById(R.id.v_indicator);


        desView = parent.findViewById(R.id.fl_introduce_detail);
        vIntroduce = child.findViewById(R.id.tv_introduce);
        vComment = child.findViewById(R.id.tv_comment);
        vDetail = child.findViewById(R.id.tv_detail);
        vRecommend = child.findViewById(R.id.tv_recommend);
        if (dependency instanceof NestedScrollView) {
            final NestedScrollView scrollView = (NestedScrollView) dependency;
            vIntroduce.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!vIntroduce.isSelected()){
                        scrollView. scrollTo(0,desView.getTop()+childHeight);
                    }
                }
            });
            vRecommend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!vRecommend.isSelected()){
                        scrollView. scrollTo(0,llRecommend.getTop()+childHeight);
                    }
                }
            });
//            vComment.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (!vComment.isSelected()){
//                        scrollView. scrollTo(0,ll_comment.getTop()+childHeight);
//                    }
//                }
//            });
//            vDetail.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (!vDetail.isSelected()){
//                        scrollView. scrollTo(0,ll_detail_notice.getTop()+childHeight);
//                    }
//                }
//            });


        }





        return dependency instanceof NestedScrollView;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, final ConstraintLayout child, View dependency) {
        if (childHeight == 0) {
            childHeight = child.getBottom();
        }

        if (dependency instanceof NestedScrollView) {
            NestedScrollView scrollView = (NestedScrollView) dependency;

//            scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//                @Override
//                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                    ConstraintSet set = new ConstraintSet();
//                    set.clone(child);
//
//                    Log.d("getY()", "scrollY=" + scrollY + "locationView=" + locationView.getTop());
//                    // 判断是否滑到场地
//                    if (scrollY >= locationView.getTop() && scrollY <= locationView.getBottom()) {
//                        vIntroduce.setSelected(true);
//                        set.connect(indicator.getId(), ConstraintSet.LEFT, vIntroduce.getId(), ConstraintSet.LEFT);
//                        set.connect(indicator.getId(), ConstraintSet.RIGHT, vIntroduce.getId(), ConstraintSet.RIGHT);
//                        set.applyTo(child);
//                    } else {
//                        vIntroduce.setSelected(false);
//
//                    }
//                    // 判断是否滑倒详情
//                    if (scrollY >= vIntroduce.getTop() && scrollY <= vIntroduce.getBottom()) {
//                        set.connect(indicator.getId(), ConstraintSet.LEFT, vDetail.getId(), ConstraintSet.LEFT);
//                        set.connect(indicator.getId(), ConstraintSet.RIGHT, vDetail.getId(), ConstraintSet.RIGHT);
//                        vDetail.setSelected(true);
//                        set.applyTo(child);
//                    } else {
//                        vDetail.setSelected(false);
//                    }
////                    // 判断是否滑倒点评
////                    if (scrollY >= ll_comment.getTop() && scrollY <= ll_comment.getBottom()) {
////                        set.connect(indicator.getId(), ConstraintSet.LEFT, vComment.getId(), ConstraintSet.LEFT);
////                        set.connect(indicator.getId(), ConstraintSet.RIGHT, vComment.getId(), ConstraintSet.RIGHT);
////                        vComment.setSelected(true);
////                        set.applyTo(child);
////                    } else {
////                        vComment.setSelected(false);
////                    }
//                    // 判断是否滑倒推荐
//                    if (scrollY >= llRecommend.getTop() && scrollY <= llRecommend.getBottom()) {
//                        set.connect(indicator.getId(), ConstraintSet.LEFT, vRecommend.getId(), ConstraintSet.LEFT);
//                        set.connect(indicator.getId(), ConstraintSet.RIGHT, vRecommend.getId(), ConstraintSet.RIGHT);
//                        vRecommend.setSelected(true);
//                        set.applyTo(child);
//                    } else {
//                        vRecommend.setSelected(false);
////                        if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())){
////                            vRecommend.setSelected(true);
////                        }else{
////                            vRecommend.setSelected(false);
////                        }
//                    }
//
//                }
//            });


            if (dependency.getY() <= 0) {

                child.setVisibility(View.VISIBLE);
            } else {
                child.setVisibility(View.GONE);
            }
        }
        Log.d("getY()", "class=" + dependency.getClass());


        return false;
    }
}
