package com.daqsoft.provider.utils;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.daqsoft.baselib.utils.SPUtils;
import com.daqsoft.baselib.utils.ToastUtils;
import com.daqsoft.provider.ARouterPath;
import com.daqsoft.provider.MainARouterPath;
import com.daqsoft.provider.SPKey;
import com.daqsoft.provider.bean.MineMessageBean;

import java.util.List;

import daqsoft.com.baselib.R;

/**
 * 上下滚动的 textView
 */
public class ScrrollTextView extends LinearLayout {
    private TextView mBannerTV1;
    private TextView mBannerTV2;
    private Handler handler;
    private boolean isShow = false;
    private int startY1, endY1, startY2, endY2;
    private Runnable runnable;
    private List<MineMessageBean> list;
    private int position = 0;
    private int offsetY = 100;
    private boolean hasPostRunnable = false;

    public ScrrollTextView(Context context) {
        this(context, null);
    }

    public ScrrollTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrrollTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View view = LayoutInflater.from(context).inflate(R.layout.widget_scroll_text_layout, this);
        mBannerTV1 = view.findViewById(R.id.tv_banner1);
        mBannerTV2 = view.findViewById(R.id.tv_banner2);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                isShow = !isShow;
                if (position == list.size() - 1) {
                    position = 0;
                }

                if (isShow) {
                    mBannerTV1.setText(list.get(position++).getTitle());
                    mBannerTV2.setText(list.get(position).getTitle());
                } else {
                    mBannerTV2.setText(list.get(position++).getTitle());
                    mBannerTV1.setText(list.get(position).getTitle());
                }


                startY1 = isShow ? 0 : offsetY;
                endY1 = isShow ? -offsetY : 0;
                ObjectAnimator.ofFloat(mBannerTV1, "translationY", startY1, endY1).setDuration(300).start();

                startY2 = isShow ? offsetY : 0;
                endY2 = isShow ? 0 : -offsetY;
                ObjectAnimator.ofFloat(mBannerTV2, "translationY", startY2, endY2).setDuration(300).start();

                handler.postDelayed(runnable, 6000);
            }
        };
    }

    public List<MineMessageBean> getList() {
        return list;
    }

    public void setList(List<MineMessageBean> list) {
        this.list = list;

        //处理最后一条数据切换到第一条数据 太快的问题
        if (list.size() > 1) {
            list.add(list.get(0));
        }
    }

    public void startScroll() {
        mBannerTV1.setText(list.get(0).getTitle());
        mBannerTV1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickhandle(list.get(position));
            }
        });
        mBannerTV2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickhandle(list.get(position));
            }
        });

        if (list.size() > 1) {
            if(!hasPostRunnable) {
                hasPostRunnable = true;
                //处理第一次进入 第一条数据切换第二条 太快的问题
                handler.postDelayed(runnable,6000);
            }
        } else {
            //只有一条数据不进行滚动
            hasPostRunnable = false;
//            mBannerTV1.setText(list.get(0));
        }
    }






    private void onClickhandle(MineMessageBean bean) {
        if(TextUtils.isEmpty(bean.getMsgType())){
            return;
        }
        if (bean.getMsgType().equals("1")) {
            ARouter.getInstance()
                    .build(ARouterPath.UserModule.USER_MEASSAGE_NOTICE_DETAIL)
                    .withString("id", bean.getId())
                    .navigation();
            return;
        }
        // 活动与邀请 跳转对象：1活动；2话题；3商品；4外部链接
        if (bean.getJumpType().equals("1")) {
            if (!TextUtils.isEmpty(bean.getJumpUrl())) {
                ARouter.getInstance()
                        .build(ARouterPath.Provider.WEB_ACTIVITY)
                        .withString("mTitle", bean.getTitle())
                        .withString("html",bean.getJumpUrl())
                        .navigation();
            } else {
                ARouter.getInstance()
                        .build(MainARouterPath.MAIN_HOT_ACITITY)
                        .withString("id", bean.getRelationId())
                        .navigation();

            }
        }else if (bean.getJumpType().equals("2")) {
            ARouter.getInstance()
                    .build(MainARouterPath.MAIN_TOPIC_DETAIL)
                    .withString("id",bean.getRelationId())
                    .navigation();

        }else if (bean.getJumpType().equals("3")) {
            String shopUrl = SPUtils.getInstance().getString(SPKey.SHOP_URL, "");
            String uuid = SPUtils.getInstance().getString(SPKey.UUID);
            String token = SPUtils.getInstance().getString(SPKey.USER_CENTER_TOKEN);
            String encry = SPUtils.getInstance().getString(SPKey.ENCRYPTION);
            // 拼接跳转商品页面地址
           String url=shopUrl+"/goods/detail?&id="+bean.getRelationId()+"&unid="+uuid+"&token="+token+"&encryption="+encry;
            ARouter.getInstance()
                    .build(ARouterPath.Provider.WEB_ACTIVITY)
                    .withString("mTitle",bean.getTitle())
                    .withString("html", url)
                    .navigation();

        }else if (bean.getJumpType().equals("4")) {
            ARouter.getInstance()
                    .build(ARouterPath.Provider.WEB_ACTIVITY)
                    .withString("mTitle", bean.getTitle())
                    .withString("html", bean.getJumpUrl())
                    .navigation();

        }

    }

    public void stopScroll() {
        handler.removeCallbacks(runnable);
        hasPostRunnable = false;
    }


}