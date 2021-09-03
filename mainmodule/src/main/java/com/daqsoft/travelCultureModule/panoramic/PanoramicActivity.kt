package com.daqsoft.travelCultureModule.panoramic

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.MainPanoramicActivityBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.provider.bean.ResourceTypeLabel
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.provider.view.ListPopupWindow
import com.daqsoft.provider.view.convenientbanner.utils.ScreenUtil
import com.daqsoft.provider.view.popupwindow.AreaSelectPopupWindow
import com.jakewharton.rxbinding2.view.RxView
import com.tbruyelle.rxpermissions2.RxPermissions
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * @des     720全景漫游
 * @class   PanoramicActivity
 * @author  Wongxd
 * @date    2020-4-20  9:21
 *
 */
@Route(path = MainARouterPath.PANORAMIC_LIST)
internal class PanoramicActivity : TitleBarActivity<MainPanoramicActivityBinding, PanoramicViewModel>() {

    override fun getLayout(): Int = R.layout.main_panoramic_activity

    override fun setTitle(): String = getString(R.string.main_panoramic)

    override fun injectVm(): Class<PanoramicViewModel> = PanoramicViewModel::class.java


    private var mLastDistance = ""
    private var mLat = ""
    private var mLng = ""
    private var mAreaSiteSwitch = ""
    private var mType = ""


    private val distance: List<String> = listOf("不限", "距离优先")

    /**
     * 弹窗
     */
    private var areaListPopupWindow: AreaSelectPopupWindow? = null

    /**
     * 类型
     */
    private var typeListPopupWindow: ListPopupWindow<*>? = null
    private var typeListVenuePopupWindow: ListPopupWindow<*>? = null
    /**
     * 距离
     */
    private var distanceListPopupWindow: ListPopupWindow<*>? = null


    private var mAdapter:PanoramicAdapter?=null


    private var permissions: RxPermissions? = null


    private  var  isVenue:Boolean=false

    override fun initData() {
        permissions = RxPermissions(this)
        location()
    }

