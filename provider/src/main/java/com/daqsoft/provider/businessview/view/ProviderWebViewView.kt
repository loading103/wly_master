package com.daqsoft.provider.businessview.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.bean.ContentBean
import com.daqsoft.provider.businessview.adapter.ProviderContentAdapter
import com.daqsoft.provider.businessview.adapter.ProviderContentNewAdapter
import com.daqsoft.provider.databinding.LayoutProviderContentBinding
import com.daqsoft.provider.databinding.LayoutProviderWebBinding
import com.daqsoft.provider.utils.WebViewUtils

class ProviderWebViewView : FrameLayout {

    var mContext: Context? = null

    var binding: LayoutProviderWebBinding? = null

    var title: String = "资讯"

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        initView()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_provider_web, this, false)
        binding?.webInfor?.settings?.defaultTextEncodingName = "utf-8"
        binding?.webInfor?.settings?.javaScriptEnabled = true
        binding?.webInfor?.isScrollContainer = false
        binding?.webInfor?.isVerticalScrollBarEnabled = false
        binding?.webInfor?.isHorizontalScrollBarEnabled = false
        addView(binding!!.root)
    }

    fun setData( title: String,content: String?) {
        if(content.isNullOrBlank()){
            this.visibility=View.GONE
        }else{
            this.visibility=View.VISIBLE
            binding?.title=title
//            if (!content.isNullOrEmpty() && content.contains("frame")) {
//                WebViewUtils.pptWeb(  binding?.webInfor, content,null,context as Activity)
//            }else{
                binding?.webInfor?.loadDataWithBaseURL(null, StringUtil.getHtml(content), "text/html", "utf-8", null)
//            }
        }
    }

    fun setTitles(title:String){
        binding?.title = title
    }

    fun setvisibility(isShow:Boolean){
        if(isShow){
            visibility =View.VISIBLE
        }else{
            visibility = View.GONE
        }
    }
}