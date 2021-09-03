package com.daqsoft.provider.bean

/**
 * @Description
 * @ClassName   ScAppointmentBean
 * @Author      luoyi
 * @Time        2020/5/25 20:04
 */
data class ScAppointmentBean(
    /**
     * 资源信息
     */
    var resource:ResourceBean,
    /**
     * 详情URL
     */
      var infoUrl:String,
    /**
     *  取消终止时间，超过该时间不允许取消
     */
      var cancelTime:String
//    /**
//     * 取消状态 0：不可取消, 1可取消
//     */
//    var cancelStatus        string
//var payMoney    实付金额    number
//var ticketPrice    票价    number
//var orderNum    订单数量    number
//var orderEndTime    预定结束时间    string
//var orderStartTime    预定开始时间    string
//var orderStatus    订单状态    number
//var orderCode    订单编号    string

)
data class ResourceBean(
    /**
     * 图片链接，多个，号隔开
     */
    var images: String,
    /**
     * 资源名称
     */
    var resourceName: String
)
