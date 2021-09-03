package com.daqsoft.travelCultureModule.contentActivity

import android.graphics.Typeface
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.*
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.PageDealUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityNewContentBinding
import com.daqsoft.mainmodule.databinding.ItemCollectionTypeNewBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.ZARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.CollectionBean
import com.daqsoft.provider.bean.ResourceChannel
import com.daqsoft.travelCultureModule.clubActivity.bean.ClubZixunBean
import com.daqsoft.travelCultureModule.net.MainRepository

/**
 * 新版资讯
 */
@Route(path = MainARouterPath.MAIN_CONTENT_NEW)
class ContentNewActivity : TitleBarActivity<ActivityNewContentBinding, ContentNewViewModel>() {
    /**
     * 关联资源类型
     */
    @Autowired
    @JvmField
    var linksResourceType = ""

    /**
     * 关联资源ID
     */
    @Autowired
    @JvmField
    var linksResourceId = ""

    /**
     * 类型数据
     */
    var typeList : MutableList<ResourceChannel> ?= null

    /**
     * 资源类型
     */
    var resourceType = ""

    /**
     * 当前页码
     */
    var page = 1
    var pageSize = 10
    override fun getLayout(): Int = R.layout.activity_new_content

    override fun setTitle(): String = "资讯列表"

    override fun injectVm(): Class<ContentNewViewModel> = ContentNewViewModel::class.java

    var contentLsAdapter:ConentNewLsAdapter ?= null

