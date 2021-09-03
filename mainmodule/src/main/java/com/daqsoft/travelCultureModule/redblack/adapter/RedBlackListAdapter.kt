package com.daqsoft.travelCultureModule.redblack.adapter

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
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.ZARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.travelCultureModule.redblack.bean.AreaListBean
import com.daqsoft.travelCultureModule.redblack.bean.AreaListBeanItem
import com.daqsoft.travelCultureModule.redblack.bean.ResoureListBeanItem

class RedBlackListAdapter : BaseQuickAdapter<ResoureListBeanItem, BaseViewHolder> {
    private  var type: String? =null;
    private  var title: String? =null;
    constructor( type :String?,title :String?) : super( R.layout.item_my_redblack_list_type){
        this.type=type
        this.title=title
    }

    override fun convert(helper: BaseViewHolder, bean: ResoureListBeanItem?) {
        setCommonData(helper, bean)
    }

    /**
     * 如果是景区地区公共部分其他的排行
     */
    private fun setCommonData(helper: BaseViewHolder, bean: ResoureListBeanItem?) {
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

        if (!bean?.images.isNullOrEmpty()) {
            var url = bean?.images?.split(",")?.get(0)
            Glide.with(mContext)
                .load(url)
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .error(R.mipmap.placeholder_img_fail_240_180)
                .into(helper.getView<ImageView>(R.id.iv_content))
        }else{
            helper.getView<ImageView>(R.id.iv_content).setImageResource(R.mipmap.placeholder_img_fail_240_180)
        }

        helper.getView<TextView>(R.id.tv_tag).text=bean?.level
        helper.getView<TextView>(R.id.tv_score).text="综合评分${bean?.numAvg}"
        helper.getView<TextView>(R.id.tv_comment).text="${bean?.commentNum}条评论"
        helper.getView<TextView>(R.id.tv_area).text=bean?.name
        helper.getView<RatingBar>(R.id.rating_bar_des).rating = bean?.numAvg?.toFloat()!!

        helper.itemView.setOnClickListener{
            var path=""
            when(type){
                ResourceType.CONTENT_TYPE_SCENERY-> {
                    path=MainARouterPath.MAIN_SCENIC_DETAIL
                }
                ResourceType.CONTENT_TYPE_HOTEL-> {
                    path=ZARouterPath.ZMAIN_HOTEL_DETAIL
                }
                ResourceType.CONTENT_TYPE_RESTAURANT-> {
                    path=MainARouterPath.MAIN_FOOD_DETAIL
                }
                ResourceType.CONTENT_TYPE_AGRITAINMENT-> {
                    path=ARouterPath.CountryModule.COUNTRY_HAPPINESS_DETAIL
                }

            }
            ARouter.getInstance()
                .build(path)
                .withString("id", bean.id.toString())
                .navigation()
        }
    }


//    /**
//     * 如果是景区其他的排行
//     */
//    private fun setIsOtherData(helper: BaseViewHolder, bean: ResoureListBeanItem?) {
//        helper.getView<ConstraintLayout>(R.id.ll_root).setOnClickListener {
//            if (AppUtils.isLogin()) {
////                var intent = Intent(mContext, RedBlackWriteCommitActivity::class.java)
////                mContext?.startActivity(intent)
//            } else {
//                ToastUtils.showMessage("该操作需要登录，请先登录")
//                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
//                    .navigation()
//
//            }
//        }
//    }

}