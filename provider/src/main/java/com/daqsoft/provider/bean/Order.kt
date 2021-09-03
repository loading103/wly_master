package com.daqsoft.provider.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal


/**
 * @Description 预订数据
 * @ClassName   Order
 * @Author      PuHua
 * @Time        2019/11/12 11:50
 */
data class OrderRoom(
    val activity: DataActivity,
    val activityRoom: ActivityRoom,
    val backIntegral: String,
    val backMoney: String,
    val backNum: String,
    val commentStatus: String,
    val endTime: String,
    val id: String,
    val orderCode: String,
    val orderNum: String,
    val orderType: String,
    val payIntegral: String,
    val payMoney: Double,
    val startTime: String,
    val status: Int
)

/**
 * 活动室
 */
data class ActivityRoom(
    val activityRoomId: String,
    val address: String,
    val faithAuditValue: String,
    val faithUseValue: String,
    val image: String,
    val lat: String,
    val lon: String,
    val name: String,
    val phone: String,
    val resourceId: String,
    val resourceType: String,
    val useEndTime: String,
    val useStartTime: String,
    val venueId: String,
    val venueName: String,
    val money: Double,
    val integral: Int,
    val cancelStatus: Int,
    val cancelTime: String,
    /**免审状态*/
    var faithAuditStatus:String,
    /**优享状态*/
    var faithUseStatus: String
)

/**
 * 活动数据
 */
data class DataActivity(
    val activityId: String,
    val activityType: String,
    val address: String,
    val faithAuditStatus: String,
    val faithAuditValue: String,
    val faithUseStatus: String,
    val faithUseValue: String,
    val image: String,
    val lat: String,
    val lon: String,
    val name: String,
    val phone: String,
    val resourceId: String,
    val resourceName: String,
    val resourceType: String,
    val seats: List<Seat>,
    val serviceTime: String,
    val signEndTime: String,
    val signNum: String,
    val signStartTime: String,
    val useEndTime: String,
    val useStartTime: String,
    val money: String,
    val integral: String,
    val cancelStatus: Int,
    val cancelTime: String
)

/**
 * 场馆座位
 */
@Parcelize
data class Seat(
    // 总行列标准的行列
    val col: String,
    // 显示给用户的行列
    val realCol: String,
    val realRow: String,
    val row: String,
    // 座位状态(0无座，1有座,2有座被占--自己定义的，与后台无关)
    var status: String,
    // 座位ID
    val id: String
) : Parcelable

/**
 * 订单详情
 */
@Parcelize
data class OrderDetail(
    var venueInfo: VenueInfo?,
    var reservationType: String?,
    var activityId: String?,
    var activityMethod: String?,
    var activityType: String?,
    var address: String,
    var backNum: Int,
    var channel: String,
    var comment: Int,
    var consumeCode: String,
    var consumeNum: String,
    var createTime: String,
    // 诚信状态			0 关闭 1 开启
    var faithAuditStatus: String,
    var faithAuditValue: String,
    var faithUseStatus: String,
    var faithUseValue: String,
    var resourceId: String?,
    var id: String,
    var image: String,
    var latitude: String,
    var longitude: String,
    var orderCode: String,
    var orderIndateEnd: String,
    var orderIndateStart: String,
    var orderName: String,
    var servicePhone: String?,
    var orderNum: String,
    var orderQrCode: String,
    var orderStatus: String,
    var payIntegral: String,
    var payMoney: String,
    var recordList: List<Record>?,
    var remark: String,
    var resourceType: String,
    var seatList: List<Seat>,
    var serviceTime: String,
    var signEndTime: String,
    var signStartTime: String,
    var stock: String,
    var updateTime: String,
    var useEndTime: String,
    var useStartTime: String,
    var userName: String,
    var userPhone: String,
    var venueId: String,
    var venueList: List<Venue>?,
    // 0 不可取消 1 随时 2 24小时内 3 自定义时间
    var cancelStatus: String,
    var cancelTime: String,
    var idCard: String,
    var useNum: String,
    var images: String,
    var orderEndTime: String,
    var orderStartTime: String,
    var validInfo: ValidInfo?,
    var phone: String,
    var orderId: String,
    var resourceName: String,
    var code: String,
    var isGuideOrder: Int,
    var roomStartTime: String?,
    var roomEndTime: String?,
    /**
     * 关联讲解员信息
     */
    var hasRelationResource: HasRelationResource?,
    var hasAttached: Int,
    var validList: MutableList<ValideInfoBean>?,
    var surplusNum: Int,
    var cardType: String?
) : Parcelable

@Parcelize
data class Record(
    var code: String?,
    var createTime: String?,
    var integral:Int,
    var money: String?,
    var num: String?,
    var operateUser: String?,
    var phone: String?,
    var remark: String?,
    var siteName: String?,
    var type: String?
) : Parcelable

@Parcelize
data class Venue(
    val id: String,
    val name: String,
    val resourceType: String
) : Parcelable

/**
 * 订单状态常量，更新的时候请同时更新R.array.order_status
 */
class OrderStatusConstant {

