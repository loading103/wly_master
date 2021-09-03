package com.daqsoft.travelCultureModule.story

import android.annotation.SuppressLint
import android.content.Intent
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.daqsoft.baselib.base.*
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.*
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.businessview.fragment.ProviderAddCommentFragment
import com.daqsoft.provider.commentModule.CommentAdapter
import com.daqsoft.provider.network.home.bean.NoPassResourceBean
import com.daqsoft.provider.network.home.bean.NoPassWordBean
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.provider.view.BaseDialog
import com.daqsoft.provider.view.convenientbanner.bean.VideoImageBean
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.convenientbanner.holder.VideoImageHolder
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import com.daqsoft.provider.view.popupwindow.SharePopWindow
import com.daqsoft.provider.adapter.GridStoryAdapter
import com.daqsoft.provider.businessview.event.UpdateCommentEvent
import com.daqsoft.travelCultureModule.story.utils.WindowUtils
import com.daqsoft.travelCultureModule.story.vm.StoryDetailActivityViewModel
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.main_story_detail.view.*
import me.nereo.multi_image_selector.BigImgActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.toast
import java.lang.StringBuilder
import java.util.ArrayList
import java.util.concurrent.TimeUnit


/**
 * @Description 故事详情页面
 * @ClassName   HotActivityDetailActivity
 * @Author      PuHua
 * @Time        2019/12/23 17:28
 */
@Route(path = MainARouterPath.MAIN_STORY_DETAIL)
class StoryDetailActivity : TitleBarActivity<MainStoryDetailBinding, StoryDetailActivityViewModel>() {

    @JvmField
    @Autowired
    var id: String = ""

    /**
     * 根据类型不同调用不同的取详情的接口
     * 1取公共故事接口 2取我的故事接口
     */
    @JvmField
    @Autowired
    var type = 1

    var storyDetail:HomeStoryBean? = null
    //添加评论弹窗
    private var commentPopup: PopupWindow? = null

    private var addCommentPopFragment: ProviderAddCommentFragment? = null

    override fun getLayout(): Int = R.layout.main_story_detail

    override fun setTitle(): String = getString(R.string.home_story_detail)
//    private val storyImageAdapter = StoryImageAdapter()

    override fun injectVm(): Class<StoryDetailActivityViewModel> = StoryDetailActivityViewModel::class.java

    private val storyAdapter by lazy { GridStoryAdapter(this,GridStoryAdapter.ARENA) }


    //删除对话框
    var deleteDialog: BaseDialog? = null

    /**
     * 分享弹窗
     */
    var sharePopWindow: SharePopWindow? = null


    /**
     * 点赞人数列表适配器
     */
    private val avatarAdapter = object
        : RecyclerViewAdapter<ItemAvataryBinding, ThumbBean>(R.layout.item_avatary) {
        override fun setVariable(mBinding: ItemAvataryBinding, position: Int, item: ThumbBean) {
            Glide.with(this@StoryDetailActivity)
                .load(item.headUrl)
                .placeholder(R.mipmap.mine_profile_photo_default)
                .into(mBinding.image)
        }

    }

