package com.daqsoft.travelCultureModule.specialty
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
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
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.baselib.widgets.dialog.QrCodeDialog
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivitySpecialDetailBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.bean.QuickTopNavigationItem
import com.daqsoft.provider.bean.SpeaiclDetailBean
import com.daqsoft.provider.businessview.adapter.ProviderActivityAdapter
import com.daqsoft.provider.businessview.event.LoginStatusEvent
import com.daqsoft.provider.businessview.event.UpdateAudioStateEvent
import com.daqsoft.provider.businessview.event.UpdateCommentEvent
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.businessview.view.ListenerAudioView
import com.daqsoft.provider.businessview.view.OrderAddressPopWindow
import com.daqsoft.provider.businessview.view.ProviderRecommendView
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
import com.daqsoft.travelCultureModule.specialty.viewmodel.SpecialDetailViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_hotel_detail.*
import kotlinx.android.synthetic.main.include_detail_module.view.*
import me.nereo.multi_image_selector.BigImgActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.textColor

/**
 * @Description ????????????
 */
@Route(path = MainARouterPath.MAIN_SPECIAL_DETAIL)
class SpecialtyDetailActivity : TitleBarActivity<ActivitySpecialDetailBinding, SpecialDetailViewModel>() {

    @JvmField
    @Autowired
    var id: String = "0"

    // ??????????????????
    var specialDetailBean: SpeaiclDetailBean? = null

    // ???????????????
    var actvityAdapter: ProviderActivityAdapter? = null

    var isHaveVide: Boolean = false

    var isHave720: Boolean = false

    // ?????????dialog
    var mQrCodeDialog: QrCodeDialog? = null

    // ?????????????????????
    var selfLocation: LatLng? = null

    // ????????????
    var specialLat: Double = 0.0

    // ????????????
    var specialLng: Double = 0.0

    var maxWebViewHeight: Int = 0
    /**
     * ??????????????????
     */
    var webViewHeight = 0

    var sharePopWindow: SharePopWindow? = null

    /**
     * ????????????
     */
    var orderAddressPopWindow: OrderAddressPopWindow? = null

    override fun getLayout(): Int {
        return R.layout.activity_special_detail
    }

    override fun setTitle(): String {
        return "????????????"
    }

    override fun injectVm(): Class<SpecialDetailViewModel> {
        return SpecialDetailViewModel::class.java
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        actvityAdapter = ProviderActivityAdapter(this@SpecialtyDetailActivity)
        mBinding.rvSpecialDetailActivities.layoutManager = LinearLayoutManager(
            this@SpecialtyDetailActivity,
            LinearLayoutManager.VERTICAL, false
        )
        mBinding.rvSpecialDetailActivities.adapter = actvityAdapter
        initViewModel()
        initPlayStickTop()
        initViewEvent()
    }


