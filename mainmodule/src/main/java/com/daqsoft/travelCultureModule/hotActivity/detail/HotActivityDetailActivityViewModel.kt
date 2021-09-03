package com.daqsoft.travelCultureModule.hotActivity.detail

import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.bean.CreditBean
import com.daqsoft.provider.bean.HotActivityDetailBean
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.comment.beans.CommentBean
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.rxCommand.ReplyCommand
import com.daqsoft.provider.uiTemplate.titleBar.activity.eventbus.ActivityCollection
import com.daqsoft.travelCultureModule.hotActivity.bean.ActivityRelationBean
import com.daqsoft.travelCultureModule.net.MainRepository
import org.greenrobot.eventbus.EventBus

/**
 * @Description
 * @ClassName   HotActivityDetailActivityViewModel
 * @Author      luoyi
 * @Time        2020/3/20 16:14
 */
class HotActivityDetailActivityViewModel : BaseViewModel() {

    /**
     * 活动详情
     */
    var activityDetailBean = MutableLiveData<HotActivityDetailBean>()
    /**
     * 活动关联场所列表
     */
    var activityRelations = MutableLiveData<MutableList<ActivityRelationBean>>()
    /**
     * 获取活动评论列表
     */
    var commentBeans = MutableLiveData<MutableList<CommentBean>>()
    /**
     * 推荐的活动
     */
    var activities = MutableLiveData<MutableList<ActivityBean>>()
    /**
     * 诚信分
     */
    var creditBean = MutableLiveData<CreditBean>()
    /**
     * 点赞，收藏之后刷新页面
     */
    val notify = MutableLiveData<BaseResponse<Any>>()

    var previousReviewActiviesLd = MutableLiveData<MutableList<ActivityBean>>()


    /**
     * 去到报名或预订页面
     */
    fun gotoOrderApply() {
        if (AppUtils.isLogin()) {
            when (activityDetailBean.value!!.type) {
                // 预订
                ActivityType.ACTIVITY_TYPE_RESERVE -> {
                    ARouter.getInstance()
                        .build(MainARouterPath.MAIN_ACTIVITY_ORDER)
                        .withString("id", activityDetailBean.value!!.id)
                        .withString("type", activityDetailBean.value!!.type)
                        .navigation()
                }
                // 报名
                ActivityType.ACTIVITY_TYPE_ENROLL -> {
                    ARouter.getInstance()
                        .build(MainARouterPath.MAIN_ACTIVITY_SIGN)
                        .withString("id", activityDetailBean.value!!.id)
                        .withString("type", activityDetailBean.value!!.type)
                        .navigation()
                }
            }
        } else {
            ToastUtils.showUnLoginMsg()
            ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                .navigation()
        }
    }

    /**
     * 点赞功能
     */
    val thumbCommand: ReplyCommand = object : ReplyCommand {
        override fun run() {
            if (AppUtils.isLogin()) {
                if (activityDetailBean.value!!.userResourceStatus.thumbStatus) {
                    // 取消点赞
                    thumbCancell(activityDetailBean.value!!.id)
                } else {
                    // 点赞
                    thumbUp(activityDetailBean.value!!.id)
                }
            } else {
                ToastUtils.showUnLoginMsg()
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
    }
    /**
     * 收藏功能
     */
    val collectionCommand: ReplyCommand = object : ReplyCommand {
        override fun run() {
            if (AppUtils.isLogin()) {
                if (activityDetailBean.value!!.userResourceStatus.collectionStatus) {
                    collectionCancel(activityDetailBean.value!!.id)
                } else {
                    collection(activityDetailBean.value!!.id)
                }
            } else {
                ToastUtils.showMessage("该操作需要登录，请先登录")
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
    }
    val attentCommand: ReplyCommand = object : ReplyCommand {
        override fun run() {
            if (AppUtils.isLogin()) {
                if (activityDetailBean.value!!.userResourceStatus.resourceFansStatus) {
                    attentionResourceCancle(activityDetailBean.value!!.id)
                } else {
                    attentionResource(activityDetailBean.value!!.id)
                }
            } else {
                ToastUtils.showMessage("该操作需要登录，请先登录")
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
    }

    /**
     * 点赞接口
     */
    fun thumbUp(resourceId: String) {
        mPresenter.value?.loading = true
        CommentRepository.service.postThumbUp(resourceId, ResourceType.CONTENT_TYPE_ACTIVITY)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {

                    if (response.code == 0) {
                        toast.postValue("点赞成功~")
                        notify.postValue(response)
                    }

                }

                override fun onFailed(response: BaseResponse<Any>) {
//                    toast.postValue("点赞失败~")
                }
            })
    }

    /**
     * 取消点赞接口
     */
    fun thumbCancell(resourceId: String) {
        mPresenter.value?.loading = true
        CommentRepository.service.postThumbCancel(resourceId, ResourceType.CONTENT_TYPE_ACTIVITY)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {

                    if (response.code == 0) {
                        toast.postValue("取消点赞成功~")
                        notify.postValue(response)
                    }

                }
            })
    }

    /**
     * 收藏接口
     */
    fun collection(resourceId: String) {
        mPresenter.value?.loading = true
        CommentRepository.service.posClloection(resourceId, ResourceType.CONTENT_TYPE_ACTIVITY)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {

                    if (response.code == 0) {
                        toast.postValue("收藏成功~")
                        notify.postValue(response)
                        EventBus.getDefault().post(ActivityCollection(true))
                    }

                }
            })
    }

