package com.daqsoft.volunteer.utils

/**
 * @package name：com.daqsoft.volunteer.utils
 * @date 2020/9/2 14:04
 * @author zp
 * @describe 志愿者模块枚举
 */
class EnumGlobal {

    /**
     * 排名类型
     * @property value String
     * @constructor
     */
    enum class RankingTypeEnum(var value: String){
        SERVICETIME("serviceTime"),//服务时长
        INTEGRAL("integral");//积分
    }

    /**
     * 榜单分类
     * @property value String
     * @constructor
     */
    enum class ListClassEnum(var value: String){
        VOLUNTEER("volunteer"),//志愿者
        VOLUNTEERTEAM("volunteerTeam");//志愿者团队
    }

}