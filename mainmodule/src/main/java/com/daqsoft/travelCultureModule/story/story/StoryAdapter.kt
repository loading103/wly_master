package com.daqsoft.travelCultureModule.story.story

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.databinding.ViewDataBinding
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.MultipleRecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemFindStrategyBinding
import com.daqsoft.mainmodule.databinding.ItemStoryListStrategyBinding
import com.daqsoft.mainmodule.databinding.ItemStoryMainBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.bean.HomeStoryBean
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.travelCultureModule.story.FindStrategyListActivity
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

/**
 * 故事适配器
 */
class StoryAdapter(
    context: Context, storyType: Int = 1,
    /**
     * 增加模块名（判断非遗跳转）
     */
    val module: String? = null
) : MultipleRecyclerViewAdapter<ViewDataBinding, HomeStoryBean>() {

    val mContext = context
    var isShowDelete = false
    var storyType = storyType

    override fun setVariable(mBinding: ViewDataBinding, position: Int, item: HomeStoryBean) {
        when (mBinding) {
            is ItemStoryMainBinding -> {
                if (item.sourceUrl.isNullOrEmpty()) {
                    mBinding.reprint.visibility = View.GONE
                } else {
                    mBinding.reprint.visibility = View.VISIBLE
                }
                // 是故事
                Glide.with(mContext).load(item.vipHead)
                    .placeholder(R.mipmap.mine_profile_photo_default).into(mBinding.ivUser)
                mBinding.name = item.vipNickName

                mBinding.likeNumber = item.likeNum.toString()

                mBinding.commentNumber = item.commentNum.toString()
                if (storyType == 2) {
                    mBinding.tvTime.visibility = View.VISIBLE
                    mBinding.tvTime.text = item.createDate
                    mBinding.ivUser.visibility =View.GONE
                    mBinding.tvUser.visibility =View.GONE
                    mBinding.reprint.visibility =View.GONE
                }

                // 图片数量
                if (!item.images.isNullOrEmpty()) {
                    mBinding.tvImageNumber.text =
                        mContext.getString(
                            R.string.home_story_image_number,
                            item.images.size.toString()
                        )
                    mBinding.tvImageNumber.visibility = View.VISIBLE
                } else {
                    mBinding.tvImageNumber.visibility = View.GONE
                }
                val typeStr = ResourceType.getName(item.resourceType)
                mBinding.location = DividerTextUtils.convertDotString(
                    StringBuilder(), item.resourceRegionName,
                    typeStr,
                    item.resourceName
                )
                // 地址
                if (item.resourceRegionName.isNullOrEmpty()) {
                    // 判断是否关联地址和类型
                    if( mBinding.location.isNullOrEmpty()){
                        mBinding.locationName.visibility = View.GONE
                    }else{
                        mBinding.locationName.visibility = View.VISIBLE
                    }
                    if (item.resourceType == ResourceType.CONTENT_TYPE_RURAL_SPOTS) {
                        mBinding.location = DividerTextUtils.convertDotString(
                            StringBuilder(), "乡村景观点",
                            typeStr,
                            item.resourceName
                        )
                        mBinding.locationName.visibility = View.VISIBLE
                    }
                } else {
                    mBinding.locationName.visibility = View.VISIBLE
                }

//                    mBinding.location = item.resourceRegionName + "·" + item.resourceName


                //  封面图
                if (!item.video.isNullOrEmpty()) {
                    // 当有视频时
                    mBinding.ivVideo.visibility = View.VISIBLE
                    mBinding.aiImage.setVideoImage(item.video)

                } else {
                    mBinding.ivVideo.visibility = View.GONE
                    // 当没有视频只有图片时
                    mBinding.aiImage.setImages(item.images)

                }

                // 判断是否需要添加表填
                var ssb = SpannableStringBuilder()

                if (!item.tagName.isNullOrEmpty()) {
                    ssb.append("#" + item.tagName + "#")
                    ssb.setSpan(
                        ForegroundColorSpan(mContext.resources.getColor(R.color.colorPrimary)),
                        0,
                        ssb.length,
                        Spanned
                            .SPAN_INCLUSIVE_EXCLUSIVE
                    )
                }
                ssb.append(item.title?:"")
                mBinding.tvContent.text = ssb

                if (!ssb.isNullOrEmpty()) {
                    mBinding.tvContent.visibility = View.VISIBLE
                } else {
                    mBinding.tvContent.visibility = View.GONE
                }

                RxView.clicks(mBinding.root)
                    .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                        run {

                            if (module.isNullOrEmpty()) {
                                ARouter.getInstance()
                                    .build(MainARouterPath.MAIN_STORY_DETAIL)
                                    .withString("id", item.id)
                                    .withInt("type", storyType)
                                    .navigation()
                            } else {
                                when (module) {
                                    "intangible_heritage" -> {
                                        ARouter.getInstance()
                                            .build(ARouterPath.LegacyModule.LEGACY_WORKS_DETAIL)
                                            .withInt("type", 0)
                                            .withString("id", item.id.toString())
                                            .navigation()
                                    }
                                    else -> {

                                    }
                                }
                            }

//                         if(AppUtils.isLogin()){
//                            ARouter.getInstance()
//                                .build(MainARouterPath.MAIN_STORY_DETAIL)
//                                .withString("id", item.id)
//                                .withInt("type", storyType)
//                                .navigation()
//                         }else{
//                             ToastUtils.showMessage("非常抱歉，登录后才能访问~")
//                             ARouter.getInstance()
//                                 .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
//                                 .navigation()
//                         }
                        }
                    }
                if (isShowDelete) {
                    mBinding.ivDelete.visibility = View.VISIBLE
                    mBinding.ivDelete.onNoDoubleClick {
                        if (onClickItemListener != null) {
                            onClickItemListener?.let { it(item, position) }
                        }
                    }
                }
            }

            is ItemStoryListStrategyBinding -> {
                if (item.sourceUrl.isNullOrEmpty()) {
                    mBinding.reprint.visibility = View.GONE
                } else {
                    mBinding.reprint.visibility = View.VISIBLE
                }
                Glide.with(mContext).load(item.vipHead)
                    .placeholder(R.mipmap.mine_profile_photo_default).into(mBinding.ivUser)
                mBinding.name = item.vipNickName

                mBinding.likeNumber = item.likeNum.toString()
                mBinding.commentNumber = item.commentNum
                if (storyType == 2) {
                    mBinding.tvTime.visibility = View.VISIBLE
                    mBinding.tvTime.text = item.createDate
                    mBinding.ivUser.visibility =View.GONE
                    mBinding.tvUser.visibility =View.GONE
                    mBinding.reprint.visibility =View.GONE
                }
                // 图片数量
                if (!item.images.isNullOrEmpty()) {
                    mBinding.tvImageNumber.text =
                        mContext.getString(
                            R.string.home_story_image_number,
                            item.images.size.toString()
                        )
                    mBinding.tvImageNumber.visibility = View.VISIBLE
                } else {
                    mBinding.tvImageNumber.visibility = View.GONE
                }
                // 地址
                if (item.resourceRegionName.isNullOrEmpty()) {
                    // 判断是否关联地址和类型
                    mBinding.locationName.visibility = View.GONE
                } else {
                    mBinding.locationName.visibility = View.VISIBLE
                }
                mBinding.location = DividerTextUtils.convertDotString(
                    StringBuilder(), item.resourceRegionName,
                    item.resourceName
                )

                mBinding.tvCityType.text = mContext.getString(R.string.home_story_type_strategy)
                mBinding.aiImage.setCover(item.cover)

                if (!item.video.isNullOrEmpty()) {
                    // 当有视频时
                    mBinding.ivVideo.visibility = View.VISIBLE
                } else {
                    mBinding.ivVideo.visibility = View.GONE
                }
                var ssb = SpannableStringBuilder()
                if (!item.tagName.isNullOrEmpty()) {
                    ssb.append("#" + item.tagName + "#")
                    ssb.setSpan(
                        ForegroundColorSpan(mContext.resources.getColor(R.color.colorPrimary)),
                        0,
                        ssb.length,
                        Spanned
                            .SPAN_INCLUSIVE_EXCLUSIVE
                    )
                }
                ssb.append(item.title)
                mBinding.tvContent.text = ssb

                if (!ssb.isNullOrEmpty()) {
                    mBinding.tvContent.visibility = View.VISIBLE
                } else {
                    mBinding.tvContent.visibility = View.GONE
                }
                if (!item.strategyDetail.isNullOrEmpty()) {
                    val strategyDetail = item.strategyDetail[0]
                    when (strategyDetail.contentType) {
                        Constant.STORY_STRATEGY_IMAGE_TYPE -> {
                            val images = strategyDetail.content.split(",")
                            if (!item.images.isNullOrEmpty()) {
                                mBinding.tvImageNumber.text =
                                    mContext.getString(
                                        R.string.home_story_image_number,
                                        images.size.toString()
                                    )
                                mBinding.tvImageNumber.visibility = View.VISIBLE
                            } else {
                                mBinding.tvImageNumber.visibility = View.INVISIBLE
                            }


                        }
                    }
                }

                RxView.clicks(mBinding.aiImage)
                    .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                        run {
//                            if (AppUtils.isLogin()) {
                            ARouter.getInstance()
                                .build(MainARouterPath.MAIN_STRATEGY_DETAIL)
                                .withString("id", item.id)
                                .withInt("type", storyType)
                                .navigation()
//                            } else {
//                                ToastUtils.showMessage("非常抱歉，登录后才能访问~")
//                                ARouter.getInstance()
//                                    .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
//                                    .navigation()
//                            }
                        }
                    }
                if (isShowDelete) {
                    mBinding.ivDelete.visibility = View.VISIBLE
                    mBinding.ivDelete.onNoDoubleClick {
                        if (onClickItemListener != null) {
                            onClickItemListener?.let { it(item, position) }
                        }
                    }
                }
            }
            is ItemFindStrategyBinding -> {
                if (item.sourceUrl.isNullOrEmpty()) {
                    mBinding.reprint.visibility = View.GONE
                } else {
                    mBinding.reprint.visibility = View.VISIBLE
                }
                Glide.with(mContext).load(item.vipHead)
                    .placeholder(R.mipmap.mine_profile_photo_default).into(mBinding.ivUser)
                mBinding.name = item.vipNickName

                if (storyType == 2) {
                    mBinding.ivUser.visibility =View.GONE
                    mBinding.tvUser.visibility =View.GONE
                    mBinding.reprint.visibility =View.GONE
                }
                mBinding.tvLable.text="游记攻略";
                mBinding.tvLable.visibility = View.VISIBLE
                mBinding.likeNumber = item.showNum.toString()
                val drawable = mContext.resources.getDrawable(R.mipmap.time_home_browse)
                drawable.setBounds(0, 0, drawable.minimumWidth,drawable.minimumHeight);
                mBinding.tvLike.setCompoundDrawables( drawable,null, null, null)
                mBinding.commentNumber = item.commentNum
                mBinding.cover = item.cover

                // 地址
                if (item.resourceRegionName.isNullOrEmpty()) {
                    // 判断是否关联地址和类型
                    mBinding.locationName.visibility = View.GONE
                } else {
                    mBinding.locationName.visibility = View.VISIBLE
                }
                mBinding.location = DividerTextUtils.convertDotString(
                    StringBuilder(), item
                        .regionNum + "个目的地"
                    , item.playPointNum + "个游玩点", item.hotelNum + "家酒店", item.foodNum + "家餐饮"
                )

                mBinding.tvCityType.text = mContext.getString(R.string.home_story_type_strategy)

                var ssb = SpannableStringBuilder()
                if (!item.tagName.isNullOrEmpty()) {
                    ssb.append("#" + item.tagName + "#")
                    ssb.setSpan(
                        ForegroundColorSpan(mContext.resources.getColor(R.color.colorPrimary)),
                        0,
                        ssb.length,
                        Spanned
                            .SPAN_INCLUSIVE_EXCLUSIVE
                    )
                }
                ssb.append(item.title)
                mBinding.tvContent.text = ssb

                if (ssb.isNullOrEmpty()) {
                    mBinding.tvContent.visibility = View.GONE
                } else {
                    mBinding.tvContent.visibility = View.VISIBLE
                }

                if(position==getData().size-1){
                    mBinding.lin1.visibility=View.GONE
                }else{
                    mBinding.lin1.visibility=View.VISIBLE
                }
                RxView.clicks(mBinding.root)
                    .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                        run {
                            if (AppUtils.isLogin()) {
                                ARouter.getInstance()
                                    .build(MainARouterPath.MAIN_STRATEGY_DETAIL)
                                    .withString("id", item.id)
                                    .withInt("type", storyType)
                                    .navigation()
                            } else {
                                if(mContext is FindStrategyListActivity){//如果是找共虐   不登录可以看
                                    ARouter.getInstance()
                                        .build(MainARouterPath.MAIN_STRATEGY_DETAIL)
                                        .withString("id", item.id)
                                        .withInt("type", storyType)
                                        .navigation()
                                }else{
                                    ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                                    ARouter.getInstance()
                                        .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                                        .navigation()
                                }

                            }

                        }
                    }
            }
        }
    }

    /**
     * 设置点击Item回调
     * @author caihj
     * @date 2019/6/10
     */
    private var onClickItemListener: ((item: HomeStoryBean, position: Int) -> Unit)? = null

    /**
     * 设置点击Item回调
     * @author caihj
     * @date 2019/6/10
     */
    fun setClickItemListener(onClickItemListener: (item: HomeStoryBean, position: Int) -> Unit) {
        this.onClickItemListener = onClickItemListener
    }

}