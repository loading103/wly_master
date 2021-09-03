package com.daqsoft.travelCultureModule.story

import android.content.Intent
import android.text.Html
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.*
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.adapter.GridStoryAdapter
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.businessview.event.UpdateCommentEvent
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.businessview.fragment.ProviderAddCommentFragment
import com.daqsoft.provider.commentModule.CommentAdapter
import com.daqsoft.provider.network.home.bean.NoPassResourceBean
import com.daqsoft.provider.network.home.bean.NoPassWordBean
import com.daqsoft.provider.utils.HtmlUtils
import com.daqsoft.provider.view.BaseDialog
import com.daqsoft.provider.view.popupwindow.SharePopWindow
import com.daqsoft.travelCultureModule.story.utils.WindowUtils
import com.daqsoft.travelCultureModule.story.vm.StrategyDetailActivityViewModel
import com.jakewharton.rxbinding2.view.RxView
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard
import me.nereo.multi_image_selector.BigImgActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.toast
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * @Description 攻略详情
 * @ClassName   HotActivityDetailActivity
 * @Author      PuHua
 * @Time        2019/12/23 17:28
 */
@Route(path = MainARouterPath.MAIN_STRATEGY_DETAIL)
class StrategyDetailActivity : TitleBarActivity<MainStragegyDetailBinding, StrategyDetailActivityViewModel>() {

    @JvmField
    @Autowired
    var id: String = ""

    /**
     * 根据类型不同调用不同的取详情的接口
     * 1取功能接口 2取我的故事接口
     */
    @JvmField
    @Autowired
    var type = 1

    //删除对话框
    var deleteDialog: BaseDialog? = null

    //添加评论弹窗
    private var commentPopup: PopupWindow? = null

    /**
     * 分享弹窗
     */
    var sharePopWindow: SharePopWindow? = null

    //    private var commentPopup: PopupWindow? = null
    private var addCommentPopFragment: ProviderAddCommentFragment? = null

    override fun getLayout(): Int = R.layout.main_stragegy_detail

    override fun setTitle(): String = getString(R.string.home_strategy_detail_title)

    override fun injectVm(): Class<StrategyDetailActivityViewModel> =
        StrategyDetailActivityViewModel::class.java

    private val storyAdapter by lazy { GridStoryAdapter(this, GridStoryAdapter.ARENA) }

    private var homeStoryBean: HomeStoryBean? = null

    /**
     * 点赞人数列表适配器
     */
    private val avatarAdapter = object
        : RecyclerViewAdapter<ItemAvataryBinding, ThumbBean>(R.layout.item_avatary) {
        override fun setVariable(mBinding: ItemAvataryBinding, position: Int, item: ThumbBean) {
//            mBinding.url = item.headUrl
                Glide.with(mBinding.root.context)
                    .load(item.headUrl)
                    .placeholder(R.mipmap.mine_profile_photo_default)
                    .error(R.mipmap.mine_profile_photo_default)
                    .into(mBinding.image)
        }

    }

