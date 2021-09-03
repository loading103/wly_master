package com.daqsoft.integralmodule.repository

import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.net.HeaderInterceptor
import com.daqsoft.baselib.net.gsonTypeAdapters.FaultToleranceConvertFactory
import com.daqsoft.integralmodule.repository.api.IntegralService
import com.daqsoft.provider.network.RetrofitFactory

/**
 * 积分仓库
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-11-21
 * @since JDK 1.8.0_191
 */
class IntegralRepository {

    companion object {
        //        /**
//         * ip地址
//         */
//        var baseUrl:String = ""
        val integralService: IntegralService = RetrofitFactory.Builder()
            .setBaseUrl(BaseApplication.baseUrl)
            .addInterceptor(HeaderInterceptor())
            .addConvertFactory(FaultToleranceConvertFactory.create())
            .build(IntegralService::class.java)
    }

    /**
     * 会员当前积分统计
     */
    fun pointCount() =
        integralService
            .pointCount()

    /**
     * C端用户任务列表
     */
    fun getApiTaskList(params: String) =
        integralService
            .getApiTaskList(params)


    /**
     * 站点详情
     */
    fun siteInfo() = integralService.siteInfo()

    /**
     * 获取任务
     */
    fun getTask(id: String, userId: String) = integralService.pickTask(id, userId)

    /**
     * 完成任务
     */
    fun completeTask(id: String, userId: String) = integralService.completeTast(id, userId)

    /**
     * 用户积分详情
     */
    fun detail(time: String, type: Int) =
        integralService
            .detail(type, time)

    /**
     * 积分配置规则
     */
    fun pointConfigInfo() =
        integralService
            .pointConfigInfo()

    fun getVisitingCard() = integralService.getVisitingCard()

    /**
     * 消息未读书
     */
    fun getNoReadNumber() = integralService.getNoReadMessage()
    fun getNoReadNumberT() = integralService.getNoReadMessage1()
    fun geNoReadList() = integralService.getNoReadMessageList()
}