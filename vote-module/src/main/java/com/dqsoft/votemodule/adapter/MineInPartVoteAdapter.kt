package com.dqsoft.votemodule.adapter

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.MineVoteWorkBean
import com.daqsoft.provider.bean.VoteBean
import com.daqsoft.provider.bean.VoteWorkBean
import com.dqsoft.votemodule.R
import com.dqsoft.votemodule.databinding.ItemMineVoteBinding

/**
 * @Description 我的投票
 * @ClassName   MineVoteAdapter
 * @Author      luoyi
 * @Time        2020/11/10 15:13
 */
class MineInPartVoteAdapter : RecyclerViewAdapter<ItemMineVoteBinding, MineVoteWorkBean>(R.layout.item_mine_vote) {


    var onItemClickListener: OnItemClickListener? = null

    override fun setVariable(mBinding: ItemMineVoteBinding, position: Int, item: MineVoteWorkBean) {
        item?.let {
            mBinding.data = it
            mBinding.tvVoteWorkTime.text = "${DateUtil.formatDateByString("yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss", it.createTime)}参与"
            mBinding.imgDelVote.visibility = View.VISIBLE
            item?.auditInfo?.let { voteAuditInfo ->
                //待审核(4) 审核通过(6) 回退/驳回(7) 撤回(8) 终止(9) 审核不通过(79)
                when (voteAuditInfo.auditStatus) {
                    4 -> {
                        mBinding.imgDelVote.visibility = View.GONE
                        mBinding.tvVoteWorkInfo.text = "待审核"
                        mBinding.tvVoteWorkInfo.setTextColor(ContextCompat.getColor(mBinding.root.context, R.color.color_ff9e05))
                    }
                    6 -> {
                        mBinding.tvVoteWorkInfo.text = "已通过"
                        mBinding.tvVoteWorkInfo.setTextColor(ContextCompat.getColor(mBinding.root.context, R.color.color_36cd64))
                    }
                    7, 9, 79 -> {
                        mBinding.tvVoteWorkInfo.text = "不通过"
                        mBinding.tvVoteWorkInfo.setTextColor(ContextCompat.getColor(mBinding.root.context, R.color.color_666))
                    }
                    8 -> {
                        mBinding.tvVoteWorkInfo.text = "已撤回"
                        mBinding.tvVoteWorkInfo.setTextColor(ContextCompat.getColor(mBinding.root.context, R.color.color_666))
                    }

                }
            }

            mBinding.imgDelVote.onNoDoubleClick {
                onItemClickListener?.onDelVoteWork(item.id.toString())
            }
            mBinding.root.onNoDoubleClick {
                ARouter.getInstance().build(ARouterPath.VoteModule.VOTE_MINE_WORK_DETAIL)
                    .withString("proId", it.id.toString())
                    .withInt("mode", 1)
                    .navigation()
            }
        }

    }

    interface OnItemClickListener {
        fun onDelVoteWork(id: String)
    }
}