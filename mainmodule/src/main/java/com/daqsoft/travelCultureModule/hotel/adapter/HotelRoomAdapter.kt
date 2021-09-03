package com.daqsoft.travelCultureModule.hotel.adapter

import android.content.Context
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemHotelRoomBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.HotelRoomBean

/**
 * @Description 酒店房间适配器
 * @ClassName   HotelRoomAdapter
 * @Author      luoyi
 * @Time        2020/4/9 14:26
 */
class HotelRoomAdapter : RecyclerViewAdapter<ItemHotelRoomBinding, HotelRoomBean> {

    var mContext: Context? = null
    var isShowMore: Boolean = false

    var shopName: String = ""
    var shopUrl: String = ""
    var roomNum: String = "1"
    var startDate: String = ""
    var endDate: String = ""
    var onHotelRoomListener: OnHotelRoomListener? = null

    constructor(context: Context) : super(R.layout.item_hotel_room) {
        mContext = context
    }

    override fun setVariable(mBinding: ItemHotelRoomBinding, position: Int, item: HotelRoomBean) {
        mBinding.txtHotelRoomName.text = "" + item.roomName
        // 处理图片
        if (!item.imageUrls.isNullOrEmpty()) {
            val image = item.imageUrls[0]
            Glide.with(mContext!!)
                .load(item.url + image)
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .into(mBinding.imgHotelRoom)
            mBinding.txtHotelRoomImgsNum.text = "${item.imageUrls.size}"
            mBinding.txtHotelRoomImgsNum.visibility = View.VISIBLE
            mBinding.vHotelRoomImg.visibility = View.VISIBLE
        } else {
            mBinding.vHotelRoomImg.visibility = View.GONE
            mBinding.txtHotelRoomImgsNum.visibility = View.GONE
        }
        // 是否有全景图
        if (!item.wholeView.isNullOrEmpty()) {
            mBinding.imgHotelRoom720.visibility = View.VISIBLE
        } else {
            mBinding.imgHotelRoom720.visibility = View.GONE
        }
        // 是否有视频
        if (!item.vedio.isNullOrEmpty()) {
            mBinding.imgHotelRoomVideo.visibility = View.VISIBLE
        } else {
            mBinding.imgHotelRoomVideo.visibility = View.GONE
        }
        // 处理info
        val strInfo = StringBuilder("")
        strInfo.append(getBreakFastStr(item.breakfast))
        if (!item.acreage.isNullOrEmpty()) {
            strInfo.append("·${item.acreage}平米")
        }
        if (!item.bedType.isNullOrEmpty()) {
            strInfo.append("·${item.bedType}")
        }
        if (!item.bedLongWide.isNullOrEmpty()) {
            strInfo.append(item.bedLongWide)
        }

        mBinding.txtHotelInfo.text = strInfo.toString()

        val strTag = StringBuilder("")
        // 处理tags
        strTag.append("${item.refundDeclare} | 极速入住")
        mBinding.txtHotelTags.text = strTag.toString()
        // 处理价格 和预订
        mBinding.txtHotelRoomPrice.text = "${item.salePrice}"
        if (item.stock < 5) {
            mBinding.txtHotelSurplusRoomNum.text = "仅剩下${item.stock}间"
            mBinding.txtHotelSurplusRoomNum.visibility = View.VISIBLE
        } else {
            mBinding.txtHotelSurplusRoomNum.visibility = View.GONE
        }
        // 预订
        mBinding.txtHotelConfirm.onNoDoubleClick {
            if (!item.hotelSn.isNullOrEmpty()) {
                if (AppUtils.isLogin()) {
                    var uuid = SPUtils.getInstance().getString(SPKey.UUID)
                    var token = SPUtils.getInstance().getString(SPKey.USER_CENTER_TOKEN)
                    var encry = SPUtils.getInstance().getString(SPKey.ENCRYPTION)
                    // 拼接跳转酒店下单页面地址
                    var url = "$shopUrl/hotel/confirm?hotelSn=${item.sn}&num=${roomNum}&startDate=${startDate}&endDate=${endDate}&encryption=${encry}" +
                            "&unid=${uuid}&token=${token}&timestamp=${System.currentTimeMillis()}"
                    ARouter.getInstance()
                        .build(ARouterPath.Provider.WEB_ACTIVITY)
                        .withString("mTitle", shopName)
                        .withString("html", url)
                        .navigation()
                } else {
                    ToastUtils.showMessage("非常抱歉，你还未登录~")
                    ARouter.getInstance()
                        .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                        .navigation()
                }
            }
        }
        mBinding.root.onNoDoubleClick {
            if(onHotelRoomListener!=null){
                onHotelRoomListener?.onHotelRoomClick(item.sn)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (isShowMore) {
            if(getData().size>3) {
                3
            }else{
                getData().size
            }
        } else {
            getData().size
        }
    }

    /**
     * 根据早餐类型获取 早餐中文
     */
    fun getBreakFastStr(breakFast: Int): String {
        return when (breakFast) {
            0 -> {
                mContext!!.resources.getString(R.string.breakfast_0)
            }
            1 -> {
                mContext!!.resources.getString(R.string.breakfast_1)
            }
            2 -> {
                mContext!!.resources.getString(R.string.breakfast_2)
            }
            3 -> {
                mContext!!.resources.getString(R.string.breakfast_3)
            }
            else -> {
                ""
            }
        }
    }

    interface OnHotelRoomListener {
        fun onHotelRoomClick(roomSn: String)
    }
}