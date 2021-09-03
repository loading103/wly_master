package com.daqsoft.travelCultureModule.country.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityCountryDetailBinding
import com.daqsoft.mainmodule.databinding.ItemPanorBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.businessview.adapter.ProviderActivityV2Adapter
import com.daqsoft.provider.businessview.event.LoginStatusEvent
import com.daqsoft.provider.businessview.event.UpdateCommentEvent
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.businessview.view.ListenerAudioView
import com.daqsoft.provider.businessview.view.OrderAddressPopWindow
import com.daqsoft.provider.businessview.view.ProviderRecommendView
import com.daqsoft.provider.electronicBeans.ProductBean
import com.daqsoft.provider.event.UpdateAudioPlayerEvent
import com.daqsoft.provider.event.UpdateScenicVideoPlayerEvent
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.net.StatisticsRepository
import com.daqsoft.provider.network.venues.bean.AudioInfo
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.provider.view.popupwindow.SharePopWindow
import com.daqsoft.travelCultureModule.country.adapter.CountryHappinessNewAdapter
import com.daqsoft.travelCultureModule.country.adapter.CountryHotelAdapter
import com.daqsoft.travelCultureModule.country.adapter.CountryScenicSpotAdapter
import com.daqsoft.travelCultureModule.country.fragment.CountryDetailTopFragment
import com.daqsoft.travelCultureModule.country.model.CountryDetailViewModel
import com.daqsoft.travelCultureModule.hotel.view.RouteOrderView
import com.daqsoft.travelCultureModule.resource.view.AppointmentPopWindow
import com.daqsoft.travelCultureModule.resource.view.RouterPopWindow
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.textColor

/**
 * 乡村详情页面
 */
