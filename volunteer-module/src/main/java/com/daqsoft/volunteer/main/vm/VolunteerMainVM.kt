package com.daqsoft.volunteer.main.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.SiteInfoBean
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.net.CommonRepository
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.thetravelcloudwithculture.home.bean.HomeContentBean
import com.daqsoft.volunteer.bean.*
import com.daqsoft.volunteer.net.VolunteerRepository
import com.daqsoft.volunteer.utils.Constant

/**
 *@package:com.daqsoft.volunteer.main.vm
 *@date:2020/6/1 17:34
 *@author: caihj
 *@des:TODO
 **/
class VolunteerMainVM:BaseViewModel() {
    /**
     * 志愿者和志愿者团队数量
     */
    var volunteerCount = MutableLiveData<VolunteerCountBean>()

    /**
     * 获取志愿者数和团队数量
     */
    fun getVolunteerCount (region:String){
        VolunteerRepository.service.getVolunteerCount(region).excute(object :BaseObserver<VolunteerCountBean>(){
            override fun onSuccess(response: BaseResponse<VolunteerCountBean>) {
                volunteerCount.postValue(response.data)
            }

        })
    }

    var volunteerFocus = MutableLiveData<MutableList<VolunteerTeamBean>>()

    /**
     * 获取我的关注
     */
    fun getVolunteerFocus(){
        VolunteerRepository.service.getMyVolunteerFocusList().excute(object :BaseObserver<VolunteerTeamBean>(){
            override fun onSuccess(response: BaseResponse<VolunteerTeamBean>) {
                volunteerFocus.postValue(response.datas)
            }

        })
    }

    /**
     * 地区
     */
    val areas = MutableLiveData<MutableList<ChildRegion>>()

    /**
     * 站点下级区域(两层)
     */
    fun getChildRegions() {
        VolunteerRepository.service.getChildRegions().excute(object : BaseObserver<ChildRegion>() {
            override fun onSuccess(response: BaseResponse<ChildRegion>) {
                areas.postValue(response.datas)
            }
        })
    }
    /**
     * 站点详情
     */
    var siteBean = MutableLiveData<SiteInfoBean>()

    /**
     * 获取站点信息
     */
    fun siteInfo() {
        CommonRepository.service.getSiteInfo()
            .excute(object : BaseObserver<SiteInfoBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<SiteInfoBean>) {
                    if (response.code == 0 && response.data != null) {
                        SPUtils.getInstance().put(SPKey.SHOP_URL, response.data?.shopUrl)
                        SPUtils.getInstance().put(SPKey.SITE_CODE, response.data?.siteCode)
                        // 保存id
                        SPUtils.getInstance().put(SPKey.SITE_ID, response.data?.id.toString())
                        SPUtils.getInstance().put(SPKey.REGION,response.data?.region)
                        SPUtils.getInstance().put(SPKey.REGION_NAME,response.data?.regionName)
                    }
                    siteBean.postValue(response.data)
                }
            })
    }

    var volunteerServiceRank = MutableLiveData<VolunteerRankBean>()
    var volunteerIntegralRank = MutableLiveData<VolunteerRankBean>()
    var volunteerTeamServiceRank = MutableLiveData<VolunteerRankBean>()
    var volunteerTeamIntegralRank = MutableLiveData<VolunteerRankBean>()

    /**
     * 获取排行榜
     */
    fun getRankList(type:String,teamType:String,region:String){
        val params = HashMap<String,String>()
        params["listTypeEnum"] = "week"
        params["listClassEnum"] = teamType
        params["rankingTypeEnum"] = type
        params["pageSize"] = "3"
        params["region"] = region

        VolunteerRepository.service.getVolunteerRankList(params).excute(object :BaseObserver<VolunteerRankBean>(){
            override fun onSuccess(response: BaseResponse<VolunteerRankBean>) {
                if(type == Constant.INTEGRAL){
                    if(teamType == Constant.VOLUNTEER){
                        volunteerIntegralRank.postValue(response.data)
                    }else{
                        volunteerTeamIntegralRank.postValue(response.data)
                    }
                }else{
                    if(teamType == Constant.VOLUNTEER){
                        volunteerServiceRank.postValue(response.data)
                    }else{
                        volunteerTeamServiceRank.postValue(response.data)
                    }
                }
            }
        })
    }

    var activities = MutableLiveData<MutableList<VolunteerActivityBean>>()

    /**
     * 志愿招募
     */
    fun getVolunteerActivityList(){
        val params = HashMap<String,String>()
        VolunteerRepository.service.getVolunteerActivityList(params).excute(object :BaseObserver<VolunteerActivityBean>(){
            override fun onSuccess(response: BaseResponse<VolunteerActivityBean>) {
                activities.postValue(response.datas)
            }

        })
    }


    val collectLiveData = MutableLiveData<Int>()

    val canceCollectLiveData = MutableLiveData<Int>()


    /**
     * 收藏接口
     */
    fun collection(resourceId: String, position: Int) {
        mPresenter?.value?.isNeedRecyleView = false
        mPresenter?.value?.loading = false
        CommentRepository.service.posClloection(resourceId, ResourceType.CONTENT_TYPE_ACTIVITY_VOLUNT)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    ToastUtils.showMessage("收藏成功~")
                    collectLiveData.postValue(position)
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
            ResourceType.CONTENT_TYPE_ACTIVITY_VOLUNT
        )
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    ToastUtils.showMessage("取消收藏成功~")
                    canceCollectLiveData.postValue(position)

                }

                override fun onFailed(response: BaseResponse<Any>) {
                    ToastUtils.showMessage("取消收藏失败，请稍后再试~")
                }
            })
    }


    // 获取资讯列表
    var volunteerNews = MutableLiveData<MutableList<HomeContentBean>>()

    /**
     * 获取资讯列表
     */
    fun getVolunteerNews() {
        val params = HashMap<String,String>()
        params["channelCode"] = com.daqsoft.provider.bean.Constant.HOME_CONTENT_TYPE_volunteerNews
        params["pageSize"] = "3"
        mPresenter.value?.loading = false
        VolunteerRepository.service.getVolunteerNewsList(params).excute(object : BaseObserver<HomeContentBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<HomeContentBean>) {
                volunteerNews.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<HomeContentBean>) {
                volunteerNews.postValue(null)
            }

        })
    }
}