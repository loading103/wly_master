package com.daqsoft.legacyModule.smriti.ui

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
import com.daqsoft.legacyModule.adapter.GridWorksAdapter
import com.daqsoft.legacyModule.adapter.LegacyStoryGridAdapter
import com.daqsoft.legacyModule.bean.LegacyHeritageItemListBean
import com.daqsoft.legacyModule.bean.LegacyHeritagePeopleListBean
import com.daqsoft.legacyModule.bean.UpdateHeritageEvent
import com.daqsoft.legacyModule.databinding.ActivityLegacyPeopleDetailBinding
import com.daqsoft.legacyModule.databinding.ActivityLegacySmritiDetailBinding
import com.daqsoft.legacyModule.databinding.LegacyDetailItemPeopleBinding
import com.daqsoft.legacyModule.databinding.LegacyDetailItemProjectBinding
import com.daqsoft.legacyModule.getRealImages
import com.daqsoft.legacyModule.smriti.adapter.LegacyDetailClassifyAdapter
import com.daqsoft.legacyModule.smriti.bean.LegacyDetailBean
import com.daqsoft.legacyModule.smriti.bean.LegacyPeopleDetailBean
import com.daqsoft.legacyModule.smriti.event.UpdateFocusStatusEvent
import com.daqsoft.legacyModule.smriti.event.UpdateVideoPlayerEvent
import com.daqsoft.legacyModule.smriti.event.UpdateVideoPlayerStateEvent
import com.daqsoft.legacyModule.smriti.fragment.LegacyPicFragment
import com.daqsoft.legacyModule.smriti.fragment.LegacyVideoFragment
import com.daqsoft.legacyModule.smriti.util.TextFontUtil
import com.daqsoft.legacyModule.smriti.view.LegacyPageTransformer
import com.daqsoft.legacyModule.smriti.vm.LegacyPeopleDetailViewModel
import com.daqsoft.legacyModule.smriti.vm.LegacySmritiDetailViewModel
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.businessview.adapter.MoreListViewAdapter
import com.daqsoft.provider.businessview.event.UpdateCommentEvent
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.commentModule.CommentAdapter
import com.daqsoft.provider.network.venues.bean.AudioInfo
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.provider.utils.HtmlUtils
import com.daqsoft.provider.view.popupwindow.SharePopWindow
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.toast
import java.lang.StringBuilder

/**
 * desc :???????????????
 * @author caihj
 * @date 2020/5/15 11:33
 */
