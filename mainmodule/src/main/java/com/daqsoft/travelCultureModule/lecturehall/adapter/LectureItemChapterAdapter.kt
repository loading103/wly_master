package com.daqsoft.travelCultureModule.lecturehall.adapter

import android.content.Context
import android.graphics.Paint
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemLectureChapterBinding
import com.daqsoft.mainmodule.databinding.ItemLectureHallChapterBinding
import com.daqsoft.provider.bean.LectureHallChapter
import com.daqsoft.provider.bean.LectureHallChapterBean
import com.daqsoft.provider.view.RadiusBackgroundV2Span
import org.jetbrains.anko.append
import org.jetbrains.anko.textColor
import java.lang.StringBuilder

/**
 * @Description
 * @ClassName   LectureHallChapterAdapter
 * @Author      luoyi
 * @Time        2020/6/17 9:28
 */
class LectureItemChapterAdapter :
    RecyclerViewAdapter<ItemLectureChapterBinding, LectureHallChapter> {

    var mContext: Context? = null

    var onLectureChapterListener: OnLectureChapterListener? = null
    var parentPos: Int = 0
    var selectPos: Int = 0
    var oldParentPos: Int = 0

    constructor(
        context: Context,
        parentPos: Int,
        oldParentPos: Int
    ) : super(R.layout.item_lecture_chapter) {
        this.mContext = context
        this.parentPos = parentPos
        this.oldParentPos = oldParentPos
    }


    override fun setVariable(
        mBinding: ItemLectureChapterBinding,
        position: Int,
        item: LectureHallChapter
    ) {
        item?.let {
            // 父节点位置
            var pPos = parentPos + 1
            // 子节点位置
            var cPos = position + 1
            mBinding.tvChapterCatalog.text = "${pPos}.${cPos}"

            var stringBuilder = SpannableStringBuilder("" + it.name)
            mBinding.tvChapterTime.text = DateUtil.getTime(it.duration * 1000)
            var colorPrimary: Int = 0
            if (selectPos == position && oldParentPos == parentPos) {
                colorPrimary = mContext!!.resources.getColor(R.color.colorPrimary)
                mBinding.tvChapterCatalog.textColor = colorPrimary
                mBinding.tvChapterCatalog.paint.isFakeBoldText = true
                mBinding.tvChapterTitle.textColor = colorPrimary
                mBinding.tvChapterTitle.paint.isFakeBoldText = true
                mBinding.tvChapterTime.textColor = colorPrimary
                mBinding.imgPlayer.visibility = View.GONE
                mBinding.animgPlayer.visibility = View.VISIBLE
                mBinding.tvChapterStatus.setBackgroundResource(R.drawable.shape_color_primary_bwhite_r3)
            } else {
                mBinding.imgPlayer.visibility = View.VISIBLE
                mBinding.animgPlayer.visibility = View.GONE
                colorPrimary = mContext!!.resources.getColor(R.color.color_333)
                mBinding.tvChapterCatalog.textColor = colorPrimary
                mBinding.tvChapterCatalog.paint.isFakeBoldText = false
                mBinding.tvChapterTitle.textColor = colorPrimary
                mBinding.tvChapterTitle.paint.isFakeBoldText = false
                mBinding.tvChapterStatus.setBackgroundResource(R.drawable.shape_c33_bwhite_r3)
                mBinding.tvChapterTime.textColor = mContext!!.resources.getColor(R.color.color_999)
            }
            if (it.userDuration >= it.duration) {
                mBinding.tvChapterStatus.visibility = View.VISIBLE
                mBinding.tvChapterStatus.setTextColor(colorPrimary)

            } else {
                mBinding.tvChapterStatus.visibility = View.GONE
            }
            mBinding.tvChapterTitle.setText(stringBuilder)
            mBinding.root.onNoDoubleClick {
                onLectureChapterListener?.onChapterClick(item, parentPos, position)
                notifyItemChanged(position, "selectPos")
            }
        }
    }

    override fun payloadUpdateUi(
        mBinding: ItemLectureChapterBinding,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads[0] == "selectPos") {
            var colorPrimary = mContext!!.resources.getColor(R.color.colorPrimary)
            mBinding.tvChapterCatalog.textColor = colorPrimary
            mBinding.tvChapterCatalog.paint.isFakeBoldText = true
            mBinding.tvChapterTitle.textColor = colorPrimary
            mBinding.tvChapterTitle.paint.isFakeBoldText = true
            mBinding.tvChapterTime.textColor = colorPrimary
            mBinding.imgPlayer.visibility = View.GONE
            mBinding.animgPlayer.visibility = View.VISIBLE
        } else if (payloads[0] == "unSelectPos") {
            mBinding.imgPlayer.visibility = View.VISIBLE
            mBinding.animgPlayer.visibility = View.GONE
            var colorPrimary = mContext!!.resources.getColor(R.color.color_333)
            mBinding.tvChapterCatalog.textColor = colorPrimary
            mBinding.tvChapterCatalog.paint.isFakeBoldText = false
            mBinding.tvChapterTitle.textColor = colorPrimary
            mBinding.tvChapterTitle.paint.isFakeBoldText = false
            mBinding.tvChapterTime.textColor = mContext!!.resources.getColor(R.color.color_999)
        }
    }

    /**
     * 取消选中
     */
    fun unSelectPos(oldSelectPos: Int) {
        if (oldSelectPos < getData().size) {
            notifyItemChanged(oldSelectPos, "unSelectPos")
        }
    }

    interface OnLectureChapterListener {
        fun onChapterClick(item: LectureHallChapter, parentPos: Int, position: Int)
    }
}