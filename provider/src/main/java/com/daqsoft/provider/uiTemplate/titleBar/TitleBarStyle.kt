package com.daqsoft.provider.uiTemplate.titleBar

import com.daqsoft.provider.bean.CommonTemlate

/**
 * @Description
 * @ClassName   TitleBarStyle
 * @Author      luoyi
 * @Time        2020/10/14 11:24
 */
class TitleBarStyle {
    companion object {
        const val STYLE_ONE: String = "1"
        const val STYLE_TWO: String = "2"
        const val STYLE_THREE: String = "3"
        const val TITLE_TYPE_IMAGE = "image"
        const val TITLE_TYPE_WORD = "word"

        /**
         * 返回当前主标题样式模板
         * 扩展模板，需要修改该方法
         * @param commonTemlate CommonTemlate实体
         */
        fun getTemplateStyle(commonTemlate: CommonTemlate): String {
            var type: String = TitleBarStyle.STYLE_ONE
            // 目前判断逻辑为默认样式1 只有主标题为文字时，切配置主标题前面头图的（icon）为样式2
            if (!commonTemlate.mainTitleType.isNullOrEmpty()) {
                if (commonTemlate.mainTitleType == TitleBarStyle.TITLE_TYPE_WORD) {
                    if (!commonTemlate.icon.isNullOrEmpty()) {
                        type = TitleBarStyle.STYLE_TWO
                    }
                }
            }
            return type
        }
    }

}