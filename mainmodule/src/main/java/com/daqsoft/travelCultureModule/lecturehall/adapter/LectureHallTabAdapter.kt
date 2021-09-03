package com.daqsoft.travelCultureModule.lecturehall.adapter

import android.content.Context
import android.view.View
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemLectureHallTabBinding
import com.daqsoft.travelCultureModule.country.bean.ValueKeyBean
import org.jetbrains.anko.textColor

/**
 * @Description
 * @ClassName   LectureHallTabAdapter
 * @Author      luoyi
 * @Time        2020/6/16 10:07
 */
class LectureHallTabAdapter : RecyclerViewAdapter<ItemLectureHallTabBinding, ValueKeyBean> {

    var mContext: Context? = null

    var selectPos: Int = 0

    var onTabClickListener: OnTabClickListener? = null

    constructor(context: Context) : super(R.layout.item_lecture_hall_tab) {
        this.mContext = context
    }

    override fun setVariable(mBinding: ItemLectureHallTabBinding, position: Int, item: ValueKeyBean) {
        item?.let {
            mBinding.tvLectureTab.text = it.name
            if (selectPos == position) {
                mBinding.tvLectureTab.textColor = mContext!!.resources.getColor(R.color.colorPrimary)
                mBinding.tvLectureTab.paint.isFakeBoldText = true
                mBinding.vSlide.visibility = View.VISIBLE
            } else {
                mBinding.tvLectureTab.textColor = mContext!!.resources.getColor(R.color.color_666)
                mBinding.tvLectureTab.paint.isFakeBoldText = false
                mBinding.vSlide.visibility = View.GONE
            }
            mBinding.root.onNoDoubleClick {
                if(selectPos!=position) {
                    selectPos = position
                    notifyDataSetChanged()
                    onTabClickListener?.onTabClick(position)
                }
            }
        }
    }

    interface OnTabClickListener {
        fun onTabClick(position: Int)
    }
}