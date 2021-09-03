package com.dqsoft.votemodule.adapter

import android.view.View
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.MineVoteWorkBean
import com.dqsoft.votemodule.R
import com.dqsoft.votemodule.databinding.ItemMineVoteWorkV2Binding

/**
 * @Description
 * @ClassName   MineVoteWorkV2Adapter
 * @Author      luoyi
 * @Time        2020/11/17 14:38
 */
class MineVoteWorkV2Adapter : RecyclerViewAdapter<ItemMineVoteWorkV2Binding, MineVoteWorkBean>(R.layout.item_mine_vote_work_v2) {

    var onItemClickListener:OnItemClickListener? = null

    override fun setVariable(mBinding: ItemMineVoteWorkV2Binding, position: Int, item: MineVoteWorkBean) {
        item?.let {
            mBinding.data = it
            mBinding.tvVoteWorkTime.text = "${DateUtil.formatDateByString("yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss", it.createTime)}参与"
            mBinding.imgDelVote.visibility = View.VISIBLE
            mBinding.imgDelVote.visibility = View.VISIBLE
            if (it.voteInfo != null && !it.voteInfo!!.title.isNullOrEmpty()) {
                mBinding.tvVoteWorkInfo.text = "${it.voteInfo!!.title}"
                mBinding.tvVoteWorkInfo.visibility = View.VISIBLE
            } else {
                mBinding.tvVoteWorkInfo.visibility = View.GONE
            }

            item?.auditInfo?.let { voteAuditInfo ->
                //待审核(4) 审核通过(6) 回退/驳回(7) 撤回(8) 终止(9) 审核不通过(79)
                when (voteAuditInfo.auditStatus) {
                    4 -> {
                        mBinding.imgDelVote.visibility = View.GONE
                        mBinding.tvMineVoteWorkStatus.text = "待审核"
                        mBinding.tvMineVoteWorkStatus.background =
                            mBinding.root.context.resources.getDrawable(R.drawable.shape_provider_tl5_br5_ff9e05)
                    }
                    6 -> {
                        mBinding.tvMineVoteWorkStatus.text = "已通过"
                        mBinding.tvMineVoteWorkStatus.background =
                            mBinding.root.context.resources.getDrawable(R.drawable.shape_provider_tl5_br5_36cd64)
                    }
                    7, 9, 79 -> {
                        mBinding.tvMineVoteWorkStatus.text = "不通过"
                        mBinding.tvMineVoteWorkStatus.background =
                            mBinding.root.context.resources.getDrawable(R.drawable.shape_provider_tl5_br5_6600000)
                    }
                    8 -> {
                        mBinding.tvMineVoteWorkStatus.text = "已撤回"
                        mBinding.tvMineVoteWorkStatus.background =
                            mBinding.root.context.resources.getDrawable(R.drawable.shape_provider_tl5_br5_6600000)
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