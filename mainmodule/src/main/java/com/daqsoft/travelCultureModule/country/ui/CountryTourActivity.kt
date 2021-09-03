package com.daqsoft.travelCultureModule.country.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.RadioButton
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityCountryTourBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.ChangeTabEvent
import com.daqsoft.provider.businessview.adapter.ProviderStoriesAdapter
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.provider.utils.HtmlUtils
import com.daqsoft.provider.view.convenientbanner.bean.VideoImageBean
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.convenientbanner.holder.VideoImageHolder
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import com.daqsoft.travelCultureModule.country.CHANNEL_FW_CODE
import com.daqsoft.travelCultureModule.country.CHANNEL_GL_CODE
import com.daqsoft.travelCultureModule.country.adapter.*
import com.daqsoft.travelCultureModule.country.model.CountryTourViewModel
import com.daqsoft.travelCultureModule.country.util.TextFontUtil
import com.daqsoft.travelCultureModule.country.view.CountryNestScrollView
import com.jakewharton.rxbinding2.view.RxView
import com.scwang.smart.refresh.layout.util.SmartUtil
import me.nereo.multi_image_selector.BigImgActivity
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.connectivityManager
import java.util.concurrent.TimeUnit

/**
 * desc :乡村游首页
 * @author 江云仙
 * @date 2020/4/13 9:42
 */
@Route(path = ARouterPath.CountryModule.COUNTRY_TOUR_LIST)
class CountryTourActivity : TitleBarActivity<ActivityCountryTourBinding, CountryTourViewModel>() {
    /**
     * 乡村游记攻略
     */
    private val travelGuideAdapter = TravelGuideAdapter(this)

    // 当前经纬度
    var currentLat = ""
    var currentLon = ""

    var isHaveVide: Boolean = false
    var isHave720: Boolean = false

    // 头部选项
    private lateinit var checkRadio: RadioButton

    // 头部选项底部横线
    private lateinit var checkView: View

    // 头部选项对应的需要滑动目标值
    internal var scrollViewH1 = 0
    internal var scrollViewH2 = 0
    internal var scrollViewH3 = 0

    // 获取siteId
    @JvmField
    @Autowired
    var jumpSiteId: String = ""
    var siteId: String = ""

    // 城市编码
    // 农家乐适配器
    private var countryHappinessAdapter: CountryHappinessAdapter? = null

    // 全部乡村
    private var countryAllAdapter: CountryAllAdapter? = null

    // 风物推荐
    private var countryFWRecommendAdapter: CountryFWRecommendAdapter? = null

    // 名宿
    private var countryHotelAdapter: CountryHotelAdapter? = null

    // 故事
    private var countryStoryAdapter: ProviderStoriesAdapter? = null

    override fun getLayout(): Int {
        return R.layout.activity_country_tour
    }

    override fun setTitle(): String = getString(R.string.Country_tour)

    override fun injectVm(): Class<CountryTourViewModel> = CountryTourViewModel::class.java

    override fun initView() {
        showLoadingDialog()
        setIniTView()
        setCountryHappiness()
        setClick()
    }

