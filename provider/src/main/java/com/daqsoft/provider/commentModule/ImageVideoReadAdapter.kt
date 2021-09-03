package com.daqsoft.provider.commentModule

import android.content.Context
import android.content.Intent
import android.view.View
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.R
import com.daqsoft.provider.base.LocalResourceType
import com.daqsoft.provider.databinding.ItemImageVideoBinding
import me.nereo.multi_image_selector.BigImgActivity
import me.nereo.multi_image_selector.video.PlayerActivity
import java.util.*

/**
 * @Description 图片视频查看专用适配器
 * @ClassName   ImageVideoReadAdapter
 * @Author      PuHua
 * @Time        2019/12/27 14:48
 */
class ImageVideoReadAdapter(context: Context) :
    RecyclerViewAdapter<ItemImageVideoBinding, ResourceBean>(
        R.layout.item_image_video
    ) {
    private val mContext = context
    private var datas: MutableList<ResourceBean> = mutableListOf()
    /**
     * 设置item
     */
    override fun setVariable(mBinding: ItemImageVideoBinding, position: Int, item: ResourceBean) {
        // 当位图片时隐藏播放按钮
        if (item.type == LocalResourceType.IMAGE) {
            mBinding.ivVideoStart.visibility = View.GONE
        } else {
            mBinding.ivVideoStart.visibility = View.VISIBLE
        }
        // 当位视频时取第一帧
        if (item.type == LocalResourceType.VIDEO) {

            Glide.with(mContext)
                .load(StringUtil.getVideoCoverUrl(item.url))
                .placeholder(R.drawable.placeholder_img_fail_240_180)
                .into(mBinding.aivImage)
            // 异步加载视频缩略图
//            val disposable = Observable.just<String>(item.url)
//                .map<Any> { s -> ThumbnilUtits.getThumbnilBaos(s, MediaStore.Images.Thumbnails.MICRO_KIND) }
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe { bytes ->
//                    Glide.with(mBinding.aivImage)
//                        .load(bytes)
//                        .into(mBinding.aivImage)
//                }
//            mBinding.aivImage.setImageBitmap(getNetVideoBitmap(item.url))
        } else {
            GlideModuleUtil.loadDqImageWaterMark(item.url,mContext!!,mBinding.aivImage)
        }
        mBinding.aivImage.onNoDoubleClick {
            if (item.type == LocalResourceType.VIDEO) {
                val intent = Intent(mContext, PlayerActivity::class.java)
                intent.putExtra("title", "视频播放")
                intent.putExtra("url", item.url)
                mContext.startActivity(intent)
            } else {
                val images: MutableList<String> = mutableListOf()

                if(datas.isNotEmpty()){
                    datas.forEach{
                        images.add(it.url)
                    }
                }else{
                    images.add(item.url)
                }
                val intent = Intent(mContext, BigImgActivity::class.java)

                if(datas.isNotEmpty()){
                    intent.putExtra("position", position)
                }else{
                    intent.putExtra("position",0)
                }
                intent.putExtra("ispl",true)
                intent.putStringArrayListExtra(
                    "imgList",
                    ArrayList(images)
                )
                mContext.startActivity(intent)
            }
        }
    }

    fun setData(resources: MutableList<ResourceBean>) {
        datas=resources
    }
}
