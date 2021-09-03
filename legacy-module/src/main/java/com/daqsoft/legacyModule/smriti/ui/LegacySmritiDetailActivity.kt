package com.daqsoft.legacyModule.smriti.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.adapter.LegacyStoryGridAdapter
import com.daqsoft.legacyModule.bean.LegacyHeritagePeopleListBean
import com.daqsoft.legacyModule.databinding.ActivityLegacySmritiDetailBinding
import com.daqsoft.legacyModule.databinding.LegacyDetailItemPeopleBinding
import com.daqsoft.legacyModule.getRealImages
import com.daqsoft.legacyModule.smriti.adapter.LegacyDetailClassifyAdapter
import com.daqsoft.legacyModule.smriti.bean.LegacyDetailBean
import com.daqsoft.legacyModule.smriti.event.UpdateFocusStatusEvent
import com.daqsoft.legacyModule.smriti.event.UpdateVideoPlayerEvent
import com.daqsoft.legacyModule.smriti.event.UpdateVideoPlayerStateEvent
import com.daqsoft.legacyModule.smriti.fragment.LegacyPicFragment
import com.daqsoft.legacyModule.smriti.fragment.LegacyVideoFragment
import com.daqsoft.legacyModule.smriti.util.TextFontUtil
import com.daqsoft.legacyModule.smriti.view.LegacyPageTransformer
import com.daqsoft.legacyModule.smriti.vm.LegacySmritiDetailViewModel
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.bean.LegacyProductBean
import com.daqsoft.provider.businessview.adapter.MoreListViewAdapter
import com.daqsoft.provider.businessview.event.UpdateCommentEvent
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.commentModule.CommentAdapter
import com.daqsoft.provider.network.venues.bean.AudioInfo
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.provider.utils.HtmlUtils
import com.daqsoft.provider.utils.StringUtils
import com.daqsoft.provider.view.popupwindow.SharePopWindow
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.toast
import java.lang.StringBuilder

/**
 * desc :非遗项目详情
 * @author 江云仙
 * @date 2020/4/24 11:33
 */
@Route(path = ARouterPath.LegacyModule.LEGACY_Smrity_DETAIL_ACTIVITY)
class LegacySmritiDetailActivity : TitleBarActivity<ActivityLegacySmritiDetailBinding, LegacySmritiDetailViewModel>() {
    /**
     * 非遗项目id
     */
    @JvmField
    @Autowired
    var id = ""

    @JvmField
    @Autowired
    var position = -1

    // 视频播放fragment
    var legacyVideoFragment: LegacyVideoFragment? = null

    var mLegacyImages: MutableList<String>? = mutableListOf()

    /**
     * 标识是否存在视频
     */
    var isHaveVideo: Boolean = false

    /**
     * 图片多少
     */
    var imageSize: Int = 0

    /**
     * 滑动视频-图片fragments
     */
    var mDatasLegacyTopFrags: MutableList<Fragment> = mutableListOf()

    var legacyClassfyAdapter: LegacyDetailClassifyAdapter? = null

    var legacyDetails: LegacyDetailBean? = null

    /**
     * 分享弹窗
     */
    var sharePopWindow: SharePopWindow? = null

    /**
     * 非遗项目分类适配器
     */
//    private val legacyDetailClassifyAdapter by lazy { LegacyDetailClassifyAdapter() }
    override fun getLayout(): Int {
        return R.layout.activity_legacy_smriti_detail
    }

    override fun setTitle(): String {
        return "非遗项目详情"
    }

    override fun injectVm(): Class<LegacySmritiDetailViewModel> = LegacySmritiDetailViewModel::class.java
    override fun initView() {
        mModel.id = id
        mModel.heritageItemId = id
        setClassifyHeaderData()
        setClick()
    }

