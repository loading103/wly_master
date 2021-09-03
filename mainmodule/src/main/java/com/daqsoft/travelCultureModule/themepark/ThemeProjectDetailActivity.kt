package com.daqsoft.travelCultureModule.themepark
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
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
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.baselib.widgets.dialog.QrCodeDialog
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityPlayProjecDetailBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.businessview.adapter.ProviderActivityAdapter
import com.daqsoft.provider.businessview.event.LoginStatusEvent
import com.daqsoft.provider.businessview.event.UpdateAudioStateEvent
import com.daqsoft.provider.businessview.event.UpdateCommentEvent
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.network.venues.bean.AudioInfo
import com.daqsoft.provider.network.venues.bean.ScenicLabelBean
import com.daqsoft.provider.scrollview.DqScrollView
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.provider.utils.*
import com.daqsoft.provider.view.convenientbanner.bean.VideoImageBean
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.convenientbanner.holder.VideoImageHolder
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import com.daqsoft.provider.view.popupwindow.SharePopWindow
import com.daqsoft.travelCultureModule.playground.adapter.PlayGroupDetailTopStickAdapter
import com.daqsoft.travelCultureModule.themepark.viewmodel.ThemeProjectDetailViewModel
import kotlinx.android.synthetic.main.activity_hotel_detail.*
import me.nereo.multi_image_selector.BigImgActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.textColor

/**
 * @Description 项目详情
 */
@Route(path = MainARouterPath.THEME_PROJECT_DETAIL)
class ThemeProjectDetailActivity : TitleBarActivity<ActivityPlayProjecDetailBinding, ThemeProjectDetailViewModel>() {

    @JvmField
    @Autowired
    var id: String = "0"

    // 娱乐场所实体
    var foodDetailBean: ThemeProjectDetailBean? = null

    // 活动适配器
    var actvityAdapter: ProviderActivityAdapter? = null

    var isHaveVide: Boolean = false

    var isHave720: Boolean = false

    // 二维码dialog
    var mQrCodeDialog: QrCodeDialog? = null

    // 当前位置经纬度
    var selfLocation: LatLng? = null

    // 场所经度
    var foodLat: Double = 0.0

    // 场所纬度
    var foodLng: Double = 0.0

    var maxWebViewHeight: Int = 0
    /**
     * 文本内容高度
     */
    var webViewHeight = 0

    var sharePopWindow: SharePopWindow? = null
    override fun getLayout(): Int {
        return R.layout.activity_play_projec_detail
    }

    override fun setTitle(): String {
        return "项目详情"
    }

    override fun injectVm(): Class<ThemeProjectDetailViewModel> {
        return ThemeProjectDetailViewModel::class.java
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        actvityAdapter = ProviderActivityAdapter(this@ThemeProjectDetailActivity)
        initViewModel()
        initPlayStickTop()
        initViewEvent()
    }


