package com.daqsoft.legacyModule.mine.adpter

import android.content.Context
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.bean.FansBean
import com.daqsoft.legacyModule.bean.LegacyHeritageItemListBean
import com.daqsoft.legacyModule.bean.LegacyHeritagePeopleListBean
import com.daqsoft.legacyModule.bean.LegacyWatchStoryListBean
import com.daqsoft.legacyModule.databinding.ActivityItemFansBinding
import com.daqsoft.legacyModule.databinding.LegacyModuleItemHeritageBinding
import com.daqsoft.legacyModule.databinding.LegacyModuleItemHeritagePeopleBinding
import com.daqsoft.legacyModule.databinding.LegacyModuleItemWatchStoryBinding
import com.daqsoft.legacyModule.smriti.util.TextFontUtil
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.utils.DividerTextUtils
import java.lang.StringBuilder


/**
 * @des    非遗粉丝关注 adapter
 * @class  LegacyHeritageAdapter
 * @author Caihj
 * @date   2020-5-18 14:22
 *
 */
class LegacyFansAdapter(context: Context, type: String) : RecyclerViewAdapter<ActivityItemFansBinding, FansBean>
    (R.layout.activity_item_fans) {
    private val mContext: Context = context
    private val mType = type
    var onItemClick: OnItemClickListener? = null

    override fun setVariable(mBinding: ActivityItemFansBinding, position: Int, item: FansBean) {
        mBinding.tvName.text = item.nickName
        if (!item.fansTime.isNullOrEmpty()) {
            mBinding.tvTime.text = item.fansTime
        }
        if (!item.heritagePeopleType.isNullOrEmpty()) {
            mBinding.tvType.text = item.heritagePeopleType
        } else {
            mBinding.tvType.visibility = View.GONE
        }
        if (item.fansStatus == 1) {
            mBinding.tvFoucs.text = "取消关注"
        } else {
            mBinding.tvFoucs.text = "关注"
        }
//        if(item.fansStatus == 1 && mType =="pk" ){
//            mBinding.tvFoucs.visibility = View.GONE
//        }
        if (mType == "pk") {
            mBinding.tvTitle.visibility = View.VISIBLE
            mBinding.tvTitle.text = item.title
            if (!item.storyType.isNullOrEmpty())
                mBinding.tvType.visibility = View.VISIBLE
            mBinding.tvType.text = item.storyType
            mBinding.tvTime.text = item.createDate
        }
        Glide.with(mContext)
            .load(item.headUrl)
            .placeholder(R.mipmap.mine_profile_photo_default)
            .into(mBinding.rvHead)
        if (item.heritagePeopleId.isNullOrEmpty()) {
            mBinding.tvFoucs.visibility = View.GONE
        } else {
            mBinding.tvFoucs.visibility = View.VISIBLE

        }
        mBinding.tvFoucs.onNoDoubleClick {
            if (item.fansStatus == 1) {
                onItemClick!!.onItemClick(item.heritagePeopleId, position, false)
            } else {
                onItemClick!!.onItemClick(item.heritagePeopleId, position, true)
            }
        }
        mBinding.root.onNoDoubleClick {
            if (item.heritagePeopleId.isNullOrEmpty()) {
                ToastUtils.showMessage("该用户不是非遗传承人,不能查看详情!")
            } else {
                ARouter.getInstance().build(ARouterPath.LegacyModule.LEGACY_PEOPLE_DETAIL_ACTIVITY)
                    .withString("id", item.heritagePeopleId)
                    .navigation()
            }
        }
    }


    override fun payloadUpdateUi(mBinding: ActivityItemFansBinding, position: Int, payloads: MutableList<Any>) {
        if (payloads[0] == "updateFocus") {
            try {
                if (getData()[position].fansStatus == 1) {
                    mBinding.tvFoucs.text = "取消关注"
                } else {
                    mBinding.tvFoucs.text = "关注"
                }
            } catch (e: Exception) {

            }
        }
    }

    fun notifyFocusStatus(postion: Int) {
        try {
            if (!getData().isNullOrEmpty()) {
                getData()[postion].fansStatus = 1
                notifyItemChanged(postion, "updateFocus")
            }
        } catch (e: Exception) {

        }
    }

    interface OnItemClickListener {
        fun onItemClick(id: String, position: Int, status: Boolean)
    }
}