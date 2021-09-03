package com.daqsoft.travelCultureModule.itrobot.view

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.daqsoft.mainmodule.R
import kotlinx.android.synthetic.main.layout_recoder_info_dialog.*

/**
 * @Description
 * @ClassName   RecoderInfoDialog
 * @Author      luoyi
 * @Time        2020/5/25 12:13
 */
class RecoderInfoDialog : AlertDialog {
    private var mTvRobotStatus: TextView? = null
    private var mImgRobotAnim: ImageView? = null
    private var mContext: Context? = null

    constructor(context: Context?) : super(context, R.style.RecoderInfoDialog) {

        mContext = context
        init()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_recoder_info_dialog)
        startAnimation()
    }

    private fun init() {
        setCancelable(true)
    }


    override fun show() {
        startAnimation()
        super.show()
    }

    private fun startAnimation() {
        try {
            val drawable = img_robot_gif?.background as AnimationDrawable
            if (!drawable.isRunning) {
                drawable.start()
            }

        } catch (e: Exception) {
            Log.e("ssss", e.message)
        }
    }

    override fun dismiss() {
        try {
            val drawable = img_robot_gif?.background as AnimationDrawable
            if (drawable.isRunning) {
                drawable.stop()
            }
        } catch (e: Exception) {
        }
        super.dismiss()
    }

    fun updateText(status: String?) {
        tv_robot_staus?.text = status
    }
}