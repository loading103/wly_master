package com.daqsoft.travelCultureModule.citycard

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.adapter.setImageUrlqwx
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.*
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.ZARouterPath
import com.daqsoft.provider.adapter.GridStoryAdapter
import com.daqsoft.provider.adapter.ViewPagerAdapter
import com.daqsoft.provider.base.ActivityMethod
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.businessview.event.UpdateAudioStateEvent
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.provider.view.popupwindow.SharePopWindow
import com.daqsoft.thetravelcloudwithculture.home.bean.CityCardDetail
import com.daqsoft.travelCultureModule.citycard.vm.CityCardViewModel
import com.daqsoft.venuesmodule.repository.bean.VenuesListBean
import com.dueeeke.videocontroller.StandardVideoController
import com.dueeeke.videocontroller.component.*
import com.dueeeke.videoplayer.ijk.IjkPlayer
import com.dueeeke.videoplayer.ijk.IjkPlayerFactory
import com.dueeeke.videoplayer.player.AbstractPlayer
import com.dueeeke.videoplayer.player.VideoView
import com.dueeeke.videoplayer.util.L
import com.jakewharton.rxbinding2.view.RxView
import com.scwang.smart.refresh.layout.util.SmartUtil.dp2px
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.util.concurrent.TimeUnit


