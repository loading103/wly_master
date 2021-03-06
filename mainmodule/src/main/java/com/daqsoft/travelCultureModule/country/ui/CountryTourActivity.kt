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
 * desc :???????????????
 * @author ?????????
 * @date 2020/4/13 9:42
 */
@Route(path = ARouterPath.CountryModule.COUNTRY_TOUR_LIST)
class CountryTourActivity : TitleBarActivity<ActivityCountryTourBinding, CountryTourViewModel>() {
    /**
     * ??????????????????
     */
    private val travelGuideAdapter = TravelGuideAdapter(this)

    // ???????????????
    var currentLat = ""
    var currentLon = ""

    var isHaveVide: Boolean = false
    var isHave720: Boolean = false

    // ????????????
    private lateinit var checkRadio: RadioButton

    // ????????????????????????
    private lateinit var checkView: View

    // ??????????????????????????????????????????
    internal var scrollViewH1 = 0
    internal var scrollViewH2 = 0
    internal var scrollViewH3 = 0

    // ??????siteId
    @JvmField
    @Autowired
    var jumpSiteId: String = ""
    var siteId: String = ""

    // ????????????
    // ??????????????????
    private var countryHappinessAdapter: CountryHappinessAdapter? = null

    // ????????????
    private var countryAllAdapter: CountryAllAdapter? = null

    // ????????????
    private var countryFWRecommendAdapter: CountryFWRecommendAdapter? = null

    // ??????
    private var countryHotelAdapter: CountryHotelAdapter? = null

    // ??????
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
     * ????????????
     */
    private fun setClick() {
        // ????????????????????????
        mBinding.countryAll.cdtvAllMore.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.CountryModule.COUNTRY_ALL_MORE_ACTIVITY)
                .navigation()
        }
        // ????????????????????????
        mBinding.countryFwRecommend.cdtvFwRecommendMore.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.CountryModule.COUNTRY_CONTENT_ACTIVITY)
                .withString("channelCode", CHANNEL_FW_CODE)
                .withString("jumpTitle", "?????????????????????")
                .navigation()
        }
        // ?????????????????????
        mBinding.countryFwRecommend.llChange.onNoDoubleClick {
            mModel.mFwCurrPage += 1
            mModel.getTravelFWGuide(CHANNEL_FW_CODE)
        }
        // ????????????????????????
        mBinding.countryTravelGuide.cdtvTravelGuideMore.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.CountryModule.COUNTRY_CONTENT_ACTIVITY)
                .withString("channelCode", CHANNEL_GL_CODE)
                .withString("jumpTitle", "??????????????????")
                .navigation()
        }
        // ?????????????????????
        mBinding.llCountryTour.cdtvTvTitleMore.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.CountryModule.COUNTRY_HAPPINESS_LIST)
                .navigation()
        }
        // ?????????????????????
        mBinding.llLiveStay.cdtvTvTitleMore.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.CountryModule.COUNTRY_HOTEL_LIST)
                .navigation()
        }
        // ?????????????????????
        mBinding.llReadStory.cdtvTvTitleMore.onNoDoubleClick {
            EventBus.getDefault().post(ChangeTabEvent("TIME"))
        }
    }

    /**
     *????????????????????????
     */
    @SuppressLint("SetTextI18n")
    private fun setCountryHappiness() {
        countryFWRecommendAdapter = CountryFWRecommendAdapter(this)
        countryFWRecommendAdapter!!.emptyViewShow = false
        val gridLayoutManager = GridLayoutManager(this, 3)
        //??????????????????
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
        // ????????????
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
        // ????????????
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
        // ??????????????????
        mModel.travelGLGuides.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.countryTravelGuide.root.visibility = View.VISIBLE
                travelGuideAdapter.menus.addAll(it)
                travelGuideAdapter.notifyDataSetChanged()
            } else {
                mBinding.countryTravelGuide.root.visibility = View.GONE
            }
            // ??????????????????
            mBinding.countryTravelGuide.xTravelGuide.setAdapter(travelGuideAdapter)
