package com.daqsoft.usermodule.ui.invitation

import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityInviteLsBinding
import com.daqsoft.usermodule.ui.invitation.adapter.InviteLsAdapter
import com.daqsoft.usermodule.ui.invitation.viewmodel.InviteLsViewModel

/**
 * @Description 邀请列表
 * @ClassName   InviteLsActivity
 * @Author      luoyi
 * @Time        2020/7/27 16:32
 */
@Route(path = ARouterPath.UserModule.USER_INVITE_LS_ACTIVITY)
class InviteLsActivity : TitleBarActivity<ActivityInviteLsBinding, InviteLsViewModel>() {

    /**
     * 当前页码
     */
    private var currPage: Int = 1
    /**
     * 页面大小
     */
    private var pageSize: Int = 12

    val mInviteLsAdapter: InviteLsAdapter by lazy {
        InviteLsAdapter().apply {
            emptyViewShow = false
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_invite_ls
    }

    override fun setTitle(): String {
        return "我的邀请"
    }

    override fun injectVm(): Class<InviteLsViewModel> {
        return InviteLsViewModel::class.java
    }

    override fun initView() {
        mBinding.rvInvites.adapter = mInviteLsAdapter
        mBinding.rvInvites.layoutManager = LinearLayoutManager(
            this@InviteLsActivity,
            LinearLayoutManager.VERTICAL, false
        )
        mInviteLsAdapter?.setOnLoadMoreListener {
            currPage = +1
            mModel.getInviteLs(pageSize, currPage)
        }
        mBinding.tvToInvite.onNoDoubleClick {
            // 前页肯定是邀请有礼页面
            finish()
        }
        initViewModel()
    }

    private fun initViewModel() {
        mModel.inviteLsLd.observe(this, Observer {
            dissMissLoadingDialog()
            if (currPage == 1) {
                mInviteLsAdapter?.clear()
                mBinding.rvInvites.visibility = View.VISIBLE
            }
            if (!it.isNullOrEmpty()) {
                mInviteLsAdapter.add(it)
                if (mBinding.rvInvites.visibility == View.GONE) {
                    mBinding.rvInvites.visibility = View.VISIBLE
                    mBinding.vEmptyInviteLs.visibility = View.GONE
                }
            } else if (currPage == 1) {
                //  数据为空
                mBinding.rvInvites.visibility = View.GONE
                mBinding.vEmptyInviteLs.visibility = View.VISIBLE
            }
            if (it.isNullOrEmpty() || it.size < pageSize) {
                mInviteLsAdapter?.loadComplete()
            } else {
                mInviteLsAdapter?.loadEnd()
            }
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })
    }

    override fun initData() {
        showLoadingDialog()
        mModel.getInviteLs(pageSize, currPage)
    }
}
