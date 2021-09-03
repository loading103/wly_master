package com.daqsoft.servicemodule.adapter

import android.graphics.Color
import android.os.Build
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.daqsoft.android.scenic.servicemodule.R
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.JavaUtils
import com.daqsoft.provider.view.convenientbanner.adapter.CBPageAdapterHelper
import com.daqsoft.provider.view.convenientbanner.utils.ScreenUtil
import com.daqsoft.servicemodule.bean.BusLineDetailBean
import com.daqsoft.servicemodule.bean.BusLineDetailBean.Companion.BUS_BUS
import com.daqsoft.servicemodule.bean.BusLineDetailBean.Companion.BUS_FOOTER
import com.daqsoft.servicemodule.bean.BusLineDetailBean.Companion.BUS_HEADER
import com.daqsoft.servicemodule.bean.BusLineDetailBean.Companion.BUS_WALK
import com.daqsoft.servicemodule.uitils.DrawableUtil
import com.daqsoft.servicemodule.uitils.TimeSwitch
import timber.log.Timber

/**
 *公交线路详情
 */
class BusLineDetailAdapter(data: List<BusLineDetailBean>, var startAddress: String, var endAddress: String) : BaseMultiItemQuickAdapter<BusLineDetailBean, BaseViewHolder>(data) {
    init {
        addItemType(BUS_HEADER, R.layout.item_bus_header)
        addItemType(BUS_FOOTER, R.layout.item_bus_header)
        addItemType(BUS_BUS, R.layout.item_bus_bus)
        addItemType(BUS_WALK, R.layout.item_bus_walk)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun convert(helper: BaseViewHolder, item: BusLineDetailBean?) {
        when (helper.itemViewType) {
            BUS_BUS -> {
                if (item?.message?.type!! is String) {
                    if ((item?.message?.type!! as String).contains("地铁")) {
                        helper.getView<ImageView>(R.id.img_trafic).setImageResource(R.mipmap.service_bus_result_icon_metro)
                    } else {
                        helper.getView<ImageView>(R.id.img_trafic).setImageResource(R.mipmap.service_bus_result_icon_bus)
                    }
                }

                helper.setText(R.id.tv_name, item?.message?.departure_stop?.name)
                helper.setText(R.id.station_name, item?.message?.departure_stop?.name)
                helper.setText(R.id.end_name, "开往" + item?.message?.name!!.split("--")[1].split(")")[0] + "方向")
                helper.setText(R.id.exit_name, item?.message?.arrival_stop?.name)
                // 设置部分字体颜色
                var busStr = ""
                if (item?.message?.via_stops?.size!! > 0) {
                    busStr = (item?.message?.via_stops?.size ?: 0).toString() + "站"
                }
                val busNextStr = "(" + TimeSwitch.secondToTime((item?.message?.duration ?: "0").toLong()) + ")"
                val busColor = JavaUtils.setTextColor(busStr + busNextStr, mContext.getColor(R.color.color_36cd64), 0, busStr.length)
                helper.setText(R.id.station_num, busColor)

                if (item?.message?.type!! is String) {
                    if ((item?.message?.type!! as String).contains("地铁")) {
                        if (item.message?.name!!.contains("地铁")) {
                            helper.setText(R.id.station_name, item.message?.name!!.split("地铁")[1].split("(")[0])
                        }
//                        val phone_normal = BaseApplication.context.resources.getDrawable(R.mipmap.service_bus_result_icon_metro)
                    } else {
//                        val phone_normal = BaseApplication.context.resources.getDrawable(R.mipmap.service_bus_result_icon_bus)
                        if (item?.message?.name!!.contains("(")) {
                            helper.setText(R.id.station_name, item?.message?.name!!.split("(")[0])
                        }
                    }
                }
            }
            BUS_WALK -> {
                Timber.e("时间" + item?.message?.duration.toString())
                helper.setText(R.id.tv_bus_walk, TimeSwitch.setWalkDis(item?.message?.distance?.toLongOrNull() ?: 0) + " (" + TimeSwitch.secondToTime((item?.message?.duration ?: "0").toLong()) + ")")
            }
            BUS_HEADER -> {
                helper.setText(R.id.tv_header, "起点（$startAddress)")
                val leftDrawable = BaseApplication.context.resources.getDrawable(R.drawable.green_oval)
                DrawableUtil.leftDrawable(leftDrawable, helper.getView(R.id.tv_header))
                val padding = ScreenUtil.dip2px(BaseApplication.context, 20f)
                helper.getView<TextView>(R.id.tv_header).setPadding(0, padding, 0, 0)
            }
            BUS_FOOTER -> {
                val leftDrawable = BaseApplication.context.resources.getDrawable(R.drawable.yellow_oval)
                DrawableUtil.leftDrawable(leftDrawable, helper.getView(R.id.tv_header))
                helper.setText(R.id.tv_header, "终点（$endAddress)")
                val padding = ScreenUtil.dip2px(BaseApplication.context, 20f)
                helper.getView<TextView>(R.id.tv_header).setPadding(0, 0, 0, padding)
            }

        }
    }

}