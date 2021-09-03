package com.daqsoft.travelCultureModule.clubActivity.adapter

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.adapter.setImageUrl
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemClubZixunBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.travelCultureModule.clubActivity.bean.ClubZixunBean
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit


class ClubZixunAdapter(context: Context) :
    RecyclerViewAdapter<ItemClubZixunBinding, ClubZixunBean>(
        R.layout.item_club_zixun
    ) {
    @SuppressLint("CheckResult")
    override fun setVariable(mBinding: ItemClubZixunBinding, position: Int, item: ClubZixunBean) {
        mBinding.ivCiznName.text = item.title
        mBinding.ivCiznContent.text = item.createCompany
        if (item.imageUrls.isNotEmpty()) {
            setImageUrl(
                mBinding.ivCizn, item.imageUrls[0].url,
                AppCompatResources.getDrawable(BaseApplication.context, R.drawable.placeholder_img_fail_240_180), 5
            )
        } else {
            setImageUrl(
                mBinding.ivCizn, "",
                AppCompatResources.getDrawable(BaseApplication.context, R.drawable.placeholder_img_fail_240_180), 5
            )
        }
        RxView.clicks(mBinding.root)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if (item.contentType.equals("IMAGE")) {
                    ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_IMG)
                        .withString("id", item.id.toString())
                        .navigation()
                } else {
                    ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_INFO)
                        .withString("id", item.id.toString())
                        .withString("contentTitle", "资讯详情")
                        .navigation()
                }
            }
    }


}