    /**
     * 定位自己当前的位置
     */
    @SuppressLint("CheckResult")
    private fun location() {


        fun doLocation() {
            var locationString = ""
            GaoDeLocation.getInstance().init(this, object : GaoDeLocation.OnGetCurrentLocationLisner {

                override fun onResult(
                    adCode: String, result: String, lat_: Double,
                    lon_: Double, adcode: String
                ) {
                    mLat = lat_.toString()
                    mLng = lon_.toString()
                }

                override fun onError(errormsg: String) {
                    ToastUtils.showMessage(errormsg)
                }
            })
        }

        if (
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            //未开启定位权限
            permissions?.request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)!!.subscribe {
                if (it) {
                    doLocation()
                } else {
                    mModel.toast.postValue("非常抱歉，正常授权才能使用地图模式")
                }
            }
        } else {
            doLocation()
        }
    }


    @SuppressLint("CheckResult")
    override fun initView() {
        mAdapter =PanoramicAdapter(this@PanoramicActivity)
        mBinding.tvScine.isSelected=true
        mBinding.view1.visibility=View.VISIBLE
        RxView.clicks(mBinding.tvArea)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                areaListPopupWindow?.show(mBinding.tvArea)
                typeListPopupWindow?.dismiss()
                typeListVenuePopupWindow?.dismiss()
                distanceListPopupWindow?.dismiss()
            }

        RxView.clicks(mBinding.tvType)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                areaListPopupWindow?.dismiss()
                if(isVenue){
                    typeListVenuePopupWindow?.show()
                }else{
                    typeListPopupWindow?.show()
                }
                distanceListPopupWindow?.dismiss()
            }

        RxView.clicks(mBinding.tvDistance)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                areaListPopupWindow?.dismiss()
                typeListPopupWindow?.dismiss()
                typeListVenuePopupWindow?.dismiss()
                distanceListPopupWindow?.show()
            }

        RxView.clicks(mBinding.tvVenar)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                showLoadingDialog()
                mBinding.tvScine.isSelected=false
                mBinding.tvVenar.isSelected=true
                mBinding.view1.visibility=View.GONE
                mBinding.view2.visibility=View.VISIBLE
                isVenue=true
                mBinding.tvType.text = "类型"
                mType=""
                mModel.pageManager.initPageIndex()
                typeListVenuePopupWindow?.previous?.isSelected = false
                mModel.getVenueDataList(mAreaSiteSwitch, mType, if (mLastDistance == distance[1]) mLat else "", if (mLastDistance == distance[1]) mLng else "")
            }
        RxView.clicks(mBinding.tvScine)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                showLoadingDialog()
                mBinding.tvScine.isSelected=true
                mBinding.tvVenar.isSelected=false
                mBinding.view1.visibility=View.VISIBLE
                mBinding.view2.visibility=View.GONE
                mBinding.tvType.text = "类型"
                typeListPopupWindow?.previous?.isSelected = false;
                mType=""
                isVenue=false
                mModel.pageManager.initPageIndex()
                mModel.getDataList(mAreaSiteSwitch, mType, if (mLastDistance == distance[1]) mLat else "", if (mLastDistance == distance[1]) mLng else "")
            }


        mBinding.rv.apply {
            layoutManager = LinearLayoutManager(this@PanoramicActivity)
            adapter = mAdapter
        }



        mBinding.srl.apply {
//            setRefreshHeader(ClassicsHeader(this@PanoramicActivity))

            setOnRefreshListener {
                doRefresh()
            }
            setOnLoadMoreListener {
                mModel.pageManager.nexPageIndex
                mModel.getDataList(mAreaSiteSwitch, mType, if (mLastDistance == distance[1]) mLat else "", if (mLastDistance == distance[1]) mLng else "")
            }
        }


        initViewModel()
        mModel.getChildRegions()
        mModel.getTypeList()
        mModel.getTypeVenueList()
        doRefresh()
    }

    private fun doRefresh() {
        mModel.pageManager.initPageIndex()
        if(isVenue){
            mModel.getVenueDataList(mAreaSiteSwitch, mType, if (mLastDistance == distance[1]) mLat else "", if (mLastDistance == distance[1]) mLng else "")
        }else{
            mModel.getDataList(mAreaSiteSwitch, mType, if (mLastDistance == distance[1]) mLat else "", if (mLastDistance == distance[1]) mLng else "")
        }
    }

    private fun initViewModel() {


        mModel.dataList.observe(this, Observer {

            dissMissLoadingDialog()
            mBinding.srl.finishRefresh()
            mBinding.srl.finishLoadMore()
            mBinding.rv.visibility = View.VISIBLE

            if (mModel.pageManager.isFirstIndex) {
                mAdapter?.clear()
            }

            if (it.isNotEmpty())
                mAdapter?.add(it.toMutableList())

//            if (it.isNullOrEmpty()) {
//                mAdapter.loadEnd()
//                if (mAdapter.itemCount > 1)
//                    mAdapter.notifyItemChanged(mAdapter.itemCount - 1)
//            } else {
//                mAdapter.add(it.toMutableList())
//            }


        })


        mModel.areas.observe(this, Observer<List<ChildRegion>> {

            val childRegions: MutableList<ChildRegion> = mutableListOf()
            childRegions.add(0, ChildRegion("", "全部", "", "", ArrayList(), 0, "0"))
            childRegions.addAll(it)

            areaListPopupWindow = AreaSelectPopupWindow.getInstance(
                mBinding.root.context, false
            ) { region ->
                mBinding.tvArea.text = "" + region.name
                if (mAreaSiteSwitch != region.siteId) {
                    mAreaSiteSwitch = region.siteId
                    doRefresh()
                }
            }

            areaListPopupWindow?.firstData = childRegions
            val temp = ArrayList<ChildRegion>()
            temp.add(0, ChildRegion("", "不限", "", "", ArrayList(), 0, ""))
            areaListPopupWindow?.secondData = temp

        })


        mModel.typeList.observe(this, Observer {
            val typeList: MutableList<ResourceTypeLabel> = mutableListOf()
            typeList.add(ResourceTypeLabel("", "", "", "", "全部"))
            typeList.addAll(it)
            typeListPopupWindow = ListPopupWindow.getInstance<ResourceTypeLabel>(mBinding.tvType, typeList, ScreenUtil.dip2px(this, 250f)) { item ->
                val bean = item as ResourceTypeLabel
                mBinding.tvType.text = bean.labelName
                if (mType != bean.id) {
                    mType = bean.id
                    doRefresh()
                }
            }

        })

        mModel.typeListVenue.observe(this, Observer {
            val typeList: MutableList<ResourceTypeLabel> = mutableListOf()
            typeList.add(ResourceTypeLabel("", "", "", "", "全部",name = "全部"))
            it.forEach { it1->
                it1.labelName=it1.name.toString()
            }

            typeList.addAll(it)
            typeListVenuePopupWindow = ListPopupWindow.getInstance<ResourceTypeLabel>(mBinding.tvType, typeList, ScreenUtil.dip2px(this, 250f)) { item ->
                val bean = item as ResourceTypeLabel
                mBinding.tvType.text = bean.name
                if (mType != bean.id) {
                    mType = bean.id
                    doRefresh()
                }
            }

        })

        distanceListPopupWindow = ListPopupWindow.getInstance<String>(mBinding.tvDistance, distance) { item ->
            val bean = item as String
            mBinding.tvDistance.text = bean
            if (mLastDistance != bean) {
                mLastDistance = bean
                doRefresh()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        GaoDeLocation.getInstance().release()
    }
}