//            mBinding.countryTravelGuide.xTravelGuide.setPageTransformer(BranchScalePageTransformer())
        })

        //??????
        mModel.weather.observe(this, Observer {
            mBinding.countryHeader.tvWeather.visibility = View.VISIBLE
            mBinding.countryHeader.url = it.pic
            if(it.min.isNullOrEmpty()||it.max.isNullOrEmpty()){
                mBinding.countryHeader.tvWeather.text=""
            }else {
                mBinding.countryHeader.tvWeather.text = it.min + "-" + it.max + "???"
            }
        })
        // ?????????
        countryHappinessAdapter = CountryHappinessAdapter()
        countryHappinessAdapter!!.emptyViewShow = false
        mBinding.llCountryTour.rvCountry.layoutManager = GridLayoutManager(this, 2)
        mBinding.llCountryTour.rvCountry.adapter = countryHappinessAdapter
        //??????????????????
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
        //???????????????
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

        // ????????????
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


        //??????banner
        mModel.visitingCardBean.observe(this, Observer { it ->
            dissMissLoadingDialog()
            mBinding.countryTourCoor.visibility = View.VISIBLE
            mBinding.countryHeader.root.visibility = View.VISIBLE
            mBinding.countryHeader.tvIntroduce.text = HtmlUtils.html2Str(it.introduce)
            //??????????????????
            mModel.nickName.observe(this, Observer { childIt ->
                if (!it.name.isNullOrEmpty()) {
                    mBinding.countryHeader.tvTitle.visibility = View.VISIBLE
                    var countryTitle = ""
                    countryTitle = if (childIt == "null" || childIt.isNullOrEmpty()) {
                        getString(R.string.country_title, "??????") + it.name
                    } else {
                        getString(R.string.country_title, childIt) + it.name
                    }
                    val countryTitleStr = TextFontUtil.setTextSize(
                        countryTitle,
                        SmartUtil.dp2px(19f),
                        countryTitle.indexOf("???") + 1,
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
            //??????
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
//            //??????
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
                                // ????????????
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
                                // ??????720

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

        //??????????????????????????????
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
     *???????????????
     */
    private fun setIniTView() {
        mBinding.llCountryTour.tvTitleName.text = "?????????"
        mBinding.llLiveStay.tvTitleName.text = "?????????"
        mBinding.llReadStory.tvTitleName.text = "?????????"
        setScrollViewListener()


    }

    /**
     * ????????????ScrollView????????????
     * */
    private fun setScrollViewListener() {
        checkRadio = mBinding.llHeaderTitle.rbCountryHappiness
        checkView = mBinding.llHeaderTitle.llRadio1
        //???????????????????????????
        checkRadio.setTextColor(resources.getColor(R.color.main))
        checkRadio.paint.isFakeBoldText = true
        checkView.background = resources.getDrawable(R.drawable.country_green_underline)
        //????????????????????????????????????????????????
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
     *????????????????????????????????????
     */
    private fun clickRadio(radioButton: RadioButton, lineView: View) {
        //?????????
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
        //??????siteId
        if (jumpSiteId == null) {
            siteId = SPUtils.getInstance().getString(SPKey.SITE_ID)
        } else {
            mModel.areaSiteSwitch = jumpSiteId
            siteId = jumpSiteId
        }

        //??????
        mModel.getStoryList()
        //??????????????????
        mModel.getTravelGuide(CHANNEL_GL_CODE)
        // ????????????
        mModel.getCountryAllList(jumpSiteId)
        //????????????
        mModel.getTravelFWGuide(CHANNEL_FW_CODE)

        //????????????
//        val ad_code = SPUtils.getInstance().getString(SPUtils.Config.AD_CODE)
        //?????????????????????
        mModel.getVisitingCard(siteId)
        //????????????
//        mModel.getWeather(ad_code)


    }

    //??????????????????
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
