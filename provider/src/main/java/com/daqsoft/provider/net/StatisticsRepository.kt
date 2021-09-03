package com.daqsoft.provider.net

import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.net.HeaderInterceptor
import com.daqsoft.baselib.net.gsonTypeAdapters.FaultToleranceConvertFactory
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.UIHelperUtils
import com.daqsoft.provider.R
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.ScenicComfortBean
import com.daqsoft.provider.network.RetrofitFactory
import com.umeng.socialize.utils.CommonUtil
import io.reactivex.Observable
import retrofit2.http.*
import java.lang.Exception

/**
 * @Description 统计服务数据上传类
 * @ClassName   StatisticsRespository
 * @Author      luoyi
 * @Time        2020/7/30 15:26
 */
class StatisticsRepository {

    val service: StatisticsService by lazy {
        RetrofitFactory.Builder()
            .setBaseUrl(BaseApplication.dataUploadUrl)
            .addInterceptor(HeaderInterceptor())
            .addConvertFactory(FaultToleranceConvertFactory.create())
            .build(StatisticsService::class.java)
    }

    companion object {
        val instance: StatisticsRepository by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            StatisticsRepository()
        }

    }

    /**
     * 统计文章
     * @param contentName 文章名称
     */
    fun statisticsContent(contentName: String?) {
        if (contentName.isNullOrEmpty() || BaseApplication.isDebug || BaseApplication.appArea == "ws") {
            return
        }
        var params: HashMap<String, String> = HashMap()
        params["site"] = BaseApplication.siteCode
        var appName: String? = UIHelperUtils.getAppName()
        if (!appName.isNullOrEmpty()) {
            params["project"] = appName
        } else {
            params["project"] = BaseApplication.context.getString(R.string.app_name)
        }
        params["clientType"] = "Android"
        var phoneNumer: String? = SPUtils.getInstance().getString(SPKey.PHONE)
        if (!phoneNumer.isNullOrEmpty()) {
            params["uniqueId"] = phoneNumer
        }
        params["special"] = contentName
        var adCode: String? = SPUtils.getInstance().getString(SPKey.SITE_REGION)
        adCode?.let {
            params["regionCode"] = adCode
        }
        service?.statistics(params).excute(object : BaseObserver<Any>() {
            override fun onSuccess(response: BaseResponse<Any>) {

            }

        })
    }

    /**
     * 统计页面访问
     * @param moduleName 模块名称
     * @param status 状态
     *
     */
    fun statisticsPage(moduleName: String?, status: Int) {
        if (moduleName.isNullOrEmpty() || BaseApplication.isDebug|| BaseApplication.appArea == "ws") {
            return
        }
        var params: HashMap<String, String> = HashMap()
        params["site"] = BaseApplication.siteCode
        var appName: String? = UIHelperUtils.getAppName()
        if (!appName.isNullOrEmpty()) {
            params["project"] = appName
        } else {
            params["project"] = BaseApplication.context.getString(R.string.app_name)
        }
        params["clientType"] = "Android"
        var phoneNumer: String? = SPUtils.getInstance().getString(SPKey.PHONE)
        if (!phoneNumer.isNullOrEmpty()) {
            params["uniqueId"] = phoneNumer
        }
        params["module"] = moduleName
        params["status"] = status.toString()
        var adCode: String? = SPUtils.getInstance().getString(SPKey.SITE_REGION)
        adCode?.let {
            params["regionCode"] = adCode
        }
        service?.statistics(params).excute(object : BaseObserver<Any>() {
            override fun onSuccess(response: BaseResponse<Any>) {
            }

        })
    }

    /**
     * 栏目点击
     * @param column 栏目名称
     *
     */
    fun statistcsClick(column: String) {
        if (column.isNullOrEmpty() || BaseApplication.isDebug || BaseApplication.appArea == "ws") {
            return
        }
        var params: HashMap<String, String> = HashMap()
        params["site"] = BaseApplication.siteCode
        var appName: String? = UIHelperUtils.getAppName()
        if (!appName.isNullOrEmpty()) {
            params["project"] = appName
        } else {
            params["project"] = BaseApplication.context.getString(R.string.app_name)
        }
        params["clientType"] = "Android"
        var phoneNumer: String? = SPUtils.getInstance().getString(SPKey.PHONE)
        if (!phoneNumer.isNullOrEmpty()) {
            params["uniqueId"] = phoneNumer
        }
        params["column"] = column
        params["region"] = "下"
        var adCode: String? = SPUtils.getInstance().getString(SPKey.SITE_REGION)
        adCode?.let {
            params["regionCode"] = adCode
        }

        service?.statistics(params).excute(object : BaseObserver<Any>() {
            override fun onSuccess(response: BaseResponse<Any>) {

            }

        })

    }

    /**
     *  模块点击
     * @param module 模块名称
     */
    fun statistcsModuleClick(module: String?, region: String) {
        if (module.isNullOrEmpty() || BaseApplication.isDebug || BaseApplication.appArea == "ws") {
            return
        }
        var params: HashMap<String, String> = HashMap()
        params["site"] = BaseApplication.siteCode
        var appName: String? = UIHelperUtils.getAppName()
        if (!appName.isNullOrEmpty()) {
            params["project"] = appName
        } else {
            params["project"] = BaseApplication.context.getString(R.string.app_name)
        }
        params["clientType"] = "Android"
        var phoneNumer: String? = SPUtils.getInstance().getString(SPKey.PHONE)
        if (!phoneNumer.isNullOrEmpty()) {
            params["uniqueId"] = phoneNumer
        }
        params["module"] = module
        params["region"] = region
        var adCode: String? = SPUtils.getInstance().getString(SPKey.SITE_REGION)
        adCode?.let {
            params["regionCode"] = adCode
        }
        try {
            service?.statistics(params).excute(object : BaseObserver<Any>() {
                override fun onSuccess(response: BaseResponse<Any>) {

                }
            })
        } catch (e: Exception) {

        }

    }

    /**
     * 统计服务
     */
    interface StatisticsService {
        /**
         * 统计信息
         */
        @FormUrlEncoded
        @POST(ProviderApi.STATISTICS_URL)
        fun statistics(@FieldMap hashMap: HashMap<String, String>): Observable<BaseResponse<Any>>

        /**
         * 获取景区舒适度
         */
        @GET(ProviderApi.MAX_REAL_PEOPLE)
        fun getMaxRealPeole(
            @Query("key") key: String = "mXKeKUVybFVz",
            @Query("sign") sign: String = "HIzMNyRoKLXdUxsnwCJZcKeaa"
            ,
            @Query("resourcecode") resourcecode: String,
            @Query("beginTime") beginTime: String,
            @Query("endTime") endTime: String
        ): Observable<BaseResponse<ScenicComfortBean>>

    }


}