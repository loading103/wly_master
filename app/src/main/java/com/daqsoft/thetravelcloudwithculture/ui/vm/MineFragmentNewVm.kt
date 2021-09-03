package com.daqsoft.thetravelcloudwithculture.ui.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.integralmodule.repository.IntegralRepository
import com.daqsoft.integralmodule.repository.bean.SiteInfoBean
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.net.TemplateApi
import com.daqsoft.provider.net.TemplateRepository
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.network.home.bean.UserCurrPoint
import com.daqsoft.provider.network.home.bean.UserPointTaskInfoBean
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.travelCultureModule.net.MainRepository


/**
 * 个人中心模板化vm
 */
class MineFragmentNewVm : BaseViewModel() {

    /**
     * 个人资料
     */
    val info by lazy { MutableLiveData<BaseResponse<MineUserInfoBean>>() }

    /**
     * 当前积分
     */
    val point by lazy { MutableLiveData<UserCurrPoint>() }

    /**
     * 任务信息
     */
    val taskInfo by lazy { MutableLiveData<UserPointTaskInfoBean>() }

    /**
     * 签到结果
     */
    val checkInResult by lazy { MutableLiveData<BaseResponse<Any>>() }

    /**
     * 故事列表数据
     */
    val storyList by lazy { MutableLiveData<MutableList<HomeStoryBean>>() }

    /**
     * 站点详情
     */
    val siteBean by lazy { MutableLiveData<SiteInfoBean>() }

    /**
     * 个人信息模板
     */
    val personTemplate by lazy { MutableLiveData<PersonInfoTemplate>() }

    /**
     * 个人中心模板
     */
    val mineTemplateLd by lazy { MutableLiveData<MutableList<StyleTemplate>>() }

    val noReadNumber by lazy { MutableLiveData<String>() }
    val  noReadList by lazy { MutableLiveData<MutableList<MineMessageBean>>() }
    /**
     * 刷新用户信息
     */
    fun getMineCenterInfo() {
        UserRepository().userService
            .getMineCenterInfo()
            .excute(object : BaseObserver<MineUserInfoBean>() {
                override fun onSuccess(response: BaseResponse<MineUserInfoBean>) {
                    if (response?.data != null) {
                        SPUtils.getInstance().put(SPKey.NICK_NAME, response.data?.nickName)
                        SPUtils.getInstance().put(SPKey.MOBILE, response.data?.phone)
                        SPUtils.getInstance().put(SPKey.HEAD_URL, response.data?.headUrl)
                        SPUtils.getInstance().put(SPKey.HERITAGE_PEOPLEID, response.data?.heritagePeopleId)
                        info.postValue(response)
                    }
                }
            })
    }

    /**
     * 获取当前用户积分
     */
    fun getCurrPoint() {
        HomeRepository.service.getCurrPoint()
            .excute(object : BaseObserver<UserCurrPoint>() {
                override fun onSuccess(response: BaseResponse<UserCurrPoint>) {
                    if (response.code == 0 && response?.data != null) {
                        point.postValue(response.data)
                    }
                }
            })
    }

    /**
     * 获取数据列表
     */
    fun getHotStoryList() {
        val param = HashMap<String, String>()
        // homeCover   首页封面	number	【选填】是否首页封面1：是 0：否
//        param["homeCover"] = "1"
        //  pageSize
        param["pageSize"] = "20"
        param["orderType"] = "createTime"
        mPresenter.value?.loading = true
        MainRepository.service.getVipList(param)
            .excute(object : BaseObserver<HomeStoryBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeStoryBean>) {
                    storyList.postValue(response.datas)
                }
            })
    }

    /**
     * 获取签到任务
     */
    fun getPointTaskInfo() {
        HomeRepository.service.getPointTaskInfo()
            .excute(object : BaseObserver<UserPointTaskInfoBean>() {
                override fun onSuccess(response: BaseResponse<UserPointTaskInfoBean>) {
                    if (response.code == 0 && response?.data != null) {
                        taskInfo.postValue(response.data)
                    }
                }

            })
    }

    /**
     * 个人信息模板
     */
    fun getPersonTemplate() {
        TemplateRepository.instance.service.getPersonTemplate()
            .excute(object : BaseObserver<PersonInfoTemplate>() {
                override fun onSuccess(response: BaseResponse<PersonInfoTemplate>) {
                    personTemplate.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<PersonInfoTemplate>) {
                    personTemplate.postValue(null)
                }
            })
    }

    /**
     * 个人模板
     */
    fun getMineTemplate() {
        TemplateRepository.instance.service.getTemplate(TemplateApi.PERSONAL_CENTER)
            .excute(object : BaseObserver<StyleTemplate>() {
                override fun onSuccess(response: BaseResponse<StyleTemplate>) {
                    mineTemplateLd.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<StyleTemplate>) {
                    mineTemplateLd.postValue(null)
                }
            })
    }

    /**
     * 签到
     */
    fun getCheckIn() {
        HomeRepository.service.getUserCheckIn()
            .excute(object : BaseObserver<Any>() {
                override fun onSuccess(response: BaseResponse<Any>) {
                    toast.postValue("签到成功!")
                    checkInResult.postValue(response)
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    toast.postValue(response.message)
                    checkInResult.postValue(response)

                }

            })
    }

    /**
     * 获取站点信息
     */
    fun siteInfo() {
        IntegralRepository().siteInfo()
            .excute(object : BaseObserver<SiteInfoBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<SiteInfoBean>) {
                    if (response.code == 0 && response.data != null) {
                        SPUtils.getInstance().put(SPKey.SHOP_URL, response.data?.shopUrl)
                        SPUtils.getInstance().put(SPKey.SITE_CODE, response.data?.siteCode)
                        // 保存id
                        SPUtils.getInstance().put(SPKey.SITE_ID, response.data?.id.toString())
                    }
                    siteBean.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<SiteInfoBean>) {
                    siteBean.postValue(null)
                }
            })
    }

    /**
     * 获取未读消息
     */
    fun getNoNumber1() {
        var index=0
        IntegralRepository().getNoReadNumber()
            .excute(object : BaseObserver<MineMessageBean>() {
                override fun onSuccess(response: BaseResponse<MineMessageBean>) {
                    if (response.code == 0 && response.data != null) {
                        index += (response?.data?.num?.toInt() ?:0)
                        getNoNumber2(index)
                    }else{
                        getNoNumber2(index)
                    }
                }

                override fun onFailed(response: BaseResponse<MineMessageBean>) {
                    getNoNumber2(index)
                }
            })
    }
    fun getNoNumber2(index: Int) {
        var number=index
        IntegralRepository().getNoReadNumberT()
            .excute(object : BaseObserver<MineMessageBean>() {
                override fun onSuccess(response: BaseResponse<MineMessageBean>) {
                    if (response.code == 0 && response.data != null) {
                        number += (response?.data?.messageNum?.toInt() ?:0)
                        noReadNumber.postValue(number.toString())
                    }else{
                        noReadNumber.postValue(number.toString())
                    }
                }
                override fun onFailed(response: BaseResponse<MineMessageBean>) {
                    noReadNumber.postValue(number.toString())
                }
            })
    }
    fun getNoNumberList() {
        IntegralRepository().geNoReadList()
            .excute(object : BaseObserver<MineMessageBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<MineMessageBean>) {
                    noReadList.postValue(response?.datas)
                }

                override fun onFailed(response: BaseResponse<MineMessageBean>) {
                    noReadList.postValue(null)
                }
            })
    }
}