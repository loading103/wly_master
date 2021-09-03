package com.daqsoft.volunteer.volunteer.vm

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.volunteer.bean.RankData
import com.daqsoft.volunteer.bean.VolunteerRankBean
import com.daqsoft.volunteer.net.VolunteerRepository
import com.daqsoft.volunteer.utils.Constant

/**
 *@package:com.daqsoft.volunteer.volunteer.vm
 *@date:2020/6/4 15:09
 *@author: caihj
 *@des:TODO
 **/
class VolunteerRankVM:BaseViewModel() {
    /**
     * 排名类型 serviceTime 服务时长 integral积分
     */
    var rankingTypeEnum = "serviceTime"

    /**
     * 榜单分类 volunteer:志愿者榜单 volunteerTeam:志愿者团队榜单
     */
    var listClassEnum = "volunteer"

    /**
     * week:周榜 month:月榜 quarter:季度榜单 year:年榜单
     */
    var listTypeEnum = "week"

    var currentPage = 1
    val pageSize = "10"
    var volunteerRank = MutableLiveData<VolunteerRankBean>()
    var time = ""
    fun getRankData(){
        val params = HashMap<String,String>()
        params["listTypeEnum"] = "week"
        params["pageSize"] = pageSize
        params["currPage"] = currentPage.toString()
        params["listTypeEnum"] = listTypeEnum
        params["rankingTypeEnum"] = rankingTypeEnum
        params["listClassEnum"] = listClassEnum
        if(time.isNotEmpty()){
            params["time"] = time
        }
        VolunteerRepository.service.getVolunteerRankList(params).excute(object : BaseObserver<VolunteerRankBean>(){
            override fun onSuccess(response: BaseResponse<VolunteerRankBean>) {
                volunteerRank.postValue(response.data)
            }
        })
    }

    var ranks = MutableLiveData<MutableList<RankData>>()


    fun getVolunteerRank(){
        val params = HashMap<String,String>()
        params["listTypeEnum"] = "week"
        params["pageSize"] = pageSize
        params["currPage"] = currentPage.toString()
        params["listTypeEnum"] = listTypeEnum
        params["rankingTypeEnum"] = rankingTypeEnum
        params["listClassEnum"] = listClassEnum
        VolunteerRepository.service.getVolunteerRank(params).excute(object : BaseObserver<RankData>(){
            override fun onSuccess(response: BaseResponse<RankData>) {
                ranks.postValue(response.datas)
            }
        })
    }
}