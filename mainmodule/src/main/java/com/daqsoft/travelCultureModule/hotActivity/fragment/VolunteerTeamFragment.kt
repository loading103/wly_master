package com.daqsoft.travelCultureModule.hotActivity.fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.*
import com.daqsoft.provider.bean.VolunteerTeamBean

/**
 * @des 志愿团队列表
 * @author PuHua
 * @Date 2019/12/5 17:52
 */
class VolunteerTeamFragment(beans: MutableList<VolunteerTeamBean>) :
    BaseFragment<MainVolunteerTeamFragmentBinding,
            BaseViewModel>() {
    /**
     * 关联的资源
     */
    private val relationBeans = beans

    override fun getLayout(): Int = R.layout.main_volunteer_team_fragment

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
     * 志愿团队
     */
    private val relationAdapter = object :
        RecyclerViewAdapter<MainVolunteerTeamBinding, VolunteerTeamBean>(
            R.layout.main_volunteer_team
        ) {

        override fun setVariable(mBinding: MainVolunteerTeamBinding, position: Int, item: VolunteerTeamBean) {
            mBinding.name = item.teamName
            mBinding.location = item.teamRegionName
            mBinding.slogn = item.teamSlogan
            mBinding.content = "团队人数：" + item.teamMemeberNum + "人"
//            mBinding.serviceTime = "服务时长："+item.teamServiceTime+"小时"
            mBinding.url = item.teamIcon

//            mBinding.url = item.
//
//            /**
//             * 标签适配器
//             */
//            val tagAdapter= object :
//                RecyclerViewAdapter<MainItemHotActivityTagBinding, String>(
//                    R.layout.main_item_hot_activity_tag
//                ) {
//                override fun setVariable(mBinding: MainItemHotActivityTagBinding, position: Int, item: String) {
//                    mBinding.name = item
//                }
//
//            }
//
//            // 标签
//            val tagLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//            mBinding.rvTags.layoutManager = tagLayoutManager
//            mBinding.rvTags.adapter = tagAdapter
//            val tags = item.tags.split(",")
//
//            for( i in tags.indices){
//                if (tags[i].isNotEmpty()){
//                    tagAdapter.addItem(tags[i])
//                }
//            }
//
//         var content =   when(item.resourceType){
//                ResourceType.CONTENT_TYPE_VENUE->{
//                    // 场馆
//                    "可预订活动室"+item.activityCount+"个"
//                }
//             ResourceType.CONTENT_TYPE_HOTEL->{
//                 ""
//             }
//             ResourceType.CONTENT_TYPE_SCENERY->{
//                 "玩乐点"+item.activityCount+"个"
//             }
//             ResourceType.CONTENT_TYPE_RESTAURANT->{
//                 "人均消费"+item.count+"元"
//             }
//
//             else -> ""
//         }
//
//            mBinding.content = content
//            mBinding.location = item.regionName
//            // 是否游金牌解说
//            if (item.goldFlag == "1"){
//                mBinding.ivGoldFlag.visibility = View.VISIBLE
//            }else{
//                mBinding.ivGoldFlag.visibility = View.GONE
//            }
//            // 视频
//            if(item.videoUrl.isNotEmpty()){
//                mBinding.ivVideo.visibility = View.VISIBLE
//            }else{
//                mBinding.ivVideo.visibility = View.GONE
//            }
//            // 720
//            if (item.panoramaUrl.isNotEmpty()){
//                mBinding.iv720.visibility = View.VISIBLE
//            }else{
//                mBinding.iv720.visibility = View.GONE
//            }

        }

    }


    override fun injectVm(): Class<BaseViewModel> = BaseViewModel::class.java

}
