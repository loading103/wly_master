package com.daqsoft.provider.bean

/**
 * 收藏的列表实体类
 * @author 黄熙
 * @date 2020/2/28 0028
 * @version 1.0.0
 * @since JDK 1.8
 */
class CollectionBean(
    /**
     *资源类型
     */
    var resourceType: String,
    /**
     *数据id
     */
    var id: Int,
    /**
     *资源名称
     */
    var resourceName: String,
    /**
     *资源图片
     */
    var resourceImage: String,
    /**
     * 资源ID
     */
    var resourceId: Int,
    /**
     * 资源状态（大于0数据在该站点存在）
     */
    var resourceStatus: Int,
    /**
     * 收藏时间
     */
    var collectionTime: String,
    /**
     * 内容类型
     */
    var contentType: String,
    /**
     * 章节
     */
    var chapterNum: Int,
    /**
     * 学习时长
     */
    var totalDuration: Int
){
    fun getTime():String{
        return "收藏于$collectionTime"
    }
}