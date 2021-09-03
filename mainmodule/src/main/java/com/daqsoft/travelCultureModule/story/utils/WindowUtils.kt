package com.daqsoft.travelCultureModule.story.utils

import android.app.Activity
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemNoPassReasonResourceBinding
import com.daqsoft.mainmodule.databinding.ItemNoPassReasonWordBinding
import com.daqsoft.mainmodule.databinding.WindowStoryFailReasonBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.network.home.bean.NoPassMsgBean
import com.daqsoft.provider.network.home.bean.NoPassResourceBean
import com.daqsoft.provider.network.home.bean.NoPassWordBean

/**
 * window弹框工具类
 * @author 黄熙
 * @date 2020/2/20 0020
 * @version 1.0.0
 * @since JDK 1.8
 */
object WindowUtils {

    /**
     * 初始化时光故事不通过原因弹框
     * @param activity 上下文
     */
    fun initStoryFailedReasonWindow(activity: Activity, data: NoPassMsgBean): PopupWindow {
        // popupwindow初始化
        val popupWindow = Utils.initPopupWindow(
            activity,
            0.8f,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            activity.resources.getDimension(R.dimen.dp_500).toInt()
        )
        // 数据绑定
        var binding = DataBindingUtil.inflate<WindowStoryFailReasonBinding>(
            LayoutInflater.from(activity), R.layout.window_story_fail_reason, null, false
        )
        popupWindow.contentView = binding.root
        popupWindow.animationStyle = R.style.AnimBottom
        popupWindow.setOnDismissListener {
            Utils.setWindowBackGroud(activity, 1f)
        }
        // 文字适配器
        var wordAdaper = object : RecyclerViewAdapter<ItemNoPassReasonWordBinding, NoPassWordBean>
            (R.layout.item_no_pass_reason_word) {
            override fun setVariable(
                mBinding: ItemNoPassReasonWordBinding,
                position: Int,
                item: NoPassWordBean
            ) {
                mBinding.item = item
                var content =
                    (position + 1).toString() + "、“" + item.context + "”" + item.label + ";"
                val spannableString = SpannableString(content)
                spannableString.setSpan(
                    ForegroundColorSpan(Color.RED),
                    (position + 1).toString().length + 1,
                    (position + 1).toString().length + 2 + item.context.length + 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                mBinding.tvContent.setText(spannableString)
            }

        }
        binding.recyclerWord.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = wordAdaper
        }
        // 图片适配器
        var imageAdaper = object : RecyclerViewAdapter<ItemNoPassReasonResourceBinding,
                NoPassResourceBean>
            (R.layout.item_no_pass_reason_resource) {
            override fun setVariable(
                mBinding: ItemNoPassReasonResourceBinding,
                position: Int,
                item: NoPassResourceBean
            ) {
                mBinding.item = item
            }

        }
        binding.recyclerPicture.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = imageAdaper
        }
        // 视频适配器
        var videoAdaper = object : RecyclerViewAdapter<ItemNoPassReasonResourceBinding,
                NoPassResourceBean>
            (R.layout.item_no_pass_reason_resource) {
            override fun setVariable(
                mBinding: ItemNoPassReasonResourceBinding,
                position: Int,
                item: NoPassResourceBean
            ) {
                mBinding.ivPlay.visibility = View.VISIBLE
                mBinding.item = item
            }

        }
        binding.recyclerVideo.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = videoAdaper
        }
        // 音频适配器
        var aduioAdaper = object : RecyclerViewAdapter<ItemNoPassReasonResourceBinding,
                NoPassResourceBean>
            (R.layout.item_no_pass_reason_resource) {
            override fun setVariable(
                mBinding: ItemNoPassReasonResourceBinding,
                position: Int,
                item: NoPassResourceBean
            ) {
                mBinding.ivPlay.visibility = View.VISIBLE
                mBinding.item = item
            }

        }
        binding.recyclerAudio.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = aduioAdaper
        }
        if (data != null) {
            if (data.textList != null && data.textList.isNotEmpty()) {
                binding.llWord.visibility = View.VISIBLE
                wordAdaper.add(data.textList)
                wordAdaper.notifyDataSetChanged()
            } else {
                binding.llWord.visibility = View.GONE
            }
            if (data.imageList != null && data.imageList.isNotEmpty()) {
                binding.llPicture.visibility = View.VISIBLE
                imageAdaper.add(data.imageList)
                imageAdaper.notifyDataSetChanged()
            } else {
                binding.llPicture.visibility = View.GONE
            }
            if (data.videoList != null && data.videoList.isNotEmpty()) {
                binding.llVideo.visibility = View.VISIBLE
                videoAdaper.add(data.videoList)
                videoAdaper.notifyDataSetChanged()
            } else {
                binding.llVideo.visibility = View.GONE
            }
            if (data.voiceList != null && data.voiceList.isNotEmpty()) {
                binding.llAudio.visibility = View.VISIBLE
                aduioAdaper.add(data.voiceList)
                aduioAdaper.notifyDataSetChanged()
            } else {
                binding.llAudio.visibility = View.GONE
            }
        }
        binding.tvCancel.setOnClickListener {
            popupWindow.dismiss()
        }
        binding.tvSure.setOnClickListener {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_STORY_STRATEGY_ADD)
                .withInt("type",1)
                .navigation()
            popupWindow.dismiss()
        }
        return popupWindow
    }


}