    /**
     * 简单话题适配器
     */
    private val topicAdapter = object : RecyclerViewAdapter<ItemSimpleTopicBinding, SimpleTopic>(
        R.layout.item_simple_topic
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
//        mBinding.xGallery.setAdapter(storyImageAdapter)
        mModel.storyDetail.observe(this, Observer {
            //            storyImageAdapter.menus.addAll(it.images)
//            storyImageAdapter.notifyDataSetChanged()
            setStoryImg(it)
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
            storyDetail = it
            mBinding.tvLike.text = it.likeNum.toString()
            mBinding.tvCollect.text = it.collectionNum.toString()
//            mBinding.tvCommentNum.text = it.commentNum
            mBinding.tvContent.loadDataWithBaseURL(null, it.content, "text/html", "utf-8", null)
            if (type == 2 || it.showNum.toInt() == 0) {
                mBinding.tvViewNumber.visibility = View.GONE
            }
            if (type == 2 || it.likeNum == 0) {
                mBinding.rvThumb.visibility = View.GONE
                mBinding.tvThumbNumber.visibility = View.GONE
            }
            mBinding.tvThumbNumber.text = it.likeNum.toString()
            if (!it.vipHead.isNullOrEmpty()) {
                Glide.with(this).load(it.vipHead).placeholder(R.mipmap.mine_profile_photo_default)
                    .into(mBinding.ivUser)
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
                val text = DividerTextUtils.convertDotString(it.resourceCompleteRegionName.split(","))
                mBinding.tvIntroduce.text = text
                if (!it.consumePerson.isNullOrEmpty()){
                    val sb = StringBuilder()
                    sb.append(text)
                    sb.append("  |  ")
                    sb.append("人均消费${it.consumePerson}")
                    mBinding.tvIntroduce.text = sb.toString()
                }
            }
            if (!it.resourceRegionName.isNullOrEmpty()) {
                var tags: ArrayList<String> = ArrayList<String>()
                var regions = it.resourceRegionName.split(",")
                if (!regions.isNullOrEmpty()) {
                    tags.addAll(regions)
                }
                if (!it.resourceType.isNullOrEmpty()) {
                    var typeName: String? = ResourceType.getName(it.resourceType)
                    if (!typeName.isNullOrEmpty()) {
                        tags.add(typeName)
                    }
                }
                if (!tags.isNullOrEmpty()) {
                    mBinding.tvLocation.text = DividerTextUtils.convertDotString(tags)
                    mBinding.tvLocation.visibility=View.VISIBLE
                }else{
                    mBinding.tvLocation.visibility=View.GONE
                }
            }else{
//                var tags: ArrayList<String> = ArrayList<String>()
//                if (!it.resourceType.isNullOrEmpty()) {
//                    var typeName: String? = ResourceType.getName(it.resourceType)
//                    if (!typeName.isNullOrEmpty()) {
//                        tags.add(typeName)
//                    }
//                }
//                if (!tags.isNullOrEmpty()) {
//                    mBinding.tvLocation.text = DividerTextUtils.convertDotString(tags)
//                    mBinding.tvLocation.visibility=View.VISIBLE
//                }else{
//                    mBinding.tvLocation.visibility=View.GONE
//                }
            }
            if(TextUtils.isEmpty(it.resourceTypeName) &&  TextUtils.isEmpty(it.resourceImage) &&TextUtils.isEmpty(it.resourceName) && TextUtils.isEmpty(mBinding.tvIntroduce.text) ){
                mBinding.cvItem.visibility=View.GONE
            }else{
                mBinding.cvItem.visibility=View.VISIBLE
            }

            topicAdapter.clear()
            topicAdapter.add(it.topicInfo as MutableList<SimpleTopic>)
            mBinding.tvViewNumber.text =
                String.format(getString(R.string.home_story_show_number), it.showNum)
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

                if (it.status == "3") {
                    hideContent()
                    mBinding.llBottomDel.visibility = View.VISIBLE
                }
                mBinding.tvStatus.visibility = View.GONE
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
                try {
                    commentAdapter.clear()
                    commentAdapter.add(it)
                }catch (e:Exception){
                    commentAdapter.add(it)
                }

            }
        })
        mModel.storyList.observe(this, Observer {
            storyAdapter!!.add(it)
        })

        // 故事
        val storyLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        storyAdapter!!.emptyViewShow = false
        mBinding.rvMore.layoutManager = storyLayoutManager
        mBinding.rvMore.adapter = storyAdapter


