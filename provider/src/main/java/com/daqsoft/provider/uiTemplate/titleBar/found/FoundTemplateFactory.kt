package com.daqsoft.provider.uiTemplate.titleBar.found

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.provider.R
import com.daqsoft.provider.uiTemplate.TemplateType
import com.daqsoft.provider.uiTemplate.titleBar.found.holder.FoundTemplateOneHolder
import com.daqsoft.provider.uiTemplate.titleBar.found.holder.FoundTemplateTwoHolder

/**
 * @Description
 * @ClassName   FoundTemplateFactory
 * @Author      luoyi
 * @Time        2020/10/15 15:59
 */
class FoundTemplateFactory : FoundTemplateImpl {

    companion object {
        /**
         * 发现身边样式1
         */
        var FOUND_STYLE_ONE = "1"

        /**
         * 发现身边样式2
         */
        var FOUND_STYLE_TWO = "2"
    }

    override fun getItemViewType(style: String): Int {
        return when (style) {
            FOUND_STYLE_ONE -> {
                TemplateType.FOUND_STYLE_ONE
            }
            FOUND_STYLE_TWO -> {
                TemplateType.FOUND_STYLE_TWO
            }
            else -> {
                TemplateType.FOUND_STYLE_ONE
            }
        }
    }

    override fun create(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TemplateType.FOUND_STYLE_ONE -> {
                FoundTemplateOneHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_found_template_one, parent, false
                    )
                )
            }
            TemplateType.FOUND_STYLE_TWO -> {
                FoundTemplateTwoHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_found_template_two, parent, false
                    )
                )
            }
            else -> {
                FoundTemplateOneHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_found_template_one, parent, false
                    )
                )
            }
        }

    }

}