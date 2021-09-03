package com.daqsoft.travelCultureModule.researchbase.viewmodel

import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.LabelType
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.rxCommand.ReplyCommand
import com.daqsoft.travelCultureModule.net.MainRepository
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.base.AdvCodeType
import com.daqsoft.provider.base.PublishChannel
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.travelCultureModule.researchbase.adapter.ResearchLsAdapter
import com.daqsoft.travelCultureModule.resource.BaseResourceViewModel


/**
 * @Description 景区列表的viewModel
 * @ClassName   HotActivitiesFragmentViewModel
 * @Author      PuHua
 * @Time        2019/12/25 10:42
 */
class ResearchListViewModel : BaseResourceViewModel() {


    /**
     * 分页管理器
     */
    val pageManager: PageManager by lazy {
        PageManager(10)
    }

    /**
     * 主题类型
     */
    var tag: String? = ""
    /**
     * 地区编码
     */
    var region: String = ""
    /**
     * 关键字
     */
    var keyWord: String = ""
    /**
     * 排序规则
     */
    var sortType: String = ""

    /**
     * 经度
     */
    var lng: String = ""
    /**
     * 纬度
     */
    var lat: String = ""

    /**
     * 适合人群
     */
    var crowd: String? = ""

    var areaSiteSwitch: String? = ""

    /**
     * 等级
     */
    var scenicLevel: String? = ""

    var isInitRequest: Boolean = true

    var collectLiveData: MutableLiveData<Int> = MutableLiveData()

    var canceCollectLiveData: MutableLiveData<Int> = MutableLiveData()

    var topAdsLiveData: MutableLiveData<HomeAd> = MutableLiveData()

    /**
     * 暂不使用
     */
    override fun getList(param: HashMap<String, String>) {
    }

    /**
     * 获取分类标签
     */
    override fun getSelectLabel() {
        mPresenter.value?.loading = false
        mPresenter.value?.isNeedRecyleView = false
        val list = mutableListOf<MutableList<ResourceTypeLabel>>()
        var merge = Observable.concat(
            MainRepository.service.getSelectLabel(LabelType.RESEARCH_BASE_THEME, ResourceType.CONTENT_TYPE_RESEARCH_BASE),
            MainRepository.service.getSelectLabel(LabelType.SUITABLE_FOR_PEOPLE, ResourceType.CONTENT_TYPE_RESEARCH_BASE)
        )
        merge.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<BaseResponse<ResourceTypeLabel>> {
                override fun onComplete() {
                    selectLabels.postValue(list)
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: BaseResponse<ResourceTypeLabel>) {
                    val data = t.datas
                    data!!.add(0,ResourceTypeLabel("", "", "", "", "不限"))
                    list.add(data)
                }

                override fun onError(e: Throwable) {

                }

            })
    }


    /**
     * 获取景区列表信息
     */
    val researchList = MutableLiveData<MutableList<ResearchBean>>()
    fun getScenicList() {
        param.clear()
        mPresenter.value?.loading = false
        // 景区类型
        if (!tag.isNullOrEmpty()) {
            param["tag"] = tag!!
        }
        // 地区编码
        if (!region.isNullOrEmpty()) {
            param["region"] = region
        }
        // 关键字
        if (!keyWord.isNullOrEmpty()) {
            param["keyword"] = keyWord
        }
        // 排序类型
        if (!sortType.isNullOrEmpty()) {
            param["sortType"] = sortType
        }
        if (!crowd.isNullOrEmpty()) {
            param["crowd"] = crowd!!
        }
        // 经纬度
        if (!lng.isNullOrEmpty() && !lat.isNullOrEmpty()) {
            param["lng"] = lng
            param["lat"] = lat
        }
        // 等级
        if (!scenicLevel.isNullOrEmpty()) {
            param["scenicLevel"] = scenicLevel!!
        }
        // 页面大小
        param["pageSize"] = pageManager.pageSize.toString()
        // 当前页码
        param["currPage"] = pageManager.pageIndex.toString()
        if (!areaSiteSwitch.isNullOrEmpty())
            param["areaSiteSwitch"] = areaSiteSwitch!!
        MainRepository.service.getResearchList(param).excute(object : BaseObserver<ResearchBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<ResearchBean>) {
                researchList.postValue(response?.datas!!)
            }

            override fun onFailed(response: BaseResponse<ResearchBean>) {
                super.onFailed(response)
                mError.postValue(response)
            }
        })
    }




    /**
     * 去到地图模式
     */
    val gotoMap = object : ReplyCommand {
        override fun run() {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_SCENIC_LIST_MAP)
                .withInt("mSelectTabPos", 0)
                .withString("mSelectType", ResourceType.CONTENT_TYPE_RESEARCH_BASE)
                .navigation()
        }
    }

    /**
     * 收藏接口
     */
    fun collectionScenic(resourceId: String, position: Int) {
        mPresenter.value?.loading = false
        mPresenter.value?.isNeedRecyleView = false
        CommentRepository.service.posClloection(resourceId, ResourceType.CONTENT_TYPE_RESEARCH_BASE)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    ToastUtils.showMessage("已收藏~")
                    collectLiveData.postValue(position)
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    ToastUtils.showMessage("收藏失败，请稍后再试~")
                    mError.postValue(response)
                }
            })
    }

    /**
     * 取消收藏接口
     */

    fun collectionCancelScenic(resourceId: String, position: Int) {
        mPresenter.value?.loading = false
        mPresenter.value?.isNeedRecyleView = false
        CommentRepository.service.posCollectionCancel(
            resourceId,
            ResourceType.CONTENT_TYPE_RESEARCH_BASE
        )
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    ToastUtils.showMessage("已取消收藏~")
                    canceCollectLiveData.postValue(position)
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    ToastUtils.showMessage("取消收藏失败，请稍后再试~")
                    mError.postValue(response)
                }
            })
    }

    /**
     * 获取景区顶部广告
     */
    fun getScenicTopAds() {
        mPresenter?.value?.loading = false
        HomeRepository.service.getHomeAd(PublishChannel.MICRO_SITE, AdvCodeType.SCENIC_LIST_TOP_ADV)
            .excute(object : BaseObserver<HomeAd>() {
                override fun onSuccess(response: BaseResponse<HomeAd>) {
                    topAdsLiveData.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<HomeAd>) {
                    topAdsLiveData.postValue(null)
                }
            })
    }
}