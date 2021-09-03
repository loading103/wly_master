package com.daqsoft.travelCultureModule.lecturehall.adapter

import android.content.Context
import android.os.Build
import android.text.Html
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemLectureHallChapterBinding
import com.daqsoft.provider.bean.LectureHallChapter
import com.daqsoft.provider.bean.LectureHallChapterBean
import org.greenrobot.eventbus.EventBus

/**
 * @Description
 * @ClassName   LectureHallChapterAdapter
 * @Author      luoyi
 * @Time        2020/6/17 9:28
 */
class LectureHallChapterAdapter : RecyclerViewAdapter<ItemLectureHallChapterBinding, LectureHallChapterBean> {

    var mContext: Context? = null
    var onChapterItemCLickListener: OnChapterItemCLickListener? = null
    var oldParentSelectPos: Int = 0
    var oldChildSelectPos: Int = 0
    var adapterMap: HashMap<Int, LectureItemChapterAdapter> = HashMap()

    constructor(context: Context) : super(R.layout.item_lecture_hall_chapter) {
        this.mContext = context
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun setVariable(
        mBinding: ItemLectureHallChapterBinding,
        position: Int,
        item: LectureHallChapterBean
    ) {
        item?.let {
//            adaptiveText(mBinding.tvMainTarget,it.name)
            mBinding.tvMainTarget.text = "${it.name}"
            if (!it.ctcSchoolChapterVOS.isNullOrEmpty()) {
                mBinding.rvChapterLs.visibility = View.VISIBLE
                var adapter = LectureItemChapterAdapter(mContext!!, position, oldParentSelectPos)
                adapterMap.put(position, adapter)
                adapter?.emptyViewShow = false
                mBinding.rvChapterLs.layoutManager = LinearLayoutManager(
                    mContext!!,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                mBinding.rvChapterLs.adapter = adapter
                adapter?.clear()
                adapter?.add(it.ctcSchoolChapterVOS)
                adapter?.onLectureChapterListener = object : LectureItemChapterAdapter.OnLectureChapterListener {
                    override fun onChapterClick(
                        item: LectureHallChapter,
                        parentPos: Int,
                        childPosition: Int
                    ) {
                        if (oldParentSelectPos == parentPos && childPosition == oldChildSelectPos) {
                            // 重复选中，暂时什么也不做
//                            EventBus.getDefault().post()
                            onChapterItemCLickListener?.onItemClick(item)
                        } else {
                            updateOldSelectPos(oldParentSelectPos, oldChildSelectPos)
                            oldChildSelectPos = childPosition
                            oldParentSelectPos = parentPos
                            onChapterItemCLickListener?.onItemClick(item)
                        }
                    }
                }
            } else {
                mBinding.rvChapterLs.visibility = View.GONE
            }
        }
    }

    fun updateOldSelectPos(oldParentPos: Int, oldChildPos: Int) {
        var adapter = adapterMap[oldParentPos]
        if (adapter != null) {
            adapter?.notifyItemChanged(oldChildPos, "unSelectPos")
        }
    }

    interface OnChapterItemCLickListener {
        fun onItemClick(item: LectureHallChapter)
    }

    /**
     * 处理 TextView 中英文混排 自动换行
     * @param view TextView 显示文字的 TextView
     * @param originText String 原文字
     */
    fun adaptiveText(view: TextView, originText: String) {
        view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val paddingLeft = view.paddingLeft
                val paddingRight = view.paddingRight
                val paint = view.paint
                val availableTextWidth = view.width - paddingLeft - paddingRight
                val originalTextLines = originText.replace("\r", "").split("\n")
                val newTextBuilder = StringBuilder()
                for (originalTextLine in originalTextLines) {
                    //文本内容小于TextView宽度，即不换行，不作处理
                    if (paint.measureText(originalTextLine) <= availableTextWidth) {
                        newTextBuilder.append(originalTextLine)
                    } else {
                        //如果整行宽度超过控件可用宽度，则按字符测量，在超过可用宽度的前一个字符处手动换行
                        var lineWidth = 0f
                        var i = 0
                        while (i != originalTextLine.length) {
                            val charAt = originalTextLine[i]
                            lineWidth += paint.measureText(charAt.toString())
                            if (lineWidth <= availableTextWidth) {
                                newTextBuilder.append(charAt)
                            } else {
                                //单行超过TextView可用宽度，换行
                                newTextBuilder.append("\n")
                                lineWidth = 0f
                                --i //该代码作用是将本轮循环回滚，在新的一行重新循环判断该字符
                            }
                            ++i
                        }
                    }
                }
                view.text = newTextBuilder.toString()
            }
        })
    }


}