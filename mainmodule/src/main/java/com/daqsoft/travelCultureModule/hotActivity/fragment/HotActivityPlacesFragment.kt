package com.daqsoft.travelCultureModule.hotActivity.fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.MainActivityRoomsFragmentBinding
import com.daqsoft.mainmodule.databinding.MainItemHotActivityRelationBinding
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.travelCultureModule.hotActivity.bean.ActivityRelationBean

/**
 * @des 活动关联的场所页面
 * @author PuHua
 * @Date 2019/12/5 17:52
 */
class HotActivityPlacesFragment(beans: MutableList<ActivityRelationBean>) :
    BaseFragment<MainActivityRoomsFragmentBinding,
            BaseViewModel>() {
    /**
     * 关联的资源
     */
    private val relationBeans = beans

    override fun getLayout(): Int = R.layout.main_activity_rooms_fragment

    override fun initData() {


    }

    override fun initView() {

        val tagLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mBinding.rvActivities.layoutManager = tagLayoutManager
        mBinding.rvActivities.adapter = relationAdapter

        relationAdapter.add(relationBeans)

        if (relationBeans.size > 4) {
            mBinding.tvActivitiesMore.visibility = View.VISIBLE

        } else {
            mBinding.tvActivitiesMore.visibility = View.GONE
        }

    }

    /**
     * 活动场地适配器
     */
    private val relationAdapter = object :
        RecyclerViewAdapter<MainItemHotActivityRelationBinding, ActivityRelationBean>(
            R.layout.main_item_hot_activity_relation
        ) {

        override fun setVariable(mBinding: MainItemHotActivityRelationBinding, position: Int, item: ActivityRelationBean) {
            mBinding.name = item.resourceName
            var imageUrl = ""
            if (!item.images.isNullOrEmpty()) {
                var imageUrLs = item.images.split(",")
                if (!imageUrLs.isNullOrEmpty()) {
                    imageUrl = imageUrLs[0]
                }
            }
            Glide.with(context!!)
                .load(imageUrl)
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .into(mBinding.ivImage)

            // 标签
            val tags = item.tags.split(",")
            if (!tags.isNullOrEmpty()) {
                mBinding.rvTags.visibility = View.VISIBLE
                mBinding.rvTags.setLabels(tags)
            } else {
                mBinding.rvTags.visibility = View.GONE
            }
            var content = when (item.resourceType) {
                ResourceType.CONTENT_TYPE_VENUE -> {
                    // 场馆
                    "可预订活动室" + item.activityCount + "个"
                }
                ResourceType.CONTENT_TYPE_HOTEL -> {
                    ""
                }
                ResourceType.CONTENT_TYPE_SCENERY -> {
                    "玩乐点" + item.activityCount + "个"
                }
                ResourceType.CONTENT_TYPE_RESTAURANT -> {
                    "人均消费" + item.count + "元"
                }

                else -> ""
            }

            mBinding.content = content
            mBinding.location = item.regionName
            // 是否游金牌解说
            if (item.goldFlag == "1") {
                mBinding.ivGoldFlag.visibility = View.VISIBLE
            } else {
                mBinding.ivGoldFlag.visibility = View.GONE
            }
            // 视频
            if (item.videoUrl.isNotEmpty()) {
                mBinding.ivVideo.visibility = View.VISIBLE
            } else {
                mBinding.ivVideo.visibility = View.GONE
            }
            // 720
            if (item.panoramaUrl.isNotEmpty()) {
                mBinding.iv720.visibility = View.VISIBLE
            } else {
                mBinding.iv720.visibility = View.GONE
            }

        }

    }


    override fun injectVm(): Class<BaseViewModel> = BaseViewModel::class.java

}