    override fun initCustomTitleBar(mTitleBar: TitleBar) {
        showShareButton(com.daqsoft.provider.R.mipmap.common_nav_button_share)
        setShareClick(View.OnClickListener {
            specialDetailBean?.let {
                if (sharePopWindow == null) {
                    sharePopWindow = SharePopWindow(this@SpecialtyDetailActivity)
                }
                var content= Constant.SHARE_DEC
                if(!TextUtils.isEmpty(it.briefing)){
                    content=it.briefing
                }
                sharePopWindow?.setShareContent(
                    it.name, content, it.images.getRealImages(),
                    ShareModel.getSpecialDetailUrl(id)
                )
                if (!sharePopWindow!!.isShowing) {
                    sharePopWindow?.showAsDropDown(mTitleBar)
                }

            }
        })
    }
    private fun initViewEvent() {
        mBinding.vSpecialDetailAudios?.onPlayerListener = object : ListenerAudioView.OnAudioPlayerListener {
            override fun onStartPlayer() {
                mBinding.cbrSpecialDetail.pauseVideoPlayer()
            }
        }
        mBinding.prvSpecialDetail.onItemClickTabListener = object : ProviderRecommendView.OnItemClickTabListener {
            override fun getMapResourceRecommend(type: String) {
                if (specialDetailBean != null && specialDetailBean!!.latitude != null && specialDetailBean!!.longitude != null) {
                    mModel.gethMapRecList(type, id.toString(), specialDetailBean!!.latitude.toString(), specialDetailBean!!.longitude.toString())
                }
            }

        }
        mBinding.tvStatus.onNoDoubleClick {
            if (!id.isNullOrEmpty()) {
                ARouter.getInstance().build(MainARouterPath.MAIN_PLAYGROUND_INFO)
                    .withInt("id", id.toInt())
                    .navigation()
            }
        }

        mBinding.tvCd.onNoDoubleClick {
            if (specialDetailBean != null && specialDetailBean?.latitude != 0.0 && specialDetailBean?.longitude != 0.0) {
                if (MapNaviUtils.isGdMapInstalled()) {
                    MapNaviUtils.openGaoDeNavi(
                        BaseApplication.context, 0.0, 0.0, null,
                        specialDetailBean!!.latitude.toDouble(), specialDetailBean!!.longitude.toDouble(),
                        specialDetailBean!!.regionName
                    )
                } else {
                    mModel.toast.postValue("??????????????????????????????????????????")
                }
            } else {
                mModel.toast.postValue("?????????????????????????????????")
            }
        }
        mBinding.tvSpecialDetailCommentNum.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                ARouter.getInstance().build(ARouterPath.Provider.PROVIDER_POST_COMMENT)
                    .withString("id", id.toString())
                    .withString("type", ResourceType.CONTENT_TYPE_SPECIALTY)
                    .withString("contentTitle", specialDetailBean!!.name)
                    .navigation()
            } else {
                ToastUtils.showUnLoginMsg()
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
        mBinding.tvSpecialDetailCollect.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                if (specialDetailBean != null && specialDetailBean!!.vipResourceStatus != null) {
                    showLoadingDialog()
                    if (specialDetailBean!!.vipResourceStatus!!.collectionStatus) {
                        mModel.collectionCancel(id.toString())
                    } else {
                        mModel.collection(id.toString())
                    }
                }
            } else {
                ToastUtils.showMessage("????????????????????????????????????")
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
        mBinding.tvSpecialDetailThumb.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                if (specialDetailBean != null && specialDetailBean!!.vipResourceStatus != null) {
                    showLoadingDialog()
                    if (specialDetailBean!!.vipResourceStatus!!.likeStatus) {
                        mModel.thumbCancell(id.toString())
                    } else {
                        mModel.thumbUp(id.toString())
                    }
                }
            } else {
                ToastUtils.showMessage("????????????????????????????????????")
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }

