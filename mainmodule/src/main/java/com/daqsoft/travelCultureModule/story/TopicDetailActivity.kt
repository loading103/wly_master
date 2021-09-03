package com.daqsoft.travelCultureModule.story

import android.annotation.SuppressLint
import android.os.Build
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.*
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityTopicDetailBinding
import com.daqsoft.mainmodule.databinding.TopicRulePopupBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.bean.HomeTopicBean
import com.daqsoft.provider.bean.VipResourceStatus
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.provider.utils.HtmlUtils
import com.daqsoft.provider.view.popupwindow.SharePopWindow
import com.daqsoft.travelCultureModule.story.bean.TopicBean
import com.daqsoft.provider.adapter.GridStoryAdapter
import com.daqsoft.travelCultureModule.story.story.StoryAdapter
import com.daqsoft.travelCultureModule.story.utils.TopicResourceType
import com.daqsoft.travelCultureModule.story.vm.TopicDetailViewModel
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.activity_topic_detail.*
import java.util.concurrent.TimeUnit

/**
 *@package:com.daqsoft.travelCultureModule.story
 *@date:2020/4/2 20:11
 *@author: caihj
 *@des:话题详情页面
 *
 *
 * 更新者    邓益千
 * 更新日期  2020年4月27日
 * 更新内容  1、将话题详情页的数据带过来
 *          2、获取评论时增加话题id参数
 **/
@Route(path = MainARouterPath.MAIN_TOPIC_DETAIL)
class TopicDetailActivity : TitleBarActivity<ActivityTopicDetailBinding, TopicDetailViewModel>() {

    @JvmField
    @Autowired
    var id: String = ""

    var isCollect: Boolean = false

    //添加规则弹窗
    private val rulePopup by lazy { initRulePopup() }

    /**话题详情实体类*/
    private lateinit var topic: TopicBean

    override fun getLayout(): Int = R.layout.activity_topic_detail

    override fun setTitle(): String = getString(R.string.home_topic_detail)

    /**
     * 分享弹窗
     */
    var sharePopWindow: SharePopWindow? = null

    override fun injectVm(): Class<TopicDetailViewModel> = TopicDetailViewModel::class.java
    private val storyAdapter by lazy { StoryAdapter(this) }

    private val storyGridAdapter by lazy { GridStoryAdapter(this, GridStoryAdapter.ARENA) }

