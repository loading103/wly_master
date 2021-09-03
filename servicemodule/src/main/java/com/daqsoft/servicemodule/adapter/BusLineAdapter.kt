package com.daqsoft.servicemodule.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.android.scenic.servicemodule.R
import com.daqsoft.android.scenic.servicemodule.databinding.*
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.provider.ARouterPath
import com.daqsoft.servicemodule.bean.Segments
import com.daqsoft.servicemodule.bean.TransitS
import com.daqsoft.servicemodule.uitils.DrawableUtil
import com.daqsoft.servicemodule.uitils.MathUtil
import com.daqsoft.servicemodule.uitils.TimeSwitch
import com.daqsoft.servicemodule.view.MyLayoutManager
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

/**
 * desc :公交线路适配器
 * @author 江云仙
 * @date 2020/4/3 14:20
 * @version 1.0.0
 * @since JDK 1.8
 */
open class BusLineAdapter(var startAddress: String, var endAddress: String) : RecyclerViewAdapter<ItemBusLineBinding, TransitS>(R.layout.item_bus_line) {
    @SuppressLint("CheckResult")
    override fun setVariable(mBinding: ItemBusLineBinding, position: Int, item: TransitS) {
        mBinding.tvBusTime.text = TimeSwitch.secondToTime((item.duration ?: "0").toLong())
        setWalkDis(item.walking_distance, mBinding.tvBusDis)
        var itemBusAdapter = object : RecyclerViewAdapter<ItemBusLineChildBinding, Segments>(R.layout.item_bus_line_child) {
            override fun setVariable(mBinding: ItemBusLineChildBinding, position: Int, childItem: Segments) {
                if (position==item.segments.filter { it.bus.buslines.size>0 }.size-1){
                    mBinding.childViewLine.visibility= View.GONE
                }else{
                    mBinding.childViewLine.visibility= View.VISIBLE
                }

                if (childItem.bus.buslines.size>0){
                    if (childItem.bus.buslines[0].type!! is String){
                        if (((childItem.bus.buslines[0].type!!) as String).contains("地铁")){
                            val phone_normal = BaseApplication.context.resources.getDrawable(R.mipmap.service_bus_result_icon_metro)
                            DrawableUtil.leftDrawable(phone_normal,mBinding.itemBusAddress)
                            if (childItem.bus.buslines[0].name!!.contains("地铁")) {
                                mBinding.itemBusAddress.text = childItem.bus.buslines[0].name!!.split("地铁")[1].split("(")[0]
                            }
                        }else{
                            val phone_normal = BaseApplication.context.resources.getDrawable(R.mipmap.service_bus_result_icon_bus)
                            DrawableUtil.leftDrawable(phone_normal,mBinding.itemBusAddress)
                            if (childItem.bus.buslines[0].name!!.contains("(")) {
                                mBinding.itemBusAddress.text = childItem.bus.buslines[0].name!!.split("(")[0]
                            }
                        }
                    }else{

                    }


                }


            }
        }
        if(item.segments.size>0){
            itemBusAdapter.add(item.segments.filter { it.bus.buslines.size>0 } as MutableList<Segments>)
        }
        val gridLayoutManager = MyLayoutManager()/*GridLayoutManager(BaseApplication.context, 3)*/
        gridLayoutManager.isAutoMeasureEnabled = true
        mBinding.recyAddress.layoutManager = gridLayoutManager
        mBinding.recyAddress.adapter = itemBusAdapter
        //适配器点击事件
        RxView.clicks(mBinding.root)
            // 1秒内不可重复点击或仅响应一次事件
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                //跳转到结果详情
                ARouter.getInstance()
                    .build(ARouterPath.ServiceModule.SERVICE_BUS_LINE_DETAIL_ACTIVITY)
                    .withParcelable("busLineChildDetail" ,item)
                    .withString("startAddress",startAddress)
                    .withString("endAddress",endAddress)
                    .navigation()
            }
    }

    /**
     *设置步行距离
     */
    @SuppressLint("SetTextI18n")
    private fun setWalkDis(walkingDistance: Long, tvBusDis: TextView) {
        val walkingDis = MathUtil.div(walkingDistance.toDouble(), 1000.0, 1)
        if (walkingDis > 0) {
            tvBusDis.text = "步行" + walkingDis + "公里"
        } else {
            tvBusDis.text = "步行" + walkingDistance + "米"
        }

    }
}