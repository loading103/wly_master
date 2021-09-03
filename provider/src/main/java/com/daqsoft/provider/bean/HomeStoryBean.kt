package com.daqsoft.provider.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @des 首页故事标签数据
 * @author PuHua
 * @Date 2019/12/11 11:54
 */
@Parcelize
data class HomeStoryTagBean(
    val cover: String,
    val id: String,
    val name: String,
    val storyNum: String,
    var select:Boolean = false
) : Parcelable

/**
 * 个人标签
 */
data class TagBean(val id:String,val name:String){
    override fun toString(): String {
        return name
    }
}

/**
 * 线路标签
 */
data class LineTagBean(val tagId:String,val tagName:String,var select: Boolean = false){
    override fun toString(): String {
        return tagName
    }
}

/**
 * 标签详情
 */

data class HomeStoryTagDetail(
    // 评论数量
    val commentNum: Int,
    // 内容数量
    val contentNum: Int,
    // 点赞数量
    val likeNum: Int,
    // 参与人数
    val participateNum: Int,
    // 浏览量
    val showNum: Int,
    // 标签名称
    val tagName: String
)
/**
 * @des 首页故事列表
 * @author PuHua
 * @Date 2019/12/11 14:06
 */
@Parcelize
data class HomeStoryBean(
    // 4：待审核  6：审核通过  79：审核不通过
    val auditStatus: String,
    val autoCover: String,
    var collectionNum: Int,
    val commentNum: String,
    val content: String,
    val cover: String,
    val createDate: String,
    val foodNum: String,
    val homeCover: String,
    val hotelNum: String,
    val id: String,
    val images: List<String>,
    val latitude: String,
    var likeNum: Int,
    val listCover: String,
    val longitude: String,
    val playPointNum: String,
    val regionNum: String,
    val resourceId: String,
    val resourceName: String,
    val resourceRegionName: String,
    val resourceType: String,
    val resourceTypeName:String,
    val showNum: String,
    val status: String,
    // strategy：攻略 story：故事 使用引用Constant
    val storyType: String,
    val strategyDetail: List<StrategyDetail>,
    val tagName: String,
    val title: String,
    val topicName: String,
    val video: String,
    val videoCover: String,
    val vipHead: String,
    val vipNickName: String,
    val vipResourceStatus: VipResourceStatus,
    // 地区完整名称
    val resourceCompleteRegionName:String,
    val resourceImage:String,
    val topicInfo:List<SimpleTopic>,
    // 审核方式（machine：机器审核）
    val auditType:String,
    // 审核结果
    val auditResult:String,
    val ichHp:Boolean,
    val ich:Boolean,
    val ichHpName:String,
    val top:Int,
    val pkStoryTitle:String,
    val pkNum:String,
    val pkId:String,
    var sourceUrl:String?,
    var heirAttentionStatus:Boolean,
    val vipPhone:String,
    val consumePerson:String?
):Parcelable{
     fun isVisible():Boolean {
       return resourceTypeName.isNullOrEmpty()
    }
}
/**
 * @des 攻略信息
 * @author PuHua
 * @Date 2019/12/11 14:09
 */
@Parcelize
data class StrategyDetail(
    var content: String,
    val contentType: String,
    val resourceId: String,
    val resourceName: String,
    // CONTENT：内容 IMAGE：图片 VIDEO:视频 使用引用Constant
    val resourceType: String,
    val title: String,
    val videoCover: String,
    var introduction:String?=""
):Parcelable

/**
 * 故事攻略里面简单话题类型
 */
@Parcelize
data class SimpleTopic(
    // 话题id
    val topicId:String,
    // 话题名称
    val topicName: String
):Parcelable

/**
 * 点赞的实体
 */
data class ThumbBean(
    val headUrl: String,
    val id: Int,
    val name: String,
    // 志愿者状态	number	大于0 标识为志愿者
    val volunteerStatus: Int
)