@Route(path = MainARouterPath.MAIN_MDD_CITY_INFO)
class CityInfoActivity : TitleBarActivity<ActivityCityInfoBinding, CityCardViewModel>(),
    View.OnClickListener {

    @JvmField
    @Autowired
    var region: String = ""

    @JvmField
    @Autowired
    var id: String = ""

    @JvmField
    @Autowired
    var name: String? = ""

    var viewList = ArrayList<View>()
    var mList = ArrayList<String>()
    var siteId: String? = ""
    /**
     * ????????????
     */
    var sharePopWindow: SharePopWindow? = null
    lateinit var curCityBean: CityCardDetail
    private val menus = mutableListOf<HomeMenu>()

    private val videoDatas = mutableListOf<VideoView<IjkPlayer>>()

    private var menuAdapter: ViewPagerAdapter? = null

    private var storyAdapter: GridStoryAdapter? = null

    private val foundAroundAdapter: CityFoundAroundAdapter by lazy {
        CityFoundAroundAdapter()
    }

    override fun getLayout(): Int = R.layout.activity_city_info

    override fun setTitle(): String = getString(R.string.culture_citycard_info)

    override fun injectVm(): Class<CityCardViewModel> = CityCardViewModel::class.java


//?????????????????? http://site488314.c-ctc.test.daqsoft.com/#/destination-city/150100/288  region: "150100" siteId: 288
//    override fun initCustomTitleBar(mTitleBar: TitleBar) {
//        showShareButton(com.daqsoft.provider.R.mipmap.common_nav_button_share)
//        setShareClick(object : View.OnClickListener {
//            override fun onClick(v: View?) {
//                curCityBean?.let {
//                    if (sharePopWindow == null) {
//                        sharePopWindow = SharePopWindow(this@CityInfoActivity)
//                    }
//                    sharePopWindow?.setShareContent(
//                        it.name, "", it.images[0],
//                        ShareModel.getCityDesc(id)
//                    )
//                    if (!sharePopWindow!!.isShowing) {
//                        sharePopWindow?.showAsDropDown(mTitleBar)
//                    }
//                }
//            }
//
//        })
//    }

    override fun initView() {

        mBinding.tvCityName.text = name

        menuAdapter = ViewPagerAdapter(supportFragmentManager)
        mBinding.circleIndicator.binViewPager(mBinding.viewPager)

        mBinding.viewPager.adapter = menuAdapter
        var ninckName: String = SPUtils.getInstance().getString(SPKey.NICK_NAME)
        mBinding.tvHelloCity.setText(
            "Hi," + if (!ninckName.isNullOrEmpty()) {
                "${ninckName},"
            } else {
                ""
            } + "?????????"
        )
        mBinding.tvCityImgs.setOnClickListener(this)
        mBinding.tvCityVideo.setOnClickListener(this)

        mModel.cityInfo.observe(this, Observer {
            curCityBean = it
            mModel.getWeather(it.region)
            mModel.getMDDCity(it.siteId.toString(), "6", "county", it.region)
            mBinding.citybean = it
            var videoUrls: List<String> = ArrayList()
            // ???????????????
            if (!curCityBean.videoEx.isNullOrEmpty()) {
                videoUrls = curCityBean.videoEx!!.split(",")
                if (!videoUrls.isNullOrEmpty()) {
                    for (item in videoUrls) {
                        mList.add(item)
                    }
                    mBinding.videovisible = true
                } else {
                    mBinding.videovisible = false
                }
            } else {
                mBinding.videovisible = false
            }
            if (!curCityBean.panoramaUrl.isNullOrEmpty()) {
                mBinding.tvCity720.visibility = View.VISIBLE
            }
            if (!curCityBean.images.isNullOrEmpty()) {
                for (position in curCityBean.images.indices) {
                    mList.add(curCityBean.images[position])
                }
                mBinding.imgevisible = true
            } else {
                mBinding.imgevisible = false
            }
            if (mList.size <= 0) {
                mBinding.rlCityImgs.visibility = View.GONE
            }
            if (!curCityBean.panoramaUrl.isNullOrEmpty()) {
                mBinding.tvCity720.visibility = View.VISIBLE
            }
            mBinding.imgevisible = !curCityBean.images.isNullOrEmpty()

            mBinding.vpCityHead.offscreenPageLimit=mList.size
            mBinding.vpCityHead.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                    videoDatas?.forEach {
                        it.pause()
                    }
                }
            })
            mBinding.vpCityHead.setAdapter(object : PagerAdapter() {
                override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                    container.removeView(viewList[position])
                }

                override fun instantiateItem(container: ViewGroup, position: Int): Any {
                    var videoUrls: MutableList<String> = ArrayList()
                    var videoSize: Int = 0
                    if (!curCityBean.videoEx.isNullOrEmpty()) {
                        videoUrls = curCityBean.videoEx!!.split(",").toMutableList()
                        if (!videoUrls.isNullOrEmpty()) {
                            videoSize = videoUrls.size
                        }
                    }
                    if (videoSize > 0 && position < videoSize) {
//                        var imageView: fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard =
//                            fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard(this@CityInfoActivity)
//
//                        var param = LinearLayout.LayoutParams(
//                            ViewGroup.LayoutParams.MATCH_PARENT,
//                            ViewGroup.LayoutParams.MATCH_PARENT
//                        )
//                        imageView.layoutParams = param as ViewGroup.LayoutParams?
//                        imageView.setUp(videoUrls[position], JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
//                        var videoCover: String = StringUtil.getVideoCoverUrl(videoUrls[position])
//                        imageView.thumbImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                        GlideModuleUtil.loadDqImage(videoCover, imageView.thumbImageView)
//
//
//                        container.addView(imageView)
//                        viewList.add(imageView)
//                        return imageView


                        val mVideoPlayer = VideoView<IjkPlayer>(this@CityInfoActivity)
                        var param = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        mVideoPlayer.layoutParams = param
                        val controller = StandardVideoController(this@CityInfoActivity)
                        val prepareView = PrepareView(this@CityInfoActivity)
                        // ?????????
                        val thumb = prepareView.findViewById<ImageView>(com.daqsoft.provider.R.id.thumb)
                        // ??????????????????
                        val playButton = prepareView.findViewById<ImageView>(com.daqsoft.provider.R.id.start_play)
                        playButton.background = this@CityInfoActivity.resources.getDrawable(com.daqsoft.provider.R.drawable.dkplayer_custom_shape_play_bg)
                        var coverUrl = ""
                        coverUrl = StringUtil.getVideoCoverUrl(videoUrls[position])
                        Glide.with(this@CityInfoActivity).load(coverUrl)
                            .into(thumb)
                        // ????????????
                        val errorView = ErrorView(this@CityInfoActivity)
                        prepareView.setClickStart()
                        //??????????????????
                        val completeView = CompleteView(this@CityInfoActivity)
                        val titleView = TitleView(this@CityInfoActivity)
                        // ?????? ??????/??????/????????????
                        controller.addControlComponent(completeView, errorView, prepareView, titleView)
                        controller.addControlComponent(VodControlView(this@CityInfoActivity))
                        controller.addControlComponent(GestureView(this@CityInfoActivity))
                        controller.setCanChangePosition(true)
                        mVideoPlayer?.setVideoController(controller)
                        mVideoPlayer?.setUrl(StringUtil.enCodeVideoUrl(videoUrls[position]))

                        mVideoPlayer?.setScreenScaleType(VideoView.SCREEN_SCALE_DEFAULT)
                        // ??????IjkPlayer??????
                        mVideoPlayer?.setPlayerFactory(IjkPlayerFactory.create())
                        // ??????????????????
                        mVideoPlayer?.addOnStateChangeListener(mOnVideoViewStateChangeListener)
                        mVideoPlayer?.setOnClickListener {
                            mVideoPlayer?.start()
                        }
                        container.addView(mVideoPlayer)
                        viewList.add(mVideoPlayer)
                        videoDatas.add(mVideoPlayer)
                        return mVideoPlayer
                    } else {
                        var imageView = ImageView(this@CityInfoActivity)
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP)
                        setImageUrlqwx(
                            imageView, mList.get(position), AppCompatResources.getDrawable(
                                BaseApplication.context, R.drawable.placeholder_img_fail_h300
                            ), 5
                        )
                        container.addView(imageView)
                        viewList.add(imageView)
                        return imageView
                    }
                }

                override fun getCount(): Int {
                    return mList.size
                }

                override fun isViewFromObject(view: View, `object`: Any): Boolean {
                    return view === `object`
                }
            })
        })

        mModel.topMenus.observe(this, Observer {
            mBinding.menuevisible = it.size > 0
            menus.addAll(it)
            var n = it.size / 10
            n += 1
            if (n > 0) {
                for (i in 0 until n) {

                    var ms = if (i == n - 1) {
                        menus.subList(i * 10, menus.size)
                    } else {
                        menus.subList(i * 10, i * 10 + 10)
                    }
                    menuAdapter!!.addFragment(HomeMenuFragment.newInstance(ms, id, region))

                }

                mBinding.circleIndicator.total = n
            }
            menuAdapter!!.notifyDataSetChanged()
            if (menus.size >= 5)
                topMenuAdapter.add(menus.subList(0, 5))
            else {
                topMenuAdapter.add(menus)
            }
        })
        // ??????
        mModel.weather.observe(this, Observer {
            mBinding.weather = it
        })
        //?????????
        val gridLayoutManagerDqx = GridLayoutManager(this, 3)
        mBinding.rvCityDqx.layoutManager = gridLayoutManagerDqx
        mBinding.rvCityDqx.adapter = adapter_dqx
        mModel.mddDQXList.observe(this, Observer {
            if (it.size > 0) {
                mBinding.llCityDqx.visibility = View.VISIBLE
                adapter_dqx.add(it)
            } else {
                mBinding.llCityDqx.visibility = View.GONE
            }

        })

        // ??????
        val tagLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvCityBrand.layoutManager = tagLayoutManager
        mBinding.rvCityBrand.adapter = adapter_brand
        mModel.homeBranchBeanList.observe(this, Observer {
            if (it.size > 0) {
                mBinding.llCityBrand.visibility = View.VISIBLE
                adapter_brand.add(it)
            } else {
                mBinding.llCityBrand.visibility = View.GONE
            }
        })
        // ??????
        val gridLayoutManager = GridLayoutManager(this, 2)
        mBinding.rvCitySecnic.layoutManager = gridLayoutManager
        mBinding.rvCitySecnic.adapter = adapter_scenic
        mModel.secnicList.observe(this, Observer {
            if (it.size > 0) {
                mBinding.llCitySecnic.visibility = View.VISIBLE
                adapter_scenic.add(it)
            } else {
                mBinding.llCitySecnic.visibility = View.GONE
            }
        })
        //??????
        val gridLayoutManagerActivity = GridLayoutManager(this, 2)
        mBinding.rvCityActivity.layoutManager = gridLayoutManagerActivity
        mBinding.rvCityActivity.adapter = adapter_activity
        mModel.activities.observe(this, Observer {
            if (it.size > 0) {
                mBinding.llCityActivity.visibility = View.VISIBLE
                adapter_activity.add(it)
            } else {
                mBinding.llCityActivity.visibility = View.GONE
            }
        })
        //??????
        val gridLayoutManagerChangguan = GridLayoutManager(this, 2)
        mBinding.rvCityChangugan.layoutManager = gridLayoutManagerChangguan
        mBinding.rvCityChangugan.adapter = adapter_changguan
        mModel.venuesList.observe(this, Observer {
            if (it.size > 0) {
                mBinding.llCityChangguan.visibility = View.VISIBLE
                adapter_changguan.add(it)
            } else {
                mBinding.tvCiCityCg.visibility = View.GONE
                mBinding.llCityChangguan.visibility = View.GONE
            }
        })
        //??????
        val gridLayoutManagerHotel = GridLayoutManager(this, 2)
        mBinding.rvCityHotel.layoutManager = gridLayoutManagerHotel
        mBinding.rvCityHotel.adapter = adapter_hotel
        mModel.hotelList.observe(this, Observer {
            if (it.size > 0) {
                mBinding.llCityHotel.visibility = View.VISIBLE
                adapter_hotel.add(it)
            } else {
                mBinding.tvCiCityHotle.visibility = View.GONE
                mBinding.llCityHotel.visibility = View.GONE
            }
        })
        //??????
        val gridLayoutManagerFood = GridLayoutManager(this, 2)
        mBinding.rvCityFood.layoutManager = gridLayoutManagerFood
        mBinding.rvCityFood.adapter = adapter_food
        mModel.foodList.observe(this, Observer {
            if (it.size > 0) {
                mBinding.llCityFood.visibility = View.VISIBLE
                adapter_food.add(it)
            } else {
                mBinding.tvCiCityFood.visibility = View.GONE
                mBinding.llCityFood.visibility = View.GONE
            }
        })
        //??????
        val storyLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        storyAdapter = GridStoryAdapter(this!!)
        mBinding.rvCityStory.layoutManager = storyLayoutManager
        mBinding.rvCityStory.adapter = storyAdapter
        mModel.storyList.observe(this, Observer {
            mBinding.mSwipeRefreshLayout.finishRefresh()

            if (it.size > 0) {
                mBinding.llCityStory.visibility = View.VISIBLE
                storyAdapter!!.add(it)
            } else {
                mBinding.tvCiCityStory.visibility = View.GONE
                mBinding.llCityStory.visibility = View.GONE
            }
        })
        //??????

        val gridLayoutManager1 = GridLayoutManager(this, 2)
        mBinding.rvCitySpecial.layoutManager = gridLayoutManager1
        mBinding.rvCitySpecial.adapter = adapter_special
        mModel.researchList.observe(this, Observer {
            if (it.size > 0) {
                mBinding.llCitySpecial.visibility = View.VISIBLE
                mBinding.tvCitySpecial.setOnClickListener {
                    ARouter.getInstance()
                        .build(MainARouterPath.SPECIAL_BASELIST)
                        .withString("areaSiteSwitch",id)
                        .withString("region",region)
                        .withString("siteId", curCityBean?.siteId?.toString())
                        .navigation()
                }
                adapter_special.add(it)
            } else {
                mBinding.llCitySpecial.visibility = View.GONE
            }
        })




        mBinding.mSwipeRefreshLayout.setOnRefreshListener {

//            mBinding.mSwipeRefreshLayout.isRefreshing = false
            storyAdapter!!.clear()
            adapter_food.clear()
            adapter_hotel.clear()
            adapter_changguan.clear()
            adapter_activity.clear()
            adapter_scenic.clear()
            adapter_brand.clear()
            adapter_dqx.clear()
            adapter_special.clear()
            mList.clear()
            topMenuAdapter.clear()
            menus.clear()
            menuAdapter = ViewPagerAdapter(supportFragmentManager)
            reloadData()
        }
        // ????????????
        mModel.foundArouds.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.llCityFoundAround.visibility = View.VISIBLE
                mBinding.rvCityFoundAround.run {
                    layoutManager = GridLayoutManager(
                        this@CityInfoActivity,
                        3,
                        GridLayoutManager.VERTICAL,
                        false
                    )
                    adapter = foundAroundAdapter
                }
                foundAroundAdapter.clear()
                foundAroundAdapter.add(it)
            } else {
                mBinding.llCityFoundAround.visibility = View.GONE
            }
        })
    }

    /**
     * ?????????????????????
     */
    private val topMenuAdapter =
        object : RecyclerViewAdapter<ItemCityMenuBinding, HomeMenu>(R.layout.item_city_menu) {
            override fun setVariable(mBinding: ItemCityMenuBinding, position: Int, item: HomeMenu) {
                mBinding.url = item.unSelectIcon
            }

        }

    override fun initData() {
        mModel.getCityCard(id)
        mModel.getCityMenus(Constant.HOME_MENU_TOP, id)
        mModel.getBranchList(id)
        mModel.getScenicList(id)
        mModel.getActivityList(id)
        mModel.getVenusList("4", id)
        mModel.getHotelList(id)
        mModel.getFoodList(id)
        mModel.getHotStoryList(id)
        mModel.getHotStoryList(id)
        mModel.getSpical(id)
        mBinding.tvCity720.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.Provider.WEB_ACTIVITY)
                .withString("mTitle", curCityBean.name)
                .withString("html", curCityBean.panoramaUrl)
                .navigation()
        }
        GaoDeLocation.getInstance()
            .init(this@CityInfoActivity, object : GaoDeLocation.OnGetCurrentLocationLisner {

                override fun onResult(
                    adCode: String, result: String, lat_: Double,
                    lon_: Double, adcode: String
                ) {
                    foundAroundAdapter?.currentPostion = LatLng(lat_, lon_)
                    id?.let {
                        mModel.getFounds(lat_, lon_, id)
                    }


                }

                override fun onError(errormsg: String) {
                    mModel.getCityCard("")
                    Timber.e("???????????????????????????")
                }
            })

    }

    /**
     * ?????????
     */
    var adapter_dqx = object :
        RecyclerViewAdapter<ItemCityCardDqxBinding, BrandMDD>(R.layout.item_city_card_dqx) {

        override fun setVariable(mBinding: ItemCityCardDqxBinding, position: Int, item: BrandMDD) {
            mBinding.dqx = item
            var logoUrl = item.images
            if (logoUrl != "") logoUrl = logoUrl.split(",")[0]
            setImageUrlqwx(
                mBinding.ivMddLogoDqx, logoUrl, AppCompatResources.getDrawable(
                    BaseApplication.context, R.drawable.placeholder_img_fail_240_180
                ), 5
            )
            mBinding.llItemClickDqx.setOnClickListener {
                ARouter.getInstance().build(MainARouterPath.MAIN_MDD_CITY_INFO)
                    .withString("id", item.id.toString())
                    .withString("siteId", curCityBean?.siteId?.toString())
                    .navigation()
            }
        }

    }

    /**
     * ??????????????????
     */
    var adapter_brand = object :
        RecyclerViewAdapter<ItemCityBrandBinding, HomeBranchBean>(R.layout.item_city_brand) {

        override fun setVariable(
            Binding: ItemCityBrandBinding,
            position: Int,
            item: HomeBranchBean
        ) {
            Binding.tvItemBrandNameCity.text = item.name
            Binding.tvItemBrandIntroduceCity.text = item.slogan
            if (!item.relationResourceNameStr.isNullOrEmpty()) {
                if (item.relationResourceNameStr.contains(",")) {
                    var names = item.relationResourceNameStr.split(",")
                    if (names.size >= 3) {
                        names = names.subList(0, 3)
                    }
                    item.relationResourceNameStr = DividerTextUtils.convertDotString(names)
                }
            }
            Binding.tvItemBrandOtherCity.text = item.relationResourceNameStr
            setImageUrlqwx(
                Binding.ivItemBrandBackgroundCity, item.brandImage, AppCompatResources.getDrawable(
                    BaseApplication.context, R.drawable.placeholder_img_fail_h300
                ), 5
            )
            var data_color = item.mainColor.split(",")
            if (data_color.size == 3) {
                var color =
                    Color.rgb(data_color[0].toInt(), data_color[1].toInt(), data_color[2].toInt())
                val colors = intArrayOf(color, -0x00) //???????????????????????????????????????????????????
                val gd =
                    GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors) //??????drawable
                // gd.alpha=240
                gd.cornerRadius = dp2px(5F).toFloat()

                Binding.rlItemBrandCity.background = gd
            }
            Binding.llItemBrandCity.setOnClickListener {
                ( mBinding.rvCityBrand.getLayoutManager() as LinearLayoutManager).scrollToPositionWithOffset(position, 0)
                ARouter.getInstance().build(MainARouterPath.MAIN_BRANCH_DETAIL)
                    .withString("id", item.id)
                    .withString("siteId", curCityBean?.siteId?.toString())
                    .navigation()
            }
        }

    }

    /**
     * ????????????
     */
    var adapter_scenic =
        object : RecyclerViewAdapter<ItemCityScenicBinding, ScenicBean>(R.layout.item_city_scenic) {

            @SuppressLint("CheckResult")
            override fun setVariable(
                mBinding: ItemCityScenicBinding,
                position: Int,
                item: ScenicBean
            ) {
                mBinding.scenic = item
                if (item.level == "?????????") {
                    mBinding.tvIbLevelCity.visibility = View.INVISIBLE
                } else {
                    mBinding.tvIbLevelCity.text = item.level.length.toString() + "A"
                }
                var logoUrl = item.images
                if (logoUrl != "") logoUrl = logoUrl.split(",")[0]
                setImageUrlqwx(
                    mBinding.ivIbLogoCity, logoUrl, AppCompatResources.getDrawable(
                        BaseApplication.context, R.drawable.placeholder_img_fail_240_180
                    ), 5
                )
                RxView.clicks(mBinding.root)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        ARouter.getInstance()
                            .build(MainARouterPath.MAIN_SCENIC_DETAIL)
                            .withString("id", item.id.toString())
                            .navigation()
                    }

            }

        }


    /**
     * ????????????
     */
    var adapter_special =
        object : RecyclerViewAdapter<ItemCitySpecialBinding, SpeaiclBean>(R.layout.item_city_special) {

            @SuppressLint("CheckResult")
            override fun setVariable(
                mBinding: ItemCitySpecialBinding,
                position: Int,
                item: SpeaiclBean
            ) {
                mBinding.scenic = item
                var logoUrl = item.getheadImages()
                setImageUrlqwx(
                    mBinding.ivIbLogoCity, logoUrl, AppCompatResources.getDrawable(
                        BaseApplication.context, R.drawable.placeholder_img_fail_240_180
                    ), 5
                )
                RxView.clicks(mBinding.root)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        ARouter.getInstance()
                            .build(MainARouterPath.MAIN_SPECIAL_DETAIL)
                            .withString("id", item.id.toString())
                            .navigation()
                    }

            }

        }

    /**
     * ?????????
     */
    var adapter_activity = object :
        RecyclerViewAdapter<ItemCityActivityBinding, ActivityBean>(R.layout.item_city_activity) {
        override fun setVariable(
            mBinding: ItemCityActivityBinding,
            position: Int,
            item: ActivityBean
        ) {
            mBinding.activity = item
            if (item.money.toFloat() <= 0) mBinding.tvCityActivityFree.text = "??????"
            var url = item.images
            if (item.images != "") {
                url = item.images.split(",")[0]
            }
            setImageUrlqwx(
                mBinding.ivIbLogoCityActivity, url, AppCompatResources.getDrawable(
                    BaseApplication.context, R.drawable.placeholder_img_fail_240_180
                ), 5
            )
            RxView.clicks(mBinding.root)
                // 1????????????????????????????????????????????????
                .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                    run {
                        // ????????????????????? ????????????webactivity
                        if (item.jumpType.equals("2")) {
                            ARouter.getInstance()
                                .build(ARouterPath.Provider.WEB_ACTIVITY)
                                .withString("mTitle", item.jumpName)
                                .withString("html", item.jumpUrl)
                                .navigation()
                        } else {
                            when (item.type) {
                                // ????????????
                                ActivityType.ACTIVITY_TYPE_VOLUNT -> {
                                    ARouter.getInstance()
                                        .build(MainARouterPath.MAIN_VOLUNTEER_ACTIVITY)
//                                    .build( MainARouterPath.MAIN_SEAT_SELECT_ACTIVITY)
                                        .withString("id", item.id)
                                        .withString("classifyId", item.classifyId)
                                        .navigation()
                                }
                                // ????????????
                                ActivityType.ACTIVITY_TYPE_RESERVE -> {
                                    // ??????
                                    when (item.method) {
                                        // ??????????????????
                                        ActivityMethod.ACTIVITY_MODE_INTEGRAL_PAY -> {

                                            ARouter.getInstance()
                                                .build(MainARouterPath.MAIN_ACTIVITY_PAY_ORDER)
                                                .withString("jumpUrl", item.jumpUrl)
                                                .navigation()
                                        }
                                        else -> {
                                            ARouter.getInstance()
                                                .build(MainARouterPath.MAIN_HOT_ACITITY)
                                                .withString("id", item.id)
                                                .withString("classifyId", item.classifyId)
                                                .withString("region", item.region)
                                                .navigation()
                                        }
                                    }
                                }
                                else -> {
                                    ARouter.getInstance()
                                        .build(MainARouterPath.MAIN_HOT_ACITITY)
                                        .withString("id", item.id)
                                        .withString("classifyId", item.classifyId)
                                        .navigation()
                                }

                            }
                        }
                    }
                }

        }
    }

    /**
     * ??????
     */
    var adapter_changguan = object :
        RecyclerViewAdapter<ItemCityChangguanBinding, VenuesListBean>(R.layout.item_city_changguan) {
        @SuppressLint("CheckResult")
        override fun setVariable(
            mBinding: ItemCityChangguanBinding,
            position: Int,
            item: VenuesListBean
        ) {
            mBinding.changguan = item
            var url = item.images
            if (item.images != "") {
                url = item.images.split(",")[0]
            }
            setImageUrlqwx(
                mBinding.ivIbLogoCityChangguan, url, AppCompatResources.getDrawable(
                    BaseApplication.context, R.drawable.placeholder_img_fail_240_180
                ), 5
            )
            RxView.clicks(mBinding.root)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    ARouter.getInstance()
                        .build(ARouterPath.VenuesModule.VENUES_DETAILS_ACTIVITY)
                        .withString("id", item.id)
                        .navigation()
                }
        }
    }

    /**
     * ??????
     */
    var adapter_hotel =
        object : RecyclerViewAdapter<ItemCityHotelBinding, HotelBean>(R.layout.item_city_hotel) {
            @SuppressLint("CheckResult")
            override fun setVariable(
                mBinding: ItemCityHotelBinding,
                position: Int,
                item: HotelBean
            ) {
                mBinding.hotel = item
                var url = item.images
                if (item.images != "") {
                    url = item.images.split(",")[0]
                }
                setImageUrlqwx(
                    mBinding.ivIbLogoCityHotel, url, AppCompatResources.getDrawable(
                        BaseApplication.context, R.drawable.placeholder_img_fail_240_180
                    ), 5
                )
                RxView.clicks(mBinding.root)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        ARouter.getInstance()
                            .build(ZARouterPath.ZMAIN_HOTEL_DETAIL)
                            .withString("id", item.id.toString())
                            .navigation()
                    }
            }
        }

    /**
     * ??????
     */
    var adapter_food = object :
        RecyclerViewAdapter<ItemCityCardFoodBinding, FoodBean>(R.layout.item_city_card_food) {
        @SuppressLint("CheckResult")
        override fun setVariable(mBinding: ItemCityCardFoodBinding, position: Int, item: FoodBean) {
            mBinding.food = item
            var url = item.images
            if (item.images != "") {
                url = item.images.split(",")[0]
            }
            setImageUrlqwx(
                mBinding.ivIbLogoCityFood, url, AppCompatResources.getDrawable(
                    BaseApplication.context, R.drawable.placeholder_img_fail_240_180
                ), 5
            )
            RxView.clicks(mBinding.root)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    ARouter.getInstance()
                        .build(MainARouterPath.MAIN_FOOD_DETAIL)
                        .withString("id", item.id.toString())
                        .navigation()
                }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_city_name -> {
                ARouter.getInstance().build(MainARouterPath.MAIN_MDD_LIST)
                    .navigation()
            }
            R.id.tv_city_dqx -> {
                ARouter.getInstance().build(MainARouterPath.MAIN_MDD_LIST)
                    .withString("country", "country")
                    .withString("regionId", curCityBean.siteId.toString())
                    .navigation()
            }
            R.id.tv_city_brand -> {
                ARouter.getInstance().build(MainARouterPath.MAIN_BRANCH_LIST)
                    .withString("siteId", curCityBean.siteId.toString())
                    .navigation()
            }
            // ??????
            R.id.tv_city_secnic -> {
                ARouter.getInstance().build(MainARouterPath.MAINE_SCENIC_LIST)
                    .withString("region", curCityBean.region)
                    .withString("siteId", curCityBean.siteId.toString())
                    .navigation()
            }
            // ??????
            R.id.tv_city_activity -> {
                EventBus.getDefault().post(ChangeTabEvent("ACTIVITY"))
            }
            // ??????
            R.id.tv_city_changguan -> {
                ARouter.getInstance()
                    .build(ARouterPath.VenuesModule.VENUES_LIST_ACTIVITY)
                    .withString("region", curCityBean.region)
                    .withString("siteId", curCityBean.siteId.toString())
                    .navigation()
            }
            R.id.tv_city_lvyouluxian -> {

            }
            // ??????
            R.id.tv_city_hotel -> {
                ARouter.getInstance().build(ZARouterPath.ZMAIN_HOTEL_LIST)
                    .withString("region", curCityBean.region)
                    .withString("siteId", curCityBean.siteId.toString())
                    .navigation()
            }
            // ??????
            R.id.tv_city_food -> {
                ARouter.getInstance().build(MainARouterPath.MAIN_FOOD_LS)
                    .withString("region", curCityBean.region)
                    .withString("siteId", curCityBean.siteId.toString())
                    .navigation()
            }
            // ??????
            R.id.tv_city_story -> {
                EventBus.getDefault().post(ChangeTabEvent("TIME"))
            }
            // 720??????
            R.id.tv_city_720 -> {
                ARouter.getInstance()
                    .build(ARouterPath.Provider.WEB_ACTIVITY)
                    .withString("html", curCityBean.panoramaUrl)
                    .navigation()
            }
            // ??????
            R.id.tv_city_video -> {
                var position = mBinding.viewPager.currentItem
                if (position != 0) {
                    mBinding.viewPager.currentItem = 0
                }
            }
            R.id.tv_city_imgs -> {
                curCityBean?.let {
                    var videoUrls: List<String> = ArrayList()
                    var videoSize: Int = 0
                    if (!it.videoEx.isNullOrEmpty()) {
                        videoUrls = curCityBean.videoEx!!.split(",")
                        if (!videoUrls.isNullOrEmpty()) {
                            videoSize = videoUrls.size
                        }
                    }
                    if (videoSize > 0) {
                        mBinding.vpCityHead.setCurrentItem(videoSize)
                    } else {
                        mBinding.vpCityHead.setCurrentItem(0)
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        videoDatas?.forEach {
            it?.pause()
        }
        JCVideoPlayer.releaseAllVideos()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoDatas?.forEach {
            it.release()
        }
        JCVideoPlayer.releaseAllVideos()
        GaoDeLocation.getInstance().release()
    }



    private val mOnVideoViewStateChangeListener: VideoView.OnStateChangeListener = object : VideoView.OnStateChangeListener {
        override fun onPlayerStateChanged(playerState: Int) {
            when (playerState) {
                VideoView.PLAYER_NORMAL -> {
                }
                VideoView.PLAYER_FULL_SCREEN -> {
                }
            }
        }

        override fun onPlayStateChanged(playState: Int) {
            when (playState) {
                VideoView.STATE_IDLE -> {
                }
                VideoView.STATE_PREPARING -> {
                }
                VideoView.STATE_PREPARED -> {
                }
                VideoView.STATE_PLAYING -> {
                }
                VideoView.STATE_PAUSED -> {
                }
                VideoView.STATE_BUFFERING -> {
                }
                VideoView.STATE_BUFFERED -> {
                }
                VideoView.STATE_PLAYBACK_COMPLETED -> {
                }
                VideoView.STATE_ERROR -> {

                }
            }
        }
    }
}