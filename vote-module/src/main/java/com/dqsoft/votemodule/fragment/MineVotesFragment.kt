package com.dqsoft.votemodule.fragment

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.utils.PageDealUtils
import com.dqsoft.votemodule.R
import com.dqsoft.votemodule.adapter.VoteLsAdapter
import com.dqsoft.votemodule.databinding.FragMineVotesBinding
import com.dqsoft.votemodule.vm.MineVotesViewModel

/**
 * @Description
 * @ClassName   MineVotesFragment
 * @Author      luoyi
 * @Time        2020/11/19 11:54
 */
class MineVotesFragment : BaseFragment<FragMineVotesBinding, MineVotesViewModel>() {

    private val voteLsAdapter: VoteLsAdapter by lazy {
        VoteLsAdapter().apply {
            mode=1
            setOnLoadMoreListener {
                mModel.currPage = mModel.currPage + 1
                mModel.getMineVoteList()
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.frag_mine_votes
    }

    override fun injectVm(): Class<MineVotesViewModel> {
        return MineVotesViewModel::class.java
    }

    override fun initView() {
        mBinding.rvMineVotes.run {
            adapter = voteLsAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        mBinding.srlMineVotes.setOnRefreshListener {
            mModel.currPage = 1
            mModel.getMineVoteList()
        }
        mModel.mineVoteLsLd.observe(this, Observer {

            PageDealUtils().pageDeal(mModel.currPage, it, voteLsAdapter)
            if (it != null && !it.datas.isNullOrEmpty()) {
                voteLsAdapter.add(it.datas!!)
            }
            mBinding.srlMineVotes.finishRefresh()
        })
    }

    override fun initData() {
        mModel.currPage = 1
        mModel.getMineVoteList()

    }
}