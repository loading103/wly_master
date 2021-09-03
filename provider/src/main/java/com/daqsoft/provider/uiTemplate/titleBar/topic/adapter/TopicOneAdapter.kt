package com.daqsoft.provider.uiTemplate.titleBar.topic.adapter

import android.view.View
import androidx.core.view.isVisible
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.HomeTopicBean
import com.daqsoft.provider.databinding.ItemTopicStyleOneBinding
import com.daqsoft.provider.net.ProviderApi
import com.daqsoft.provider.view.extend.onModuleNoDoubleClick

/**
 * 话题适配器样式一
 */
class TopicOneAdapter : RecyclerViewAdapter<ItemTopicStyleOneBinding, HomeTopicBean>(R.layout.item_topic_style_one) {
    override fun setVariable(
        mBinding: ItemTopicStyleOneBinding,
        position: Int,
        item: HomeTopicBean
    ) {

        mBinding.url = item.image
        mBinding.name = item.name

        if (item.hot) {
            mBinding.tvHot.visibility = View.VISIBLE
        } else {
            mBinding.tvHot.visibility = View.GONE
        }
        if (!item.topicTypeName.isNullOrEmpty()) {
            mBinding.tvType.text = item.topicTypeName
        } else {
            mBinding.tvType.visibility = View.INVISIBLE
            mBinding.tvType.text = "热门"
        }
        val drawable = mBinding.root.context.resources.getDrawable(R.drawable.shape_round_gray_d2)
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        if (mBinding.tvHot.isVisible && mBinding.tvType.isVisible) {
            mBinding.tvContentsTxt.setCompoundDrawables(null, null, drawable, null)
        }
        if (!item.hot && item.topicTypeName.isNullOrEmpty()) {
            mBinding.clHot.visibility = View.INVISIBLE
        }

        if (item.contentNum != "0") {
            mBinding.tvContents.text = item.contentNum
        } else {
            mBinding.groupContent.visibility = View.GONE
        }
        if (item.participateNum != "0") {
            mBinding.tvNumbers.text = item.participateNum
        } else {
            mBinding.groupParticipate.visibility = View.GONE
        }
        if (item.showNum != "0") {
            mBinding.tvWatchTimes.text = item.showNum
        } else {
            mBinding.groupBrowse.visibility = View.GONE
        }
        if (mBinding.groupContent.isVisible && (mBinding.groupParticipate.isVisible || mBinding.groupBrowse.isVisible)) {
            mBinding.tvContentsTxt.setCompoundDrawables(null, null, drawable, null)
        }
        if (mBinding.groupParticipate.isVisible && mBinding.groupBrowse.isVisible) {
            mBinding.tvNumbersTxt.setCompoundDrawables(null, null, drawable, null)
        }
        if (item.contentNum == "0" && item.participateNum == "0" && item.showNum == "0") {
            mBinding.clContent.visibility = View.GONE
        }


        mBinding.root.onModuleNoDoubleClick(
            mBinding.root.context.resources.getString(R.string.home_topic),
            ProviderApi.REGION_MAIN_COLUMNS
        ) {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_TOPIC_DETAIL)
                .withString("id", item.id)
                .navigation()
        }
    }
}