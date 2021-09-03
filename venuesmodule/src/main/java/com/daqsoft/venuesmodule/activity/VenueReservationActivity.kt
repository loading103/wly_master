package com.daqsoft.venuesmodule.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.VenueDateInfo
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ActivityVenueReservationBinding
import com.daqsoft.venuesmodule.fragment.VenueSelectResevationFragment
import com.daqsoft.venuesmodule.viewmodel.VenueReservationViewModel

/**
 * @Description 場館預約
 * @ClassName   VenueReservationActivity
 * @Author      luoyi
 * @Time        2020/4/30 14:28
 */
@Route(path = ARouterPath.VenuesModule.VENUES_RESERVATION_ACTIVITY)
class VenueReservationActivity : TitleBarActivity<ActivityVenueReservationBinding, VenueReservationViewModel>() {

    /**
     * 文化场馆的资源ID
     */
    @JvmField
    @Autowired
    var venueId: String = ""

    var reservationType: Int = 1
    /**
     * 选择时间预约fragment
     */
    var selectResevationFrag: VenueSelectResevationFragment? = null

    /**
     * 旋转的时间信息
     */
    var selectDateInfo: String? = ""

    override fun getLayout(): Int {
        return R.layout.activity_venue_reservation
    }

    override fun setTitle(): String {
        return "场馆预约"
    }

    override fun injectVm(): Class<VenueReservationViewModel> {
        return VenueReservationViewModel::class.java
    }

    override fun initView() {
        // 添加选择时间fragment
        selectResevationFrag = VenueSelectResevationFragment.newInstance(venueId, reservationType)
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
                if (teamOrderStatus != 1) {
                    mBinding.vVenueTeamReservation.visibility = View.GONE
                }
                if (personOrderStatus != 1) {
                    mBinding.vVenuePersonReservation.visibility = View.GONE
                }
                if (teamOrderStatus != 1 && personOrderStatus == 1) {
                    reservationType = 1
                    selectPersonResevation()
                }
                if (personOrderStatus != 1 && teamOrderStatus == 1) {
                    reservationType = 2
                    selectTeamReservation()
                }
            }

            override fun controlResevationRecordShow(isHaveOrderRecorder: Boolean) {
                if (isHaveOrderRecorder) {
                    mBinding.tvVenueReservationRecord.visibility = View.VISIBLE
                }
            }

        }
        transactFragment(R.id.flv_venue_select_time, selectResevationFrag!!)

        initViewEvent()
    }

    private fun initViewEvent() {
        mBinding.vVenuePersonReservation.onNoDoubleClick {
            // 个人预约
            selectPersonResevation()
            selectResevationFrag?.updateType(0)
        }

        mBinding.vVenueTeamReservation.onNoDoubleClick {
            // 团队预约
            selectTeamReservation()
            selectResevationFrag?.updateType(1)
        }
        mBinding.tvVenueReservationRight.onNoDoubleClick {
            if (!selectResevationFrag?.dateInfo.isNullOrEmpty()) {
                ARouter.getInstance().build(ARouterPath.VenuesModule.VENUES_RESERVATION_INFO_ACTIVITY)
                    .withInt("type", reservationType)
                    .withString("venueId", venueId)
                    .withString("date", selectDateInfo)
                    .navigation()
            } else {
                ToastUtils.showMessage("请选正确的日期~")
            }
        }
        mBinding.tvVenueReservationRecord.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                ARouter.getInstance()
                    .build(ARouterPath.UserModule.USER_APPOINTMENT_LIST)
                    .navigation()
            } else {
                ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                ARouter.getInstance()
                    .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
    }

    private fun selectTeamReservation() {
        mBinding.vVenuePersonSlide.visibility = View.GONE
        mBinding.vVenueTeamSlide.visibility = View.VISIBLE
        mBinding.tvVenuePersonReservation.setTextColor(resources.getColor(R.color.color_666))
        mBinding.tvVenueTeamReservation.setTextColor(resources.getColor(R.color.color_333))
        mBinding.tvVenuePersonReservation.paint.isFakeBoldText = false
        mBinding.tvVenueTeamReservation.paint.isFakeBoldText = true
        reservationType = 2
    }

    private fun selectPersonResevation() {
        mBinding.vVenueTeamSlide.visibility = View.GONE
        mBinding.vVenuePersonSlide.visibility = View.VISIBLE
        mBinding.tvVenuePersonReservation.setTextColor(resources.getColor(R.color.color_333))
        mBinding.tvVenueTeamReservation.setTextColor(resources.getColor(R.color.color_666))
        mBinding.tvVenuePersonReservation.paint.isFakeBoldText = true
        mBinding.tvVenueTeamReservation.paint.isFakeBoldText = false
        reservationType = 1
    }

    override fun initData() {
    }
}