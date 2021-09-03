package com.daqsoft.provider.businessview.model

import com.daqsoft.baselib.base.BaseApplication

/**
 * @Description 分享model
 * @ClassName   ShareModel
 * @Author      luoyi
 * @Time        2020/6/19 11:28
 */
object ShareModel {


    /**
     * 景区分享
     */
    fun getScenicDetailUrl(id: String): String {
        return "${BaseApplication.webSiteUrl}scenic-detail/${id}"
    }

    /**
     * 酒店详情
     */
    fun getHotelDetailUrl(id: String): String {
        return "${BaseApplication.webSiteUrl}hotel-detail/${id}"
    }

    /**
     * 美食详情
     */
    fun getFoodDetailUrl(id: String): String {
        return "${BaseApplication.webSiteUrl}food-detail/${id}"
    }

    /**
     * 场馆详情
     */
    fun getVenueDetailUrl(id: String): String {
        return "${BaseApplication.webSiteUrl}venues-detail/${id}"
    }

    /**
     * 活动详情
     */
    fun getActivityDetailUrl(id: String): String {
        return "${BaseApplication.webSiteUrl}activity-detail/${id}"
    }
    /**
     * 娱乐场所详情
     */
    fun getPlayGroundDetailUrl(id: String): String {
        return "${BaseApplication.webSiteUrl}entertain-detail/${id}"
    }
    /**
     * 获取文章详情
     */
    fun getContentDetailUrl(id: String): String {
        return "${BaseApplication.webSiteUrl}news-detail/${id}"
    }

    /**
     * 课程详情
     */
    fun getCourseDescUrl(id: String): String {
        return "${BaseApplication.webSiteUrl}course-desc/${id}"
    }

    /**
     * 品牌详情
     */
    fun getPinPaiDetailUrl(id: String): String {
        return "${BaseApplication.webSiteUrl}brand-desc/${id}"
    }
    /**
     * 故事详情
     */
    fun getStoryDetailUrl(id: String): String {
        return "${BaseApplication.webSiteUrl}story-detail/${id}"
    }

    /**
     * 话题详情
     */
    fun getTopicDetailUrl(id: String): String {
        return "${BaseApplication.webSiteUrl}topic-detail/${id}"
    }
    /**
     * 攻略详情
     */
    fun getStrateDetailUrl(id: String): String {
        return "${BaseApplication.webSiteUrl}strategy-detail/${id}"
    }
    /**
     * 非遗项目详情
     */
    fun getProjectDescUrl(id: String): String {
        return "${BaseApplication.webSiteUrl}project-desc/${id}"
    }

    /**
     *传承人详情
     */
    fun getSuccessorDesc(id: String): String {
        return "${BaseApplication.webSiteUrl}successor-desc/${id}"
    }

    /**
     * 体验基地详情
     */
    fun getExperienceDesc(id: String): String {
        return "${BaseApplication.webSiteUrl}experience-desc/${id}"
    }

    /**
     * 传习基地详情
     */
    fun getInheritanceDesc(id: String): String {
        return "${BaseApplication.webSiteUrl}inheritance-desc/${id}"
    }
    /**
     * 慢直播详情
     */
    fun getSlowDesc(id: String): String {
        return "${BaseApplication.webSiteUrl}live-detail/${id}"
    }
    /**
     * 农家乐详情
     */
    fun getCountryHapDesc(id: String): String {
        return "${BaseApplication.webSiteUrl}homestay-detail/${id}"
    }
    /**
     * 城市名片详情
     */
    fun getCityDesc(id: String): String {
        return "${BaseApplication.webSiteUrl}destination-city/${id}"
    }

    /**
     * 社团详情
     */
    fun getClubDesc(id: String): String {
        return "${BaseApplication.webSiteUrl}group-desc/${id}"
    }

    /**
     * 乡村详情
     */
    fun getXCDesc(id: String): String {
        return "${BaseApplication.webSiteUrl}rural-detail/${id}"
    }
    /**
     * 资讯详情
     */
    fun getZXDesc(id: String): String {
        return "${BaseApplication.webSiteUrl}rural-detail/${id}"
    }
    /**
     * 图片咨询
     */
    fun getPicZXDesc(id: String): String {
        return "${BaseApplication.webSiteUrl}swiper-detail/${id}"
    }
    /**
     * 智能行程
     */
    fun getZnXcDesc(id: String): String {
        return "${BaseApplication.webSiteUrl}itinerary-detail/${id}"
    }
    /**
     * 我的投票
     */
    fun getVoteDesc(id: String): String {
        return "${BaseApplication.webSiteUrl}vote-detail/${id}"
    }
    /**
     * 我发起的投票
     */
    fun getMineVoteDesc(id: String): String {
        return "${BaseApplication.webSiteUrl}my-join/${id}"
    }
    /**
     * 导游导览
     */
    fun getDaoYouDesc(id: String): String {
        return "${BaseApplication.webSiteUrl}guide-map?tourId=${id}"
    }
    /**
     * 社团成员
     */
    fun getClubMemberDesc(id: String): String {
        return "${BaseApplication.webSiteUrl}member-introduce/${id}"
    }

    /**
     * 展览
     */
    fun getExhibitionDetail(id: String): String {
        return "${BaseApplication.webSiteUrl}exhibition-detail/${id}"
    }

    /**
     * 展览
     */
    fun getCultureDetail(id: String): String {
        return "${BaseApplication.webSiteUrl}relics-detail/${id}"
    }
    /**
     * 通知详情
     */
    fun getNoticeDesc(id: String): String {
        return "${BaseApplication.webSiteUrl}message-detail/${id}"
    }

    /**
     * 志愿服务
     */
    fun getVoteFengCai(id: String): String {
        return "${BaseApplication.webSiteUrl}service-record-desc/${id}/TEAM_ACTIVITY_SERVICE${id}"
    }
    /**
     * 研学基地
     */
    fun getResearchDetailUrl(id: String): String {
        return "${BaseApplication.webSiteUrl}studies-base-detail/${id}"
    }
    /**
     * 特产
     */
    fun getSpecialDetailUrl(id: String): String {
        return "${BaseApplication.webSiteUrl}speciality-detail/${id}"
    }

    fun getSpecialInfor(id: String): String {
        return "${BaseApplication.webSiteUrl}speciality-introduce/${id}"
    }
}