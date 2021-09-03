package com.daqsoft.venuesmodule.adapter

import android.content.Context
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.Contact
import com.daqsoft.provider.bean.HelathInfoBean
import com.daqsoft.provider.bean.HelathSetingBean
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ItemReserationPersonBinding

/**
 * @Description 预约人信息
 * @ClassName   VenueResPersonAdapter
 * @Author      luoyi
 * @Time        2020/8/3 16:17
 */
class VenueResPersonAdapter : RecyclerViewAdapter<ItemReserationPersonBinding, Contact> {
    var mContext: Context? = null
    var onVenueResPersonListener: OnVenueResPersonListener? = null
    var selectPos: Int = -1

    constructor(context: Context) : super(R.layout.item_reseration_person) {
        this.mContext = context
    }

    override fun setVariable(mBinding: ItemReserationPersonBinding, position: Int, item: Contact) {
        item?.let {
            mBinding.bean = item
            mBinding.postion = if (position == 0) {
                mBinding.tvToInputInfo.text = "点击填写预约人信息"
                "预约人"
            } else {
                mBinding.tvToInputInfo.text = "点击填写随行人信息"
                "随行人${position}"
            }
            mBinding.tvReserationPhone.text = "手机号:${Utils.phoneInvisible(it.phone)}"
            if (!it.certType.isNullOrEmpty() && !it.certNumber.isNullOrEmpty()) {
                if (it.certType!!.toLowerCase() == "id_card") {
                    mBinding.tvReservationIdcard.text =
                        "身份证：${Utils.IDNumberInvisible(it.certNumber)}"
                } else {
                    if (!it.certTypeName.isNullOrEmpty()) {
                        mBinding.tvReservationIdcard.text = "${it.certTypeName}:${it.certNumber}"
                    } else {
                        mBinding.tvReservationIdcard.text = "证件号:${it.certNumber}"
                    }
                }
            } else {
                mBinding.tvReservationIdcard.text = "未知信息"
            }
            if (it.healthInfoBean != null) {
                mBinding.tvRegisterAddress.text = "${it.healthInfoBean!!.healthRegionAddress}"
                dealHealthCodeInfo(it.healthInfoBean!!, mBinding)
                dealTravelCodeInfo(it.healthInfoBean!!, mBinding)
            } else {
                mBinding.llvTravelCodeInfo.visibility = View.GONE
                mBinding.vReserationHealthCodeInfo.visibility = View.GONE
            }
        }
        if (selectPos == position) {
            mBinding.vReserationUseInfo.visibility = View.VISIBLE
            mBinding.imgReserationOpen.setImageResource(R.mipmap.venue_arrow_up)
        } else {
            mBinding.vReserationUseInfo.visibility = View.GONE
            mBinding.imgReserationOpen.setImageResource(R.mipmap.venue_arrow_down)
        }
        if (position == getData().size - 1) {
            mBinding.vLineReserationPerson.visibility = View.GONE
        } else {
            mBinding.vLineReserationPerson.visibility = View.VISIBLE
        }
        mBinding.tvToInputInfo.onNoDoubleClick {
            onVenueResPersonListener?.toInputView(item, position)
        }
        mBinding.imgSelectIdcardPhoto.onNoDoubleClick {
            onVenueResPersonListener?.toPhotoIdCard(position)
        }
        mBinding.imgSelectVenueRtnName.onNoDoubleClick {
            onVenueResPersonListener?.toSelectContact(position)
        }
        mBinding.vReserationPersonInfo.onNoDoubleClick {
            updateSelectPos(position)
        }
        mBinding.vReserationOpen.onNoDoubleClick {
            updateSelectPos(position)
        }
        mBinding.imgReserationDel.onNoDoubleClick {
            if (selectPos != position) {
                selectPos = position
            } else {
                selectPos = -1
            }
            onVenueResPersonListener?.clearData(position, item.id)
//            updateSelectPos(-1)
        }
        mBinding.imgReserationEdt.onNoDoubleClick {
            onVenueResPersonListener?.toEditContact(position, item)
        }
    }

    private fun updateSelectPos(position: Int) {
        if (selectPos != position) {
            selectPos = position
        } else {
            selectPos = -1
        }
        try {
            notifyItemRangeChanged(0, itemCount, "updateSelectStatus")
        } catch (e: Exception) {

        }
    }

