package com.daqsoft.travelCultureModule.citycard.adapter
import android.content.Context
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.adapter.setImageUrlqwx
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemMddListBinding
import com.daqsoft.mainmodule.databinding.ItemPlaygroundLsBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.BrandMDD

class CityListAdapter : RecyclerViewAdapter<ItemMddListBinding, BrandMDD> {

    var context: Context? = null
    var selfLocation: LatLng? = null
    var region: String? = ""
    var listData:MutableList<BrandMDD>?=null

    constructor(context: Context) : super(R.layout.item_mdd_list) {
        this.context = context
    }

    override fun setVariable(mBinding: ItemMddListBinding, position: Int, item: BrandMDD) {

        mBinding.root.setOnClickListener {
            ARouter.getInstance().build(MainARouterPath.MAIN_MDD_CITY_INFO)
                .withString("id",getData()[position].id.toString())
                .navigation()
        }


        if(item.images!="") {
            setImageUrlqwx(
                mBinding.ivMddLogo, item.images.split(",")[0], AppCompatResources.getDrawable(
                    BaseApplication.context, R.drawable.placeholder_img_fail_240_180), 5)
        }else{
            setImageUrlqwx(
                mBinding.ivMddLogo,item.images, AppCompatResources.getDrawable(
                    BaseApplication.context, R.drawable.placeholder_img_fail_240_180), 5)
        }
        if(item.videoUrl==""){
            mBinding.ivMddPlay.visibility= View.GONE
        }
        if(item.siteLabelNames==""){
            mBinding.tvMddSign.visibility= View.INVISIBLE
        }else{
            mBinding.tvMddSign.visibility= View.VISIBLE
            mBinding.tvMddSign.text=item.siteLabelNames
        }
        mBinding.tvMddName.text= item.name
        if(item.slogan=="")
            mBinding.tvMddIntroduce.visibility= View.GONE
        mBinding.tvMddIntroduce.text=item.slogan
        if(item.scenicNameStr.length>=20) {
            mBinding.tvMddContent.text = item.scenicNameStr.subSequence(0, 20).toString() + "...等" + item.scenicCount + "个玩乐点"
        }else if(item.scenicNameStr==""){
            // tvContent.visibility=View.GONE
        }else{
            mBinding.tvMddContent.text = item.scenicNameStr  + item.scenicCount + "个玩乐点"
        }
    }

}