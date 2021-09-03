package com.daqsoft.travelCultureModule.resource

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.travelCultureModule.net.MainRepository

/**
 * @Description 景区列表的viewModel
 * @ClassName   HotActivitiesFragmentViewModel
 * @Author      PuHua
 * @Time        2019/12/25 10:42
 */
abstract class BaseResourceViewModel : BaseViewModel() {

    val param = HashMap<String, String>()

    /**
     * 条件列表
     */
    val selectLabels = MutableLiveData<MutableList<MutableList<ResourceTypeLabel>>>()
    /**
     * 地区
     */
    val areas = MutableLiveData<MutableList<ChildRegion>>()


    /**
     * 站点下级区域(两层)
     */
    fun getChildRegions() {
        mPresenter.value?.loading = false
        mPresenter.value?.isNeedRecyleView=false
        MainRepository.service.getChildRegions().excute(object : BaseObserver<ChildRegion>() {
            override fun onSuccess(response: BaseResponse<ChildRegion>) {
                areas.postValue(response.datas)
            }
        })
    }


    abstract fun getList(param: HashMap<String, String>)
    /**
     * 获取景区类型条件选择项
     */
    abstract fun getSelectLabel()


    /**
     * 收藏接口
     */
    fun collection(resourceId: String) {
        mPresenter.value?.loading = true
        CommentRepository.service.posClloection(resourceId, ResourceType.CONTENT_TYPE_ACTIVITY)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    if (response.code == 0) {
                        toast.postValue("收藏成功!")
                    }else{
                        toast.postValue(response.message)
                    }

                }
            })
    }

    /**
     * 取消收藏接口
     */

    fun collectionCancel(resourceId: String) {
        mPresenter.value?.loading = true
        CommentRepository.service.posCollectionCancel(
            resourceId,
            ResourceType.CONTENT_TYPE_ACTIVITY
        )
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {

                    if (response.code == 0) {
                        toast.postValue("取消收藏成功!")
                    }

                }
            })
    }
}