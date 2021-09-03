package com.daqsoft.travelCultureModule.contentActivity

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.WebSettings
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.adapter.setCenterCropImage
import com.daqsoft.baselib.adapter.setImageUrlqwx
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.widgets.ArcImageView
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityContentInfoBinding
import com.daqsoft.mainmodule.databinding.ItemResourceContentBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.businessview.event.UpdateCommentEvent
import com.daqsoft.provider.businessview.fragment.ProviderAddCommentFragment
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.commentModule.CommentAdapter
import com.daqsoft.provider.network.comment.beans.CommentBean
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.provider.utils.WebViewUtils
import com.daqsoft.provider.view.popupwindow.SharePopWindow
import com.daqsoft.travelCultureModule.contentActivity.bean.ContentInfoBean
import com.daqsoft.travelCultureModule.contentActivity.bean.ImageUrl
import com.daqsoft.travelCultureModule.contentActivity.bean.ResourceLink
import com.daqsoft.travelCultureModule.contentActivity.vm.ContentActivityViewModel
import com.google.gson.internal.LinkedTreeMap
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.toast
import java.io.IOException

/**
 * 咨询详情界面
 */
@Route(path = MainARouterPath.MAIN_CONTENT_INFO)
class ContentInfoActivity : TitleBarActivity<ActivityContentInfoBinding, ContentActivityViewModel>() {

    var mList = ArrayList<ImageUrl>()
    var viewList = ArrayList<View>()
    var page = 1

    @Autowired
    @JvmField
    var contentTitle: String = ""

    var contentId: String = ""
    var pageSize = 6

    var id : String = ""
    val commentAdapter = CommentAdapter(this)
    val mediaPlayer = MediaPlayer()
    var isPlay = false
    lateinit var imageAdudio: ImageView
    lateinit var subscribe: Disposable
    private var addCommentPopFragment: ProviderAddCommentFragment? = null


    override fun getLayout(): Int = R.layout.activity_content_info

    override fun setTitle(): String = contentTitle

    override fun injectVm(): Class<ContentActivityViewModel> = ContentActivityViewModel::class.java
    /**
     * 分享弹窗
     */
    var sharePopWindow: SharePopWindow? = null
    override fun initCustomTitleBar(mTitleBar: TitleBar) {
        showShareButton(com.daqsoft.provider.R.mipmap.common_nav_button_share)
        setShareClick(View.OnClickListener {
            cur_bean?.let {
                if (sharePopWindow == null) {
                    sharePopWindow = SharePopWindow(this@ContentInfoActivity)
                }
                sharePopWindow?.setShareContent(
                    it.title, if (it.content.isNullOrEmpty()) {
                        "点击查看详情"
                    } else {
                        it.summary
                    }, if (it.imageUrls.isNullOrEmpty()) {
                        ""
                    } else {
                        "" + it.imageUrls[0].url
                    },
                    ShareModel.getContentDetailUrl(contentId)
                )
                if (!sharePopWindow!!.isShowing) {
                    sharePopWindow?.showAsDropDown(mTitleBar)
                }
            }
        })
    }



