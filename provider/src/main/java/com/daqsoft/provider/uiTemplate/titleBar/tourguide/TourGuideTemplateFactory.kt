package com.daqsoft.provider.uiTemplate.titleBar.tourguide

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.provider.R
import com.daqsoft.provider.uiTemplate.TemplateType
import com.daqsoft.provider.uiTemplate.titleBar.tourguide.holder.TourGuideTemplateOneHolder
import com.daqsoft.provider.uiTemplate.titleBar.tourguide.holder.TourGuideTemplateTwoHolder

/**
 * @Description 导游导览模板生成器
 * @ClassName   TourGuideTemplateFactory
 * @Author      luoyi
 * @Time        2020/10/14 10:33
 */
class TourGuideTemplateFactory : TourGuideTemplateImpl {


    companion object {
        /**
         * 导游导览样式1
         */
        var TOUR_GUIDE_STYLE_ONE = "1"

        /**
         * 导游导览样式2
         */
        var TOUR_GUIDE_STYLE_TWO = "2"

    }

    override fun getItemViewType(style: String): Int {
        return when (style) {
            TOUR_GUIDE_STYLE_ONE -> {
               TemplateType.TOUR_GUID_ONE
            }
            TOUR_GUIDE_STYLE_TWO -> {
                TemplateType.TOUR_GUID_TWO
            }
            else -> {
                TemplateType.TOUR_GUID_ONE
            }
        }
    }

    override fun create(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TemplateType.TOUR_GUID_ONE -> {
                TourGuideTemplateOneHolder( DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_tour_guide_template_one, parent, false
                ))
            }
            TemplateType.TOUR_GUID_TWO -> {
                TourGuideTemplateTwoHolder( DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_tour_guide_template_two, parent, false
                ))
            }
            else -> {
                TourGuideTemplateOneHolder( DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_tour_guide_template_one, parent, false
                ))
            }
        }

    }

}