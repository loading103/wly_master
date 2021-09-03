package com.daqsoft.volunteer.utils

import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.Constant
import okhttp3.Cache.key

/**
 *@package:com.daqsoft.volunteer.utils
 *@date:2020/6/3 16:22
 *@author: caihj
 *@des:TODO
 **/
object JumpUtils {
    /**
     * 跳转到志愿者注册
     */
    fun gotoVolunteerRegister(){
        gotoPageNoArgs(ARouterPath.VolunteerModule.VOLUNTEER_REGISTER)
    }

    /**
     * 跳转到志愿者申请详情
     */
    fun gotoVolunteerApplyDetail(){
        gotoPageNoArgs(ARouterPath.VolunteerModule.VOLUNTEER_APPLY_DETAIL)
    }

    /**
     * 跳转到志愿团队申请详情
     */
    fun gotoVolunteerTeamApplyDetail(){
        gotoPageNoArgs(ARouterPath.VolunteerModule.VOLUNTEER_TEAM_APPLY_DETAIL)
    }

    /**
     * 志愿者审核记录
     */
    fun gotoVolunteerAuditLog(){
        gotoPage(ARouterPath.VolunteerModule.VOLUNTEER_AUDIT_LOG)
    }

    /**
     * 跳转到品牌详情
     */
    fun gotoBrandDetail(id:String){
        gotoPageOneArgs(ARouterPath.VolunteerModule.VOLUNTEER_BRAND_DETAIL,PageParams("id",id))
    }

    /**
     * 查看志愿风采列表
     */
    fun gotoServiceRecordList(id:String){
        gotoPageOneArgs(ARouterPath.VolunteerModule.VOLUNTEER_SERVICE_RECORD_LIST,PageParams("id",id))
    }

    /**
     * 志愿服务记录详情
     */
    fun gotoServiceRecordDetail(id:String){
        gotoPageOneArgs(ARouterPath.VolunteerModule.VOLUNTEER_SERVICE_RECORD,PageParams("id",id))
    }

    /**
     * 志愿者成功后跳转界面
     */
    fun gotoVolunteerRegSuc(type:Int){
        gotoPageOneIntArgs(ARouterPath.VolunteerModule.APPLY_SUCCESS_RESULT,PageParams("type",type.toString()))
    }

    /**
     * 志愿者成功后跳转界面
     */
    fun gotoVolunteerApplySuc(type:Int,id:String){
        gotoPageIntStringArgs(ARouterPath.VolunteerModule.APPLY_SUCCESS_RESULT,PageParams("type",type.toString()), PageParams("id",id))
    }

    /**
     * 跳转到志愿者团队注册
     */
    fun gotoVolunteerTeamRegister(){
        gotoPageNoArgs(ARouterPath.VolunteerModule.VOLUNTEER_TEAM_REGISTER)
    }

    /**
     * 跳转到搜索界面
     */
    fun gotoSearch(){
        gotoPageNoArgs(MainARouterPath.MAIN_ALL_SEARCH)
    }

    /**
     * 跳转到志愿者详情页面
     */
    fun gotoVolunteerDetail(id:String){
        gotoPageOneArgs(ARouterPath.VolunteerModule.VOLUNTEER_DETAIL, PageParams("id",id))
    }


    /**
     * 跳转到志愿团队详情页面
     */
    fun gotoVolunteerTeamDetail(id:String){
        gotoPageOneArgs(ARouterPath.VolunteerModule.VOLUNTEER_TEAM_DETAIL, PageParams("id",id))
    }

    /**
     * 跳转到志愿者列表
     */
    fun gotoVolunteerList(){
        gotoPageNoArgs(ARouterPath.VolunteerModule.VOLUNTEER_LIST)
    }

    /**
     * 跳转到修改紧急联系人界面
     */
    fun gotoUpdateVolunteerContact(name:String,phone:String){
        gotoPage(ARouterPath.VolunteerModule.VOLUNTEER_UPDATE_CONTACT, PageParam("name",name),PageParam("phone",phone))
    }

    /**
     * @description  跳转志愿者排行榜
     * @date: 2020/9/2 14:34
     * @author: zp
     * @param rankingTypeEnum String 排名类型
     * @param listClassEnum String 榜单分类
     */
    fun goToVolunteerRankList(rankingTypeEnum:String,listClassEnum:String){
        gotoPage(ARouterPath.VolunteerModule.VOLUNTEER_RANK_LIST,
            PageParam("rankingTypeEnum",rankingTypeEnum),
            PageParam("listClassEnum",listClassEnum)
        )
    }


    /**
     * 跳转到志愿者证界面
     */
    fun gotoVolunteerCard(){
        gotoPage(ARouterPath.VolunteerModule.VOLUNTEER_CARD)
    }

    /**
     * 修改志愿者信息
     *
     */
    fun gotoUpdateVolunteerInfo(type:String,data:String){
        gotoPage(ARouterPath.VolunteerModule.VOLUNTEER_UPDATE_INFORMATION, PageParam("type",type),PageParam("data",data))
    }

    /**
     * 跳转到志愿者团队列表
     */
    fun gotoVolunteerTeamList(){
        gotoPageNoArgs(ARouterPath.VolunteerModule.VOLUNTEER_TEAM_LIST)
    }

    /**
     * 活动招募
     */
    fun gotoVolunteerActivity(){
        gotoPageNoArgs(ARouterPath.VolunteerModule.VOLUNTEER_ACTIVITY_LIST)
    }

