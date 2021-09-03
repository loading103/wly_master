package com.daqsoft.provider.bean


/**
 * @Description 运营专区模板
 * 接口文档：http://rap.daqsoft.com/workspace/myWorkspace.do?projectId=1478#22992
 * @ClassName   OperationTemplate
 * @Author      luoyi
 * @Time        2020/10/9 11:20
 */
class StyleTemplate {
    /**
     * 模块类型
     */
    var moduleType: String? = null

    /**
     * 布局详情
     */
    var layoutDetails: MutableList<CommonTemlate> = mutableListOf()

    /**
     * 布局详情
     */
    var layoutDetail: CommonTemlate? = null
}

/**
 * @Description 运营专区模板
 * 接口文档：http://rap.daqsoft.com/workspace/myWorkspace.do?projectId=1478#22988
 * @ClassName   OperationTemplate
 * @Author      luoyi
 * @Time        2020/10/9 11:20
 */
class CommonTemlate {

    /** operation 运营专区字段 **/
    /**
     * 图片数量
     */
    var imgNum: Int = 0

    /**
     * 运营专区配置详情
     */
    var operateDetailDtoList: MutableList<OperationTemplate>? = mutableListOf()

    /**carousel 轮播图 */
    /**
     * 广告id
     */
    var adId: Int = 0

    /**
     * 图片地址
     */
    var imgUrl: String? = null

    /**
     * 跳转类型
     */
    var type: String? = null

    /**
     * 跳转地址
     */
    var jumpUrl: String? = null

    /**
     * 资源ID
     */
    var resourceId: Int = 0

    /**
     * 资源类型
     */
    var resourceType: String? = null

    /**menu,bottomMenu,topMenu 菜单**/
    /**
     * 子级菜单
     */
    var subList: MutableList<SubMenu>? = mutableListOf()

    var gif: String? = null

    /**
     * 外部链接
     */
    var externalLink: String? = null

    /**
     * 未选中图标
     */
    var unSelectIcon: String? = null

    /**
     * 跳转类型
     */
    var jumpType: String? = null

    /**
     * 图标大小
     */
    var iconSize: String? = null

    /**
     * 选中图标
     */
    var selectIcon: String? = null

    /**
     * 菜单名称
     */
    var name: String? = null

    /**
     * 菜单编码
     */
    var menuCode: String? = null

    /**component 标题 **/
    /**
     * 副标题
     */
    var subTitle: String? = null

    /**
     * 主标题
     */
    var mainTitle: String? = null

    /**
     * 主标题类型
     */
    var mainTitleType: String? = null

    /**
     * 副标题类型
     */
    var subTitleType: String? = null

    /**
     * 菜单值
     */
    var menuValue: String? = null

    /**
     * 样式 menu:菜单 image:图片 customize:自定义
     */
    var style: String? = null

    /**
     * 展示数量
     */
    var showNum: Int = 0

    /**
     * 组件详情 根据menu 取不同的值
     */
    var componentDetail: MutableList<CommponentDetail>? = mutableListOf()

    /** channelTitle 菜单编码等字段复用前面实体字段，请参考接口文档**/
    /**
     * 图标
     */
    var icon: String? = null

    /**
     * 展示模板
     */
    var moduleCode: String? = null

    /**
     * 获取运营专区图片地址
     * @param pos 运营专区地址
     */
    fun getOperationImageUrl(pos: Int): String? {
        if (!operateDetailDtoList.isNullOrEmpty()) {
            if (pos in operateDetailDtoList!!.indices) {
                return operateDetailDtoList!![pos].imgUrl
            }
        }
        return ""
    }

    /**
     * 获取运营专区 实体
     * @param pos 运营专区地址
     */
    fun getOperation(pos: Int): OperationTemplate? {
        if (!operateDetailDtoList.isNullOrEmpty()) {
            if (pos in operateDetailDtoList!!.indices) {
                return operateDetailDtoList!![pos]
            }
        }
        return null
    }
}

/**
 * 运营专区实体
 */
class OperationTemplate {
    /**
     * 图片链接
     */
    var imgUrl: String? = null

    /**
     * 菜单Code
     */
    var menuCode: String? = null

    /**
     * 资源名称
     */
    var resourceName: String? = null

    /**
     * 跳转类型
     */
    var jumpType: String = "0"

    /**
     * 外部链接
     */
    var externalLink: String? = null

    /**
     * 资源值
     */
    var resourceValue: String? = null

    /**
     * 菜单位置
     */
    var location: String? = null

    /**
     * 资源类型
     */
    var resourceType: String? = null
}

/**
 * 子菜单实体
 */
class SubMenu {
    /**
     * 外部链接
     */
    var externalLink: String? = null

    /**
     * 未选中图标
     */
    var unSelectIcon: String? = null

    /**
     * 跳转类型
     */
    var jumpType: Int = 0

    /**
     * 图标大小
     */
    var iconSize: String? = null

    /**
     * 选中图标
     */
    var selectIcon: String? = null

    /**
     * 菜单名称
     */
    var name: String? = null

    /**
     * 菜单编码
     */
    var menuCode: String? = null
}

/**
 * 组件详情
 */
class CommponentDetail {
    /** common **/
    /**
     * 跳转类型
     */
    var jumpType: String? = "0"

    /**
     * 主标题
     */
    var mainTitle: String? = null

    /**
     * 菜单编码
     */
    var menuCode: String? = null

    /**
     * 外部链接
     */
    var externalLink: String? = null

    /**style image**/
    /**
     * 图片地址
     */
    var imageUrl: String? = null


    /**style customize**/
    /**
     * 背景颜色
     */
    var backgroundColor: String? = null

    /**
     * 主标题
     */
    var mainTile: String? = null

    /**
     * 图标
     */
    var icon: String? = null

    /**
     * 副标题
     */
    var subTitle: String? = null

    /**style menu**/
    /**
     * 选中图标
     */
    var selectIcon: String? = null

    /**
     * 未选中图标
     */
    var unSelectIcon: String? = null

    /**
     * 菜单名称
     */
    var name: String? = null
}

/**
 * 个人信息模板
 */
class PersonInfoTemplate {
    /**
     * 子标题类型
     */
    var subTitleType: String? = null

    /**
     * 子标题
     */
    var subTitle: String? = null

    /**
     * 未登录背景图
     */
    var noLoginBackgroundImage: String? = null

    /**
     * 主标题类型
     */
    var mainTitleType: String? = null

    /**
     * 主标题
     */
    var mainTitle: String? = null

    /**
     * 已登录背景图
     */
    var loginBackgroundImage: String? = null

    /**
     * 客户端类型
     */
    var clientType: String? = null
}