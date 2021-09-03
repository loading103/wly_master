package com.daqsoft.provider.uiTemplate.banner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.SingleLayoutHelper
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.databinding.ItemIndexTopBannerBinding
import com.daqsoft.provider.uiTemplate.BaseDelegateAdapter
import com.daqsoft.provider.uiTemplate.TemplateType
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.thetravelcloudwithculture.home.bean.CityCardBean
import com.daqsoft.thetravelcloudwithculture.ui.viewholder.HomeTopViewHolder

/**
 * @Description 首页顶部站点头图
 * @ClassName   IndexTopBannerAdapter
 * @Author      luoyi
 * @Time        2020/10/10 11:56
 */
class IndexTopBannerAdapter(helper: LayoutHelper) :
    BaseDelegateAdapter<ItemIndexTopBannerBinding>(
        helper,
        R.layout.item_index_top_banner
    ) {
    var datas: MutableList<CityCardBean> = mutableListOf()
    var onIndexTopBannerItemClickListener: OnIndexTopBannerItemClickListener? = null
    override fun bindDataToView(mBinding: ItemIndexTopBannerBinding, position: Int) {
        if (!datas.isNullOrEmpty()) {
            mBinding.cbrCity.setPages(object : CBViewHolderCreator {
                override fun createHolder(itemView: View?): Holder<*> {
                    return HomeTopViewHolder(itemView!!, itemView.context!!)
                }

                override fun getLayoutId(): Int {
                    return R.layout.home_sc_top_item
                }

            }, datas).setCanLoop(datas.size > 1).setPointViewVisible(datas.size > 1)
                .setPageIndicatorPadding(
                    0, 0, 0,
                    Utils.dip2px(BaseApplication.context!!, 22.0f).toInt()
                ).startTurning(3000)
        }
        mBinding.tvSearch.onNoDoubleClick {
            ARouter.getInstance().build(MainARouterPath.MAIN_ALL_SEARCH)
                .navigation()
        }
        mBinding.imgScanCode.onNoDoubleClick {
            onIndexTopBannerItemClickListener?.goToScanCode()
        }
    }


    override fun getItemCount(): Int {
        return 1
    }

    override fun getItemViewType(position: Int): Int {
        return TemplateType.INDEX_TOP_BANNER
    }

    interface OnIndexTopBannerItemClickListener {
        fun goToScanCode()
    }

}