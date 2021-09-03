package com.daqsoft.travelCultureModule.resource.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.LayoutScenicTopImagesBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.utils.GaosiUtils
import com.daqsoft.provider.adapter.ScenicTopAdapter
import com.daqsoft.travelCultureModule.resource.fragment.ScenicImageFragment
import java.lang.Exception
import java.util.ArrayList

/**
 * @Description 景点详情顶部图片
 * @ClassName   ScenicTopImagesView
 * @Author      luoyi
 * @Time        2020/4/3 14:37
 */
class ScenicTopImagesView : FrameLayout {

    var mContext: Context? = null
    var binding: LayoutScenicTopImagesBinding? = null

    /**
     * 全景图
     */
    var pannoramaUrl: String? = null

    /**
     * 图片集合
     */
    var images: List<String>? = mutableListOf()

    /**
     * 直播地址
     */
    var liveUrl: String? = null

    var spotsId: String? = ""
    var spotsName: String? = ""
    var count: Int = 0
    var panoramaCover: String? = ""

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        initView()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.layout_scenic_top_images,
            this,
            false
        )
        addView(binding!!.root)
    }

    public fun setData(
        spotid: String,
        spotName: String?,
        panoramaUrl: String?,
        panoramaCover: String?,
        images: List<String>?,
        liveUrl: String,
        fragmentManager: FragmentManager
    ) {
        this.spotsId = spotid
        this.spotsName = spotsName
        this.pannoramaUrl = panoramaUrl
        this.images = images
        this.liveUrl = liveUrl
        this.panoramaCover = panoramaCover
        updateUi(fragmentManager)
    }

    private fun updateUi(fragmentManager: FragmentManager) {
        count = 0
        // 720 图片
        if (!pannoramaUrl.isNullOrEmpty()) {
            count = 1
        }
        // 直播
        if (!liveUrl.isNullOrEmpty()) {
            count = +1
        }
        var imageSize = 0
        if (!images.isNullOrEmpty()) {
            imageSize = images!!.size
        }
        if (count > 0 || imageSize > 2) {
            // 720或者直播存在
            binding?.vImagesTwo?.visibility = View.GONE
            binding?.vScenicImages?.visibility = View.VISIBLE
            // 绑定第一个位置的图片
            if (!pannoramaUrl.isNullOrEmpty()) {
                binding?.ivPanor?.visibility = View.VISIBLE
                if (!panoramaCover.isNullOrEmpty()) {
                    Glide.with(mContext!!)
                        .load(panoramaCover)
                        .centerCrop()
                        .into(binding?.aiImage1!!)
                } else {
                    Glide.with(mContext!!)
                        .load(R.mipmap.provider_scenic_details_720_pic_default)
                        .centerCrop()
                        .into(binding?.aiImage1!!)
                }
                binding?.aiImage1?.onNoDoubleClick {
                    ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
                        .withString("mTitle", "")
                        .withString("html", pannoramaUrl)
                        .navigation()
                }
            } else {
                binding?.ivPanor?.visibility = View.GONE
                if (imageSize > 0) {
                    Glide.with(mContext!!)
                        .load(images!![0])
                        .placeholder(R.mipmap.placeholder_img_fail_240_180)
                        .into(binding?.aiImage1!!)
                }
            }
            // 绑定第二个位置数据
            if (imageSize > 0) {
                var imageUrl = ""
                if (imageSize > 1) {
                    imageUrl = images!![1]
                } else {
                    imageUrl = images!![0]
                }
                Glide.with(mContext!!)
                    .load(imageUrl)
                    .placeholder(R.mipmap.placeholder_img_fail_240_180)
                    .into(binding?.aiImage2!!)
            }
            if (!liveUrl.isNullOrEmpty()) {
                binding?.tvLiveNumber?.visibility = View.VISIBLE
                binding?.tvLive?.visibility = View.VISIBLE
                binding?.aiImageCover2?.visibility = View.VISIBLE
                binding?.aiImage2?.onNoDoubleClick {
                    if (spotsId != null) {
                        // todo 原需求是只有一条直播，跳转直播详情，多条跳转直播列表，但是目前后台只配置一条直播
                        ARouter.getInstance()
                            .build(ARouterPath.SlowLiveModule.SLOW_LIVE_DETAIL_ACTIVITY)
                            .withInt("scenicSpotsId", spotsId!!.toInt())
                            .withString("scenicSpotsName", spotsName)
                            .navigation()
                    }
                }
            } else {
                binding?.tvLive?.visibility = View.GONE
                binding?.tvLiveNumber?.visibility = View.GONE
                binding?.aiImageCover2?.visibility = View.GONE
                binding?.aiImage2?.onNoDoubleClick {
                    ARouter.getInstance().build(MainARouterPath.MAIN_SCENIC_SPOT_IMAGES)
                        .withStringArrayList("mDataImages", ArrayList(images))
                        .withString("name", "")
                        .navigation()
                }
            }
            // 绑定第三个位置图片
            if (imageSize > 0) {
                var imagUrl3: String? = images!![imageSize - 1]
                Glide.with(mContext!!).load(imagUrl3)
                    .placeholder(R.mipmap.placeholder_img_fail_240_180)
                    .into(binding?.aiImage3!!)
                binding?.tvLiveBannerNumber?.text = "共${imageSize}张"
                binding?.aiImage3?.onNoDoubleClick {
                    // 景区点列表
                    ARouter.getInstance().build(MainARouterPath.MAIN_SCENIC_SPOT_IMAGES)
                        .withStringArrayList("mDataImages", ArrayList(images))
                        .withString("name", "")
                        .navigation()
                }
            }
        } else {
            if (imageSize > 0) {
                binding?.vScenicImages?.visibility = View.GONE
                binding?.vImagesTwo?.visibility = View.VISIBLE
                val fragments: MutableList<Fragment> = mutableListOf()
                fragments.clear()
                for (i in images!!.indices) {
                    fragments.add(ScenicImageFragment.newInstance(images!![i], images as MutableList<String>, i))
                }
                if (imageSize > 1) {
                    binding?.vpindicatorIamges?.total = fragments.size
                    binding?.vpindicatorIamges?.binViewPager(binding?.vpTwoImages)
                    binding?.vpindicatorIamges?.visibility = View.VISIBLE
                } else {
                    binding?.vpindicatorIamges?.visibility = View.GONE
                }
                val adapater = ScenicTopAdapter(fragments, fragmentManager)
                binding?.vpTwoImages?.adapter = adapater
                binding?.vpTwoImages?.offscreenPageLimit = fragments.size
                binding?.tvLiveBannerNumber?.text = "共${imageSize}张"
                binding?.aiImage3?.onNoDoubleClick {
                    ARouter.getInstance().build(MainARouterPath.MAIN_SCENIC_SPOT_IMAGES)
                        .withStringArrayList("mDataImages", ArrayList(images))
                        .withString("name", "")
                        .navigation()
                }
            } else {
                binding?.root?.visibility = View.GONE
            }
        }
    }

}