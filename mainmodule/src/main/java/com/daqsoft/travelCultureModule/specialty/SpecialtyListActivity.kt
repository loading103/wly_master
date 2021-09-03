package com.daqsoft.travelCultureModule.specialty

import android.annotation.SuppressLint
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.mainmodule.databinding.MainSpecialListActivityBinding
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.ResourceTypeLabel
import com.daqsoft.provider.bean.SpeaiclBean
import com.daqsoft.provider.bean.ValueKeyBean
import com.daqsoft.provider.net.StatisticsRepository
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.provider.view.convenientbanner.ConvenientBanner
import com.daqsoft.provider.view.convenientbanner.holder.AdvImageHolder
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.travelCultureModule.playground.adapter.PlayGroundLsAdapter
import com.daqsoft.travelCultureModule.resource.model.ScenicModel
import com.daqsoft.travelCultureModule.specialty.adapter.SpecialLsAdapter
import com.daqsoft.travelCultureModule.specialty.viewmodel.SpecialtyListViewModel
import com.google.android.material.appbar.AppBarLayout
import java.util.*

/**
 * @Description 特产列表
 * @ClassName   ResearchListActivity
 */
@Route(path = MainARouterPath.SPECIAL_BASELIST)
class SpecialtyListActivity : TitleBarActivity<MainSpecialListActivityBinding, SpecialtyListViewModel>() {
    /**
     * 用户当前位置
     */
    var selfLocation: LatLng? = null


    @JvmField
    @Autowired
    var region: String? = ""
    @JvmField
    @Autowired
    var siteId: String? = ""

    @JvmField
    @Autowired
    var areaSiteSwitch: String? = ""


    /**
     * 景区的适配器
     */
    var adapter: SpecialLsAdapter? = null

    override fun getLayout(): Int = R.layout.main_special_list_activity

    override fun setTitle(): String = " 特产列表"

    override fun injectVm(): Class<SpecialtyListViewModel> = SpecialtyListViewModel::class.java


    @SuppressLint("CheckResult")
    override fun initView() {
        mBinding.vm = mModel
        mBinding.rtsScenicLsType.setModel(region, mModel, this)
        if(!areaSiteSwitch.isNullOrBlank()){
            mModel.areaSiteSwitch=areaSiteSwitch
        }
        initViewEvent()
        initViewModel()
        StatisticsRepository.instance.statisticsPage(title, 1)
    }


