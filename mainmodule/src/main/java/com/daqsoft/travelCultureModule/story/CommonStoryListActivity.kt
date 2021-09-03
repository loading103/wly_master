package com.daqsoft.travelCultureModule.story

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.PageDealUtils
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityCommonStoryLsBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.StoreBean
import com.daqsoft.provider.businessview.adapter.ProviderStoriesAdapter
import com.daqsoft.travelCultureModule.net.MainRepository
import com.daqsoft.travelCultureModule.story.vm.CommonStoryLsViewModel

/**
 * @Description
 * @ClassName   CommonStoryListActivity
 * @Author      luoyi
 * @Time        2020/9/23 22:15
 */
@Route(path = MainARouterPath.MAIN_COMMON_STOREY_LIST)
class CommonStoryListActivity : TitleBarActivity<ActivityCommonStoryLsBinding, CommonStoryLsViewModel>() {
    @JvmField
    @Autowired
    var resourceType: String = ""

    @JvmField
    @Autowired
    var resourceId: String = ""
    val storiesAdapter by lazy { ProviderStoriesAdapter(this@CommonStoryListActivity) }

    override fun getLayout(): Int {
        return R.layout.activity_common_story_ls
    }

    override fun setTitle(): String {
        return "全部故事"
    }

    override fun injectVm(): Class<CommonStoryLsViewModel> {
        return CommonStoryLsViewModel::class.java
    }

    override fun initView() {
        mBinding.rvStoryList.run {
            layoutManager = StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )
            adapter = storiesAdapter
        }
        mBinding.swpRefreshStoryList.setOnRefreshListener {
            mModel.pageIndex = 1
            mModel.getStoryList(resourceId, resourceType)
        }
        storiesAdapter?.setOnLoadMoreListener {
            mModel.pageIndex++
            mModel.getStoryList(resourceId, resourceType)
        }
        mModel.storyList.observe(this, Observer {
            mBinding.swpRefreshStoryList.finishRefresh()
            PageDealUtils().pageDeal(mModel.pageIndex, it, storiesAdapter!!)
            if (it != null && !it.datas.isNullOrEmpty()) {
                storiesAdapter.add(it.datas!!)
            }
        })
    }

    override fun initData() {
        mModel.getStoryList(resourceId, resourceType)
    }
}