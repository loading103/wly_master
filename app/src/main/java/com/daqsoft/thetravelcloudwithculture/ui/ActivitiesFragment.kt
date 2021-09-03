package com.daqsoft.thetravelcloudwithculture.ui

import androidx.lifecycle.ViewModelProvider
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.LazyFragment
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.FragmentActivitiesBinding
import com.daqsoft.thetravelcloudwithculture.ui.vm.ActivitiesFragmentVm

/**
 * 活动界面
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-10-22
 * @since JDK 1.8.0_191
 */
class ActivitiesFragment :BaseFragment<FragmentActivitiesBinding,ActivitiesFragmentVm>(){
    override fun getLayout(): Int = R.layout.fragment_activities

    override fun initData() {
    }

    override fun initView() {
    }

    override fun injectVm(): Class<ActivitiesFragmentVm> =ActivitiesFragmentVm::class.java



}