package com.daqsoft.legacyModule.mine

import android.annotation.SuppressLint
import android.content.Intent
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ResourceUtils
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.adapter.GridWorksAdapter
import com.daqsoft.legacyModule.bean.LegacyStoryListBean
import com.daqsoft.legacyModule.databinding.*
import com.daqsoft.legacyModule.dp
import com.daqsoft.legacyModule.mine.vm.WorksDetailVM
import com.daqsoft.provider.*
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.bean.HomeStoryBean
import com.daqsoft.provider.bean.SimpleTopic
import com.daqsoft.provider.bean.ThumbBean
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.commentModule.CommentAdapter
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.provider.view.FlowLayout
import com.daqsoft.provider.view.convenientbanner.bean.VideoImageBean
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.convenientbanner.holder.VideoImageHolder
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import com.daqsoft.provider.view.popupwindow.SharePopWindow
import com.jakewharton.rxbinding2.view.RxView
import me.nereo.multi_image_selector.BigImgActivity
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
import java.util.ArrayList
import java.util.concurrent.TimeUnit

/**
 *@package:com.daqsoft.legacyModule.mine
 *@date:2020/5/19 14:50
 *@author: caihj
 *@des:作品详情页面
 **/
@Route(path = ARouterPath.LegacyModule.LEGACY_WORKS_DETAIL)
class WorksDetailActivity : TitleBarActivity<ActivityWorksDetailBinding, WorksDetailVM>() {


    @JvmField
    @Autowired
    var id: String = ""

    /**
     * 根据类型不同调用不同的取详情的接口
     * 0获取其他传承人作品 1取我的作品接口
     */
    @JvmField
    @Autowired
    var type = 1

    var worksDetail: HomeStoryBean? = null
    /**
     * 分享弹窗
     */
    var sharePopWindow: SharePopWindow? = null

    override fun getLayout(): Int = R.layout.activity_works_detail

    override fun setTitle(): String = getString(R.string.legacy_works_detail_title)

    override fun injectVm(): Class<WorksDetailVM> = WorksDetailVM::class.java


