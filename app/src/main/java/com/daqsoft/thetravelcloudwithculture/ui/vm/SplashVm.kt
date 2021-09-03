package com.daqsoft.thetravelcloudwithculture.ui.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.utils.file.DownLoadFileUtil
import com.daqsoft.provider.base.AdvCodeType
import com.daqsoft.provider.base.PublishChannel
import com.daqsoft.provider.bean.AdInfo
import com.daqsoft.provider.bean.HomeAd
import com.daqsoft.provider.bean.StyleTemplate
import com.daqsoft.provider.net.TemplateApi
import com.daqsoft.provider.net.TemplateRepository
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.view.convenientbanner.ConvenientBanner
import com.google.gson.GsonBuilder

class SplashVm : BaseViewModel() {
    /**
     * 首页模板
     */
    var mImageAds = MutableLiveData<HomeAd>()

    /**
     * 获取图片闪屏广告
     */
    fun getSplashImageAds() {
        HomeRepository.service.getHomeAd(PublishChannel.MICRO_SITE, AdvCodeType.APP_SPLASH_ADV).excute(object : BaseObserver<HomeAd>() {
            override fun onSuccess(response: BaseResponse<HomeAd>) {
                mImageAds.postValue(response.data)
            }
            override fun onFailed(response: BaseResponse<HomeAd>) {
                super.onFailed(response)
                mImageAds.postValue(null)
            }
        })
    }

    /**
     * 获取竖屏视频
     */
    var mVideoVAds = MutableLiveData<HomeAd>()
    fun getSplashVideoV() {
        HomeRepository.service.getHomeAd(PublishChannel.MICRO_SITE, AdvCodeType.APP_SPLASH_VS_ADV).excute(object : BaseObserver<HomeAd>() {
            override fun onSuccess(response: BaseResponse<HomeAd>) {
                mVideoVAds.postValue(response.data)
            }
            override fun onFailed(response: BaseResponse<HomeAd>) {
                super.onFailed(response)
                mVideoVAds.postValue(null)
            }
        })
    }


    /**
     * 获取app横屏视频
     */
    var mVideoHAds = MutableLiveData<HomeAd>()
    fun getSplashVideoH() {
        HomeRepository.service.getHomeAd(PublishChannel.MICRO_SITE, AdvCodeType.APP_SPLASH_LE_ADV).excute(object : BaseObserver<HomeAd>() {
            override fun onSuccess(response: BaseResponse<HomeAd>) {
                mVideoHAds.postValue(response.data)
            }
            override fun onFailed(response: BaseResponse<HomeAd>) {
                super.onFailed(response)
                mVideoHAds.postValue(null)
            }
        })
    }
}
