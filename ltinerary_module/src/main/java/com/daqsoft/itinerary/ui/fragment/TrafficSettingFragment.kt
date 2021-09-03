package com.daqsoft.itinerary.ui.fragment

import android.content.Context
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.itinerary.R
import com.daqsoft.itinerary.databinding.FragmentSettingTrafficBinding
import com.daqsoft.itinerary.ui.ItineraryCustomActivity
import com.daqsoft.itinerary.vm.ItineraryCustomViewModel

/**
 * @Author：      邓益千
 * @Create by：   2020/4/23 20:35
 * @Description： 步骤三，出行方式
 */
class TrafficSettingFragment : BaseFragment<FragmentSettingTrafficBinding,ItineraryCustomViewModel>(){

    private lateinit var parentActivity: ItineraryCustomActivity

    override fun getLayout(): Int = R.layout.fragment_setting_traffic

    override fun injectVm(): Class<ItineraryCustomViewModel> = ItineraryCustomViewModel::class.java

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentActivity = context as ItineraryCustomActivity
    }

    override fun initView() {
        parentActivity.customLabel.value?.travelType = "JOURNEY_TRAVEL_SELF"

        mBinding.viewDriving.setOnClickListener {
            mBinding.viewBus.isChecked = false
            parentActivity.customLabel.value?.travelType = "JOURNEY_TRAVEL_SELF"
        }
        mBinding.viewBus.setOnClickListener {
            mBinding.viewDriving.isChecked = false
            parentActivity.customLabel.value?.travelType = "JOURNEY_TRAVEL_TRAFFIC"
        }
    }

    override fun initData() {
    }
}