    override fun initCustomTitleBar(mTitleBar: TitleBar) {
        showShareButton(com.daqsoft.provider.R.mipmap.common_nav_button_share)
        setShareClick(object : View.OnClickListener {
            override fun onClick(v: View?) {
                worksDetail?.let {
                    if (sharePopWindow == null) {
                        sharePopWindow = SharePopWindow(this@WorksDetailActivity)
                    }
                    var content= Constant.SHARE_DEC
                    sharePopWindow?.setShareContent(
                        it?.title, content,  it?.images[0],
                        ShareModel.getStoryDetailUrl(it?.id.toString())
                    )
                    if (!sharePopWindow!!.isShowing) {
                        sharePopWindow?.showAsDropDown(mTitleBar)
                    }
                }
            }

        })
    }
    @SuppressLint("SetTextI18n")
    override fun initView() {
        mModel.worksDetail.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.clRoot.visibility = View.VISIBLE
            setStoryImg(it)
            worksDetail = it
            mModel.getPkWorks(it.id)
            mBinding.tvLike.text = it.likeNum.toString()
            mBinding.tvCollect.text = it.collectionNum.toString()
            mBinding.tvThumbNumber.text = it.likeNum.toString()
            mBinding.tvThumbNumber.visibility = if (it.likeNum > 0) {
                View.VISIBLE
            } else {
                View.GONE
            }
            mBinding.tvContent.loadDataWithBaseURL(null, it.content, "text/html", "utf-8", null)
            if (!it.pkId.isNullOrEmpty()) {
                mBinding.llPkTitle.visibility = View.VISIBLE
                mBinding.tvPkTitle.text = it.pkStoryTitle
                mBinding.llPkTitle.onNoDoubleClick {
                    ARouter.getInstance()
                        .build(ARouterPath.LegacyModule.LEGACY_WORKS_DETAIL)
                        .withInt("type", 0)
                        .withString("id", it.pkId)
                        .navigation()
                }
            }
            if (!it.vipHead.isNullOrEmpty()) {
                Glide.with(this).load(it.vipHead).placeholder(R.mipmap.mine_profile_photo_default)
                    .into(mBinding.ivUser)
            }
            if (!it.resourceRegionName.isNullOrEmpty()) {
                val sb = StringBuilder()
                mBinding.tvLocation.text = DividerTextUtils.convertDotString(
                    sb,
                    it.resourceRegionName,
                    getResourceTypeName(it.resourceType)
                )

            }
            mBinding.tvUser.text = it.vipNickName
            mBinding.tvTime.text = "${it.createDate} 发布"
            mBinding.tvTitle.text = it.title

            mBinding.tvWatch.visibility=View.INVISIBLE
//            if (!it.resourceRegionName.isNullOrEmpty()) {
//                mBinding.tvCity.text = it.tagName
//            } else {
//                mBinding.tvCity.visibility = View.GONE
//            }
//            if (!it.resourceName.isNullOrEmpty()) {
//                mBinding.tvResourceName.text = it.resourceName
//            } else {
//                mBinding.tvResourceName.visibility = View.GONE
//            }
//            if (!it.tagName.isNullOrEmpty()) {
//                mBinding.tvTag.text = "#${it.tagName}#"
//            } else {
//                mBinding.tvTag.visibility = View.GONE
//            }

            // 标签 2020/10/15 修改
            if (!it.resourceRegionName.isNullOrEmpty()) {
                val view = LayoutInflater.from(mBinding.root.context).inflate(
                    R.layout.activity_works_detail_flowlayout_item,
                    mBinding.llTag,
                    false
                )
                val textView = view.findViewById<TextView>(R.id.tv_resource_name)
                val drawable = this.getDrawable(R.mipmap.time_home_hot_position)?.apply {
                    setBounds(0, 0, minimumWidth, minimumHeight)
                }
                textView.setCompoundDrawables(drawable, null, null, null)
                textView.compoundDrawablePadding = 6.dp
                textView.text = it.resourceRegionName
                mBinding.llTag.addView(view)
            }
            if (!it.resourceName.isNullOrEmpty()) {
                val view = LayoutInflater.from(mBinding.root.context).inflate(
                    R.layout.activity_works_detail_flowlayout_item,
                    mBinding.llTag,
                    false
                )
                val textView = view.findViewById<TextView>(R.id.tv_resource_name)
                textView.text = it.resourceName
                mBinding.llTag.addView(view)
            }
            if (!it.tagName.isNullOrEmpty()) {
                val view = LayoutInflater.from(mBinding.root.context).inflate(
                    R.layout.activity_works_detail_flowlayout_item,
                    mBinding.llTag,
                    false
                )
                val textView = view.findViewById<TextView>(R.id.tv_resource_name)
                textView.text = "#${it.tagName}#"
                mBinding.llTag.addView(view)
            }

            if (!it.resourceName.isNullOrEmpty()) {
                mBinding.clProduct.visibility = View.VISIBLE
                Glide.with(this).load(it.resourceImage)
                    .placeholder(R.mipmap.placeholder_img_fail_h158).into(mBinding.avIcon)
                mBinding.tvLocationName.text = it.resourceName
                if (!it.resourceTypeName.isNullOrEmpty()) {
                    mBinding.tvGrade.visibility = View.VISIBLE
                    mBinding.tvGrade.text = it.resourceTypeName
                } else {
                    mBinding.tvGrade.visibility = View.GONE
                }
            } else {
                mBinding.clProduct.visibility = View.GONE
            }

            if (it.vipResourceStatus.likeStatus) {
                changeTvImage(mBinding.tvLike, R.mipmap.bottom_icon_like_selected)
            } else {
                changeTvImage(mBinding.tvLike, R.mipmap.bottom_icon_like_normal)
            }
            if (it.vipResourceStatus.collectionStatus) {
                changeTvImage(mBinding.tvCollect, R.mipmap.bottom_icon_collect_selected)
            } else {
                changeTvImage(mBinding.tvCollect, R.mipmap.bottom_icon_collect_normal)
            }
            if (!it.resourceCompleteRegionName.isNullOrEmpty()) {
                val text =
                    DividerTextUtils.convertDotString(it.resourceCompleteRegionName.split(","))
                mBinding.tvIntroduce.text = text
                if (!it.consumePerson.isNullOrEmpty()) {
                    val sb = java.lang.StringBuilder()
                    sb.append(text)
                    sb.append("  |  ")
                    sb.append("人均消费${it.consumePerson}")
                    mBinding.tvIntroduce.text = sb.toString()
                }
            }

            topicAdapter.add(it.topicInfo as MutableList<SimpleTopic>)
            mBinding.tvViewNumber.text =
                String.format(getString(R.string.works_show_number), it.showNum)
            val legacyId = SPUtils.getInstance().getString(SPKey.HERITAGE_PEOPLEID)
            if (!it.ichHp) {
                mBinding.ivLegacyPeopleLabel.visibility = View.GONE
            }

            // PK的作品不能再次被PK，自己也不能PK自己的作品
            if (it.vipPhone == SPUtils.getInstance()
                    .getString(SPKey.PHONESTR) || !it.pkId.isNullOrEmpty()
            ) {
                mBinding.tvWatch.visibility = View.GONE
            } else {
                mBinding.ivPkBtn.visibility = View.VISIBLE
                if (it.heirAttentionStatus) {
                    mBinding.tvWatch.isSelected = false
                    mBinding.tvWatch.text = "已关注"
                } else {
                    mBinding.tvWatch.isSelected = true
                    mBinding.tvWatch.text = "关注"
                }
            }
        })
        mBinding.rvThumb.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvThumb.adapter = avatarAdapter
        mModel.thumbList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.rvThumb.visibility = View.VISIBLE
                avatarAdapter.add(it)
            }
        })

        // 评论
        mBinding.rvComments.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val commentAdapter = CommentAdapter(this)

        mBinding.rvComments.adapter = commentAdapter
        mModel.commentBeans.observe(this, Observer {
            if (it.size > 0) {
                mBinding.tvReplayNum.text =
                    resources.getString(R.string.home_comments_number).format(it.size)
                commentAdapter.add(it)
            } else {
                mBinding.clComment.visibility = View.GONE
            }
        })

