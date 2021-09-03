package com.daqsoft.travelCultureModule.country.ui


import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.adapter.setCenterCropImage
import com.daqsoft.baselib.adapter.setImageUrlqwx
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.baselib.utils.PageDealUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityContentBinding
import com.daqsoft.mainmodule.databinding.ItemContentBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.view.popupwindow.AreaSelectPopupWindow
import com.daqsoft.travelCultureModule.clubActivity.bean.ClubZixunBean
import com.daqsoft.travelCultureModule.contentActivity.ConentLsAdapter
import com.daqsoft.travelCultureModule.contentActivity.vm.ContentActivityViewModel
import com.google.gson.internal.LinkedTreeMap
import com.jakewharton.rxbinding2.view.RxView
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

/**
 * 资讯列表界面
 */
@Route(path = ARouterPath.CountryModule.COUNTRY_CONTENT_ACTIVITY)
class CountryContentActivity : TitleBarActivity<ActivityContentBinding, ContentActivityViewModel>() {
    var page = 1
    var pageSize = 6
    var region = ""
    var areaSiteSwitch = ""

    @Autowired
    @JvmField
    var channelCode = ""
    @Autowired
    @JvmField
    var jumpTitle = ""

    var contentLsAdapter: ConentLsAdapter? = null
    //    lateinit var pop: ListPopupWindow<Any>
    override fun getLayout(): Int = R.layout.activity_content

    override fun setTitle(): String = jumpTitle


    override fun injectVm(): Class<ContentActivityViewModel> = ContentActivityViewModel::class.java
    val adapter = object : RecyclerViewAdapter<ItemContentBinding, ClubZixunBean>(R.layout.item_content) {
        @SuppressLint("CheckResult")
        override fun setVariable(mBinding: ItemContentBinding, position: Int, item: ClubZixunBean) {
            RxView.clicks(mBinding.llContent)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    if (item.contentType.equals("IMAGE")) {
                        ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_IMG)
                            .withString("id", item.id.toString())
                            .navigation()
                    } else {
                        ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_INFO)
                            .withString("id", item.id.toString())
                            .withString("contentTitle", "资讯详情")
                            .navigation()
                    }
                }
            mBinding.tvCiTilteName.text = item.title
            if (item.summary.equals("")) {
                mBinding.tvCiContent.visibility = View.GONE
            } else {
                mBinding.tvCiContent.text = item.summary
            }
            mBinding.tvCiName.text = item.createCompany
            setCenterCropImage(
                mBinding.ivCiCommanyImg, item.createCompanyLogo, AppCompatResources.getDrawable(
                    BaseApplication.context, R.drawable.placeholder_img_fail_h158
                ), true
            )
            //formatTosepara(item.showNum)+"浏览·"+
            mBinding.tvCiLook.text = formatTosepara(item.likeNum) + "赞·" + formatTosepara(item.commentNum) + "评论"
            var video = item.video.toString();
            mBinding.llCiImg.removeAllViews()
            if (video.equals("")) {
                var img_num = item.imageUrls.size
                for (position in item.imageUrls.indices) {
                    val imageView: ImageView = ImageView(this@CountryContentActivity)
                    var width = resources.displayMetrics.widthPixels
                    var hight = width - resources.getDimension(R.dimen.dp_20) * 2
                    var new_wid = (hight - 60) / 3
                    var new_hight = (new_wid * 81 / 109)
                    if (img_num == 1) {
                        new_wid = hight
                        new_hight = new_wid * 167 / 335
                    } else if (img_num == 2) {
                        new_wid = (hight - 40) / 2
                        new_hight = new_wid * 124 / 165
                    }
                    val param = LinearLayout.LayoutParams(new_wid.toInt(), new_hight.toInt())
                    param.leftMargin = 20
                    imageView.layoutParams = param as ViewGroup.LayoutParams?
                    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                    setImageUrlqwx(
                        imageView, item.imageUrls[position].url, AppCompatResources.getDrawable(
                            BaseApplication.context, R.drawable.placeholder_img_fail_h300
                        ), 5
                    )
                    mBinding.llCiImg.addView(imageView)
                }
                if (img_num == 0) {
                    var audio = item.audio.toString();

                    if (audio.equals("")) {

                    } else {
                        var map: LinkedTreeMap<String, String> = (item.audio as LinkedTreeMap<String, String>)
                        mBinding.rvCiAudio.visibility = View.VISIBLE
                    }
                }
            } else {
                var map: LinkedTreeMap<String, String> = (item.video as LinkedTreeMap<String, String>)
                var imageView: fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard = fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard(this@CountryContentActivity)
                var width = resources.displayMetrics.widthPixels
                var new_width = width - resources.getDimension(R.dimen.dp_20) * 2
                var new_hight = (new_width * 167 / 335)
                var param = LinearLayout.LayoutParams(new_width.toInt(), new_hight.toInt(), 1F)
                imageView.layoutParams = param as ViewGroup.LayoutParams?
                imageView.setUp(map.get("url"), JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
                if (map.get("imgUrl").equals("")) {
                    imageView.thumbImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    setImageUrlqwx(
                        imageView.thumbImageView, map.get("imgUrl"), AppCompatResources.getDrawable(
                            BaseApplication.context, R.drawable.placeholder_img_fail_h300
                        ), 5
                    )
                } else {
                    // 异步加载视频缩略图
                    Glide.with(this@CountryContentActivity)
                        .setDefaultRequestOptions(RequestOptions().frame(1000000).centerCrop())
                        .load(map.get("url"))
                        .into(imageView.thumbImageView)
                }
                mBinding.llCiImg.addView(imageView)
            }
        }

    }

    /**
     * 地区选择弹窗窗口
     */
    private var areaListPopWindow: AreaSelectPopupWindow? = null

    override fun initView() {

        mBinding.llSearch.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_ALL_SEARCH)
                .navigation()
        }

        mModel.getChildRegions();
