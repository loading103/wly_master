package com.daqsoft.usermodule.ui.collection

import android.graphics.Typeface
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.ZARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.CollectionBean
import com.daqsoft.provider.bean.CollectionTypeBean
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityCollectionBinding
import com.daqsoft.usermodule.databinding.ItemCollectionBinding
import com.daqsoft.usermodule.databinding.ItemCollectionTypeBinding
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

/**
 * @Description 收藏页面
 * @ClassName   CollectionActivity
 * @Author      Huangxi
 * @Time        2020/2/28
 */
@Route(path = ARouterPath.UserModule.USER_COLLECTION_ACTIVITY)
class CollectionActivity : TitleBarActivity<ActivityCollectionBinding, CollectionViewModel>() {
    /**
     * 类型数据
     */
    var typeList = ArrayList<CollectionTypeBean>()

    /**
     * 资源类型
     */
    var resourceType = ""

    /**
     * 当前页码
     */
    var currPage = 1

    override fun getLayout(): Int = R.layout.activity_collection

    override fun setTitle(): String = "收藏"

    override fun injectVm(): Class<CollectionViewModel> = CollectionViewModel::class.java


    override fun initView() {
        mBinding.recyclerType.apply {
            layoutManager = LinearLayoutManager(
                this@CollectionActivity, LinearLayoutManager
                    .HORIZONTAL, false
            )
            adapter = typeAdapter
        }
        mBinding.recyclerList.apply {
            layoutManager = LinearLayoutManager(
                this@CollectionActivity, LinearLayoutManager
                    .VERTICAL, false
            )
            adapter = collectAdapter
        }
        collectAdapter.setOnLoadMoreListener {
            currPage++
            mModel.getCollectionList(resourceType, currPage)
        }
        mBinding.refreshList.setOnRefreshListener {
            currPage = 1
            mModel.getCollectionList(resourceType, currPage)
        }
    }

    override fun initData() {
        mModel.getCollectionType()
    }

    override fun notifyData() {
        super.notifyData()
        mModel.typeList.observe(this, Observer {
            typeList.add(CollectionTypeBean("全部", "", true))
            var types = resources.getStringArray(R.array.collection_type)
            for (type in types) {
                var data = type.split(",")
                var collect = CollectionTypeBean(data[0], data[1], false)
                for (value in it) {
                    if (value.equals(collect.value)) {
                        typeList.add(collect)
                    }
                }
            }
            typeAdapter.add(typeList)
            typeAdapter.notifyDataSetChanged()
            currPage = 1
            mModel.getCollectionList(resourceType, currPage)
        })
        mModel.datas.observe(this, Observer {
            if (currPage == 1) {
//                mBinding.refreshList.isRefreshing = false
                mBinding.refreshList.finishRefresh()
            }
            pageDeal(currPage, it, collectAdapter)
            if (it.datas != null) {
                collectAdapter.add(it!!.datas!!)
                collectAdapter.notifyDataSetChanged()
            }
        })
        // 取消收藏
        mModel.cancelCollectLiveData.observe(this, Observer {
            typeAdapter.notifyDataSetChanged()
            currPage = 1
            mModel.getCollectionList(resourceType, currPage)
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
        RecyclerViewAdapter<ItemCollectionTypeBinding, CollectionTypeBean>(R.layout.item_collection_type) {
        override fun setVariable(
            mBinding: ItemCollectionTypeBinding,
            position: Int,
            item: CollectionTypeBean
        ) {
            mBinding.item = item
            mBinding.tvType.isSelected = false
            mBinding.tvType.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            if (item.checked) {
                mBinding.tvType.isSelected = true
                mBinding.tvType.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                resourceType = item.value
            }
            mBinding.root.setOnClickListener {
                for (type in typeList) {
                    type.checked = false
                    if (type.value.equals(item.value)) {
                        type.checked = true
                    }
                }
                resourceType = item.value
                notifyDataSetChanged()
                currPage = 1
                mModel.getCollectionList(resourceType, currPage)
            }

        }

    }

    /**
     * 资源的适配器
     */
    var collectAdapter = object :
        RecyclerViewAdapter<ItemCollectionBinding, CollectionBean>(R.layout.item_collection) {
        override fun setVariable(
            mBinding: ItemCollectionBinding,
            position: Int,
            item: CollectionBean
        ) {
            mBinding.item = item
            mBinding.imageUrl = item.resourceImage.getRealImages()
            // 资源类型名称
            var resourceName = ResourceType.getName(item.resourceType)
            if (resourceName.isNotEmpty()) {
                mBinding.tvType.text = ResourceType.getName(item.resourceType)
                mBinding.tvType.visibility = View.VISIBLE
            } else {
                mBinding.tvType.visibility = View.GONE
            }
            RxView.clicks(mBinding.root)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    itemOnClick(item)
                }
            RxView.clicks(mBinding.ivDelCollect)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    mModel.cancelCollect(
                        item.resourceId.toString(),
                        item.resourceType
                    )
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
                    .withString("id", bean.resourceId.toString())
                    .navigation()
            }
            // "特产详情"
            ResourceType.CONTENT_TYPE_SPECIALTY -> {
                ARouter.getInstance().build(MainARouterPath.MAIN_SPECIAL_DETAIL)
                    .withString("id",bean.resourceId.toString())
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
class CollectionViewModel : BaseViewModel() {
    /**
     * 类型列表
     */
    var typeList = MutableLiveData<List<String>>()

    /**
     * 收藏列表
     */
    var datas = MutableLiveData<BaseResponse<CollectionBean>>()

    /**
     * 取消收藏
     */
    var cancelCollectLiveData = MutableLiveData<Any>()

    /**
     * 获取收藏类型
     */
    fun getCollectionType() {
        UserRepository().userService.getCollectionType()
            .excute(object : BaseObserver<List<String>>() {
                override fun onSuccess(response: BaseResponse<List<String>>) {
                    if (response != null && response.code == 0 && response.data != null) {
                        typeList.postValue(response.data)
                    }
                }

            })
    }

    /**
     * 获取收藏列表
     */
    fun getCollectionList(resourceType: String, currPage: Int) {
        UserRepository().userService.getCollectionList(resourceType, currPage)
            .excute(object : BaseObserver<CollectionBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<CollectionBean>) {
                    datas.postValue(response)
                }

            })
    }

    /**
     * 取消收藏
     */
    fun cancelCollect(resourceId: String, resourceType: String) {
        if (!resourceId.isNullOrEmpty() && !resourceType.isNullOrEmpty()) {
            CommentRepository.service.posCollectionCancel(resourceId, resourceType)
                .excute(object : BaseObserver<Any>() {
                    override fun onSuccess(response: BaseResponse<Any>) {
                        cancelCollectLiveData.postValue(response)
                        toast.postValue("取消收藏成功~")
                    }

                    override fun onFailed(response: BaseResponse<Any>) {
                        toast.postValue("取消收藏，请稍后再试~")
                    }
                })
        }
    }

}
