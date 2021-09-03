package com.daqsoft.travelCultureModule.specialty.viewmodel

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
import com.daqsoft.travelCultureModule.clubActivity.bean.TypeBean
import com.daqsoft.travelCultureModule.researchbase.adapter.ResearchLsAdapter
import com.daqsoft.travelCultureModule.resource.BaseResourceViewModel


class SpecialtyListViewModel : BaseResourceViewModel() {


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

    var type: String? = ""

    var areaSiteSwitch: String? = ""


    var isInitRequest: Boolean = true

    var collectLiveData: MutableLiveData<Int> = MutableLiveData()

    var canceCollectLiveData: MutableLiveData<Int> = MutableLiveData()

    var topAdsLiveData: MutableLiveData<HomeAd> = MutableLiveData()

    val types = MutableLiveData<MutableList<ResourceTypeLabel>>()

    /**
     * 暂不使用
     */
    override fun getList(param: HashMap<String, String>) {
    }

    /**
     * 获取分类标签
     *
     */
    fun getType() {
        val list = mutableListOf<MutableList<ResourceTypeLabel>>()
        MainRepository.service.getSelectLabel(LabelType.SPECIALITY_TYPE, ResourceType.CONTENT_TYPE_SPECIALTY)
            .subscribeOn(Schedulers.io())
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
    override fun getSelectLabel() {

        MainRepository.service.getSearchType(LabelType.SPECIALITY_TYPE).excute(object : BaseObserver<TypeBean>() {
            override fun onSuccess(response: BaseResponse<TypeBean>) {
                var map = response.datas?.map {
                    ResourceTypeLabel(
                        "", "", "", it.id, it.name

                    )
                }?.toMutableList()
                if (map == null) {
                    map = mutableListOf()
                }
                map.add(0, ResourceTypeLabel("", "", "", "", "不限"))
                types.value = map
                getType()
            }

        })
    }


    /**
     * 获取特产列表信息
     */
    val researchList = MutableLiveData<MutableList<SpeaiclBean>>()
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
        if (!type.isNullOrEmpty()) {
            param["type"] = type!!
        }
        // 经纬度
        if (!lng.isNullOrEmpty() && !lat.isNullOrEmpty()) {
            param["lng"] = lng
            param["lat"] = lat
        }
        // 页面大小
        param["pageSize"] = pageManager.pageSize.toString()
        // 当前页码
        param["currPage"] = pageManager.pageIndex.toString()
        if (!areaSiteSwitch.isNullOrEmpty())
            param["areaSiteSwitch"] = areaSiteSwitch!!
        MainRepository.service.getSpecialList(param).excute(object : BaseObserver<SpeaiclBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<SpeaiclBean>) {
                researchList.postValue(response?.datas!!)
            }

            override fun onFailed(response: BaseResponse<SpeaiclBean>) {
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
                .withString("mSelectType", ResourceType.CONTENT_TYPE_SPECIALTY)
                .navigation()
        }
    }

    /**
     * 收藏接口
     */
    fun collectionScenic(resourceId: String, position: Int) {
        mPresenter.value?.loading = false
        mPresenter.value?.isNeedRecyleView = false
        CommentRepository.service.posClloection(resourceId, ResourceType.CONTENT_TYPE_SPECIALTY)
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
            ResourceType.CONTENT_TYPE_SPECIALTY
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