    var adapter = object : PagerAdapter() {
        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(viewList[position])
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val imageView = ArcImageView(this@ContentInfoActivity)
            imageView.setCornerRadius(5f)
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP)
            var imageUrl: String = mList[position].url
            Glide.with(this@ContentInfoActivity)
                .load(StringUtil.getDqImageUrl(imageUrl, 670, 335))
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .into(imageView)
            container.addView(imageView)
            viewList.add(imageView)
            return imageView
        }

        override fun getCount(): Int {
            return mList.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }
    }
    lateinit var cur_bean: ContentInfoBean

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView() {
        EventBus.getDefault().register(this)
        contentId = intent.getStringExtra("id")
        mModel.getZixunInfo(contentId ?: "")
        mBinding.tvLikeNum.setOnClickListener {
            if (gotoLogin()) {
                if (cur_bean != null && cur_bean.vipResourceStatus.likeStatus) {
                    mModel.check_dislike(contentId ?: "", ResourceType.CONTENT)
                    cur_bean.vipResourceStatus.likeStatus = false
                    checkBoomlike(false)
                    cur_bean.likeNum = cur_bean.likeNum - 1
                    mBinding.tvLikeNum.text = cur_bean.likeNum.toString()
                } else if (cur_bean != null && !cur_bean.vipResourceStatus.likeStatus) {
                    mModel.check_like(contentId ?: "", ResourceType.CONTENT)
                    cur_bean.vipResourceStatus.likeStatus = true
                    checkBoomlike(true)
                    cur_bean.likeNum = cur_bean.likeNum + 1;
                    mBinding.tvLikeNum.text = cur_bean.likeNum.toString()
                }
            }
        }
        mBinding.tvCollectNum.setOnClickListener {
            if (gotoLogin()) {
                if (cur_bean != null && cur_bean!!.vipResourceStatus.collectionStatus) {
                    mModel.check_discolect(contentId ?: "", ResourceType.CONTENT)
                    cur_bean.vipResourceStatus.collectionStatus = false
                    checkBoomColect(false)
                    cur_bean.collectionNum = cur_bean.collectionNum - 1;
                    mBinding.tvCollectNum.text = cur_bean.collectionNum.toString()
                } else if (cur_bean != null && !cur_bean!!.vipResourceStatus.collectionStatus) {
                    mModel.check_colect(contentId ?: "", ResourceType.CONTENT)
                    cur_bean.vipResourceStatus.collectionStatus = true
                    checkBoomColect(true)
                    cur_bean.collectionNum = cur_bean.collectionNum + 1;
                    mBinding.tvCollectNum.text = cur_bean.collectionNum.toString()
                }
            }
        }

        mBinding.tvShare.onNoDoubleClick {
            toast("分享功能开发中")
        }

        mBinding.tvComtentSay.setOnClickListener {
            if (gotoLogin()) {
                showCommentPopup()
                // 获取焦点
//                mBinding.etContentSay.setFocusable(true);
//                mBinding.etContentSay.setFocusableInTouchMode(true);
//                mBinding.etContentSay.requestFocus();
//                // 弹出软键盘
//                var imm: InputMethodManager =
//                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                imm.showSoftInput(mBinding.etContentSay, 0);
//                mBinding.llContentLike.visibility = View.GONE
//                mBinding.llContentComent.visibility = View.VISIBLE
            }
        }
        //发送评论
        mBinding.tvContentSend.setOnClickListener {
            mBinding.llContentLike.visibility = View.VISIBLE
            mBinding.llContentComent.visibility = View.GONE
            var imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(mBinding.etContentSay.getWindowToken(), 0);
            var content = mBinding.etContentSay.text.toString()
            if (!content.equals("")) {
                mModel.post_comment(contentId ?: "", ResourceType.CONTENT, content)
                mBinding.etContentSay.setText("")
            }

        }
        mModel.message_comment.observe(this@ContentInfoActivity, Observer {
            toast(it)
        })
        mModel.zixunInfo.observe(this@ContentInfoActivity, Observer {
            if (it != null) {
                cur_bean = it
                if (it.content.isNullOrEmpty()) {
                    if (it.videoInfo != null && !it.videoInfo.toString().isNullOrEmpty()) {//加载视频资源
                        var map: LinkedTreeMap<String, String> =
                            (it.videoInfo as LinkedTreeMap<String, String>)
                        var imageView: fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard =
                            fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard(this@ContentInfoActivity)
                        var width = resources.displayMetrics.widthPixels
                        var new_width = width
                        var new_hight = (new_width * 210 / 375)
                        var param = LinearLayout.LayoutParams(new_width.toInt(), new_hight.toInt())
                        imageView.layoutParams = param as ViewGroup.LayoutParams?
                        imageView.setUp(map.get("url"), JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
                        if (!map["imgUrl"].isNullOrEmpty()) {
                            imageView.thumbImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            setImageUrlqwx(
                                imageView.thumbImageView,
                                map.get("imgUrl"),
                                AppCompatResources.getDrawable(
                                    BaseApplication.context, R.drawable.placeholder_img_fail_240_180
                                ),
                                5
                            )
                        } else {
                            // 异步加载视频缩略图
                            Glide.with(this@ContentInfoActivity)
                                .setDefaultRequestOptions(RequestOptions().frame(1000000).centerCrop())
                                .load(map.get("url"))
                                .into(imageView.thumbImageView)
                        }
                        mBinding.llContentVideo.addView(imageView)
                    }

                    var audioDes = ""
                    if (it.audioInfo != null && !it.audioInfo.toString().isNullOrEmpty()) {
                        var map: LinkedTreeMap<String, String> =
                            (it.audioInfo as LinkedTreeMap<String, String>)
                        audioDes = map["description"] ?: ""
                        playOnlineSound(map.get("url").toString())
                        var view = layoutInflater.inflate(R.layout.item_audio, null)
                        if (!map.get("imgUrl").isNullOrEmpty()) {
                            val imageBgColor = view.findViewById<RelativeLayout>(R.id.rl_audio)
                            imageBgColor.backgroundColor = resources.getColor(R.color.white)
                            val imageBgAdudio = view.findViewById<ImageView>(R.id.iv_item_audio)
                            Glide.with(this).load(map.get("imgUrl")).into(imageBgAdudio)
                        }
                        imageAdudio = view.findViewById<ImageView>(R.id.iv_item_play)
                        imageAdudio.setOnClickListener {
                            if (!isPlay) {
                                isPlay = true
                                mediaPlayer.start()
                                imageAdudio.setImageResource(R.mipmap.zxxq_video_pause)
                            } else {
                                isPlay = false
                                mediaPlayer.pause()
                                imageAdudio.setImageResource(R.mipmap.zxxq_video)
                            }
                        }
                        mBinding.llContentVideo.addView(view)
                    }
                } else {
                    if (it.videoInfo != null && !it.videoInfo.toString().isNullOrEmpty()) {//加载视频资源
                        var map: LinkedTreeMap<String, String> =
                            (it.videoInfo as LinkedTreeMap<String, String>)
                        var imageView: fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard =
                            fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard(this@ContentInfoActivity)
                        var width = resources.displayMetrics.widthPixels
                        var new_width = width
                        var new_hight = (new_width * 210 / 375)
                        var param = LinearLayout.LayoutParams(new_width.toInt(), new_hight.toInt())
                        imageView.layoutParams = param as ViewGroup.LayoutParams?
                        imageView.setUp(map.get("url"), JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
                        if (!map["imgUrl"].isNullOrEmpty()) {
                            imageView.thumbImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            setImageUrlqwx(
                                imageView.thumbImageView,
                                map.get("imgUrl"),
                                AppCompatResources.getDrawable(
                                    BaseApplication.context, R.drawable.placeholder_img_fail_240_180
                                ),
                                5
                            )
                        } else {
                            // 异步加载视频缩略图
                            Glide.with(this@ContentInfoActivity)
                                .setDefaultRequestOptions(RequestOptions().frame(1000000).centerCrop())
                                .load(map.get("url"))
                                .into(imageView.thumbImageView)
                        }
                        mBinding.llContentVideo2.addView(imageView)
                        val textView = TextView(this)
                        val textViewParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        textView.layoutParams = textViewParams
                        textView.text = "建议在wifi环境下观看"
                        textViewParams.topMargin = resources.getDimensionPixelSize(R.dimen.dp_5)
                        textViewParams.bottomMargin = resources.getDimensionPixelSize(R.dimen.dp_10)
                        mBinding.llContentVideo2.addView(textView)
                    }

                    var audioDes = ""
                    if (it.audioInfo != null && !it.audioInfo.toString().isNullOrEmpty()) {
                        var map: LinkedTreeMap<String, String> =
                            (it.audioInfo as LinkedTreeMap<String, String>)
                        audioDes = map["description"] ?: ""
                        playOnlineSound(map.get("url").toString())
                        var view = layoutInflater.inflate(R.layout.item_audio, null)
                        if (!map.get("imgUrl").isNullOrEmpty()) {
                            val imageBgColor = view.findViewById<RelativeLayout>(R.id.rl_audio)
                            imageBgColor.backgroundColor = resources.getColor(R.color.white)
                            val imageBgAdudio = view.findViewById<ImageView>(R.id.iv_item_audio)
                            Glide.with(this).load(map.get("imgUrl")).into(imageBgAdudio)
                        }
                        imageAdudio = view.findViewById<ImageView>(R.id.iv_item_play)
                        imageAdudio.setOnClickListener {
                            if (!isPlay) {
                                isPlay = true
                                mediaPlayer.start()
                                imageAdudio.setImageResource(R.mipmap.zxxq_video_pause)
                            } else {
                                isPlay = false
                                mediaPlayer.pause()
                                imageAdudio.setImageResource(R.mipmap.zxxq_video)
                            }
                        }
                        mBinding.llContentVideo2.addView(view)
                    }
                }
                mBinding.tvContentTitle.text = it.title
                mBinding.tvContentCompanyName.text = it.createCompanyName
                var contentTime: String = DividerTextUtils.convertDotString(
                    StringBuilder(),
                    if (BaseApplication.appArea == "sc") {
                        "${it.showNum}次浏览"
                    } else {
                        ""
                    },
                    DateUtil.formatDateByString(
                        "yyyy-MM-dd",
                        "yyyy-MM-dd HH:mm",
                        it.publishTime
                    ) + "发布"
                )
                mBinding.tvContentTime.text = contentTime
                mBinding.tvLikeNum.text = it.likeNum.toString()
                mBinding.tvCollectNum.text = it.collectionNum.toString()
                checkBoomlike(it.vipResourceStatus.likeStatus)
                checkBoomColect(it.vipResourceStatus.collectionStatus)
                var contentLY = ""
                if (!it.author.isNullOrEmpty()) {
                    contentLY = contentLY + "原文作者:" + if (!it.author.isNullOrEmpty()) it.author else it.createCompanyName + "    "
                }
                if (!it.source.isNullOrEmpty()) {
                    contentLY = contentLY + "文章来源:" + it.source
                }
                mBinding.tvContentLy.text = contentLY
                if (!it.sourceUrl.isNullOrEmpty()) {
                    val spb = SpannableStringBuilder()
                    val text = "原文链接：" + it.sourceUrl
                    spb.append(text)
                    val colorSpan = ForegroundColorSpan(resources.getColor(R.color.color_333))
                    spb.setSpan(colorSpan, 5, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    mBinding.tvContentLj.text = spb
                } else {
                    mBinding.tvContentLj.visibility = View.GONE
                }

                mBinding.tvComment.apply {
                    if (it.commentNum > 0)
                        text = "${it.commentNum}"
                    onNoDoubleClick {
                        ARouter.getInstance()
                            .build(MainARouterPath.MAIN_CONTENT_COMMENT_LIST)
                            .withString("infoId", contentId ?: "")
                            .navigation()
                    }
                }

                mBinding.tvContentCommentNum.text = "(共" + formatTosepara(it.commentNum) + "条评论)"
                if (it.createCompanyLogo.isNullOrEmpty()) {
                    mBinding.ivContentCompanyImg.visibility = View.GONE
                } else {
                    mBinding.ivContentCompanyImg.visibility = View.VISIBLE
                    setCenterCropImage(
                        mBinding.ivContentCompanyImg, it.createCompanyLogo,
                        AppCompatResources.getDrawable(
                            BaseApplication.context, R.drawable.placeholder_img_fail_240_180
                        ), true
                    )
                }
                mBinding.mvContentInfo.settings.javaScriptEnabled = true
                var setting = mBinding.mvContentInfo.settings
                setting.setJavaScriptEnabled(true)
                // 不使用缓存
                setting.setCacheMode(WebSettings.LOAD_NO_CACHE)
                setting.setUserAgentString(System.getProperty("http.agent"))
                // 把html中的内容放大webview等宽的一列中
                setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN)
                setting.setAppCacheEnabled(true)
                setting.setDomStorageEnabled(true)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    setting
                        .setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW)
                }
                if (!it.content.isNullOrEmpty() && it.content.contains("frame")) {
                    WebViewUtils.pptWeb(mBinding.mvContentInfo, it.content,null,this@ContentInfoActivity)
                } else {
                    val html = StringUtil.getHtml(" <html><header><style type=\"text/css\">body{word-wrap:break-word;}</style></header>" + it.content + "</html>")
                    WebViewUtils.pptWeb(mBinding.mvContentInfo,null,html,this@ContentInfoActivity)
                }

                var imges = it.imageUrls
                if (!imges.isNullOrEmpty()) {
                    for (value in imges.indices) {
                        mList.add(imges[value] as ImageUrl)
                        if (value == 0 && !imges[value].resourceName.isNullOrEmpty()) {
                            mBinding.tvContentPosition.visibility = View.VISIBLE

                            mBinding.tvContentPosition.text = imges[value].resourceName
                        }
                    }
                } else {
                    mBinding.tvContentPosition.visibility = View.GONE
                }
                if (!it.tagName.isNullOrEmpty()) {
                    mBinding.lvTags.setLabels(it.tagName as MutableList<String>?)
                } else {
                    mBinding.lvTags.visibility = View.GONE
                }
                mBinding.tvCiiCur.text = "1"
                mBinding.tvCiiAll.text = mList.size.toString()
                adapter.notifyDataSetChanged()
                if (mList.size == 0) {
                    mBinding.rlImg.visibility = View.GONE
                }

                if (!it.resourceLinksInfo.isNullOrEmpty()) {
                    resAdapter.add(it.resourceLinksInfo
                        // 过滤品牌
                        .filter { it.resourceType != ResourceType.CONTENT_TYPE_BRAND }
                            as MutableList<ResourceLink>)
                    resAdapter.notifyDataSetChanged()
                } else {
                    mBinding.rvResource.visibility = View.GONE
                }
                if (!it.annex.isNullOrEmpty()) {
                    val fj = it.annex[0] as String
                    mBinding.tvContentFj.visibility = View.VISIBLE
                    mBinding.tvContentFj.text = fj.substring(fj.lastIndexOf("/") + 1, fj.length)
                }
                mBinding.tvContentFj.onNoDoubleClick {
                    toast("请前往微官网下载查看")
                }
            }
        })
        val resLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        resAdapter.emptyViewShow = false
        mBinding.rvResource.layoutManager = resLinearLayoutManager
        mBinding.rvResource.adapter = resAdapter
        mBinding.vpCii.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                mBinding.tvCiiCur.text = (position + 1).toString()
            }

            override fun onPageSelected(position: Int) {
                if (!mList[position].resourceName.isNullOrEmpty()) {
                    mBinding.tvContentPosition.visibility = View.VISIBLE
                    mBinding.tvContentPosition.text = mList[position].resourceName
                } else {
                    mBinding.tvContentPosition.visibility = View.GONE
                }
            }

        })
        mBinding.vpCii.adapter = adapter
        //获取评论列表
        mModel.commentBeans.observe(this, Observer {
            if (it.size > 0) {
                if(page==1){
                    commentAdapter.clear()
                }
                pageDeal(page, it, commentAdapter)
                commentAdapter.add(it)
            } else {
                if (page == 1)
                    mBinding.llCiComment.visibility = View.GONE
            }
        })
        val tagLayoutManager = LinearLayoutManager(this@ContentInfoActivity, LinearLayoutManager.VERTICAL, false)
        commentAdapter.emptyViewShow = false
        mBinding.rvClubPinglun.layoutManager = tagLayoutManager
        mBinding.rvClubPinglun.adapter = commentAdapter
        commentAdapter.setOnLoadMoreListener {
            page++
            reloadData()
        }

    }

    var resAdapter = object :
        RecyclerViewAdapter<ItemResourceContentBinding, ResourceLink>(R.layout.item_resource_content) {
        override fun setVariable(
            mBinding: ItemResourceContentBinding,
            position: Int,
            item: ResourceLink
        ) {
            mBinding.tvResource.text = item.resourceName
            mBinding.root.onNoDoubleClick {
                MenuJumpUtils.gotoResourceDetail(item.resourceType, item.resourceId)
            }

        }

    }


    fun checkBoomlike(like: Boolean) {
        if (like) {
            mBinding.tvLikeNum.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                resources.getDrawable(R.mipmap.main_bottom_icon_like_selected), null, null
            )
        } else {
            mBinding.tvLikeNum.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                resources.getDrawable(R.mipmap.main_bottom_icon_like_normal), null, null
            )
        }
    }

    fun checkBoomColect(colect: Boolean) {
        if (colect) {
            mBinding.tvCollectNum.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                resources.getDrawable(R.mipmap.main_bottom_icon_collect_selected), null, null
            )
        } else {
            mBinding.tvCollectNum.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                resources.getDrawable(R.mipmap.main_bottom_icon_collect_normal), null, null
            )
        }

    }

    fun gotoLogin(): Boolean {
        if (!AppUtils.isLogin()) {
            toast("您还没登录，请先登录！")
            ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY).navigation()
            return false
        }
        return true
    }


    override fun initData() {
         id = intent.getStringExtra("id") ?: ""
        mModel.getActivityCommentList(id, ResourceType.CONTENT, page, pageSize)
    }

    private fun pageDeal(
        page: Int?, response: MutableList<CommentBean>, adapter:
        RecyclerViewAdapter<*, *>
    ) {
        if (page == null) {
            return
        }
        if (page == 1) {
            // adapter.clear()
        }
        if (response == null) {
            adapter.loadEnd()
            return
        }
        if (response.size >= pageSize) {
            adapter.loadComplete()
        } else {
            adapter.loadEnd()
        }
    }

    private fun playOnlineSound(soundUrlDict: String) {
        try {
            mediaPlayer.setDataSource(soundUrlDict)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener(object : MediaPlayer.OnPreparedListener {
                override fun onPrepared(mediaPlayer: MediaPlayer) {
                    //  alltime=mediaPlayer.getDuration()

                }
            })
            mediaPlayer.setOnCompletionListener(object : MediaPlayer.OnCompletionListener {
                override fun onCompletion(mp: MediaPlayer?) {
                    if (mp != null) {
                        mp!!.reset()
                        mediaPlayer.setDataSource(soundUrlDict)
                        mediaPlayer.prepareAsync()
                        isPlay = false
                        imageAdudio.setImageResource(R.mipmap.zxxq_video)
                    }
                    //"onCompletion: play sound."
                }
            })

        } catch (e1: IOException) {

        }

    }

    /**
     * 显示弹窗
     */
    private fun showCommentPopup() {
        if (addCommentPopFragment == null) {
            addCommentPopFragment =
                ProviderAddCommentFragment.newInstance(contentId ?: "", ResourceType.CONTENT)
        }
        if (!addCommentPopFragment!!.isAdded) {
            addCommentPopFragment?.show(supportFragmentManager, "story_add_comment")
        }
    }


    fun pauseMediaPlayer() {
        try {
            if (isPlay) {
                isPlay = false
                mediaPlayer.pause()
                mediaPlayer.stop()
                mediaPlayer.release()
                imageAdudio.setImageResource(R.mipmap.zxxq_video)
            }
        } catch (e: Exception) {
        }
    }

    override fun onResume() {
        super.onResume()
        while (mBinding.mvContentInfo.canGoBack()) {
            mBinding.mvContentInfo.goBack()
        }
    }

    override fun onPause() {
        super.onPause()
        pauseMediaPlayer()
        JCVideoPlayer.releaseAllVideos()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        pauseMediaPlayer()
        JCVideoPlayer.releaseAllVideos()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRecordEvent(event: UpdateCommentEvent) {
        page=1
        mModel.getActivityCommentList(id, ResourceType.CONTENT, page, pageSize)
    }

}