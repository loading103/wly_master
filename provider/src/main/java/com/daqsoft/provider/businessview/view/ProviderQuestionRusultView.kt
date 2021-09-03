package com.daqsoft.provider.businessview.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.QuestionSubmitBean
import com.daqsoft.provider.businessview.adapter.QuestionResultAdapter
import com.daqsoft.provider.databinding.LayoutProviderQuestionBinding


/**
 * 单选多选问答题
 */
class ProviderQuestionRusultView : FrameLayout {

    var mContext: Context? = null

    var binding: LayoutProviderQuestionBinding? = null
    /**
     * 答题列表
     */
    var list: MutableList<QuestionSubmitBean> = mutableListOf()

    private val mAdapter by lazy { QuestionResultAdapter() }

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        initView()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_provider_question, this, false)
        binding?.recyclerProviderDetailsStory.apply {
            this?.adapter = mAdapter
            this?.addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    val position = parent.getChildAdapterPosition(view)
                    if(position==mAdapter.datas.size){
                        outRect.bottom = Utils.dip2px(context!!, 120f).toInt()
                    }

                }
            })
        }
        addView(binding!!.root)
    }

    /**
     * 设置数据
     */
    fun setNewData(data: MutableList<QuestionSubmitBean>) {
        list.clear()
        list.addAll(data)
        mAdapter.datas=list
        mAdapter.notifyDataSetChanged()
    }
    /**
     * 设置引导语
     */
    fun setGuideBody(topTitle:String?,title:String?) {
        mAdapter.guidebody=topTitle
        mAdapter.title=title
        mAdapter.notifyItemChanged(0)
    }

}