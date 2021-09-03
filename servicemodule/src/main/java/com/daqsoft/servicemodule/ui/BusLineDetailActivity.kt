package com.daqsoft.servicemodule.ui

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.android.scenic.servicemodule.R
import com.daqsoft.android.scenic.servicemodule.databinding.ActivityBusLineDetailBinding
import com.daqsoft.android.scenic.servicemodule.databinding.ItemBusLineChildBinding
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.provider.ARouterPath
import com.daqsoft.servicemodule.adapter.BusLineDetailAdapter
import com.daqsoft.servicemodule.bean.BusLineDetailBean
import com.daqsoft.servicemodule.bean.BusLineDetailBean.Companion.BUS_BUS
import com.daqsoft.servicemodule.bean.BusLineDetailBean.Companion.BUS_FOOTER
import com.daqsoft.servicemodule.bean.BusLineDetailBean.Companion.BUS_HEADER
import com.daqsoft.servicemodule.bean.BusLineDetailBean.Companion.BUS_WALK
import com.daqsoft.servicemodule.bean.Segments
import com.daqsoft.servicemodule.bean.TransitS
import com.daqsoft.servicemodule.model.BusLineDetailViewModel
import com.daqsoft.servicemodule.uitils.DrawableUtil
import com.daqsoft.servicemodule.uitils.MathUtil
import com.daqsoft.servicemodule.uitils.TimeSwitch
import com.daqsoft.servicemodule.view.MyLayoutManager
import kotlinx.android.synthetic.main.header_bus_detail.view.*

/**
 * desc :公交线路详情
 * @author 江云仙
 * @date 2020/4/3 17:41
 */
@Route(path = ARouterPath.ServiceModule.SERVICE_BUS_LINE_DETAIL_ACTIVITY)
open class BusLineDetailActivity : TitleBarActivity<ActivityBusLineDetailBinding, BusLineDetailViewModel>() {
    @JvmField
    @Autowired(name = "busLineChildDetail")
    var transits: TransitS? = null
    @JvmField
    @Autowired(name = "endAddress")
    var endAddress = ""
    @JvmField
    @Autowired(name = "startAddress")
    var startAddress = ""

    override fun getLayout(): Int {
        return R.layout.activity_bus_line_detail
    }

    override fun setTitle(): String {
        return "结果详情"
    }

    override fun injectVm(): Class<BusLineDetailViewModel> = BusLineDetailViewModel::class.java
    override fun initView() {
        setAdapter()
    }

    @SuppressLint("LogNotTimber")
    private fun setAdapter() {
        val tagLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.recyBusLine.layoutManager = tagLayoutManager
        val busLineAdapter = BusLineDetailAdapter(mutableListOf(), startAddress, endAddress)
        mBinding.recyBusLine.adapter = busLineAdapter
        busLineAdapter.addHeaderView(getHeaderView())
        val list: MutableList<BusLineDetailBean> = mutableListOf()
        list.add(BusLineDetailBean(BUS_HEADER, null))
        if (transits != null && transits!!.segments.size > 0) {
            for (data in transits!!.segments) {
                    list.add(BusLineDetailBean(BUS_WALK, data.walking))
                if (data.bus.buslines.size > 0) {
                    list.add(BusLineDetailBean(BUS_BUS, data.bus.buslines[0]))
                }
            }
        }

        list.add(BusLineDetailBean(BUS_FOOTER, null))
        busLineAdapter.replaceData(list)
    }


    open lateinit var header: View
    @SuppressLint("SetTextI18n")
    private fun getHeaderView(): View? {
        header = layoutInflater.inflate(R.layout.header_bus_detail, null)
        header.tv_start_address.text = "$startAddress-$endAddress"
        header.tv_bus_time.text = TimeSwitch.secondToTime((transits?.duration ?: "0").toLong())
        header.tv_bus_dis.text = setWalkDis(transits?.walking_distance ?: 0)
        val itemBusAdapter = object : RecyclerViewAdapter<ItemBusLineChildBinding, Segments>(R.layout.item_bus_line_child) {
            override fun setVariable(mBinding: ItemBusLineChildBinding, position: Int, childItem: Segments) {
                if (position == transits!!.segments.filter { it.bus.buslines.size > 0 }.size - 1) {
                    mBinding.childViewLine.visibility = View.GONE
                } else {
                    mBinding.childViewLine.visibility = View.VISIBLE
                }

                if (childItem.bus.buslines.size > 0) {
//                    if (childItem.bus.buslines[0].type!!.contains("地铁")){
                    if (childItem.bus.buslines[0].type!! is String) {
                        if ((childItem.bus.buslines[0].type!! as String).contains("地铁")) {
                            val phone_normal = resources.getDrawable(R.mipmap.service_bus_result_icon_metro)
                            DrawableUtil.leftDrawable(phone_normal, mBinding.itemBusAddress)
                            if (childItem.bus.buslines[0].name!!.contains("地铁")) {
                                mBinding.itemBusAddress.text = childItem.bus.buslines[0].name!!.split("地铁")[1].split("(")[0]
                            }
                        } else {
                            val phone_normal = resources.getDrawable(R.mipmap.service_bus_result_icon_bus)
                            DrawableUtil.leftDrawable(phone_normal, mBinding.itemBusAddress)
                            if (childItem.bus.buslines[0].name!!.contains("(")) {
                                mBinding.itemBusAddress.text = childItem.bus.buslines[0].name!!.split("(")[0]
                            }
                        }
                    }

                }


            }
        }
        if (transits!!.segments.size > 0) {
            itemBusAdapter.add(transits!!.segments.filter { it.bus.buslines.size > 0 } as MutableList<Segments>)
        }
       var  gridLayoutManager=MyLayoutManager()/*GridLayoutManager(BaseApplication.context, 3)*/
        gridLayoutManager.isAutoMeasureEnabled = true
        header.recy_address.layoutManager = gridLayoutManager
        header.recy_address.adapter = itemBusAdapter
        return header

    }

    /**
     *设置步行距离
     */
    @SuppressLint("SetTextI18n")
    private fun setWalkDis(walkingDistance: Long): String {
        val walkingDis = MathUtil.div(walkingDistance.toDouble(), 1000.0, 1)
        return if (walkingDis > 0) {
            "步行" + walkingDis + "公里"
        } else {
            "步行" + walkingDistance + "米"
        }

    }

    override fun initData() {
    }
}
