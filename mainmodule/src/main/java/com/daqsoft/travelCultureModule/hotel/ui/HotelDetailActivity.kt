package com.daqsoft.travelCultureModule.hotel.ui

import android.content.Intent
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.view.Gravity
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.view.OptionsPickerView
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.*
import com.daqsoft.baselib.utils.file.DownLoadFileUtil
import com.daqsoft.baselib.utils.file.ShareDownLoadFileUtil
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.baselib.widgets.dialog.QrCodeDialog
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityHotelDetailBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.ZARouterPath.HOTEL_INFO_MORE
import com.daqsoft.provider.ZARouterPath.ZMAIN_HOTEL_DETAIL
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.bean.HotelDetailBean
import com.daqsoft.provider.businessview.adapter.ProviderActivityAdapter
import com.daqsoft.provider.businessview.event.LoginStatusEvent
import com.daqsoft.provider.businessview.event.UpdateAudioStateEvent
import com.daqsoft.provider.businessview.event.UpdateCommentEvent
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.businessview.view.ListenerAudioView
import com.daqsoft.provider.businessview.view.OrderAddressPopWindow
import com.daqsoft.provider.businessview.view.ProviderRecommendView
import com.daqsoft.provider.electronicBeans.ProductBean
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.net.StatisticsRepository
import com.daqsoft.provider.network.venues.bean.AudioInfo
import com.daqsoft.provider.network.venues.bean.ScenicLabelBean
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.provider.utils.MapNaviUtils
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.provider.view.convenientbanner.bean.VideoImageBean
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.convenientbanner.holder.VideoImageHolder
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import com.daqsoft.provider.view.popupwindow.SharePopWindow
import com.daqsoft.travelCultureModule.hotel.view.HotelRoomPopWindow
import com.daqsoft.travelCultureModule.hotel.view.HotelSelectRoomView
import com.daqsoft.travelCultureModule.hotel.view.RouteOrderView
import com.daqsoft.travelCultureModule.hotel.viewmodel.HotelDetailViewModel
import com.daqsoft.travelCultureModule.resource.adapter.ScenicLabelAdapter
import com.daqsoft.travelCultureModule.resource.view.RouterPopWindow
import com.daqsoft.travelCultureModule.sidetour.SideTourMapActivity
import com.umeng.socialize.UMShareAPI
import kotlinx.android.synthetic.main.activity_hotel_detail.*
import me.nereo.multi_image_selector.BigImgActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList


/**
 * @Description 资源--安逸住酒店列表
 * @ClassName   HotelListActivity
 * @Author      PuHua
 * @Time        2020/2/24 9:49
 */
@Route(path = ZMAIN_HOTEL_DETAIL)
class HotelDetailActivity : TitleBarActivity<ActivityHotelDetailBinding, HotelDetailViewModel>() {


    @JvmField
    @Autowired
    var id: String = "0"

    /**
     * 酒店详情实体
     */
    var hotelDetailBean: HotelDetailBean? = null

    /**
     * 活动适配器
     */
    var actvityAdapter: ProviderActivityAdapter? = null

    /**
     * 房间人数选择弹窗
     */
    private var contextPublicWindow: OptionsPickerView<String>? = null

    /**
     * 酒店房间详情
     */
    private var hotelRoomPopWindow: HotelRoomPopWindow? = null

    var isHaveVide: Boolean = false
    var isHave720: Boolean = false

    /**
     * 二维码dialog
     */
    var mQrCodeDialog: QrCodeDialog? = null

    /**
     * 开始时间
     */
    var startTime: String = ""

    /**
     * 结束时间
     */
    var endTime: String = ""

    /**
     * 房间数目
     */
    var roomNum: String = "1"

    /**
     * 预订须知
     */
    var routerPopWindow: RouterPopWindow? = null

    /**
     * 酒店预订
     */
    var orderAddressPopWindow: OrderAddressPopWindow? = null

    /**
     * 分享弹窗
     */
    var sharePopWindow: SharePopWindow? = null

    private var hotelLat: Double = 0.0
    private var hotelLng: Double = 0.0
    override fun getLayout(): Int = R.layout.activity_hotel_detail

    override fun injectVm(): Class<HotelDetailViewModel> {
        return HotelDetailViewModel::class.java
    }

