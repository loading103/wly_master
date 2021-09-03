package com.daqsoft.travelCultureModule.clubActivity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;

import com.daqsoft.mainmodule.R;


public class ClubHeaderBehavior extends CoordinatorLayout.Behavior<ConstraintLayout> {
    private Context mContext;

    View ll_club_info;//介绍
    View ll_club_person;//成员
    View ll_club_activity;//活动
    View ll_club_zixun;//咨询
    View ll_club_pinglun;//评论

    View desView;
    View desView2;
    View desView3;
    View info;
    View person;
    View activity;
    View zixun;
    View pinglun;

    View info1;
    View person1;
    View activity1;
    View zixun1;
    View pinglun1;

    View indicator,indicator1;
    private int childHeight =0;

    public ClubHeaderBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ConstraintLayout child, View dependency) {
        ll_club_info = parent.findViewById(R.id.ll_club_info);
        ll_club_person= parent.findViewById(R.id.ll_club_person);
        ll_club_activity =parent.findViewById(R.id.ll_club_activity);
        ll_club_zixun=parent.findViewById(R.id.ll_club_zixun);
        ll_club_pinglun=parent.findViewById(R.id.ll_club_pinglun);

        desView = parent.findViewById(R.id.fl_club_detail);
        desView2 = parent.findViewById(R.id.rl_title);
        desView3 = parent.findViewById(R.id.rl_title_top);
        info = child.findViewById(R.id.tv_ci_introduce);
        person = child.findViewById(R.id.tv_ci_person);
        activity = child.findViewById(R.id.tv_ci_activity);
        zixun = child.findViewById(R.id.tv_ci_zixun);
        pinglun=child.findViewById(R.id.tv_ci_dianping);
        indicator=child.findViewById(R.id.v_ci_indicator);

        info1 = parent.findViewById(R.id.tv_ci_info1);
        person1 = parent.findViewById(R.id.tv_ci_person1);
        activity1 = parent.findViewById(R.id.tv_ci_activity1);
        zixun1 =parent.findViewById(R.id.tv_ci_zixun1);
        pinglun1=parent.findViewById(R.id.tv_ci_dianping1);
        indicator1=parent.findViewById(R.id.v_ci_indicator1);

        if (dependency instanceof NestedScrollView) {
            final NestedScrollView scrollView = (NestedScrollView) dependency;


            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!info.isSelected()){
                        scrollView. scrollTo(0,desView.getTop()+childHeight);
                    }
                }
            });
            person.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!person.isSelected()){
                        scrollView. scrollTo(0,ll_club_person.getTop()+childHeight);
                    }
                }
            });
            activity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!activity.isSelected()){
                        scrollView. scrollTo(0,ll_club_activity.getTop()+childHeight);
                    }
                }
            });
            zixun.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!zixun.isSelected()){
                        scrollView. scrollTo(0,ll_club_zixun.getTop()+childHeight);
                    }
                }
            });
            pinglun.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!pinglun.isSelected()){
                        scrollView. scrollTo(0,ll_club_pinglun.getTop()+childHeight);
                    }
                }
            });
            info1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!info1.isSelected()){
                        scrollView. scrollTo(0,desView.getTop()+childHeight);
                    }
                }
            });
            person1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!person1.isSelected()){
                        scrollView. scrollTo(0,ll_club_person.getTop()+childHeight);
                    }
                }
            });
            activity1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!activity1.isSelected()){
                        scrollView. scrollTo(0,ll_club_activity.getTop()+childHeight);
                    }
                }
            });
            zixun1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!zixun1.isSelected()){
                        scrollView. scrollTo(0,ll_club_zixun.getTop()+childHeight);
                    }
                }
            });
            pinglun1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!pinglun1.isSelected()){
                        scrollView. scrollTo(0,ll_club_pinglun.getTop()+childHeight);
                    }
                }
            });
        }
        return dependency instanceof NestedScrollView;
    }

    @Override
    public boolean onDependentViewChanged(final CoordinatorLayout parent, final ConstraintLayout child, View dependency) {
        if (childHeight == 0) {
            childHeight =desView2.getTop()-(desView3.getHeight()- desView2.getTop());
        }

        if (dependency instanceof NestedScrollView) {
            NestedScrollView scrollView = (NestedScrollView) dependency;

            scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    ConstraintSet set = new ConstraintSet();
                    set.clone(child);

                    Log.d("getY()", "scrollY=" + scrollY + "locationView=" + ll_club_info.getTop());
                    // 判断是否滑到介绍
                    if(info.getVisibility()!=View.GONE) {
                        if (scrollY >= ll_club_info.getTop() + desView2.getTop() && scrollY <= ll_club_info.getBottom() + desView2.getTop()) {
                            info.setSelected(true);
                            set.connect(indicator.getId(), ConstraintSet.LEFT, info.getId(),
                                    ConstraintSet.LEFT);
                            set.connect(indicator.getId(), ConstraintSet.RIGHT, info.getId(),
                                    ConstraintSet.RIGHT);
                            set.applyTo(child);
                        } else {
                            info.setSelected(false);
                        }

                    }
                    // 判断是否滑倒成员
                    if(person.getVisibility()!=View.GONE) {
                        if (scrollY >= ll_club_person.getTop() + desView2.getTop() && scrollY <= ll_club_person.getBottom() + desView2.getTop()) {
                            set.connect(indicator.getId(), ConstraintSet.LEFT, person.getId(),
                                    ConstraintSet.LEFT);
                            set.connect(indicator.getId(), ConstraintSet.RIGHT, person.getId(),
                                    ConstraintSet.RIGHT);
                            person.setSelected(true);
                            set.applyTo(child);
                        } else {
                            person.setSelected(false);
                        }
                    }
                    // 判断是否滑倒活动
                    if(activity.getVisibility()!=View.GONE) {
                        if (scrollY >= ll_club_activity.getTop() + desView2.getTop() && scrollY <= ll_club_activity.getBottom() + desView2.getTop()) {
                            set.connect(indicator.getId(), ConstraintSet.LEFT, activity.getId(),
                                    ConstraintSet.LEFT);
                            set.connect(indicator.getId(), ConstraintSet.RIGHT, activity.getId(),
                                    ConstraintSet.RIGHT);
                            activity.setSelected(true);
                            set.applyTo(child);
                        } else {
                            activity.setSelected(false);
                        }
                    }
                    // 判断是否滑倒咨询
                    if(zixun.getVisibility()!=View.GONE) {
                        if (scrollY >= ll_club_zixun.getTop() + desView2.getTop() && scrollY <= ll_club_zixun.getBottom() + desView2.getTop()) {
                            set.connect(indicator.getId(), ConstraintSet.LEFT, zixun.getId(),
                                    ConstraintSet.LEFT);
                            set.connect(indicator.getId(), ConstraintSet.RIGHT, zixun.getId(),
                                    ConstraintSet.RIGHT);
                            zixun.setSelected(true);
                            set.applyTo(child);
                        } else {
                            zixun.setSelected(false);
                        }
                    }
                    // 判断是否滑倒评论
                    if(pinglun.getVisibility()!=View.GONE) {
                        if (scrollY >= ll_club_pinglun.getTop() + desView2.getTop() && scrollY <= ll_club_pinglun.getBottom() + desView2.getTop()) {
                            set.connect(indicator.getId(), ConstraintSet.LEFT, pinglun.getId(),
                                    ConstraintSet.LEFT);
                            set.connect(indicator.getId(), ConstraintSet.RIGHT, pinglun.getId(),
                                    ConstraintSet.RIGHT);
                            pinglun.setSelected(true);
                            set.applyTo(child);
                        } else {
                            pinglun.setSelected(false);
                        }
                    }
                    if(scrollY>desView.getTop()+desView2.getTop()){
                        child.setVisibility(View.VISIBLE);
                    }else{
                        child.setVisibility(View.GONE);
                    }
                }
            });

//            if (dependency.getY() <= 0) {
//                child.setVisibility(View.VISIBLE);
//            } else {
//                child.setVisibility(View.GONE);
//            }
        }

        return true;
    }
}
