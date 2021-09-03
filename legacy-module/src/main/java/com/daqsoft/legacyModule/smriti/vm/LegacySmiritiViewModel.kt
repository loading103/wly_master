package com.daqsoft.legacyModule.smriti.vm

import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.smriti.CONTENT_TYPE_HERITAGE_ITEM
import com.daqsoft.legacyModule.smriti.bean.LegacyBehalfBean
import com.daqsoft.legacyModule.smriti.bean.LegacyRecommendBean
import com.daqsoft.legacyModule.smriti.bean.TypeBean
import com.daqsoft.legacyModule.smriti.net.LegacySmritiRepository
import com.daqsoft.baselib.bean.ChildRegion

/**
 * desc :非遗传承viewmodel
 * @author 江云仙
 * @date 2020/4/21 15:14
 */
class LegacySmiritiViewModel  :BaseViewModel(){
    /**
     * 地区
     */
    val areas = MutableLiveData<MutableList<ChildRegion>>()
    /**
    *非遗推荐数据集合
    */
    var legacyRecommends= MutableLiveData<MutableList<LegacyRecommendBean>>()
    /**
    *非遗代表性项目
    */
    var legacyBehalfs= MutableLiveData<MutableList<LegacyBehalfBean>>()
    /**
    *级别筛选
    */
    var levelBeans= MutableLiveData<MutableList<TypeBean>>()
    // 批次
    var batchBeans= MutableLiveData<MutableList<TypeBean>>()
    // 类型
    var typeBeans= MutableLiveData<MutableList<TypeBean>>()

    var level = ""
    var batch = ""
    //类型
    var type = ""
    var region = ""
    var sortType = ""
    var areaSiteSwitch = ""
    var mCurrPage = 1
    var pageSize =10

    /**
    *获取非遗推荐列表数据
    */
    fun getLegaRecommendList() {
        val map = HashMap<String, Any>()
        LegacySmritiRepository.service.getRecommendList(map).excute(object : BaseObserver<LegacyRecommendBean>(){
            override fun onSuccess(response: BaseResponse<LegacyRecommendBean>) {
                legacyRecommends.postValue(response.datas)
            }

        })
    }
    /**
    *获取非遗推荐列表数据
    */
    fun getBehalfList() {
        mPresenter.value?.loading = false
        mPresenter.value?.isNeedRecyleView = false
        val map = HashMap<String, Any>()
        map["level"]=level
        map["batch"]=batch
        map["type"]=type
        map["region"]=region
        map["sortType"]=sortType
        map["areaSiteSwitch"]=areaSiteSwitch
        map["currPage"]=mCurrPage
        map["pageSize"]=pageSize
        LegacySmritiRepository.service.getBehalfList(map).excute(object : BaseObserver<LegacyBehalfBean>(mPresenter){
            override fun onSuccess(response: BaseResponse<LegacyBehalfBean>) {
                legacyBehalfs.postValue(response.datas)
            }
            override fun onFailed(response: BaseResponse<LegacyBehalfBean>) {
                mPresenter.value?.loading = false
                mError.postValue(response)
            }

        })
    }
    /**
    *级别筛选
    */
    fun getLevel(type:String) {
        val map = HashMap<String, Any>()
        map["type"]=type
        LegacySmritiRepository.service.getSearchType(map).excute(object : BaseObserver<TypeBean>(){
            override fun onSuccess(response: BaseResponse<TypeBean>) {
                levelBeans.postValue(response.datas)
            }

        })
    }
    /**
    *级别筛选
    */
    fun getBatch(type:String) {
        val map = HashMap<String, Any>()
        map["type"]=type
        LegacySmritiRepository.service.getSearchType(map).excute(object : BaseObserver<TypeBean>(){
            override fun onSuccess(response: BaseResponse<TypeBean>) {
                batchBeans.postValue(response.datas)
            }

        })
    }
    /**
    *级别筛选
    */
    fun getType(type:String) {
        val map = HashMap<String, Any>()
        map["type"]=type
        LegacySmritiRepository.service.getSearchType(map).excute(object : BaseObserver<TypeBean>(){
            override fun onSuccess(response: BaseResponse<TypeBean>) {
                typeBeans.postValue(response.datas)
            }

        })
    }
    /**
     * 收藏接口
     */
    fun collection(
        item: LegacyBehalfBean,
        imgCollect: ImageView
    ) {
        val map = HashMap<String, Any>()
        map["resourceId"]=item.id
        map["resourceType"]=CONTENT_TYPE_HERITAGE_ITEM
        LegacySmritiRepository.service.posClloection(map)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    if (response.code == 0) {
                        ToastUtils.showMessage("收藏成功~")
                        item.collectionStatus =1
                        imgCollect.setImageResource(R.mipmap.provider_collect_selected)
                    }

                }
            })
    }

    /**
     * 取消收藏接口
     */
    fun collectionCancel(
        item: LegacyBehalfBean,
        imgCollect: ImageView
    ) {
//        mPresenter.value?.loading = true
        val map = HashMap<String, Any>()
        map["resourceId"]=item.id
        map["resourceType"]=CONTENT_TYPE_HERITAGE_ITEM
        LegacySmritiRepository.service.posCollectionCancel(map)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    if (response.code == 0) {
                        ToastUtils.showMessage("取消收藏成功~")
                        item.collectionStatus =0
                        imgCollect.setImageResource(R.mipmap.provider_collect_normal)
                    }

                }
            })
    }

    /**
     * 站点下级区域(两层)
     */
    fun getChildRegions() {
        mPresenter?.value?.isNeedRecyleView = false
        mPresenter?.value?.loading = false
        LegacySmritiRepository.service.getChildRegions().excute(object : BaseObserver<ChildRegion>() {
            override fun onSuccess(response: BaseResponse<ChildRegion>) {
                areas.postValue(response.datas)
            }
            override fun onFailed(response: BaseResponse<ChildRegion>) {
                mError.postValue(response)
            }
        })
    }
}