    @SuppressLint("CheckResult", "SetTextI18n", "ResourceAsColor")
    override fun initView() {
//        mBinding.tvContent.apply {
//            maxLines = 2
//            setHasAnimation(true)
//            setCloseInNewLine(true)
//            setOpenSuffixColor(resources.getColor(R.color.white))
//            setCloseSuffixColor(resources.getColor(R.color.white))
//        }


        mBinding.tvContent.onNoDoubleClick {
            if (expandFlag) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mBinding.tvContent.text = Html.fromHtml(topicIntroduction, Html.FROM_HTML_MODE_COMPACT)
                } else {
                    mBinding.tvContent.text = Html.fromHtml(topicIntroduction)
                }
                mBinding.tvContentBg.visibility = View.VISIBLE
            }
        }

        mModel.storyList.observe(this, Observer {

            for (i in 0 until (it!!.size ?: 0)) {
                var data = it!![i]
                if (mBinding.tvNew.isSelected) {
                    storyGridAdapter!!.addItem(data)
                } else {
                    if (data.storyType == Constant.STORY_TYPE_STORY) {
                        storyAdapter!!.addViewType(R.layout.item_story_main)
                    } else if (data.storyType == Constant.STORY_TYPE_STRATEGY) {
                        storyAdapter!!.addViewType(R.layout.item_story_list_strategy)
                    }
                    storyAdapter!!.addItem(data)
                }
            }
            if (mBinding.tvNew.isSelected) {
                storyGridAdapter!!.notifyDataSetChanged()
                storyGridAdapter!!.loadEnd()
            } else {
                if (storyAdapter!!.itemCount == 0) {
                    v_topic_empty?.visibility = View.VISIBLE
                    if (BaseApplication.appArea == "sc") {
                        v_topic_empty.findViewById<ImageView>(R.id.empty_image).setImageResource(R.mipmap.common_empty)
                    }
                }
                storyAdapter!!.notifyDataSetChanged()
                storyAdapter!!.loadEnd()
            }
        })

        mModel.topicDetail.observe(this, Observer {
            if (it != null) {
                topic = it
                if (it.topicStatus != "1") {
                    mBinding.ivTopicEnd.visibility = View.VISIBLE
                    mBinding.tvTopicEnd.visibility = View.VISIBLE
                    mBinding.btnWriteStory.isEnabled = false
                    mBinding.btnWriteStory.background = getDrawable(R.drawable.home_story_write_btn_unable)
                    mBinding.btnWriteStory.text = getString(R.string.home_story_topic_label_end)
                } else if (it.topicStatus == "1") {
                    mBinding.ivTopicEnd.visibility = View.VISIBLE
                    mBinding.tvTopicEnd.visibility = View.VISIBLE
                    mBinding.btnWriteStory.isEnabled = true
                    mBinding.tvTopicEnd.text = "进行中"

                }
                if (!it.topicTypeName.isNullOrEmpty()) {
                    mBinding.ivTopicType.visibility = View.VISIBLE
                    mBinding.tvTopicType.visibility = View.VISIBLE
                    Glide.with(this!!).load(TopicResourceType.getTopicDetailTypeBgIcon(it.topicTypeName)).into(mBinding.ivTopicType)
                    mBinding.tvTopicType.text = it.topicTypeName
                }
                isCollect = it.vipResourceStatus.collectionStatus
                changeCollect(isCollect)

                mBinding.tvTitle.text = "#${it.name}"
                if (it.hot) {
                    val drawable = getDrawable(R.mipmap.home_ht_hot)
                    mBinding.tvTitle.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                } else {
                    mBinding.tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                }

                val numStr = mutableListOf<String>()
                if (!it.contentNum.isNullOrEmpty() && it.contentNum != "0") {
                    numStr.add("${it.contentNum}条内容")
                }
                if (!it.participateNum.isNullOrEmpty() && it.participateNum != "0") {
                    numStr.add("${it.participateNum}人参与")
                }
                if (!it.showNum.isNullOrEmpty() && it.showNum != "0") {
                    numStr.add("${it.showNum}次浏览")
                }

                if (!numStr.isNullOrEmpty())
                    mBinding.tvLocation.text = DividerTextUtils.convertDotString(numStr)

//                mBinding.tvContent.setOriginalText(HtmlUtils.html2Str(it.introduce))

                // 处理话题介绍

                topicIntroduction = it.introduce
                toggleEllipsize(2, HtmlUtils.html2Str(it.introduce), "展开", R.color.white, mBinding.tvContent)


                Glide.with(this).load(it.image).into(mBinding.ivCover)
                try {
                    mBinding.tvTime.text = "${DateUtil.formatDateByString("yyyy.MM.dd", "yyyy-MM-dd", it.startDate)} - " +
                            "${DateUtil.formatDateByString("yyyy.MM.dd", "yyyy-MM-dd", it.endDate)}"
                } catch (e: Exception) {
                }

            }
        })

        mModel.collectLiveData.observe(this, Observer {
            isCollect = true
            changeCollect(isCollect)
        })
        mModel.cancelCollectLiveData.observe(this, Observer {
            isCollect = false
            changeCollect(isCollect)
        })


        // 点击热门
        var d = RxView.clicks(mBinding.tvHot)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                val set = ConstraintSet()
                set.clone(mBinding.clStoryTypeSelect)

                if (!mBinding.tvHot.isSelected) {
                    mBinding.tvHot.isSelected = true
                    mBinding.tvNew.isSelected = false
                    set.connect(mBinding.vIndicator.id, ConstraintSet.LEFT, mBinding.tvHot.id, ConstraintSet.LEFT)
                    set.connect(mBinding.vIndicator.id, ConstraintSet.RIGHT, mBinding.tvHot.id, ConstraintSet.RIGHT)
                    set.applyTo(mBinding.clStoryTypeSelect)
                    switchStoryLayout()
                    mModel.getHotStoryList(id, mModel.TYPEHOT)
                }
            }
        // 点击最新
        RxView.clicks(mBinding.tvNew)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                val set = ConstraintSet()
                set.clone(mBinding.clStoryTypeSelect)

                if (!mBinding.tvNew.isSelected) {
                    mBinding.tvNew.isSelected = true

                    mBinding.tvHot.isSelected = false
                    set.connect(mBinding.vIndicator.id, ConstraintSet.LEFT, mBinding.tvNew.id, ConstraintSet.LEFT)
                    set.connect(mBinding.vIndicator.id, ConstraintSet.RIGHT, mBinding.tvNew.id, ConstraintSet.RIGHT)
                    set.applyTo(mBinding.clStoryTypeSelect)
                    switchStoryLayout(1)
                    mModel.getHotStoryList(id, "")
                }
            }


        // 故事
        val storyLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.rvStory.layoutManager = storyLayoutManager
        mBinding.rvStory.adapter = storyAdapter

        mBinding.tvTopicMain.onNoDoubleClick {
            ARouter.getInstance().build(MainARouterPath.MAIN_TOPIC_LIST)
                .navigation()
            finish()
        }
        mBinding.ivTopicCollect.onNoDoubleClick {
            if (AppUtils.isLogin()) {
            if (isCollect) {
                mModel.cancelCollect(id)
            } else {
                mModel.collect(id)
            }
        } else {
            ToastUtils.showMessage("非常抱歉，登录后才能访问~")
            ARouter.getInstance()
                .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                .navigation()
        }
        }
        mBinding.tvRule.onNoDoubleClick {
            showRulePop()
        }
    }


    override fun initCustomTitleBar(mTitleBar: TitleBar) {
        showShareButton(com.daqsoft.provider.R.mipmap.common_nav_button_share)
        setShareClick(View.OnClickListener {
            topic?.let {
                if (sharePopWindow == null) {
                    sharePopWindow = SharePopWindow(this@TopicDetailActivity)
                }
                // 设置分享数据
                var content= Constant.SHARE_DEC
                sharePopWindow?.setShareContent(
                    it.name, content, it.image.getRealImages(),
                    ShareModel.getTopicDetailUrl(id)
                )
                if (!sharePopWindow!!.isShowing) {
                    sharePopWindow?.showAsDropDown(mTitleBar)
                }
            }
        })
    }

    /**
     * 修改收藏状态
     */
    private fun changeCollect(isCollect: Boolean) {
        if (isCollect) {
            mBinding.ivTopicCollect.setImageResource(R.mipmap.provider_collect_selected)
        } else {
            mBinding.ivTopicCollect.setImageResource(R.mipmap.provider_collect_normal)
        }
    }

    /**
     * 初始化规则弹窗
     */
    private fun initRulePopup(): PopupWindow {
        val popUpWindow = Utils.initPopupWindow(
            this@TopicDetailActivity,
            0.8f,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val popupViewBinding =
            DataBindingUtil.inflate<TopicRulePopupBinding>(LayoutInflater.from(this@TopicDetailActivity), R.layout.topic_rule_popup, null, false)
        popupViewBinding.tvRuleContent.movementMethod = ScrollingMovementMethod.getInstance()
        popupViewBinding.tvRuleContent.text = Html.fromHtml(mModel.topicDetail.value?.rule)
        popUpWindow.contentView = popupViewBinding.root
        popUpWindow.animationStyle = R.style.AnimBottom
        popUpWindow.setOnDismissListener { Utils.setWindowBackGroud(this@TopicDetailActivity, 1f) }
        return popUpWindow
    }

    private fun showRulePop() {
        rulePopup.showAtLocation(mBinding.root, Gravity.BOTTOM, 0, 0)
    }

    /**
     * 切换故事布局
     * @param type 0 列表布局 1 瀑布流布局
     */
    private fun switchStoryLayout(type: Int = 0) {
        if (type == 0) {
            val storyLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            mBinding.rvStory.layoutManager = storyLayoutManager
            mBinding.rvStory.adapter = storyAdapter
            v_topic_empty?.visibility = View.GONE
        } else {
            val storyLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            mBinding.rvStory.layoutManager = storyLayoutManager
            mBinding.rvStory.adapter = storyGridAdapter
            v_topic_empty?.visibility = View.GONE
        }
    }

    override fun initData() {
        mModel.getHotStoryList(id, mModel.TYPEHOT)
        mModel.getTopicDetail(id)
    }

    /**
     * 去到添加故事详情页面
     */
    fun gotoShareStory(v: View) {
        if (topic != null) {
            val topicBean = HomeTopicBean(
                topic.contentNum,
                topic.hot,
                topic.id,
                topic.image,
                topic.name,
                topic.participateNum,
                topic.showNum,
                topic.topicStatus,
                topic.topicTypeName,
                VipResourceStatus(topic.vipResourceStatus.collectionStatus, topic.vipResourceStatus.likeStatus,false)
            )

            ARouter.getInstance()
                .build(MainARouterPath.MAIN_STORY_STRATEGY_ADD)
                .withParcelable("topic", topicBean)
                .navigation()
        }
    }

    fun gotoCommentPage(v: View) {

        ARouter.getInstance()
            .build(MainARouterPath.MAIN_ACTIVITY_COMMENT)
            .withString("id", id)
            .navigation()
    }


    var topicIntroduction = ""

    /**
     * 话题介绍是否可展开
     */
    var expandFlag = false

    /**
     * textView 超过行数显示 展开
     * @param minLines Int 最小子展示多少行
     * @param originText String 原文字
     * @param endText String 提示展开的文字
     * @param endColorID Int 提示展开文字的颜色
     * @param view TextView 需要需效果的textView
     */
    private fun toggleEllipsize(
        minLines: Int,
        originText: String,
        endText: String,
        endColorID: Int,
        view: TextView
    ) {
        if (TextUtils.isEmpty(originText)) {
            return
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val paddingLeft = view.paddingLeft
                val paddingRight = view.paddingRight
                val paint = view.paint
                val moreText = view.textSize * endText.length
                val availableTextWidth = (view.width - paddingLeft - paddingRight) * minLines - moreText * 2
                val ellipsizeStr = TextUtils.ellipsize(originText, paint, availableTextWidth, TextUtils.TruncateAt.END)
                if (ellipsizeStr.length < originText.length) {
                    val temp = ellipsizeStr.toString() + endText
                    val ssb = SpannableStringBuilder(temp)
                    ssb.setSpan(
                        ForegroundColorSpan(resources.getColor(endColorID)),
                        temp.length - endText.length, temp.length,
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                    )
                    view.text = ssb
                    expandFlag = true
                } else {
                    view.text = originText
                    expandFlag = false
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } else {
                    view.viewTreeObserver.removeGlobalOnLayoutListener(this)
                }
            }
        })
    }

}