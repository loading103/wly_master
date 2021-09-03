package com.daqsoft.guidemodule.adapter

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.guidemodule.R
import com.daqsoft.guidemodule.bean.GuideScenicListBean
import com.daqsoft.guidemodule.bean.GuideSearchBean
import com.daqsoft.guidemodule.databinding.GuideLayoutSearchTypeBinding
import com.daqsoft.provider.utils.MenuJumpUtils
import java.text.DecimalFormat

/**
 * @Description
 * @ClassName   GuideSearchLsAdapter
 * @Author      luoyi
 * @Time        2020/11/24 14:07
 */
class GuideSearchLsAdapter : RecyclerViewAdapter<GuideLayoutSearchTypeBinding, GuideScenicListBean>(R.layout.guide_layout_search_type) {

    var onItemClickListner: OnItemClickListner? = null

    public var content: String? = ""
    override fun setVariable(mBinding: GuideLayoutSearchTypeBinding, position: Int, item: GuideScenicListBean) {
        item?.let {
            var text: String? = it.name?.replace("<em>", "").replace("</em>", "")
            //text="美食123美食456美食"
            val style = SpannableStringBuilder(text)
            if (!content.isNullOrEmpty()) {
                val result = style.split(content!!)
                var cur = 0
                for (position in result.indices) {
                    cur += result[position].length
                    if (position == result.size - 1) {

                    } else {
                        style.setSpan(
                            ForegroundColorSpan(mBinding.root.context.resources.getColor(R.color.guide_yellow_ff9e05)),
                            cur,
                            content!!.length + cur,
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                        )     //设置指定位置文字的颜色
                        cur += content!!.length
                    }
                }
            }
            mBinding.tvSearchName.text = style
            mBinding.tvSearchAddress.text = item.address ?: ""
            if (!item.distance.isNullOrEmpty()) {
                val netDis = item.distance.toDouble() ?: 0.0
                val disDistance = if (netDis > 1000) {
                    val df = DecimalFormat("0.00")
                    df.format(netDis / 1000) + "KM"
                } else {
                    netDis.toInt().toString() + "M"
                }
                mBinding.tvSearchDis.text = "距您:${disDistance}"
            }
            mBinding.root.onNoDoubleClick {
                if (!item.resourceType.isNullOrEmpty()) {
                    onItemClickListner?.onItemClick(item)
                }
            }
        }

    }

    interface OnItemClickListner {
        fun onItemClick(item: GuideScenicListBean)
    }
}