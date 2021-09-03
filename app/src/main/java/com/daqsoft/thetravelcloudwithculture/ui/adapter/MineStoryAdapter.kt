package com.daqsoft.thetravelcloudwithculture.ui.adapter

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.vlayout.LayoutHelper
import com.bumptech.glide.Glide
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.bean.HomeStoryBean
import com.daqsoft.provider.uiTemplate.BaseDelegateAdapter
import com.daqsoft.provider.uiTemplate.TemplateType
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.ItemIndexMineStoryBinding

/**
 * 个人中心我的故事适配器
 */
class MineStoryAdapter(helper: LayoutHelper) : BaseDelegateAdapter<ItemIndexMineStoryBinding>(helper, R.layout.item_index_mine_story) {
    var datas: MutableList<HomeStoryBean> = mutableListOf()
    override fun bindDataToView(mBinding: ItemIndexMineStoryBinding, position: Int) {

        if (datas.isNullOrEmpty()) {
            mBinding.vEmptyTime.visibility = View.VISIBLE
            mBinding.vMineStory.visibility = View.GONE
        } else {
            mBinding.vEmptyTime.visibility = View.GONE
            mBinding.vMineStory.visibility = View.VISIBLE
            datas[0].run {
                if (this == null) {
                    mBinding.vEmptyTime.visibility = View.VISIBLE
                    mBinding.vMineStory.visibility = View.GONE
                } else {
                    var imageUrl: String = ""
                    if (!this.cover.isNullOrEmpty()) {
                        imageUrl = this.cover
                    } else if (!this.images.isNullOrEmpty()) {
                        imageUrl = this.images[0]
                    } else if (!this.videoCover.isNullOrEmpty()) {
                        imageUrl = this.videoCover
                    }
                    if (imageUrl.isNullOrEmpty()) {
                        mBinding.imgTimeCover.visibility = View.GONE
                    } else {
                        mBinding.imgTimeCover.visibility = View.VISIBLE
                        Glide.with(mBinding.root.context).load(imageUrl)
                            .placeholder(R.mipmap.placeholder_img_fail_240_180)
                            .into(mBinding.imgTimeCover)
                    }
                    if (!this.createDate.isNullOrEmpty()) {
                        mBinding.tvTime.text = this.createDate
                        mBinding.tvTime.visibility = View.VISIBLE
                    } else {
                        mBinding.tvTime.visibility = View.GONE
                    }
                    // 判断是否需要添加 标签
                    var ssb = SpannableStringBuilder()

                    if (!this.tagName.isNullOrEmpty()) {
                        ssb.append("#" + this.tagName + "#")
                        ssb.setSpan(
                            ForegroundColorSpan(mBinding.root.context.resources.getColor(com.daqsoft.mainmodule.R.color.colorPrimary)),
                            0,
                            ssb.length,
                            Spanned
                                .SPAN_INCLUSIVE_EXCLUSIVE
                        )
                    }
                    ssb.append(this.title)
                    mBinding.tvStoryContent.text = ssb

                    if (!ssb.isNullOrEmpty()) {
                        mBinding.tvStoryContent.visibility = View.VISIBLE
                    } else {
                        mBinding.tvStoryContent.visibility = View.GONE
                    }

                    // 地址
                    if (this.resourceRegionName.isNullOrEmpty()) {
                        // 判断是否关联地址和类型
                        mBinding.tvAddressTag.visibility = View.GONE
                    } else {
                        mBinding.tvAddressTag.visibility = View.VISIBLE
                    }
                    mBinding.tvAddressTag.text = "" + DividerTextUtils.convertDotString(
                        StringBuilder(), this.resourceRegionName,
                        this.resourceName
                    )
                    mBinding.tvLike.text = "${this.likeNum}"
                    mBinding.tvComment.text = "${this.commentNum}"
                    mBinding.vMineStory.onNoDoubleClick {
                        if (AppUtils.isLogin()) {
                            if (this.storyType == Constant.STORY_TYPE_STORY) {
                                ARouter.getInstance()
                                    .build(MainARouterPath.MAIN_STORY_DETAIL)
                                    .withString("id", this.id)
                                    .withInt("type", 2)
                                    .navigation()
                            } else if (this.storyType == Constant.STORY_TYPE_STRATEGY) {
                                ARouter.getInstance()
                                    .build(MainARouterPath.MAIN_STRATEGY_DETAIL)
                                    .withString("id", this.id)
                                    .withInt("type", 1)
                                    .navigation()
                            }
                        } else {
                            ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                            ARouter.getInstance()
                                .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                                .navigation()
                        }
                    }

                    mBinding.tvMineAllStory.onNoDoubleClick {
                        if (AppUtils.isLogin()) {
                            ARouter.getInstance().build(MainARouterPath.MINE_STORY_LIST).navigation()
                        } else {
                            ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                            ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                                .navigation()
                        }
                    }
                }
            }
        }
        mBinding.tvEmptyShareStory.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                ARouter.getInstance().build(MainARouterPath.MAIN_STORY_STRATEGY_ADD)
                    .navigation()
            } else {
                ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                ARouter.getInstance()
                    .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
        mBinding.tvTimeShareStory.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                ARouter.getInstance().build(MainARouterPath.MAIN_STORY_STRATEGY_ADD)
                    .navigation()
            } else {
                ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                ARouter.getInstance()
                    .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
    }


    override fun getItemCount(): Int {
        return 1
    }

    override fun getItemViewType(position: Int): Int {
        return TemplateType.MINE_STORY
    }

}