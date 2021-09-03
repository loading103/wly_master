package com.daqsoft.travelCultureModule.hotel.viewmodel

import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.AdvCodeType
import com.daqsoft.provider.base.LabelType
import com.daqsoft.provider.base.PublishChannel
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.rxCommand.ReplyCommand
import com.daqsoft.travelCultureModule.net.MainRepository
import com.daqsoft.travelCultureModule.resource.BaseResourceViewModel
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * @Description 酒店列表的viewModel
 * @ClassName   HotActivitiesFragmentViewModel
 * @Author      PuHua
 * @Time        2019/12/25 10:42
 */
class HotelListViewModel : BaseResourceViewModel() {


    var collectLiveData: MutableLiveData<Int> = MutableLiveData()

    var canceCollectLiveData: MutableLiveData<Int> = MutableLiveData()
    /**
     * 获取景区类型条件选择项
     */
    override fun getSelectLabel() {
        mPresenter.value?.loading = false
        mPresenter.value?.isNeedRecyleView = false
        // 串行获取所有条件
        val list = mutableListOf<MutableList<ResourceTypeLabel>>()

        var concat = Observable.concat(
            MainRepository.service.getSelectLabel(
                LabelType.HOTEL_TYPE,
                ResourceType
                    .CONTENT_TYPE_HOTEL
            ), MainRepository.service.getSelectLabel(
                LabelType
                    .HOTEL_FACILITIES, ResourceType.CONTENT_TYPE_HOTEL
            ),
            MainRepository.service.getSelectLabel(
                LabelType.SPECIAL_SERVICE, ResourceType
                    .CONTENT_TYPE_HOTEL
            )
        )
        concat.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<BaseResponse<ResourceTypeLabel>> {
                override fun onComplete() {
                    selectLabels.postValue(list)
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: BaseResponse<ResourceTypeLabel>) {
                    val data = t.datas
                    //data!!.add(  ResourceTypeLabel("", "","","","全部"))
                    list.add(data!!)
                }

                override fun onError(e: Throwable) {

                }

            })
    }

    override fun getList(p: HashMap<String, String>) {
        mPresenter.value?.loading = false
        mPresenter.value?.isNeedRecyleView = false
        val keys = p.keys
        val iterator1 = keys.iterator()
        while (iterator1.hasNext()) {
            val key = iterator1.next()
            val value = p[key]
            param[key] = value!!
        }

//        mPresenter.value?.loading = true
        MainRepository.service.getHotelList(param).excute(object : BaseObserver<HotelBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<HotelBean>) {
                hotelList.postValue(response)
            }

            override fun onFailed(response: BaseResponse<HotelBean>) {
                mError.postValue(response)
            }
        })
    }

    /**
     * 选中的景区类型
     */
    var type = ""


    /**
     * 酒店列表
     */
    val hotelList = MutableLiveData<BaseResponse<HotelBean>>()


    /**
     * 去到地图模式
     */
    val gotoMap = object : ReplyCommand {
        override fun run() {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_SCENIC_LIST_MAP)
                .withInt("mSelectTabPos", 2)
                .withString("mSelectType", ResourceType.CONTENT_TYPE_HOTEL)
                .navigation()
        }

    }

    /**
     * 收藏接口
     */
    fun collectionScenic(resourceId: String, position: Int) {
        mPresenter.value?.loading = false
        mPresenter.value?.isNeedRecyleView=false
        CommentRepository.service.posClloection(resourceId, ResourceType.CONTENT_TYPE_HOTEL)
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
        mPresenter.value?.isNeedRecyleView=false
        CommentRepository.service.posCollectionCancel(
            resourceId,
            ResourceType.CONTENT_TYPE_HOTEL
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

    var topAdsLiveData: MutableLiveData<HomeAd> = MutableLiveData()

    /**
     * 获取酒店顶部广告
     */
    fun getHotelTopAds() {
        mPresenter?.value?.loading = false
        HomeRepository.service.getHomeAd(PublishChannel.MICRO_SITE, AdvCodeType.HOTEL_LIST_TOP_ADV)
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