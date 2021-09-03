package com.daqsoft.venuesmodule.activity.writeroff

import android.content.Intent
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.KeyEvent
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.*
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.repository.HttpRepository
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.FullyLinearLayoutManager
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.ProjectConfig
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.OrderAttacthPersonBean
import com.daqsoft.provider.bean.OrderUserBean
import com.daqsoft.provider.bean.OrderUserListBean
import com.daqsoft.provider.businessview.view.TipDialog
import com.daqsoft.provider.network.venues.VenuesRepository
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.adapter.SelectCanceUserAdapter
import com.daqsoft.venuesmodule.databinding.ActivityNoWriterOffUserBinding
import com.daqsoft.venuesmodule.databinding.ItemChooseUserBinding
import com.daqsoft.venuesmodule.databinding.ItemNoVerifyUserBinding
import org.jetbrains.anko.append
import timber.log.Timber

/**
 * @Description
 * @ClassName   NoWriterOffUserActivity
 * @Author      luoyi
 * @Time        2020/8/5 10:01
 */
@Route(path = ARouterPath.VenuesModule.NO_WRITER_OFF_USER_ACTIVITY)
class NoWriterOffUserActivity :
    TitleBarActivity<ActivityNoWriterOffUserBinding, NoWriterOffUserViewModel>() {

    @JvmField
    @Autowired
    var orderId: String? = ""

    @JvmField
    @Autowired
    var orderCode: String? = ""

    @JvmField
    @Autowired
    var chooseData: ArrayList<OrderUserBean>? = null

    /**
     * 选择的用户列表
     */
    var chooseUserList: ArrayList<OrderUserBean> = ArrayList<OrderUserBean>()
    /**
     * 所有用户列表
     */
    var userList: ArrayList<OrderUserBean> = ArrayList<OrderUserBean>()
    /**
     * 搜索内容
     */
    var name: String = ""
    var selectDatas: MutableList<String> = mutableListOf()
    override fun getLayout(): Int {
        return R.layout.activity_no_writer_off_user
    }

    override fun setTitle(): String {
        return "不核销用户"
    }

    override fun injectVm(): Class<NoWriterOffUserViewModel> {
        return NoWriterOffUserViewModel::class.java
    }

    override fun initView() {
        chooseUserList.clear()
        mBinding.etSearchWords.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (mBinding.etSearchWords.text.toString().trim().isNullOrEmpty()) {
                    name = ""
                    mBinding.tvAllUserCount.text = "(0)"
                    initData()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
        mBinding?.llvReservationPersons?.minSelect = 0
        mBinding?.llvReservationPersons?.setOnLabelSelectChangeListener { label, data, isSelect, position ->
            label?.setTextColor(resources.getColor(com.daqsoft.provider.R.color.c_36cd64))
            label?.setBackgroundResource(com.daqsoft.provider.R.drawable.shape_venue_selected_r3)

        }
        mBinding.recyclerAllUser.apply {
            layoutManager =
                object : LinearLayoutManager(this@NoWriterOffUserActivity, VERTICAL, false) {
                    override fun canScrollVertically(): Boolean {
                        return false
                    }
                }
            userAdapter.emptyViewShow = false
            adapter = userAdapter
        }
        mBinding.tvNoVerifyUser.setOnClickListener {
            var intent = Intent()
            intent.putParcelableArrayListExtra("data", chooseUserList)
            intent.putExtra("writerOffNum", userAdapter.getData().size - chooseUserList.size)
            setResult(0x0004, intent)
            finish()
        }
    }

    override fun initData() {

        userAdapter.clear()
        if (!orderId.isNullOrEmpty())
            mModel.getOrderUserList(orderId!!, name)
    }
    private fun setLabelsData() {
        updateSelectDatas()
        mBinding?.llvReservationPersons?.setLabels(selectDatas)
        mBinding?.llvReservationPersons?.selectAll()
    }

    private fun updateSelectDatas() {
        selectDatas.clear()
        if (chooseUserList != null) {
            for (item in chooseUserList) {
                    selectDatas.add("" + item.userName)
            }
        }
    }
    override fun notifyData() {
        super.notifyData()
        mModel.data.observe(this, androidx.lifecycle.Observer {
            if (it.code == 0 && it.data != null) {
                // 有效期内
                if (!chooseData.isNullOrEmpty()) {
                    chooseUserList.clear()
                    chooseUserList.addAll(chooseData!!)
                }
                if (it.data!!.wait != null && !it.data!!.wait.isNullOrEmpty()) {
                    mBinding.tvAllUserCount.text = "(" + it.data!!.wait.size.toString() + ")"
                    if (name.isNullOrEmpty()) {
                        userList.clear()
                        userList = it.data!!.wait as ArrayList<OrderUserBean>
                    }
                    userAdapter.add(it.data!!.wait as MutableList<OrderUserBean>)
                    userAdapter.notifyDataSetChanged()
                    refreshChooseCount()
                }
            }
        })
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event?.keyCode == KeyEvent.KEYCODE_ENTER && event?.action == KeyEvent.ACTION_UP) {
            if (!mBinding.etSearchWords.text.toString().trim().isNullOrEmpty()) {
                name = mBinding.etSearchWords.text.toString().trim()
                mBinding.tvAllUserCount.text = "(0)"
                initData()
                return true
            }
        }
        return super.dispatchKeyEvent(event)
    }

    /**
     * 不核销用户列表适配器
     */
    private val chooseNoVerifyAdapter by lazy {
        object :
            RecyclerViewAdapter<ItemNoVerifyUserBinding, OrderUserBean>(R.layout.item_no_verify_user) {
            override fun setVariable(
                mBinding: ItemNoVerifyUserBinding,
                position: Int,
                item: OrderUserBean
            ) {
                mBinding.item = item
            }

        }
    }
    /**
     * 全部用户
     */
    private val userAdapter by lazy {
        object :
            RecyclerViewAdapter<ItemChooseUserBinding, OrderUserBean>(R.layout.item_choose_user) {
            override fun setVariable(
                mBinding: ItemChooseUserBinding,
                position: Int,
                item: OrderUserBean
            ) {
                mBinding.item = item
                for (bean in chooseUserList) {
                    if (bean.userPhone == item.userPhone
                        && bean.userCardNumber == item.userCardNumber
                        && bean.userName == item.userName
                    ) {
                        item.select = true
                    }
                }
                mBinding.tvUserName.isSelected = item.select
                mBinding.root.setOnClickListener {
                    if (!item.select && !chooseUserList.toString().contains(item.toString())) {
                        chooseUserList.add(item)
                    }
                    if (item.select && chooseUserList.toString().contains(item.toString())) {
                        var it_b = chooseUserList.iterator()
                        while (it_b.hasNext()) {
                            val bean = it_b.next()
                            if (bean.userPhone == item.userPhone
                                && bean.userCardNumber == item.userCardNumber
                                && bean.userName == item.userName
                            ) {
                                it_b.remove()
                            }
                        }
                    }
                    item.select = !item.select
                    refreshChooseCount()
                    notifyDataSetChanged()
                }
            }

        }
    }

    /**
     * 刷新不核销用户总数
     */
    fun refreshChooseCount() {
        Timber.e("数量=" + chooseUserList.size + "--" + chooseUserList.toString())
        mBinding.tvNoUserCount.text = "(" + chooseUserList.size + ")"
        if (chooseUserList.size >= userList.size) {
            mBinding.tvNoVerifyUser.text = "确认不核销" + chooseUserList.size + "人"
            mBinding.tvNoVerifyUser.isEnabled = false
            showDialog()
        } else {
            mBinding.tvNoVerifyUser.text = "确认不核销" + chooseUserList.size + "人"
            mBinding.tvNoVerifyUser.isEnabled = true
        }
        chooseNoVerifyAdapter.clear()
        chooseNoVerifyAdapter.add(chooseUserList)
        chooseNoVerifyAdapter.notifyDataSetChanged()
        setLabelsData()
    }

    /**
     * 弹出温馨提示框
     */
    fun showDialog() {
        var spanContent = SpannableStringBuilder("")
        var info = "您已选择“"
        spanContent.append(info)
        var name = "全部用户不核销"
        var content = "”，至少需要核销1位用户"
        var forregroundCSpan = ForegroundColorSpan(resources.getColor(R.color.viewfinder_laser))
        spanContent.append(name, forregroundCSpan, 0, name.length)
        spanContent.append(content)
        TipDialog.Builder().setSpanContent(spanContent).create(this).show()
    }


}

class NoWriterOffUserViewModel : BaseViewModel() {
    /**
     * 订单随性人数据
     */
    var data: MutableLiveData<BaseResponse<OrderUserListBean>> = MutableLiveData()

    /**
     * 获取订单随行人订单
     * @param orderId 订单ID
     */
    fun getOrderUserList(orderId: String, name: String) {
        VenuesRepository.venuesService.getOrderUserList(
            orderId,
            SPUtils.getInstance().getString(SPUtils.Config.TOKEN),
            BaseApplication.siteCode,
            name
        )
            .excute(object : BaseObserver<OrderUserListBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<OrderUserListBean>) {
                    data.postValue(response)
                }

            })
    }
}