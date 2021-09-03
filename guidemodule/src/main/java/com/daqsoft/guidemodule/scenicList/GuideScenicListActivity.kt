package com.daqsoft.guidemodule.scenicList

import android.annotation.SuppressLint
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.maps.model.LatLng
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.guidemodule.R
import com.daqsoft.guidemodule.bean.GuideHomeListBean
import com.daqsoft.guidemodule.databinding.GuideActivityScenicListBinding
import com.daqsoft.guidemodule.widget.GuideScenicListTypeSelectView
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.base.LabelType
import com.daqsoft.provider.bean.ResourceTypeLabel
import com.daqsoft.provider.service.GaoDeLocation
import java.util.*

/**
 * @Description Guide景区列表
 * @ClassName   GuideScenicListActivity
 * @Author      Wongxd
 * @Time        2020/4/3 9:24
 */
@Route(path = ARouterPath.GuideModule.GUIDE_SCENIC_LIST_ACTIVITY)
internal class GuideScenicListActivity :
    TitleBarActivity<GuideActivityScenicListBinding, GuideScenicListViewModel>() {

    companion object {
        // 等级
        private val scenicLevel = mutableListOf(
            ResourceTypeLabel("", "", "", "", "不限").apply {
                select = true
            },
            ResourceTypeLabel("", "", "", "AAAAA", "AAAAA"),
            ResourceTypeLabel("", "", "", "AAAA", "AAAA"),
            ResourceTypeLabel("", "", "", "AAA", "AAA"),
            ResourceTypeLabel("", "", "", "AA", "AA"),
            ResourceTypeLabel("", "", "", "A", "A")
        )

        // 第一层排序
        private val firstType = mutableListOf(
            ResourceTypeLabel("", "", "", "level", "景区等级").setSelects(true),
            ResourceTypeLabel("", "", "", LabelType.SCENIC_THEME, "景区主题"),
            ResourceTypeLabel("", "", "", "crowd", "景区人群")
        )
    }

    /**
     * 用户当前位置
     */
    private var selfLocation: LatLng? = null

    /**
     * 景区的适配器
     */
    private var adapter: GuideScenicListAdapter? = null

    override fun getLayout(): Int = R.layout.guide_activity_scenic_list

    override fun setTitle(): String = getString(R.string.guide_scenic_list)

    override fun injectVm(): Class<GuideScenicListViewModel> = GuideScenicListViewModel::class.java


    @SuppressLint("CheckResult")
    override fun initView() {
        mBinding.guideScenicLsType.setModel(mModel, this)
        mBinding.guideScenicLsType.getData(
            ArrayList(firstType),
            ArrayList(scenicLevel)
        )
        initViewEvent()
        initViewModel()
    }


    private var mHot = false
    private var mDistance = false

    private fun initViewEvent() {
        adapter = GuideScenicListAdapter(this@GuideScenicListActivity)
        mBinding.rvScenicList.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        mBinding.rvScenicList.adapter = adapter
        adapter?.setOnLoadMoreListener {
            mModel.pageManager.nexPageIndex
            mModel.getScenicList(mHot, mDistance)
        }
        mBinding.srl.setOnRefreshListener {
            //            mBinding.srl.isRefreshing = true
            mModel.pageManager.initPageIndex()
            mModel.getScenicList(mHot, mDistance)
        }

        mBinding.guideScenicLsType.setOnTypeSelectListener(object :
            GuideScenicListTypeSelectView.OnTypeSelectListener {

            override fun onHotAndDistanceChanged(hot: Boolean, distance: Boolean) {

                mHot = hot
                mDistance = distance

                mModel.pageManager.initPageIndex()
                mBinding.rvScenicList.visibility = View.GONE
                showLoadingDialog()
                mModel.getScenicList(mHot, mDistance)

            }

            override fun onTypesSelected(
                item: HashMap<String, String>?,
                hot: Boolean,
                distance: Boolean
            ) {

                mHot = hot
                mDistance = distance

                if (item != null) {
                    mModel.level = item["level"]
                    mModel.crowd = item["crowd"]
                    mModel.theme = item["theme"]
                    mModel.pageManager.initPageIndex()
                    mBinding.rvScenicList.visibility = View.GONE
                    showLoadingDialog()
                    mModel.getScenicList(mHot, mDistance)
                }
            }


        })


    }

    private fun initViewModel() {
        mModel.scenicList.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.rvScenicList.visibility = View.VISIBLE

            pageDeal(it)
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.srl.finishRefresh(false)
        })

    }

    private fun pageDeal(it: MutableList<GuideHomeListBean>?) {
        dissMissLoadingDialog()
        if (!mBinding.rvScenicList.isVisible) {
            mBinding.rvScenicList.visibility = View.VISIBLE
        }
//        mBinding.srl.isRefreshing = false
        mBinding.srl.finishRefresh(false)
        if (mModel.pageManager.isFirstIndex) {
            mBinding.rvScenicList.smoothScrollToPosition(0)
            adapter?.clear()
        }
        if (!it.isNullOrEmpty()) {
            adapter?.add(it)
        }
        if (it.isNullOrEmpty() || it.size < mModel.pageManager.pageSize) {
            adapter?.loadEnd()
        } else {
            adapter?.loadComplete()
        }
    }

    override fun initData() {
        location()
    }

    /**
     * 定位自己的位置
     */
    private fun location() {
        showLoadingDialog()
        GaoDeLocation.getInstance().init(this, object : GaoDeLocation.OnGetCurrentLocationLisner {

            override fun onResult(
                adCode: String, result: String, lat_: Double,
                lon_: Double, adcode: String
            ) {
                try {
                    val selfLocation = LatLng(lat_, lon_)
                    adapter?.selfLocation = selfLocation
                    mModel.lat = lat_.toString()
                    mModel.lng = lon_.toString()
                    mModel.getScenicList(mHot, mDistance)

                    val param = HashMap<String, String>()
                    param["lat"] = lat_.toString()
                    param["lng"] = lon_.toString()
                    param["sortType"] = "disNum"
                    mModel.getSelectLabel()

                } catch (e: Exception) {

                }

            }

            override fun onError(errormsg: String) {
                mModel.getScenicList(mHot, mDistance)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        GaoDeLocation.getInstance().release()
    }
}