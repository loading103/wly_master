package com.daqsoft.usermodule.ui.order

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.FullyLinearLayoutManager
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.OrderAttachPMapBean
import com.daqsoft.provider.bean.OrderAttacthPersonBean
import com.daqsoft.provider.businessview.event.UpdateOrderCanceStatus
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityAppointCanceBinding
import com.daqsoft.usermodule.ui.order.adapter.SelectCanceUserAdapter
import org.greenrobot.eventbus.EventBus

/**
 * @Description 取消订单
 * @ClassName   AppointCanceOrderActivity
 * @Author      luoyi
 * @Time        2020/8/4 15:43
 */
@Route(path = ARouterPath.UserModule.USER_APPOINT_CANCE_ORDER_ACITIVTY)
class AppointCanceOrderActivity :
    TitleBarActivity<ActivityAppointCanceBinding, AppointCanceOrderViewModel>() {

    @JvmField
    @Autowired
    var orderId: String? = ""

    @JvmField
    @Autowired
    var orderCode: String? = ""
    /**
     * 选择取消订单用户信息
     */
    var selectCanceUserAdapter: SelectCanceUserAdapter? = null

    var selectDatas: MutableList<String> = mutableListOf()

    var isSelectAll: Boolean = false

    override fun getLayout(): Int {
        return R.layout.activity_appoint_cance
    }

    override fun setTitle(): String {
        return resources.getString(R.string.order_cancel_order)
    }

    override fun injectVm(): Class<AppointCanceOrderViewModel> {
        return AppointCanceOrderViewModel::class.java
    }

    override fun initView() {
        selectCanceUserAdapter = SelectCanceUserAdapter(this@AppointCanceOrderActivity)
        mBinding.rvAllUsers.adapter = selectCanceUserAdapter
        selectCanceUserAdapter?.onSelectCanceUserListener =
            object : SelectCanceUserAdapter.OnSelectCanceUserListener {
                override fun onSelectStatus(position: Int) {
                    var item = selectCanceUserAdapter!!.getData()[position]
                    if (item.isSelect == 1) {
                        item.isSelect = 0
                    } else {
                        item.isSelect = 1
                    }
                    selectCanceUserAdapter?.notifyDataSetChanged()
                    setLabelsData()
                    updateSelectNumTxT()
                }

            }
        mBinding.rvAllUsers.layoutManager = FullyLinearLayoutManager(
            this@AppointCanceOrderActivity,
            FullyLinearLayoutManager.VERTICAL,
            false
        )
        initViewModel()
        initViewEvent()

        mBinding.tvCanceOrder.text = "请选择需要取消的用户"
    }

    private fun initViewEvent() {
        mBinding?.llvReservationPersons?.minSelect = 0
        mBinding?.llvReservationPersons?.setOnLabelSelectChangeListener { label, data, isSelect, position ->
            label?.setTextColor(resources.getColor(com.daqsoft.provider.R.color.c_36cd64))
            label?.setBackgroundResource(com.daqsoft.provider.R.drawable.shape_venue_selected_r3)

        }
        mBinding?.edtSearchWords?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    // 拼配名称
                    searchUserName(s.toString())
                } else {
                    // 取消选中匹配
                    searchUserName("")
                }
            }

        })
        mBinding.vSelectAll.onNoDoubleClick {
            if (!isSelectAll) {
                // 选择所有
                selectAllData()
                mBinding?.imgSelectStatus?.setImageResource(R.mipmap.register_button_choose_selected)
                mBinding?.tvSelectAll?.text = "取消"
                isSelectAll = true
            } else {
                clearAllData()
                mBinding?.imgSelectStatus?.setImageResource(R.mipmap.register_button_choose_normal)
                mBinding?.tvSelectAll?.text = "全选"
                isSelectAll = false
            }
            setLabelsData()
        }
        mBinding.tvCanceOrder.onNoDoubleClick {
            var selectNum = getSelectNum()
            if (selectNum > 0) {
                var opersids: String = getOpersides()
                if (!orderCode.isNullOrEmpty()) {
                    showLoadingDialog()
                    mModel.canceActivityOrderById(orderCode!!, selectNum, opersids, "", "")
                } else {
                    ToastUtils.showMessage("数据异常，请稍后再试")
                }
            } else {
                ToastUtils.showMessage("请选择要取消的用户")
            }
        }
    }

    private fun clearAllData() {
        if (selectCanceUserAdapter != null) {
            for (i in selectCanceUserAdapter!!.getData().indices) {
                selectCanceUserAdapter!!.getData()[i].isSelect = 0
            }
            selectCanceUserAdapter?.notifyDataSetChanged()
        }
    }

    private fun selectAllData() {
        if (selectCanceUserAdapter != null) {
            for (i in selectCanceUserAdapter!!.getData().indices) {
                selectCanceUserAdapter!!.getData()[i].isSelect = 1
            }
            selectCanceUserAdapter?.notifyDataSetChanged()
        }
    }

    private fun setLabelsData() {
        updateSelectDatas()
        mBinding?.llvReservationPersons?.setLabels(selectDatas)
        mBinding?.llvReservationPersons?.selectAll()
    }

    private fun updateSelectDatas() {
        selectDatas.clear()
        if (selectCanceUserAdapter != null) {
            for (item in selectCanceUserAdapter!!.getData()) {
                if (item.isSelect == 1) {
                    selectDatas.add("" + item.userName)
                }
            }
        }
    }

    private fun searchUserName(name: String) {
        var select = -1
        if (selectCanceUserAdapter != null) {
            for (i in selectCanceUserAdapter!!.getData().indices) {
                var item = selectCanceUserAdapter!!.getData()[i]
                if (!name.isNullOrEmpty() && (item.userName!!.startsWith(name)
                            || (!item.userPhone.isNullOrEmpty() && item.userPhone!!.endsWith(name))
                            || (!item.userCardNumber.isNullOrEmpty() && item.userCardNumber!!.endsWith(
                        name
                    )))
                ) {
                    if (select == -1) {
                        select = i
                    }
                    selectCanceUserAdapter!!.getData()[i].isContainName = true
                } else {
                    selectCanceUserAdapter!!.getData()[i]!!.isContainName = false
                }
            }
        }
        // 选中 第一个搜索到得值
        if (select != -1) {
            mBinding.rvAllUsers.smoothScrollToPosition(select)
        }
        selectCanceUserAdapter?.notifyItemRangeChanged(
            0,
            selectCanceUserAdapter!!.itemCount,
            "select_status"
        )
    }

    private fun getOpersides(): String {
        var temps: MutableList<String> = mutableListOf()
        for (item in selectCanceUserAdapter!!.getData()) {
            if (item.isSelect == 1) {
                temps.add(item.id.toString())
            }
        }
        return temps.joinToString(",")
    }

    private fun updateSelectNumTxT() {
        var seletNun = 0
        if (selectCanceUserAdapter != null) {
            seletNun = getSelectNum()
        }
        if (seletNun > 0) {
            mBinding.tvCanceOrder.text = "确认取消${seletNun}人的预约"
        } else {
            mBinding.tvCanceOrder.text = "请选择需要取消的用户"
        }
    }

    private fun getSelectNum(): Int {
        var selectNum: Int = 0
        for (i in selectCanceUserAdapter!!.getData().indices) {
            var item = selectCanceUserAdapter!!.getData()[i]
            if (item.isSelect == 1) {
                selectNum += 1
            }
        }
        return selectNum
    }

    private fun initViewModel() {
        mModel.attachPersonsLd.observe(this, Observer {
            if (it != null) {
                if (!it.wait.isNullOrEmpty()) {
                    selectCanceUserAdapter?.clear()
                    selectCanceUserAdapter?.add(it.wait!!)
                    mBinding.allUserNum = "(${it.wait!!.size})"
                } else {

                }
            }
        })
        mModel.canceActivityOrderSuccess.observe(this, Observer {
            dissMissLoadingDialog()
            if (it) {
                ToastUtils.showMessage("取消成功")
                EventBus.getDefault().post(UpdateOrderCanceStatus())
                finish()
            } else {
                ToastUtils.showMessage("请稍后再试")
            }
        })
    }


    override fun initData() {
        if (!orderId.isNullOrEmpty())
            mModel.getAttachPersonInfos(orderId!!)
    }
}

