package com.daqsoft.travelCultureModule.food

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.location.Location
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.*
import com.daqsoft.baselib.utils.file.DownLoadFileUtil
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.baselib.widgets.dialog.QrCodeDialog
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityFoodDetailBinding
import com.daqsoft.provider.*
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.bean.FoodDetailBean
import com.daqsoft.provider.businessview.adapter.ProviderActivityAdapter
import com.daqsoft.provider.businessview.event.LoginStatusEvent
import com.daqsoft.provider.businessview.event.UpdateAudioStateEvent
import com.daqsoft.provider.businessview.event.UpdateCommentEvent
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.businessview.view.ListenerAudioView
import com.daqsoft.provider.businessview.view.ProviderRecommendView
import com.daqsoft.provider.network.venues.bean.AudioInfo
import com.daqsoft.provider.network.venues.bean.ScenicLabelBean
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.provider.utils.AddressUtil
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.provider.utils.MapNaviUtils
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.provider.view.convenientbanner.bean.VideoImageBean
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.convenientbanner.holder.VideoImageHolder
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import com.daqsoft.provider.view.popupwindow.SharePopWindow
import com.daqsoft.travelCultureModule.food.viewmodel.FoodDetailViewModel
import com.daqsoft.travelCultureModule.resource.adapter.ScenicLabelAdapter
import com.daqsoft.travelCultureModule.sidetour.SideTourMapActivity
import kotlinx.android.synthetic.main.activity_hotel_detail.*
import me.nereo.multi_image_selector.BigImgActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @Description 美食详情
 * @ClassName   FoodDetailActivity
 * @Author      luoyi
 * @Time        2020/4/11 9:16
 */
@Route(path = MainARouterPath.MAIN_FOOD_DETAIL)
class FoodDetailActivity : TitleBarActivity<ActivityFoodDetailBinding, FoodDetailViewModel>() {
    @JvmField
    @Autowired
    var id: String = "0"

    /**
     * 酒店详情实体
     */
    var foodDetailBean: FoodDetailBean? = null

    var maxWebViewHeight: Int = 0
    /**
     * 文本内容高度
     */
    var webViewHeight = 0
    /**
     * 活动适配器
     */
    var actvityAdapter: ProviderActivityAdapter? = null
    var isHaveVide: Boolean = false
    var isHave720: Boolean = false

    /**
     * 二维码dialog
     */
    var mQrCodeDialog: QrCodeDialog? = null

    /**
     * 当前位置经纬度
     */
    var selfLocation: LatLng? = null

    /**
     * 参馆经度
     */
    var foodLat: Double = 0.0

    /**
     * 餐馆纬度
     */
    var foodLng: Double = 0.0
    /**
     * 分享弹窗
     */
    var sharePopWindow: SharePopWindow? = null

    override fun getLayout(): Int {
        return R.layout.activity_food_detail
    }

    override fun setTitle(): String {
        return getString(R.string.food_detail)
    }

