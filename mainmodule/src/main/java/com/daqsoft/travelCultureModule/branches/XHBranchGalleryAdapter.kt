package com.daqsoft.travelCultureModule.branches

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.MainBranchItemHBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.HomeBranchBean
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

/**
 * @Description 品牌展播的适配器
 * @ClassName   XGalleryAdapter
 * @Author      PuHua
 * @Time        2019/12/9 15:36
 */
class XHBranchGalleryAdapter : PagerAdapter() {

    val menus = mutableListOf<HomeBranchBean>()

    override fun getCount(): Int {
        return menus.size
    }

    override fun isViewFromObject(view: View, ob: Any): Boolean {
        return view === ob
    }

    @SuppressLint("CheckResult")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val mBinding: MainBranchItemHBinding = DataBindingUtil.inflate(
            LayoutInflater.from(container.context),
            R.layout.main_branch_item_h,
            null,
            false
        )
        val homeBranchBean = menus[position]
//        Glide.with(container.context)
//            .load(StringUtil.getImageUrlFill(homeBranchBean.brandImage, 220, 340))
//            .skipMemoryCache(true).into(mBinding.image)
        mBinding.url = homeBranchBean.brandImage
        mBinding.name = homeBranchBean.name
        mBinding.slog = homeBranchBean.slogan
        RxView.clicks(mBinding.root)
              // 1秒内不可重复点击或仅响应一次事件
              .throttleFirst(1, TimeUnit.SECONDS)
              .subscribe {
                  ARouter.getInstance()
                      .build(MainARouterPath.MAIN_BRANCH_DETAIL)
                      .withString("id",homeBranchBean.id.toString())
                      .navigation()
              }

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