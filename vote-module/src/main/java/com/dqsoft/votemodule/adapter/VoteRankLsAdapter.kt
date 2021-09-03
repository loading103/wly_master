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
import com.dqsoft.votemodule.databinding.ItemVoteRankLsBinding
import org.jetbrains.anko.textColor
import java.lang.StringBuilder

/**
 * @Description
 * @ClassName   VoteRankLsAdapter
 * @Author      luoyi
 * @Time        2020/11/9 15:57
 */
class VoteRankLsAdapter : RecyclerViewAdapter<ItemVoteRankLsBinding, VoteWorkBean>(R.layout.item_vote_rank_ls) {
    override fun setVariable(mBinding: ItemVoteRankLsBinding, position: Int, item: VoteWorkBean) {
        mBinding.index = "${position + 1}"
        mBinding.data = item
        var typestr = DividerTextUtils.converttDivideString(
            StringBuilder(), "-", item.typeName ?: "",
            item.typeChildName ?: ""
        )
        mBinding.tvRankResourceInfo.text = "编号：${item.codeNum}  " + if (!typestr.isNullOrEmpty()) {
            "类型：${typestr}"
        } else {
            ""
        }
        if (item.voteButton == 0) {
            mBinding.vRankVoteNum.visibility = View.GONE
        }
        mBinding.tvVoteRankPos.setTextColor(mBinding.root.context.resources.getColor(R.color.white))
        when (position) {
            0 -> {
                mBinding.vVoteRankLs.background = mBinding.root.context.resources.getDrawable(R.mipmap.vote_details_rank_1st)
                mBinding.tvRankResourceInfo.setTextColor(mBinding.root.context.resources.getColor(R.color.white))
                mBinding.tvRankResourceName.setTextColor(mBinding.root.context.resources.getColor(R.color.white))
                mBinding.tvRanksNumLable.setTextColor(mBinding.root.context.resources.getColor(R.color.white))
                mBinding.tvRankVoteNum.setTextColor(mBinding.root.context.resources.getColor(R.color.white))
            }
            1 -> {
                mBinding.vVoteRankLs.background = mBinding.root.context.resources.getDrawable(R.mipmap.vote_details_rank_2nd)
                mBinding.tvRankResourceInfo.setTextColor(mBinding.root.context.resources.getColor(R.color.white))
                mBinding.tvRankResourceName.setTextColor(mBinding.root.context.resources.getColor(R.color.white))
                mBinding.tvRanksNumLable.setTextColor(mBinding.root.context.resources.getColor(R.color.white))
                mBinding.tvRankVoteNum.setTextColor(mBinding.root.context.resources.getColor(R.color.white))
            }
            2 -> {
                mBinding.vVoteRankLs.background = mBinding.root.context.resources.getDrawable(R.mipmap.vote_details_rank_3nd)
                mBinding.tvRankResourceInfo.setTextColor(mBinding.root.context.resources.getColor(R.color.white))
                mBinding.tvRankVoteNum.setTextColor(mBinding.root.context.resources.getColor(R.color.white))
                mBinding.tvRanksNumLable.setTextColor(mBinding.root.context.resources.getColor(R.color.white))
                mBinding.tvRankResourceName.setTextColor(mBinding.root.context.resources.getColor(R.color.white))
            }
            else -> {
                mBinding.vVoteRankLs.background = null
                mBinding.tvRankResourceInfo.setTextColor(mBinding.root.context.resources.getColor(R.color.color_999))
                mBinding.tvRankResourceName.setTextColor(mBinding.root.context.resources.getColor(R.color.color_333))
                mBinding.tvRankVoteNum.setTextColor(mBinding.root.context.resources.getColor(R.color.color_333))
                mBinding.tvRanksNumLable.setTextColor(mBinding.root.context.resources.getColor(R.color.color_333))
                mBinding.tvVoteRankPos.setTextColor(mBinding.root.context.resources.getColor(R.color.color_666))
            }
        }
        mBinding.root.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.VoteModule.VOTE_MINE_WORK_DETAIL)
                .withString("proId", item.id.toString())
                .withInt("mode", 0)
                .navigation()
        }

    }
}