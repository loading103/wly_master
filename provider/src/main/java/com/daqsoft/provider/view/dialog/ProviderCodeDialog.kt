package com.daqsoft.provider.view.dialog

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.utils.file.DownLoadFileUtil
import com.daqsoft.provider.R
import kotlinx.android.synthetic.main.dialog_code_provider.*
import org.jetbrains.anko.sdk27.coroutines.onLongClick

/**
 * @Description
 * @ClassName   ProviderCodeDialog
 * @Author      luoyi
 * @Time        2020/9/28 9:29
 */
class ProviderCodeDialog() : DialogFragment(), View.OnClickListener {
     var url = "http://site241962.c.daqctc.com/img/snsj-ewm.562237a2.jpg"
     var name: String? = ""
    override fun onClick(view: View) {
        when (view.id) {
            R.id.tv_know -> {
                dismiss()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity!!.setFinishOnTouchOutside(false)
    }

    override fun onStart() {
        super.onStart()
        //添加动画
        val window = dialog!!.window
        val dm = DisplayMetrics()
//        window!!.setLayout((dm.widthPixels * 0.75).toInt(),ViewGroup.LayoutParams.WRAP_CONTENT)
        activity!!.windowManager.defaultDisplay.getMetrics(dm)
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        layoutParams.width =/*activity!!.windowManager.defaultDisplay.*/dm.widthPixels * 3 / 4
        layoutParams.windowAnimations = R.style.dialogWindowAnim
        window.attributes = layoutParams

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.dialog_code_provider, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
        img_code?.onLongClick {
            // 保存图片到本地
            downloadImage(url)
            dismiss()
        }

    }

    private fun downloadImage(url: String) {
        try {
            DownLoadFileUtil.downNetworkImage(url, object : DownLoadFileUtil.DownImageListener {
                override fun onDownLoadImageSuccess() {
                    ToastUtils.showMessage("保存二维码成功~")
                }
            })
        } catch (e: Exception) {
            ToastUtils.showMessage("保存二维码失败~")
        }
    }

    private fun initView() {
        tv_know.setOnClickListener(this)
        Glide.with(BaseApplication.context)
            .load(url)
            .into(dialog!!.img_code)
        title?.text = name ?: ""

    }

}