    override fun injectVm(): Class<FoodDetailViewModel> {
        return FoodDetailViewModel::class.java
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        actvityAdapter = ProviderActivityAdapter(this@FoodDetailActivity)
        mBinding.rvFoodDetailActivities.layoutManager = LinearLayoutManager(
            this@FoodDetailActivity,
            LinearLayoutManager.VERTICAL, false
        )
        mBinding.rvFoodDetailActivities.adapter = actvityAdapter
        initViewModel()
        initViewEvent()
    }
    override fun initCustomTitleBar(mTitleBar: TitleBar) {

        showShareButton(com.daqsoft.provider.R.mipmap.common_nav_button_share)
        setShareClick(View.OnClickListener {
            foodDetailBean?.let {
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
                    ShareModel.getFoodDetailUrl(id)
                )
                if (!sharePopWindow!!.isShowing) {
                    sharePopWindow?.showAsDropDown(mTitleBar)
                }
            }
        })
    }
    private fun initViewEvent() {
        mBinding.vFoodDetailAudios?.onPlayerListener = object : ListenerAudioView.OnAudioPlayerListener {
            override fun onStartPlayer() {
                mBinding.cbrFoodDetail.pauseVideoPlayer()
            }
        }
        mBinding.prvFoodDetail.onItemClickTabListener = object : ProviderRecommendView.OnItemClickTabListener {
            override fun getMapResourceRecommend(type: String) {
                if (foodDetailBean != null && foodDetailBean!!.latitude != null && foodDetailBean!!.longitude != null) {
                    mModel.gethMapRecList(type, id.toString(), foodDetailBean!!.latitude.toString(), foodDetailBean!!.longitude.toString())
                }
            }

        }
        mBinding.tvStatus.onNoDoubleClick {
            if (!id.isNullOrEmpty()) {
                ARouter.getInstance().build(MainARouterPath.MAIN_FOOD_INFO)
                    .withInt("id", id.toInt())
                    .navigation()
            }
        }

        mBinding.tvFoodDetailsAddressValue.onNoDoubleClick {
            if (foodDetailBean != null && foodDetailBean?.latitude != 0.0 && foodDetailBean?.longitude != 0.0) {
                if (MapNaviUtils.isGdMapInstalled()) {
                    MapNaviUtils.openGaoDeNavi(
                        BaseApplication.context, 0.0, 0.0, null,
                        foodDetailBean!!.latitude.toDouble(), foodDetailBean!!.longitude.toDouble(),
                        foodDetailBean!!.regionName
                    )
                } else {
                    mModel.toast.postValue("非常抱歉，系统未安装地图软件")
                }
            } else {
                mModel.toast.postValue("非常抱歉，暂无位置信息")
            }
        }
        mBinding.tvFoodDetailsPhone.onNoDoubleClick {
            foodDetailBean?.let {
                SystemHelper.callPhone(this, it.phone)
            }
        }
        mBinding.tvWexQrcode.onNoDoubleClick {
            dealShowQrCode()
        }
        mBinding.tvFoodWebsiteValue.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.Provider.WEB_ACTIVITY)
                .withString("mTitle", foodDetailBean?.name)
                .withString("html", StringUtil.formatHtmlUrl(foodDetailBean?.websiteUrl))
                .navigation()
        }
        mBinding.llFoodDetailsParking.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_SIDE_TOUR)
                .withInt(SideTourMapActivity.TAB_POS, SideTourMapActivity.TAB_PARK)
                .withDouble("lat", foodLat)
                .withDouble("lon", foodLng)
                .navigation()
        }
        mBinding.llFoodDetailsBathroom.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_SIDE_TOUR)
                .withInt(SideTourMapActivity.TAB_POS, SideTourMapActivity.TAB_TOILET)
                .withDouble("lat", foodLat)
                .withDouble("lon", foodLng)
                .navigation()
        }
        mBinding.tvFoodDetailCommentNum.onNoDoubleClick {
//            ARouter.getInstance().build(ARouterPath.Provider.PROVIDER_POST_COMMENT)
//                .withString("id", id.toString())
//                .withString("type", ResourceType.CONTENT_TYPE_RESTAURANT)
//                .withString("contentTitle", foodDetailBean!!.name)
//                .navigation()
            if (AppUtils.isLogin()) {
                ARouter.getInstance().build(ARouterPath.Provider.PROVIDER_POST_COMMENT)
                .withString("id", id.toString())
                .withString("type", ResourceType.CONTENT_TYPE_RESTAURANT)
                .withString("contentTitle", foodDetailBean!!.name)
                .navigation()
            } else {
                ToastUtils.showUnLoginMsg()
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
        mBinding.tvFoodDetailCollect.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                if (foodDetailBean != null && foodDetailBean!!.vipResourceStatus != null) {
                    showLoadingDialog()
                    if (foodDetailBean!!.vipResourceStatus!!.collectionStatus) {
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
        mBinding.tvFoodDetailThumb.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                if (foodDetailBean != null && foodDetailBean!!.vipResourceStatus != null) {
                    showLoadingDialog()
                    if (foodDetailBean!!.vipResourceStatus!!.likeStatus) {
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
        mBinding.llFoodDetailsBus.onNoDoubleClick {
            // 公交查询
            ARouter.getInstance().build(ARouterPath.ServiceModule.SERVICE_QUERY_BUS_ACTIVITY)
                .navigation()
        }
        mBinding.llFoodDetailsComplaint.onNoDoubleClick {
            // 旅游投诉
            MenuJumpUtils.gotoComplaint()
//            ARouter.getInstance().build(MainARouterPath.MAIN_COMPLAINT_ACTIVITY)
//                .navigation()
        }
        mBinding.txtFoodDetailPannaor.onNoDoubleClick {
            if (isHaveVide) {
                mBinding.cbrFoodDetail.setCurrentItem(1, true)
            } else {
                mBinding.cbrFoodDetail.setCurrentItem(0, true)
            }
        }
        mBinding.txtFoodDetailImages.onNoDoubleClick {
            var pos = 0
            if (isHaveVide) {
                pos += 1
            }
            if (isHave720) {
                pos += 1
            }
            try {
                mBinding.cbrFoodDetail.setCurrentItem(pos, true)
            } catch (e: Exception) {
            }

        }
    }

    private fun initViewModel() {
        // 酒店详情
        mModel.foodDetailLiveData.observe(this, androidx.lifecycle.Observer {
            dissMissLoadingDialog()
            bindHotelDetail(it)

        })
        // 周边推荐
        mModel.mapResLiveData.observe(this, Observer {
            if (it != null) {
                mBinding.prvFoodDetail.setData(it.type, it.datas)
            }
        })
        // 评论
        mModel.commentBeans.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.pcvFoodDetailComments.visibility = View.VISIBLE
                mBinding.pcvFoodDetailComments.setData(it)
            } else {
                mBinding.pcvFoodDetailComments.visibility = View.GONE
            }
        })
        // 故事类别
        mModel.storyList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.psvFoodStories.visibility = View.VISIBLE
//                mBinding.psvFoodStories.setData(it)

                mBinding.psvFoodStories.setDataNumber(id,ResourceType.CONTENT_TYPE_RESTAURANT,it,mModel.storyNumber)
            } else {
                mBinding.psvFoodStories.visibility = View.GONE
            }
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })
        mModel.refreshFoodDetailLiveData.observe(this, Observer {
            if (it != null) {
                foodDetailBean = it
                // 点赞关注
                if (it.vipResourceStatus != null) {
                    // 收藏
                    setCollectUi(it.vipResourceStatus.collectionStatus)
                    // 点赞
                    setThumbUi(it.vipResourceStatus.likeStatus)
                }
                if (!it.collectionNum.isNullOrEmpty() && it.collectionNum != "0") {
                    mBinding.tvFoodDetailCollect.text = "${it.collectionNum}"
                }
                if (!it.likeNum.isNullOrEmpty() && it.likeNum != "0") {
                    mBinding.tvFoodDetailThumb.text = "${it.likeNum}"
                }
            }
        })
        // 收藏
        mModel.colllectLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            setCollectUi(it)
            foodDetailBean!!.vipResourceStatus?.collectionStatus = it
            var temp = foodDetailBean!!.collectionNum
            if (!temp.isNullOrEmpty()) {
                var collectNum: Int = temp.toInt()
                if (it) {
                    // 收藏成功
                    val result = collectNum + 1
                    foodDetailBean!!.collectionNum = result.toString()
                    mBinding.tvFoodDetailCollect.text = result.toString()
                } else {
                    // 取消收藏
                    if (collectNum > 0) {
                        val result = collectNum - 1
                        foodDetailBean!!.collectionNum = result.toString()
                        if (result > 0) {
                            mBinding.tvFoodDetailCollect.text = result.toString()
                        } else {
                            mBinding.tvFoodDetailCollect.text = "收藏"
                        }
                    }
                }
            }

        })
        // 点赞
        mModel.thumbLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            setThumbUi(it)
            foodDetailBean!!.vipResourceStatus?.likeStatus = it
            var temp = foodDetailBean!!.likeNum
            if (!temp.isNullOrEmpty()) {
                var likeNum: Int = temp.toInt()
                if (it) {
                    // 点赞成功
                    val result = likeNum + 1
                    foodDetailBean!!.likeNum = result.toString()
                    mBinding.tvFoodDetailThumb.text = result.toString()
                } else {
                    // 取消点赞
                    if (likeNum > 0) {
                        val result = likeNum - 1
                        foodDetailBean!!.likeNum = result.toString()
                        if (result > 0) {
                            mBinding.tvFoodDetailThumb.text = result.toString()
                        } else {
                            mBinding.tvFoodDetailThumb.text = "点赞"
                        }
                    }
                }
            }
        })
        mModel.foodProductLiveData.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.vFoodProduct.visibility = View.VISIBLE
                mBinding.vFoodProduct.setData(it)
            } else {
                mBinding.vFoodProduct.visibility = View.GONE
            }
        })
        //
    }

    private fun bindHotelDetail(data: FoodDetailBean?) {
        dissMissLoadingDialog()
        if (data == null) {
            ToastUtils.showMessage("暂未找到酒店信息，请稍后再试~")
            finish()
            return
        }

        mBinding.foodCoorToolBar.visibility = View.VISIBLE
        mBinding.bean = data!!
        foodDetailBean = data!!
        data?.let {
            if (!it.name.isNullOrEmpty()) {
                setTitleContent("" + it.name)
            }
            // 标签处理
            if (!it.type.isNullOrEmpty()) {
                val tags = mutableListOf<ScenicLabelBean>()
                for (i in it.type)
                    tags.add(ScenicLabelBean(i!!, 3))
                val scenicLabelAdapter = ScenicLabelAdapter(BaseApplication.context)
                mBinding.recyclerFoodDetailsLabel.layoutManager = LinearLayoutManager(
                    BaseApplication.context!!, LinearLayoutManager.HORIZONTAL,
                    false
                )
                mBinding.recyclerFoodDetailsLabel.adapter = scenicLabelAdapter
                scenicLabelAdapter!!.add(tags)
                mBinding.recyclerFoodDetailsLabel.visibility = View.VISIBLE
            } else {
                mBinding.recyclerFoodDetailsLabel.visibility = View.GONE
            }
            var status = DividerTextUtils.convertString(
                java.lang.StringBuilder(), if (it.cutRegionName.isNullOrEmpty()) {
                    ""
                } else {
                    it.cutRegionName
                }, if (it.openTime.isNullOrEmpty()) {
                    ""
                } else {
                    String.format(getString(R.string.food_ls_opentime, it.openTime))
                },if (it.roomNum.isNullOrEmpty()) {
                    ""
                } else {
                    "包厢数量：${it.roomNum}间"
                },if (it.consumPerson.isNullOrEmpty()) {
                    ""
                } else {
                    "人均消费：${it.consumPerson}/人"
                }
            )


            if(it.introduce.isNullOrEmpty()){
                mBinding.llInfor.visibility==View.GONE
                mBinding.webInfor.visibility==View.GONE
                mBinding.ivContentMore.visibility==View.GONE
                mBinding.ilFoodIntrouduce.hide=true
            }else{
                mBinding.llInfor.visibility==View.VISIBLE
                mBinding.webInfor.visibility==View.VISIBLE
                mBinding.ivContentMore.visibility==View.VISIBLE
                mBinding.webInfor.settings.defaultTextEncodingName = "utf-8"
                mBinding.webInfor.settings.javaScriptEnabled = true
                mBinding.webInfor.isScrollContainer = false
                mBinding.webInfor.isVerticalScrollBarEnabled = false
                mBinding.webInfor.isHorizontalScrollBarEnabled = false
                mBinding.webInfor.loadDataWithBaseURL(null, StringUtil.getHtml(it.introduce), "text/html", "utf-8", null)


                maxWebViewHeight = Utils.dip2px(this, 189f).toInt()
                mBinding.webInfor.setCallBack {
                    webViewHeight = it
                    if (it > maxWebViewHeight) {
                        val layoutParams = mBinding.webInfor.layoutParams as  ConstraintLayout.LayoutParams
                        layoutParams.width=  ConstraintLayout.LayoutParams.MATCH_PARENT
                        layoutParams.height=  maxWebViewHeight
                        mBinding.webInfor.layoutParams=layoutParams
                        mBinding.ivContentMore.visibility = View.VISIBLE
                    } else if (it < maxWebViewHeight) {
                        val layoutParams = mBinding.webInfor.layoutParams as  ConstraintLayout.LayoutParams
                        layoutParams.width=  ConstraintLayout.LayoutParams.MATCH_PARENT
                        layoutParams.height=   ConstraintLayout.LayoutParams.WRAP_CONTENT
                        mBinding.webInfor.layoutParams=layoutParams
                    }
                }

                mBinding.ivContentMore.setOnClickListener {
                    if (mBinding.ivContentMore.isSelected) {
                        mBinding.ivContentMore.isSelected = false
                        mBinding.ivContentMore.setImageResource(R.mipmap.main_arrow_down)
                        if (webViewHeight >= Utils.dip2px(this, 189f).toInt()) {
                            val layoutParams = mBinding.webInfor.layoutParams as  ConstraintLayout.LayoutParams
                            layoutParams.width=  ConstraintLayout.LayoutParams.MATCH_PARENT
                            layoutParams.height=  maxWebViewHeight
                            mBinding.webInfor.layoutParams=layoutParams
                            webViewHeight = Utils.dip2px(this, 189f).toInt()
                        }
                    } else {
                        mBinding.ivContentMore.isSelected = true
                        mBinding.ivContentMore.setImageResource(R.mipmap.main_arrow_up)
                        val layoutParams = mBinding.webInfor.layoutParams as  ConstraintLayout.LayoutParams
                        layoutParams.width=  ConstraintLayout.LayoutParams.MATCH_PARENT
                        layoutParams.height=  ConstraintLayout.LayoutParams.WRAP_CONTENT
                        mBinding.webInfor.layoutParams=layoutParams
                        webViewHeight += 100
                    }

                }
            }

            var spanstatusInfo = StringBuilder("")
            if (!status.isNullOrEmpty()) {
                spanstatusInfo.append(status)
            }
            try {
                var moreInfo = "更多信息 >"
                var status = spanstatusInfo.toString() + moreInfo
                var spanStatus = SpannableString(status)
                spanStatus.setSpan(
                    ForegroundColorSpan(resources.getColor(R.color.colorPrimary)),
                    status.length - moreInfo.length, status.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
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
                mBinding.txtFoodDetailVideo.visibility = View.VISIBLE
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
                mBinding.txtFoodDetailPannaor.visibility = View.VISIBLE
            }
            // 地址
            val strAddress = StringBuilder("")
            if (!it.regionName.isNullOrEmpty()) {
                strAddress.append(it!!.regionName)
                if (it.longitude != 0.0 && it.latitude != 0.0 && selfLocation != null) {
                    strAddress.append("距您" + AddressUtil.getLocationDistanceCh(selfLocation!!, LatLng(it.latitude, it.longitude)))
                }
                mBinding.tvFoodDetailsAddressValue.text = strAddress.toString()
            } else {
                mBinding.tvFoodDetailsAddressValue.text = ""
                mBinding.tvFoodDetailsAddressValue.visibility = View.GONE
            }
            //图片
            if (!images.isNullOrEmpty()) {
                for (item in images) {
                    dataVideoImages.add(VideoImageBean().apply {
                        type = 0
                        imageUrl = item
                    })
                }
                mBinding.txtFoodDetailImages.visibility = View.VISIBLE
                mBinding.txtFoodDetailImages.text = "1/${images.size}"
            }
            if (!dataVideoImages.isNullOrEmpty()) {
                mBinding.cbrFoodDetail.visibility = View.VISIBLE
                mBinding.cbrFoodDetail.setPages(object : CBViewHolderCreator {
                    override fun createHolder(itemView: View?): Holder<*> {
                        return VideoImageHolder(itemView!!, this@FoodDetailActivity)
                    }

                    override fun getLayoutId(): Int {
                        return R.layout.layout_video_image
                    }
                }, dataVideoImages).setCanLoop(dataVideoImages.size > 1).startTurning(3000).setPointViewVisible(false).setOnItemClickListener {
                    when (dataVideoImages[it].type) {
                        0 -> {
                            // 图片点击
                            val intent =
                                Intent(this@FoodDetailActivity, BigImgActivity::class.java)
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

                        }
                    }
                }
                mBinding.cbrFoodDetail.onPageChangeListener = object : OnPageChangeListener {
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
                                mBinding.txtFoodDetailImages.text = "${pos + 1}/${images?.size}"
                            }
                        }
                    }

                    override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                    }

                }
            } else {
                mBinding.cbrFoodDetail.visibility = View.GONE
            }

            // 听解说
            var audios: MutableList<AudioInfo> = mutableListOf()
            // 金牌解说