@Route(path = ARouterPath.CountryModule.COUNTRY_COUNTRY_DETAIL_ACTIVITY)
class CountryDetailActivity :
    TitleBarActivity<ActivityCountryDetailBinding, CountryDetailViewModel>() {
    @JvmField
    @Autowired
    var id: String = ""

    /**
     * 景点适配器
     */
    var spotAdapter: CountryScenicSpotAdapter? = null

    /**
     * 热门活动适配器
     */
    var hotActivityAdapter: ProviderActivityV2Adapter? = null

    /**
     * 农家乐适配器
     */
    private var countryHappinessAdapter: CountryHappinessNewAdapter? = null

    /**
     * 名宿
     */
    private var countryHotelAdapter: CountryHotelAdapter? = null

    /**
     * 乡村详情实体
     */
    var mCountryDetailBean: CountryDetailBean? = null

    /**
     * 乡村标签
     */
    var scenicTags: ScenicTags? = null

    /**
     * 品牌背景drawalbe
     */
    var brandGradientDrawable: GradientDrawable? = null

    /**
     * 乡村预览须知window
     */
    var appointMentPopWindow: AppointmentPopWindow? = null

    var routerPopWindow: RouterPopWindow? = null

    var countryDetailTopFragment: CountryDetailTopFragment? = null
    /**
     * 分享弹窗
     */
    var sharePopWindow: SharePopWindow? = null
    /**
     * 门票预订
     */
    var orderAddressPopWindow: OrderAddressPopWindow? = null

    override fun getLayout(): Int = R.layout.activity_country_detail

    override fun setTitle(): String = getString(R.string.country_detail)

    override fun injectVm(): Class<CountryDetailViewModel> = CountryDetailViewModel::class.java

    override fun initView() {
        mBinding.vm = mModel
        EventBus.getDefault().register(this)
        initViewEvent()
        initViewModel()
    }
    override fun initCustomTitleBar(mTitleBar: TitleBar) {
        showShareButton(com.daqsoft.provider.R.mipmap.common_nav_button_share)
        setShareClick(object : View.OnClickListener {
            override fun onClick(v: View?) {
                mCountryDetailBean?.let {
                    if (sharePopWindow == null) {
                        sharePopWindow = SharePopWindow(this@CountryDetailActivity)
                    }
                    var content= Constant.SHARE_DEC
                    if(!TextUtils.isEmpty(it.summary)){
                        content=it.summary
                    }
                    sharePopWindow?.setShareContent(
                        it?.name, content,  it?.images.getRealImages(),
                        ShareModel.getXCDesc(it?.id.toString())
                    )
                    if (!sharePopWindow!!.isShowing) {
                        sharePopWindow?.showAsDropDown(mTitleBar)
                    }
                }
            }

        })
    }
    override fun initData() {
        mModel.id = id

        showLoadingDialog()
        // 乡村详情
        mModel.getCountryInfo(id, true)
        //获取品牌信息
//        mModel.getScenicBrandList(id)
        // 故事列表
        mModel.getStoryList(id, ResourceType.CONTENT_TYPE_COUNTRY)
        // 评论
        mModel.getActivityCommentList(id)
        // 定位
        doLocation(ResourceType.CONTENT_TYPE_COUNTRY)
        // 乡村资讯
        mModel.getScenicContentLs(id)
    }


    override fun initPageParams() {
        isHideAnother = false
    }

    private fun initViewEvent() {
        // 景点
        spotAdapter = CountryScenicSpotAdapter()
        // 旅游路线
        // 直播
        mBinding.rvPanor.layoutManager = StaggeredGridLayoutManager(
            2, StaggeredGridLayoutManager
                .VERTICAL
        )
        mBinding.rvPanor.adapter = liveAdapter
        // 活动
        hotActivityAdapter = ProviderActivityV2Adapter(this)
        val activityLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvActivities.layoutManager = activityLayoutManager
        mBinding.rvActivities.adapter = hotActivityAdapter


        mBinding.llVenuesDetailsComplaint.onNoDoubleClick {
            // 旅游投诉
            MenuJumpUtils.gotoComplaint()
//            ARouter.getInstance().build(MainARouterPath.MAIN_COMPLAINT_ACTIVITY)
//                .navigation()
        }
        mBinding.llVenuesDetailsBus.onNoDoubleClick {
            // 公交查询
            ARouter.getInstance().build(ARouterPath.ServiceModule.SERVICE_QUERY_BUS_ACTIVITY)
                .navigation()
        }

        mBinding.vScenicDetailAudios?.onPlayerListener =
            object : ListenerAudioView.OnAudioPlayerListener {
                override fun onStartPlayer() {
                    EventBus.getDefault().post(UpdateScenicVideoPlayerEvent(1))
                }

            }
        mBinding.prvScenicDetail.onItemClickTabListener =
            object : ProviderRecommendView.OnItemClickTabListener {
                override fun getMapResourceRecommend(type: String) {
                    if (mCountryDetailBean != null && (mCountryDetailBean?.latitude?:0.0)>0 && (mCountryDetailBean?.longitude?:0.0)>0 ) {
                        mModel.gethMapRecList(
                            type,
                            id,
                            mCountryDetailBean?.longitude.toString(),
                            mCountryDetailBean?.latitude.toString()
                        )
                    }
                }
            }
        mBinding.tvScenicDetailCollect.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                if (mCountryDetailBean != null && mCountryDetailBean!!.vipResourceStatus != null) {
                    showLoadingDialog()
                    if (mCountryDetailBean!!.vipResourceStatus!!.collectionStatus) {
                        mModel.collectionCancel(id)
                    } else {
                        mModel.collection(id)
                    }
                }
            } else {
                ToastUtils.showMessage("该操作需要登录，请先登录")
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
        mBinding.tvScenicDetailThumb.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                if (mCountryDetailBean != null && mCountryDetailBean!!.vipResourceStatus != null) {
                    showLoadingDialog()
                    if (mCountryDetailBean!!.vipResourceStatus!!.likeStatus) {
                        mModel.thumbCancell(id)
                    } else {
                        mModel.thumbUp(id)
                    }
                }
            } else {
                ToastUtils.showMessage("该操作需要登录，请先登录")
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
        mBinding.tvScenicDetailCommentNum.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.Provider.PROVIDER_ORDER_COM_ACTIVITY)
                .withString("id", id)
                .withString("type", ResourceType.CONTENT_TYPE_COUNTRY)
                .withString("contentTitle", mCountryDetailBean!!.name)
                .navigation()
        }
        mBinding.tvScenicDetailShare.onNoDoubleClick {

        }
        // 农家乐查看更多
        mBinding.llCountryTour.cdtvTvTitleMore.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.CountryModule.COUNTRY_HAPPINESS_LIST)
                .navigation()
        }
        // 民宿查看更多
        mBinding.llLiveStay.cdtvTvTitleMore.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.CountryModule.COUNTRY_HOTEL_LIST)
                .navigation()
        }
    }

    private fun initViewModel() {

        initScenicInfoViewModel()
//        initScenicProductViewModel()
        // 错误处理
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })
        mModel.contentLsLd.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.prvConentLs.visibility = View.VISIBLE
                mBinding.prvConentLs.setData(id, ResourceType.CONTENT_TYPE_COUNTRY, it)
            } else {
                mBinding.prvConentLs.visibility = View.GONE
            }
        })
        mModel.scenicDetail.observe(this, Observer {
            if (!it.introduce.isNullOrEmpty()) {
                mBinding.tvIntroduce.settings.defaultTextEncodingName = "utf-8"
                mBinding.tvIntroduce.settings.javaScriptEnabled = true
                mBinding.tvIntroduce.loadDataWithBaseURL(
                    null, StringUtil.getHtml(it.introduce),
                    "text/html", "utf-8", null
                )
                mBinding.tvIntroduce.visibility = View.VISIBLE
            } else {
                mBinding.tvIntroduce.visibility = View.GONE
            }
        })

        mModel.scenicDetail.observe(this, Observer {
            if (!it.trafficInfo.isNullOrEmpty()) {
                mBinding.tvTrafficinfo.settings.defaultTextEncodingName = "utf-8"
                mBinding.tvTrafficinfo.settings.javaScriptEnabled = true
                mBinding.tvTrafficinfo.loadDataWithBaseURL(
                    null, StringUtil.getHtml(it.trafficInfo),
                    "text/html", "utf-8", null
                )
                mBinding.tvTrafficinfo.visibility = View.VISIBLE
            } else {
                mBinding.tvTrafficinfo.visibility = View.GONE
            }
        })

        // 农家乐
        countryHappinessAdapter = CountryHappinessNewAdapter()
        countryHappinessAdapter!!.emptyViewShow = false
        mBinding.llCountryTour.rvCountry.layoutManager = GridLayoutManager(this, 2)
        mBinding.llCountryTour.rvCountry.adapter = countryHappinessAdapter
        //农家乐适配器
        mModel.agritainment.observe(this, Observer {
            val data = it
            if (!data.isNullOrEmpty()) {
                mBinding.llCountryTour.tvTitleName.text = "农家乐"
                mBinding.llCountryTour.tvTitleName.compoundDrawablePadding =   resources.getDimension(R.dimen.dp_12).toInt()
                mBinding.llCountryTour.tvTitleName.textColor = resources.getColor(R.color.txt_black)
                mBinding.llCountryTour.tvTitleName.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.mipmap.whcgxq_bt_bq),null,null,null)
                mBinding.llCountryTour.root.visibility = View.VISIBLE
                countryHappinessAdapter?.add(data)
                countryHappinessAdapter?.notifyDataSetChanged()
            } else {
                mBinding.llCountryTour.root.visibility = View.GONE
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
                mBinding.llLiveStay.tvTitleName.text = "民宿"
                mBinding.llLiveStay.tvTitleName.compoundDrawablePadding =  resources.getDimension(R.dimen.dp_12).toInt()
                mBinding.llLiveStay.tvTitleName.textColor = resources.getColor(R.color.txt_black)
                mBinding.llLiveStay.tvTitleName.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.mipmap.whcgxq_bt_bq),null,null,null)
                mBinding.llLiveStay.root.visibility = View.VISIBLE
                countryHotelAdapter?.add(data)
                countryHotelAdapter?.notifyDataSetChanged()
            } else {
                mBinding.llLiveStay.root.visibility = View.GONE
            }
        })
    }

    /**
     * 乡村展示相关信息
     */
    private fun initScenicInfoViewModel() {
        // 景点
        mModel.spots.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                var imagUrl: String? = ""
                if (mCountryDetailBean != null) {
                    if (!mCountryDetailBean!!.images.isNullOrEmpty()) {
                        imagUrl = mCountryDetailBean!!.images.getRealImages()
                    }
                }
                mBinding.vScenicSpots.setData(
                    it,
                    supportFragmentManager,
                    mCountryDetailBean?.name,
                    imagUrl,
                    scenicTags,
                    id.toInt(),
                    getString(R.string.country_spots)
                )
                mBinding.vScenicSpots.visibility = View.VISIBLE
            } else {
                if (mBinding.vScenicSpots.isVisible)
                    mBinding.vScenicSpots.hideSpotsContent()
            }
        })
        // 刷新乡村数据
        mModel.refereshScenicDetail.observe(this, Observer {
            if (it != null) {
                mCountryDetailBean = it
                if (it.vipResourceStatus != null) {
                    // 收藏
                    setCollectUi(it.vipResourceStatus.collectionStatus)
                    // 点赞
                    setThumbUi(it.vipResourceStatus.likeStatus)
                }
                if (it.collectionNum>0) {
                    mBinding.tvScenicDetailCollect.text = "${it.collectionNum}"
                }
                if (it.likeNum>0) {
                    mBinding.tvScenicDetailThumb.text = "${it.likeNum}"
                }
            }
        })
        // 收藏
        mModel.colllectLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            setCollectUi(it)
            mCountryDetailBean!!.vipResourceStatus?.collectionStatus = it
            var temp = mCountryDetailBean!!.collectionNum
            if (temp>0) {
                var collectNum: Int = temp.toInt()
                if (it) {
                    // 收藏成功
                    val result = collectNum + 1
                    mCountryDetailBean!!.collectionNum = result
                    mBinding.tvScenicDetailCollect.text = result.toString()
                } else {
                    // 取消收藏
                    if (collectNum > 0) {
                        val result = collectNum - 1
                        mCountryDetailBean!!.collectionNum = result
                        if (result > 0) {
                            mBinding.tvScenicDetailCollect.text = result.toString()
                        } else {
                            mBinding.tvScenicDetailCollect.text = "收藏"
                        }
                    }
                }
            }

        })
        // 点赞
        mModel.thumbLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            setThumbUi(it)
            mCountryDetailBean!!.vipResourceStatus?.likeStatus = it
            var temp = mCountryDetailBean!!.likeNum
            if (temp>0) {
                var likeNum: Int = temp.toInt()
                if (it) {
                    // 点赞成功
                    val result = likeNum + 1
                    mCountryDetailBean!!.likeNum = result
                    mBinding.tvScenicDetailThumb.text = result.toString()
                } else {
                    // 取消点赞
                    if (likeNum > 0) {
                        val result = likeNum - 1
                        mCountryDetailBean!!.likeNum = result
                        if (result > 0) {
                            mBinding.tvScenicDetailThumb.text = result.toString()
                        } else {
                            mBinding.tvScenicDetailThumb.text = "点赞"
                        }
                    }
                }
            }
        })

        // 故事列表
        mModel.storyList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.psvScenicStories.visibility = View.VISIBLE
                mBinding.psvScenicStories?.let {it1->
                    it1.resourceType = ResourceType.CONTENT_TYPE_COUNTRY
                    it1.resourceId = id
                    it1.setDataNew(it)
                }
            } else {
                mBinding.psvScenicStories.visibility = View.GONE
            }
        })
        // 旅游路线
        mModel.routeResult.observe(this, Observer {
            if (!it.productListVO.isNullOrEmpty()) {
                mBinding.vSenicDetailRouters.visibility = View.VISIBLE
                mBinding.vSenicDetailRouters.setData(it.productListVO as MutableList<ProductBean>)
                mBinding.vSenicDetailRouters?.onRouterViewListener =
                    object : RouteOrderView.OnRouterViewListener {
                        override fun onGetRouterViewListener(snCode: String, name: String) {
                            showLoadingDialog()
                            mModel.getRouterReservationInfo(snCode, name)
                        }

                    }
            } else {
                mBinding.vSenicDetailRouters.visibility = View.GONE
            }
        })
        // 评论
        mModel.commentBeans.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.pcvScenicDetailComments.visibility = View.VISIBLE
                mBinding.pcvScenicDetailComments.setData(it)
            } else {
                mBinding.pcvScenicDetailComments.visibility = View.GONE
            }

        })

        mModel.goldStory.observe(this, Observer {

        })
        // 乡村详情
        mModel.scenicDetail.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.bean = it
            bindScenicData(it)
            //  获取景点信息
            mModel.getScenicSpots(id)
        })
        // 品牌信息
        mModel.scenicBrandListLiveData.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.vScenicDetailBrand.visibility = View.VISIBLE
                if (it.size > 0) {
                    var brandBean = it[0]
                    if (brandBean != null) {
                        mBinding.txtScenicDetailBrandName.text = "${brandBean.name}"
                        mBinding.txtScenicDetailBrandDesc.text = "${brandBean.slogan}"
                        var spanScenicTags = StringBuilder("")
                        if (!brandBean.siteCount.isNullOrEmpty() && brandBean.siteCount != "0") {
                            spanScenicTags.append("${brandBean.siteCount}个目的地城市")
                        }
                        if (brandBean.relationResourceCount != 0) {
                            spanScenicTags.append(" ${brandBean.relationResourceCount}个乡村玩乐")
                        }
                        mBinding.txtScenicDetailBrandInfo.text = spanScenicTags.toString()
                        Glide.with(BaseApplication.context!!).load(brandBean.brandImage)
                            .placeholder(R.mipmap.placeholder_img_fail_240_180)
                            .centerCrop()
                            .into(mBinding.imgScenicDetailBrandBg)
                        if (!brandBean.mainColor.isNullOrEmpty()) {
                            try {
                                // 取主色，然后设置颜色渐变
                                var colors = brandBean.mainColor.split(",")
                                var color =
                                    Color.argb(
                                        255,
                                        colors[0].toInt(),
                                        colors[1].toInt(),
                                        colors[2].toInt()
                                    )
                                var color2 =
                                    Color.argb(
                                        230,
                                        colors[0].toInt(),
                                        colors[1].toInt(),
                                        colors[2].toInt()
                                    )
                                var colorints = intArrayOf(
                                    color,
                                    color2,
                                    resources.getColor(R.color.color_ff_white)
                                )
                                brandGradientDrawable = GradientDrawable(
                                    GradientDrawable.Orientation.LEFT_RIGHT,
                                    colorints
                                )
                                brandGradientDrawable!!.shape = GradientDrawable.RECTANGLE
                                brandGradientDrawable!!.gradientType =
                                    GradientDrawable.LINEAR_GRADIENT
                                brandGradientDrawable!!.colors = colorints
                                mBinding.imgScenicDetailBrandBgG.background = brandGradientDrawable
                            } catch (e: Exception) {

                            }

                        }
                        mBinding?.vScenicDetailBrand.onNoDoubleClick {
                            if (brandBean!!.id != null) {
                                ARouter.getInstance().build(MainARouterPath.MAIN_BRANCH_DETAIL)
                                    .withString("id", brandBean!!.id)
                                    .navigation()
                            }
                        }
                    }
                }
            } else {
                mBinding.vScenicDetailBrand.visibility = View.GONE
            }
        })
        // 周边推荐
        mModel.mapResLiveData.observe(this, Observer {
            if (it != null) {
                mBinding.prvScenicDetail.setData(it.type, it.datas)
            }
        })
    }

    private fun setThumbUi(it: Boolean) {
        if (it) {
            // 点赞成功
            var drawable =
                resources.getDrawable(R.mipmap.bottom_icon_like_selected)
            mBinding.tvScenicDetailThumb.setCompoundDrawablesWithIntrinsicBounds(
                null,
                drawable,
                null,
                null
            )
        } else {
            // 取消点赞
            var drawable =
                resources.getDrawable(R.mipmap.bottom_icon_like_normal)
            mBinding.tvScenicDetailThumb.setCompoundDrawablesWithIntrinsicBounds(
                null,
                drawable,
                null,
                null
            )
        }
    }

    private fun setCollectUi(it: Boolean) {
        if (it) {
            // 收藏
            var collect =
                resources.getDrawable(R.mipmap.bottom_icon_collect_selected)
            mBinding.tvScenicDetailCollect.setCompoundDrawablesWithIntrinsicBounds(
                null,
                collect,
                null,
                null
            )
        } else {
            // 取消收藏
            var collect =
                resources.getDrawable(R.mipmap.bottom_icon_collect_normal)
            mBinding.tvScenicDetailCollect.setCompoundDrawablesWithIntrinsicBounds(
                null,
                collect,
                null,
                null
            )
        }
    }


    /**
     * 直播适配器
     */
    val liveAdapter = object : RecyclerViewAdapter<ItemPanorBinding,
            ScenicSpotsPanor>(R.layout.item_panor) {
        override fun setVariable(
            mBinding: ItemPanorBinding,
            position: Int,
            item: ScenicSpotsPanor
        ) {
            mBinding.name = item.name

        }

    }

    private fun bindScenicData(it: CountryDetailBean) {
        if (it == null) {
            mBinding.scrollScenicDetail.visibility = View.GONE
            ToastUtils.showMessage("未找到乡村信息，请稍后再试~")
            finish()
            return
        } else {
            StatisticsRepository.instance.statisticsPage(it.name, 1)
            mCountryDetailBean = it
            // 加载乡村顶部数据
            countryDetailTopFragment = CountryDetailTopFragment.newInstance(it)
            transactFragment(R.id.fr_scenic_detail_top, countryDetailTopFragment!!)
            mBinding.scrollScenicDetail.visibility = View.VISIBLE
//            if (!it.resourceCode.isNullOrEmpty() && !it.sysCode.isNullOrEmpty() && !it.shopUrl.isNullOrEmpty()) {
//                mModel.resourceCode = it.resourceCode
//                mModel.sysCode = it.sysCode
//                mModel.getSiteInfo()
//            }
        }
        if (!it.name.isNullOrEmpty()) {
            setTitleContent("" + it.name)
        }


        // 听解说
        var audios: MutableList<AudioInfo> = mutableListOf()

        if (!it.audioInfo.isNullOrEmpty()) {
            audios.addAll(it.audioInfo)
        }
        if (!audios.isNullOrEmpty()) {
            mBinding.vScenicDetailAudios.visibility = View.VISIBLE
            mBinding.vScenicDetailAudios.setData(audios)
        } else {
            mBinding.vScenicDetailAudios.visibility = View.GONE
        }

        // 活动
        mModel.hideActivity.set(true)
        // 评论数目
        if (it.commentNum>0) {
            mBinding.pcvScenicDetailComments.updateCommentNum(
                it.commentNum.toInt(),
                id,
                ResourceType.CONTENT_TYPE_COUNTRY,
                mCountryDetailBean!!.name
            )
            if (it.commentNum.toInt() > 0) {
                mBinding.tvScenicDetailCommentNum.text = "${it.commentNum}"
            }
        }
        if (it.vipResourceStatus != null) {
            // 收藏
            setCollectUi(it.vipResourceStatus.collectionStatus)
            // 点赞
            setThumbUi(it.vipResourceStatus.likeStatus)
        }
        if (it.collectionNum>0) {
            mBinding.tvScenicDetailCollect.text = "${it.collectionNum}"
        }
        if (it.likeNum>0) {
            mBinding.tvScenicDetailThumb.text = "${it.likeNum}"
        }

        if (it.latitude>0 && it.longitude>0) {
            mModel.scenicLat = it.latitude.toDouble()
            mModel.scenicLng = it.longitude.toDouble()
            mBinding.prvScenicDetail.visibility = View.VISIBLE
            mBinding.prvScenicDetail?.setLocation(
                LatLng(
                    it.latitude.toDouble(),
                    it.longitude.toDouble()
                )
            )
            mModel.gethMapRecList(ResourceType.CONTENT_TYPE_HOTEL, id, it.longitude.toString(), it.latitude.toString())
        } else {
            mBinding.prvScenicDetail.visibility = View.GONE
        }
        // 设置景点数据
        mBinding.vScenicSpots?.setTour(null)

    }


    override fun onPause() {
        super.onPause()
        EventBus.getDefault().post(UpdateScenicVideoPlayerEvent(1))
        mBinding.vScenicDetailAudios.pauseAudioPlayer()
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            StatisticsRepository.instance.statisticsPage(mCountryDetailBean?.name, 2)
            EventBus.getDefault().post(UpdateScenicVideoPlayerEvent(2))
            EventBus.getDefault().unregister(this)
            mBinding.vScenicDetailAudios?.stopAudioPlayer()
            mBinding.vScenicDetailAudios?.releaseAudioPlayer()
            mBinding.prvScenicDetail?.clear()
            GaoDeLocation.getInstance().release()

            orderAddressPopWindow = null
            brandGradientDrawable = null
        } catch (e: java.lang.Exception) {

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdateAudioPlayerState(event: UpdateAudioPlayerEvent) {
        // 视频启动播放
        try {
            countryDetailTopFragment?.isContinue = !(event.type == 1 || event.type == 0)
            mBinding?.vScenicDetailAudios?.updatePauseUi()
        } catch (e: java.lang.Exception) {

        }

    }

    private fun doLocation(type: String) {

        GaoDeLocation.getInstance().init(this, object : GaoDeLocation.OnGetCurrentLocationLisner {

            override fun onResult(
                adCode: String, result: String, lat_: Double,
                lon_: Double, adcode: String
            ) {
                mModel.lat = lat_.toString()
                mModel.lng = lon_.toString()
//                mBinding.prvScenicDetail?.setLocation(LatLng(lat_, lon_))
//                mModel.gethMapRecList(type, id)
                // 获取农家乐列表
                mModel.getAgritainmentList()
                // 获取名宿列表
//                mModel.getApiHotelList()
            }

            override fun onError(errormsg: String) {
                // 获取农家乐列表
                mModel.getAgritainmentList()
                // 获取名宿列表
//                mModel.getApiHotelList()
            }
        })
    }

    override fun onBackPressed() {
        try {
            // 处理视频播发器全屏问题
            if (countryDetailTopFragment != null) {
                if (!countryDetailTopFragment!!.isHaveVideo || countryDetailTopFragment!!.scenicVideoFrag == null || !countryDetailTopFragment!!.scenicVideoFrag!!.onBackPress()) {
                    super.onBackPressed()
                }
            }
        } catch (e: java.lang.Exception) {

        }

    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun updateDataStatus(event: LoginStatusEvent) {
//        if (event != null) {
//            mModel?.getActivityDetail(id, false)
//        }
        mModel?.refreshCountryDetail(id)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRecordEvent(event: UpdateCommentEvent) {
        mModel.getCountryInfo(id, true)
        // 评论
        mModel.getActivityCommentList(id)
    }
}