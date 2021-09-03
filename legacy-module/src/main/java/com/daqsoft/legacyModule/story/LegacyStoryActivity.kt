package com.daqsoft.legacyModule.story

import android.view.TextureView
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.adapter.GridWorksAdapter
import com.daqsoft.legacyModule.adapter.LegacyStoryLinerAdapter
import com.daqsoft.legacyModule.bean.LegacyStoryListBean
import com.daqsoft.legacyModule.databinding.LegacyModuleActivityStoryBinding
import com.daqsoft.legacyModule.databinding.LegacyModuleItemStoryTagBinding
import com.daqsoft.legacyModule.rv.dsl.linear
import com.daqsoft.legacyModule.sp2px
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath


/***
 * @des     全部故事
 * @class   AllStoryActivity
 * @author  Wongxd
 * @date    2020-4-24  13:38
 */
@Route(path = ARouterPath.LegacyModule.LEGACY_STORY_ACTIVITY)
internal class LegacyStoryActivity : TitleBarActivity<LegacyModuleActivityStoryBinding, LegacyStoryViewModel>() {

    override fun getLayout(): Int = R.layout.legacy_module_activity_story

    override fun setTitle(): String = getString(R.string.legacy_module_legacy_story)

    override fun injectVm(): Class<LegacyStoryViewModel> = LegacyStoryViewModel::class.java


    private var storyAdapter:LegacyStoryLinerAdapter? = null
    private var storyGridAdapter:GridWorksAdapter? = null

    private var mTotalScrollY = 0

    private var totalScrollYSlop = 0

    override fun initView() {



        initStoryTypeTab()

        initViewModel()
        switchStoryLayout()
    }

    private fun initStoryTypeTab() {
        fun setTabStatus(tv: TextView, v: View, checked: Boolean) {
            tv.setTextColor(resources.getColor(if (checked) R.color.color_333 else R.color.color_666))
            tv.textSize = if (checked) 16f else 14f
            v.isVisible = checked
            tv.isSelected = checked
        }

        fun hotTabClick() {
            setTabStatus(mBinding.tvHot, mBinding.vHot, true)
            setTabStatus(mBinding.tvNew, mBinding.vNew, false)
            switchStoryLayout(0)
            mModel.orderType = "likeNumAndCommentNumAndShowNum"
            mModel.pageManager.initPageIndex()
            mModel.getStoryList()

        }
        hotTabClick()

        fun newTabClick() {
            setTabStatus(mBinding.tvHot, mBinding.vHot, false)
            setTabStatus(mBinding.tvNew, mBinding.vNew, true)
            switchStoryLayout(1)
            mModel.pageManager.initPageIndex()
            mModel.orderType = ""
            mModel.getStoryList()
        }

        mBinding.llHot.onNoDoubleClick { hotTabClick() }

        mBinding.llNew.onNoDoubleClick { newTabClick() }
    }
    /**
     * 切换故事布局
     * @param type 0 列表布局 1 瀑布流布局
     */
    private fun switchStoryLayout(type: Int = 0) {
        if (type == 0) {
            if(storyAdapter == null){
                storyAdapter = LegacyStoryLinerAdapter(this)
            }
            storyAdapter?.setOnLoadMoreListener {
                mModel.pageManager.pageIndex++
                mModel.getStoryList()
            }
            val storyLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            mBinding.rvStory.layoutManager = storyLayoutManager
            mBinding.rvStory.adapter = storyAdapter
        } else {
            if(storyGridAdapter == null){
                storyGridAdapter = GridWorksAdapter(this)
            }
            storyGridAdapter?.setOnLoadMoreListener {
                mModel.pageManager.pageIndex++
                mModel.getStoryList()
            }
            val storyLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            mBinding.rvStory.layoutManager = storyLayoutManager
            mBinding.rvStory.adapter = storyGridAdapter
        }
    }



    private fun initViewModel() {

        //故事标签
        mModel.storyTagList.observe(this, Observer { storyTagList ->

            mBinding.rvStoryType.isVisible = storyTagList.isNotEmpty()

            if (storyTagList.isNullOrEmpty()) return@Observer


            mBinding.rvStoryType.linear {

                orientation = LinearLayoutManager.HORIZONTAL

                storyTagList.forEach { tagItem ->

                    itemDsl {
                        xml(R.layout.legacy_module_item_story_tag)
                        render {
                            val binding = DataBindingUtil.bind<LegacyModuleItemStoryTagBinding>(it)
                            binding?.tag = tagItem

                            it.onNoDoubleClick {
                                ARouter.getInstance()
                                    .build(MainARouterPath.MAIN_STORY_TAG_DETAIL)
                                    .withString("id", tagItem.id.toString())
                                    .withString("module","intangible_heritage")
                                    .navigation()
                            }

                        }
                    }

                }
            }

        })


        //故事列表
        mModel.storyList.observe(this, Observer { storyList ->
            dissMissLoadingDialog()
            pageDel(storyList)
    })
    }

    private fun pageDel(list:List<LegacyStoryListBean>){

        if (mModel.pageManager.isFirstIndex) {
            mBinding.rvStory.isVisible = list.isNotEmpty()
            if(mBinding.tvHot.isSelected){
                storyAdapter?.clearNotify()
            }else{
                storyGridAdapter?.clearNotify()

            }
        }
        if (!list.isNullOrEmpty()) {
            if(mBinding.tvHot.isSelected){
                storyAdapter?.add(list.toMutableList())
            }else{
                storyGridAdapter?.add(list.toMutableList())

            }        }
        if (list.isNullOrEmpty() || list.size < mModel.pageManager.pageSize) {
            if(mBinding.tvHot.isSelected){
                storyAdapter?.loadEnd()
            }else{
                storyGridAdapter?.loadEnd()
            }
        } else {
            if(mBinding.tvHot.isSelected){
                storyAdapter?.loadComplete()
            }else{
                storyGridAdapter?.loadComplete()
            }
        }
    }


    override fun initData() {
        mModel.pageManager.initPageIndex()
        mModel.getStoryTagList()
    }
}