        mBinding.txtSpecialDetailImagesRoot.onNoDoubleClick {
            var pos = 0
            if (isHaveVide) {
                pos += 1
            }
            if (isHave720) {
                pos += 1
            }
            try {
                mBinding.cbrSpecialDetail.setCurrentItem(pos, true)
            } catch (e: Exception) {
            }

        }
    }

    private fun initViewModel() {
        mModel.orderAddresInfoLiveData.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                // ??????????????????
                mBinding?.btBuy.visibility = View.VISIBLE
                mBinding?.btBuy.onNoDoubleClick {
                    if (it.size > 1) {
                        // ????????????
                        var location: IntArray = IntArray(2)
                        mBinding.llButtom.getLocationOnScreen(location);
                        if (orderAddressPopWindow == null) {
                            orderAddressPopWindow = OrderAddressPopWindow(this@SpecialtyDetailActivity)
                            orderAddressPopWindow!!.updatePopWindowHeight(location[1])
                        }
                        orderAddressPopWindow?.updateData(it)
                        if (!orderAddressPopWindow!!.isShowing) {

                            orderAddressPopWindow?.showAtLocation(
                                mBinding.llButtom,
                                Gravity.NO_GRAVITY, 0, 0)
                        }
                    } else {
                        // ????????????
                        ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
                            .withString("mTitle", "??????")
                            .withString("html", it[0].url)
                            .navigation()
                    }
                }
            } else {
                mBinding?.btBuy.visibility = View.GONE
            }
        })
        // ????????????
        mModel.specialDetailLiveData.observe(this, androidx.lifecycle.Observer {
            dissMissLoadingDialog()
            bindHotelDetail(it)

        })

        mModel.contentLsLd.observe(this, Observer {
            mBinding.prvConentLs?.setvisibility(!it.isNullOrEmpty())
            if (!it.isNullOrEmpty()) {
                addTopData("??????", mBinding.prvConentLs.id,4)
                mBinding.prvConentLs.setData(id, ResourceType.CONTENT_TYPE_SPECIALTY, it)
            }
        })
        // ????????????
        mModel.mapResLiveData.observe(this, Observer {
            if (it != null) {
                addTopData("??????", mBinding.prvSpecialDetail.id,6)
                mBinding.prvSpecialDetail.setData(it.type, it.datas)
            }
        })
        // ??????
        mModel.commentBeans.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                addTopData("??????", mBinding.pcvSpecialDetailComments.id,5)
                mBinding.pcvSpecialDetailComments.visibility = View.VISIBLE
                mBinding.pcvSpecialDetailComments.setData(it)
            } else {
                mBinding.pcvSpecialDetailComments.visibility = View.GONE
            }
        })
        // ????????????
        mModel.storyList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                addTopData("??????", mBinding.psvSpecialStories.id,7)
                mBinding.psvSpecialStories.visibility = View.VISIBLE
                mBinding.psvSpecialStories.setDataNumber(id,ResourceType.CONTENT_TYPE_SPECIALTY,it,mModel.storyNumber)
            } else {
                mBinding.psvSpecialStories.visibility = View.GONE
            }
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })
        mModel.refreshSpecialDetailLiveData.observe(this, Observer {
            if (it != null) {
                specialDetailBean = it
                // ????????????
                if (it.vipResourceStatus != null) {
                    // ??????
                    setCollectUi(it.vipResourceStatus.collectionStatus)
                    // ??????
                    setThumbUi(it.vipResourceStatus.likeStatus)
                }
                if (!it.collectionNum.isNullOrEmpty() && it.collectionNum != "0") {
                    mBinding.tvSpecialDetailCollect.text = "${it.collectionNum}"
                }
                if (!it.likeNum.isNullOrEmpty() && it.likeNum != "0") {
                    mBinding.tvSpecialDetailThumb.text = "${it.likeNum}"
                }
            }
        })
        // ??????
        mModel.colllectLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            setCollectUi(it)
            specialDetailBean!!.vipResourceStatus?.collectionStatus = it
            var temp = specialDetailBean!!.collectionNum
            if (!temp.isNullOrEmpty()) {
                var collectNum: Int = temp.toInt()
                if (it) {
                    // ????????????
                    val result = collectNum + 1
                    specialDetailBean!!.collectionNum = result.toString()
                    mBinding.tvSpecialDetailCollect.text = result.toString()
                } else {
                    // ????????????
                    if (collectNum > 0) {
                        val result = collectNum - 1
                        specialDetailBean!!.collectionNum = result.toString()
                        if (result > 0) {
                            mBinding.tvSpecialDetailCollect.text = result.toString()
                        } else {
                            mBinding.tvSpecialDetailCollect.text = "??????"
                        }
                    }
                }
            }

        })
        // ??????
        mModel.thumbLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            setThumbUi(it)
            specialDetailBean!!.vipResourceStatus?.likeStatus = it
            var temp = specialDetailBean!!.likeNum
            if (!temp.isNullOrEmpty()) {
                var likeNum: Int = temp.toInt()
                if (it) {
                    // ????????????
                    val result = likeNum + 1
                    specialDetailBean!!.likeNum = result.toString()
                    mBinding.tvSpecialDetailThumb.text = result.toString()
                } else {
                    // ????????????
                    if (likeNum > 0) {
                        val result = likeNum - 1
                        specialDetailBean!!.likeNum = result.toString()
                        if (result > 0) {
                            mBinding.tvSpecialDetailThumb.text = result.toString()
                        } else {
                            mBinding.tvSpecialDetailThumb.text = "??????"
                        }
                    }
                }
            }
        })
        mModel.specialProductLiveData.observe(this, Observer {
//            if (!it.isNullOrEmpty()) {
//                mBinding.vSpecialProduct.visibility = View.VISIBLE
//                mBinding.vSpecialProduct.setData(it)
//            } else {
//                mBinding.vSpecialProduct.visibility = View.GONE
//            }
        })
    }




    private var   videoImageHolder:VideoImageHolder ?= null
    @SuppressLint("SetJavaScriptEnabled")
    private fun bindHotelDetail(data: SpeaiclDetailBean?) {
        dissMissLoadingDialog()
        if (data == null) {
            ToastUtils.showMessage("??????????????????????????????????????????~")
            finish()
            return
        }
        //????????????
        if (!data.orderAddressType.isNullOrEmpty()) {
            if (data.orderAddressType == "pt") {
                // ??????????????????
                mModel.getOrderAddressInfo(id.toString())
            }
        }

//        mBinding.specialCoorToolBar.visibility = View.VISIBLE
        mBinding.bean = data!!
        specialDetailBean = data!!
        data?.let {
            if (!it.name.isNullOrEmpty()) {
                setTitleContent("" + it.name)
            }
            // ????????????

            val tags = mutableListOf<ScenicLabelBean>()

            if (!it.type.isNullOrEmpty()) {
                tags.add(ScenicLabelBean(it.type, 3))
            }
            if(it.tag?.isNotEmpty()){
                it.tag?.forEach{it1->
                    tags.add(ScenicLabelBean(it1, 3))
                }

            }
            if(tags.size==0){
                mBinding.flowLayoutT.visibility=View.GONE
            }else{
                mBinding.flowLayoutT.visibility=View.VISIBLE
                setType(tags,mBinding,it)
            }

            if(it.introduce.isNullOrEmpty()){
                mBinding.moreVisible=View.GONE
            }else{
                addTopData("??????", mBinding.llInfor.id,1)
                mBinding.tvInfor.text = HtmlUtils.html2Str(it.introduce)
                mBinding.tvInfor.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw() :Boolean{
                        //???????????????????????????????????????????????????????????????
                        mBinding.tvInfor.viewTreeObserver.removeOnPreDrawListener(this)
                        if(mBinding.tvInfor.lineCount >=4){
                            mBinding.moreVisible=View.VISIBLE
                        }else{
                            mBinding.moreVisible=View.GONE
                        }

                        return true;
                    }
                });
            }

            mBinding.ivMore.setOnClickListener {
                var url=ShareModel.getSpecialInfor(id)
                ARouter.getInstance()
                    .build(ARouterPath.Provider.WEB_ACTIVITY)
                    .withString("mTitle", "????????????")
                    .withString("html", url)
                    .navigation()
            }
            var dataVideoImages: MutableList<VideoImageBean> = mutableListOf()
            //??????
            if (!it.video.isNullOrEmpty()) {
                dataVideoImages.add(0, VideoImageBean().apply {
                    type = 1
                    videoUrl = it.video
                })
                isHaveVide = true
                mBinding.txtSpecialDetailVideo.visibility = View.VISIBLE

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
                mBinding.txtSpecialDetailPannaor.visibility = View.VISIBLE
            }
            //??????
            if (!images.isNullOrEmpty()) {
                for (item in images) {
                    dataVideoImages.add(VideoImageBean().apply {
                        type = 0
                        imageUrl = item
                    })
                }
                mBinding.txtSpecialDetailImagesRoot.visibility = View.VISIBLE
                mBinding.txtSpecialDetailImages.text = "1/${images.size}"
            }

            mBinding.txtSpecialDetailVideo.onNoDoubleClick {
                mBinding.cbrSpecialDetail?.setCurrentItem(0, true)
                if (images.isNotEmpty()) {
                    Handler().postDelayed({   mBinding.txtSpecialDetailImages.text = "1/${images.size}"},300)
                }
            }
            mBinding.txtSpecialDetailPannaor.onNoDoubleClick {
                if (isHaveVide) {
                    mBinding.cbrSpecialDetail.setCurrentItem(1, true)
                } else {
                    mBinding.cbrSpecialDetail.setCurrentItem(0, true)
                }
                if (images.isNotEmpty()) {
                    Handler().postDelayed({   mBinding.txtSpecialDetailImages.text = "1/${images.size}"},300)
                }
            }


            if (!dataVideoImages.isNullOrEmpty()) {
                mBinding.cbrSpecialDetail.visibility = View.VISIBLE
                mBinding.cbrSpecialDetail.setPages(object : CBViewHolderCreator {
                    override fun createHolder(itemView: View?): Holder<*> {
                        videoImageHolder=VideoImageHolder(itemView!!, this@SpecialtyDetailActivity)
                        return videoImageHolder as VideoImageHolder
                    }

                    override fun getLayoutId(): Int {
                        return R.layout.layout_video_image
                    }
                }, dataVideoImages).setCanLoop(dataVideoImages.size > 1).setPointViewVisible(false).setOnItemClickListener {
                    when (dataVideoImages[it].type) {
                        0 -> {
                            // ????????????
                            val intent = Intent(this@SpecialtyDetailActivity, BigImgActivity::class.java)
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
                mBinding.cbrSpecialDetail.onPageChangeListener = object : OnPageChangeListener {
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
                                mBinding.txtSpecialDetailImages.text = "${pos + 1}/${images?.size}"
                            }
                        }
                    }

                    override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                    }

                }
                mBinding.cbrSpecialDetail.viewPager.setHasFixedSize(true)
                mBinding.cbrSpecialDetail.viewPager.setItemViewCacheSize(20)
            } else {
                mBinding.cbrSpecialDetail.visibility = View.GONE
            }

            // ?????????
            var audios: MutableList<AudioInfo> = mutableListOf()
            // ????????????
            if (!it.audioInfo.isNullOrEmpty()) {
                addTopData("??????", mBinding.vSpecialDetailAudios.id,2)
                audios.addAll(it.audioInfo)
            }
            if (!audios.isNullOrEmpty()) {
                mBinding.vSpecialDetailAudios.visibility = View.VISIBLE
                mBinding.vSpecialDetailAudios.setData(audios)
            } else {
                mBinding.vSpecialDetailAudios.visibility = View.GONE
            }
            // ????????????
            mBinding.pcvSpecialDetailComments.updateCommentNum(
                it.commentNum.toInt(),
                id.toString(),
                ResourceType.CONTENT_TYPE_SPECIALTY,
                specialDetailBean!!.name
            )
            if (it.commentNum.toInt() > 0) {
                mBinding.tvSpecialDetailCommentNum.text = "${it.commentNum}"
            }
            // ????????????
            if (it.vipResourceStatus != null) {
                // ??????
                setCollectUi(it.vipResourceStatus.collectionStatus)
                // ??????
                setThumbUi(it.vipResourceStatus.likeStatus)
            }
            if (!it.collectionNum.isNullOrEmpty() && it.collectionNum != "0") {
                mBinding.tvSpecialDetailCollect.text = "${it.collectionNum}"
            }
            if (!it.likeNum.isNullOrEmpty() && it.likeNum != "0") {
                mBinding.tvSpecialDetailThumb.text = "${it.likeNum}"
            }

            // ??????
            if (!it.activity.isNullOrEmpty()) {
                try {
                    addTopData("??????", mBinding.ilActivity.clLabel.id,3)
                    actvityAdapter?.clear()
                    if(it.activity.size > 2 ){
                        actvityAdapter?.add(it.activity.subList(0,2) as MutableList<ActivityBean>)
                    }else{
                        actvityAdapter?.add(it.activity)
                    }

                    il_activity.visibility = View.VISIBLE
                    mBinding?.rvSpecialDetailActivities.visibility = View.VISIBLE
                    if(it.activity.size>=2){
                        mBinding.ilActivity.hideRight=false
                        mBinding.ilActivity.rightContent="????????????"
                        il_activity.tv_replay_num.setOnClickListener {it1->
                            ARouter.getInstance().build(MainARouterPath.MAIN_FIND_ACTIVITY)
                                .withString("toJson",Gson().toJson(it.activity))
                                .navigation()
                        }
                    }else{
                        mBinding.ilActivity.hideRight=true
                    }
                } catch (e: Exception) {
                }
            } else {
                il_activity.visibility = View.GONE
                mBinding?.rvSpecialDetailActivities.visibility = View.GONE
            }
            // ????????????
