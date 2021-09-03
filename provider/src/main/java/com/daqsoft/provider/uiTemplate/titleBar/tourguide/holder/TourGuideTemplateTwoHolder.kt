package com.daqsoft.provider.uiTemplate.titleBar.tourguide.holder

import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.bean.TourHomeBean
import com.daqsoft.provider.databinding.ItemTourGuideTemplateOneBinding
import com.daqsoft.provider.databinding.ItemTourGuideTemplateTwoBinding
import com.daqsoft.provider.uiTemplate.operation.holder.BaseOperationViewHolder
import com.daqsoft.provider.utils.DividerTextUtils
import java.lang.StringBuilder

/**
 * @Description
 * @ClassName   TourGuideTemplateOneHolder
 * @Author      luoyi
 * @Time        2020/10/14 14:28
 */
class TourGuideTemplateTwoHolder(binding: ItemTourGuideTemplateTwoBinding) :
    BaseOperationViewHolder<ItemTourGuideTemplateTwoBinding>(binding) {
    override fun bindDataToUI(template: CommonTemlate) {
//        binding
    }

    fun bindData(tourBean: TourHomeBean?, template: CommonTemlate?) {
        tourBean?.let {
            binding.tourGuideImg = tourBean.images
//            binding.tourName = it.name
//            binding.tourInfo = DividerTextUtils.convertDotString(
//                StringBuilder(),
//                "景点·${it.placeNum}",
//                "线路·${it.lineNum}",
//                "停车场·${it.parkingNum}",
//                "厕所·${it.toiletNum}"
//            )
            binding.root.onNoDoubleClick {
//                when (it.type) {
//                    1 -> {
//                        ARouter.getInstance()
//                            .build(ARouterPath.GuideModule.GUIDE_TOUR_ACTIVITY)
//                            .withString("tourId", "")
//                            .withString("allAreaTourId", template?.menuValue)
//                            .navigation()
//                    }
//                    2 -> {
                        ARouter.getInstance()
                            .build(ARouterPath.GuideModule.GUIDE_TOUR_ACTIVITY)
                            .withString("tourId", template?.menuValue)
                            .withString("allAreaTourId", "")
                            .navigation()
//                    }
//                }
            }
        }
    }
}