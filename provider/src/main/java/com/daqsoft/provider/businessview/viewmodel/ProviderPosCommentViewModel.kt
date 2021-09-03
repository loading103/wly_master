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

/**
 * @Description
 * @ClassName   ProviderPosCommentViewModel
 * @Author      luoyi
 * @Time        2020/4/13 11:01
 */
class ProviderPosCommentViewModel : BaseViewModel() {
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
    val commentTagsBeans = MutableLiveData<HashMap<String, CommentTagsBean>>()

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
    fun addComment(
        resourceId: String,
        type: String,
        content: String,
        level: String,
        orderId: String?,
        emoticonsIds: String = "",
        startNumber : String?
    ) {
        var imageUrls = ""
        var videos = ""
        if (images != "") {
            var imags = images.split(",")
            val videoType = arrayListOf<String>("mp4", "avi", "wmv", "MP4", "AVI", "WMV")

            for (item in imags) {
                var videoStr = item.substring(item.lastIndexOf(".") + 1, item.length)
                if (videoType.contains(videoStr)) {
                    videos += "$item,"
                } else {
                    imageUrls += "$item,"
                }
            }
            if (!imageUrls.isNullOrEmpty()) {
                imageUrls = imageUrls.substring(0, imageUrls.lastIndexOf(","))
            }
        }
        var currOrderId: String? = orderId
        if (orderId.isNullOrEmpty()) {
            currOrderId = ""
        }

        CommentRepository.service.postAddCommentNew(
            resourceId,
            type,
            content,
            level,
            imageUrls,
            tagIds,
            videos,
            currOrderId,
            emoticonsIds,
            startNumber
        ).excute(object : BaseObserver<String>() {
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
                    ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                        .navigation()
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
        CommentRepository.service.getCommentTagCountInfo(resourceType, resourceId, true)
            .excute(object : BaseObserver<HashMap<String, CommentTagsBean>>() {
                override fun onSuccess(response: BaseResponse<HashMap<String, CommentTagsBean>>) {
                    commentTagsBeans.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<HashMap<String, CommentTagsBean>>) {
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