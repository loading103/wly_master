package com.daqsoft.provider.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.daqsoft.provider.R
import com.daqsoft.provider.databinding.ItemTabViewBinding
import org.jetbrains.anko.textColor

/**
 * @Description
 * @ClassName   TabItemView
 * @Author      luoyi
 * @Time        2020/5/21 10:31
 */
class TabItemView : FrameLayout {

    private var mContext: Context? = null

    private var binding: ItemTabViewBinding? = null

    var isSelect: Boolean = false

    private var smailImageUrl: String? = ""
    private var bigImageUrl: String? = ""
    private var title: String? = ""

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        initView()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.item_tab_view,
            this,
            false
        )
        addView(binding!!.root)
    }

    fun upSelected(select: Boolean) {
        isSelect = select
        binding?.txtItemTab?.text = "$title"
        if (isSelect) {
            var height = mContext!!.resources.getDimension(R.dimen.dp_30)
            binding?.imgItemTab?.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, height.toInt())
            binding?.txtItemTab?.textColor = mContext!!.resources!!.getColor(R.color.color_333)
            Glide.with(mContext!!)
                .load(bigImageUrl)
                .into(binding!!.imgItemTab)
        } else {
            var height = mContext!!.resources.getDimension(R.dimen.dp_20)
            binding?.imgItemTab?.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, height.toInt())
            binding?.txtItemTab?.textColor = mContext!!.resources!!.getColor(R.color.color_999)
            Glide.with(mContext!!)
                .load(smailImageUrl)
                .into(binding!!.imgItemTab)
        }
    }

    fun setSmailImageUrl(url: String?) {
        smailImageUrl = url
    }

    fun setBigImageUrl(url: String?) {
        bigImageUrl = url
    }

    fun setText(text: String) {
        this.title = text
    }
}