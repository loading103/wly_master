package com.daqsoft.venuesmodule.adapter

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.extend.dp
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.SocietiesBean
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ItemSocietiesBinding

/**
 * @package name：com.daqsoft.venuesmodule.adapter
 * @date 2020/9/16 17:25
 * @author zp
 * @describe 社团 adapter
 */
class SocietiesAdapter : RecyclerViewAdapter<ItemSocietiesBinding,SocietiesBean>(R.layout.item_societies) {

    override fun getItemCount(): Int {
        if (getData().size > 4){
            return 4
        }
        return super.getItemCount()
    }


    @SuppressLint("SetTextI18n")
    override fun setVariable(mBinding: ItemSocietiesBinding, position: Int, item: SocietiesBean) {
        // 宣传图
        val images = item.image.split(",")
        var url = ""
        if (images.isNotEmpty()) {
            url = images[0]
        }
        Glide
            .with(mBinding.root.context)
            .load(url)
            .apply(RequestOptions().apply {
                transform(MultiTransformation(CenterCrop(), RoundedCorners(5.dp)))
            })
            .placeholder(R.drawable.placeholder_img_fail_240_180)
            .into(mBinding.image)

        // 名称
        mBinding.title.text = item.name

        // 类型
        if (!item.type.isNullOrEmpty()){
            mBinding.tag.visibility = View.VISIBLE
            mBinding.tag.text = item.type
        }

        // 粉丝数
        if (item.fansNum == 0){
            mBinding.content.visibility = View.GONE
        }else{
            mBinding.content.text = "${item.fansNum}${mBinding.root.context.resources.getString(R.string.venue_fans)}"
        }

        // 社团详情
        mBinding.root.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_CLUB_INFO)
                .withInt("id",item.id)
                .navigation()
        }
    }
}