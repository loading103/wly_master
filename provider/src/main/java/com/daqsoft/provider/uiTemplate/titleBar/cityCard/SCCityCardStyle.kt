package com.daqsoft.provider.uiTemplate.titleBar.cityCard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.databinding.ScCityCardStyleBinding
import com.daqsoft.provider.uiTemplate.TemplateType
import com.daqsoft.provider.uiTemplate.titleBar.story.SCStoryStyleFactory
import com.daqsoft.provider.uiTemplate.titleBar.view.TitleBarFactoryView

/**
 * @package name：com.daqsoft.provider.uiTemplate.titleBar.cityCard
 * @date 12/10/2020 上午 10:07
 * @author zp
 * @describe 城市名片样式
 */
class SCCityCardStyle(
    private val layoutHelper: LayoutHelper,
    val commonTemlate:CommonTemlate
) : DelegateAdapter.Adapter<SCCityCardStyle.RecyclerViewItemHolder>() {

    inner class RecyclerViewItemHolder(val binding: ScCityCardStyleBinding) :
        RecyclerView.ViewHolder(binding.root)

    val cityCardStyleView by lazy {
        SCCityCardStyleFactory.getStyleView(commonTemlate.moduleCode ?: "1").create(BaseApplication.context)
    }


    /**
     * 标题栏
     */
    val titleBarView by lazy {
        TitleBarFactoryView(BaseApplication.context).apply {
            setData(commonTemlate)
            onTitileBarClickListener =
                object : TitleBarFactoryView.OnTitleBarClickListener {
                    override fun toMoreInfo(commonTemlate: CommonTemlate) {
                        ARouter.getInstance().build(MainARouterPath.MAIN_MDD_LIST).navigation()
                    }
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewItemHolder {
        val mBinding = DataBindingUtil.inflate<ScCityCardStyleBinding>(
            LayoutInflater.from(parent.context),
            R.layout.sc_city_card_style,
            parent,
            false
        )
        return RecyclerViewItemHolder(mBinding)
    }

    override fun onBindViewHolder(holder: RecyclerViewItemHolder, position: Int) {
        holder.binding.apply {
            // 标题栏
            this.titleBar.removeAllViews()
            this.titleBar.addView(titleBarView)
            // 内容
            this.content.removeAllViews()
            this.content.addView(cityCardStyleView)
        }

    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onCreateLayoutHelper(): LayoutHelper {
        return layoutHelper
    }

    override fun getItemViewType(position: Int): Int {
        return TemplateType.CITY_CARD + (commonTemlate.moduleCode?:"1").toInt()
    }
}


