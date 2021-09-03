package com.daqsoft.thetravelcloudwithculture.xj.ui

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.databinding.ItemHomeHotTopicBinding
import com.daqsoft.mainmodule.databinding.ItemHomeStoryTypeBinding
import com.daqsoft.mainmodule.databinding.MainItemHotActivityClassifyBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.adapter.GridStoryAdapter
import com.daqsoft.provider.base.AdvCodeType
import com.daqsoft.provider.base.PublishChannel
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.view.convenientbanner.ConvenientBanner
import com.daqsoft.provider.view.convenientbanner.holder.BaseBannerImageHolder
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.FragmentHomeActivityTopicStoryXjBinding
import com.daqsoft.thetravelcloudwithculture.ui.MainActivity
import com.daqsoft.thetravelcloudwithculture.ui.event.RefreshHomeEvent
import com.daqsoft.travelCultureModule.story.story.GridHomeStoryAdapter
import com.daqsoft.travelCultureModule.story.utils.TopicResourceType
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
class XJActivityTopicStoryFragment : BaseFragment<FragmentHomeActivityTopicStoryXjBinding,
        XJActivityTopicStoryFragmentViewModel>() {


    /**
     * 故事适配器
     */
    private var storyAdapter: GridStoryAdapter? = null

    override fun injectVm(): Class<XJActivityTopicStoryFragmentViewModel> =
        XJActivityTopicStoryFragmentViewModel::class.java


    override fun getLayout(): Int = R.layout.fragment_home_activity_topic_story_xj

    override fun initData() {
        // 广告
        mModel.getAds()
        // 话题
        mModel.getTopicList()

        mModel.getHotStoryTagList()
    }

    override fun initView() {
        EventBus.getDefault().post(this)
        // 话题
        val topicLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvTopic.layoutManager = topicLayoutManager
        mBinding.rvTopic.adapter = topicAdapter

        // 故事类型

        val storyTypeLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvStoryType.layoutManager = storyTypeLayoutManager
        mBinding.rvStoryType.adapter = hotClassifyAdapter

        // 故事
        val storyLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        storyAdapter =GridStoryAdapter(context!!,GridStoryAdapter.ARENA)


        mBinding.rvStory.layoutManager = storyLayoutManager
        mBinding.rvStory.adapter = storyAdapter
        mBinding.llHomeStoryMore.onNoDoubleClick {
            val position = (activity as MainActivity).getPositionByCode("TIME")
            if (position == -1) {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_TIME)
                    .navigation()
            } else {
                (activity as MainActivity).changeTab(position)
            }
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
                topicAdapter.clear()
                topicAdapter.add(it)
                topicAdapter.notifyDataSetChanged()
                mBinding.llHomeTopic.visibility = View.GONE
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
                    .setCanLoop(images.size > 1)
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
            dissMissLoadingDialog()
            if (!it.isNullOrEmpty()) {
                val storyType = HomeStoryTagBean("", "0", "全部", "", true)
                it.add(0, storyType)
                mModel.storyTypeTagId = "0"
                hotClassifyAdapter.currentClassify = it[0]
                hotClassifyAdapter.clear()
                hotClassifyAdapter.add(it)
                mBinding.rvStoryType.visibility = View.VISIBLE
                mModel.getHotStoryList()
            } else {
                mBinding.rvStoryType.visibility = View.GONE
            }

        })
        // 故事
        mModel.storyList.observe(this, Observer {
            dissMissLoadingDialog()
            if (!it.isNullOrEmpty()) {
                mBinding.llHomeStoryMore.visibility = View.VISIBLE
                mBinding.rvStory.visibility = View.VISIBLE
                storyAdapter!!.clear()
                storyAdapter!!.add(it)
            }else{
                storyAdapter!!.clear()
            }
        })

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

    /**
     * 热门活动分类适配器
     */
    private val hotClassifyAdapter = object :
        RecyclerViewAdapter<MainItemHotActivityClassifyBinding, HomeStoryTagBean>(com.daqsoft.mainmodule.R.layout.main_item_hot_activity_classify) {
        var currentClassify: HomeStoryTagBean? = null

        override fun setVariable(
            mBinding: MainItemHotActivityClassifyBinding,
            position: Int,
            item: HomeStoryTagBean
        ) {
            mBinding.label.text = item.name
            mBinding.label.isSelected = item.select

            var d = RxView.clicks(mBinding.root)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    run {
                        if (currentClassify != item) {
                            item.select = true
                            if (currentClassify != null) {
                                currentClassify!!.select = false
                            }
                            currentClassify = item
                            mModel.storyTypeTagId = item.id
                            selectToTypePos(position)
                            showLoadingDialog()
//                            storyAdapter?.clear()
                            mModel.getHotStoryList()
                            notifyDataSetChanged()
                        }
                    }
                }
        }
    }

    /**
     *  选择点击的class
     */
    fun selectToTypePos(position: Int) {
        mBinding?.rvStoryType.smoothScrollToPosition(position)
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
     * 故事类型适配器
     */
    private val storyTypeAdapter = object :
        RecyclerViewAdapter<ItemHomeStoryTypeBinding, HomeStoryTagBean>(R.layout.item_home_story_type) {
        override fun setVariable(
            mBinding: ItemHomeStoryTypeBinding,
            position: Int,
            item: HomeStoryTagBean
        ) {
            mBinding.url = item.cover
            mBinding.name = item.name
            mBinding.cover.background.alpha = 255 - 153
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

/**
 * @des 活动话题和故事页面的viewMode
 * @author PuHua
 * @Date 2019/12/5 17:54
 */
class XJActivityTopicStoryFragmentViewModel : BaseViewModel() {
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
     * 故事标签id
     */
    var storyTypeTagId = "0"

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
        mPresenter.value?.loading = true
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
        mPresenter.value?.loading = true
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
        mPresenter.value?.loading = true
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
        param["listCover"] = "1"
        param["tagId"] = storyTypeTagId
        //  pageSize
        param["pageSize"] = "20"
        mPresenter.value?.loading = true
        HomeRepository.service.getStoryList(param)
            .excute(object : BaseObserver<HomeStoryBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeStoryBean>) {
                    storyList.postValue(response.datas)
                }
            })
    }
}