    /**
     *点击事件
     */
    private fun setClick() {
        // vp滑动事件
        mBinding.legacyHeader.vpLegacySmriti.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (isHaveVideo && position != 0) {
                    EventBus.getDefault().post(UpdateVideoPlayerStateEvent(1))
                    changeVideoAndPicLabel(true)
                }
                var offsetPos = -1
                if (isHaveVideo) offsetPos += 1
                if (position > offsetPos && imageSize > 0) {
                    mBinding.legacyHeader.tvPic.text = "图集" + "${position - offsetPos}/${imageSize}"
                }
            }

        })
        // 视频点击事件
        mBinding.legacyHeader.tvVideo.onNoDoubleClick {
            mBinding.legacyHeader.vpLegacySmriti.setCurrentItem(0, true)
            if (imageSize > 0) {
                mBinding.legacyHeader.tvPic.text = "图集" + "1/${imageSize}"
            }
        }
        // 图片点击事件
        mBinding.legacyHeader.tvPic.onNoDoubleClick {
            var offset: Int = -1
            if (isHaveVideo) offset += 1
            mBinding.legacyHeader.vpLegacySmriti.setCurrentItem(offset + 1, true)
            if (imageSize > 0) {
                mBinding.legacyHeader.tvPic.text = "图集" + "1/${imageSize}"
            }
        }
        mBinding.tvLike.onNoDoubleClick {
            if (gotoLogin()) {
                showLoadingDialog()
                if (legacyDetails?.getLikeStatus()!!) {
                    mModel.cancelLike()
                } else {
                    mModel.addLike()
                }
            }
        }
        mBinding.tvCollect.onNoDoubleClick {
            if (gotoLogin()) {
                showLoadingDialog()
                if (!legacyDetails?.getCollectionStatus()!!) {
                    mModel.collect()
                } else {
                    mModel.cancelCollect()
                }
            }
        }
        mBinding.tvAddComment.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.Provider.PROVIDER_ORDER_COM_ACTIVITY)
                .withString("id", id)
                .withString("type", ResourceType.CONTENT_TYPE_HERITAGE_ITEM)
                .withString("contentTitle", mModel.legacyDetails.value!!.name)
                .navigation()
        }
        mBinding.clComments.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.Provider.PROVIDER_COMMENT_LS)
                .withString("id", id)
                .withString("type", ResourceType.CONTENT_TYPE_HERITAGE_ITEM)
                .withString("contentTitle", mModel.legacyDetails.value!!.name)
                .navigation()
        }
        mBinding.clStory.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.LegacyModule.LEGACY_STORY_ACTIVITY)
                .navigation()
        }
    }

    /**
     *头部分类数据
     */
    private fun setClassifyHeaderData() {
        mModel.legacyDetails.observe(this, Observer {
            dissMissLoadingDialog()
            legacyDetails = it

            mBinding.root.visibility = View.VISIBLE
            // 图片相关
            val images = it.images.split(",")
            if (!images.isNullOrEmpty()) {
                mLegacyImages?.clear()
                mLegacyImages?.addAll(images)
            }

            // 视频 和图片
            if (!it.video.isNullOrEmpty()) {
                legacyVideoFragment = LegacyVideoFragment.newInstance(it.video)
                mDatasLegacyTopFrags.add(legacyVideoFragment!!)
                isHaveVideo = true
                mBinding.legacyHeader.tvVideo.visibility = View.VISIBLE
            } else {
                mBinding.legacyHeader.tvVideo.visibility = View.GONE
            }
            mBinding.tvLike.text = it.likeNum.toString()
            mBinding.tvCollect.text = it.collectionNum.toString()
            if (it.commentNum == 0) {
                mBinding.tvAddComment.text = "写评论"
            } else {
                mBinding.tvAddComment.text = "${it.commentNum}"
            }
            if (it.getLikeStatus()) {
                changeTvImage(mBinding.tvLike, R.mipmap.bottom_icon_like_selected)
            } else {
                changeTvImage(mBinding.tvLike, R.mipmap.bottom_icon_like_normal)
            }
            if (it.getCollectionStatus()) {
                changeTvImage(mBinding.tvCollect, R.mipmap.bottom_icon_collect_selected)
            } else {
                changeTvImage(mBinding.tvCollect, R.mipmap.bottom_icon_collect_normal)
            }

            if (!images.isNullOrEmpty()) {
                try {
                    for (i in images.indices) {
                        val item = images[i]
                        mDatasLegacyTopFrags.add(LegacyPicFragment.newInstance(item, images as MutableList<String>, i))
                    }
                } catch (e: Exception) {

                }
//
                imageSize = images.size
                mBinding.legacyHeader.tvPic.text = "图集1/${imageSize}"
                mBinding.legacyHeader.tvPic.visibility = View.VISIBLE
            } else {
                mBinding.legacyHeader.tvPic.visibility = View.GONE
            }
            if (!mDatasLegacyTopFrags.isNullOrEmpty()) {
                mBinding.legacyHeader.vpLegacySmriti.setPageTransformer(false, LegacyPageTransformer())
                legacyClassfyAdapter = LegacyDetailClassifyAdapter(mDatasLegacyTopFrags, supportFragmentManager)
                mBinding.legacyHeader.vpLegacySmriti.adapter = legacyClassfyAdapter
                mBinding.legacyHeader.vpLegacySmriti.offscreenPageLimit = mDatasLegacyTopFrags.size
                mBinding.legacyHeader.vpLegacySmriti.visibility = View.VISIBLE
            } else {
                mBinding.legacyHeader.vpLegacySmriti.visibility = View.GONE
            }


            setHeaderData(it)
            listenerSpeaker(it)

            // 获取非遗商品
            mModel.getProductList(it.resourceCode)

        })

        // 非遗商品
        mModel.productBean.observe(this, Observer {
            if (it.list.isNotEmpty()) {
                supportFragmentManager.beginTransaction().apply {
                    val legacyCommodityFragment = LegacyCommodityFragment.newInstance(legacyDetails?.resourceCode ?: "")
                    legacyCommodityFragment.setData(it.list as MutableList<LegacyProductBean>)
                    add(mBinding.commodityContainer.id, legacyCommodityFragment)
                    commit()
                }
            }
        })


        // 非遗传承人
        mModel.legacyPeopleBean.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.mvPeople.visibility = View.VISIBLE
                mBinding.mvPeople.setTitle("非遗传承人")
                mBinding.mvPeople.setTitleIcon(R.drawable.legacy_project_details_title_line)
                mBinding.mvPeople.setTitleInfo("(${it.size})")
                mBinding.mvPeople.setAdapter(peopleAdapter, it)
            }
        })

        mBinding.rvStory.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = storyAdapter
        }
        // 故事列表
        mModel.detailStoryList.observe(this, Observer { storyList ->
            if (storyList.isNotEmpty()) {
                mBinding.clStory.visibility = View.VISIBLE
                mBinding.tvStoryNum.text = resources.getString(R.string.home_story_number).format(storyList.size)
            }
            mBinding.rvStory.isVisible = storyList.isNotEmpty()
            if (storyList.isNullOrEmpty()) return@Observer
            storyAdapter.clearNotify()
            storyAdapter.add(storyList.toMutableList())

        })
        mModel.storyPageBean.observe(this, Observer {
            mBinding.tvStoryNum.text = resources.getString(R.string.home_story_number).format(it.total)

        })

        // 评论
        mBinding.rvComments.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val commentAdapter = CommentAdapter(this)
        mBinding.rvComments.adapter = commentAdapter
        mModel.commentBeans.observe(this, Observer {response->
            response.datas?.let{
                if (it.size > 0) {
                    commentAdapter?.clear()
                    mBinding.clComments.visibility = View.VISIBLE
                    mBinding.rvComments.visibility = View.VISIBLE
                    mBinding.tvReplayNum.text = resources.getString(R.string.home_comments_number).format(response.page?.total)
                    commentAdapter.add(it)
                }
            }
        })
        mModel.likeStatus.observe(this, Observer {
            dissMissLoadingDialog()
            legacyDetails?.likeStatus = 1
            legacyDetails?.likeNum = legacyDetails?.likeNum!! + 1
            mBinding.tvLike.text = legacyDetails?.likeNum!!.toString()
            changeTvImage(mBinding.tvLike, R.mipmap.bottom_icon_like_selected)
        })
        mModel.cancelLikeStatus.observe(this, Observer {
            dissMissLoadingDialog()
            legacyDetails?.likeStatus = 0
            legacyDetails?.likeNum = legacyDetails?.likeNum!! - 1
            mBinding.tvLike.text = legacyDetails?.likeNum!!.toString()
            changeTvImage(mBinding.tvLike, R.mipmap.bottom_icon_like_normal)
        })
        mModel.collectStatus.observe(this, Observer {
            dissMissLoadingDialog()
            EventBus.getDefault().post(UpdateFocusStatusEvent(id, true, position))
            legacyDetails?.collectionStatus = 1
            legacyDetails?.collectionNum = legacyDetails?.collectionNum!! + 1
            mBinding.tvCollect.text = legacyDetails?.collectionNum.toString()
            changeTvImage(mBinding.tvCollect, R.mipmap.bottom_icon_collect_selected)
        })
        mModel.cancelCollectStatus.observe(this, Observer {
            dissMissLoadingDialog()
            EventBus.getDefault().post(UpdateFocusStatusEvent(id, false, position))
            legacyDetails?.collectionStatus = 0
            legacyDetails?.collectionNum = legacyDetails?.collectionNum!! - 1
            mBinding.tvCollect.text = legacyDetails?.collectionNum.toString()
            changeTvImage(mBinding.tvCollect, R.mipmap.bottom_icon_collect_normal)
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })
    }


    override fun initCustomTitleBar(mTitleBar: TitleBar) {
        showShareButton(com.daqsoft.provider.R.mipmap.common_nav_button_share)
        setShareClick(View.OnClickListener {
            legacyDetails?.let {
                if (sharePopWindow == null) {
                    sharePopWindow = SharePopWindow(this)
                }
                var content= Constant.SHARE_DEC
                // 设置分享数据
                sharePopWindow?.setShareContent(
                    it.name, content, it.images.getRealImages(),
                    ShareModel.getProjectDescUrl(id)
                )
                if (!sharePopWindow!!.isShowing) {
                    sharePopWindow?.showAsDropDown(mTitleBar)
                }
            }
        })
    }

    // 非遗传承人适配器
    private val peopleAdapter by lazy {
        object : MoreListViewAdapter<LegacyDetailItemPeopleBinding, LegacyHeritagePeopleListBean>(R.layout.legacy_detail_item_people) {
            override fun setVariable(mBinding: LegacyDetailItemPeopleBinding, position: Int, item: LegacyHeritagePeopleListBean) {
                Glide.with(this@LegacySmritiDetailActivity)
                    .load(item.images.getRealImages())
                    .into(mBinding.iv)
                mBinding.tvName.text = item.name
                val sb = StringBuilder()
                var sex = if (item.gender == 1) {
                    "性别：男"
                } else {
                    "性别：女"
                }
                if (item.nationality.isNullOrEmpty()) {
                    mBinding.tvSex.text = sex
                } else {
                    val info = DividerTextUtils.convertString(sb, sex, "名族：${item.nationality}")
                    mBinding.tvSex.text = info
                }
                sb.clear()
                var productNum = item.storyNum.toString()
                val spb = SpannableStringBuilder()
                val product = "$productNum 个作品"
                val fans = "${item.fans} 个粉丝"
                val show = "${item.showNum} 次浏览"
                val text = DividerTextUtils.convertDotString(sb, product, fans, show)
                spb.append(text)
                val productLen = productNum.length
                val fansLen = item.fans.toString().length
                val showNumLen = item.showNum.toString().length


                // 设置颜色
                var colorSpan = ForegroundColorSpan(resources.getColor(R.color.color_333))
                spb.setSpan(colorSpan, 0, productLen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                colorSpan = ForegroundColorSpan(resources.getColor(R.color.color_333))
                spb.setSpan(colorSpan, product.length + 3, product.length + 3 + fansLen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                colorSpan = ForegroundColorSpan(resources.getColor(R.color.color_333))
                spb.setSpan(
                    colorSpan,
                    product.length + fans.length + 6,
                    product.length + fans.length + 6 + showNumLen,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                mBinding.tvDesNum.text = spb

                mBinding.root.onNoDoubleClick {
                    ARouter.getInstance().build(ARouterPath.LegacyModule.LEGACY_PEOPLE_DETAIL_ACTIVITY)
                        .withString("id", item.id)
                        .navigation()
                }
            }

        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setHeaderData(legacyBean: LegacyDetailBean) {
        mBinding.legacyHeader.bean = legacyBean



        mBinding.legacyHeader.webview.apply {
            settings.javaScriptEnabled = true
            loadDataWithBaseURL(null, StringUtil.getHtml(legacyBean.introduce.trim()), "text/html", "utf-8", null)
        }


        mBinding.legacyHeader.tvIntroduce.text = HtmlUtils.html2Str(legacyBean.introduce)
        val strBuilder = StringBuilder("")
//        if (!legacyBean.regionName.isNullOrEmpty()) {
//            mBinding.legacyHeader.tvHeaderHint.visibility = View.VISIBLE
//            strBuilder.append("地区：${legacyBean.regionName}")
//        }
//        if (!legacyBean.level.isNullOrEmpty()) {
//            mBinding.legacyHeader.tvHeaderHint.visibility = View.VISIBLE
//            strBuilder.append(" | 级别：${legacyBean.level}")
//        }
//        if (!legacyBean.batch.isNullOrEmpty()) {
//            mBinding.legacyHeader.tvHeaderHint.visibility = View.VISIBLE
//            strBuilder.append(" | 批次：${legacyBean.batch}")
//        }
        val textStr = DividerTextUtils.convertString(strBuilder, legacyBean.regionName, legacyBean.type, legacyBean.level, legacyBean.batch)
        mBinding.legacyHeader.tvHeaderHint.text = textStr

        //设置粗体和字体大小
        var strNumBuilder = StringBuilder("")
        val spb = SpannableStringBuilder()
        var storyNum = if (legacyBean.storyNum.isNullOrEmpty()) {
            "0"
        } else {
            legacyBean.storyNum
        }
        val text = DividerTextUtils.convertDotString(strNumBuilder, "$storyNum 个非遗故事", "${legacyBean.peopleNum} 个代表性传承人", "${legacyBean.showNum} 次浏览")
        spb.append(text)
        val productLen = storyNum.length
        val fansLen = legacyBean.peopleNum.length
        val showNumLen = legacyBean.showNum.length
        var colorSpan = ForegroundColorSpan(resources.getColor(R.color.color_333))
        spb.setSpan(colorSpan, 0, productLen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        colorSpan = ForegroundColorSpan(resources.getColor(R.color.color_333))
        spb.setSpan(colorSpan, productLen + 9, productLen + 9 + fansLen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        colorSpan = ForegroundColorSpan(resources.getColor(R.color.color_333))
        spb.setSpan(colorSpan, productLen + 20 + fansLen, productLen + 20 + fansLen + showNumLen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        mBinding.legacyHeader.tvNum.text = spb
    }

    /**
     * 修改textView顶部图片
     */
    private fun changeTvImage(v: TextView, image: Int) {
        val topDrawable = getDrawable(image)
        topDrawable?.setBounds(0, 0, topDrawable.minimumWidth, topDrawable.minimumHeight)
        v.setCompoundDrawables(null, topDrawable, null, null)
    }

    override fun initData() {
        EventBus.getDefault().register(this)
        showLoadingDialog()
        mModel.getLegacyDetail()
        mModel.getLegacyPeople()
        mModel.getDetailStoryList()
        mModel.getActivityCommentList()
    }

    private val storyAdapter by lazy { LegacyStoryGridAdapter(this) }


    fun changeVideoAndPicLabel(isShow: Boolean) {
        if (isShow) {
            mBinding.legacyHeader.tvVideo.visibility = View.VISIBLE
            mBinding.legacyHeader.tvPic.visibility = View.VISIBLE
        } else {
            mBinding.legacyHeader.tvVideo.visibility = View.GONE
            mBinding.legacyHeader.tvPic.visibility = View.GONE
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun dealVideoPlayer(event: UpdateVideoPlayerEvent) {
        when (event.type) {
            1 -> {
                // 播放
                changeVideoAndPicLabel(false)
            }
            2 -> {
                // Stop
                changeVideoAndPicLabel(true)
            }
        }
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

    /**
     * 听解说
     */
    private fun listenerSpeaker(legacyBean: LegacyDetailBean) {
        // 听解说
        var audios: MutableList<AudioInfo> = mutableListOf()
        // 金牌解说
        if (legacyBean.goldStory != null && !legacyBean.goldStory.audio.isNullOrEmpty()) {
            audios.add(AudioInfo().apply {
                type = 1
                linkUrl = legacyBean.goldStory.link
                audio = legacyBean.goldStory.turl
                name = legacyBean.name
            })
        }

        if (!legacyBean.audioInfo.isNullOrEmpty()) {
            audios.addAll(legacyBean.audioInfo)
        }
        if (!audios.isNullOrEmpty()) {
            mBinding.vProjectDetailAudios.visibility = View.VISIBLE
            mBinding.vProjectDetailAudios.setTitle("听非遗")
            mBinding.vProjectDetailAudios.setTitleIcon(R.drawable.legacy_project_details_title_line)
            mBinding.vProjectDetailAudios.setData(audios)
        } else {
            mBinding.vProjectDetailAudios.visibility = View.GONE
        }

    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().post(UpdateVideoPlayerStateEvent(1))
        mBinding.vProjectDetailAudios.pauseAudioPlayer()

    }

    override fun onBackPressed() {
        try {
            // 处理视频播发器全屏问题
            if (!isHaveVideo || mDatasLegacyTopFrags == null || !legacyVideoFragment!!.onBackPress()) {
                super.onBackPressed()
            }
        } catch (e: java.lang.Exception) {

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            EventBus.getDefault().post(UpdateVideoPlayerStateEvent(2))
            EventBus.getDefault().unregister(this)
            mDatasLegacyTopFrags.clear()
            legacyClassfyAdapter = null
            mBinding.vProjectDetailAudios?.stopAudioPlayer()
            mBinding.vProjectDetailAudios?.releaseAudioPlayer()
        } catch (e: java.lang.Exception) {

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRecordEvent(event: UpdateCommentEvent) {
        mModel.getLegacyDetail()
        mModel.getActivityCommentList()
    }
}
