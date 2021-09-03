package com.daqsoft.travelCultureModule.hotActivity.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.MainActivityHotDetailBinding
import com.daqsoft.mainmodule.databinding.MainItemHotActivityRecommendBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ActivityMethod
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.base.ActivityType.Companion.ACTIVITY_TYPE_SERVICE
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.businessview.event.LoginStatusEvent
import com.daqsoft.provider.businessview.event.UpdateCommentEvent
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.commentModule.CommentAdapter
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.view.convenientbanner.holder.BaseBannerImageHolder
import com.daqsoft.provider.view.convenientbanner.holder.BaseBannerImageHolderV2
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import com.daqsoft.provider.view.dialog.SincerityDialog
import com.daqsoft.provider.view.popupwindow.SharePopWindow
import com.daqsoft.travelCultureModule.hotActivity.bean.ActivityRelationBean
import com.daqsoft.travelCultureModule.hotActivity.adapter.ActivityPreviousReviewAdapter
import com.daqsoft.travelCultureModule.hotActivity.detail.adapter.RelationAdapter
import com.daqsoft.travelCultureModule.hotActivity.fragment.HotActivityIntroduceFragment
import kotlinx.android.synthetic.main.main_activity_hot_detail.*
import me.nereo.multi_image_selector.BigImgActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception


/**
 * @Description 文化旅游活动详情页面 普通/报名/预订
 * @ClassName   HotActivityDetailActivity
 * @Author      PuHua
 * @Time        2019/12/23 17:28
 */
