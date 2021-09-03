package com.daqsoft.travelCultureModule.hotActivity

import android.graphics.Rect
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.utils.Utils
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.FragActivityIndexBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.adapter.SCPopularActivitiesAdapter
import com.daqsoft.provider.bean.ActivityOverViewTypes
import com.daqsoft.provider.bean.Classify
import com.daqsoft.provider.bean.HomeAd
import com.daqsoft.provider.bean.SubChanelChildBean
import com.daqsoft.provider.businessview.adapter.ProviderAcitivtyPageV1Adapter
import com.daqsoft.provider.businessview.adapter.ProviderActSubTabAdapter
import com.daqsoft.provider.businessview.adapter.ProviderActTabAdapter
import com.daqsoft.provider.businessview.event.LoginStatusEvent
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.provider.view.convenientbanner.ConvenientBanner
import com.daqsoft.provider.view.convenientbanner.holder.AdvImageHolder
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.travelCultureModule.clubActivity.bean.ClubZixunBean
import com.daqsoft.travelCultureModule.contentActivity.ContentLsV1Adapter
import com.daqsoft.travelCultureModule.contentActivity.WonderfulMomentAdapter
import com.daqsoft.travelCultureModule.hotActivity.adapter.ActivityOverViewTypesAdapter
import com.daqsoft.travelCultureModule.hotActivity.adapter.HotActivityPagerAdapter
import com.daqsoft.travelCultureModule.hotActivity.model.ActivityIndexViewModel
import com.daqsoft.provider.uiTemplate.titleBar.activity.ActivityPageTransFormer
import com.daqsoft.travelCultureModule.hotActivity.adapter.ActivityOverViewOldTypesAdapter
import com.daqsoft.travelCultureModule.hotActivity.adapter.EventCalendarAdapter
import kotlinx.android.synthetic.main.frag_activity_index.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


/**
 * @Description
 * @ClassName   ServiceIndexFragment
 * @Author      luoyi
 * @Time        2020/6/9 9:37
 */
class ActivityIndexFragment : BaseFragment<FragActivityIndexBinding, ActivityIndexViewModel>() {

    /**
     * 推荐活动适配器
     */
    private var recommendActivitisAdapter: SCPopularActivitiesAdapter? = null

    /**
     * 推荐类型适配器
     */
    private var recommendTabAdapter: ProviderActTabAdapter? = null

    /**
     * 活动日历
     */
    private var activitiesDailyAdapter: EventCalendarAdapter? = null

    /**
     * 活动类型
     */
    private var activityOverViewTypesAdapter: ActivityOverViewOldTypesAdapter? = null

    /**
     * 活动专题类型
     */
    private var activitySubTabTypeAdapter: ProviderActSubTabAdapter? = null

    /**
     * 精彩回归
     */
    private val contentLsV1Adapter: WonderfulMomentAdapter by lazy {
        WonderfulMomentAdapter()
    }


    override fun getLayout(): Int {
        return R.layout.frag_activity_index
    }

