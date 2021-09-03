package com.daqsoft.guidemodule.search

import android.annotation.SuppressLint
import android.content.Intent
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.PageDealUtils
import com.daqsoft.guidemodule.GuideMapShowType
import com.daqsoft.guidemodule.R
import com.daqsoft.guidemodule.adapter.GuideSearchLsAdapter
import com.daqsoft.guidemodule.adapter.GuideSearchTypeAdapter
import com.daqsoft.guidemodule.bean.GuideResouceBean
import com.daqsoft.guidemodule.bean.GuideScenicListBean
import com.daqsoft.guidemodule.bean.GuideSearchBean
import com.daqsoft.guidemodule.databinding.GuideSearchActivityBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.service.GaoDeLocation
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.toast
import timber.log.Timber
import java.text.DecimalFormat

/**
 * @Description  导游导览搜索
 * @ClassName   GuideSearchActivity
 * @Author      Wongxd
 * @Time        2020/4/1 17:02
 */
@Route(path = ARouterPath.GuideModule.GUIDE_SEARCH_ACTIVITY)
internal class GuideSearchActivity :
    TitleBarActivity<GuideSearchActivityBinding, GuideSearchViewModel>() {

    @JvmField
    @Autowired
    var isShowLegacy: Boolean = true

    @JvmField
    @Autowired
    var tourId: String = ""

    @JvmField
    @Autowired
    var defaultType: String = ""

    @JvmField
    @Autowired
    var mapMode: Int = 1

    /**
     * 是否初始化
     */
    private var isInitTab: Boolean = true

    /**
     * 搜索类型
     */
    private var mSelectType: String? = ""

    /**
     * 当前场馆类型
     */
    private var currVenueType: String? = ""

    private var currVenuTypeMode: String? = ""

    /**
     * 搜索关键字
     */
    private var searchKey: String? = ""

    private var currPage: Int = 1

    private val guideSearshTypeAdapter: GuideSearchTypeAdapter by lazy {
        GuideSearchTypeAdapter().apply {
            emptyViewShow = false
        }.apply {
            onItemclickListener = object : GuideSearchTypeAdapter.OnItemClickListener {
                override fun onItemClick(resource: GuideResouceBean) {
                    mSelectType = resource.resourceType
                    currVenueType = resource.type.toString()
                    currVenuTypeMode = resource.value
                    currPage = 1
                    getSearchData()
                }

            }
        }
    }
    private val guideSearchResultAdapter: GuideSearchLsAdapter by lazy {
        GuideSearchLsAdapter().apply {
            onItemClickListner = object : GuideSearchLsAdapter.OnItemClickListner {
                override fun onItemClick(item: GuideScenicListBean) {
                    var data = Intent()
                    data.putExtra("resouceType", item.resourceType)
                    data.putExtra("resourceId", item.resourceId)
                    data.putExtra("venueTypeValue", currVenuTypeMode)
                    setResult(1002, data)
                    finish()
                }

            }
        }
    }

    override fun getLayout(): Int = R.layout.guide_search_activity

    override fun setTitle(): String = getString(R.string.guide_search)


    override fun injectVm(): Class<GuideSearchViewModel> = GuideSearchViewModel::class.java

    @SuppressLint("ResourceType")
    override fun initView() {

        mBinding.rvSearchTypes.adapter = guideSearshTypeAdapter

        mBinding.rvSearchResults.adapter = guideSearchResultAdapter


        mBinding.etAllSearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED || actionId == EditorInfo.IME_ACTION_DONE
            ) {
                searchKey = mBinding.etAllSearch?.text.toString()
                guideSearchResultAdapter?.content = searchKey
                currPage = 1
                getSearchData()
            }
            false
        })

        mBinding.tvSearchCancel.setOnClickListener {
            mBinding.etAllSearch.text.clear()
            searchKey = ""
            guideSearchResultAdapter?.content = searchKey
            currPage = 1
            getSearchData()
        }
        guideSearchResultAdapter.setOnLoadMoreListener {
            currPage = currPage + 1
            getSearchData()
        }
        initViewModel()


    }

    private fun initViewModel() {
        mModel.homeList.observe(this, Observer {
            PageDealUtils().pageDeal(currPage, it, guideSearchResultAdapter)
            if (it != null && !it.datas.isNullOrEmpty()) {
                guideSearchResultAdapter.add(it.datas!!)
            }
        })
        mModel.tourResourceListLd.observe(this, Observer {
            guideSearshTypeAdapter.clear()
            guideSearshTypeAdapter.addItem(GuideResouceBean().apply {
                name = "全部"
            })
            mSelectType = ""
            currVenueType = ""
            currVenuTypeMode = ""
            // 默认搜索全部
            getSearchData()
            if (!it.isNullOrEmpty()) {
                guideSearshTypeAdapter.add(it)
            }
        })
    }


    private fun renderView() {
    }


    private var mLat = 0.0
    private var mLng = 0.0

    override fun initData() {

        GaoDeLocation.getInstance().init(this, object : GaoDeLocation.OnGetCurrentLocationLisner {

            override fun onResult(
                adCode: String, result: String, lat_: Double,
                lon_: Double, adcode: String
            ) {
                mLat = lat_
                mLng = lon_
                mModel.getGuideResoureType(tourId ?: "",mapMode)
            }

            override fun onError(errormsg: String) {
                Timber.e("获取定位位置出错了")
                mModel.getGuideResoureType(tourId ?: "",mapMode)
            }
        })
    }


    private fun getSearchData() {
        var params: HashMap<String, String> = hashMapOf()
        if (!mSelectType.isNullOrEmpty()) {
            params["resourceType"] = mSelectType!!
            if (mSelectType == ResourceType.CONTENT_TYPE_VENUE && !currVenueType.isNullOrEmpty()) {
                params["type"] = currVenueType!!
            }
        }
        params["tourId"] = tourId
        if (mLat != null && mLng != null) {
            params["lat"] = mLat.toString()
            params["lon"] = mLng.toString()
        }
        params["pageSize"] = "12"
        params["currPage"] = "$currPage"
        if (!searchKey.isNullOrEmpty()) {
            params["name"] = searchKey!!
        }
        mModel.getGuideHomeList(params)
    }

    override fun onDestroy() {
        super.onDestroy()
        GaoDeLocation.getInstance().release()
    }
}