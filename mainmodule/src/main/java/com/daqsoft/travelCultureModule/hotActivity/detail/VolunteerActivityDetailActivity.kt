package com.daqsoft.travelCultureModule.hotActivity.detail

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.AppManager
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.SystemHelper
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.view.convenientbanner.holder.BaseBannerImageHolder
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.MainActivityVolunteerDetailBinding
import com.daqsoft.mainmodule.databinding.MainItemHotActivityRecommendBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ActivityMethod
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.businessview.event.UpdateCommentEvent
import com.daqsoft.provider.businessview.event.UpdateOrderCommentStatus
import com.daqsoft.provider.commentModule.CommentAdapter
import com.daqsoft.provider.databinding.LayoutDialogNoticeBinding
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.rxCommand.ReplyCommand
import com.daqsoft.provider.view.SpaceItemDecoration
import com.daqsoft.travelCultureModule.hotActivity.detail.adapter.RelationAdapter
import com.daqsoft.travelCultureModule.hotActivity.detail.adapter.VolunteerRelationAdapter
import com.daqsoft.travelCultureModule.hotActivity.detail.viewmodel.VolunteerActivityDetailActivityViewModel
import com.daqsoft.travelCultureModule.hotActivity.fragment.VolunteerIntroduceFragment
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.main_activity_volunteer_detail.*
import me.nereo.multi_image_selector.BigImgActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


/**
 * @Description ???????????????????????????
 * @ClassName   HotActivityDetailActivity
 * @Author      PuHua
 * @Time        2019/12/23 17:28
 */
