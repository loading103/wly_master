package com.daqsoft.provider.uiTemplate.titleBar.column

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.daqsoft.provider.bean.SubChanelChildBean


/**
 * @package name：com.daqsoft.provider.uiTemplate.titleBar.information
 * @date 12/10/2020 上午 10:07
 * @author zp
 * @describe 资讯样式基类
 */
abstract class SCColumnStyleBase : FrameLayout{

    constructor(context: Context) : super(context){
        initView()
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        initView()
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        initView()
    }

    protected abstract fun initView()

    protected abstract fun columnDataChanged(data:MutableList<SubChanelChildBean>)

    fun setColumnDataChanged(data:MutableList<SubChanelChildBean>){
        columnDataChanged(data)
    }
}