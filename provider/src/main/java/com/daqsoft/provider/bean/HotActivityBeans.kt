package com.daqsoft.provider.bean

import android.os.Parcelable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.provider.R
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.utils.DividerTextUtils
import kotlinx.android.parcel.Parcelize
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.textColorResource

/**
 * @des 活动的分类标签
 * @author PuHua
 * @Date 2019/12/5 14:20
 */
data class Classify(
    // 所属分类活动数量
    val activityCount: Int,
    // 分类ID
    val id: String,
    // 分类名称
    val labelName: String,
    // 类型是否选中
    var select: Boolean
)

/**
 * @des 首页活动的数据(活动也可以使用)
 * @author PuHua
 * @Date 2019/12/10 16:48
 */
@Parcelize
data class ActivityBean(
    // 活动状态 0(未开始) 1(进行中) 2(已结束) 3 (报名中/招募中)
    val activityStatus: String,
    // 地址
    val address: String,
    val classifyId: String,
    // 分类名
    val classifyName: String,
    // 诚信免审状态
    val faithAuditStatus: String,
    // 诚信免审分值
    val faithAuditValue: String,
    // 诚信优享状态 0 关闭 1 开启
    val faithUseStatus: String,
    // 诚信优享分值
    val faithUseValue: String,
    // 活动ID
    val id: String,
    // 封面图
    val images: String,
    // 积分价格
    val integral: String,
    // 跳转名称
    val jumpName: String,
    // 跳转类型
    val jumpType: String,
    // 跳转URL
    val jumpUrl: String,
    // 纬度
    val latitude: String,
    // 经度
    val longitude: String,
    // 活动方式
    val method: String,
    // 价格
    val money: String,
    // 活动名称
    val name: String,
    val cityRegionNames: String,
    // 组织ID
    val orgId: String,
    // 推荐至频道首页
    val recommendChannelHomePage: String,
    // 推荐至首页
    val recommendHomePage: String,
    // 已招募人数
    val recruitedCount: String,
    // 地区编码
    val region: String,
    // 地区名称(多个，分隔)
    val regionName: String,
    // 资源数量
    val resourceCount: String,
    // 数据管理ID
    val resourceId: String,
    // 资源字符串(以,分隔)
    val resourceNameStr: String,
    // 报名/招募结束时间
    val signEndTime: String,
    // 报名/招募开始时间
    val signStartTime: String,
    // 站点ID
    val siteId: String,
    // 排序
    val sort: String,
    // 数据状态
    val status: String,
    // 剩余库存
    val stock: String,
    val tag: String,
    // 标签字符串
    val tagNames: String,
    // 置顶
    val top: String,
    // 总库存
    val totalStock: String,
    // 活动类型
    var type: String,
    // 活动结束时间
    val useEndTime: String,
    // 活动开始时间
    val useStartTime: String,
    var alreadySignCount: Int?,
    // 用户收藏状态
    val userResourceStatus: UserResourceStatus,
    val remark: String,
    val signNum: String
) : Parcelable {
    fun getShowStartTime(): String {
        try {
            if(useStartTime.isNullOrBlank() || useEndTime.isNullOrBlank()){
                return ""
            }
            return "展览时间: " +useStartTime.subSequence(5,10).toString().replace("-",".")+"-"+useEndTime.subSequence(5,10).toString().replace("-",".")
        }catch (e :Exception){
            return ""
        }
    }

    fun getShowAdress(): String {
        var sb=StringBuffer()
        if(!cityRegionNames.isNullOrBlank() ){
            sb.append(cityRegionNames.replace(","," · "))
        }
        if(!address.isNullOrBlank()){
            sb.append(address)
        }
        return "展览地点: $sb"
    }
    public fun getDesc(): String {
        var datas: MutableList<String> = mutableListOf()
        var statusStr: String = ""
        var stockStr: String = ""
        var typeStr: String = ""
        when (type) {
            // 预订
            ActivityType.ACTIVITY_TYPE_RESERVE -> {
                statusStr = "预订中"
                typeStr = "预订"
            }
            // 报名
            ActivityType.ACTIVITY_TYPE_ENROLL -> {
                statusStr = "报名中"
                typeStr = "报名"
            }
            // 展览
            ActivityType.ACTIVITY_TYPE_SERVICE -> {
                typeStr = "展览"
                statusStr = "进行中"
            }
            ActivityType.ACTIVITY_TYPE_PLAIN -> {
                // 普通
                statusStr = "进行中"
                typeStr = "宣传"
            }
            ActivityType.ACTIVITY_TYPE_VOLUNT -> {
                typeStr = "志愿者"
                // 志愿
                statusStr = "招募中"
                // 如果有库存
                var tvPeriodText = StringBuilder()
                if (!stock.isNullOrEmpty() && stock.toInt() > 0) {
                    stockStr = "还剩${stock}个名额"
                }
            }
        }
        when (activityStatus) {
            "0" -> {
                statusStr = "未开始"
            }
            "1" -> {
                // 进行中用上面的字段
//                statusStr = "进行中"
            }
            "2" -> {
                statusStr = "已结束"
            }
        }
        datas.add(statusStr)
        if (!stockStr.isNullOrEmpty()) {
            datas.add(stockStr)
        }
        if (!typeStr.isNullOrEmpty()) {
            datas.add(typeStr)
        }
        return DividerTextUtils.convertDotString(datas)
    }
    fun getWsDesc(): String {
        var datas: MutableList<String> = mutableListOf()
        var statusStr: String = ""
        var stockStr: String = ""
        var typeStr: String = ""
        when (type) {
            // 预订
            ActivityType.ACTIVITY_TYPE_RESERVE -> {
                statusStr = "预订中"
                if(tagNames.isNullOrEmpty())
                    typeStr = "预订"
                else
                    typeStr = tagNames
            }
            // 报名
            ActivityType.ACTIVITY_TYPE_ENROLL -> {
                statusStr = "报名中"
                if(tagNames.isNullOrEmpty())
                    typeStr = "报名"
                else
                    typeStr = tagNames
            }
            // 展览
            ActivityType.ACTIVITY_TYPE_SERVICE -> {
                statusStr = "进行中"
                if(tagNames.isNullOrEmpty())
                    typeStr = "展览"
                else
                    typeStr = tagNames
            }
            ActivityType.ACTIVITY_TYPE_PLAIN -> {
                statusStr = "进行中"
                // 普通
                if(tagNames.isNullOrEmpty())
                    typeStr = "宣传"
                else
                    typeStr = tagNames
            }
            ActivityType.ACTIVITY_TYPE_VOLUNT -> {
                // 志愿
                statusStr = "招募中"
                if(tagNames.isNullOrEmpty())
                    typeStr = "志愿者"
                else
                    typeStr = tagNames
                // 如果有库存
                var tvPeriodText = StringBuilder()
                if (!stock.isNullOrEmpty() && stock.toInt() > 0) {
                    stockStr = "还剩${stock}个名额"
                }
            }
        }
        when (activityStatus) {
            "0" -> {
                statusStr = "未开始"
            }
            "1" -> {
                // 进行中用上面的字段
//                statusStr = "进行中"
            }
            "2" -> {
                statusStr = "已结束"
            }
        }
        datas.add(statusStr)
        if (!stockStr.isNullOrEmpty()) {
            datas.add(stockStr)
        }
        if (!typeStr.isNullOrEmpty()) {
            typeStr=typeStr.replace(","," ");
            datas.add(typeStr)
        }
        return DividerTextUtils.convertDotString(datas)
    }
    public fun getPriceInfo(): String {
        var priceStr = ""
        if (type != ActivityType.ACTIVITY_TYPE_PLAIN) {
            if (money.toDouble() == 0.0 && integral == "0") {
                priceStr = BaseApplication.context.resources.getString(R.string.provider_order_free)
            } else if (money.toDouble() > 0.0) {
                var dMoney = money.toDouble()
                if (dMoney % 1 == 0.0) {
                    priceStr = dMoney.toInt().toString()
                } else {
                    priceStr = money
                }
                priceStr = "${priceStr}${BaseApplication.context.resources.getString(R.string.provider_chinese_yuan)}"
            } else {
                priceStr = "${integral}${BaseApplication.context.resources.getString(R.string.provider_integral)}"
            }
        }
        return priceStr
    }

    fun getInpartStr(): String {
        if (!recruitedCount.isNullOrEmpty()) {
            if (recruitedCount.toInt() > 0) {
                return "${recruitedCount}人参加"
            }
        }
        return ""
    }
    fun getRealImages(): String {
        return images.getRealImages()
    }
}

