package com.daqsoft.provider.businessview.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.EmoticonsBean
import com.daqsoft.provider.network.comment.CommentRepository

/**
 * @Description
 * @ClassName   ProviderAddEmoticonsLsViewModel
 * @Author      luoyi
 * @Time        2020/11/2 10:52
 */
class ProviderAddEmoticonsLsViewModel : BaseViewModel() {

    var currPage: Int = 1
    var pageSize: Int = 10

    var emoticonsLd: MutableLiveData<BaseResponse<EmoticonsBean>> = MutableLiveData()

    fun getEmoticons() {
        mPresenter?.value?.loading = false
        CommentRepository.service.getEmotList(currPage, pageSize).excute(object :
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