package com.daqsoft.thetravelcloudwithculture.ui.vm

import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.base.AdvCodeType
import com.daqsoft.provider.base.PublishChannel
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.net.TemplateApi
import com.daqsoft.provider.net.TemplateRepository
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.thetravelcloudwithculture.home.bean.*
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.travelCultureModule.net.MainRepository
import java.util.*
import kotlin.collections.HashMap


/**
 * 首页Vm
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-10-22
 * @since JDK 1.8.0_191
 */
class HomeFragmentVm : BaseViewModel() {
    /**
     * 顶部菜单
     */
    var topMenus = MutableLiveData<MutableList<HomeMenu>>()

    /**
     * 首页模块
     */
    var homeModules = MutableLiveData<MutableList<HomeModule>>()
    /**
     * 城市名片列表
     */
    var cityCards = MutableLiveData<MutableList<CityCardBean>>()
    /**
     * 首页模板
     */
    var homeTemplateLd = MutableLiveData<MutableList<StyleTemplate>>()
    var appIndexAds = MutableLiveData<HomeAd>()

    /**
     * 发现身边
     */
    var foundArouds = MutableLiveData<Pair<Int, MutableList<FoundAroundBean>>>()

    /**
     * 首页界面模板集合
     */
    var templateMaps: HashMap<Int, DelegateAdapter.Adapter<RecyclerView.ViewHolder>> = HashMap()
    /**
     * 机器人问候语
     */
    var itRobotInfoLiveData = MutableLiveData<ItRobotGreeting>()


    /**
     * 地区编码
     */
    var region: String? = ""

    /**
     * 经度
     */
    var lon: String? = ""

    /**
     * 纬度
     */
    var lat: String? = ""
    /**
     * 获取菜单
     */
    fun getAPPMenus(location: String) {
        mPresenter.value?.loading = true

        HomeRepository.service.getAPPMenuList(Constant.HOME_CLIENT_TYPE, location).excute(object :
            BaseObserver<HomeMenu>(mPresenter) {
            override fun onSuccess(response: BaseResponse<HomeMenu>) {
                topMenus.postValue(response.datas)
            }
        })
    }

    /**
     * 获取首页模块列表
     */
    fun getHomeModule(location: String) {

        mPresenter.value?.loading = true

        HomeRepository.service.getHomeModule(location).excute(object :
            BaseObserver<HomeModule>(mPresenter) {
            override fun onSuccess(response: BaseResponse<HomeModule>) {
                homeModules.postValue(response.datas)
            }
        })
    }

    /**
     * 获取首页模板
     */
    fun getHomeTemplate() {
        TemplateRepository.instance.service.getTemplate(TemplateApi.HOME)
            .excute(object : BaseObserver<StyleTemplate>() {
                override fun onSuccess(response: BaseResponse<StyleTemplate>) {
                    homeTemplateLd.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<StyleTemplate>) {
                    homeTemplateLd.postValue(null)
                }
            })
    }