/**
 * @des 用户收藏状态
 * @author PuHua
 * @Date 2019/12/25 18:45
 */
@Parcelize
data class UserResourceStatus(
    // 收藏状态 (false) 收藏状态 false 否 true 是
    var collectionStatus: Boolean,
    // 点赞状态 (true) 点赞状态 false 否 true 是
    val thumbStatus: Boolean,
    // 关注资源
    var resourceFansStatus: Boolean
) : Parcelable

/**
 * @des 活动详情实体
 * @author PuHua
 * @Date 2019/12/25 18:40
 */
@Parcelize
data class HotActivityDetailBean(
    // 活动详情URL
    val activityInfoUrl: String,
    // 活动状态
    // 0(未开始) 1(进行中) 2(已结束) 3 (报名中/招募中)
    val activityStatus: String,
    // 地址
    val address: String,
    // 已招募人数
    val alreadySignCount: String,
    // 0 可以预订 1 限购购买完 2 申请中 3 结束 4 未开始 5 没有库存
    val buttonStatus: String,
    // 取消状态
    val cancelStatus: String,
    // 取消时间
    val cancelTime: String,
    // 活动分类
    val classifyId: String,
    // 协办单位
    val coOrganizer: String,
    // 诚信免审状态
    val faithAuditStatus: String,
    // 诚信免审分值
    val faithAuditValue: String,
    // 诚信优享状态 0 关闭 1 开启
    val faithUseStatus: String,
    val classifyName: String,
    // 诚信优享分值
    val faithUseValue: String,
    // 温馨提示
    val hint: String,
    // 活动ID
    val id: String,
    // 封面多图,以【,】分隔
    val images: String,
    // 积分价格
    val integral: String,
    // 介绍
    val introduce: String,
    // 0 不能加入 1 可以加入 2 申请中 3 已报名/已购满
    val isJoin: String,
    // 纬度
    val latitude: String,
    // 负责人姓名
    val liableName: String,
    // 城市编码
    var region: String?,
    // 限购数量
    val limitNum: String,
    // 用户可以购买数量
    val limitPayCount: String,
    // 线下 0 线上 1
    val lineFlag: String,
    // 经度
    val longitude: String,
    // 活动方式
    val method: String,
    // 名称
    val name: String,
    // 有效期结束
    val orderValidEnd: String,
    // 有效期开始
    val orderValidStart: String,
    // 手机号
    val phone: String,
    // 备注
    val remark: String,
    val resourceCount: ResourceCount,
    // 活动室ID
    val seatId: String,
    // 服务时长
    val serviceTime: String,
    // 报名人数
    val signCount: String,
    // 报名/招募结束时间
    val signEndTime: String,
    // 报名/招募开始时间
    val signStartTime: String,
    // 已招募人
    val signUser: List<SignUser>? = mutableListOf(),
    // 主办单位
    val sponsor: String,
    // 剩余库存
    val stock: String,
    // 标签字符串
    val tagNames: String,
    // 座位模板ID
    val templateId: String,
    // 总招募人员
    val totalStock: String,
    // 活动类型
    val type: String,
    // 承办单位
    val undertakeUnit: String,
    //活动结束日期
    val useEndTime: String,
    // 活动开始日期
    val useStartTime: String,
    // 收藏评论点赞状态
    val userResourceStatus: UserResourceStatus,
    // 核销结束值
    val validEndValue: String,
    // 核销开始值
    val validStartValue: String,
    // 核销结束类型
    val validTimeEndType: String,
    // 核销开始类型
    val validTimeStartType: String,
    // 是否为志愿者
    val volunteerStatus: String,
    // 所属场所
    val resourceNames: String,
    // 副标题
    val subhead: String,
    // 指导
    var teachUnit: String,
    /**
     * 直播地址
     */
    var liveUrl: String,
    var alreadyActivity: String,
    var activityResult: ActivityResult
) : Parcelable