    override fun payloadUpdateUi(
        mBinding: ItemReserationPersonBinding,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads[0] == "updateSelectStatus") {
            if (selectPos == position) {
                mBinding.vReserationUseInfo.visibility = View.VISIBLE
                mBinding.imgReserationOpen.setImageResource(R.mipmap.venue_arrow_up)
            } else {
                mBinding.vReserationUseInfo.visibility = View.GONE
                mBinding.imgReserationOpen.setImageResource(R.mipmap.venue_arrow_down)
            }
        }
        super.payloadUpdateUi(mBinding, position, payloads)
    }

    interface OnVenueResPersonListener {
        fun toInputView(person: Contact, position: Int)
        fun toPhotoIdCard(postion: Int)
        fun toSelectContact(position: Int)
        fun clearData(position: Int, id: Int)
        fun toEditContact(position: Int, contact: Contact)
    }

    /**
     * 处理旅游码
     */
    private fun dealTravelCodeInfo(data: HelathInfoBean, mBinding: ItemReserationPersonBinding) {
        var healthSetingInfo = data.healthSetingBean
        healthSetingInfo?.let {
            if (healthSetingInfo!!.enableTravelCode) {
                // 智游码
                mBinding.llvTravelCodeInfo.visibility = View.VISIBLE
                mBinding.tvTravelCodeName.text = "${it.smartTravelName}状况"
                if (!data.smartTravelCodeRegisterStatus) {
                    // 未注册
                    mBinding.imgHealthCodeStatus.setImageResource(com.daqsoft.provider.R.mipmap.venue_book_condition_icon_unknown)
                    mBinding.tvHealthCodeStatus.text = "未注册"
                    mBinding.tvHealthCodeStatus.setTextColor(mContext!!.resources.getColor(com.daqsoft.provider.R.color.color_999))
                    if (!healthSetingInfo!!.enableHealthyCode) {
                    }
                } else {
                    // 注册
                    mBinding.imgHealthCodeStatus.setImageResource(com.daqsoft.provider.R.mipmap.venue_book_condition_icon_low)
                    mBinding.tvHealthCodeStatus.text = "已注册"
                    mBinding.tvHealthCodeStatus.setTextColor(mContext!!.resources.getColor(com.daqsoft.provider.R.color.c_36cd64))
                }
            } else {
                mBinding.llvTravelCodeInfo.visibility = View.GONE
            }
            mBinding.tvTravelCodeMore.onNoDoubleClick {
                ARouter.getInstance()
                    .build(ARouterPath.VenuesModule.VENUES_ZYTF_CODE_INFO_ACTIVITY)
                    .withString("introduce", healthSetingInfo!!.healthIntroduce)
                    .withString("name", "" + healthSetingInfo!!.smartTravelName)
                    .navigation()
            }
        }
    }

    /**
     * 处理健康码
     */
    private fun dealHealthCodeInfo(data: HelathInfoBean, mBinding: ItemReserationPersonBinding) {
        var healthSetingInfo = data.healthSetingBean
        healthSetingInfo?.let {
            if (healthSetingInfo!!.enableHealthyCode) {
                mBinding.vReserationHealthCodeInfo.visibility = View.VISIBLE
                // 健康状态 01 正常 11 黄码 31红码
                if (!data.healthCode.isNullOrEmpty()) {
                    when (data.healthCode) {
                        "01" -> {
                            // 绿码
                            mBinding.tvHealthStatus.text =
                                mContext!!.getString(com.daqsoft.provider.R.string.health_normal)
                            mBinding.tvHealthStatus.setTextColor(mContext!!.resources.getColor(com.daqsoft.provider.R.color.c_36cd64))
                            mBinding.imgHealthStatus.setImageResource(com.daqsoft.provider.R.mipmap.venue_book_condition_icon_low)
                            mBinding.tvHealthCanReservation.text =
                                mContext!!.getString(com.daqsoft.provider.R.string.health_can_reseravtion)
                            mBinding.tvHealthCanReservation.setTextColor(
                                mContext!!.resources.getColor(
                                    com.daqsoft.provider.R.color.c_36cd64
                                )
                            )
                        }
                        "11" -> {
                            // 黄码
                            mBinding.tvHealthStatus.text =
                                mContext!!.getString(com.daqsoft.provider.R.string.health_middle)
                            mBinding.tvHealthStatus.setTextColor(mContext!!.resources.getColor(com.daqsoft.provider.R.color.color_ff9e05))
                            mBinding.tvHealthCanReservation.setTextColor(
                                mContext!!.resources.getColor(
                                    com.daqsoft.provider.R.color.color_ff9e05
                                )
                            )
                            mBinding.imgHealthStatus.setImageResource(com.daqsoft.provider.R.mipmap.venue_book_condition_icon_middle)
                        }
                        "31" -> {
                            // 红码
                            mBinding.tvHealthStatus.text =
                                mContext!!.getString(com.daqsoft.provider.R.string.health_bad)
                            mBinding.tvHealthStatus.setTextColor(mContext!!.resources.getColor(com.daqsoft.provider.R.color.ff4e4e))
                            mBinding.tvHealthCanReservation.setTextColor(
                                mContext!!.resources.getColor(
                                    com.daqsoft.provider.R.color.ff4e4e
                                )
                            )
                            mBinding.imgHealthStatus.setImageResource(com.daqsoft.provider.R.mipmap.venue_book_condition_icon_high)
                        }
                    }
                }
            } else {
                mBinding.vReserationHealthCodeInfo.visibility = View.GONE
            }
        }
    }
}