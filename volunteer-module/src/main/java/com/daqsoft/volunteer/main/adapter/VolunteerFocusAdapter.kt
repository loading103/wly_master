package com.daqsoft.volunteer.main.adapter

import android.content.Context
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.getRealImages
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.bean.VolunteerTeamBean
import com.daqsoft.volunteer.databinding.VolunteerFocusItemBinding
import com.daqsoft.volunteer.utils.StringUtils


/**
 * @des    我的关注 adapter
 * @class  LegacyWatchStoryAdapter
 * @author caihj
 * @date   2020-6-2 14:16
 *
 */
internal open class VolunteerFocusAdapter(val context:Context) : RecyclerViewAdapter<VolunteerFocusItemBinding, VolunteerTeamBean>
    (R.layout.volunteer_focus_item) {
    override fun setVariable(mBinding: VolunteerFocusItemBinding, position: Int, item: VolunteerTeamBean) {
        Glide.with(context).load(item.images.getRealImages()).placeholder(R.mipmap.placeholder_img_fail_h158).into(mBinding.ivImg)
        mBinding.tvTime.text = "${item.createTime} 发布了1条${StringUtils.getVolunteerTypeName(item.type)}"
        mBinding.tvName.text = item.teamName
        Glide.with(context).load(item.teamLogo).placeholder(R.mipmap.mine_profile_photo_default).into(mBinding.ivHeader)
        mBinding.tvContent.text = item.content
        mBinding.tvType.text = StringUtils.getVolunteerTypeName(item.type)
        mBinding.root.onNoDoubleClick {
//            ARouter.getInstance()
//                .build(ARouterPath.LegacyModule.LEGACY_WORKS_DETAIL)
//                .withInt("type",0)
//                .withString("id",item.id)
//                .navigation()
        }
    }
}