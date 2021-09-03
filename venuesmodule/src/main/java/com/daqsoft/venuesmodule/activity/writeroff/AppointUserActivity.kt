package com.daqsoft.venuesmodule.activity.writeroff

import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.widgets.FullyLinearLayoutManager
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.OrderAttachPMapBean
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.view.scrollview.FadActionScrollview
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.adapter.HavedWriterOffAdapter
import com.daqsoft.venuesmodule.adapter.WaitWriterOffAdapter
import com.daqsoft.venuesmodule.databinding.ActivityAppointUserBinding

/**
 * @Description
 * @ClassName   AppointUserActivity
 * @Author      luoyi
 * @Time        2020/8/5 10:31
 */
@Route(path = ARouterPath.VenuesModule.VENUE_APPOINT_USER_ACTIVITY)
class AppointUserActivity : TitleBarActivity<ActivityAppointUserBinding, AppointUserViewModel>() {

    @JvmField
    @Autowired
    var orderId: String? = ""

    var havedWriterOffAdapter: HavedWriterOffAdapter? = null
    var waitWriterOffAdapter: WaitWriterOffAdapter? = null

    var isNeedShowTabBar: Boolean = true

    override fun getLayout(): Int {
        return R.layout.activity_appoint_user
    }

    override fun setTitle(): String {
        return resources.getString(R.string.venue_reservation_user_num)
    }

    override fun injectVm(): Class<AppointUserViewModel> {
        return AppointUserViewModel::class.java
    }

    override fun initView() {
        havedWriterOffAdapter = HavedWriterOffAdapter()
        havedWriterOffAdapter?.emptyViewShow = false
        mBinding.rvHavedWriterUsers?.adapter = havedWriterOffAdapter
        mBinding.rvHavedWriterUsers?.layoutManager = FullyLinearLayoutManager(
            this@AppointUserActivity,
            FullyLinearLayoutManager.VERTICAL,
            false
        )

        waitWriterOffAdapter = WaitWriterOffAdapter()
        waitWriterOffAdapter?.emptyViewShow = false
        mBinding.rvNoWriterUsers.adapter = waitWriterOffAdapter
        mBinding.rvNoWriterUsers.layoutManager = FullyLinearLayoutManager(
            this@AppointUserActivity,
            FullyLinearLayoutManager.VERTICAL,
            false
        )
        initViewModel()
        initViewEvent()
    }

    private fun initViewEvent() {
        mBinding.scrollAppointUser.setOnScrollListener {
            if (it >= mBinding.vWaitWriterOffUsers.top && mBinding.vWaitWriterOffUsers.visibility == View.VISIBLE) {
                mBinding.isShowTabBar = true
                mBinding.selectTab = 0
            } else if (it > mBinding.vHavedWriterOffUsers.top) {
                mBinding.isShowTabBar = true
                mBinding.selectTab = 1
            } else if (it < mBinding.vWaitWriterOffUsers.top && mBinding.vWaitWriterOffUsers.visibility == View.VISIBLE) {
                mBinding.isShowTabBar = false
            } else if (it < mBinding.vHavedWriterOffUsers.top && mBinding.vWaitWriterOffUsers.visibility == View.GONE) {
                mBinding.isShowTabBar = false
            }

        }
        mBinding.vTabHavedHexiao.onNoDoubleClick {
            mBinding.scrollAppointUser.smoothScrollTo(0, mBinding.vHavedWriterOffUsers.top - 50)
            mBinding.isShowTabBar = false
        }
        mBinding.vTabWaitHexiao.onNoDoubleClick {
            mBinding.scrollAppointUser.smoothScrollTo(0, mBinding.vWaitWriterOffUsers.top - 50)
            mBinding.isShowTabBar = false
        }
    }

    private fun initViewModel() {
        mModel.attachPersonsLd.observe(this, Observer {
            // 待核销
            if (!it.wait.isNullOrEmpty()) {
                waitWriterOffAdapter?.clear()
                waitWriterOffAdapter?.add(it.wait!!)
                mBinding.waitWriterOffNum = "(${it.wait!!.size})"
            } else {
                mBinding.vWaitWriterOffUsers.visibility = View.GONE
                mBinding.vLine.visibility = View.GONE
                mBinding.vTabWaitHexiao.visibility = View.GONE
            }
            // 已核销
            if (!it.end.isNullOrEmpty()) {
                havedWriterOffAdapter?.clear()
                havedWriterOffAdapter?.add(it.end!!)
                mBinding.havedWriterOffNum = "(${it.end!!.size})"
            } else {
                mBinding.vHavedWriterOffUsers.visibility = View.GONE
                mBinding.vTabHavedHexiao.visibility = View.GONE
            }
        })
    }

    override fun initData() {
        if (!orderId.isNullOrEmpty()) {
            mModel.getAttachPersonInfos(orderId!!)
        }
    }
}

class AppointUserViewModel : BaseViewModel() {
    var attachPersonsLd = MutableLiveData<OrderAttachPMapBean>()

    fun getAttachPersonInfos(orderId: String) {
        UserRepository().userService.getOrderAttachedList(orderId)
            .excute(object : BaseObserver<OrderAttachPMapBean>() {
                override fun onSuccess(response: BaseResponse<OrderAttachPMapBean>) {
                    attachPersonsLd.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<OrderAttachPMapBean>) {
                    attachPersonsLd.postValue(null)
                }
            })
    }
}