    /**
     * 获取城市名片
     */
    fun getCityCard(region: String) {

        mPresenter.value?.loading = false

        HomeRepository.service.getCityCardS(region).excute(object :
            BaseObserver<CityCardBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<CityCardBean>) {
                cityCards.postValue(response.datas)
            }
        })
    }


    /**
     * 推荐活动列表
     */
    var recommendedActivitiesLiveData: MutableLiveData<Pair<Int, MutableList<ActivityBean>>> =
        MutableLiveData()

    /**
     * 获取推荐活动列表
     */
    fun getRecommendedActivities(classifyId: String, index:Int) {
        var params: HashMap<String, String> = HashMap()
        if (!classifyId.isNullOrEmpty()) {
            params["classifyId"] = classifyId
        } else {
            params["classifyId"] = ""
        }
        params["currPage"] = "1"
        params["pageSize"] = "3"
        params["orderType"] = "1"
        mPresenter?.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        MainRepository.service.getActivityList(params)
            .excute(object : BaseObserver<ActivityBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ActivityBean>) {
                    response.datas?.let {
                        if(it.isNotEmpty()){
                            recommendedActivitiesLiveData.postValue(Pair(index, it))
                        }
                    }
                }
            })
    }



    val collectLiveData = MutableLiveData<Pair<Int,Int>>()
    /**
     * 收藏接口
     */
    fun collection(resourceId: String, position: Int) {
        mPresenter?.value?.isNeedRecyleView = false
        mPresenter?.value?.loading = false
        CommentRepository.service.posClloection(resourceId, ResourceType.CONTENT_TYPE_ACTIVITY)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    ToastUtils.showMessage("收藏成功")
                    getRecommendedActivities("",position)
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    ToastUtils.showMessage("收藏失败，请稍后再试~")
                }
            })
    }

    /**
     * 取消收藏接口
     */
    fun collectionCancel(resourceId: String, position: Int) {
        mPresenter?.value?.isNeedRecyleView = false
        mPresenter?.value?.loading = false
        CommentRepository.service.posCollectionCancel(
            resourceId,
            ResourceType.CONTENT_TYPE_ACTIVITY
        )
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    ToastUtils.showMessage("取消收藏成功~")
                    getRecommendedActivities("",position)
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    ToastUtils.showMessage("取消收藏失败，请稍后再试~")
                }
            })
    }

    /**
     * 线路类型
     */
    val lineTypeList: MutableLiveData<Pair<Int, MutableList<LineTagBean>>> = MutableLiveData()

    /**
     * 获取精品线路类型列表
     */
    fun getLineTypeList(index: Int) {
        mPresenter.value?.loading = false
        HomeRepository.service.getHomeLineTagList("lineChannel")
            .excute(object : BaseObserver<LineTagBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<LineTagBean>) {
                    response.datas?.let {
                        lineTypeList.postValue(Pair(index, it))
                    }
                }
            })
    }

    /**
     * 精品线路
     */
    var lineList: MutableLiveData<Pair<Int, MutableList<HomeContentBean>>> = MutableLiveData()

    /**
     * 选中的精品线路类型
     */
    var lineType = ""

    /**
     * 获取精品线路
     */
    fun getContentList(index: Int) {
        mPresenter.value?.loading = false
        val param = java.util.HashMap<String, String>()
        param["channelCode"] = Constant.HOME_CONTENT_TYPE_lineChannel
        param["pageSize"] = "4"
        param["currPage"] = "1"
        param["tagId"] = lineType
        HomeRepository.service.getHomeContentList(param)
            .excute(object : BaseObserver<HomeContentBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeContentBean>) {
                    response.datas?.let {
                        lineList.postValue(Pair(index, it))
                    }
                }

                override fun onFailed(response: BaseResponse<HomeContentBean>) {
                    mError.postValue(response)
                }
            })
    }


    /**
     * 品牌展播
     */
    var branchBeanList: MutableLiveData<Pair<Int, MutableList<HomeBranchBean>>> = MutableLiveData()

    /**
     * 品牌展播列表
     */
    fun getBranchList(index: Int) {
        mPresenter.value?.loading = true
        val param = java.util.HashMap<String, String>()
        // queryType  【选填】默认为1 1 首页/品牌名片 2 城市名片 3 随机推荐
        param["queryType"] = "1"
        // 活动类型id
        param["pageSize"] = "6"
        param["currPage"] = "1"
        HomeRepository.service.getBranchList(param).excute(object : BaseObserver<HomeBranchBean>() {
            override fun onSuccess(response: BaseResponse<HomeBranchBean>) {
                response.datas?.let {
                    branchBeanList.postValue(Pair(index, it))
                }
            }

            override fun onFailed(response: BaseResponse<HomeBranchBean>) {
            }

        })
    }

    /**
     * 话题列表
     */
    val topicList: MutableLiveData<Pair<Int, MutableList<HomeTopicBean>>> = MutableLiveData()

    /**
     * 获取话题列表
     */
    fun getTopicList(index: Int) {
        mPresenter.value?.loading = false
        HomeRepository.service.getTopicList("10")
            .excute(object : BaseObserver<HomeTopicBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeTopicBean>) {
                    response.datas?.let {
                        if(it.isNotEmpty()){
                            topicList.postValue(Pair(index, it))
                        }
                    }
                }
            })
    }

    /**
     * 获取广告
     */
    fun getAppIndexAds() {
        mPresenter.value?.loading = false
        HomeRepository.service.getHomeAd(PublishChannel.MICRO_SITE, AdvCodeType.APP_INDEX_LOG)
            .excute(object : BaseObserver<HomeAd>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeAd>) {
                    appIndexAds.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<HomeAd>) {
                    appIndexAds.postValue(null)
                }
            })
    }


    /**
     * 资讯liveData
     */
    var informationLiveData = MutableLiveData<Pair<Int,MutableList<HomeContentBean>>>()
    /**
     * 资讯数据最大页数
     */
    var informationMaxPager:Int? = null
    /**
     * 获取周边
     * 资讯分页
     */
    var informationPager = 1
    /**
     * 获取资讯
     */
    fun getInformationList(position:Int) {
        informationMaxPager?.let {
            if (informationPager > it){
                informationPager = 1
            }
        }

        mPresenter.value?.loading = false
        val param = java.util.HashMap<String, String>()
//        param["channelCode"] = Constant.HOME_CONTENT_TYPE_systemChannel
        param["channelCode"] = "rmzx"
        param["pageSize"] = "9"
        param["currPage"] = informationPager.toString()
        HomeRepository.service.getHomeContentList(param)
            .excute(object : BaseObserver<HomeContentBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeContentBean>) {
                    response.page?.let {
                        informationMaxPager = it.totalPage
                    }
                    response.datas?.let {
                        if (it.isNotEmpty()){
                            informationLiveData.value = Pair(position,it)
                        }
                    }
                }
            })
    }


    /**
     * 故事标签LiveData
     */
    val storyTagLiveData = MutableLiveData<Pair<Int,MutableList<HomeStoryTagBean>>>()
    /**
     * 获取故事标签
     */
    fun getStoryTagList(position:Int) {
        val param = HashMap<String, String>()
        // recommend   是否推荐【选填】1：是 0：否
        param["recommend"] = "1"
        // top  是否置顶 选填】1：是 0：否
        param["top"] = "1"
        // 最小故事数量
        param["minStoryNum"] = "1"
        //  size 查询数量 【选填】默认查询全部
        param["size"] = "6"
        mPresenter.value?.loading = false
        HomeRepository.service.getHotStoryTagList(param)
            .excute(object : BaseObserver<HomeStoryTagBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeStoryTagBean>) {
                    response.datas?.let {
                        if (it.isNotEmpty()){
                            storyTagLiveData.value = Pair(position,it)
                        }
                    }
                }
            })
    }


    /**
     * 故事列表数据
     */
    val storyLiveData = MutableLiveData<Pair<Int,MutableList<StoreBean>>>()
    /**
     * 获取故事
     */
    fun getStoryList(position:Int) {
        val param = HashMap<String, String>()
        // homeCover   首页封面	number	【选填】是否首页封面1：是 0：否
        //
        param["tagId"] = "0"
        param["listCover"] = "1"
        //  pageSize
        param["pageSize"] = "20"
        mPresenter.value?.loading = false
        HomeRepository.service.getStoryListNeW(param)
            .excute(object : BaseObserver<StoreBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<StoreBean>) {
                    response.datas?.let {
                        if (it.isNotEmpty()){
                            storyLiveData.value = Pair(position,it)
                        }
                    }
                }
            })
    }


    /**
     * 城市名片liveData
     */
    val cityCardLiveData = MutableLiveData<Pair<Int,MutableList<BrandMDD>>>()
    /**
     * 获取城市名片
     */
    fun getCityCardList(position:Int) {
        mPresenter.value?.loading = false
        var siteId = SPUtils.getInstance().getString(SPKey.SITE_ID)
        var siteRegion: String? = SPUtils.getInstance().getString(SPKey.SITE_REGION)
        var type: String = "CITY"
        if (!siteRegion.isNullOrEmpty()) {
            if (siteRegion.endsWith("000")) {
                type = "CITY"
            } else {
                type = "county"
            }
        }
        HomeRepository.service.getMDDCity("10", type, siteId)
            .excute(object : BaseObserver<BrandMDD>(mPresenter) {
                override fun onSuccess(response: BaseResponse<BrandMDD>) {
                    response.datas?.let {
                        if (it.isNotEmpty()){
                            cityCardLiveData.value = Pair(position,it)
                        }
                    }
                }
            })
    }


    /**
     * 栏目livedata
     */
    val columnLiveData = MutableLiveData<Pair<Int,MutableList<SubChanelChildBean>>>()
    /**
     *获取精彩瞬间栏目信息
     */
    fun getColumnList(position: Int) {
        MainRepository.service.getActivitySubset("systemChannel")
            .excute(object : BaseObserver<SubChannelBean>() {
                override fun onSuccess(response: BaseResponse<SubChannelBean>) {
                    response.data?.let {
                        it.subset?.let {
                            if (it.isNotEmpty()){
                                columnLiveData.value = Pair(position,it as MutableList<SubChanelChildBean>)
                            }
                        }
                    }
                }
            })
    }


    fun getFounds(lat: Double, lon: Double, pos: Int) {
        mPresenter.value?.loading = false
        val param = HashMap<String, String>()
        param["distance"] = "2"
        param["size"] = "5"
        param["latitude"] = "$lat"
        param["longitude"] = "$lon"
        HomeRepository.service.getFoundList(param)
            .excute(object : BaseObserver<FoundAroundBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<FoundAroundBean>) {
                    response.datas?.let {
                        foundArouds.postValue(Pair(pos, it))
                    }
                }

                override fun onFailed(response: BaseResponse<FoundAroundBean>) {
                    foundArouds.postValue(null)
                }

            })
    }

    val guideInfoData = MutableLiveData<Pair<Int, TourHomeBean?>>()

    /**
     * 导游导览详情
     */
    fun getGuideInfo(id: String?, pos: Int) {
        if (id != null) {
            mPresenter.value?.loading = false
            mPresenter.value?.isNeedRecyleView = false
            HomeRepository.service.getGuideHomeDetail(id)
                .excute(object : BaseObserver<TourHomeBean>(mPresenter) {
                    override fun onSuccess(response: BaseResponse<TourHomeBean>) {
                        guideInfoData.postValue(Pair(pos, response.data))
                    }

                    override fun onFailed(response: BaseResponse<TourHomeBean>) {
                        guideInfoData.postValue(null)
                    }
                })
        }
    }

    /**
     * 获取机器人问候语
     *      * region	用户当前位置地区编码	string
     * token	用户token	string
     * lon	经度	number
     * lat	纬度	number
     * distance	距离	number	默认：1 单位：km
     * sign	唯一标识	string	【必填】建议生成uuid值，在页面没有清除缓存前该值生成后不改变
     */
    fun getItRobotGreeting() {
        var params: java.util.HashMap<String, String> = hashMapOf()
        if (!lon.isNullOrEmpty()) {
            params["lon"] = lon!!
        }
        if (!lat.isNullOrEmpty()) {
            params["lat"] = lat!!
        }
        var distance = ""
        if (!distance.isNullOrEmpty()) {
            params["distance"] = distance
        }
        if (!region.isNullOrEmpty()) {
            params["region"] = region!!
        }

        var sign: String = SPUtils.getInstance().getString(SPUtils.Config.APP_SIGN_ID, "")
        if (sign.isNullOrEmpty()) {
            sign = createSignUuid()

        }
        params["sign"] = sign
        HomeRepository.service.getItRobotGreetings(params)
            .excute(object : BaseObserver<ItRobotGreeting>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ItRobotGreeting>) {
                    itRobotInfoLiveData.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<ItRobotGreeting>) {
                    itRobotInfoLiveData?.postValue(null)
                }

            })


    }

    private fun createSignUuid(): String {
        var sign: UUID? = UUID.randomUUID()
        SPUtils.getInstance().put(SPUtils.Config.APP_SIGN_ID, sign.toString())
        return sign.toString()
    }

}