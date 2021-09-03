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
import com.daqsoft.legacyModule.bean.LegacyHeritageExperienceBaseListBean
import com.daqsoft.legacyModule.bean.LegacyHeritageItemListBean
import com.daqsoft.legacyModule.bean.LegacyHeritagePeopleListBean
import com.daqsoft.legacyModule.bean.LegacyWatchStoryListBean
import com.daqsoft.legacyModule.databinding.LegacyModuleItemHeritageBaseBinding
import com.daqsoft.legacyModule.databinding.LegacyModuleItemHeritageBinding
import com.daqsoft.legacyModule.databinding.LegacyModuleItemHeritagePeopleBinding
import com.daqsoft.legacyModule.databinding.LegacyModuleItemWatchStoryBinding
import com.daqsoft.legacyModule.getRealImages
import com.daqsoft.legacyModule.smriti.util.TextFontUtil
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.utils.DividerTextUtils
import java.lang.StringBuilder


/**
 * @des    非遗基地 adapter
 * @class  LegacyHeritageAdapter
 * @author Caihj
 * @date   2020-5-12 14:22
 *
 */
internal class LegacyHeritageBaseAdapter(context:Context,type:String) : RecyclerViewAdapter<LegacyModuleItemHeritageBaseBinding, LegacyHeritageExperienceBaseListBean>
    (R.layout.legacy_module_item_heritage_base) {
    private val mContext:Context = context
    private val type:String = type
    var onItemClick: LegacyHeritageAdapter.OnItemClickListener? = null

    override fun setVariable(mBinding: LegacyModuleItemHeritageBaseBinding, position: Int, item: LegacyHeritageExperienceBaseListBean) {
        Glide.with(mContext).load(item.images.getRealImages()).placeholder(R.mipmap.placeholder_img_fail_240_180).into(mBinding.iv)
        if (!item.name.isNullOrEmpty()) {
            mBinding.tvName.visibility = View.VISIBLE
            mBinding.tvName.text = item.name
        } else {
            mBinding.tvName.visibility = View.GONE
        }
        if(!item.heritageItemName.isNullOrEmpty()){
            mBinding.tvProject.visibility = View.VISIBLE
            mBinding.tvProject.text = mContext.getString(R.string.legacy_project_name).format(item.heritageItemName)
        }else{
            mBinding.tvProject.visibility = View.GONE
        }
        if(type == ResourceType.CONTENT_TYPE_HERITAGE_TEACHING_BASE){
            if(!item.region.isNullOrEmpty())
                mBinding.tvPlace.text = mContext.getString(R.string.legacy_project_address).format(item.region)
        }else{
            if(!item.baseRegionName.isNullOrEmpty())
                mBinding.tvPlace.text = mContext.getString(R.string.legacy_project_address).format(item.baseRegionName)
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
            ARouter.getInstance()
                .build(ARouterPath.LegacyModule.LEGACY_EXPERIENCE_DETAIL_ACTIVITY)
                .withString("id",item.id)
                .withString("type",type)
                .withInt("position",position)
                .navigation()
        }
    }

    override fun payloadUpdateUi(mBinding: LegacyModuleItemHeritageBaseBinding, position: Int, payloads: MutableList<Any>) {
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