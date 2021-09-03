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
 * @Description 景区详情页面
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

    /**是否显示底部加入行程按钮*/
    @JvmField
    @Autowired(name = "isShowButton")
    var isShowButton: Boolean = false

    /**跳转来源*/
    @JvmField
    @Autowired(name = "source")
    var source: String = ""

    /**
     * 景点适配器
     */
    var spotAdapter: ScenicSpotAdapter? = null

    /**
     * 热门活动适配器
     */
    var hotActivityAdapter: ProviderActivityV2Adapter? = null

    /**
     * 景区详情实体
     */
    var mScenicDetailBean: ScenicDetailBean? = null

    /**
     * 景区标签
     */
    var scenicTags: ScenicTags? = null

    /**
     * 品牌背景drawalbe
     */
    var brandGradientDrawable: GradientDrawable? = null

    /**
     * 景区预览须知window
     */
    var appointMentPopWindow: AppointmentPopWindow? = null

    var routerPopWindow: RouterPopWindow? = null

    var scenicDetailTopFrag: ScenicDetailTopFragment? = null

    /**
     * 门票预订
     */
    var orderAddressPopWindow: OrderAddressPopWindow? = null

    var prviderTicketBookingDialog: ProviderTicketBookingFragment? = null

    /**
     * 分享弹窗
     */
    var sharePopWindow: SharePopWindow? = null

    override fun getLayout(): Int = R.layout.main_secnic_detail_activity

    override fun setTitle(): String = getString(R.string.main_scenic_detail)

    override fun injectVm(): Class<ScenicDetailViewModel> = ScenicDetailViewModel::class.java

    override fun initView() {

        if (!source.isNullOrEmpty() && !isShowButton) {
            //隐藏评论等布局
            mBinding.vMainScenicDetailBottom.getChildAt(0).visibility = View.GONE
            //显示加入行程按钮
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
        // 景点
        spotAdapter = ScenicSpotAdapter()
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
        // 错误处理
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })
        // 景区资讯
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
                    // 设置分享数据
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
     * 景区展示相关信息
     */
    private fun initScenicInfoViewModel() {
        // 景点
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
        // 刷新景区数据
        mModel.refereshScenicDetail.observe(this, Observer {
            if (it != null) {
                mScenicDetailBean = it
                if (it.vipResourceStatus != null) {
                    // 收藏
                    setCollectUi(it.vipResourceStatus.collectionStatus)
                    // 点赞
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
        // 收藏
        mModel.colllectLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            setCollectUi(it)
            mScenicDetailBean!!.vipResourceStatus?.collectionStatus = it
            var temp = mScenicDetailBean!!.collectionNum
            if (temp.isLegalInt()) {
                var collectNum: Int = temp.toInt()
                if (it) {
                    // 收藏成功
                    val result = (collectNum + 1).toString()
                    mScenicDetailBean!!.collectionNum = result
                    mBinding.tvScenicDetailCollect.text = result
                } else {
                    // 取消收藏
                    if (collectNum > 0) {
                        val result = (collectNum - 1)
                        mScenicDetailBean!!.collectionNum = result.toString()
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
            mScenicDetailBean!!.vipResourceStatus?.likeStatus = it
            var temp = mScenicDetailBean!!.likeNum
            if (temp.isLegalInt()) {
                var likeNum: Int = temp.toInt()
                if (it) {
                    // 点赞成功
                    val result = likeNum + 1
                    mScenicDetailBean!!.likeNum = result.toString()
                    mBinding.tvScenicDetailThumb.text = result.toString()
                } else {
                    // 取消点赞
                    if (likeNum > 0) {
                        val result = likeNum - 1
                        mScenicDetailBean!!.likeNum = result.toString()
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
            mBinding.psvScenicStories.setVisibe(!it.isNullOrEmpty())
            if (!it.isNullOrEmpty()) {
//                mBinding.psvScenicStories.setData(it)
                mBinding.psvScenicStories.setDataNumber(id,ResourceType.CONTENT_TYPE_SCENERY,it,mModel.storyNumber)
            }
        })
        // 旅游路线
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
        // 评论
        mModel.commentBeans.observe(this, Observer {
            mBinding.pcvScenicDetailComments.setVisible(!it.isNullOrEmpty())
            if (!it.isNullOrEmpty()) {
                mBinding.pcvScenicDetailComments.setData(it)
            }
        })

        mModel.goldStory.observe(this, Observer {

        })
        // 景区详情
        mModel.scenicDetail.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.bean = it
            bindScenicData(it)
            //  获取景点信息
            mModel.getScenicSpots(id)
        })
        // 直播
        mModel.scenicSpotsPanor.observe(this, Observer {
            // 活动
            if (!it.isNullOrEmpty()) {
                try {
                    liveAdapter?.clear()
                    liveAdapter?.add(it)
                    if(it.size>0) {
                        mBinding.ilPanor.hideRight = false
                        mBinding.ilPanor.rightContent = "共${it.size}个实景"
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
                            spanScenicTags.append(" ${brandBean.relationResourceCount}个景区玩乐")
                        }
                        mBinding.txtScenicDetailBrandInfo.text = spanScenicTags.toString()
                        Glide.with(context!!).load(brandBean.brandImage)
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

    /**
     * 景区商品相关信息
     */
    private fun initScenicProductViewModel() {
        // 门票列表
        mModel.tiketsLiveData.observe(this, Observer {
            if (it != null && !it.sptTitleList.isNullOrEmpty() && mScenicDetailBean != null) {
                // 新增行程弹窗
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
                                ToastUtils.showMessage("暂无相关信息")
                            }
                        }

                    }
            } else {
                mBinding.vScenicDetailTikets.visibility = View.GONE
            }
        })

        // 预订须知
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
        // 线路须知
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
        // 下单信息
        mModel.orderAddresInfoLiveData.observe(this, Observer {
            if (!it.isNullOrEmpty()  && !it[0].url.isNullOrEmpty() ) {
                // 显示预订按钮
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
            // 显示底部按钮
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
                // 显示弹窗
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
                //  2020-5-28 栾建新加功能，提示弹窗
                var item = mutableList[0]
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
                            .create(this@ScenicDetailActivity).show()
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

    private fun setThumbUi(it: Boolean) {
        if (it) {
            // 点赞成功
            var drawable =
                resources.getDrawable(R.mipmap.bottom_icon_like_selected)
            mBinding.tvScenicDetailThumb.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
        } else {
            // 取消点赞
            var drawable =
                resources.getDrawable(R.mipmap.bottom_icon_like_normal)
            mBinding.tvScenicDetailThumb.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
        }
    }

    private fun setCollectUi(it: Boolean) {
        if (it) {
            // 收藏
            var collect =
                resources.getDrawable(R.mipmap.bottom_icon_collect_selected)
            mBinding.tvScenicDetailCollect.setCompoundDrawablesWithIntrinsicBounds(null, collect, null, null)
        } else {
            // 取消收藏
            var collect =
                resources.getDrawable(R.mipmap.bottom_icon_collect_normal)
            mBinding.tvScenicDetailCollect.setCompoundDrawablesWithIntrinsicBounds(null, collect, null, null)
        }
    }


    /**
     * 直播适配器
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
        // 景区详情
        mModel.getScenicDetail(id, true)
        //获取品牌信息
        mModel.getScenicBrandList(id)
        // 故事列表
        mModel.getStoryList(id, ResourceType.CONTENT_TYPE_SCENERY)
        // 评论
        mModel.getActivityCommentList(id)
        //直播
        mModel.getScenicPanor(id)
        // 定位
        doLocation(ResourceType.CONTENT_TYPE_SCENERY)
        // 景区资讯
        mModel.getScenicContentLs(id)

    }

    private fun bindScenicData(it: ScenicDetailBean) {
        if (it == null) {
            mBinding.scrollScenicDetail.visibility = View.GONE
            ToastUtils.showMessage("未找到景区信息，请稍后再试~")
            finish()
            return
        } else {
            StatisticsRepository.instance.statisticsPage(it.name, 1)
            mScenicDetailBean = it
            // 加载景区顶部数据
            scenicDetailTopFrag = ScenicDetailTopFragment.newInstance(it)
            transactFragment(R.id.fr_scenic_detail_top, scenicDetailTopFrag!!)
            mBinding.scrollScenicDetail.visibility = View.VISIBLE


            if (!it.resourceCode.isNullOrEmpty() && !it.sysCode.isNullOrEmpty() && !it.shopUrl.isNullOrEmpty()) {
                mModel.resourceCode = it.resourceCode
                mModel.sysCode = it.sysCode
                mModel.getSiteInfo()
            }
            // 获取下单地址
            if (it.isOpen == 1) {
                if (!it.orderAddressType.isNullOrEmpty()) {
                    if (it.orderAddressType == "pt") {
                        // 平台订单
                        mModel.getOrderAddressInfo(id.toString())
                        setReseationInfo(it, it.orderAddressType)
                    } else if (it.orderAddressType == "custom") {
                        // 分时预约数据
//                        if (BaseApplication.appArea == "sc") {
                        setReseationInfo(it, it.orderAddressType)
//                        } else {
//                            fl_scenic_reservation.visibility = View.GONE
//                        }
                    } else if (it.orderAddressType == "ds") {
                        // 电商订单

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

        // 组装跳转景点详情的数据
        scenicTags = ScenicTags("", "", "")
        if (!it.level.isNullOrEmpty()) {
            scenicTags!!.level = it.level
        }
        if (!it.theme.isNullOrEmpty()) {
            var tags = DividerTextUtils.convertDotString(it.theme)
            scenicTags!!.tags = tags
        }


        // 听解说
        var audios: MutableList<AudioInfo> = mutableListOf()
        // 金牌解说
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

        // 活动
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

        // 评论数目
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
            // 收藏
            setCollectUi(it.vipResourceStatus.collectionStatus)
            // 点赞
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
        // 设置景点数据
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

        mBinding.tvScenicToOrder.text = "景区预约"
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
        // 视频启动播放
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
            // 处理视频播发器全屏问题
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