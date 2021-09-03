package com.daqsoft.travelCultureModule.clubActivity

import android.content.Context
import android.view.View
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication.Companion.context
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemClubBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.travelCultureModule.clubActivity.bean.ClubBean
import java.lang.StringBuilder

class ClubAdapter(context: Context, callback: CallBack) :
    RecyclerViewAdapter<ItemClubBinding, ClubBean>(
        R.layout.item_club
    ) {
    var callback = callback
    override fun setVariable(mBinding: ItemClubBinding, position: Int, item: ClubBean) {
        mBinding.tvClubTitle.setText(item.name)
        if (item.summary.equals("")) {
            mBinding.tvClubContent.visibility = View.GONE
        } else {
            mBinding.tvClubContent.setText(item.summary)
        }
        mBinding.tvClubType.setText(item.type)
        var clubfans: String? = DividerTextUtils.convertDotString(
            StringBuilder(),
            if (item.fansNum.isNullOrEmpty() || item.fansNum.toInt() == 0) {
                ""
            } else {
                item.fansNum + "粉丝"
            }, if (item.lookNum.isNullOrEmpty() || item.lookNum.toInt() == 0) {
                ""
            } else {
                item.lookNum + "浏览"
            }
        )
        if (!clubfans.isNullOrEmpty()) {
            mBinding.tvClubFans.setText(clubfans)
            mBinding.tvClubFans.visibility = View.VISIBLE
        } else {
            mBinding.tvClubFans.visibility = View.GONE
        }
        Glide.with(context!!)
            .load(StringUtil.getDqImageUrl(item.image.getRealImages(), 670, 292))
            .placeholder(R.mipmap.placeholder_img_fail_240_180)
            .into(mBinding.ivClubHead)
        mBinding.llItemClub.setOnClickListener {
            ARouter.getInstance().build(MainARouterPath.MAIN_CLUB_INFO)
                .withInt("id", item.id)
                .navigation()
        }
        if (item.resourceFansStatus.fansStaus) {
            mBinding.tvClubGuanzhu.text = "已关注"
            mBinding.tvClubGuanzhu.background = getDrawable(context, R.drawable.club_guanzhu_false);
        } else {
            mBinding.tvClubGuanzhu.text = "关注"
            mBinding.tvClubGuanzhu.background = getDrawable(context, R.drawable.club_guanzhu_true);
        }
        mBinding.tvClubGuanzhu.setOnClickListener {
            if (item.resourceFansStatus.fansStaus) {
                callback.postData(item.id.toString(), false)
                item.resourceFansStatus.fansStaus = false
                mBinding.tvClubGuanzhu.text = "关注"
                mBinding.tvClubGuanzhu.background =
                    getDrawable(context, R.drawable.club_guanzhu_true);
            } else {
                callback.postData(item.id.toString(), true)
                item.resourceFansStatus.fansStaus = true
                mBinding.tvClubGuanzhu.text = "已关注"
                mBinding.tvClubGuanzhu.background =
                    getDrawable(context, R.drawable.club_guanzhu_false);
            }
        }
    }

    interface CallBack {
        fun postData(id: String, guanzhu: Boolean)
    }
}

internal fun String.getRealImages(): String {
    if (!this.isNullOrEmpty()) {

        if (!this.contains(","))
            return this

        val imgList = this.split(",")
        if (!imgList.isNullOrEmpty()) {
            return imgList[0]
        }
    }
    return ""
}