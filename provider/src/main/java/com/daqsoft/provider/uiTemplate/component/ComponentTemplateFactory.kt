package com.daqsoft.provider.uiTemplate.component

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.uiTemplate.TemplateType
import com.daqsoft.provider.uiTemplate.component.holder.ComponentTemplateCustomHolder
import com.daqsoft.provider.uiTemplate.component.holder.ComponentTemplateImageHolder
import com.daqsoft.provider.uiTemplate.component.holder.ComponentTemplateMenuHolder
import com.daqsoft.provider.uiTemplate.operation.holder.*

/**
 * @Description 运营专区样式生成器
 * @ClassName   OperationTemplateFactory
 * @Author      luoyi
 * @Time        2020/10/12 13:56
 */
class ComponentTemplateFactory : ComponentTemplateImpl {

    companion object {

        /**
         * 图片样式
         */
        const val STYLE_SUB_IMAGE = "image"

        /**
         * 自定义样式
         */
        const val STYLE_SUB_CUSTOM = "customize"

        /**
         * 菜单样式
         */
        const val STYLE_SUB_MENU = "menu"
    }

    override fun create(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TemplateType.COMPOENT_ONE_IMAGE -> {
                return ComponentTemplateImageHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_component_template_two, parent, false
                    )
                )
            }
            TemplateType.COMPOENT_ONE_CUSTOM -> {
                return ComponentTemplateCustomHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_component_template_two, parent, false
                    )
                )
            }
            TemplateType.COMPOENT_ONE_MENU -> {
                return ComponentTemplateMenuHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_component_template_one, parent, false
                    )
                )
            }
        }
        return ComponentTemplateMenuHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_component_template_one, parent, false
            )
        )
    }

    override fun getItemType(commonTemlate: CommonTemlate): Int {
        commonTemlate.run {
            return when (showNum) {
                2 -> {
                    // 两图样式
                    if (style != null) {
                        when (style) {
                            STYLE_SUB_IMAGE -> {
                                TemplateType.COMPOENT_ONE_IMAGE
                            }
                            STYLE_SUB_CUSTOM -> {
                                TemplateType.COMPOENT_ONE_CUSTOM
                            }
                            else -> {
                                TemplateType.COMPOENT_ONE_MENU
                            }
                        }
                    } else {
                        TemplateType.COMPOENT_ONE_MENU
                    }
                }
                4 -> {
                    TemplateType.COMPOENT_ONE_MENU

                }
                else -> {
                    TemplateType.COMPOENT_ONE_MENU
                }
            }
        }
    }

}