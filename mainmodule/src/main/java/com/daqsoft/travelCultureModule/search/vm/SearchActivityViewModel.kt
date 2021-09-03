package com.daqsoft.travelCultureModule.search.vm

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.travelCultureModule.net.MainRepository
import com.daqsoft.travelCultureModule.search.bean.SearchBean

class SearchActivityViewModel : BaseViewModel() {
    var searchList = MutableLiveData<MutableList<SearchBean>>()
    var searchRecordList = MutableLiveData<MutableList<String>>()

    fun getSearchList(currPage: String, pageSize: String, keyword: String, searchType: String) {
        MainRepository.service.searchAll(currPage, pageSize, keyword, searchType).excute(object :
            BaseObserver<SearchBean>() {
            override fun onSuccess(response: BaseResponse<SearchBean>) {
                searchList.postValue(response.datas)
            }
        })
    }

    fun getSearchRecordList(size: String, searchType: String) {
        MainRepository.service.getSearchRecord("10", "").excute(object : BaseObserver<String>() {
            override fun onSuccess(response: BaseResponse<String>) {
                searchRecordList.postValue(response.datas)
            }
        })
    }

    var clear = MutableLiveData<String>()
    /**
     * 清除历史记录
     */
    fun clearSearchRcord() {
        MainRepository.service.deletSearchRecord("").excute(object : BaseObserver<String>() {
            override fun onSuccess(response: BaseResponse<String>) {
                clear.postValue("")
            }

        })
    }

    /**
     * 保存搜索结果
     */
    fun saveSearchRcord(key: String) {
        MainRepository.service.saveSearchRecord(key, "").excute(object : BaseObserver<String>() {
            override fun onSuccess(response: BaseResponse<String>) {

            }

        })
    }
}