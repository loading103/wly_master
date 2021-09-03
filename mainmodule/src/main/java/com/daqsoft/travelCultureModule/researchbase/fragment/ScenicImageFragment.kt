package com.daqsoft.travelCultureModule.researchbase.fragment
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemScenicImageBinding
import com.daqsoft.provider.view.convenientbanner.holder.BaseBannerImageHolder
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import me.nereo.multi_image_selector.BigImgActivity
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

/**
 * @Description
 * @ClassName   SenicImageFragment
 * @Author      luoyi
 * @Time        2020/3/31 17:57
 */
class ScenicImageFragment : BaseFragment<ItemScenicImageBinding, BaseViewModel>(), OnPageChangeListener {

    var images: MutableList<String> = mutableListOf()
    var onPageChangeListener: OnPageChangedListener? = null
    var imageUrl: String? = null
    var position: Int? = 0

    companion object {
        const val IMAGES: String = "images"
        const val CURRENT_POS: String = "pos"
        const val IMAGE_URL: String = "url"

        fun newInstance(imageUrl: String, list: MutableList<String>, positon: Int): ScenicImageFragment {
            val scenicImageFragment = ScenicImageFragment()
            val bundle = Bundle()
            bundle.putStringArrayList(IMAGES, ArrayList(list))
            bundle.putInt(CURRENT_POS, positon)
            bundle.putString(IMAGE_URL, imageUrl)
            scenicImageFragment.arguments = bundle
            return scenicImageFragment
        }
    }



    override fun getLayout(): Int {
        return R.layout.item_scenic_image
    }


    override fun injectVm(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun initView() {
        getParams()
        try {
            GlideModuleUtil.loadDqImageWaterMark(imageUrl,mBinding.cbItemScenicImages)
//            Glide.with(context!!).load(imageUrl).placeholder(R.mipmap.placeholder_img_fail_240_180)
//                .into(mBinding.cbItemScenicImages)
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