package com.daqsoft.provider.uiTemplate.banner

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import com.alibaba.android.vlayout.LayoutHelper
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.databinding.ItemAdsBannerTemplateBinding
import com.daqsoft.provider.uiTemplate.BaseDelegateAdapter
import com.daqsoft.provider.uiTemplate.TemplateType
import com.youth.banner.indicator.CircleIndicator

/**
 * @Description 广告轮播图
 * @ClassName   AdsBannerAdapter
 * @Author      luoyi
 * @Time        2020/10/10 16:38
 */
class AdsBannerAdapter(helper: LayoutHelper) : BaseDelegateAdapter<ItemAdsBannerTemplateBinding>(
    helper,
    R.layout.item_ads_banner_template
) {
    var adses: MutableList<CommonTemlate> = mutableListOf()
    override fun bindDataToView(mBinding: ItemAdsBannerTemplateBinding, position: Int) {
        if (!adses.isNullOrEmpty()) {
            var images = mutableListOf<String>()
            for (img in adses) {
                images.add(img.imgUrl!!)
            }
            mBinding.banner.adapter = ImageBannerAdapter(adses)
            mBinding.banner.outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    outline.setRoundRect(0, 0, view.width, view.height, Utils.dip2px(BaseApplication.context, 5f))
                }
            }
            mBinding.banner.clipToOutline = true
            mBinding.banner.indicator = CircleIndicator(mBinding.root.context)
            mBinding.banner.start()
//            mBinding?.bannerTopAdv?.stopTurning()
//            mBinding?.bannerTopAdv
//                .setPages(object : CBViewHolderCreator {
//                    override fun createHolder(itemView: View?): Holder<*> {
//                        return BaseBannerImageHolder(itemView!!)
//                    }
//
//                    override fun getLayoutId(): Int {
//                        return R.layout.holder_img_adv_90
//                    }
//                }, images)
//                .setCanLoop(true)
//                .setPointViewVisible(images.size > 1)
//                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
//                .setOnItemClickListener {
//                    // 跳转事件
//                    var item = adses.get(it)
//                    item?.let { adv ->
//                        MenuJumpUtils.adJump(adv)
//                        StatisticsRepository.instance.statistcsModuleClick(
//                            "首页广告",
//                            ProviderApi.REGION_ADV
//                        )
//                    }
//                }
//                .setFirstItemPos(0)
//                .setPageIndicator(null)
//                .startTurning(3000)
        }
    }

    override fun getItemCount(): Int {
        return 1
    }

    fun stopTurning() {
//        binding?.bannerTopAdv?.stopTurning()
    }

    fun startTuring() {
//        binding?.bannerTopAdv?.startTurning(3000)
    }

    override fun getItemViewType(position: Int): Int {
        return TemplateType.ADS_BANNER
    }
}