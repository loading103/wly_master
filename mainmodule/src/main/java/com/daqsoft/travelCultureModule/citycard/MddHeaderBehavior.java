package com.daqsoft.travelCultureModule.citycard;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;

import com.daqsoft.mainmodule.R;


public class MddHeaderBehavior extends CoordinatorLayout.Behavior<ConstraintLayout> {
    private Context mContext;

    View ll_mdd_city;//目的地城市
    View ll_mdd_dqx;//天府旅游名县

    View desView;

    View city;
    View dqx;

    View indicator;
    private int childHeight =10;

    public MddHeaderBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ConstraintLayout child, View dependency) {
        ll_mdd_city = parent.findViewById(R.id.ll_mdd_city);
        ll_mdd_dqx= parent.findViewById(R.id.ll_mdd_dqx);


        desView = parent.findViewById(R.id.fl_mdd_detail);

        city = child.findViewById(R.id.tv_mdd_city);
        dqx = child.findViewById(R.id.tv_mdd_dqx);


        indicator=child.findViewById(R.id.v_ci_indicator);
        if (dependency instanceof NestedScrollView) {
            final NestedScrollView scrollView = (NestedScrollView) dependency;
            city.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!city.isSelected()){
                        scrollView. scrollTo(0,desView.getTop()+childHeight);
                    }
                }
            });
            dqx.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!dqx.isSelected()){
                        scrollView. scrollTo(0,ll_mdd_dqx.getTop()+childHeight);
                    }
                }
            });

        }
        return dependency instanceof NestedScrollView;
    }

    @Override
    public boolean onDependentViewChanged(final CoordinatorLayout parent, final ConstraintLayout child, View dependency) {
        if (childHeight == 0) {
            childHeight = child.getBottom();
        }

        if (dependency instanceof NestedScrollView) {
            NestedScrollView scrollView = (NestedScrollView) dependency;

            scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    ConstraintSet set = new ConstraintSet();
                    set.clone(child);


                    // 判断是否滑到介绍
                    if(ll_mdd_city.getVisibility()!=View.GONE) {
                        if (scrollY >= ll_mdd_city.getTop() && scrollY <= ll_mdd_city.getBottom()) {
                            city.setSelected(true);
                            set.connect(indicator.getId(), ConstraintSet.LEFT, city.getId(),
                                    ConstraintSet.LEFT);
                            set.connect(indicator.getId(), ConstraintSet.RIGHT, city.getId(),
                                    ConstraintSet.RIGHT);
                            set.applyTo(child);
                        } else {
                            city.setSelected(false);
                        }
                    }

                    // 判断是否滑倒成员
                    if(ll_mdd_dqx.getVisibility()!=View.GONE) {
                        if (scrollY >= ll_mdd_dqx.getTop() && scrollY <= ll_mdd_dqx.getBottom()) {
                            set.connect(indicator.getId(), ConstraintSet.LEFT, dqx.getId(),
                                    ConstraintSet.LEFT);
                            set.connect(indicator.getId(), ConstraintSet.RIGHT, dqx.getId(),
                                    ConstraintSet.RIGHT);
                            dqx.setSelected(true);
                            set.applyTo(child);
                        } else {
                            dqx.setSelected(false);
                        }
                    }
                    if(scrollY>ll_mdd_city.getTop()){
                        child.setVisibility(View.VISIBLE);
                    }else{
                        child.setVisibility(View.GONE);
                    }
                }

            });



        }

        return true;
    }
}
