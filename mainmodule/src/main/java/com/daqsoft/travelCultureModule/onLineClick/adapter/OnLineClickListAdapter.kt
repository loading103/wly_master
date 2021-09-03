package com.daqsoft.travelCultureModule.onLineClick.adapter

import android.annotation.SuppressLint
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemOnLineClickBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.travelCultureModule.onLineClick.bean.OnLineClickBean
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

/**
 * desc :
 * @author 江云仙
 * @date 2020/4/20 15:20
 */
class OnLineClickListAdapter :
    RecyclerViewAdapter<ItemOnLineClickBinding, OnLineClickBean>(R.layout.item_on_line_click) {
    @SuppressLint("CheckResult")
    override fun setVariable(
        mBinding: ItemOnLineClickBinding,
        position: Int,
        item: OnLineClickBean
    ) {
        mBinding.title = item.title
        mBinding.introduce = item.summary
        if (!item.imageUrls.isNullOrEmpty()) {
            mBinding.urls = item.imageUrls[0].url
        }
        RxView.clicks(mBinding.root)
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                // //点击跳转网页
                RxView.clicks(mBinding.root)
                    // 1秒内不可重复点击或仅响应一次事件
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_INFO)
                            .withString("id", item.id)
                            .withString("contentTitle", "资讯详情")
                            .navigation()
                    }

            }
    }

}