package com.daqsoft.travelCultureModule.venuecollect.viewmodel
import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.home.HomeRepository
import java.util.HashMap

/**
 * @Description 陈列展览列表ViewModel
 */
class ExhibitionLsViewModel : BaseViewModel() {

    val listdatas = MutableLiveData<MutableList<VenueCollectBean>>()

    val details = MutableLiveData<VenueCollectDetailBean>()

    /**
     * 获取地图资源
     */
    var tjdatas =MutableLiveData<MutableList<VenueCollectBean>>()

    /**
     * 顶部标签
     */
    var topdatas = mutableListOf<ExhibitionTagBean>(
        ExhibitionTagBean("1", "临展特展",true),
        ExhibitionTagBean("2", "常设展览"),
        ExhibitionTagBean("3", "线上展览")
    )


    /**
     * 获取常设展览列表
     */
    fun getTopListDatas() {
        HomeRepository.service.getExhibitionShowList("",0, pageManager.pageIndex, pageManager.pageSize)
            .excute(object : BaseObserver<VenueCollectBean>() {
                override fun onSuccess(response: BaseResponse<VenueCollectBean>) {
                    listdatas.postValue(response.datas)
                    totleNumber= response.page?.total!!
                }

                override fun onFailed(response: BaseResponse<VenueCollectBean>) {
                    listdatas.postValue(null)
                }
            })
    }
    /**
     * 获取线上展览列表
     */
    fun getOnLineListDatas() {
        HomeRepository.service.getExhibitionOnlineList("","",0, pageManager.pageIndex, pageManager.pageSize)
            .excute(object : BaseObserver<VenueCollectBean>() {
                override fun onSuccess(response: BaseResponse<VenueCollectBean>) {
                    listdatas.postValue(response.datas)
                    totleNumber= response.page?.total!!
                }

                override fun onFailed(response: BaseResponse<VenueCollectBean>) {
                    listdatas.postValue(response.datas)
                }
            })
    }


    /**
     * 获取展览详情
     */
    fun getShowDetails(id:String) {
        mPresenter?.value?.loading=true
        HomeRepository.service.getExhibitionDetail(id.toInt())
            .excute(object : BaseObserver<VenueCollectDetailBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<VenueCollectDetailBean>) {
                    details.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<VenueCollectDetailBean>) {
                    super.onFailed(response)
                    details.postValue(null)
                    ToastUtils.showMessage(response.message)
                }
            })
    }

    /**
     * 获取相关推介列表
     */
    fun getTjListDatas(id: String) {
        HomeRepository.service.getExhibitionShowList(onId = id.toInt())
            .excute(object : BaseObserver<VenueCollectBean>() {
                override fun onSuccess(response: BaseResponse<VenueCollectBean>) {
                    tjdatas.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<VenueCollectBean>) {
                    tjdatas.postValue(null)
                }
            })
    }

    /**
     * 获取所有文物
     */
    val mWdatas = MutableLiveData<MutableList<CultureListBean>>()
    fun getWwListDatas(id: String) {
        HomeRepository.service.getCultureList(onId = id.toInt(),exhibitionId = id.toInt())
            .excute(object : BaseObserver<CultureListBean>() {
                override fun onSuccess(response: BaseResponse<CultureListBean>) {
                    mWdatas.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<CultureListBean>) {
                    mWdatas.postValue(null)
                }
            })
    }


    /**
     * 收藏接口
     */
    var collectLiveData: MutableLiveData<Int> = MutableLiveData()
    var canceCollectLiveData: MutableLiveData<Int> = MutableLiveData()

    fun collectionScenic(resourceId: String, position: Int, type: String) {
        mPresenter.value?.loading = true
        CommentRepository.service.posClloection(resourceId,type)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    ToastUtils.showMessage("已收藏~")
                    collectLiveData.postValue(position)
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    ToastUtils.showMessage("收藏失败，请稍后再试~")
                    mError.postValue(response)
                }
            })
    }

    /**
     * 取消收藏接口
     */

    fun collectionCancelScenic(resourceId: String, position: Int, type: String) {
        mPresenter.value?.loading = true
        CommentRepository.service.posCollectionCancel(resourceId, type)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    ToastUtils.showMessage("已取消收藏~")
                    canceCollectLiveData.postValue(position)
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    ToastUtils.showMessage("取消收藏失败，请稍后再试~")
                    mError.postValue(response)
                }
            })
    }



    /**
     * 获取临展特展
     */
    val pageManager = PageManager(10)
    var totleNumber:Int =0
    var activities: MutableLiveData<MutableList<ActivityBean>> = MutableLiveData()
    fun getActivityList(classId: String) {
        val param = HashMap<String, String>()
        param["classifyId"] = "0"
        param["pageSize"] = pageManager.pageSize.toString()
        param["currPage"] =pageManager.pageIndex.toString()
        HomeRepository.service.getActivityList(param)
            .excute(object : BaseObserver<ActivityBean>() {
                override fun onSuccess(response: BaseResponse<ActivityBean>) {
                    activities.postValue(response.datas)
                    totleNumber= response.page?.total!!
                }

                override fun onFailed(response: BaseResponse<ActivityBean>) {
                    activities.postValue(null)
                }
            })
    }

}