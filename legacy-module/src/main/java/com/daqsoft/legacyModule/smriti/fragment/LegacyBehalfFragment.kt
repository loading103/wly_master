package com.daqsoft.legacyModule.smriti.fragment

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.databinding.FragmentLegacyBehalfBinding
import com.daqsoft.legacyModule.smriti.HERITAGE_BATCH
import com.daqsoft.legacyModule.smriti.HERITAGE_LEVEL
import com.daqsoft.legacyModule.smriti.HERITAGE_TYPE
import com.daqsoft.legacyModule.smriti.adapter.LegacyBehalfAdapter
import com.daqsoft.legacyModule.smriti.adapter.LegacyRecommendAdapter
import com.daqsoft.legacyModule.smriti.bean.LegacyBehalfBean
import com.daqsoft.legacyModule.smriti.bean.TypeBean
import com.daqsoft.legacyModule.smriti.vm.LegacySmiritiViewModel
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.provider.view.ListPopupWindow
import com.daqsoft.provider.view.popupwindow.AreaSelectPopupWindow
import com.google.android.material.appbar.AppBarLayout
import com.jakewharton.rxbinding2.view.RxView
import org.jetbrains.anko.support.v4.onRefresh
import java.util.concurrent.TimeUnit

/**
 * desc :非遗项目
 * @author 江云仙
 * @date 2020/4/23 17:57
 */
class LegacyBehalfFragment : BaseFragment<FragmentLegacyBehalfBinding, LegacySmiritiViewModel>() {
    // 等级弹窗
    var levelPopupWindow: ListPopupWindow<Any>? = null
    // 级别弹窗
    var batchPopupWindow: ListPopupWindow<Any>? = null
    // 类型弹窗
    var typePopupWindow: ListPopupWindow<Any>? = null
    /**
     * 地区选择弹窗窗口
     */
    private var areaListPopWindow: AreaSelectPopupWindow? = null
    /**
     *非遗代表性项目adapter
     */
    private val legacyBehalfAdapter by lazy { LegacyBehalfAdapter().apply { setEmptyTop(true) }}
    private val legacyRecommendAdapter by lazy { LegacyRecommendAdapter() }
    override fun getLayout(): Int {
        return R.layout.fragment_legacy_behalf
    }
    override fun injectVm(): Class<LegacySmiritiViewModel> = LegacySmiritiViewModel::class.java
    override fun initView() {
        setSlidingConflict()
        initRecyclerView()
        initBehalfData()
        initClick()
    }

