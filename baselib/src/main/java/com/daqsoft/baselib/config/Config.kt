package com.daqsoft.baselib.config

import android.view.View

/**
 * 配置文件
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-10-16
 * @since JDK 1.8.0_191
 */
object Config {
    /**
     * 加载中视图
     */
    var loadingView: View? = null
    /**
     * 错误视图
     */
    var errorView: View? = null
    /**
     * 标题栏背景颜色
     */
    var titleBarColor: Int? = null
    /**
     * 标题栏字体颜色
     */
    var titleBarTextColor: Int? = null
    /**
     * 状态栏主题 R.string.theme_dark （黑色） ， R.string.theme_light （白色）
     */
    var theme: Int? = null
    /**
     * 返回键图标
     */
    var titleBarBackImg: Int? = null
}