        // 话题
        topicAdapter.emptyViewShow = false
        mBinding.rvTopic.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL,
            false
        )
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
        mBinding.tvViewNumber.visibility = View.GONE
        mBinding.tvThumbNumber.visibility = View.GONE
    }


    override fun initData() {
        mModel.getThumbList(id, ResourceType.CONTENT_TYPE_STORY)
        mModel.getActivityCommentList(id)
        mModel.getHotStoryList(id)
    }



    override fun onResume() {
        super.onResume()
        if (type == 1) {
            mModel.getHotStoryTagDetail(id)
        } else {
//            mBinding.llBottomBtn.visibility = View.GONE
            mModel.getMineStoryDetail(id)
        }
    }


    override fun initCustomTitleBar(mTitleBar: TitleBar) {
        showShareButton(com.daqsoft.provider.R.mipmap.common_nav_button_share)
        setShareClick(View.OnClickListener {
            storyDetail?.let {
                if (sharePopWindow == null) {
                    sharePopWindow = SharePopWindow(this@StoryDetailActivity)
                }
                val imageUrl = if(it.images.isNullOrEmpty())"" else it.images[0]
                // 设置分享数据
                var content= Constant.SHARE_DEC
                if(!TextUtils.isEmpty(it.content)){
                    content=it.content
                }
                sharePopWindow?.setShareContent(
                    it.title, content, imageUrl,
                    ShareModel.getStoryDetailUrl(id)
                )
                if (!sharePopWindow!!.isShowing) {
                    sharePopWindow?.showAsDropDown(mTitleBar)
                }
            }
        })
    }

    /**
     * 修改textView顶部图片
     */
    private fun changeTvImage(v: TextView, image: Int) {
        val topDrawable = getDrawable(image)
        topDrawable?.setBounds(0, 0, topDrawable.minimumWidth, topDrawable.minimumHeight)
        v.setCompoundDrawables(null, topDrawable, null, null)
    }


    private fun setStoryImg(it: HomeStoryBean) {
        if (it.images.isNullOrEmpty() && it.video.isNullOrEmpty()) {
            mBinding.cbannerStoryDetail.visibility = View.GONE
            mBinding.vStoryDetailIndex.visibility = View.GONE
        } else {
            mBinding.vStoryDetailIndex.visibility = View.VISIBLE
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

            mBinding.cbannerStoryDetail.setPages(object : CBViewHolderCreator {
                override fun createHolder(itemView: View?): Holder<*> {
                    videoImageHolder=VideoImageHolder(itemView!!, this@StoryDetailActivity)
                    return VideoImageHolder(itemView!!, this@StoryDetailActivity)
                }

                override fun getLayoutId(): Int {
                    return R.layout.layout_video_image
                }
            }, imagesAndVideo).setCanLoop(false).setPointViewVisible(false).setOnItemClickListener {
                val intent =
                    Intent(this@StoryDetailActivity, BigImgActivity::class.java)
                intent.putExtra("position", it)
                intent.putStringArrayListExtra(
                    "imgList",
                    ArrayList(images)
                )
                startActivity(intent)
            }
            mBinding.cbannerStoryDetail?.setOnPageChangeListener(object : OnPageChangeListener {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                }

                override fun onPageSelected(index: Int) {
                    mBinding?.txtCurrentIndex.text = ((index + 1).toString())
                    mBinding?.txtTotalSize.text = "/${total}"
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                }

            })
        }
    }

    fun initVideoData(type: Int, path: String): VideoImageBean {
        var video: VideoImageBean = VideoImageBean()
        video.type = type
        if (type == 1) {
            video.videoUrl = path
        } else {
            video.imageUrl = path
        }
        return video
    }

    /**
     * 初始化事件
     */
    @SuppressLint("CheckResult")
    private fun initEvent() {
        mBinding.tvLike.onNoDoubleClick {
            if (gotoLogin()) {
                showLoadingDialog()
                if (mModel.storyDetail.value?.vipResourceStatus?.likeStatus!!) {
                    mModel.cancelLike(id)
                } else {
                    mModel.addLike(id)
                }
            }
        }
        mBinding.tvCollect.onNoDoubleClick {
            if (gotoLogin()) {
                showLoadingDialog()
                if (!mModel.storyDetail.value?.vipResourceStatus?.collectionStatus!!) {
                    mModel.collect(id)
                } else {
                    mModel.cancelCollect(id)
                }
            }
        }
//        mBinding.tvCommentNum.onNoDoubleClick {
//            if(gotoLogin()){
//                ARouter.getInstance()
//                    .build(MainARouterPath.MAIN_COMMENT_ADD)
//                    .withString("id", id)
//                    .withString("type",ResourceType.CONTENT_TYPE_STORY)
//                    .navigation()
//            }
//        }
        mBinding.tvAddComment.onNoDoubleClick {
            if (gotoLogin()) {
//                initCommentPopup()
                showCommentPopup()
            }
        }
        mBinding.tvDel.onNoDoubleClick {
            deleteDialog?.show()
        }
        mBinding.tvEdit.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_STORY_STRATEGY_ADD)
                .withInt("type", 0)
                .withString("id", id)
                .navigation()
            finish()
        }
        mBinding.tvResourceName.onNoDoubleClick {
            gotoResource()
        }
        mBinding.clResource.onNoDoubleClick {
            gotoResource()
        }
        mBinding.tvTag.onNoDoubleClick {
            gotoResource()
        }
    }

    private fun gotoResource() {
        val type = mModel.storyDetail.value?.resourceType
        val id = mModel.storyDetail.value?.resourceId
        if (type != null && id != null) {
            MenuJumpUtils.gotoResourceDetail(type, id)
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
        deleteDialog!!.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            deleteDialog!!.dismiss()
        }
        deleteDialog!!.findViewById<TextView>(R.id.tv_query).setOnClickListener {
            mModel.deleteStory(id)
            deleteDialog!!.dismiss()
            showLoadingDialog()
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
    private var   videoImageHolder:VideoImageHolder ?= null
    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        if(videoImageHolder!=null){
            (videoImageHolder as VideoImageHolder).release()
        }
    }

    /**
     * 初始化添加评论弹窗
     */
    private fun initCommentPopup() {
        if (commentPopup == null) {
            commentPopup = Utils.initPopupWindow(
                this@StoryDetailActivity,
                0.8f,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val popupViewBinding = DataBindingUtil.inflate<LayoutAddCommentPopupBinding>(
                LayoutInflater.from(this@StoryDetailActivity),
                R.layout.layout_add_comment_popup,
                null,
                false
            )
            popupViewBinding.etContent.text.clear()
            popupViewBinding.btnSend.onNoDoubleClick {
                val content = popupViewBinding.etContent.text.toString()
                if (content.isNullOrEmpty()) {
                    toast("请输入评论内容!")
                } else {
                    mModel.publishComment(id, content)
                }
            }
            commentPopup!!.contentView = popupViewBinding.root
            commentPopup!!.animationStyle = R.style.AnimBottom
            commentPopup!!.setOnDismissListener {
                Utils.setWindowBackGroud(this@StoryDetailActivity, 1f)
                commentPopup = null
            }
        }
    }

    /**
     * 显示弹窗
     */
    private fun showCommentPopup() {
//        commentPopup?.showAtLocation(mBinding.root, Gravity.BOTTOM, 0, 0)
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
        commentPopup?.dismiss()
        commentPopup = null
    }

    fun gotoCommentPage(v: View) {

        ARouter.getInstance()
            .build(MainARouterPath.MAIN_ACTIVITY_COMMENT)
            .withString("id", id)
            .withString("type", ResourceType.CONTENT_TYPE_STORY)
            .navigation()
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRecordEvent(event: UpdateCommentEvent) {
        // 评论
        mModel.getActivityCommentList(id)
    }
}