    /**
     *滑动冲突
     */
    private fun setSlidingConflict() {
        //解决SwipeRefreshLayout和CoordinatorLayout滑动冲突
        mBinding.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { p0, p1 ->
            mBinding.swprefreshLegacySmriti.isEnabled = p1 >= 0
        })

    }

    /**
     *点击事件
     */
    @SuppressLint("CheckResult")
    private fun initClick() {
        //上拉刷新
        mBinding.swprefreshLegacySmriti.setOnRefreshListener {
//            mBinding.swprefreshLegacySmriti.isRefreshing = true
            mModel.mCurrPage = 1
            mModel.getBehalfList()
        }
        //下拉加载
        legacyBehalfAdapter.setOnLoadMoreListener {
            mModel.mCurrPage++
            mModel.getBehalfList()
        }
        legacyBehalfAdapter.setOnItemListener(object :LegacyBehalfAdapter.OnItemClickListener{
            //收藏事件
            override fun onCollectClick(item: LegacyBehalfBean, imgCollect: ImageView) {
                mModel.collection(item,imgCollect)
            }
            //取消收藏
            override fun onCancelCollectClick(item: LegacyBehalfBean, imgCollect: ImageView) {
                mModel.collectionCancel(item,imgCollect)
            }

        })
        //点击地区
        RxView.clicks(mBinding.legacySelection.tvArea).throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if (areaListPopWindow != null) {
                    areaListPopWindow!!.show(mBinding.legacySelection.tvArea)
                }
            }
        //点击级别
        mBinding.legacySelection.tvLevel.onNoDoubleClick {
            levelPopupWindow?.show()
        }
        //点击类别
        mBinding.legacySelection.tvSort.onNoDoubleClick {
            typePopupWindow?.show()
        }
        //点击批次
        mBinding.legacySelection.tvBatch.onNoDoubleClick {
            batchPopupWindow?.show()
        }

    }

    /**
     *列表数据
     */
    private fun initBehalfData() {
        mModel.legacyBehalfs.observe(this, Observer {
            pageDealed(it)
        })

        mModel.legacyRecommends.observe(this, Observer {
            if (it.isNotEmpty()) {
                legacyRecommendAdapter.clear()
                mBinding.legacyRecommend.root.visibility = View.VISIBLE
                legacyRecommendAdapter.add(it)
                legacyRecommendAdapter.notifyDataSetChanged()
            } else {
                mBinding.legacyRecommend.root.visibility = View.GONE
            }
        })

        // 地区
        mModel.areas.observe(this, Observer {
            if (areaListPopWindow == null) {
                it.add(0, ChildRegion("", "全部", "", "", emptyList()))
                areaListPopWindow =
                    AreaSelectPopupWindow.getInstance(
                        BaseApplication.context, false
                    ) { item ->
                        mModel.areaSiteSwitch = item.siteId
                        mModel.region = item.region
                        mModel.mCurrPage = 1
                        showLoadingDialog()
                        if ( item .name=="全部"){
                            mBinding.legacySelection.tvArea.text ="地区"
                        }else{
                            mBinding.legacySelection.tvArea.text = item.name
                        }
                        mModel.getBehalfList()
                    }
                areaListPopWindow!!.firstData = it
                var temp: MutableList<ChildRegion> = mutableListOf()
                temp.add(0, ChildRegion("", "不限", "", "", emptyList()))
                areaListPopWindow!!.secondData = ArrayList(temp)
            }
        })
        //级别
        mModel.levelBeans.observe(this, Observer {
            it?.add(0, TypeBean("","全部","","",""))
            levelPopupWindow = ListPopupWindow.getInstance(mBinding.legacySelection.tvLevel, it) { item ->
                kotlin.run {
                    if ( (item as TypeBean).name=="全部"){
                        mBinding.legacySelection.tvLevel.text ="级别"
                    }else{
                        mBinding.legacySelection.tvLevel.text = item.name
                    }
                    mModel.level = item.id
                    showLoadingDialog()
                    mModel.getBehalfList()
                }
            }
        })
        //类别
        mModel.typeBeans.observe(this, Observer {
            it?.add(0, TypeBean("","全部","","",""))
            typePopupWindow = ListPopupWindow.getInstance(mBinding.legacySelection.tvSort, it) { item ->
                kotlin.run {
                    if ( (item as TypeBean).name=="全部"){
                        mBinding.legacySelection.tvSort.text ="类别"
                    }else{
                        mBinding.legacySelection.tvSort.text = item.name
                    }
                    mModel.type = item.id
                    showLoadingDialog()
                    mModel.getBehalfList()
                }
            }
        })
        //批次
        mModel.batchBeans.observe(this, Observer {
            it?.add(0, TypeBean("","全部","","",""))
            batchPopupWindow = ListPopupWindow.getInstance(mBinding.legacySelection.tvBatch, it) { item ->

                kotlin.run {
                    if ( (item as TypeBean).name=="全部"){
                        mBinding.legacySelection.tvBatch.text ="批次"
                    }else{
                        mBinding.legacySelection.tvBatch.text = item.name
                    }
                    mModel.batch = item.id
                    showLoadingDialog()
                    mModel.getBehalfList()
                }
            }
        })


    }

    private fun pageDealed(it: MutableList<LegacyBehalfBean>?) {
        dissMissLoadingDialog()
        if (!mBinding.recyLegacySmriti.isVisible) {
            mBinding.recyLegacySmriti.visibility = View.VISIBLE
        }
//        mBinding.swprefreshLegacySmriti.isRefreshing = false
        mBinding.swprefreshLegacySmriti.finishRefresh()
        if (mModel.mCurrPage == 1) {
            mBinding.recyLegacySmriti.smoothScrollToPosition(0)
            legacyBehalfAdapter!!.clear()
        }
        if (!it.isNullOrEmpty()) {
            legacyBehalfAdapter!!.add(it!!)
        }
        if (it.isNullOrEmpty() || it.size < mModel.pageSize) {
            legacyBehalfAdapter?.loadEnd()
        } else {
            legacyBehalfAdapter?.loadComplete()
        }
    }

    /**
     *设置适配器
     */
    private fun initRecyclerView() {
        mBinding.recyLegacySmriti.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = legacyBehalfAdapter
        }
        mBinding.legacyRecommend.recyRecommend.apply {
            val gridLayoutManager = GridLayoutManager(activity, 3)
            layoutManager = gridLayoutManager
            adapter = legacyRecommendAdapter
            //设置滑动惯性
            mBinding.legacyRecommend.recyRecommend.isNestedScrollingEnabled = false
            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (position) {
                        0 -> return 2
                        else -> 1
                    }
                }
            }
        }


    }

    override fun initData() {
        //非遗推荐
        mModel.getLegaRecommendList()
        //非遗代表性项目
        mModel.getBehalfList()
        //级别筛选
        mModel.getLevel(HERITAGE_LEVEL)
        //批次筛选
        mModel.getBatch(HERITAGE_BATCH)
        //类别筛选
        mModel.getType(HERITAGE_TYPE)
        //获取地区数据
        mModel.getChildRegions()
    }

}
