package com.daqsoft.provider.uiTemplate.titleBar.culturetourism

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.provider.R
import com.daqsoft.provider.uiTemplate.TemplateType
import com.daqsoft.provider.uiTemplate.titleBar.culturetourism.holder.CultureTourismViewHolderOne
import com.daqsoft.provider.uiTemplate.titleBar.culturetourism.holder.CultureTourismViewHolderThree
import com.daqsoft.provider.uiTemplate.titleBar.culturetourism.holder.CultureTourismViewHolderTwo

/**
 * 文旅品牌样式 适配器工厂
 */
class CultureTourismFactory : CultureTourismImpl {

    companion object {
        /**
         * 精品线路样式1
         */
        var CULTURE_TOURISM_STYLE_ONE = "1"

        /**
         * 精品线路样式1
         */
        var CULTURE_TOURISM_STYLE_TWO = "2"

        /**
         * 精品线路样式3
         */
        var CULTURE_TOURISM_STYLE_THREE = "3"

        var CULTURE_TOURISM_STYLE_FOUR = "4"
    }

    override fun create(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TemplateType.CULTURE_TOURISM_STYLE_ONE -> {
                CultureTourismViewHolderOne(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_culture_tourism_style, parent, false
                    )
                )
            }
            TemplateType.CULTURE_TOURISM_STYLE_TWO -> {
                CultureTourismViewHolderTwo(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_culture_tourism_style, parent, false
                    )
                )
            }
            TemplateType.CULTURE_TOURISM_STYLE_THREE -> {
                CultureTourismViewHolderThree(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_culture_tourism_style, parent, false
                    )
                )
            }
            TemplateType.CULTURE_TOURISM_STYLE_FOUR->{
                CultureTourismViewHolderThree(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_culture_tourism_style, parent, false
                    )
                )
            }
            else -> {
                CultureTourismViewHolderOne(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_culture_tourism_style, parent, false
                    )
                )
            }
        }
    }

    override fun getItemViewType(style: String): Int {
        return when (style) {
            CULTURE_TOURISM_STYLE_ONE -> {
                TemplateType.CULTURE_TOURISM_STYLE_ONE
            }
            CULTURE_TOURISM_STYLE_TWO -> {
                TemplateType.CULTURE_TOURISM_STYLE_TWO
            }
            CULTURE_TOURISM_STYLE_THREE -> {
                TemplateType.CULTURE_TOURISM_STYLE_THREE
            }
            else -> {
                TemplateType.CULTURE_TOURISM_STYLE_THREE
            }
        }
    }
}