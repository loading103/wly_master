package com.daqsoft.provider.view.convenientbanner;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daqsoft.provider.R;
import com.daqsoft.provider.view.convenientbanner.adapter.CBPageAdapter;
import com.daqsoft.provider.view.convenientbanner.helper.CBLoopScaleHelper;
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator;
import com.daqsoft.provider.view.convenientbanner.holder.VideoImageHolder;
import com.daqsoft.provider.view.convenientbanner.listener.CBPageChangeListener;
import com.daqsoft.provider.view.convenientbanner.listener.OnItemClickListener;
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener;
import com.daqsoft.provider.view.convenientbanner.view.CBLoopViewPager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 * 页面翻转控件，极方便的广告栏
 * 支持无限循环，自动翻页，翻页特效
 *
 * @author Sai 支持自动翻页
 */
public class ConvenientBanner<T> extends RelativeLayout {
    private List<T> mDatas;
    private int[] page_indicatorId = new int[2];
    private ArrayList<ImageView> mPointViews = new ArrayList<ImageView>();
    private CBPageAdapter pageAdapter;
    private CBLoopViewPager viewPager;
    private ViewGroup loPageTurningPoint;
    private long autoTurningTime = -1;
    private boolean turning;
    private boolean canTurn = false;
    private boolean canLoop = true;
    private CBLoopScaleHelper cbLoopScaleHelper;
    private CBPageChangeListener pageChangeListener;
    private OnPageChangeListener onPageChangeListener;
    private AdSwitchTask adSwitchTask;
    private LinearLayoutManager linearLayoutManager;

    public enum PageIndicatorAlign {
        ALIGN_PARENT_LEFT, ALIGN_PARENT_RIGHT, CENTER_HORIZONTAL
    }

    public ConvenientBanner(Context context) {
        super(context);
        init(context);
    }

