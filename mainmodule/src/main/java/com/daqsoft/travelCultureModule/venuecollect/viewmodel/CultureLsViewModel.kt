package com.daqsoft.travelCultureModule.venuecollect.viewmodel
import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.network.home.HomeRepository
import java.util.*

/**
 * @Description 陈列展览列表ViewModel
 */
class CultureLsViewModel : BaseViewModel() {

    val listdatas = MutableLiveData<MutableList<CultureListBean>>()

    val details = MutableLiveData<CultureDetailBean>()

    val topdatas = MutableLiveData<MutableList<ExhibitionTagBean>>()

    val pageManager = PageManager(10)

    var totleNumber:Int =0
    /**
     * 获取展览列表
     * 展厅类型：EXHIBITION_TYPE藏品分类：COLLECTION_MUSEUM_TYPE
     */
    fun getTopDatas() {
        HomeRepository.service.getCultureTopList("COLLECTION_MUSEUM_TYPE")
            .excute(object : BaseObserver<ExhibitionTagBean>() {
                override fun onSuccess(response: BaseResponse<ExhibitionTagBean>) {
                    topdatas.postValue(response.datas)

                }

                override fun onFailed(response: BaseResponse<ExhibitionTagBean>) {
                    topdatas.postValue(null)
                }
            })
    }
    /**
     * 获取展览列表
     */
    fun getListDatas(id:String,type:String="") {

        HomeRepository.service.getCultureList(id.toInt(),type,currPage = pageManager.pageIndex,pageSize = pageManager.pageSize)
            .excute(object : BaseObserver<CultureListBean>() {
                override fun onSuccess(response: BaseResponse<CultureListBean>) {
                    totleNumber= response.page?.total ?: 0
                    listdatas.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<CultureListBean>) {
                    listdatas.postValue(null)
                }
            })
    }

    /**
     * 获取展览列表
     */
    fun getTuiJListDatas(id:String,type:String="") {

        HomeRepository.service.getCultureListTJ(id.toInt(),type,currPage = pageManager.pageIndex,pageSize = pageManager.pageSize)
            .excute(object : BaseObserver<CultureListBean>() {
                override fun onSuccess(response: BaseResponse<CultureListBean>) {
                    totleNumber= response.page?.total ?: 0
                    listdatas.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<CultureListBean>) {
                    listdatas.postValue(null)
                }
            })
    }



    /**
     * 获取展览详情
     */
    fun getShowDetails(id:String) {
        mPresenter?.value?.loading=true
        HomeRepository.service.getCultureDetail(id.toInt())
            .excute(object : BaseObserver<CultureDetailBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<CultureDetailBean>) {
                    details.postValue(response.data)
                }


                override fun onFailed(response: BaseResponse<CultureDetailBean>) {
                    super.onFailed(response)
                    ToastUtils.showMessage(response.message)
                }
            })
    }
}