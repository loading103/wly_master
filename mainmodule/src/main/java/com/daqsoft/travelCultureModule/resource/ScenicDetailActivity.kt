package com.daqsoft.travelCultureModule.resource

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.renderscript.ScriptGroup
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.col.sln3.it
import com.amap.api.maps.model.LatLng
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication.Companion.context
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemPanorBinding
import com.daqsoft.mainmodule.databinding.ItemPanorNewBinding
import com.daqsoft.mainmodule.databinding.MainSecnicDetailActivityBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.businessview.adapter.ProviderActivityV2Adapter
import com.daqsoft.provider.businessview.event.LoginStatusEvent
import com.daqsoft.provider.businessview.event.UpdateCommentEvent
import com.daqsoft.provider.businessview.fragment.AppointmentFragment
import com.daqsoft.provider.businessview.fragment.ProviderTicketBookingFragment
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.businessview.view.ListenerAudioView
import com.daqsoft.provider.businessview.view.OrderAddressPopWindow
import com.daqsoft.provider.businessview.view.ProviderRecommendView
import com.daqsoft.provider.electronicBeans.ProductBean
import com.daqsoft.provider.event.UpdateAudioPlayerEvent
import com.daqsoft.provider.event.UpdateScenicVideoPlayerEvent
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.isLegalInt
import com.daqsoft.provider.net.StatisticsRepository
import com.daqsoft.provider.network.venues.bean.AudioInfo
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.provider.view.dialog.ProviderTipDialog
import com.daqsoft.provider.view.popupwindow.SharePopWindow
import com.daqsoft.travelCultureModule.hotel.view.RouteOrderView
import com.daqsoft.travelCultureModule.resource.adapter.ScenicSpotAdapter
import com.daqsoft.travelCultureModule.resource.bean.LiveListBean
import com.daqsoft.travelCultureModule.resource.fragment.ScenicDetailTopFragment
import com.daqsoft.travelCultureModule.resource.view.AppointmentPopWindow
import com.daqsoft.travelCultureModule.resource.view.RouterPopWindow
import com.daqsoft.travelCultureModule.resource.view.ScenicTiketView
import com.daqsoft.travelCultureModule.resource.viewmodel.ScenicDetailViewModel
import com.umeng.socialize.UMShareAPI
import kotlinx.android.synthetic.main.main_secnic_detail_activity.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @Description ??????????????????
 * @ClassName   ScenicDetailActivity
 * @Author      luoyi
 * @Time        2020/4/1 18:33
 */
