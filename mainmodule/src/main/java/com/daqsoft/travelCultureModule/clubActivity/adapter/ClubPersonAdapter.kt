package com.daqsoft.travelCultureModule.clubActivity.adapter

import android.content.Context
import android.text.Html
import androidx.appcompat.content.res.AppCompatResources
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.adapter.setCenterCropImage
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemClubPersonBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.travelCultureModule.clubActivity.bean.ClubPersonBean


class ClubPersonAdapter(context: Context) :
    RecyclerViewAdapter<ItemClubPersonBinding, ClubPersonBean>(
        R.layout.item_club_person
    ) {
    override fun setVariable(mBinding: ItemClubPersonBinding, position: Int, item: ClubPersonBean) {
        setCenterCropImage(
            mBinding.ivCiPerson, item.image, AppCompatResources.getDrawable(
                BaseApplication.context, R.mipmap.mine_profile_photo_default
            ), true
        )
        mBinding.tvCiPersonName.text = item.name
        mBinding.tvCiPersonPosition.text = if (item.duty.isNullOrEmpty()) "成员" else item.duty
        mBinding.tvCiPersonContent.text = item.summary
        mBinding.llCiPerson.setOnClickListener {
            ARouter.getInstance().build(MainARouterPath.MAIN_CLUB_PERSON_INFO)
                .withString("id", item.id.toString())
                .navigation()
        }

    }


}