package com.daqsoft.itinerary.ui.fragment

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.extend.dp
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.itinerary.R
import com.daqsoft.itinerary.adapter.CustomScenicAdapter
import com.daqsoft.itinerary.adapter.CustomVenueAdapter
import com.daqsoft.itinerary.adapter.DestinationCityAdapter
import com.daqsoft.itinerary.bean.CityBean
import com.daqsoft.itinerary.bean.ItineraryLabelBean
import com.daqsoft.itinerary.bean.PlayItems
import com.daqsoft.itinerary.databinding.FragmentAddAimBinding
import com.daqsoft.itinerary.interfa.OnItemSelectedListener
import com.daqsoft.itinerary.ui.ItineraryCustomActivity
import com.daqsoft.itinerary.vm.ItineraryCustomViewModel
import com.daqsoft.itinerary.widget.SpaceItemDecoration
import com.daqsoft.itinerary.widget.StringListPopupWindow
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.Constants
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.ScenicBean
import com.daqsoft.provider.bean.VenueTypeBean
import com.daqsoft.provider.view.ListPopupWindow
import com.daqsoft.venuesmodule.repository.bean.VenuesListBean
import daqsoft.com.baselib.databinding.LayoutAdapterFooterBinding
import org.jetbrains.anko.textColor

/**
 * @Author：      邓益千
 * @Create by：   2020/4/23 20:39
 * @Description： 步骤四，添加想去的景点
 */
class AddAimFragment : BaseFragment<FragmentAddAimBinding, ItineraryCustomViewModel>() {

    private var label = "scenic"

    private var isFirstLoad = true

    /**景区当前页*/
    private var scenicPage = 1

    /**场馆当前页*/
    private var venuePage = 1

    /**景区总数*/
    private var scenicTotal = 0

    private var index = 0

    /**城市编码*/
    private var cityRegion = ""

    /**景区数据*/
    private val scenicList: MutableList<ScenicBean> by lazy {
        ArrayList<ScenicBean>()
    }

    /**场馆数据*/
    private val venuesList: MutableList<VenuesListBean> by lazy {
        ArrayList<VenuesListBean>()
    }

    /**保存景区选中的数据*/
    private val tempScenicList: MutableList<ScenicBean> by lazy {
        ArrayList<ScenicBean>()
    }

    /**保存场馆选中的数据*/
    private val tempVenuesList: MutableList<VenuesListBean> by lazy {
        ArrayList<VenuesListBean>()
    }

    /**城市*/
    private val cityAdapter: DestinationCityAdapter by lazy {
        DestinationCityAdapter()
    }

    /**景区*/
    private val scenicAdapter: CustomScenicAdapter by lazy {
        CustomScenicAdapter().apply {
            setOnItemSelectedListener(scenicSelectedListener)
//            setOnLoadMoreListener({
//                scenicPage++
//                mModel.getScenicList(labelBean.scenicLabelsId,labelBean.sceneryCrowd,scenicPage,cityRegion)
//            },mBinding.recyclerView)
        }
    }

    /**场馆*/
    private val venuesAdapter: CustomVenueAdapter by lazy {
        CustomVenueAdapter().apply {
//            setItemFooterTypeIsShow(false)
            setOnItemSelectedListener(scenicSelectedListener)
        }
    }

    //    private var pop: StringListPopupWindow<String>?  = null
    private var pop: ListPopupWindow<Any>? = null
    private val popData = arrayListOf("景区", "场馆")

    private lateinit var labelBean: ItineraryLabelBean

    private lateinit var parentActivity: ItineraryCustomActivity

    override fun getLayout(): Int = R.layout.fragment_add_aim

