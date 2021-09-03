package com.daqsoft.thetravelcloudwithculture.ui.vm

import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.MineUserInfoBean
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.network.home.bean.UserCurrPoint
import com.daqsoft.provider.network.home.bean.UserPointTaskInfoBean
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.baselib.extend.excute
import com.daqsoft.integralmodule.repository.IntegralRepository
import com.daqsoft.integralmodule.repository.bean.SiteInfoBean
import com.daqsoft.provider.base.AdvCodeType
import com.daqsoft.provider.base.PublishChannel
import com.daqsoft.provider.bean.HomeAd
import com.daqsoft.provider.bean.HomeStoryBean
import com.daqsoft.provider.bean.MineMessageBean
import com.daqsoft.travelCultureModule.net.MainRepository


/**
 * 我的Vm
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-10-22
 * @since JDK 1.8.0_191
 */
class MineFragmentVm : BaseViewModel() {

    /**
     * 个人资料
     */
    var info = MutableLiveData<BaseResponse<MineUserInfoBean>>()
    /**
     * 当前积分
     */
    var point = MutableLiveData<UserCurrPoint>()
    /**
     * 任务信息
     */
    var taskInfo = MutableLiveData<UserPointTaskInfoBean>()
    /**
     * 签到结果
     */
    var checkInResult = MutableLiveData<BaseResponse<Any>>()

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
                        SPUtils.getInstance().put(SPKey.HERITAGE_PEOPLEID,response.data?.heritagePeopleId)
                        info.postValue(response)
                    }
                }
            })
    }

    /**
     * 故事列表数据
     */
    val storyList = MutableLiveData<MutableList<HomeStoryBean>>()

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
        param["orderType"] = "createTime"
        mPresenter.value?.loading = false
        MainRepository.service.getVipList(param)
            .excute(object : BaseObserver<HomeStoryBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeStoryBean>) {
                    storyList.postValue(response.datas)
                }
            })
    }

    /**
     * 获取当前用户积分
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
     * 站点详情
     */
    var siteBean = MutableLiveData<SiteInfoBean>()

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
     * 广告
     */
    var homeAd = MutableLiveData<HomeAd>()
    /**
     * 获取广告
     * http://www.xn--efvu3ql9f.com/api/res/api/ad/view?publishChannel=APP&adCode=feedback&token=44ae05eb76144f77b3ccce43ed41473e&source=android&siteCode=site688790
     * https://www.xn--efvu3ql9f.com/api/res/api/ad/view?token=8e9cab379a144699a55757bd3bb2626b&publishChannel=MICRO_SITE&adCode=feedback
     */
    fun getAds() {
        mPresenter.value?.loading = false
        HomeRepository.service.getHomeAd("MICRO_SITE", "feedback")
            .excute(object : BaseObserver<HomeAd>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeAd>) {
                    homeAd.postValue(response.data)
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