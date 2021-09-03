package com.daqsoft.travelCultureModule.branches.adapter

import android.graphics.Color
import androidx.appcompat.content.res.AppCompatResources
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.adapter.setImageUrlqwx
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemBrandPalyfunZixunBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.travelCultureModule.clubActivity.bean.ClubZixunBean

/**
 * @Author：      邓益千
 * @Create by：   2020/5/8 19:24
 * @Description： 攻略
 */
class StrategyAdapter: RecyclerViewAdapter<ItemBrandPalyfunZixunBinding, ClubZixunBean>(R.layout.item_brand_palyfun_zixun) {

    override fun setVariable(mBinding: ItemBrandPalyfunZixunBinding, position: Int, item: ClubZixunBean) {

        mBinding.ivIbPlayName.text = item.title
        mBinding.ivIbPlayName.setTextColor(Color.parseColor("#333333"))
        if (item.imageUrls.isNotEmpty()) {
            setImageUrlqwx(
                mBinding.ivIbPlayLogo, item.imageUrls[0].url,
                AppCompatResources.getDrawable(BaseApplication.context, R.drawable.placeholder_img_fail_h300), 5
            )
        } else {
            setImageUrlqwx(
                mBinding.ivIbPlayLogo, "",
                AppCompatResources.getDrawable(BaseApplication.context, R.drawable.placeholder_img_fail_h300), 5
            )
        }
        mBinding.root.onNoDoubleClick {
            ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_INFO)
                .withString("id", item.id.toString())
                .withString("contentTitle", "攻略详情")
                .navigation()
        }

    }
}