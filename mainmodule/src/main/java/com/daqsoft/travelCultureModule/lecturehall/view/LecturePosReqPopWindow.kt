package com.daqsoft.travelCultureModule.lecturehall.view

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.PopHotelRoomBinding
import com.daqsoft.mainmodule.databinding.PopPostLectureReqBinding
import com.daqsoft.provider.ARouterPath

/**
 * @Description 课程提问
 * @ClassName   LecturePosReqPopWindow
 * @Author      luoyi
 * @Time        2020/6/18 9:43
 */
class LecturePosReqPopWindow : PopupWindow {

    private var binding: PopPostLectureReqBinding? = null
    private var context: Context? = null
   var onPostCommentListener: OnPostCommentListener? = null

    constructor(context: Context) : super(context) {
        this.context = context
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.pop_post_lecture_req,
            null,
            false
        )
        initView()
        initPopWindow()
    }

    private fun initView() {
        binding?.root?.onNoDoubleClick {
            dismiss()
        }
        binding?.tvLecturePostComment?.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                var content: String? = binding?.edtLectureComment?.text.toString()
                if (content.isNullOrEmpty()) {
                    ToastUtils.showMessage("请输入评论内容！")
                    return@onNoDoubleClick
                }
                onPostCommentListener?.onPostComment(content)

            }else{
                ToastUtils.showUnLoginMsg()
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY).navigation()
            }
        }
    }

    private fun initPopWindow() {
        contentView = binding?.root
        this.width = ViewGroup.LayoutParams.MATCH_PARENT
        // 设置弹出窗体的高
        this.height = ViewGroup.LayoutParams.WRAP_CONTENT
        // 设置弹出窗体可点击()
        this.isFocusable = true;
        this.isOutsideTouchable = true;
        // 实例化一个ColorDrawable颜色为半透明
        val dw = ColorDrawable(0x00FFFFFF);

        //设置弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }

    interface OnPostCommentListener {
        fun onPostComment(content: String)
    }
}