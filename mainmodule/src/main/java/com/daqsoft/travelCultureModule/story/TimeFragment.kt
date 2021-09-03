package com.daqsoft.travelCultureModule.story

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.PandaRefreshHeader
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.FragmentTimeBinding
import com.daqsoft.mainmodule.databinding.ItemHomeHotTopicBinding
import com.daqsoft.mainmodule.databinding.ItemHomeStoryTypeBinding
import com.daqsoft.mainmodule.databinding.ItemStoryStrategyBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.provider.utils.GaosiUtils
import com.daqsoft.provider.view.LabelsView
import com.daqsoft.travelCultureModule.story.story.StoryAdapter
import com.daqsoft.travelCultureModule.story.utils.TopicResourceType
import com.daqsoft.travelCultureModule.story.vm.TimeFragmentViewModel
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

/**
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-10-22
 * @since JDK 1.8.0_191
 */
class TimeFragment : BaseFragment<FragmentTimeBinding, TimeFragmentViewModel>() {


    override fun getLayout(): Int = R.layout.fragment_time

    override fun injectVm(): Class<TimeFragmentViewModel> = TimeFragmentViewModel::class.java

    override fun initData() {

        mModel.getTopicList()

        mModel.getHotStoryTagList()



        mModel.getCoverList()

        mModel.getStrategyList()

    }

    private var currStoryPage: Int = 1

    private val storyAdapter by lazy { StoryAdapter(context!!) }

    private var isLoadMore: Boolean = false

    private val mDatasStoryTypes by lazy { mutableListOf<HomeStoryTagBean>() }

