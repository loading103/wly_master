package com.daqsoft.travelCultureModule.venuecollect
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityCultureLsBinding
import com.daqsoft.mainmodule.databinding.ActivityExhibitionLsBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.CultureDetailBean
import com.daqsoft.provider.bean.ExhibitionTagBean
import com.daqsoft.travelCultureModule.venuecollect.adapter.CultureListAdapter
import com.daqsoft.travelCultureModule.venuecollect.adapter.TopTagAdapter
import com.daqsoft.travelCultureModule.venuecollect.viewmodel.CultureLsViewModel
import com.scwang.smartrefresh.layout.footer.ClassicsFooter

/**
 * 精品文物列表界面
 */
@Route(path = MainARouterPath.COLLECT_CULYURE_LIST)
class CulturalRelicLsActivity : TitleBarActivity<ActivityCultureLsBinding, CultureLsViewModel>() {
    private var Id :String="0"
    private var type=""

    // List适配器
    private val mAdapter by lazy { CultureListAdapter().apply {
        emptyViewShow=false
    }}
    // 头部适配器
    private val topAdapter: TopTagAdapter by lazy {
        TopTagAdapter(this).apply {
            emptyViewShow=false
            onItemCLickListener =  object : TopTagAdapter.OnItemCLickListener {
                override fun onItemClick(item: ExhibitionTagBean, position: Int) {
                    type=item.value
                    showLoadingDialog()
                    mModel.pageManager.initPageIndex()
                    mModel.getListDatas(Id,type)
                }
            }
        }
    }


    override fun getLayout(): Int {
        return R.layout.activity_culture_ls
    }

    override fun setTitle(): String {
        return "精品文物"
    }

    override fun injectVm(): Class<CultureLsViewModel> {
        return CultureLsViewModel::class.java
    }

    override fun initView() {
        initViewModel()
        mBinding.rvLineType.adapter = topAdapter
        mBinding.swprefreshFoods.apply {
            setRefreshFooter(ClassicsFooter(context).setDrawableSize(20f))
            setOnLoadMoreListener {
                mModel.pageManager.nexPageIndex
                mModel.getListDatas(Id,type)
            }
            setOnRefreshListener {
                mModel.pageManager.initPageIndex()
                mModel.getListDatas(Id,type)
            }
        }
        mBinding.recycleView.apply {
            val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            staggeredGridLayoutManager.gapStrategy=StaggeredGridLayoutManager.GAP_HANDLING_NONE
            setHasFixedSize(true)
            this.layoutManager = staggeredGridLayoutManager
            this.adapter = mAdapter

            this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState);
                    staggeredGridLayoutManager.invalidateSpanAssignments(); //防止第一行到顶部有空白区域
                }
            })
        }
    }


    override fun initData() {
        // 获取顶部标签
        showLoadingDialog()
        mModel.getTopDatas()
    }

    private fun initViewModel() {
        mModel.topdatas.observe(this, Observer {
            if(it==null){
                return@Observer
            }
            it.add(0,ExhibitionTagBean(id="",name="全部"))
            it[0].select=true
            topAdapter.setNewData(it)
            type=it[0].value
            // 获取内容
            mModel.pageManager.initPageIndex()
            mModel.getListDatas(Id,type)
        })

        mModel.listdatas.observe(this, Observer {
            dissMissLoadingDialog()
            mAdapter.emptyViewShow=true
            mBinding.swprefreshFoods.finishRefresh()
            mBinding.swprefreshFoods.finishLoadMore()
            if(it==null){
                return@Observer
            }
            if (mModel.pageManager.isFirstIndex) {
                mAdapter.setNewData(it)
                if( mAdapter.getData().size==mModel.totleNumber){
                    mBinding.swprefreshFoods.setNoMoreData(true)
                }else{
                    mBinding.swprefreshFoods.setNoMoreData(false)
                }
            }else{
                if( mAdapter.getData().size==mModel.totleNumber){
                    mBinding.swprefreshFoods.setNoMoreData(true)
                }else{
                    mBinding.swprefreshFoods.setNoMoreData(false)
                }
                mAdapter.add(it)
            }

            mAdapter.notifyDataSetChanged()
        })
    }
}