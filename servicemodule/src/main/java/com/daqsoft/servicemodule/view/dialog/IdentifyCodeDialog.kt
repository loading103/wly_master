package com.daqsoft.servicemodule.view.dialog

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.daqsoft.android.scenic.servicemodule.R
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.utils.file.DownLoadFileUtil
import kotlinx.android.synthetic.main.dialog_identify_code.*
import org.jetbrains.anko.sdk27.coroutines.onLongClick
/**
 * desc :拍照识花
 * @author 江云仙
 * @date 2020/4/22 11:06
 */
class IdentifyCodeDialog() : DialogFragment() , View.OnClickListener{
    private var url="http://site241962.c.daqctc.com/img/snsj-ewm.b1275249.jpg"
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.dialog_identify_code, container, false)

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

    }
}