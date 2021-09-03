package com.daqsoft.provider.uiTemplate.titleBar.information

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.lifecycle.*
import com.daqsoft.thetravelcloudwithculture.home.bean.HomeContentBean
import kotlinx.android.synthetic.main.item_home_popular_activity_sc.view.*


/**
 * @package name：com.daqsoft.provider.uiTemplate.titleBar.information
 * @date 12/10/2020 上午 10:07
 * @author zp
 * @describe 资讯样式基类
 */
abstract class SCInformationStyleBase : FrameLayout{

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

    protected abstract fun informationDataChanged(data:MutableList<HomeContentBean>)

    fun setInformationDataChanged(data:MutableList<HomeContentBean>){
        informationDataChanged(data)
    }
}