package com.daqsoft.provider.uiTemplate.titleBar.tourguide

import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.bean.TourBean
import com.daqsoft.provider.bean.TourHomeBean
import com.daqsoft.provider.uiTemplate.titleBar.tourguide.holder.TourGuideTemplateOneHolder
import com.daqsoft.provider.uiTemplate.titleBar.tourguide.holder.TourGuideTemplateTwoHolder
import com.daqsoft.provider.uiTemplate.titleBar.view.TitleBarFactoryView

/**
 * @Description
 * @ClassName   TourGuideTitleAdapter
 * @Author      luoyi
 * @Time        2020/10/14 10:02
 */
class TourGuideTitleAdapter(var helper: LayoutHelper) :
    DelegateAdapter.Adapter<RecyclerView.ViewHolder>() {

    var commonTemlate: CommonTemlate? = null
    var tourBean: TourHomeBean? = null
    val factory: TourGuideTemplateFactory by lazy {
        TourGuideTemplateFactory()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return factory.create(parent, viewType)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onCreateLayoutHelper(): LayoutHelper {
        return helper
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        commonTemlate?.let {
            when (holder) {
                is TourGuideTemplateOneHolder -> {
                    var titlbarView = TitleBarFactoryView(holder.binding.root.context).apply {
                        isShowMore = false
                        setData(it)
                        onTitileBarClickListener =
                            object : TitleBarFactoryView.OnTitleBarClickListener {
                                override fun toMoreInfo(commonTemlate: CommonTemlate) {

                                }

                            }
                    }
                    var views = holder.binding.llTourGuideTemplateOne.children
                    for (v in views) {
                        if (v is TitleBarFactoryView) {
                            holder.binding.llTourGuideTemplateOne.removeView(v)
                        }
                    }

                    holder.binding.llTourGuideTemplateOne.addView(
                        titlbarView,
                        0
                    )
                    tourBean?.let {
                        holder.bindData(tourBean, commonTemlate)
                    }
                }
                is TourGuideTemplateTwoHolder -> {
                    var titlbarView = TitleBarFactoryView(holder.binding.root.context).apply {
                        isShowMore = false
                        setData(it)
                        onTitileBarClickListener =
                            object : TitleBarFactoryView.OnTitleBarClickListener {
                                override fun toMoreInfo(commonTemlate: CommonTemlate) {

                                }

                            }
                    }
                    var views = holder.binding.llTourGuideTemplateTwo.children
                    for (v in views) {
                        if (v is TitleBarFactoryView) {
                            holder.binding.llTourGuideTemplateTwo.removeView(v)
                        }
                    }
                    holder.binding.llTourGuideTemplateTwo.removeView(titlbarView)
                    holder.binding.llTourGuideTemplateTwo.addView(
                        titlbarView,
                        0
                    )
                    tourBean?.let {
                        holder.bindData(tourBean, commonTemlate)
                    }
                }
                else -> {

                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (commonTemlate != null && !commonTemlate!!.moduleCode.isNullOrEmpty()) {
            return factory.getItemViewType(commonTemlate!!.moduleCode!!)
        }
        return super.getItemViewType(position)
    }

}