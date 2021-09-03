package com.daqsoft.thetravelcloudwithculture.ws

import android.Manifest
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.AnimatorUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.adapter.ViewPagerAdapter
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.bean.HomeMenu
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.FragmentHomeWsBinding
import com.daqsoft.thetravelcloudwithculture.databinding.ItemHomeTopMenuBinding
import com.daqsoft.thetravelcloudwithculture.databinding.ItemHomeTopMenuWsBinding
import com.daqsoft.thetravelcloudwithculture.home.fragment.XjHomeMenuFragment
import com.daqsoft.thetravelcloudwithculture.home.view.BottomScrollView
import com.daqsoft.thetravelcloudwithculture.ui.event.RefreshHomeEvent
import com.daqsoft.thetravelcloudwithculture.xj.ui.vm.XJHomeFragmentVm
import com.daqsoft.travelCultureModule.itrobot.view.ItRobotWindowView
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
class WSHomeFragment : BaseFragment<FragmentHomeWsBinding, XJHomeFragmentVm>() {
    private val menus = mutableListOf<HomeMenu>()

    /**
     * 目录适配器
     */
    private var menuAdapter: ViewPagerAdapter? = null

    /**
     *  轮询机器人信息工具
     *   每隔 15s
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

    var permissions: RxPermissions? = null

    val homeBannerAndHappyFragment: WSHomeBannerAndHappyFragment by lazy {
        WSHomeBannerAndHappyFragment()
    }

    lateinit var animtor : AnimatorUtil
    /**
     * 第一次显示机器人chNextFragment
     */
    var isFirstShowItRobot: Boolean = true

    var isShowButtonRobot: Boolean = false

    override fun getLayout(): Int = R.layout.fragment_home_ws
    var lis: AppBarLayout.OnOffsetChangedListener? = null
    override fun initData() {
        mModel.getAPPMenus(Constant.HOME_MENU_TOP)
        mModel.getTzggList()
        val gaoDeLocation = GaoDeLocation()
        GaoDeLocation.getInstance()
            .init(context, object : GaoDeLocation.OnGetCurrentLocationLisner {

                override fun onResult(
                    adCode: String, result: String, lat_: Double,
                    lon_: Double, adcode: String
                ) {
                    mModel.getCityCard(adCode)
                    homeBannerAndHappyFragment.getFoundAround(lat_, lon_)
                }

                override fun onError(errormsg: String) {
                    mModel.getCityCard(BaseApplication.defaultAdCode)
                    Timber.e("获取定位位置出错了")
                }
            })

    }

    /**
     * 顶部菜单适配器
     */
    private val topMenuAdapter = object :
        RecyclerViewAdapter<ItemHomeTopMenuWsBinding, HomeMenu>(
            R.layout.item_home_top_menu_ws
        ) {
        override fun setVariable(mBinding: ItemHomeTopMenuWsBinding, position: Int, item: HomeMenu) {
            mBinding.url = item.unSelectIcon
        }

    }