    /**
     * 内容适配器
     */
    private val contentAdapter = object
        :
        RecyclerViewAdapter<ItemStrategyContentBinding, StrategyDetail>(R.layout.item_strategy_content) {
        override fun setVariable(
            mBinding: ItemStrategyContentBinding, position: Int, item:
            StrategyDetail
        ) {
            mBinding.tvTitle.paint.isFakeBoldText = true
            mBinding.strategy = item
            mBinding.llcVideo.visibility = View.GONE
            mBinding.tvContent.visibility = View.GONE
            mBinding.llImage.visibility = View.GONE
            if (item.title.isNullOrEmpty()) {
                mBinding.tvTitle.visibility = View.GONE
            } else {
                mBinding.tvTitle.visibility = View.VISIBLE
            }
            // CONTENT：内容 IMAGE：图片 VIDEO:视频 使用引用Constant
            when (item.contentType) {
                "CONTENT" -> {
                    mBinding.tvContent.visibility = View.VISIBLE
                    mBinding.tvContent.text = HtmlUtils.html2Str(item.content)
                }
                "IMAGE" -> {
                    mBinding.llImage.visibility = View.VISIBLE
                    mBinding.image.onNoDoubleClick {
                        val intent = Intent(this@StrategyDetailActivity, BigImgActivity::class.java)
                        intent.putExtra("position", 0)
                        val list = ArrayList<String>()
                        list.add(item.content)
                        intent.putStringArrayListExtra("imgList", list)
                        startActivity(intent)
                    }
                    mBinding.tvImageInto.text = "${Html.fromHtml(item.introduction)}"
                }
                "VIDEO" -> {
                    mBinding.llcVideo.visibility = View.VISIBLE
                    mBinding.vStarategyVideo.removeAllViews()
                    var videoUrl = item.content
                    var imageView: fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard =
                        fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard(this@StrategyDetailActivity)
                    var width = resources.displayMetrics.widthPixels
                    var new_width = width - resources.getDimension(R.dimen.dp_20) * 2
                    var new_hight = (new_width * 9 / 16)
                    var param = LinearLayout.LayoutParams(new_width.toInt(), new_hight.toInt(), 1F)
                    imageView.layoutParams = param as ViewGroup.LayoutParams?
                    imageView.setUp(videoUrl, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
                    // 异步加载视频缩略图
                    Glide.with(this@StrategyDetailActivity)
                        .load(StringUtil.getVideoCoverUrl(videoUrl))
                        .into(imageView.thumbImageView)
                    mBinding.vStarategyVideo.addView(imageView, 0)
                    mBinding.tvVideoInfo.text = Html.fromHtml(item.introduction ?: "")
                }
            }
        }

    }


    override fun initCustomTitleBar(mTitleBar: TitleBar) {
        showShareButton(com.daqsoft.provider.R.mipmap.common_nav_button_share)
        setShareClick(View.OnClickListener {
            homeStoryBean?.let {
                if (sharePopWindow == null) {
                    sharePopWindow = SharePopWindow(this@StrategyDetailActivity)
                }
                val imageUrl = if (it.images.isNullOrEmpty()) "" else it.images[0]
                // 设置分享数据
                var content= Constant.SHARE_DEC
                if(!TextUtils.isEmpty(it.content)){
                    content=it.content
                }
                sharePopWindow?.setShareContent(
                    it.title, content, imageUrl,
                    ShareModel.getStrateDetailUrl(id)
                )
                if (!sharePopWindow!!.isShowing) {
                    sharePopWindow?.showAsDropDown(mTitleBar)
                }
            }
        })
    }


    /**
     * 简单话题适配器
     */
    private val topicAdapter = object : RecyclerViewAdapter<ItemSimpleTopicBinding, SimpleTopic>(
        R.layout
            .item_simple_topic
    ) {
        override fun setVariable(
            mBinding: ItemSimpleTopicBinding,
            position: Int,
            item: SimpleTopic
        ) {
            mBinding.name = item.topicName
        }
    }

    override fun initView() {
        mBinding.vm = mModel
        EventBus.getDefault().register(this)
        mBinding.rvContent.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        contentAdapter.emptyViewShow = false
        mBinding.rvContent.adapter = contentAdapter
        mModel.storyDetail.observe(this, Observer {
            mBinding.vMainStragegyDetail.visibility = View.VISIBLE
            topicAdapter.add(it.topicInfo as MutableList<SimpleTopic>)
            homeStoryBean = it
            if (!it.sourceUrl.isNullOrEmpty() && it.sourceUrl != "undefined") {
                mBinding.tvReadTheOrigninal.visibility = View.VISIBLE
                mBinding.tvReadTheOrigninal.onNoDoubleClick {
                    ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
                        .withString("html", it.sourceUrl)
                        .navigation()
                }
            } else {
                mBinding.tvReadTheOrigninal.visibility = View.GONE
            }

            if (!it.vipHead.isNullOrEmpty()) {
                Glide.with(this).load(it.vipHead).placeholder(R.mipmap.mine_profile_photo_default)
                    .into(mBinding.ivUser)
            }
            if (type == 2 || it.showNum.toInt() == 0) {
                mBinding.tvViewNumber.visibility = View.GONE
            }
            if (type == 2 || it.likeNum == 0) {
                mBinding.rvThumb.visibility = View.GONE
                mBinding.tvThumbNumber.visibility = View.GONE
            }
            updateLikeUi()
            updateCollectUi()
            if (it.cover.isNotEmpty()) {
                Glide.with(mBinding.imgStrategyCover!!.context).load(it.cover)
                    .placeholder(com.daqsoft.provider.R.mipmap.placeholder_img_fail_240_180)
                    .into(mBinding.imgStrategyCover!!)
            } else {
                mBinding.imgStrategyCover.visibility = View.GONE
            }
            if (it.strategyDetail != null) {
                contentAdapter.add(it.strategyDetail as MutableList<StrategyDetail>)
                contentAdapter.notifyDataSetChanged()
            }
            mBinding.tvContent.settings.javaScriptEnabled = true
            mBinding.tvContent.loadDataWithBaseURL(
                null,
                StringUtil.getHtml(it.content),
                "text/html",
                "utf-8",
                null
            )
            if (it.auditStatus == "4") {
                hideContent()
                mBinding.tvStatus.text = getString(R.string.home_story_status_valid)
            } else if (it.auditStatus == "79") {
                hideContent()
                if (it.auditType == "machine") {
                    mBinding.tvStatus.text = getString(R.string.home_story_status_valid_machine)
                } else {
                    mBinding.tvStatus.text =
                        getString(R.string.home_story_status_valid_human, it.auditResult)

                    RxView.clicks(mBinding.tvStatus)
                        .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                            run {
                                showFailedReasonWindow()
                            }
                        }
                }
            } else {
                mBinding.tvStatus.visibility = View.GONE
            }
            if (it.status == "3") {
                hideContent()
                mBinding.llBottomDel.visibility = View.VISIBLE
            }
        })

        mBinding.rvThumb.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        avatarAdapter.emptyViewShow = false
        mBinding.rvThumb.adapter = avatarAdapter

        mModel.thumbList.observe(this, Observer {
            avatarAdapter.add(it)
        })

        // 评论
        mBinding.rvComments.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val commentAdapter = CommentAdapter(this)
        commentAdapter.isShowAll = false
        commentAdapter.emptyViewShow = false
        mBinding.rvComments.adapter = commentAdapter
        mModel.commentBeans.observe(this, Observer {
            if (it.size > 0) {
                try {
                    commentAdapter.clear()
                    commentAdapter.add(it)
                }catch (e:Exception){
                    commentAdapter.add(it)
                }
            }
            mBinding.tvCommentNum.text = if (!it.isNullOrEmpty()) {
                getString(R.string.country_comment)
            } else {
                var commentNum = it.size
                if (commentNum > 0) {
                    "" + it.size
                } else {
                    getString(R.string.country_comment)
                }
            }
        })
        mModel.storyList.observe(this, Observer {
            storyAdapter!!.add(it)
        })

        mModel.like.observe(this, Observer {
            homeStoryBean!!.vipResourceStatus.likeStatus = true
            var num = homeStoryBean!!.likeNum.toInt()
            homeStoryBean!!.likeNum = num + 1
            updateLikeUi()
        })
        mModel.cancelLike.observe(this, Observer {
            homeStoryBean!!.vipResourceStatus.likeStatus = false
            if (homeStoryBean!!.likeNum.toInt() > 0) {
                var num = homeStoryBean!!.likeNum.toInt()
                homeStoryBean!!.likeNum = num - 1
            }
            updateLikeUi()
        })
        mModel.collect.observe(this, Observer {
            homeStoryBean!!.vipResourceStatus.collectionStatus = true
            var num = homeStoryBean!!.collectionNum
            homeStoryBean!!.collectionNum = num + 1
            updateCollectUi()
        })

        mModel.cancelCollect.observe(this, Observer {
            homeStoryBean!!.vipResourceStatus.collectionStatus = false
            if (homeStoryBean!!.collectionNum.toInt() > 0) {
                var num = homeStoryBean!!.collectionNum
                homeStoryBean!!.collectionNum = num - 1
            }
            updateCollectUi()
        })

        // 故事
        val storyLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        mBinding.rvMore.layoutManager = storyLayoutManager
        storyAdapter!!.emptyViewShow = false
        mBinding.rvMore.adapter = storyAdapter

        // 话题
        mBinding.rvTopic.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL,
            false
        )

