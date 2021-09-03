package com.dqsoft.votemodule.activity

import android.view.View
import android.view.ViewParent
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.PageDealUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.network.vote.VoteConstant
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import com.daqsoft.provider.view.dialog.ProviderOperationTipDialog
import com.dqsoft.votemodule.R
import com.dqsoft.votemodule.adapter.MineVotePageAdapter
import com.dqsoft.votemodule.adapter.MineVoteWorkV2Adapter
import com.dqsoft.votemodule.adapter.VoteLsAdapter
import com.dqsoft.votemodule.databinding.ActivityMineVoteBinding
import com.dqsoft.votemodule.databinding.ActivityVoteLsBinding
import com.dqsoft.votemodule.event.UpdateWorkStatusEvent
import com.dqsoft.votemodule.fragment.MineUploadWorksFragment
import com.dqsoft.votemodule.fragment.MineVotesFragment
import com.dqsoft.votemodule.vm.MineVoteViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.textColor

/**
 * @Description 我的投票列表
 * @ClassName   MineVoteLsActivity
 * @Author      luoyi
 * @Time        2020/11/9 9:52
 */
@Route(path = ARouterPath.VoteModule.MINE_VOTE)
class MineVoteLsActivity : TitleBarActivity<ActivityMineVoteBinding, MineVoteViewModel>() {

    var tabPos: Int = 0

    var datas: MutableList<Fragment> = mutableListOf()

    private val votePageAdapter: MineVotePageAdapter by lazy {
        datas.clear()
        datas.add(MineUploadWorksFragment())
        datas.add(MineVotesFragment())
        MineVotePageAdapter(datas, supportFragmentManager)
    }

    override fun getLayout(): Int {
        return R.layout.activity_mine_vote
    }

    override fun setTitle(): String {
        return "我的投票"
    }

    override fun injectVm(): Class<MineVoteViewModel> {
        return MineVoteViewModel::class.java
    }

    override fun initView() {
        mBinding.vMineVoteLs.onNoDoubleClick {
            initMineVoteTab()
            setSelectMineVote()
            tabPos = 1
            mBinding.vpMineVote.setCurrentItem(1)
        }
        mBinding.vMineInpartWork.onNoDoubleClick {
            initMineVoteTab()
            setSelectMineWorks()
            tabPos = 0
            mBinding.vpMineVote.setCurrentItem(0)
        }
        mBinding.vpMineVote.adapter = votePageAdapter
        mBinding.vpMineVote.currentItem = 0
        mBinding.vpMineVote.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                initMineVoteTab()
                if (position == 0) {
                    setSelectMineWorks()
                    tabPos = 0
                } else {
                    setSelectMineVote()
                    tabPos = 1
                }
            }

        })
        initViewModel()
    }

    private fun setSelectMineWorks() {
        mBinding.vMineInpart.visibility = View.VISIBLE
        mBinding.tvInpartVote.run {
            textSize = 15f
            setTextColor(ContextCompat.getColor(this@MineVoteLsActivity, R.color.color_333))
            paint.isFakeBoldText = true
        }
    }

    private fun setSelectMineVote() {
        mBinding.vMineVote.visibility = View.VISIBLE
        mBinding.tvMineVote.run {
            textSize = 15f
            setTextColor(ContextCompat.getColor(this@MineVoteLsActivity, R.color.color_333))
            paint.isFakeBoldText = true
        }
    }

    private fun initViewModel() {
    }

    private fun initMineVoteTab() {
        mBinding.vMineInpart.visibility = View.GONE
        mBinding.vMineVote.visibility = View.GONE
        mBinding.tvInpartVote.run {
            textSize = 14f
            setTextColor(ContextCompat.getColor(this@MineVoteLsActivity, R.color.color_666))
            paint.isFakeBoldText = false

        }
        mBinding.tvMineVote.run {
            textSize = 14f
            setTextColor(ContextCompat.getColor(this@MineVoteLsActivity, R.color.color_666))
            paint.isFakeBoldText = false

        }
    }

    override fun initData() {
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}