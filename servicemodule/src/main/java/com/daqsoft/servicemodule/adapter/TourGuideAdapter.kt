package com.daqsoft.servicemodule.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.daqsoft.android.scenic.servicemodule.R
import com.daqsoft.android.scenic.servicemodule.databinding.ItemTourGuideBinding
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.adapter.setImageUrl
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.servicemodule.bean.TourGuideBean


/**
 * desc :导游列表适配器
 * @author 江云仙
 * @date 2020/4/2 14:12
 * @version 1.0.0
 * @since JDK 1.8
 */
class TourGuideAdapter : RecyclerViewAdapter<ItemTourGuideBinding, TourGuideBean>(
    R.layout.item_tour_guide
) {
    @SuppressLint("SetTextI18n")
    override fun setVariable(mBinding: ItemTourGuideBinding, position: Int, item: TourGuideBean) {
        mBinding.imgHead.visibility = if (item.photo.isNotEmpty()) View.VISIBLE else View.GONE
        if (item.photo.isNotEmpty()) {
            setImageUrl(mBinding.imgHead, item.photo, AppCompatResources.getDrawable(BaseApplication.context, R.drawable.placeholder_img_fail_h300), 5)
        }
        mBinding.tvUserName.text = "" + item.name
        if(item.company.isNotEmpty()){
            mBinding.tvCompany.text = "所属公司：" + item.company
        }else{
            mBinding.tvCompany.visibility = View.GONE
        }
        if(item.level.isNotEmpty()){
            mBinding.tvLevel.text = "导游等级：" + item.level
        }else{
            mBinding.tvLevel.visibility = View.GONE
        }
        mBinding.tvNumber.text = "导游证号：" + item.certificateNo
        mBinding.tvLanguage.text = "语言种类：" + item.language
        setLeftDrawable(mBinding.tvUserName, item.gender)


    }

    private fun setLeftDrawable(tvUserName: TextView, gender: String) {
        var leftDrawable: Drawable? = null
        when (gender) {
            "男性" -> {
                leftDrawable = BaseApplication.context.resources.getDrawable(R.mipmap.service_guide_result_icon_male)

            }
            "女性" -> {
                leftDrawable = BaseApplication.context.resources.getDrawable(R.mipmap.service_guide_result_icon_female)
            }
        }
        leftDrawable?.setBounds(0, 0, leftDrawable.minimumWidth, leftDrawable.minimumHeight) // left, top, right, bottom
        tvUserName.setCompoundDrawables(null, null, leftDrawable, null);  // left, top, right, bottom
    }

}