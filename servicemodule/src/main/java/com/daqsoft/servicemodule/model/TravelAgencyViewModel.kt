package com.daqsoft.servicemodule.model

import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.provider.rxCommand.ReplyCommand
import com.daqsoft.servicemodule.bean.TravelAgencyBean
import com.daqsoft.servicemodule.net.ServiceRepository

/**
 * desc :旅行社viewModel
 * @author 江云仙
 * @date 2020/4/2 18:55
 */
class TravelAgencyViewModel : BaseViewModel() {

    /**
     *旅行社列表数据
     */
    var result = MutableLiveData<MutableList<TravelAgencyBean>>()
    /**
     * 地区
     */
    val areas = MutableLiveData<MutableList<ChildRegion>>()
    /**
     * 选中的地区
     */
    var region = ""
    /**
     * 当前经纬度
     */
    var currentLat = ""

    var currentLon = ""
    /**
     * 等级
     */
    var level: String? = ""
    /**
     * 活动分页页码
     */
    var mCurrPage = 1
    /**
     * 获取页码大小
     */
    var mPageSize = 10

    /**
     *  搜索关键字
     */
    var mKeyWords: String? = ""
    /**
     * 去到地图模式
     */
    val gotoMap = object : ReplyCommand {
        override fun run() {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_ACTIVITY_MAP)
                .navigation()
        }

    }


    /**
     * 站点下级区域(两层)
     */
    fun getChildRegions() {
        mPresenter?.value?.isNeedRecyleView = false
        mPresenter?.value?.loading = false
        ServiceRepository().service.getChildRegions().excute(object : BaseObserver<ChildRegion>() {
            override fun onSuccess(response: BaseResponse<ChildRegion>) {
                areas.postValue(response.datas)
            }
        })
    }


    /**
     * 获取旅行社列表
     */
    fun getTravelAgencyList() {
//        mPresenter?.value?.loading = true
        if (mCurrPage == 1) {
            mPresenter?.value?.loading = false
        } else {
            mPresenter?.value?.isNeedRecyleView = false
        }
        val map = HashMap<String, Any>()
        // 经纬度
        map["latitude"] = currentLat
        map["longitude"] = currentLon
        map["region"] = region
        map["currPage"] = mCurrPage.toString()
        map["pageSize"] = mPageSize.toString()
        if(!level.isNullOrEmpty()) {
            map["level"] = level!!
        }
        if (!mKeyWords.isNullOrEmpty() && mKeyWords!!.isNotBlank()) {
            map["name"] = mKeyWords!!
        }
        ServiceRepository().service.agencyList(map)
            .excute(object : BaseObserver<TravelAgencyBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<TravelAgencyBean>) {
                    result.postValue(response.datas)
                }

            })
    }


}