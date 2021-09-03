package com.daqsoft.travelCultureModule.citycard

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.daqsoft.baselib.adapter.setImageUrlqwx
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.dp
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.baselib.widgets.layoutmanager.FullyGridLayoutManager
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityCurrentCityInfoBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.thetravelcloudwithculture.home.bean.CityCardDetail
import com.daqsoft.provider.bean.HomeMenu
import com.daqsoft.provider.view.GridDecoration
import com.daqsoft.travelCultureModule.citycard.adapter.CityListAdapter
import com.daqsoft.travelCultureModule.citycard.vm.CityCardViewModel
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard


/**
 * @des 当前主站点、主城市名片的介绍
 * @author HuangXi
 * @Date 2020/4/13 14:52
 */
@Route(path = MainARouterPath.MAIN_MDD_CURRENT_CITY_INFO)
class
CurrentCityInfoActivity :
    TitleBarActivity<ActivityCurrentCityInfoBinding, CityCardViewModel>(), View.OnClickListener {
    override fun getLayout(): Int = R.layout.activity_current_city_info

    override fun setTitle(): String = getString(R.string.culture_citycard_ls)

    /**
     * 文本内容高度
     */
    var webViewHeight = 0

    /**
     * 站点id
     */
    var siteId: String? = ""

    override fun injectVm(): Class<CityCardViewModel> = CityCardViewModel::class.java
    var viewList = ArrayList<View>()
    var mList = ArrayList<String>()
    var maxWebViewHeight: Int = 0
    lateinit var curCityBean: CityCardDetail
    val madapter: CityListAdapter by lazy { CityListAdapter(this@CurrentCityInfoActivity) }

    private val menus = mutableListOf<HomeMenu>()
    override fun initView() {
        maxWebViewHeight = Utils.dip2px(this, 189f).toInt()
        mBinding.webViewCityInfo.isScrollContainer = false
        mBinding.webViewCityInfo.isVerticalScrollBarEnabled = false
        mBinding.webViewCityInfo.isHorizontalScrollBarEnabled = false
        mBinding.ivContentMore.isSelected = false
        var ninckName: String = SPUtils.getInstance().getString(SPKey.NICK_NAME)

        mBinding.recyclerView.apply {
            layoutManager= FullyGridLayoutManager(this@CurrentCityInfoActivity, 2)
            adapter = madapter
            addItemDecoration(GridDecoration(2,12.dp, includeEdge = true, isRemoveBoth = true))
        }
        mBinding.tvCurrentCityHello.setText(
            "Hi," + if (!ninckName.isNullOrEmpty()) {
                "${ninckName},"
            } else {
                ""
            } + "这里是"
        )
        mBinding.webViewCityInfo.setCallBack {
            webViewHeight = it
            if (it > maxWebViewHeight) {
                mBinding.webViewCityInfo.layoutParams =
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        Utils.dip2px(this, 189f).toInt()
                    )
                mBinding.ivContentMore.visibility = View.VISIBLE
            } else if (it < maxWebViewHeight) {
                mBinding.webViewCityInfo.layoutParams =
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
            }
        }
        mBinding.ivContentMore.setOnClickListener {
            if (mBinding.ivContentMore.isSelected) {
                mBinding.ivContentMore.isSelected = false
                mBinding.ivContentMore.setImageResource(R.mipmap.main_arrow_down)
                if (webViewHeight >= Utils.dip2px(this, 189f).toInt()) {
                    mBinding.webViewCityInfo.layoutParams =
                        LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            Utils.dip2px(this, 189f).toInt()
                        )
                    webViewHeight = Utils.dip2px(this, 189f).toInt()
                }
            } else {
                mBinding.ivContentMore.isSelected = true
                mBinding.ivContentMore.setImageResource(R.mipmap.main_arrow_up)
                mBinding.webViewCityInfo.layoutParams =
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                webViewHeight += 100
            }

        }
        // 地区县

        mBinding.mSwipeRefreshLayout.setOnRefreshListener {
//            mBinding.mSwipeRefreshLayout.isRefreshing = false
            mList.clear()
            adapterCityInfo.notifyDataSetChanged()
            reloadData()
        }
        mBinding.tvCity720.onNoDoubleClick {
            curCityBean?.let {
                ARouter.getInstance()
                    .build(ARouterPath.Provider.WEB_ACTIVITY)
                    .withString("mTitle", curCityBean.name)
                    .withString("html", curCityBean.panoramaUrl)
                    .navigation()
            }
        }
        mBinding.tvCityVideo.onNoDoubleClick {
            var position = mBinding.vpCityHead.currentItem
            if (position != 0) {
                mBinding.vpCityHead.currentItem = 0
            }
        }
        mBinding.tvCityImgs.onNoDoubleClick {
            curCityBean?.let {
                var videoUrls: List<String> = ArrayList()
                var videoSize: Int = 0
                if (!it.videoEx.isNullOrEmpty()) {
                    videoUrls = curCityBean.videoEx!!.split(",")
                    if (!videoUrls.isNullOrEmpty()) {
                        videoSize = videoUrls.size
                    }
                }
                if (videoSize > 0) {
                    mBinding.vpCityHead.setCurrentItem(videoSize)
                } else {
                    mBinding.vpCityHead.setCurrentItem(0)
                }
            }
        }
    }


    override fun initData() {
        siteId = intent.getStringExtra("id")
        if (!siteId.isNullOrEmpty()) {
            mModel.getCityCard(siteId!!)
        }


    }

    override fun notifyData() {
        super.notifyData()
        mModel.cityInfo.observe(this, Observer {
            curCityBean = it
//            mBinding.viewUsername.text = "{Hi,${SPUtils.getInstance().getString(SPKey.NICK_NAME)},这里是}"
            mModel.getWeather(it.region)
            mModel.getMDDCity(siteId!!, "50", "city", it.region)
            mModel.getMDDCity(siteId!!, "50", "county", it.region)
            mBinding.citybean = it
            var videoUrls: List<String> = ArrayList()
            // 多视频处理
            if (!curCityBean.videoEx.isNullOrEmpty()) {
                videoUrls = curCityBean.videoEx!!.split(",")
                if (!videoUrls.isNullOrEmpty()) {
                    for (item in videoUrls) {
                        mList.add(item)
                    }
                    mBinding.videovisible = true
                } else {
                    mBinding.videovisible = false
                }
            } else {
                mBinding.videovisible = false
            }
            if (!curCityBean.panoramaUrl.isNullOrEmpty()) {
                mBinding.tvCity720.visibility = View.VISIBLE
            }
            if (!curCityBean.images.isNullOrEmpty()) {
                for (position in curCityBean.images.indices) {
                    mList.add(curCityBean.images[position])
                }
                mBinding.imgevisible = true
            } else {
                mBinding.imgevisible = false
            }
            if (mList.size <= 0) {
                mBinding.rlCityImgs.visibility = View.GONE
            }
            mBinding.vpCityHead.setAdapter(adapterCityInfo)

            mBinding.webViewCityInfo.settings.javaScriptEnabled = true
            mBinding.webViewCityInfo.loadDataWithBaseURL(
                null,
                StringUtil.getHtml(curCityBean.introduce),
                "text/html",
                "utf-8",
                null
            )

        })
        mModel.mddCityList.observe(this, Observer {
            if (it != null) {
                if (it.size > 0) {
                    mBinding.llMddCity.visibility = View.VISIBLE
                    mBinding.mgvMddCity.numColumns = 2
                    var adapter = gvMDDCityAdapter(it)
                    mBinding.mgvMddCity.adapter = adapter
                } else {
                    mBinding.llMddCity.visibility = View.GONE
                    mBinding.tvMddCity.visibility = View.GONE
                }
            }
        })
//        mModel.mddDQXList.observe(this, Observer {
//            if (it != null) {
//                if (it.size > 0) {
//                    mBinding.llMddDqx.visibility = View.VISIBLE
//                    mBinding.mgvMddDqx.numColumns = 2
//                    var adapter = gvMDDCityAdapter(it)
//                    mBinding.mgvMddDqx.adapter = adapter
//                } else {
//                    mBinding.llMddDqx.visibility = View.GONE
//                    mBinding.tvMddDqx.visibility = View.GONE
//                }
//            }
//        })

        mModel.mddDQXList.observe(this, Observer {
            if (it != null) {
                if (it.size > 0) {
                    mBinding.llMddDqx.visibility = View.VISIBLE
                    madapter.add(it)
                } else {
                    mBinding.llMddDqx.visibility = View.GONE
                    mBinding.tvMddDqx.visibility = View.GONE
                }
            }
        })
        // 天气
        mModel.weather.observe(this, Observer {
            mBinding.weather = it
        })
    }

    var adapterCityInfo = object : PagerAdapter() {
        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(viewList[position])
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            var videoUrls: List<String> = ArrayList()
            var videoSize: Int = 0
            if (!curCityBean.videoEx.isNullOrEmpty()) {
                videoUrls = curCityBean.videoEx!!.split(",")
                if (!videoUrls.isNullOrEmpty()) {
                    videoSize = videoUrls.size
                }
            }
            if (videoSize > 0 && position < videoSize) {
                var imageView: fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard =
                    fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard(this@CurrentCityInfoActivity)

                var param = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                imageView.layoutParams = param as ViewGroup.LayoutParams?
                imageView.setUp(videoUrls[position], JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
                var videoCover: String = StringUtil.getVideoCoverUrl(videoUrls[position])
                imageView.thumbImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                GlideModuleUtil.loadDqImage(videoCover, imageView.thumbImageView)


                container.addView(imageView)
                viewList.add(imageView)
                return imageView
            } else {
                var imageView = ImageView(this@CurrentCityInfoActivity)
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP)
                setImageUrlqwx(
                    imageView, mList.get(position), AppCompatResources.getDrawable(
                        BaseApplication.context, R.drawable.placeholder_img_fail_h300
                    ), 5
                )
                container.addView(imageView)
                viewList.add(imageView)
                return imageView
            }
        }

        override fun getCount(): Int {
            return mList.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_city_name -> {
                ARouter.getInstance().build(MainARouterPath.MAIN_MDD_LIST)
                    .navigation()
            }
            R.id.tv_city_dqx -> {
                ARouter.getInstance().build(MainARouterPath.MAIN_MDD_LIST)
                    .withString("dqx", "dqx")
                    .navigation()
            }
            R.id.tv_city_brand -> {
                ARouter.getInstance().build(MainARouterPath.MAIN_BRANCH_LIST)
                    .withString("siteId", curCityBean.siteId.toString())
                    .navigation()
            }
            R.id.tv_city_secnic -> {

            }
            R.id.tv_city_activity -> {

            }
            R.id.tv_city_changguan -> {

            }
            R.id.tv_city_lvyouluxian -> {

            }
            R.id.tv_city_hotel -> {

            }
            R.id.tv_city_food -> {

            }
            R.id.tv_city_story -> {

            }
        }
    }

    override fun onPause() {
        super.onPause()
        JCVideoPlayer.releaseAllVideos()
    }

    override fun onDestroy() {
        super.onDestroy()
        JCVideoPlayer.releaseAllVideos()
    }
}
