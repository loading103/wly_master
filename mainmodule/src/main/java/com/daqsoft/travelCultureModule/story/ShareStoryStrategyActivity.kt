package com.daqsoft.travelCultureModule.story

import android.content.Intent
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.MainAddStoryStrategyBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.adapter.ViewPagerAdapter
import com.daqsoft.provider.bean.HomeTopicBean
import com.daqsoft.travelCultureModule.story.vm.StoryDetailActivityViewModel
import kotlin.collections.ArrayList

/**
 * @Description 分享故事和攻略页面
 * @ClassName   ShareStoryStrategyActivity
 * @Author      PuHua
 * @Time        2020/2/18 16:48
 *
 * 更新者     邓益千
 * 更新日期   2020年4月27日
 * 更新内容   将话题详情的数据传递到写故事页面
 */
@Route(path = MainARouterPath.MAIN_STORY_STRATEGY_ADD)
class ShareStoryStrategyActivity : TitleBarActivity<MainAddStoryStrategyBinding, StoryDetailActivityViewModel>() {

    /**话题详情*/
    @JvmField
    @Autowired(name = "topic")
    var topic: HomeTopicBean? = null

    @Autowired
    @JvmField
    var type: Int = 0

    @Autowired
    @JvmField
    var id: String? = ""

    override fun getLayout(): Int = R.layout.main_add_story_strategy

    override fun setTitle(): String = "分享时光故事"

    private val viewPagerAdapter by lazy { ViewPagerAdapter(supportFragmentManager) }

    private val writeStoryFragment: WriteStoryFragment by lazy { WriteStoryFragment() }

    val writeStrategyFragment by lazy { WriteStrategyFragment() }

    override fun injectVm(): Class<StoryDetailActivityViewModel> = StoryDetailActivityViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        viewPagerAdapter.addFragment(writeStoryFragment)
        viewPagerAdapter.addFragment(writeStrategyFragment)

        mBinding.vpViewPager.adapter = viewPagerAdapter
        val mTitles = mutableListOf("写故事", "写攻略")
        mBinding.stTab.setViewPager(
            mBinding.vpViewPager, mTitles.toTypedArray(), this,
            ArrayList(viewPagerAdapter.mFragments)
        )
        type = intent.getIntExtra("type", 0)
        mBinding.stTab.currentTab = type
        id = intent.getStringExtra("id")
        var bundle = Bundle()
        bundle.putString("id", id)
        bundle.putParcelable("topic", topic)
        if (type == 0) {
            writeStoryFragment.arguments = bundle
        } else {
            writeStrategyFragment.arguments = bundle
        }

    }

    override fun initData() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (mBinding.vpViewPager.currentItem == 0) {
            writeStoryFragment.onActivityResult(requestCode, resultCode, data)
        } else {
            writeStrategyFragment.onActivityResult(requestCode, resultCode, data)
        }
    }

}