package com.dqsoft.votemodule.fragment

import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.utils.PageDealUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.network.vote.VoteConstant
import com.daqsoft.provider.view.dialog.ProviderOperationTipDialog
import com.dqsoft.votemodule.R
import com.dqsoft.votemodule.adapter.MineInPartVoteAdapter
import com.dqsoft.votemodule.adapter.MineVoteWorkV2Adapter
import com.dqsoft.votemodule.databinding.FragMineUploadWorksBinding
import com.dqsoft.votemodule.event.UpdateWorkStatusEvent
import com.dqsoft.votemodule.vm.MineWorksViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @Description
 * @ClassName   MineUploadWorksFragment
 * @Author      luoyi
 * @Time        2020/11/19 11:54
 */
class MineUploadWorksFragment : BaseFragment<FragMineUploadWorksBinding, MineWorksViewModel>() {

    private val mineUpLoadWorkAdapter: MineVoteWorkV2Adapter by lazy {
        MineVoteWorkV2Adapter().apply {
            setOnLoadMoreListener {
                mModel.currPage = mModel.currPage + 1
                mModel.getMineInPartWorkList()
            }
            onItemClickListener = object : MineVoteWorkV2Adapter.OnItemClickListener {
                override fun onDelVoteWork(id: String) {
                    currentProId = id
                        deleteTipDialog.show()
                }

            }
        }
    }
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
            .create(context!!)
    }

    override fun getLayout(): Int {
        return R.layout.frag_mine_upload_works
    }

    override fun injectVm(): Class<MineWorksViewModel> {
        return MineWorksViewModel::class.java
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        mBinding.rvMineWorks.run {
            adapter = mineUpLoadWorkAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        mBinding.srlMineWorks.setOnRefreshListener {
            mModel.currPage = 1
            mModel.getMineInPartWorkList()
            mBinding.rvMineWorks.visibility = View.INVISIBLE
            mBinding.rvMineWorks.smoothScrollToPosition(0)
        }
        mModel.voteWorkOpeartionLd.observe(this, Observer {

        })
        mModel.mineWorkLsLd.observe(this, Observer {
            mBinding.srlMineWorks.finishRefresh()
            PageDealUtils().pageDeal(mModel.currPage, it, mineUpLoadWorkAdapter)
            if (it != null && !it.datas.isNullOrEmpty()) {
                mineUpLoadWorkAdapter.add(it.datas!!)
            }
            if (!mBinding.rvMineWorks.isVisible) {
                mBinding.rvMineWorks.visibility = View.VISIBLE
            }
            dissMissLoadingDialog()
        })
        mModel.voteWorkOpeartionLd.observe(this, Observer {
            dissMissLoadingDialog()
            if (it) {
                ToastUtils.showMessage("删除成功~")
                mBinding.rvMineWorks.visibility = View.INVISIBLE
                showLoadingDialog()
                mModel.currPage = 1
                mBinding.rvMineWorks.smoothScrollToPosition(0)
                mModel.getMineInPartWorkList()
            }
        })
    }

    override fun initData() {
        showLoadingDialog()
        mModel.currPage = 1
        mModel.getMineInPartWorkList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun updateWorkStatus(event: UpdateWorkStatusEvent) {
        // 重新获取状态
        mModel.currPage = 1
        mModel.getMineInPartWorkList()
    }
}