    private fun initViewEvent() {
        adapter =SpecialLsAdapter(this@SpecialtyListActivity).apply {
            onItemClickListener = object : SpecialLsAdapter.OnSpecialItemClickListener {
                override fun onCollectClick(id: String, postion: Int, status: Boolean) {
                    if (AppUtils.isLogin()) {
                        showLoadingDialog()
                        if (status) {
                            mModel.collectionCancelScenic(id, postion)
                        } else {
                            mModel.collectionScenic(id, postion)
                        }
                    } else {
                        ToastUtils.showMessage("该操作需要登录，请先登录")
                        ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                            .navigation()
                    }
                }
            }
        }
        adapter?.emptyViewShow = false
        mBinding.recyScenicList.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        mBinding.recyScenicList.adapter = adapter
        adapter!!.setOnLoadMoreListener {
            mModel.pageManager.nexPageIndex
            mModel.getScenicList()
        }
        mBinding.srRefresh.setOnRefreshListener {
            //            mBinding.srRefresh.isRefreshing = true
            mModel.pageManager.initPageIndex()
            mModel.getScenicList()
        }
        mBinding.rtsScenicLsType.setmOnTypeSelectListener(object : SpecialTypeSelectView.OnTypeSelectListener {

            override fun OnAreaSelected(region: ChildRegion?) {
                if (region != null) {
                    mModel.region = region.region
                    mModel.areaSiteSwitch = region.siteId
                    mModel.pageManager.initPageIndex()
                    mBinding.recyScenicList.visibility = View.GONE
                    showLoadingDialog()
                    mModel.getScenicList()
                }
            }

            override fun onTypesSelected(item: HashMap<String, String>?) {
                if (item != null) {
                    mModel.type = item!!["type"]
                    mModel.tag = item!!["tag"]
                    mModel.pageManager.initPageIndex()
                    mBinding.recyScenicList.visibility = View.GONE
                    showLoadingDialog()
                    mModel.getScenicList()
                }
            }

            override fun OnSortTypeSelected(bean: ValueKeyBean?) {
                if (bean != null) {
                    mModel.sortType = bean.value
                    mModel.pageManager.initPageIndex()
                    mBinding.recyScenicList.visibility = View.GONE
                    showLoadingDialog()
                    mModel.getScenicList()
                }
            }
        })
        mBinding?.edtSearchScenic.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_ALL_SEARCH)
                .navigation()
        }
        mBinding?.vScenicTopToSerach.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_ALL_SEARCH)
                .navigation()
        }
        mBinding.vTopScenicMapMode.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_SCENIC_LIST_MAP)
                .withInt("mSelectTabPos", 0)
                .withString("mSelectType", ResourceType.CONTENT_TYPE_SCENERY)
                .navigation()
        }
        mBinding?.appbarScenicTop.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { p0, p1 ->
            if (p1 >= 0) {
                mBinding.srRefresh.setEnabled(true);
            } else {
                mBinding.srRefresh.setEnabled(false);
            }
        })

        mBinding?.recyScenicList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                var topRowVerticalPosition: Int = 0
                if (recyclerView == null || recyclerView.childCount == 0) {
                    topRowVerticalPosition = 0
                } else {
                    topRowVerticalPosition = recyclerView.getChildAt(0).getTop()
                }
                mBinding.srRefresh.isEnabled = topRowVerticalPosition >= 0
            }
        })
    }

    private fun initViewModel() {
        mModel.types.observe(this, Observer {
            mBinding.rtsScenicLsType.getData(
                ArrayList(ScenicModel.firstSpecialType),
                it as ArrayList<ResourceTypeLabel>
            )
        })

        mModel.researchList.observe(this, Observer {
            pageDealed(it)
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.srRefresh.finishRefresh()
        })
        mModel.canceCollectLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            adapter?.notifyCollectStatus(it, false)
        })
        mModel.collectLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            adapter?.notifyCollectStatus(it, true)
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })
        mModel.topAdsLiveData.observe(this, Observer {
            if (it == null || it.adInfo.isNullOrEmpty()) {
                mBinding.vScenicListNoAdv.visibility = View.VISIBLE
                mBinding.vScenicListTopAdv.visibility = View.GONE
            } else {
                mBinding.cbanerScenicTopAdv
                    .setPages(object : CBViewHolderCreator {
                        override fun createHolder(itemView: View?): Holder<*> {
                            return AdvImageHolder(itemView!!)
                        }

                        override fun getLayoutId(): Int {
                            return com.daqsoft.provider.R.layout.layout_common_adv
                        }
                    }, it.adInfo)
                    .setCanLoop(it.adInfo.size > 1)
                    .setPointViewVisible(it.adInfo.size > 1)
                    .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                    .setOnItemClickListener { it2 ->
                        run {
                            // 跳转事件
                            MenuJumpUtils.adJump(it.adInfo[it2])
                        }

                    }
                    .setPageIndicator(null)
                    .startTurning()

                if (!it.adInfo.isNullOrEmpty()) {
                    mBinding.vScenicListNoAdv.visibility = View.GONE
                    mBinding.vScenicListTopAdv.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun pageDealed(it: MutableList<SpeaiclBean>?) {
        dissMissLoadingDialog()
        if (!mBinding.recyScenicList.isVisible) {
            mBinding.recyScenicList.visibility = View.VISIBLE
        }
//        mBinding.srRefresh.isRefreshing = false
        mBinding.srRefresh.finishRefresh()
        if (mModel.pageManager.isFirstIndex) {
            mBinding.recyScenicList.smoothScrollToPosition(0)
            adapter!!.clear()
        }
        if (!it.isNullOrEmpty()) {
            adapter!!.add(it!!)
        }
        if (it.isNullOrEmpty() || it.size < mModel.pageManager.pageSize) {
            adapter?.loadEnd()
        } else {
            adapter?.loadComplete()
        }
        adapter?.emptyViewShow = true
    }

    override fun initData() {
//        mModel.getScenicTopAds()
        location()
    }

    /**
     * 定位自己的位置
     */
    fun location() {
        if (region != null) {
//            mModel.region = region!!
        }
        if (!siteId.isNullOrEmpty()) {
            mModel.areaSiteSwitch = siteId
        }
        mModel.mPresenter.value?.loading = true
        GaoDeLocation.getInstance().init(this, object : GaoDeLocation.OnGetCurrentLocationLisner {

            override fun onResult(
                adCode: String, result: String, lat_: Double,
                lon_: Double, adcode: String
            ) {
                try {
                    var selfLocation = LatLng(lat_, lon_)
                    adapter?.selfLocation = selfLocation
                    mModel?.lat = lat_.toString()
                    mModel?.lng = lon_.toString()
                    showLoadingDialog()
                    mModel.getScenicList()
                    mBinding.rtsScenicLsType.getChildRegions(lat_,lon_)
                } catch (e: Exception) {

                }

            }

            override fun onError(errormsg: String) {
                mModel.getScenicList()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (mBinding.vScenicListTopAdv.isVisible) {
            mBinding.cbanerScenicTopAdv.startTurning(3000)
        }
    }

    override fun onPause() {
        super.onPause()
        if (mBinding.vScenicListTopAdv.isVisible) {
            mBinding.cbanerScenicTopAdv.stopTurning()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        StatisticsRepository.instance.statisticsPage(title, 2)
        GaoDeLocation.getInstance().release()
    }
}