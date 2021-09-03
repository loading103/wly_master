package com.daqsoft.travelCultureModule.sidetour.adapter

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemClubActivityBinding
import com.daqsoft.mainmodule.databinding.ItemSideTourParkingImgBinding
import com.daqsoft.travelCultureModule.clubActivity.bean.ClubActivityBean
import com.jakewharton.rxbinding2.view.RxView
import me.nereo.multi_image_selector.BigImgActivity
import java.util.ArrayList
import java.util.concurrent.TimeUnit

/**
 * @Description
 * @ClassName   ImageRecyAdapter
 * @Author      luoyi
 * @Time        2020/3/19 19:34
 */
class ImageRecyAdapter(context: Context) :
    RecyclerViewAdapter<ItemSideTourParkingImgBinding, String>(
        R.layout.item_side_tour_parking_img
    ) {
    var context = context
    override fun setVariable(mBinding: ItemSideTourParkingImgBinding, position: Int, item: String) {
        mBinding?.imageUrl = item
        mBinding?.placeHolder = context.getDrawable(R.mipmap.placeholder_img_fail_240_180)
        RxView.clicks(mBinding.imgRecy)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                val intent =
                    Intent(context, BigImgActivity::class.java)
                intent.putExtra("position", position)
                intent.putStringArrayListExtra(
                    "imgList",
                    ArrayList(getData())
                )
                context.startActivity(intent)
            }
    }
}