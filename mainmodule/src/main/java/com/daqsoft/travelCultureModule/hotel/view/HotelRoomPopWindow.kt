package com.daqsoft.travelCultureModule.hotel.view

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.PopHotelRoomBinding
import com.daqsoft.mainmodule.databinding.PopWindowRouterAppointmentBinding
import com.daqsoft.mainmodule.databinding.PopWindowScenicAppointmentBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.HotelRoomInfoBean
import com.daqsoft.provider.bean.RouterReservationBean
import com.daqsoft.provider.bean.ScenicReservationBean
import com.daqsoft.provider.utils.HtmlUtils
import com.daqsoft.provider.view.convenientbanner.bean.VideoImageBean
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.convenientbanner.holder.VideoImageHolder
import com.daqsoft.travelCultureModule.hotel.view.CustomNumberPicker
import me.nereo.multi_image_selector.BigImgActivity


/**
 * @Description 预约说明弹窗
 * @ClassName   AppointmentPopWindow
 * @Author      luoyi
 * @Time        2020/4/16 16:59
 */
class HotelRoomPopWindow : PopupWindow {
    /**
     * 订票须知
     */
    private var hotelRoomInfoBean: HotelRoomInfoBean? = null


    private var mViewAppointment: View? = null

    private var binding: PopHotelRoomBinding? = null
    private var context: Context? = null

