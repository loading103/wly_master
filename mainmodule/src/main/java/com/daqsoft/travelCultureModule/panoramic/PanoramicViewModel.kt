package com.daqsoft.travelCultureModule.panoramic

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.base.LabelType
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.bean.PageManager
import com.daqsoft.provider.bean.ResourceTypeLabel
import com.daqsoft.travelCultureModule.net.MainRepository
import com.daqsoft.travelCultureModule.panoramic.bean.PanoramicListBean


/**
 * @des     全景漫游ViewModel
 * @class   PanoramicViewModel
 * @author  Wongxd
 * @date    2020-4-20  9:18
 *
 */
internal class PanoramicViewModel : BaseViewModel() {

    /**
     * 地区
     */
    val areas = MutableLiveData<MutableList<ChildRegion>>()


    /**
     * 站点下级区域(两层)
     */
    fun getChildRegions() {
        mPresenter.value?.loading = false
        mPresenter.value?.isNeedRecyleView = false
        MainRepository.service.getChildRegions().excute(object : BaseObserver<ChildRegion>() {
            override fun onSuccess(response: BaseResponse<ChildRegion>) {
                areas.postValue(response.datas)
            }
        })
    }


    val typeList = MutableLiveData<MutableList<ResourceTypeLabel>>()
    val typeListVenue = MutableLiveData<MutableList<ResourceTypeLabel>>()
    /**
     * 景区720类型
     */
    fun getTypeList() {
        MainRepository.service.getSelectLabel(
            LabelType.SCENIC_THEME, ResourceType.CONTENT_TYPE_SCENERY
        ).excute(object : BaseObserver<ResourceTypeLabel>() {
            override fun onSuccess(response: BaseResponse<ResourceTypeLabel>) {
                typeList.postValue(response.datas)
            }
        })
    }

    /**
     * 场馆720类型
     */
    fun getTypeVenueList() {
        MainRepository.service.getSelectLabelVenue("VENUE_type").excute(object : BaseObserver<ResourceTypeLabel>() {
            override fun onSuccess(response: BaseResponse<ResourceTypeLabel>) {
                typeListVenue.postValue(response.datas)
            }
        })
    }

    /**
     * 分页管理器
     * https://www.xn--efvu3ql9f.com/api/config/api/dict/dictType?type=VENUE_type
     */
    val pageManager: PageManager by lazy { PageManager(10) }

    val dataList = MutableLiveData<List<PanoramicListBean>>()
    /**
     * 获取景区数据
     */
    fun getDataList(
        areaSiteSwitch: String,
        type: String,
        lat: String,
        lng: String
    ) {
        mPresenter.value?.loading = true
        MainRepository.service.getPanoramicList(areaSiteSwitch, type, lat, lng, pageManager.pageIndex.toString(), pageManager.pageSize.toString())
            .excute(object : BaseObserver<PanoramicListBean>() {
                override fun onSuccess(response: BaseResponse<PanoramicListBean>) {
                    dataList.postValue(response.datas)
                    mPresenter.value?.loading = false
                }

                override fun onFailed(response: BaseResponse<PanoramicListBean>) {
                    super.onFailed(response)
                    dataList.postValue(null)
                    ToastUtils.showMessage(response.message)
                }

            })
    }

    /**
     * 获取场馆数据
     */
    fun getVenueDataList(
        areaSiteSwitch: String,
        type: String,
        lat: String,
        lng: String
    ) {
        mPresenter.value?.loading = true
        MainRepository.service.getVenuePanoramicList(areaSiteSwitch, type, lat, lng, pageManager.pageIndex.toString(), pageManager.pageSize.toString())
            .excute(object : BaseObserver<PanoramicListBean>() {
                override fun onSuccess(response: BaseResponse<PanoramicListBean>) {
                    dataList.postValue(response.datas)
                    mPresenter.value?.loading = false
                }

                override fun onFailed(response: BaseResponse<PanoramicListBean>) {
                    super.onFailed(response)
                    dataList.postValue(null)
                    ToastUtils.showMessage(response.message)
                }
            })
    }
}