    override fun injectVm(): Class<ItineraryCustomViewModel> = ItineraryCustomViewModel::class.java

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentActivity = context as ItineraryCustomActivity
        labelBean = parentActivity.customLabel.value!!
    }

    override fun initView() {
        //城市
        mBinding.cityViewList.apply {
            adapter = cityAdapter.apply {
                emptyViewShow = false
                setOnItemSelectedListener(citySelectedListener)
            }
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val position = parent.getChildAdapterPosition(view)
                    val count = state.itemCount - 1
                    outRect.left = 5.dp
                    if (position == count) {
                        outRect.right = 5.dp
                    }

                }
            })
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        }

        mBinding.recyclerView.apply {
            adapter = scenicAdapter
            layoutManager = GridLayoutManager(context, 2)
            addItemDecoration(SpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.dp_8)))
        }
        mBinding.vVenueTab.onNoDoubleClick {
            mBinding.vSlideScenicTab.visibility = View.GONE
            mBinding.vSlideVenueTab.visibility = View.VISIBLE
            mBinding.labelNameView.textSize = 13f
            mBinding.labelNameView.paint.isFakeBoldText = false
            mBinding.labelNameView.textColor = ContextCompat.getColor(context!!, R.color.color_666)
            mBinding.labelVenueView.textColor = ContextCompat.getColor(context!!, R.color.color_333)
            mBinding.labelVenueView.textSize = 14f
            mBinding.labelVenueView.paint.isFakeBoldText = true
            label = "venues"
            mBinding.recyclerView.adapter = venuesAdapter
            parentActivity.showLoadingDialog()
            mModel.getVenueList(
                labelBean.venueLabelsId,
                labelBean.venueCrowd,
                venuePage,
                cityRegion,
                "100"
            )
        }
        mBinding.vScenicTab.onNoDoubleClick {
            mBinding.vSlideScenicTab.visibility = View.VISIBLE
            mBinding.vSlideVenueTab.visibility = View.GONE
            mBinding.labelNameView.textSize = 14f
            mBinding.labelNameView.paint.isFakeBoldText = true
            mBinding.labelVenueView.textSize = 13f
            mBinding.labelVenueView.paint.isFakeBoldText = false
            mBinding.labelNameView.textColor = ContextCompat.getColor(context!!, R.color.color_333)
            mBinding.labelVenueView.textColor = ContextCompat.getColor(context!!, R.color.color_666)
            label = "scenic"
            mBinding.recyclerView.adapter = scenicAdapter
            parentActivity.showLoadingDialog()
            mModel.getScenicList(
                labelBean.scenicLabelsId,
                labelBean.sceneryCrowd,
                scenicPage,
                cityRegion,
                "100"
            )
        }

    }

    override fun initData() {
        parentActivity.showLoadingDialog()

        //城市数据
        mModel.cityList.observe(this, Observer {
            val temp = CityBean()
            temp.isSelected = true
            it.add(0, temp)
            cityAdapter.add(it)
        })

        //根据选择的标签响应数据 景区和列表
        mModel.customBean.observe(this, Observer {
            parentActivity.dissMissLoadingDialog()
            it.scenic?.let { scenics ->
                tempScenicList.clear()
                tempScenicList.addAll(scenics)
                scenics.forEach { bean ->
                    //如果游玩时间0，默认4小时
                    if (bean.suggestedHour == 0) {
                        bean.suggestedHour = 4
                    }
                    bean.isChecked = true
                    bean.type = Constants.TYPE_SCENERY
                    parentActivity.customLabel.value?.sourceParams!!.add(
                        PlayItems(
                            bean.id.toInt(),
                            bean.type,
                            bean.dataId,
                            bean.isChecked,
                            bean.suggestedHour,
                            false
                        )
                    )
                }
                scenicList.clear()
                scenicList.addAll(scenics)
                scenicTotal = scenics.size
            }
            it.venue?.let { venues ->
                tempVenuesList.clear()
                tempVenuesList.addAll(venues)
                venues.forEach { bean ->
                    //如果游玩时间0，默认4小时
                    if (bean.suggestedHour == 0) {
                        bean.suggestedHour = 4
                    }
                    bean.isChecked = true
                    bean.resType = Constants.TYPE_VENUE
                    parentActivity.customLabel.value?.sourceParams!!.add(
                        PlayItems(
                            bean.id.toInt(),
                            bean.resType,
                            bean.dataId,
                            bean.isChecked,
                            bean.suggestedHour,
                            false
                        )
                    )
                }
                venuesList.clear()
                venuesList.addAll(venues)
            }
            parentActivity.updateDate(parentActivity.customLabel.value?.sourceParams!!)
            mModel.getScenicList(labelBean.scenicLabelsId, labelBean.sceneryCrowd, scenicPage, cityRegion, "100")
        })

        //景区列表
        mModel.scenicList.observe(this, Observer {
            if (isFirstLoad) {
                isFirstLoad = false
                scenicTotal += mModel.total
            }

            parentActivity.dissMissLoadingDialog()
            it.forEach { scenic ->
                if (!scenicList.contains(scenic)) {
                    scenicList.add(scenic)
                }
            }
            scenicAdapter.setNewData(scenicList)
            if (scenicAdapter.itemCount == scenicTotal) {
                mBinding.noMore.visibility = View.VISIBLE
            } else {
                mBinding.noMore.visibility = View.GONE
                scenicAdapter.loadMoreComplete()
            }
        })

        //场馆列表
        mModel.venuesList.observe(this, Observer {
            parentActivity.dissMissLoadingDialog()
            it.forEach { venue ->
                if (!venuesList.contains(venue)) {
                    venuesList.add(venue)
                }
            }
            venuesAdapter.setNewData(venuesList)

            if (venuesAdapter.itemCount == mModel.venueTotal) {
                mBinding.noMore.visibility = View.VISIBLE
            } else {
                mBinding.noMore.visibility = View.GONE
                venuesAdapter.loadMoreComplete()
            }
        })

        //获取目的地城市
        mModel.getDestination()

        //根据标签请求默认的场馆和景区
        mModel.getVenueAndScenic(labelBean)
    }

    private var scenicSelectedListener = object : OnItemSelectedListener<PlayItems> {
        override fun onSelected(position: Int, item: PlayItems) {
            if (item.isJump) {
                index = position
                var path = MainARouterPath.MAIN_SCENIC_DETAIL
                if (item.resourceType == Constants.TYPE_VENUE) {
                    path = ARouterPath.VenuesModule.VENUES_DETAILS_ACTIVITY
                }
                ARouter.getInstance()
                    .build(path)
                    .withBoolean("isShowButton", item.isSelected)
                    .withString("source", "itinerary")
                    .withString("id", item.resourceId.toString())
                    .navigation(activity, 12)
            } else {
                if (parentActivity.customLabel.value?.sourceParams!!.contains(item)) {
                    parentActivity.customLabel.value?.sourceParams!!.remove(item)
                } else {
                    parentActivity.customLabel.value?.sourceParams!!.add(item)
                }
                parentActivity.updateDate(parentActivity.customLabel.value?.sourceParams!!)

                addOrRemove(item)
            }
        }
    }

    //城市监听
    private val citySelectedListener = object : OnItemSelectedListener<CityBean> {
        override fun onSelected(position: Int, item: CityBean) {
            initDate()
            parentActivity.showLoadingDialog()
            cityRegion = item.region
            if (item.regionName == "全部") {
                cityRegion = ""
            }
            if (label == "venues") {
                mModel.getVenueList(labelBean.venueLabelsId, labelBean.venueCrowd, venuePage, cityRegion, "100")
            } else {
                mModel.getScenicList(labelBean.scenicLabelsId, labelBean.sceneryCrowd, scenicPage, cityRegion, "100")
            }
        }
    }

    private fun initDate() {
        isFirstLoad = true
        scenicPage = 1
        venuePage = 1
        scenicTotal = 0
        scenicList.clear()
        venuesList.clear()

        scenicList.addAll(tempScenicList)
        venuesList.addAll(tempVenuesList)

        scenicAdapter.setNewData(scenicList)
        venuesAdapter.setNewData(venuesList)
    }

    /**根据选中状态 把数据装载到临时集合中，只做界面展示，不做其他操作*/
    private fun addOrRemove(item: PlayItems) {
        //如果是景区类型数据
        if (item.resourceType == Constants.TYPE_SCENERY) {
            var bean: ScenicBean? = null
            scenicList.forEach { scenicBean ->
                if (scenicBean.id.toInt() == item.resourceId) {
                    bean = scenicBean
                    return@forEach
                }
            }
            bean?.let {
                if (item.isSelected) {
                    if (!tempScenicList.contains(it)) {
                        tempScenicList.add(it)
                    }
                } else {
                    if (tempScenicList.contains(it)) {
                        tempScenicList.remove(it)
                    }
                }
            }
        } else {
            var bean: VenuesListBean? = null
            venuesList.forEach { venuesBean ->
                if (venuesBean.id.toInt() == item.resourceId) {
                    bean = venuesBean
                    return@forEach
                }
            }
            bean?.let {
                if (item.isSelected) {
                    if (!tempVenuesList.contains(it)) {
                        tempVenuesList.add(it)
                    }
                } else {
                    if (tempVenuesList.contains(it)) {
                        tempVenuesList.remove(it)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 12 && resultCode == RESULT_OK) {
            if (label == "scenic") {
                val bean = scenicList[index]
                bean.isChecked = true
                scenicAdapter.notifyDataSetChanged()
                parentActivity.customLabel.value?.sourceParams!!.add(
                    PlayItems(
                        bean.id.toInt(),
                        bean.type,
                        bean.dataId,
                        bean.isChecked,
                        bean.suggestedHour,
                        false
                    )
                )
            } else {
                val bean = venuesList[index]
                bean.isChecked = true
                venuesAdapter.notifyDataSetChanged()
                parentActivity.customLabel.value?.sourceParams!!.add(
                    PlayItems(
                        bean.id.toInt(),
                        bean.resType,
                        bean.dataId,
                        bean.isChecked,
                        bean.suggestedHour,
                        false
                    )
                )
            }
            parentActivity.updateDate(parentActivity.customLabel.value?.sourceParams!!)
        }
    }

}