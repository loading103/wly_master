package com.daqsoft.thetravelcloudwithculture.ui.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.StyleTemplate
import com.daqsoft.provider.net.TemplateApi
import com.daqsoft.provider.net.TemplateRepository

/**
 * @Description
 * @ClassName   ServiceTemplateVm
 * @Author      luoyi
 * @Time        2020/10/16 15:09
 */
class ServiceTemplateVm : BaseViewModel() {
    /**
     * 首页模板
     */
    var serviceTemplateLd = MutableLiveData<MutableList<StyleTemplate>>()

    /**
     * 获取服务模板
     */
    fun getServiceTemplate() {
        TemplateRepository.instance.service.getTemplate(TemplateApi.SERVICE_PAGE)
            .excute(object : BaseObserver<StyleTemplate>() {
                override fun onSuccess(response: BaseResponse<StyleTemplate>) {
                    serviceTemplateLd.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<StyleTemplate>) {
                    serviceTemplateLd.postValue(null)
                }
            })
    }
}