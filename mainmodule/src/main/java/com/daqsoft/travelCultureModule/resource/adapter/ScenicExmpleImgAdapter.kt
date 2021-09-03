package com.daqsoft.travelCultureModule.resource.adapter

import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemScenicExmpleImgBinding
import me.nereo.multi_image_selector.BigImgActivity

/**
 * @Description
 * @ClassName   ScenicExmpleImgAdapter
 * @Author      luoyi
 * @Time        2020/4/21 15:57
 */
class ScenicExmpleImgAdapter : RecyclerViewAdapter<ItemScenicExmpleImgBinding, String> {

    private var mContext: Context? = null

    constructor(context: Context) : super(R.layout.item_scenic_exmple_img) {
        this.mContext = context
    }

    override fun setVariable(mBinding: ItemScenicExmpleImgBinding, position: Int, item: String) {
        Glide.with(mContext!!)
            .load(item).placeholder(R.mipmap.placeholder_img_fail_240_180)
            .into(mBinding.imgScenicExmple)
        mBinding.root.onNoDoubleClick {
            val intent =
                Intent(mContext, BigImgActivity::class.java)
            intent.putExtra("position", position)
            intent.putStringArrayListExtra(
                "imgList",
                ArrayList(getData())
            )
            mContext!!.startActivity(intent)
        }
    }

}