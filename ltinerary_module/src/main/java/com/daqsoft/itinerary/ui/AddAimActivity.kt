package com.daqsoft.itinerary.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.collection.ArrayMap
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.daqsoft.baselib.base.BaseActivity
import com.daqsoft.baselib.extend.dp
import com.daqsoft.baselib.utils.PageDealUtils
import com.daqsoft.itinerary.R
import com.daqsoft.itinerary.adapter.CustomScenicAdapter
import com.daqsoft.itinerary.adapter.CustomVenueAdapter
import com.daqsoft.itinerary.adapter.DestinationCityAdapter
import com.daqsoft.itinerary.bean.CityBean
import com.daqsoft.itinerary.bean.ItineraryDetailBean
import com.daqsoft.itinerary.bean.PlayItems
import com.daqsoft.itinerary.databinding.ActivityAddaimBinding
import com.daqsoft.itinerary.interfa.OnItemSelectedListener
import com.daqsoft.provider.ItineraryRouter
import com.daqsoft.itinerary.vm.ItineraryCustomViewModel
import com.daqsoft.itinerary.widget.SpaceItemDecoration
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.Constants
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.ScenicBean
import com.daqsoft.venuesmodule.repository.bean.VenuesListBean

/**
 * @Author：      邓益千
 * @Create by：   2020/5/16 15:51
 * @Description： 添加想去的景点
 */
@Route(path = ItineraryRouter.ITINERARY_ADD_SCENIC)
class AddAimActivity : BaseActivity<ActivityAddaimBinding, ItineraryCustomViewModel>() {

    @JvmField
    @Autowired
    var dayId: String = ""

    @JvmField
    @Autowired
    var dataId = 0

    @JvmField
    @Autowired
    var itineraryId: String = ""

    /**景区当前页*/
    private var scenicPage = 1

    /**场馆当前页*/
    private var venuePage = 1

    private var cityRegion = ""

    private lateinit var itinerary: ItineraryDetailBean

    /**景区*/
    private val scenicAdapter: CustomScenicAdapter by lazy {
        CustomScenicAdapter()
    }

    /**场馆*/
    private val venuesAdapter: CustomVenueAdapter by lazy {
        CustomVenueAdapter()
    }

    private val cityAdapter: DestinationCityAdapter by lazy {
        DestinationCityAdapter()
    }

    override fun getLayout(): Int = R.layout.activity_addaim

    override fun injectVm(): ItineraryCustomViewModel {
        return ItineraryCustomViewModel()
    }

    override fun setViewModel() {
        //MainARouterPath.MAIN_SCENIC_DETAIL
    }

