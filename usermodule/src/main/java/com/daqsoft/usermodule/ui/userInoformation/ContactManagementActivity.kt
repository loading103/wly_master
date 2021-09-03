package com.daqsoft.usermodule.ui.userInoformation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.AESOperator
import com.daqsoft.baselib.utils.SM4Util
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.Contact
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.view.BaseDialog
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityRecevieAddressListBinding
import com.daqsoft.usermodule.databinding.ItemContactManagementBinding
import com.daqsoft.usermodule.repository.constant.IntentConstant
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.activity_recevie_address_list.*
import java.util.concurrent.TimeUnit


/**
 * 管理常用联系人
 */
@Route(path = ARouterPath.UserModule.USER_CONTACT_MANAGEMENT)
class ContactManagementActivity :
    TitleBarActivity<ActivityRecevieAddressListBinding, ContactManagementViewModel>() {
    override fun setTitle(): String = getString(R.string.user_usual_person)

    override fun injectVm(): Class<ContactManagementViewModel> =
        ContactManagementViewModel::class.java

    override fun initData() {
        initDeleteDialog()
    }

    override fun getLayout(): Int = R.layout.activity_recevie_address_list
    /**
     * type 1 管理模式 2 选择模式
     */
    @JvmField
    @Autowired
    var type: Int = 1
    /**
     * 数据集合
     */
    protected val data = arrayListOf<Contact>()
    protected var page = 1
    protected var pageSize = 10
    var deleteDialog: BaseDialog? = null
    protected val adapter = object :
        RecyclerViewAdapter<ItemContactManagementBinding, Contact>(
            R.layout.item_contact_management,
            data
        ) {
        @SuppressLint("CheckResult")
        override fun setVariable(
            mBinding: ItemContactManagementBinding,
            position: Int,
            item: Contact
        ) {
            mBinding.item = item
            if (item.certType.isNullOrEmpty()) {
                mBinding.certType.visibility = View.GONE
            }
            if (item.certNumber.isNullOrEmpty()) {
                mBinding.cert.visibility = View.GONE
            }
            RxView.clicks(mBinding.smContentView)
                // 1秒内不可重复点击或仅响应一次事件
                .throttleFirst(1, TimeUnit.SECONDS).subscribe { o ->
                    run {
                        if (type != null && type == 2) {
                            var intentData = Intent()
                            var bundle = Bundle()
                            bundle.putParcelable("object", item)
                            intentData.putExtra("bundle", bundle)
                            setResult(2, intentData)
                            finish()
                        } else {
                            ARouter.getInstance()
                                .build(ARouterPath.UserModule.USER_ADD_CONTACT)
                                .withParcelable(IntentConstant.OBJECT, item)
                                .navigation()
                        }
                    }
                }
            RxView.clicks(mBinding.ivEdit)
                .throttleFirst(1, TimeUnit.SECONDS).subscribe { o ->
                    run {
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.USER_ADD_CONTACT)
                            .withParcelable(IntentConstant.OBJECT, item)
                            .navigation()
                    }
                }
            RxView.clicks(mBinding.btnDelete)
                // 1秒内不可重复点击或仅响应一次事件
                .throttleFirst(1, TimeUnit.SECONDS).subscribe { o ->
                    run {
                        deleteId = item.id.toString()
                        deleteDialog?.show()
                    }
                }
            mBinding.swipeLayout.smoothCloseMenu(10)
        }
    }
    var deleteId: String = ""

    private fun initDeleteDialog() {
        deleteDialog = BaseDialog(this)
        deleteDialog!!.contentView(R.layout.base_delete_dialog)
            .layoutParams(
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
            .gravity(Gravity.CENTER)
            .animType(BaseDialog.AnimInType.BOTTOM)
            .canceledOnTouchOutside(false)
        deleteDialog!!.findViewById<TextView>(R.id.tv_title).text = "删除联系人"
        deleteDialog!!.findViewById<TextView>(R.id.tv_query).text = "删除"
        deleteDialog!!.findViewById<TextView>(R.id.tv_content).text = "确定删除联系人?"
        deleteDialog!!.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            deleteDialog!!.dismiss()
        }
        deleteDialog!!.findViewById<TextView>(R.id.tv_query).setOnClickListener {
            mModel.deleteContact(deleteId)
            deleteDialog!!.dismiss()
        }
    }


    override fun initView() {
        mModel.contacts.observe(this, Observer {
            mBinding.mSwipeRefreshLayout.finishRefresh()
            if (it.datas != null) {
                for (item in it.datas!!) {
                    if (!item.certNumber.isNullOrEmpty()) {
                        item.certNumber =SM4Util.decryptByEcb(item.certNumber)
                    }
                    if (!item.phone.isNullOrEmpty()) {
                        item.phone =SM4Util.decryptByEcb(item.phone)
                    }
                }
                adapter?.clear()
                adapter.add(it.datas!!)
                adapter.loadEnd()
//                if(it.datas.isNullOrEmpty()||it.datas.size<)
//                if (it.page != null) {
//                    if (it.page!!.currPage < it.page!!.totalPage) {
//                        adapter.loadComplete()
//                    } else {
//                        adapter.loadEnd()
//                    }
//                }
            }
        })

        mBinding.mSwipeRefreshLayout.setOnRefreshListener {
            page = 1
            adapter.clear()
            mModel.getContactList()
//            mSwipeRefreshLayout.isRefreshing = false
        }
        adapter.setItemFooterTypeIsShow(false)
        mRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        mRecyclerView.adapter = adapter
//        adapter.setOnLoadMoreListener {
//            page++
//            mModel.getContactList()
//        }
        mBinding.submit.text = getString(R.string.user_add_contact)
    }

    /**
     * 去到新增联系人页面
     */
    fun submit(v: View) {
        ARouter.getInstance()
            .build(ARouterPath.UserModule.USER_ADD_CONTACT)
            .navigation()
    }

    override fun onResume() {
        super.onResume()
        mModel.getContactList()
    }
}

/**
 * 常用联系人
 */
class ContactManagementViewModel : BaseViewModel() {

    val contacts = MediatorLiveData<BaseResponse<Contact>>()
    /**
     * 获取常用联系人列表
     */
    fun getContactList() {
        mPresenter.value?.loading = true
        UserRepository().userService.getContactList()
            .excute(object : BaseObserver<Contact>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Contact>) {
                    contacts.postValue(response)
                }
            })
    }

    /**
     * 删除常用联系人
     */
    fun deleteContact(id: String) {
        mPresenter.value?.loading = true
        UserRepository().userService.deleteContact(id)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    getContactList()
                }
            })
    }

}