    /**
     * 点击事件
     */
    private fun setClick() {
        // 全部乡村查看更多
        mBinding.countryAll.cdtvAllMore.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.CountryModule.COUNTRY_ALL_MORE_ACTIVITY)
                .navigation()
        }
        // 风物推荐查看更多
        mBinding.countryFwRecommend.cdtvFwRecommendMore.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.CountryModule.COUNTRY_CONTENT_ACTIVITY)
                .withString("channelCode", CHANNEL_FW_CODE)
                .withString("jumpTitle", "乡村游风物推荐")
                .navigation()
        }
        // 风物推荐换一批
        mBinding.countryFwRecommend.llChange.onNoDoubleClick {
            mModel.mFwCurrPage += 1
            mModel.getTravelFWGuide(CHANNEL_FW_CODE)
        }
        // 游记攻略查看更多
        mBinding.countryTravelGuide.cdtvTravelGuideMore.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.CountryModule.COUNTRY_CONTENT_ACTIVITY)
                .withString("channelCode", CHANNEL_GL_CODE)
                .withString("jumpTitle", "乡村游记攻略")
                .navigation()
        }
        // 农家乐查看更多
        mBinding.llCountryTour.cdtvTvTitleMore.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.CountryModule.COUNTRY_HAPPINESS_LIST)
                .navigation()
        }
        // 住民宿查看更多
        mBinding.llLiveStay.cdtvTvTitleMore.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.CountryModule.COUNTRY_HOTEL_LIST)
                .navigation()
        }
        // 故事跳转到时光
        mBinding.llReadStory.cdtvTvTitleMore.onNoDoubleClick {
            EventBus.getDefault().post(ChangeTabEvent("TIME"))
        }
    }

    /**
     *农家乐列表适配器
     */
    @SuppressLint("SetTextI18n")
    private fun setCountryHappiness() {
        countryFWRecommendAdapter = CountryFWRecommendAdapter(this)
        countryFWRecommendAdapter!!.emptyViewShow = false
        val gridLayoutManager = GridLayoutManager(this, 3)
        //设置滑动惯性
        mBinding.countryFwRecommend.recyFwRecommend.isNestedScrollingEnabled = false
        mBinding.countryFwRecommend.recyFwRecommend.layoutManager = gridLayoutManager
        mBinding.countryFwRecommend.recyFwRecommend.adapter = countryFWRecommendAdapter
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (position) {
                    0 -> return 2
                    else -> 1
                }
            }
        }
        // 全部乡村
        countryAllAdapter = CountryAllAdapter(this)
        countryAllAdapter?.emptyViewShow = false
        mBinding.countryAll.recyAllCountry.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBinding.countryAll.recyAllCountry.adapter = countryAllAdapter
        mBinding.countryAll.recyAllCountry.isNestedScrollingEnabled = false
        mModel.countryList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                countryAllAdapter?.clear()
                countryAllAdapter?.add(it)
                countryAllAdapter?.notifyDataSetChanged()
                mBinding.countryAll.root.visibility = View.VISIBLE
            } else {
                mBinding.countryAll.root.visibility = View.GONE
            }
        })
        // 风物推荐
        mModel.travelFWGuides.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                countryFWRecommendAdapter!!.clear()
                mBinding.countryFwRecommend.root.visibility = View.VISIBLE
                countryFWRecommendAdapter!!.add(it)
                countryFWRecommendAdapter!!.notifyDataSetChanged()
                if (it.size < 5) {
                    mModel.mFwCurrPage = 0
                }
            } else {
                mBinding.countryFwRecommend.root.visibility = View.GONE
            }
        })
        // 乡村游记攻略
        mModel.travelGLGuides.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.countryTravelGuide.root.visibility = View.VISIBLE
                travelGuideAdapter.menus.addAll(it)
                travelGuideAdapter.notifyDataSetChanged()
            } else {
                mBinding.countryTravelGuide.root.visibility = View.GONE
            }
            // 乡村游记攻略
            mBinding.countryTravelGuide.xTravelGuide.setAdapter(travelGuideAdapter)
