package com.daqsoft.provider.net

import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.bean.StyleTemplate
import com.google.gson.*
import org.json.JSONArray
import java.lang.Exception
import java.lang.reflect.Type

/**
 * @Description
 * @ClassName   TemplateDeserializer
 * @Author      luoyi
 * @Time        2020/10/9 13:29
 */
class TemplateDeserializer : JsonDeserializer<StyleTemplate> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): StyleTemplate {
        var template = StyleTemplate()
        var gson = GsonBuilder().create()
        if (json != null) {
            var jsonObject = json.asJsonObject
            var detail = jsonObject.get("layoutDetail")
            var moduleType = jsonObject.get("moduleType")
            template.moduleType = moduleType.asString
            detail?.let {
                if (detail.isJsonArray) {
                    var temps: MutableList<CommonTemlate> = mutableListOf()
                    for (obj: JsonElement in detail.asJsonArray) {
                        if (obj != null) {
                            try {
                                var commonTemlate = gson.fromJson(obj, CommonTemlate::class.java)
                                if (commonTemlate != null) {
                                    temps.add(commonTemlate)
                                }
                            } catch (e: Exception) {

                            }
                        }
                    }
                    template.layoutDetails = temps
                } else if (detail.isJsonObject) {
                    var temp: CommonTemlate? = null
                    try {
                        temp = gson.fromJson(detail, CommonTemlate::class.java)
                    } catch (e: Exception) {

                    }
                    template.layoutDetail = temp
                }
            }
        }
        return template
    }
}