//        if (type == 1) {
//            mBinding.llBottomBtn.visibility = View.GONE
//        }
        mModel.likeStatus.observe(this, Observer {
            dissMissLoadingDialog()
            worksDetail?.vipResourceStatus?.likeStatus = true
            worksDetail?.likeNum = worksDetail?.likeNum!! + 1
            mBinding.tvLike.text = worksDetail?.likeNum!!.toString()
            changeTvImage(mBinding.tvLike, R.mipmap.bottom_icon_like_selected)
        })
        mModel.cancelLikeStatus.observe(this, Observer {
            dissMissLoadingDialog()
            worksDetail?.vipResourceStatus?.likeStatus = false
            worksDetail?.likeNum = worksDetail?.likeNum!! - 1
            mBinding.tvLike.text = worksDetail?.likeNum!!.toString()
            changeTvImage(mBinding.tvLike, R.mipmap.bottom_icon_like_normal)
        })
        mModel.collectStatus.observe(this, Observer {
            dissMissLoadingDialog()
            worksDetail?.vipResourceStatus?.collectionStatus = true
            worksDetail?.collectionNum = worksDetail?.collectionNum!! + 1
            mBinding.tvCollect.text = worksDetail?.collectionNum.toString()
            changeTvImage(mBinding.tvCollect, R.mipmap.bottom_icon_collect_selected)
        })
        mModel.cancelCollectStatus.observe(this, Observer {
            dissMissLoadingDialog()
            worksDetail?.vipResourceStatus?.collectionStatus = false
            worksDetail?.collectionNum = worksDetail?.collectionNum!! - 1
            mBinding.tvCollect.text = worksDetail?.collectionNum.toString()
            changeTvImage(mBinding.tvCollect, R.mipmap.bottom_icon_collect_normal)
        })

        mModel.focusStatus.observe(this, Observer {
            dissMissLoadingDialog()
            worksDetail?.heirAttentionStatus = true
            mBinding.tvWatch.isSelected = false
            mBinding.tvWatch.text = "已关注"
        })
        mModel.cancelFocusStatus.observe(this, Observer {
            dissMissLoadingDialog()
            worksDetail?.heirAttentionStatus = false
            mBinding.tvWatch.isSelected = true
            mBinding.tvWatch.text = "关注"
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })

        mBinding.tvCommentNum.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.Provider.PROVIDER_POST_COMMENT)
                .withString("id", id)
                .withString("type", ResourceType.CONTENT_TYPE_STORY)
                .withString("contentTitle", mModel.worksDetail.value!!.title)
                .navigation()
        }
        mBinding.clComment.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.Provider.PROVIDER_COMMENT_LS)
                .withString("id", id)
                .withString("type", ResourceType.CONTENT_TYPE_STORY)
                .withString("contentTitle", mModel.worksDetail.value!!.title)
                .navigation()
        }

        mBinding.tvLike.onNoDoubleClick {
            if (gotoLogin()) {
                showLoadingDialog()
                if (worksDetail?.vipResourceStatus?.likeStatus!!) {
                    mModel.cancelLike()
                } else {
                    mModel.addLike()
                }
            }
        }
        mBinding.tvCollect.onNoDoubleClick {
            if (gotoLogin()) {
                showLoadingDialog()
                if (!worksDetail?.vipResourceStatus?.collectionStatus!!) {
                    mModel.collect()
                } else {
                    mModel.cancelCollect()
                }
            }
        }
        // 关注
        mBinding.tvWatch.onNoDoubleClick {
            if (worksDetail?.heirAttentionStatus!!) {
                mModel.focusCancelHeritagePeople()
            } else {
                mModel.focusHeritagePeople()
            }
        }

        mBinding.clProduct.onNoDoubleClick {
            if (!mModel.worksDetail?.value?.resourceId.isNullOrEmpty() && !mModel.worksDetail?.value?.resourceType.isNullOrEmpty()) {
                var path = ""
                when (mModel.worksDetail?.value?.resourceType) {
                    // 场馆
                    ResourceType.CONTENT_TYPE_VENUE -> {
                        path = ARouterPath.VenuesModule.VENUES_DETAILS_ACTIVITY
                    }
                    // 农家乐
                    ResourceType.CONTENT_TYPE_AGRITAINMENT -> {
                        path = ARouterPath.CountryModule.COUNTRY_HAPPINESS_DETAIL
                    }
                    // 	活动室
                    ResourceType.CONTENT_TYPE_ACTIVITY_SHIU -> {
                        path = ARouterPath.VenuesModule.ACTIVITY_ROOM_DETAIL
                    }
                    // 酒店
                    ResourceType.CONTENT_TYPE_HOTEL -> {
                        path = ZARouterPath.ZMAIN_HOTEL_DETAIL
                        mModel.worksDetail?.value?.resourceId?.toInt()?.let {
                            ARouter.getInstance().build(path)
                                .withString("id", it.toString())
                                .navigation()
                        }
                        return@onNoDoubleClick
                    }
                    // 景区
                    ResourceType.CONTENT_TYPE_SCENERY -> {
                        path = MainARouterPath.MAIN_SCENIC_DETAIL
                    }
                    // 景点
                    ResourceType.CONTENT_TYPE_SCENIC_SPOTS -> {
                        path = MainARouterPath.MAIN_SCENIC_SPOT_DETAI
                    }
                    // 餐饮
                    ResourceType.CONTENT_TYPE_RESTAURANT -> {
                        path = MainARouterPath.MAIN_FOOD_DETAIL
                        mModel.worksDetail?.value?.resourceId?.let {
                            ARouter.getInstance().build(path)
                                .withString("id", it)
                                .navigation()
                        }
                        return@onNoDoubleClick
                    }
                    // 活动
                    ResourceType.CONTENT_TYPE_ACTIVITY -> {
                        path = ARouterPath.VenuesModule.VENUES_DETAILS_ACTIVITY
                    }
                    // 非遗传习基地
                    ResourceType.CONTENT_TYPE_HERITAGE_TEACHING_BASE -> {
                        mModel.worksDetail?.value?.resourceId?.let {
                            ARouter.getInstance()
                                .build(ARouterPath.LegacyModule.LEGACY_EXPERIENCE_DETAIL_ACTIVITY)
                                .withString("id", it)
                                .withString("type", mModel.worksDetail.value?.resourceType)
                                .navigation()
                        }
                    }
                    // 非遗体验基地
                    ResourceType.CONTENT_TYPE_HERITAGE_EXPERIENCE_BASE -> {
                        mModel.worksDetail?.value?.resourceId?.let {
                            ARouter.getInstance()
                                .build(ARouterPath.LegacyModule.LEGACY_EXPERIENCE_DETAIL_ACTIVITY)
                                .withString("id", it)
                                .withString("type", mModel.worksDetail.value?.resourceType)
                                .navigation()
                        }
                    }
                    // 特产
                    ResourceType.CONTENT_TYPE_SPECIALTY -> {
                        path = MainARouterPath.MAIN_SPECIAL_DETAIL
                    }
                    else -> {
                        toast("功能开发中，敬请期待!")
                    }
                }
                if (!path.isNullOrEmpty())
                    ARouter.getInstance().build(path)
                        .withString("id", mModel.worksDetail?.value?.resourceId)
                        .navigation()
            }
        }

        val storyLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvWorks.layoutManager = storyLayoutManager
        mBinding.rvWorks.adapter = worksAdapter

        mModel.pkWorks.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.clPk.visibility = View.VISIBLE
                mBinding.tvPkNum.text =
                    resources.getString(R.string.home_product_number).format(it.size)
                worksAdapter!!.add(it)
            }
        })

        mModel.pageBean.observe(this, Observer {
            mBinding.tvPkNum.text =
                resources.getString(R.string.home_product_number).format(it.total)
        })
        mBinding.tvPkNum.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.LegacyModule.LEGACY_WORK_LIST_ACTIVITY)
                .withString("type", "1")
                .withString("id", worksDetail?.id)
                .navigation()
        }
        mBinding.ivPkBtn.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.LegacyModule.LEGACY_PUBLISH_WORKS)
                .withString("id", worksDetail?.id)
                .withString("pkTitle", worksDetail?.title)
                .withString("type", "pk")
                .navigation()
        }


        // 话题
        topicAdapter.emptyViewShow = false
        mBinding.rvTopic.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL,
            false
        )
        mBinding.rvTopic.adapter = topicAdapter
    }


    private fun gotoLogin(): Boolean {
        if (!AppUtils.isLogin()) {
            toast("您还没有登录，请先登录!")
            ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                .navigation()
            return false
        }
        return true
    }

    private val worksAdapter = object :
        RecyclerViewAdapter<ItemLegacyWorkLineBinding, LegacyStoryListBean>(R.layout.item_legacy_work_line) {
        override fun setVariable(
            mBinding: ItemLegacyWorkLineBinding,
            position: Int,
            item: LegacyStoryListBean
        ) {
            Glide.with(this@WorksDetailActivity).load(item.vipHead)
                .placeholder(R.mipmap.mine_profile_photo_default).into(mBinding.ivUser)
            mBinding.name = item.vipNickName
            var image = ""
            if (!item.images.isNullOrEmpty()) {
                image = item.images[0]
            } else {
                if (!item.cover.isNullOrEmpty()) {
                    image = item.cover
                }
            }
            if (image.isNotEmpty()) {
                Glide.with(this@WorksDetailActivity).load(image).into(mBinding.image)
                if (!item.video.isNullOrEmpty()) {
                    mBinding.ivVideo.visibility = View.VISIBLE
                } else {
                    mBinding.ivVideo.visibility = View.GONE
                }
            } else {
                mBinding.image.visibility = View.GONE
            }

            if (item.resourceRegionName.isNullOrEmpty()) {
                mBinding.locationName.visibility = View.GONE
            } else {
                mBinding.locationName.text = item.resourceRegionName
            }

            if (item.resourceRegionName.isNullOrEmpty()) {
                // 判断是否关联地址和类型
                mBinding.tvCityType.visibility = View.INVISIBLE
            } else {
                mBinding.tvCityType.visibility = View.VISIBLE
            }
            mBinding.tvCityType.text =
                item.resourceRegionName + "·" + ResourceType.getName(item.resourceType)
            mBinding.tvCityType.setTextColor(resources.getColor(R.color.txt_gray))

            // 判断是否需要添加表填
            var ssb = SpannableStringBuilder()
            if (!item.tagName.isNullOrEmpty()) {
                ssb.append("#" + item.tagName + "#")
                ssb.setSpan(
                    ForegroundColorSpan(resources.getColor(R.color.colorPrimary)), 0, ssb
                        .length, Spanned
                        .SPAN_INCLUSIVE_EXCLUSIVE
                )
            }
            ssb.append(item.title)
            mBinding.tvContent.text = ssb

            if (ssb.isNullOrEmpty()) {
                mBinding.tvContent.visibility = View.INVISIBLE
            } else {
                mBinding.tvContent.visibility = View.VISIBLE
            }

            if (!item.images.isNullOrEmpty() && item.images.size > 1) {
                mBinding.tvImageNumber.text = getString(
                    R.string
                        .home_work_image_number, item.images.size.toString()
                )
                mBinding.tvImageNumber.visibility = View.VISIBLE
            } else {
                mBinding.tvImageNumber.visibility = View.GONE
            }
            RxView.clicks(mBinding.root)
                .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                    run {
                        ARouter.getInstance()
                            .build(ARouterPath.LegacyModule.LEGACY_WORKS_DETAIL)
                            .withInt("type", 0)
                            .withString("id", item.id.toString())
                            .navigation()
                    }
                }
            if (item.likeNum > 0) {
                mBinding.likeNumber = item.likeNum.toString()
                mBinding.tvLike.visibility = View.VISIBLE
            } else {
                mBinding.tvLike.visibility = View.GONE
            }
        }

    }

    private fun getResourceTypeName(resourceType: String): String {
        var type = ""
        when (resourceType) {
            // 场馆
            ResourceType.CONTENT_TYPE_VENUE -> {
                type = "场馆"
            }
            // 农家乐
            ResourceType.CONTENT_TYPE_AGRITAINMENT -> {
                type = "农家乐"
            }
            // 	活动室
            ResourceType.CONTENT_TYPE_ACTIVITY_SHIU -> {
                type = "活动室"
            }
            // 酒店
            ResourceType.CONTENT_TYPE_HOTEL -> {
                type = "酒店"
            }
            // 景区
            ResourceType.CONTENT_TYPE_SCENERY -> {
                type = "景区"
            }
            // 景点
            ResourceType.CONTENT_TYPE_SCENIC_SPOTS -> {
                type = "景点"
            }
            // 餐饮
            ResourceType.CONTENT_TYPE_RESTAURANT -> {
                type = "餐饮"
            }
            // 活动
            ResourceType.CONTENT_TYPE_ACTIVITY -> {
                type = "活动"
            }
            // 非遗传习基地
            ResourceType.CONTENT_TYPE_HERITAGE_TEACHING_BASE -> {
                type = "非遗传习基地"
            }
            //非遗体验基地
            ResourceType.CONTENT_TYPE_HERITAGE_EXPERIENCE_BASE -> {
                type = "非遗体验基地"
            }
            else -> {
                type = "未知"
            }
        }
        return type
    }

    /**
     * 点赞人数列表适配器
     */
    private val avatarAdapter = object
        : RecyclerViewAdapter<ItemWorksAvatarBinding, ThumbBean>(R.layout.item_works_avatar) {
        override fun setVariable(mBinding: ItemWorksAvatarBinding, position: Int, item: ThumbBean) {
//            mBinding.url = item.headUrl
            Glide.with(mBinding.root.context)
                .load(item.headUrl)
                .placeholder(R.mipmap.mine_profile_photo_default)
                .into(mBinding.image)
        }

    }

    private fun setStoryImg(it: HomeStoryBean) {
        if (it.images.isNullOrEmpty() && it.video.isNullOrEmpty()) {
            mBinding.cbannerWorkDetail.visibility = View.GONE
            mBinding.vWorksDetailIndex.visibility = View.GONE
        } else {
            mBinding.vWorksDetailIndex.visibility = View.VISIBLE
            var total = 0
            val images = it.images
            var imagesAndVideo = mutableListOf<VideoImageBean>()
            if (!it.video.isNullOrEmpty()) {
                total += 1
                imagesAndVideo.add(initVideoData(1, it.video))
            }
            if (!images.isNullOrEmpty()) {
                total += images.size
                for (item in images) {
                    imagesAndVideo.add(initVideoData(0, item))
                }
            }
            mBinding.txtCurrentIndex.text = "1"
            mBinding.txtTotalSize.text = "/${total}"

            mBinding.cbannerWorkDetail.setPages(object : CBViewHolderCreator {
                override fun createHolder(itemView: View?): Holder<*> {
                    return VideoImageHolder(itemView!!, this@WorksDetailActivity)
                }

                override fun getLayoutId(): Int {
                    return R.layout.layout_video_image
                }
            }, imagesAndVideo).setCanLoop(false).setPointViewVisible(false).setOnItemClickListener {
                val intent =
                    Intent(this, BigImgActivity::class.java)
                intent.putExtra("position", it)
                intent.putStringArrayListExtra(
                    "imgList",
                    ArrayList(images)
                )
                startActivity(intent)
            }
            mBinding.cbannerWorkDetail?.onPageChangeListener = object : OnPageChangeListener {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                }

                override fun onPageSelected(index: Int) {
                    mBinding?.txtCurrentIndex.text = ((index + 1).toString())
                    mBinding?.txtTotalSize.text = "/${total}"
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                }

            }
        }
    }

    /**
     * 修改textView顶部图片
     */
    private fun changeTvImage(v: TextView, image: Int) {
        val topDrawable = getDrawable(image)
        topDrawable?.setBounds(0, 0, topDrawable.minimumWidth, topDrawable.minimumHeight)
        v.setCompoundDrawables(null, topDrawable, null, null)
    }

    /**
     * 简单话题适配器
     */
    private val topicAdapter = object : RecyclerViewAdapter<ItemWorksTopicBinding, SimpleTopic>(
        R.layout
            .item_works_topic
    ) {
        override fun setVariable(
            mBinding: ItemWorksTopicBinding,
            position: Int,
            item: SimpleTopic
        ) {
            mBinding.name = item.topicName
        }
    }

    private fun initVideoData(type: Int, path: String): VideoImageBean {
        var video: VideoImageBean = VideoImageBean()
        video.type = type
        if (type == 1) {
            video.videoUrl = path
        } else {
            video.imageUrl = path
        }
        return video
    }


    override fun initData() {
        showLoadingDialog()
        mModel.id = id
        if (type == 1) {
            mModel.getMineWorkDetail(id)
        } else {
            mModel.getPeopleWorksDetail(id)
        }
        mModel.getThumbList(id, ResourceType.CONTENT_TYPE_STORY)
        mModel.getActivityCommentList(id)
    }
}