package com.daqsoft.travelCultureModule.themepark.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.provider.bean.PageManager
import com.daqsoft.provider.bean.PlayChooseType
import com.daqsoft.provider.bean.ThemeAreaListBean
import com.daqsoft.travelCultureModule.country.bean.ResourceTypeLabel

class ThemePlayListViewModel : BaseViewModel(){

    val datas = MutableLiveData<MutableList<ThemeAreaListBean>>()

    val pageManager = PageManager(10)

    var totleNumber:Int =0

    var topdatas = arrayListOf("明星项目", "热度最高","离我最近")

    val types1 = mutableListOf(
        ResourceTypeLabel("", "", "", "1", "1").setSelects(true),
        ResourceTypeLabel("", "", "", "2", "2").setSelects(true),
        ResourceTypeLabel("", "", "", "3", "3").setSelects(true),
        ResourceTypeLabel("", "", "", "4", "4").setSelects(true)
    )
    val types2 = mutableListOf(
        ResourceTypeLabel("", "", "", "21", "21").setSelects(true),
        ResourceTypeLabel("", "", "", "22", "22").setSelects(true),
        ResourceTypeLabel("", "", "", "23", "23").setSelects(true),
        ResourceTypeLabel("", "", "", "24", "24").setSelects(true)
    )
    val types3 = mutableListOf(
        ResourceTypeLabel("", "", "", "31", "31").setSelects(true),
        ResourceTypeLabel("", "", "", "32", "32").setSelects(true),
        ResourceTypeLabel("", "", "", "33", "33").setSelects(true),
        ResourceTypeLabel("", "", "", "34", "34").setSelects(true)
    )
    val types4 = mutableListOf(
        ResourceTypeLabel("", "", "", "41", "41").setSelects(true),
        ResourceTypeLabel("", "", "", "42", "42").setSelects(true),
        ResourceTypeLabel("", "", "", "43", "43").setSelects(true),
        ResourceTypeLabel("", "", "", "44", "44").setSelects(true)
    )

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




    /**
     * 审核状态选项（todo  等接口从后台获取数据）
     */
    val chooseStates: MutableList<PlayChooseType> = mutableListOf(
        PlayChooseType("1", "全部"),
        PlayChooseType("2", "已通过"),
        PlayChooseType("3", "待审核"),
        PlayChooseType("4", "被驳回"),
        PlayChooseType("5", "已撤销")
    )

    /**
     * 请假类型选项（todo  等接口从后台获取数据）
     */
    val chooseTypes: MutableList<PlayChooseType> = mutableListOf(
        PlayChooseType("1", "全部"),
        PlayChooseType("2", "事假"),
        PlayChooseType("3", "病假"),
        PlayChooseType("4", "产假"),
        PlayChooseType("5", "年假"),
        PlayChooseType("6", "调休假")
    )
}