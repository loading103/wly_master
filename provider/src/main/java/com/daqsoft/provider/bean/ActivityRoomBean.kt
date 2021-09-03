package com.daqsoft.provider.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @Description
 * @ClassName   ActiviyRoomBean
 * @Author      luoyi
 * @Time        2020/3/27 13:40
 */
data class ActivityRoomBean(
    /**
     * 活动室id
     */
    var id: String,
    /**
     * 活动室名称
     */
    var name: String,
    /**
     * 场馆id
     */
    var venueId: String,
    var panoramaCover:String?,
    /**
     * 活动室类型
     */
    var type: String,
    /**
     * 活动室面积
     */
    var area: String,
    /**
     * 可容纳人数
     */
    var galleryful: String,
    /**
     * 图片
     */
    var images: String,
    var openStatus: Boolean,
    var faithAuditStatus: String,
    var faithAuditValue: String,
    var faithUseStatus: String,
    var faithUseValue: String,
    var cancelStatus: String,
    var cancelTime: String,
    var venueName: String,
    var video: String,
    var audio:String,
    /**
     * 地址
     */
    var address: String,
    /**
     * 720
     */
    var panoramaUrl: String,
    var sourceSiteId: String,
    /**
     * 标签
     */
    var labelName:String,
    var phone:String,
    /**
     * 设备
     */
    var equipment:String,
    var select:Boolean = false,
    var activityRoom:List<ActivityRoomDateBean>
)

class ActivityRoomDateBean(
    var list:ArrayList<ActivityRoomTimeBean>,
    var date:String,
    var week:String,
    var select:Boolean = false
)

class ActivityRoomTimeBean(
    var week:String,
    var startTime:String,
    var id:String,
    var endTime:String,
    var oderDate:String,
    var remarks:String,
    var status:String,
    var select: Boolean = false
)

/**
 * @des 预订结果
 * @author PuHua
 * @Date 2020/1/12 16:34
 */
data class OrderResultBean(
    // 订单code
    val orderCode: String
)


/**
 * 订单保存实体
 */
data class OrderSaveBean(
    // 活动ID
    val activityId: Int,
    // 地址
    val address: String,
    // 核销码
    val code: String,
    // 订单创建时间
    val createTime: String,
    val id: String,
    // 封面URL
    val image: String,
    // 订单编号
    val orderCode: String,
    // 订单有效期结束时间
    val orderIndateEnd: String,
    // 订单有效期开始时间
    val orderIndateStart: String,
    // 订单名称
    val orderName: String,
    // 二维码URL
    val orderQrCode: String,
    // 资源类型
    val resourceType: String,
    // 座位ID
    val seatId: String,
    // 预订人姓名
    val userName: String,
    // 预订人手机号
    val userPhone: String
)

/**
 * @Description 查看当前账号的单数
 * @ClassName   OrderNumberBean
 * @Author      PuHua
 * @Time        2020/1/6 17:31
 */
data class OrderNumberBean(
    // 该手机号预订过的订单数量
    val existNum: Int
)