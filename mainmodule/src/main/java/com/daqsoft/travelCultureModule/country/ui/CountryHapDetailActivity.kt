package com.daqsoft.travelCultureModule.country.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.SystemHelper
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityCountryHapDetailBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.businessview.event.UpdateCommentEvent
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.businessview.view.ProviderRecommendView
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.network.venues.bean.ScenicLabelBean
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.provider.utils.HtmlUtils
import com.daqsoft.provider.utils.MapNaviUtils
import com.daqsoft.provider.view.convenientbanner.bean.VideoImageBean
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.convenientbanner.holder.VideoImageHolder
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import com.daqsoft.provider.view.popupwindow.SharePopWindow
import com.daqsoft.travelCultureModule.country.CHANNEL_FW_CODE
import com.daqsoft.travelCultureModule.country.CONTENT_TYPE_AGRITAINMENT
import com.daqsoft.travelCultureModule.country.adapter.CountryLabelAdapter
import com.daqsoft.travelCultureModule.country.bean.CountryHapDetailBean
import com.daqsoft.travelCultureModule.country.model.CountryHapDetailViewModel
import com.daqsoft.travelCultureModule.country.util.TimeUtils
import com.daqsoft.travelCultureModule.country.view.MerchantsGoodsView
import me.nereo.multi_image_selector.BigImgActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * desc :??????????????????
 * @author ?????????
 * @date 2020/4/15 15:45
 */
