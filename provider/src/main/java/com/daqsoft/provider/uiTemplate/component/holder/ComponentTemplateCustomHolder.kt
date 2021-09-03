package com.daqsoft.provider.uiTemplate.component.holder

import android.view.View
import androidx.core.view.children
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.bean.CommponentDetail
import com.daqsoft.provider.databinding.ItemComponentTemplateTwoBinding
import com.daqsoft.provider.uiTemplate.component.adapter.ComponentTwoItemCustomAdapter
import com.daqsoft.provider.uiTemplate.operation.holder.BaseOperationViewHolder
import com.daqsoft.provider.uiTemplate.titleBar.view.TitleBarFactoryView
import com.daqsoft.provider.utils.MenuJumpUtils
import java.lang.ref.SoftReference

/**
 * @Description
 * @ClassName   ComponentTemplateTwoHolder
 * @Author      luoyi
 * @Time        2020/10/16 15:57
 */
class ComponentTemplateCustomHolder(binding:ItemComponentTemplateTwoBinding):BaseOperationViewHolder<ItemComponentTemplateTwoBinding>(binding) {
    var fragmentManger: SoftReference<FragmentManager>? = null
    private val componentItemAdapter: ComponentTwoItemCustomAdapter by lazy {
        ComponentTwoItemCustomAdapter()
    }

    override fun bindDataToUI(template: CommonTemlate) {
        var titlbarView = TitleBarFactoryView(binding.root.context).apply {
            isShowMore=false
            setData(template)
//            setBackGround(R.color.f5)
            setTitleSize(18f)
            onTitileBarClickListener =
                object : TitleBarFactoryView.OnTitleBarClickListener {
                    override fun toMoreInfo(commonTemlate: CommonTemlate) {

                    }

                }
        }
        var views =binding.llCommponentTemplateTwo.children
        for (v in views) {
            if (v is TitleBarFactoryView) {
                binding.llCommponentTemplateTwo.removeView(v)
            }
        }
        binding.llCommponentTemplateTwo.removeView(titlbarView)
        binding.llCommponentTemplateTwo.addView(
            titlbarView,
            0
        )
    }

    fun bindCommonent(templates: MutableList<CommponentDetail>, template: CommonTemlate) {
        bindDataToUI(template)
        if (!templates.isNullOrEmpty()) {
            binding.root.visibility = View.VISIBLE
            binding.rvComponentTempatentTwo.run {
                componentItemAdapter.clear()
                componentItemAdapter.add(templates)
                layoutManager = GridLayoutManager(binding.rvComponentTempatentTwo.context,2,
                    GridLayoutManager.VERTICAL,false)
                adapter = componentItemAdapter.apply {
                    onItemClickListener=object :ComponentTwoItemCustomAdapter.OnItemClickListener{
                        override fun onItemClick(item: CommponentDetail) {
                            MenuJumpUtils.servicePageJumpUtils(
                                item,
                                "",
                                "",
                                fragmentManger?.get(),
                                binding.root.context
                            )
                        }

                    }
                }
            }
        } else {
            binding.root.visibility = View.GONE
        }
    }
}