//        mModel.areas.observe(this@ContentActivity, Observer {
//            pop = ListPopupWindow.getInstance(mBinding.tvContentArea, it, ListPopupWindow.WindowDataBack<ChildRegion1> {
//                region = it.region
//                mBinding.tvContentArea.text = it.name
//                reloadData()
//            })
//        })

        // 地区
        mModel.areas.observe(this, Observer {

            if (areaListPopWindow == null) {
                it.add(0, ChildRegion("", "全部", "", "", emptyList()))
                areaListPopWindow =
                    AreaSelectPopupWindow.getInstance(
                        BaseApplication.context, false
                    ) { item ->
                        areaSiteSwitch = item.siteId
                        region = item.region
                        mBinding.tvContentArea.text = item.name
                        reloadData()
                    }
                areaListPopWindow?.firstData = it
                val temp: MutableList<ChildRegion> = mutableListOf()
                temp.add(0, ChildRegion("", "不限", "", "", emptyList()))
                areaListPopWindow?.secondData = ArrayList(temp)
            }


        })


        mBinding.tvContentArea.setOnClickListener {
            //            pop.show()
            areaListPopWindow?.show(mBinding.tvContentArea)
        }
        contentLsAdapter = ConentLsAdapter(this@CountryContentActivity)
        contentLsAdapter?.emptyViewShow = false
        val tagLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.rvContent.layoutManager = tagLayoutManager
        mBinding.rvContent.adapter = contentLsAdapter
        mModel.zixunList.observe(this, Observer { response ->
            mBinding.mSwipeRefreshLayout.finishRefresh()

            response.datas?.let {
                if (it.size != null) {
//                var data=it
//                pageDeal(page, it, adapter)
//                adapter.add(data)


//                    pageDeal(page, it, contentLsAdapter!!)
                    PageDealUtils().pageDeal(page,response,adapter)
                    if (it.isNotEmpty()){
                        contentLsAdapter?.add(it)
                    }

                }
            }
        })
        contentLsAdapter?.setOnLoadMoreListener {
            page++
            reloadData()
        }
        mBinding.mSwipeRefreshLayout.setOnRefreshListener {
//            mBinding.mSwipeRefreshLayout.isRefreshing = false
            page = 1
            contentLsAdapter?.emptyViewShow = false
            reloadData()
        }
    }

    override fun initData() {
        mModel.channelCode = channelCode
        mModel.getZixunList("", "", "publishTime", pageSize.toString(), page.toString(), region, areaSiteSwitch)
    }

    private fun pageDeal(
        page: Int?, response: MutableList<ClubZixunBean>, adapter:
        RecyclerViewAdapter<*, *>
    ) {
        if (page == null) {
            return
        }
        if (page == 1) {
            adapter.clear()
            if (response.isNullOrEmpty()) {
                adapter.emptyViewShow = true
            }
        }
        if (response == null) {
            adapter.loadEnd()
            return
        }
        if (response.size >= pageSize) {
            adapter.loadComplete()
        } else {
            adapter.loadEnd()
        }
    }

    fun formatTosepara(data: Int): String? {
        val df = DecimalFormat("#,###")
        return df.format(data)
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