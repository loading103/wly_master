package com.daqsoft.travelCultureModule.lecturehall.adapter

import android.content.Context
import android.view.View
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemLectureTypeBinding
import com.daqsoft.provider.bean.LectureType
import org.jetbrains.anko.textColor

/**
 * @Description
 * @ClassName   LectureHallTypeAdapter
 * @Author      luoyi
 * @Time        2020/6/15 14:31
 */
class LectureHallTypeAdapter : RecyclerViewAdapter<ItemLectureTypeBinding, LectureType> {

    var selectPos: Int = 0

    var mContext: Context? = null

    var onLectureHallItemClickListener: OnLectureHallItemClickListener? = null

    constructor(context: Context) : super(R.layout.item_lecture_type) {
        mContext = context
    }

    override fun setVariable(mBinding: ItemLectureTypeBinding, position: Int, item: LectureType) {
        item?.let {
            mBinding.tvLectureTypeTab.text = it.name
            if (selectPos == position) {
                mBinding.tvLectureTypeTab.textColor = mContext!!.resources.getColor(R.color.colorPrimary)
                mBinding.tvLectureTypeTab.paint.isFakeBoldText = true
                mBinding.vSelctThumb.visibility = View.VISIBLE
            } else {
                mBinding.tvLectureTypeTab.textColor = mContext!!.resources.getColor(R.color.color_666)
                mBinding.tvLectureTypeTab.paint.isFakeBoldText = false
                mBinding.vSelctThumb.visibility = View.GONE
            }
        }
        mBinding.root?.onNoDoubleClick {
            selectPos = position
            notifyDataSetChanged()
            onLectureHallItemClickListener?.onItemCLick(position, item)
        }
    }

    interface OnLectureHallItemClickListener {
        fun onItemCLick(position: Int, data: LectureType)
    }
}