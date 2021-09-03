package com.daqsoft.travelCultureModule.story.story

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
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
import com.daqsoft.baselib.adapter.MultipleRecyclerViewAdapter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.provider.bean.StoreBean
import com.jakewharton.rxbinding2.view.RxView
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit

/**
 * 故事适配器
 */
class StoryLsAdapter(context: Context, storyType: Int = 1, isFindStragy: Boolean = true) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val mContext = context
    var isShowDelete = false
    var storyType = storyType
    var isFindStragy: Boolean = isFindStragy
    var datas: MutableList<HomeStoryBean> = mutableListOf()

    companion object {
        const val TYPE_STORE = 1001
        const val TYPE_STRAGY = 1002
        const val TYPE_FIND_STRAGY = 1003
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        setVariable(holder, position, datas[0])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_STRAGY) {
            return StoryViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_story_main, parent, false
                )
            )
        } else if (viewType == TYPE_FIND_STRAGY) {
            return FindStratgyViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_find_strategy, parent, false
                )
            )
        } else {
            return StratgyViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_story_list_strategy, parent, false
                )
            )
        }
    }

    fun setVariable(holder: RecyclerView.ViewHolder, position: Int, item: HomeStoryBean) {
        when (holder) {
            is StoryViewHolder -> {
                // 是故事

                Glide.with(mContext).load(item.vipHead)
                    .placeholder(R.mipmap.mine_profile_photo_default).into(holder.mBinding.ivUser)
                holder.mBinding.name = item.vipNickName
                holder.mBinding.likeNumber = item.likeNum.toString()
                holder.mBinding.commentNumber = item.commentNum.toString()
                if (storyType == 2) {
                    holder.mBinding.tvTime.visibility = View.VISIBLE
                    holder.mBinding.tvTime.text = item.createDate
                }

                // 图片数量
                if (!item.images.isNullOrEmpty()) {
                    holder.mBinding.tvImageNumber.text =
                        mContext.getString(
                            R.string.home_story_image_number,
                            item.images.size.toString()
                        )
                    holder.mBinding.tvImageNumber.visibility = View.VISIBLE
                } else {
                    holder.mBinding.tvImageNumber.visibility = View.GONE
                }
                val typeStr = ResourceType.getName(item.resourceType)
                holder.mBinding.location = DividerTextUtils.convertDotString(
                    StringBuilder(), item.resourceRegionName,
                    typeStr,
                    item.resourceName
                )
                // 地址
                if (item.resourceRegionName.isNullOrEmpty()) {
                    // 判断是否关联地址和类型
                    holder.mBinding.locationName.visibility = View.GONE
                    if (item.resourceType == ResourceType.CONTENT_TYPE_RURAL_SPOTS) {
                        holder.mBinding.location = DividerTextUtils.convertDotString(
                            StringBuilder(), "乡村景观点",
                            typeStr,
                            item.resourceName
                        )
                        holder.mBinding.locationName.visibility = View.VISIBLE
                    }
                } else {
                    holder.mBinding.locationName.visibility = View.VISIBLE
                }

//                    holder.mBinding.location = item.resourceRegionName + "·" + item.resourceName


                //  封面图
                if (!item.video.isNullOrEmpty()) {
                    // 当有视频时
                    holder.mBinding.ivVideo.visibility = View.VISIBLE
                    holder.mBinding.aiImage.setVideoImage(item.video)

                } else {
                    holder.mBinding.ivVideo.visibility = View.GONE
                    // 当没有视频只有图片时
                    holder.mBinding.aiImage.setImages(item.images)

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
                ssb.append(item.content)
                holder.mBinding.tvContent.text = ssb

                if (!ssb.isNullOrEmpty()) {
                    holder.mBinding.tvContent.visibility = View.VISIBLE
                } else {
                    holder.mBinding.tvContent.visibility = View.GONE
                }

                RxView.clicks(holder.mBinding.root)
                    .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                        run {

                            //                         if(AppUtils.isLogin()){
                            ARouter.getInstance()
                                .build(MainARouterPath.MAIN_STORY_DETAIL)
                                .withString("id", item.id)
                                .withInt("type", storyType)
                                .navigation()
//                         }else{
//                             ToastUtils.showMessage("非常抱歉，登录后才能访问~")
//                             ARouter.getInstance()
//                                 .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
//                                 .navigation()
//                         }
                        }
                    }
                if (isShowDelete) {
                    holder.mBinding.ivDelete.visibility = View.VISIBLE
                    holder.mBinding.ivDelete.onNoDoubleClick {
                        if (onClickItemListener != null) {
                            onClickItemListener?.let { it(item, position) }
                        }
                    }
                }
            }

            is StratgyViewHolder -> {
                Glide.with(mContext).load(item.vipHead)
                    .placeholder(R.mipmap.mine_profile_photo_default).into(holder.mBinding.ivUser)
                holder.mBinding.name = item.vipNickName
                holder.mBinding.likeNumber = item.likeNum.toString()
                holder.mBinding.commentNumber = item.commentNum
                if (storyType == 2) {
                    holder.mBinding.tvTime.visibility = View.VISIBLE
                    holder.mBinding.tvTime.text = item.createDate
                }
                // 图片数量
                if (!item.images.isNullOrEmpty()) {
                    holder.mBinding.tvImageNumber.text =
                        mContext.getString(
                            R.string.home_story_image_number,
                            item.images.size.toString()
                        )
                    holder.mBinding.tvImageNumber.visibility = View.VISIBLE
                } else {
                    holder.mBinding.tvImageNumber.visibility = View.GONE
                }
                // 地址
                if (item.resourceRegionName.isNullOrEmpty()) {
                    // 判断是否关联地址和类型
                    holder.mBinding.locationName.visibility = View.GONE
                } else {
                    holder.mBinding.locationName.visibility = View.VISIBLE
                }
                holder.mBinding.location = DividerTextUtils.convertDotString(
                    StringBuilder(), item.resourceRegionName,
                    item.resourceName
                )

                holder.mBinding.tvCityType.text = mContext.getString(R.string.home_story_type_strategy)
                holder.mBinding.aiImage.setCover(item.cover)

                if (!item.video.isNullOrEmpty()) {
                    // 当有视频时
                    holder.mBinding.ivVideo.visibility = View.VISIBLE
                } else {
                    holder.mBinding.ivVideo.visibility = View.GONE
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
                holder.mBinding.tvContent.text = ssb

                if (!ssb.isNullOrEmpty()) {
                    holder.mBinding.tvContent.visibility = View.VISIBLE
                } else {
                    holder.mBinding.tvContent.visibility = View.GONE
                }
                if (!item.strategyDetail.isNullOrEmpty()) {
                    val strategyDetail = item.strategyDetail[0]
                    when (strategyDetail.contentType) {
                        Constant.STORY_STRATEGY_IMAGE_TYPE -> {
                            val images = strategyDetail.content.split(",")
                            if (!item.images.isNullOrEmpty()) {
                                holder.mBinding.tvImageNumber.text =
                                    mContext.getString(
                                        R.string.home_story_image_number,
                                        images.size.toString()
                                    )
                                holder.mBinding.tvImageNumber.visibility = View.VISIBLE
                            } else {
                                holder.mBinding.tvImageNumber.visibility = View.INVISIBLE
                            }


                        }
                    }
                }

                RxView.clicks(holder.mBinding.aiImage)
                    .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                        run {
                            if (AppUtils.isLogin()) {
                                ARouter.getInstance()
                                    .build(MainARouterPath.MAIN_STRATEGY_DETAIL)
                                    .withString("id", item.id)
                                    .withInt("type", storyType)
                                    .navigation()
                            } else {
                                ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                                ARouter.getInstance()
                                    .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                                    .navigation()
                            }
                        }
                    }
                if (isShowDelete) {
                    holder.mBinding.ivDelete.visibility = View.VISIBLE
                    holder.mBinding.ivDelete.onNoDoubleClick {
                        if (onClickItemListener != null) {
                            onClickItemListener?.let { it(item, position) }
                        }
                    }
                }
            }
            is FindStratgyViewHolder -> {
                Glide.with(mContext).load(item.vipHead)
                    .placeholder(R.mipmap.mine_profile_photo_default).into(holder.mBinding.ivUser)
                holder.mBinding.name = item.vipNickName
                holder.mBinding.likeNumber = item.likeNum.toString()
                holder.mBinding.commentNumber = item.commentNum
                holder.mBinding.cover = item.cover

                // 地址
                if (item.resourceRegionName.isNullOrEmpty()) {
                    // 判断是否关联地址和类型
                    holder.mBinding.locationName.visibility = View.GONE
                } else {
                    holder.mBinding.locationName.visibility = View.VISIBLE
                }
                holder.mBinding.location = DividerTextUtils.convertDotString(
                    StringBuilder(), item
                        .regionNum + "个目的地"
                    , item.playPointNum + "个游玩点", item.hotelNum + "家酒店", item.foodNum + "家餐饮"
                )

                holder.mBinding.tvCityType.text = mContext.getString(R.string.home_story_type_strategy)

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
                holder.mBinding.tvContent.text = ssb

                if (ssb.isNullOrEmpty()) {
                    holder.mBinding.tvContent.visibility = View.GONE
                } else {
                    holder.mBinding.tvContent.visibility = View.VISIBLE
                }

                RxView.clicks(holder.mBinding.root)
                    .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                        run {
                            if (AppUtils.isLogin()) {
                                ARouter.getInstance()
                                    .build(MainARouterPath.MAIN_STRATEGY_DETAIL)
                                    .withString("id", item.id)
                                    .withInt("type", storyType)
                                    .navigation()
                            } else {
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

    override fun getItemViewType(position: Int): Int {
        var data = datas[position]
        if (data.storyType == Constant.STORY_TYPE_STORY) {
            return TYPE_STORE
        } else if (data.storyType == Constant.STORY_TYPE_STRATEGY) {
            return TYPE_STRAGY
        }
        return super.getItemViewType(position)
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

    override fun getItemCount(): Int {
        return datas.size
    }

    class StoryViewHolder(val mBinding: ItemStoryMainBinding) : RecyclerView.ViewHolder(mBinding.root) {


    }

    class StratgyViewHolder(val mBinding: ItemStoryListStrategyBinding) : RecyclerView.ViewHolder(mBinding.root) {


    }

    class FindStratgyViewHolder(val mBinding: ItemFindStrategyBinding) : RecyclerView.ViewHolder(mBinding.root) {

    }
}