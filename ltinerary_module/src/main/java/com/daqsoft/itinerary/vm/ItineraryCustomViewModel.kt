package com.daqsoft.itinerary.vm

import androidx.collection.ArrayMap
import androidx.lifecycle.MutableLiveData
import com.amap.api.col.sln3.it
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.itinerary.bean.*
import com.daqsoft.itinerary.repository.ItineraryRepository
import com.daqsoft.provider.Constants
import com.daqsoft.provider.bean.ScenicBean
import com.daqsoft.provider.bean.ScenicDetailBean
import com.daqsoft.provider.network.net.excut
import com.daqsoft.venuesmodule.repository.bean.VenuesListBean
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject

/**
 * @Author：      邓益千
 * @Create by：   2020/4/23 19:40
 * @Description： 行程定制model层
 */
class ItineraryCustomViewModel: BaseViewModel() {

    init {
        mPresenter.value?.loading = false
    }

    var total = 0


    var pageTotal = 0

    /**场馆总数*/
    var venueTotal = 0

    /**在行程中添加场馆or景区 成功的标识*/
    val addResult = MutableLiveData<String>()

    /**行程详情*/
    val itineraryDetail = MutableLiveData<ItineraryDetailBean>()

    /**场馆的标签*/
    val venuesLabel = MutableLiveData<MutableList<LabelBean>>()

    /**场馆列表*/
    val venuesList = MutableLiveData<MutableList<VenuesListBean>>()

    /**城市列表*/
    var cityList = MutableLiveData<MutableList<CityBean>>()

    /**省列表*/
    var provinceList = MutableLiveData<MutableList<ProvinceBean>>()

    /**站点信息*/
    val siteInfo = MutableLiveData<SiteInfoBean>()

    /**根据选择的标签，响应的景区和场馆*/
    val customBean = MutableLiveData<CustomBean>()

    /**根据地区编码返回景区列表*/
    val customScenic = MutableLiveData<MutableList<CunsomScenicBean>>()

    /**景区的标签*/
    val scenicLabel = MutableLiveData<MutableList<LabelBean>>()

    /**景区列表*/
    val scenicList = MutableLiveData<MutableList<ScenicBean>>()

    /**
     * 景区详情
     */
    val scenicDetail = MutableLiveData<ScenicDetailBean>()

    //获取地区
    val areas = MutableLiveData<MutableList<ChildRegion>>()

    /**
     * 目的地,城市列表
     * @param type ALL(所有)
     *             nation(国家)
     *             province(省)
     *             city(市)
     *             county(县)
     */
    fun getDestination(cityType: String = "city"){
        mPresenter.value?.loading = true
        ItineraryRepository.service.getDestination(cityType).excute(object: BaseObserver<CityBean>(){
            override fun onSuccess(response: BaseResponse<CityBean>) {
                cityList.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<CityBean>) {
                super.onFailed(response)
                ToastUtils.showMessage(response.message)
            }
        })
    }

    /**获取景区的标签*/
    fun getScenicLabels(scenicTheme: MutableList<LabelBean>,scenicCrowd: MutableList<LabelBean>){
        mPresenter.value?.loading = true
        // 景区的主题
        val obs = ItineraryRepository.service.getLabels("SCENIC_THEME", Constants.TYPE_SCENERY)
        // 景区的人群
        val obs2 = ItineraryRepository.service.getLabels("SUITABLE_FOR_PEOPLE",Constants.TYPE_SCENERY)
        //合并请求
        Observable.zip(obs,obs2, BiFunction<BaseResponse<LabelBean>, BaseResponse<LabelBean>, BaseResponse<LabelBean>> { t1, t2 ->
            scenicTheme.addAll(t1.datas!!)
            scenicCrowd.addAll(t2.datas!!)
            t1
        }).excute(object: BaseObserver<LabelBean>(){
            override fun onSuccess(response: BaseResponse<LabelBean>) {
                scenicLabel.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<LabelBean>) {
                super.onFailed(response)
                ToastUtils.showMessage(response.message)
            }
        })
    }

    /**
     * 获取场馆的标签
     * @param venueTheme
     * @param venueCrowd
     */
    fun getVenueLabel(venueTheme: MutableList<LabelBean>,venueCrowd: MutableList<LabelBean>){
        mPresenter.value?.loading = true
        // 场馆主题标签
        val obs = ItineraryRepository.service.getLabels("VENUES_THEME", Constants.TYPE_VENUE)
        // 场馆的人群标签
        val obs2 = ItineraryRepository.service.getLabels("SUITABLE_FOR_PEOPLE",Constants.TYPE_VENUE)
        //合并请求
        Observable.zip(obs,obs2, BiFunction<BaseResponse<LabelBean>, BaseResponse<LabelBean>, BaseResponse<LabelBean>> { t1, t2 ->
            venueTheme.addAll(t1.datas!!)
            venueCrowd.addAll(t2.datas!!)
                t1
            }).excute(object: BaseObserver<LabelBean>(){
            override fun onSuccess(response: BaseResponse<LabelBean>) {
                venuesLabel.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<LabelBean>) {
                super.onFailed(response)
                ToastUtils.showMessage(response.message)
            }
        })
    }

    /**根据参数查询场馆和景区
     * @param label 参数类
     * @param region 地区编码
     */
    fun getVenueAndScenic(label: ItineraryLabelBean) {
        ItineraryRepository.service.getVenueAndScenic(label.labelName).excute(object: BaseObserver<CustomBean>(){
            override fun onSuccess(response: BaseResponse<CustomBean>) {
                customBean.postValue(response.data)
            }

            override fun onFailed(response: BaseResponse<CustomBean>) {
                super.onFailed(response)
                ToastUtils.showMessage(response.message)
            }
        })
    }