        topicAdapter.emptyViewShow = false
        mBinding.rvTopic.adapter = topicAdapter
        initEvent()
        initDeleteDialog()
    }

    // 审核状态隐藏显示内容
    private fun hideContent() {
        mBinding.llBottomBtn.visibility = View.GONE
        mBinding.tvMore.visibility = View.GONE
        mBinding.rvMore.visibility = View.GONE
        mBinding.vLine.visibility = View.GONE
        mBinding.clViewNum.visibility = View.GONE
    }

    /**
     * 修改textView顶部图片
     */
    private fun changeTvImage(v: TextView, image: Int) {
        val topDrawable = getDrawable(image)
        topDrawable?.setBounds(0, 0, topDrawable.minimumWidth, topDrawable.minimumHeight)
        v.setCompoundDrawables(null, topDrawable, null, null)
    }

    private fun updateLikeUi() {
        mBinding.tvLike.text = homeStoryBean!!.likeNum.toString()
        mBinding.tvThumbNumber.text = homeStoryBean!!.likeNum.toString()
        if (homeStoryBean!!.vipResourceStatus.likeStatus) {
            changeTvImage(mBinding.tvLike, R.mipmap.bottom_icon_like_selected)
        } else {
            changeTvImage(mBinding.tvLike, R.mipmap.bottom_icon_like_normal)
        }
    }

    private fun updateCollectUi() {
        mBinding.tvCollect.text = homeStoryBean!!.collectionNum.toString()
        if (homeStoryBean!!.vipResourceStatus.collectionStatus) {
            changeTvImage(mBinding.tvCollect, R.mipmap.bottom_icon_collect_selected)
        } else {
            changeTvImage(mBinding.tvCollect, R.mipmap.bottom_icon_collect_normal)
        }
    }

    /**
     * 初始化事件
     */
    private fun initEvent() {
        mBinding.imgStrategyCover.onNoDoubleClick {
            val intent =
                Intent(this@StrategyDetailActivity, BigImgActivity::class.java)
            intent.putExtra("position", 0)
            intent.putStringArrayListExtra(
                "imgList",
                ArrayList(listOf(mModel.storyDetail.value?.cover))
            )
            startActivity(intent)
        }

        mBinding.tvLike.onNoDoubleClick {
            if (gotoLogin()) {
                showLoadingDialog()
                if (homeStoryBean?.vipResourceStatus?.likeStatus!!) {
                    mModel.cancelLike(id)
                } else {
                    mModel.addLike(id)
                }
            }
        }
        mBinding.tvCollect.onNoDoubleClick {
            if (gotoLogin()) {
                showLoadingDialog()
                if (!homeStoryBean?.vipResourceStatus?.collectionStatus!!) {
                    mModel.collect(id)
                } else {
                    mModel.cancelCollect(id)
                }
            }
        }
        mBinding.tvCommentNum.onNoDoubleClick {
            if (gotoLogin()) {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_COMMENT_ADD)
                    .withString("id", id)
                    .withString("type", ResourceType.CONTENT_TYPE_STORY)
                    .navigation()
            }
        }
        mBinding.tvAddComment.onNoDoubleClick {
            if (gotoLogin()) {
                showCommentPopup()
            }
        }
        mBinding.tvDel.onNoDoubleClick {
            deleteDialog?.show()
        }
        mBinding.tvEdit.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_STORY_STRATEGY_ADD)
                .withInt("type", 1)
                .withString("id", homeStoryBean?.id)
                .navigation()
            finish()
        }
    }

    private fun initDeleteDialog() {
        deleteDialog = BaseDialog(this)
        deleteDialog!!.contentView(R.layout.dialog_delete_story)
            .layoutParams(
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
            .gravity(Gravity.CENTER)
            .animType(BaseDialog.AnimInType.BOTTOM)
            .canceledOnTouchOutside(false)
        deleteDialog!!.findViewById<TextView>(R.id.tv_title).text = "删除攻略"
        deleteDialog!!.findViewById<TextView>(R.id.tv_content).text = "攻略删除后不可恢复，确认删除？"
        deleteDialog!!.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            deleteDialog!!.dismiss()
        }
        deleteDialog!!.findViewById<TextView>(R.id.tv_query).setOnClickListener {
            mModel.deleteStory(id)
            deleteDialog!!.dismiss()
            showLoadingDialog()
        }
    }

    override fun onPause() {
        super.onPause()
        JCVideoPlayer.releaseAllVideos()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        JCVideoPlayer.releaseAllVideos()
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

    override fun initData() {
        if (type == 1) {
            mModel.getHotStoryTagDetail(id)
        } else {
            mModel.getMineStoryDetail(id)
            mBinding.llBottomBtn.visibility = View.GONE
        }
        mModel.getThumbList(id, ResourceType.CONTENT_TYPE_STORY)
        mModel.getActivityCommentList(id)

        mModel.getHotStoryList(id)
    }

    fun gotoCommentPage(v: View) {

        ARouter.getInstance()
            .build(MainARouterPath.MAIN_ACTIVITY_COMMENT)
            .withString("id", id)
            .withString("type", ResourceType.CONTENT_TYPE_STORY)
            .navigation()
    }


    /**
     * 显示弹窗
     */
    private fun showCommentPopup() {
        if (addCommentPopFragment == null) {
            addCommentPopFragment =
                ProviderAddCommentFragment.newInstance(id, ResourceType.CONTENT_TYPE_STORY)
        }
        if (!addCommentPopFragment!!.isAdded) {
            addCommentPopFragment?.show(supportFragmentManager, "story_add_comment")
        }
    }

    /**
     * 隐藏弹窗
     */
    private fun dismissPopup() {
//        commentPopup?.dismiss()
//        commentPopup = null
    }

    /**
     * 展示审核不通过的原因弹框
     */
    private fun showFailedReasonWindow() {
        mModel.getNoPassMsg(id)
    }

    override fun notifyData() {
        super.notifyData()
        mModel.noPassMsgBean.observe(this, Observer {
            var noPassWordBean = NoPassResourceBean(
                "http://file.geeker.com.cn/uploadfile/cultural-tourism-cloud/1570158708754/AAFF7B06-CFF8-41EE-8440-56356A88E8D9.jpeg",
                "暴力图片，接口实际付款时间反馈数据数据库福建省开发技术可大方"
            )
            var wordBean = NoPassWordBean("分子暴力、、、、开始了打卡了", "恐怕")
            it.textList.add(wordBean)
            it.textList.add(wordBean)
            it.textList.add(wordBean)
            it.imageList.add(noPassWordBean)
            it.imageList.add(noPassWordBean)
            it.imageList.add(noPassWordBean)
            it.videoList.add(noPassWordBean)
            it.videoList.add(noPassWordBean)
            it.voiceList.add(noPassWordBean)
            WindowUtils.initStoryFailedReasonWindow(this, it)
                .showAtLocation(mBinding.root, Gravity.CENTER, 0, 0)
        })

        mModel.dismissPop.observe(this, Observer {
            dismissPopup()
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })
        mModel.dofinish.observe(this, Observer {
            dissMissLoadingDialog()
        })
        mModel.deleteFinish.observe(this, Observer {
            dissMissLoadingDialog()
            finish()
        })
    }

    override fun onBackPressed() {
        // 未处理列表backpress
        super.onBackPressed()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRecordEvent(event: UpdateCommentEvent) {
        // 评论
        mModel.getActivityCommentList(id)
    }
}
