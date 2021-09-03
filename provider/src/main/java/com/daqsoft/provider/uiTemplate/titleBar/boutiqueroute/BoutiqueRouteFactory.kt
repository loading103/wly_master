package com.daqsoft.provider.uiTemplate.titleBar.boutiqueroute

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.provider.R
import com.daqsoft.provider.uiTemplate.TemplateType
import com.daqsoft.provider.uiTemplate.titleBar.boutiqueroute.holder.BoutiqueRouteViewHolderFour
import com.daqsoft.provider.uiTemplate.titleBar.boutiqueroute.holder.BoutiqueRouteViewHolderOne
import com.daqsoft.provider.uiTemplate.titleBar.boutiqueroute.holder.BoutiqueRouteViewHolderThree
import com.daqsoft.provider.uiTemplate.titleBar.boutiqueroute.holder.BoutiqueRouteViewHolderTwo

/**
 * 精品路线样式 适配器工厂
 */
class BoutiqueRouteFactory : BoutiqueRouteImpl {

    companion object {
        /**
         * 精品线路样式1
         */
        var BOUTIQUE_ROUTE_STYLE_ONE = "1"

        /**
         * 精品线路样式1
         */
        var BOUTIQUE_ROUTE_STYLE_TWO = "2"

        /**
         * 精品线路样式3
         */
        var BOUTIQUE_ROUTE_STYLE_THREE = "3"
    }

    override fun create(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TemplateType.BOUTIQUE_ROUTE_STYLE_ONE -> {
                BoutiqueRouteViewHolderOne(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_boutique_route_style, parent, false
                    )
                )
            }
            TemplateType.BOUTIQUE_ROUTE_STYLE_TWO -> {
                BoutiqueRouteViewHolderTwo(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_boutique_route_style, parent, false
                    )
                )
            }
            TemplateType.BOUTIQUE_ROUTE_STYLE_THREE -> {
                BoutiqueRouteViewHolderThree(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_boutique_route_style, parent, false
                    )
                )
            }
            TemplateType.BOUTIQUE_ROUTE_STYLE_FOUR -> {
                BoutiqueRouteViewHolderFour(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_boutique_route_style_four, parent, false
                    )
                )
            }
            else -> {
                BoutiqueRouteViewHolderOne(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_boutique_route_style, parent, false
                    )
                )
            }
        }
    }

    override fun getItemViewType(style: String): Int {
        return when (style) {
            BOUTIQUE_ROUTE_STYLE_ONE -> {
                TemplateType.BOUTIQUE_ROUTE_STYLE_ONE
            }
            BOUTIQUE_ROUTE_STYLE_TWO -> {
                TemplateType.BOUTIQUE_ROUTE_STYLE_TWO
            }
            BOUTIQUE_ROUTE_STYLE_THREE -> {
                TemplateType.BOUTIQUE_ROUTE_STYLE_THREE
            }
            else -> {
                TemplateType.BOUTIQUE_ROUTE_STYLE_ONE
            }
        }
    }
}
