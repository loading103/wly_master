package com.daqsoft.thetravelcloudwithculture.sc.fragment

import android.annotation.SuppressLint
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager.widget.PagerAdapter
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.baselib.utils.Utils.dip2px
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.AdvCodeType
import com.daqsoft.provider.base.PublishChannel
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.net.ProviderApi
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.view.convenientbanner.ConvenientBanner
import com.daqsoft.provider.view.convenientbanner.holder.BaseBannerImageHolder
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.extend.onModuleNoDoubleClick
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.FragmentHomeActivityTopicStoryScBinding
import com.daqsoft.thetravelcloudwithculture.databinding.ItemHomeHotTopicScBinding
import com.daqsoft.thetravelcloudwithculture.databinding.ItemHomeStoryTypeScBinding
import com.daqsoft.thetravelcloudwithculture.databinding.ItemHomeStoryTypeScNewBinding
import com.daqsoft.thetravelcloudwithculture.sc.TopicPageTransformer
import com.daqsoft.thetravelcloudwithculture.ui.event.RefreshHomeEvent
import com.daqsoft.provider.adapter.GridStoryAdapter
import com.jakewharton.rxbinding2.view.RxView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit


/**
 * @des 活动话题和故事页面
 * @author PuHua
 * @Date 2019/12/5 17:52
 */
