package com.daqsoft.travelCultureModule.redblack.adapter

import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.mainmodule.R
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.ZARouterPath
import com.daqsoft.travelCultureModule.redblack.bean.AreaListBean
import com.daqsoft.travelCultureModule.redblack.bean.AreaListBeanItem
import kotlin.math.roundToInt

class RedBlackAreaListAdapter : BaseQuickAdapter<AreaListBeanItem, BaseViewHolder> {

    constructor() : super( R.layout.item_my_redblack_list )


    override fun convert(helper: BaseViewHolder, bean: AreaListBeanItem?) {
        setCommonData(helper, bean)
    }

    /**
     * 如果是景区地区公共部分其他的排行
     */
    private fun setCommonData(helper: BaseViewHolder, bean: AreaListBeanItem?) {
        when (helper.adapterPosition) {
            1 -> {
                helper.getView<ImageView>(R.id.iv_tag).visibility = View.VISIBLE
                helper.getView<TextView>(R.id.iv_tag1).visibility = View.INVISIBLE
                helper.getView<ImageView>(R.id.iv_tag).setImageResource(R.mipmap.rank_area_tag_2nd)
            }
            2 -> {
                helper.getView<ImageView>(R.id.iv_tag).visibility = View.VISIBLE
                helper.getView<TextView>(R.id.iv_tag1).visibility = View.INVISIBLE
                helper.getView<ImageView>(R.id.iv_tag).setImageResource(R.mipmap.rank_area_tag_3rd)
            }
            else -> {
                helper.getView<ImageView>(R.id.iv_tag).visibility = View.INVISIBLE
                helper.getView<TextView>(R.id.iv_tag1).visibility = View.VISIBLE
                helper.getView<TextView>(R.id.iv_tag1).text = "TOP" + (helper.adapterPosition + 1)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            bean?.totalAvg?.let {
                helper.getView<RatingBar>(R.id.rating_bar_des).rating = it.toFloat()
            }
        }

        helper.getView<TextView>(R.id.tv_area_score).text=bean?.scenic.toString()
        helper.getView<TextView>(R.id.tv_hotel_score).text=bean?.hotel.toString()
        helper.getView<TextView>(R.id.tv_food_score).text=bean?.dining.toString()
        helper.getView<TextView>(R.id.tv_njl_score).text=bean?.agr.toString()
        helper.getView<TextView>(R.id.tv_area).text=bean?.regionName
        helper.getView<TextView>(R.id.tv_score).text="综合评分 ：${bean?.totalAvg}"
        Glide.with(mContext)
            .load(bean?.image)
            .placeholder(R.mipmap.placeholder_img_fail_240_180)
            .into(helper.getView(R.id.iv_content))

        helper.getView<ConstraintLayout>(R.id.sl_area).setOnClickListener{
            ARouter.getInstance().build(ZARouterPath.RED_BLACK_LIST)
                .withString("type","CONTENT_TYPE_SCENERY")
                .withString("itemTitle",bean?.regionName)
                .withString("itemRegion",bean?.region)
                .navigation()
        }
        helper.getView<ConstraintLayout>(R.id.sl_hotel).setOnClickListener{
            ARouter.getInstance().build(ZARouterPath.RED_BLACK_LIST)
                .withString("type","CONTENT_TYPE_HOTEL")
                .withString("itemTitle",bean?.regionName)
                .withString("itemRegion",bean?.region)
                .navigation()
        }
        helper.getView<ConstraintLayout>(R.id.sl_food).setOnClickListener{
            ARouter.getInstance().build(ZARouterPath.RED_BLACK_LIST)
                .withString("type","CONTENT_TYPE_RESTAURANT")
                .withString("itemTitle",bean?.regionName)
                .withString("itemRegion",bean?.region)
                .navigation()
        }
        helper.getView<ConstraintLayout>(R.id.sl_njl).setOnClickListener{
            ARouter.getInstance().build(ZARouterPath.RED_BLACK_LIST)
                .withString("type","CONTENT_TYPE_AGRITAINMENT")
                .withString("itemTitle",bean?.regionName)
                .withString("itemRegion",bean?.region)
                .navigation()
        }
    }

    /**
     * 如果是地区排行
     */
    private fun setIsAreaData(helper: BaseViewHolder, bean: AreaListBeanItem?) {
        helper.getView<ConstraintLayout>(R.id.ll_root).setOnClickListener {
            if (AppUtils.isLogin()) {
//                var intent = Intent(mContext, RedBlackWriteCommitActivity::class.java)
//                mContext?.startActivity(intent)
            } else {
                ToastUtils.showMessage("该操作需要登录，请先登录")
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()

            }
        }
    }
}