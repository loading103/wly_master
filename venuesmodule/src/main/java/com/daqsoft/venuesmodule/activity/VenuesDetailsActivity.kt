package com.daqsoft.venuesmodule.activity

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Point
import android.graphics.Rect
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseApplication.Companion.baseUrl
import com.daqsoft.baselib.base.BaseApplication.Companion.context
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.dp
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.SystemHelper
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.utils.file.DownLoadFileUtil
import com.daqsoft.baselib.utils.file.DownLoadFileUtil.DownImageListener
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.baselib.widgets.dialog.QrCodeDialog
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.bean.ActivityRoomBean
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.bean.ValueIdKeyBean
import com.daqsoft.provider.businessview.event.UpdateAudioStateEvent
import com.daqsoft.provider.businessview.event.UpdateCommentEvent
import com.daqsoft.provider.businessview.fragment.AppointmentFragment
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.businessview.view.ListenerAudioView
import com.daqsoft.provider.businessview.view.OrderAddressPopWindow
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.net.StatisticsRepository
import com.daqsoft.provider.network.venues.bean.LabelBean
import com.daqsoft.provider.network.venues.bean.VenuesDetailsBean
import com.daqsoft.provider.scrollview.DqScrollView
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.provider.utils.MapNaviUtils
import com.daqsoft.provider.utils.Utils
import com.daqsoft.provider.view.convenientbanner.bean.VideoImageBean
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.convenientbanner.holder.VideoImageHolder
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import com.daqsoft.provider.view.dialog.ProviderTipDialog
import com.daqsoft.provider.view.popupwindow.SharePopWindow
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.activity.event.UpdateAudioPlayerStateEvent
import com.daqsoft.venuesmodule.activity.event.UpdatePlayUiStateEvent
import com.daqsoft.venuesmodule.activity.event.UpdatePlayerStateEvent
import com.daqsoft.venuesmodule.activity.widgets.VenueCommentaryView
import com.daqsoft.venuesmodule.activity.widgets.VenueRecommendView
import com.daqsoft.venuesmodule.adapter.*
import com.daqsoft.venuesmodule.databinding.ActivityVenuesDetailsNewBinding
import com.daqsoft.venuesmodule.fragment.PerformanceReservationFragment
import com.daqsoft.venuesmodule.fragment.VenueImageFragment
import com.daqsoft.venuesmodule.fragment.VenueVideoFragment
import com.daqsoft.venuesmodule.model.QuickNavigationItem
import com.daqsoft.venuesmodule.model.VenuesDetailsViewModel
import com.jakewharton.rxbinding2.view.RxView
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer
import me.nereo.multi_image_selector.BigImgActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit


/**
 * 文化场馆详情页Activity
 * @author luoyi
 * @date 2020/03/25 9:10
 * @version 1.1.0
 * @since JDK 1.8
 */
