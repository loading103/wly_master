package com.daqsoft.volunteer.bean

/**
 *@package:com.daqsoft.volunteer.bean
 *@date:2020/6/9 16:54
 *@author: caihj
 *@des:表格数据实体
 **/
data class VolunteerTeamSignTableBean(
    val type:String,// 数据类型 TITLE,CONTENT,TIME,BOTTOM
    val datas:List<String>
)

class TableConst{
    companion object{
        // 标题
        const val TITLE = "TITLE"
        // 内容
        const val CONTENT = "CONTENT"
        // 时间
        const val TIME = "TIME"
        // 底部
        const val BOTTOM = "BOTTOM"
    }
}