    override fun initCustomTitleBar(mTitleBar: TitleBar) {
        showShareButton(com.daqsoft.provider.R.mipmap.common_nav_button_share)
        setShareClick(View.OnClickListener {
            foodDetailBean?.let {
                if (sharePopWindow == null) {
                    sharePopWindow = SharePopWindow(this@ThemeProjectDetailActivity)
                }
                var content= Constant.SHARE_DEC
                if(!TextUtils.isEmpty(it.summary)){
                    content=it.summary
                }
                sharePopWindow?.setShareContent(
                    it.name, content, it.images.getRealImages(),
                    ShareModel.getPlayGroundDetailUrl(id)
                )
                if (!sharePopWindow!!.isShowing) {
                    sharePopWindow?.showAsDropDown(mTitleBar)
                }

            }
        })
    }
    private fun initViewEvent() {
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
        mBinding.tvFoodDetailCommentNum.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                ARouter.getInstance().build(ARouterPath.Provider.PROVIDER_POST_COMMENT)
                    .withString("id", id.toString())
                    .withString("type", ResourceType.CONTENT_TYPE_ENTERTRAINMENT)
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

        mBinding.txtFoodDetailImagesRoot.onNoDoubleClick {
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
        // 评论
        mModel.commentBeans.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                addTopData("点评", mBinding.pcvFoodDetailComments.id,3)
                mBinding.pcvFoodDetailComments.visibility = View.VISIBLE
                mBinding.pcvFoodDetailComments.setData(it)
            } else {
                mBinding.pcvFoodDetailComments.visibility = View.GONE
            }
        })
        // 故事类别
        mModel.storyList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                addTopData("故事", mBinding.psvFoodStories.id,4);
                mBinding.psvFoodStories.visibility = View.VISIBLE
                mBinding.psvFoodStories.setDataNumber(id,ResourceType.CONTENT_TYPE_ENTERTRAINMENT,it,mModel.storyNumber)
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
    }




    private var   videoImageHolder:VideoImageHolder ?= null
    @SuppressLint("SetJavaScriptEnabled")
    private fun bindHotelDetail(data: ThemeProjectDetailBean?) {
        dissMissLoadingDialog()
        if (data == null) {
            ToastUtils.showMessage("暂未找到酒店信息，请稍后再试~")
            finish()
            return
        }

        mBinding.bean = data!!
        foodDetailBean = data!!
        data?.let {
            if (!it.name.isNullOrEmpty()) {
                setTitleContent("" + it.name)
            }
            // 标签处理

            val tags = mutableListOf<ScenicLabelBean>()

            if (!it.type.isNullOrEmpty()) {
                tags.add(ScenicLabelBean(it.type, 3))
            }
            if(it.applyTag.isNotEmpty()){
                it.applyTag.forEach{it1->
                    tags.add(ScenicLabelBean(it1, 3))
                }

            }
            if(it.entEqtTag.isNotEmpty()){
                it.entEqtTag.forEach{it1->
                    tags.add(ScenicLabelBean(it1, 3))
                }
            }
            if(it.feature.isNotEmpty()){
                it.feature.forEach{it1->
                    tags.add(ScenicLabelBean(it1, 3))
                }
            }

            if(tags.size==0){
                mBinding.flowLayoutT.visibility=View.GONE

            }else{
                mBinding.flowLayoutT.visibility=View.VISIBLE
                setType(tags,mBinding,it)
            }

            var status = DividerTextUtils.convertString(
                java.lang.StringBuilder(), if (it.cutRegionName.isNullOrEmpty()) {
                    ""
                } else {
                    it.cutRegionName
                }, if (it.openStartTime.isNullOrEmpty()) {
                    ""
                } else {
                    String.format(getString(R.string.food_ls_opentime,it.openStartTime+"-"+it.openEndTime))
                }, if (it.consumPerson.isNullOrEmpty()) {
                    ""
                } else {
                    "人均消费：¥${it.consumPerson}/人"
                }
            )
            var spanstatusInfo = StringBuilder("")

//            if(it.introduce.isNullOrEmpty()){
//                mBinding.llInfor.visibility==View.GONE
//                mBinding.ilFoodIntrouduce.hide=true
//            }else{
//                mBinding.llInfor.visibility==View.VISIBLE
//                mBinding.webInfor.settings.defaultTextEncodingName = "utf-8"
//                mBinding.webInfor.settings.javaScriptEnabled = true
//                mBinding.webInfor.loadDataWithBaseURL(null, StringUtil.getHtml(it.introduce), "text/html", "utf-8", null)
//            }
            if(it.introduce.isNullOrEmpty()){
                mBinding.llItemJianj.llInfor.visibility==View.GONE
                mBinding.webInfor.visibility==View.GONE
                mBinding.ivContentMore.visibility==View.GONE
            }else{
                mBinding.llItemJianj.llInfor.visibility==View.VISIBLE
                mBinding.webInfor.visibility==View.VISIBLE
                mBinding.ivContentMore.visibility==View.VISIBLE
                mBinding.webInfor.settings.defaultTextEncodingName = "utf-8"
                mBinding.webInfor.settings.javaScriptEnabled = true
                mBinding.webInfor.isScrollContainer = false
                mBinding.webInfor.isVerticalScrollBarEnabled = false
                mBinding.webInfor.isHorizontalScrollBarEnabled = false


//                if (!it.introduce.isNullOrEmpty() && it.introduce.contains("iframe") || it.introduce.contains("frame")) {
//                    WebViewUtils.pptWeb(mBinding.webInfor, it.introduce,this@PlayGroundDetailActivity)
//                }else{
                    mBinding.webInfor.loadDataWithBaseURL(null, StringUtil.getHtml(it.introduce), "text/html", "utf-8", null)
//                }


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
                    strAddress.append("  距您" + AddressUtil.getLocationDistanceCh(selfLocation!!, LatLng(it.latitude, it.longitude)))
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
                mBinding.txtFoodDetailImagesRoot.visibility = View.VISIBLE
                mBinding.txtFoodDetailImages.text = "1/${images.size}"
            }

            mBinding.txtFoodDetailVideo.onNoDoubleClick {
                mBinding.cbrFoodDetail?.setCurrentItem(0, true)
                if (images.isNotEmpty()) {
                    Handler().postDelayed({   mBinding.txtFoodDetailImages.text = "1/${images.size}"},300)
                }
            }
            mBinding.txtFoodDetailPannaor.onNoDoubleClick {
                if (isHaveVide) {
                    mBinding.cbrFoodDetail.setCurrentItem(1, true)
                } else {
                    mBinding.cbrFoodDetail.setCurrentItem(0, true)
                }
                if (images.isNotEmpty()) {
                    Handler().postDelayed({   mBinding.txtFoodDetailImages.text = "1/${images.size}"},300)
                }
            }


            if (!dataVideoImages.isNullOrEmpty()) {
                mBinding.cbrFoodDetail.visibility = View.VISIBLE
                mBinding.cbrFoodDetail.setPages(object : CBViewHolderCreator {
                    override fun createHolder(itemView: View?): Holder<*> {
                        videoImageHolder=VideoImageHolder(itemView!!, this@ThemeProjectDetailActivity)
                        return videoImageHolder as VideoImageHolder
                    }

                    override fun getLayoutId(): Int {
                        return R.layout.layout_video_image
                    }
                }, dataVideoImages).setCanLoop(dataVideoImages.size > 1).setPointViewVisible(false).setOnItemClickListener {
                    when (dataVideoImages[it].type) {
                        0 -> {
                            // 图片点击
                            val intent = Intent(this@ThemeProjectDetailActivity, BigImgActivity::class.java)
                            intent.putExtra("position", it)
                            intent.putStringArrayListExtra("imgList", ArrayList(images))
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
                mBinding.cbrFoodDetail.viewPager.setHasFixedSize(true)
                mBinding.cbrFoodDetail.viewPager.setItemViewCacheSize(20)
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
            // 评论数目
            mBinding.pcvFoodDetailComments.updateCommentNum(
                it.commentNum.toInt(),
                id.toString(),
                ResourceType.CONTENT_TYPE_ENTERTRAINMENT,
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

            if (!data.sysCode.isNullOrEmpty() && !data.resourceCode.isNullOrEmpty())
                mModel.refreshUserInfo(data.resourceCode, data.sysCode)
        }

    }



    override fun initData() {
//        showLoadingDialog()
        doLocation()
        mModel.getActivityCommentList("1")
        mModel.getStoryList("1", ResourceType.CONTENT_TYPE_ENTERTRAINMENT)
    }


    override fun onPause() {
        super.onPause()
        mBinding.cbrFoodDetail.pauseVideoPlayer()
        mBinding.cbrFoodDetail.stopTurning()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
    }
    override fun onDestroy() {
        super.onDestroy()
        try {
            EventBus.getDefault().unregister(this)
            mBinding?.cbrFoodDetail?.stopVideoPlayer()
            GaoDeLocation.getInstance().release()

            if(videoImageHolder!=null){
                (videoImageHolder as VideoImageHolder).release()
            }

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
                }
                2 -> {
                    mBinding?.cbrFoodDetail.stopTurning()
                }
                3 -> {
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()

            ToastUtils.showMessage(e.message)
        }
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

    private fun setType(
        types: MutableList<ScenicLabelBean>,
        mBinding: ActivityPlayProjecDetailBinding,
        its: ThemeProjectDetailBean
    ) {
        mBinding.flowLayoutT.removeAllViews()
        var levelCount = types.size

        for (index in types.indices) {
            var vi = TextView(this)
            var lp = ViewGroup.MarginLayoutParams(
                ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams
                    .WRAP_CONTENT)
            lp.rightMargin =resources.getDimension(R.dimen.dp_8).toInt()
            lp.topMargin = resources.getDimension(R.dimen.dp_8).toInt()
            vi.layoutParams = lp
            vi.setPadding(
                resources.getDimension(R.dimen.dp_3).toInt(),
                resources.getDimension(R.dimen.dp_1).toInt(),
                resources.getDimension(R.dimen.dp_3).toInt(),
                resources.getDimension(R.dimen.dp_1).toInt())
            vi.textSize = 12f

            if (index == 0 && !its.type.isNullOrEmpty()) {
                vi.text = types[index].name
                vi.background =
                    resources.getDrawable(R.drawable.shape_scenic_r2_cprimary)
                vi.textColor = resources.getColor(R.color.colorPrimary)
            } else {
                vi.text = types[index].name
                vi.background =
                    resources.getDrawable(R.drawable.shape_scenic_r2_cprimarysecond)
                vi.textColor = resources.getColor(R.color.colorPrimarySecond)
            }
            mBinding.flowLayoutT.addView(vi)
        }
    }
    override fun onBackPressed() {
        if (!isHaveVide || !mBinding.cbrFoodDetail.onBackPress()) {
            super.onBackPressed()
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun updateDataStatus(event: LoginStatusEvent) {
        mModel?.refreshFoodDetailStatus(id.toString())
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRecordEvent(event: UpdateCommentEvent) {
        mModel.getActivityCommentList(id.toString())
        mModel.getFoodDetail(id.toString())
    }


    /**
     * 滑动悬浮顶部内容适配器
     */
    var childHeight: Int = 0

    var mPlayStickTopAdapter: PlayGroupDetailTopStickAdapter? = null

    val quickNavigationItem = arrayListOf<QuickTopNavigationItem>()

    var  isClick:Boolean=false
    private fun initPlayStickTop() {
        mPlayStickTopAdapter = PlayGroupDetailTopStickAdapter()
        mPlayStickTopAdapter!!.emptyViewShow = false
        mBinding.recyTopScrollStick.layoutManager = LinearLayoutManager(BaseApplication.context!!, LinearLayoutManager.HORIZONTAL, false)
        mBinding.recyTopScrollStick.adapter = mPlayStickTopAdapter
        childHeight = resources.getDimensionPixelSize(R.dimen.dp_40).toInt()

        mPlayStickTopAdapter?.add(quickNavigationItem)
        mPlayStickTopAdapter?.onItemClickListener =
            object : PlayGroupDetailTopStickAdapter.OnItemClickListener {
                override fun onItemClick(id: Int) {
                    mBinding.scrollView.post(object : Runnable {
                        override fun run() {
                            if (findViewById<View>(id).isGone) {
                                ToastUtils.showMessage("暂无数据")
                            } else {
                                isClick=true
                                Handler().postDelayed({isClick=false},1000)
                                mBinding.scrollView.smoothScrollTo(0, findViewById<View>(id).top- childHeight
                                )
                            }
                        }
                    })

                }
            }
        mBinding.scrollView.setOnScrollListener(object : DqScrollView.OnScrollListener {
            override fun onScrollY(scrollY: Int) {
                try {
                    if(isClick){
                        return
                    }
                    if (!mModel.checkVisibleOnScreen(mBinding.llItemJianj.llInfor)) {
                        if (mPlayStickTopAdapter!!.getData().isEmpty() || mPlayStickTopAdapter!!.getData().size<2){
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
                                mPlayStickTopAdapter?.updateSelectPos(item.id, mBinding.recyTopScrollStick)
                                return@forEachIndexed
                            } else if (scrollY in top..bottom) {
                                mPlayStickTopAdapter?.updateSelectPos(item.id, mBinding.recyTopScrollStick)
                                return@forEachIndexed
                            }
                        }
                    } else {
                        mBinding.recyTopScrollStick.visibility = View.GONE
                    }
                }catch (e:Exception){
                    return
                }
            }
        })
    }

    private fun addTopData(content: String, id: Int,order :Int) {
        quickNavigationItem.add(QuickTopNavigationItem(content,id,order))
        quickNavigationItem.sortBy { it.order }
        mPlayStickTopAdapter?.setNewData(quickNavigationItem)
        mPlayStickTopAdapter?.notifyDataSetChanged()
    }

}