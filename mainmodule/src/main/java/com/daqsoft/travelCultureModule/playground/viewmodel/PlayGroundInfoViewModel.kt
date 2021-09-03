package com.daqsoft.travelCultureModule.playground.viewmodel
import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.PlayGroundDetailBean
import com.daqsoft.travelCultureModule.net.MainRepository

class PlayGroundInfoViewModel() : BaseViewModel() {


    /**
     * 娱乐场所详情
     */
    val playDetailLiveData: MutableLiveData<PlayGroundDetailBean> = MutableLiveData()

    /**
     * 获取娱乐场所详情
     * @param id String
     */
    fun getFoodDetail(id: String) {
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false

        MainRepository.service.getPlayGroundetail(id).excute(object : BaseObserver<PlayGroundDetailBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<PlayGroundDetailBean>) {
                playDetailLiveData.postValue(response.data)
            }

            override fun onFailed(response: BaseResponse<PlayGroundDetailBean>) {
                mError.postValue(response)
            }
        })
    }

}