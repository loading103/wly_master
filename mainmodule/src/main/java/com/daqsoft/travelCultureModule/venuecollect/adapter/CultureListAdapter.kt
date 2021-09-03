package com.daqsoft.travelCultureModule.venuecollect.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.ImageLoadUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.baselib.widgets.timepicker.IMDensityUtil
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemCultureListBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.CultureListBean


internal class CultureListAdapter : RecyclerViewAdapter<ItemCultureListBinding, CultureListBean>(R.layout.item_culture_list) {

    @SuppressLint("CheckResult")
    override fun setVariable(mBinding: ItemCultureListBinding, position: Int, item: CultureListBean) {
        mBinding.datas=item


        ImageLoadUtil.glideStageManage(item.getRealImages(),mBinding.ivContent)

        mBinding.root.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.COLLECT_CULYURE_DETAIL)
                .withString("id",item.id)
                .navigation()
        }
    }









}