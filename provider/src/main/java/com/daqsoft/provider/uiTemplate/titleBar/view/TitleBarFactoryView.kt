package com.daqsoft.provider.uiTemplate.titleBar.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.databinding.ItemTitleBarContainTemplateBinding
import com.daqsoft.provider.uiTemplate.titleBar.TitleBarStyle
import kotlinx.android.synthetic.main.item_title_bar_contain_template.view.*
import org.jetbrains.anko.backgroundColor

/**
 * @Description 标题栏顶部view
 * @ClassName   TtitleBarStyleOneView
 * @Author      luoyi
 * @Time        2020/10/14 11:54
 */
class TitleBarFactoryView : FrameLayout {

    var mContext: Context? = null

    lateinit var binding: ItemTitleBarContainTemplateBinding
    var onTitileBarClickListener: OnTitleBarClickListener? = null
    var isShowMore: Boolean = true

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.mContext = context
        initView()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.item_title_bar_contain_template,
            this,
            false
        )
        addView(binding!!.root)
    }

    private fun initViewEvent(commonTemlate: CommonTemlate) {
        var visibly = if (isShowMore) {
            View.VISIBLE
        } else {
            View.GONE
        }
        binding?.vTitleBarTemplateOne?.ctvTitleBarStyleOneMore?.visibility = visibly
        binding?.vTitleBarTemplateTwo?.ctvTitleBarStyleTwoMore?.visibility = visibly
        binding?.vTitleBarTemplateOne?.ctvTitleBarStyleOneMore?.onNoDoubleClick {
            onTitileBarClickListener?.toMoreInfo(commonTemlate)
        }
        binding?.vTitleBarTemplateTwo?.ctvTitleBarStyleTwoMore?.onNoDoubleClick {
            onTitileBarClickListener?.toMoreInfo(commonTemlate)
        }
    }

    fun setTitleSize(spSize: Float) {
        binding?.vTitleBarTemplateOne?.tvTitleBarStyleOne.textSize = spSize
        binding?.vTitleBarTemplateTwo?.tvTitleBarStyleTwo.textSize = spSize
    }

    fun setBackGround(backRes: Int) {
        mContext?.let {
            binding?.vTitleBarTemplateOne?.llTitleBarStyleOne?.backgroundColor = it.resources.getColor(backRes)
            binding?.vTitleBarTemplateTwo?.llTitleBarStyleTwo?.backgroundColor = it.resources.getColor(backRes)
//            binding?.vTitleBarTemplateTwo?.llTitleBarStyleThree?.backgroundColor = it.resources.getColor(backRes)
        }

    }

    /**
     * @param commonTemlate CommonTemlate 样式模板实体
     */
    public fun setData(commonTemlate: CommonTemlate) {
        binding.template = commonTemlate
        var titleBarStyle = TitleBarStyle.getTemplateStyle(commonTemlate)
        when (titleBarStyle) {
            TitleBarStyle.STYLE_ONE -> {
                v_title_bar_template_one.visibility = View.VISIBLE
                v_title_bar_template_two.visibility = View.GONE
                v_title_bar_template_three.visibility = View.GONE
            }
            TitleBarStyle.STYLE_TWO -> {
                v_title_bar_template_one.visibility = View.GONE
                v_title_bar_template_two.visibility = View.VISIBLE
                v_title_bar_template_three.visibility = View.GONE
            }
            TitleBarStyle.STYLE_THREE -> {
                v_title_bar_template_one.visibility = View.GONE
                v_title_bar_template_two.visibility = View.GONE
                v_title_bar_template_three.visibility = View.VISIBLE
            }
        }
        binding.llContain.visibility = View.VISIBLE
        initViewEvent(commonTemlate)
    }

    /**
     * 标题栏点击事件
     */
    interface OnTitleBarClickListener {
        fun toMoreInfo(commonTemlate: CommonTemlate)
    }

}