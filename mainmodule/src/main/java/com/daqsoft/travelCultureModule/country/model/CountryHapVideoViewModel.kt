package com.daqsoft.travelCultureModule.country.model

import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.mainmodule.R
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.travelCultureModule.country.CONTENT_TYPE_AGRITAINMENT
import com.daqsoft.travelCultureModule.country.TYPE
import com.daqsoft.travelCultureModule.country.adapter.CountryHappinessMoreAdapter
import com.daqsoft.travelCultureModule.country.bean.AgritainmentBean
import com.daqsoft.travelCultureModule.country.bean.ResourceTypeLabel
import com.daqsoft.travelCultureModule.country.bean.ValueKeyBean
import com.daqsoft.travelCultureModule.country.net.CountryRepository

/**
 * desc :
 * @author 江云仙
 * @date 2020/4/15 16:40
 */
class CountryHapVideoViewModel :BaseViewModel(){
    //标签类型
    var labelType = TYPE
    //资源类型
    var resourceType = CONTENT_TYPE_AGRITAINMENT
    /**
     * 地区
     */
    val areas = MutableLiveData<MutableList<ChildRegion>>()
    val types = mutableListOf(
        ResourceTypeLabel("", "", "", "1", "农家乐类型").setSelects(true)
    )
    /**
     * 农家乐类型
     */
    val countryHappinessTypes = MutableLiveData<MutableList<ResourceTypeLabel>>()
    /**
     * 分页页码
     */
    var mCurrPage = 1
    /**
     * 获取页码大小
     */
    var mPageSize = 10
    //地区编码
    var region = ""
    //类型
    var type = ""
    //等级
    var level = ""
    //设施
    var eqt = ""
    //经纬度
    var lat = ""
    var lon = ""
    //下级站点ID
    var areaSiteSwitch = ""
    //排序方式
    var sortType = ""
    /**
     *农家乐集合数据
     */
    var agritainment = MutableLiveData<MutableList<AgritainmentBean>>()
    // 排序
    val sorts = mutableListOf(
        ValueKeyBean("不限", "", true),
        ValueKeyBean("可订优先", "order"),
        ValueKeyBean("诚信优先", "credit"),
        ValueKeyBean("距离优先", "distance"),
        ValueKeyBean("推荐优先", "recommend"),
        ValueKeyBean("人气优先", "popularity"),
        ValueKeyBean("好评优先", "praise")
    )
    fun getAgritainmentList() {
        val map = HashMap<String, Any>()
        map["currPage"] = mCurrPage
        map["pageSize"] = mPageSize
        map["region"] = region
        map["type"] = type
        map["lat"] = lat
        map["lon"] = lon
        map["areaSiteSwitch"] = areaSiteSwitch
        map["sortType"] = sortType
        CountryRepository.service.agritainmentList(map).excute(object : BaseObserver<AgritainmentBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<AgritainmentBean>) {
                agritainment.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<AgritainmentBean>) {
//                super.onFailed(response)
                mError.postValue(response)
            }
        })
    }
    /**
     * 站点下级区域(两层)
     */
    fun getChildRegions() {
        mPresenter?.value?.isNeedRecyleView = false
        mPresenter?.value?.loading = false
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
     * 获取农家乐类型
     */
    fun getCountryHappinessTypes() {
        val map = HashMap<String, String>()
        map["labelType"] = labelType
        map["resourceType"] = resourceType
        CountryRepository.service.getSelectResLabel(map)
            .excute(object : BaseObserver<ResourceTypeLabel>() {
                override fun onSuccess(response: BaseResponse<ResourceTypeLabel>) {
                    countryHappinessTypes.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<ResourceTypeLabel>) {
                    mError.postValue(response)
                }
            })
    }
    /**
     * 收藏接口
     */
    fun collection(
        resourceId: String,
        imgCountryHapCollect: ImageView,
        item: AgritainmentBean,
        countryHappinessMoreAdapter: CountryHappinessMoreAdapter
    ) {
        CommentRepository.service.posClloection(resourceId, CONTENT_TYPE_AGRITAINMENT)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    if (response.code == 0) {
                        toast.postValue("收藏成功~")
                        item.collectionStatus = "1"
                        countryHappinessMoreAdapter.notifyDataSetChanged()
//                        imgCountryHapCollect.setImageResource(R.mipmap.activity_collect_selected)
                    }

                }
            })
    }

    /**
     * 取消收藏接口
     */
    fun collectionCancel(
        resourceId: String,
        imgCountryHapCollect: ImageView,
        item: AgritainmentBean,
        countryHappinessMoreAdapter: CountryHappinessMoreAdapter
    ) {
//        mPresenter.value?.loading = true
        CommentRepository.service.posCollectionCancel(resourceId, CONTENT_TYPE_AGRITAINMENT)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    if (response.code == 0) {
                        toast.postValue("取消收藏成功~")
//                        notify.postValue(response)
                        item.collectionStatus = "0"
                        countryHappinessMoreAdapter.notifyDataSetChanged()
                        imgCountryHapCollect.setImageResource(R.mipmap.activity_collect_normal)
                    }

                }
            })
    }
}