//            if (it. != null && !it.g.audio.isNullOrEmpty()) {
//                audios.add(AudioInfo().apply {
//                    type = 1
//                    linkUrl = it..link
//                    audio = it..turl
//                    name = it.name
//                })
//            }

            if (!it.audioInfo.isNullOrEmpty()) {
                audios.addAll(it.audioInfo)
            }
            if (!audios.isNullOrEmpty()) {
                mBinding.vFoodDetailAudios.visibility = View.VISIBLE
                mBinding.vFoodDetailAudios.setData(audios)
            } else {
                mBinding.vFoodDetailAudios.visibility = View.GONE
            }
            // 评论数目
            mBinding.pcvFoodDetailComments.updateCommentNum(
                it.commentNum.toInt(),
                id.toString(),
                ResourceType.CONTENT_TYPE_RESTAURANT,
                foodDetailBean!!.name
            )
            if (it.commentNum.toInt() > 0) {
                mBinding.tvFoodDetailCommentNum.text = "${it.commentNum}"
            }
            // 点赞关注
            if (it.vipResourceStatus != null) {
                // 收藏
                setCollectUi(it.vipResourceStatus.collectionStatus)
                // 点赞
                setThumbUi(it.vipResourceStatus.likeStatus)
            }
            if (!it.collectionNum.isNullOrEmpty() && it.collectionNum != "0") {
                mBinding.tvFoodDetailCollect.text = "${it.collectionNum}"
            }
            if (!it.likeNum.isNullOrEmpty() && it.likeNum != "0") {
                mBinding.tvFoodDetailThumb.text = "${it.likeNum}"
            }

            // 活动
            if (!it.activity.isNullOrEmpty()) {
                try {
                    actvityAdapter?.clear()
                    actvityAdapter?.add(it.activity as MutableList<ActivityBean>)
                    il_activity.visibility = View.VISIBLE
                    mBinding?.rvFoodDetailActivities.visibility = View.VISIBLE
                } catch (e: Exception) {
                }
            } else {
                il_activity.visibility = View.GONE
                mBinding?.rvFoodDetailActivities.visibility = View.GONE
            }
            // 美食商品
            if (!it.sysCode.isNullOrEmpty() && !it.resourceCode.isNullOrEmpty() && !it.shopUrl.isNullOrEmpty()) {
                mBinding.vFoodProduct?.setShopInfo(it.shopName, it.shopUrl)
            }
            // 周边推荐
            if (it.latitude != null && it.longitude != null) {
                foodLat = it.latitude
                foodLng = it.longitude
                mBinding.prvFoodDetail?.visibility = View.VISIBLE
                mBinding.prvFoodDetail?.setLocation(LatLng(it.latitude, it.longitude))
                mModel.gethMapRecList(ResourceType.CONTENT_TYPE_SCENERY, id.toString(), it.latitude.toString(), it.longitude.toString())
            } else {
                mBinding.prvFoodDetail?.visibility = View.GONE
            }
            if (!data.sysCode.isNullOrEmpty() && !data.resourceCode.isNullOrEmpty())
                mModel.refreshUserInfo(data.resourceCode, data.sysCode)
        }

    }

    override fun initData() {
        showLoadingDialog()
        doLocation()
        mModel.getActivityCommentList(id.toString())
        mModel.getStoryList(id.toString(), ResourceType.CONTENT_TYPE_RESTAURANT)
    }


    override fun onPause() {
        super.onPause()
        mBinding.cbrFoodDetail.pauseVideoPlayer()
        mBinding.cbrFoodDetail.stopTurning()
    }

    override fun onResume() {
        super.onResume()
        mBinding.cbrFoodDetail?.startTurning(3000)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            EventBus.getDefault().unregister(this)
            mBinding?.cbrFoodDetail?.stopVideoPlayer()
            mBinding.vFoodDetailAudios?.stopAudioPlayer()
            mBinding.vFoodDetailAudios?.releaseAudioPlayer()
            GaoDeLocation.getInstance().release()

            mQrCodeDialog = null
        } catch (e: Exception) {
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateAudioPlayer(event: UpdateAudioStateEvent) {
        // 1 准备播放 2 播放 3暂停/完成
        try {
            when (event.type) {
                1 -> {
                    mBinding?.cbrFoodDetail.stopTurning()
                    mBinding?.vFoodDetailAudios?.pauseAudioPlayer()
                }
                2 -> {
                    mBinding?.cbrFoodDetail.stopTurning()
                    mBinding?.vFoodDetailAudios?.pauseAudioPlayer()
                }
                3 -> {
                    mBinding?.cbrFoodDetail.startTurning(3000)
                }
            }
        } catch (e: java.lang.Exception) {

        }
        mBinding?.vFoodDetailAudios?.pauseAudioPlayer()
    }

    private fun doLocation() {

        GaoDeLocation.getInstance().init(this, object : GaoDeLocation.OnGetCurrentLocationLisner {

            override fun onResult(
                adCode: String, result: String, lat_: Double,
                lon_: Double, adcode: String
            ) {
                mModel.lat = lat_.toString()
                mModel.lng = lon_.toString()
                selfLocation = LatLng(lat_, lon_)
                mModel.getFoodDetail(id.toString())

            }

            override fun onError(errormsg: String) {
                mModel.getFoodDetail(id.toString())
//                mModel.gethMapRecList(type, id.toString())
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
            mBinding.tvFoodDetailThumb.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
        } else {
            // 取消点赞
            var drawable =
                resources.getDrawable(com.daqsoft.mainmodule.R.mipmap.bottom_icon_like_normal)
            mBinding.tvFoodDetailThumb.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
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
            mBinding.tvFoodDetailCollect.setCompoundDrawablesWithIntrinsicBounds(null, collect, null, null)
        } else {
            // 取消收藏
            var collect =
                resources.getDrawable(com.daqsoft.mainmodule.R.mipmap.bottom_icon_collect_normal)
            mBinding.tvFoodDetailCollect.setCompoundDrawablesWithIntrinsicBounds(null, collect, null, null)
        }
    }

    /**
     * 处理二维码显示
     */
    private fun dealShowQrCode() {
        if (foodDetailBean != null && !foodDetailBean!!.officialUrl.isNullOrEmpty()) {
            if (mQrCodeDialog == null) {
                mQrCodeDialog = QrCodeDialog.Builder().qrCodeImageUrl(foodDetailBean!!.officialUrl).title(foodDetailBean!!.officialName)
                    .onDownLoadListener(object : QrCodeDialog.OnDownLoadListener {
                        override fun onDownLoadImage(url: String) {
                            try {
                                showLoadingDialog()
                                DownLoadFileUtil.downNetworkImage(url, object : DownLoadFileUtil.DownImageListener {
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
                    .build(this@FoodDetailActivity)
            } else {
                mQrCodeDialog?.updateData(foodDetailBean!!.officialUrl, foodDetailBean!!.officialName)
            }
            mQrCodeDialog!!.show()
        }
    }

    override fun onBackPressed() {
        if (!isHaveVide || !mBinding.cbrFoodDetail.onBackPress()) {
            super.onBackPressed()
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun updateDataStatus(event: LoginStatusEvent) {
//        if (event != null) {
//            mModel?.getActivityDetail(id, false)
//        }
        mModel?.refreshFoodDetailStatus(id.toString())
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRecordEvent(event: UpdateCommentEvent) {
        mModel.getActivityCommentList(id.toString())
        mModel.getFoodDetail(id.toString())
    }
}