package com.daqsoft.venuesmodule.activity

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ActivityVenuesImagesBinding
import com.daqsoft.venuesmodule.databinding.ItemVenueImagesBinding
import java.util.ArrayList

/**
 * @Description
 * @ClassName   VenuesImagesActivity
 * @Author      luoyi
 * @Time        2020/3/26 10:33
 */
@Route(path = ARouterPath.VenuesModule.VENUES_IMAGES_ACTIVITY)
class VenuesImagesActivity : TitleBarActivity<ActivityVenuesImagesBinding, BaseViewModel>() {

    @JvmField
    @Autowired
    var mDataImages: ArrayList<String>? = null
    @JvmField
    @Autowired
    var name: String = ""

    override fun getLayout(): Int {
        return R.layout.activity_venues_images
    }

    override fun setTitle(): String {
        return "场馆相册"
    }

    override fun injectVm(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun initView() {
        if (!mDataImages.isNullOrEmpty()) {
            var layoumanager = GridLayoutManager(this@VenuesImagesActivity, 2)
            mBinding.recyVenuesImages.adapter = adapter
            mBinding.recyVenuesImages.layoutManager = layoumanager
            adapter.add(mDataImages!!.toMutableList())
        }
    }

    override fun initData() {
    }

    var adapter = object :
        RecyclerViewAdapter<ItemVenueImagesBinding, String>(R.layout.item_venue_images) {
        override fun setVariable(mBinding: ItemVenueImagesBinding, position: Int, item: String) {
            Glide.with(this@VenuesImagesActivity).load(item).placeholder(R.mipmap.placeholder_img_fail_240_180).into(mBinding.imgVenueOne)
            mBinding.root.onNoDoubleClick {
                ARouter.getInstance().build(ARouterPath.VenuesModule.VENUES_BIG_IAMGE_ACTIVITY)
                    .withInt("position", position)
                    .withInt("totalSize", getData().size)
                    .withString("name", name)
                    .withStringArrayList("list",mDataImages)
                    .navigation()
            }
        }

    }
}