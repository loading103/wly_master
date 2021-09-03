package com.daqsoft.travelCultureModule.story.story

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemTopicListBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.HomeTopicBean
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.travelCultureModule.story.utils.TopicResourceType
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

/**
 *@package:com.daqsoft.travelCultureModule.story.story
 *@date:2020/4/2 16:57
 *@author: caihj
 *@des:话题列表适配器
 **/
class TopicLsAdapter : RecyclerViewAdapter<ItemTopicListBinding, HomeTopicBean> {

    var context:Context?=null
    var onItemClick: OnItemClickListener? = null

    constructor(context:Context):super(R.layout.item_topic_list){
        this.context = context
    }

    @SuppressLint("CheckResult", "SetTextI18n")
    override fun setVariable(mBinding: ItemTopicListBinding, position: Int, item: HomeTopicBean) {

        mBinding.name = item.name
        Glide.with(context!!).load(item.image).placeholder(R.drawable.placeholder_img_fail_240_180).into(mBinding.ivItemTopicCover)
        val nums = mutableListOf<String>()
        if(item.contentNum != "0"){
            nums.add("${item.contentNum}条内容")
        }
        if(item.participateNum != "0"){
            nums.add(" ${item.participateNum}人参与")
        }
        if(item.showNum != "0"){
            nums.add("${item.showNum}次浏览")
        }
        if(item.topicStatus == "2"){
            mBinding.tvEnd.visibility = View.VISIBLE
        }else{
            mBinding.tvEnd.visibility = View.GONE
        }
        mBinding.tvDescription.text =  DividerTextUtils.convertDotString(nums)
        if (item.hot) {
            val drawable = context!!.getDrawable(R.mipmap.home_ht_hot)
            mBinding.tvTitle.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        } else {
            mBinding.tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
        if(item.topicTypeName.isNullOrEmpty()){
            mBinding.tvTypeName.text = null
            mBinding.tvTypeName.setBackgroundResource(0)
        }else{
            mBinding.tvTypeName.text = item.topicTypeName
            mBinding.tvTypeName.setBackgroundResource(TopicResourceType.getTypeBgIcon(item.topicTypeName))
        }

        if(item.vipResourceStatus.collectionStatus){
            mBinding.imgTopicCollect.setImageResource(R.mipmap.provider_collect_selected)
        }else{
            mBinding.imgTopicCollect.setImageResource(R.mipmap.provider_collect_normal)
        }
        RxView.clicks(mBinding.imgTopicCollect)// 1秒内不可重复点击或仅响应一次事件
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                if (item.vipResourceStatus != null && onItemClick != null) {
                    onItemClick!!.onItemClick(item.id,position, item.vipResourceStatus.collectionStatus)
                }
            }
        mBinding.root.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_TOPIC_DETAIL)
                .withString("id", item.id)
                .navigation()
        }
    }

    override fun payloadUpdateUi(mBinding: ItemTopicListBinding, position: Int, payloads: MutableList<Any>) {
        if (payloads[0] == "updateCollect") {
            try {
                val item =getData()[position]
                if (item.vipResourceStatus != null) {
                    if(item.vipResourceStatus.collectionStatus){
                        mBinding.imgTopicCollect.setImageResource(R.mipmap.provider_collect_selected)
                    }else{
                        mBinding.imgTopicCollect.setImageResource(R.mipmap.provider_collect_normal)
                    }
                }
            } catch (e: Exception) {

            }
        }
    }

    fun notifyCollectStatus(postion: Int) {
        try {
            if (!getData().isNullOrEmpty()) {
                var bean = getData()[postion]
                if (bean.vipResourceStatus != null) {
                    getData()[postion].vipResourceStatus!!.collectionStatus = !bean.vipResourceStatus.collectionStatus
                    notifyItemChanged(postion, "updateCollect")
                }
            }
        } catch (e: Exception) {

        }
    }

    interface OnItemClickListener {
        fun onItemClick(id:String,position: Int, status: Boolean)
    }
}