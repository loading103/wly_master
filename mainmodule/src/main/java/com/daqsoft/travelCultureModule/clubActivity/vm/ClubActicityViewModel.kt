package com.daqsoft.travelCultureModule.clubActivity.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.travelCultureModule.clubActivity.bean.ClubBean
import com.daqsoft.travelCultureModule.clubActivity.bean.TypeBean
import com.daqsoft.travelCultureModule.net.MainRepository

class ClubActicityViewModel:BaseViewModel(){
    // 获取社团列表数据
    var clubList = MutableLiveData<MutableList<ClubBean>>()
    fun getClubList(content:String,siteId :String,type:String,curPage:String,pageSize:String){
        mPresenter.value?.loading = true
        MainRepository.service.getClubList(content,"",type,curPage,pageSize,siteId).excute(object:
            BaseObserver<ClubBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<ClubBean>) {
                clubList.postValue(response.datas)
            }
        })
    }
    // 关注资源
    fun attentionResource(resourceId :String ,resourceType:String){
        mPresenter.value?.loading = true
        CommentRepository.service.attentionResource(resourceId ,resourceType).excute(object :BaseObserver<Any>(mPresenter){
            override fun onSuccess(response: BaseResponse<Any>) {
            }
        })
    }
    //取消关注资源
    fun attentionResourceCancle(resourceId :String ,resourceType:String ){
        mPresenter.value?.loading = true
        CommentRepository.service.attentionResourceCancle(resourceId,resourceType).excute(object :BaseObserver<Any>(mPresenter){
            override fun onSuccess(response: BaseResponse<Any>) {
            }
        })
    }
    //获取地区
    val areas = MutableLiveData<MutableList<ChildRegion>>()
    fun getChildRegions(){
        mPresenter.value?.loading = true
        MainRepository.service.getChildRegions().excute(object : BaseObserver<ChildRegion>(mPresenter) {
            override fun onSuccess(response: BaseResponse<ChildRegion>) {
                areas.postValue(response.datas)
            }
        })
    }
    //获取类型
    val types=MutableLiveData<MutableList<TypeBean>>()
    fun getType(type:String){
        mPresenter.value?.loading = true
        MainRepository.service.getSearchType(type).excute(object : BaseObserver<TypeBean>(mPresenter){
            override fun onSuccess(response: BaseResponse<TypeBean>) {
                types.postValue(response.datas)
            }

        })
    }
}