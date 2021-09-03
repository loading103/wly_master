package com.daqsoft.travelCultureModule.resource

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemStoryImageViewBinding
import com.daqsoft.provider.bean.HomeBranchBean

/**
 * @Description 下一站的适配器
 * @ClassName   XGalleryAdapter
 * @Author      PuHua
 * @Time        2019/12/9 15:36
 */
class ImageVideoAdapter : PagerAdapter() {

    val menus = mutableListOf<String>()

    override fun getCount(): Int {
        return menus.size
    }

    override fun isViewFromObject(view: View, ob: Any): Boolean {
        return view === ob
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val mBinding: ItemStoryImageViewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(container.context),
            R.layout.item_story_image_view,
            null,
            false)
        val homeBranchBean = menus[position]
        mBinding.url = homeBranchBean

        container.addView(mBinding.root)
        return mBinding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, ob: Any) {
//        super.destroyItem(container, position, ob)
        if (ob != null && ob is View) {
            container.removeView(ob as View?)
        }
    }

}