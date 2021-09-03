package com.daqsoft.travelCultureModule.hotel.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.UIHelperUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.baselib.widgets.timepicker.DatePopupWindow
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.LayoutHotelSelectRoomBinding
import com.daqsoft.provider.bean.HotelRoomBean
import com.daqsoft.travelCultureModule.hotel.adapter.HotelRoomAdapter
import java.lang.Exception
import java.util.*

/**
 * @Description 酒店选择房间
 * @ClassName   HotelSelectRoomView
 * @Author      luoyi
 * @Time        2020/4/8 15:28
 */
class HotelSelectRoomView : FrameLayout {
    /**
     * 时间选择window
     */
    var datePickPopWindow: DatePopupWindow? = null

    var mContext: Context? = null

    var binding: LayoutHotelSelectRoomBinding? = null
    /**
     * 酒店房间适配器
     */
    var hotelRoomAdapter: HotelRoomAdapter? = null
    /**
     * 开始时间
     */
    var startTime: String? = ""
    /**
     * 结束时间
     */
    var endTime: String? = ""
    /**
     * 房间数目
     */
    var roomNum: String = "1"

    private val startGroup = -1
    private val endGroup = -1
    private val startChild = -1
    private val endChild = -1

    public var onclickDateListener: OnClickSelectDateListener? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        initView()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.layout_hotel_select_room,
            this,
            false
        )
        addView(binding!!.root)
        // 当前时间
        val date = Date()
        binding?.txtHotelStartTime?.text = DateUtil.getDateDayString(date)
        binding?.txtHotelStartStatus?.text = "今天"
        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.DAY_OF_MONTH, 1)
        // 明天的时间
        val tomorrow = cal.time
        binding?.txtHotelEndTime?.text = DateUtil.getDateDayString(tomorrow)
        binding?.txtHotelEndStatus?.text = DateUtil.getDayOfWeek(tomorrow)
        binding?.root?.onNoDoubleClick {
            if (datePickPopWindow == null) {
                datePickPopWindow = DatePopupWindow.Builder(mContext!!, Calendar.getInstance().getTime(), binding?.root)
                    .setInitSelect(startGroup, startChild, endGroup, endChild)
                    .setColor(R.color.colorPrimary_un,R.color.colorPrimary)
                    .setConfrimDrawable(R.drawable.shape_corlor_primary_btn_bg)
                    .setInitDay(false)
                    .setDateOnClickListener(object : DatePopupWindow.DateOnClickListener {
                        override fun getDate(
                            startDate: String?, endDate: String?, startGroupPosition: Int,
                            startChildPosition: Int, endGroupPosition: Int, endChildPosition: Int,
                            dayOffSet: String
                        ) {
                            val startDateL = DateUtil.getHotelDateByString(startDate)
                            val endDateL = DateUtil.getHotelDateByString(endDate)
                            binding?.txtHotelStartTime?.text = DateUtil.getDateDayString(startDateL)
                            binding?.txtHotelEndTime?.text = DateUtil.getDateDayString(endDateL)
                            binding?.txtHotelSelectDay?.text = dayOffSet + "晚"

                            if (DateUtil.isSameDate(startDateL, Date())) {
                                binding?.txtHotelStartStatus?.text = "今天"
                            } else {
                                binding?.txtHotelStartStatus?.text = DateUtil.getDayOfWeek(startDateL)
                            }
                            binding?.txtHotelEndStatus?.text = DateUtil.getDayOfWeek(endDateL)
                            startTime = startDate
                            endTime = endDate
                            if (onclickDateListener != null) {
                                onclickDateListener!!.onClick(startDate, endDate)
                            }
                        }
                    })
                    .builder()
            }
            datePickPopWindow?.showAtLocation(binding?.root,Gravity.NO_GRAVITY,0,0)
        }
        hotelRoomAdapter = HotelRoomAdapter(mContext!!)
        binding?.recyHotelRoomLs?.layoutManager = LinearLayoutManager(mContext!!, LinearLayoutManager.VERTICAL, false)
        binding?.recyHotelRoomLs?.adapter = hotelRoomAdapter
        binding?.vHotelRoomMore?.onNoDoubleClick {
            try {
                if (hotelRoomAdapter!!.isShowMore) {
                    hotelRoomAdapter?.isShowMore = false
                    val drawable2 = mContext!!.getDrawable(com.daqsoft.provider.R.mipmap.provider_arrow_up)
                    drawable2.setBounds(0, 0, drawable2.minimumWidth, drawable2.minimumHeight)
                    binding?.txtHotelLookAllRooms?.setCompoundDrawables(null,null,drawable2,null)
                } else {
                    hotelRoomAdapter?.isShowMore = true
                    val drawable2 = mContext!!.getDrawable(com.daqsoft.provider.R.mipmap.provider_arrow_down)
                    drawable2.setBounds(0, 0, drawable2.minimumWidth, drawable2.minimumHeight)
                    binding?.txtHotelLookAllRooms?.setCompoundDrawables(null,null,drawable2,null)
                }
                hotelRoomAdapter?.notifyDataSetChanged()
            } catch (e: Exception) {

            }

        }
        binding?.txtHotelRoomNum?.onNoDoubleClick {
            if (onclickDateListener != null) {
                onclickDateListener!!.onSelectRoom()
            }
        }
        hotelRoomAdapter?.onHotelRoomListener = object : HotelRoomAdapter.OnHotelRoomListener {
            override fun onHotelRoomClick(roomSn: String) {
                if (onclickDateListener != null) {
                    onclickDateListener?.onShowRoomInfo(startTime!!, endTime!!, roomSn)
                }
            }

        }
    }


    fun updateData(datas: MutableList<HotelRoomBean>?) {
        if (!datas.isNullOrEmpty()) {
            val size = datas.size
            if (size > 3) {
                binding?.vHotelRoomMore?.visibility = View.VISIBLE
                hotelRoomAdapter?.isShowMore = true
            } else {
                binding?.vHotelRoomMore?.visibility = View.GONE
                hotelRoomAdapter?.isShowMore = false
            }
            hotelRoomAdapter?.clear()
            hotelRoomAdapter?.add(datas)
            binding?.recyHotelRoomLs?.visibility = View.VISIBLE
        } else {
            hotelRoomAdapter?.clear()
            binding?.recyHotelRoomLs?.visibility = View.GONE
        }
    }

    fun setTxtHotelRoomNum(s: String, num: String) {
        binding?.txtHotelRoomNum?.text = s
        roomNum = num
        hotelRoomAdapter?.roomNum = roomNum
    }

    /**
     * 设置酒店参数
     */
    fun setHotelRoomParam(startDate: String, endDate: String?) {
        this.startTime = startDate
        this.endTime = endDate
        hotelRoomAdapter?.startDate = startTime!!
        hotelRoomAdapter?.endDate = endTime!!
    }

    /**
     * 设置酒店店铺信息
     */
    fun setHotelShopInfo(shopName: String, shopUrl: String) {
        hotelRoomAdapter?.shopUrl = shopUrl
        hotelRoomAdapter?.shopName = shopName
    }


    public interface OnClickSelectDateListener {
        fun onClick(startDate: String?, endDate: String?)
        fun onSelectRoom()
        fun onShowRoomInfo(inTime: String, outTime: String, roomSn: String)
    }

}