    constructor(context: Context) : super(context) {
        this.context = context
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.pop_hotel_room,
            null,
            false
        )
        mViewAppointment = binding!!.root
        initView()
        initPopWindow()
    }

    private fun initPopWindow() {
        contentView = mViewAppointment
        this.width = ViewGroup.LayoutParams.MATCH_PARENT
        // 设置弹出窗体的高
        this.height = ViewGroup.LayoutParams.WRAP_CONTENT
        // 设置弹出窗体可点击()
        this.isFocusable = true;
        this.isOutsideTouchable = true;
        // 实例化一个ColorDrawable颜色为半透明
        val dw = ColorDrawable(0x00FFFFFF);

        //设置弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }

    /**
     * 更新数据
     */
    public fun updateData(hotelRoomInfoBean: HotelRoomInfoBean) {
        this.hotelRoomInfoBean = hotelRoomInfoBean
        binding?.scrollHotelRoomPop?.smoothScrollTo(0, 0)
        hotelRoomInfoBean?.let {
            binding?.txtPopHotelRoomName?.text = "${it.roomName}"
            val strTag = StringBuilder("")
            // 处理tags
            strTag.append("${it.refundDeclare} | 极速入住")
            binding?.txtPopHotelRoomTags?.text = strTag.toString()
            if (!it.acreage.isNullOrEmpty()) {
                binding?.txtHotelRoomArea?.text = "房间面积：" + it.acreage+"平米"
            } else {
                binding?.txtHotelRoomArea?.text = "房间面积：暂无"
            }
            if (it.num > 0) {
                binding?.txtHotelRoomNum?.text = "可住人数：可住${it.num}人"
            } else {
                binding?.txtHotelRoomNum?.text = "可住人数：暂无"
            }
            if (!it.window.isNullOrEmpty()) {
                binding?.txtHotelRoomWindow?.text = "是否有窗：${it.window}"
            } else {
                binding?.txtHotelRoomWindow?.text = "是否有窗：暂无"
            }
            if (!it.breakfastStr.isNullOrEmpty()) {
                binding?.txtHotelRoomBreakfast?.text = "早  餐：${it.breakfastStr}"
            } else {
                binding?.txtHotelRoomBreakfast?.text = "早  餐：暂无"
            }
            var bedstr = ""
            if (!it.bedType.isNullOrEmpty()) {
                bedstr = it.bedType
            }
            if (!it.bedLongWide.isNullOrEmpty()) {
                if (!bedstr.isNullOrEmpty()) {
                    bedstr = "$bedstr,"
                }
                bedstr += it.bedLongWide
            }
            if (!bedstr.isNullOrEmpty()) {
                binding?.txtHotelRoomBedType?.text = "床  型：${bedstr}"
            } else {
                binding?.txtHotelRoomBedType?.text = "床  型：暂无"
            }
            if (!it.wholeView.isNullOrEmpty()) {
                binding?.imgPopHotelRoom720?.visibility = View.VISIBLE
                binding?.imgPopHotelRoom720?.onNoDoubleClick {
                    ARouter.getInstance()
                        .build(ARouterPath.Provider.WEB_ACTIVITY)
                        .withString("mTitle", it.roomName)
                        .withString("html", it.wholeView)
                        .navigation()
                }
            } else {
                binding?.imgPopHotelRoom720?.visibility = View.GONE
            }
            // 服务
            if (!it.service.isNullOrEmpty()) {
                binding?.txtHotelRoomServiceValue?.text = HtmlUtils.html2Str(it.service)
                binding?.txtHotelRoomService?.visibility = View.VISIBLE
                binding?.txtHotelRoomServiceValue?.visibility = View.VISIBLE
            } else {
                binding?.txtHotelRoomService?.visibility = View.GONE
                binding?.txtHotelRoomServiceValue?.visibility = View.GONE
            }
            // 房型特色
            if (!it.specialty.isNullOrEmpty()) {
                binding?.txtHotelRoomSpcail?.visibility = View.VISIBLE
                binding?.txtHotelRoomSpcailValue?.visibility = View.VISIBLE
                binding?.txtHotelRoomSpcailValue?.text = it.specialty
            } else {
                binding?.txtHotelRoomSpcail?.visibility = View.GONE
                binding?.txtHotelRoomSpcailValue?.visibility = View.GONE
            }
            // 预订须知
            if (!it.cancelTimeStr.isNullOrEmpty()) {
                binding?.txtHotelRoomOrderCance?.visibility = View.VISIBLE
                binding?.txtHotelRoomOrderCance?.text = "订单取消：" + it.cancelTimeStr
            } else {
                binding?.txtHotelRoomOrderCance?.visibility = View.GONE
            }
            if (it.confirmType.isNullOrEmpty()) {
                binding?.txtHotelRoomConfirmAction?.visibility = View.VISIBLE
                binding?.txtHotelRoomConfirmAction?.text = "确认方式：" + it.confirmType
            } else {
                binding?.txtHotelRoomConfirmAction?.visibility = View.GONE
            }
            if (!it.refundDeclare.isNullOrEmpty()) {
                binding?.txtHotelRoomRefundTip?.visibility = View.VISIBLE
                binding?.txtHotelRoomRefundTip?.text = "退款说明：" + it.refundDeclare
            } else {
                binding?.txtHotelRoomRefundTip?.visibility = View.GONE
            }
            // 房型图片
            var dataVideoImages: MutableList<VideoImageBean> = mutableListOf()
            //视频
            if (!it.vedio.isNullOrEmpty()) {
                dataVideoImages.add(0, VideoImageBean().apply {
                    type = 1
                    videoUrl = it.vedio
                    imageUrl =  it.vedioCover
                })
            }
            //图片
            if (!it.imageUrls.isNullOrEmpty()) {
                for (item in it.imageUrls) {
                    dataVideoImages.add(VideoImageBean().apply {
                        type = 0
                        imageUrl = hotelRoomInfoBean!!.url + item
                    })
                }
            }
            if (!dataVideoImages.isNullOrEmpty()) {
                binding?.cbPopHotelRoom?.visibility = View.VISIBLE
                binding?.cbPopHotelRoom?.setPages(object : CBViewHolderCreator {
                    override fun createHolder(itemView: View?): Holder<*> {
                        return VideoImageHolder(itemView!!, context!!)
                    }

                    override fun getLayoutId(): Int {
                        return R.layout.layout_video_image
                    }
                }, dataVideoImages)?.setCanLoop(false)?.setPointViewVisible(false)?.setOnItemClickListener {
                    when (dataVideoImages[it].type) {
                        0 -> {
                            val intent =
                                Intent(context!!, BigImgActivity::class.java)
                            intent.putExtra("position", it)
                            intent.putStringArrayListExtra(
                                "imgList",
                                ArrayList(hotelRoomInfoBean?.imageUrls!!.toMutableList())
                            )
                            context!!.startActivity(intent)
                        }

                    }
                }
            } else {
                binding?.cbPopHotelRoom?.visibility = View.GONE
            }

            if (!it.feeExclude.isNullOrEmpty()) {
                binding?.txtHotelRoomCashInclude?.visibility = View.VISIBLE
                binding?.txtHotelRoomCashIncludeValue?.visibility = View.VISIBLE
                binding?.txtHotelRoomCashIncludeValue?.text = it.feeExclude
            } else {
                binding?.txtHotelRoomCashInclude?.visibility = View.GONE
                binding?.txtHotelRoomCashIncludeValue?.visibility = View.GONE
            }
        }
    }


    private fun initView() {
        binding?.root?.onNoDoubleClick {
            dismiss()
        }
        setOnDismissListener {
            binding?.cbPopHotelRoom?.stopVideoPlayer()
        }
    }

    fun onbackPress(): Boolean? {
        return binding?.cbPopHotelRoom?.onBackPress()
    }

}