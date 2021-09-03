package com.daqsoft.travelCultureModule.themepark.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.provider.bean.PageManager
import com.daqsoft.provider.bean.ThemeAreaListBean

class ThemeAreaListViewModel : BaseViewModel(){

    val datas = MutableLiveData<MutableList<ThemeAreaListBean>>()

    val pageManager = PageManager(10)

    var totleNumber:Int =0
    /**
     * 获取数据
     */
    fun getListData() {
        var beans:MutableList<ThemeAreaListBean> = mutableListOf(
            ThemeAreaListBean(),
            ThemeAreaListBean(),
            ThemeAreaListBean(),
            ThemeAreaListBean(),
            ThemeAreaListBean(),
            ThemeAreaListBean(),
            ThemeAreaListBean()
        )
        datas.postValue(beans)
//        UserRepository.userService.getMessageListData(classify,type,pageManager.pageIndex,pageManager.pageSize)
//            .excute(object : BaseObserver<MessageListBean>() {
//                override fun onSuccess(response: BaseResponse<MessageListBean>) {
//                    datas.postValue(response.datas)
//                    totleNumber= response.page?.total ?: 0
//                }
//            })
    }
}