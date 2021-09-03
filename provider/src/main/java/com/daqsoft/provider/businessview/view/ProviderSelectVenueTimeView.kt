package com.daqsoft.provider.businessview.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.daqsoft.provider.R
import com.daqsoft.provider.databinding.LayoutProviderSelectVenueTimeBinding

/**
 * @Description
 * @ClassName   ProviderSelectVenueTimeView
 * @Author      luoyi
 * @Time        2020/4/30 17:25
 */
class ProviderSelectVenueTimeView : FrameLayout {

    private var mContext: Context? = null
    private var binding: LayoutProviderSelectVenueTimeBinding? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        initView()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.layout_provider_select_venue_time,
            this,
            false
        )
        addView(binding?.root)
    }

    public fun setMothDate(){

    }
}