package com.daqsoft.travelCultureModule.citycard;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;

import com.daqsoft.mainmodule.R;
import com.daqsoft.provider.view.convenientbanner.utils.ScreenUtil;


public class CityInfoBehavior extends CoordinatorLayout.Behavior<ConstraintLayout> {
    private Context mContext;

    View ll_city_changguan;//找场馆
    View ll_city_lvyouluxian;//旅游路线
    View ll_city_hotel;//住酒店
    View ll_city_food;//吃美食
    View ll_city_story;//读故事
    View ll_boom;
    View desView;

    View changguan;
    View lvyouluxian;
    View hotel;
    View food;
    View story;

    View indicator;
    private int childHeight =10;
    private int childTop=0;
    public CityInfoBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ConstraintLayout child, View dependency) {
        ll_city_changguan = parent.findViewById(R.id.ll_city_changguan);
        ll_city_lvyouluxian= parent.findViewById(R.id.ll_city_lvyouluxian);
        ll_city_hotel =parent.findViewById(R.id.ll_city_hotel);
        ll_city_food=parent.findViewById(R.id.ll_city_food);
        ll_city_story=parent.findViewById(R.id.ll_city_story);
        ll_boom=parent.findViewById(R.id.ll_bottom);
        //desView = parent.findViewById(R.id.fl_club_detail);

        changguan = child.findViewById(R.id.tv_ci_city_cg);
        lvyouluxian = child.findViewById(R.id.tv_ci_city_lylx);
        hotel = child.findViewById(R.id.tv_ci_city_hotle);
        food = child.findViewById(R.id.tv_ci_city_food);
        story=child.findViewById(R.id.tv_ci_city_story);


        indicator=child.findViewById(R.id.v_ci_indicator);
        if (dependency instanceof NestedScrollView) {
            final NestedScrollView scrollView = (NestedScrollView) dependency;
            changguan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!changguan.isSelected()){
                        scrollView. scrollTo(0,ll_city_changguan.getTop()+childHeight);
                    }
                }
            });
            lvyouluxian.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!lvyouluxian.isSelected()){
                        scrollView. scrollTo(0,lvyouluxian.getTop()+childHeight);
                    }
                }
            });
            hotel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!hotel.isSelected()){
                        scrollView. scrollTo(0,ll_city_hotel.getTop()+childHeight);
                    }
                }
            });
            food.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!food.isSelected()){
                        scrollView. scrollTo(0,ll_city_food.getTop()+childHeight);
                    }
                }
            });
            story.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!story.isSelected()){
                        scrollView. scrollTo(0,ll_city_story.getTop()+childHeight);
                    }
                }
            });

        }

        return dependency instanceof NestedScrollView;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onDependentViewChanged(final CoordinatorLayout parent, final ConstraintLayout child, View dependency) {
        if (childHeight == 0) {
            childHeight = child.getBottom();
        }
        if(childTop==0){
            childTop=child.getBottom();
        }
        if (dependency instanceof NestedScrollView) {
            NestedScrollView scrollView = (NestedScrollView) dependency;

            final CoordinatorLayout.LayoutParams lp= new CoordinatorLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ScreenUtil.dip2px(mContext,48));
            scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    ConstraintSet set = new ConstraintSet();
                    set.clone(child);
                    if(changguan.getVisibility()!=View.GONE) {
                        if (scrollY >= ll_city_changguan.getTop() && scrollY <= ll_city_changguan.getBottom()) {
                            changguan.setSelected(true);
                            set.connect(indicator.getId(), ConstraintSet.LEFT, changguan.getId(),
                                    ConstraintSet.LEFT);
                            set.connect(indicator.getId(), ConstraintSet.RIGHT, changguan.getId(),
                                    ConstraintSet.RIGHT);
                            set.applyTo(child);
                        } else {
                            changguan.setSelected(false);
                        }
                    }else{
                        if(lvyouluxian.getVisibility()!=View.GONE){
                            set.connect(indicator.getId(), ConstraintSet.LEFT, lvyouluxian.getId(),
                                    ConstraintSet.LEFT);
                            set.connect(indicator.getId(), ConstraintSet.RIGHT, lvyouluxian.getId(),
                                    ConstraintSet.RIGHT);
                            set.applyTo(child);
                        }else{
                            if(hotel.getVisibility()!=View.GONE) {
                                set.connect(indicator.getId(), ConstraintSet.LEFT, hotel.getId(),
                                        ConstraintSet.LEFT);
                                set.connect(indicator.getId(), ConstraintSet.RIGHT, hotel.getId(),
                                        ConstraintSet.RIGHT);
                                set.applyTo(child);
                            }else{
                                if(food.getVisibility()!=View.GONE) {
                                    set.connect(indicator.getId(), ConstraintSet.LEFT, food.getId(),
                                            ConstraintSet.LEFT);
                                    set.connect(indicator.getId(), ConstraintSet.RIGHT, food.getId(),
                                            ConstraintSet.RIGHT);
                                    set.applyTo(child);
                                }else{
                                    if(story.getVisibility()!=View.GONE) {
                                        set.connect(indicator.getId(), ConstraintSet.LEFT, story.getId(),
                                                ConstraintSet.LEFT);
                                        set.connect(indicator.getId(), ConstraintSet.RIGHT, story.getId(),
                                                ConstraintSet.RIGHT);
                                        set.applyTo(child);
                                    }else{
                                        child.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }
                    }
                    if(lvyouluxian.getVisibility()!=View.GONE) {
                        if (scrollY >= ll_city_lvyouluxian.getTop() && scrollY <= ll_city_changguan.getBottom()) {
                            set.connect(indicator.getId(), ConstraintSet.LEFT, lvyouluxian.getId(),
                                    ConstraintSet.LEFT);
                            set.connect(indicator.getId(), ConstraintSet.RIGHT, lvyouluxian.getId(),
                                    ConstraintSet.RIGHT);
                            ll_city_lvyouluxian.setSelected(true);
                            set.applyTo(child);
                        } else {
                            lvyouluxian.setSelected(false);
                        }
                    }
                    if(hotel.getVisibility()!=View.GONE) {
                        if (scrollY >= ll_city_hotel.getTop() && scrollY <= ll_city_hotel.getBottom()) {
                            set.connect(indicator.getId(), ConstraintSet.LEFT, hotel.getId(),
                                    ConstraintSet.LEFT);
                            set.connect(indicator.getId(), ConstraintSet.RIGHT, hotel.getId(),
                                    ConstraintSet.RIGHT);
                            hotel.setSelected(true);
                            set.applyTo(child);
                        } else {
                            hotel.setSelected(false);
                        }
                    }
                    if(food.getVisibility()!=View.GONE) {
                        if (scrollY >= ll_city_food.getTop() && scrollY <= ll_city_food.getBottom()) {
                            set.connect(indicator.getId(), ConstraintSet.LEFT, food.getId(),
                                    ConstraintSet.LEFT);
                            set.connect(indicator.getId(), ConstraintSet.RIGHT, food.getId(),
                                    ConstraintSet.RIGHT);
                            food.setSelected(true);
                            set.applyTo(child);
                        } else {
                            food.setSelected(false);
                        }
                    }
                    if(story.getVisibility()!=View.GONE) {
                        if (scrollY >= ll_city_story.getTop() && scrollY <= ll_city_story.getBottom()) {
                            set.connect(indicator.getId(), ConstraintSet.LEFT, story.getId(),
                                    ConstraintSet.LEFT);
                            set.connect(indicator.getId(), ConstraintSet.RIGHT, story.getId(),
                                    ConstraintSet.RIGHT);
                            story.setSelected(true);
                            set.applyTo(child);
                        } else {
                            story.setSelected(false);
                        }
                    }
                    if(scrollY>=ll_city_changguan.getTop()){
                        lp.gravity=Gravity.TOP;
                        child.setLayoutParams(lp);
                    }else{
                        lp.gravity=Gravity.BOTTOM;
                        child.setLayoutParams(lp);
                    }
                    if(scrollY>0){
                        child.setVisibility(View.VISIBLE);
                    }else{
                        child.setVisibility(View.GONE);
                    }
                }
            });



//            if (dependency.getY() <= 0) {
//                child.setVisibility(View.VISIBLE);
//            } else if(dependency.getY()>0&&dependency.getY()<childTop){
//                child.setVisibility(View.GONE);
//            }else{
//                child.setVisibility(View.VISIBLE);
//            }
        }

        return true;
    }
}
