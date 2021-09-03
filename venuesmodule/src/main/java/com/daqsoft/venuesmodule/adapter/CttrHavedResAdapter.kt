package com.daqsoft.venuesmodule.adapter

import android.content.Context
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.provider.bean.GuideOrderInfo
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.IncludeHavedResCommentatorBinding
import org.jetbrains.anko.append

/**
 * @Description
 * @ClassName   CttrHavedResAdapter
 * @Author      luoyi
 * @Time        2020/7/10 11:24
 */
class CttrHavedResAdapter : RecyclerViewAdapter<IncludeHavedResCommentatorBinding, GuideOrderInfo> {

    var mContext: Context? = null

    constructor(context: Context) : super(R.layout.include_haved_res_commentator) {
        this.mContext = context
        emptyViewShow = false
    }

    override fun setVariable(mBinding: IncludeHavedResCommentatorBinding, position: Int, item: GuideOrderInfo) {
        item?.let {
            var times: String = if (it.orderTime != null) {
                "${it.orderTime.startTime}-${item.orderTime.endTime}"
            } else {
                ""
            }
            mBinding.tvComtatorExtimeValue.text = "${it.orderDate} ${times}"
            if (!it.guideOrderLanguage.isNullOrEmpty()) {
                mBinding.tvComtatorLgValue.text = if (it.guideOrderLanguage == "CH") {
                    "中文"
                } else {
                    "英文"
                }
                if (!it.exhibitionList.isNullOrEmpty()) {
                    for (item in it.exhibitionList) {
                        var exhibitionView = LayoutInflater.from(mContext!!).inflate(R.layout.item_haved_exhall, null)
                        var tvExhibitionName: TextView = exhibitionView!!.findViewById(R.id.tv_exhall_name)
                        var tvExhibitionIsRecommed: TextView = exhibitionView!!.findViewById(R.id.tv_exhall_recommend)
                        var strName: SpannableStringBuilder = SpannableStringBuilder("${item.name}")
                        if (item.recommend == 1) {
                            var tvColorSpan = ForegroundColorSpan(Color.parseColor("#ff9e05"))
                            var tvBgSpan = BackgroundColorSpan(Color.parseColor("#fff5e6"))
                            var recommend: String = " 推荐"
                            strName.append(recommend, tvColorSpan, tvBgSpan, 0, recommend.length)
                        } else {
                            tvExhibitionIsRecommed.visibility = View.GONE
                        }
                        tvExhibitionName.text = strName
                        mBinding.lvComtatorExhallValue.addView(exhibitionView)

                    }
                }

            }
            if (it.orderTime != null) {
                mBinding.tvComtatorExtimeValue.text = "${it.orderTime.startTime}-${it.orderTime.endTime}"
            }
            mBinding.tvComtatorCostValue.text = "${it.guideOrderPayMoney}元(到馆支付)"
        }
    }

}