    override fun initView() {
        mBinding.recyclerType.apply {
            layoutManager = LinearLayoutManager(this@ContentNewActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = typeAdapter.apply { emptyViewShow=false }
        }
        val tagLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.recyclerList.layoutManager = tagLayoutManager
        contentLsAdapter = ConentNewLsAdapter(this@ContentNewActivity)
        contentLsAdapter?.emptyViewShow = false
        mBinding.recyclerList.adapter = contentLsAdapter


    }

    override fun initData() {
        showLoadingDialog()
        mModel.getType(linksResourceId,linksResourceType)
    }

    override fun notifyData() {
        super.notifyData()
        mModel.typeList.observe(this, Observer {
            typeList=it
            typeAdapter.add(it)
            typeAdapter.notifyDataSetChanged()
            page = 1
            mModel.getZixunList(linksResourceId,linksResourceType, pageSize.toString(),page.toString(),resourceType)
        })

        mModel.zixunList.observe(this, Observer { response->
            mBinding.smartlayout.finishRefresh()
            dissMissLoadingDialog()
            response.datas?.let {
                if (it.size != null && contentLsAdapter != null) {
                    PageDealUtils().pageDeal(page, response, contentLsAdapter!!)
                    if (it.isNotEmpty()){
                        contentLsAdapter?.add(it)
                    }
                } else {
                    if (page == 1) {
                        contentLsAdapter?.emptyViewShow = true
                    }
                }
            }
        })
    }

    /**
     * 列表数据页码处理
     *
     * @param page     当前页
     * @param response 返回数据体
     * @param adapter  适配器
     */
    private fun pageDeal(
        page: Int?,
        response: BaseResponse<*>,
        adapter: RecyclerViewAdapter<*, *>
    ) {
        if (page == null) {
            return
        }
        if (page == 1) {
            adapter.clear()
        }
        if (response.page == null) {
            adapter.loadEnd()
            return
        }
        if (response.page!!.currPage < response.page!!.totalPage) {
            adapter.loadComplete()
        } else {
            adapter.loadEnd()
        }
    }

    /**
     * 资源类型的适配器
     */
    var typeAdapter = object :
        RecyclerViewAdapter<ItemCollectionTypeNewBinding, ResourceChannel>(R.layout.item_collection_type_new) {
        override fun setVariable(
            mBinding: ItemCollectionTypeNewBinding,
            position: Int,
            item: ResourceChannel
        ) {
            mBinding.item = item
            mBinding.tvType.isSelected = false
            mBinding.tvType.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            if (item.checked) {
                mBinding.tvType.isSelected = true
                mBinding.tvType.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                resourceType = item.channelCode.toString()
            }
            mBinding.root.setOnClickListener {
                for (type in typeList!!) {
                    type.checked = false
                    if (type.id == item.id) {
                        type.checked = true
                    }
                }
                resourceType = item.channelCode.toString()
                notifyDataSetChanged()
                page = 1
                mModel.getZixunList(linksResourceId,linksResourceType,pageSize.toString(),page.toString(),resourceType)
            }

        }

    }

    /**
     * 点击事件（item）
     * 数据源 searchBean
     */
    fun itemOnClick(bean: CollectionBean) {
        when (bean.resourceType) {
            // 景区
            ResourceType.CONTENT_TYPE_SCENERY ->
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_SCENIC_DETAIL)
                    .withString("id", bean.resourceId.toString())
                    .navigation()
            // 酒店
            ResourceType.CONTENT_TYPE_HOTEL -> {
                ARouter.getInstance()
                    .build(ZARouterPath.ZMAIN_HOTEL_DETAIL)
                    .withString("id", bean.resourceId.toString())
                    .navigation()
            }
            // 场馆
            ResourceType.CONTENT_TYPE_VENUE -> {
                ARouter.getInstance()
                    .build(ARouterPath.VenuesModule.VENUES_DETAILS_ACTIVITY)
                    .withString("id", bean.resourceId.toString())
                    .navigation()
            }
            // 美食(餐饮)
            ResourceType.CONTENT_TYPE_RESTAURANT -> {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_FOOD_DETAIL)
                    .withString("id", bean.resourceId.toString())
                    .navigation()
            }
            // 内容
            ResourceType.CONTENT -> {
                if (bean.contentType.equals("IMAGE")) {
                    ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_IMG)
                        .withString("id", bean.resourceId.toString())
                        .navigation()
                } else {
                    ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_INFO)
                        .withString("id", bean.resourceId.toString())
                        .withString("contentTitle", "资讯详情")
                        .navigation()
                }
            }
            // 故事
            ResourceType.CONTENT_TYPE_STORY -> {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_STORY_DETAIL)
                    .withString("id", bean.resourceId.toString())
                    .withInt("type", 1)
                    .navigation()
            }
            ResourceType.CONTENT_TYPE_ACTIVITY -> {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_HOT_ACITITY)
                    .withString("id", bean.resourceId.toString())
                    .withString("classifyId", "")
                    .navigation()
            }
            ResourceType.CONTENT_TYPE_TOPIC -> {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_TOPIC_DETAIL)
                    .withString("id", bean.resourceId.toString())
                    .navigation()
            }
            // 品牌
            ResourceType.CONTENT_TYPE_BRAND -> {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_BRANCH_DETAIL)
                    .withString("id", bean.resourceId.toString())
                    .navigation()
            }
            // 乡村
            ResourceType.CONTENT_TYPE_COUNTRY -> {
                ARouter.getInstance()
                    .build(ARouterPath.CountryModule.COUNTRY_COUNTRY_DETAIL_ACTIVITY)
                    .withString("id", bean.resourceId.toString())
                    .navigation()
            }
            // 农家乐
            ResourceType.CONTENT_TYPE_AGRITAINMENT -> {
                ARouter.getInstance()
                    .build(ARouterPath.CountryModule.COUNTRY_HAPPINESS_DETAIL)
                    .withString("id", bean.resourceId.toString())
                    .navigation()
            }
            // 大讲堂
            ResourceType.CONTENT_TYPE_COURSE -> {
                ARouter.getInstance().build(MainARouterPath.LECTURE_HALL_DETIAL)
                    .withString("id", bean.resourceId.toString())
                    .navigation()
            }
            // 娱乐场所
            ResourceType.CONTENT_TYPE_ENTERTRAINMENT -> {
                ARouter.getInstance().build(MainARouterPath.MAIN_PLAYGROUND_DETAIL)
                    .withString("id", bean.resourceId.toString())
                    .navigation()
            }
            // 展厅
            ResourceType.CONTENT_TYPE_EXHIBITION -> {
                ARouter.getInstance()
                    .build(MainARouterPath.COLLECT_SHOW_DETAIL)
                    .withString("id",bean.id.toString())
                    .navigation()

            }
            // 展览
            ResourceType.CONTENT_TYPE_EXHIBIT_SHOW -> {
                ARouter.getInstance()
                    .build(MainARouterPath.COLLECT_SHOW_DETAIL)
                    .withString("id",bean.id.toString())
                    .navigation()
            }
            // 馆藏
            ResourceType.CONTENT_TYPE_COLLECTION -> {
                ARouter.getInstance()
                    .build(MainARouterPath.COLLECT_CULYURE_DETAIL)
                    .withString("id",bean.id.toString())
                    .navigation()
            }
            // "非遗保护基地"
            ResourceType.CONTENT_TYPE_HERITAGE_PROTECT_BASE -> {
                intangibleHeritageJump(ARouterPath.LegacyModule.LEGACY_EXPERIENCE_DETAIL_ACTIVITY,bean)
            }
            // "非遗传习基地"
            ResourceType.CONTENT_TYPE_HERITAGE_TEACHING_BASE -> {
                intangibleHeritageJump(ARouterPath.LegacyModule.LEGACY_EXPERIENCE_DETAIL_ACTIVITY,bean)
            }
            // "非遗体验基地"
            ResourceType.CONTENT_TYPE_HERITAGE_EXPERIENCE_BASE -> {
                intangibleHeritageJump(ARouterPath.LegacyModule.LEGACY_EXPERIENCE_DETAIL_ACTIVITY,bean)
            }
            // "非遗传承人"
            ResourceType.CONTENT_TYPE_HERITAGE_PEOPLE -> {
                intangibleHeritageJump(ARouterPath.LegacyModule.LEGACY_PEOPLE_DETAIL_ACTIVITY,bean)
            }
            // "非遗项目"
            ResourceType.CONTENT_TYPE_HERITAGE_ITEM -> {
                intangibleHeritageJump(ARouterPath.LegacyModule.LEGACY_Smrity_DETAIL_ACTIVITY,bean)
            }
            // "研学基地"
            ResourceType.CONTENT_TYPE_RESEARCH_BASE -> {
                ARouter.getInstance()
                    .build(MainARouterPath.RESEARCH_DETAIL)
                    .withString("id", bean.id.toString())
                    .navigation()
            }
            // "特产详情"
            ResourceType.CONTENT_TYPE_SPECIALTY -> {
                ARouter.getInstance().build(MainARouterPath.MAIN_SPECIAL_DETAIL)
                    .withString("id",bean.id.toString())
                    .navigation()
            }
            else -> {
                ToastUtils.showMessage("功能开发中~")
            }
        }

    }


    /**
     * 非遗跳转
     * @param path String
     * @param bean CollectionBean
     */
    private fun intangibleHeritageJump(path:String, bean: CollectionBean){
        ARouter.getInstance().build(path)
            .withString("id", bean.resourceId.toString())
            .withString("type",bean.resourceType)
            .navigation()
    }

}

