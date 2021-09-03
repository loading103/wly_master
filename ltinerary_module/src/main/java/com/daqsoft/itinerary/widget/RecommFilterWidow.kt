package com.daqsoft.itinerary.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.amap.api.col.sln3.it
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.ResourceUtils
import com.daqsoft.itinerary.R
import com.daqsoft.itinerary.bean.RecommFilterLabelBean
import com.daqsoft.itinerary.databinding.ItineraryItemFilterLabelBinding
import kotlinx.android.synthetic.main.itinerary_window_recomm_filter.view.*
import java.lang.ref.SoftReference

/**
 * @Author：      邓益千
 * @Create by：   2020/5/11 10:54
 * @Description： 推荐筛选弹窗
 */
class RecommFilterWidow : PopupWindow{

    private lateinit var view: View

    private var labelBean: RecommFilterLabelBean? = null

    private var listener: OnFilterListener? = null
    private var mContext: SoftReference<Context>

    constructor(context: Context) : super(context) {
        mContext = SoftReference<Context>(context)
        initWindow()
    }

    fun setOnFilterListener(listener: OnFilterListener?){
        this.listener = listener
    }

    private fun initWindow(){
        view = LayoutInflater.from(mContext.get()).inflate(R.layout.itinerary_window_recomm_filter,null)
        view.reset_view.setOnClickListener {
            dismiss()
            listener?.onDone(0)
        }
        view.sure_view.setOnClickListener {
            dismiss()
            listener?.onDone(1)
        }

        contentView = view
        width = -1
        height = -2
        isFocusable = true
        isOutsideTouchable = true
        setBackgroundDrawable(ColorDrawable(mContext.get()!!.resources.getColor(R.color.white)))
    }


    fun setLabelData(labelBean: RecommFilterLabelBean){
        val paddingLeft = view.resources.getDimensionPixelSize(R.dimen.dp_20)
        val paddingTop = view.resources.getDimensionPixelSize(R.dimen.dp_8)
        val params = LinearLayout.LayoutParams(-2,-2)
        params.marginStart = paddingLeft
        params.topMargin = paddingTop

        /**循环添加 天*/
        labelBean.playDay.forEach {
            val tagView = CheckBox(ContextThemeWrapper(view.context, R.style.LabelStyleB)).apply {
                text = it.name
                buttonDrawable = null
                setPadding(paddingLeft,paddingTop,paddingLeft,paddingTop)
                setBackgroundResource(R.drawable.itinerary_shape_filter_label_bg)
                setOnCheckedChangeListener(dayListener)
                layoutParams = params
                tag = it
            }
            view.day_layout.addView(tagView)
        }


        /**循环添加 人群*/
        labelBean.fit.forEach {
            val tagView = CheckBox(ContextThemeWrapper(view.context, R.style.LabelStyleB)).apply {
                text = it.name
                buttonDrawable = null
                setPadding(paddingLeft,paddingTop,paddingLeft,paddingTop)
                setBackgroundResource(R.drawable.itinerary_shape_filter_label_bg)
                setOnCheckedChangeListener(crowdListener)
                layoutParams = params
                tag = it
            }
            view.crowd_view_list.addView(tagView)
        }

        /**循环添加 游玩类型*/
        labelBean.personality.forEach {
            val tagView = CheckBox(ContextThemeWrapper(view.context, R.style.LabelStyleB)).apply {
                text = it.name
                buttonDrawable = null
                setPadding(paddingLeft,paddingTop,paddingLeft,paddingTop)
                setBackgroundResource(R.drawable.itinerary_shape_filter_label_bg)
                setOnCheckedChangeListener(playListener)
                layoutParams = params
                tag = it
            }
            view.play_view_list.addView(tagView)
        }

    }

    /**天 点击事件*/
    private val dayListener = CompoundButton.OnCheckedChangeListener { view,isChecked ->
        val itemBean = view.tag as RecommFilterLabelBean
        itemBean.isSelected = isChecked
        val checkBox = view as CheckBox
        var color = 0
        color = if (isChecked){
            view.context.resources.getColor(R.color.color_FF9E05)
        } else {
            view.context.resources.getColor(R.color.color_666666)
        }
        checkBox.setTextColor(color)
        listener?.onClickDay(itemBean)
    }

    /**人群 点击事件*/
    private val crowdListener = CompoundButton.OnCheckedChangeListener { view,isChecked ->
        val itemBean = view.tag as RecommFilterLabelBean
        itemBean.isSelected = isChecked
        val checkBox = view as CheckBox
        var color = 0
        color = if (isChecked){
            view.context.resources.getColor(R.color.color_FF9E05)
        } else {
            view.context.resources.getColor(R.color.color_666666)
        }
        checkBox.setTextColor(color)
        listener?.onClickCrowd(itemBean)
    }

    /**游玩类型 点击事件*/
    private val playListener = CompoundButton.OnCheckedChangeListener { view,isChecked ->
        val itemBean = view.tag as RecommFilterLabelBean
        itemBean.isSelected = isChecked
        val checkBox = view as CheckBox
        var color = 0
        color = if (isChecked){
            view.context.resources.getColor(R.color.color_FF9E05)
        } else {
            view.context.resources.getColor(R.color.color_666666)
        }
        checkBox.setTextColor(color)
        listener?.onClickTag(itemBean)
    }

    fun resetState(){
        labelBean?.let {
            it.playDay.forEach { day->
                day.isSelected = false
            }

            it.fit.forEach { crowd->
                crowd.isSelected = false
            }
            it.personality.forEach { play->
                play.isSelected = false
            }
        }

        for(index in 0 until view.day_layout.childCount){
            val childView = view.day_layout.getChildAt(index) as CheckBox
            childView.isChecked = false
        }

        for(index in 0 until view.crowd_view_list.childCount){
            val childView = view.crowd_view_list.getChildAt(index) as CheckBox
            childView.isChecked = false
        }

        for(index in 0 until view.play_view_list.childCount){
            val childView = view.play_view_list.getChildAt(index) as CheckBox
            childView.isChecked = false
        }
    }

    interface OnFilterListener{
        fun onClickDay(item: RecommFilterLabelBean)
        fun onClickCrowd(item: RecommFilterLabelBean)
        fun onClickTag(item: RecommFilterLabelBean)
        fun onDone(tag: Int)
    }
}