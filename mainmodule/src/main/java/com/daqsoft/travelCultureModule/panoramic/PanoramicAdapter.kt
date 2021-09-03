package com.daqsoft.travelCultureModule.panoramic

import android.content.Context
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemRvPanoramicBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.travelCultureModule.panoramic.bean.PanoramicListBean

internal class PanoramicAdapter : RecyclerViewAdapter<ItemRvPanoramicBinding, PanoramicListBean> {

    private var mContext: Context? = null

    constructor(context: Context) : super(R.layout.item_rv_panoramic) {
        mContext = context
    }

    override fun setVariable(mBinding: ItemRvPanoramicBinding, position: Int, item: PanoramicListBean) {
        mBinding.item = item
        var url = item.panoramaCover
//        if(!item.images.isNullOrEmpty()){
//         var images =   item.images.split(",")
//            if(!images.isNullOrEmpty()){
//                url = images[0]
//            }
//        }
        Glide.with(mContext!!).load(url)
            .placeholder(R.mipmap.img_panaro_2_1)
            .into(mBinding.ivBg)
        mBinding.root.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.Provider.WEB_ACTIVITY)
                .withString("mTitle", item.name)
                .withString("html", item.url)
                .navigation()
        }
    }


}