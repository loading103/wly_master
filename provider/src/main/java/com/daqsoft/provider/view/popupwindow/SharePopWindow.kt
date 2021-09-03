package com.daqsoft.provider.view.popupwindow

import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.file.ShareDownLoadFileUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.SharePlatBean
import com.daqsoft.provider.databinding.ItemSharePlateBinding
import com.daqsoft.provider.databinding.LayoutSharePopwindowBinding
import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb


/**
 * @Description 分享弹窗
 * @ClassName   SharePopWindow
 * @Author      luoyi
 * @Time        2020/6/1 14:55
 */
class SharePopWindow : PopupWindow {

    var binding: LayoutSharePopwindowBinding? = null

    private var mViewAppointment: View? = null

    private var mContext: Activity? = null
    private var shareMeida: UMWeb? = null

    /**
     * 分享平台
     */
    private val plates: MutableList<SharePlatBean> = mutableListOf(
//        SharePlatBean("QQ好友", R.mipmap.share_icon_qq, "qq"),
//        SharePlatBean("QQ空间", R.mipmap.share_icon_qqzone, "qqzone"),
        SharePlatBean("微信好友", R.mipmap.share_icon_wechat, "weixin"),
        SharePlatBean("朋友圈", R.mipmap.share_icon_friend, "wxcircle")
    )
    constructor(context: Activity) : super(context) {
        mContext = context
        isOutsideTouchable = true
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_share_popwindow, null, false)
        mViewAppointment = binding!!.root
        initView()
        initPopWindow()
    }

    /**
     * 设置分享内容
     * @param title 分享标题
     * @param content 分享内容
     * @param imageUrl 分享图片地址
     * @param shareUrl 分享地址
     */
    fun setShareContent(title: String?, content: String?, imageUrl: String?, shareUrl: String?) {
        Log.e("分享链接-----------",title)
        shareMeida = UMWeb(shareUrl)
        shareMeida?.title = title
        shareMeida?.description = content
        // 没有传缩略图
        if(!imageUrl.isNullOrBlank()){
            ShareDownLoadFileUtil.downNetworkImage(imageUrl,object :ShareDownLoadFileUtil.DownImageListener{
                override fun onDownLoadImageSuccess() {
                    val bitmap = ShareDownLoadFileUtil.getBitmap()
                    mContext?.let {
                        if (bitmap == null) {
                            shareMeida?.setThumb(UMImage(mContext, imageUrl)) //网络缩略图
                        } else {
                            shareMeida?.setThumb(UMImage(mContext, bitmap)) //本地缩略图
                        }
                    }
                }
            })
        }
    }



    private fun initView() {
        adapter?.emptyViewShow = false
        binding?.recySharePlats?.layoutManager =
            GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false)
        binding?.recySharePlats?.adapter = adapter
        adapter?.clear()
        adapter.add(plates)
        binding?.tvShareCance?.onNoDoubleClick {
            dismiss()
        }
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


    val adapter = object :
        RecyclerViewAdapter<ItemSharePlateBinding, SharePlatBean>(R.layout.item_share_plate) {
        override fun setVariable(
            mBinding: ItemSharePlateBinding,
            position: Int,
            item: SharePlatBean
        ) {
            item?.let {
                if (item.sharePlatRes != 0) {
                    mBinding.imgSharePlat.setImageResource(item.sharePlatRes!!)
                }
                mBinding.tvSharePlat.text = item.sharePlatTitle
            }
            mBinding.root.onNoDoubleClick {
                dismiss()
                when (item.sharePlatType) {
                    "qq" -> {
                        if (mContext != null) {
                            ShareAction(mContext!!)
                                .setPlatform(SHARE_MEDIA.QQ)
                                .withMedia(shareMeida)
                                .share()
                        }
                    }
                    "qqzone" -> {
                        ShareAction(mContext!!)
                            .setPlatform(SHARE_MEDIA.QZONE)
                            .withMedia(shareMeida)
                            .share()
                    }
                    "weixin" -> {
                        Log.e("分享链接-----------",shareMeida?.toUrl())
                        ShareAction(mContext!!)
                            .setPlatform(SHARE_MEDIA.WEIXIN)
                            .withMedia(shareMeida)
                            .share()
                    }
                    "wxcircle" -> {
                        ShareAction(mContext!!)
                            .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                            .withMedia(shareMeida)
                            .share()
                    }
                }
            }
        }
    }
}