    /**
     * 景区列表
     * @param label 主题类型
     * @param crowd 适合人群
     * @param currPage 查询页码
     */
    fun getScenicList(label: String, crowd: String,currPage: Int,region: String = "", pageSize: String= "10"){
        ItineraryRepository.service.getScenicList(label,crowd,currPage.toString(),region,pageSize).excute(object: BaseObserver<ScenicBean>(){
            override fun onSuccess(response: BaseResponse<ScenicBean>) {
                response.datas?.forEach {
                    it.type = Constants.TYPE_SCENERY //景区标识
                }
                total = response.page!!.total
                pageTotal = response.page!!.totalPage
                scenicList.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<ScenicBean>) {
                super.onFailed(response)
                ToastUtils.showMessage(response.message)
            }
        })
    }

    /**
    * 场馆列表
    * @param label 主题类型
     * @param crowd 适合人群
     * @param currPage 查询页码
    */
    fun getVenueList(label: String, crowd: String,currPage: Int ,region: String = "", pageSize: String = "10"){
        ItineraryRepository.service.getVenueList(label,crowd,currPage.toString(),region,pageSize).excute(object: BaseObserver<VenuesListBean>(){
            override fun onSuccess(response: BaseResponse<VenuesListBean>) {
                response.datas?.forEach {
                    it.resType = Constants.TYPE_VENUE
                }
                venuesList.postValue(response.datas)
                venueTotal = response.page!!.total
            }

            override fun onFailed(response: BaseResponse<VenuesListBean>) {
                ToastUtils.showMessage(response.message)
            }
        })
    }

    /**保存行程设置*/
    fun saveItinerary(label: ItineraryLabelBean): MutableLiveData<String>{
        val json = Gson().toJson(label)
        val body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"),json)
        val result = MutableLiveData<String>()
        ItineraryRepository.service.saveItinerary(body).excute(object: Observer<ResponseBody>{
            override fun onComplete() {}
            override fun onError(e: Throwable) {}
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(response: ResponseBody) {
                val resultJson = response.string()
                val json = JSONObject(resultJson)
                if (json.getInt("code") == 0) {
                    val body = JSONObject(json.getString("data"))
                    result.postValue(body.getInt("id").toString())
                } else {
                    ToastUtils.showMessage(json.getString("message"))
                }
            }
        })
        return result
    }

    /**
     * 行程详情
     * @param itineraryId 行程id
     */
    fun getItineraryDetail(itineraryId: String){
        mPresenter.value?.loading = true
        ItineraryRepository.service.getItineraryDetail(itineraryId).excute(object : BaseObserver<ItineraryDetailBean>() {
            override fun onSuccess(response: BaseResponse<ItineraryDetailBean>) {
                itineraryDetail.postValue(response.data)
            }

            override fun onFailed(response: BaseResponse<ItineraryDetailBean>) {
                super.onFailed(response)
                ToastUtils.showMessage(response.message)
            }
        })
    }

    fun getCustomScenic(region: String,size: Int){
        ItineraryRepository.service.getCustomScenic(region,size.toString()).excute(object : BaseObserver<CunsomScenicBean>() {
            override fun onSuccess(response: BaseResponse<CunsomScenicBean>) {
                customScenic.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<CunsomScenicBean>) {
                super.onFailed(response)
                ToastUtils.showMessage(response.message)
            }
        })
    }

    /**在行程中添加场馆or景区*/
    fun operateSource(params: Map<String,Any>){
        mPresenter.value?.loading = true
        val json = Gson().toJson(params)
        val body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"),json)
        ItineraryRepository.service.operateSource(body).excute(object : Observer<ResponseBody> {
            override fun onComplete() {}
            override fun onSubscribe(d: Disposable) {}
            override fun onError(e: Throwable) {}
            override fun onNext(response: ResponseBody) {
                val resultJson = response.string()
                val json = JSONObject(resultJson)
                addResult.value = json.getString("message")
                if (json.getInt("code") != 0){
                    ToastUtils.showMessage(json.getString("message"))
                }
            }
        })
    }

    /**获取站点ID*/
    fun getSiteCode(){
        ItineraryRepository.service.getSiteCode().excute(object: BaseObserver<SiteInfoBean>(){
            override fun onSuccess(response: BaseResponse<SiteInfoBean>) {
                siteInfo.value = response.data
            }

            override fun onFailed(response: BaseResponse<SiteInfoBean>) {
                super.onFailed(response)
                ToastUtils.showMessage(response.message)
            }
        })
    }

    fun getProvince(){
        ItineraryRepository.service.getProvince().excute(object: BaseObserver<ProvinceBean>(){
            override fun onSuccess(response: BaseResponse<ProvinceBean>) {
                provinceList.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<ProvinceBean>) {
                super.onFailed(response)
                ToastUtils.showMessage(response.message)
            }
        })
    }

    /**
     * 站点下级区域(两层)
     */
    fun getChildRegions() {
        mPresenter.value?.loading = false
        mPresenter.value?.isNeedRecyleView=false
        ItineraryRepository.service.getChildRegions().excute(object : BaseObserver<ChildRegion>() {
            override fun onSuccess(response: BaseResponse<ChildRegion>) {
                areas.postValue(response.datas)
            }
        })
    }

}