@Route(path = ARouterPath.VenuesModule.VENUES_DETAILS_ACTIVITY)
class VenuesDetailsActivity :
    TitleBarActivity<ActivityVenuesDetailsNewBinding, VenuesDetailsViewModel>(),
    VenueImageFragment.OnPageChangedListener {


    companion object {
        const val MUSEUM = "博物馆"
        const val CULTURAL_HALL = "文化馆"
        const val THEATER = "剧院/剧团"
        const val ART_GALLERY = "美术馆"
        val fixedTypeCollection = arrayListOf(MUSEUM, CULTURAL_HALL, ART_GALLERY, THEATER)
    }

    /**跳转来源*/
    @JvmField
    @Autowired(name = "source")
    var source: String = ""

    /**是否显示底部加入行程按钮*/
    @JvmField
    @Autowired(name = "isShowButton")
    var isShowButton: Boolean = false

    /**
     * 文化场馆的资源ID
     */
    @JvmField
    @Autowired
    var id: String = ""


    /**
     * 场馆类型
     */
    @JvmField
    @Autowired
    var type: String? = ""

    /**
     * 场馆标签的数据
     */
    var labelList = mutableListOf<LabelBean>()

    /**
     * 初始化标签label的适配器
     */
    var labelAdapter: LabelAdapter? = null

    /**
     * 滑动悬浮顶部内容适配器
     */
    var mVenueStickTopAdapter: VenueDetailTopStickAdapter? = null

    /**
     * 滑动顶部数据适配器
     */
    var mDatasStickTops: MutableList<ValueIdKeyBean> = mutableListOf()

    var mVenuesBean: VenuesDetailsBean? = null

    var isHaveVide: Boolean = false
    var isHave720: Boolean = false

    var imageSize: Int = 0

    /**
     * 门票预订
     */
    var orderAddressPopWindow: OrderAddressPopWindow? = null


    /**
     * 其他栏目adapter
     */
    private val otherColumnsAdapter by lazy { OtherColumnsAdapter(context) }

    /**
     * 是否添加过演出预定Fragment
     */
    private var performanceReservationFlag = false

    /**
     * 分享弹窗
     */
    var sharePopWindow: SharePopWindow? = null

    var mQrCodeDialog: QrCodeDialog? = null
    override fun getLayout() = R.layout.activity_venues_details_new
    override fun setTitle() = "场馆详情"
    override fun injectVm(): Class<VenuesDetailsViewModel> = VenuesDetailsViewModel::class.java
    var childHeight: Int = 0

    override fun initView() {

        if (!source.isNullOrEmpty() && !isShowButton) {
            //隐藏评论等布局
            mBinding.vMainActivityBottom.visibility = View.GONE
            //显示加入行程按钮
            mBinding.itineraryLayout.visibility = View.VISIBLE
            mBinding.itineraryLayout.getChildAt(0).let {
                it.visibility = View.VISIBLE
                it.setOnClickListener {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }

        EventBus.getDefault().register(this)
        childHeight = resources.getDimensionPixelSize(R.dimen.dp_122).toInt()
        mBinding.model = mModel
        initRecyclerViewAdapter()


        initViewModel()
        initViewEvent()
        initQuickNavigation()
    }
    override fun initCustomTitleBar(mTitleBar: TitleBar) {
        showShareButton(com.daqsoft.provider.R.mipmap.common_nav_button_share)
        setShareClick(View.OnClickListener {
            mVenuesBean?.let {
                if (sharePopWindow == null) {
                    sharePopWindow = SharePopWindow(this)
                }
                var content= Constant.SHARE_DEC
                if(!TextUtils.isEmpty(it.summary)){
                    content=it.summary
                }
                // 设置分享数据
                sharePopWindow?.setShareContent(
                    it.name, content, it.images.getRealImages(),
                    ShareModel.getVenueDetailUrl(id)
                )
                if (!sharePopWindow!!.isShowing) {
                    sharePopWindow?.showAsDropDown(mTitleBar)
                }
            }
        })
    }

    val quickNavigationItem = arrayListOf<QuickNavigationItem>()
    val quickNavigationAdapter by lazy { QuickNavigationAdapter() }
    private fun initQuickNavigation() {

        if (BaseApplication.appArea == "sc") {
            // 2020/9/23 修改 针对下列类型定制 UI ps:后台类型为字符
            when (type) {
                MUSEUM -> {
                    // 活动
                    quickNavigationItem.add(
                        QuickNavigationItem(
                            resources.getString(R.string.venues_activity),
                            R.mipmap.venue_details_anchor_icon_activity,
                            mBinding.vavVenueAcitvity.id
                        )
                    )
                    // 资讯
                    quickNavigationItem.add(
                        QuickNavigationItem(
                            resources.getString(R.string.venue_news),
                            R.mipmap.venue_details_anchor_icon_news,
                            mBinding.prvConentLs.id
                        )
                    )
                    // 预约
                    quickNavigationItem.add(
                        QuickNavigationItem(
                            resources.getString(R.string.venue_reserve),
                            R.mipmap.venue_details_anchor_icon_book,
                            mBinding.flVenueReservation.id
                        )
                    )
                    // 主题展馆
                    quickNavigationItem.add(
                        QuickNavigationItem(
                            resources.getString(R.string.venue_theme_exhibition_hall),
                            R.mipmap.venue_details_anchor_icon_theme,
                            mBinding.varvVenueActivityRoom.id
                        )
                    )
                }
                CULTURAL_HALL -> {
                    // 活动
                    quickNavigationItem.add(
                        QuickNavigationItem(
                            resources.getString(R.string.venues_activity),
                            R.mipmap.venue_details_anchor_icon_activity,
                            mBinding.vavVenueAcitvity.id
                        )
                    )
                    // 资讯
                    quickNavigationItem.add(
                        QuickNavigationItem(
                            resources.getString(R.string.venue_news),
                            R.mipmap.venue_details_anchor_icon_news,
                            mBinding.prvConentLs.id
                        )
                    )
                    // 预约
                    quickNavigationItem.add(
                        QuickNavigationItem(
                            resources.getString(R.string.venue_reserve),
                            R.mipmap.venue_details_anchor_icon_book,
                            mBinding.flVenueReservation.id
                        )
                    )
                    // 艺术资源
                    quickNavigationItem.add(
                        QuickNavigationItem(
                            resources.getString(R.string.venue_art_resources),
                            R.mipmap.venue_details_anchor_icon_column,
                            mBinding.rvVenueOtherColumns.id
                        )
                    )
                    // 社团
                    quickNavigationItem.add(
                        QuickNavigationItem(
                            resources.getString(R.string.venue_club),
                            R.mipmap.venue_details_anchor_icon_community,
                            mBinding.flVenueSocieties.id
                        )
                    )
                }
                ART_GALLERY -> {
                    // 活动
                    quickNavigationItem.add(
                        QuickNavigationItem(
                            resources.getString(R.string.venues_activity),
                            R.mipmap.venue_details_anchor_icon_activity,
                            mBinding.vavVenueAcitvity.id
                        )
                    )
                    // 资讯
                    quickNavigationItem.add(
                        QuickNavigationItem(
                            resources.getString(R.string.venue_news),
                            R.mipmap.venue_details_anchor_icon_news,
                            mBinding.prvConentLs.id
                        )
                    )
                    // 预约
                    quickNavigationItem.add(
                        QuickNavigationItem(
                            resources.getString(R.string.venue_reserve),
                            R.mipmap.venue_details_anchor_icon_book,
                            mBinding.flVenueReservation.id
                        )
                    )
                    // 线上展览
                    quickNavigationItem.add(
                        QuickNavigationItem(
                            resources.getString(R.string.venue_online_exhibition),
                            R.mipmap.venue_details_anchor_icon_column,
                            mBinding.rvVenueOtherColumns.id
                        )
                    )
                }
                THEATER -> {
                    // 活动
                    quickNavigationItem.add(
                        QuickNavigationItem(
                            resources.getString(R.string.venues_activity),
                            R.mipmap.venue_details_anchor_icon_activity,
                            mBinding.vavVenueAcitvity.id
                        )
                    )
                    // 资讯
                    quickNavigationItem.add(
                        QuickNavigationItem(
                            resources.getString(R.string.venue_news),
                            R.mipmap.venue_details_anchor_icon_news,
                            mBinding.prvConentLs.id
                        )
                    )
                    // 预约
                    quickNavigationItem.add(
                        QuickNavigationItem(
                            resources.getString(R.string.venue_reserve),
                            R.mipmap.venue_details_anchor_icon_book,
                            mBinding.flVenueReservation.id
                        )
                    )
                    // 经典赏析
                    quickNavigationItem.add(
                        QuickNavigationItem(
                            resources.getString(R.string.venue_classic_appreciation),
                            R.mipmap.venue_details_anchor_icon_column,
                            mBinding.rvVenueOtherColumns.id
                        )
                    )
                }
            }
        }

        mVenueStickTopAdapter?.add(quickNavigationItem)

        quickNavigationAdapter.emptyViewShow = false
        quickNavigationAdapter.add(quickNavigationItem)
        quickNavigationAdapter.checkIsEmpty = object : QuickNavigationAdapter.CheckIsEmpty {
            override fun isEmpty(empty: Boolean) {
                mBinding.llQuickNavigation.visibility = if (empty) View.GONE else View.VISIBLE
            }
        }

        quickNavigationAdapter.onItemClickListener =
            object : QuickNavigationAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, item: QuickNavigationItem) {
                    mBinding.scrollVenues.post {
                        if (findViewById<View>(item.id).isGone) {
                            ToastUtils.showMessage("暂无数据")
                        } else {
                            mBinding.scrollVenues.smoothScrollTo(
                                0,
                                findViewById<View>(item.id).top + childHeight
                            )
                        }
                    }
                }
            }
        quickNavigationAdapter.onItemClickListener = object : QuickNavigationAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, item: QuickNavigationItem) {
                mBinding.scrollVenues.post {
                    if (findViewById<View>(item.id).isGone) {
                        ToastUtils.showMessage("暂无数据")
                    } else {
                        mBinding.scrollVenues.smoothScrollTo(
                            0,
                            findViewById<View>(item.id).top + childHeight + 100
                        )
                    }
                }
            }
        }
        mBinding.llQuickNavigation.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.HORIZONTAL,
            false
        )
        mBinding.llQuickNavigation.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val itemCount = state.itemCount - 1

                val position = parent.getChildAdapterPosition(view)
                outRect.left = 8.dp
                if (position == 0) {
                    outRect.left = 20.dp
                }
                if (position == itemCount) {
                    outRect.right = 20.dp
                }

            }
        })
        mBinding.llQuickNavigation.adapter = quickNavigationAdapter
    }

    private fun initViewEvent() {
        mBinding.txtVenueDetailVideo.onNoDoubleClick {
            mBinding.cbrVenueDetail.setCurrentItem(0, true)
        }

        mBinding.vcvCommentarySpeaking.onPlayerListener = object :
            VenueCommentaryView.OnAudioPlayerListener {
            override fun onStartPlayer() {
                mBinding.cbrVenueDetail.pauseVideoPlayer()
            }
        }

        mBinding.txtVenueDetailPannaor.onNoDoubleClick {
            if (isHaveVide) {
                mBinding.cbrVenueDetail.setCurrentItem(1, true)
            } else {
                mBinding.cbrVenueDetail.setCurrentItem(0, true)
            }
        }
        mBinding.txtVenueDetailImages.onNoDoubleClick {
            var pos = 0
            if (isHaveVide) {
                pos += 1
            }
            if (isHave720) {
                pos += 1
            }
            try {
                mBinding.cbrVenueDetail.setCurrentItem(pos, true)
            } catch (e: Exception) {
            }

        }
        mBinding.tvVenuesDetailsPhone.onNoDoubleClick {
            mVenuesBean?.let {
                SystemHelper.callPhone(this, it.phone)
            }
        }
        mBinding.vVenuesVideo.onNoDoubleClick {
            //

        }
        mBinding.tvShare.onNoDoubleClick {
            ToastUtils.showMessage("功能正在开发中~")
        }
        RxView.clicks(mBinding.tvVenuesDetailsAddressValue)
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                dealMapNav()
            }

        mBinding.llVenuesDetailsBus.onNoDoubleClick {
            // 公交查询
            ARouter.getInstance().build(ARouterPath.ServiceModule.SERVICE_QUERY_BUS_ACTIVITY)
                .navigation()
        }
        mBinding.llVenuesDetailsComplaint.onNoDoubleClick {
            // 旅游投诉
            ARouter.getInstance().build(MainARouterPath.MAIN_COMPLAINT_ACTIVITY)
                .navigation()
        }
        mBinding.tvWexQrcode.onNoDoubleClick {
            dealShowQrCode()
        }
        mBinding.tvVenuesWebsiteValue.onNoDoubleClick {
            val url = dealWebSite()
            if (url.isBlank()) {
                ToastUtils.showMessage("官网地址不正确")
                return@onNoDoubleClick
            }

            ARouter.getInstance()
                .build(ARouterPath.Provider.WEB_ACTIVITY)
                .withString("mTitle", mVenuesBean?.name)
                .withString("html", url)
                .navigation()
        }
        mBinding.vncVenueRecommend.onItemClickTabListener =
            object : VenueRecommendView.OnItemClickTabListener {
                override fun getMapResourceRecommend(type: String) {
                    if (!mModel.lat.isNullOrEmpty() && !mModel.lng.isNullOrEmpty()) {
//                    doLocation(type)
                        mModel.gethMapRecList(type, id)
                    } else {
                        mModel.gethMapRecList(type, id)
                    }

                }

            }
        mVenueStickTopAdapter?.onItemClickListener =
            object : VenueDetailTopStickAdapter.OnItemClickListener {
                override fun onItemClick(id: Int) {
                    mBinding.scrollVenues.post(object : Runnable {
                        override fun run() {
                            if (findViewById<View>(id).isGone) {
                                ToastUtils.showMessage("暂无数据")
                            } else {
                                mBinding.scrollVenues.smoothScrollTo(
                                    0,
                                    findViewById<View>(id).top + childHeight + 100
                                )
                            }
                        }
                    })

                }
            }
        mBinding.tvThumb.onNoDoubleClick {
            if (mVenuesBean != null && mVenuesBean!!.vipResourceStatus != null) {
                if (mVenuesBean!!.vipResourceStatus.likeStatus) {
                    showLoadingDialog()
                    mModel.thumbCancell(id)
                } else {
                    showLoadingDialog()
                    mModel.thumbUp(id)
                }
            }
        }

        mBinding.tvCollect.onNoDoubleClick {
            if (mVenuesBean != null && mVenuesBean!!.vipResourceStatus != null) {
                if (mVenuesBean!!.vipResourceStatus.collectionStatus) {
                    showLoadingDialog()
                    mModel.collectionCancel(id)
                } else {
                    showLoadingDialog()
                    mModel.collection(id)
                }
            }
        }
        mBinding.scrollVenues.setOnScrollListener(object : DqScrollView.OnScrollListener {
            override fun onScrollY(scrollY: Int) {
//                val data = mVenueStickTopAdapter?.getData()
//                if (data.isNullOrEmpty()) {
//                    return
//                }
//                for (i in data.indices) {
//                    val top = childHeight + findViewById<View>(data[i].id).top
//                    val bottom = childHeight + findViewById<View>(data[i].id).bottom
//                    if (i == 0 && scrollY < top) {
//                        mVenueStickTopAdapter?.updateSelectPos(i)
//                        mBinding.recyTopScrollStick.visibility = View.GONE
//                        break
//                    } else if (scrollY in top..bottom) {
//                        mBinding.recyTopScrollStick.visibility = View.GONE
//                        mVenueStickTopAdapter?.updateSelectPos(data[i].id)
//                        break
//                    }
//                }

                if (!checkVisibleOnScreen(mBinding.llQuickNavigation)) {
                    if (mVenueStickTopAdapter!!.getData().isEmpty()){
                        mBinding.recyTopScrollStick.visibility = View.GONE
                    }else{
                        mBinding.recyTopScrollStick.visibility = View.VISIBLE
                    }
                    quickNavigationItem.forEachIndexed { index, item ->
                        if (findViewById<View>(item.id).isGone) {
                            return@forEachIndexed
                        }
                        val top = childHeight + findViewById<View>(item.id).top
                        val bottom = childHeight + findViewById<View>(item.id).bottom
                        if (index == 0 && scrollY < top) {
                            mVenueStickTopAdapter?.updateSelectPos(item.id)
                            return@forEachIndexed
                        } else if (scrollY in top..bottom) {
                            mVenueStickTopAdapter?.updateSelectPos(item.id)
                            return@forEachIndexed
                        }
                    }
                } else {
                    mBinding.recyTopScrollStick.visibility = View.GONE
                }
            }

        })
        mBinding.tvCommentNum.onNoDoubleClick {
            // 2020/11/5 修改
            ARouter.getInstance().build(ARouterPath.Provider.PROVIDER_ORDER_COM_ACTIVITY)
                .withString("id", id)
                .withString("type", ResourceType.CONTENT_TYPE_VENUE)
                .withString("contentTitle", mVenuesBean?.name)
                .withString("orderId", "")
                .navigation()


        }
        mBinding.tvVenuesOnlineReadOperation.onNoDoubleClick {
            if (!mVenuesBean?.onlineReadingImage.isNullOrEmpty()) {
                try {
                    SystemHelper.copyContentToClip(mVenuesBean?.onlineReadingOfficialName!!)
                    ToastUtils.showMessage("复制成功，微信搜索该名称，在线阅读~")
                } catch (e: java.lang.Exception) {
                    ToastUtils.showMessage("复制失败~")
                }
            }
        }
        mBinding.txtVenueReservation.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                ARouter.getInstance().build(ARouterPath.VenuesModule.VENUES_RESERVATION_V1_ACTIVITY)
                    .withString("venueId", id)
                    .navigation()
            } else {
                ToastUtils.showUnLoginMsg()
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
    }

    /**
     *  处理官网地址
     */
    private fun dealWebSite(): String {

        mVenuesBean?.websiteUrl?.let {
            if (!it.contains("https://") && !it.contains("http://")) {
                return "http://$it"
            }
        }
        return mVenuesBean?.websiteUrl ?: ""

//        if (!mVenuesBean!!.websiteUrl!!.contains("https://") && !mVenuesBean!!.websiteUrl!!.contains(
//                "http://"
//            )
//        ) {
//            return "http://" + mVenuesBean!!.websiteUrl
//        }
//        return mVenuesBean!!.websiteUrl
    }


    private fun initViewModel() {
        mVenueStickTopAdapter = VenueDetailTopStickAdapter()
        mVenueStickTopAdapter!!.emptyViewShow = false
        mBinding.recyTopScrollStick.layoutManager =
            LinearLayoutManager(context!!, LinearLayoutManager.HORIZONTAL, false)
        mBinding.recyTopScrollStick.adapter = mVenueStickTopAdapter
        // 添加介绍楼层
        mModel.updateStickTopItem("介绍", R.id.cl_venues_details_info)
        mModel.venuesDetailsBean.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.scrollVenues.visibility = View.VISIBLE

            title = it.name

            mVenuesBean = it
            if (it == null) {
                ToastUtils.showMessage("未找到场馆信息信息，请稍后再试~")
                finish()
                return@Observer
            } else {
                StatisticsRepository.instance.statisticsPage(it.name, 1)
            }
            // 场馆标签的数据
            labelList.clear()

            // 支付方式
//            if (!it.payRule.isNullOrEmpty()) {
//                labelList.add(LabelBean(it.payRule, 0))
//            }

            // 场馆类型
            if (!it.type.isNullOrEmpty()) {
                labelList.add(LabelBean(it.type, 1))
            }
            if (!it.venueLevel.isNullOrEmpty()) {
                labelList.add(LabelBean(it.venueLevel, 1))
            }

            var dataVideoImages: MutableList<VideoImageBean> = mutableListOf()
            //视频
            if (!it.video.isNullOrEmpty()) {
                dataVideoImages.add(0, VideoImageBean().apply {
                    type = 1
                    videoUrl = it.video
                })
                isHaveVide = true
                mBinding.txtVenueDetailVideo.visibility = View.VISIBLE
            }
            var images = it.images.split(",")
            //720
            if (!it.panoramaUrl.isNullOrEmpty()) {
                dataVideoImages.add(VideoImageBean().apply {
                    type = 2
                    videoUrl = it.panoramaUrl
                    imageUrl = it.panoramaCover
                    name = it.name
                })
                isHave720 = true
                mBinding.txtVenueDetailPannaor.visibility = View.VISIBLE
            }
            //图片
            if (!images.isNullOrEmpty()) {
                for (item in images) {
                    dataVideoImages.add(VideoImageBean().apply {
                        type = 0
                        imageUrl = item
                    })
                }
                mBinding.txtVenueDetailImages.visibility = View.VISIBLE
                mBinding.txtVenueDetailImages.text = "画册1/${images.size}"
            }
            if (!dataVideoImages.isNullOrEmpty()) {
                mBinding.cbrVenueDetail.visibility = View.VISIBLE
                mBinding.cbrVenueDetail.setPages(object : CBViewHolderCreator {
                    override fun createHolder(itemView: View?): Holder<*> {
                        return VideoImageHolder(itemView!!, this@VenuesDetailsActivity)
                    }

                    override fun getLayoutId(): Int {
                        return R.layout.layout_video_image
                    }
                }, dataVideoImages).setCanLoop(dataVideoImages.size > 1).setPointViewVisible(false)
                    .setOnItemClickListener {
                        when (dataVideoImages[it].type) {
                            0 -> {
                                // 图片点击
                                val intent =
                                    Intent(this@VenuesDetailsActivity, BigImgActivity::class.java)
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
                    }.startTurning(3000)
                mBinding.cbrVenueDetail.onPageChangeListener = object : OnPageChangeListener {
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
                            if (pos >= 0) {
                                mBinding.txtVenueDetailImages.text = "画册${pos + 1}/${images?.size}"
                            }
                        }
                    }

                    override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                    }

                }
            } else {
                mBinding.cbrVenueDetail.visibility = View.GONE
            }

            // 场馆标签
            if (!it.tagName.isNullOrEmpty()) {
                for (tag in it.tagName) {
                    labelList.add(LabelBean(tag, 2))
                }
            }
            if (!labelList.isNullOrEmpty() && labelList.size > 1) {
                labelAdapter!!.notifyDataSetChanged()
            }
            if (!it.trafficInfo.isNullOrEmpty()) {
                mBinding.webVenuesDetailsTraffic.loadContent(it.trafficInfo)
                mBinding.webVenuesDetailsTraffic.visibility = View.VISIBLE
            } else {
                mBinding.webVenuesDetailsTraffic.visibility = View.GONE
            }

            // 活动
            if (!it.activity.isNullOrEmpty()) {
                try {
                    if (BaseApplication.appArea != "sc") {
                        // 活动
                        quickNavigationItem.add(
                            QuickNavigationItem(
                                resources.getString(R.string.venues_activity),
                                R.mipmap.venue_details_anchor_icon_activity,
                                mBinding.vavVenueAcitvity.id
                            )
                        )
                        quickNavigationAdapter.setNewData(quickNavigationItem)
                        updateQuickNavigation()

                        mVenueStickTopAdapter?.setNewData(quickNavigationItem)
                        mVenueStickTopAdapter?.notifyDataSetChanged()
                    } else {
                        if (!fixedTypeCollection.contains(type)) {
                            // 活动
                            quickNavigationItem.add(
                                QuickNavigationItem(
                                    resources.getString(R.string.venues_activity),
                                    R.mipmap.venue_details_anchor_icon_activity,
                                    mBinding.vavVenueAcitvity.id
                                )
                            )
                            quickNavigationAdapter.setNewData(quickNavigationItem)
                            updateQuickNavigation()

                            mVenueStickTopAdapter?.setNewData(quickNavigationItem)
                            mVenueStickTopAdapter?.notifyDataSetChanged()
                        }
                    }
                    mModel.updateStickTopItem("活动", mBinding.vavVenueAcitvity.id)
                    mBinding.vavVenueAcitvity.setData(
                        it.activity as MutableList<ActivityBean>,
                        id,
                        ResourceType.CONTENT_TYPE_VENUE
                    )
                    mBinding.vavVenueAcitvity.visibility = View.VISIBLE
                } catch (e: Exception) {
                }
            } else {
                mBinding.vavVenueAcitvity.visibility = View.GONE
            }

            if (it.isOpen == 1) {
                if (it.orderAddressType == "pt") {
                    // 第三方预约
                    mBinding.txtVenueReservation.visibility = View.GONE
                    mModel.getOrderAddressInfo(id)
                    setReseration(it, it.orderAddressType)
                } else if (it.orderAddressType == "custom") {
                    //分时预约
                    setReseration(it, it.orderAddressType)
                }
            } else {
                mBinding.txtVenueReservation.visibility = View.GONE
                setReseration(it, "pt")
            }

            // 主题展馆
            if (!it.activityRoom.isNullOrEmpty()) {

                if (BaseApplication.appArea != "sc") {
                    //主题展馆
                    quickNavigationItem.add(
                        QuickNavigationItem(
                            resources.getString(R.string.venue_theme_exhibition_hall),
                            R.mipmap.venue_details_anchor_icon_theme,
                            mBinding.varvVenueActivityRoom.id
                        )
                    )
                    quickNavigationAdapter.setNewData(quickNavigationItem)
                    updateQuickNavigation()

                    mVenueStickTopAdapter?.setNewData(quickNavigationItem)
                    mVenueStickTopAdapter?.notifyDataSetChanged()
                } else {
                    if (!fixedTypeCollection.contains(type)) {
                        //主题展馆
                        quickNavigationItem.add(
                            QuickNavigationItem(
                                resources.getString(R.string.venue_theme_exhibition_hall),
                                R.mipmap.venue_details_anchor_icon_theme,
                                mBinding.varvVenueActivityRoom.id
                            )
                        )
                        quickNavigationAdapter.setNewData(quickNavigationItem)
                        quickNavigationAdapter.notifyDataSetChanged()

                        mVenueStickTopAdapter?.setNewData(quickNavigationItem)
                        mVenueStickTopAdapter?.notifyDataSetChanged()
                    }
                }


                mModel.updateStickTopItem("活动室", R.id.varv_venue_activity_room)
                mBinding.varvVenueActivityRoom.setData(it.activityRoom as MutableList<ActivityRoomBean>)
                mBinding.varvVenueActivityRoom.visibility = View.VISIBLE
            } else {
                mBinding.varvVenueActivityRoom.visibility = View.GONE
            }

            // 场馆介绍
            if (!it.introduce.isNullOrEmpty()) {
                mBinding.webVenuesDetailsContent.loadContent(it.introduce)
                mBinding.webVenuesDetailsContent.visibility = View.VISIBLE
            } else {
                mBinding.webVenuesDetailsContent.visibility = View.GONE
            }

            // 听解说
            if (!it.audioInfo.isNullOrEmpty()) {
                mModel.updateStickTopItem("解说", mBinding.vcvCommentarySpeaking.id)
                mBinding.vcvCommentarySpeaking.visibility = View.VISIBLE
                mBinding.vcvCommentarySpeaking.setData(it.audioInfo)
            } else {
                mBinding.vcvCommentarySpeaking.visibility = View.GONE
            }


            // 评论数目
            try {
                if (it.commentNum > 0) {
                    mBinding.vncVenueComents.updateCommentNum(
                        it.commentNum,
                        id,
                        ResourceType.CONTENT_TYPE_VENUE
                    )
                    mBinding.tvCommentNum.text = it.commentNum.toString()
                }
            } catch (e: Exception) {

            }
