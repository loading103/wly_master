package com.daqsoft.legacyModule.adapter

import android.content.Context
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.bean.LegacyHeritageItemListBean
import com.daqsoft.legacyModule.bean.LegacyHeritagePeopleListBean
import com.daqsoft.legacyModule.bean.LegacyWatchStoryListBean
import com.daqsoft.legacyModule.databinding.LegacyModuleItemHeritageBinding
import com.daqsoft.legacyModule.databinding.LegacyModuleItemHeritagePeopleBinding
import com.daqsoft.legacyModule.databinding.LegacyModuleItemWatchStoryBinding
import com.daqsoft.legacyModule.smriti.util.TextFontUtil
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.utils.DividerTextUtils
import java.lang.StringBuilder


/**
 * @des    非遗传承人 adapter
 * @class  LegacyHeritageAdapter
 * @author Caihj
 * @date   2020-5-12 14:22
 *
 */
internal class LegacyHeritagePeopleAdapter(context: Context) :
    RecyclerViewAdapter<LegacyModuleItemHeritagePeopleBinding, LegacyHeritagePeopleListBean>
        (R.layout.legacy_module_item_heritage_people) {
    private val mContext: Context = context
    var onItemClick: OnItemClickListener? = null

    override fun setVariable(
        mBinding: LegacyModuleItemHeritagePeopleBinding,
        position: Int,
        item: LegacyHeritagePeopleListBean
    ) {
        var imageUrl = ""
        if (!item.images.isNullOrEmpty() && !item.images.split(",").isNullOrEmpty()) {
            imageUrl = item.images.split(",")[0]
        }
        Glide.with(mContext).load(imageUrl).placeholder(R.mipmap.placeholder_img_fail_240_180)
            .into(mBinding.iv)
        if (!item.name.isNullOrEmpty()) {
            mBinding.tvName.visibility = View.VISIBLE
            mBinding.tvName.text = item.name
        } else {
            mBinding.tvName.visibility = View.GONE
        }
        val sb = StringBuilder()
        var sex = if (item.gender == 1) {
            "性别：男"
        } else {
            "性别：女"
        }
        val info = DividerTextUtils.convertString(
            sb,
            sex,
            if (!item.nationality.isNullOrEmpty()) {
                "名族：${item.nationality}"
            } else {
                ""
            },
            "传承项目：${item.heritageItemName}"
        )
        mBinding.tvContent.text = info
        sb.clear()
        var productNum = if (item.productNum.isNullOrEmpty()) {
            "0"
        } else {
            item.productNum
        }
        val spb = SpannableStringBuilder()
        val text = DividerTextUtils.convertDotString(
            sb,
            "$productNum 个作品",
            "${item.fans} 个粉丝",
            "${item.showNum} 次浏览"
        )
        spb.append(text)
        val productLen = productNum.length
        val fansLen = item.fans.toString().length
        val showNumLen = item.showNum.toString().length

        // 是自己就不显示关注
        if (item.isMe == 1) {
            mBinding.tvWatch.visibility = View.GONE
        }
        if (item.fansStatus == 1) {
//            mBinding.tvWatch.isEnabled = false
            mBinding.tvWatch.text = "已关注"
            mBinding.tvWatch.isSelected=false
        }else{
            mBinding.tvWatch.text = "关注"
            mBinding.tvWatch.isSelected=true
        }
        mBinding.tvWatch.onNoDoubleClick {
            onItemClick!!.onItemClick(item.id, position, item.fansStatus == 0)
        }
        // 设置颜色
        var colorSpan = ForegroundColorSpan(mContext.resources.getColor(R.color.color_333))
        spb.setSpan(colorSpan, 0, productLen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        colorSpan = ForegroundColorSpan(mContext.resources.getColor(R.color.color_333))
        spb.setSpan(
            colorSpan,
            productLen + 5,
            productLen + 5 + fansLen,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        colorSpan = ForegroundColorSpan(mContext.resources.getColor(R.color.color_333))
        spb.setSpan(
            colorSpan,
            productLen + 10 + fansLen,
            productLen + 10 + fansLen + showNumLen,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        mBinding.tvDesNum.text = spb
        mBinding.root.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.LegacyModule.LEGACY_PEOPLE_DETAIL_ACTIVITY)
                .withString("id", item.id)
                .withInt("position", position)
                .navigation()
        }
    }


    override fun payloadUpdateUi(
        mBinding: LegacyModuleItemHeritagePeopleBinding,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads[0] == "updateFocus") {
            try {
                val item = getData()[position]
//                mBinding.tvWatch.isEnabled = item.fansStatus != 1
                if (item.fansStatus == 1){
                    mBinding.tvWatch.text = "已关注"
                    mBinding.tvWatch.isSelected=false
                }else{
                    mBinding.tvWatch.text = "关注"
                    mBinding.tvWatch.isSelected=true
                }
            } catch (e: Exception) {

            }
        }
    }

    fun notifyFocusStatus(postion: Int) {
        try {
            if (!getData().isNullOrEmpty()) {
                if (getData()[postion].fansStatus == 1) {
                    getData()[postion].fansStatus = 0
                } else {
                    getData()[postion].fansStatus = 1
                }
                notifyItemChanged(postion, "updateFocus")
            }
        } catch (e: Exception) {

        }
    }

    interface OnItemClickListener {
        fun onItemClick(id: String, position: Int, status: Boolean)
    }
}