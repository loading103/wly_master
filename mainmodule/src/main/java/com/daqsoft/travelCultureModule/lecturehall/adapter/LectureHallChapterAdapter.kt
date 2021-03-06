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
                            // ????????????????????????????????????
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
     * ?????? TextView ??????????????? ????????????
     * @param view TextView ??????????????? TextView
     * @param originText String ?????????
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
                    //??????????????????TextView????????????????????????????????????
                    if (paint.measureText(originalTextLine) <= availableTextWidth) {
                        newTextBuilder.append(originalTextLine)
                    } else {
                        //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                        var lineWidth = 0f
                        var i = 0
                        while (i != originalTextLine.length) {
                            val charAt = originalTextLine[i]
                            lineWidth += paint.measureText(charAt.toString())
                            if (lineWidth <= availableTextWidth) {
                                newTextBuilder.append(charAt)
                            } else {
                                //????????????TextView?????????????????????
                                newTextBuilder.append("\n")
                                lineWidth = 0f
                                --i //????????????????????????????????????????????????????????????????????????????????????
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