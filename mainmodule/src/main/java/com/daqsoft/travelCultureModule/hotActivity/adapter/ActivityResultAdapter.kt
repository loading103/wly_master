package com.daqsoft.travelCultureModule.hotActivity.adapter

import android.content.Context
import android.content.Intent
import android.view.View
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemActResultBinding
import com.daqsoft.provider.bean.ImageVideoBean
import me.nereo.multi_image_selector.BigImgActivity
import me.nereo.multi_image_selector.video.PlayerActivity
import java.util.ArrayList

/**
 * @Description
 * @ClassName   ActivityResultAdapter
 * @Author      luoyi
 * @Time        2020/6/12 15:02
 */
class ActivityResultAdapter : RecyclerViewAdapter<ItemActResultBinding, ImageVideoBean> {

    var mContext: Context? = null

    var showMore: Boolean = false

    constructor(context: Context) : super(R.layout.item_act_result) {
        this.mContext = context
    }

    override fun getItemCount(): Int {
        if (!showMore && getData().size > 9) {
            return 9
        }
        return super.getItemCount()
    }

    override fun setVariable(mBinding: ItemActResultBinding, position: Int, item: ImageVideoBean) {
        item?.let {
            if(it.type==1){
                mBinding.imgActResult?.let {
                    Glide.with(mContext!!)
                        .load(StringUtil.getVideoCoverUrl(item.video!!))
                        .placeholder(R.mipmap.placeholder_img_fail_240_180)
                        .into(mBinding.imgActResult)
                }
                mBinding.imgActResultVideo?.visibility = View.VISIBLE
            } else {
                mBinding.imgActResult?.let {
                    Glide.with(mContext!!)
                        .load(item.image)
                        .placeholder(R.mipmap.placeholder_img_fail_240_180)
                        .into(mBinding.imgActResult)
                }
                mBinding.imgActResultVideo?.visibility = View.GONE
            }
            mBinding.root.onNoDoubleClick {
                if (item.type == 1) {
                    val intent =
                        Intent(mContext, PlayerActivity::class.java)
                    intent.putExtra("title", "视频播放")
                    intent.putExtra("url", item.video)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    mContext!!.startActivity(intent)
                } else {
                    var list: ArrayList<String> = ArrayList()
                    list.add(item.image!!)
                    val intent =
                        Intent(mContext!!, BigImgActivity::class.java)
                    intent.putExtra("position", 0)
                    intent.putStringArrayListExtra(
                        "imgList", list
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    mContext!!.startActivity(intent)
                }
            }
        }
    }
}