package com.daqsoft.travelCultureModule.contentActivity


import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.PageDealUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityContentBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.StudyChannel
import com.daqsoft.provider.businessview.event.UpdateCommentEvent
import com.daqsoft.provider.net.StatisticsRepository
import com.daqsoft.travelCultureModule.contentActivity.vm.ContentActivityStudyModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 资讯列表界面
 * 秦文肖
 */
@Route(path = MainARouterPath.MAIN_CONTENT_STUDY)
class ContentStudyActivity : TitleBarActivity<ActivityContentBinding, ContentActivityStudyModel>() {
    var page = 1
    var pageSize = 10

    @Autowired
    @JvmField
    var channelCode = ""

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



    var contentLsAdapter: ConentLsAdapter? = null

    val contentLsV1Adapter by lazy { ContentLsV1Adapter(this@ContentStudyActivity) }

    override fun getLayout(): Int = R.layout.activity_content

    override fun setTitle(): String =
        if (channelCode == StudyChannel.STUDY_THEME )
            "研学主题"
        else if (channelCode ==  StudyChannel.STUDY_CLASS )
            "研学课程"
        else if (channelCode ==  StudyChannel.STUDY_ZIXUN )
            "研学资讯"
        else if (channelCode == StudyChannel.STUDY_LINE )
            "研学线路"

        else "资讯列表"


    override fun injectVm(): Class<ContentActivityStudyModel> = ContentActivityStudyModel::class.java

    override fun initView() {
        EventBus.getDefault().register(this)
        mBinding.llSearch.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_ALL_SEARCH)
                .navigation()
        }
        val tagLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.rvContent.layoutManager = tagLayoutManager
        contentLsAdapter = ConentLsAdapter(this@ContentStudyActivity)
        contentLsAdapter?.emptyViewShow = false
        mBinding.rvContent.adapter = contentLsAdapter

        mModel.zixunList.observe(this, Observer { response->
            mBinding.mSwipeRefreshLayout.finishRefresh()
            response.datas?.let {
                if (it.size != null && contentLsAdapter != null) {
                    PageDealUtils().pageDeal(page, response, contentLsAdapter!!)
                    if (it.isNotEmpty()){
                        contentLsAdapter?.add(it)
                    }
                } else {
                    if (page == 1) {
                        contentLsAdapter?.emptyViewShow = true
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
            page = 1
            reloadData()
        }
        StatisticsRepository.instance.statisticsPage(title, 1)
    }

    override fun initData() {
        if (linksResourceId.isNullOrEmpty())
            linksResourceId = ""
        if (linksResourceType.isNullOrEmpty())
            linksResourceType = ""
        mModel.getZixunList(linksResourceId, linksResourceType,channelCode,  pageSize.toString(), page.toString())
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        StatisticsRepository.instance.statisticsPage(title, 2)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRecordEvent(event: UpdateCommentEvent) {
    }
}