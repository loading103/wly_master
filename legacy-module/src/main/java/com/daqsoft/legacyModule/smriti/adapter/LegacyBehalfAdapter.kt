package com.daqsoft.legacyModule.smriti.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.databinding.ItemLegacyBehalfBinding
import com.daqsoft.legacyModule.smriti.bean.LegacyBehalfBean
import com.daqsoft.legacyModule.smriti.util.TextFontUtil
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.jakewharton.rxbinding2.view.RxView
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit

/**
 * desc :非遗代表性项目adapter
 * @author 江云仙
 * @date 2020/4/21 17:07
 */
class LegacyBehalfAdapter : RecyclerViewAdapter<ItemLegacyBehalfBinding, LegacyBehalfBean>(R.layout.item_legacy_behalf) {

    @SuppressLint("CheckResult")
    override fun setVariable(mBinding: ItemLegacyBehalfBinding, position: Int, item: LegacyBehalfBean) {
        if (!item.images.isNullOrEmpty()&&!item.images.split(",").isNullOrEmpty()) {
            mBinding.bgUrl = item.images.split(",")[0]
        }
        if (!item.name.isNullOrEmpty()) {
            mBinding.tvTitle.visibility = View.VISIBLE
            mBinding.tvTitle.text = item.name
        } else {
            mBinding.tvTitle.visibility = View.GONE
        }
        var strBuilder = StringBuilder("")
        if (!item.regionName.isNullOrEmpty()) {
            strBuilder.append("地区：${item.regionName}")
        }
        if (!item.level.isNullOrEmpty()) {
            strBuilder.append(" | 级别：${item.level}")
        }
        if (!item.batch.isNullOrEmpty()) {
            strBuilder.append(" | 批次：${item.batch}")
        }
        mBinding.tvInfo.text = strBuilder.toString()


        //设置粗体和字体大小
        var strNumBuilder = StringBuilder("")
        val storyNumstr = item.storyNum
        if (!storyNumstr.isNullOrEmpty()) {
            val textBoldAndSize = TextFontUtil.setTextBoldAndSize(storyNumstr, Color.parseColor("#333333"), null, 0, storyNumstr.length, true)
            strNumBuilder.append(textBoldAndSize).append("个非遗故事")
        }
        val peopleNumstr = item.peopleNum
        if (!peopleNumstr.isNullOrEmpty()) {
            val textBoldAndSize = TextFontUtil.setTextBoldAndSize(peopleNumstr, Color.parseColor("#333333"), null, 0, peopleNumstr.length, false)
            strNumBuilder.append("·").append(textBoldAndSize).append("个代表性传承人")
        }
        val showNumstr = item.showNum
        if (!showNumstr.isNullOrEmpty()) {
            val textBoldAndSize = TextFontUtil.setTextBoldAndSize(showNumstr, Color.parseColor("#333333"), null, 0, showNumstr.length, false)
            strNumBuilder.append("·").append(textBoldAndSize).append("次浏览")
        }
        mBinding.tvNum.text = strNumBuilder.toString()
        if (!item.type.isNullOrEmpty()) {
            mBinding.tvLabel.text = item.type
            mBinding.tvLabel.visibility = View.VISIBLE
        } else {
            mBinding.tvLabel.visibility = View.GONE
        }
        //设置是否显示视频按钮
        if (!item.video.isNullOrEmpty()) {
            mBinding.imgLegacyPlay.visibility = View.VISIBLE
            item.videoCover?.let {
                mBinding.bgUrl = it
            }
        } else {
            mBinding.imgLegacyPlay.visibility = View.GONE
        }
        if (item.collectionStatus != null) {
            if (item.collectionStatus == 0) {
                mBinding.imgLegacyBehalfCollect.setImageResource(R.mipmap.provider_collect_normal)
            } else {
                mBinding.imgLegacyBehalfCollect.setImageResource(R.mipmap.provider_collect_selected)
            }
        } else {
            mBinding.imgLegacyBehalfCollect.setImageResource(R.mipmap.provider_collect_normal)
        }
        //点击收藏
        RxView.clicks(mBinding.imgLegacyBehalfCollect)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if (listener != null) {
                    if (item.collectionStatus == 0) {//未收藏
                        listener!!.onCollectClick(item,mBinding.imgLegacyBehalfCollect)
                    }else{//取消收藏
                        listener!!.onCancelCollectClick(item,mBinding.imgLegacyBehalfCollect)
                    }
                }


            }
        RxView.clicks(mBinding.root)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                ARouter.getInstance().build(ARouterPath.LegacyModule.LEGACY_Smrity_DETAIL_ACTIVITY)
                    .withString("id", item.id)
                    .navigation()


            }

    }

    private var listener: OnItemClickListener? = null
    fun setOnItemListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onCollectClick(item: LegacyBehalfBean, position: ImageView)
        fun onCancelCollectClick(item: LegacyBehalfBean, position: ImageView)
    }

}