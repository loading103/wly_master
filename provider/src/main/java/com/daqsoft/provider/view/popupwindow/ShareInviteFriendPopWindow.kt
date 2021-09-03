package com.daqsoft.provider.view.popupwindow

import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.R
import com.daqsoft.provider.databinding.LayoutShareInviteFriendBinding
import com.umeng.socialize.ShareAction
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb

/**
 * @Description 邀请好友页面
 * @ClassName   ShareInviteFriendPopWindow
 * @Author      luoyi
 * @Time        2020/7/29 10:24
 */
class ShareInviteFriendPopWindow : PopupWindow {

    var binding: LayoutShareInviteFriendBinding? = null
    private var mViewAppointment: View? = null
    private var shareMeida: UMWeb? = null
    private var mContext: Activity? = null

    constructor(context: Activity) : super(context) {
        mContext = context
        isOutsideTouchable = true
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.layout_share_invite_friend,
            null,
            false
        )
        mViewAppointment = binding!!.root

        initPopWindow()
        initView()
    }

    private fun initPopWindow() {
        contentView = mViewAppointment
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

    /**
     * 设置分享内容
     * @param title 分享标题
     * @param content 分享内容
     * @param imageUrl 分享图片地址
     * @param shareUrl 分享地址
     */
    fun setShareContent(title: String?, content: String?, imageUrl: String?, shareUrl: String?) {
        shareMeida = UMWeb(shareUrl)
        mContext?.let {
            if (imageUrl.isNullOrEmpty()) {
                shareMeida?.setThumb(UMImage(mContext, R.mipmap.placeholder_img_fail_240_180))
            } else {
                shareMeida?.setThumb(UMImage(mContext, imageUrl))
            }
            shareMeida?.title = title
            shareMeida?.description = content
        }

    }

    private fun initView() {
        binding?.vWxFriend?.onNoDoubleClick {
            mContext?.let {
                ShareAction(mContext)
                    .setPlatform(SHARE_MEDIA.WEIXIN)
                    .withMedia(
                        shareMeida
                    )
                    .share()
            }
        }

        binding?.vWxCircle?.onNoDoubleClick {
            mContext?.let {
                ShareAction(mContext)
                    .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                    .withMedia(
                        shareMeida
                    )
                    .share()
            }

        }
        binding?.tvShareCance?.onNoDoubleClick {
            dismiss()
        }
    }
}