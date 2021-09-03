package com.daqsoft.usermodule.ui.userInoformation

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.*
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.JavaUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityRecevieAddressListBinding
import com.daqsoft.usermodule.databinding.ItemReceiveAddressManagementBinding
import com.daqsoft.usermodule.repository.constant.IntentConstant
import com.daqsoft.provider.bean.ReceiveAddressBean
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.view.BaseDialog
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.activity_recevie_address_list.*
import java.util.concurrent.TimeUnit


/**
 * 管理收货地址
 */
@Route(path = ARouterPath.UserModule.USER_RECEIVE_ADDRESS)
class ReceiverAddressManagementActivity :
    TitleBarActivity<ActivityRecevieAddressListBinding, ReceiverAddressManagementViewModel>() {
    override fun setTitle(): String = getString(R.string.user_receive_address)

    override fun injectVm(): Class<ReceiverAddressManagementViewModel> =
        ReceiverAddressManagementViewModel::class.java

    override fun initData() {
    }

    override fun getLayout(): Int = R.layout.activity_recevie_address_list

    /**
     * 数据集合
     */
    protected val data = arrayListOf<ReceiveAddressBean>()
    protected var page = 1
    protected var pageSize = 10

    protected val adapter = object :
        RecyclerViewAdapter<ItemReceiveAddressManagementBinding, ReceiveAddressBean>(
            R.layout.item_receive_address_management,
            data
        ) {
        @SuppressLint("CheckResult")
        override fun setVariable(
            mBinding: ItemReceiveAddressManagementBinding,
            position: Int,
            item: ReceiveAddressBean
        ) {
//            mBinding.mNameTv.text = item.consignee
////            mBinding.phone.content = item.phone
////            mBinding.address.content = item.address
            mBinding.item = item
            mBinding.swipeLayout.onNoDoubleClick {
                ARouter.getInstance()
                    .build(ARouterPath.UserModule.USER_ADD_RECEIVE_ADDRESS)
                    .withParcelable(IntentConstant.OBJECT, item)
                    .navigation()
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

    var deleteId:String = ""

    override fun initView() {
        mModel.addressBeans.observe(this, Observer {
            mBinding.mSwipeRefreshLayout.finishRefresh()
            JavaUtils.pageDeal(page, it, adapter)
            if (it.datas != null) {
                adapter.add(it.datas!!)
            }
        })

        mBinding.mSwipeRefreshLayout.setOnRefreshListener {
            page = 1
            adapter.clear()
            mModel.getUserReceiveAddress()
//            mSwipeRefreshLayout.isRefreshing = false
        }
        adapter.setItemFooterTypeIsShow(false)
        mRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        mRecyclerView.adapter = adapter
        adapter.setOnLoadMoreListener {
            page++
            mModel.getUserReceiveAddress()
        }
        initDeleteDialog()
    }

    var deleteDialog:BaseDialog?= null

    private fun initDeleteDialog(){
        deleteDialog = BaseDialog(this)
        deleteDialog!!.contentView(R.layout.base_delete_dialog)
            .layoutParams( ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            .gravity(Gravity.CENTER)
            .animType(BaseDialog.AnimInType.BOTTOM)
            .canceledOnTouchOutside(false)
        deleteDialog!!.findViewById<TextView>(R.id.tv_title).text = "删除收货地址"
        deleteDialog!!.findViewById<TextView>(R.id.tv_query).text = "删除"
        deleteDialog!!.findViewById<TextView>(R.id.tv_content).text = "确定删除收货地址?"
        deleteDialog!!.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            deleteDialog!!.dismiss()
        }
        deleteDialog!!.findViewById<TextView>(R.id.tv_query).setOnClickListener {
            mModel.deleteAddress(deleteId)
            deleteDialog!!.dismiss()
        }
    }


//    /**
//     * 统一处理ViewModel传过来的回调
//     */
//    override fun presenter() {
//
//        mModel.mPresenter.observe(this, Observer {
//            if (init) {
//                init = false
//                return@Observer
//            }
//
//            if (it.errorMessage.isNotEmpty()) {
//                toast(it.errorMessage)
//                it.errorMessage = ""
//            }
//            if (it.loading) {
//                // 开启网络加载框
//                mContentView.showLoadingView(Config.loadingView)
//            } else {
//                // 关闭网络加载框
//                if (mBinding.mSwipeRefreshLayout!=null&&mBinding.mSwipeRefreshLayout.isRefreshing){
//                    mBinding.mSwipeRefreshLayout.isRefreshing = false
//                }
//                if (it.error) {
//                    mContentView.showErrorView()
//                } else {
//                    mContentView.showContentView()
//                }
//            }
//            if (it.login) {
//                // 需要登录时跳转登录界面
//                // TODO:因为Baselib不依赖provicer模块，因此此处地址用写死的LoginActivity的路径字符串，不能用ARouterPath
//                // 修改方案为在BaseApplication里定义loginPath作为跳转登录页
//                // 清空保存的token
//                SPUtils.getInstance().put(SPUtils.Config.TOKEN, "")
//                ARouter.getInstance()
//                    .build(BaseApplication.loginPath)
//                    .navigation()
//                it.login = false
//            }
//            // 视图显示完毕后，将加载状态置为初始状态
////            it.loading = true
//        })
//    }

    /**
     * 去到新增地址页面
     */
    fun submit(v: View) {
        ARouter.getInstance()
            .build(ARouterPath.UserModule.USER_ADD_RECEIVE_ADDRESS)
            .navigation()
    }

    override fun onResume() {
        super.onResume()
        mModel.getUserReceiveAddress()
    }
}

/**
 * 收货人列表viewmodel
 */
class ReceiverAddressManagementViewModel : BaseViewModel() {

    val addressBeans = MediatorLiveData<BaseResponse<ReceiveAddressBean>>()
    /**
     * 获取列表
     */
    fun getUserReceiveAddress() {
        mPresenter.value?.loading = true
        UserRepository().userService.getReceiveAddress()
            .excute(object : BaseObserver<ReceiveAddressBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ReceiveAddressBean>) {
                    addressBeans.postValue(response)
                }
            })
    }

    /**
     * 删除收货人地址
     */
    fun deleteAddress(id: String) {
        mPresenter.value?.loading = true
        UserRepository().userService.deleteAddress(id)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    getUserReceiveAddress()
                }
            })
    }
}