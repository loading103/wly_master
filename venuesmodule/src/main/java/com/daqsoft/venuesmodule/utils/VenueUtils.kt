package com.daqsoft.venuesmodule.utils

import com.daqsoft.provider.bean.VenueDateInfo
import com.daqsoft.provider.bean.VenueDateNumBean

/**
 * @Description
 * @ClassName   VenueUtils
 * @Author      luoyi
 * @Time        2020/7/8 16:17
 */
object VenueUtils {

    /**
     * @param currentIndex 当前天日期
     * @param dateInfo 场馆预约日期对象
     * @param numBean 场馆预约数量对象
     * @param futureOrderDayNum 可以预约未来多少天的数量
     * @param teamAdvanceOrderDay 团队预约 必须提前多少天
     * @param personAdvanceOrderDay 个人预约 个人至少提前多少天
     * @param type 1 个人预约 2 团队预约
     */
    fun isCanRserationDate(
        currentIndex: Int,
        dateInfo: VenueDateInfo, numBean: VenueDateNumBean,
        futureOrderDayNum: Int, teamAdvanceOrderDay: Int, personAdvanceOrderDay: Int, type: Int
    ): VenueDateInfo {
        //1.是否开放 2.是否预约过 3.预约库存是否足够
        if (dateInfo.openStatus == 0) {
            dateInfo.status = 3
        } else {
            var stock = dateInfo.maxNum - numBean.orderNum
            if (stock > 0) {
                // 4.是否在预约时间内：在提前多少天和可预约多少天范围内
                if (type == 1) {
                    // 个人预约
                    if (personAdvanceOrderDay == 0) {
                        dateInfo.status = 4
                    } else {
                        var indexMin = personAdvanceOrderDay + currentIndex
                        if (dateInfo.index > indexMin) {
                            if (futureOrderDayNum == 0) {
                                dateInfo.status = 4
                            } else {
                                var indexMax = indexMin + futureOrderDayNum
                                if (dateInfo.index < indexMax) {
                                    dateInfo.status = 4
                                } else {
                                    dateInfo.status = 7
                                }
                            }
                        } else {
                            dateInfo.status = 7
                        }
                    }
                } else {
                    // 团队预约
                    if (teamAdvanceOrderDay == 0) {
                        dateInfo.status = 4
                    } else {
                        var indexMin = teamAdvanceOrderDay + currentIndex
                        if (dateInfo.index > indexMin) {
                            if (futureOrderDayNum == 0) {
                                dateInfo.status = 4
                            } else {
                                var indexMax = indexMin + futureOrderDayNum
                                if (dateInfo.index < indexMax) {
                                    dateInfo.status = 4
                                } else {
                                    dateInfo.status = 7
                                }
                            }
                        } else {
                            dateInfo.status = 7
                        }
                    }
                }
            } else {
                dateInfo.status = 6
            }
        }


        return dateInfo
    }

}