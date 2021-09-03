package com.daqsoft.provider.network.venues.bean

/**
 * 文化场馆的类别标签实体类
 * @author 黄熙
 * @date 2020/1/22 0022
 * @version 1.0.0
 * @since JDK 1.8
 */
class LabelBean (
    /**
     * 名称
     */
    var name :String,
    /**
     * 类型，0：支付方式，1：场馆类型，2：场馆标签
     */
    var type:Int
)

/**
 * 景区label
 */
class ScenicLabelBean(
    /**
     * 名称
     */
    var name: String,
    /**
     * 类型 1 level 2 诚信分 3其它
     */
    var type: Int)


