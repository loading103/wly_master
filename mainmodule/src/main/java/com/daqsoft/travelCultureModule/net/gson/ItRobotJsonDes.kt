package com.daqsoft.travelCultureModule.net.gson

import com.daqsoft.provider.bean.ItRobotDataBean
import com.daqsoft.provider.bean.ItRobotRequestBean
import com.google.gson.*
import java.lang.Exception
import java.lang.reflect.Type


/**
 * @Description
 * @ClassName   ItRobotJsonDes
 * @Author      luoyi
 * @Time        2020/5/22 11:30
 */
class ItRobotJsonDes : JsonDeserializer<ItRobotDataBean> {


    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): ItRobotDataBean {
        var itRobotDataBean: ItRobotDataBean = ItRobotDataBean()
        val jsonObject = json!!.asJsonObject
        var type: String? = jsonObject.get("type").asString
        itRobotDataBean.type = type
        var success: Int = jsonObject.get("success").asInt
        itRobotDataBean.success = success
        try {
            var qaType: String? = jsonObject.get("qaType")?.asString
            itRobotDataBean.qaType = qaType
        } catch (e: Exception) {

        }

        var resourceType: String? = jsonObject.get("resourceType").asString
        itRobotDataBean.resourceType = resourceType
        var jsonData = jsonObject.get("data")
        // 处理 后台返回 骚代码， data 可能为数组，也有可能为字符串
        if (jsonData != null && !jsonData.isJsonNull && context != null) {
            if (jsonData.isJsonArray) {
                var datas: MutableList<ItRobotRequestBean> = mutableListOf()
                var jsonList: JsonArray? = jsonData.asJsonArray
                if (jsonList != null) {

                    for (item in jsonList) {
                        if (item != null) {
                            var jo: JsonObject = item.asJsonObject
                            var itRobotRequestBean: ItRobotRequestBean = context!!.deserialize(jo, ItRobotRequestBean::class.java)
                            if (itRobotRequestBean != null) {
                                datas.add(itRobotRequestBean)
                            }
                        }
                    }
                }
                itRobotDataBean.data = datas
            } else {
                try {
                    var amseer = jsonData.asString
                    itRobotDataBean.answer = amseer
                } catch (e: Exception) {

                }
            }
        }

        return itRobotDataBean
    }
}