package com.daqsoft.integralmodule.ui.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.integralmodule.R
import com.daqsoft.integralmodule.repository.IntegralRepository
import com.daqsoft.integralmodule.repository.bean.*
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.ElectronicLogin
import com.daqsoft.provider.bean.ListStatusBean
import com.daqsoft.provider.bean.ProductListBean
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.network.net.ElectronicObserver
import com.daqsoft.provider.network.net.ElectronicRepository
import com.daqsoft.provider.network.net.excut
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject

/**
 * 积分会员商城首页Vm
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-11-21
 * @since JDK 1.8.0_191
 */
class MemberHomeActivityVm : BaseViewModel() {

    val repository = IntegralRepository()
    /**
     * 任务列表
     */
    val taskListData = MutableLiveData<MutableList<TaskListBean>>()
    /**
     * 商品列表
     */
    val productListData = MutableLiveData<ProductListBean>()
    /**
     * 积分数量
     */
    val data = MutableLiveData<PointCountBean>()
    /**
     * 进度条
     */
    val progress = MutableLiveData<String>()
    /**
     * 领取任务
     */
    val getTaskLiveData = MutableLiveData<ListStatusBean>()
    /**
     * 完成任务
     */
    val completeLiveData = MutableLiveData<ListStatusBean>()
    /**
     * 签到任务
     */
    var checkInResult = MutableLiveData<BaseResponse<Any>>()
    /**
     * 商铺编码
     */
    var shopCode: String? = ""

    fun pointCount() {
        repository.pointCount()
            .excute(object : BaseObserver<PointCountBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<PointCountBean>) {
                    data.postValue(response.data)
                    var nextLevelPoint: String = "0"
                    if (!response?.data?.nextLevelPoint.isNullOrEmpty()) {
                        nextLevelPoint = response?.data?.nextLevelPoint!!
                    }
                    progress.postValue("${response.data?.totalPoints}/${nextLevelPoint}")
                }
            })
    }


    /**
     * 设置任务状态
     */
    fun setStatus(finishStatus: Int): String {
        return when (finishStatus) {
            0 -> return BaseApplication.context.getString(R.string.integralmodule_unaccalimed)
            1 -> return BaseApplication.context.getString(R.string.integralmodule_pending)
            2 -> return BaseApplication.context.getString(R.string.integralmodule_completed_not_reveived)
            3 -> return BaseApplication.context.getString(R.string.integralmodule_expired)
            4 -> return BaseApplication.context.getString(R.string.integralmodule_completed_received)
            else -> return ""
        }
    }

    /**
     * 签到
     */
    fun getCheckIn() {
        HomeRepository.service.getUserCheckIn()
            .excute(object : BaseObserver<Any>() {
                override fun onSuccess(response: BaseResponse<Any>) {
                    checkInResult.postValue(response)
                }

            })
    }


    /**
     * 获取积分任务
     */
    fun getApiTaskList() {
        val ja = JSONArray()
        val jo = JSONObject()
        jo.put("userId", SPUtils.getInstance().getInt(SPKey.VIPID))
        jo.put("cruxValue", SPUtils.getInstance().getInt(SPKey.SITEID))
        jo.put("publishChannel", "CULTURAL")
        jo.put("code", BaseApplication.siteCode)
        jo.put("jumpType", "APP")
        ja.put(jo)
        repository.getApiTaskList(ja.toString())
            .excute(object : BaseObserver<TaskListBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<TaskListBean>) {
                    taskListData.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<TaskListBean>) {
                    mError.postValue(response)
                }
            })
    }

    /**
     * 站点详情
     */
    fun siteInfo() {
        repository.siteInfo()
            .excute(object : BaseObserver<SiteInfoBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<SiteInfoBean>) {
                    shopCode = response.data?.shopCode
                    freeLogin()

                }
            })
    }

    fun freeLogin() {
        ElectronicRepository.electronicService.login(
            SPUtils.getInstance().getString(SPKey.UID),
            SPUtils.getInstance().getString(SPKey.USERCENTERTOKEN),
            SPUtils.getInstance().getString(SPKey.ENCRYPTION)
        )
            .excut(object : ElectronicObserver<ElectronicLogin>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ElectronicLogin>) {
                    if (response.code == 1 && response.data != null) {
                        integralProductList(response.data!!.sessionId)
                    }
                }

                override fun onFailed(response: BaseResponse<ElectronicLogin>) {
                    super.onFailed(response)
                }
            })
    }

    /**
     * 获取当前任务
     */
    fun getTask(taskId: String, position: Int) {
        var userId = SPUtils.getInstance().getInt(SPKey.VIPID)
        repository.getTask(taskId, userId.toString())
            .excute(object : BaseObserver<Any>() {
                override fun onSuccess(response: BaseResponse<Any>) {
                    getTaskLiveData?.postValue(ListStatusBean(position, true, "领取任务成功~"))
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    getTaskLiveData?.postValue(
                        ListStatusBean(position, false, response.message)
                    )
                }

            })
    }

    /**
     * 完成任务
     */
    fun completeTask(taskId: String, position: Int) {
        var userId = SPUtils.getInstance().getInt(SPKey.VIPID)
        repository.completeTask(taskId, userId.toString())
            .excute(object : BaseObserver<CompleteTaskBean>() {
                override fun onSuccess(response: BaseResponse<CompleteTaskBean>) {
                    completeLiveData?.postValue(ListStatusBean(position, true, "恭喜您，完成任务~").apply {
                        if (response.data != null) {
                            key = response.data!!.receiveStatus
                        }
                    })
                }

                override fun onFailed(response: BaseResponse<CompleteTaskBean>) {
                    completeLiveData?.postValue(ListStatusBean(position, false, response.message))
                }

            })
    }


    /**
     * 积分商品列表
     */
    fun integralProductList(sessionId: String?) {
        ElectronicRepository.electronicService.integralProductList(sessionId)
            .excut(object : ElectronicObserver<ProductListBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ProductListBean>) {
                    if (response.code == 1 && response.data != null && response.data!!.productList
                        != null && response.data!!.productList!!.isNotEmpty()
                    ) {
                        productListData.postValue(response.data!!)
                    }
                }

                override fun onFailed(response: BaseResponse<ProductListBean>) {
                    super.onFailed(response)

                }

            })
    }

    /**
     * 积分规则
     */
    var rule: String? = null

    /**
     * 积分配置规则
     */
    fun pointConfigInfo() {
        repository.pointConfigInfo()
            .excute(object : BaseObserver<ConfigInfoBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ConfigInfoBean>) {
                    rule = response.data?.configRule
                }
            })
    }

}