    /**
     * 跳转到资讯列表
     */
    fun gotoNewsList(){
        gotoPageOneArgs(MainARouterPath.MAIN_CONTENT, PageParams("channelCode", Constant.HOME_CONTENT_TYPE_volunteerNews))
    }

    /**
     * 跳转到评论
     */
    fun gotoCommentPage(id:String,type:String){
        gotoPageTwoStringArgs(MainARouterPath.MAIN_COMMENT_ADD, PageParams("id",id),PageParams("type",type))
    }

    /**
     * 跳转到评论列表
     */
    fun gotoCommentList(id:String,type:String){
        gotoPageTwoStringArgs(MainARouterPath.MAIN_ACTIVITY_COMMENT, PageParams("id",id),PageParams("type",type))
    }

    /**
     * 跳转到成员列表界面
     */
    fun gotoMembers(id:String){
        gotoPageOneArgs(ARouterPath.VolunteerModule.VOLUNTEER_TEAM_MEMBERS,PageParams("id",id))
    }

    /**
     * 志愿服务入口
     */
    fun gotoVolunteerServices(){
        gotoPage(ARouterPath.VolunteerModule.VOLUNTEER_SERVICES_IN)
    }

    fun menuJump(key:String){
        var path = ""
        when(key){
            // 活动招募
            "hdzm" -> path = ARouterPath.VolunteerModule.VOLUNTEER_ACTIVITY_LIST
            // 加入团队
            "jrtd" -> path = ARouterPath.VolunteerModule.VOLUNTEER_TEAM_LIST
            // 志愿榜单
            "zybd" -> path = ARouterPath.VolunteerModule.VOLUNTEER_RANK_LIST
            // 积分兑换
            "jfdh" -> {
            }
            // 志愿签到
            "zyqd" -> path = ARouterPath.VolunteerModule.VOLUNTEER_SIGN_LIST
            // 志愿风采
            "zyfc" -> path = ARouterPath.VolunteerModule.VOLUNTEER_FENG_CAI_LIST
            // 志愿品牌
            "zypp" -> path = ARouterPath.VolunteerModule.VOLUNTEER_BRAND_LIST
            // 志愿资讯
            "zyzx" ->{
               gotoNewsList()
                return
            }
        }
        gotoPageNoArgs(path)
    }

    /**
     * 志愿者中心菜单跳转
     */
    fun volunteerCenterMenu(key:String){
        var path = ""
        when(key){
            // 基本资料
            "jbzl" -> path = ARouterPath.VolunteerModule.VOLUNTEER_INFORMATION
            // 我的活动
            "wdhd" ->{

            }
            // 签到申请
            "qdsq" ->{

            }
            // 志愿签到
            "zyqd" ->{

            }
            // 已签到活动
            "yqdhd" ->{

            }
            // 签到记录
            "qdjl" ->{

            }
            // 我的团队
            "wdtd" ->{

            }
            // 我的关注
            "wdgz" ->{

            }
            // 公益积分
            "gyjf" ->{

            }
            // 消息中心
            "xxzx" ->{

            }
            // 补录申请
            "blsq" ->{

            }
            // 团队管理
            "tdgl" ->{

            }
            // 志愿招募
            "zyzm" ->{

            }
            // 我的审核
            "wdsh" ->{

            }
            // 志愿者邀请
            "zyzyq" ->{

            }
            // 服务记录
            "fwjl" ->{

            }
            // 签到记录(团队)
            "qdjlt" ->{

            }
            // 团队积分
            "tdjf" ->{

            }
            // 补录记录
            "bljl" ->{

            }

        }
        gotoPageNoArgs(path)
    }

    /**
     * 跳转到没有参数的页面
     */
    private fun gotoPageNoArgs(path:String){
        if(path.isEmpty()){
            ToastUtils.showMessage("页面路径为空，跳转失败!")
            return
        }
        ARouter
            .getInstance()
            .build(path)
            .navigation()
    }

    /**
     * 用于传递多个参数跳转
     */
    private fun gotoPage(path: String,vararg params:PageParam){
       val route = ARouter
            .getInstance().build(path)
        if(params.isNotEmpty()){
            for(param in params){
                if(param.value is Int){
                    route.withInt(param.key,param.value)
                }
                if(param.value is String){
                    route.withString(param.key,param.value)
                }
            }
        }
        route.navigation()
    }


    private fun gotoPageOneArgs(path:String,args:PageParams){
        ARouter
            .getInstance()
            .build(path)
            .withString(args.key,args.value)
            .navigation()
    }

    private fun gotoPageTwoStringArgs(path:String,args1:PageParams,args2:PageParams){
        ARouter
            .getInstance()
            .build(path)
            .withString(args1.key,args1.value)
            .withString(args2.key,args2.value)
            .navigation()
    }

    private fun gotoPageIntStringArgs(path:String,args1:PageParams,args2:PageParams){
        ARouter
            .getInstance()
            .build(path)
            .withInt(args1.key,args1.value.toInt())
            .withString(args2.key,args2.value)
            .navigation()
    }

    private fun gotoPageOneIntArgs(path:String,args:PageParams){
        ARouter
            .getInstance()
            .build(path)
            .withInt(args.key,args.value.toInt())
            .navigation()
    }
}
data class PageParams(
    val key:String,
    val value:String
)

data class PageParam(
    val key:String,
    val value:Any
)