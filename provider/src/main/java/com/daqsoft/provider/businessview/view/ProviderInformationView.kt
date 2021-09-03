package com.daqsoft.provider.businessview.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.InformationBean
import com.daqsoft.provider.businessview.adapter.ProviderInfoAdapter
import com.daqsoft.provider.databinding.LayoutProviderInfoBinding

/**
 * desc :资讯列表
 * @author 江云仙
 * @date 2020/4/23 14:59
 */
class ProviderInformationView :FrameLayout{
    var mContext: Context? = null

    var binding: LayoutProviderInfoBinding? = null
    /**
     * 活动数据集
     */
    var datas: MutableList<InformationBean> = mutableListOf()

    var adapter: ProviderInfoAdapter? = null
    //
    var channelCode=""

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        initView()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.layout_provider_info,
            this,
            false
        )

        //查看更多
        binding!!.tvProviderSeeMore.onNoDoubleClick {
            //跳转到资讯列表
            ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT)
                .withString("channelCode",channelCode)
                .navigation()
        }
        addView(binding!!.root)


    }

    /**
     * 设置数据
     */
    fun setData(data: MutableList<InformationBean>,channelCode:String) {
        this.channelCode =channelCode
        updateUi()
        datas.clear()
        adapter?.add(data)

    }


    fun updateUi() {
        adapter = ProviderInfoAdapter(mContext!!)
        binding?.recyclerProviderDetailsStory?.layoutManager = LinearLayoutManager(
            mContext,
            LinearLayoutManager.VERTICAL,false
        )
        binding?.recyclerProviderDetailsStory?.adapter = adapter
    }
}