    companion object {
        // 全部,0
        const val ORDER_STATUS_ALL = ""
        // 待审核,4
        const val ORDER_STATUS_WAITE_VALIDE = "4"
        // 未通过,79
        const val ORDER_STATUS_NO_PASS = "79"
        // 待消费,11
        const val ORDER_STATUS_WAITE_COST = "11"
        // 已完成,12
        const val ORDER_STATUS_FINISHED = "12"
        // 已失效,13
        const val ORDER_STATUS_NO_EFFEFECT = "13"
        // 已取消,14
        const val ORDER_STATUS_CANCELED = "14"
    }
}

@Parcelize
data class ValideInfoBean(
    val validNum: Int,
    var validTime: String?,
    var createChannel: String?
) : Parcelable

/**
 * 订单列表数据集
 */
data class OrderListDataBean(
    /**
     * 活动室信息
     */
    var activityRoom: ActivityRoom,
    /**
     * 活动信息
     */
    var activity: DataActivity,
    /**
     * 是否评论（0未评论，1已评论）
     */
    var commentStatus: Int,
    /**
     * 退回数量
     */
    var backNum: Int,
    /**
     * 退回积分
     */
    var backIntegral: Int,
    /**
     * 退回金额
     */
    var backMoney: BigDecimal,
    /**
     * 支付积分
     */
    var payIntegral: String,
    /**
     * 支付金额
     */
    var payMoney: Double,
    /**
     * 订单数量
     */
    var orderNum: Int,
    /**
     * 订单有效期结束时间
     */
    var endTime: String,
    /**
     * 订单有效期开始时间
     */
    var startTime: String,
    /**
     * 订单状态
     */
    var status: Int,
    /**
     * 订单类型
     */
    var orderType: String,
    /**
     * 订单编号
     */
    var orderCode: String,
    var id: Int,
    /**
     * 场馆信息
     */
    var venueInfo: OrderVenueInfo,
    /**
     *  是否讲解员订单，1是，0否
     */
    var isGuideOrder: Int,

    var surplusNum: Int,
    /**
     * 第三方预约信息
     */
    var tripartiteOrderInfo: TripartiteOrderInfo?
)

/**
 * 订单场馆信息
 */
data class OrderVenueInfo(
    /**
     *老人数量
     */
    var oldManNum: Int,
    /**
     * 使用人数
     */
    var useNum: Int,
    /**
     * 取消状态
     */
    var cancelStatus: Int,
    /**
     * 取消时间
     */
    var cancelTime: String,
    /**
     * 预定类型,个人/团队
     */
    var reservationType: String,
    /**
     * 场馆ID
     */
    var venueId: Int,
    /**
     * 手机号
     */
    var phone: String,
    /**
     * 	地址
     */
    var address: String,
    /**
     * 图片
     */
    var image: String,
    /**
     * 经度
     */
    var lon: String,
    /**
     * 纬度
     */
    var lat: String,
    /**
     * 	场馆名称
     */
    var venueName: String,
    /**
     * 资源类型
     */
    var resourceType: String,
    /**
     * 资源ID
     */
    var resourceId: Int,
    /**
     * 场馆预定开始时间
     */
    var useStartTime: String,
    /**
     * 场馆预定结束时间
     */
    var useEndTime: String,
    /**
     * 积分
     */
    var integral: Int,
    /**
     * 金钱
     */
    var money: String,
    /**
     * 企业名称
     */
    var companyName: String,
    /**
     * 成人数量
     */
    var adultNum: Int,
    /**
     * 小孩数量
     */
    var childNum: Int,
    /**
     * 青年数量
     */
    var teenagerNum: Int,
    /**
     * 展厅数量（讲解员独有）
     */
    var guideOrderExhibitionNum: Int,
    /**
     * 展厅语言（讲解员独有）
     */
    var guideOrderLanguage: String,
    /**
     * 支付金额（讲解员独有）
     */
    var guideOrderPayMoney: String,
    /**
     * 讲解员名称（讲解员独有）
     */
    var guideName: String,
    /**
     * 展厅信息列表（讲解员独有）
     */
    var guideExhibitions: MutableList<GuideExhibitions>,
    /**
     * 关联讲解员信息
     */
    var hasRelationResource: HasRelationResource?

)

/**
 * 讲解展厅
 */
data class GuideExhibitions(
    /**
     * 讲解展厅
     */
    var name: String,
    /**
     * 是否推荐，0否，1是
     */
    var recommend: Int
)

@Parcelize
data class HasRelationResource(
    /**
     * 是否有注册讲解员信息
     */
    var exist: Boolean,
    /**
     * 开始预约讲解员时间
     */
    var date: String?
) : Parcelable

/**核销信息*/
@Parcelize
data class ValidInfo(
    val validTime: String,
    val validNum: String,
    val validFlag: Boolean,
    val createChannel: String,
    val consumeCode: String
) : Parcelable

/**
 * 第三方预约
 */
data class TripartiteOrderInfo(
    /**
     * 	封面图
     */
    var coverImage: String?,
    /**
     * 名称
     */
    var name: String?,
    /**
     * 预约开始
     */
    var orderEndTime: String?,
    /**
     * 预约结束
     */
    var orderStartTime: String?,
    /**
     * 详情跳转链接
     */
    var infoUrl: String?
)