    override fun setTitle(): String {
        return "酒店详情"
    }

    override fun initView() {
        EventBus.getDefault().register(this)

        startTime = DateUtil.getHotelDateString(Date())
        endTime = DateUtil.getNextHotelDateString(Date())

        actvityAdapter = ProviderActivityAdapter(this@HotelDetailActivity)
        mBinding.rvHotelDetailActivities.layoutManager = LinearLayoutManager(
            this@HotelDetailActivity,
            LinearLayoutManager.VERTICAL, false
        )
        mBinding.rvHotelDetailActivities.adapter = actvityAdapter
        initViewModel()
        initViewEvent()
    }

    private fun initViewEvent() {
        mBinding.vHotelDetailAudios?.onPlayerListener =
            object : ListenerAudioView.OnAudioPlayerListener {
                override fun onStartPlayer() {
                    mBinding.cbrHotelDetail.pauseVideoPlayer()
                }
            }
        mBinding.prvHotelDetail.onItemClickTabListener =
            object : ProviderRecommendView.OnItemClickTabListener {
                override fun getMapResourceRecommend(type: String) {
                    if (!hotelDetailBean!!.latitude.isNullOrEmpty() && !hotelDetailBean!!.longitude.isNullOrEmpty()) {
                        mModel.gethMapRecList(
                            type,
                            id.toString(),
                            hotelDetailBean!!.latitude,
                            hotelDetailBean!!.longitude
                        )
                    }
                }

            }
        mBinding.tvStatus.onNoDoubleClick {
            ARouter.getInstance().build(HOTEL_INFO_MORE)
                .withInt("id", id.toInt())
                .navigation()
        }
        mBinding.txtHotelDetailPannaor.onNoDoubleClick {
            if (isHaveVide) {
                mBinding.cbrHotelDetail.setCurrentItem(1, true)
            } else {
                mBinding.cbrHotelDetail.setCurrentItem(0, true)
            }
        }
        mBinding.txtHotelDetailImages.onNoDoubleClick {
            var pos = 0
            if (isHaveVide) {
                pos += 1
            }
            if (isHave720) {
                pos += 1
            }
            try {
                mBinding.cbrHotelDetail.setCurrentItem(pos, true)
            } catch (e: Exception) {
            }

        }

        mBinding.tvHotelDetailsAddressValue.onNoDoubleClick {
            if (hotelDetailBean != null && !hotelDetailBean?.latitude.isNullOrEmpty() && !hotelDetailBean?.longitude.isNullOrEmpty()) {
                if (MapNaviUtils.isGdMapInstalled()) {
                    MapNaviUtils.openGaoDeNavi(
                        BaseApplication.context,
                        0.0,
                        0.0,
                        null,
                        hotelDetailBean!!.latitude.toDouble(),
                        hotelDetailBean!!.longitude.toDouble(),
                        hotelDetailBean!!.regionName
                    )
                } else {
                    mModel.toast.postValue("非常抱歉，系统未安装地图软件")
                }
            } else {
                mModel.toast.postValue("非常抱歉，暂无位置信息")
            }
        }
        mBinding.tvHotelDetailsPhone.onNoDoubleClick {
            hotelDetailBean?.let {
                SystemHelper.callPhone(this, it.phone)
            }
        }
        mBinding.tvWexQrcode.onNoDoubleClick {
            dealShowQrCode()
        }
        mBinding.tvHotelWebsiteValue.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.Provider.WEB_ACTIVITY)
                .withString("mTitle", hotelDetailBean?.name)
                .withString("html", StringUtil.formatHtmlUrl(hotelDetailBean?.websiteUrl))
                .navigation()
        }
        mBinding.llHotelDetailsParking.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_SIDE_TOUR)
                .withInt(SideTourMapActivity.TAB_POS, SideTourMapActivity.TAB_PARK)
                .withDouble("lat", hotelLat)
                .withDouble("lon", hotelLng)
                .navigation()
        }
        mBinding.llHotelDetailsBathroom.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_SIDE_TOUR)
                .withInt(SideTourMapActivity.TAB_POS, SideTourMapActivity.TAB_TOILET)
                .withDouble("lat", hotelLat)
                .withDouble("lon", hotelLng)
                .navigation()
        }
        mBinding.tvHotelDetailCommentNum.onNoDoubleClick {
//            ARouter.getInstance().build(ARouterPath.Provider.PROVIDER_POST_COMMENT)
//                .withString("id", id.toString())
//                .withString("type", ResourceType.CONTENT_TYPE_HOTEL)
//                .withString("contentTitle", hotelDetailBean!!.name)
//                .navigation()
            if (AppUtils.isLogin()) {
                ARouter.getInstance().build(ARouterPath.Provider.PROVIDER_POST_COMMENT)
                    .withString("id", id.toString())
                    .withString("type", ResourceType.CONTENT_TYPE_HOTEL)
                    .withString("contentTitle", hotelDetailBean!!.name)
                    .navigation()
            } else {
                ToastUtils.showUnLoginMsg()
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }

        }
        mBinding.tvHotelDetailCollect.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                if (hotelDetailBean != null && hotelDetailBean!!.vipResourceStatus != null) {
                    showLoadingDialog()
                    if (hotelDetailBean!!.vipResourceStatus!!.collectionStatus) {
                        mModel.collectionCancel(id.toString())
                    } else {
                        mModel.collection(id.toString())
                    }
                }
            } else {
                ToastUtils.showMessage("该操作需要登录，请先登录")
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
        mBinding.tvHotelDetailThumb.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                if (hotelDetailBean != null && hotelDetailBean!!.vipResourceStatus != null) {
                    showLoadingDialog()
                    if (hotelDetailBean!!.vipResourceStatus!!.likeStatus) {
                        mModel.thumbCancell(id.toString())
                    } else {
                        mModel.thumbUp(id.toString())
                    }
                }
            } else {
                ToastUtils.showMessage("该操作需要登录，请先登录")
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
        mBinding.hotelSelectRoomInfo.onclickDateListener =
            object : HotelSelectRoomView.OnClickSelectDateListener {
                override fun onClick(startDate: String?, endDate: String?) {
                    if (hotelDetailBean != null) {
                        mModel.getHotelRoomLs(
                            hotelDetailBean!!.resourceCode,
                            roomNum.toInt(),
                            endDate!!,
                            startDate!!,
                            hotelDetailBean!!.sysCode
                        )
                    }
                }

                override fun onSelectRoom() {
                    if (contextPublicWindow == null) {
                        initSeleceHotelRoomWindow()
                    }
                    UIHelperUtils.showOptionsPicker(this@HotelDetailActivity, contextPublicWindow!!)
                }

                override fun onShowRoomInfo(inTime: String, outTime: String, roomSn: String) {
                    showLoadingDialog()
                    mModel.getHotelRoomDetail(outTime, inTime, roomSn)
                }

            }
        mBinding.llHotelDetailsBus.onNoDoubleClick {
            // 公交查询
            ARouter.getInstance().build(ARouterPath.ServiceModule.SERVICE_QUERY_BUS_ACTIVITY)
                .navigation()
        }
        mBinding.llHotelDetailsComplaint.onNoDoubleClick {
            // 旅游投诉
            MenuJumpUtils.gotoComplaint()
//            ARouter.getInstance().build(MainARouterPath.MAIN_COMPLAINT_ACTIVITY)
//                .navigation()
        }

    }

    override fun initCustomTitleBar(mTitleBar: TitleBar) {
        showShareButton(com.daqsoft.provider.R.mipmap.common_nav_button_share)
        setShareClick(object : View.OnClickListener {
            override fun onClick(v: View?) {
                hotelDetailBean?.let {
                    if (sharePopWindow == null) {
                        sharePopWindow = SharePopWindow(this@HotelDetailActivity)
                    }
                    var content= Constant.SHARE_DEC
                    if(!TextUtils.isEmpty(it.summary)){
                        content=it.summary
                    }
                    sharePopWindow?.setShareContent(
                        it.name, content, it.images.getRealImages(),
                        ShareModel.getHotelDetailUrl(id)
                    )
                    if (!sharePopWindow!!.isShowing) {
                        sharePopWindow?.showAsDropDown(mTitleBar)
                    }
                }
            }

        })
    }

    private fun initViewModel() {
        // 酒店详情
        mModel.hotelDetailLiveData.observe(this, Observer {
            bindHotelDetail(it)
            doLocation(ResourceType.CONTENT_TYPE_SCENERY)
        })
        mModel.refreshDetailLiveData.observe(this, Observer {
            if (it != null) {
                hotelDetailBean = it
                // 点赞关注
                if (it.vipResourceStatus != null) {
                    // 收藏
                    setCollectUi(it.vipResourceStatus.collectionStatus)
                    // 点赞
                    setThumbUi(it.vipResourceStatus.likeStatus)
                }
                if (!it.collectionNum.isNullOrEmpty() && it.collectionNum != "0") {
                    mBinding.tvHotelDetailCollect.text = "${it.collectionNum}"
                }
                if (!it.likeNum.isNullOrEmpty() && it.likeNum != "0") {
                    mBinding.tvHotelDetailThumb.text = "${it.likeNum}"
                }
            }
        })
        // 周边推荐
        mModel.mapResLiveData.observe(this, Observer {
            if (it != null) {
                mBinding.prvHotelDetail.setData(it.type, it.datas)
            }
        })
        // 评论
        mModel.commentBeans.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.pcvHotelDetailComments.visibility = View.VISIBLE
                mBinding.pcvHotelDetailComments.setData(it)
            } else {
                mBinding.pcvHotelDetailComments.visibility = View.GONE
            }
        })
        // 故事类别
        mModel.storyList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.psvHotelStories.visibility = View.VISIBLE
