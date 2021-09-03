package com.daqsoft.provider.businessview.adapter

import android.content.Context
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.ContentBean
import com.daqsoft.provider.databinding.ItemProviderContentBinding
import java.lang.Exception

/**
 * @Description
 * @ClassName   ProviderContentAdapter
 * @Author      luoyi
 * @Time        2020/7/1 16:36
 */
class ProviderContentAdapter : RecyclerViewAdapter<ItemProviderContentBinding, ContentBean> {
    var mContext: Context? = null

    constructor(context: Context) : super(R.layout.item_provider_content) {
        this.mContext = context
    }

    override fun setVariable(mBinding: ItemProviderContentBinding, position: Int, item: ContentBean) {

        item?.let {

            mBinding.title = it.title
            mBinding.time = DateUtil.formatDateByString(
                "yyyy-MM-dd", "yy-MM-dd HH:mm",
                it.publishTime
            )
            var imageUrl: String? = ""
            if (!it.imageUrls.isNullOrEmpty()) {
                var image = it.imageUrls[0]
                if (image != null && !image.url.isNullOrEmpty()) {
                    imageUrl = image.url
                }
            }
            if (it.video != null) {
                try {
                    var video: String? = it.video.toString()
                    if (!video.isNullOrEmpty()) {
                        mBinding.imgProviderContent.visibility = View.VISIBLE
                    } else {
                        mBinding.imgProviderVideo.visibility = View.GONE
                    }
                } catch (e: Exception) {
                    mBinding.imgProviderVideo.visibility = View.GONE
                }

            } else {
                mBinding.imgProviderVideo.visibility = View.GONE
            }
            GlideModuleUtil.loadDqImageWaterMark(imageUrl,mContext!!,mBinding.imgProviderContent)
        mBinding.root.onNoDoubleClick {
            item?.let {
                if (it != null) {
                    if (it!!.contentType.equals("IMAGE")) {
                        ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_IMG)
                            .withString("id", it!!.id.toString())
                            .withInt("type", 1)
                            .navigation()
                    } else {
                        ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_INFO)
                            .withString("id", it!!.id.toString())
                            .withString("contentTitle", "资讯详情")
                            .withInt("type", 1)
                            .navigation()
                    }
                }
            }
        }
        }

    }
}