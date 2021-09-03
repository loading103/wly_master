package com.daqsoft.travelCultureModule.country.model

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.CountryListBean
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.travelCultureModule.country.CONTENT_TYPE_AGRITAINMENT
import com.daqsoft.travelCultureModule.country.TYPE
import com.daqsoft.travelCultureModule.country.bean.ValueKeyBean
import com.daqsoft.travelCultureModule.country.net.CountryRepository

/**
 * 全部乡村列表ViewModel
 */
class CountryAllMoreViewModel : BaseViewModel() {
    // 标签类型
    var labelType = TYPE

    // 资源类型
    var resourceType = CONTENT_TYPE_AGRITAINMENT

    // 分页页码
    var mCurrPage = 1

    // 获取页码大小
    var mPageSize = 10

    // 地区编码
    var region = ""

    // 经纬度
    var lat = ""
    var lon = ""

    //排序方式
    var sortType = ""

    //下级站点ID
    var areaSiteSwitch = ""

    val sorts = mutableListOf(
        ValueKeyBean("不限", "", true),
        ValueKeyBean("推荐排序", "recommendHomePage"),
        ValueKeyBean("热度排行", "hot"),
        ValueKeyBean("距离排行", "disNum")
    )

    /**
     * 站点下级区域(两层)
     */
    val areas = MutableLiveData<MutableList<ChildRegion>>()
    fun getChildRegions() {
        mPresenter.value?.isNeedRecyleView = false
        mPresenter.value?.loading = false
        CountryRepository.service.getChildRegions().excute(object : BaseObserver<ChildRegion>() {
            override fun onSuccess(response: BaseResponse<ChildRegion>) {
                areas.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<ChildRegion>) {
                mError.postValue(response)
            }
        })
    }

    /**
     * 全部乡村列表
     */
    val countryList = MutableLiveData<MutableList<CountryListBean>>()
    fun getCountryAllList(siteId:String?=null) {
        mPresenter.value?.loading = false
        mPresenter.value?.isNeedRecyleView = false
        val map = HashMap<String, Any>()
        map["currPage"] = mCurrPage
        map["pageSize"] = "20"
        map["region"] = region
        map["keyword"] = ""
        map["sortType"] = sortType
        map["lng"] = lon
        map["lat"] = lat
        siteId?.let { map["areaSiteSwitch"] = siteId }

        CountryRepository.service.getApiRuralList(map)
            .excute(object : BaseObserver<CountryListBean>() {
                override fun onSuccess(response: BaseResponse<CountryListBean>) {
                    countryList.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<CountryListBean>) {
                    mError.postValue(response)
                }
            })
    }

    /**
     * 收藏接口
     */
    var collectLiveData: MutableLiveData<Int> = MutableLiveData()
    fun collectionScenic(resourceId: String, position: Int) {
        mPresenter.value?.loading = false
        mPresenter.value?.isNeedRecyleView = false
        CommentRepository.service.posClloection(resourceId, ResourceType.CONTENT_TYPE_COUNTRY)
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
    var canceCollectLiveData: MutableLiveData<Int> = MutableLiveData()
    fun collectionCancelScenic(resourceId: String, position: Int) {
        mPresenter.value?.loading = false
        mPresenter.value?.isNeedRecyleView = false
        CommentRepository.service.posCollectionCancel(resourceId, ResourceType.CONTENT_TYPE_COUNTRY)
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
}