    public ConvenientBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ConvenientBanner);
        canLoop = a.getBoolean(R.styleable.ConvenientBanner_canLoop, true);
        autoTurningTime = a.getInteger(R.styleable.ConvenientBanner_autoTurningTime, -1);
        a.recycle();
        init(context);
    }

    private void init(Context context) {
        View hView = LayoutInflater.from(context).inflate(
                R.layout.include_viewpager, this, true);
        viewPager = (CBLoopViewPager) hView.findViewById(R.id.cbLoopViewPager);
        loPageTurningPoint = (ViewGroup) hView
                .findViewById(R.id.loPageTurningPoint);
        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        viewPager.setLayoutManager(linearLayoutManager);
        cbLoopScaleHelper = new CBLoopScaleHelper();
        adSwitchTask = new AdSwitchTask(this);
        page_indicatorId[0] = R.mipmap.index_icon_lunbo_normal;
        page_indicatorId[1] = R.mipmap.index_icon_lunbo_selected;
    }

    public ConvenientBanner setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        viewPager.setLayoutManager(layoutManager);
        return this;
    }

    public ConvenientBanner setPages(CBViewHolderCreator holderCreator, List<T> datas) {
        this.mDatas = datas;
        pageAdapter = new CBPageAdapter(holderCreator, mDatas, canLoop);
        viewPager.setAdapter(pageAdapter);
        viewPager.setFocusable(false);
        if (page_indicatorId != null)
            setPageIndicator(page_indicatorId);

        cbLoopScaleHelper.setFirstItemPos(canLoop ? mDatas.size() : 0);
        cbLoopScaleHelper.attachToRecyclerView(viewPager);

        return this;
    }

    public ConvenientBanner setCanLoop(boolean canLoop) {
        this.canLoop = canLoop;
        pageAdapter.setCanLoop(canLoop);
        notifyDataSetChanged();
        return this;
    }

    public boolean isCanLoop(boolean b) {
        return canLoop;
    }


    /**
     * 通知数据变化
     */
    public void notifyDataSetChanged() {
        viewPager.getAdapter().notifyDataSetChanged();
        if (page_indicatorId != null)
            setPageIndicator(page_indicatorId);
        cbLoopScaleHelper.setCurrentItem(canLoop ? mDatas.size() : 0);
    }

    /**
     * 设置底部指示器是否可见
     *
     * @param visible
     */
    public ConvenientBanner setPointViewVisible(boolean visible) {
        loPageTurningPoint.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * 底部指示器资源图片
     *
     * @param pageIndicatorId
     */
    public ConvenientBanner setPageIndicator(int[] pageIndicatorId) {
        loPageTurningPoint.removeAllViews();
        mPointViews.clear();
        if (pageIndicatorId != null && pageIndicatorId.length == 2) {
            this.page_indicatorId = page_indicatorId;
        }
        if (mDatas == null) return this;
        for (int count = 0; count < mDatas.size(); count++) {
            // 翻页指示的点
            ImageView pointView = new ImageView(getContext());
            pointView.setPadding(5, 0, 5, 0);
            if (cbLoopScaleHelper.getFirstItemPos() % mDatas.size() == count)
                pointView.setImageResource(page_indicatorId[1]);
            else
                pointView.setImageResource(page_indicatorId[0]);
            mPointViews.add(pointView);
            loPageTurningPoint.addView(pointView);
        }
        pageChangeListener = new CBPageChangeListener(mPointViews,
                page_indicatorId);
        cbLoopScaleHelper.setOnPageChangeListener(pageChangeListener);
        if (onPageChangeListener != null)
            pageChangeListener.setOnPageChangeListener(onPageChangeListener);

        return this;
    }

    public OnPageChangeListener getOnPageChangeListener() {
        return onPageChangeListener;
    }

    /**
     * 设置翻页监听器
     *
     * @param onPageChangeListener
     * @return
     */
    public ConvenientBanner setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
        //如果有默认的监听器（即是使用了默认的翻页指示器）则把用户设置的依附到默认的上面，否则就直接设置
        if (pageChangeListener != null)
            pageChangeListener.setOnPageChangeListener(onPageChangeListener);
        else cbLoopScaleHelper.setOnPageChangeListener(onPageChangeListener);
        return this;
    }

    /**
     * 监听item点击
     *
     * @param onItemClickListener
     */
    public ConvenientBanner setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        if (onItemClickListener == null) {
            pageAdapter.setOnItemClickListener(null);
            return this;
        }
        pageAdapter.setOnItemClickListener(onItemClickListener);
        return this;
    }

    /**
     * 获取当前页对应的position
     *
     * @return
     */
    public int getCurrentItem() {
        return cbLoopScaleHelper.getRealCurrentItem();
    }

    /**
     * 设置当前页对应的position
     *
     * @return
     */
    public ConvenientBanner setCurrentItem(int position, boolean smoothScroll) {
        cbLoopScaleHelper.setCurrentItem(canLoop ? mDatas.size() + position : position, smoothScroll);
        return this;
    }

    /**
     * 设置第一次加载当前页对应的position
     * setPageIndicator之前使用
     *
     * @return
     */
    public ConvenientBanner setFirstItemPos(int position) {
        cbLoopScaleHelper.setFirstItemPos(canLoop ? mDatas.size() + position : position);
        return this;
    }

    /**
     * 指示器的方向
     *
     * @param align 三个方向：居左 （RelativeLayout.ALIGN_PARENT_LEFT），居中 （RelativeLayout.CENTER_HORIZONTAL），居右 （RelativeLayout.ALIGN_PARENT_RIGHT）
     * @return
     */
    public ConvenientBanner setPageIndicatorAlign(PageIndicatorAlign align) {
        LayoutParams layoutParams = (LayoutParams) loPageTurningPoint.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, align == PageIndicatorAlign.ALIGN_PARENT_LEFT ? RelativeLayout.TRUE : 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, align == PageIndicatorAlign.ALIGN_PARENT_RIGHT ? RelativeLayout.TRUE : 0);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, align == PageIndicatorAlign.CENTER_HORIZONTAL ? RelativeLayout.TRUE : 0);
        loPageTurningPoint.setLayoutParams(layoutParams);
        return this;
    }

    /**
     * 指示器的方向
     *
     * @param top 上边间距
     * @param left 左边间距
     * @param right 右边边间距
     * @param bottom 下边间距
     *
     * @return
     */
    public ConvenientBanner setPageIndicatorPadding(int left,int top,int right,int bottom) {
        loPageTurningPoint.setPadding(left,top,right,bottom);
        return this;
    }

    public CBLoopViewPager getViewPager(){
        return  viewPager;
    }

    /***
     * 是否开启了翻页
     * @return
     */
    public boolean isTurning() {
        return turning;
    }

    /***
     * 开始翻页
     * @param autoTurningTime 自动翻页时间
     * @return
     */
    public ConvenientBanner startTurning(long autoTurningTime) {
        if (autoTurningTime < 0) return this;
        //如果是正在翻页的话先停掉
        if (turning) {
            stopTurning();
        }
        //设置可以翻页并开启翻页
        canTurn = true;
        this.autoTurningTime = autoTurningTime;
        turning = true;
        postDelayed(adSwitchTask, autoTurningTime);
        return this;
    }

    public ConvenientBanner startTurning() {
        startTurning(autoTurningTime);
        return this;
    }


    public void stopTurning() {
        turning = false;
        if(adSwitchTask!=null)
        removeCallbacks(adSwitchTask);
    }


    //触碰控件的时候，翻页应该停止，离开的时候如果之前是开启了翻页的话则重新启动翻页
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        int action = ev.getAction();
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_OUTSIDE) {
            // 开始翻页
            if (canTurn) startTurning(autoTurningTime);
        } else if (action == MotionEvent.ACTION_DOWN) {
            // 停止翻页
            if (canTurn) stopTurning();
        }
        return super.dispatchTouchEvent(ev);
    }

    static class AdSwitchTask implements Runnable {

        private final WeakReference<ConvenientBanner> reference;

        AdSwitchTask(ConvenientBanner convenientBanner) {
            this.reference = new WeakReference<ConvenientBanner>(convenientBanner);
        }

        @Override
        public void run() {
            ConvenientBanner convenientBanner = reference.get();

            if (convenientBanner != null) {
                if (convenientBanner.viewPager != null && convenientBanner.turning) {
                    int page = convenientBanner.cbLoopScaleHelper.getCurrentItem() + 1;
                    convenientBanner.cbLoopScaleHelper.setCurrentItem(page, true);
                    convenientBanner.postDelayed(convenientBanner.adSwitchTask, convenientBanner.autoTurningTime);
                }
            }
        }
    }

    public void pauseVideoPlayer() {
        if (pageAdapter != null) {
            pageAdapter.notifyItemRangeChanged(0, pageAdapter.getItemCount(), "pauseVideoPlayer");
        }
    }

    public void stopVideoPlayer() {
        if (pageAdapter != null) {
            pageAdapter.notifyItemRangeChanged(0, pageAdapter.getItemCount(), "stopVideoPlayer");
        }
    }

    public Boolean onBackPress() {
        try {
            if (mDatas == null || mDatas.size() <= 0) {
                return false;
            }
            View itemView = linearLayoutManager.findViewByPosition(0);
            if (itemView == null)
                return false;
            if (itemView.getTag() instanceof VideoImageHolder) {
                VideoImageHolder holder = (VideoImageHolder) itemView.getTag();
                if (holder != null && holder.getMVideoPlayer() != null) {
                    return holder.getMVideoPlayer().onBackPressed();
                }
            }

        } catch (Exception e) {

        }
        return false;

    }
//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        startTurning(autoTurningTime);
//    }

}
