package com.daqsoft.usermodule.view

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.*
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.LayoutNumberOperateBinding
import org.jetbrains.anko.toast

class NumberOperateView : LinearLayout {
    /**
     * 能输入的最大值
     */
    var maxNumber: Int? = 0

    /**
     * 当前输入的值
     */
    var number: Int? = 0
        get() {
            return field
        }
        set(value) {

            if (mBinding != null) {
                mBinding!!.etNumber.setText(value.toString())
            }
            field = value
        }


    var mBinding: LayoutNumberOperateBinding? = null

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_number_operate,
            null, false)
        addView(mBinding!!.root)
        var temp: String = "0"
        mBinding!!.add.setOnClickListener {
            if (number!! <= (maxNumber!! - 1)) {
                number = number?.plus(1)
                mBinding!!.etNumber.setSelection(number.toString().length)
            } else {
                context!!.toast("输入的最大数字为$maxNumber")
            }
            number?.let { it1 -> textChangeListener(it1) }
        }
        mBinding!!.increase.setOnClickListener {

            if (number!! > 0) {
                number = number?.plus(-1)
                mBinding!!.etNumber.setSelection(number.toString().length)
            } else {
                context!!.toast("输入的最小数字为0")
            }
            number?.let { it1 -> textChangeListener(it1) }
        }
        mBinding!!.etNumber.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                temp = s.toString()
                // 当变化前为空时置0
                if (temp == "") {
                    temp = "0"
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s.toString().length < temp.length) {
                    // 由后向前删除数字时
                    if (s.toString().equals("")) {
                        if (temp.toInt() < 10) {
                            number = 0
                            mBinding!!.etNumber.setSelection(mBinding!!.etNumber.text.length)
                            return
                        }
                        number = temp.toInt()
                        mBinding!!.etNumber.setSelection(mBinding!!.etNumber.text.length)
                        return
                    }

                } else {
                    // 增加数字时
                    if (s.toString().equals("")) {
                        if (temp.toInt() < 10) {
                            number = 0
                            mBinding!!.etNumber.setSelection(mBinding!!.etNumber.text.length)
                            return
                        }
                        number = temp.toInt()
                        mBinding!!.etNumber.setSelection(mBinding!!.etNumber.text.length)
                        return
                    }

                    var num = s.toString().toInt()
                    // 输入值不能大于最大值
                    if (num > maxNumber!!) {
                        context!!.toast("输入的最大数字为" + maxNumber)
                        number = temp.toInt()
                        mBinding!!.etNumber.setSelection(mBinding!!.etNumber.text.length)
                        return
                    }
                    // 输入值不能都为0
                    if (s.toString().startsWith("00")) {
                        number = 0
                        return
                    }
                }
                // 防止循环调用
                if (temp != s.toString()) {
                    number = s.toString().toInt()
                    mBinding!!.etNumber.setSelection(mBinding!!.etNumber.text.length)
                }


            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    //    @InverseBindingMethods(
//            InverseBindingMethod(
//                    type = NumberOperateView::class,
//                    attribute = "number",
//                    method = "getText"
//            )
//    )
//
    class DataBindding {

        companion object {
            @JvmStatic
            @BindingAdapter("number", "maxNumber", requireAll = false)
            fun binding(view: NumberOperateView, number: Int, maxNumber: Int) {
                view.number = number
                view.maxNumber = maxNumber
            }
        }
    }

    lateinit var textChangeListener:(Int)->Unit


}