package com.daqsoft.venuesmodule.activity

import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.provider.ARouterPath
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ActivityVenuesBigImageBinding
import com.daqsoft.venuesmodule.databinding.ActivityVenuesImagesBinding
import com.daqsoft.venuesmodule.fragment.VenueImageFragment
import me.nereo.multi_image_selector.pic.MyImageVpAdapter
import java.util.ArrayList

/**
 * @Description 场馆相册大图模式
 * @ClassName   VenuesImagesActivity
 * @Author      luoyi
 * @Time        2020/3/26 10:33
 */
@Route(path = ARouterPath.VenuesModule.VENUES_BIG_IAMGE_ACTIVITY)
class VenuesBigImageActivity : TitleBarActivity<ActivityVenuesBigImageBinding, BaseViewModel>() {

    @JvmField
    @Autowired
    var name: String = ""
    @JvmField
    @Autowired
    var position: Int = 0
    @JvmField
    @Autowired
    var totalSize: Int = 0
    @JvmField
    @Autowired
    var list: ArrayList<String>? = null

    override fun getLayout(): Int {
        return R.layout.activity_venues_big_image
    }

    override fun setTitle(): String {
        return "查看图片"
    }

    override fun injectVm(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun initView() {
        if (!list.isNullOrEmpty()) {
            var adapater = MyImageVpAdapter(list, this@VenuesBigImageActivity)
            mBinding.vpImages.adapter = adapater
            mBinding.vpImages.addOnPageChangeListener(object:ViewPager.OnPageChangeListener{
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                    mBinding.currentIndex = (position+1)
                }

            })
        }
    }

    override fun initData() {
        mBinding.total = totalSize
        mBinding.currentIndex = (position+1)
        mBinding.title = name
    }

}