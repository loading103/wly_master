package com.daqsoft.provider.uiTemplate.titleBar.found.holder

import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.ZARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.bean.FoundAroundBean
import com.daqsoft.provider.databinding.ItemFoundTemplateOneBinding
import com.daqsoft.provider.net.ProviderApi
import com.daqsoft.provider.net.StatisticsRepository
import com.daqsoft.provider.uiTemplate.operation.holder.BaseOperationViewHolder
import com.daqsoft.provider.uiTemplate.titleBar.view.TitleBarFactoryView
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.provider.view.convenientbanner.ConvenientBanner
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import org.jetbrains.anko.support.v4.toast

/**
 * @Description 发现身边样式1
 * @ClassName   FoundTemplateOneHolder
 * @Author      luoyi
 * @Time        2020/10/15 15:55
 */
class FoundTemplateOneHolder(binding: ItemFoundTemplateOneBinding) :
    BaseOperationViewHolder<ItemFoundTemplateOneBinding>(binding) {

    override fun bindDataToUI(template: CommonTemlate) {
        var titlbarView = TitleBarFactoryView(binding.root.context).apply {
            isShowMore=false
            setData(template)
            onTitileBarClickListener =
                object : TitleBarFactoryView.OnTitleBarClickListener {
                    override fun toMoreInfo(commonTemlate: CommonTemlate) {

                    }

                }
        }
        var views = binding.llFoundTemplateOne.children
        for (v in views) {
            if (v is TitleBarFactoryView) {
                binding.llFoundTemplateOne.removeView(v)
            }
        }

        binding.llFoundTemplateOne.addView(
            titlbarView,
            0
        )
    }

    fun setFoundsToUI(founds: MutableList<FoundAroundBean>, currentLoction: LatLng) {
        if (!founds.isNullOrEmpty()) {
            binding.root.visibility = View.VISIBLE
            binding.circleIndicator.total = founds.size
            binding?.bannerFound
                .setPages(object : CBViewHolderCreator {
                    override fun createHolder(itemView: View?): Holder<*> {
                        return FoundAroundContentHolder(itemView!!, currentLoction!!)
                    }

                    override fun getLayoutId(): Int {
                        return R.layout.item_content_found
                    }
                }, founds)
                .setCanLoop(founds.size > 1)
                .setPointViewVisible(false)
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_LEFT)
                .setOnItemClickListener {
                    StatisticsRepository.instance.statistcsModuleClick(
                        "发现身边",
                        ProviderApi.REGION_MAIN_COLUMNS
                    )
                    var path = ""
                    when (founds[it].resourceType) {
                        // 场馆
                        ResourceType.CONTENT_TYPE_VENUE -> {
                            path = ARouterPath.VenuesModule.VENUES_DETAILS_ACTIVITY
                        }
                        // 农家乐
                        ResourceType.CONTENT_TYPE_AGRITAINMENT -> {
                            path = ARouterPath.CountryModule.COUNTRY_HAPPINESS_DETAIL
                        }
                        // 	活动室
                        ResourceType.CONTENT_TYPE_ACTIVITY_SHIU -> {
                            path = ARouterPath.VenuesModule.ACTIVITY_ROOM_DETAIL
                        }
                        // 酒店
                        ResourceType.CONTENT_TYPE_HOTEL -> {
                            path = ZARouterPath.ZMAIN_HOTEL_DETAIL
                        }
                        // 景区
                        ResourceType.CONTENT_TYPE_SCENERY -> {
                            path = MainARouterPath.MAIN_SCENIC_DETAIL
                        }
                        // 景点
                        ResourceType.CONTENT_TYPE_SCENIC_SPOTS -> {
                            path = MainARouterPath.MAIN_SCENIC_SPOT_DETAI
                        }
                        // 餐饮
                        ResourceType.CONTENT_TYPE_RESTAURANT -> {
                            path = MainARouterPath.MAIN_FOOD_DETAIL
                        }
                        // 特产
                        ResourceType.CONTENT_TYPE_SPECIALTY -> {
                            path = MainARouterPath.MAIN_SPECIAL_DETAIL
                        }
                        // 活动
                        ResourceType.CONTENT_TYPE_ACTIVITY -> {
                            path = ARouterPath.VenuesModule.VENUES_DETAILS_ACTIVITY
                        }
                        else -> {
                            ToastUtils.showMessage("功能开发中，敬请期待!")
                        }
                    }
                    if (!path.isNullOrEmpty())
                        ARouter.getInstance().build(path)
                            .withString("id", founds[it].resourceId)
                            .navigation()
                }
                .setPageIndicator(null)
                .startTurning(3000)
                .setOnPageChangeListener(object : OnPageChangeListener {
                    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {

                    }

                    override fun onPageSelected(index: Int) {
                        binding.circleIndicator.setSteps(index)
                    }

                    override fun onScrollStateChanged(
                        recyclerView: RecyclerView?,
                        newState: Int
                    ) {
                    }

                })
        } else {
            binding.root.visibility = View.GONE
        }
    }
}