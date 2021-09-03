package com.daqsoft.baselib.utils

import com.daqsoft.baselib.adapter.MultipleRecyclerViewAdapter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseResponse

/**
 * 翻页处理工具类
 * @author 黄熙
 * @date 2020/3/1 0001
 * @version 1.0.0
 * @since JDK 1.8
 */
class PageDealUtils {

    var onPageListener: OnPageListener? = null

    /**
     * 列表数据页码处理
     *
     * @param page     当前页
     * @param response 返回数据体
     * @param adapter  适配器
     */
    fun pageDeal(
        page: Int?,
        response: BaseResponse<*>?,
        adapter: RecyclerViewAdapter<*, *>?
    ) {
        if (page == null) {
            return
        }
        if (page == 1) {
            adapter?.clear()
        }
        if (response == null) {
            adapter?.loadEnd()
            return
        }
        var isShowEmpte = false
        if (page == 1 && response.datas.isNullOrEmpty()) {
            onPageListener?.onEmpty()
            isShowEmpte = true
        }
        if (response.page == null) {
            adapter?.loadEnd()
            return
        }
        if (response!!.page!!.currPage < response!!.page!!.totalPage) {
            adapter?.loadComplete()
        } else {
            adapter?.loadEnd()
            if (!isShowEmpte)
                onPageListener?.onNoMoreData()
        }
    }

    /**
     * 列表数据页码处理
     *
     * @param page     当前页
     * @param response 返回数据体
     * @param adapter  适配器
     */
    fun pageDeal(
        page: Int?,
        response: BaseResponse<*>,
        adapter: MultipleRecyclerViewAdapter<*, *>
    ) {
        if (page == null) {
            return
        }
        if (page == 1) {
            adapter.clear()
        }
        if (response.page == null) {
            adapter.loadEnd()
            return
        }
        if (response.page!!.currPage < response.page!!.totalPage) {
            adapter.loadComplete()
        } else {
            adapter.loadComplete()
            adapter.loadEnd()
        }
    }

    interface OnPageListener {
        fun onEmpty()
        fun onNoMoreData()
    }
}