    /**
     * 取消收藏接口
     */
    fun collectionCancel(resourceId: String) {
        mPresenter.value?.loading = true
        CommentRepository.service.posCollectionCancel(resourceId, ResourceType.CONTENT_TYPE_ACTIVITY)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {

                    if (response.code == 0) {
                        toast.postValue("取消收藏成功~")
                        notify.postValue(response)
                        EventBus.getDefault().post(ActivityCollection(false))
                    }

                }
            })
    }

    /**
     * 关注活动
     */
    fun attentionResource(resourceId: String) {
        mPresenter.value?.loading = false
        CommentRepository.service.attentionResource(resourceId, ResourceType.CONTENT_TYPE_ACTIVITY).excute(object : BaseObserver<Any>(mPresenter) {
            override fun onSuccess(response: BaseResponse<Any>) {
                if (response.code == 0) {
                    toast.postValue("关注成功~")
                    notify.postValue(response)
                }
            }

            override fun onFailed(response: BaseResponse<Any>) {
                toast.postValue("关注失败~")
            }
        })
    }

    /**
     * 取消关注活动
     */
    fun attentionResourceCancle(resourceId: String) {
        mPresenter.value?.loading = false
        CommentRepository.service.attentionResourceCancle(resourceId, ResourceType.CONTENT_TYPE_ACTIVITY).excute(object : BaseObserver<Any>(mPresenter) {
            override fun onSuccess(response: BaseResponse<Any>) {
                if (response.code == 0) {
                    toast.postValue("取消关注成功~")
                    notify.postValue(response)
                }
            }

            override fun onFailed(response: BaseResponse<Any>) {
                toast.postValue("取消关注失败~")
            }
        })
    }

    /**
     * 获取活动详情
     */
    fun getActivityDetail(id: String, refresh: Boolean) {
        mPresenter.value?.loading = true

        MainRepository.service.getActivityDetail(id, "")
            .excute(object : BaseObserver<HotActivityDetailBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HotActivityDetailBean>) {
                    activityDetailBean.postValue(response.data)
                    if (!refresh)
//                        getActivityRelationList(id)
//                    getCreditScore()
                        postReadRecord(id)
                }
            })
    }

    /**
     * 新增阅读记录
     */
    fun postReadRecord(id: String) {
        mPresenter.value?.loading = false
        CommentRepository.service.postAddRecord(id, ResourceType.CONTENT_TYPE_ACTIVITY)
            .excute(object : BaseObserver<Any>() {
                override fun onSuccess(response: BaseResponse<Any>) {

                    if (response.code == 0) {
//                        toast.postValue("新增成功")
                    } else {
//                        toast.postValue(response.message)
                    }
                }
            })
    }

    /**
     * 获取诚信分
     */
    fun getCreditScore() {
        mPresenter.value?.loading = false
        mPresenter?.value?.isNeedRecyleView = false
        val phone = SPUtils.getInstance().getString(SPKey.PHONESTR)
        val siteCode = SPUtils.getInstance().getString(SPKey.SITE_CODE)
        UserRepository().userService.getCreditScore(phone, "cultural", siteCode)
            .excute(object : BaseObserver<CreditBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<CreditBean>) {
                    creditBean.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<CreditBean>) {
                    creditBean.postValue(null)
                }
            })
    }


    /**
     * 获取活动关联资源
     */
    fun getActivityRelationList(id: String) {

        mPresenter.value?.loading = false

        MainRepository.service.getActivityRelationList("1", "6", id).excute(object : BaseObserver<ActivityRelationBean>
            () {
            override fun onSuccess(response: BaseResponse<ActivityRelationBean>) {
                activityRelations.postValue(response.datas)

            }
        })
    }

    /**
     * 获取评论列表
     */
    fun getActivityCommentList(id: String, type: String = ResourceType.CONTENT_TYPE_STORY) {

        mPresenter.value?.loading = false
        val param = HashMap<String, String>()
        param["resourceType"] = type
        param["resourceId"] = id
        CommentRepository.service.getCommentList(param).excute(object : BaseObserver<CommentBean>() {
            override fun onSuccess(response: BaseResponse<CommentBean>) {
                commentBeans.postValue(response.datas)


            }
        })
    }

    /**
     * 获取推荐的活动列表
     */
    fun getActivityList(classifyId: String, activityId: String) {
        val param = HashMap<String, String>()

        param["activityId"] = activityId
        // 活动类型id
        if (classifyId != "")
            param["classifyId"] = classifyId
        // 不查询结束得活动
        param["notEndStatus"] = "1"
        mPresenter.value?.loading = false
        MainRepository.service.getActivityList(param).excute(object : BaseObserver<ActivityBean>() {
            override fun onSuccess(response: BaseResponse<ActivityBean>) {

                if (response.datas!!.size > 4) {
                    activities.postValue(response.datas!!.subList(0, 4))
                } else {
                    activities.postValue(response.datas)
                }


            }
        })
    }

    /**
     * 获取往期回顾列表
     * @param id 活动id
     */
    fun getPreviousReviewActivies(id: String) {
        mPresenter.value?.loading = false
        MainRepository.service.getPreviousActivitis(id).excute(object : BaseObserver<ActivityBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<ActivityBean>) {
                previousReviewActiviesLd.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<ActivityBean>) {
                previousReviewActiviesLd.postValue(null)
            }

        })
    }
}