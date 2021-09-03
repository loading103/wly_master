package com.daqsoft.provider.uiTemplate.menu.topMenu

import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.databinding.ItemOperationStyleImgFourBinding
import com.daqsoft.provider.databinding.ItemOperationStyleImgThreeSubOneBinding
import com.daqsoft.provider.databinding.ItemTopMenuTemplateBinding
import com.daqsoft.provider.uiTemplate.operation.holder.BaseOperationViewHolder
import com.daqsoft.provider.view.convenientbanner.ConvenientBanner
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import java.lang.ref.SoftReference

/**
 * @Description
 * @ClassName   OperataionImgFourViewHolder
 * @Author      luoyi
 * @Time        2020/10/12 14:05
 */
class TopMenuViewHolder(
    val mBinding: ItemTopMenuTemplateBinding
) :
    BaseOperationViewHolder<ItemTopMenuTemplateBinding>(mBinding) {

    var fragmentManger: SoftReference<FragmentManager>? = null

    fun bindDataToUI(menus: MutableList<CommonTemlate>) {
        if (!menus.isNullOrEmpty()) {
            var count = menus.size
            var pageSize = 10
            var pageCount = (count + pageSize - 1) / pageSize
            var menusDatas: MutableList<MutableList<CommonTemlate>> = mutableListOf()
            for (index in 1..pageCount) {
                var start = (index - 1) * pageSize
                var max = start + pageSize
                var end = if (max > count) {
                    count
                } else {
                    max
                }
                var data = menus.subList(start, end)
                menusDatas.add(data)
            }
            mBinding.circleIndicator.total = pageCount
            mBinding.circleIndicator.setSteps(0)
            if (pageCount > 1) {
                mBinding.circleIndicator.visibility = View.VISIBLE
            } else {
                mBinding.circleIndicator.visibility = View.GONE
            }


            mBinding.cbrMenus.setPages(object : CBViewHolderCreator {
                override fun createHolder(itemView: View?): Holder<*> {
                    return HomeMenuHolderV2(itemView!!, itemView!!.context).apply {
                        fragmentManger?.let {
                            mFragmentManger = fragmentManger
                        }

                    }
                }

                override fun getLayoutId(): Int {
                    return R.layout.item_home_menu_template
                }
            }, menusDatas)
                .setCanLoop(menusDatas.size > 1)
                .setPageIndicator(null)
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                .setPointViewVisible(false)
                .setOnPageChangeListener(object : OnPageChangeListener {
                    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    }

                    override fun onPageSelected(index: Int) {
                        mBinding.circleIndicator.setSteps(index)
                    }

                    override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                    }

                })

        }
    }

    override fun bindDataToUI(template: CommonTemlate) {

    }

}