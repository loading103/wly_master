package com.daqsoft.volunteer.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.StringUtil.getImageUrl
import com.daqsoft.baselib.utils.StringUtil.getVideoCoverUrl
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.databinding.MuiltImageNewViewBinding
import com.daqsoft.volunteer.databinding.MuiltImageViewItemBinding

/**
 * @Description 不定图片个数显示控件
 * @ClassName MuitImageView
 * @Author PuHua
 * @Time 2020/1/17 15:01
 * @Version 2.0
 */
class MuiltImage2View : RelativeLayout {
    var binding: MuiltImageNewViewBinding? = null

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        initView(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    /**
     * 初始化页面
     *
     * @param context
     */
    private fun initView(context: Context) {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.muilt_image_new_view,
            this,
            false
        )
        addView(binding!!.root)
    }

    fun setImages(
        images: MutableList<String>,
        videoUrl: String?
    ) {
        var isHaveVideo = false
        var size = images.size
        if (videoUrl != null && videoUrl !== "") {
            isHaveVideo = true
            size += 1
        }
        if (size == 1) {
            binding!!.aiImageOne.visibility = View.VISIBLE
            if (isHaveVideo) {
                binding!!.ivImageOnePlay.visibility = View.VISIBLE
                setVideoImage(videoUrl, binding!!.aiImageOne)
            } else {
                binding!!.ivImageOnePlay.visibility = View.GONE
                setCover(images[0])
            }
            binding!!.vTwoImage.visibility = View.GONE
        } else if (size == 2) {
            binding!!.vTwoImage.visibility = View.VISIBLE
            binding!!.aiImageOne.visibility = View.GONE
            binding!!.aiImage1.visibility = View.VISIBLE
            binding!!.aiImage2.visibility = View.VISIBLE
            if (isHaveVideo) {
                binding!!.ivImage1Play.visibility = View.VISIBLE
                setVideoImage(videoUrl, binding!!.aiImage1)
                setImage(images[0], binding!!.aiImage2)
            } else {
                binding!!.ivImage1Play.visibility = View.GONE
                setImage(images[0], binding!!.aiImage1)
                setImage(images[1], binding!!.aiImage2)
            }
        } else if (size >= 3) run {
            binding!!.aiImageOne.visibility = View.GONE
            binding!!.vTwoImage.visibility = View.GONE
            binding!!.rvImages.visibility = View.VISIBLE
            if(isHaveVideo){
                if (videoUrl != null) {
                    images.add(0,videoUrl)
                }
            }
            val imageAdapter = object :
                RecyclerViewAdapter<MuiltImageViewItemBinding, String>(R.layout.muilt_image_view_item) {
                override fun setVariable(
                    mBinding: MuiltImageViewItemBinding,
                    position: Int,
                    item: String
                ) {
                    if(isHaveVideo && position == 0){
                        mBinding.ivCiPlay.visibility = View.VISIBLE
                        setVideoImage(item,mBinding.ivImage)
                    }else{
                        mBinding.ivCiPlay.visibility = View.GONE
                        setImage(item,mBinding.ivImage)
                    }
                    if(size > 9 && position == 8){
                        mBinding.tvNum.visibility = View.VISIBLE
                        mBinding.tvNum.text = size.toString()
                    }else{
                        mBinding.tvNum.visibility = View.GONE
                    }
                }

            }
            binding!!.rvImages.apply {
                layoutManager = GridLayoutManager(context,3)
                adapter = imageAdapter
            }
                val temp = mutableListOf<String>()
               for (item in images.subList(0,9)){
                   if (item != null) {
                       temp.add(item)
                   }
               }
             imageAdapter.add(temp)
        } else {
            binding!!.root.visibility = View.GONE
        }
    }


    private fun setCover(url: String?) {
        Glide.with(binding!!.aiImageOne)
            .load(getImageUrl(url, 670, 336))
            .placeholder(R.mipmap.placeholder_img_fail_240_180)
            .into(binding!!.aiImageOne)
    }

    private fun setImage(url: String?, view: ImageView?) {
        Glide.with(view!!)
            .load(url)
            .placeholder(R.mipmap.placeholder_img_fail_240_180)
            .into(view)
    }

    @SuppressLint("CheckResult")
    fun setVideoImage(url: String?, view: ImageView?) {
        Glide.with(binding!!.aiImageOne)
            .load(getVideoCoverUrl(url!!))
            .placeholder(R.mipmap.placeholder_img_fail_240_180)
            .into(view!!)
    }
}