@Route(path = MainARouterPath.MAIN_VOLUNTEER_ACTIVITY)
class VolunteerActivityDetailActivity :
    TitleBarActivity<MainActivityVolunteerDetailBinding, VolunteerActivityDetailActivityViewModel>() {

    @JvmField
    @Autowired
    var id: String = ""
    @JvmField
    @Autowired
    var classifyId: String = ""
    @JvmField
    @Autowired
    var region: String = ""
    /**
     * ???????????????
     */
    private var vlunteerAdapter: VolunteerRelationAdapter? = null
    /**
     * ?????????????????????
     */
    private var relationAdapter: RelationAdapter? = null

    override fun getLayout(): Int = R.layout.main_activity_volunteer_detail

    override fun setTitle(): String = getString(R.string.main_hot_activity_detail)

    override fun injectVm(): Class<VolunteerActivityDetailActivityViewModel> =
        VolunteerActivityDetailActivityViewModel::class.java

    override fun initView() {
        EventBus.getDefault().register(this)
        mBinding.vm = mModel
        isHideAnother = false
        vlunteerAdapter = VolunteerRelationAdapter(this@VolunteerActivityDetailActivity)
        relationAdapter = RelationAdapter()
        initViewModel()
    }

    /**
     * ?????????model
     */
    private fun initViewModel() {
        // ???????????????
        mModel.activityVolunteerTeam.observe(this, Observer {
            if (it.size > 0) {
                val tagLayoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                mBinding.llVolunteerTeam.rvActivities.layoutManager = tagLayoutManager
                mBinding.llVolunteerTeam.rvActivities.adapter = vlunteerAdapter
                vlunteerAdapter!!.add(it)
                if (it.size > 4) {
                    mBinding.llVolunteerTeam.tvActivitiesMore.visibility = View.VISIBLE
                } else {
                    mBinding.llVolunteerTeam.tvActivitiesMore.visibility = View.GONE
                }
                ll_volunteer_team?.visibility = View.VISIBLE
            } else {
                ll_volunteer_team?.visibility = View.GONE
            }
        })

        // ????????????
        mModel.activities.observe(this, Observer {
            if (it.size > 0) {
                val tagLayoutManager = GridLayoutManager(
                    this@VolunteerActivityDetailActivity, 2,
                    GridLayoutManager.VERTICAL, false
                )
                val space = resources.getDimensionPixelSize(R.dimen.dp_16)
//                mBinding.llRecommend.rvRecommend.addItemDecoration(SpaceItemDecoration(space))
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

                        mBinding.name = item.name

                        Glide.with(this@VolunteerActivityDetailActivity)
                            .load(item.images.getRealImages())
                            .placeholder(R.mipmap.placeholder_img_fail_240_180)
                            .into(mBinding.ivImage)

                        mBinding.type = item.classifyName
                        if (item.faithUseStatus == "1") {
                            // ????????????
                            mBinding.imgHonety.setBackgroundResource(R.mipmap.activity_details_recommend_activity_tag_youxiang)
                            mBinding.imgHonety.visibility = View.VISIBLE
                        } else if (item.faithAuditStatus == "1") {
                            // ????????????
                            mBinding.imgHonety.setBackgroundResource(R.mipmap.activity_details_recommend_activity_tag_mianshen)
                            mBinding.imgHonety.visibility = View.VISIBLE
                        } else {
                            mBinding.imgHonety.visibility = View.GONE
                        }
                        if (item.type == ActivityType.ACTIVITY_TYPE_RESERVE || item.type == ActivityType.ACTIVITY_TYPE_ENROLL) {
                            // ????????????/??????/??????
                            if (item.integral == "0") {
                                mBinding.price = getString(R.string.order_free)
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
                                    // ????????????
                                    ActivityType.ACTIVITY_TYPE_VOLUNT -> {
                                        ARouter.getInstance()
                                            .build(MainARouterPath.MAIN_VOLUNTEER_ACTIVITY)
//                                    .build( MainARouterPath.MAIN_SEAT_SELECT_ACTIVITY)
                                            .withString("id", item.id)
                                            .withString("classifyId", item.classifyId)
                                            .navigation()
                                    }
                                    // ????????????
                                    ActivityType.ACTIVITY_TYPE_RESERVE -> {
                                        // ??????
                                        when (item.method) {
                                            // ??????????????????
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
                recommendAdapter.add(it)
                ll_recommend?.visibility = View.VISIBLE
            } else {
                ll_recommend?.visibility = View.GONE
            }
        })
        // ?????????
        mModel.creditBean.observe(this, Observer {
            if (mModel.creditBean.value!!.creditScore.toInt() <= 350) {
                // ?????????????????????????????????350?????????????????????7????????????????????????????????????????????????????????????????????????????????????350???????????????2?????????????????????
                noticeConfirm(this@VolunteerActivityDetailActivity)
            } else {
                mModel.gotoSign()
            }
        })

        mModel.notify.observe(this, Observer {
            mModel.getActivityDetail(id, true)
        })

        mModel.activityDetailBean.observe(this, Observer {
            // ????????????
            when (it.activityStatus) {
                "0" -> {
                    // ?????????
                    mBinding.tvStatus.text = "?????????"
                    mBinding.tvStatus.background = getDrawable(R.mipmap.volunteer_activity_details_wks)
                }
                "1" -> {
                    // ?????????
                    mBinding.tvStatus.text = "?????????"
                    mBinding.tvStatus.background = getDrawable(R.mipmap.volunteer_activity_details_jxz)
                }
                "2" -> {
                    // ?????????
                    mBinding.tvStatus.text = "?????????"
                    mBinding.tvStatus.background = getDrawable(R.mipmap.volunteer_activity_details_end)
                }
                "3" -> {
                    // (?????????/?????????)
                    mBinding.tvStatus.text = "?????????"
                    mBinding.tvStatus.background = getDrawable(R.mipmap.volunteer_activity_details_jxz)
                }
            }
            mBinding.tvStatus.visibility = View.VISIBLE
            mBinding.url = it.images
            var hideTag: Int = 0
            // ????????????
            if (!it.sponsor.isNullOrEmpty()) {
                hideTag = 1
                mBinding.llVolunteerMoreInfo.sponsor = it.sponsor
                mBinding.llVolunteerMoreInfo.tvSponsor.visibility = View.VISIBLE
                mBinding.llVolunteerMoreInfo.tvStartLabel.visibility = View.VISIBLE
            } else {
                mBinding.llVolunteerMoreInfo.tvSponsor.visibility = View.GONE
                mBinding.llVolunteerMoreInfo.tvStartLabel.visibility = View.GONE
            }

            // ?????????
            if (!it.liableName.isNullOrEmpty()) {
                mBinding.llVolunteerMoreInfo.response = it.liableName
                mBinding.llVolunteerMoreInfo.tvResponseLabel.visibility = View.VISIBLE
                mBinding.llVolunteerMoreInfo.tvResponse.visibility = View.VISIBLE
                hideTag = 1
            } else {
                mBinding.llVolunteerMoreInfo.tvResponseLabel.visibility = View.GONE
                mBinding.llVolunteerMoreInfo.tvResponse.visibility = View.GONE
            }
            // ??????????????????
            if (!it.phone.isNullOrEmpty()) {
                val phoneNum = it.phone
                mBinding.llVolunteerMoreInfo.phoneNum = phoneNum
                mBinding.llVolunteerMoreInfo.txtContactPhoneNum.visibility = View.VISIBLE
                mBinding.llVolunteerMoreInfo.tvContactUsLabel.visibility = View.VISIBLE
                RxView.clicks(mBinding.llVolunteerMoreInfo.txtContactPhoneNum)
                    .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                        if (!phoneNum.isNullOrEmpty()) {
                            SystemHelper.callPhone(this,phoneNum)
                        }
                    }
                hideTag = 1
            } else {
                mBinding.llVolunteerMoreInfo.txtContactPhoneNum.visibility = View.GONE
                mBinding.llVolunteerMoreInfo.tvContactUsLabel.visibility = View.GONE
            }
            if (!it.introduce.isNullOrEmpty()) {
                mBinding.llVolunteerMoreInfo.tvNotice.settings.defaultTextEncodingName = "utf-8"
                mBinding.llVolunteerMoreInfo.tvNotice.settings.setJavaScriptEnabled(true)
                mBinding.llVolunteerMoreInfo.tvNotice.loadDataWithBaseURL(null,StringUtil.getHtml(it.introduce), "text/html","utf-8",null)
                mBinding.llVolunteerMoreInfo.tvNotice.visibility = View.VISIBLE
                hideTag = 1
            } else {
                mBinding.llVolunteerMoreInfo.tvNotice.visibility = View.GONE
            }
            if (hideTag == 0) {
                ll_volunteer_more_info.visibility = View.GONE
            }
            if (!it.images.isNullOrEmpty()) {
                val images = it.images.split(",")
                if (!images.isNullOrEmpty()) {
                    mBinding?.txtCurrentIndex.text = "1"
                    mBinding?.txtTotalSize.text = "/${images.size}"
                    mBinding?.cbannerVolunteerDetail.setPages(object : CBViewHolderCreator {
                        override fun createHolder(itemView: View?): Holder<*> {
                            return BaseBannerImageHolder(itemView!!)
                        }

                        override fun getLayoutId(): Int {
                            return R.layout.holder_img_base
                        }
                    }, images).setCanLoop(false).setPointViewVisible(false).setOnItemClickListener {
                        val intent =
                            Intent(this@VolunteerActivityDetailActivity, BigImgActivity::class.java)
                        intent.putExtra("position", it)
                        intent.putStringArrayListExtra(
                            "imgList",
                            ArrayList(images)
                        )
                        startActivity(intent)
                    }
                    if (images.size > 1) {
                        mBinding?.cbannerVolunteerDetail.setCanLoop(true)
                    } else {
                        mBinding?.cbannerVolunteerDetail.setCanLoop(false)
                    }
                    mBinding?.cbannerVolunteerDetail?.setOnPageChangeListener(object : OnPageChangeListener {
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
            title = it.name
            // ????????????
            var hotActivityIntroduceFragment = VolunteerIntroduceFragment(it)
            transactFragment(R.id.fl_introduce_detail, hotActivityIntroduceFragment)

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

            // ?????????
            mBinding.tvThumb.text = it.resourceCount.thumbNum.toString()

            mBinding.tvCollect.text = it.resourceCount.collectionNum.toString()

            mBinding.tvCommentNum.text = it.resourceCount.commentNum.toString()

            mBinding.tvCommentNum.onNoDoubleClick {
                if (AppUtils.isLogin()) {

                    ARouter.getInstance()
                        .build(MainARouterPath.MAIN_COMMENT_ADD)
                        .withString("id", id)
                        .withString("type", ResourceType.CONTENT_TYPE_ACTIVITY)
                        .navigation()
                } else {
                    ToastUtils.showUnLoginMsg()
                    ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                        .navigation()
                }
            }
            mBinding.tvThumb.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)

            mBinding.tvCollect.setCompoundDrawablesWithIntrinsicBounds(null, collect, null, null)

            // ??????????????????????????????????????????????????????????????????????????????????????????
            mBinding.tvOrder.text =
                when (it.buttonStatus) {
                    // ????????????
                    "0" -> {
                        mBinding.tvOrder.background = resources.getDrawable(R.color.colorPrimary)
                        mBinding.tvOrder.isEnabled = true
                        var spannableString = SpannableString(
                            getString(
                                R.string.main_activity_join_immediately,
                                it.signCount
                            )
                        )
                        spannableString.setSpan(AbsoluteSizeSpan(15, true), 0, 4, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                        spannableString.setSpan(
                            AbsoluteSizeSpan(10, true), 4, spannableString.length, Spanned
                                .SPAN_INCLUSIVE_EXCLUSIVE
                        )
                        spannableString

                    }
                    // ???????????????
                    // ?????????????????????????????????2???????????????????????????1????????????????????????????????????????????????????????????
                    // ???????????????????????????2????????????????????????????????????????????????????????????????????????
                    "1" -> {
                        getString(R.string.main_activity_sign_done)
                    }
                    // ?????????
                    "2" -> {
                        mBinding.tvOrder.background = resources.getDrawable(R.color.c2f0d0)
                        getString(R.string.main_activity_order_apply)
                    }
                    // ??????
                    // ????????????????????????????????????????????????????????????????????????????????????????????????
                    "3" -> {
                        getString(R.string.main_activity_recruit_over)
                    }
                    // ?????????
                    "4" -> getString(R.string.main_activity_order_unstart)
                    // ????????????
                    "5" -> getString(R.string.main_activity_order_empty)

                    else -> getString(R.string.main_activity_order_immediately)
                }
        })

        // ????????????
        mModel.activityRelations.observe(this, Observer {
            if (it.size > 0) {
                ll_palce?.visibility = View.VISIBLE
                val tagLayoutManager =
                    LinearLayoutManager(this@VolunteerActivityDetailActivity, LinearLayoutManager.VERTICAL, false)
                mBinding.llPalce.rvActivities.layoutManager = tagLayoutManager
                mBinding.llPalce.rvActivities.adapter = relationAdapter
                relationAdapter!!.add(it)
                if (it.size > 4) {
                    mBinding.llPalce.tvActivitiesMore.visibility = View.VISIBLE

                } else {
                    mBinding.llPalce.tvActivitiesMore.visibility = View.GONE
                }
                ll_palce?.visibility = View.VISIBLE
            } else {
                ll_palce?.visibility = View.GONE
            }
        })

        // ??????
        mModel.commentBeans.observe(this, Observer {
            if (it.size > 0) {
                ll_comment?.visibility = View.VISIBLE
                val comments = if (it.size > 2) {
                    it.subList(0, 2)
                } else {
                    it
                }
                val tagLayoutManager =
                    LinearLayoutManager(this@VolunteerActivityDetailActivity, LinearLayoutManager.VERTICAL, false)
                mBinding.llComment.rvComments.layoutManager = tagLayoutManager

                val commentAdapter = CommentAdapter(this)
                mBinding.llComment.rvComments.adapter = commentAdapter

                mBinding.llComment.tvCommentNum1.setOnClickListener{
                    ARouter.getInstance()
                        .build(MainARouterPath.MAIN_ACTIVITY_COMMENT)
                        .withString("id", id)
                        .withString("type", ResourceType.CONTENT_TYPE_ACTIVITY)
                        .navigation()
                }
                commentAdapter.add(comments)
            } else {
                ll_comment?.visibility = View.GONE
            }

        })
    }

    /**
     * ???????????????
     * @param sureCommand ??????
     */
    fun noticeConfirm(context: Activity) {
        val dialog = AlertDialog.Builder(AppManager.instance.currentActivity()).create()
        dialog.show()
        val window = dialog.window
        val binding = DataBindingUtil.inflate<LayoutDialogNoticeBinding>(
            context.layoutInflater,
            R.layout.layout_dialog_notice, null, false
        )

        window!!.setContentView(binding.root)
        binding.label = ""
        binding.notice = "???????????????????????????{25}??????????????????350???????????????????????????{2019.10.10-2019.10.16}?????????????????????????????????"
        binding.cancel = "?????????"
        binding.sure = "????????????"
        binding.lvHonesty.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.UserModule.USER_CREDIT_ACTIVITY)
                .navigation()
        }

        binding.cancelSubmit = object : ReplyCommand {
            override fun run() {
                dialog.dismiss()
            }
        }
        binding.sureSubmit = object : ReplyCommand {
            override fun run() {
                dialog.dismiss()
            }
        }
    }

    override fun initData() {
        mModel.getActivityDetail(id, false)
        mModel.getActivityRelationList(id)
        mModel.getActivityVolunteerTeamList(id)
        mModel.getActivityCommentList(id)
        if (!classifyId.isNullOrEmpty()) {
            mModel.getActivityList(classifyId, id)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRecordEvent(event: UpdateCommentEvent) {
        // ??????
        mModel.getActivityCommentList(id)
    }
    /**
     * ????????????????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateCommentStatus(event: UpdateOrderCommentStatus) {
        if (mModel != null) {
            mModel.getActivityCommentList(id)
            mModel.getActivityDetail(id, true)
        }
    }
}

