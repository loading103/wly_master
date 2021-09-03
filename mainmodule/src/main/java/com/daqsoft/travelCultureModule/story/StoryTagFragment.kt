package com.daqsoft.travelCultureModule.story

import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.FragmentStoryTagHotBinding
import com.daqsoft.mainmodule.databinding.ItemStoryTagBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.HomeStoryTagBean

/**
 * @des 首页第一部分菜单
 * @author PuHua
 * @Date 2019/12/5 17:52
 */
class StoryTagFragment(var menus: MutableList<HomeStoryTagBean>) : BaseFragment<FragmentStoryTagHotBinding, StoryTagFragmentViewModel>() {

    override fun getLayout(): Int = R.layout.fragment_story_tag_hot

    override fun initData() {

        adapter.add(this.menus)
    }

    override fun initView() {
        val gridLayoutManager = GridLayoutManager(context, 2)
        mBinding.recyclerView.layoutManager = gridLayoutManager
        mBinding.recyclerView.adapter = adapter
    }

    private val adapter = object :
        RecyclerViewAdapter<ItemStoryTagBinding, HomeStoryTagBean>(
            R.layout.item_story_tag
        ) {
        override fun setVariable(mBinding: ItemStoryTagBinding, position: Int, item: HomeStoryTagBean) {
            mBinding.url = item.cover
            mBinding.name = item.name
            mBinding.cover.background.alpha = 255 - 153
            if (!item.name.isNullOrEmpty()) {
                mBinding.tvType.text = "#${item.name}#"
            }
            mBinding.tvStoryNumber.text = if (item.storyNum != null && item.storyNum.toInt() > 0)
                getString(R.string.home_story_number, item.storyNum)
            else getString(R.string.home_story_tag)
            mBinding.root.onNoDoubleClick {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_STORY_TAG_DETAIL)
                    .withString("id", item.id)
                    .navigation()
            }
        }

    }

    override fun injectVm(): Class<StoryTagFragmentViewModel> = StoryTagFragmentViewModel::class.java

}


/**
 * @des 首页第一部分的viewMode
 * @author PuHua
 * @Date 2019/12/5 17:54
 */
class StoryTagFragmentViewModel : BaseViewModel() {

}