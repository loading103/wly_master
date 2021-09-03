package com.daqsoft.travelCultureModule.lecturehall.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.comment.beans.CommentBean

/**
 * @Description
 * @ClassName   LectureCommentViewModel
 * @Author      luoyi
 * @Time        2020/6/16 13:41
 */
class LectureCommentViewModel : BaseViewModel() {

    val commentBeans: MutableLiveData<MutableList<CommentBean>> = MutableLiveData()

    fun getLectureCommentList(id: String, currPage: Int) {
        val param = HashMap<String, String>()
        param["resourceType"] = "CONTENT_TYPE_COURSE"
        param["resourceId"] = id
        param["pageSize"] = "10"
        param["currPage"] = currPage.toString()
        CommentRepository.service.getCommentList(param)
            .excute(object : BaseObserver<CommentBean>() {
                override fun onSuccess(response: BaseResponse<CommentBean>) {
                    commentBeans.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<CommentBean>) {
                    commentBeans.postValue(null)
                }
            })
    }

}