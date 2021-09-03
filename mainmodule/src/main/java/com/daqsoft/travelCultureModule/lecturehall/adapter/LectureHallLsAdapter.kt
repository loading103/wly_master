package com.daqsoft.travelCultureModule.lecturehall.adapter

import android.content.Context
import android.text.SpannableStringBuilder
import android.view.View
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemLectureHallLsBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.LectureHall
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.utils.DividerTextUtils
import org.jetbrains.anko.textColor
import java.lang.StringBuilder

/**
 * @Description 课程列表
 * @ClassName   LectureTypeAdapter
 * @Author      luoyi
 * @Time        2020/6/15 14:32
 */
class LectureHallLsAdapter : RecyclerViewAdapter<ItemLectureHallLsBinding, LectureHall> {

    var mContext: Context? = null

    constructor(context: Context) : super(R.layout.item_lecture_hall_ls) {
        mContext = context
    }

    override fun setVariable(mBinding: ItemLectureHallLsBinding, position: Int, item: LectureHall) {
        item?.let {
            Glide.with(mContext!!)
                .load(it.image.getRealImages())
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .into(mBinding.imgLectureHall)
            mBinding.tvLectureHall.text = "${item.name}"
            var timeStr = DividerTextUtils.convertDotString(
                StringBuilder(),
                if (item.chapterNum > 0) {
                    "共${item.chapterNum}节课"
                } else {
                    ""
                }, if (item.totalDuration > 0) {
                    "${DateUtil.getFormatedTime(item.totalDuration * 1000L)}"
                } else {
                    ""
                }
            )
            if (!timeStr.isNullOrEmpty()) {
                mBinding.tvLectureHallTimes.visibility = View.VISIBLE
                mBinding.tvLectureHallTimes.text = timeStr
            } else {
                mBinding.tvLectureHallTimes.visibility = View.GONE
            }

            if (item.objectOriented.isNullOrEmpty()) {
                mBinding.llvLectureHallTags.visibility = View.GONE
            } else {
                var tags = item.objectOriented.split("、")
                if (tags.isNullOrEmpty()) {
                    mBinding.llvLectureHallTags.visibility = View.GONE
                } else {
                    mBinding.llvLectureHallTags.setLabels(tags)
                    mBinding.llvLectureHallTags.visibility = View.VISIBLE
                }
            }
            mBinding.tvLectureHallStudyNum.text = "${item.userNum}人已学习"

            if (item.studyStatus == 0) {
                mBinding.tvLectureHallStudyStatus.text = "未学习"
                mBinding.tvLectureHallStudyStatus.textColor = mContext!!.resources.getColor(R.color.colorPrimary)
                mBinding.tvLectureHallStudyStatus.background = mContext!!.resources.getDrawable(R.drawable.shape_ce2fce9_r3)
            } else {
                if (item.studyStatus == 1) {
                    mBinding.tvLectureHallStudyStatus.text = "已学习${DateUtil.getFormatedTime(item.duration * 1000L)}"
                    mBinding.tvLectureHallStudyStatus.textColor = mContext!!.resources.getColor(R.color.color_ff9e05)
                    mBinding.tvLectureHallStudyStatus.background = mContext!!.resources.getDrawable(R.drawable.shape_cfff5e6_r3)
                } else {
                    mBinding.tvLectureHallStudyStatus.text = "已学完"
                    mBinding.tvLectureHallStudyStatus.textColor = mContext!!.resources.getColor(R.color.color_999)
                    mBinding.tvLectureHallStudyStatus.background = mContext!!.resources.getDrawable(R.drawable.shape_cf5_r3)
                }
            }
            mBinding.root.onNoDoubleClick {
                MainARouterPath.toLectureHallDetail(item.id.toString())
            }
        }

    }
}