//            if (!it.sysCode.isNullOrEmpty() && !it.resourceCode.isNullOrEmpty() && !it.shopUrl.isNullOrEmpty()) {
//                mBinding.vSpecialProduct?.setShopInfo(it.shopName, it.shopUrl)
//            }
            // ????????????
            if (it.latitude != null && it.longitude != null) {
                specialLat = it.latitude
                specialLng = it.longitude
                mBinding.prvSpecialDetail?.visibility = View.VISIBLE
                mBinding.prvSpecialDetail?.setLocation(LatLng(it.latitude, it.longitude))
                mModel.gethMapRecList(ResourceType.CONTENT_TYPE_SCENERY, id.toString(), it.latitude.toString(), it.longitude.toString())
            } else {
                mBinding.prvSpecialDetail?.visibility = View.GONE
            }
//            if (!data.sysCode.isNullOrEmpty() && !data.resourceCode.isNullOrEmpty())
//                mModel.refreshUserInfo(data.resourceCode, data.sysCode)
        }

    }



    override fun initData() {
        showLoadingDialog()
        doLocation()
        mModel.getActivityCommentList(id.toString())
        // ??????
        mModel.getScenicContentLs(id)

        mModel.getStoryList(id.toString(), ResourceType.CONTENT_TYPE_SPECIALTY)
    }


    override fun onPause() {
        super.onPause()
        mBinding.cbrSpecialDetail.pauseVideoPlayer()
        mBinding.cbrSpecialDetail.stopTurning()
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
            mBinding?.cbrSpecialDetail?.stopVideoPlayer()
            mBinding.vSpecialDetailAudios?.stopAudioPlayer()
            mBinding.vSpecialDetailAudios?.releaseAudioPlayer()
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
        // 1 ???????????? 2 ?????? 3??????/??????
        try {
            when (event.type) {
                1 -> {
                    mBinding?.cbrSpecialDetail.stopTurning()
                }
                2 -> {
                    mBinding?.cbrSpecialDetail.stopTurning()
                }
                3 -> {
//                    mBinding?.cbrSpecialDetail.startTurning(3000)
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()

            ToastUtils.showMessage(e.message)
        }
        mBinding?.vSpecialDetailAudios?.pauseAudioPlayer()
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
                mModel.getSpecialDetail(id.toString())

            }

            override fun onError(errormsg: String) {
                mModel.getSpecialDetail(id.toString())
            }
        })
    }

    /**
     * ??????
     */
    private fun setThumbUi(it: Boolean) {
        if (it) {
            // ????????????
            var drawable =
                resources.getDrawable(com.daqsoft.mainmodule.R.mipmap.bottom_icon_like_selected)
            mBinding.tvSpecialDetailThumb.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
        } else {
            // ????????????
            var drawable =
                resources.getDrawable(com.daqsoft.mainmodule.R.mipmap.bottom_icon_like_normal)
            mBinding.tvSpecialDetailThumb.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
        }
    }

    /**
     * ??????
     */
    private fun setCollectUi(it: Boolean) {
        if (it) {
            // ??????
            var collect =
                resources.getDrawable(com.daqsoft.mainmodule.R.mipmap.bottom_icon_collect_selected)
            mBinding.tvSpecialDetailCollect.setCompoundDrawablesWithIntrinsicBounds(null, collect, null, null)
        } else {
            // ????????????
            var collect =
                resources.getDrawable(com.daqsoft.mainmodule.R.mipmap.bottom_icon_collect_normal)
            mBinding.tvSpecialDetailCollect.setCompoundDrawablesWithIntrinsicBounds(null, collect, null, null)
        }
    }


    private fun setType(
        types: MutableList<ScenicLabelBean>,
        mBinding: ActivitySpecialDetailBinding,
        its: SpeaiclDetailBean
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
                    resources.getDrawable(R.drawable.shape_scenic_r2_d4)
                vi.textColor = resources.getColor(R.color.color_333)
            } else {
                vi.text = types[index].name
                vi.background =
                    resources.getDrawable(R.drawable.shape_scenic_r2_d4)
                vi.textColor = resources.getColor(R.color.color_333)
            }
            mBinding.flowLayoutT.addView(vi)
        }
    }
    override fun onBackPressed() {
        if (!isHaveVide || !mBinding.cbrSpecialDetail.onBackPress()) {
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
        mModel.getSpecialDetail(id.toString())
    }

    /**
     * ?????????????????????????????????
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
                                ToastUtils.showMessage("????????????")
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
                    if (!mModel.checkVisibleOnScreen(mBinding.tvSpecialDetailName)) {
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

//http://10.252.252.119:8080/idaqCustomer/getPages?key=&customerType=0&page=1&size=10&menuId=1070
//http://10.252.252.119:8080/idaqCustomer/getPages?gradeClassify=&key=??????&page=1&size=10&menuId=1070&customerType=