    @SuppressLint("CheckResult")
    override fun initView() {
        // 进入标签页面
        RxView.clicks(mBinding.tvAllStory)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_STORY_TAG)
                    .navigation()
            }
        RxView.clicks(mBinding.ctvMoreStrategy)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_STRATEGY_FIND)
                    .navigation()
            }


        if(activity is  TimActivity){
            mBinding.tvTitle.visibility=View.GONE
        }else{
            if(BaseApplication.appArea=="ws")
                mBinding.tvTitle.visibility=View.VISIBLE
            else
                mBinding.tvTitle.visibility=View.GONE
        }
        // 攻略
        val strategyManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvStrategy.layoutManager = strategyManager
        mBinding.rvStrategy.adapter = strategyAdapter

        // 话题
        val topicLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvTopic.layoutManager = topicLayoutManager
        mBinding.rvTopic.adapter = topicAdapter

        // 故事
        switchStoryLayout()

        initViewModel()

        initViewEvent()
    }

    private fun initViewModel() {
        mModel.topicList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.rvTopic.visibility = View.VISIBLE
                mBinding.vTopicMore.visibility = View.VISIBLE
                topicAdapter.add(it)
            }
            mBinding.smartRefreshLayout.finishRefresh()
        })

        mModel.storyTagList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.vMoreStory.visibility = View.VISIBLE
                mBinding.llvStoryTypies.visibility = View.VISIBLE
                var dataStoryTypies: MutableList<String> = mutableListOf()
                dataStoryTypies.add(0, "全部")
                mModel.tagId = ""
                for (item in it) {
                    dataStoryTypies.add("" + item.name)
                    if (!item.name.isNullOrEmpty() && item.name == "打卡新疆") {
                        mModel.tagName = "打卡新疆"
                    }
                }
                mDatasStoryTypes.clear()
                mDatasStoryTypes.addAll(it)
                mBinding.llvStoryTypies.setLabels(dataStoryTypies)
            }
            currStoryPage = 1
            mModel.getHotStoryList(currStoryPage)

        })

        mModel.strategyList.observe(this, Observer {
            if (it.size > 0) {
                mBinding.rvStrategy.visibility = View.VISIBLE
                mBinding.vMoreStrategy.visibility = View.VISIBLE
                strategyAdapter.add(it)
            }
        })

        mModel.storyList.observe(this, Observer {
            dissMissLoadingDialog()
            if (currStoryPage == 1) {
                storyAdapter!!.clear()
            }
            if (it != null) {
                for (i in 0 until (it!!.size ?: 0)) {
                    var data = it!![i]
                    if (data.storyType == Constant.STORY_TYPE_STORY) {
                        storyAdapter!!.addViewType(R.layout.item_story_main)
                    } else if (data.storyType == Constant.STORY_TYPE_STRATEGY) {
                        storyAdapter!!.addViewType(R.layout.item_story_list_strategy)
                    }
                }
                if (!it.isNullOrEmpty()) {
                    storyAdapter!!.add(it)
                } else {
                    if (currStoryPage == 1) {
                        storyAdapter?.emptyViewShow = true
                    }
                }

                if (it.isNullOrEmpty() || it.size < 10) {
                    storyAdapter?.loadComplete()
                    storyAdapter?.loadEnd()
                } else {
                    storyAdapter?.loadComplete()
                }
            }
        })

        // 封面
        mModel.coverList.observe(this, Observer {
            if (it != null) {
                val item = it
                mBinding.clLayout.visibility = View.VISIBLE
                mBinding.avatar = item.vipHead
                if (!item.images.isNullOrEmpty()) {
                    Glide.with(context!!).load(item.images[0])
                        .placeholder(R.mipmap.placeholder_img_fail_240_180)
                        .into(mBinding.cityImage)
                }
                mBinding.name = item.vipNickName
                if (!item.resourceRegionName.isNullOrEmpty()) {
                    // 判断是否关联地址和类型
                    var location = DividerTextUtils.convertDotString(
                        StringBuilder(), item.resourceRegionName, ResourceType
                            .getName(item.resourceType), item.resourceName
                    )
                    mBinding.tvLocation.text = location
                    mBinding.tvLocation.visibility = View.VISIBLE
                } else {
                    mBinding.tvLocation.visibility = View.GONE
                }

                if (!item.tagName.isNullOrEmpty()) {
                    mBinding.tvTag.text = item.tagName
                    mBinding.ivTagImage.visibility = View.VISIBLE
                    mBinding.tvTag.visibility = View.VISIBLE
                } else {
                    mBinding.ivTagImage.visibility = View.GONE
                    mBinding.tvTag.visibility = View.GONE
                }


                mBinding.likeNumber = item.likeNum.toString()
                mBinding.commentNumber = item.commentNum.toString()
                mBinding.content = item.title
                if (!item.images.isNullOrEmpty()) {
                    Glide.with(this)
                        .asBitmap()
                        .load(item.images[0])
                        .centerCrop()
                        .override(150, 150)
                        .into(object : CustomTarget<Bitmap>() {

                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {

                                var b = GaosiUtils.rsBlur(context, resource, 25)

                                mBinding.ivGaosi.setImageBitmap(b)
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {

                            }
                        })
                }
            }
        })
    }

    /**
     * 初始化事件
     */
    @SuppressLint("ClickableViewAccessibility", "CheckResult")
    private fun initViewEvent() {
        mBinding.btnWriteStory.setOnClickListener(View.OnClickListener {
            if (AppUtils.isLogin()) {
                ARouter.getInstance().build(MainARouterPath.MAIN_STORY_STRATEGY_ADD)
                    .navigation()
            } else {
                ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                ARouter.getInstance()
                    .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        })
        mBinding.smartRefreshLayout.run {
            setOnRefreshListener {
                currStoryPage = 1
                initData()

            }
            setRefreshHeader(PandaRefreshHeader(context!!))
        }
        RxView.clicks(mBinding.clLayout)
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                run {
                    ARouter.getInstance()
                        .build(MainARouterPath.MAIN_STORY_DETAIL)
                        .withString("id", mModel.coverList.value?.id)
                        .withInt("type", 1)
                        .navigation()
                }
            }
        mBinding.tvTopicTitle.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_TOPIC_LIST)
                .navigation()
        }
        mBinding.llvStoryTypies.setOnLabelSelectChangeListener(object : LabelsView.OnLabelSelectChangeListener {
            override fun onLabelSelectChange(label: TextView?, data: Any?, isSelect: Boolean, position: Int) {
                if (isSelect) {
                    label?.setTextColor(resources.getColor(com.daqsoft.provider.R.color.white))
                    label?.setBackgroundResource(com.daqsoft.provider.R.drawable.shape_provider_rec_tab_12_selected)
                    var size = mDatasStoryTypes.size
                    if (position <= size) {
                        var currPos = position - 1
                        if (currPos < 0) {
                            currPos = 0
                        }

                        var tagId = ""
                        if (position != 0) {
                            var tag: HomeStoryTagBean? = mDatasStoryTypes[currPos]
                            if (tag != null) {
                                tagId = tag.id
                            }
                        }
                        mModel.tagId = tagId
                        currStoryPage = 1
                        showLoadingDialog()
                        mModel.getHotStoryList(currStoryPage)
                    }
                } else {
                    label?.setTextColor(resources.getColor(com.daqsoft.provider.R.color.color_333))
                    label?.setBackgroundResource(com.daqsoft.provider.R.drawable.shape_provider_act_tab_12_unselect)
                }
            }
        })
    }

    /**
     * 清除数据
     */
    private fun clearData() {
        topicAdapter?.clear()
        strategyAdapter?.clear()
        storyAdapter?.clear()
        storyTypeAdapter?.clear()
        mDatasStoryTypes?.clear()
    }


    /**
     * 切换故事布局
     * @param type 0 列表布局 1 瀑布流布局
     */
    private fun switchStoryLayout(type: Int = 0) {
        val storyLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mBinding.rvStory.layoutManager = storyLayoutManager
        mBinding.rvStory.adapter = storyAdapter
        storyAdapter?.emptyViewShow = false
        storyAdapter?.emptyContent = "-推荐故事到头啦-"
        storyAdapter?.bottomMagin = true
        storyAdapter?.setOnLoadMoreListener {
            currStoryPage += 1
            mModel.getHotStoryList(currStoryPage)
        }
    }

    /**
     * 切换按钮显示和隐藏
     * @param isShow false 隐藏 true 显示
     */
    private fun switchStoryBtnState(isShow: Boolean = true) {
        if (isShow) {
            mBinding.btnWriteStory.visibility = View.VISIBLE
        } else {
            mBinding.btnWriteStory.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clearData()
    }

    /**
     * 热门话题分类适配器
     */
    private val topicAdapter = object :
        RecyclerViewAdapter<ItemHomeHotTopicBinding, HomeTopicBean>(R.layout.item_home_hot_topic) {
        override fun setVariable(
            mBinding: ItemHomeHotTopicBinding,
            position: Int,
            item: HomeTopicBean
        ) {
            mBinding.url = item.image
            mBinding.name = item.name
            mBinding.memberNumber = item.participateNum
            mBinding.watchNumber = item.showNum
            mBinding.tvContentNumber.text =
                getString(R.string.home_topic_content_number, item.contentNum)
            if (item.topicTypeName.isNullOrEmpty()) {
                mBinding.tvTypeName.visibility = View.GONE
            } else {
                mBinding.tvTypeName.visibility = View.VISIBLE
                mBinding.tvTypeName.text = item.topicTypeName
                mBinding.tvTypeName.setBackgroundResource(TopicResourceType.getTypeBgIcon(item.topicTypeName))
            }
            if (item.hot) {
                val drawable = context!!.getDrawable(R.mipmap.home_ht_hot)
                mBinding.tvTitle.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            } else {
                mBinding.tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }
            mBinding.root.onNoDoubleClick {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_TOPIC_DETAIL)
                    .withString("id", item.id)
                    .navigation()
            }
        }

    }

    /**
     * 攻略分类适配器
     */
    private val strategyAdapter = object :
        RecyclerViewAdapter<ItemStoryStrategyBinding, HomeStoryBean>(R.layout.item_story_strategy) {
        @SuppressLint("CheckResult")
        override fun setVariable(
            mBinding: ItemStoryStrategyBinding,
            position: Int,
            item: HomeStoryBean
        ) {
            if(item.sourceUrl.isNullOrEmpty()){
                mBinding.reprint.visibility = View.GONE
            }else{
                mBinding.reprint.visibility = View.VISIBLE
            }
            RxView.clicks(mBinding.root)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    ARouter.getInstance()
                        .build(MainARouterPath.MAIN_STRATEGY_DETAIL)
                        .withString("id", item.id)
                        .withInt("type", 1)
                        .navigation()
                }
            Glide.with(context!!).load(item.vipHead)
                .placeholder(R.mipmap.mine_profile_photo_default).into(mBinding.ivUser)

            mBinding.name = item.vipNickName
            mBinding.likeNumber = item.likeNum.toString()
            mBinding.commentNumber = item.commentNum
            // 图片数量
            if (item.images.isNotEmpty()) {
                mBinding.tvImageNumber.text =
                    getString(
                        R.string.home_story_image_number,
                        item.images.size.toString()
                    )
                mBinding.tvImageNumber.visibility = View.VISIBLE
            } else {
                mBinding.tvImageNumber.visibility = View.GONE
            }
            // 地址
            if (item.resourceRegionName.isNullOrEmpty()) {
                // 判断是否关联地址和类型
                mBinding.locationName.visibility = View.GONE
            } else {
                mBinding.locationName.visibility = View.VISIBLE
            }
            mBinding.location = DividerTextUtils.convertDotString(
                StringBuilder(), item.resourceRegionName,
                item.resourceName
            )

            mBinding.tvCityType.text = getString(R.string.home_story_type_strategy)
            Glide.with(mBinding.aiImage)
                .load(item.cover)
                .centerCrop()
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .into(mBinding.aiImage)

            if (item.video.isNotEmpty()) {
                // 当有视频时
                mBinding.ivVideo.visibility = View.VISIBLE
            } else {
                mBinding.ivVideo.visibility = View.GONE
            }
            var ssb = SpannableStringBuilder()
            if (item.tagName.isNotEmpty()) {
                ssb.append("#" + item.tagName + "#")
                ssb.setSpan(
                    ForegroundColorSpan(resources.getColor(R.color.colorPrimary)),
                    0,
                    ssb.length,
                    Spanned
                        .SPAN_INCLUSIVE_EXCLUSIVE
                )
            }
            ssb.append(item.title)
            mBinding.tvContent.text = ssb

            if (ssb.isNullOrEmpty()) {
                mBinding.tvContent.visibility = View.GONE
            } else {
                mBinding.tvContent.visibility = View.VISIBLE
            }
            if (item.strategyDetail.isNotEmpty()) {
                val strategyDetail = item.strategyDetail[0]
                when (strategyDetail.contentType) {
                    Constant.STORY_STRATEGY_IMAGE_TYPE -> {
                        val images = strategyDetail.content.split(",")
                        if (item.images.isNotEmpty()) {
                            mBinding.tvImageNumber.text =
                                getString(
                                    R.string.home_story_image_number,
                                    images.size.toString()
                                )
                            mBinding.tvImageNumber.visibility = View.VISIBLE
                        } else {
                            mBinding.tvImageNumber.visibility = View.INVISIBLE
                        }


                    }
                }
            }
        }

    }

    /**
     * 故事类型适配器
     */
    private val storyTypeAdapter = object :
        RecyclerViewAdapter<ItemHomeStoryTypeBinding, HomeStoryTagBean>(R.layout.item_home_story_type) {
        @SuppressLint("CheckResult")
        override fun setVariable(
            mBinding: ItemHomeStoryTypeBinding,
            position: Int,
            item: HomeStoryTagBean
        ) {

            mBinding.url = item.cover
            mBinding.name = item.name
//            mBinding.cover.background.alpha = 102
            mBinding.tvStoryNumber.text = if (item.storyNum != null && item.storyNum.toInt() > 0)
                getString(R.string.home_story_number, item.storyNum)
            else getString(R.string.home_story_tag)

            RxView.clicks(mBinding.root)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    ARouter.getInstance()
                        .build(MainARouterPath.MAIN_STORY_TAG_DETAIL)
                        .withString("id", item.id)
                        .navigation()
                }
        }

    }


}