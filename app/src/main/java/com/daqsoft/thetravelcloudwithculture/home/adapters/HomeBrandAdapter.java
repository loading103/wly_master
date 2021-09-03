package com.daqsoft.thetravelcloudwithculture.home.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.daqsoft.baselib.utils.StringUtil;
import com.daqsoft.baselib.widgets.ArcImageView;
import com.daqsoft.provider.MainARouterPath;
import com.daqsoft.provider.bean.HomeBranchBean;
import com.daqsoft.provider.utils.DividerTextUtils;
import com.daqsoft.thetravelcloudwithculture.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 用一句话来描述功能
 *
 * @author 黄熙
 * @version 1.0.0
 * @date 2020/4/9 0009
 * @since JDK 1.8
 */
public class HomeBrandAdapter extends PagerAdapter {
    /**
     * 数据集合
     */
    ArrayList<HomeBranchBean> datas = new ArrayList<>();
    /**
     * 上下文
     */
    Context context;

    public HomeBrandAdapter(Context context, ArrayList<HomeBranchBean> list) {
        this.datas = list;
        this.context = context;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_branch, null, false);
        position = position % datas.size();
        int finalPosition = position;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ARouter.getInstance().build(MainARouterPath.MAIN_BRANCH_DETAIL)
                        .withString("id", datas.get(finalPosition).getId())
                        .navigation();
            }
        });
        ArcImageView imageView = view.findViewById(R.id.image);
        TextView tvName = view.findViewById(R.id.tv_brand_name);
        TextView tvInfo = view.findViewById(R.id.tv_brand_info);
        TextView tvContent = view.findViewById(R.id.tv_brand_content);
        Glide.with(context)
                .load(StringUtil.INSTANCE.getDqImageUrl(datas.get(position).getBrandImage(),600,700))
                .centerCrop()
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .error(R.mipmap.placeholder_img_fail_240_180)
                .into(imageView);
        tvName.setText(datas.get(position).getName());
        tvInfo.setText(datas.get(position).getSlogan());
        if (datas.get(position).getRelationResourceNameStr() != null) {
            String[] scenicS = datas.get(position).getRelationResourceNameStr().split(",");
            List<String> ss = new ArrayList<String>();
            if (scenicS.length >= 3) {
                ss.add(scenicS[0]);
                ss.add(scenicS[1]);
                ss.add(scenicS[2]);
            } else {
                for (String item : scenicS
                ) {
                    ss.add(item);
                }
            }

            String sn = DividerTextUtils.INSTANCE.convertDotString(ss);
            tvContent.setText(sn);
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position,
                            Object object) {

        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }
}
