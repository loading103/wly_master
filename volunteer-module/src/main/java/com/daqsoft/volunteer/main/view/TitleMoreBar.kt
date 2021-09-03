package com.daqsoft.volunteer.main.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.databinding.MoreTitleBarBinding

/**
 *@package:com.daqsoft.volunteer.main.view
 *@date:2020/6/2 9:26
 *@author: caihj
 *@des:TODO
 **/
class TitleMoreBar:LinearLayout {

    private var mContext: Context? = null
    private var content:String? = ""
    private var binding:MoreTitleBarBinding? = null
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        val typeAttr = context!!.obtainStyledAttributes(attrs!!,R.styleable.TitleMoreBar)
        content = typeAttr.getString(R.styleable.TitleMoreBar_volunteer_title)
         typeAttr.recycle()
        initView()
    }

    private fun initView(){
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),R.layout.more_title_bar,this,false)
        binding!!.tvTitle.text = content
        addView(binding!!.root)
    }
}