package com.daqsoft.travelCultureModule.resource

import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityScenicSpotsImgsBinding
import com.daqsoft.mainmodule.databinding.ItemScenicSpotsImagesBinding
import com.daqsoft.provider.MainARouterPath
import me.nereo.multi_image_selector.BigImgActivity
import java.util.*

/**
 * @Description
 * @ClassName  SpotsImagesActivity
 * @Author      luoyi
 * @Time        2020/4/21 17:15
 */
@Route(path = MainARouterPath.MAIN_SCENIC_SPOT_IMAGES)
class SpotsImagesActivity : TitleBarActivity<ActivityScenicSpotsImgsBinding, BaseViewModel>() {

    @JvmField
    @Autowired
    var mDataImages: ArrayList<String>? = null
    @JvmField
    @Autowired
    var name: String = ""

    override fun getLayout(): Int {
        return R.layout.activity_scenic_spots_imgs
    }

    override fun setTitle(): String {
        return "景点相册"
    }

    override fun injectVm(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun initView() {
        if (!mDataImages.isNullOrEmpty()) {
            var layoumanager = GridLayoutManager(this@SpotsImagesActivity, 2)
            mBinding.recyScenicSpotsImages.adapter = adapter
            mBinding.recyScenicSpotsImages.layoutManager = layoumanager
            adapter.add(mDataImages!!.toMutableList())
        }
    }

    override fun initData() {
    }

    var adapter = object :
        RecyclerViewAdapter<ItemScenicSpotsImagesBinding, String>(R.layout.item_scenic_spots_images) {
        override fun setVariable(mBinding: ItemScenicSpotsImagesBinding, position: Int, item: String) {
            Glide.with(this@SpotsImagesActivity).load(item).placeholder(R.mipmap.placeholder_img_fail_240_180).into(mBinding.imgScenicSpotsOne)
            mBinding.root.onNoDoubleClick {
                // 图片点击
                val intent = Intent(this@SpotsImagesActivity, BigImgActivity::class.java)
                intent.putExtra("position", position)
                intent.putStringArrayListExtra(
                    "imgList",
                    ArrayList(mDataImages)
                )
                startActivity(intent)
            }
        }

    }
}