    override fun initView() {
        mBinding.cityViewList.apply {
            adapter = cityAdapter.apply {
                emptyViewShow = false
                setOnItemSelectedListener(cityListener)
            }
            addItemDecoration(object : RecyclerView.ItemDecoration(){
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val position = parent.getChildAdapterPosition(view)
                    val count = state.itemCount - 1
                    outRect.left = 5.dp
                    if (position == count){
                        outRect.right = 5.dp
                    }
                }
            })
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL,false)
        }

        mBinding.scenicViewList.apply {
            //景区adapter属性设置
            adapter = scenicAdapter.apply {
                //加载更多
                setOnLoadMoreListener({
                    scenicPage++
                    mModel.getScenicList("", "", scenicPage, cityRegion)
                }, mBinding.scenicViewList)

                isCancel(false)
                setOnItemSelectedListener(scenicSelectedListener)
            }
            layoutManager = GridLayoutManager(context, 2)
            addItemDecoration(SpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.dp_10)))
        }

        mBinding.venueViewList.apply {
            //场馆adapter属性设置
            adapter = venuesAdapter.apply {
                //加载更多
                setOnLoadMoreListener({
                    venuePage++
                    mModel.getVenueList("", "", venuePage, cityRegion)
                }, mBinding.scenicViewList)
                isCancel(false)
                setOnItemSelectedListener(scenicSelectedListener)
            }
            layoutManager = GridLayoutManager(context, 2)
            addItemDecoration(SpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.dp_10)))
        }
    }

    override fun initData() {
        mModel.addResult.observe(this, Observer {
            setResult(Activity.RESULT_OK)
            finish()
        })

        //城市数据
        mModel.cityList.observe(this, Observer {
            val allBean = CityBean()
            allBean.isSelected = true
            it.add(0, allBean)
            cityAdapter.add(it)
        })

        //详情
        mModel.itineraryDetail.observe(this, Observer {
            itinerary = it
            mModel.getScenicList("", "", scenicPage, cityRegion)
        })

        //景区列表
        mModel.scenicList.observe(this, Observer {

            if (!it.isNullOrEmpty()) {
                mBinding.label1View.visibility = View.VISIBLE
                mBinding.scenicViewList.visibility = View.VISIBLE
                scenicDataMatch(it)
            } else {
                if(scenicPage==1) {
                    mBinding.label1View.visibility = View.GONE
                    mBinding.scenicViewList.visibility = View.GONE
                }else{
                    scenicAdapter.loadMoreEnd(true)
                    //请求场馆列表
//                    mModel.getVenueList("", "", venuePage, cityRegion)
                }
            }
            if (scenicPage == mModel.pageTotal) {
                scenicAdapter.loadMoreEnd(true)
                //请求场馆列表
                mModel.getVenueList("", "", venuePage, cityRegion)
            } else {
                scenicAdapter.loadMoreComplete()
            }
        })

        //场馆列表
        mModel.venuesList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.label2View.visibility = View.VISIBLE
                mBinding.venueViewList.visibility = View.VISIBLE
                venueDataMatch(it)
            } else {
                if (venuePage == 1) {
                    mBinding.label2View.visibility = View.GONE
                    mBinding.venueViewList.visibility = View.GONE
                }else{
                    venuesAdapter.loadMoreEnd(true)
                }
            }
            if (mModel.venueTotal == venuesAdapter.itemCount+1) {
                venuesAdapter.loadMoreEnd(true)
            } else {
                venuesAdapter.loadMoreComplete()
            }
        })

        //获取目的地城市
        mModel.getDestination()

        mModel.getItineraryDetail(itineraryId)
    }

    /**景区数据匹配*/
    private fun scenicDataMatch(scenics: MutableList<ScenicBean>) {
        for (sc in scenics) {
            sc.dataId = dataId
            for (day in itinerary.dayList) {
                for (plan in day.sourceList) {
                    if (plan.resourceId == sc.id.toInt()) {
                        sc.isChecked = true
                    }
                }
            }
        }
        scenicAdapter.addData(scenics)
    }

    /**数据匹配*/
    private fun venueDataMatch(scenics: MutableList<VenuesListBean>) {
        for (sc in scenics) {
            sc.dataId = dataId
            for (day in itinerary.dayList) {
                for (plan in day.sourceList) {
                    if (plan.resourceId == sc.id.toInt()) {
                        sc.isChecked = true
                    }
                }
            }
        }
        venuesAdapter.addData(scenics)
    }

    /**城市切换*/
    private val cityListener = object : OnItemSelectedListener<CityBean> {
        override fun onSelected(position: Int, item: CityBean) {
            cityRegion = item.region
            venuesAdapter.setNewData(null)
            scenicAdapter.setNewData(null)
            scenicPage = 1
            venuePage = 1
            if (item.regionName == "全部") cityRegion = ""
            mModel.getScenicList("", "", scenicPage, cityRegion)
        }
    }

    /**
     * 点击item 的类型 是 景区 还是场馆
     */
    private var label = ""
    // 点击的 position
    private var index = -1


    private var scenicSelectedListener = object : OnItemSelectedListener<PlayItems> {
        override fun onSelected(position: Int,item: PlayItems) {
            index = position
            label = item.resourceType

            if(item.isJump){
                var path = MainARouterPath.MAIN_SCENIC_DETAIL
                if (item.resourceType == Constants.TYPE_VENUE){
                    path = ARouterPath.VenuesModule.VENUES_DETAILS_ACTIVITY
                }
                ARouter.getInstance()
                    .build(path)
                    .withBoolean("isShowButton",item.isSelected)
                    .withString("source","itinerary")
                    .withString("id", item.resourceId.toString())
                    .navigation(this@AddAimActivity,13)
            }else{
                if (!item.isSelected){
                    AlertDialog.Builder(this@AddAimActivity)
                        .setTitle("温馨提示")
                        .setMessage("是否添加到行程中？")
                        .setPositiveButton("确定"){ dialog, _ ->
                            dialog.dismiss()

                            if (label == Constants.TYPE_SCENERY) {
                                val bean = scenicAdapter.data[index]
                                bean.isChecked = true
                                scenicAdapter.notifyDataSetChanged()
                            } else if (label == Constants.TYPE_VENUE) {
                                val bean = venuesAdapter.data[index]
                                bean.isChecked = true
                                venuesAdapter.notifyDataSetChanged()
                            }

                            val jsonParams = ArrayMap<String,Any>()
                            jsonParams["dayId"] = dayId
                            jsonParams["journeyId"] = itineraryId
                            jsonParams["operateType"] = 1

                            val params = ArrayMap<String,Any>()
                            params["id"] = item.resourceId
                            params["resourceType"] = item.resourceType
                            params["sourceId"] = item.dataId
                            params["type"] = "JOURNEY_SOURCE"

                            jsonParams["params"] = arrayListOf(params)
                            mModel.operateSource(jsonParams)
                        }.setNegativeButton("取消",null).create().show()
                }
            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 13 && resultCode == Activity.RESULT_OK){

            val jsonParams = ArrayMap<String,Any>()
            jsonParams["dayId"] = dayId
            jsonParams["journeyId"] = itineraryId
            jsonParams["operateType"] = 1
            val params = ArrayMap<String,Any>()

            if (label == Constants.TYPE_SCENERY) {
                val bean = scenicAdapter.data[index]
                bean.isChecked = true
                scenicAdapter.notifyDataSetChanged()
                params["id"] = bean.id
                params["resourceType"] = Constants.TYPE_SCENERY
                params["sourceId"] = bean.dataId
                params["type"] = "JOURNEY_SOURCE"
            } else if (label == Constants.TYPE_VENUE) {
                val bean = venuesAdapter.data[index]
                bean.isChecked = true
                venuesAdapter.notifyDataSetChanged()
                params["id"] = bean.id
                params["resourceType"] = Constants.TYPE_VENUE
                params["sourceId"] = bean.dataId
                params["type"] = "JOURNEY_SOURCE"
            }
            jsonParams["params"] = arrayListOf(params)
            mModel.operateSource(jsonParams)
        }
    }
}