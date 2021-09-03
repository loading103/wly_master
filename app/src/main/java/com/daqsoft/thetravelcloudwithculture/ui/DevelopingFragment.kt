package com.daqsoft.thetravelcloudwithculture.ui

import android.opengl.Visibility
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.FragmentDevelopBinding
import com.daqsoft.thetravelcloudwithculture.databinding.FragmentDevelopBindingImpl
import com.daqsoft.thetravelcloudwithculture.databinding.FragmentShopBinding

/**
 * 开发中的页面
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-10-22
 * @since JDK 1.8.0_191
 */
class DevelopingFragment : BaseFragment<FragmentDevelopBinding, BaseViewModel>() {
    override fun getLayout(): Int = R.layout.fragment_develop

    override fun initData() {
    }

    override fun initView() {
        mBinding.tvTitle.visibility= View.VISIBLE
        mBinding.tvLine.visibility= View.VISIBLE
    }

    override fun injectVm(): Class<BaseViewModel> =BaseViewModel::class.java


}