package com.daqsoft.usermodule.ui.invitation.adapter

import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.provider.bean.InviteBean
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ItemMineInviteBinding

/**
 * @Description
 * @ClassName   InviteLsAdapter
 * @Author      luoyi
 * @Time        2020/7/28 15:42
 */
class InviteLsAdapter : RecyclerViewAdapter<ItemMineInviteBinding, InviteBean>(R.layout.item_mine_invite) {
    override fun setVariable(mBinding: ItemMineInviteBinding, position: Int, item: InviteBean) {
        item?.let {
            mBinding.name = it.nickName
            mBinding.imageUrl = it.headUrl
            mBinding.integral = "+${it.rewardPoints}积分"
            mBinding.status ="邀请成功"
            mBinding.time = it.inviteTime
        }
    }
}