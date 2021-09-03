package com.daqsoft.travelCultureModule.themepark

import android.graphics.Rect
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.dp
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityThemeListBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.travelCultureModule.themepark.adapter.ThemeAreaListAdapter
import com.daqsoft.travelCultureModule.themepark.viewmodel.ThemeAreaListViewModel
import com.scwang.smartrefresh.layout.footer.ClassicsFooter

/**
 * @Description 主题区域
 */
@Route(path = MainARouterPath.THEME_AREA_LIST)
class ThemeAreaListActivity : TitleBarActivity<ActivityThemeListBinding, ThemeAreaListViewModel>() {

    @JvmField
    @Autowired
    var Id: String = ""

    private val mAdapter: ThemeAreaListAdapter by lazy {
        ThemeAreaListAdapter().apply { emptyViewShow=false }
    }

    override fun getLayout(): Int {
        return R.layout.activity_theme_list
    }

    override fun setTitle(): String {
        return "主题区域"
    }
    override fun injectVm(): Class<ThemeAreaListViewModel> {
        return ThemeAreaListViewModel::class.java
    }

    override fun initView() {
        initViewModel()
        mModel.pageManager.initPageIndex()

        mBinding.recyclerView.apply {
            adapter = mAdapter
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    val position = parent.getChildAdapterPosition(view)
                    val count = state.itemCount - 1
                    if (position != 0 && position != count){
                        outRect.bottom = 20.dp
                    }
                }
            })
        }
        mBinding.smartRefreshLayout.apply {
            setEnableLoadMore(true)
            setRefreshFooter(ClassicsFooter(context).setDrawableSize(20f))
            setOnRefreshListener {
                mModel.pageManager.initPageIndex()
                mModel.getListData()
            }
            setOnLoadMoreListener{
                mModel.pageManager.nexPageIndex
                mModel.getListData()
            }
        }
    }

    override fun initData() {
        showLoadingDialog()
        mModel.getListData()
    }


    private fun initViewModel() {
        mModel.datas?.observe(this, Observer {
            dissMissLoadingDialog()
            mAdapter.emptyViewShow=true
            mBinding.smartRefreshLayout.finishRefresh()
            mBinding.smartRefreshLayout.finishLoadMore()
            if(it==null){
                return@Observer
            }
            if (mModel.pageManager.isFirstIndex) {
                mAdapter.setNewData(it)
                if( mAdapter.getData().size==mModel.totleNumber){
                    mBinding.smartRefreshLayout.setNoMoreData(true)
                }else{
                    mBinding.smartRefreshLayout.setNoMoreData(false)
                }
            }else{
                if( mAdapter.getData().size==mModel.totleNumber){
                    mBinding.smartRefreshLayout.setNoMoreData(true)
                }else{
                    mBinding.smartRefreshLayout.setNoMoreData(false)
                }
                mAdapter.add(it)
            }
            mAdapter.notifyDataSetChanged()
        })
    }

}