/**
 * @des 收藏评论点赞状态
 * @author PuHua
 * @Date 2019/12/25 18:46
 */
@Parcelize
data class ResourceCount(
    // 收藏数量
    val collectionNum: String,
    // 评论数量
    val commentNum: String,
    // 点赞数量
    val thumbNum: String,
    // 关注数量
    var fansCount: String
) : Parcelable

/**
 * @des 已招募人
 * @author PuHua
 * @Date 2019/12/25 18:53
 */
@Parcelize
data class SignUser(
    // 用户头像
    val headUrl: String,
    // 用户ID
    val id: String,
    // 昵称
    val nickname: String
) : Parcelable


/**
 * 活动概览 实体
 */
@Parcelize
data class ActivityOverView(
    var total: Int,
    var notStartCount: Int,
    var startCount: Int,
    var endCount: Int,
    var result: MutableList<ActivityOverViewTypes> = mutableListOf()
) : Parcelable

/**
 * 活动概览分类实体
 */
@Parcelize
data class ActivityOverViewTypes(
    var name: String,
    var id: String,
    var ration: Float,
    var num: Int
) : Parcelable

/**
 * 活动成果
 */
@Parcelize
data class ActivityResult(
    var images: String,
    /**
     * 视频集
     */
    var videos: String,
    /**
     * 视频封面图
     */
    var videoCover: String
) : Parcelable