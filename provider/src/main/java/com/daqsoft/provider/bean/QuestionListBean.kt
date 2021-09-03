package com.daqsoft.provider.bean

import com.daqsoft.provider.getRealImages
import java.io.Serializable

data class QuestionListBean(
    val id: String,
    val images: String,
    val startTime: String,
    val endTime: String,
    var inputPass: String,
    val joinCount: String,
    val processStatus: String, //0 未开始 1 进行中 2 结束
    val stock: String,
    val publicResult:String,
    val updateTime:String,
    val title: String,
    val guideBody: String,
    val groupCode: String,
    val login: String

){

    fun getJoinNumber():String {
        if(joinCount.isNullOrBlank() || joinCount=="0"){
            return ""
        }else{
            return joinCount
        }
    }
    fun getState():String{
        if(processStatus.equals("2") || stock=="0" ){
            return "2"
        }
        if(processStatus.equals("1")){
            return "1"
        }
        if(processStatus.equals("0")){
            return "0"
        }
        return "2"
    }

    fun getRealImages():String{
     return   images.getRealImages()
    }

    fun getStartTime1():String{
        if(startTime.isNullOrBlank()){
            return ""
        }

        return  startTime.subSequence(0,startTime.length-3).toString()
    }
    fun getEndTime1():String{
        if(endTime.isNullOrBlank()){
            return ""
        }
        return  endTime.subSequence(0,endTime.length-3).toString()
    }

    fun getCreattime():String{
        if(updateTime.isNullOrBlank()){
            return ""
        }
        return  updateTime.subSequence(0,updateTime.length-3).toString()
    }
}

data class QuestionBean(
    val id: Int,
    val chooseList: QuestionChooseBean
)
data class QuestionChooseBean(
    val answerBody: String,
    val id: Int
)

/**
 * 提交详情
 */
data class QuestionSubmitRoot (
    val id: String="",
    val type: String="",
    val title: String="",
    val startTime: String="",
    val publicResult: String="", //公开结果
    val login: String="",
    val limitNum: String="",  //限购人数
    val inputPass: String="",
    val images: String="",
    val endTime: String="",
    val guideBody: String="",
    val dataStatus: String="",
    val groupCode: String="",
    var questionList: MutableList<QuestionSubmitBean> = mutableListOf()
):Serializable{
    fun getRealImages():String{
        return   images.getRealImages()
    }
}
data class QuestionSubmitBean(
    val  type: String,//  ('SINGLE','MULTIPLE','JUDGE','QA')
    val title: String,   //('单选问题','多选问题','判断问题','问答问题')
    val questionId: String,
    var content: String="",  //判断问答题是不是填写了
    val joinCount: String,
    val dataStatus: String,
    val chooseList: MutableList<ChooseListBean>
):Serializable

data class ChooseListBean(
    val  title: String,//  ('SINGLE','MULTIPLE','JUDGE','QA')
    val  chooseId: String,
    var  haveChoosed:Boolean=false,
    var userCheck: Boolean,   //  分组code
    var joinCount:  String="",
    var answerBody:  String=""
):Serializable

data class SubmitRoot(
    val  groupCode: String,   //  分组code
    val  paperId: String,
    val questionList: MutableList<SubmitBean>
)
data class SubmitBean(
    var id: Int?=null,   //  分组code
    val chooseList: MutableList<SubmitChooseBean> = mutableListOf()
)
data class SubmitChooseBean(
    var id: Int=0,   //  选项ID
    var answerBody: String="" //回答题填写内容
){

}
data class SubmitResultBean(
    var groupCode: String="",   //  分组code
    var publishResult: Boolean//结果公开 false 否 true 是
)



