package com.daqsoft.legacyModule.home

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseApplication.Companion.context
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.adapter.GridWorksAdapter
import com.daqsoft.legacyModule.adapter.LegacyHeritageAdapter
import com.daqsoft.legacyModule.adapter.LegacyStoryGridAdapter
import com.daqsoft.legacyModule.adapter.LegacyWatchStoryAdapter
import com.daqsoft.legacyModule.bean.LegacyWatchStoryListBean
import com.daqsoft.legacyModule.databinding.*
import com.daqsoft.legacyModule.home.bean.DiscoverTypeBean
import com.daqsoft.legacyModule.home.bean.LegacyFoodBean
import com.daqsoft.legacyModule.home.bean.TopMenuBean
import com.daqsoft.legacyModule.home.event.HeritageActivityQuantityEvent
import com.daqsoft.legacyModule.rv.dsl.flow
import com.daqsoft.legacyModule.rv.dsl.linear
import com.daqsoft.legacyModule.smriti.event.UpdateFocusStatusEvent
import com.daqsoft.legacyModule.smriti.fragment.HeritageTeachingBaseFragment
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.adapter.GridStoryAdapter
import com.daqsoft.provider.adapter.ViewPagerAdapter
import com.daqsoft.provider.bean.HomeTopicBean
import com.daqsoft.provider.bean.LineTagBean
import com.daqsoft.provider.view.convenientbanner.ConvenientBanner
import com.daqsoft.provider.view.convenientbanner.holder.BaseBannerImageHolder
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.legacy_module_activity_home.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit


/**
 * @des    品非遗首页
 * @class  LegacyHomeActivity
 * @author Wongxd
 * @date   2020-4-20  19:55
 */

@Route(path = ARouterPath.LegacyModule.LEGACY_HOME_ACTIVITY)
internal class LegacyHomeActivity :
    TitleBarActivity<LegacyModuleActivityHomeBinding, LegacyHomeViewModel>() {
    var classfiyId: String? = ""
    var isRenderActivity: Boolean = false
    override fun getLayout(): Int = R.layout.legacy_module_activity_home

    override fun setTitle(): String = getString(R.string.legacy_module_home)

    override fun injectVm(): Class<LegacyHomeViewModel> = LegacyHomeViewModel::class.java

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun heritageActivityQuantityEvent(event: HeritageActivityQuantityEvent) {
        if (event.quantity <= 0){
            val newDara = discoverTypeAdapter.getData()
            newDara.removeAt(0)
            newDara[0].select = true
            discoverTypeAdapter.currentType = newDara[0]
            discoverTypeAdapter.notifyDataSetChanged()
            supportFragmentManager.beginTransaction().apply {
                hide(heritageHomeActivityFragment as Fragment)
                heritagePeopleFragment = HeritageHomePeopleFragment.newInstance()
                currentFragment = heritagePeopleFragment
                add(R.id.fl_content, heritagePeopleFragment!!)
                tabIndex = 0
            }.commit()
        }
    }

    override fun initView() {

        EventBus.getDefault().register(this)

        mModel.getActivityClassify()
        renderWatchStory()
        mBinding.tvSearch.apply {
            // 搜索背景透明度
            background.alpha = 70

            onNoDoubleClick {
                ARouter.getInstance().build(MainARouterPath.MAIN_ALL_SEARCH)
                    .navigation()
            }
        }

        renderTopMenu()

        mBinding.llStoryMore.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.LegacyModule.LEGACY_STORY_ACTIVITY)
                .navigation()
        }



        mBinding.rvStory.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
