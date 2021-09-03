package com.daqsoft.legacyModule.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.bean.LegacyHeritageItemListBean
import com.daqsoft.legacyModule.bean.LegacyWatchStoryListBean
import com.daqsoft.legacyModule.databinding.LegacyModuleItemHeritageBinding
import com.daqsoft.legacyModule.databinding.LegacyModuleItemWatchStoryBinding
import com.daqsoft.legacyModule.smriti.util.TextFontUtil
import com.daqsoft.provider.ARouterPath
import java.lang.StringBuilder


/**
 * @des    非遗项目 adapter
 * @class  LegacyHeritageAdapter
 * @author Wongxd
 * @date   2020-4-21 14:22
 *
 */
internal class LegacyHeritageAdapter(context:Context) : RecyclerViewAdapter<LegacyModuleItemHeritageBinding, LegacyHeritageItemListBean>
    (R.layout.legacy_module_item_heritage) {
    private val mContext:Context = context
    var onItemClick: OnItemClickListener? = null

    override fun setVariable(mBinding: LegacyModuleItemHeritageBinding, position: Int, item: LegacyHeritageItemListBean) {
        mBinding.item = item
        var imageUrl =""
        if (!item.images.isNullOrEmpty()&&!item.images.split(",").isNullOrEmpty()) {
            imageUrl = item.images.split(",")[0]
        }
        Glide.with(mContext).load(imageUrl).placeholder(R.mipmap.placeholder_img_fail_240_180).into(mBinding.iv)
        if (!item.name.isNullOrEmpty()) {
            mBinding.tvName.visibility = View.VISIBLE
            mBinding.tvName.text = item.name
        } else {
            mBinding.tvName.visibility = View.GONE
        }

        val strBuilder = StringBuilder("")
        if (!item.regionName.isNullOrEmpty()) {
            strBuilder.append("地区：${item.regionName} | ")
        }
        if (!item.level.isNullOrEmpty()) {
            strBuilder.append("级别：${item.level} | ")
        }
        if (!item.batch.isNullOrEmpty()) {
            strBuilder.append("批次：${item.batch}")
        }
        mBinding.tvDes.text = strBuilder.toString()


        val strNumBuilder = StringBuilder("")
        val storyNumStr = item.storyNum.toString()

        strNumBuilder
            .append(TextFontUtil.setTextBoldAndSize(storyNumStr, Color.parseColor("#333333"), null, 0, storyNumStr.length, true))
            .append("个非遗故事")

        val peopleNumStr = item.peopleNum.toString()

        strNumBuilder.append(" · ")
            .append(TextFontUtil.setTextBoldAndSize(peopleNumStr, Color.parseColor("#333333"), null, 0, peopleNumStr.length, false))
            .append("个代表性传承人")

        val showNumStr = item.showNum.toString()
            strNumBuilder.append(" · ")
                .append(TextFontUtil.setTextBoldAndSize(showNumStr, Color.parseColor("#333333"), null, 0, showNumStr.length, false))
                .append("次浏览")

        mBinding.tvDesNum.text = strNumBuilder.toString()
        if (!item.type.isNullOrEmpty()) {
            mBinding.tvTag.text = item.type
            mBinding.tvTag.visibility = View.VISIBLE
        } else {
            mBinding.tvTag.visibility = View.GONE
        }

        if(item.collectionStatus == 1){
            mBinding.imgTopicCollect.setImageResource(R.mipmap.provider_collect_selected)
        }else{
            mBinding.imgTopicCollect.setImageResource(R.mipmap.provider_collect_normal)
        }

        mBinding.imgTopicCollect.onNoDoubleClick {
            if (onItemClick != null) {
                onItemClick!!.onItemClick(item.id.toString(),position, item.collectionStatus == 1)
            }
        }

        mBinding.root.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.LegacyModule.LEGACY_Smrity_DETAIL_ACTIVITY)
                .withString("id", item.id.toString())
                .withInt("position",position)
                .navigation()
        }
    }

    override fun payloadUpdateUi(mBinding: LegacyModuleItemHeritageBinding, position: Int, payloads: MutableList<Any>) {
        if (payloads[0] == "updateCollect") {
            try {
                val item =getData()[position]

                    if(item.collectionStatus == 1){
                        mBinding.imgTopicCollect.setImageResource(R.mipmap.provider_collect_selected)
                    }else{
                        mBinding.imgTopicCollect.setImageResource(R.mipmap.provider_collect_normal)
                    }

            } catch (e: Exception) {

            }
        }
    }

    fun notifyCollectStatus(postion: Int) {
        try {
            if (!getData().isNullOrEmpty()) {
                var bean = getData()[postion]
                if(bean.collectionStatus == 1){
                    getData()[postion].collectionStatus = 0
                }else{
                    getData()[postion].collectionStatus = 1

                }
                    notifyItemChanged(postion, "updateCollect")

            }
        } catch (e: Exception) {

        }
    }

    interface OnItemClickListener {
        fun onItemClick(id:String,position: Int, status: Boolean)
    }
}