package com.daqsoft.legacyModule.smriti.ui

import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.databinding.ActivityAllIntangibleHeritageStoriesBinding
import com.daqsoft.legacyModule.smriti.vm.AllIntangibleHeritageStoriesViewModel
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.adapter.GridStoryAdapter

/**
 * @package name：com.daqsoft.legacyModule.smriti.ui
 * @date 16/11/2020 上午 9:47
 * @author zp
 * @describe
 */

@Route(path = ARouterPath.LegacyModule.LEGACY_PROJECT_STORY)
class AllIntangibleHeritageStories : TitleBarActivity<ActivityAllIntangibleHeritageStoriesBinding, AllIntangibleHeritageStoriesViewModel>() {

    private val intangibleHeritageStoryAdapter by lazy { GridStoryAdapter(this, GridStoryAdapter.ARENA) }



    @JvmField
    @Autowired
    var resourceId: String? = ""


    @JvmField
    @Autowired
    var resourceType: String? = ""


    override fun getLayout(): Int {
        return R.layout.activity_all_intangible_heritage_stories
    }

    override fun setTitle(): String {
       return "全部故事"
    }

    override fun injectVm(): Class<AllIntangibleHeritageStoriesViewModel> {
        return AllIntangibleHeritageStoriesViewModel::class.java
    }

    override fun initView() {
        initRecycleView()
        initViewObservable()
    }


    private fun initViewObservable() {
        // 非遗故事
        mModel.intangibleHeritageStoryLiveData.observe(this, Observer { response ->
            response.datas?.let { storyList ->
                intangibleHeritageStoryAdapter.clear()
                intangibleHeritageStoryAdapter.add(storyList.toMutableList())
                intangibleHeritageStoryAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun initData() {
        mModel.getIntangibleHeritageStory(resourceId?:"",resourceType?:"")
    }

    private fun initRecycleView(){
        mBinding.story.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = intangibleHeritageStoryAdapter
        }


    }
}


