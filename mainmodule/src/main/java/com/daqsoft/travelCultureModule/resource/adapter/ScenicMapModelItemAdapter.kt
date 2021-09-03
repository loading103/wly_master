package com.daqsoft.travelCultureModule.resource.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemScenicMapModelBinding
import com.daqsoft.mainmodule.databinding.ItemScenicMapModelNewBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.ZARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.ScenicBean
import com.daqsoft.travelCultureModule.resource.ScenicMapModelActivity
import com.jakewharton.rxbinding2.view.RxView
import java.lang.Exception
import java.lang.StringBuilder
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit


/**
 * @Description 地图模式里面的活动item的适配器
 * @ClassName   XGalleryAdapter
 * @Author      PuHua
 * @Time        2019/12/9 15:36
 */
class ScenicMapModelItemAdapter(context: Context) : PagerAdapter() {
    val mContext = context
    /**
     * 自己的位置
     */
    var latLng: LatLng? = null


    val menus = mutableListOf<ScenicBean>()

    override fun getCount(): Int {
        return menus.size
    }

    override fun isViewFromObject(view: View, ob: Any): Boolean {
        return view === ob
    }

    @SuppressLint("CheckResult")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val mBinding: ItemScenicMapModelNewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(container.context),
            R.layout.item_scenic_map_model_new,
            null,
            false
        )
        val item = menus[position]
        if (item != null) {
            val images = item.images.split(",")
            var imgUrl = ""
            if (images.isNotEmpty()) {
                imgUrl = images[0]
            }
            mBinding.url = imgUrl
            mBinding.placeholder = container.context.getDrawable(R.mipmap.placeholder_img_fail_240_180)
            mBinding.name = item.name
            Log.e("name=", "" + item.name)
            mBinding.address = item.address
            //营业时间
            var  sb=StringBuilder()
            if(!item.openStartTime.isNullOrEmpty()){
                sb.append("营业时间："+item.openStartTime)
            }else{
                sb.append("营业时间：")
            }
            if(!item.openEndTime.isNullOrEmpty()){
                if(item.openStartTime.isNullOrEmpty()){
                    sb.append(item.openEndTime+"关闭")
                }else{
                    sb.append("-"+item.openEndTime)
                }
            }
            mBinding.txtOpenTime.text=sb.toString()
            if(item.openEndTime.isNullOrEmpty() && item.openStartTime.isNullOrEmpty()){
                mBinding.txtOpenTime.visibility=View.INVISIBLE
            }


            var adress = ""
            if (!item.latitude.isNullOrEmpty() &&  !item.longitude.isNullOrEmpty() &&   (mContext as ScenicMapModelActivity).lat>0 && (mContext as ScenicMapModelActivity).lon> 0 ) {
                var latLng = LatLng((mContext as ScenicMapModelActivity).lat.toDouble(), (mContext as ScenicMapModelActivity).lon.toDouble())
                var end = LatLng(item.latitude!!.toDouble(), item.longitude!!.toDouble())
                var dis = AMapUtils.calculateLineDistance(latLng, end)
                var disDistance = if (dis > 1000) {
                    val df = DecimalFormat("0.00")
                    df.format(dis / 1000) + "KM | "
                } else {
                    dis.toInt().toString() + "M | "
                }
                adress = "距离" + disDistance + item?.regionName
            } else {
                adress = item?.regionName
            }
            if(!item.address.isNullOrEmpty()){
                adress=adress+"·"+item.address
            }
            mBinding.txtAdress.text=adress

            if(!item.consumPerson.isNullOrEmpty()){
                mBinding.tvPrice.text="人均"+item.consumPerson+"元"
            }else{
                mBinding.tvPrice.visibility=View.GONE
            }
            if(!item.activityInfo.isNullOrEmpty() && item.activityInfo.toString()!="0"){
                mBinding.tvActivity.text="在线活动"+item.activityInfo+"个"
            }else{
                mBinding.tvActivity.visibility=View.GONE
            }

            if(mBinding.tvPrice.text.isEmpty() && mBinding.tvScore.text.isEmpty()   &&mBinding.tvActivity.text.isEmpty()  ){
                mBinding.llRoot.visibility=View.GONE
            }else{
                mBinding.llRoot.visibility=View.VISIBLE
            }




            RxView.clicks(mBinding.txtGoTopInfo)
                // 1秒内不可重复点击或仅响应一次事件
                .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                    run {
                        //跳转对应页面
//                    ARouter.getInstance()
//                        .build(MainARouterPath.MAIN_HOT_ACITITY)
//                        .withString("id", item.id)
//                        .withString("classifyId", item.classifyId)
//                        .navigation()
                        item.resourceType?.let {
                            when (item.resourceType!!) {
                                ResourceType.CONTENT_TYPE_SCENERY -> {
                                    // 景区详情
                                    ARouter.getInstance()
                                        .build(MainARouterPath.MAIN_SCENIC_DETAIL)
                                        .withString("id", item.id)
                                        .navigation()
                                }
                                ResourceType.CONTENT_TYPE_VENUE -> {
                                    //场馆详情
                                    ARouter.getInstance().build(ARouterPath.VenuesModule.VENUES_DETAILS_ACTIVITY)
                                        .withString("id", item.id)
                                        .navigation()
                                }
                                ResourceType.CONTENT_TYPE_HOTEL -> {
                                    // 酒店详情
                                    try {
                                        ARouter.getInstance()
                                            .build(ZARouterPath.ZMAIN_HOTEL_DETAIL)
                                            .withString("id", item.id)
                                            .navigation()
                                    } catch (e: Exception) {
                                    }

                                }
                                ResourceType.CONTENT_TYPE_RESTAURANT -> {
                                    // 美食详情
                                    try {
                                        ARouter.getInstance().build(MainARouterPath.MAIN_FOOD_DETAIL)
                                            .withString("id", item.id)
                                            .navigation()
                                    } catch (e: Exception) {
                                    }

                                }
                                ResourceType.CONTENT_TYPE_COUNTRY -> {
                                    // 乡村详情
                                    try {
                                        ARouter.getInstance().build(ARouterPath.CountryModule.COUNTRY_COUNTRY_DETAIL_ACTIVITY)
                                            .withString("id", item.id)
                                            .navigation()

                                    } catch (e: Exception) {
                                    }
                                }
                                ResourceType.CONTENT_TYPE_AGRITAINMENT -> {
                                    // 农家乐详情
                                    try {
                                        ARouter.getInstance().build(ARouterPath.CountryModule.COUNTRY_HAPPINESS_DETAIL)
                                            .withString("id", item.id)
                                            .navigation()

                                    } catch (e: Exception) {
                                    }
                                }
                                ResourceType.CONTENT_TYPE_ENTERTRAINMENT -> {
                                    // 娱乐场所详情
                                    try {
                                        ARouter.getInstance().build(MainARouterPath.MAIN_PLAYGROUND_DETAIL)
                                            .withString("id", item.id.toString())
                                            .navigation()

                                    } catch (e: Exception) {
                                    }
                                }
                                ResourceType.CONTENT_TYPE_RESEARCH_BASE -> {
                                    // 研学基地详情
                                    try {
                                        ARouter.getInstance()
                                            .build(MainARouterPath.RESEARCH_DETAIL)
                                            .withString("id", item.id.toString())
                                            .navigation()

                                    } catch (e: Exception) {
                                    }
                                }
                                ResourceType.CONTENT_TYPE_SPECIALTY -> {
                                    // 特产详情
                                    try {
                                        ARouter.getInstance().build(MainARouterPath.MAIN_SPECIAL_DETAIL)
                                            .withString("id", item.id.toString())
                                            .navigation()

                                    } catch (e: Exception) {
                                    }
                                }
                                else -> {

                                }
                            }
                        }

                    }
                }
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

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }
}