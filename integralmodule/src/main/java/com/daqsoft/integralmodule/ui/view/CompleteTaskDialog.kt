package com.daqsoft.integralmodule.ui.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.daqsoft.integralmodule.R
import com.daqsoft.integralmodule.databinding.IntegralmoduleActivityPicIntegralBinding

/**
 * @Description 完成任务弹窗
 * @ClassName   CompleteTaskDialog
 * @Author      luoyi
 * @Time        2020/4/28 13:43
 */
class CompleteTaskDialog : Dialog {

    var onClickCompleteListener: OnClickCompleteListener? = null

    constructor(context: Context) : super(context) {
        initView()
    }

    var binding: IntegralmoduleActivityPicIntegralBinding? = null

    private fun initView() {
        binding = DataBindingUtil.inflate<IntegralmoduleActivityPicIntegralBinding>(
            layoutInflater, R.layout.integralmodule_activity_pic_integral, null, false
        )

        binding?.mCancelIv?.setOnClickListener {
            dismiss()
        }
        binding?.mSeeIntegralTv?.setOnClickListener {
            onClickCompleteListener?.onClickIntegralDetail()
            dismiss()
        }
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setContentView(binding!!.root)
        setCanceledOnTouchOutside(false)
    }

    public fun updateContent(name: String?, synopsis: String?, rewardIntegral: String?) {
        if (!name.isNullOrEmpty()) {
            binding?.mTaskNameTv?.text = name
        }
        if (!synopsis.isNullOrEmpty()) {
            binding?.mTaskDesTv?.text = synopsis
        }
        if (!rewardIntegral.isNullOrEmpty()) {
            binding?.mIntegralTv?.text = "+${rewardIntegral}分"
        }
    }

    interface OnClickCompleteListener {
        fun onClickIntegralDetail()
    }
}