@Route(path = MainARouterPath.MAIN_SCENIC_DETAIL)
class ScenicDetailActivity :
    TitleBarActivity<MainSecnicDetailActivityBinding, ScenicDetailViewModel>() {

    @JvmField
    @Autowired
    var id: String = ""

    /**????????????????????????????????????*/
    @JvmField
    @Autowired(name = "isShowButton")
    var isShowButton: Boolean = false

    /**????????????*/
    @JvmField
    @Autowired(name = "source")
    var source: String = ""

    /**
     * ???????????????
     */
    var spotAdapter: ScenicSpotAdapter? = null

    /**
     * ?????????????????????
     */
    var hotActivityAdapter: ProviderActivityV2Adapter? = null

    /**
     * ??????????????????
     */
    var mScenicDetailBean: ScenicDetailBean? = null

    /**
     * ????????????
     */
    var scenicTags: ScenicTags? = null

    /**
     * ????????????drawalbe
     */
    var brandGradientDrawable: GradientDrawable? = null

    /**
     * ??????????????????window
     */
    var appointMentPopWindow: AppointmentPopWindow? = null

    var routerPopWindow: RouterPopWindow? = null

    var scenicDetailTopFrag: ScenicDetailTopFragment? = null

    /**
     * ????????????
     */
    var orderAddressPopWindow: OrderAddressPopWindow? = null

    var prviderTicketBookingDialog: ProviderTicketBookingFragment? = null

    /**
     * ????????????
     */
    var sharePopWindow: SharePopWindow? = null

    override fun getLayout(): Int = R.layout.main_secnic_detail_activity

    override fun setTitle(): String = getString(R.string.main_scenic_detail)

    override fun injectVm(): Class<ScenicDetailViewModel> = ScenicDetailViewModel::class.java

    override fun initView() {

        if (!source.isNullOrEmpty() && !isShowButton) {
            //?????????????????????
            mBinding.vMainScenicDetailBottom.getChildAt(0).visibility = View.GONE
            //????????????????????????
            mBinding.vMainScenicDetailBottom.getChildAt(1).let {
                it.visibility = View.VISIBLE
                (it as LinearLayout).getChildAt(0).setOnClickListener {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }

        mBinding.vm = mModel
        EventBus.getDefault().register(this)
        initViewEvent()
        initViewModel()
    }

    override fun initPageParams() {
        isHideAnother = false
    }

    private fun initViewEvent() {
        // ??????
        spotAdapter = ScenicSpotAdapter()
        // ????????????
        // ??????
        mBinding.rvPanor.layoutManager = StaggeredGridLayoutManager(
            2, StaggeredGridLayoutManager
                .VERTICAL
        )
        mBinding.rvPanor.adapter = liveAdapter
        // ??????
        hotActivityAdapter = ProviderActivityV2Adapter(this)
        val activityLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvActivities.layoutManager = activityLayoutManager
        mBinding.rvActivities.adapter = hotActivityAdapter


        mBinding.llVenuesDetailsComplaint.onNoDoubleClick {
            // ????????????
            MenuJumpUtils.gotoComplaint()
        }
        mBinding.llVenuesDetailsBus.onNoDoubleClick {
            // ????????????
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
                    mScenicDetailBean?.let {
                        if (!it.latitude.isNullOrEmpty() && !it.longitude.isNullOrEmpty()) {
                            mModel.gethMapRecList(
                                type,
                                id,
                                mScenicDetailBean!!.longitude,
                                mScenicDetailBean!!.latitude
                            )
                        }
                    }
                }
            }
        mBinding.tvScenicDetailCollect.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                if (mScenicDetailBean != null && mScenicDetailBean!!.vipResourceStatus != null) {
                    showLoadingDialog()
                    if (mScenicDetailBean!!.vipResourceStatus!!.collectionStatus) {
                        mModel.collectionCancel(id)
                    } else {
                        mModel.collection(id)
                    }
                }
            } else {
                ToastUtils.showUnLoginMsg()
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
        mBinding.tvScenicDetailThumb.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                mScenicDetailBean?.let {
                    if (it.vipResourceStatus != null) {
                        showLoadingDialog()
                        if (mScenicDetailBean!!.vipResourceStatus!!.likeStatus) {
                            mModel.thumbCancell(id)
                        } else {
                            mModel.thumbUp(id)
                        }
                    }
                }
            } else {
                ToastUtils.showUnLoginMsg()
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
        mBinding.tvScenicDetailCommentNum.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                ARouter.getInstance().build(ARouterPath.Provider.PROVIDER_POST_COMMENT)
                    .withString("id", id)
                    .withString("type", ResourceType.CONTENT_TYPE_SCENERY)
                    .withString("contentTitle", mScenicDetailBean!!.name)
                    .navigation()
            } else {
                ToastUtils.showUnLoginMsg()
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
    }

    private fun initViewModel() {
        initScenicInfoViewModel()
        initScenicProductViewModel()
        // ????????????
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })
        // ????????????
        mModel.contentLsLd.observe(this, Observer {
            mBinding.prvConentLs?.setvisibility(!it.isNullOrEmpty())
            if (!it.isNullOrEmpty()) {
                mBinding.prvConentLs.setData(id, ResourceType.CONTENT_TYPE_SCENERY, it)
            }
        })
    }

    override fun initCustomTitleBar(mTitleBar: TitleBar) {
        showShareButton(com.daqsoft.provider.R.mipmap.common_nav_button_share)
        setShareClick(object : View.OnClickListener {
            override fun onClick(v: View?) {
                mScenicDetailBean?.let {
                    if (sharePopWindow == null) {
                        sharePopWindow = SharePopWindow(this@ScenicDetailActivity)
                    }
                    var content= Constant.SHARE_DEC
                    if(!TextUtils.isEmpty(it.summary)){
                        content=it.summary
                    }
                    // ??????????????????
                    sharePopWindow?.setShareContent(
                        it.name, content, it.images.getRealImages(),
                        ShareModel.getScenicDetailUrl(id)
                    )
                    if (!sharePopWindow!!.isShowing) {
                        sharePopWindow?.showAsDropDown(mTitleBar)
                    }
                }
            }

        })
    }

    /**
     * ????????????????????????
     */
    private fun initScenicInfoViewModel() {
        // ??????
        mModel.spots.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.vScenicSpots.setData(
                    it,
                    supportFragmentManager,
                    mScenicDetailBean?.name,
                    mScenicDetailBean?.images?.getRealImages(),
                    scenicTags,
                    id.toInt()
                )
                mBinding.vScenicSpots.visibility = View.VISIBLE
            } else {
                if (mBinding.vScenicSpots.isVisible)
                    mBinding.vScenicSpots.hideSpotsContent()
            }
        })
        // ??????????????????
        mModel.refereshScenicDetail.observe(this, Observer {
            if (it != null) {
                mScenicDetailBean = it
                if (it.vipResourceStatus != null) {
                    // ??????
                    setCollectUi(it.vipResourceStatus.collectionStatus)
                    // ??????
                    setThumbUi(it.vipResourceStatus.likeStatus)
                }
                if (it.collectionNum.isLegalInt()) {
                    mBinding.tvScenicDetailCollect.text = "${it.collectionNum}"
                }
                if (it.likeNum.isLegalInt()) {
                    mBinding.tvScenicDetailThumb.text = "${it.likeNum}"
                }
            }
        })
        // ??????
        mModel.colllectLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            setCollectUi(it)
            mScenicDetailBean!!.vipResourceStatus?.collectionStatus = it
            var temp = mScenicDetailBean!!.collectionNum
            if (temp.isLegalInt()) {
                var collectNum: Int = temp.toInt()
                if (it) {
                    // ????????????
                    val result = (collectNum + 1).toString()
                    mScenicDetailBean!!.collectionNum = result
                    mBinding.tvScenicDetailCollect.text = result
                } else {
                    // ????????????
                    if (collectNum > 0) {
                        val result = (collectNum - 1)
                        mScenicDetailBean!!.collectionNum = result.toString()
                        if (result > 0) {
                            mBinding.tvScenicDetailCollect.text = result.toString()
                        } else {
                            mBinding.tvScenicDetailCollect.text = "??????"
                        }
                    }
                }
            }

        })
        // ??????
        mModel.thumbLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            setThumbUi(it)
            mScenicDetailBean!!.vipResourceStatus?.likeStatus = it
            var temp = mScenicDetailBean!!.likeNum
            if (temp.isLegalInt()) {
                var likeNum: Int = temp.toInt()
                if (it) {
                    // ????????????
                    val result = likeNum + 1
                    mScenicDetailBean!!.likeNum = result.toString()
                    mBinding.tvScenicDetailThumb.text = result.toString()
                } else {
                    // ????????????
                    if (likeNum > 0) {
                        val result = likeNum - 1
                        mScenicDetailBean!!.likeNum = result.toString()
                        if (result > 0) {
                            mBinding.tvScenicDetailThumb.text = result.toString()
                        } else {
                            mBinding.tvScenicDetailThumb.text = "??????"
                        }
                    }
                }
            }
        })

        // ????????????
        mModel.storyList.observe(this, Observer {
            mBinding.psvScenicStories.setVisibe(!it.isNullOrEmpty())
            if (!it.isNullOrEmpty()) {
//                mBinding.psvScenicStories.setData(it)
                mBinding.psvScenicStories.setDataNumber(id,ResourceType.CONTENT_TYPE_SCENERY,it,mModel.storyNumber)
            }
        })
        // ????????????
        mModel.routeResult.observe(this, Observer {
            mBinding.vSenicDetailRouters.setVisible(!it.productListVO.isNullOrEmpty())
            if (!it.productListVO.isNullOrEmpty()) {
                mBinding.vSenicDetailRouters.setData(it.productListVO as MutableList<ProductBean>)
                mBinding.vSenicDetailRouters?.onRouterViewListener =
                    object : RouteOrderView.OnRouterViewListener {
                        override fun onGetRouterViewListener(snCode: String, name: String) {
                            showLoadingDialog()
                            mModel.getRouterReservationInfo(snCode, name)
                        }
                    }
            }
        })
        // ??????
        mModel.commentBeans.observe(this, Observer {
            mBinding.pcvScenicDetailComments.setVisible(!it.isNullOrEmpty())
            if (!it.isNullOrEmpty()) {
                mBinding.pcvScenicDetailComments.setData(it)
            }
        })

        mModel.goldStory.observe(this, Observer {

        })
        // ????????????
        mModel.scenicDetail.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.bean = it
            bindScenicData(it)
            //  ??????????????????
            mModel.getScenicSpots(id)
        })
        // ??????
        mModel.scenicSpotsPanor.observe(this, Observer {
            // ??????
            if (!it.isNullOrEmpty()) {
                try {
                    liveAdapter?.clear()
                    liveAdapter?.add(it)
                    if(it.size>0) {
                        mBinding.ilPanor.hideRight = false
                        mBinding.ilPanor.rightContent = "???${it.size}?????????"
                        mBinding.ilPanor.tvReplayNum.setOnClickListener {
                            ARouter.getInstance().build(MainARouterPath.MAIN_SCENIC_SPOTS_LIVES)
                                .withString("spotId",  id)
                                .navigation()
                        }
                    }
                    else{
                        mBinding.ilPanor.hideRight=false
                    }
                } catch (e: Exception) {
                }
            } else {
            }
        })


        // ????????????
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
                            spanScenicTags.append("${brandBean.siteCount}??????????????????")
                        }
                        if (brandBean.relationResourceCount != 0) {
                            spanScenicTags.append(" ${brandBean.relationResourceCount}???????????????")
                        }
                        mBinding.txtScenicDetailBrandInfo.text = spanScenicTags.toString()
                        Glide.with(context!!).load(brandBean.brandImage)
                            .placeholder(R.mipmap.placeholder_img_fail_240_180)
                            .centerCrop()
                            .into(mBinding.imgScenicDetailBrandBg)
                        if (!brandBean.mainColor.isNullOrEmpty()) {
                            try {
                                // ????????????????????????????????????
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
        // ????????????
        mModel.mapResLiveData.observe(this, Observer {
            if (it != null) {
                mBinding.prvScenicDetail.setData(it.type, it.datas)
            }
        })
    }

    /**
     * ????????????????????????
     */
    private fun initScenicProductViewModel() {
        // ????????????
        mModel.tiketsLiveData.observe(this, Observer {
            if (it != null && !it.sptTitleList.isNullOrEmpty() && mScenicDetailBean != null) {
                // ??????????????????
                initTripTiket(it)
                mBinding.vScenicDetailTikets.setData(
                    it,
                    mScenicDetailBean!!.shopName,
                    mScenicDetailBean!!.shopUrl
                )
                mBinding.vScenicDetailTikets.visibility = View.VISIBLE
                mBinding.vScenicDetailTikets.onScenicTiketViewListener =
                    object : ScenicTiketView.OnScenicTiketViewListener {
                        override fun onScenicTiketGetRegistinfo(productId: String?) {
                            if (!productId.isNullOrEmpty()) {
                                showLoadingDialog()
                                mModel.getReservationInfo(productId)
                            } else {
                                ToastUtils.showMessage("??????????????????")
                            }
                        }

                    }
            } else {
                mBinding.vScenicDetailTikets.visibility = View.GONE
            }
        })

        // ????????????
        mModel.scenicReservationLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            if (it != null) {
                if (appointMentPopWindow == null) {
                    appointMentPopWindow = AppointmentPopWindow(this@ScenicDetailActivity)
                }
                appointMentPopWindow!!.updateData(it)
                appointMentPopWindow!!.showAtLocation(mBinding.root, Gravity.BOTTOM, 0, 0)
            }
        })
        // ????????????
        mModel.routerReservationLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            if (it != null) {
                if (routerPopWindow == null) {
                    routerPopWindow = RouterPopWindow(this@ScenicDetailActivity)
                }
                routerPopWindow!!.updateData(it)
                routerPopWindow!!.showAtLocation(mBinding.root, Gravity.BOTTOM, 0, 0)
            }
        })
        // ????????????
        mModel.orderAddresInfoLiveData.observe(this, Observer {
            if (!it.isNullOrEmpty()  && !it[0].url.isNullOrEmpty() ) {
                // ??????????????????
                mBinding?.btScenicToOrder.visibility = View.VISIBLE
                mBinding?.btScenicToOrder.onNoDoubleClick {
                    scenicToOrder(it)
                }
                mBinding?.tvScenicToOrder.visibility = View.VISIBLE
                mBinding?.tvScenicToOrder.onNoDoubleClick {
                    scenicToOrder(it)
                }
            } else {
                mBinding?.btScenicToOrder.visibility = View.GONE
                mBinding?.tvScenicToOrder.visibility = View.GONE
            }
        })

    }

    private fun initTripTiket(data: SptTicketBean) {
        if (!source.isNullOrEmpty() && !isShowButton) {
            prviderTicketBookingDialog = ProviderTicketBookingFragment.newInstance(
                mScenicDetailBean!!.shopName,
                mScenicDetailBean!!.shopUrl
            ).apply {
                onScenicTiketViewListener = object : ProviderTicketBookingFragment.OnScenicTiketViewListener {
                    override fun onScenicTiketGetRegistinfo(productId: String?) {
                        productId?.let {
                            mModel.getReservationInfo(it)
                        }
                    }
                }
                mScenicTiketBean = data
            }
            // ??????????????????
            mBinding.btScenicToOrder.visibility = View.VISIBLE
            mBinding.btScenicToOrder.onNoDoubleClick {
                //
                showScenicProductDialog()
            }
        }
    }

    private fun showScenicProductDialog() {
        if (prviderTicketBookingDialog != null && !prviderTicketBookingDialog!!.isAdded) {
            prviderTicketBookingDialog?.show(supportFragmentManager, "scenic_ticket_book")
        }
    }

    private fun scenicToOrder(mutableList: MutableList<OderAddressInfoBean>) {
        if (AppUtils.isLogin()) {
            if (mutableList.size > 1) {
                // ????????????
                var location: IntArray = IntArray(2)
                mBinding.vMainScenicDetailBottom.getLocationOnScreen(location);
                if (orderAddressPopWindow == null) {
                    orderAddressPopWindow =
                        OrderAddressPopWindow(this@ScenicDetailActivity)
                    orderAddressPopWindow!!.updatePopWindowHeight(location[1])
                }
                orderAddressPopWindow?.updateData(mutableList)
                if (!orderAddressPopWindow!!.isShowing) {
                    orderAddressPopWindow?.showAtLocation(
                        mBinding.vMainScenicDetailBottom,
                        Gravity.NO_GRAVITY,
                        0,
                        0
                    )
                }
            } else {
                //  2020-5-28 ?????????????????????????????????
                var item = mutableList[0]
                if (item != null) {
                    if (item.linkTips.isNullOrEmpty()) {
                        ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
                            .withString("mTitle", "??????")
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
                                        .withString("mTitle", "??????")
                                        .withString("html", item.url)
                                        .navigation()
                                }
                            })
                            .create(this@ScenicDetailActivity).show()
                    }
                } else {
                    ToastUtils.showMessage("?????????????????????????????????~")
                }
            }
        } else {
            ToastUtils.showUnLoginMsg()
            ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                .navigation()
        }
    }

    private fun setThumbUi(it: Boolean) {
        if (it) {
            // ????????????
            var drawable =
                resources.getDrawable(R.mipmap.bottom_icon_like_selected)
            mBinding.tvScenicDetailThumb.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
        } else {
            // ????????????
            var drawable =
                resources.getDrawable(R.mipmap.bottom_icon_like_normal)
            mBinding.tvScenicDetailThumb.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
        }
    }

    private fun setCollectUi(it: Boolean) {
        if (it) {
            // ??????
            var collect =
                resources.getDrawable(R.mipmap.bottom_icon_collect_selected)
            mBinding.tvScenicDetailCollect.setCompoundDrawablesWithIntrinsicBounds(null, collect, null, null)
        } else {
            // ????????????
            var collect =
                resources.getDrawable(R.mipmap.bottom_icon_collect_normal)
            mBinding.tvScenicDetailCollect.setCompoundDrawablesWithIntrinsicBounds(null, collect, null, null)
        }
    }


    /**
     * ???????????????
     */
    val liveAdapter = object : RecyclerViewAdapter<ItemPanorNewBinding,
            LiveListBean>(R.layout.item_panor_new) {
        override fun setVariable(
            mBinding: ItemPanorNewBinding,
            position: Int,
            item: LiveListBean
        ) {
            mBinding.name = item.scenicSpotsName
            mBinding.root.setOnClickListener {
                ARouter.getInstance()
                    .build(ARouterPath.SlowLiveModule.SLOW_LIVE_DETAIL_ACTIVITY)
                    .withInt("scenicSpotsId", item.scenicSpotsId)
                    .withString("scenicSpotsName", item.scenicSpotsName)
                    .navigation()
            }
        }

    }

    override fun initData() {
        mModel.id = id

        showLoadingDialog()
        // ????????????
        mModel.getScenicDetail(id, true)
        //??????????????????
        mModel.getScenicBrandList(id)
        // ????????????
        mModel.getStoryList(id, ResourceType.CONTENT_TYPE_SCENERY)
        // ??????
        mModel.getActivityCommentList(id)
        //??????
        mModel.getScenicPanor(id)
        // ??????
        doLocation(ResourceType.CONTENT_TYPE_SCENERY)
        // ????????????
        mModel.getScenicContentLs(id)

    }

    private fun bindScenicData(it: ScenicDetailBean) {
        if (it == null) {
            mBinding.scrollScenicDetail.visibility = View.GONE
            ToastUtils.showMessage("???????????????????????????????????????~")
            finish()
            return
        } else {
            StatisticsRepository.instance.statisticsPage(it.name, 1)
            mScenicDetailBean = it
            // ????????????????????????
            scenicDetailTopFrag = ScenicDetailTopFragment.newInstance(it)
            transactFragment(R.id.fr_scenic_detail_top, scenicDetailTopFrag!!)
            mBinding.scrollScenicDetail.visibility = View.VISIBLE


            if (!it.resourceCode.isNullOrEmpty() && !it.sysCode.isNullOrEmpty() && !it.shopUrl.isNullOrEmpty()) {
                mModel.resourceCode = it.resourceCode
                mModel.sysCode = it.sysCode
                mModel.getSiteInfo()
            }
            // ??????????????????
            if (it.isOpen == 1) {
                if (!it.orderAddressType.isNullOrEmpty()) {
                    if (it.orderAddressType == "pt") {
                        // ????????????
                        mModel.getOrderAddressInfo(id.toString())
                        setReseationInfo(it, it.orderAddressType)
                    } else if (it.orderAddressType == "custom") {
                        // ??????????????????
//                        if (BaseApplication.appArea == "sc") {
                        setReseationInfo(it, it.orderAddressType)
//                        } else {
//                            fl_scenic_reservation.visibility = View.GONE
//                        }
                    } else if (it.orderAddressType == "ds") {
                        // ????????????

                    }
                }
            } else {
                mModel.getOrderAddressInfo(id)
                setReseationInfo(it, "pt")
            }


        }
        if (!it.name.isNullOrEmpty()) {
            setTitleContent("" + it.name)
        }

        // ?????????????????????????????????
        scenicTags = ScenicTags("", "", "")
        if (!it.level.isNullOrEmpty()) {
            scenicTags!!.level = it.level
        }
        if (!it.theme.isNullOrEmpty()) {
            var tags = DividerTextUtils.convertDotString(it.theme)
            scenicTags!!.tags = tags
        }


        // ?????????
        var audios: MutableList<AudioInfo> = mutableListOf()
        // ????????????
        if (it.goldStory != null && !it.goldStory.audio.isNullOrEmpty()) {
            audios.add(AudioInfo().apply {
                type = 1
                linkUrl = it.goldStory.link
                audio = it.goldStory.turl
                name = it.name
                time = it.audioTime
            })
        }

        if (!it.audioInfo.isNullOrEmpty()) {
            audios.addAll(it.audioInfo)
        }
        if (!audios.isNullOrEmpty()) {
            mBinding.vScenicDetailAudios.visibility = View.VISIBLE
            mBinding.vScenicDetailAudios.setData(audios)
        } else {
            mBinding.vScenicDetailAudios.visibility = View.GONE
        }

        // ??????
        if (!it.activity.isNullOrEmpty()) {
            try {
                hotActivityAdapter?.clear()
                hotActivityAdapter?.add(it.activity as MutableList<ActivityBean>)
                mModel.hideActivity.set(false)
            } catch (e: Exception) {
            }
        } else {
            mModel.hideActivity.set(true)
        }

        // ????????????
        if (!it.commentNum.isNullOrEmpty() && it.commentNum != "NULL") {
            mBinding.pcvScenicDetailComments.updateCommentNum(
                it.commentNum.toInt(),
                id,
                ResourceType.CONTENT_TYPE_SCENERY,
                mScenicDetailBean!!.name
            )
            if (it.commentNum.toInt() > 0) {
                mBinding.tvScenicDetailCommentNum.text = "${it.commentNum}"
            }
        }
        if (it.vipResourceStatus != null) {
            // ??????
            setCollectUi(it.vipResourceStatus.collectionStatus)
            // ??????
            setThumbUi(it.vipResourceStatus.likeStatus)
        }
        if (!it.collectionNum.isNullOrEmpty() && it.collectionNum != "0") {
            mBinding.tvScenicDetailCollect.text = "${it.collectionNum}"
        }
        if (!it.likeNum.isNullOrEmpty() && it.likeNum != "0") {
            mBinding.tvScenicDetailThumb.text = "${it.likeNum}"
        }

        if (!it.latitude.isNullOrEmpty() && !it.longitude.isNullOrEmpty()) {
            mModel.scenicLat = it.latitude.toDouble()
            mModel.scenicLng = it.longitude.toDouble()
            mBinding.prvScenicDetail.visibility = View.VISIBLE
            mBinding.prvScenicDetail?.setLocation(
                LatLng(
                    it.latitude.toDouble(),
                    it.longitude.toDouble()
                )
            )
            mModel.gethMapRecList(ResourceType.CONTENT_TYPE_HOTEL, id, it.longitude, it.latitude)
        } else {
            mBinding.prvScenicDetail.visibility = View.GONE
        }
        // ??????????????????
        if (it.tour != null) {
            mBinding.vScenicSpots?.setTour(it.tour)
            mBinding.vScenicSpots.visibility = View.VISIBLE
        } else {
            mBinding.vScenicSpots?.setTour(null)
        }

    }

    private fun setReseationInfo(it: ScenicDetailBean, type: String = "") {
        fl_scenic_reservation.visibility = View.VISIBLE
        transactFragment(
            R.id.fl_scenic_reservation,
            AppointmentFragment.newInstance(
                id,
                ResourceType.CONTENT_TYPE_SCENERY, "",
                type
            ).apply {
                onAppointmentListener = object : AppointmentFragment.OnAppointmentListener {
                    override fun onGetAppointDate(isHide: Boolean) {
                        if (isHide) {
                            mBinding.tvScenicToOrder.visibility = View.GONE
                        } else {
                            mBinding.tvScenicToOrder.visibility = View.VISIBLE
                        }
                    }

                }
            }
        )

        mBinding.tvScenicToOrder.text = "????????????"
        mBinding.tvScenicToOrder.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                ARouter.getInstance().build(ARouterPath.VenuesModule.SCENIC_RESERVATION_ACTIVITY)
                    .withString("scenicId", id)
                    .navigation()
            } else {
                ToastUtils.showUnLoginMsg()
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
    }


    override fun onPause() {
        super.onPause()
        EventBus.getDefault().post(UpdateScenicVideoPlayerEvent(1))
        mBinding.vScenicDetailAudios.pauseAudioPlayer()
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            StatisticsRepository.instance.statisticsPage(mScenicDetailBean?.name, 2)
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
        // ??????????????????
        try {
            scenicDetailTopFrag?.isContinue = !(event.type == 1 || event.type == 0)
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
            }

            override fun onError(errormsg: String) {
            }
        })
    }

    override fun onBackPressed() {
        try {
            // ?????????????????????????????????
            if (scenicDetailTopFrag != null) {
                if (!scenicDetailTopFrag!!.isHaveVideo || scenicDetailTopFrag!!.scenicVideoFrag == null ||
                    !scenicDetailTopFrag!!.scenicVideoFrag!!.onBackPress()
                ) {
                    super.onBackPressed()
                }
            }
        } catch (e: java.lang.Exception) {

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun updateDataStatus(event: LoginStatusEvent) {
        mModel?.refreshScenicDetail(id)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRecordEvent(event: UpdateCommentEvent) {
        mModel.getScenicDetail(id, true)
        mModel.getActivityCommentList(id.toString())
    }
}