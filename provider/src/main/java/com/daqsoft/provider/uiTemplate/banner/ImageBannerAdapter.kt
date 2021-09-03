package com.daqsoft.provider.uiTemplate.banner

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.utils.MenuJumpUtils
import com.youth.banner.adapter.BannerAdapter

/**
 * @Description
 * @ClassName   ImageBannerAdapter
 * @Author      luoyi
 * @Time        2020/10/10 19:16
 */
class ImageBannerAdapter(private val datas: MutableList<CommonTemlate>?) :
    BannerAdapter<CommonTemlate, ImageBannerAdapter.ImageBannerViewHolder>(datas) {


    class ImageBannerViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): ImageBannerViewHolder {
        val imageView = ImageView(parent!!.context)
        imageView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        return ImageBannerViewHolder(imageView)
    }

    override fun onBindView(
        holder: ImageBannerViewHolder?,
        data: CommonTemlate?,
        position: Int,
        size: Int
    ) {
        data?.let {
            GlideModuleUtil.loadCornerRadiusImage(it.imgUrl, holder!!.itemView as ImageView, R.mipmap.placeholder_img_fail_240_180, 5)
            holder?.itemView?.onNoDoubleClick {
                MenuJumpUtils.adJump(data!!)
            }
        }

    }
}