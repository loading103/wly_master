package com.dqsoft.votemodule.adapter

import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.VoteBean
import com.daqsoft.provider.network.vote.VoteConstant
import com.dqsoft.votemodule.R
import com.dqsoft.votemodule.databinding.ItemVoteLsBinding

/**
 * @Description
 * @ClassName   VoteLsAdapter
 * @Author      luoyi
 * @Time        2020/11/9 10:51
 */
class VoteLsAdapter : RecyclerViewAdapter<ItemVoteLsBinding, VoteBean>(R.layout.item_vote_ls) {

    // 0 投票列表 1我的投票
    var mode: Int = 0

    override fun setVariable(mBinding: ItemVoteLsBinding, position: Int, item: VoteBean) {
        mBinding.data = item
        mBinding.lvInputInfo.visibility = View.GONE
        item?.let {
            mBinding.rlVoteEndTime.run {
                visibility = View.VISIBLE
            }

            if (item.resourceCount != null) {
                mBinding.tvVotePersonNum.text = "${item.resourceCount?.ticketCount}次"
                mBinding.tvVotePersonNum.visibility = View.VISIBLE
            } else {
                mBinding.tvVotePersonNum.text = ""
                mBinding.tvVotePersonNum.visibility = View.GONE
            }
            when (it.voteStatus) {
                VoteConstant.STATUS.END -> {
                    mBinding.tvVoteStatus.run {
                        background = mBinding.root.context.resources.getDrawable(R.mipmap.vote_list_tag_yugao)
                        text = "已结束"
                    }
                    mBinding.vVoteStartTime.visibility = View.GONE
                    mBinding.lvInputInfo.visibility = View.VISIBLE
                }
                VoteConstant.STATUS.UN_START -> {

                    if(it.uploadStatus!="0"){
                        mBinding.tvVoteStatus.run {
                            background = mBinding.root.context.resources.getDrawable(R.mipmap.vote_list_tag_toupiaozhong)
                            text = "活动中"
                        }
                    }else{
                        mBinding.tvVoteStatus.run {
                            background = mBinding.root.context.resources.getDrawable(R.mipmap.vote_list_tag_yugao)
                            text = "预告"
                        }
                    }
                    mBinding.vVoteStartTimeTip.visibility = View.VISIBLE
                    mBinding.vVoteStartTime.visibility = View.VISIBLE
                    mBinding.tvVoteStartTime.text = "开始：${DateUtil.formatDateByString("yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss", it.startTime)}"
                }
                VoteConstant.STATUS.PROGRESS_ING -> {
                    mBinding.tvVoteStatus.run {
                        background = mBinding.root.context.resources.getDrawable(R.mipmap.vote_list_tag_toupiaozhong)
                        text = "活动中"
                    }

                    mBinding.vVoteStartTime.visibility = View.GONE
                    mBinding.vVoteStartTimeTip.visibility = View.GONE
                    mBinding.lvInputInfo.visibility = View.VISIBLE
                }
                else -> {
                    mBinding.tvVoteStatus.run {
                        background = mBinding.root.context.resources.getDrawable(R.mipmap.vote_list_tag_yugao)
                        text = "预告"
                    }
                    mBinding.vVoteStartTimeTip.visibility = View.VISIBLE
                    mBinding.vVoteStartTime.visibility = View.VISIBLE
                    mBinding.tvVoteStartTime.text = "开始：${DateUtil.formatDateByString("yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss", it.startTime)}"
                }
            }
            if (mode == 0) {
                mBinding.rlVoteEndTime.visibility = View.VISIBLE
                mBinding.tvVoteEndTime.text = "结束：${DateUtil.formatDateByString("yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss", it.endTime)}"
//                mBinding.vVoteStartTime.visibility = View.GONE
//                mBinding.tvVoteFirstTime.visibility = View.GONE
            } else {
//                mBinding.tvInputPersonNameLabel.text = "参与"
                mBinding.rlVoteEndTime.visibility = View.GONE
                mBinding.vVoteStartTime.visibility = View.GONE
                if (!item.firstVoteTime.isNullOrEmpty()) {
                    mBinding.tvVoteFirstTime.visibility = View.VISIBLE
                    mBinding.tvVoteFirstTime.text = "${DateUtil.formatDateByString("yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss", it.firstVoteTime)}投票"
                } else {
                    mBinding.tvVoteFirstTime.visibility = View.GONE
                }

            }
            mBinding.root.onNoDoubleClick {
                ARouter.getInstance().build(ARouterPath.VoteModule.VOTE_DETAIL).withString("voteId", it.id.toString())
                    .navigation()
            }
        }
    }
}