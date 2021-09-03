package com.dqsoft.votemodule.activity

import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.PageDealUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.network.vote.VoteConstant
import com.daqsoft.provider.view.dialog.ProviderOperationTipDialog
import com.dqsoft.votemodule.R
import com.dqsoft.votemodule.adapter.MineInPartVoteAdapter
import com.dqsoft.votemodule.databinding.ActivityMineVoteWorkLsBinding
import com.dqsoft.votemodule.event.UpdateWorkStatusEvent
import com.dqsoft.votemodule.vm.VoteInPartLsViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @Description 我的参与列表
 * @ClassName   VoteInPartListActivity
 * @Author      luoyi
 * @Time        2020/11/16 15:45
 */
@Route(path = ARouterPath.VoteModule.MINE_VOTE_WORK_LIST)
class VoteInPartListActivity : TitleBarActivity<ActivityMineVoteWorkLsBinding, VoteInPartLsViewModel>() {

    @Autowired
    @JvmField
    var voteId: String? = ""


    var currentProId: String? = ""

    /**
     * 删除确认弹框
     */
    private val deleteTipDialog: ProviderOperationTipDialog by lazy {
        ProviderOperationTipDialog.Builder().setContent(resources.getString(R.string.vote_tip_delete))
            .setTitle(resources.getString(R.string.vote_tip_delete_title))
            .setOnTipConfirmListener(object : ProviderOperationTipDialog.OnTipConfirmListener {
                override fun onConfirm() {
                    showLoadingDialog()
                    mModel.operationWorkDetail(currentProId ?: "", VoteConstant.OPERATION_STATUS.DELETE)
                }

            })
            .create(this@VoteInPartListActivity)
    }

    private val mineInPartAdapter: MineInPartVoteAdapter by lazy {
        MineInPartVoteAdapter().apply {
            setOnLoadMoreListener {
                mModel.currPage = +1
                mModel.getVoteWorkList()
            }
            onItemClickListener = object : MineInPartVoteAdapter.OnItemClickListener {
                override fun onDelVoteWork(id: String) {
                    currentProId = id
                    deleteTipDialog.show()
                }

            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_mine_vote_work_ls
    }

    override fun setTitle(): String {
        return resources.getString(R.string.vote_bottom_mine_inpart)
    }

    override fun injectVm(): Class<VoteInPartLsViewModel> {
        return VoteInPartLsViewModel::class.java
    }

    override fun initView() {
        mModel.voteId = voteId
        EventBus.getDefault().register(this)
        mBinding.rvMineVoteWorks.apply {
            layoutManager = LinearLayoutManager(this@VoteInPartListActivity, LinearLayoutManager.VERTICAL, false)
            adapter = mineInPartAdapter
        }
        mBinding.srlVoteWorkList.setOnRefreshListener {
            initData()
        }
        mBinding.tvVoteBottomTitle.onNoDoubleClick {
            // 我要参与
            if (!AppUtils.isLogin()) {
                ToastUtils.showUnLoginMsg()
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
                return@onNoDoubleClick
            } else {
                ARouter.getInstance().build(ARouterPath.VoteModule.VOTE_INPART)
                    .withString("voteId", voteId)
                    .withInt("mode", 1)
                    .navigation()
            }
        }
        initViewModel()
    }

    private fun initViewModel() {
        mModel.voteWorkListLd.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.srlVoteWorkList.finishRefresh()
            PageDealUtils().pageDeal(mModel.currPage, it, mineInPartAdapter)
            if (it != null && !it.datas.isNullOrEmpty()) {
                mineInPartAdapter.add(it.datas!!)
            }
            if (!mBinding.rvMineVoteWorks.isVisible) {
                mBinding.rvMineVoteWorks.visibility = View.VISIBLE
                dissMissLoadingDialog()
            }
        })
        mModel.voteWorkOpeartionLd.observe(this, Observer {
            dissMissLoadingDialog()
            if (it) {
                ToastUtils.showMessage("删除成功~")
                mBinding.rvMineVoteWorks.visibility = View.INVISIBLE
                showLoadingDialog()
                mModel.currPage = 1
                mBinding.rvMineVoteWorks.smoothScrollToPosition(0)
                mModel.getVoteWorkList()
            }
        })
    }

    override fun initData() {
        showLoadingDialog()
        mModel.currPage = 1
        mModel.getVoteWorkList()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateWorkStatus(event: UpdateWorkStatusEvent) {
        // 重新获取状态
        mModel.currPage = 1
        mModel.getVoteWorkList()
    }
}