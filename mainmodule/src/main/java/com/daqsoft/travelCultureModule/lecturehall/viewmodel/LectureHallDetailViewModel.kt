package com.daqsoft.travelCultureModule.lecturehall.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.LectureHallDetailBean
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.travelCultureModule.net.MainRepository

/**
 * @Description
 * @ClassName   LectureHallDetailViewModel
 * @Author      luoyi
 * @Time        2020/6/15 11:47
 */
class LectureHallDetailViewModel : BaseViewModel() {


    var lectureHallDetailLd: MutableLiveData<LectureHallDetailBean> = MutableLiveData()

    var lectureCommentLd: MutableLiveData<Boolean> = MutableLiveData()

    var collectLd: MutableLiveData<Boolean> = MutableLiveData()

    var canceCollectLd: MutableLiveData<Boolean> = MutableLiveData()

    /**
     * @param id 课程id
     */
    fun getLectureHallDetail(id: String) {
        mPresenter?.value?.loading = false

        MainRepository.service.getLectureHallDetail(id)
            .excute(object : BaseObserver<LectureHallDetailBean>() {
                override fun onSuccess(response: BaseResponse<LectureHallDetailBean>) {
                    lectureHallDetailLd?.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<LectureHallDetailBean>) {
                    lectureHallDetailLd?.postValue(null)
                }

            })
    }

    /**
     * @param  id 课程id
     * @param content 评论内容
     */
    fun postLectureHallComment(id: String, content: String) {
        mPresenter?.value?.loading = false
        CommentRepository.service.postAddComment(id, "CONTENT_TYPE_COURSE", content)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    if (response.code == 0) {
                        lectureCommentLd?.postValue(true)
                    } else {
                        lectureCommentLd?.postValue(false)
                    }
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    lectureCommentLd?.postValue(false)
                }

            })
    }

    /**
     * 收藏
     */
    fun collect(resourceId: String) {
        CommentRepository.service.posClloection(resourceId, ResourceType.CONTENT_TYPE_COURSE)
            .excute(object
                : BaseObserver<Any>() {
                override fun onSuccess(response: BaseResponse<Any>) {
                    collectLd.postValue(true)
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    collectLd.postValue(false)
                }
            })
    }

    /**
     * 取消收藏
     */
    fun canceCollect(resourceId: String) {
        CommentRepository.service.posCollectionCancel(resourceId, ResourceType.CONTENT_TYPE_COURSE)
            .excute(object
                : BaseObserver<Any>() {
                override fun onSuccess(response: BaseResponse<Any>) {
                    canceCollectLd.postValue(true)
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    canceCollectLd.postValue(false)
                }
            })
    }

    /**
     * 上传学习记录
     */
    fun postStudyRecorder(id: String, duration: Int) {
        MainRepository.service.postLectureHallRecorder(id, duration.toString())
            .excute(object : BaseObserver<Any>() {
                override fun onSuccess(response: BaseResponse<Any>) {

                }

            })
    }
}