package com.daqsoft.usermodule.ui.order.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.ResourceUtils
import com.daqsoft.provider.bean.OrderAttacthPersonBean
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ItemOrderUserInfoBinding

/**
 * @Description
 * @ClassName   MineOrderUserInfoAdapter
 * @Author      luoyi
 * @Time        2020/8/14 10:56
 */
class MineOrderUserInfoAdapter :
    RecyclerViewAdapter<ItemOrderUserInfoBinding, OrderAttacthPersonBean> {

    var mContext: Context? = null

    var isShowAll: Boolean = true


    constructor(context: Context) : super(R.layout.item_order_user_info) {
        this.mContext = context
        emptyViewShow = false
    }

    override fun setVariable(
        mBinding: ItemOrderUserInfoBinding,
        position: Int,
        item: OrderAttacthPersonBean
    ) {
        item?.let {
            mBinding.tvOrderUserName.text = "${it.userName}"
            if (position == itemCount - 1) {
                mBinding.vUserInfoLine.visibility = View.GONE
            } else {
                mBinding.vUserInfoLine.visibility = View.VISIBLE
            }
            if (it.healthInfo == null) {
                mBinding.tvHealthStatus.visibility = View.GONE
                mBinding.tvTravelCodeStatus.visibility = View.GONE

            } else {
                if (it.healthInfo!!.enableHealthyCode) {
                    mBinding.tvHealthStatus.visibility = View.VISIBLE
                } else {
                    mBinding.tvHealthStatus.visibility = View.GONE
                }
                if (it.healthInfo!!.enableTravelCode) {
                    mBinding.tvTravelCodeStatus.visibility = View.VISIBLE
                } else {
                    mBinding.tvTravelCodeStatus.visibility = View.GONE
                }



                if (it.healthInfo?.healthCode.isNullOrEmpty()) {
                    mBinding.tvHealthStatus.text = "未注册"
                    mBinding.tvHealthStatus.setTextColor(mContext!!.resources.getColor(R.color.color_999))
                    var drawable = ResourceUtils.getDrawable(mContext!!, R.mipmap.icon_health_unkn)
                    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                    mBinding.tvHealthStatus.setCompoundDrawables(drawable, null, null, null)
                } else {
                    var drawable: Drawable
                    when (it.healthInfo!!.healthCode) {
                        "01" -> {
                            mBinding.tvHealthStatus.text = "低风险"
                            mBinding.tvHealthStatus.setTextColor(mContext!!.resources.getColor(R.color.c_36cd64))
                            drawable =
                                ResourceUtils.getDrawable(mContext!!, R.mipmap.icon_health_normal)
                        }
                        "11" -> {
                            mBinding.tvHealthStatus.text = "中风险"
                            mBinding.tvHealthStatus.setTextColor(mContext!!.resources.getColor(R.color.color_ff9e05))
                            drawable =
                                ResourceUtils.getDrawable(mContext!!, R.mipmap.icon_health_warn)
                        }
                        "31" -> {
                            mBinding.tvHealthStatus.text = "高风险"
                            mBinding.tvHealthStatus.setTextColor(mContext!!.resources.getColor(R.color.ff4e4e))
                            drawable =
                                ResourceUtils.getDrawable(mContext!!, R.mipmap.icon_health_danger)
                        }
                        else -> {
                            mBinding.tvHealthStatus.text = "未知"
                            mBinding.tvHealthStatus.setTextColor(mContext!!.resources.getColor(R.color.color_999))
                            drawable =
                                ResourceUtils.getDrawable(mContext!!, R.mipmap.icon_health_unkn)
                        }
                    }
                    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                    mBinding.tvHealthStatus.setCompoundDrawables(drawable, null, null, null)
                }
                // 旅游码状态
                var travelName = if (!item.healthInfo!!.smartTravelName.isNullOrEmpty()) {
                    "(${item.healthInfo!!.smartTravelName})"
                } else {
                    ""
                }
                var trvalDrawable: Drawable
                if (it.healthInfo!!.smartTravelRegisterStatus) {
                    mBinding.tvTravelCodeStatus.text = "已注册${travelName}"
                    mBinding.tvTravelCodeStatus.setTextColor(mContext!!.resources.getColor(R.color.c_36cd64))
                    trvalDrawable =
                        ResourceUtils.getDrawable(
                            mContext!!,
                            R.mipmap.venue_book_condition_icon_low
                        )
                } else {
                    mBinding.tvTravelCodeStatus.setTextColor(mContext!!.resources.getColor(R.color.color_999))
                    mBinding.tvTravelCodeStatus.text = "未注册${travelName}"
                    trvalDrawable = ResourceUtils.getDrawable(
                        mContext!!,
                        R.mipmap.venue_book_condition_icon_unknown
                    )
                }
                trvalDrawable.setBounds(
                    0,
                    0,
                    trvalDrawable.minimumWidth,
                    trvalDrawable.minimumHeight
                )
                mBinding.tvTravelCodeStatus.setCompoundDrawables(trvalDrawable, null, null, null)
                mBinding.tvTravelCodeStatus.compoundDrawablePadding =
                    mContext!!.resources.getDimension(R.dimen.dp_5).toInt()
            }
        }

    }

    override fun getItemCount(): Int {
        if (!isShowAll) {
            return 3
        }
        return super.getItemCount()
    }
}