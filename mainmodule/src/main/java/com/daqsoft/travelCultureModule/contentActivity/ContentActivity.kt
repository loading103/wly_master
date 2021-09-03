package com.daqsoft.travelCultureModule.contentActivity


import android.graphics.Rect
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseApplication.Companion.context
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.dp
import com.daqsoft.baselib.utils.PageDealUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityContentBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.businessview.event.UpdateCommentEvent
import com.daqsoft.provider.net.StatisticsRepository
import com.daqsoft.provider.view.popupwindow.AreaSelectPopupWindow
import com.daqsoft.travelCultureModule.clubActivity.bean.ClubZixunBean
import com.daqsoft.travelCultureModule.contentActivity.vm.ContentActivityViewModel
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.DecimalFormat

/**
 * 资讯列表界面
 * 秦文肖
 */
@Route(path = MainARouterPath.MAIN_CONTENT)
class ContentActivity : TitleBarActivity<ActivityContentBinding, ContentActivityViewModel>() {
    var page = 1
    var pageSize = 10
    var region = ""
    var areaSiteSwitch = ""

    @Autowired
    @JvmField
    var channelCode = ""

    @Autowired
    @JvmField
    var topTitle = ""
    /**
     * 关联资源类型
     */
    @Autowired
    @JvmField
    var linksResourceType = ""

    /**
     * 关联资源ID
     */
    @Autowired
    @JvmField
    var linksResourceId = ""

    /**
     * 标题
     */
    @Autowired
    @JvmField
    var titleStr = ""


    var contentLsAdapter: ConentLsAdapter? = null

    val contentLsV1Adapter by lazy { ContentLsV1Adapter(this@ContentActivity) }

    override fun getLayout(): Int = R.layout.activity_content

    override fun setTitle(): String = if (!titleStr.isNullOrEmpty()) {
        titleStr
    } else if (channelCode == "lineChannel" && topTitle.isNullOrEmpty())
        getString(R.string.culture_line_ls)
    else if (channelCode == "lineChannel" && !topTitle.isNullOrEmpty())
        getString(R.string.culture_fine_ls)
    else if (channelCode == "systemChannel") { getString(R.string.culture_content_ls) }
    else "资讯列表"


    override fun injectVm(): Class<ContentActivityViewModel> = ContentActivityViewModel::class.java

    /**
     * 地区选择弹窗窗口
     */
    private var areaListPopWindow: AreaSelectPopupWindow? = null

    override fun initView() {

        EventBus.getDefault().register(this)
        mBinding.llSearch.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_ALL_SEARCH)
                .navigation()
        }

        mModel.getChildRegions()

        // 地区
        mModel.areas.observe(this, Observer {

            if (areaListPopWindow == null) {
                it.add(0, com.daqsoft.baselib.bean.ChildRegion("", "全部", "", "", emptyList()))
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
                val temp: MutableList<com.daqsoft.baselib.bean.ChildRegion> = mutableListOf()
                temp.add(0, com.daqsoft.baselib.bean.ChildRegion("", "不限", "", "", emptyList()))
                areaListPopWindow?.secondData = ArrayList(temp)
            }


        })


        mBinding.tvContentArea.setOnClickListener {
            //            pop.show()
            areaListPopWindow?.show(mBinding.tvContentArea)
        }
        val tagLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.rvContent.layoutManager = tagLayoutManager
        if (channelCode != null && (channelCode == "systemChannel" || channelCode == "lineChannel")) {
            contentLsAdapter = ConentLsAdapter(this@ContentActivity)
            contentLsAdapter?.emptyViewShow = false
            mBinding.rvContent.adapter = contentLsAdapter
        } else {
            contentLsAdapter?.emptyViewShow = false
            mBinding.rvContent.adapter = contentLsV1Adapter
        }

        mModel.zixunList.observe(this, Observer { response->
            mBinding.mSwipeRefreshLayout.finishRefresh()
            response.datas?.let {
                if (channelCode != null && (channelCode == "systemChannel" || channelCode == "lineChannel")) {
                    if (it.size != null && contentLsAdapter != null) {
//                    pageDeal(page, it, contentLsAdapter!!)
                        PageDealUtils().pageDeal(page, response, contentLsAdapter!!)
                        if (it.isNotEmpty()){
                            contentLsAdapter?.add(it)
                        }
                    } else {
                        if (page == 1) {
                            contentLsAdapter?.emptyViewShow = true
                        }
                    }
                } else {
                    if (!it.isNullOrEmpty()) {
//                    pageDeal(page, it, contentLsV1Adapter!!)
                        PageDealUtils().pageDeal(page, response, contentLsV1Adapter!!)
                        if (it.isNotEmpty()){
                            contentLsV1Adapter?.add(it)
                        }
                    } else {
                        if (page == 1) {
                            contentLsV1Adapter?.emptyViewShow = true
                        }
                    }
                }
            }
        })
        contentLsV1Adapter?.setOnLoadMoreListener {
            page++
            reloadData()
        }
        contentLsAdapter?.setOnLoadMoreListener {
            page++
            reloadData()
        }
        mBinding.mSwipeRefreshLayout.setOnRefreshListener {
            //            mBinding.mSwipeRefreshLayout.isRefreshing = false
            page = 1
            reloadData()
        }
        StatisticsRepository.instance.statisticsPage(title, 1)
    }

    override fun initData() {
        mModel.channelCode = channelCode
        if (linksResourceId.isNullOrEmpty())
            linksResourceId = ""
        if (linksResourceType.isNullOrEmpty())
            linksResourceType = ""
        mModel.getZixunList(
            linksResourceId, linksResourceType, "publishTime", pageSize.toString(), page.toString(),
            region, areaSiteSwitch
        )
    }

    private fun pageDeal(
        page: Int?, response: MutableList<ClubZixunBean>, adapter:
        RecyclerViewAdapter<*, ClubZixunBean>
    ) {
        if (page == null) {
            return
        }
        if (page == 1) {
            adapter.clear()
        }
        if (!response.isNullOrEmpty()) {
            adapter.add(response)
        }
        if (response == null) {
            adapter.loadEnd()
            return
        }
        if (response.size > pageSize) {
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
        EventBus.getDefault().unregister(this)
        JCVideoPlayer.releaseAllVideos()
        StatisticsRepository.instance.statisticsPage(title, 2)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRecordEvent(event: UpdateCommentEvent) {
    }
}