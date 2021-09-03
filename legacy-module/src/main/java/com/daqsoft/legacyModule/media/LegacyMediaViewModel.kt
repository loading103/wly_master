package com.daqsoft.legacyModule.media

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.legacyModule.media.bean.LegacyMediaListBean
import com.daqsoft.legacyModule.net.LegacyRepository
import com.daqsoft.provider.bean.PageManager

/**
 * @des    视听非遗 ViewModel
 * @class  LegacyMediaViewModel
 * @author  Wongxd
 * @date    2020-4-21  15:20
 */
internal class LegacyMediaViewModel : BaseViewModel() {


    val pageManager = PageManager(10)

    val mediaList = MutableLiveData<MutableList<LegacyMediaListBean>>()

    fun getMediaList(mediaType: String) {
        mPresenter.value?.loading = true
        LegacyRepository.service.getMediaList(pageManager.pageIndex.toString(), pageManager.pageSize.toString(), mediaType)
            .excute(object : BaseObserver<LegacyMediaListBean>() {
                override fun onSuccess(response: BaseResponse<LegacyMediaListBean>) {
                    mPresenter.value?.loading = false
                    mediaList.postValue(response.datas)
                }
            })
    }

}