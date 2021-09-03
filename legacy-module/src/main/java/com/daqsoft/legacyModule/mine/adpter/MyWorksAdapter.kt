package com.daqsoft.legacyModule.mine.adpter

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.bean.LegacyStoryListBean
import com.daqsoft.legacyModule.databinding.ActivityItemWorksBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.bean.HomeStoryBean
import com.daqsoft.provider.utils.DividerTextUtils
import java.lang.StringBuilder

/**
 *@package:com.daqsoft.legacyModule.mine.adpter
 *@date:2020/5/18 15:42
 *@author: caihj
 *@des:TODO
 **/
class MyWorksAdapter(context: Context) :
    RecyclerViewAdapter<ActivityItemWorksBinding, LegacyStoryListBean>(R.layout.activity_item_works) {
    private val mContext = context
    override fun setVariable(
        mBinding: ActivityItemWorksBinding,
        position: Int,
        item: LegacyStoryListBean
    ) {
        mBinding.tvLike.text = item.likeNum.toString()
        mBinding.tvComment.text = item.commentNum.toString()
        mBinding.tvShow.text = item.showNum.toString()
        mBinding.tvPeople.text = item.pkNum.toString()
        if (!item.pkId.isNullOrEmpty()) {
            var ssb = SpannableStringBuilder()
            if (!item.tagName.isNullOrEmpty()) {
                ssb.append("#" + item.tagName + "#")
                ssb.setSpan(
                    ForegroundColorSpan(mContext.resources.getColor(R.color.legacy_module_primary_color)),
                    0,
                    ssb.length,
                    Spanned
                        .SPAN_INCLUSIVE_EXCLUSIVE
                )
            }
            ssb.append(item.content)
            mBinding.tvContent.text = ssb
            if (ssb.isNullOrEmpty()) {
                mBinding.tvContent.visibility = View.GONE
            } else {
                mBinding.tvContent.visibility = View.VISIBLE
            }
            mBinding.tvPkTitle.text = "" + item.title
            mBinding.tvPkTitle.visibility = View.VISIBLE
        } else {
            mBinding.tvPkTitle.visibility = View.GONE
            var ssb = SpannableStringBuilder()
            if (!item.tagName.isNullOrEmpty()) {
                ssb.append("#" + item.tagName + "#")
                ssb.setSpan(
                    ForegroundColorSpan(mContext.resources.getColor(R.color.legacy_module_primary_color)),
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

        }
        if (item.top == 1) {
            // 置顶和非置顶区别
            mBinding.tvTop.visibility = View.VISIBLE
        } else {
            mBinding.tvTop.visibility = View.GONE
        }

        // 地址
        if (item.resourceRegionName.isNullOrEmpty()) {
            // 判断是否关联地址和类型
            mBinding.locationName.visibility = View.GONE
        } else {
            mBinding.locationName.visibility = View.VISIBLE
        }
        mBinding.locationName.text = DividerTextUtils.convertDotString(
            StringBuilder(), item.resourceRegionName,
            item.resourceName
        )
        var rightDrawable = mContext.resources.getDrawable(R.mipmap.time_home_hot_position)
        rightDrawable.setBounds(
            0,
            0,
            rightDrawable.minimumWidth,
            rightDrawable.minimumHeight
        ) // left, top, right, bottom
        mBinding.locationName.setCompoundDrawables(rightDrawable, null, null, null);

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

        //  封面图
        if (!item.video.isNullOrEmpty()) {
            // 当有视频时
            mBinding.ivVideo.visibility = View.VISIBLE
            mBinding.aiImage.setVideoImage(item.video)

        } else {
            mBinding.ivVideo.visibility = View.GONE
            // 当没有视频只有图片时
            if (!item.images.isNullOrEmpty()){
                mBinding.aiImage.setImages(item.images)
            }else{
                mBinding.aiImage.setCover(item.cover?:"")
            }


        }
        mBinding.ivMore.onNoDoubleClick {
            onClickItemListener?.let { it(item, position) }
        }
        mBinding.root.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.LegacyModule.LEGACY_WORKS_DETAIL)
                .withInt("type", 1)
                .withString("id", item.id.toString())
                .navigation()
        }

    }


    /**
     * 设置点击Item回调
     * @author caihj
     * @date 2019/6/10
     */
    private var onClickItemListener: ((item: LegacyStoryListBean, position: Int) -> Unit)? = null

    /**
     * 设置点击Item回调
     * @author caihj
     * @date 2019/6/10
     */
    fun setClickItemListener(onClickItemListener: (item: LegacyStoryListBean, position: Int) -> Unit) {
        this.onClickItemListener = onClickItemListener
    }

}