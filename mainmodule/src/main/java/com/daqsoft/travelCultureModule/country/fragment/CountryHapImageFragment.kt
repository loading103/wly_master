package com.daqsoft.travelCultureModule.country.fragment

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.CountryHapFragImagesBinding
import com.daqsoft.provider.view.convenientbanner.holder.BaseBannerImageHolder
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import me.nereo.multi_image_selector.BigImgActivity
import java.util.ArrayList

/**
 * desc :乡村游图片banner
 * @author 江云仙
 * @date 2020/4/15 16:37
 */
class CountryHapImageFragment : BaseFragment<CountryHapFragImagesBinding, BaseViewModel>, OnPageChangeListener {

    var images: MutableList<String> = mutableListOf()
    var onPageChangeListener: OnPageChangedListener? = null

    constructor(list: MutableList<String>) {
        this.images = list
    }

    constructor(list: MutableList<String>, onPageChange: OnPageChangedListener) {
        this.images = list
        this.onPageChangeListener = onPageChange
    }

    override fun getLayout(): Int {
        return R.layout.country_hap_frag_images
    }


    override fun injectVm(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun initView() {
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