@Route(path = ARouterPath.LegacyModule.LEGACY_PEOPLE_DETAIL_ACTIVITY)
class LegacyPeopleDetailActivity :
    TitleBarActivity<ActivityLegacyPeopleDetailBinding, LegacyPeopleDetailViewModel>() {
    /**
     * ???????????????id
     */
    @JvmField
    @Autowired
    var id = ""

    @JvmField
    @Autowired
    var position = -1

    // ????????????fragment
    var legacyVideoFragment: LegacyVideoFragment? = null

    var mLegacyImages: MutableList<String>? = mutableListOf()

    /**
     * ????????????????????????
     */
    var isHaveVideo: Boolean = false

    /**
     * ????????????
     */
    var imageSize: Int = 0

    /**
     * ????????????-??????fragments
     */
    var mDatasLegacyTopFrags: MutableList<Fragment> = mutableListOf()

    var legacyClassfyAdapter: LegacyDetailClassifyAdapter? = null
    var peopleDetail: LegacyPeopleDetailBean? = null

    /**
     * ????????????
     */
    var sharePopWindow: SharePopWindow? = null

    /**
     * ???????????????????????????
     */
//    private val legacyDetailClassifyAdapter by lazy { LegacyDetailClassifyAdapter() }
    override fun getLayout(): Int {
        return R.layout.activity_legacy_people_detail
    }

    override fun setTitle(): String {
        return "?????????????????????"
    }

    override fun injectVm(): Class<LegacyPeopleDetailViewModel> =
        LegacyPeopleDetailViewModel::class.java

    override fun initView() {
        mModel.id = id
        setClassifyHeaderData()
        setClick()
    }

    /**
     *????????????
     */
    private fun setClick() {
        // vp????????????
        mBinding.legacyHeader.vpLegacySmriti.addOnPageChangeListener(object :
            ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (isHaveVideo && position != 0) {
                    EventBus.getDefault().post(UpdateVideoPlayerStateEvent(1))
                    changeVideoAndPicLabel(true)
                }
                var offsetPos = -1
                if (isHaveVideo) offsetPos += 1
                if (position > offsetPos && imageSize > 0) {
                    mBinding.legacyHeader.tvPic.text = "??????" + "${position - offsetPos}/${imageSize}"
                }
            }

        })
        // ??????????????????
        mBinding.legacyHeader.tvVideo.onNoDoubleClick {
            mBinding.legacyHeader.vpLegacySmriti.setCurrentItem(0, true)
            if (imageSize > 0) {
                mBinding.legacyHeader.tvPic.text = "??????" + "1/${imageSize}"
            }
        }
        // ??????????????????
        mBinding.legacyHeader.tvPic.onNoDoubleClick {
            var offset: Int = -1
            if (isHaveVideo) offset += 1
            mBinding.legacyHeader.vpLegacySmriti.setCurrentItem(offset + 1, true)
            if (imageSize > 0) {
                mBinding.legacyHeader.tvPic.text = "??????" + "1/${imageSize}"
            }
        }
        mBinding.tvLike.onNoDoubleClick {
            if (gotoLogin()) {
                showLoadingDialog()
                if (peopleDetail?.getLikeStatus()!!) {
                    mModel.cancelLike()
                } else {
                    mModel.addLike()
                }
            }
        }
        mBinding.tvCollect.onNoDoubleClick {
            if (gotoLogin()) {
                showLoadingDialog()
                if (!peopleDetail?.getCollectionStatus()!!) {
                    mModel.collect()
                } else {
                    mModel.cancelCollect()
                }
            }
        }
        mBinding.tvAddComment.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.Provider.PROVIDER_ORDER_COM_ACTIVITY)
                .withString("id", id)
                .withString("type", ResourceType.CONTENT_TYPE_HERITAGE_PEOPLE)
                .withString("contentTitle", mModel.legacyDetails.value!!.name)
                .navigation()
        }
        mBinding.clComments.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.Provider.PROVIDER_COMMENT_LS)
                .withString("id", id)
                .withString("type", ResourceType.CONTENT_TYPE_HERITAGE_PEOPLE)
                .withString("contentTitle", mModel.legacyDetails.value!!.name)
                .navigation()
        }
        mBinding.tvProductNum.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.LegacyModule.LEGACY_WORK_LIST_ACTIVITY)
                .withString("id", id)
                .withString("type", "2")
                .withString("phone", mModel.legacyDetails.value!!.phone)
                .navigation()
        }
    }

    fun changeVideoAndPicLabel(isShow: Boolean) {
        if (isShow) {
            mBinding.legacyHeader.tvVideo.visibility = View.VISIBLE
            mBinding.legacyHeader.tvPic.visibility = View.VISIBLE
        } else {
            mBinding.legacyHeader.tvVideo.visibility = View.GONE
            mBinding.legacyHeader.tvPic.visibility = View.GONE
        }
    }

    override fun initCustomTitleBar(mTitleBar: TitleBar) {
        showShareButton(com.daqsoft.provider.R.mipmap.common_nav_button_share)
        setShareClick(View.OnClickListener {
            peopleDetail?.let {
                if (sharePopWindow == null) {
                    sharePopWindow = SharePopWindow(this)
                }
                var content= Constant.SHARE_DEC
                // ??????????????????
                sharePopWindow?.setShareContent(
                    it.name, content, it.images.getRealImages(),
                    ShareModel.getSuccessorDesc(id)
                )
                if (!sharePopWindow!!.isShowing) {
                    sharePopWindow?.showAsDropDown(mTitleBar)
                }
            }
        })
    }

    /**
     *??????????????????
     */
    private fun setClassifyHeaderData() {
        mModel.legacyDetails.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.root.visibility = View.VISIBLE
            peopleDetail = it
            // ????????????
            val images = it.images.split(",")
            if (!images.isNullOrEmpty()) {
                mLegacyImages?.clear()
                mLegacyImages?.addAll(images)
            }

            // ?????? ?????????
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
                mBinding.tvAddComment.text = "?????????"
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
                        mDatasLegacyTopFrags.add(
                            LegacyPicFragment.newInstance(
                                item,
                                images as MutableList<String>,
                                i
                            )
                        )
                    }
                } catch (e: Exception) {

                }
