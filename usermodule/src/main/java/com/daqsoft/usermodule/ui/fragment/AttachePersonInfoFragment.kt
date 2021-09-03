package com.daqsoft.usermodule.ui.fragment

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.widgets.FullyLinearLayoutManager
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.bean.OrderAttachPMapBean
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.network.net.excut
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.FragAttachPersonInfoBinding
import com.daqsoft.usermodule.ui.order.adapter.MineOrderUserInfoAdapter

/**
 * @Description 多人预约信息
 * @ClassName   AttachePersonInfoFragment
 * @Author      luoyi
 * @Time        2020/8/13 20:48
 */
class AttachePersonInfoFragment :
    BaseFragment<FragAttachPersonInfoBinding, AttachPersonInfoViewModel>() {
    /**
     * 已取消的用户
     */
    var haveCanceAdapter: MineOrderUserInfoAdapter? = null
    /**
     * 已使用的用户
     */
    var haveUserAdapter: MineOrderUserInfoAdapter? = null
    /**
     * 待使用的用户
     */
    var waitUserAdapter: MineOrderUserInfoAdapter? = null
    /**
     * 已失效的用户
     */
    var havedInvalidAdapter: MineOrderUserInfoAdapter? = null

    companion object {

        const val ORDER_ID = "order_id"

        fun newInstance(orderId: String): AttachePersonInfoFragment {
            var frag = AttachePersonInfoFragment()
            var bundle = Bundle()
            bundle.putString(ORDER_ID, orderId)
            frag.arguments = bundle
            return frag
        }

    }

    override fun getLayout(): Int {
        return R.layout.frag_attach_person_info
    }

    override fun injectVm(): Class<AttachPersonInfoViewModel> {
        return AttachPersonInfoViewModel::class.java
    }

    override fun initView() {
        initViewModel()
        waitUserAdapter = MineOrderUserInfoAdapter(context!!)
        mBinding.rvWaitUse.adapter = waitUserAdapter
        mBinding.rvWaitUse.layoutManager =
            FullyLinearLayoutManager(context!!, FullyLinearLayoutManager.VERTICAL, false)

        haveCanceAdapter = MineOrderUserInfoAdapter(context!!)
        mBinding.rvWaitCance.adapter = haveCanceAdapter
        mBinding.rvWaitCance.layoutManager =
            FullyLinearLayoutManager(context!!, FullyLinearLayoutManager.VERTICAL, false)

        haveUserAdapter = MineOrderUserInfoAdapter(context!!)
        mBinding.rvHavedUse.adapter = haveUserAdapter
        mBinding.rvHavedUse.layoutManager =
            FullyLinearLayoutManager(context!!, FullyLinearLayoutManager.VERTICAL, false)

        havedInvalidAdapter = MineOrderUserInfoAdapter(context!!)
        mBinding.rvHavedInvalid.adapter = havedInvalidAdapter
        mBinding.rvHavedInvalid.layoutManager =
            FullyLinearLayoutManager(context!!, FullyLinearLayoutManager.VERTICAL, false)
        initViewEvent()
    }

    private fun initViewEvent() {
        mBinding.tvMoreWaiteCance.onNoDoubleClick {
            haveCanceAdapter!!.isShowAll = !haveCanceAdapter!!.isShowAll
            if (haveCanceAdapter!!.isShowAll) {
                mBinding.tvMoreWaiteCance?.text = "收起更多"
                val topDrawable =
                    resources.getDrawable(com.daqsoft.provider.R.mipmap.provider_arrow_up)
                topDrawable?.setBounds(0, 0, topDrawable.minimumWidth, topDrawable.minimumHeight)
                mBinding.tvMoreWaiteCance?.setCompoundDrawables(null, null, topDrawable, null)
            } else {
                val topDrawable =
                    resources.getDrawable(com.daqsoft.provider.R.mipmap.provider_arrow_down)
                topDrawable?.setBounds(0, 0, topDrawable.minimumWidth, topDrawable.minimumHeight)
                mBinding.tvMoreWaiteCance?.setCompoundDrawables(null, null, topDrawable, null)
                mBinding.tvMoreWaiteCance?.text = "查看更多"
            }
            haveCanceAdapter?.notifyDataSetChanged()
        }
        mBinding.tvMoreHavedInvalid.onNoDoubleClick {
            havedInvalidAdapter!!.isShowAll = !havedInvalidAdapter!!.isShowAll
            if (havedInvalidAdapter!!.isShowAll) {
                mBinding.tvMoreHavedInvalid?.text = "收起更多"
                val topDrawable =
                    resources.getDrawable(com.daqsoft.provider.R.mipmap.provider_arrow_up)
                topDrawable?.setBounds(0, 0, topDrawable.minimumWidth, topDrawable.minimumHeight)
                mBinding.tvMoreHavedInvalid?.setCompoundDrawables(null, null, topDrawable, null)
            } else {
                val topDrawable =
                    resources.getDrawable(com.daqsoft.provider.R.mipmap.provider_arrow_down)
                topDrawable?.setBounds(0, 0, topDrawable.minimumWidth, topDrawable.minimumHeight)
                mBinding.tvMoreHavedInvalid?.setCompoundDrawables(null, null, topDrawable, null)
                mBinding.tvMoreHavedInvalid?.text = "查看更多"
            }
            havedInvalidAdapter!!.notifyDataSetChanged()
        }
        mBinding.tvMoreHavedUse.onNoDoubleClick {
            haveUserAdapter!!.isShowAll = !haveUserAdapter!!.isShowAll
            if (haveUserAdapter!!.isShowAll) {
                mBinding.tvMoreHavedUse?.text = "收起更多"
                val topDrawable =
                    resources.getDrawable(com.daqsoft.provider.R.mipmap.provider_arrow_up)
                topDrawable?.setBounds(0, 0, topDrawable.minimumWidth, topDrawable.minimumHeight)
                mBinding.tvMoreHavedUse?.setCompoundDrawables(null, null, topDrawable, null)
            } else {
                val topDrawable =
                    resources.getDrawable(com.daqsoft.provider.R.mipmap.provider_arrow_down)
                topDrawable?.setBounds(0, 0, topDrawable.minimumWidth, topDrawable.minimumHeight)
                mBinding.tvMoreHavedUse?.setCompoundDrawables(null, null, topDrawable, null)
                mBinding.tvMoreHavedUse?.text = "查看更多"
            }
            haveUserAdapter!!.notifyDataSetChanged()
        }
        mBinding.tvMoreWaitUse.onNoDoubleClick {
            waitUserAdapter!!.isShowAll = !waitUserAdapter!!.isShowAll
            if (waitUserAdapter!!.isShowAll) {
                mBinding.tvMoreWaitUse?.text = "收起更多"
                val topDrawable =
                    resources.getDrawable(com.daqsoft.provider.R.mipmap.provider_arrow_up)
                topDrawable?.setBounds(0, 0, topDrawable.minimumWidth, topDrawable.minimumHeight)
                mBinding.tvMoreWaitUse?.setCompoundDrawables(null, null, topDrawable, null)
            } else {
                val topDrawable =
                    resources.getDrawable(com.daqsoft.provider.R.mipmap.provider_arrow_down)
                topDrawable?.setBounds(0, 0, topDrawable.minimumWidth, topDrawable.minimumHeight)
                mBinding.tvMoreWaitUse?.setCompoundDrawables(null, null, topDrawable, null)
                mBinding.tvMoreWaitUse?.text = "查看更多"
            }
            waitUserAdapter!!.notifyDataSetChanged()
        }
    }

    private fun initViewModel() {
        mModel.attachPersonsLd.observe(this, Observer {
            if (it != null) {
                mBinding.root.visibility = View.VISIBLE
                if (it.cancel.isNullOrEmpty()) {
                    mBinding.vWaitCance.visibility = View.GONE
                    mBinding.vWaitCanceTitle.visibility = View.GONE
                } else {
                    mBinding.vWaitCance.visibility = View.VISIBLE
                    mBinding.vWaitCanceTitle.visibility = View.VISIBLE
                    if (it.cancel!!.size > 3) {
                        haveCanceAdapter?.isShowAll = false
                        mBinding.tvMoreWaiteCance.visibility = View.VISIBLE
                    }
                    haveCanceAdapter?.clear()
                    haveCanceAdapter?.add(it.cancel!!)

                }

                if (it.wait.isNullOrEmpty()) {
                    mBinding.vWaitUse.visibility = View.GONE
                    mBinding.vWaitUseTitle.visibility = View.GONE
                } else {
                    mBinding.vWaitUse.visibility = View.VISIBLE
                    mBinding.vWaitUseTitle.visibility = View.VISIBLE
                    if (it.wait!!.size > 3) {
                        waitUserAdapter?.isShowAll = false
                        mBinding.tvMoreWaitUse.visibility = View.VISIBLE
                    }
                    waitUserAdapter?.clear()
                    waitUserAdapter?.add(it.wait!!)
                }

                if (!it.end.isNullOrEmpty()) {
                    mBinding.vHavedUse.visibility = View.VISIBLE
                    mBinding.vHavedUseTitle.visibility = View.VISIBLE
                    if (it.end!!.size > 3) {
                        haveUserAdapter?.isShowAll = false
                        mBinding.tvMoreHavedUse.visibility = View.VISIBLE
                    }
                    haveUserAdapter?.clear()
                    haveUserAdapter?.add(it.end!!)
                } else {
                    mBinding.vHavedUse.visibility = View.GONE
                    mBinding.vHavedUseTitle.visibility = View.GONE
                }
                if (!it.lose.isNullOrEmpty()) {
                    mBinding.vHavedInvalid.visibility = View.VISIBLE
                    mBinding.vHavedInvalidTitle.visibility = View.VISIBLE
                    if (it.lose!!.size > 3) {
                        havedInvalidAdapter?.isShowAll = false
                        mBinding.tvMoreHavedInvalid.visibility = View.VISIBLE
                    }
                    havedInvalidAdapter?.clear()
                    havedInvalidAdapter?.add(it.lose!!)
                } else {
                    mBinding.vHavedInvalid.visibility = View.GONE
                    mBinding.vHavedInvalidTitle.visibility = View.GONE
                }


                if (it.cancel.isNullOrEmpty() && it.end.isNullOrEmpty() && it.lose.isNullOrEmpty() && !it.wait.isNullOrEmpty()) {
                    mBinding.vWaitUseTitle.visibility = View.GONE
                }


            } else {
                mBinding.root.visibility = View.GONE
            }
        })
    }

    override fun initData() {
        var orderId: String? = arguments?.getString(ORDER_ID)
        if (!orderId.isNullOrEmpty()) {
            mModel.getAttachPersonInfos(orderId)
        }
    }
}

class AttachPersonInfoViewModel : BaseViewModel() {

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