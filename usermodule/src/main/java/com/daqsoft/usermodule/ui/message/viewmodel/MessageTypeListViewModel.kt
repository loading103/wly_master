package com.daqsoft.usermodule.ui.message.viewmodel

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.event.UpdateMessageNumberEvent
import com.daqsoft.provider.network.net.UserRepository
import org.greenrobot.eventbus.EventBus

/**
 * @Description
 * @ClassName   MessageListViewModel
 * @Author      luoyi
 * @Time        2021/3/9 17:21
 */
class MessageTypeListViewModel : BaseViewModel(){

    val datas = MutableLiveData<MutableList<MessageListBean>>()
    val pageManager = PageManager(10)
    var totleNumber:Int =0
    /**
     * 获取数据
     */
    fun getListData(classify: String,type: String) {
        UserRepository.userService.getMessageListData(classify,type,pageManager.pageIndex,pageManager.pageSize)
            .excute(object : BaseObserver<MessageListBean>() {
                override fun onSuccess(response: BaseResponse<MessageListBean>) {
                    datas.postValue(response.datas)
                    totleNumber= response.page?.total ?: 0
                }
            })
    }
    fun getListDataLoading(classify: String,type: String) {
        UserRepository.userService.getMessageListData(classify,type)
            .excute(object : BaseObserver<MessageListBean>() {
                override fun onSuccess(response: BaseResponse<MessageListBean>) {
                    datas.postValue(response.datas)
                }
            })
    }

    /**
     * 获取志愿者数据
     */
    fun getVotDatas() {
        UserRepository.userService.getVotMessageList1("all",pageManager.pageIndex,pageManager.pageSize)
            .excute(object : BaseObserver<MessageListBean>() {
                override fun onSuccess(response: BaseResponse<MessageListBean>) {
                    datas.postValue(response.datas)
                }

            })
    }
    /**
     * 获取志愿者数据
     */
    /**
     * 获取顶部未读数数据
     */
    fun setMessageReaded(classify: Int,type: Int) {
        UserRepository.userService.ReadMessage(classify,type)
            .excute(object : BaseObserver<String>() {
                override fun onSuccess(response: BaseResponse<String>) {
                    EventBus.getDefault().post(UpdateMessageNumberEvent())
                }

                override fun onFailed(response: BaseResponse<String>) {
                    super.onFailed(response)
                }
            })
    }



    /**
     * 志愿消息未读书单独出来的
     */
    fun setVotedNumberReaded() {
        UserRepository.userService.UndateReadMessage()
            .excute(object : BaseObserver<String>() {
                override fun onSuccess(response: BaseResponse<String>) {
                    EventBus.getDefault().post(UpdateMessageNumberEvent())
                }

                override fun onFailed(response: BaseResponse<String>) {
                    super.onFailed(response)
                }
            })
    }
}