//
                imageSize = images.size
                mBinding.legacyHeader.tvPic.text = "??????1/${imageSize}"
                mBinding.legacyHeader.tvPic.visibility = View.VISIBLE
            } else {
                mBinding.legacyHeader.tvPic.visibility = View.GONE
            }
            if (!mDatasLegacyTopFrags.isNullOrEmpty()) {
                mBinding.legacyHeader.vpLegacySmriti.setPageTransformer(
                    false,
                    LegacyPageTransformer()
                )
                legacyClassfyAdapter =
                    LegacyDetailClassifyAdapter(mDatasLegacyTopFrags, supportFragmentManager)
                mBinding.legacyHeader.vpLegacySmriti.adapter = legacyClassfyAdapter
                mBinding.legacyHeader.vpLegacySmriti.offscreenPageLimit = mDatasLegacyTopFrags.size
                mBinding.legacyHeader.vpLegacySmriti.visibility = View.VISIBLE
            } else {
                mBinding.legacyHeader.vpLegacySmriti.visibility = View.GONE
            }


            setHeaderData(it)
            listenerSpeaker(it)
        })
        // ???????????????
        mModel.legacyItems.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.mvProject.visibility = View.VISIBLE
                mBinding.mvProject.setTitle("????????????")
                mBinding.mvProject.setTitleIcon(R.drawable.legacy_project_details_title_line)
