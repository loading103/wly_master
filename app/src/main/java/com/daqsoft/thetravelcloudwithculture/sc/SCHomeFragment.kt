package com.daqsoft.thetravelcloudwithculture.sc


import android.Manifest
import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.PandaRefreshHeader
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.bean.HomeAd
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.provider.view.convenientbanner.ConvenientBanner
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.FragmentHomeScBinding
import com.daqsoft.thetravelcloudwithculture.databinding.ItemHomeTopMenuBinding
import com.daqsoft.thetravelcloudwithculture.home.bean.CityCardBean
import com.daqsoft.provider.bean.HomeMenu
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.thetravelcloudwithculture.sc.adapter.HomeMenuHolder
import com.daqsoft.thetravelcloudwithculture.sc.fragment.SCActivityTopicStoryFragment
import com.daqsoft.thetravelcloudwithculture.sc.fragment.SCHomeBannerAndHappyFragment
import com.daqsoft.thetravelcloudwithculture.sc.fragment.SCTripBranchNextFragment
import com.daqsoft.thetravelcloudwithculture.ui.event.RefreshHomeEvent
import com.daqsoft.thetravelcloudwithculture.ui.viewholder.HomeTopTopViewHolder
import com.daqsoft.thetravelcloudwithculture.ui.viewholder.HomeTopViewHolder
import com.daqsoft.thetravelcloudwithculture.ui.vm.SCHomeFragmentVm
import com.daqsoft.travelCultureModule.itrobot.view.ItRobotWindowView
import com.daqsoft.travelCultureModule.itrobot.view.ScItRobotWindowView
import com.google.android.material.appbar.AppBarLayout
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.util.concurrent.TimeUnit


/**
 * 首页界面
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-10-22
 * @since JDK 1.8.0_191
 */
class SCHomeFragment : BaseFragment<FragmentHomeScBinding, SCHomeFragmentVm>() {
    private val menus = mutableListOf<HomeMenu>()

    private var tripBranchNextFragment: SCTripBranchNextFragment? = null

    /**
     *  轮询机器人信息工具
     *   每隔 5s
     */
    var cutdownDisable: Disposable? = null

    /**
     * 5s 后隐藏机器人
     */
    var cutDownHideItRobot: Disposable? = null

    /**
     * 页面是否可见
     */
    var isPageVisible: Boolean = false

    /**
     * 第一次显示机器人
     */
    var isFirstShowItRobot: Boolean = true


    var permissions: RxPermissions? = null

    var mAdCode: String? = ""

    var appIndexLog: HomeAd? = null
    var isNeedLoading: Boolean = true
    lateinit var refreshHeader: PandaRefreshHeader

    override fun getLayout(): Int = R.layout.fragment_home_sc
    var lis: AppBarLayout.OnOffsetChangedListener? = null
    override fun initData() {
        mModel.getAPPMenus(Constant.HOME_MENU_TOP, isNeedLoading)
        mModel.getTzggList()
        GaoDeLocation.getInstance()
            .init(context, object : GaoDeLocation.OnGetCurrentLocationLisner {

                override fun onResult(
                    adCode: String, result: String, lat_: Double,
                    lon_: Double, adcode: String
                ) {
                    mAdCode = adCode
                    mModel.getAppIndexAds()
                    mModel.getAppIndexAdsTop()
                    tripBranchNextFragment?.getCityList()
                    homeBannerAndHappyFragment?.getFoundAround(lat_, lon_)

                }

                override fun onError(errormsg: String) {
                    mAdCode = "510000"
                    mModel.getAppIndexAds()
                    tripBranchNextFragment?.getCityList()
//                homeBannerAndHappyFragment?.getFoundAround(lat_,lon_)
                    Timber.e("获取定位位置出错了")
                }
            })


    }

