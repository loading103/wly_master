package com.daqsoft.usermodule.ui.order.widget

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextSwitcher
import androidx.databinding.DataBindingUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.PopSingleCanceAppointBinding
import java.lang.reflect.Field

/**
 * @Description
 * @ClassName   SingleCanceAppointPopWindow
 * @Author      luoyi
 * @Time        2020/8/5 9:27
 */
class SingleCanceAppointPopWindow : PopupWindow {
    private var binding: PopSingleCanceAppointBinding? = null
    private var context: Context? = null
    var orderUserNum: Int = 1
    var currentCanceNum: Int = 1
    var selectReson: Int = 0
    var selectResonStr: String = ""
    var onSingleCanceAppointListener: OnSingleCanceAppointListener? = null

    constructor(context: Context) : super(context) {
        this.context = context
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.pop_single_cance_appoint,
            null,
            false
        )
        initPopWindow()
        initViewEvent()
        context?.let {
        }
    }

    fun inSelectReason() {
        selectReson = 0
        context?.let {
            selectResonStr = context!!.resources.getString(R.string.cance_appoint_reson_one)
        }
        currentCanceNum = orderUserNum
        binding?.edtOrderNum?.text = "${orderUserNum}"
    }

    private fun initViewEvent() {
        binding?.imgCloseCanceAppoint?.onNoDoubleClick {
            dismiss()
        }
        binding?.imgAddOrderNum?.onNoDoubleClick {
            synchronized(this) {
                if (currentCanceNum < orderUserNum) {
                    currentCanceNum += 1
                    binding?.edtOrderNum?.text = "${currentCanceNum}"
                }
            }
        }
        binding?.imgReduceOrderNum?.onNoDoubleClick {
            synchronized(this) {
                if (currentCanceNum > 1) {
                    currentCanceNum -= 1
                    binding?.edtOrderNum?.text = "${currentCanceNum}"
                }
            }
        }
        binding?.vCanceAppointOne?.onNoDoubleClick {
            selectReson = 0
            selectResonStr = context!!.resources.getString(R.string.cance_appoint_reson_one)
            changeSelectReson()
        }
        binding?.vCanceAppointTwo?.onNoDoubleClick {
            selectReson = 1
            selectResonStr = context!!.resources.getString(R.string.cance_appoint_reson_two)
            changeSelectReson()
        }
        binding?.vCanceAppointThree?.onNoDoubleClick {
            selectReson = 2
            selectResonStr = context!!.resources.getString(R.string.cance_appoint_reson_three)
            changeSelectReson()
        }
        binding?.edtInputCanceRemark?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    var str = s.toString().length
                    if (str <= 100) {
                        binding?.tvRemarkTextNum?.text = "${str}/100"
                    } else {
                        binding?.tvRemarkTextNum?.text = "100/100"
                    }

                } else {
                    binding?.tvRemarkTextNum?.text = "0/100"
                }
            }

        })
        binding?.vCanceAppointFour?.onNoDoubleClick {
            selectReson = 3
            selectResonStr = context!!.resources.getString(R.string.cance_appoint_reson_four)
            changeSelectReson()
        }
        binding?.tvConfirmInput?.onNoDoubleClick {
            var remark: String? = binding?.edtInputCanceRemark?.text.toString()
            onSingleCanceAppointListener?.onSingleCanceOrder(
                currentCanceNum,
                selectResonStr,
                remark
            )
            dismiss()
        }
    }

    private fun changeSelectReson() {
        initSelectResonView()
        when (selectReson) {
            0 -> {
                binding?.imgCanceResonOne?.setImageResource(R.mipmap.refund_application_selected)
                binding?.tvCanceResonOne?.setTextColor(context!!.resources.getColor(R.color.color_333))
            }
            1 -> {
                binding?.imgCanceResonTwo?.setImageResource(R.mipmap.refund_application_selected)
                binding?.tvCanceResonTwo?.setTextColor(context!!.resources.getColor(R.color.color_333))
            }
            2 -> {
                binding?.imgCanceResonThree?.setImageResource(R.mipmap.refund_application_selected)
                binding?.tvCanceResonThree?.setTextColor(context!!.resources.getColor(R.color.color_333))
            }
            3 -> {
                binding?.imgCanceResonFour?.setImageResource(R.mipmap.refund_application_selected)
                binding?.tvCanceResonFour?.setTextColor(context!!.resources.getColor(R.color.color_333))
            }
        }
    }

    fun initSelectResonView() {
        binding?.imgCanceResonOne?.setImageResource(R.mipmap.refund_application_normal)
        binding?.imgCanceResonTwo?.setImageResource(R.mipmap.refund_application_normal)
        binding?.imgCanceResonThree?.setImageResource(R.mipmap.refund_application_normal)
        binding?.imgCanceResonFour?.setImageResource(R.mipmap.refund_application_normal)
        context?.let {
            binding?.tvCanceResonOne?.setTextColor(context!!.resources.getColor(R.color.color_666))
            binding?.tvCanceResonTwo?.setTextColor(context!!.resources.getColor(R.color.color_666))
            binding?.tvCanceResonThree?.setTextColor(context!!.resources.getColor(R.color.color_666))
            binding?.tvCanceResonFour?.setTextColor(context!!.resources.getColor(R.color.color_666))
        }


    }

    private fun initPopWindow() {
        binding?.let {
            contentView = it.root
        }

        this.width = ViewGroup.LayoutParams.MATCH_PARENT
        // 设置弹出窗体的高
        this.height = ViewGroup.LayoutParams.MATCH_PARENT
        // 设置弹出窗体可点击()
        this.isFocusable = true
        this.isOutsideTouchable = true
        // 实例化一个ColorDrawable颜色为半透明
        val dw = ColorDrawable(0x00FFFFFF);
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
//            isClippingEnabled = false
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            try {
//                val mLayoutInScreen: Field =
//                    PopupWindow::class.java.getDeclaredField("mLayoutInScreen")
//                mLayoutInScreen.isAccessible = true
//                mLayoutInScreen.set(this, true)
//            } catch (e: NoSuchFieldException) {
//                e.printStackTrace()
//            } catch (e: IllegalAccessException) {
//                e.printStackTrace()
//            }
//        }
        //设置弹出窗体的背景
        this.setBackgroundDrawable(dw);

    }

    interface OnSingleCanceAppointListener {
        fun onSingleCanceOrder(orderNum: Int, canceReason: String, remark: String?)
    }
}