//                    mBinding.mvProject.setTitleInfo("(${it.size})")
                mBinding.mvProject.setAdapter(itemAdapter, arrayListOf(it[0]))
            }
        })

        mBinding.rvProduct.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = storyAdapter
        }
        // ????????????
        mModel.detailStoryList.observe(this, Observer { storyList ->
            if (storyList.isNotEmpty()) {
                mBinding.clProduct.visibility = View.VISIBLE
                if (storyList.size <= 6) {
                    mBinding.tvProductNum.visibility = View.GONE
                } else {
                    mBinding.tvProductNum.text =
                        resources.getString(R.string.home_product_number).format(storyList.size)
                }
            }
            mBinding.rvProduct.isVisible = storyList.isNotEmpty()
            if (storyList.isNullOrEmpty()) return@Observer
            storyAdapter.clearNotify()
            storyAdapter.add(storyList.toMutableList())

        })

        // ??????
        mBinding.rvComments.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val commentAdapter = CommentAdapter(this)
        mBinding.rvComments.adapter = commentAdapter
        mModel.commentBeans.observe(this, Observer {response->
            response.datas?.let {
                if (it.size > 0) {
                    commentAdapter?.clear()
                    mBinding.clComments.visibility = View.VISIBLE
                    mBinding.rvComments.visibility = View.VISIBLE
                    mBinding.tvReplayNum.text =
                        resources.getString(R.string.home_comments_number).format(response.page?.total)
                    commentAdapter.add(it)
                }
            }
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })

        mModel.likeStatus.observe(this, Observer {
            dissMissLoadingDialog()
            peopleDetail?.likeStatus = 1
            peopleDetail?.likeNum = peopleDetail?.likeNum!! + 1
            mBinding.tvLike.text = peopleDetail?.likeNum!!.toString()
            changeTvImage(mBinding.tvLike, R.mipmap.bottom_icon_like_selected)
        })
        mModel.cancelLikeStatus.observe(this, Observer {
            dissMissLoadingDialog()
            peopleDetail?.likeStatus = 0
            peopleDetail?.likeNum = peopleDetail?.likeNum!! - 1
            mBinding.tvLike.text = peopleDetail?.likeNum!!.toString()
            changeTvImage(mBinding.tvLike, R.mipmap.bottom_icon_like_normal)
        })
        mModel.collectStatus.observe(this, Observer {
            dissMissLoadingDialog()
            peopleDetail?.collectionStatus = 1
            peopleDetail?.collectionNum = peopleDetail?.collectionNum!! + 1
            mBinding.tvCollect.text = peopleDetail?.collectionNum.toString()
            changeTvImage(mBinding.tvCollect, R.mipmap.bottom_icon_collect_selected)
            EventBus.getDefault().post(UpdateHeritageEvent())
        })
        mModel.cancelCollectStatus.observe(this, Observer {
            dissMissLoadingDialog()
            peopleDetail?.collectionStatus = 0
            peopleDetail?.collectionNum = peopleDetail?.collectionNum!! - 1
            mBinding.tvCollect.text = peopleDetail?.collectionNum.toString()
            changeTvImage(mBinding.tvCollect, R.mipmap.bottom_icon_collect_normal)
            EventBus.getDefault().post(UpdateHeritageEvent())
        })

        mModel.focusStatus.observe(this, Observer {
            dissMissLoadingDialog()
            peopleDetail?.fansStatus = 1
            mBinding.legacyHeader.tvWatch.isSelected = false
            mBinding.legacyHeader.tvWatch.text = "?????????"
            EventBus.getDefault().post(UpdateFocusStatusEvent(peopleDetail?.id!!, true, position))
        })
        mModel.cancelFocusStatus.observe(this, Observer {
            dissMissLoadingDialog()
            peopleDetail?.fansStatus = 0
            mBinding.legacyHeader.tvWatch.isSelected = true
            mBinding.legacyHeader.tvWatch.text = "??????"
            EventBus.getDefault().post(UpdateFocusStatusEvent(peopleDetail?.id!!, false, position))
        })
    }

    // ?????????????????????
    private val itemAdapter by lazy {
        object :
            MoreListViewAdapter<LegacyDetailItemProjectBinding, LegacyHeritageItemListBean>(R.layout.legacy_detail_item_project) {
            override fun setVariable(
                mBinding: LegacyDetailItemProjectBinding,
                position: Int,
                item: LegacyHeritageItemListBean
            ) {
                Glide.with(this@LegacyPeopleDetailActivity)
                    .load(item.images.getRealImages())
                    .into(mBinding.iv)
                mBinding.tvName.text = item.name
                val sb = StringBuilder()

                val info = DividerTextUtils.convertDotString(sb, item.type, item.level, item.batch)
                mBinding.tvLevel.text = info
                sb.clear()

                var productNum = item.storyNum
                val spb = SpannableStringBuilder()
                val strs = mutableListOf<String>()
                var productLen = productNum.toString().length
                var fansLen = item.peopleNum.toString().length
                var showNumLen = item.showNum.toString().length
                if (productNum != 0) {
                    strs.add("${productNum}???????????????")
                } else {
                    productLen = 0
                }
                if (item.peopleNum != 0) {
                    strs.add("${item.peopleNum}????????????")
                } else {
                    fansLen = 0
                }
                if (item.showNum != 0) {
                    strs.add("${item.showNum} ?????????")
                } else {
                    showNumLen = 0
                }
                val text = DividerTextUtils.convertDotString(strs)
                spb.append(text)
                try {
                    var colorSpan = ForegroundColorSpan(resources.getColor(R.color.color_333))
                    var length=0
                    if(productNum!=0){
                        spb.setSpan(colorSpan, 0, productLen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        length=productLen+6;
                    }
                    if(fansLen!=0){
                        colorSpan = ForegroundColorSpan(resources.getColor(R.color.color_333))
                        spb.setSpan(colorSpan, length, length+fansLen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        length += fansLen + 5;
                    }
                    if(showNumLen!=0){
                        colorSpan = ForegroundColorSpan(resources.getColor(R.color.color_333))
                        spb.setSpan(colorSpan, length, length+showNumLen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }

                    mBinding.tvDesNum.text = spb
                }catch (e :Exception){
                    mBinding.tvDesNum.text = spb
                }
                mBinding.root.onNoDoubleClick {
                    ARouter.getInstance()
                        .build(ARouterPath.LegacyModule.LEGACY_Smrity_DETAIL_ACTIVITY)
                        .withString("id", item.id.toString())
                        .navigation()
                }
            }

        }
    }

    private fun setHeaderData(legacyBean: LegacyPeopleDetailBean) {
        val strBuilder = StringBuilder("")

        val textStr = DividerTextUtils.convertString(
            strBuilder,
            if (!legacyBean?.getGender().isNullOrEmpty()) {
                "${legacyBean.getGender()}"
            } else {
                ""
            },
            if (legacyBean.nationality.isNullOrEmpty()) {
                ""
            } else {
                "${legacyBean.nationality}"
            },
            if (legacyBean.regionName.isNullOrEmpty()) {
                ""
            } else {
                "${legacyBean.regionName}"
            },
            if (legacyBean.level.isNullOrEmpty()) {
                ""
            } else {
                "${legacyBean.level}"
            },
            if (legacyBean.batch.isNullOrEmpty()) {
                ""
            } else {
                "${legacyBean.batch}"
            }
        )
        mBinding.legacyHeader.tvHeaderHint.text = textStr
        mBinding.legacyHeader.tvTitle.text = legacyBean.name
        if (legacyBean.type.isNullOrEmpty()) {
            mBinding.legacyHeader.tvType.visibility = View.GONE
        }
        if (legacyBean.isMe()) {
            mBinding.legacyHeader.tvWatch.visibility = View.GONE
        } else {
            if (legacyBean.getFanStatus()) {
                mBinding.legacyHeader.tvWatch.isSelected = false
                mBinding.legacyHeader.tvWatch.text = "?????????"
            } else {
                mBinding.legacyHeader.tvWatch.isSelected = true
                mBinding.legacyHeader.tvWatch.text = "??????"
            }
            mBinding.legacyHeader.tvWatch.onNoDoubleClick {
                if (peopleDetail?.getFanStatus()!!) {
                    mModel.focusCancelHeritagePeople()
                } else {
                    mModel.focusHeritagePeople()
                }
            }
        }
        mBinding.legacyPeopleIntroduce.tvReportCompany.text = legacyBean.reportCompany
        if (legacyBean.introduce.isNotEmpty()) {
            mBinding.legacyPeopleIntroduce.tvIntroduce.visibility = View.VISIBLE
            mBinding.legacyPeopleIntroduce.tvIntroduce.settings.javaScriptEnabled = true
            mBinding.legacyPeopleIntroduce.tvIntroduce.loadDataWithBaseURL(
                null,
                StringUtil.getHtml(legacyBean.introduce),
                "text/html",
                "utf-8",
                null
            )
        }
        //???????????????????????????
        var strNumBuilder = StringBuilder("")
        val spb = SpannableStringBuilder()
        var storyNum = if (legacyBean.storyNum.isNullOrEmpty()) {
            "0"
        } else {
            legacyBean.storyNum
        }
        val text = DividerTextUtils.convertDotString(
            strNumBuilder,
            "$storyNum ?????????",
            "${legacyBean.fans} ?????????",
            "${legacyBean.showNum} ?????????"
        )
        spb.append(text)
        val productLen = storyNum.length
        val fansLen = legacyBean.fans.toString().length
        val showNumLen = legacyBean.showNum.length
        var colorSpan = ForegroundColorSpan(resources.getColor(R.color.color_333))
        spb.setSpan(colorSpan, 0, productLen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        colorSpan = ForegroundColorSpan(resources.getColor(R.color.color_333))
        spb.setSpan(
            colorSpan,
            productLen + 7,
            productLen + 7 + fansLen,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        colorSpan = ForegroundColorSpan(resources.getColor(R.color.color_333))
        spb.setSpan(
            colorSpan,
            productLen + 14 + fansLen,
            productLen + 14 + fansLen + showNumLen,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        mBinding.legacyHeader.tvNum.text = spb
    }

    /**
     * ??????textView????????????
     */
    private fun changeTvImage(v: TextView, image: Int) {
        val topDrawable = getDrawable(image)
        topDrawable?.setBounds(0, 0, topDrawable.minimumWidth, topDrawable.minimumHeight)
        v.setCompoundDrawables(null, topDrawable, null, null)
    }

    override fun initData() {
        EventBus.getDefault().register(this)
        showLoadingDialog()
        mModel.getLegacyPeopleDetail()
        mModel.getActivityCommentList()
    }

    private val storyAdapter by lazy { GridWorksAdapter(this) }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun dealVideoPlayer(event: UpdateVideoPlayerEvent) {
        when (event.type) {
            1 -> {
                // ??????
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
            toast("?????????????????????????????????!")
            ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                .navigation()
            return false
        }
        return true
    }

    /**
     * ?????????
     */
    private fun listenerSpeaker(legacyBean: LegacyPeopleDetailBean) {
        // ?????????
        var audios: MutableList<AudioInfo> = mutableListOf()
        // ????????????
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
            mBinding.vPeopleDetailAudios.visibility = View.VISIBLE
            mBinding.vPeopleDetailAudios.setTitle("?????????")
            mBinding.vPeopleDetailAudios.setTitleIcon(R.drawable.legacy_project_details_title_line)
            mBinding.vPeopleDetailAudios.setData(audios)
        } else {
            mBinding.vPeopleDetailAudios.visibility = View.GONE
        }

    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().post(UpdateVideoPlayerStateEvent(1))
        mBinding.vPeopleDetailAudios.pauseAudioPlayer()

    }

    override fun onBackPressed() {
        try {
            // ?????????????????????????????????
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
            mBinding.vPeopleDetailAudios?.stopAudioPlayer()
            mBinding.vPeopleDetailAudios?.releaseAudioPlayer()
        } catch (e: java.lang.Exception) {

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRecordEvent(event: UpdateCommentEvent) {
        mModel.getLegacyPeopleDetail()
        mModel.getActivityCommentList()
    }
}