//            adapter = storyAdapter
            adapter = intangibleHeritageStoryAdapter
        }

        foodAdapter = ViewPagerAdapter(supportFragmentManager)
        mBinding.vpFood.adapter = foodAdapter

        mBinding.vpFoodIndicator.binViewPager(mBinding.vpFood)
        mBinding.llFoodMore.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.LegacyModule.LEGACY_FOOD_LIST_ACTIVITY)
                .navigation()
        }

        // 话题
        val topicLayoutManager =
            LinearLayoutManager(this@LegacyHomeActivity, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvTopic.layoutManager = topicLayoutManager
        mBinding.rvTopic.adapter = topicAdapter

        mBinding.llDiscoverMore.onNoDoubleClick {
            if (tabIndex == -1) {
                // 活动首页
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_ACTIVITY_LS)
                    .withString("classifyId", classfiyId)
                    .navigation()
            } else {
                ARouter.getInstance()
                    .build(ARouterPath.LegacyModule.LEGACY_Smrity_ACTIVITY)
                    .withInt("tabIndex", tabIndex)
                    .navigation()
            }
        }
        mBinding.llTopicMore.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_TOPIC_LIST)
                .withString("name","非遗")
                .navigation()
        }
        initViewModel()
        mBinding.llWatchStoryMore.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.LegacyModule.LEGACY_WORK_LIST_ACTIVITY)
                .withString("type", "0")
                .navigation()
        }
    }

    /**
     * 我的关注
     */
    private var watchStoryAdapter: LegacyWatchStoryAdapter? = null

    private fun renderWatchStory() {
        watchStoryAdapter = LegacyWatchStoryAdapter(this)
        mBinding.rvWatchStory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvWatchStory.adapter = watchStoryAdapter!!
        mModel.watchStoryList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.llWatchStory.visibility = View.VISIBLE
                watchStoryAdapter!!.add(it as MutableList<LegacyWatchStoryListBean>)
            }
        })
    }

    /**
     * 热门话题分类适配器
     */
    private val topicAdapter = object :
        RecyclerViewAdapter<ItemHomeTopicBinding, HomeTopicBean>(R.layout.item_home_topic) {
        override fun setVariable(
            mBinding: ItemHomeTopicBinding,
            position: Int,
            item: HomeTopicBean
        ) {
            mBinding.url = item.image
            mBinding.name = item.name
            mBinding.memberNumber = item.participateNum
            mBinding.watchNumber = item.showNum
//            mBinding.tvContentNumber.text = getString(R.string.home_topic_content_number, item.contentNum)
//            if (item.topicTypeName.isEmpty()) {
//                mBinding.tvTypeName.visibility = View.GONE
//            } else {
//                mBinding.tvTypeName.visibility = View.VISIBLE
//                mBinding.tvTypeName.text = item.topicTypeName
////                mBinding.tvTypeName.setBackgroundResource(TopicResourceType.getTypeBgIcon(item.topicTypeName))
//            }
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
            if (item.topicTypeName.isNotEmpty()) {
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

    private val storyAdapter by lazy { LegacyStoryGridAdapter(this) }

    private val intangibleHeritageStoryAdapter by lazy { GridStoryAdapter(this,GridStoryAdapter.ARENA,module = "intangible_heritage") }


    private var menuAdapter: ViewPagerAdapter? = null

    private var foodAdapter: ViewPagerAdapter? = null

    /**
     * 顶部功能
     */
    private fun renderTopMenu() {
        if(BaseApplication.appArea=="xj"){
            mBinding.vpTop.visibility=View.INVISIBLE
        }else{
            mBinding.vpTop.visibility=View.VISIBLE
        }

        menuAdapter = ViewPagerAdapter(supportFragmentManager)
        mBinding.vpTop.adapter = menuAdapter
        mBinding.topVpIndicator.binViewPager(mBinding.vpTop)

//        ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT)
//            .withString("channelCode", "systemChannel")
//            .navigation()
        val topMenuList = listOf(
            TopMenuBean(
                "非遗传承",
                R.drawable.legacy_index_entrance_fycc,
                ARouterPath.LegacyModule.LEGACY_Smrity_ACTIVITY
            ),
//            TopMenuBean("非遗地图", R.drawable.legacy_index_entrance_map, ARouterPath.LegacyModule.LEGACY_Smrity_ACTIVITY),
            TopMenuBean(
                "非遗美食",
                R.drawable.legacy_index_entrance_food,
                ARouterPath.LegacyModule.LEGACY_FOOD_LIST_ACTIVITY
            ),
            TopMenuBean(
                "非遗资讯",
                R.drawable.legacy_index_entrance_news,
                ARouterPath.LegacyModule.LEGACY_NEWS_ACTIVITY
            ),
            TopMenuBean(
                "视听非遗",
                R.drawable.legacy_index_entrance_media,
                ARouterPath.LegacyModule.LEGACY_MEDIA_ACTIVITY
            )
//            TopMenuBean("非遗商品", R.drawable.legacy_index_entrance_shop, ARouterPath.LegacyModule.LEGACY_PRODUCT_LIST_ACTIVITY)
        )


        val count = topMenuList.size
        val pageSize = 4
        val pageCount = (count + pageSize - 1) / pageSize
        for (index in 1..pageCount) {
            val start = (index - 1) * pageSize
            val max = start + pageSize
            val end = Math.min(max, count)
            val data = topMenuList.subList(start, end)
            menuAdapter?.addFragment(LegacyHomeMenuFragment(data))
        }
        menuAdapter?.notifyDataSetChanged()
        mBinding.topVpIndicator.total = pageCount
        mBinding.topVpIndicator.visibility = if (pageCount > 1) {
            View.VISIBLE
        } else {
            View.GONE
        }
        mBinding.vpTop.offscreenPageLimit = pageCount

    }

    /**
     * 非遗美食
     */
    private fun renderFood(foods: List<LegacyFoodBean>) {

        val count = foods.size
        val pageSize = 4
        val pageCount = (count + pageSize - 1) / pageSize
        for (index in 1..pageCount) {
            val start = (index - 1) * pageSize
            val max = start + pageSize
            val end = max.coerceAtMost(count)
            val data = foods.subList(start, end)

            val arrayList = ArrayList(data.filter { it.recommendChannelHomePage == 1 })
            if(pageCount==1 && (arrayList!=null || arrayList.size==0)){
                mBinding.llFoodMore.visibility=  View.GONE
                mBinding.vpFood.visibility = View.GONE
            }else{
                mBinding.llFoodMore.visibility = View.VISIBLE
                mBinding.vpFood.visibility = View.VISIBLE
                foodAdapter!!.addFragment(LegacyHomeFoodFragment.newInstance(arrayList))
            }

        }
        if(foodAdapter!=null){
            foodAdapter!!.notifyDataSetChanged()
        }
        mBinding.vpFoodIndicator.total = pageCount
        mBinding.vpFoodIndicator.visibility = if (pageCount > 1) {
            View.VISIBLE
        } else {
            View.GONE
        }
        mBinding.vpFood.offscreenPageLimit = pageCount
    }

    /**
     * 渲染发现非遗模块
     */
    private fun renderDiscover() {

        val transaction = supportFragmentManager.beginTransaction()
        if (isRenderActivity) {
            heritageHomeActivityFragment = HeritageHomeActivityFragment.newIntance(classfiyId ?: "")
            currentFragment = heritageHomeActivityFragment
            transaction.add(R.id.fl_content, heritageHomeActivityFragment!!)
            tabIndex = -1
        } else {
            heritagePeopleFragment = HeritageHomePeopleFragment.newInstance()
            currentFragment = heritagePeopleFragment
            transaction.add(R.id.fl_content, heritagePeopleFragment!!)
            tabIndex = 0
        }
        transaction.commit()
    }


    var heritageItemFragment: HeritageHomeItemFragment? = null
    var heritagePeopleFragment: HeritageHomePeopleFragment? = null
    var heritageExperienceBaseFragment: HeritageHomeExperienceBaseFragment? = null
    var heritageTeachingBaseFragment: HeritageHomeTeachingBaseFragment? = null
    var heritageHomeActivityFragment: HeritageHomeActivityFragment? = null
    var currentFragment: Fragment? = null
    var tabIndex = 0
    /**
     * 切换发现非遗类型
     */
    fun switchFragment(type: String, position: Int) {
        tabIndex = position
        val transaction = supportFragmentManager.beginTransaction()
        when (type) {
            "heritageActivity" -> {
                if (heritageHomeActivityFragment != null) {
                    if (currentFragment is HeritageHomeActivityFragment) {
                        return
                    }
                } else {
                    classfiyId?.let {
                        heritageHomeActivityFragment =
                            HeritageHomeActivityFragment.newIntance(it)
                    }

                }
                currentFragment?.let { transaction.hide(it) }
                transaction.show(heritageHomeActivityFragment!!)
                currentFragment = heritageHomeActivityFragment
                tabIndex = -1
            }
            "heritagePeople" -> {
                if (heritagePeopleFragment != null) {
                    if (currentFragment is HeritageHomePeopleFragment) {
                        return
                    }
                } else {
                    heritagePeopleFragment = HeritageHomePeopleFragment.newInstance()
                    if (isRenderActivity)
                        transaction.add(R.id.fl_content, heritagePeopleFragment!!)
                }
                currentFragment?.let { transaction.hide(it) }
                transaction.show(heritagePeopleFragment!!)
                currentFragment = heritagePeopleFragment
                tabIndex = 1
            }
            "heritageItem" -> {
                if (heritageItemFragment != null) {
                    if (currentFragment is HeritageHomeItemFragment) {
                        return
                    }
                } else {
                    heritageItemFragment = HeritageHomeItemFragment.newInstance()
                    transaction.add(R.id.fl_content, heritageItemFragment!!)
                }
                currentFragment?.let { transaction.hide(it) }
                transaction.show(heritageItemFragment!!)
                currentFragment = heritageItemFragment
                tabIndex = 0
            }
            "heritageExperienceBase" -> {
                if (heritageExperienceBaseFragment != null) {
                    if (currentFragment is HeritageHomeExperienceBaseFragment) {
                        return
                    }
                } else {
                    heritageExperienceBaseFragment =
                        HeritageHomeExperienceBaseFragment.newInstance()
                    transaction.add(R.id.fl_content, heritageExperienceBaseFragment!!)
                }
                currentFragment?.let { transaction.hide(it) }
                transaction.show(heritageExperienceBaseFragment!!)
                currentFragment = heritageExperienceBaseFragment
                tabIndex = 2
            }
            "heritageTeachingBase" -> {
                if (heritageTeachingBaseFragment != null) {
                    if (currentFragment is HeritageHomeTeachingBaseFragment) {
                        return
                    }
                } else {
                    heritageTeachingBaseFragment = HeritageHomeTeachingBaseFragment.newInstance()
                    transaction.add(R.id.fl_content, heritageTeachingBaseFragment!!)
                }
                currentFragment?.let { transaction.hide(it) }
                transaction.show(heritageTeachingBaseFragment!!)
                currentFragment = heritageTeachingBaseFragment
                tabIndex = 3
            }
        }
        transaction.commit()
    }

    /**
     * 发现非遗类型
     */
    private fun renderDiscoverType() {
//        val typeList: MutableList<DiscoverTypeBean> = mutableListOf(
//            DiscoverTypeBean("非遗活动", "heritageActivity", false),
//            DiscoverTypeBean("非遗传承人", "heritagePeople", false),
//            DiscoverTypeBean("非遗项目", "heritageItem", false),
//            DiscoverTypeBean("项目体验基地", "heritageExperienceBase", false),
//            DiscoverTypeBean("传习保护示范基地", "heritageTeachingBase", false)
//        )

        val typeList: MutableList<DiscoverTypeBean> = mutableListOf(
            DiscoverTypeBean("非遗活动", "heritageActivity", false),
            DiscoverTypeBean("非遗传承人", "heritagePeople", false),
            DiscoverTypeBean("非遗项目", "heritageItem", false)
        )
        if(BaseApplication.appArea!="ws" && BaseApplication.appArea!="xj"){
            typeList.add( DiscoverTypeBean("项目体验基地", "heritageExperienceBase", false))
            typeList.add(   DiscoverTypeBean("传习保护示范基地", "heritageTeachingBase", false))
        }
        if (isRenderActivity) {
            typeList[0].select = true
        } else {
            typeList.removeAt(0)
            typeList[0].select = true
        }
        discoverTypeAdapter.currentType = typeList[0]
        discoverTypeAdapter.add(typeList as MutableList<DiscoverTypeBean>)
        mBinding.rvDiscoverType.layoutManager =
            LinearLayoutManager(this@LegacyHomeActivity, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvDiscoverType.adapter = discoverTypeAdapter
        discoverTypeAdapter.notifyDataSetChanged()
        renderDiscover()
    }

    private fun initViewModel() {

        mModel.topImgData.observe(this, Observer {

            if(it.name.isNullOrBlank()){
                mBinding.tvNameAfter.visibility=View.GONE
                mBinding.tvNamePre.tv_name_pre.visibility=View.GONE
            }else{
                mBinding.tvNameAfter.visibility=View.VISIBLE
                mBinding.tvNamePre.visibility=View.VISIBLE
            }
            if (it.name.length >15) {
                mBinding.tvName.text = it.name.substring(0, 15)
            } else mBinding.tvName.text = it.name
            try {
                mBinding.url = it.images.split(",")[0]
            } catch (e: Exception) {
            }
        })

        mBinding.clRoot.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.LegacyModule.LEGACY_Smrity_DETAIL_ACTIVITY)
                .withString("id", mModel.topImgData.value?.id.toString())
                .navigation()
        }
        mModel.activityClassifies.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                it.forEach { obj ->
                    if (obj != null && !obj.labelName.isNullOrEmpty()) {
                        if (obj!!.labelName == "非遗" && obj!!.activityCount > 0) {
                            isRenderActivity = true
                            classfiyId = obj.id
                        }
                    }
                }
            }
            renderDiscoverType()
        })

        // 广告
        mModel.homeAdData.observe(this, Observer {
            val images = mutableListOf<String>()
            if (it.adInfo.isNotEmpty()) {
                val homeAd = it.adInfo
                mBinding.bannerTopAd.visibility = View.VISIBLE
                for (img in it.adInfo) {
                    images.add(img.imgUrl)
                }
                mBinding.bannerTopAd
                    .setPages(object : CBViewHolderCreator {
                        override fun createHolder(itemView: View?): Holder<*> {
                            return BaseBannerImageHolder(itemView!!)
                        }

                        override fun getLayoutId(): Int {
                            return R.layout.legacy_module_item_vp_home_ad
                        }
                    }, images)
                    .setCanLoop(false)
                    .setPointViewVisible(true)
                    .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                    .setOnItemClickListener { pos ->
                        // 跳转事件
                        ARouter.getInstance()
                            .build(ARouterPath.Provider.WEB_ACTIVITY)
                            .withString("mTitle", "详情")
                            .withString("html", homeAd[pos].jumpUrl)
                            .navigation()
                    }
                    .setPageIndicator(null)
                    .startTurning(3000)
            }

        })

        // 非遗美食
        mModel.homeFoodList.observe(this, Observer {
            if (it.isNotEmpty()) {
                renderFood(it)
            }
        })

        // 话题
        mModel.topicList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                topicAdapter.clear()
                topicAdapter.add(it as MutableList<HomeTopicBean>)
                mBinding.llTopicMore.visibility = View.VISIBLE
            } else {
                mBinding.llTopicMore.visibility = View.GONE
                mBinding.rvTopic.visibility = View.GONE
            }
        })

        // 故事标签
        mModel.homeStoryTagList.observe(this, Observer { storyTagList ->

            if (storyTagList.isNotEmpty())
                mBinding.llStoryMore.visibility = View.VISIBLE

            mBinding.rvStoryType.isVisible = storyTagList.isNotEmpty()
            if (storyTagList.isEmpty()) return@Observer
            mBinding.rvStoryType.linear {
                orientation = LinearLayoutManager.HORIZONTAL

                storyTagList.forEach { tagItem ->

                    itemDsl {
                        xml(R.layout.legacy_module_item_story_tag)
                        render {
                            val binding = DataBindingUtil.bind<LegacyModuleItemStoryTagBinding>(it)
                            binding?.tag = tagItem
                            it.onNoDoubleClick {
                                ARouter.getInstance()
                                    .build(MainARouterPath.MAIN_STORY_TAG_DETAIL)
                                    .withString("id", tagItem.id.toString())
                                    .withString("module","intangible_heritage")
                                    .navigation()
                            }
                        }
                    }

                }
            }
        })

        // 故事列表
        mModel.homeStoryList.observe(this, Observer { storyList ->
            if (storyList.isNotEmpty())
                mBinding.llStoryMore.visibility = View.VISIBLE
            mBinding.rvStory.isVisible = storyList.isNotEmpty()
            if (storyList.isEmpty()) return@Observer
            storyAdapter.clearNotify()
            storyAdapter.add(storyList.toMutableList())
        })


        // 非遗故事
        mModel.intangibleHeritageStoryLivedData.observe(this, Observer {
            if (it.isNotEmpty()){
                mBinding.llStoryMore.visibility = View.VISIBLE
                mBinding.rvStory.isVisible = it.isNotEmpty()
                intangibleHeritageStoryAdapter.clearNotify()
                intangibleHeritageStoryAdapter.add(it.toMutableList())
            }
        })

    }


    override fun initData() {
        mModel.getTopImgData()
        mModel.getWatchStory()
        mModel.getHomeAdInfo()
        mModel.getHomeFoodList()
        mModel.getHomeDiscoverList()
        mModel.getHomeStoryTagList()
//        mModel.getHomeStoryList()
        mModel.getIntangibleHeritageStory()
        mModel.getHomeTopic()
    }


    // 发现非遗类型
    private val discoverTypeAdapter = object :
        RecyclerViewAdapter<HomeItemDiscoverTypeBinding, DiscoverTypeBean>(R.layout.home_item_discover_type) {
        var currentType: DiscoverTypeBean? = null
        override fun setVariable(
            mBinding: HomeItemDiscoverTypeBinding,
            position: Int,
            item: DiscoverTypeBean
        ) {
            mBinding.label.text = item.name
            mBinding.label.isSelected = item.select

            mBinding.root.onNoDoubleClick {
                if (currentType != item) {
                    item.select = true
                    if (currentType != null) {
                        currentType!!.select = false
                    }
                    currentType = item
                    selectToTypePos(position)
//                    showLoadingDialog()
                    switchFragment(item.type, position)
                    notifyDataSetChanged()
                }
            }
        }
    }

    /**
     *  选择点击的class
     */
    fun selectToTypePos(position: Int) {
        mBinding?.rvDiscoverType.smoothScrollToPosition(position)
    }
}

