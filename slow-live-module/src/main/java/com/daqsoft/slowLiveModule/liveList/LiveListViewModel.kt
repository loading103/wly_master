package com.daqsoft.slowLiveModule.liveList

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.PageManager
import com.daqsoft.slowLiveModule.bean.LiveContentTypeBean
import com.daqsoft.slowLiveModule.bean.LiveListBean
import com.daqsoft.slowLiveModule.bean.LiveTopBean
import com.daqsoft.slowLiveModule.net.LiveRepository


/**
 * @des     LiveListViewModel
 * @class   LiveListViewModel
 * @author  Wongxd
 * @date    2020-4-15  15:18
 *
 */
internal class LiveListViewModel : BaseViewModel() {
    /**
     * 分页管理器
     */
    val pageManager: PageManager by lazy { PageManager(10) }


    val liveList: MutableLiveData<List<LiveListBean>> = MutableLiveData()

    fun getLiveList() {
        mPresenter.value?.error = false
        mPresenter.value?.loading = true
        LiveRepository.service.getLiveList(pageManager.pageSize.toString(), pageManager.pageIndex.toString())
            .excute(object : BaseObserver<LiveListBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<LiveListBean>) {
                    mPresenter.value?.loading = false
                    liveList.postValue(response.datas)
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    mPresenter.value?.error = true
                    mPresenter.value?.loading = false
                }
            })
    }


    val liveTop: MutableLiveData<MutableList<LiveTopBean>> = MutableLiveData()

    fun getLiveTop() {
        mPresenter.value?.loading = true

        LiveRepository.service.getLiveTop().excute(object : BaseObserver<LiveTopBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<LiveTopBean>) {
                mPresenter.value?.error = false
                mPresenter.value?.loading = false
                mPresenter.value?.isNeedRecyleView = true

                liveTop.postValue(response.datas)
                pageManager.initPageIndex()

                getLiveContentType()
                getLiveList()
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                mPresenter.value?.loading = false
                mPresenter.value?.error = true
                mPresenter.value?.isNeedRecyleView = true
            }

            override fun onFailed(response: BaseResponse<LiveTopBean>) {
                super.onFailed(response)
                mPresenter.value?.loading = false
                mPresenter.value?.error = true
                mPresenter.value?.isNeedRecyleView = true
            }
        })
    }


    val liveContentTypeList: MutableLiveData<LiveContentTypeBean> = MutableLiveData()

    fun getLiveContentType() {

        LiveRepository.service.getContentType("mzb").excute(object : BaseObserver<LiveContentTypeBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<LiveContentTypeBean>) {
                liveContentTypeList.postValue(response.data)
            }
        })

    }

}