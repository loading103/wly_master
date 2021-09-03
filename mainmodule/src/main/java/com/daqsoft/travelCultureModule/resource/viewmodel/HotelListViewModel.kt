package com.daqsoft.travelCultureModule.resource.viewmodel

import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.LabelType
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.*
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
    /**
     * 获取景区类型条件选择项
     */
    override fun getSelectLabel() {
        mPresenter.value?.loading = true
        // 串行获取所有条件
        val list = mutableListOf<MutableList<ResourceTypeLabel>>()

//        val f1 = MainRepository.service.getSelectLabel(LabelType
//            .HOTEL_FACILITIES,ResourceType.CONTENT_TYPE_HOTEL).startWith()


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
                    data!!.add(0, ResourceTypeLabel("", "", "", "", "不限"))
                    list.add(data)
                }

                override fun onError(e: Throwable) {

                }

            })
    }

    override fun getList(p: HashMap<String, String>) {
        val keys = p.keys
        val iterator1 = keys.iterator()
        while (iterator1.hasNext()) {
            val key = iterator1.next()
            val value = p[key]
            param[key] = value!!
        }

//        mPresenter.value?.loading = true
        MainRepository.service.getHotelList(param).excute(object : BaseObserver<HotelBean>() {
            override fun onSuccess(response: BaseResponse<HotelBean>) {
                hotelList.postValue(response.datas)
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
    val hotelList = MutableLiveData<MutableList<HotelBean>>()


    /**
     * 去到地图模式
     */
    val gotoMap = object : ReplyCommand {
        override fun run() {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_SCENIC_LIST_MAP)
                .withString("mSelectType", ResourceType.CONTENT_TYPE_HOTEL)
                .navigation()
        }

    }
}