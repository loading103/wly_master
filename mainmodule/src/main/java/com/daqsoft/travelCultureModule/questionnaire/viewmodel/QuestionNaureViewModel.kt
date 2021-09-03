package com.daqsoft.travelCultureModule.questionnaire.viewmodel
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.utils.UIHelperUtils
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.network.home.HomeRepository
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONArray
import kotlin.math.log

/**
 * @Description 陈列展览列表ViewModel
 */
class QuestionNaureViewModel : BaseViewModel() {

    val listdatas = MutableLiveData<MutableList<QuestionListBean>>()

    val details = MutableLiveData<QuestionListBean>()

    val pageManager = PageManager(10)

    var totleNumber:Int =0


    val detailsBean = MutableLiveData<QuestionSubmitRoot>()

    val submitBean = MutableLiveData<SubmitResultBean>()
    /**
     * 获取调查问卷列表
     */
    fun getListDatas(searech: String="") {

        HomeRepository.service.getQuestionListNeW(searech,pageManager.pageIndex,pageManager.pageSize)
            .excute(object : BaseObserver<QuestionListBean>() {
                override fun onSuccess(response: BaseResponse<QuestionListBean>) {
                    listdatas.postValue(response.datas)
                    totleNumber= response.page?.total!!
                }

                override fun onFailed(response: BaseResponse<QuestionListBean>) {
                    mError.postValue(response)
                }
            })
    }
    /**
     * 获取我的调查问卷列表
     */
    fun getMineListDatas() {
        HomeRepository.service.getQuestionUserListNeW(pageManager.pageIndex,pageManager.pageSize)
            .excute(object : BaseObserver<QuestionListBean>() {
                override fun onSuccess(response: BaseResponse<QuestionListBean>) {
                    listdatas.postValue(response.datas)
                    totleNumber= response.page?.total!!
                }

                override fun onFailed(response: BaseResponse<QuestionListBean>) {
                    mError.postValue(response)
                }
            })
    }
    /**
     * 获取问卷列表详情
     */
    fun getListDetailDatas(paperId:String) {
        HomeRepository.service.getQuestionDetail(paperId)
            .excute(object : BaseObserver<QuestionSubmitRoot>() {
                override fun onSuccess(response: BaseResponse<QuestionSubmitRoot>) {
                    detailsBean.postValue(response.data)
                }
                override fun onFailed(response: BaseResponse<QuestionSubmitRoot>) {
                    mError.postValue(response)
                }
            })
    }

    /**
     * 获取问卷列表详情
     */
    fun submitQuestListDatas(groupCode:String,paperId:String, beans:MutableList<SubmitBean>) {
        var bean=SubmitRoot(groupCode,paperId,beans)
        Log.e("提交数据---------",Gson().toJson(bean))
        val body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"),Gson().toJson(bean))
        HomeRepository.service.getQuestionSubmit(body)
            .excute(object : BaseObserver<SubmitResultBean>() {
                override fun onSuccess(response: BaseResponse<SubmitResultBean>) {
                    submitBean.postValue(response.data)
                }
                override fun onFailed(response: BaseResponse<SubmitResultBean>) {
                    mError.postValue(response)
                }
            })
    }

    /**
     * 获取问卷结果列表详情
     */
    val resultDatas = MutableLiveData<MutableList<QuestionSubmitBean>>()
    fun getResultListDatas(groupCode:String,paperId:String) {
        HomeRepository.service.getQuestionResult(groupCode,paperId)
            .excute(object : BaseObserver<QuestionSubmitBean>() {
                override fun onSuccess(response: BaseResponse<QuestionSubmitBean>) {
                    resultDatas.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<QuestionSubmitBean>) {
                    mError.postValue(response)
                }
            })
    }
}