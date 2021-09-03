package com.daqsoft.provider.bean

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 订单随行人实体类
 * @author 黄熙
 * @date 2020/8/11 0011
 * @version 1.0.0
 * @since JDK 1.8
 */

class OrderUserListBean(
        /**
         * 已取消
         */
        var cancel: List<OrderUserBean>,
        /**
         * 已失效
         */
        var lose: List<OrderUserBean>,
        /**
         * 待使用
         */
        var wait: List<OrderUserBean>,
        /**
         * 已使用
         */
        var end: List<OrderUserBean>

)

/**
 * 随行人的实体类
 */
class OrderUserBean(
        /**
         *用户手机号
         */
        var userPhone: String,
        /**
         *用户姓名
         */
        var userName: String,
        /**
         *证件类型
         */
        var userCardType: String,
        /**
         *证件号
         */
        var userCardNumber: String,
        /**
         *订单状态
         */
        var orderStatus: Int,
        /**
         *是否为领队
         */
        var leader: Int,
        /**
         *数据ID
         */
        var id: Int,
        /**
         * 是否选中
         */
        var select: Boolean = false


) :Parcelable{
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readByte() != 0.toByte()) {
    }

    override fun toString(): String {
        return "OrderUserBean(userPhone='$userPhone', userName='$userName', userCardType='$userCardType', userCardNumber='$userCardNumber', orderStatus=$orderStatus, leader=$leader, id=$id, select=$select)"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userPhone)
        parcel.writeString(userName)
        parcel.writeString(userCardType)
        parcel.writeString(userCardNumber)
        parcel.writeInt(orderStatus)
        parcel.writeInt(leader)
        parcel.writeInt(id)
        parcel.writeByte(if (select) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrderUserBean> {
        override fun createFromParcel(parcel: Parcel): OrderUserBean {
            return OrderUserBean(parcel)
        }

        override fun newArray(size: Int): Array<OrderUserBean?> {
            return arrayOfNulls(size)
        }
    }
}