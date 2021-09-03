package com.daqsoft.volunteer.volunteer.adapter

import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.bean.MineServiceBean
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.databinding.VolunteerCenterMenuItemBinding
import com.daqsoft.volunteer.utils.JumpUtils

/**
 *@package:com.daqsoft.volunteer.volunteer.adapter
 *@date:2020/6/28 11:30
 *@author: caihj
 *@des:TODO
 **/
class VolunteerMenuAdapter:RecyclerViewAdapter<VolunteerCenterMenuItemBinding,MineServiceBean>(R.layout.volunteer_center_menu_item) {
    override fun setVariable(mBinding: VolunteerCenterMenuItemBinding, position: Int, item: MineServiceBean) {
            mBinding.ivIcon.setImageResource(item.icon)
            mBinding.tvTitle.text = item.name
            mBinding.root.onNoDoubleClick {
                JumpUtils.volunteerCenterMenu(item.type)
            }
    }
}