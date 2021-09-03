package com.daqsoft.travelCultureModule.contentActivity

import android.content.Context
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemContentLsBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.utils.StringUtils
import com.daqsoft.travelCultureModule.clubActivity.bean.ClubZixunBean
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

/**
 * @Description
 * @ClassName   ConentLsAdapter
 * @Author      luoyi
 * @Time        2020/6/4 16:06
 */
class ConentLsAdapter : RecyclerViewAdapter<ItemContentLsBinding, ClubZixunBean> {

    var mContext: Context? = null

    constructor(context: Context) : super(R.layout.item_content_ls) {
        mContext = context
    }

    override fun setVariable(mBinding: ItemContentLsBinding, position: Int, item: ClubZixunBean) {
        if (item != null) {
            var prePosition = position - 1
            // 0 小图 1 大图
            var type = 0
            if (prePosition < 0) {
                type = 1
            } else {
                var preItem = getData()[prePosition]
                var time1 = DateUtil.formatDate("yyyy-MM-dd", preItem.publishTime)
                var time2 = DateUtil.formatDate("yyyy-MM-dd", item.publishTime)
                if (time1 != null && time2 != null) {
                    if (DateUtil.isSameDate(time1, time2)) {
                        type = 0
                    } else {
                        type = 1
                    }
                }
            }
            when (type) {
                0 -> {
                    // 小图模式
                    mBinding.llSmallImgContent.visibility = View.VISIBLE
                    mBinding.llBigImgContent.visibility = View.GONE
                    mBinding.tvSmallContentName.text = item.title
                    mBinding.tvSmallTime.text = item.publishTime + "发布"
                    mBinding.tvSmallCommentNum.text = "" + item.commentNum
                    if (item.tagName.isNullOrEmpty()) {
                        mBinding.tvSmallContentTag.visibility = View.GONE
                    } else {
                        mBinding.tvSmallContentTag.visibility = View.VISIBLE
                        if (item.tagName.size > 0) {
                            var tag = item.tagName[0]
                            if (tag != null) {
                                mBinding.tvSmallContentTag.text = "" + tag
                            }
                        }
                    }
                    var imageUrl: String? = ""
                    if (item.contentType == "VIDEO") {
                        item.video?.let {
                            if (!it.imgUrl.isNullOrEmpty()) {
                                imageUrl = it.imgUrl
                            }
                        }
                    } else if (item.contentType == "AUDIO") {
                        item.audio?.let {
                            if (!it.imgUrl.isNullOrEmpty()) {
                                imageUrl = it.imgUrl
                            }
                        }
                    } else {
                        if (!item.imageUrls.isNullOrEmpty()) {
                            imageUrl = item.imageUrls[0].url
                        }
                    }
                    if (imageUrl.isNullOrEmpty()) {
                        mBinding.imgSmallContent.visibility = View.GONE
                    } else {
                        mBinding.imgSmallContent.visibility = View.VISIBLE
                        Glide.with(mContext!!)
                            .load(imageUrl)
                            .placeholder(R.mipmap.placeholder_img_fail_240_180)
                            .into(mBinding.imgSmallContent)
                    }
                }
                1 -> {
                    // 大图模式
                    mBinding.llSmallImgContent.visibility = View.GONE
                    mBinding.llBigImgContent.visibility = View.VISIBLE
                    mBinding.tvContentName.text = item.title
                    mBinding.tvBigTime.text = item.publishTime + "发布"
                    mBinding.tvBigCommentNum.text = "" + item.commentNum
                    if (item.tagName.isNullOrEmpty()) {
                        mBinding.tvBigContentTag.visibility = View.GONE
                    } else {
                        mBinding.tvBigContentTag.visibility = View.VISIBLE
                        if (item.tagName.size > 0) {
                            var tag = item.tagName[0]
                            if (tag != null) {
                                mBinding.tvBigContentTag.text = "" + tag
                            }
                        }
                    }
                    var imageUrl: String? = ""
                    if (item.contentType == "VIDEO") {
                        mBinding.imgVideoPlay.visibility = View.VISIBLE
                        mBinding.tvVideo.visibility = View.VISIBLE
                    } else {
                        mBinding.imgVideoPlay.visibility = View.GONE
                        mBinding.tvVideo.visibility = View.GONE
                    }
                    if (item.contentType == "VIDEO") {
                        item.video?.let {
                            if (!it.imgUrl.isNullOrEmpty()) {
                                imageUrl = it.imgUrl
                            }
                        }
                    } else if (item.contentType == "AUDIO") {
                        item.audio?.let {
                            if (!it.imgUrl.isNullOrEmpty()) {
                                imageUrl = it.imgUrl
                            }
                        }
                    } else {
                        if (!item.imageUrls.isNullOrEmpty()) {
                            imageUrl = item.imageUrls[0].url
                        }
                    }
                    if (imageUrl.isNullOrEmpty()) {
                        mBinding.imgBigContent.visibility = View.GONE
                    } else {
                        mBinding.imgBigContent.visibility = View.VISIBLE
                        Glide.with(mContext!!)
                            .load(imageUrl)
                            .placeholder(R.mipmap.placeholder_img_fail_240_180)
                            .into(mBinding.imgBigContent)
                    }
                }
            }
        }
        mBinding.root.onNoDoubleClick {
            if (!item.sourceUrl.isNullOrEmpty()) {
                ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
                    .withString("html", StringUtils().addHttp(item.sourceUrl))
                    .navigation()
            } else {
                if (item.contentType.equals("IMAGE")) {
                    ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_IMG)
                        .withString("id", item.id.toString())
                        .navigation()
                } else {
                    ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_INFO)
                        .withString("id", item.id.toString())
                        .withString("contentTitle", "资讯详情")
                        .navigation()
                }
            }
        }
    }
}