//                mBinding.psvHotelStories.setData(it)
                mBinding.psvHotelStories.setDataNumber(id,ResourceType.CONTENT_TYPE_HOTEL,it,mModel.storyNumber)
            } else {
                mBinding.psvHotelStories.visibility = View.GONE
            }
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
            if (it != null && it.requestCode == 1) {
                // 房间
                mBinding.hotelSelectRoomInfo.visibility = View.GONE
            }
        })
        // 收藏
        mModel.colllectLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            setCollectUi(it)
            hotelDetailBean!!.vipResourceStatus?.collectionStatus = it
            var temp = hotelDetailBean!!.collectionNum
            if (!temp.isNullOrEmpty() && temp != "NULL") {
                var collectNum: Int = temp.toInt()
                if (it) {
                    // 收藏成功
                    val result = collectNum + 1
                    hotelDetailBean!!.collectionNum = result.toString()
                    mBinding.tvHotelDetailCollect.text = result.toString()
                } else {
                    // 取消收藏
                    if (collectNum > 0) {
                        val result = collectNum - 1
                        hotelDetailBean!!.collectionNum = result.toString()
                        if (result > 0) {
                            mBinding.tvHotelDetailCollect.text = result.toString()
                        } else {
                            mBinding.tvHotelDetailCollect.text = "收藏"
                        }
                    }
                }
            }

        })
        // 点赞
        mModel.thumbLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            setThumbUi(it)
            hotelDetailBean!!.vipResourceStatus?.likeStatus = it
            var temp = hotelDetailBean!!.likeNum
            if (!temp.isNullOrEmpty() && temp != "NULL") {
                var likeNum: Int = temp.toInt()
                if (it) {
                    // 点赞成功
                    val result = likeNum + 1
                    hotelDetailBean!!.likeNum = result.toString()
                    mBinding.tvHotelDetailThumb.text = result.toString()
                } else {
                    // 取消点赞
                    if (likeNum > 0) {
                        val result = likeNum - 1
                        hotelDetailBean!!.likeNum = result.toString()
                        if (result > 0) {
                            mBinding.tvHotelDetailThumb.text = result.toString()
                        } else {
                            mBinding.tvHotelDetailThumb.text = "点赞"
                        }
                    }
                }
            }
        })
        mModel.hotelRoomsLiveData.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.hotelSelectRoomInfo.updateData(it)
            } else {
                mBinding.hotelSelectRoomInfo.updateData(null)
            }
        })
        mModel.hotelRoomInfoLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            if (it != null) {
                if (hotelRoomPopWindow == null) {
                    hotelRoomPopWindow = HotelRoomPopWindow(this@HotelDetailActivity)
                }
                hotelRoomPopWindow?.updateData(it)

                hotelRoomPopWindow?.showAtLocation(mBinding.root, Gravity.BOTTOM, 0, 0)
            } else {
                ToastUtils.showMessage("非常抱歉，未找到详情信息~")
            }
        })
        // 线路须知
        mModel.routerReservationLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            if (it != null) {
                if (routerPopWindow == null) {
                    routerPopWindow = RouterPopWindow(this@HotelDetailActivity)
                }
                routerPopWindow!!.updateData(it)
                routerPopWindow!!.showAtLocation(mBinding.root, Gravity.BOTTOM, 0, 0)
            }
        })
        // 线路
        mModel.routeResult.observe(this, Observer {
            dissMissLoadingDialog()
            if (it != null && !it.productListVO.isNullOrEmpty()) {
                mBinding?.hotelRouterLs.visibility = View.VISIBLE
                mBinding.hotelRouterLs.setData(it.productListVO as MutableList<ProductBean>)
                mBinding.hotelRouterLs?.onRouterViewListener =
                    object : RouteOrderView.OnRouterViewListener {
                        override fun onGetRouterViewListener(snCode: String, name: String) {
                            showLoadingDialog()
                            mModel.getRouterReservationInfo(snCode, name)
                        }

                    }
            } else {
                mBinding?.hotelRouterLs.visibility = View.GONE
            }
        })
        mModel.orderAddresInfoLiveData.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                // 显示预订按钮
                mBinding?.tvHotelToOrder.visibility = View.VISIBLE
                mBinding?.tvHotelToOrder.onNoDoubleClick {
                    if (it.size > 1) {
                        // 显示弹窗
                        var location: IntArray = IntArray(2)
                        mBinding.vMainHotelDetailBottom.getLocationOnScreen(location);
                        if (orderAddressPopWindow == null) {
                            orderAddressPopWindow = OrderAddressPopWindow(this@HotelDetailActivity)
                            orderAddressPopWindow!!.updatePopWindowHeight(location[1])
                        }
                        orderAddressPopWindow?.updateData(it)
                        if (!orderAddressPopWindow!!.isShowing) {

                            orderAddressPopWindow?.showAtLocation(
                                mBinding.vMainHotelDetailBottom,
                                Gravity.NO_GRAVITY,
                                0,
                                0
                            )
                        }
                    } else {
                        // 直接跳转
                        ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
                            .withString("mTitle", "详情")
                            .withString("html", it[0].url)
                            .navigation()
                    }
                }
            } else {
                mBinding?.tvHotelToOrder.visibility = View.GONE
            }
        })
    }

    private fun bindHotelDetail(data: HotelDetailBean?) {
        dissMissLoadingDialog()
        if (data == null) {
            ToastUtils.showMessage("暂未找到酒店信息，请稍后再试~")
            finish()
            return
        }
        mBinding.hotelCoorToolBar.visibility = View.VISIBLE
        mBinding.bean = data!!
        hotelDetailBean = data!!
        StatisticsRepository.instance.statisticsPage(data.name, 1)
        if (!data.orderAddressType.isNullOrEmpty()) {
            if (data.orderAddressType == "pt") {
                // 获取下单地址
                mModel.getOrderAddressInfo(id.toString())
            }
        }
        // 获取酒店客房
        if (!hotelDetailBean!!.sysCode.isNullOrEmpty() && !hotelDetailBean!!.shopUrl.isNullOrEmpty() && !hotelDetailBean!!.resourceCode.isNullOrEmpty()) {
            mBinding.hotelSelectRoomInfo.visibility = View.VISIBLE
            mModel.getHotelRoomLs(
                hotelDetailBean!!.resourceCode,
                roomNum.toInt(),
                endTime,
                startTime,
                hotelDetailBean!!.sysCode
            )
            mModel.sysCode = hotelDetailBean!!.sysCode
            mModel.resourceCode = hotelDetailBean!!.resourceCode
            mModel.getRouteList()
            mBinding.hotelSelectRoomInfo.setHotelShopInfo(
                hotelDetailBean!!.shopName,
                hotelDetailBean!!.shopUrl
            )
            mBinding.hotelSelectRoomInfo.setHotelRoomParam(startTime, endTime)
        } else {
            mBinding.hotelSelectRoomInfo.visibility = View.GONE
        }

        data?.let {
            if (!it.name.isNullOrEmpty()) {
                setTitleContent(it.name)
            }

            // 标签处理
            if (!it.hotelLevel.isNullOrEmpty() || !it.type.isNullOrEmpty()) {
                val tags = mutableListOf<ScenicLabelBean>()
                if (!it.hotelLevel.isNullOrEmpty()) {
                    var levels = it.hotelLevel?.split(",")
                    if (!levels.isNullOrEmpty()) {
                        for (item in levels) {
                            if (!item.isNullOrEmpty() && item != "无星级") {
                                tags.add(ScenicLabelBean(item, 1))
                            }
                        }
                    }
                }
                if (!it.type.isNullOrEmpty()) {
                    for (i in it.type)
                        tags.add(ScenicLabelBean(i!!, 3))
                }
                val scenicLabelAdapter = ScenicLabelAdapter(BaseApplication.context)
                scenicLabelAdapter.emptyViewShow = false
                mBinding.recyclerHotelDetailsLabel.layoutManager = LinearLayoutManager(
                    BaseApplication.context!!, LinearLayoutManager.HORIZONTAL,
                    false
                )
                mBinding.recyclerHotelDetailsLabel.adapter = scenicLabelAdapter
                scenicLabelAdapter!!.add(tags)
                mBinding.recyclerHotelDetailsLabel.visibility = View.VISIBLE
            } else {
                mBinding.recyclerHotelDetailsLabel.visibility = View.GONE
            }
            var spanstatusInfo = StringBuilder("")
            if (!it.cutRegionName.isNullOrEmpty()) {
                spanstatusInfo.append(it.cutRegionName)
            }
            if (!it.checkInTime.isNullOrEmpty()) {
                spanstatusInfo.append(" | 入住时间：" + it.checkInTime + "以后")
            }
            if (!it.checkInTime.isNullOrEmpty()) {
                spanstatusInfo.append(" | 退房时间：" + it.checkOutTime + "以前")
            }
            try {
                var moreInfo = "更多信息  >"
                var status = spanstatusInfo.toString() + moreInfo
                var spanStatus = SpannableString(status)
                spanStatus.setSpan(
                    ForegroundColorSpan(resources.getColor(R.color.colorPrimary)),
                    status.length - moreInfo.length,
                    status.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
//                var d: Drawable = getResources().getDrawable(R.mipmap.more_right_arrow)
//                d.setBounds(
//                    0, 0, Utils.dip2px(this, 4.5f).toInt(), Utils.dip2px(this, 9f).toInt()
//                )
//                var imageSpan = ImageSpan(d, ImageSpan.ALIGN_BASELINE)
//                spanStatus.setSpan(imageSpan, status.length - 3, status.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                mBinding.tvStatus.text = spanStatus
            } catch (e: Exception) {
            }


            var dataVideoImages: MutableList<VideoImageBean> = mutableListOf()
            //视频
            if (!it.video.isNullOrEmpty()) {
                dataVideoImages.add(0, VideoImageBean().apply {
                    type = 1
                    videoUrl = it.video
                })
                isHaveVide = true
                mBinding.txtHotelDetailVideo.visibility = View.GONE
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
                mBinding.txtHotelDetailPannaor.visibility = View.VISIBLE
            }
            //图片
            if (!images.isNullOrEmpty()) {
                for (item in images) {
                    dataVideoImages.add(VideoImageBean().apply {
                        type = 0
                        imageUrl = item
                    })
                }
                mBinding.txtHotelDetailImages.visibility = View.VISIBLE
                mBinding.txtHotelDetailImages.text = "1/${images.size}"
            }
            if (!dataVideoImages.isNullOrEmpty()) {
                mBinding.cbrHotelDetail.visibility = View.VISIBLE
                mBinding.cbrHotelDetail.setPages(object : CBViewHolderCreator {
                    override fun createHolder(itemView: View?): Holder<*> {
                        return VideoImageHolder(itemView!!, this@HotelDetailActivity)
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
                                    Intent(this@HotelDetailActivity, BigImgActivity::class.java)
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
                mBinding.cbrHotelDetail.onPageChangeListener = object : OnPageChangeListener {
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
                                mBinding.txtHotelDetailImages.text = "${pos + 1}/${images?.size}"
                            }
                        }
                    }

                    override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                    }

                }
            } else {
                mBinding.cbrHotelDetail.visibility = View.GONE
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
                })
            }

            if (!it.audioInfo.isNullOrEmpty()) {
                audios.addAll(it.audioInfo)
            }
            if (!audios.isNullOrEmpty()) {
                mBinding.vHotelDetailAudios.visibility = View.VISIBLE
                mBinding.vHotelDetailAudios.setData(audios)
            } else {
                mBinding.vHotelDetailAudios.visibility = View.GONE
            }
            // 评论数目
            if (!it.commentNum.isNullOrEmpty() && it.commentNum != "NULL") {
                mBinding.pcvHotelDetailComments.updateCommentNum(
                    it.commentNum.toInt(),
                    id.toString(),
                    ResourceType.CONTENT_TYPE_HOTEL,
                    hotelDetailBean!!.name
                )
                if (it.commentNum.toInt() > 0) {
                    mBinding.tvHotelDetailCommentNum.text = "${it.commentNum}"
                }
            }
            // 点赞关注
            if (it.vipResourceStatus != null) {
                // 收藏
                setCollectUi(it.vipResourceStatus.collectionStatus)
                // 点赞
                setThumbUi(it.vipResourceStatus.likeStatus)
            }
            if (!it.collectionNum.isNullOrEmpty() && it.collectionNum != "0") {
                mBinding.tvHotelDetailCollect.text = "${it.collectionNum}"
            }
            if (!it.likeNum.isNullOrEmpty() && it.likeNum != "0") {
                mBinding.tvHotelDetailThumb.text = "${it.likeNum}"
            }

            // 活动
            if (!it.activity.isNullOrEmpty()) {
                try {
                    actvityAdapter?.clear()
                    actvityAdapter?.add(it.activity as MutableList<ActivityBean>)
                    mBinding.vm?.hideActivity?.set(false)
                    il_activity.visibility = View.VISIBLE
                    mBinding?.rvHotelDetailActivities.visibility = View.VISIBLE
                } catch (e: Exception) {
                }
            } else {
                mBinding.vm?.hideActivity?.set(true)
                il_activity.visibility = View.GONE
                mBinding?.rvHotelDetailActivities.visibility = View.GONE
            }
            // 周边推荐
            if (!it.latitude.isNullOrEmpty() && !it.longitude.isNullOrEmpty()) {
                hotelLat = it.latitude.toDouble()
                hotelLng = it.longitude.toDouble()
                mBinding?.prvHotelDetail?.setLocation(
                    LatLng(
                        it.latitude.toDouble(),
                        it.longitude.toDouble()
                    )
                )
                mModel.gethMapRecList(
                    ResourceType.CONTENT_TYPE_RESTAURANT,
                    id.toString(),
                    it.latitude,
                    it.longitude
                )
                mBinding?.prvHotelDetail?.visibility = View.VISIBLE
            } else {
                mBinding?.prvHotelDetail?.visibility = View.GONE
            }
        }

    }

    override fun initData() {
        mModel.refreshUserInfo(id)
        mModel.getActivityCommentList(id)
        mModel.getStoryList(id, ResourceType.CONTENT_TYPE_HOTEL)

    }

    override fun onResume() {
        super.onResume()
        mBinding.cbrHotelDetail.startTurning(3000)
    }

    override fun onPause() {
        super.onPause()
        mBinding.cbrHotelDetail.pauseVideoPlayer()

        mBinding.cbrHotelDetail.stopTurning()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            EventBus.getDefault().unregister(this)
            mBinding?.cbrHotelDetail?.stopVideoPlayer()
            mBinding.vHotelDetailAudios?.stopAudioPlayer()
            mBinding.vHotelDetailAudios?.releaseAudioPlayer()
            orderAddressPopWindow = null
            contextPublicWindow = null
            mQrCodeDialog = null
            StatisticsRepository.instance.statisticsPage(hotelDetailBean?.name, 2)
            GaoDeLocation.getInstance().release()
        } catch (e: Exception) {
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateAudioPlayer(event: UpdateAudioStateEvent) {
        // 1 准备播放 2 播放 3暂停/完成
        try {
            when (event.type) {
                1 -> {
                    mBinding?.cbrHotelDetail.stopTurning()
                    mBinding?.vHotelDetailAudios?.pauseAudioPlayer()
                }
                2 -> {
                    mBinding?.cbrHotelDetail.stopTurning()
                    mBinding?.vHotelDetailAudios?.pauseAudioPlayer()
                }
                3 -> {
                    mBinding?.cbrHotelDetail.startTurning(3000)
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
//                mBinding.prvHotelDetail?.setLocation(LatLng(lat_, lon_))
//                mModel.gethMapRecList(type, id.toString())
            }

            override fun onError(errormsg: String) {
            }
        })
    }

    /**
     * 点赞
     */
    private fun setThumbUi(it: Boolean) {
        if (it) {
            // 点赞成功
            var drawable =
                resources.getDrawable(com.daqsoft.mainmodule.R.mipmap.bottom_icon_like_selected)
            mBinding.tvHotelDetailThumb.setCompoundDrawablesWithIntrinsicBounds(
                null,
                drawable,
                null,
                null
            )
        } else {
            // 取消点赞
            var drawable =
                resources.getDrawable(com.daqsoft.mainmodule.R.mipmap.bottom_icon_like_normal)
            mBinding.tvHotelDetailThumb.setCompoundDrawablesWithIntrinsicBounds(
                null,
                drawable,
                null,
                null
            )
        }
    }

    /**
     * 收藏
     */
    private fun setCollectUi(it: Boolean) {
        if (it) {
            // 收藏
            var collect =
                resources.getDrawable(com.daqsoft.mainmodule.R.mipmap.bottom_icon_collect_selected)
            mBinding.tvHotelDetailCollect.setCompoundDrawablesWithIntrinsicBounds(
                null,
                collect,
                null,
                null
            )
        } else {
            // 取消收藏
            var collect =
                resources.getDrawable(com.daqsoft.mainmodule.R.mipmap.bottom_icon_collect_normal)
            mBinding.tvHotelDetailCollect.setCompoundDrawablesWithIntrinsicBounds(
                null,
                collect,
                null,
                null
            )
        }
    }

    /**
     * 处理二维码显示
     */
    private fun dealShowQrCode() {
        if (hotelDetailBean != null && !hotelDetailBean!!.officialUrl.isNullOrEmpty()) {
            if (mQrCodeDialog == null) {
                mQrCodeDialog = QrCodeDialog.Builder().qrCodeImageUrl(hotelDetailBean!!.officialUrl)
                    .title(hotelDetailBean!!.officialName)
                    .onDownLoadListener(object : QrCodeDialog.OnDownLoadListener {
                        override fun onDownLoadImage(url: String) {
                            try {
                                showLoadingDialog()
                                DownLoadFileUtil.downNetworkImage(
                                    url,
                                    object : DownLoadFileUtil.DownImageListener {
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
                    .build(this@HotelDetailActivity)
            } else {
                mQrCodeDialog?.updateData(hotelDetailBean!!.officialUrl, hotelDetailBean!!.officialName)
            }
            mQrCodeDialog!!.show()
        }
    }

    fun initSeleceHotelRoomWindow() {
        var complaintPublic = resources.getStringArray(R.array.hotel_room)
        contextPublicWindow = OptionsPickerBuilder(
            this@HotelDetailActivity,
            OnOptionsSelectListener { s1, s2, s3, v ->
                val roomNum = complaintPublic[s1]
                mBinding.hotelSelectRoomInfo.setTxtHotelRoomNum("${roomNum}间", roomNum)
                if (hotelDetailBean != null) {
                    mModel.getHotelRoomLs(
                        hotelDetailBean!!.resourceCode,
                        roomNum.toInt(),
                        endTime,
                        startTime,
                        hotelDetailBean?.sysCode!!
                    )
                }
            }).build<String>()
        contextPublicWindow!!.setPicker(complaintPublic.asList())
    }

    override fun onBackPressed() {
        var isBackPress: Boolean = false
        if (hotelRoomPopWindow?.onbackPress() != null) {
            isBackPress = hotelRoomPopWindow?.onbackPress()!!
        }
        if (!mBinding.cbrHotelDetail?.onBackPress() || !isHaveVide || !isBackPress) {
            super.onBackPressed()
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun updateDataStatus(event: LoginStatusEvent) {
        if (event != null) {
            mModel?.refreshHotelDetail(id.toString())
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRecordEvent(event: UpdateCommentEvent) {
        mModel.getActivityCommentList(id.toString())
        mModel.getHotelDetail(id.toString())
    }
}

