package com.daqsoft.legacyModule.mine.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.legacyModule.bean.LegacyNumCount
import com.daqsoft.legacyModule.bean.LegacyStoryListBean
import com.daqsoft.legacyModule.net.LegacyRepository
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.HomeStoryBean
import com.daqsoft.provider.bean.TagBean
import com.daqsoft.provider.bean.ValueKeyBean
import com.daqsoft.provider.network.home.HomeRepository

/**
 *@package:com.daqsoft.legacyModule.mine.vm
 *@date:2020/5/18 10:28
 *@author: caihj
 *@des:TODO
 **/ 
class MineLegacyVM:BaseViewModel() {

    var legacyNum = MutableLiveData<LegacyNumCount>()

    /**
     * 个人标签
     */
    val tags = MutableLiveData<MutableList<TagBean>>()

    /**
     * 删除状态
     */
    val deleteFinish = MutableLiveData<Boolean>()
    /**
     * 当前标签
     */
    var currentTag = ""
    /**
     * 当前排序
     */
    var currentSort =""

    /**
     * 故事排序
     */
    val sorts = mutableListOf(
        ValueKeyBean("不限", ""),
        ValueKeyBean("浏览量优先", "showNum"),
        ValueKeyBean("评论量优先", "commentNum"),
        ValueKeyBean("点赞量优先", "likeNum"),
        ValueKeyBean("被跟做优先", "pkNum")
    )

    fun getLegacyNum(){
        LegacyRepository.service.getLegacyNum().excute(object :BaseObserver<LegacyNumCount>(){
            override fun onSuccess(response: BaseResponse<LegacyNumCount>) {
                legacyNum.postValue(response.data)
            }
        })
    }

    val workList = MutableLiveData<BaseResponse<LegacyStoryListBean>>()

    val pageData = MutableLiveData<BaseResponse.PageBean>()
    val pageSize = "10"
    /**
     * 获取标签
     */
    fun getTagList(){
        val param = HashMap<String, String>()
        param["ich"] = "true"
        HomeRepository.service.getVIPTagList(param).excute(object : BaseObserver<TagBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<TagBean>) {
                tags.postValue(response.datas)
            }
        })
    }

    var currentPage = 1

    /**
     * 获取数据列表
     */
    fun getWorkList() {
        val param = HashMap<String, String>()
        param["pageSize"] = pageSize
        param["ich"] = "1"
        param["ichWorks"] = "1"
        param["currPage"] = currentPage.toString()
        param["tagId"] = currentTag
        if (currentSort != "") {
            param["orderType"] = currentSort
        }
        LegacyRepository.service.getMineLegacyWorks(param)
            .excute(object : BaseObserver<LegacyStoryListBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<LegacyStoryListBean>) {
                    workList.postValue(response)
                    pageData.postValue(response.page)
                }
            })
    }
    var topStatus = MutableLiveData<Boolean>()

    fun vipTop(id:Int,top:Int = 1){
        LegacyRepository.service.vipTop(id = id,top = top).excute(object :BaseObserver<String>(){
            override fun onSuccess(response: BaseResponse<String>) {
                topStatus.postValue(true)
                if(top == 0){
                    ToastUtils.showMessage("取消置顶成功!")
                }else{
                    ToastUtils.showMessage("置顶成功!")
                }
            }

            override fun onFailed(response: BaseResponse<String>) {
                topStatus.postValue(false)
                if(top == 0){
                    ToastUtils.showMessage("取消置顶失败!")
                }else{
                    ToastUtils.showMessage("置顶失败!")
                }
            }

        })
    }

    /**
     * 删除位置
     */
    var deletePosition:Int = -1
    /**
     * 删除作品
     */
    fun deleteWorks(id:String){
        val param = HashMap<String, String>()
        param["id"] = id
        LegacyRepository.service.postDeleteWorks(param).excute(object :BaseObserver<String>(){
            override fun onSuccess(response: BaseResponse<String>) {
                if(response.code == 0){
                    deleteFinish.postValue(true)
                }else{
                    deleteFinish.postValue(false)
                }
            }

            override fun onError(e: Throwable) {
                deleteFinish.postValue(false)
            }

        })
    }
}