    /**
     * 顶部菜单适配器
     */
    private val topMenuAdapter = object :
        RecyclerViewAdapter<ItemHomeTopMenuBinding, HomeMenu>(
            R.layout.item_home_top_menu
        ) {
        override fun setVariable(mBinding: ItemHomeTopMenuBinding, position: Int, item: HomeMenu) {
            mBinding.url = item.unSelectIcon
        }

    }
    var homeBannerAndHappyFragment: SCHomeBannerAndHappyFragment? = null
    override fun initView() {
        permissions = RxPermissions(this)
        // 搜索背景透明度

        isHideAnother = false
        // 一下为替换三个fragment
        homeBannerAndHappyFragment = SCHomeBannerAndHappyFragment()
        tripBranchNextFragment = SCTripBranchNextFragment()
        val activityTopicStoryFragment = SCActivityTopicStoryFragment()
        transactFragment(R.id.home_menu, homeBannerAndHappyFragment!!)
        transactFragment(R.id.trip_next, tripBranchNextFragment!!)
        transactFragment(R.id.activity_story, activityTopicStoryFragment)
        mBinding.tvSearch.setOnClickListener {
            ARouter.getInstance().build(MainARouterPath.MAIN_ALL_SEARCH)
                .navigation()
        }
//        mBinding.mSwipeRefreshLayout.setOnRefreshListener {
//            mBinding.mSwipeRefreshLayout.isRefreshing = true
//            initData()
//            EventBus.getDefault().post(RefreshHomeEvent())
//        }

        mBinding.smartRefreshLayout.setOnRefreshListener {
            isNeedLoading = false
            initData()
            EventBus.getDefault().post(RefreshHomeEvent())
        }
        try {
            mBinding.smartRefreshLayout.setRefreshHeader(PandaRefreshHeader(BaseApplication.context))
        } catch (e: java.lang.Exception) {

        }

        mBinding.imgScanCode.onNoDoubleClick {
            if (permissions != null) {
                permissions?.request(Manifest.permission.CAMERA)
                    ?.subscribe {
                        if (it) {
                            ARouter.getInstance().build(ARouterPath.Provider.PROVIDER_SCAN_ACTIVITY)
                                .withString("SCAN_MODE", "QR_CODE_MODE")
                                .navigation()
                        } else {
                            ToastUtils.showMessage("非常抱歉，未授权应用获取摄像头权限，无法正常使用二维码扫码功能~")
                        }
                    }

            }
        }
        mModel.tzggContentLd.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.rbvHomes.visibility = View.VISIBLE
                mBinding.rbvHomes.setData(it)
            } else {
                mBinding.rbvHomes.visibility = View.GONE
            }
        })

        mBinding.itrobotScHomeWindow.onClickHideListener =
            object : ScItRobotWindowView.OnClickHideListener {
                override fun onClickHide() {
                    mBinding.imgShowRobot.visibility = View.VISIBLE
                    mBinding.itrobotScHomeWindow.visibility = View.GONE
                }
            }
        mBinding.imgShowRobot.onNoDoubleClick {
            if(mBinding.itrobotScHomeWindow.isCanVisable) {
                mBinding.itrobotScHomeWindow.visibility = View.VISIBLE
                mBinding.imgShowRobot.visibility = View.GONE
            }else{
                MainARouterPath.toItRobotPage()
            }
        }
        initViewModel()
    }

    private fun initViewModel() {
        mModel.itRobotInfoLiveData.observe(this, Observer {
            if (it != null) {

                if (!it.greetings.isNullOrEmpty()) {
                    mBinding.itrobotScHomeWindow.setData(it)
                    mBinding.itrobotScHomeWindow.visibility = View.VISIBLE
                    mBinding.imgShowRobot.visibility = View.GONE
                    hitItRobotInfo()
                }
            }
            isPageVisible = true
            getItRobotInfo()

        })
    }


    override fun injectVm(): Class<SCHomeFragmentVm> = SCHomeFragmentVm::class.java

    override fun notifyData() {
        super.notifyData()
        mModel.topMenus.observe(this, Observer {
            //            mBinding.mSwipeRefreshLayout.isRefreshing = false
            mBinding.smartRefreshLayout.finishRefresh(true)
            menus.clear()
            menus.addAll(it)
            var count = menus.size
            var pageSize = 10
            var pageCount = (count + pageSize - 1) / pageSize
            var menusDatas: MutableList<MutableList<HomeMenu>> = mutableListOf()
            for (index in 1..pageCount) {
                var start = (index - 1) * pageSize
                var max = start + pageSize
                var end = if (max > count) {
                    count
                } else {
                    max
                }
                var data = menus.subList(start, end)
                menusDatas.add(data)
            }
            mBinding.circleIndicator.total = pageCount
            mBinding.circleIndicator.setSteps(0)

            mBinding.cbrMenus.setPages(object : CBViewHolderCreator {
                override fun createHolder(itemView: View?): Holder<*> {
                    return HomeMenuHolder(itemView!!, context!!)
                }

                override fun getLayoutId(): Int {
                    return R.layout.fragment_home_menu
                }
            }, menusDatas)
                .setCanLoop(true)
                .setPointViewVisible(true)
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                .setOnPageChangeListener(object : OnPageChangeListener {
                    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    }

                    override fun onPageSelected(index: Int) {
                        mBinding.circleIndicator.setSteps(index)
                    }

                    override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                    }

                })
                .setPageIndicator(null)
            if (menus.size >= 5)
                topMenuAdapter.add(menus.subList(0, 5))
            else {
                topMenuAdapter.add(menus)
            }

        })
        mModel.mError.observe(this, Observer {
            //            mBinding.mSwipeRefreshLayout.isRefreshing = false
            mBinding.smartRefreshLayout.finishRefresh(true)
        })

        mModel.appIndexAdsTop.observe(this, Observer {
            if (it == null || it.adInfo.isNullOrEmpty()) {
                 mBinding.llTop.visibility=View.GONE
            }else{
                mBinding.llTop.visibility=View.VISIBLE
                mBinding.llTop.setPages(object : CBViewHolderCreator {
                    override fun createHolder(itemView: View?): Holder<*> {
                        return HomeTopTopViewHolder(
                            itemView!!,
                            context!!
                        )
                    }

                    override fun getLayoutId(): Int {
                        return R.layout.home_sc_top_item_top
                    } }, it.adInfo)
                    .setCanLoop(it.adInfo.size > 1)
                    .setPointViewVisible(it.adInfo.size > 1)
                    .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                    .setOnItemClickListener { it2 ->
                        run {
                            // 跳转事件
                            MenuJumpUtils.adJump(it.adInfo[it2])
                        }

                    }
                    .setPageIndicatorPadding(
                        0, 0, 0,
                        Utils.dip2px(context!!, 22.0f).toInt()
                    ).startTurning(3000)
            }
        })



        mModel.appIndexAds.observe(this, Observer {
            if (it != null) {
                appIndexLog = it
            }
            mModel.getCityCard(mAdCode)
        })
        // 城市名片相关数据获取
        mModel.cityCards.observe(this, Observer {

            if (it.size > 0) {
                var item: CityCardBean? = it[0]
                if (item != null) {
                    it[0].appIndexLog = appIndexLog
                }

                mBinding.cbrCity.setPages(object : CBViewHolderCreator {
                    override fun createHolder(itemView: View?): Holder<*> {
                        return HomeTopViewHolder(
                            itemView!!,
                            context!!
                        )
                    }

                    override fun getLayoutId(): Int {
                        return R.layout.home_sc_top_item
                    }

                }, it).setCanLoop(it.size > 1).setPointViewVisible(it.size > 1)
                    .setPageIndicatorPadding(
                        0, 0, 0,
                        Utils.dip2px(context!!, 22.0f).toInt()
                    ).startTurning(3000)
            }
        })
    }

    /**
     * 获取机器人信息
     */
    fun getItRobotInfo() {
        var serviceRobot: Boolean = SPUtils.getInstance().getBoolean(SPKey.SITE_IT_ROBOT, false)
        if (!serviceRobot) {
            return
        }
        if (isFirstShowItRobot) {
            mBinding.imgShowRobot.visibility = View.VISIBLE
        }
        cutdownDisable = null
        cutdownDisable = Observable.interval(
            if (isFirstShowItRobot) {
                2
            } else {
                12
            }, TimeUnit.SECONDS
        )
            .observeOn(AndroidSchedulers.mainThread())
            .takeWhile { isPageVisible }
            .subscribe {
//                isPageVisible = false
                isFirstShowItRobot = false
                mModel?.getItRobotGreeting()
                cutdownDisable?.dispose()
            }

    }

    fun hitItRobotInfo() {
        cutDownHideItRobot?.dispose()
        cutDownHideItRobot = null
        cutDownHideItRobot = Observable.interval(5, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .takeWhile { isPageVisible }
            .subscribe {
//                isPageVisible = false
                if (mBinding.imgShowRobot.visibility == View.GONE) {
                    mBinding.imgShowRobot.visibility = View.VISIBLE
                    mBinding.itrobotScHomeWindow.visibility = View.GONE
                }
                cutdownDisable?.dispose()
            }
    }

    override fun onPause() {
        super.onPause()
        try {
            isPageVisible = false
            mBinding.cbrCity?.stopTurning()
            mBinding.llTop?.stopTurning()
            cutdownDisable?.dispose()
            cutDownHideItRobot?.dispose()
        } catch (e: Exception) {

        }
    }

    override fun onResume() {
        super.onResume()
        try {
            isPageVisible = true
            mBinding.cbrCity?.startTurning()
            mBinding.llTop?.startTurning()
            getItRobotInfo()
        } catch (e: Exception) {

        }

    }

    override fun onDestroyView() {
        permissions = null
        super.onDestroyView()
        GaoDeLocation.getInstance().release()
    }
}