class SCActivityTopicStoryFragment : BaseFragment<FragmentHomeActivityTopicStoryScBinding,
        SCActivityTopicStoryFragmentViewModel>() {


    /**
     * 故事适配器
     */
    private var storyAdapter: GridStoryAdapter? = null

    override fun injectVm(): Class<SCActivityTopicStoryFragmentViewModel> =
        SCActivityTopicStoryFragmentViewModel::class.java


    override fun getLayout(): Int = R.layout.fragment_home_activity_topic_story_sc

    override fun initData() {
        // 广告
        mModel.getAds()
        // 话题
        mModel.getTopicList()

        mModel.getHotStoryTagList()

        mModel.getHotStoryList()

    }

    override fun initView() {
        EventBus.getDefault().register(this)
        // 故事类型

        val storyTypeLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvStoryType.layoutManager = storyTypeLayoutManager
        mBinding.rvStoryType.adapter = storyTypeNewAdapter
        mBinding.rvStoryType.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.right = dip2px(context!!, 8f).toInt()
            }
        })

        // 故事
        val storyLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        storyAdapter = GridStoryAdapter(context!!)

        mBinding.rvStory.layoutManager = storyLayoutManager
        mBinding.rvStory.adapter = storyAdapter
        mBinding.llHomeStoryMore.onNoDoubleClick {
            EventBus.getDefault().post(ChangeTabEvent("TIME", 2))
        }
        mBinding.llHomeTopicMore.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_TOPIC_LIST)
                .navigation()
        }

    }

    override fun notifyData() {
        super.notifyData()
        // 话题
        mModel.topicList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.llHomeTopic.visibility = View.VISIBLE

                topicPagerAdapter.setData(it)
                mBinding.rvTopic.setPageTransformer(TopicPageTransformer())
                mBinding.rvTopic.setAdapter(topicPagerAdapter)
                mBinding.rvTopic.setOffscreenPageLimit(3)
                if (it.size > 3) {
                    mBinding.rvTopic.currentPosition = 1
                }

            } else {
                mBinding.llHomeTopic.visibility = View.GONE
            }
        })

        // 广告
        mModel.homeAd.observe(this, Observer {
            var images = mutableListOf<String>()
            if (!it.adInfo.isNullOrEmpty()) {
                var homeAd = it.adInfo
                mBinding.bannerCenterAdv.visibility = View.VISIBLE
                for (img in it.adInfo) {
                    if (!img.imgUrl.isNullOrEmpty())
                        images.add(img.imgUrl!!)
                }
                mBinding?.bannerCenterAdv
                    .setPages(object : CBViewHolderCreator {
                        override fun createHolder(itemView: View?): Holder<*> {
                            return BaseBannerImageHolder(itemView!!)
                        }

                        override fun getLayoutId(): Int {
                            return R.layout.holder_img_adv_90
                        }
                    }, images)
                    .setCanLoop(false)
                    .setPointViewVisible(true)
                    .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                    .setOnItemClickListener {
                        // 跳转事件
                        ARouter.getInstance()
                            .build(ARouterPath.Provider.WEB_ACTIVITY)
                            .withString("mTitle", "详情")
                            .withString("html", homeAd.get(it).jumpUrl)
                            .navigation()
                    }
                    .setPageIndicator(null)
                    .startTurning(3000)
            }

        })
        // 故事tag标签
        mModel.storyTagList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                storyTypeNewAdapter.clear()
                storyTypeNewAdapter.add(it)
                mBinding.rvStoryType.visibility = View.VISIBLE
            } else {
                mBinding.rvStoryType.visibility = View.GONE
            }

        })
        // 故事
        mModel.storyList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.rvStory.visibility = View.VISIBLE
                storyAdapter!!.clear()
                storyAdapter!!.add(it)
            } else {
                mBinding.llHomeStoryMore.visibility = View.GONE
                mBinding.rvStory.visibility = View.GONE
            }
        })

    }


    /**
     * 热门话题分类适配器
     */
    private val topicAdapter = object :
        RecyclerViewAdapter<ItemHomeHotTopicScBinding, HomeTopicBean>(R.layout.item_home_hot_topic_sc) {
        @SuppressLint("SetTextI18n")
        override fun setVariable(
            mBinding: ItemHomeHotTopicScBinding,
            position: Int,
            item: HomeTopicBean
        ) {
            mBinding.url = item.image
            mBinding.name = item.name
            if (item.participateNum != "0") {
                mBinding.tvNumbers.text = "${item.participateNum} 参与"
            } else {
                mBinding.tvNumbers.visibility = View.GONE
            }
            if (item.contentNum != "0") {
                mBinding.tvContents.text = "${item.contentNum} 内容"
            } else {
                mBinding.tvContents.visibility = View.GONE
            }
            if (item.showNum != "0") {
                mBinding.tvWatchTimes.text = "${item.showNum} 浏览"
            } else {
                mBinding.tvWatchTimes.visibility = View.GONE
            }
            if (item.hot) {
                mBinding.tvHot.visibility = View.VISIBLE
            } else {
                mBinding.tvHot.visibility = View.GONE
            }
            if (!item.topicTypeName.isNullOrEmpty()) {
                mBinding.tvType.text = item.topicTypeName
            } else {
                mBinding.tvType.visibility = View.GONE
            }
            mBinding.root.onNoDoubleClick {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_TOPIC_DETAIL)
                    .withString("id", item.id)
                    .navigation()
            }
        }

    }


    private var topicPagerAdapter = object : PagerAdapter() {
        var topics = mutableListOf<HomeTopicBean>()

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view == obj
        }

        fun setData(topics: MutableList<HomeTopicBean>) {
            this.topics = topics
        }

        override fun getCount(): Int = topics.size

        @SuppressLint("CheckResult")
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            var mBinding: ItemHomeHotTopicScBinding = DataBindingUtil.inflate(
                LayoutInflater.from(container.context),
                R.layout.item_home_hot_topic_sc,
                null,
                false
            )
            val item = topics[position]
            mBinding.url = item.image
            mBinding.name = item.name