//            mBinding.countryTravelGuide.xTravelGuide.setPageTransformer(BranchScalePageTransformer())
        })

        //天气
        mModel.weather.observe(this, Observer {
            mBinding.countryHeader.tvWeather.visibility = View.VISIBLE
            mBinding.countryHeader.url = it.pic
            if(it.min.isNullOrEmpty()||it.max.isNullOrEmpty()){
                mBinding.countryHeader.tvWeather.text=""
            }else {
                mBinding.countryHeader.tvWeather.text = it.min + "-" + it.max + "℃"
            }
        })
        // 农家乐
        countryHappinessAdapter = CountryHappinessAdapter()
        countryHappinessAdapter!!.emptyViewShow = false
        mBinding.llCountryTour.rvCountry.layoutManager = GridLayoutManager(this, 2)
        mBinding.llCountryTour.rvCountry.adapter = countryHappinessAdapter
        //农家乐适配器
        mModel.agritainment.observe(this, Observer {
            val data = it
            if (!data.isNullOrEmpty()) {
                mBinding.llCountryTour.root.visibility = View.VISIBLE
                mBinding.llHeaderTitle.rbCountryHappiness.visibility = View.VISIBLE
                mBinding.llHeaderTitle.llRadio1.visibility = View.VISIBLE
                mBinding.llHeaderTitle.root.visibility = View.VISIBLE
                countryHappinessAdapter?.add(data)
                countryHappinessAdapter?.notifyDataSetChanged()
            } else {
                mBinding.llCountryTour.root.visibility = View.GONE
                mBinding.llHeaderTitle.rbCountryHappiness.visibility = View.INVISIBLE
                mBinding.llHeaderTitle.llRadio1.visibility = View.GONE
            }
        })
        //名宿适配器
        countryHotelAdapter = CountryHotelAdapter()
        countryHotelAdapter!!.emptyViewShow = false
        mBinding.llLiveStay.rvCountry.layoutManager = GridLayoutManager(this, 2)
        mBinding.llLiveStay.rvCountry.adapter = countryHotelAdapter
        mModel.hotelList.observe(this, Observer {
            val data = it
            if (!data.isNullOrEmpty()) {
                mBinding.llLiveStay.root.visibility = View.VISIBLE
                mBinding.llHeaderTitle.rbLiveStay.visibility = View.VISIBLE
                mBinding.llHeaderTitle.root.visibility = View.VISIBLE
                countryHotelAdapter?.add(data)
                countryHotelAdapter?.notifyDataSetChanged()
            } else {
                mBinding.llLiveStay.root.visibility = View.GONE
                mBinding.llHeaderTitle.rbLiveStay.visibility = View.INVISIBLE
            }
        })

        // 故事类别
        countryStoryAdapter = ProviderStoriesAdapter(this)
        countryStoryAdapter!!.emptyViewShow = false
        mBinding.llReadStory.rvCountry.layoutManager = GridLayoutManager(this, 2)
        mBinding.llReadStory.rvCountry.adapter = countryStoryAdapter
        mModel.storyList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.llReadStory.root.visibility = View.VISIBLE
                mBinding.llHeaderTitle.rbReadStory.visibility = View.VISIBLE
                mBinding.llHeaderTitle.rbReadStory.isClickable = true
                mBinding.llHeaderTitle.root.visibility = View.VISIBLE
                countryStoryAdapter?.add(it)
                countryStoryAdapter?.notifyDataSetChanged()
            } else {
                mBinding.llReadStory.llList1.visibility = View.GONE
                mBinding.llHeaderTitle.rbReadStory.visibility = View.INVISIBLE
                mBinding.llHeaderTitle.rbReadStory.isClickable = false

            }
        })


        //头部banner
        mModel.visitingCardBean.observe(this, Observer { it ->
            dissMissLoadingDialog()
            mBinding.countryTourCoor.visibility = View.VISIBLE
            mBinding.countryHeader.root.visibility = View.VISIBLE
            mBinding.countryHeader.tvIntroduce.text = HtmlUtils.html2Str(it.introduce)
            //获取用户昵称
            mModel.nickName.observe(this, Observer { childIt ->
                if (!it.name.isNullOrEmpty()) {
                    mBinding.countryHeader.tvTitle.visibility = View.VISIBLE
                    var countryTitle = ""
                    countryTitle = if (childIt == "null" || childIt.isNullOrEmpty()) {
                        getString(R.string.country_title, "游客") + it.name
                    } else {
                        getString(R.string.country_title, childIt) + it.name
                    }
                    val countryTitleStr = TextFontUtil.setTextSize(
                        countryTitle,
                        SmartUtil.dp2px(19f),
                        countryTitle.indexOf("是") + 1,
                        countryTitle.length
                    )
                    if (countryTitleStr != null) {
                        mBinding.countryHeader.tvTitle.text = countryTitleStr
                        RxView.clicks(mBinding.countryHeader.tvTitle)
                            .throttleFirst(1, TimeUnit.SECONDS).subscribe { clickId ->
                                ARouter.getInstance()
                                    .build(ARouterPath.CountryModule.COUNTRY_CITY_LIST_ACTIVITY)
                                    .withString(
                                        "siteId",
                                        SPUtils.getInstance().getString(SPKey.SITE_ID)
                                    )
                                    .navigation()
                            }

                    }

                } else {
                    mBinding.countryHeader.tvTitle.visibility = View.GONE
                }
            })

            if (!it.region.isNullOrEmpty()) {
                mModel.getWeather(it.region)
            }

            mBinding.countryHeader.tvSummary.text = it.summary
            val dataVideoImages: MutableList<VideoImageBean> = mutableListOf()
            //视频
            if (!it.video.isNullOrEmpty()) {
                dataVideoImages.add(0, VideoImageBean().apply {
                    type = 1
                    videoUrl = it.videoEx
                    imageUrl = it.videoCoverEx
                })
                isHaveVide = true
                mBinding.countryHeader.txtCountryDetailVideo.visibility = View.VISIBLE
            }
            val images = it.images
//            //720
            if (!it.panoramaUrl.isNullOrEmpty()) {
                if (dataVideoImages.size > 0) {
                    dataVideoImages.add(1, VideoImageBean().apply {
                        type = 2
                        videoUrl = it.panoramaUrl
                        if (!images.isNullOrEmpty()) {
                            imageUrl = images[0]
                        }
                        name = it.name
                    })
                } else {
                    dataVideoImages.add(0, VideoImageBean().apply {
                        type = 2
                        videoUrl = it.panoramaUrl
                        if (!images.isNullOrEmpty()) {
                            imageUrl = images[0]
                        }
                        name = it.name
                    })
                }

                isHave720 = true
                mBinding.countryHeader.txtCountryDetailPannaor.visibility = View.VISIBLE
            }
//            //图片
            if (!images.isNullOrEmpty()) {
                for (item in images) {
                    dataVideoImages.add(VideoImageBean().apply {
                        type = 0
                        imageUrl = item
                    })
                }
                mBinding.countryHeader.txtCountryDetailImages.visibility = View.VISIBLE
//                mBinding.countryHeader.txtCountryDetailImages.text = "1/${images.size}"
            }

            if (!dataVideoImages.isNullOrEmpty()) {
                mBinding.countryHeader.cbrCountryDetail.visibility = View.VISIBLE
                mBinding.countryHeader.cbrCountryDetail.setPages(object : CBViewHolderCreator {
                    override fun createHolder(itemView: View?): Holder<*> {
                        return VideoImageHolder(itemView!!, this@CountryTourActivity)
                    }

                    override fun getLayoutId(): Int {
                        return R.layout.layout_video_image
                    }
                }, dataVideoImages).setCanLoop(false).setPointViewVisible(false)
                    .setOnItemClickListener {
                        when (dataVideoImages[it].type) {
                            0 -> {
                                // 图片点击
                                val intent =
                                    Intent(this@CountryTourActivity, BigImgActivity::class.java)
                                intent.putExtra("position", it)
                                intent.putStringArrayListExtra(
                                    "imgList",
                                    ArrayList(images)
                                )
                                startActivity(intent)
                            }
                            1 -> {
                            }
                            2 -> {
                                // 点击720

                            }
                        }
                    }
                mBinding.countryHeader.cbrCountryDetail.onPageChangeListener =
                    object : OnPageChangeListener {
                        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                        }

                        override fun onPageSelected(index: Int) {
                            var pos = index
                            if (dataVideoImages[index].type == 0) {
                                if (isHave720) {
                                    pos -= 1
                                }
                                if (isHaveVide) {
                                    pos -= 1
                                }
//                            if (pos >= 0) {
////                                mBinding.countryHeader.txtCountryDetailImages.text = "${pos + 1}/${images?.size}"
//                            }
                            }
                        }

                        override fun onScrollStateChanged(
                            recyclerView: RecyclerView?,
                            newState: Int
                        ) {
                        }

                    }
            } else {
                mBinding.countryHeader.cbrCountryDetail.visibility = View.GONE
            }
        })

        mModel.labelId.observe(this, Observer { it ->
            mModel.type = it.labelId
            mModel.getApiHotelList()
        })

        //是否显示风物推荐按钮
        mModel.fwTotalPage.observe(this, Observer {
            if (it != -1 && it > 5) {
                mBinding.countryFwRecommend.llChange.visibility = View.VISIBLE
            } else {
                mBinding.countryFwRecommend.llChange.visibility = View.GONE
            }

        })

        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })
    }

    /**
     *初始化数据
     */
    private fun setIniTView() {
        mBinding.llCountryTour.tvTitleName.text = "农家乐"
        mBinding.llLiveStay.tvTitleName.text = "住民宿"
        mBinding.llReadStory.tvTitleName.text = "读故事"
        setScrollViewListener()


    }

    /**
     * 设置底部ScrollView滑动事件
     * */
    private fun setScrollViewListener() {
        checkRadio = mBinding.llHeaderTitle.rbCountryHappiness
        checkView = mBinding.llHeaderTitle.llRadio1
        //默认设置第一个点击
        checkRadio.setTextColor(resources.getColor(R.color.main))
        checkRadio.paint.isFakeBoldText = true
        checkView.background = resources.getDrawable(R.drawable.country_green_underline)
        //有数据后需要在数据加载完成后设置
        mBinding.llHeaderTitle.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_country_happiness -> clickRadio(
                    mBinding.llHeaderTitle.rbCountryHappiness,
                    mBinding.llHeaderTitle.llRadio1
                )
                R.id.rb_live_stay -> clickRadio(
                    mBinding.llHeaderTitle.rbLiveStay,
                    mBinding.llHeaderTitle.llRadio2
                )
                R.id.rb_read_story -> clickRadio(
                    mBinding.llHeaderTitle.rbReadStory,
                    mBinding.llHeaderTitle.llRadio3
                )
            }
        }
        mBinding.llHeaderTitle.rbCountryHappiness.setOnClickListener {
            mBinding.nestScrollView.scrollTo(
                0,
                scrollViewH1
            )
        }
        mBinding.llHeaderTitle.rbLiveStay.setOnClickListener {
            mBinding.nestScrollView.scrollTo(
                0,
                scrollViewH2
            )
        }
        mBinding.llHeaderTitle.rbReadStory.setOnClickListener {
            mBinding.nestScrollView.scrollTo(
                0,
                scrollViewH3
            )
        }
        mBinding.nestScrollView.setOnScrollListener(object :
            CountryNestScrollView.OnScrollListener {
            override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
                if (scrollViewH2 == 0) {
                    scrollViewH1 = mBinding.llCountryTour.llList1.y.toInt()
                    scrollViewH2 = mBinding.llLiveStay.llList1.y.toInt()
                    scrollViewH3 = mBinding.llReadStory.llList1.y.toInt()
                }
            }

            override fun onOverScrolled(
                scrollX: Int,
                scrollY: Int,
                clampedX: Boolean,
                clampedY: Boolean
            ) {
                when (scrollY) {
                    in (scrollViewH1 + 1) until scrollViewH2 -> mBinding.llHeaderTitle.rbCountryHappiness.isChecked =
                        true
                    in (scrollViewH2 + 1) until scrollViewH3 -> mBinding.llHeaderTitle.rbLiveStay.isChecked =
                        true
                    in (scrollViewH3 + 1) until (scrollViewH3 + scrollViewH3) -> mBinding.llHeaderTitle.rbReadStory.isChecked =
                        true
                }
            }
        })
    }

    /**
     *设置选中标题栏的状态颜色
     */
    private fun clickRadio(radioButton: RadioButton, lineView: View) {
        //去重复
        if (checkRadio === radioButton) {
            return
        }
        checkRadio.setTextColor(Color.rgb(102, 102, 102))
        checkRadio.paint.isFakeBoldText = false
        checkView.setBackgroundColor(Color.argb(0, 0, 0, 0))
        checkRadio = radioButton
        checkView = lineView
        radioButton.setTextColor(resources.getColor(R.color.main))
        radioButton.paint.isFakeBoldText = true
        lineView.background = resources.getDrawable(R.drawable.country_green_underline)
    }

    override fun initData() {
        mModel.mPageSize = 4
        GaoDeLocation.getInstance().init(this, object : GaoDeLocation.OnGetCurrentLocationLisner {
            override fun onResult(
                adCode: String?,
                result: String?,
                lat: Double,
                lon: Double,
                adcode: String?
            ) {
                currentLat = lat.toString()
                currentLon = lon.toString()
                mModel.lat = currentLat
                mModel.lon = currentLon
                mModel.getAgritainmentList()
//                if (jumpSiteId == null) {
                    mModel.getCloudLabelId()
//                }

            }

            override fun onError(errorMsg: String?) {

            }

        })
        mModel.refresh()
        //获取siteId
        if (jumpSiteId == null) {
            siteId = SPUtils.getInstance().getString(SPKey.SITE_ID)
        } else {
            mModel.areaSiteSwitch = jumpSiteId
            siteId = jumpSiteId
        }

        //故事
        mModel.getStoryList()
        //乡村游记攻略
        mModel.getTravelGuide(CHANNEL_GL_CODE)
        // 全部乡村
        mModel.getCountryAllList(jumpSiteId)
        //风物推荐
        mModel.getTravelFWGuide(CHANNEL_FW_CODE)

        //城市编码
//        val ad_code = SPUtils.getInstance().getString(SPUtils.Config.AD_CODE)
        //乡村游头部信息
        mModel.getVisitingCard(siteId)
        //获取天气
//        mModel.getWeather(ad_code)


    }

    //视频退出播放
    override fun onPause() {
        super.onPause()
        mBinding.countryHeader.cbrCountryDetail.pauseVideoPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            EventBus.getDefault().unregister(this)
            mBinding.countryHeader.cbrCountryDetail?.stopVideoPlayer()
            GaoDeLocation.getInstance().release()
        } catch (e: Exception) {
        }

    }
}
