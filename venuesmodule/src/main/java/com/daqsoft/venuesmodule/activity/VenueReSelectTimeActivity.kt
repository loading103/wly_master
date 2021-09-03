package com.daqsoft.venuesmodule.activity

import android.content.Intent
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.VenueDateInfo
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ActivityResSelectTimeBinding
import com.daqsoft.venuesmodule.fragment.VenueSelectResevationFragment

/**
 * @Description 场馆预约选择时间
 * @ClassName   VenueReSelectTimeActivity
 * @Author      luoyi
 * @Time        2020/7/6 10:29
 */
@Route(path = ARouterPath.VenuesModule.VENUE_RES_SELECT_TIME_ACTIVITY)
class VenueReSelectTimeActivity : TitleBarActivity<ActivityResSelectTimeBinding, VenueReSelectTimeViewModel>() {


    /**
     * 文化场馆的资源ID
     */
    @JvmField
    @Autowired
    var venueId: String = ""
    @JvmField
    @Autowired
    var type: Int = 1
    /**
     * 选择时间预约fragment
     */
    var selectResevationFrag: VenueSelectResevationFragment? = null

    var selectDateInfo: String? = ""

    override fun getLayout(): Int {
        return R.layout.activity_res_select_time
    }

    override fun setTitle(): String {
        return "选择日期"
    }

    override fun injectVm(): Class<VenueReSelectTimeViewModel> {
        return VenueReSelectTimeViewModel::class.java
    }

    override fun initView() {
        // 添加选择时间fragment
        selectResevationFrag = VenueSelectResevationFragment.newInstance(venueId, type)
        selectResevationFrag?.selectTimeItemListener = object : VenueSelectResevationFragment.OnSelectTimeItemListener {
            override fun selectTimeItem(item: VenueDateInfo) {
//                if (item.isHavedReservation) {
//                    mBinding.tvVenueReservationRecord.visibility = View.VISIBLE
//                } else {
//                    mBinding.tvVenueReservationRecord.visibility = View.GONE
//                }
                // 判断团队预约 时间是否正常

                selectDateInfo = item.date
            }

            override fun resevationStatus(teamOrderStatus: Int, personOrderStatus: Int) {
//                if (teamOrderStatus != 1) {
//                    mBinding.vVenueTeamReservation.visibility = View.GONE
//                }
//                if (personOrderStatus != 1) {
//                    mBinding.vVenuePersonReservation.visibility = View.GONE
//                }
//                if (teamOrderStatus != 1 && personOrderStatus == 1) {
//                    reservationType = 1
//                    selectPersonResevation()
//                }
//                if (personOrderStatus != 1 && teamOrderStatus == 1) {
//                    reservationType = 2
//                    selectTeamReservation()
//                }
            }

            override fun controlResevationRecordShow(isHaveOrderRecorder: Boolean) {
                if (isHaveOrderRecorder) {
//                    mBinding.tvVenueReservationRecord.visibility= View.VISIBLE
                }
            }

        }
        transactFragment(R.id.flv_venue_select_time, selectResevationFrag!!)
        mBinding.tvConfirmTime.onNoDoubleClick {
            var intent = Intent()
            intent.putExtra("dateStr", selectDateInfo)
            setResult(3, intent)
            finish()
        }
    }

    override fun initData() {
    }
}

class VenueReSelectTimeViewModel : BaseViewModel() {

}