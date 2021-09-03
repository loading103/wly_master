package com.daqsoft.thetravelcloudwithculture.ui.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.provider.base.AdvCodeType
import com.daqsoft.provider.base.PublishChannel
import com.daqsoft.provider.bean.*
import com.daqsoft.thetravelcloudwithculture.home.bean.*
import com.daqsoft.provider.network.home.HomeRepository
import java.util.*


/**
 * 首页Vm
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-10-22
 * @since JDK 1.8.0_191
 */
class SCHomeFragmentVm : BaseViewModel() {
    /**
     * 机器人问候语
     */
    var itRobotInfoLiveData = MutableLiveData<ItRobotGreeting>()
    /**
     * 顶部菜单
     */
    var topMenus = MutableLiveData<MutableList<HomeMenu>>()

    /**
     * 城市名片列表
     */
    var cityCards = MutableLiveData<MutableList<CityCardBean>>()
    var appIndexAds = MutableLiveData<HomeAd>()

    var appIndexAdsTop = MutableLiveData<HomeAd>()
    /**
     * 地区编码
     */
    var region: String? = ""
    /**
     * 经度
     */
    var lon: String? = ""
    /**
     * 纬度
     */
    var lat: String? = ""


    /**
     * 获取菜单
     */
    fun getAPPMenus(location: String,isNeedLoading:Boolean) {
        mPresenter.value?.loading = isNeedLoading
        HomeRepository.service.getAPPMenuList(Constant.HOME_CLIENT_TYPE, location).excute(object :
            BaseObserver<HomeMenu>(mPresenter) {
            override fun onSuccess(response: BaseResponse<HomeMenu>) {
                topMenus.postValue(response.datas)
            }
        })
    }


    /**
     * 获取城市名片
     */
    fun getCityCard(region: String?) {

        mPresenter.value?.loading = false

        HomeRepository.service.getCityCardS(region).excute(object :
            BaseObserver<CityCardBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<CityCardBean>) {
                cityCards.postValue(response.datas)
            }
        })
    }

    /**
     * 获取广告
     */
    fun getAppIndexAds() {
        mPresenter.value?.loading = false
        HomeRepository.service.getHomeAd(PublishChannel.MICRO_SITE, AdvCodeType.APP_INDEX_LOG)
            .excute(object : BaseObserver<HomeAd>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeAd>) {
                    appIndexAds.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<HomeAd>) {
                    appIndexAds.postValue(null)
                }
            })
    }


    /**
     * 获取广告
     */
    fun getAppIndexAdsTop() {
        mPresenter.value?.loading = false
        HomeRepository.service.getHomeAd(PublishChannel.MICRO_SITE, AdvCodeType.APP_TOP_BANNER)
            .excute(object : BaseObserver<HomeAd>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeAd>) {
                    appIndexAdsTop.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<HomeAd>) {
                    appIndexAdsTop.postValue(null)
                }
            })
    }
    /**
     * 获取机器人问候语
     *      * region	用户当前位置地区编码	string
     * token	用户token	string
     * lon	经度	number
     * lat	纬度	number
     * distance	距离	number	默认：1 单位：km
     * sign	唯一标识	string	【必填】建议生成uuid值，在页面没有清除缓存前该值生成后不改变
     */
    fun getItRobotGreeting() {
        var params: HashMap<String, String> = hashMapOf()
        if (!lon.isNullOrEmpty()) {
            params["lon"] = lon!!
        }
        if (!lat.isNullOrEmpty()) {
            params["lat"] = lat!!
        }
        var distance = ""
        if (!distance.isNullOrEmpty()) {
            params["distance"] = distance
        }
        if (!region.isNullOrEmpty()) {
            params["region"] = region!!
        }

        var sign: String = SPUtils.getInstance().getString(SPUtils.Config.APP_SIGN_ID, "")
        if (sign.isNullOrEmpty()) {
            sign = createSignUuid()

        }
        params["sign"] = sign
        Log.e("Sign", sign)
        itRobotInfoLiveData?.postValue(null)

    }

    private fun createSignUuid(): String {
        var sign: UUID? = UUID.randomUUID()
        SPUtils.getInstance().put(SPUtils.Config.APP_SIGN_ID, sign.toString())
        return sign.toString()
    }

    var tzggContentLd = MutableLiveData<MutableList<HomeContentBean>>()

    /**
     * 获取内容（通知公告等）
     */
    fun getTzggList() {
        val param = HashMap<String, String>()
        param["channelCode"] = Constant.HOME_CONTENT_TYPE_tzgg
        param["pageSize"] = "2"
        param["currPage"] = "1"
        mPresenter?.value?.isNeedToastMessage=false
        HomeRepository.service.getHomeContentList(param)
            .excute(object : BaseObserver<HomeContentBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeContentBean>) {
                    tzggContentLd.postValue(response.datas)
                }
            })
    }
}