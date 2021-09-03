package com.daqsoft.provider.businessview.viewmodel

import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.EmoticonsBean
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.comment.beans.CommentTagsBean
import com.daqsoft.provider.network.comment.beans.CommtentTagBean

/**
 * @Description
 * @ClassName   OrderCommentViewModel
 * @Author      luoyi
 * @Time        2020/7/21 9:40
 */
class OrderCommentViewModel : BaseViewModel() {
    // 输入的文本
    var countWord = MutableLiveData<String>()

    // 图片
    var images = ""

    /**
     * 选中的标签
     */
    var tagIds = ""

    /**
     * 评论便签
     */
    val commentTagsBeans = MutableLiveData<MutableList<CommtentTagBean>>()

    /**
     * 添加完成
     */
    val addFinish = MutableLiveData<Boolean>()

    /**
     * 获取输入的字数然后生成字符串显示
     */
    fun getWordCount(words: String) {
        countWord.postValue("${words.length}/200")
    }

    /**
     * 添加评论
     */
    fun addComment(resourceId: String, type: String, content: String, level: String, orderId: String?, emoticonsId: String) {
        var imageUrls = ""
        var videos = ""
        if (images != "") {
            var imags = images.split(",")
            val videoType = arrayListOf<String>("mp4", "avi", "wmv", "MP4", "AVI", "WMV")

            for (item in imags) {
                var videoStr = item.substring(item.lastIndexOf(".") + 1, item.length)
                // 新增视频资源是否上传完成，判断地址是不是本地地址
                if (videoType.contains(videoStr) && item.startsWith("http")) {
                    videos += "$item,"
                } else if (item.startsWith("http")) {
                    imageUrls += "$item,"
                }
            }
            if (imageUrls.isNotEmpty()) {
                imageUrls = imageUrls.substring(0, imageUrls.lastIndexOf(","))
            }
        }
        var currOrderId: String? = orderId
        if (orderId.isNullOrEmpty()) {
            currOrderId = ""
        }
        CommentRepository.service.postAddComment(resourceId, type, content, imageUrls, tagIds, videos, currOrderId, emoticonsId)
            .excute(object : BaseObserver<String>() {
                override fun onSuccess(response: BaseResponse<String>) {
                    toast.postValue(response.message)
                    if (response.code == 0) {
                        addFinish.postValue(true)
                    } else {
                        addFinish.postValue(false)
                    }
                }

                override fun onFailed(response: BaseResponse<String>) {
                    if (response.code == 2) {
                        toast.postValue("请先登录")
                        ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY).navigation()
                    } else {
                        toast.postValue(response.message)
                    }
                    addFinish.postValue(false)
                    mError.postValue(response)
                }

            })

    }


    /**
     * 根据分组，获取评论便签
     */
    fun getCommentTagCountInfo(resourceId: String, resourceType: String) {
        CommentRepository.service.getCommentTag(resourceType, resourceId)
            .excute(object : BaseObserver<CommtentTagBean>() {
                override fun onSuccess(response: BaseResponse<CommtentTagBean>) {
                    commentTagsBeans.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<CommtentTagBean>) {
                    mError.postValue(response)
                }
            })
    }

    var currEmotionPage: Int = 1
    var emoTionPageSize: Int = 10

    var emoticonsLd: MutableLiveData<BaseResponse<EmoticonsBean>> = MutableLiveData()

    fun getEmoticons() {
        mPresenter?.value?.loading = false
        CommentRepository.service.getEmotList(currEmotionPage, emoTionPageSize).excute(object :
            BaseObserver<EmoticonsBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<EmoticonsBean>) {
                emoticonsLd.postValue(response)
            }

            override fun onFailed(response: BaseResponse<EmoticonsBean>) {
                emoticonsLd.postValue(null)
            }

        })
    }
}