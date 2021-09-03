package com.daqsoft.travelCultureModule.hotActivity.fragment

import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.MainActivityIntroduceFragmentBinding
import com.daqsoft.mainmodule.databinding.MainItemHotActivityTagBinding
import com.daqsoft.provider.bean.ActivityBean

/**
 * @des 活动推荐部分
 * @author PuHua
 * @Date 2019/12/5 17:52
 */
class HotActivityRecommandFragment(hotActivity: MutableList<ActivityBean>) :
    BaseFragment<MainActivityIntroduceFragmentBinding,
            BaseViewModel>() {

    private val mHotActivity = hotActivity

    override fun getLayout(): Int = R.layout.main_activity_introduce_fragment

    override fun initData() {


    }

    override fun initView() {


    }

    /**
     * 标签适配器
     */
    val tagAdapter= object :
        RecyclerViewAdapter<MainItemHotActivityTagBinding, String>(
            R.layout.main_item_hot_activity_tag
        ) {
        override fun setVariable(mBinding: MainItemHotActivityTagBinding, position: Int, item: String) {
            mBinding.name = item
        }

    }
    override fun injectVm(): Class<BaseViewModel> = BaseViewModel::class.java

}