//            // 场馆信息
//            var venusInfo = DividerTextUtils.convertString(
//                StringBuilder(""), if (it.payRule.isNullOrEmpty()) {
//                    ""
//                } else {
//                    it.payRule
//                }, if (it.openWeek.isNullOrEmpty()) {
//                    ""
//                } else {
//                    "开放时间：" + Utils().OpenWeekUtils(it.openWeek)
//                }
//            )
//            if (!venusInfo.isNullOrEmpty()) {
//                mBinding.tvVenuesInfo.text = venusInfo
//                mBinding.tvVenuesInfo.visibility = View.VISIBLE
//            } else {
//                mBinding.tvVenuesInfo.visibility = View.GONE
//            }

            val openingHours = Utils().OpenWeekUtils(it.openWeek ?: "")
            if (openingHours.isBlank()) {
                mBinding.txtVenueOpenTimeValue.visibility = View.GONE
                mBinding.tvVenuesDetailsTimeTitle.visibility = View.GONE
            } else {
                mBinding.txtVenueOpenTimeValue.text = openingHours
            }

            // 周边推荐
            if (!it.latitude.isNullOrEmpty() && !it.longitude.isNullOrEmpty()) {
                // 推荐  是根据场馆的经纬度推荐~
                mBinding.vncVenueRecommend?.setLocation(
                    LatLng(
                        it.latitude.toDouble(),
                        it.longitude.toDouble()
                    )
                )
                mBinding.vncVenueRecommend.visibility = View.VISIBLE
                mModel.updateStickTopItem("推荐", R.id.vnc_venue_recommend)
                mModel.lat = it.latitude
                mModel.lng = it.longitude
                mModel.venuesLat = it.latitude.toDouble()
                mModel.venuesLon = it.longitude.toDouble()
                mModel.gethMapRecList(ResourceType.CONTENT_TYPE_SCENERY, id)
            } else {
                mBinding.vncVenueRecommend.visibility = View.GONE
            }
