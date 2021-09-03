package com.daqsoft.itinerary.widget

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintLayout
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.itinerary.R
import com.daqsoft.itinerary.bean.NearbyBean
import com.daqsoft.itinerary.databinding.ItineraryItemNearbyBinding
import com.daqsoft.provider.Constants
import com.daqsoft.provider.utils.DividerTextUtils
import kotlinx.android.synthetic.main.itinerary_window_nearby.view.*
import org.jetbrains.anko.windowManager
import java.lang.StringBuilder

/**
 * @Author：      邓益千
 * @Create by：   2020/5/15 16:57
 * @Description：
 */
class NearbyPopupWindow(private val mContext: Context) : PopupWindow() {

    init {
        initWindow()
    }

    private lateinit var view: View

    private var listener: OnPopupWindowListener? = null

    /**起始坐标*/
    private var startlatLng: LatLng? = null

    /**某一天的ID*/
    private var dayId = 0

    /**资源Id*/
    private var sourceId = 0

    /**数据展示的是餐馆or酒店，默认餐厅*/
    private var showType = Constants.TYPE_RESTAURANT

    private lateinit var nearbyAdapter: NearbyAdapter

    fun showAtLocation(parent: View?, data: MutableList<NearbyBean>) {
        nearbyAdapter.clear()
        nearbyAdapter.add(data)
        showAtLocation(parent, Gravity.BOTTOM, 0, 0)
    }

    /**
     * @param day 某一天的ID
     * @param sourceId 某一天的下级资源id
     */
    fun setIds(day: Int, sourceId: Int) {
        dayId = day
        this.sourceId = sourceId
    }

    fun setOnPopupWindowListener(listener: OnPopupWindowListener?) {
        this.listener = listener
    }

    /**设置起始坐标*/
    fun setStartlatLng(startlatLng: LatLng) {
        this.startlatLng = startlatLng
    }

    fun setTitle(title: String) {
        view.type_view.text = title
    }

    fun setShowType(type: String) {
        this.showType = type
    }

    private fun initWindow() {
        val dm = DisplayMetrics()
        mContext.windowManager.defaultDisplay.getRealMetrics(dm)

        nearbyAdapter = NearbyAdapter()
        nearbyAdapter.emptyViewShow = false
        view = LayoutInflater.from(mContext).inflate(R.layout.itinerary_window_nearby, null)
        view.recycler_view.apply {
            adapter = nearbyAdapter
            addItemDecoration(
                DividerItemDecoration(
                    1,
                    mContext.resources.getDimensionPixelSize(R.dimen.dp_15)
                )
            )
            val param = layoutParams as ConstraintLayout.LayoutParams
            param.height = (dm.heightPixels * 0.5).toInt()
            layoutParams = param
        }
        view.close_view.setOnClickListener {
            dismiss()
        }

        contentView = view

        width = -1
        height = -1
        isFocusable = true
        isOutsideTouchable = true
        setBackgroundDrawable(ColorDrawable(mContext.resources.getColor(R.color.moietyTransparency)))
    }

    inner class NearbyAdapter :
        RecyclerViewAdapter<ItineraryItemNearbyBinding, NearbyBean>(R.layout.itinerary_item_nearby) {

        override fun setVariable(
            mBinding: ItineraryItemNearbyBinding,
            position: Int,
            item: NearbyBean
        ) {
            item.sourceId = sourceId
            val pics = item.images.split(",")
            if (!pics.isNullOrEmpty()) {
                mBinding.url = pics[0]
            }
            if (showType == Constants.TYPE_RESTAURANT) {
                mBinding.labelName.text = DividerTextUtils.convertString(
                    StringBuilder(), if (item.consumPerson.isNullOrEmpty()) {
                        ""
                    } else {
                        "人均${item.consumPerson}元"
                    }
                )
                if (!item.openTime.isNullOrEmpty()) {
                    mBinding.timeView.text = "开放时间：${item.openTime}"
                }
            } else {
                if (!item.activity.isNullOrEmpty()) {
                    mBinding.labelName.text = "可预订"
                    mBinding.timeView.text = ""
                }
            }
            if (!item.activity.isNullOrEmpty()) {
                val activity = "在线活动${item.activity.size}个"
                val ssb = SpannableStringBuilder(activity).apply {
                    val greenSpan =
                        ForegroundColorSpan(mBinding.root.context.resources.getColor(R.color.color_36CD64))
                    setSpan(greenSpan, 4, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                mBinding.activityView.text = ssb
            }
            mBinding.hotelNameView.text = item.name
            mBinding.addressView.text =
                "距离景区：${countDistance(LatLng(item.latitude, item.longitude))} | ${item.address}"

            mBinding.root.onNoDoubleClick {
                dismiss()
                listener?.call(dayId, showType, item)
            }
        }

    }

    /**
     * 计算两个坐标的距离
     * @param endLatLng 结束点
     */
    fun countDistance(endLatLng: LatLng): String {
        var km = ""
        startlatLng?.let {
            val distance = AMapUtils.calculateLineDistance(it, endLatLng)
            km = "${Utils.toKm(distance.toDouble())}KM"
        }
        return km
    }

    interface OnPopupWindowListener {
        fun call(dayId: Int, dateType: String, data: NearbyBean)
    }

}