@Route(path = MainARouterPath.MAIN_HOT_ACITITY)
class HotActivityDetailActivity :
    TitleBarActivity<MainActivityHotDetailBinding, HotActivityDetailActivityViewModel>() {

    @JvmField
    @Autowired
    var id: String = ""

    @JvmField
    @Autowired
    var classifyId: String = ""

    @JvmField
    @Autowired
    var region: String = ""
    var sharePopWindow: SharePopWindow? = null
    var activityBean: HotActivityDetailBean? = null
    /**
     * 活动场地适配器
     */
    private var relationAdapter: RelationAdapter? = null
    private var liveUrl: String? = ""
    private var type: String? = ""
    override fun getLayout(): Int = R.layout.main_activity_hot_detail

    override fun setTitle(): String = getString(R.string.main_hot_activity_detail)

    override fun injectVm(): Class<HotActivityDetailActivityViewModel> =
        HotActivityDetailActivityViewModel::class.java

    @SuppressLint("NewApi")
    override fun initView() {
        EventBus.getDefault().register(this)
        mBinding.vm = mModel
        isHideAnother = false
        relationAdapter = RelationAdapter()
        initViewModel()
        initViewEvent()
    }


    override fun initData() {
        showLoadingDialog()
        mModel.getActivityDetail(id, false)
        mModel.getActivityRelationList(id)
        mModel.getActivityCommentList(id, ResourceType.CONTENT_TYPE_ACTIVITY)
        if (!classifyId.isNullOrEmpty()) {
            mModel.getActivityList(classifyId, id)
        } else {
            ll_recommend.visibility = View.GONE
            mBinding.tvRecommend.visibility = View.GONE
        }

    }

    override fun initCustomTitleBar(mTitleBar: TitleBar) {
        showShareButton(com.daqsoft.provider.R.mipmap.common_nav_button_share)
        setShareClick(object : View.OnClickListener {
            override fun onClick(v: View?) {
                activityBean?.let {
                    if (sharePopWindow == null) {
                        sharePopWindow = SharePopWindow(this@HotActivityDetailActivity)
                    }
                    var content= Constant.SHARE_DEC
                    sharePopWindow?.setShareContent(
                        it.name, content, it.images.getRealImages(),
                        ShareModel.getActivityDetailUrl(id)
                    )
                    if (!sharePopWindow!!.isShowing) {
                        sharePopWindow?.showAsDropDown(mTitleBar)
                    }

                }
            }
        })
    }


    private fun initViewModel() {
        mModel.activityDetailBean.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.vMainActivityHotDetail.visibility = View.VISIBLE
            bindActivityDetailData(it)
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.vMainActivityHotDetail.visibility = View.VISIBLE
        })
        // 活动场地
        mModel.activityRelations.observe(this, Observer {
            setActivityPlace(it)
        })
        // 评论
        mModel.commentBeans.observe(this, Observer {
            if (it.size > 0) {
                ll_comment?.visibility = View.VISIBLE
                mBinding.llComment.rvComments?.isNestedScrollingEnabled = false
                mBinding.tvComment.visibility = View.VISIBLE
                val comments = if (it.size > 2) {
                    it.subList(0, 2)
                } else {
                    it
                }
                val tagLayoutManager =
                    LinearLayoutManager(
                        this@HotActivityDetailActivity,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                mBinding.llComment.rvComments.layoutManager = tagLayoutManager

                val commentAdapter = CommentAdapter(this)
                mBinding.llComment.rvComments.adapter = commentAdapter
                commentAdapter?.clear()
                commentAdapter.add(comments)
            } else {
                ll_comment?.visibility = View.GONE
                mBinding.vTabComment.visibility = View.GONE
            }

        })
        // 推荐活动
        mModel.activities.observe(this, Observer {
            if (it.size > 0) {
                ll_recommend.visibility = View.VISIBLE
                mBinding.tvRecommend.visibility = View.VISIBLE
                bindRecomendData(it)
            } else {
                ll_recommend.visibility = View.GONE
                mBinding.vTabRecommend.visibility = View.GONE
            }
        })

        // 诚信分
        mModel.creditBean.observe(this, Observer {
            dissMissLoadingDialog()
            if (it != null) {
                // 失信用户
                if (it.creditStatus == "LOSE_CREDIT") {
                    // 失信 但是开启诚信免审，如果分数大于诚信免审分数，可以预定
                    if (mModel.activityDetailBean!!.value?.faithAuditStatus == "1") {
                        var faithAuditValue: String? =
                            mModel.activityDetailBean!!.value?.faithAuditValue
                        //
                        var faithAuditScore: Int = 0
                        var creditScore: Int = 0
                        if (!faithAuditValue.isNullOrEmpty()) {
                            try {
                                if (!it.creditScore.isNullOrEmpty()) {
                                    creditScore = it.creditScore.toInt()
                                }
                                faithAuditScore = faithAuditValue.toInt()

                            } catch (e: Exception) {
                            }
                        }
                        if (faithAuditScore <= 0 || creditScore <= 0) {
                            SincerityDialog.Builder().setContent("" + it.loseDesc)
                                .create(this@HotActivityDetailActivity).show()
                        } else {
                            // 如果分数 大于诚信分数
                            if (creditScore >= faithAuditScore) {
                                mModel.gotoOrderApply()
                            } else {
                                // 提示失信信息
                                SincerityDialog.Builder().setContent("" + it.loseDesc)
                                    .create(this@HotActivityDetailActivity).show()
                            }
                        }
                    } else {
                        // 提示失信信息
                        SincerityDialog.Builder().setContent("" + it.loseDesc)
                            .create(this@HotActivityDetailActivity).show()
                    }
                } else {
                    mModel.gotoOrderApply()
                }
            } else {
                mModel.gotoOrderApply()
            }
        })

        mModel.notify.observe(this, Observer {
            mModel.getActivityDetail(id, true)
        })
        // 往期回顾
        mModel.previousReviewActiviesLd.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.llDetailActivityPreviousReview.vActivityPreviousReview.visibility =
                    View.VISIBLE
                var adpater: ActivityPreviousReviewAdapter =
                    ActivityPreviousReviewAdapter(this@HotActivityDetailActivity)
                adpater.emptyViewShow = false
                mBinding.llDetailActivityPreviousReview.rvActivitiesPreviousReview.layoutManager =
                    GridLayoutManager(
                        this@HotActivityDetailActivity,
                        2, GridLayoutManager.VERTICAL, false
                    )
                mBinding.llDetailActivityPreviousReview.rvActivitiesPreviousReview.adapter = adpater
                adpater.isShowllAll = it.size <= 4
                if (!adpater.isShowllAll) {
                    mBinding.llDetailActivityPreviousReview.tvActPreviousMore.visibility =
                        View.VISIBLE
                } else {
                    mBinding.llDetailActivityPreviousReview.tvActPreviousMore.visibility = View.GONE
                }
                mBinding.llDetailActivityPreviousReview.tvActPreviousMore.onNoDoubleClick {
                    mBinding.llDetailActivityPreviousReview.tvActPreviousMore.visibility = View.GONE
                    adpater.isShowllAll = true
                    adpater.notifyDataSetChanged()
                }
                adpater.clear()
                adpater.add(it)

            } else {
                mBinding.llDetailActivityPreviousReview.vActivityPreviousReview.visibility =
                    View.GONE
            }

        })
    }

    private fun setActivityPlace(it: MutableList<ActivityRelationBean>) {
        if (it.size > 0) {
            ll_place?.visibility = View.VISIBLE
            val tagLayoutManager =
                LinearLayoutManager(
                    this@HotActivityDetailActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            mBinding.llPlace.rvActivities.layoutManager = tagLayoutManager
            mBinding.llPlace.rvActivities.adapter = relationAdapter
            relationAdapter?.clear()
            relationAdapter?.add(it)
            if (it.size > 3) {
                relationAdapter?.isShowMore = true
                mBinding.llPlace.tvActivitiesMore.visibility = View.VISIBLE
            } else {
                mBinding.llPlace.tvActivitiesMore.visibility = View.GONE
                relationAdapter?.isShowMore = false
            }
            mBinding.llPlace.tvActivitiesMore.onNoDoubleClick {
                try {
                    if (relationAdapter!!.isShowMore) {
                        relationAdapter?.isShowMore = false
                        val drawable2 =
                            this@HotActivityDetailActivity.getDrawable(com.daqsoft.provider.R.mipmap.provider_arrow_up)
                        drawable2.setBounds(0, 0, drawable2.minimumWidth, drawable2.minimumHeight)
                        mBinding.llPlace.tvActivitiesMore?.setCompoundDrawables(
                            null,
                            null,
                            drawable2,
                            null
                        )
                        mBinding.llPlace.tvActivitiesMore.text = "收起全部活动场地"
                    } else {
                        relationAdapter?.isShowMore = true
                        val drawable2 =
                            this@HotActivityDetailActivity.getDrawable(com.daqsoft.provider.R.mipmap.provider_arrow_down)
                        drawable2.setBounds(0, 0, drawable2.minimumWidth, drawable2.minimumHeight)
                        mBinding.llPlace.tvActivitiesMore?.setCompoundDrawables(
                            null,
                            null,
                            drawable2,
                            null
                        )
                        mBinding.llPlace.tvActivitiesMore.text = "查看全部活动场地"
                    }
                    relationAdapter?.notifyDataSetChanged()
                } catch (e: Exception) {

                }
            }

        } else {
            ll_place?.visibility = View.GONE

        }
    }


    /**
     * 绑定推荐数据
     *  @param it 推荐列表
     */
    private fun bindRecomendData(it: MutableList<ActivityBean>) {
        val tagLayoutManager = GridLayoutManager(this@HotActivityDetailActivity, 2)
        val space = resources.getDimensionPixelSize(R.dimen.dp_16)
        mBinding.llRecommend.rvRecommend.layoutManager = tagLayoutManager

        val recommendAdapter = object :
            RecyclerViewAdapter<MainItemHotActivityRecommendBinding, ActivityBean>(
                R.layout.main_item_hot_activity_recommend
            ) {
            override fun setVariable(
                mBinding: MainItemHotActivityRecommendBinding,
                position: Int,
                item: ActivityBean
            ) {
                mBinding.name = item.name

                Glide.with(this@HotActivityDetailActivity)
                    .load(item.images.getRealImages())
                    .placeholder(R.mipmap.placeholder_img_fail_240_180)
                    .into(mBinding.ivImage)

                mBinding.type = item.classifyName
                if (item.faithUseStatus == "1") {
                    // 诚信优享
                    mBinding.imgHonety.setBackgroundResource(R.mipmap.activity_details_recommend_activity_tag_youxiang)
                    mBinding.imgHonety.visibility = View.VISIBLE
                } else if (item.faithAuditStatus == "1") {
                    // 诚信免审
                    mBinding.imgHonety.setBackgroundResource(R.mipmap.activity_details_recommend_activity_tag_mianshen)
                    mBinding.imgHonety.visibility = View.VISIBLE
                } else {
                    mBinding.imgHonety.visibility = View.GONE
                }
                if (item.type == ActivityType.ACTIVITY_TYPE_RESERVE || item.type == ActivityType.ACTIVITY_TYPE_ENROLL) {
                    // 显示价格/积分/免费
                    if (item.integral == "0") {
                        if (item.type == ActivityType.ACTIVITY_TYPE_RESERVE) {
                            mBinding.price = getString(R.string.order_free_reserve)
                        } else {
                            mBinding.price = getString(R.string.order_free)
                        }

                        mBinding.tvIntegral.visibility = View.GONE
                    } else {
                        mBinding.price = item.integral
                        mBinding.tvIntegral.visibility = View.VISIBLE
                    }
                } else {
                    mBinding.tvIntegral.visibility = View.GONE
                }
                mBinding.root.onNoDoubleClick {
                    if (!item.jumpUrl.isNullOrEmpty()) {
                        ARouter.getInstance()
                            .build(ARouterPath.Provider.WEB_ACTIVITY)
                            .withString("mTitle", item.jumpName)
                            .withString("html", item.jumpUrl)
                            .navigation()
                    } else {
                        when (item.type) {
                            // 志愿活动
                            ActivityType.ACTIVITY_TYPE_VOLUNT -> {
                                ARouter.getInstance()
                                    .build(MainARouterPath.MAIN_VOLUNTEER_ACTIVITY)
//                                    .build( MainARouterPath.MAIN_SEAT_SELECT_ACTIVITY)
                                    .withString("id", item.id)
                                    .withString("classifyId", item.classifyId)
                                    .navigation()
                            }
                            // 预订活动
                            ActivityType.ACTIVITY_TYPE_RESERVE -> {
                                // 预订
                                when (item.method) {
                                    // 付费预订活动
                                    ActivityMethod.ACTIVITY_MODE_INTEGRAL_PAY -> {

                                        ARouter.getInstance()
                                            .build(MainARouterPath.MAIN_ACTIVITY_PAY_ORDER)
                                            .withString("jumpUrl", item.jumpUrl)
                                            .navigation()
                                    }
                                    else -> {
                                        ARouter.getInstance()
                                            .build(MainARouterPath.MAIN_HOT_ACITITY)
                                            .withString("id", item.id)
                                            .withString("classifyId", item.classifyId)
                                            .withString("region", item.region)
                                            .navigation()
                                    }
                                }
                            }
                            else -> {
                                ARouter.getInstance()
                                    .build(MainARouterPath.MAIN_HOT_ACITITY)
                                    .withString("id", item.id)
                                    .withString("classifyId", item.classifyId)
                                    .navigation()
                            }

                        }
                    }
                }
            }

        }
        mBinding.llRecommend.rvRecommend.adapter = recommendAdapter
        recommendAdapter?.clear()
        recommendAdapter.add(it)
        if (it != null && it.size > 0) {
            ll_recommend.visibility = View.VISIBLE
            mBinding.tvRecommend.visibility = View.VISIBLE
        } else {
            ll_recommend.visibility = View.GONE
            mBinding.tvRecommend.visibility = View.GONE
        }

    }


    /**
     * 绑定活动详情数据
     * @param it HotActivityDetailBean 活动详情实体
     *
     */
    @SuppressLint("SetJavaScriptEnabled", "StringFormatInvalid")
    private fun bindActivityDetailData(it: HotActivityDetailBean) {
        title = it.name

        mBinding.llComment.tvCommentNum.text =
            getString(R.string.home_activity_comment_number, it.resourceCount.commentNum)

//        mBinding.url = it.images
        // 图片
        setActivityImages(it)
        activityBean = it
        it.region = region
        var hotActivityIntroduceFragment: HotActivityIntroduceFragment =
            HotActivityIntroduceFragment.newInstance(it)
        transactFragment(R.id.fl_introduce_detail, hotActivityIntroduceFragment)
        if (!it.coOrganizer.isNullOrEmpty()) {
            mBinding.llDetailNotice.co = it.coOrganizer
            mBinding.llDetailNotice.tvCo.visibility = View.VISIBLE
            mBinding.llDetailNotice.tvAddressLabel.visibility = View.VISIBLE
            mBinding.llDetailNotice.tvLocationLabel.visibility = View.VISIBLE
            mBinding.llDetailNotice.vLocationLine.visibility = View.VISIBLE
        } else {
            mBinding.llDetailNotice.tvCo.visibility = View.GONE
            mBinding.llDetailNotice.tvAddressLabel.visibility = View.GONE
        }
        if (!it.sponsor.isNullOrEmpty()) {
            mBinding.llDetailNotice.sponsor = it.sponsor
            mBinding.llDetailNotice.tvTimeLabel.visibility = View.VISIBLE
            mBinding.llDetailNotice.tvSponsor.visibility = View.VISIBLE
            mBinding.llDetailNotice.tvLocationLabel.visibility = View.VISIBLE
            mBinding.llDetailNotice.vLocationLine.visibility = View.VISIBLE
        } else {
            mBinding.llDetailNotice.tvTimeLabel.visibility = View.GONE
            mBinding.llDetailNotice.tvSponsor.visibility = View.GONE
        }
        if (!it.teachUnit.isNullOrEmpty()) {
            mBinding.llDetailNotice.tvLocationLabel.visibility = View.VISIBLE
            mBinding.llDetailNotice.guide = it.teachUnit
            mBinding.llDetailNotice.tvGuide.visibility = View.VISIBLE
            mBinding.llDetailNotice.tvGuideLabel.visibility = View.VISIBLE
            mBinding.llDetailNotice.vLocationLine.visibility = View.VISIBLE
        } else {
            mBinding.llDetailNotice.tvGuide.visibility = View.GONE
            mBinding.llDetailNotice.tvGuideLabel.visibility = View.GONE
        }

        if (!it.undertakeUnit.isNullOrEmpty()) {
            mBinding.llDetailNotice.tvLocationLabel.visibility = View.VISIBLE
            mBinding.llDetailNotice.vLocationLine.visibility = View.VISIBLE
            mBinding.llDetailNotice.undertake = it.undertakeUnit
            mBinding.llDetailNotice.tvPhoneLabel.visibility = View.VISIBLE
            mBinding.llDetailNotice.tvUndertake.visibility = View.VISIBLE
        } else {
            mBinding.llDetailNotice.tvPhoneLabel.visibility = View.GONE
            mBinding.llDetailNotice.tvUndertake.visibility = View.GONE
        }
        if (!it.introduce.isNullOrEmpty()) {
            mBinding.llDetailNotice.tvLocationLabel.visibility = View.VISIBLE
            mBinding.llDetailNotice.vLocationLine.visibility = View.VISIBLE
            mBinding.llDetailNotice.wbActivityDetail.settings.defaultTextEncodingName = "utf-8"
            mBinding.llDetailNotice.wbActivityDetail.settings.setJavaScriptEnabled(true)
            mBinding.llDetailNotice.wbActivityDetail.loadDataWithBaseURL(
                null,
                StringUtil.getHtml(it.introduce),
                "text/html",
                "utf-8",
                null
            )
            mBinding.llDetailNotice.wbActivityDetail.visibility = View.VISIBLE
        } else {
            mBinding.llDetailNotice.wbActivityDetail.visibility = View.GONE
        }
        if (!it.liveUrl.isNullOrEmpty()) {
            liveUrl = it.liveUrl
            mBinding.llDetailLive.vOnlineLive.visibility = View.VISIBLE
        } else {
            mBinding.llDetailLive.vOnlineLive.visibility = View.GONE
        }
        var drawable = if (it.userResourceStatus.thumbStatus) {
            resources.getDrawable(R.mipmap.main_bottom_icon_like_selected)
        } else {
            resources.getDrawable(R.mipmap.main_bottom_icon_like_normal)
        }

        var collect = if (it.userResourceStatus.collectionStatus) {
            resources.getDrawable(R.mipmap.main_bottom_icon_collect_selected)
        } else {
            resources.getDrawable(R.mipmap.main_bottom_icon_collect_normal)
        }

        var attentDb = if (it.userResourceStatus.resourceFansStatus) {
            resources.getDrawable(R.mipmap.bottom_icon_attent_selected)
        } else {
            resources.getDrawable(R.mipmap.bottom_icon_attent)
        }
        // 点赞数
        mBinding.tvThumb.text = it.resourceCount.thumbNum

        mBinding.tvCollect.text = it.resourceCount.collectionNum

        mBinding.tvAttent.text = it.resourceCount.fansCount

        if (it.resourceCount.commentNum.toInt() > 0)
            mBinding.tvCommentNum.text = it.resourceCount.commentNum


        mBinding.tvThumb.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)

        mBinding.tvCollect.setCompoundDrawablesWithIntrinsicBounds(null, collect, null, null)

        mBinding.tvAttent.setCompoundDrawablesWithIntrinsicBounds(null, attentDb, null, null)

        // 用户未注册，点击预订跳转登录注册页面；成功后跳转到预订页面；
        mBinding.tvOrder.text = getString(
            when (it.buttonStatus) {
                // 可以预订
                "0" -> {
                    mBinding.tvOrder.background = resources.getDrawable(R.color.colorPrimary)
                    mBinding.tvOrder.isEnabled = true
                    when (it.type) {
                        ActivityType.ACTIVITY_TYPE_RESERVE ->
                            if (!it.method.isNullOrEmpty()) {
                                if (it.method == ActivityMethod.ACTIVITY_MODE_INTEGRAL) {
                                    R.string.main_activity_order_intergral
                                } else {
                                    R.string.main_activity_order_immediately
                                }
                            } else {
                                R.string.main_activity_order_immediately
                            }
                        ActivityType.ACTIVITY_TYPE_ENROLL -> {
                            if (it.lineFlag == "0") {
                                mBinding.tvOrder.background =
                                    resources.getDrawable(R.color.colorPrimary_un)
                                mBinding.tvOrder.isEnabled = false
                                R.string.main_activity_order_under_line
                            } else {
                                R.string.main_activity_sign_immediately
                            }
                        }
                        //                           ActivityType.ACTIVITY_TYPE_VOLUNT->R.string.main_activity_join_immediately
                        else -> R.string.main_activity_order_immediately
                    }
                }
                // 限购购买完
                // 当活动设置了限购数量为2，用户第一次预订了1，进入页面按钮文字任然提示“免费预订”，
                // 当用户第一次预订了2，再次进入页面，按钮显示“已预订”，且不可点击；
                "1" -> {
                    when (it.type) {
                        ActivityType.ACTIVITY_TYPE_RESERVE -> R.string.main_activity_order_done
                        ActivityType.ACTIVITY_TYPE_ENROLL -> R.string.main_activity_sign_done
                        else -> R.string.main_activity_order_done
                    }
                }
                // 申请中
                "2" -> {
                    mBinding.tvOrder.background = resources.getDrawable(R.color.c2f0d0)
                    R.string.main_activity_order_apply
                }
                // 结束
                // 如果活动时间已结束，只变化按钮文字，提示“活动已结束，不可点击；
                "3" -> {
                    mBinding.tvIsOver.visibility = View.VISIBLE
                    mBinding.reviewsLayout.visibility = View.GONE
                    when (it.type) {
                        ActivityType.ACTIVITY_TYPE_RESERVE -> R.string.main_activity_order_over
                        ActivityType.ACTIVITY_TYPE_ENROLL -> R.string.main_activity_sign_over
                        ActivityType.ACTIVITY_TYPE_SERVICE -> R.string.main_activity_over
                        ActivityType.ACTIVITY_TYPE_PLAIN -> R.string.main_activity_over
                        else -> R.string.main_activity_order_done
                    }

                }
                // 未开始
                "4" -> R.string.main_activity_order_unstart
                // 没有库存
                "5" -> R.string.main_activity_order_empty
                "6" -> R.string.main_activity_order_notenough

                else -> R.string.main_activity_order_immediately
            }
        )
        type = it.type
        if(it.buttonStatus=="3"){
            mBinding.tvOrder.visibility = View.VISIBLE
        }else {
            if (it.type == ActivityType.ACTIVITY_TYPE_SERVICE || it.type == ActivityType.ACTIVITY_TYPE_PLAIN)
                mBinding.tvOrder.visibility = View.GONE
        }
        if (!it.hint.isNullOrEmpty()) {
            mBinding.llDetailNotice.tvNotice.settings.defaultTextEncodingName = "utf-8"
            mBinding.llDetailNotice.tvNotice.settings.setJavaScriptEnabled(true)
            mBinding.llDetailNotice.tvNotice.loadDataWithBaseURL(
                null,
                StringUtil.getHtml(it.hint),
                "text/html",
                "utf-8",
                null
            )
            mBinding.llDetailNotice.vActivityNotice.visibility = View.VISIBLE
        } else {
            mBinding.llDetailNotice.vActivityNotice.visibility = View.GONE
        }
        if (classifyId.isNullOrEmpty() && !it.classifyId.isNullOrEmpty()) {
            classifyId = it.classifyId
            mModel.getActivityList(classifyId, id)
        }
        // 往期回顾
//        if (!it.alreadyActivity.isNullOrEmpty()) {
        mModel.getPreviousReviewActivies(it.id)
//        }
        // 活动结果
        if (it.activityResult != null) {
            setActivityResult(it.activityResult)
        }
    }

    /**
     * 活动结果
     */
    private fun setActivityResult(activityResult: ActivityResult) {
        var datas: MutableList<ImageVideoBean> = mutableListOf()
        // 处理视频地址
        if (!activityResult.videos.isNullOrEmpty()) {
            var videos = activityResult.videos.split(",")
            if (!videos.isNullOrEmpty()) {
                for (item in videos) {
                    if (!item.isNullOrEmpty())
                        datas.add(ImageVideoBean(1, "", item))
                }
            }
        }
        // 处理图片地址
        if (!activityResult.images.isNullOrEmpty()) {
            var images = activityResult.images.split(",")
            if (!images.isNullOrEmpty()) {
                for (item in images) {
                    if (!item.isNullOrEmpty()) {
                        datas.add(ImageVideoBean(0, item, ""))
                    }
                }
            }
        }
        if (!datas.isNullOrEmpty()) {
            mBinding.llDetailActResult.visibility = View.VISIBLE
            mBinding.llDetailActResult.setData(datas)
        } else {
            mBinding.llDetailActResult.visibility = View.GONE
        }
    }

    /**
     * @param it 活动实体
     */
    private fun setActivityImages(it: HotActivityDetailBean) {
        if (!it.images.isNullOrEmpty()) {
            val images = it.images.split(",")
            if (!images.isNullOrEmpty()) {
                mBinding?.txtCurrentIndex.text = "1"
                mBinding?.txtTotalSize.text = "/${images.size}"
                mBinding?.cbannerHotActivityDetail.setPages(object : CBViewHolderCreator {
                    override fun createHolder(itemView: View?): Holder<*> {
                        return BaseBannerImageHolderV2(itemView!!)
                    }

                    override fun getLayoutId(): Int {
                        return R.layout.holder_img_base
                    }
                }, images).setCanLoop(true).setPointViewVisible(false).setOnItemClickListener {
                    val intent =
                        Intent(this@HotActivityDetailActivity, BigImgActivity::class.java)
                    intent.putExtra("position", it)
                    intent.putStringArrayListExtra(
                        "imgList",
                        ArrayList(images)
                    )
                    startActivity(intent)
                }.startTurning(3000)
                if (images.size > 1) {
                    mBinding?.cbannerHotActivityDetail.setCanLoop(true)
                } else {
                    mBinding?.cbannerHotActivityDetail.setCanLoop(false)
                }
                mBinding?.cbannerHotActivityDetail?.setOnPageChangeListener(object :
                    OnPageChangeListener {
                    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    }

                    override fun onPageSelected(index: Int) {
                        mBinding?.txtCurrentIndex.text = ((index + 1).toString())
                        mBinding?.txtTotalSize.text = "/${images.size}"
                    }

                    override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                    }

                })
            }

        }
    }


    fun gotoCommentPage(v: View) {
        ARouter.getInstance()
            .build(MainARouterPath.MAIN_ACTIVITY_COMMENT)
            .withString("id", id)
            .withString("type", ResourceType.CONTENT_TYPE_ACTIVITY)
            .navigation()
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun initViewEvent() {
        mBinding?.scrollView.setOnScrollChangeListener(object : View.OnScrollChangeListener {
            override fun onScrollChange(v: View?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
                if (scrollY >= 0) {
                    if (scrollY > fl_introduce_detail.top && scrollY < fl_introduce_detail.bottom
                        && !mBinding.tvIntroduce.isSelected
                    ) { // 简介
                        resetTabSelectedStatus()
                        mBinding?.vStickTop.visibility = View.VISIBLE
                        mBinding?.tvIntroduce.isSelected = true
                        changeIndicator(0)
                    } else if (scrollY > ll_comment.top && scrollY < ll_comment.bottom && ll_comment.visibility == View.VISIBLE
                        && !mBinding.tvComment.isSelected
                    ) { // 点评
                        resetTabSelectedStatus()
                        mBinding?.tvComment.isSelected = true
                        changeIndicator(1)
                    } else if (scrollY > ll_detail_notice.top && scrollY < ll_detail_notice.bottom && ll_detail_notice.visibility == View.VISIBLE
                        && !mBinding.tvDetail.isSelected
                    ) { // 详情

                        resetTabSelectedStatus()
                        mBinding?.tvDetail.isSelected = true
                        changeIndicator(2)
                    } else if (scrollY > ll_recommend.top && scrollY < ll_recommend.bottom && ll_recommend.visibility == View.VISIBLE
                        && !mBinding.tvRecommend.isSelected
                    ) { // 推荐
                        resetTabSelectedStatus()
                        mBinding?.tvRecommend.isSelected = true
                        changeIndicator(3)
                    } else if (scrollY <= fl_introduce_detail.top) {
                        // 重置
                        resetTabSelectedStatus()
                        mBinding?.vStickTop.visibility = View.GONE
                    }

                }
            }
        })

        mBinding?.vTabDetail.onNoDoubleClick {
            mBinding?.scrollView.smoothScrollTo(0, ll_detail_notice!!.top)
        }
        mBinding?.vTabComment.onNoDoubleClick {
            mBinding?.scrollView.smoothScrollTo(0, ll_comment.top)
        }
        mBinding?.vTabIntroduce.onNoDoubleClick {
            mBinding?.scrollView.smoothScrollTo(0, fl_introduce_detail.top)
        }
        mBinding?.vTabRecommend.onNoDoubleClick {
            mBinding?.scrollView.smoothScrollTo(0, ll_recommend.top)
        }
        mBinding.tvCommentNum.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_COMMENT_ADD)
                .withString("id", id)
                .withString("type", ResourceType.CONTENT_TYPE_ACTIVITY)
                .navigation()
        }
        mBinding?.llDetailLive?.vOnlineLive?.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
                .withString("html", liveUrl)
                .navigation()
        }
        mBinding.tvOrder.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                if (type == ActivityType.ACTIVITY_TYPE_SERVICE ||type == ActivityType.ACTIVITY_TYPE_PLAIN){
                    return@onNoDoubleClick
                }
                showLoadingDialog()
                mModel.getCreditScore()
