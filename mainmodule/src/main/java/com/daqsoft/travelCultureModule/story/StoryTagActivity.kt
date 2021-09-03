package com.daqsoft.travelCultureModule.story

import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityStoryTagsBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.adapter.ViewPagerAdapter
import com.daqsoft.travelCultureModule.story.vm.StoryTagActivityViewModel

/**
 * @Description 标签墙页面
 * @ClassName   StoryTagActivity
 * @Author      PuHua
 * @Time        2020/1/19 9:31
 */
@Route(path = MainARouterPath.MAIN_STORY_TAG)
class StoryTagActivity : TitleBarActivity<ActivityStoryTagsBinding, StoryTagActivityViewModel>() {

    /**
     * 目录适配器
     */
    private val hotTagAdapter by lazy { ViewPagerAdapter(supportFragmentManager) }

    override fun getLayout(): Int = R.layout.activity_story_tags

    override fun setTitle(): String = getString(R.string.story_all_tag)

    override fun injectVm(): Class<StoryTagActivityViewModel> = StoryTagActivityViewModel::class.java

    override fun initView() {
        mModel.storyHotTagList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                val page = if (it.size % 4 == 0) {
                    it.size / 4
                } else {
                    it.size / 4 + 1
                }

                for (i in 0 until page) {

                    val list = if (i == page - 1) {
                        it.subList(i * 4, it.size)
                    } else {
                        it.subList(i * 4, i * 4 + 4)
                    }

                    hotTagAdapter!!.addFragment(StoryTagFragment(list))
                }
                hotTagAdapter!!.notifyDataSetChanged()
                mBinding.vpIndicator.total = page
                mBinding.vpPager.offscreenPageLimit = page
            }
        })
        mModel.storyTagList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.lvLabels.setLabels(
                    it
                ) { _, _, data -> data.name }
            }
        })

        mBinding.vpIndicator.binViewPager(mBinding.vpPager)

        mBinding.vpPager.adapter = hotTagAdapter
        mBinding.lvLabels.clearAllSelect()
        initEvent()
    }

    override fun initData() {
        mModel.getHotStoryTagList()

        mModel.getStoryTagList()
    }

    private fun initEvent() {
        mBinding.lvLabels.setOnLabelClickListener { _, _, position ->
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_STORY_TAG_DETAIL)
                .withString("id", mModel.storyTagList.value?.get(position)?.id.toString())
                .navigation()
        }
    }
}