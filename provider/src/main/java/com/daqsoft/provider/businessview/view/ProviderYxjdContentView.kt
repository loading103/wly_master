package com.daqsoft.provider.businessview.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.base.PublishChannel
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.base.StudyChannel
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.bean.ContentBean
import com.daqsoft.provider.businessview.adapter.ProviderContentAdapter
import com.daqsoft.provider.businessview.adapter.ProviderContentNewAdapter
import com.daqsoft.provider.databinding.LayoutProviderContentBinding

/**
 * @Description 资讯view
 * @Time       研学基地
 */
class ProviderYxjdContentView : FrameLayout {

    var mContext: Context? = null

    var binding: LayoutProviderContentBinding? = null

    var title: String = "资讯"

    var channel: String = StudyChannel.STUDY_THEME

    var mDatas: MutableList<ContentBean> = mutableListOf()

    var adapter: ProviderContentNewAdapter? = null

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        initView()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.layout_provider_content,
            this,
            false
        )
        addView(binding!!.root)
        binding?.title = title
    }

    fun setData(resId: String, resTpye: String, datas: MutableList<ContentBean>) {
        if (!datas.isNullOrEmpty()) {
            adapter = ProviderContentNewAdapter(mContext!!)
            adapter?.emptyViewShow = false
            binding?.rvProvideContents?.adapter = adapter
            binding?.rvProvideContents?.layoutManager = LinearLayoutManager(
                mContext!!,
                LinearLayoutManager.VERTICAL, false
            )
            adapter?.clear()
            adapter?.add(datas)
            if (datas.size >= 2) {
                binding?.tvProviderMore?.visibility = View.VISIBLE
            } else {
                binding?.tvProviderMore?.visibility = View.GONE
            }
        }
        binding?.tvProviderMore?.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_CONTENT_STUDY)
                .withString("channelCode",channel)
                .withString("linksResourceType", resTpye)
                .withString("linksResourceId", resId)
                .withString("linksResourceId", resId)
                .navigation()
        }
    }

    fun setTitles(title:String,channel:String){
        binding?.title = title
        this.channel=channel
    }

    fun setvisibility(isShow:Boolean){
        if(isShow){
            visibility =View.VISIBLE
        }else{
            visibility = View.GONE
        }
    }
}