//                if (activityDetailBean.value!!.faithAuditStatus == "1") {
//                    // 需要验证诚信分
//                    getCreditScore()
//                } else {
//                    // 不用验证
//                    gotoOrderApply()
//                }
            } else {
                ToastUtils.showUnLoginMsg()
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
//        mBinding.tvShare.onNoDoubleClick {
//            ToastUtils.showMessage("敬请期待")
//        }
    }


    /**
     * 重置滑动tab
     */
    private fun resetTabSelectedStatus() {
        mBinding?.tvIntroduce.isSelected = false
        mBinding?.tvComment.isSelected = false
        mBinding?.tvDetail.isSelected = false
        mBinding?.tvRecommend.isSelected = false
        mBinding?.vIndicatorIntroduce.visibility = View.GONE
        mBinding?.vIndicatorComment.visibility = View.GONE
        mBinding?.vIndicatorDetail.visibility = View.GONE
        mBinding?.vIndicatorRecommend.visibility = View.GONE
    }

    /**
     * 改变滑动标识
     */
    private fun changeIndicator(pos: Int) {
        when (pos) {
            0 -> {
                mBinding?.vIndicatorIntroduce.visibility = View.VISIBLE
            }
            1 -> {
                mBinding?.vIndicatorComment.visibility = View.VISIBLE
            }
            2 -> {
                mBinding?.vIndicatorDetail.visibility = View.VISIBLE
            }
            3 -> {
                mBinding?.vIndicatorRecommend.visibility = View.VISIBLE
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun updateDataStatus(event: LoginStatusEvent) {
//        if (event != null) {
//            mModel?.getActivityDetail(id, false)
//        }
        initData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRecordEvent(event: UpdateCommentEvent) {
        // 评论
        initData()
    }
}