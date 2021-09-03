package com.daqsoft.servicemodule.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.daqsoft.android.scenic.servicemodule.R
import com.daqsoft.android.scenic.servicemodule.databinding.ItemTravelAgencyBinding
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.SystemHelper
import com.daqsoft.provider.utils.MapNaviUtils
import com.daqsoft.servicemodule.bean.TravelAgencyBean
import com.daqsoft.servicemodule.uitils.DrawableUtil
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

/**
 * desc :旅行社
 * @author 江云仙
 * @date 2020/4/2 19:23
 * @version 1.0.0
 * @since JDK 1.8
 */
 class TravelAgencyAdapter(var mContext:Context): RecyclerViewAdapter<ItemTravelAgencyBinding, TravelAgencyBean> ( R.layout.item_travel_agency
) {
    @SuppressLint("SetTextI18n", "CheckResult")
    override fun setVariable(mBinding: ItemTravelAgencyBinding, position: Int, item: TravelAgencyBean) {
        if (item.address.isNotEmpty()){
            mBinding.rvGuide.visibility= View.VISIBLE
            mBinding.tvContactAddress.text="联系地址："+item.address

            if(!item.latitude.isNullOrEmpty()&&!item.longitude.isNullOrEmpty()){
                mBinding.imgGuide.setImageResource(R.mipmap.service_agency_button_guide_highlighted)
                //地址导航点击事件
                RxView.clicks(mBinding.rvGuide)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        //去到地图模式
                        MapNaviUtils.openGaoDeNavi(mContext,
                            0.0,
                            0.0,null,
                            item.latitude.toDouble(),
                            item.longitude.toDouble(),item.name)                    }

            }else{
                mBinding.imgGuide.visibility = View.GONE
            }
        }
        if (item.contactNumber.isNotEmpty()){
            mBinding.tvPhone.visibility= View.VISIBLE
            val phone_highlighted = BaseApplication.context.resources.getDrawable(R.mipmap.service_agency_button_phone_highlighted)
            DrawableUtil.rightDrawable(phone_highlighted,mBinding.tvPhone)
            //拨打电话点击事件
            RxView.clicks(mBinding.tvPhone)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    // 未处理权限，统一处理
                    SystemHelper.callPhone(mContext, item.contactNumber)
                }

        }else{
            mBinding.tvPhone.visibility= View.GONE
//            val phone_normal = BaseApplication.context.resources.getDrawable(R.mipmap.service_agency_button_phone_normal)
//            DrawableUtil.rightDrawable(phone_normal,mBinding.tvPhone)
        }

        mBinding.tvAddress.text=item.name

        mBinding.tvPhone.text="联系电话："+item.contactNumber
    }
}