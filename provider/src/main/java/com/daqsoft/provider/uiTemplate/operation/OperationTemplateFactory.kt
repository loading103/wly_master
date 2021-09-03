package com.daqsoft.provider.uiTemplate.operation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.uiTemplate.TemplateType
import com.daqsoft.provider.uiTemplate.operation.holder.*

/**
 * @Description 运营专区样式生成器
 * @ClassName   OperationTemplateFactory
 * @Author      luoyi
 * @Time        2020/10/12 13:56
 */
class OperationTemplateFactory : OperationTemplateImpl {

    companion object {

        /**
         * 三图样式 子sub1
         */
        const val STYLE_SUB_THREE_ONE = "style_1"

        /**
         * 三图样式 子sub2
         */
        const val STYLE_SUB_THREE_TWO = "style_2"
    }

    override fun create(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
           TemplateType.OPERATION_IMG_TWO -> {
                return OperataionImgTwoViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_operation_style_img_two, parent, false
                    )
                )
            }
            TemplateType.OPERATION_IMG_THREE_STYLE_ONE -> {
                return OperataionImgThreeSubOneViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_operation_style_img_three_sub_one, parent, false
                    )
                )
            }
            TemplateType.OPERATION_IMG_THREE_STYLE_TWO -> {
                return OperataionImgThreeSubTwoViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_operation_style_img_three_sub_two, parent, false
                    )
                )
            }
            TemplateType.OPERATION_IMG_FOUR -> {
                return OperataionImgFourViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_operation_style_img_four, parent, false
                    )
                )
            }
            TemplateType.OPERATION_IMG_FIVE_N -> {
                return OperataionImgFivenViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_operation_style_img_four, parent, false
                    )
                )
            }
        }
        return OperataionImgTwoViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_operation_style_img_two, parent, false
            )
        )
    }

    override fun getItemType(commonTemlate: CommonTemlate): Int {
        commonTemlate.run {
            return when (imgNum) {
//                    1 -> {
//                        // 暂无1图样式
//                    }
                2 -> {
                    // 两图样式
                    TemplateType.OPERATION_IMG_TWO
                }
                3 -> {
                    // 三图样式
                    if (!style.isNullOrEmpty()) {
                        if (style == STYLE_SUB_THREE_ONE) {
                            // 三图样式1
                            TemplateType.OPERATION_IMG_THREE_STYLE_ONE
                        } else {
                            // 三图样式2
                            TemplateType.OPERATION_IMG_THREE_STYLE_TWO
                        }
                    } else {
                        TemplateType.OPERATION_IMG_THREE_STYLE_ONE
                    }

                }
                4 -> {
                    // 四图样式
                    TemplateType.OPERATION_IMG_FOUR
                }
                else -> {
                    // 五图样式 n
                    TemplateType.OPERATION_IMG_FIVE_N
                }
            }
        }
    }

}