class AppointCanceOrderViewModel : BaseViewModel() {
    var attachPersonsLd = MutableLiveData<OrderAttachPMapBean>()
    var canceActivityOrderSuccess = MutableLiveData<Boolean>()
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

    /**
     * 取消订单
     * @param orderCode 订单编号
     * @param canceNum 取消数量
     * @param operateIds 随行人数据id
     * @param operateExcuse 随行人取消方式
     * @param cancelNum 取消数量
     * @param operateExcuse 取消原因
     * @param remark 取消备注
     */
    fun canceActivityOrderById(
        orderCode: String,
        cancelNum: Int = 0,
        operateIds: String? = "",
        operateExcuse: String? = "",
        remark: String? = ""
    ) {
        mPresenter?.value?.loading = false
        var params: HashMap<String, String> = hashMapOf()
        params["orderCode"] = orderCode
        if (cancelNum > 0) {
            params["cancelNum"] = cancelNum.toString()
        }
        if (!operateExcuse.isNullOrEmpty()) {
            params["operateExcuse"] = operateExcuse
        }
        if (!operateIds.isNullOrEmpty()) {
            params["operateIds"] = operateIds
        }
        if (!operateExcuse.isNullOrEmpty()) {
            params["operateExcuse"] = operateExcuse
        }
        if (!remark.isNullOrEmpty()) {
            params["remark"] = remark
        }
        UserRepository().userService.canceActivityOrder(params)
            .excute(object : BaseObserver<Any>() {
                override fun onSuccess(response: BaseResponse<Any>) {
                    canceActivityOrderSuccess?.postValue(true)
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    canceActivityOrderSuccess?.postValue(false)
                }

            })
    }
}