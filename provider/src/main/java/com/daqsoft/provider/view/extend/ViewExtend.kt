package com.daqsoft.provider.view.extend

import android.util.Log
import android.view.View
import com.daqsoft.baselib.widgets.click.NoDoubleClickListener
import com.daqsoft.provider.net.StatisticsRepository

/**
 * 栏目点击事件
 * @param column 栏目名称
 * @param action 执行事件
 */
fun View.onColumnNoDoubleClick(column: String, action: () -> Unit) {
    setOnClickListener(object : NoDoubleClickListener() {
        override fun onNoDoubleClick(v: View?) {
            try {
                StatisticsRepository.instance.statistcsClick(column)
            } catch (e: Exception) {
                Log.e("ERROR", "" + e.message)
            }
            action()
        }
    })
}

/**
 * 模块点击事件
 * @param action 执行事件
 * @param module 模块名称
 */
fun View.onModuleNoDoubleClick(module: String, region: String, action: () -> Unit) {
    setOnClickListener(object : NoDoubleClickListener() {
        override fun onNoDoubleClick(v: View?) {
            try {
                StatisticsRepository.instance.statistcsModuleClick(module, region)
            } catch (e: Exception) {
            }
            action()
        }
    })
}