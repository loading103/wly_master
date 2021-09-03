package com.daqsoft.legacyModule.smriti.fragment

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.databinding.FragmentLegacyPicBinding
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import com.squareup.picasso.Picasso
import me.nereo.multi_image_selector.BigImgActivity

/**
 * desc :
 * @author 江云仙
 * @date 2020/4/29 20:02
 */
class LegacyPicFragment :  BaseFragment<FragmentLegacyPicBinding, BaseViewModel>(), OnPageChangeListener {

    var images: MutableList<String> = mutableListOf()
    var onPageChangeListener: OnPageChangedListener? = null
    var imageUrl: String? = null
    var position: Int? = 0

    companion object {
        const val IMAGES: String = "images"
        const val CURRENT_POS: String = "pos"
        const val IMAGE_URL: String = "url"

        fun newInstance(imageUrl: String, list: MutableList<String>, positon: Int): LegacyPicFragment {
            val scenicImageFragment = LegacyPicFragment()
            val bundle = Bundle()
            bundle.putStringArrayList(IMAGES, ArrayList(list))
            bundle.putInt(CURRENT_POS, positon)
            bundle.putString(IMAGE_URL, imageUrl)
            scenicImageFragment.arguments = bundle
            return scenicImageFragment
        }
    }



    override fun getLayout(): Int {
        return R.layout.fragment_legacy_pic
    }


    override fun injectVm(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun initView() {
        getParams()
        try {
//            GlideModuleUtil.loadDqImageWaterMark(imageUrl,context!!,mBinding.cbItemScenicImages)
            Picasso.with(context!!)
                .load(imageUrl)
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .into(mBinding.cbItemScenicImages)
            mBinding.cbItemScenicImages.onNoDoubleClick {
                val intent =
                    Intent(context, BigImgActivity::class.java)
                intent.putExtra("position", position)
                intent.putStringArrayListExtra(
                    "imgList",
                    ArrayList(images)
                )
                startActivity(intent)
            }

        } catch (e: Exception) {
        }

    }

    private fun getParams() {
        try {
            var imagesTemp = arguments?.getStringArrayList(IMAGES)
            if (!imagesTemp.isNullOrEmpty()) {
                images.clear()
                images.addAll(imagesTemp)
            }
            position = arguments?.getInt(CURRENT_POS)
            imageUrl = arguments?.getString(IMAGE_URL)
        } catch (e: Exception) {

        }

    }

    override fun initData() {
    }

    interface OnPageChangedListener {
        fun onPageChanged(index: Int)
    }

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
    }

    override fun onPageSelected(index: Int) {
        if (onPageChangeListener != null) {
            onPageChangeListener?.onPageChanged(index)
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}