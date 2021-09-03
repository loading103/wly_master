package com.daqsoft.slowLiveModule.liveContent

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
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.view.popupwindow.AreaSelectPopupWindow
import com.daqsoft.slowLiveModule.R
import com.daqsoft.slowLiveModule.bean.LiveContentBean
import com.daqsoft.slowLiveModule.databinding.SlowLiveAtyLiveContentBinding
import com.daqsoft.slowLiveModule.databinding.SlowLiveItemContentBinding
import com.google.gson.internal.LinkedTreeMap
import com.jakewharton.rxbinding2.view.RxView
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard
import java.text.DecimalFormat
import java.util.ArrayList
import java.util.concurrent.TimeUnit

/**
 * 资讯列表界面
 */
@Route(path = ARouterPath.SlowLiveModule.SLOW_LIVE_CONTENT_ACTIVITY)
internal class ContentActivity :
    TitleBarActivity<SlowLiveAtyLiveContentBinding, LiveContentViewModel>() {

    var page = 1
    var pageSize = 6
    var region = ""

    @JvmField
    @Autowired
    var titleStr: String = ""


    @JvmField
    @Autowired
    var channelCode: String = ""


    private var mAreaSiteSwitch = ""

    /**
     * 弹窗
     */
    private var areaListPopupWindow: AreaSelectPopupWindow? = null

    override fun getLayout(): Int = R.layout.slow_live_aty_live_content

    override fun setTitle(): String = titleStr

    override fun injectVm(): Class<LiveContentViewModel> = LiveContentViewModel::class.java

    val adapter = object :
        RecyclerViewAdapter<SlowLiveItemContentBinding, LiveContentBean>(R.layout.slow_live_item_content) {
        @SuppressLint("CheckResult")
        override fun setVariable(
            mBinding: SlowLiveItemContentBinding,
            position: Int,
            item: LiveContentBean
        ) {
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
            mBinding.tvCiLook.text =
                formatTosepara(item.likeNum) + "赞·" + formatTosepara(item.commentNum) + "评论"
            val video = item.video.toString();
            mBinding.llCiImg.removeAllViews()
            if (video.equals("")) {
                val img_num = item.imageUrls.size
                for (position in item.imageUrls.indices) {
                    val imageView: ImageView = ImageView(this@ContentActivity)
                    val width = resources.displayMetrics.widthPixels
                    val hight = width - resources.getDimension(R.dimen.dp_20) * 2
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
                    val audio = item.audio.toString();

                    if (audio.equals("")) {

                    } else {
                        val map: LinkedTreeMap<String, String> =
                            (item.audio as LinkedTreeMap<String, String>)
                        mBinding.rvCiAudio.visibility = View.VISIBLE
                    }
                }
            } else {
                val map: LinkedTreeMap<String, String> =
                    (item.video as LinkedTreeMap<String, String>)
                val imageView: fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard =
                    fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard(this@ContentActivity)
                val width = resources.displayMetrics.widthPixels
                val new_width = width - resources.getDimension(R.dimen.dp_20) * 2
                val new_hight = (new_width * 167 / 335)
                val param = LinearLayout.LayoutParams(new_width.toInt(), new_hight.toInt(), 1F)
                imageView.layoutParams = param as ViewGroup.LayoutParams?
                imageView.setUp(map.get("url"), JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
                if (map["imgUrl"] != null && map["imgUrl"]!!.isNotEmpty()) {
                    imageView.thumbImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    setImageUrlqwx(
                        imageView.thumbImageView, map.get("imgUrl"), AppCompatResources.getDrawable(
                            BaseApplication.context, R.drawable.placeholder_img_fail_h300
                        ), 5
                    )
                } else {
                    // 异步加载视频缩略图
                    Glide.with(this@ContentActivity)
                        .setDefaultRequestOptions(RequestOptions().frame(1000000).centerCrop())
                        .load(map["url"])
                        .into(imageView.thumbImageView)
                }
                mBinding.llCiImg.addView(imageView)
            }
        }

    }

    override fun initView() {


        mBinding.llSearch.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_ALL_SEARCH)
                .navigation()
        }

        mModel.areas.observe(this, Observer<List<com.daqsoft.baselib.bean.ChildRegion>> {

            val childRegions: MutableList<com.daqsoft.baselib.bean.ChildRegion> = mutableListOf()
            childRegions.add(
                0,
                com.daqsoft.baselib.bean.ChildRegion("", "全部", "", "", ArrayList(), 0, "0")
            )
            childRegions.addAll(it)

            areaListPopupWindow = AreaSelectPopupWindow.getInstance(
                mBinding.root.context, false
            ) { region ->
                mBinding.tvContentArea.text = "" + region.name
                if (mAreaSiteSwitch != region.siteId) {
                    mAreaSiteSwitch = region.siteId
                    page = 1
                    reloadData()
                }
            }

            areaListPopupWindow?.firstData = childRegions
            val temp = ArrayList<com.daqsoft.baselib.bean.ChildRegion>()
            temp.add(0, com.daqsoft.baselib.bean.ChildRegion("", "不限", "", "", ArrayList(), 0, ""))
            areaListPopupWindow?.secondData = temp

        })




        mBinding.tvContentArea.onNoDoubleClick {
            areaListPopupWindow?.show(mBinding.tvContentArea)
        }

        val tagLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.rvContent.layoutManager = tagLayoutManager
        mBinding.rvContent.adapter = adapter
        mModel.contentList.observe(this, Observer {

            mBinding.mSwipeRefreshLayout.finishRefresh()
            pageDeal(page, it, adapter)
            adapter.add(it)

        })
        adapter.setOnLoadMoreListener {
            page++
            reloadData()
        }
        mBinding.mSwipeRefreshLayout.setOnRefreshListener {
//            mBinding.mSwipeRefreshLayout.isRefreshing = false
            page = 1
            reloadData()
        }
    }

    override fun initData() {

        mModel.getChildRegions()

        mModel.getContentList(
            mAreaSiteSwitch,
            "",
            "",
            "publishTime",
            pageSize.toString(),
            page.toString(),
            region,
            channelCode
        )
    }

    private fun pageDeal(
        page: Int?, response: MutableList<LiveContentBean>, adapter:
        RecyclerViewAdapter<*, *>
    ) {
        if (page == null) {
            return
        }
        if (page == 1) {
            adapter.clear()
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
        JCVideoPlayer.releaseAllVideos()
        super.onPause()
    }

    override fun onDestroy() {
        JCVideoPlayer.releaseAllVideos()
        super.onDestroy()
    }
}