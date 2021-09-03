package com.dqsoft.votemodule.adapter

import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.VoteBean
import com.daqsoft.provider.bean.VoteWorkBean
import com.daqsoft.provider.utils.DividerTextUtils
import com.dqsoft.votemodule.R
import com.dqsoft.votemodule.databinding.ItemGridVoteLsBinding
import java.lang.StringBuilder

/**
 * @Description 作品列表
 * @ClassName   GridVoteLsAdapter
 * @Author      luoyi
 * @Time        2020/11/9 15:19
 */
class GridVoteLsAdapter : RecyclerViewAdapter<ItemGridVoteLsBinding, VoteWorkBean>(R.layout.item_grid_vote_ls) {


    var onVotoLsItemClickListener: OnVoteLsItemClickListener? = null

    override fun setVariable(mBinding: ItemGridVoteLsBinding, position: Int, item: VoteWorkBean) {
        item?.let {
            mBinding.data = it
            mBinding.tvVoteType.text = if (!it.typeName.isNullOrEmpty() || !it.typeChildName.isNullOrEmpty()) {
                "类型：" + DividerTextUtils.converttDivideString(
                    StringBuilder(), "-",
                    it.typeName ?: "", it.typeChildName ?: ""
                )
            } else {
                ""
            }

            mBinding.tvVoteNum.visibility = View.VISIBLE
            mBinding.tvVoteNumLabel.visibility = View.VISIBLE
            mBinding.tvGotoVote.visibility = View.VISIBLE
            mBinding.tvGotoVote.text = "投票"
            if (item.resourceCount != null) {
                mBinding.tvVoteNum.visibility = View.VISIBLE
                mBinding.tvVoteNumLabel.visibility = View.VISIBLE
            } else {
                mBinding.tvVoteNum.visibility = View.GONE
                mBinding.tvVoteNumLabel.visibility = View.GONE
            }
            when (it.voteButton) {
                //0 不可投票 1 可以投票 2 已投票 3 已结束
                0 -> {
                    mBinding.tvGotoVote.background = mBinding.root.context.resources.getDrawable(R.drawable.shape_vote_btn_bforbidd_r15)
                    mBinding.tvGotoVote.text = "不可投票"
                    mBinding.tvGotoVote.visibility=View.GONE
                }
                1 -> {
                    mBinding.tvGotoVote.background = mBinding.root.context.resources.getDrawable(R.drawable.shape_vote_btn_bprimary_r15)
                    mBinding.tvGotoVote.text = "投票"
                    mBinding.tvGotoVote.visibility=View.VISIBLE
                }
                2 -> {
                    mBinding.tvGotoVote.background = mBinding.root.context.resources.getDrawable(R.drawable.shape_vote_btn_bforbidd_r15)
                    mBinding.tvGotoVote.text = "已投票"
                    mBinding.tvGotoVote.visibility=View.VISIBLE
                }
                3 -> {
                    mBinding.tvGotoVote.background = mBinding.root.context.resources.getDrawable(R.drawable.shape_vote_btn_bforbidd_r15)
                    mBinding.tvGotoVote.visibility = View.GONE
                }
                4->{
                    mBinding.tvGotoVote.background = mBinding.root.context.resources.getDrawable(R.drawable.shape_vote_btn_bprimary_r15)
                }


            }
            mBinding.tvGotoVote.onNoDoubleClick {
                    onVotoLsItemClickListener?.onVoteItem(position, item)
            }
            mBinding.root.onNoDoubleClick {
                ARouter.getInstance().build(ARouterPath.VoteModule.VOTE_MINE_WORK_DETAIL)
                    .withString("proId", it.id.toString())
                    .withInt("mode", 0)
                    .navigation()
            }
        }
    }

    interface OnVoteLsItemClickListener {
        fun onVoteItem(position: Int, item: VoteWorkBean)
    }
}