    override fun initView() {

        permissions = RxPermissions(this)
        animtor= AnimatorUtil(activity);
        // 搜索背景透明度
        menuAdapter = ViewPagerAdapter(childFragmentManager)

        mBinding.circleIndicator.binViewPager(mBinding.viewPager)
        mBinding.viewPager.adapter = menuAdapter
        isHideAnother = false
        // 一下为替换三个fragment
        val tripBranchNextFragment = WsTripBranchNextFragment()
        val activityTopicStoryFragment = WSActivityTopicStoryFragment()
        transactFragment(R.id.home_menu, homeBannerAndHappyFragment)
        transactFragment(R.id.trip_next, tripBranchNextFragment)
        transactFragment(R.id.activity_story, activityTopicStoryFragment)
        mBinding.tvSearch.setOnClickListener {
            ARouter.getInstance().build(MainARouterPath.MAIN_ALL_SEARCH)
                .navigation()
        }
        mBinding.mSwipeRefreshLayout.setOnRefreshListener {
            //            mBinding.mSwipeRefreshLayout.isRefreshing = true
            initData()
            EventBus.getDefault().post(RefreshHomeEvent())
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
            object : ItRobotWindowView.OnClickHideListener {
                override fun onClickHide() {
                    mBinding.imgShowRobot.visibility = View.VISIBLE
                    mBinding.itrobotScHomeWindow.visibility = View.GONE
                    cutDownHideItRobot?.dispose()
                }
            }
        mBinding.imgShowRobot.onNoDoubleClick {
            if (mBinding.itrobotScHomeWindow.isCanVisable) {
                mBinding.itrobotScHomeWindow.visibility = View.VISIBLE
                mBinding.imgShowRobot.visibility = View.GONE
            } else {
                MainARouterPath.toItRobotPage()
            }
            hitItRobotInfo()
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
        mBinding.imgShowShare.setOnClickListener() {
            if (!isShowButtonRobot) {
                isShowButtonRobot=true;
                mBinding.imgShowShare.setImageResource(R.mipmap.float_guanmi)
                setButtonVisible(true)

            } else {
                isShowButtonRobot=false;
                mBinding.imgShowShare.setImageResource(R.mipmap.float_lammu)
                setButtonVisible(false)
            }
        }
        //监听滑动到底部显示底部分享栏目
        mBinding.bscv.setOnScrollToBottomLintener { isBottom ->
            if(isBottom && mBinding.llRoot.visibility==View.GONE){
                isShowButtonRobot=true;
                mBinding.imgShowShare.setImageResource(R.mipmap.float_guanmi)
                setButtonVisible(true)
            }

        }
    }

    /**
     *底部状态栏消失隐藏动画
     */
     fun setButtonVisible(visible: Boolean) {
        if(visible){
            mBinding.llRoot.visibility=View.VISIBLE
            mBinding.llRoot.setCanClick(true)
            animtor.translAnimation(mBinding.llRoot,true)
        }else{
            animtor.translAnimation(mBinding.llRoot,false)
            mBinding.llRoot.postDelayed({
                mBinding.llRoot.visibility=View.GONE
                mBinding.llRoot.setCanClick(false)
            },300)
        }
    }


    override fun injectVm(): Class<XJHomeFragmentVm> = XJHomeFragmentVm::class.java

    override fun notifyData() {
        super.notifyData()
        mModel.topMenus.observe(this, Observer {
            //            mBinding.mSwipeRefreshLayout.isRefreshing = false
            mBinding.mSwipeRefreshLayout.finishRefresh()
            menus.clear()
            menus.addAll(it)
            var count = menus.size
            var pageSize = 10
            var pageCount = (count + pageSize - 1) / pageSize
            for (index in 1..pageCount) {
                var start = (index - 1) * pageSize
                var max = start + pageSize
                var end = if (max > count) {
                    count
                } else {
                    max
                }
                var data = menus.subList(start, end)
                menuAdapter!!.addFragment(XjHomeMenuFragment.newInstance(data))
            }
            menuAdapter!!.notifyDataSetChanged()
            mBinding.circleIndicator.total = pageCount
            mBinding.viewPager.offscreenPageLimit = pageCount
            topMenuAdapter.clear()
            if (menus.size >= 5)
                topMenuAdapter.add(menus.subList(0, 5))
            else {
                topMenuAdapter.add(menus)
            }

        })
        mModel.mError.observe(this, Observer {
            //            mBinding.mSwipeRefreshLayout.isRefreshing = false
            mBinding.mSwipeRefreshLayout.finishRefresh()
        })
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
        // 城市名片相关数据获取
        mModel.cityCards.observe(this, Observer {
            //            mBinding.mSwipeRefreshLayout.isRefreshing = false
            mBinding.mSwipeRefreshLayout.finishRefresh()
            if (it.size > 0) {
                var cityCardBean = it[0]
                mBinding.url = cityCardBean.coverImage
                mBinding.weather = getString(
                    R.string.home_weather_str, cityCardBean.weather.min + "~" + cityCardBean
                        .weather.max
                )
                mBinding.tvWeatherStatus.text = "${cityCardBean.weather.qlty}"
                Glide.with(this)
                    .asBitmap()
                    .load(cityCardBean.weather.pic)
                    .centerCrop()
                    .into(object : CustomTarget<Bitmap>() {

                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            var drawable = BitmapDrawable(resource)
                            mBinding.tvWeather.setCompoundDrawablesWithIntrinsicBounds(
                                drawable,
                                null,
                                null,
                                null
                            )
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {

                        }
                    })
                mBinding.tvCardName.text = cityCardBean.summary
                if (!cityCardBean.english.isNullOrEmpty()) {
                    mBinding.tvCardNameEnglish.text = cityCardBean.english
                    mBinding.tvCardNameEnglish.visibility = View.VISIBLE
                } else {
                    mBinding.tvCardNameEnglish.visibility = View.GONE
                }

                mBinding.tvCardSummary.text = cityCardBean.summary
                mBinding.tvCard.setOnClickListener {
                    var siteRegion: String? = cityCardBean.region
                    if (siteRegion.isNullOrEmpty()) {
                        ARouter.getInstance().build(MainARouterPath.MAIN_MDD_CURRENT_CITY_INFO)
                            .withString("id", cityCardBean.siteId.toString())
                            .navigation()
                    } else {
                        if (siteRegion.endsWith("0000")) {
                            // 省级站点
                            ARouter.getInstance().build(MainARouterPath.MAIN_MDD_CURRENT_CITY_INFO)
                                .withString("id", cityCardBean.siteId.toString())
                                .navigation()
                        } else {
                            //市级站点
                            ARouter.getInstance().build(MainARouterPath.MAIN_MDD_CITY_INFO)
                                .withString("id", cityCardBean.siteId.toString())
                                .navigation()
                        }
                    }

                }
            }
        })
    }

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
            cutdownDisable?.dispose()
            cutDownHideItRobot?.dispose()
        } catch (e: Exception) {

        }
    }

    override fun onResume() {
        super.onResume()
        try {
            isPageVisible = true
            getItRobotInfo()
        } catch (e: Exception) {

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        permissions = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        permissions = null
        GaoDeLocation.getInstance().release()
    }
}