    override fun injectVm(): Class<ActivityIndexViewModel> {
        return ActivityIndexViewModel::class.java
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        setDateInfo()
        initViewModel()
        initViewEvent()

        if(BaseApplication.appArea=="ws")
            mBinding.tvTitle.visibility=View.VISIBLE
        else
            mBinding.tvTitle.visibility=View.GONE
        contentLsV1Adapter?.emptyViewShow = false
        mBinding.vActivityWoderfulMoment.rvActivityWonderfuls.layoutManager = LinearLayoutManager(
            context!!, LinearLayoutManager.VERTICAL,
            false
        )
        mBinding.vActivityWoderfulMoment.rvActivityWonderfuls.adapter = contentLsV1Adapter
        mBinding.vActivityWoderfulMoment.rvActivityWonderfuls.addItemDecoration(object :
            RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val point = parent.getChildAdapterPosition(view)
                if (point != 0) {
                    outRect.top = com.daqsoft.baselib.utils.Utils.dip2px(parent.context, 30F).toInt()
                }
            }
        })

        // 分类
        activitySubTabTypeAdapter = ProviderActSubTabAdapter(context!!).apply {
            this.onItemClickListener = object : ProviderActSubTabAdapter.OnItemClickListener {
                override fun onItemClick(pos: Int) {
                    activitySubTabTypeAdapter?.let {
                        mModel.getZixunList(it.getData()[pos].channelCode)
                    }
                }
            }
        }
        activitySubTabTypeAdapter?.emptyViewShow = false
        mBinding.vActivityWoderfulMoment.rvActivityWonderfulTypes.layoutManager =
            LinearLayoutManager(
                context!!,
                LinearLayoutManager.HORIZONTAL, false
            )
        mBinding.vActivityWoderfulMoment.rvActivityWonderfulTypes.adapter =
            activitySubTabTypeAdapter
        // 更多
        mBinding.vActivityWoderfulMoment.ctvWonderfulMomentMore.onNoDoubleClick {
            ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT)
                .withString("channelCode", "jcsj")
                .navigation()
        }
    }


    override fun initData() {
        showLoadingDialog()
        // 获取顶部广告数据
        mModel.getActivityTopAds()
        // 获取推荐分类
        mModel.getRecommentClassify()
        // 获取全部分类数据
        mModel.getRecommentActivities("")
        // 获取活动日历数据
        var date = DateUtil.getDqDateString(Date())
        mModel.getActivitiesBycalendar(date, date)
        // 获取活动概览数据
        var date1 = DateUtil.getDqDateString("yyyy-MM", Date())
        mModel.getActivitiesOverView(date1, date1)

        mModel.getActivitySubSet()
    }

    private fun initViewEvent() {
        mBinding.tvSearchActivities.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_ALL_SEARCH)
                .navigation()
        }
        mBinding.edtSearchActivities.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_ALL_SEARCH)
                .navigation()
        }
        mBinding.ivMap.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_ACTIVITY_MAP)
                .navigation()
        }
        mBinding.vActivitiesTopToSerach.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_ACTIVITY_MAP)
                .navigation()
        }

        mBinding.vActivityOverview.ctvOverviewMore.onNoDoubleClick {
            ARouter.getInstance().build(MainARouterPath.MAIN_ACTIVITY_OVER_VIEW)
                .navigation()
        }
        mBinding.ctvActivityMore.onNoDoubleClick {
            ARouter.getInstance().build(MainARouterPath.MAIN_ACTIVITY_LS)
                .navigation()
        }
    }

    /**
     * 设置日期
     */
    private fun setDateInfo() {
        var calendar = Calendar.getInstance()
        val mYear: Int = calendar.get(Calendar.YEAR)
        val mMonth: Int = calendar.get(Calendar.MONTH) + 1
        val mDay: Int = calendar.get(Calendar.DAY_OF_MONTH)
        var mothStr = if (mMonth < 10) {
            "0${mMonth}月"
        } else {
            "${mMonth}月"
        }
        var dayStr = if (mDay < 10) {
            "0${mDay}"
        } else {
            "${mDay}"
        }
        mBinding.tvActivityDay.text = dayStr
        mBinding.tvActivityMoth.text = mothStr
        mBinding.tvActivityYear.text = "" + mYear
        mBinding.vActivityOverview.tvActivityOverviewMonth.text = if (mMonth < 10) {
            "0${mMonth}"
        } else {
            "${mMonth}"
        }
        mBinding.vActivityOverview.tvActivityOverviewYear.text = "" + mYear
    }

    private fun initViewModel() {
        // 推荐活动列表
        mModel.recommentActivitiesLd.observe(this, Observer {
            dissMissLoadingDialog()
            if (it != null) {
                recommendActivitisAdapter = SCPopularActivitiesAdapter().apply {
                    isShowCollect = false
                }
                mBinding.vpRecommendActivities.adapter = recommendActivitisAdapter
                mBinding.vpRecommendActivities.setPageTransformer(false, ActivityPageTransFormer())
                recommendActivitisAdapter?.menus?.clear()
                recommendActivitisAdapter?.menus?.addAll(it)
                recommendActivitisAdapter?.notifyDataSetChanged()
                mBinding.vpRecommendActivities.pageMargin =
                    resources.getDimensionPixelSize(R.dimen.dp_8)
                mBinding.llTj.visibility = View.VISIBLE
            } else {
                mBinding.llTj.visibility = View.GONE
            }
        })
        // 推荐活动类型
        mModel.recommentClassifyLd.observe(this, Observer {
            dissMissLoadingDialog()
            if (!it.isNullOrEmpty()) {
                mBinding.tvRecommdActivity.visibility = View.VISIBLE
                mBinding.rvActivityTypes.visibility = View.VISIBLE
//                mBinding.llRoot.visibility = View.VISIBLE
                setRecommendClassifys(it)
            } else {
                mBinding.tvRecommdActivity.visibility = View.GONE
                mBinding.rvActivityTypes.visibility = View.GONE
//                mBinding.llRoot.visibility = View.GONE
            }
        })
        // 日历活动
        mModel.calenderActivitiesLd.observe(this, Observer {
            dissMissLoadingDialog()
            if (!it.isNullOrEmpty()) {
                activitiesDailyAdapter = EventCalendarAdapter()
                mBinding.rvEventCalendar.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                mBinding.rvEventCalendar.adapter = activitiesDailyAdapter.apply {
                    this?.onHotActPagerClickListener =
                        object : EventCalendarAdapter.OnHotActPagerClickListener {
                            override fun onCollect(activityId: String, position: Int) {
                                showLoadingDialog()
                                mModel.collection(activityId, position)
                            }

                            override fun onCanceCollect(activityId: String, position: Int) {
                                showLoadingDialog()
                                mModel.collectionCancel(activityId, position)
                            }
                        }
                }
                activitiesDailyAdapter?.clear()
                activitiesDailyAdapter?.add(it)
                mBinding.llvDailyActivities.visibility = View.VISIBLE
            } else {
                mBinding.llvDailyActivities.visibility = View.GONE
            }
        })
        // 活动概览
        mModel.activityOverViewLd.observe(this, Observer {
            dissMissLoadingDialog()
            if (it != null && it.total > 0) {
                mBinding.vActivityOverview.tvAllProviceActivityNum.text = "${it.total}场"
                if (!it.result.isNullOrEmpty()) {
                    mBinding.vActivityOverview.rvActivityOverviews.visibility = View.VISIBLE
                    activityOverViewTypesAdapter = ActivityOverViewOldTypesAdapter()
                    activityOverViewTypesAdapter?.emptyViewShow = false
                    activityOverViewTypesAdapter?.onActivityOverViewItemListener =
                        object : ActivityOverViewOldTypesAdapter.
                        OnActivityOverViewItemListener {
                            override fun onItemClick(item: ActivityOverViewTypes) {
                                var times = DateUtil.getDqDateString("yyyy-MM", Date())
                                ARouter.getInstance().build(MainARouterPath.MAIN_ACTIVITY_GL_LS)
                                    .withString("classifyId", item.id)
                                    .withString("times",times)
                                    .navigation()
                            }

                        }
                    mBinding.vActivityOverview.rvActivityOverviews.layoutManager =
                        GridLayoutManager(context!!, 3, GridLayoutManager.VERTICAL, false)
                    mBinding.vActivityOverview.rvActivityOverviews.adapter =
                        activityOverViewTypesAdapter
                    activityOverViewTypesAdapter?.clear()
                    activityOverViewTypesAdapter?.add(it.result)
                    var isShowAll: Boolean = it.result.size <= 9
                    activityOverViewTypesAdapter?.isShowAll = isShowAll
                    if (isShowAll) {
                        mBinding.vActivityOverview?.tvActivityOverviewsMore?.visibility = View.GONE
                    } else {
                        mBinding.vActivityOverview?.tvActivityOverviewsMore?.visibility =
                            View.VISIBLE
                    }
                    mBinding.vActivityOverview?.tvActivityOverviewsMore?.onNoDoubleClick {
                        if (activityOverViewTypesAdapter!!.isShowAll) {
                            activityOverViewTypesAdapter!!.isShowAll = false
                            val drawable2 =
                                resources.getDrawable(R.mipmap.activity_index_icon_arrow_more)
                            drawable2.setBounds(
                                0,
                                0,
                                drawable2.minimumWidth,
                                drawable2.minimumHeight
                            )
                            mBinding.vActivityOverview?.tvActivityOverviewsMore?.setCompoundDrawables(
                                null,
                                null,
                                drawable2,
                                null
                            )
                        } else {
                            activityOverViewTypesAdapter!!.isShowAll = true
                            val drawable2 =
                                resources.getDrawable(R.mipmap.activity_index_icon_arrow_more)
                            drawable2.setBounds(
                                0,
                                0,
                                drawable2.minimumWidth,
                                drawable2.minimumHeight
                            )
                            mBinding.vActivityOverview?.tvActivityOverviewsMore?.setCompoundDrawables(
                                null,
                                null,
                                drawable2,
                                null
                            )
                            activityOverViewTypesAdapter!!.notifyDataSetChanged()
                            mBinding.vActivityOverview?.tvActivityOverviewsMore?.visibility =
                                View.GONE
                        }
                    }
                } else {
                    mBinding.vActivityOverview.rvActivityOverviews.visibility = View.GONE
                }
                v_activity_overview.visibility = View.VISIBLE
            } else {
                v_activity_overview.visibility = View.GONE
            }
        })

        mModel.topAdsLiveData.observe(this, Observer {

            setTopAdvBanner(it)
        })
        // 精彩瞬间
        mModel.activitySubSetLd.observe(this, Observer {
            if (it == null || it.subset.isNullOrEmpty()) {
                v_activity_woderful_moment.visibility = View.GONE
            } else {
                v_activity_woderful_moment.visibility = View.VISIBLE

                if (!it.subset.isNullOrEmpty()) {
                    activitySubTabTypeAdapter?.clear()
                    activitySubTabTypeAdapter?.add(it.subset as MutableList<SubChanelChildBean>)
                    var child = it.subset[0]
                    if (child != null) {
                        mModel.getZixunList(child.channelCode + "")
                    }
                }
            }
        })
        mModel.zixunList.observe(this, Observer {
            setZiXunList(it)
        })
        mModel.collectLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            activitiesDailyAdapter?.getData()?.get(it)?.userResourceStatus?.collectionStatus = true
            activitiesDailyAdapter?.notifyDataSetChanged()
        })
        mModel.canceCollectLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            activitiesDailyAdapter?.getData()?.get(it)?.userResourceStatus?.collectionStatus = false
            activitiesDailyAdapter?.notifyDataSetChanged()
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })
    }

    private fun setZiXunList(it: MutableList<ClubZixunBean>?) {
        if (!it.isNullOrEmpty()) {
            contentLsV1Adapter?.emptyViewShow = false
            contentLsV1Adapter?.clear()
            contentLsV1Adapter?.add(it)
        } else {
            contentLsV1Adapter?.clear()
            contentLsV1Adapter?.emptyViewShow = true
        }
    }


    /**
     * 设置推荐类型数据
     * @param it 推荐类型数据集
     */
    private fun setRecommendClassifys(it: MutableList<Classify>) {
        var classifies: MutableList<Classify> = mutableListOf()
        classifies.add(0, Classify(0, "", "全部", true))
        if (!it.isNullOrEmpty()) {
            classifies.addAll(it)
        }
        recommendTabAdapter = ProviderActTabAdapter(context!!)
        recommendTabAdapter?.emptyViewShow = false
        mBinding.rvActivityTypes.layoutManager =
            LinearLayoutManager(context!!, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvActivityTypes.adapter = recommendTabAdapter
        recommendTabAdapter?.clear()
        recommendTabAdapter?.add(classifies)
        recommendTabAdapter?.onItemClickListener =
            object : ProviderActTabAdapter.OnItemClickListener {
                override fun onItemClick(pos: Int) {
                    if (pos < classifies.size) {
                        var item = classifies[pos]
                        if (item != null) {
                            mModel.getRecommentActivities(item.id)
                        }
                    }
                }
            }
    }

    /**
     * 设置顶部banner显示
     */
    private fun setTopAdvBanner(it: HomeAd?) {
        if (it == null || it.adInfo.isNullOrEmpty()) {
            mBinding.vActivityListNoAdv.visibility = View.VISIBLE
            mBinding.vActivityHaveAdv.visibility = View.GONE
        } else {
            mBinding.cbanerActivitiesTopAdv
                .setPages(object : CBViewHolderCreator {
                    override fun createHolder(itemView: View?): Holder<*> {
                        return AdvImageHolder(itemView!!)
                    }

                    override fun getLayoutId(): Int {
                        return com.daqsoft.provider.R.layout.layout_common_adv
                    }
                }, it.adInfo)
                .setCanLoop(it.adInfo.size > 1)
                .setPointViewVisible(it.adInfo.size > 1)
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                .setOnItemClickListener { it2 ->
                    run {
                        // 跳转事件
                        MenuJumpUtils.adJump(it.adInfo[it2])
                    }

                }
                .setPageIndicator(null)
                .startTurning(3000)

            if (it.adInfo.isNotEmpty()) {
                mBinding.vActivityListNoAdv.visibility = View.GONE
                mBinding.vActivityHaveAdv.visibility = View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (mBinding.vActivityHaveAdv.isVisible) {
            mBinding.cbanerActivitiesTopAdv.startTurning(3000)
        }
    }

    override fun onPause() {
        super.onPause()
        if (mBinding.vActivityHaveAdv.isVisible) {
            mBinding.cbanerActivitiesTopAdv.stopTurning()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun updateDataStatus(event: LoginStatusEvent) {
        if (event != null) {
            // 获取活动日历数据
            var date = DateUtil.getDqDateString(Date())
            mModel.getActivitiesBycalendar(date, date)
        }
    }
}