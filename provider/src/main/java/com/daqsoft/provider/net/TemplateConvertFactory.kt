package com.daqsoft.provider.net

import com.daqsoft.baselib.net.gsonTypeAdapters.GsonTypeAdapterFactory
import com.daqsoft.baselib.net.gsonTypeAdapters.ToleranceResponseBodyConverter
import com.daqsoft.provider.bean.StyleTemplate
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * @Description 自定义gson解析工厂
 * @ClassName   TemplateConvertFactory
 * @Author      luoyi
 * @Time        2020/10/9 13:52
 */
class TemplateConvertFactory : Converter.Factory {

    var gson: Gson? = null

    constructor() {
        this.gson = GsonBuilder()
            .setLenient()
            .registerTypeAdapterFactory(GsonTypeAdapterFactory())
            .registerTypeAdapter(StyleTemplate::class.java,TemplateDeserializer())
            .create()
    }

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        val adapter =
            gson!!.getAdapter(TypeToken.get(type))
        return ToleranceResponseBodyConverter(gson, adapter)
    }

}