@Route(path = ARouterPath.CountryModule.COUNTRY_HAPPINESS_DETAIL)
class CountryHapDetailActivity :
    TitleBarActivity<ActivityCountryHapDetailBinding, CountryHapDetailViewModel>()/*, CountryHapImageFragment.OnPageChangedListener*/ {
    /**
     * ????????????????????????
     */
    private var mCountryHapHeaderFrages: MutableList<Fragment> = mutableListOf()

    @JvmField
    @Autowired(name = "id")
    var id: String = ""
    var imageSize: Int = 0

    var isHaveVide: Boolean = false
    var isHave720: Boolean = false

    /**
     * ?????????????????????
     */
    var selfLocation: LatLng? = null

    override fun getLayout(): Int {
        return R.layout.activity_country_hap_detail
    }

    override fun setTitle(): String {
        return "???????????????"
    }
    /**
     * ????????????
     */
    var sharePopWindow: SharePopWindow? = null

    override fun injectVm(): Class<CountryHapDetailViewModel> =
        CountryHapDetailViewModel::class.java

    @SuppressLint("SetTextI18n")
    override fun initView() {
        EventBus.getDefault().register(this)
        showLoadingDialog()
        mBinding.introduceHeader.vm = mModel
        mBinding.model = mModel
        mModel.countryHapDetails.observe(this, Observer {
            dissMissLoadingDialog()
            if (it == null) {
                ToastUtils.showMessage("???????????????")
                finish()
                return@Observer
            }
            if (!it.name.isNullOrEmpty()) {
                setTitleContent("${it.name}")
            }
            mBinding.countryHapToolBar.visibility = View.VISIBLE
            mBinding.countryBottom.visibility = View.VISIBLE
            getCommendList(it)
            dissMissLoadingDialog()
            setBanner(it)
            setHeaderIntroduce(it)

        })

        //????????????
        mModel.foodProductLiveData.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.introduceHeader.pcvCountryHapDetailFoodProduct.visibility = View.VISIBLE
                mBinding.introduceHeader.pcvCountryHapDetailFoodProduct.setData(it)
                mBinding.introduceHeader.pcvCountryHapDetailFoodProduct.onProducatItemClickListener =
                    object : MerchantsGoodsView.OnProducatItemClickListener {
                        override fun onNotify(
                            producatId: String,
                            reaminStatus: Boolean,
                            position: Int
                        ) {
                            mModel.notify(reaminStatus, producatId, position)
                        }

                    }
            } else {
                mBinding.introduceHeader.pcvCountryHapDetailFoodProduct.visibility = View.GONE
            }
        })
        // ??????
        mModel.commentBeans.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.introduceHeader.pcvCountryHapDetailComments.visibility = View.VISIBLE
                mBinding.introduceHeader.pcvCountryHapDetailComments.setData(it)
            } else {
                mBinding.introduceHeader.pcvCountryHapDetailComments.visibility = View.GONE
            }

        })
        // ????????????
        mModel.mapResLiveData.observe(this, Observer {
            if (it != null) {
//                mBinding.introduceHeader.prvCountryHapDetailRecommend.visibility = View.VISIBLE
                mBinding.introduceHeader.prvCountryHapDetailRecommend.setData(it.type, it.datas)
            } else {
//                mBinding.introduceHeader.prvCountryHapDetailRecommend.visibility = View.VISIBLE
            }
        })
        mModel.notify.observe(this, Observer {
            mModel.getCountryHapDetails()
        })
        // ????????????
        mModel.storyList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.introduceHeader.psvCountryHapStories.visibility = View.VISIBLE
                mBinding.introduceHeader.psvCountryHapStories.setData(it)
            } else {
                mBinding.introduceHeader.psvCountryHapStories.visibility = View.GONE
            }
        })
        // ????????????
        mModel.informationBean.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.introduceHeader.pcvProviderInfo.visibility = View.VISIBLE
                mBinding.introduceHeader.pcvProviderInfo.setData(it, CHANNEL_FW_CODE)
            } else {
                mBinding.introduceHeader.pcvProviderInfo.visibility = View.GONE
            }
        })
        // ??????????????????
        mModel.notifyRemainLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            if (it != null)
                mBinding.introduceHeader.pcvCountryHapDetailFoodProduct.updateNotifyStatus(
                    it.status,
                    it.position
                )
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })
    }
    override fun initCustomTitleBar(mTitleBar: TitleBar) {
        showShareButton(com.daqsoft.provider.R.mipmap.common_nav_button_share)
        setShareClick(View.OnClickListener {
            mModel.countryHapDetails?.let {
                if (sharePopWindow == null) {
                    sharePopWindow = SharePopWindow(this@CountryHapDetailActivity)
                }
                var content= Constant.SHARE_DEC
                if(!TextUtils.isEmpty(it.value?.summary)){
                    content=it.value?.summary!!
                }

                sharePopWindow?.setShareContent(
                    it.value?.name, content,   it.value?.images.getRealImages(),
                    ShareModel.getCountryHapDesc(id)
                )
                if (!sharePopWindow!!.isShowing) {
                    sharePopWindow?.showAsDropDown(mTitleBar)
                }
            }
        })
    }
    /**
     *??????????????????
     */
    private fun getCommendList(it: CountryHapDetailBean?) {
        if (it != null) {
            if (!it.sysCode.isNullOrEmpty()) {
                mModel.getCommendList(it.resourceCode, it.sysCode)
            }
        }
    }

    /**
     *??????????????????
     */
    @SuppressLint("SetTextI18n", "SetJavaScriptEnabled")
    private fun setHeaderIntroduce(it: CountryHapDetailBean) {
        mBinding.introduceHeader.bean = it
        // ????????????
        var tags = mutableListOf<ScenicLabelBean>()
        //????????????????????????
        if (!it.businessTime.isNullOrEmpty()) {
            val businessTimes = it.businessTime.split("[~ :]")
            if (businessTimes.size > 3) {
                if (TimeUtils.isCurrentInTimeScope(
                        businessTimes[0].toInt(), businessTimes[1].toInt(),
                        businessTimes[2].toInt(), businessTimes[3].toInt()
                    )
                ) {
                    tags.add(ScenicLabelBean("?????????", 1))
                }
            }
        }
//        if (!it.startStatus.isNullOrEmpty() && it.startStatus == "1") {
//            tags.add(ScenicLabelBean("???????????????", 2))
//        }
        if (!it.level.isNullOrEmpty()) {
            var levels = it.level.split(",")
            if (!levels.isNullOrEmpty()) {
                for (i in levels) {
                    tags.add(ScenicLabelBean(i, 2))
                }
            }
        }
        if (!it.type.isNullOrEmpty()) {
            //????????????
            for (i in it.type.split(",")) {
                tags.add(ScenicLabelBean(i, 3))
            }
        }
        if (!tags.isNullOrEmpty()) {
            val countryLabelAdapter = CountryLabelAdapter(BaseApplication.context)
            mBinding.introduceHeader.recyclerCountryHapDetailsLabel.layoutManager =
                LinearLayoutManager(
                    BaseApplication.context, LinearLayoutManager.HORIZONTAL,
                    false
                )
            mBinding.introduceHeader.recyclerCountryHapDetailsLabel.adapter = countryLabelAdapter
            countryLabelAdapter!!.add(tags)
            mBinding.introduceHeader.recyclerCountryHapDetailsLabel.visibility = View.VISIBLE
        } else {
            mBinding.introduceHeader.recyclerCountryHapDetailsLabel.visibility = View.GONE
        }

        var spanstatusInfo = StringBuilder("")
        if (!it.regionName.isNullOrEmpty()) {
            spanstatusInfo.append(it.regionName)
        }
        if (!it.businessTime.isNullOrEmpty()) {
            spanstatusInfo.append(" | ???????????????" + it.businessTime)
        }
        if (!it.consumeAvg.isNullOrEmpty()) {
            spanstatusInfo.append(" | ??????????????????" + it.consumeAvg + "/???")
        }
//        if (!it.boxNum.isNullOrEmpty()) {
//            spanstatusInfo.append(" | ???????????????" + it.boxNum + "???")
//        }
        mBinding.introduceHeader.tvStatus.text = spanstatusInfo.toString()
        if (!it.boxNum.isNullOrEmpty()) {
            mBinding.introduceHeader.tvCountryBoxNum.text = "???????????????" + it.boxNum + "???"
        }

        //???????????????
        if (!it.introduce.isNullOrEmpty()) {
            mBinding.introduceHeader.tvCountryIntroduce.text = HtmlUtils.html2Str(it.introduce)
            mBinding.introduceHeader.tvCountryIntroduce.visibility = View.VISIBLE
            mBinding.introduceHeader.tvCountryIntroduceTitle.visibility = View.VISIBLE
        } else {
            mBinding.introduceHeader.tvCountryIntroduce.visibility = View.GONE
            mBinding.introduceHeader.tvCountryIntroduceTitle.visibility = View.GONE
        }


        var drawable = if (it.vipResourceStatus.likeStatus) {
            resources.getDrawable(R.mipmap.main_bottom_icon_like_selected)
        } else {
            resources.getDrawable(R.mipmap.main_bottom_icon_like_normal)
        }

        var collect = if (it.vipResourceStatus.collectionStatus) {
            resources.getDrawable(R.mipmap.main_bottom_icon_collect_selected)
        } else {
            resources.getDrawable(R.mipmap.main_bottom_icon_collect_normal)
        }

        // ?????????
        mBinding.tvThumb.text = it.likeNum

        mBinding.tvCollect.text = it.collectionNum

        mBinding.tvCommentNum.text = it.commentNum

        mBinding.introduceHeader.pcvCountryHapDetailComments.updateCommentNum(
            it.commentNum.toInt(),
            id,
            ResourceType.CONTENT_TYPE_AGRITAINMENT,
            it.name
        )

        mBinding.tvThumb.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)

        mBinding.tvCollect.setCompoundDrawablesWithIntrinsicBounds(null, collect, null, null)

        // ????????????
        if (!it.lat.isNullOrEmpty() && !it.lon.isNullOrEmpty()) {
            mModel.countryLat = it.lat.toDouble()
            mModel.countryLng = it.lon.toDouble()
            mBinding.introduceHeader.prvCountryHapDetailRecommend.setLocation(
                LatLng(
                    it.lat.toDouble(),
                    it.lon.toDouble()
                )
            )
            mModel.lat = it.lat
            mModel.lng = it.lon
            mModel.gethMapRecList(ResourceType.CONTENT_TYPE_HOTEL, id)
            mBinding.introduceHeader.prvCountryHapDetailRecommend.visibility = View.VISIBLE
        } else {
            mBinding.introduceHeader.prvCountryHapDetailRecommend.visibility = View.GONE
        }
        //????????????
        setClick(it)
    }

    /**
     *????????????
     */
    private fun setClick(it: CountryHapDetailBean) {
        //????????????
        mBinding.introduceHeader.tvCountryHapDetailsPhone.onNoDoubleClick {
            SystemHelper.callPhone(this, it.phone ?: "")
        }
        //????????????
        mBinding.introduceHeader.tvCountryHapDetailsAddressValue.onNoDoubleClick {
            if (it != null && !it?.lat.isNullOrEmpty() && !it?.lon.isNullOrEmpty()) {
                if (MapNaviUtils.isGdMapInstalled()) {
                    MapNaviUtils.openGaoDeNavi(
                        BaseApplication.context, 0.0, 0.0, null,
                        it!!.lat.toDouble(), it!!.lon.toDouble(),
                        it!!.regionName
                    )
                } else {
                    mModel.toast.postValue("??????????????????????????????????????????")
                }
            } else {
                mModel.toast.postValue("?????????????????????????????????")
            }
        }
        //????????????
        mBinding.introduceHeader.prvCountryHapDetailRecommend.onItemClickTabListener =
            object : ProviderRecommendView.OnItemClickTabListener {
                override fun getMapResourceRecommend(type: String) {
                    if (!it.lat.isNullOrEmpty() && !it.lon.isNullOrEmpty()) {
                        mModel.lat = it.lat
                        mModel.lng = it.lon
                        mModel.gethMapRecList(type, id)
                    } else {
                        doLocation(type)
                    }
                }

            }
        //??????
        mBinding.tvCommentNum.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                ARouter.getInstance()
                .build(ARouterPath.Provider.PROVIDER_POST_COMMENT)
                .withString("id", id)
                .withString("type", CONTENT_TYPE_AGRITAINMENT)
                .navigation()
            } else {
                ToastUtils.showUnLoginMsg()
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
        //??????
        mBinding.tvShare.onNoDoubleClick {
            ToastUtils.showMessage("????????????")
        }
    }

    /**
     *??????banner
     */
    private fun setBanner(it: CountryHapDetailBean) {
        mCountryHapHeaderFrages.clear()
        val dataVideoImages: MutableList<VideoImageBean> = mutableListOf()
        //??????
        if (!it.video.isNullOrEmpty()) {
            dataVideoImages.add(0, VideoImageBean().apply {
                type = 1
                videoUrl = it.video
            })
            isHaveVide = true
            mBinding.countryHapDeHeader.txtCountryDetailVideo.visibility = View.VISIBLE
        }
        val images = it.images.split(",")
//            //??????
        if (!images.isNullOrEmpty()) {
            for (item in images) {
                dataVideoImages.add(VideoImageBean().apply {
                    type = 0
                    imageUrl = item
                })
            }
            mBinding.countryHapDeHeader.txtCountryDetailImages.visibility = View.VISIBLE
            mBinding.countryHapDeHeader.txtCountryDetailImages.text =
                "?????? 1/" + images?.size/*"1/${images.size}"*/
        }
//            //720
        if (!it.panoramaUrl.isNullOrEmpty()) {
            dataVideoImages.add(dataVideoImages.size, VideoImageBean().apply {
                type = 2
                videoUrl = it.panoramaUrl
                imageUrl = it.panoramaCover
                name = it.name
            })
            isHave720 = true
            mBinding.countryHapDeHeader.txtCountryDetailPannaor.visibility = View.VISIBLE
        }
        if (!dataVideoImages.isNullOrEmpty()) {
            mBinding.countryHapDeHeader.cbrCountryDetail.visibility = View.VISIBLE
            mBinding.countryHapDeHeader.cbrCountryDetail.setPages(object : CBViewHolderCreator {
                override fun createHolder(itemView: View?): Holder<*> {
                    return VideoImageHolder(itemView!!, this@CountryHapDetailActivity)
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
                                Intent(this@CountryHapDetailActivity, BigImgActivity::class.java)
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
            mBinding.countryHapDeHeader.cbrCountryDetail.onPageChangeListener =
                object : OnPageChangeListener {
                    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    }

                    @SuppressLint("SetTextI18n")
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
                                mBinding.countryHapDeHeader.txtCountryDetailImages.text =
                                    "?????? " + (pos + 1) + "/" + images?.size/*"${pos + 1}/${images?.size}"*/
                            }
                        }
                    }

                    override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                    }

                }
        } else {
            mBinding.countryHapDeHeader.cbrCountryDetail.visibility = View.GONE
        }


    }


    override fun initData() {
        mModel.id = id
        mModel.getCountryHapDetails()
        // ??????
        mModel.getActivityCommentList(id)
        mModel.getStoryList(CONTENT_TYPE_AGRITAINMENT, id)
        doLocation(ResourceType.CONTENT_TYPE_SCENERY)
        mModel.getCountryInfo(id)
    }

    private fun doLocation(type: String) {
        GaoDeLocation.getInstance().init(this, object : GaoDeLocation.OnGetCurrentLocationLisner {

            override fun onResult(
                adCode: String, result: String, lat_: Double,
                lon_: Double, adcode: String
            ) {
                mModel.lat = lat_.toString()
                mModel.lng = lon_.toString()
                selfLocation = LatLng(lat_, lon_)
                mBinding.introduceHeader.prvCountryHapDetailRecommend.setLocation(
                    LatLng(
                        lat_,
                        lon_
                    )
                )
                mModel.gethMapRecList(type, id)
            }

            override fun onError(errormsg: String) {
//                mModel.getFoodDetail(id.toString())
                mModel.gethMapRecList(type, id)
            }
        })
    }

    //??????????????????
    override fun onPause() {
        super.onPause()
        mBinding.countryHapDeHeader.cbrCountryDetail.pauseVideoPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            EventBus.getDefault().unregister(this)
            GaoDeLocation.getInstance().release()
            mBinding.countryHapDeHeader.cbrCountryDetail?.stopVideoPlayer()
        } catch (e: Exception) {
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRecordEvent(event: UpdateCommentEvent) {
        mModel.getActivityCommentList(id)
        mModel.getCountryHapDetails()
    }
}
