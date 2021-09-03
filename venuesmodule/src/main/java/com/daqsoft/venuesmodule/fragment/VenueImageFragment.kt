package com.daqsoft.venuesmodule.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.provider.view.convenientbanner.holder.BaseBannerImageHolder
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.VenueFragImagesBinding
import com.daqsoft.venuesmodule.viewmodel.VenueImagesViewModel
import me.nereo.multi_image_selector.BigImgActivity
import java.util.ArrayList

/**
 * @Description
 * @ClassName   VenueImageFragment
 * @Author      luoyi
 * @Time        2020/3/25 11:34
 */
class VenueImageFragment : BaseFragment<VenueFragImagesBinding, VenueImagesViewModel>(), OnPageChangeListener {

    var images: MutableList<String> = mutableListOf()
    var onPageChangeListener: OnPageChangedListener? = null

    companion object {
        const val IMAGES = "images"
        fun newInstance(images: ArrayList<String>): VenueImageFragment {
            val bundle = Bundle()
            bundle.putStringArrayList(IMAGES, images)
            val venueImageFragment = VenueImageFragment()
            venueImageFragment.arguments = bundle
            return venueImageFragment
        }
    }


    override fun getLayout(): Int {
        return R.layout.venue_frag_images
    }


    override fun injectVm(): Class<VenueImagesViewModel> {
        return VenueImagesViewModel::class.java
    }

    override fun initView() {
        getParams()
        mBinding.cbImages.setPages(object : CBViewHolderCreator {
            override fun createHolder(itemView: View?): Holder<*> {
                return BaseBannerImageHolder(itemView!!)
            }

            override fun getLayoutId(): Int {
                return R.layout.holder_img_base
            }
        }, images).setCanLoop(false).setPointViewVisible(false).setOnItemClickListener {
            val intent =
                Intent(context, BigImgActivity::class.java)
            intent.putExtra("position", it)
            intent.putStringArrayListExtra(
                "imgList",
                ArrayList(images)
            )
            startActivity(intent)
        }
        mBinding.cbImages.onPageChangeListener = this
    }

    /**
     * 获取参数
     */
    private fun getParams() {
        var imagesTemp = arguments?.getStringArrayList(IMAGES)
        images.clear()
        if (!imagesTemp.isNullOrEmpty()) {
            images.addAll(imagesTemp)
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
}