//            if (item.participateNum != "0") {
//                mBinding.tvNumbers.text = "${item.participateNum} 参与"
//            } else {
//                mBinding.tvNumbers.visibility = View.GONE
//            }
//            if (item.contentNum != "0") {
//                mBinding.tvContents.text = "${item.contentNum} 内容"
//            } else {
//                mBinding.tvContents.visibility = View.GONE
//            }
//            if (item.showNum != "0") {
//                mBinding.tvWatchTimes.text = "${item.showNum} 浏览"
//            } else {
//                mBinding.tvWatchTimes.visibility = View.GONE
//            }
//            if (item.hot) {
//                mBinding.tvHot.visibility = View.VISIBLE
//            } else {
//                mBinding.tvHot.visibility = View.GONE
//            }
//            if (!item.topicTypeName.isNullOrEmpty()) {
//                mBinding.tvType.text = item.topicTypeName
//            } else {
//                mBinding.tvType.visibility = View.GONE
//            }

            // 2020/9/4 修改
            if (item.hot) {
                mBinding.tvHot.visibility = View.VISIBLE
            } else {
                mBinding.tvHot.visibility = View.GONE
            }
            if (!item.topicTypeName.isNullOrEmpty()) {
                mBinding.tvType.text = item.topicTypeName
            } else {
                mBinding.tvType.visibility = View.GONE
            }
            val drawable = resources.getDrawable(R.drawable.shape_round_gray_d2)
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            if (mBinding.tvHot.isVisible && mBinding.tvType.isVisible) {
                mBinding.tvContentsTxt.setCompoundDrawables(null, null, drawable, null)
            }
            if (!item.hot && item.topicTypeName.isNullOrEmpty()) {
                mBinding.clHot.visibility = View.GONE
            }

            if (item.contentNum != "0") {
                mBinding.tvContents.text = item.contentNum
            } else {
                mBinding.groupContent.visibility = View.GONE
            }
            if (item.participateNum != "0") {
                mBinding.tvNumbers.text = item.participateNum
            } else {
                mBinding.groupParticipate.visibility = View.GONE
            }
            if (item.showNum != "0") {
                mBinding.tvWatchTimes.text = item.showNum
            } else {
                mBinding.groupBrowse.visibility = View.GONE
            }
            if (mBinding.groupContent.isVisible && (mBinding.groupParticipate.isVisible || mBinding.groupBrowse.isVisible)) {
                mBinding.tvContentsTxt.setCompoundDrawables(null, null, drawable, null)
            }
            if (mBinding.groupParticipate.isVisible && mBinding.groupBrowse.isVisible) {
                mBinding.tvNumbersTxt.setCompoundDrawables(null, null, drawable, null)
            }
            if (item.contentNum == "0" && item.participateNum == "0" && item.showNum == "0") {
                mBinding.clContent.visibility = View.GONE
            }


            mBinding.root.onModuleNoDoubleClick(
                resources.getString(R.string.home_topic),
                ProviderApi.REGION_MAIN_COLUMNS
            ) {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_TOPIC_DETAIL)
                    .withString("id", item.id)
                    .navigation()
            }
            container.addView(mBinding.root)
            return mBinding.root
        }

        override fun getItemPosition(`object`: Any): Int {
            return POSITION_NONE
        }

        override fun destroyItem(container: ViewGroup, position: Int, ob: Any) {
//        super.destroyItem(container, position, ob)
            if (ob != null && ob is View) {
                container.removeView(ob as View?)
            }
        }
    }

    val tagBgs = listOf<Int>(
        R.mipmap.home_story_recommend_no1,
        R.mipmap.home_story_recommend_no2,
        R.mipmap.home_story_recommend_no3,
        R.mipmap.home_story_recommend_no4
    )

    /**
     * 故事类型适配器
     */
    private val storyTypeAdapter = object :
        RecyclerViewAdapter<ItemHomeStoryTypeScBinding, HomeStoryTagBean>(R.layout.item_home_story_type_sc) {
        @SuppressLint("CheckResult", "SetTextI18n")
        override fun setVariable(
            mBinding: ItemHomeStoryTypeScBinding,
            position: Int,
            item: HomeStoryTagBean
        ) {
            mBinding.url = item.cover
            mBinding.name = item.name
            mBinding.tvType.text = "#${item.name}"
            Glide.with(context!!)
                .load(tagBgs[position % 4])
                .into(mBinding.image)
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

    /**
     * 故事类型适配器   2020/9/7 新增
     */
    private val storyTypeNewAdapter = object :
        RecyclerViewAdapter<ItemHomeStoryTypeScNewBinding, HomeStoryTagBean>(R.layout.item_home_story_type_sc_new) {
        @SuppressLint("CheckResult", "SetTextI18n")
        override fun setVariable(
            mBinding: ItemHomeStoryTypeScNewBinding,
            position: Int,
            item: HomeStoryTagBean
        ) {
            mBinding.content.text = "#${item.name}"
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


    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    /**
     * 刷新数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refresh(event: RefreshHomeEvent) {
        initData()
    }
}

/**
 * @des 活动话题和故事页面的viewMode
 * @author PuHua
 * @Date 2019/12/5 17:54
 */
class SCActivityTopicStoryFragmentViewModel : BaseViewModel() {
    /**
     * 活动分类列表
     */
    val activityClassifies = MutableLiveData<MutableList<Classify>>()

    /**
     * 活动列表
     */
    val activities = MutableLiveData<MutableList<ActivityBean>>()

    /**
     * 话题列表
     */
    val topicList = MutableLiveData<MutableList<HomeTopicBean>>()

    /**
     * 故事标签列表
     */
    val storyTagList = MutableLiveData<MutableList<HomeStoryTagBean>>()

    /**
     * 故事列表数据
     */
    val storyList = MutableLiveData<MutableList<HomeStoryBean>>()

    /**
     * 广告
     */
    var homeAd = MutableLiveData<HomeAd>()

    /**
     * 获取活动分类列表
     */
    fun getActivityClassify() {
        mPresenter.value?.loading = true
        HomeRepository.service.getActivityClassify()
            .excute(object : BaseObserver<Classify>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Classify>) {
                    activityClassifies.postValue(response.datas)
                }
            })
    }

    /**
     * 获取广告
     */
    fun getAds() {
        mPresenter.value?.loading = false
        HomeRepository.service.getHomeAd(PublishChannel.MICRO_SITE, AdvCodeType.INDEX_CENTER_ADV)
            .excute(object : BaseObserver<HomeAd>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeAd>) {
                    homeAd.postValue(response.data)
                }
            })
    }

    /**
     * 获取话题列表
     */
    fun getTopicList() {
        mPresenter.value?.loading = false
        HomeRepository.service.getTopicList("10")
            .excute(object : BaseObserver<HomeTopicBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeTopicBean>) {
                    topicList.postValue(response.datas)
                }
            })
    }

    /**
     * 获取故事标签列表
     */
    fun getHotStoryTagList() {
        val param = HashMap<String, String>()
        // recommend   是否推荐【选填】1：是 0：否
        param["recommend"] = "1"
        // top  是否置顶 选填】1：是 0：否
        param["top"] = "1"
        // 最小故事数量
        param["minStoryNum"] = "1"
        //  size 查询数量 【选填】默认查询全部
        param["size"] = "6"
        mPresenter.value?.loading = false
        HomeRepository.service.getHotStoryTagList(param)
            .excute(object : BaseObserver<HomeStoryTagBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeStoryTagBean>) {
                    storyTagList.postValue(response.datas)
                }
            })
    }

    /**
     * 获取数据列表
     */
    fun getHotStoryList() {
        val param = HashMap<String, String>()
        // homeCover   首页封面	number	【选填】是否首页封面1：是 0：否
        //
        param["tagId"] = "0"
        param["listCover"] = "1"
        //  pageSize
        param["pageSize"] = "20"
        mPresenter.value?.loading = false
        HomeRepository.service.getStoryList(param)
            .excute(object : BaseObserver<HomeStoryBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeStoryBean>) {
                    storyList.postValue(response.datas)
                }
            })
    }
}