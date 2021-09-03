package com.daqsoft.volunteer.volunteer.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.volunteer.bean.VolunteerBean
import com.daqsoft.volunteer.bean.VolunteerServiceRecordBean
import com.daqsoft.volunteer.bean.VolunteerTeamListBean
import com.daqsoft.volunteer.net.VolunteerRepository

/**
 *@package:com.daqsoft.volunteer.volunteer.vm
 *@date:2020/6/4 16:21
 *@author: caihj
 *@des:TODO
 **/
class VolunteerDetailVM:BaseViewModel() {

    var volunteer = MutableLiveData<VolunteerBean>()
    var id = ""

    var services = MutableLiveData<MutableList<VolunteerServiceRecordBean>>()


    fun getVolunteer(){
        VolunteerRepository.service.getVolunteerDetail(id).excute(object :BaseObserver<VolunteerBean>(){
            override fun onSuccess(response: BaseResponse<VolunteerBean>) {
                volunteer.postValue(response.data)
            }
        })
    }

    /**
     * 获取志愿服务记录
     */
    fun getServiceRecord(){
        VolunteerRepository.service.getVolunteerServices(id).excute(object :BaseObserver<VolunteerServiceRecordBean>(){
            override fun onSuccess(response: BaseResponse<VolunteerServiceRecordBean>) {
                services.postValue(response.datas)
            }
        })
    }

    var teams = MutableLiveData<MutableList<VolunteerTeamListBean>>()


    /**
     * 获取所属团队列表
     */
    fun getVolunteerTeam(){
        VolunteerRepository.service.getVolunteerTeams(id).excute(object :BaseObserver<VolunteerTeamListBean>(){
            override fun onSuccess(response: BaseResponse<VolunteerTeamListBean>) {
                teams.postValue(response.datas)
            }

        })
    }


    var likeStatus = MutableLiveData<Boolean>()
    var cancelLikeStatus = MutableLiveData<Boolean>()
    /**
     * 点赞
     */
    fun addLike(){
        CommentRepository.service.postThumbUp(id, ResourceType.CONTENT_TYPE_VOLUNTEER).excute(object :BaseObserver<Any>(){
            override fun onSuccess(response: BaseResponse<Any>) {
                if(response.code == 0){
                    toast.postValue("感谢您的点赞")
                    likeStatus.postValue(true)
                }else{
                    toast.postValue(response.message)
                }
            }

            override fun onError(e: Throwable) {
                toast.postValue("点赞失败!")
            }

        })
    }

    /**
     * 取消点赞
     */
    fun cancelLike(){
        CommentRepository.service.postThumbCancel(id, ResourceType.CONTENT_TYPE_VOLUNTEER).excute(object :BaseObserver<Any>(){
            override fun onSuccess(response: BaseResponse<Any>) {
                if(response.code == 0){
                    toast.postValue("成功取消")
                    cancelLikeStatus.postValue(true)
                }else{
                    toast.postValue(response.message)
                }
            }

            override fun onError(e: Throwable) {
                toast.postValue("取消点赞失败")
            }
        })
    }


    var collectStatus = MutableLiveData<Boolean>()
    var cancelCollectStatus = MutableLiveData<Boolean>()

    /**
     * 收藏
     */
    fun collect(){
        CommentRepository.service.posClloection(id,ResourceType.CONTENT_TYPE_VOLUNTEER).excute(object :BaseObserver<Any>(){
            override fun onSuccess(response: BaseResponse<Any>) {
                if(response.code == 0){
                    toast.postValue("感谢您的收藏")
                    collectStatus.postValue(true)
                }else{
                    toast.postValue(response.message)
                }
            }

            override fun onError(e: Throwable) {
                toast.postValue("收藏失败")
            }
        })
    }

    /**
     * 取消收藏
     */
    fun cancelCollect(){
        CommentRepository.service.posCollectionCancel(id,ResourceType.CONTENT_TYPE_VOLUNTEER).excute(object :BaseObserver<Any>(){
            override fun onSuccess(response: BaseResponse<Any>) {
                if(response.code == 0){
                    toast.postValue("成功取消")
                    cancelCollectStatus.postValue(true)
                }else{
                    toast.postValue(response.message)
                }
            }

            override fun onError(e: Throwable) {
                toast.postValue("取消收藏失败")
            }
        })
    }

}