//            mVenueStickTopAdapter?.clear()
//            mVenueStickTopAdapter?.add(mModel.getStickTopItems())
            mBinding.likeNum = it.likeNum.toString()
            if (it.commentNum == 0) {
                mBinding.tvCommentNum.text = getString(R.string.venue_writer_comment)
            } else {
                mBinding.tvCommentNum.text = it.commentNum.toString()
            }
            mBinding.collectNum = it.collectionNum.toString()
            if (it.vipResourceStatus != null) {
                var drawable = if (it.vipResourceStatus.likeStatus) {
                    resources.getDrawable(R.mipmap.venue_bottom_icon_like_selected)
                } else {
                    resources.getDrawable(R.mipmap.venue_bottom_icon_like_normal)
                }
                var collect = if (it.vipResourceStatus.collectionStatus) {
                    resources.getDrawable(R.mipmap.venue_bottom_icon_collect_selected)
                } else {
                    resources.getDrawable(R.mipmap.venue_bottom_icon_collect_normal)
                }
                mBinding.tvThumb.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)

                mBinding.tvCollect.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    collect,
                    null,
                    null
                )
            }


        })
        mModel.mError.observe(this, Observer
        {
            dissMissLoadingDialog()
        })
        mModel.commentBeans.observe(this, Observer
        {
            // 评论
            if (!it.isNullOrEmpty()) {
                mModel.updateStickTopItem("点评", R.id.vnc_venue_coments)
//                mVenueStickTopAdapter?.getData()?.clear()
//                mVenueStickTopAdapter?.add(mModel.getStickTopItems())
                mBinding.vncVenueComents.setData(it)
                mBinding.vncVenueComents.visibility = View.VISIBLE
            } else {
                mBinding.vncVenueComents.visibility = View.GONE
            }
        })
        mModel.mapResLiveData.observe(this, Observer
        {
            if (it != null) {
                mBinding.vncVenueRecommend.setData(it.type, it.datas)
            }
        })
        mModel.storeisLiveData.observe(this, Observer
        {
            if (!it.isNullOrEmpty()) {
                mModel.updateStickTopItem("故事", R.id.vns_venue_stoies)
//                mVenueStickTopAdapter?.getData()?.clear()
//                mVenueStickTopAdapter?.add(mModel.getStickTopItems())
//                mBinding.vnsVenueStoies.setData(it)
                mBinding.vnsVenueStoies.setStoryList(it)
                mBinding.vnsVenueStoies.visibility = View.VISIBLE
            } else {
                mBinding.vnsVenueStoies.visibility = View.GONE
            }
        })
        mModel.totalStoryLiveData.observe(this, Observer {
            mBinding.vnsVenueStoies.setTotalStory(it)
        })

        mModel.canceThumbLiveData.observe(this, Observer
        {
            dissMissLoadingDialog()
            if (mVenuesBean!!.likeNum > 0) {
                mVenuesBean!!.likeNum = mVenuesBean!!.likeNum - 1
                mBinding.likeNum = mVenuesBean!!.likeNum.toString()
            } else {
                mBinding.likeNum = "点赞"
            }
            mVenuesBean!!.vipResourceStatus.likeStatus = false
            var drawable =
                resources.getDrawable(R.mipmap.venue_bottom_icon_like_normal)
            mBinding.tvThumb.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
        })
        mModel.thumbLiveData.observe(this, Observer
        {
            dissMissLoadingDialog()
            mVenuesBean!!.vipResourceStatus.likeStatus = true
            mVenuesBean!!.likeNum = mVenuesBean!!.likeNum + 1
            mBinding.likeNum = mVenuesBean!!.likeNum.toString()
            var drawable =
                resources.getDrawable(R.mipmap.venue_bottom_icon_like_selected)
            mBinding.tvThumb.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
        })
        mModel.canceCollectLiveData.observe(this, Observer
        {
            dissMissLoadingDialog()
            mVenuesBean!!.vipResourceStatus.collectionStatus = false
            if (mVenuesBean!!.collectionNum > 0) {
                mVenuesBean!!.collectionNum = mVenuesBean!!.collectionNum - 1
                mBinding.collectNum = mVenuesBean!!.collectionNum.toString()
            } else {
                mBinding.collectNum = "收藏"
            }
            var collect =
                resources.getDrawable(R.mipmap.venue_bottom_icon_collect_normal)
            mBinding.tvCollect.setCompoundDrawablesWithIntrinsicBounds(
                null,
                collect,
                null,
                null
            )
        })
        mModel.collectLiveData.observe(this, Observer
        {
            dissMissLoadingDialog()
            mVenuesBean!!.vipResourceStatus.collectionStatus = true
            mVenuesBean!!.collectionNum = mVenuesBean!!.collectionNum + 1
            mBinding.collectNum = mVenuesBean!!.collectionNum.toString()
            var collect =
                resources.getDrawable(R.mipmap.venue_bottom_icon_collect_selected)
            mBinding.tvCollect.setCompoundDrawablesWithIntrinsicBounds(
                null,
                collect,
                null,
                null
            )
        })
        mModel.contentLsLd.observe(this, Observer {
            if (!it.isNullOrEmpty()) {

                if (BaseApplication.appArea != "sc") {
                    //资讯
                    quickNavigationItem.add(
                        QuickNavigationItem(
                            resources.getString(R.string.venue_news),
                            R.mipmap.venue_details_anchor_icon_news,
                            mBinding.prvConentLs.id
                        )
                    )
                    quickNavigationAdapter.setNewData(quickNavigationItem)
                    updateQuickNavigation()

                    mVenueStickTopAdapter?.setNewData(quickNavigationItem)
                    mVenueStickTopAdapter?.notifyDataSetChanged()
                } else {
                    if (!fixedTypeCollection.contains(type)) {
                        //资讯
                        quickNavigationItem.add(
                            QuickNavigationItem(
                                resources.getString(R.string.venue_news),
                                R.mipmap.venue_details_anchor_icon_news,
                                mBinding.prvConentLs.id
                            )
                        )
                        quickNavigationAdapter.setNewData(quickNavigationItem)
                        updateQuickNavigation()

                        mVenueStickTopAdapter?.setNewData(quickNavigationItem)
                        mVenueStickTopAdapter?.notifyDataSetChanged()
                    }
                }

                mModel.updateStickTopItem("资讯", mBinding.prvConentLs.id)
                mBinding.prvConentLs.visibility = View.VISIBLE
                mBinding.prvConentLs.setData(id, ResourceType.CONTENT_TYPE_VENUE, it)
            } else {
                mBinding.prvConentLs.visibility = View.GONE
            }
        })
        // 下单信息
        mModel.orderAddresInfoLiveData.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                // 显示预订按钮
                mBinding?.txtVenueReservation.visibility = View.VISIBLE
                mBinding?.txtVenueReservation.onNoDoubleClick {
                    if (AppUtils.isLogin()) {
                        if (it.size > 1) {
                            // 显示弹窗
                            var location: IntArray = IntArray(2)
                            mBinding.vMainActivityBottom.getLocationOnScreen(location);
                            if (orderAddressPopWindow == null) {
                                orderAddressPopWindow =
                                    OrderAddressPopWindow(this@VenuesDetailsActivity)
                                orderAddressPopWindow!!.updatePopWindowHeight(location[1])
                            }
                            orderAddressPopWindow?.updateData(it)
                            if (!orderAddressPopWindow!!.isShowing) {
                                orderAddressPopWindow?.showAtLocation(
                                    mBinding.vMainActivityBottom,
                                    Gravity.NO_GRAVITY,
                                    0,
                                    0
                                )
                            }
                        } else {
                            //  2020-5-28 栾建新加功能，提示弹窗
                            var item = it[0]
                            if (item != null) {
                                if (item.linkTips.isNullOrEmpty()) {
                                    ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
                                        .withString("mTitle", "详情")
                                        .withString("html", item.url)
                                        .navigation()
                                } else {
                                    ProviderTipDialog.Builder().setContent(item.linkTips)
                                        .setContent(item.linkTips)
                                        .setOnTipConfirmListener(object :
                                            ProviderTipDialog.OnTipConfirmListener {
                                            override fun onConfirm() {
                                                ARouter.getInstance()
                                                    .build(ARouterPath.Provider.WEB_ACTIVITY)
                                                    .withString("mTitle", "详情")
                                                    .withString("html", item.url)
                                                    .navigation()
                                            }
                                        })
                                        .create(this@VenuesDetailsActivity).show()
                                }
                            } else {
                                ToastUtils.showMessage("数据异常，请联系管理员~")
                            }
                        }
                    } else {
                        ToastUtils.showUnLoginMsg()
                        ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                            .navigation()
                    }
                }
            } else {
                mBinding?.txtVenueReservation.visibility = View.GONE
            }

            // 2020/9/17 修改
            if (!it.isNullOrEmpty()) {
                if (BaseApplication.appArea != "sc") {
                    // 预约
                    quickNavigationItem.filter { it.id == mBinding.flVenueReservation.id }
                        .takeIf { it.isEmpty() }?.apply {
                            quickNavigationItem.add(
                                QuickNavigationItem(
                                    resources.getString(R.string.venue_reserve),
                                    R.mipmap.venue_details_anchor_icon_book,
                                    mBinding.flVenueReservation.id
                                )
                            )
                            quickNavigationAdapter.setNewData(quickNavigationItem)
                            updateQuickNavigation()

                            mVenueStickTopAdapter?.setNewData(quickNavigationItem)
                            mVenueStickTopAdapter?.notifyDataSetChanged()
                        }
                } else {
                    if (!fixedTypeCollection.contains(type)) {
                        // 预约
                        quickNavigationItem.filter { it.id == mBinding.flVenueReservation.id }
                            .takeIf { it.isEmpty() }?.apply {
                                quickNavigationItem.add(
                                    QuickNavigationItem(
                                        resources.getString(R.string.venue_reserve),
                                        R.mipmap.venue_details_anchor_icon_book,
                                        mBinding.flVenueReservation.id
                                    )
                                )
                                quickNavigationAdapter.setNewData(quickNavigationItem)
                                updateQuickNavigation()

                                mVenueStickTopAdapter?.setNewData(quickNavigationItem)
                                mVenueStickTopAdapter?.notifyDataSetChanged()
                            }
                    }
                }
                mBinding.flVenueReservation.visibility = View.VISIBLE
                transactFragment(
                    mBinding.flVenueReservation.id,
                    PerformanceReservationFragment.newInstance(ArrayList(it))
                )
                performanceReservationFlag = true
            }
        })

        // 其他栏目
        mModel.contentListByChannelCodeLiveData.observe(this, Observer {
            mBinding.rvVenueOtherColumns.visibility = View.VISIBLE
            mBinding.rvVenueOtherColumns.layoutManager = LinearLayoutManager(context)
            otherColumnsAdapter.add(arrayListOf(it))
            mBinding.rvVenueOtherColumns.adapter = otherColumnsAdapter

            val other = QuickNavigationItem(
                it["title"] as String,
                R.mipmap.venue_details_anchor_icon_column,
                mBinding.rvVenueOtherColumns.id
            )
            if (!quickNavigationItem.contains(other)) {
                quickNavigationItem.add(other)
            }

            quickNavigationAdapter.setNewData(quickNavigationItem)
            updateQuickNavigation()

            mVenueStickTopAdapter?.setNewData(quickNavigationItem)
            mVenueStickTopAdapter?.notifyDataSetChanged()
        })


        // 社团
        mModel.societiesListLiveData.observe(this, Observer {
            mBinding.flVenueSocieties.visibility = View.VISIBLE
            mBinding.flVenueSocieties.setData(it)

            val league = QuickNavigationItem(
                resources.getString(R.string.venue_club),
                R.mipmap.venue_details_anchor_icon_community,
                mBinding.flVenueSocieties.id
            )
            if (!quickNavigationItem.contains(league)) {
                quickNavigationItem.add(league)
            }

            quickNavigationAdapter.setNewData(quickNavigationItem)
            updateQuickNavigation()

            mVenueStickTopAdapter?.setNewData(quickNavigationItem)
            mVenueStickTopAdapter?.notifyDataSetChanged()
        })

    }

    private fun setReseration(it: VenuesDetailsBean, type: String) {

        mBinding.flVenueReservation.visibility = View.VISIBLE
        transactFragment(
            R.id.fl_venue_reservation, AppointmentFragment.newInstance(
                id, ResourceType.CONTENT_TYPE_VENUE, "",
                type,
                fromWhichPage = ARouterPath.VenuesModule.VENUES_DETAILS_ACTIVITY
            ).apply {
                onAppointmentListener = object : AppointmentFragment.OnAppointmentListener {
                    override fun onGetAppointDate(isHide: Boolean) {
                        if (isHide) {
                            if (!performanceReservationFlag) {
                                mBinding.flVenueReservation.visibility = View.GONE
                            }
                            mBinding.txtVenueReservation.visibility = View.GONE
                        } else {
                            mBinding.txtVenueReservation.visibility = View.VISIBLE

                            if (BaseApplication.appArea != "sc") {
                                //预约
                                quickNavigationItem.filter { it.id == mBinding.flVenueReservation.id }
                                    .takeIf { it.isEmpty() }?.apply {
                                        quickNavigationItem.add(
                                            QuickNavigationItem(
                                                resources.getString(R.string.venue_reserve),
                                                R.mipmap.venue_details_anchor_icon_book,
                                                mBinding.flVenueReservation.id
                                            )
                                        )
                                        quickNavigationAdapter.setNewData(quickNavigationItem)
                                        updateQuickNavigation()

                                        mVenueStickTopAdapter?.setNewData(quickNavigationItem)
                                        mVenueStickTopAdapter?.notifyDataSetChanged()
                                    }
                            } else {
                                if (!fixedTypeCollection.contains(type)) {
                                    //预约
                                    quickNavigationItem.filter { it.id == mBinding.flVenueReservation.id }
                                        .takeIf { it.isEmpty() }?.apply {
                                            quickNavigationItem.add(
                                                QuickNavigationItem(
                                                    resources.getString(R.string.venue_reserve),
                                                    R.mipmap.venue_details_anchor_icon_book,
                                                    mBinding.flVenueReservation.id
                                                )
                                            )
                                            quickNavigationAdapter.setNewData(quickNavigationItem)
                                            updateQuickNavigation()

                                            mVenueStickTopAdapter?.setNewData(quickNavigationItem)
                                            mVenueStickTopAdapter?.notifyDataSetChanged()
                                        }
                                }
                            }

                        }
                    }

                }
            }
        )
    }

    private fun updateQuickNavigation() {
        quickNavigationAdapter.notifyDataSetChanged()
        // 新增需求 ，大于1才显示快捷导航 2020/10/21-20点45分
        mBinding.llQuickNavigation.visibility = if (quickNavigationAdapter.isNeedShow()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun initData() {
        showLoadingDialog()
        mModel.getVenuesDetails(id)
        mModel.getVenuesCommentList(id)
        mModel.getVenuesStories(id)
        mModel.getVenueContentLs(id)

        if (BaseApplication.appArea == "sc") {
            when (type) {
                CULTURAL_HALL -> {
                    // 艺术资源
                    mModel.getContentListByChannelCode(
                        id,
                        "yszy",
                        resources.getString(R.string.venue_art_resources)
                    )
                    // 社团
                    mModel.getSocietiesList(id)
                }
                ART_GALLERY -> {
                    // 线上展览
                    mModel.getContentListByChannelCode(
                        id,
                        "xszl",
                        resources.getString(R.string.venue_online_exhibition)
                    )
                }
                THEATER -> {
                    // 经典赏析
                    mModel.getContentListByChannelCode(
                        id,
                        "jdsx",
                        resources.getString(R.string.venue_classic_appreciation)
                    )
                }
                MUSEUM -> {
                }
                else -> {
                    mModel.getResourceChannelList(id)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return
        }
        super.onBackPressed()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onPause() {
        super.onPause()

        EventBus.getDefault().post(UpdatePlayerStateEvent(2))
        mBinding?.vcvCommentarySpeaking.pauseAudioPlayer()
        mBinding?.vcvCommentarySpeaking.updatePauseUi()
        mBinding.cbrVenueDetail.pauseVideoPlayer()
        mBinding.cbrVenueDetail.stopTurning()
    }


    /**
     * 初始化列表适配器标签
     */
    fun initRecyclerViewAdapter() {
        // 类别标签的列表样式
        labelAdapter = LabelAdapter(context!!, labelList)
        mBinding.recyclerVenuesDetailsLabel.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBinding.recyclerVenuesDetailsLabel.adapter = labelAdapter
    }

    override fun onPageChanged(index: Int) {
        val pos: Int = index + 1
        mBinding?.txtVenueTopImgIndex.text = "$pos/$imageSize"
    }

    override fun onResume() {
        super.onResume()
        mBinding.cbrVenueDetail.startTurning(3000)
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding?.cbrVenueDetail?.stopVideoPlayer()
        JCVideoPlayer.releaseAllVideos();
        mBinding?.vcvCommentarySpeaking.releaseAudioPlayer()
        EventBus.getDefault().unregister(this)
        StatisticsRepository.instance.statisticsPage(mVenuesBean?.name, 2)
        mQrCodeDialog = null
        orderAddressPopWindow = null
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateAudioPlayer(event: UpdateAudioStateEvent) {
        // 1 准备播放 2 播放 3暂停/完成
        try {
            when (event.type) {
                1 -> {
                    mBinding?.cbrVenueDetail.stopTurning()
                    mBinding?.vcvCommentarySpeaking?.pauseAudioPlayer()
                }
                2 -> {
                    mBinding?.cbrVenueDetail.stopTurning()
                    mBinding?.vcvCommentarySpeaking?.pauseAudioPlayer()
                }
                3 -> {
                    mBinding?.cbrVenueDetail.startTurning(3000)
                }
            }
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
//                mBinding.vncVenueRecommend?.setLocation(LatLng(lat_, lon_))
//                mModel.gethMapRecList(type, id)
            }

            override fun onError(errormsg: String) {
            }
        })
    }

    /**
     * 处理二维码显示
     */
    private fun dealShowQrCode() {
        if (mVenuesBean != null && !mVenuesBean!!.officialUrl.isNullOrEmpty()) {
            if (mQrCodeDialog == null) {
                mQrCodeDialog = QrCodeDialog.Builder().qrCodeImageUrl(mVenuesBean!!.officialUrl)
                    .title(mVenuesBean!!.officialName)
                    .onDownLoadListener(object : QrCodeDialog.OnDownLoadListener {
                        override fun onDownLoadImage(url: String) {
                            try {
                                showLoadingDialog()
                                DownLoadFileUtil.downNetworkImage(url, object : DownImageListener {
                                    override fun onDownLoadImageSuccess() {
                                        dissMissLoadingDialog()
                                        ToastUtils.showMessage("保存二维码成功~")
                                    }
                                })
                            } catch (e: Exception) {
                                dissMissLoadingDialog()
                                ToastUtils.showMessage("保存二维码失败~")
                            }
                        }

                    })
                    .build(this@VenuesDetailsActivity)
            } else {
                mQrCodeDialog?.updateData(mVenuesBean!!.officialUrl, mVenuesBean!!.officialName)
            }
            mQrCodeDialog!!.show()
        }
    }

    /**
     * 处理地图导航
     */
    private fun dealMapNav() {
        if (mVenuesBean != null && !mVenuesBean?.latitude.isNullOrEmpty() && !mVenuesBean?.longitude.isNullOrEmpty()) {
            if (MapNaviUtils.isGdMapInstalled()) {
                MapNaviUtils.openGaoDeNavi(
                    context, 0.0, 0.0, null,
                    mVenuesBean!!.latitude.toDouble(), mVenuesBean!!.longitude.toDouble(),
                    mVenuesBean!!.regionName
                )
            } else {
                mModel.toast.postValue("非常抱歉，系统未安装地图软件")
            }
        } else {
            mModel.toast.postValue("非常抱歉，暂无位置信息")
        }
    }


    /**
     * 处理视频播放界面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updatePlayUi(event: UpdatePlayUiStateEvent) {
        if (event.state == 1) {
            // 暂停
            mBinding?.imgVenusPlayStatus?.setImageResource(R.mipmap.venue_details_banner_pause)
        } else if (event.state == 0) {
            // 播放
            mBinding?.imgVenusPlayStatus?.setImageResource(R.mipmap.venue_details_banner_play)
        }
    }

    /**
     * 暂停音频播放
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateAudioUi(event: UpdateAudioPlayerStateEvent) {
        if (event.state == 2) {
            // 暂停
            mBinding?.vcvCommentarySpeaking.pauseAudioPlayer()
        }
    }


    /**
     * 检查 view 是否在屏幕内是否可见
     * @param view View
     * @return Boolean
     */
    private fun checkVisibleOnScreen(view: View): Boolean {
        val dm: DisplayMetrics = context.resources.displayMetrics
        val widthPixels: Int = dm.widthPixels
        val heightPixels: Int = dm.heightPixels
        val rect = Rect(0, 0, widthPixels, heightPixels)
        val location = IntArray(2)
        view.getLocationInWindow(location)
        return view.getLocalVisibleRect(rect)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRecordEvent(event: UpdateCommentEvent) {
        mModel.getVenuesDetails(id)
        mModel.getVenuesCommentList(id)
    }
}


