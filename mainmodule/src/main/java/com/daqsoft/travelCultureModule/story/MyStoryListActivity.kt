package com.daqsoft.travelCultureModule.story

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.daqsoft.baselib.base.*
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.*
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.provider.view.BaseDialog
import com.daqsoft.provider.view.ListPopupWindow
import com.daqsoft.travelCultureModule.net.MainRepository
import com.daqsoft.travelCultureModule.story.story.StoryAdapter
import com.daqsoft.travelCultureModule.story.vm.MyStoryListActivityViewModel
import com.google.android.material.appbar.AppBarLayout
import org.jetbrains.anko.toast
import kotlin.text.StringBuilder


/**
 * @Description 我的故事页面
 * @ClassName   HotActivityDetailActivity
 * @Author      PuHua
 * @Time        2019/12/23 17:28
 */
@Route(path = MainARouterPath.MINE_STORY_LIST)
class MyStoryListActivity : TitleBarActivity<MineStoryListBinding, MyStoryListActivityViewModel>() {

    //类型弹窗
    var typePopupWindow: ListPopupWindow<Any>? = null

    //类型
    val types = mModel.types

    //排序弹窗
    var sortPopupWindow: ListPopupWindow<Any>? = null
    val sorts = mModel.sorts

    //标签弹窗
    var tagPopupWindow: ListPopupWindow<Any>? = null

    //删除对话框
    var deleteDialog: BaseDialog? = null

    override fun getLayout(): Int = R.layout.mine_story_list

    override fun setTitle(): String = getString(R.string.home_mine_story)


    override fun injectVm(): Class<MyStoryListActivityViewModel> = MyStoryListActivityViewModel::class.java
    private val storyAdapter by lazy { StoryAdapter(this, 2) }
    override fun initView() {
        mBinding.vm = mModel

        mModel.storyList.observe(this, Observer {
//            mBinding.swRefreshStory.isRefreshing = false
            mBinding.swRefreshStory.finishRefresh()
            dissMissLoadingDialog()
            pageDeal(it)
        })


        // 故事
        storyAdapter!!.isShowDelete = true
        val storyLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        mBinding.rvStory.layoutManager = storyLayoutManager
        mBinding.rvStory.adapter = storyAdapter

        mModel.tags.observe(this, Observer {

            it?.add(0, TagBean("", "全部"))
            tagPopupWindow = ListPopupWindow.getInstance(mBinding.tvTag, it as List<Any>) { item ->
                run {
                    mBinding.tvTag.text = (item as TagBean).name
                    mModel.currentTag = item.id
                    refreshData()
                }
            }

        })

//        mBinding?.swRefreshStory.setProgressViewOffset(true, -20, 100);
        mBinding.swRefreshStory.setOnRefreshListener {
//            mBinding?.swRefreshStory.isRefreshing = true
            mModel.currentPage = 1
            storyAdapter?.clear()
            mModel.getMyStoryList()
        }
        storyAdapter!!.setOnLoadMoreListener {
            mModel.currentPage = 1 + mModel.currentPage
            mModel.getMyStoryList()
        }
        storyAdapter!!.setClickItemListener { item, position ->
            mModel.deleteStory = item
            mModel.deletePosition = position
            deleteDialog?.show()
        }

        mModel.deleteFinish.observe(this, Observer {
            if (it) {
                toast("删除成功!")
                storyAdapter!!.getData().removeAt(mModel.deletePosition)
                storyAdapter!!.notifyItemRemoved(mModel.deletePosition)
            } else {
                toast("删除失败!")
            }
        })
        initEvent()
        initDeleteDialog()
    }

    /**
     * 刷新数据
     */
    private fun refreshData() {
        mModel.currentPage = 1
        storyAdapter?.clear()
        showLoadingDialog()
        mModel.getMyStoryList()
    }


    private fun initTypePopupWindow() {
        typePopupWindow = ListPopupWindow.getInstance(mBinding.tvType, types) { item ->
            run {
                mBinding.tvType.text = (item as ValueKeyBean).name
                mModel.currentType = item.value
                refreshData()
            }
        }
    }

    private fun initSortPopupWindow() {
        sortPopupWindow = ListPopupWindow.getInstance(mBinding.tvSort, sorts) { item ->
            kotlin.run {
                mBinding.tvSort.text = (item as ValueKeyBean).name
                mModel.currentSort = item.value
                refreshData()
            }
        }
    }

    private fun initDeleteDialog() {
        deleteDialog = BaseDialog(this)
        deleteDialog!!.contentView(R.layout.dialog_delete_story)
            .layoutParams(ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            .gravity(Gravity.CENTER)
            .animType(BaseDialog.AnimInType.BOTTOM)
            .canceledOnTouchOutside(false)
        deleteDialog!!.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            deleteDialog!!.dismiss()
        }
        deleteDialog!!.findViewById<TextView>(R.id.tv_query).setOnClickListener {
            mModel.deleteStory()
            deleteDialog!!.dismiss()
        }
    }

    override fun initData() {
        initTypePopupWindow()
        initSortPopupWindow()
        mModel.getTagList()
    }

    override fun onResume() {
        super.onResume()
        mModel.getMyStoryList()
    }

    private fun initEvent() {
        mBinding.tvType.onNoDoubleClick {
            typePopupWindow?.show()
        }
        mBinding.tvSort.onNoDoubleClick {
            sortPopupWindow?.show()
        }
        mBinding.tvTag.onNoDoubleClick {
            tagPopupWindow?.show()
        }
    }

    /**
     * 列表数据页码处理
     *
     * @param page     当前页
     * @param response 返回数据体
     * @param adapter  适配器
     */
    private fun pageDeal(datas: MutableList<HomeStoryBean>) {
        if (mModel.currentPage == 1) {
            mBinding?.rvStory.smoothScrollToPosition(0)
            mBinding?.rvStory.visibility = View.VISIBLE
            storyAdapter!!.clear()
            storyAdapter!!.emptyViewShow = datas.isNullOrEmpty()

        }
        if (!datas.isNullOrEmpty()) {
            for (i in 0 until (datas!!.size ?: 0)) {
                var data = datas!![i]
                if (data.storyType == Constant.STORY_TYPE_STORY) {
                    storyAdapter!!.addViewType(R.layout.item_story_main)
                } else if (data.storyType == Constant.STORY_TYPE_STRATEGY) {
                    storyAdapter!!.addViewType(R.layout.item_story_list_strategy)
                }
                storyAdapter!!.addItem(data)
            }
            storyAdapter!!.notifyDataSetChanged()
        }
        if (datas.isNullOrEmpty() || datas.size < mModel.pageSize) {
            storyAdapter!!.loadEnd()
        } else {
            storyAdapter!!.loadComplete()
        }
        dissMissLoadingDialog()
    }


}