/**
 * @Description 收藏页面ViewModel
 * @Author      Huangxi
 * @Time        2020/2/28
 */
class ContentNewViewModel : BaseViewModel() {
    /**
     * 类型列表
     */
    var typeList = MutableLiveData<MutableList<ResourceChannel>>()

    /**
     * 取消收藏
     */
    var cancelCollectLiveData = MutableLiveData<Any>()

    /**
     * 获取收藏类型
     */
    fun getType(id: String,type:String) {
        MainRepository.service.getResourceChannelList(id,type)
            .excute(object : BaseObserver<ResourceChannel>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ResourceChannel>) {
                    response.datas?.let {
                        typeList.value=it.toMutableList().apply {
                            add(0,ResourceChannel("",0,true,"全部"))
                        }
                    }
                }
            })
    }
    //获取咨询列表
    var zixunList = MutableLiveData<BaseResponse<ClubZixunBean>>()
    fun getZixunList(linksResourceId: String, linksResourceType: String, pageSize: String, currPage: String, channelCode: String) {
        MainRepository.service.getStudyAllContent(linksResourceType=linksResourceType, linksResourceId=linksResourceId,currPage = currPage,pageSize = pageSize,channelCode =channelCode)
            .excute(object : BaseObserver<ClubZixunBean>() {
                override fun onSuccess(response: BaseResponse<ClubZixunBean>) {
                    zixunList?.postValue(response)
                }

                override fun onFailed(response: BaseResponse<ClubZixunBean>) {
                    super.onFailed(response)
                    zixunList.postValue(null)
                }
            })
    }


}
