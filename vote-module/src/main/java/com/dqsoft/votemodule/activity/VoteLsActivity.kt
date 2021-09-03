package com.dqsoft.votemodule.activity

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.PageDealUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.dqsoft.votemodule.R
import com.dqsoft.votemodule.adapter.VoteLsAdapter
import com.dqsoft.votemodule.databinding.ActivityVoteLsBinding
import com.dqsoft.votemodule.vm.VoteLsViewModel
import org.w3c.dom.Text

/**
 * @Description 投票列表
 * @ClassName   VoteLsActivity
 * @Author      luoyi
 * @Time        2020/11/9 9:50
 */
@Route(path = ARouterPath.VoteModule.VOTE_LS)
class VoteLsActivity : TitleBarActivity<ActivityVoteLsBinding, VoteLsViewModel>() {

    private val voteLsAdapter: VoteLsAdapter by lazy {
        VoteLsAdapter().apply {
            emptyViewShow = false
            setOnLoadMoreListener {
                mModel.currPage = mModel.currPage + 1
                mModel.getVoteLs()
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_vote_ls
    }

    override fun setTitle(): String {
        return resources.getString(R.string.vote_ls)
    }

    override fun injectVm(): Class<VoteLsViewModel> {
        return VoteLsViewModel::class.java
    }

    override fun initView() {

        mBinding.rvVoteLs.apply {
            layoutManager = LinearLayoutManager(this@VoteLsActivity, LinearLayoutManager.VERTICAL, false)
            adapter = voteLsAdapter
        }
        mBinding.rflVoteLs.apply {
            setOnRefreshListener {
                    mModel.currPage = 1
                    mModel.getVoteLs()
            }
            setEnableLoadMore(false)
        }
        mBinding.imgMineVote.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                ARouter.getInstance().build(ARouterPath.VoteModule.MINE_VOTE).navigation()
            } else {
                ToastUtils.showUnLoginMsg()
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
        mBinding.edtInputVoteName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var content: String? = mBinding.edtInputVoteName.text.toString()
                if (content.isNullOrEmpty()) {
                    if (!mModel.searchKey.isNullOrEmpty()) {
                        mModel.searchKey = ""
                        mModel.currPage = 1
                        mModel.getVoteLs()
                    }
                } else {
                    if (mModel.searchKey != content) {
                        mModel.searchKey = content
                        mModel.currPage = 1
                        mModel.getVoteLs()
                    }
                }
            }

        })
        mBinding.vTopVoteLs.onNoDoubleClick {
//            ARouter.getInstance()
//                .build(MainARouterPath.MAIN_ALL_SEARCH)
//                .navigation()
        }
        mModel.voteBeans.observe(this, Observer {
            mBinding.rflVoteLs.finishRefresh()
            if (it != null) {
                PageDealUtils().pageDeal(mModel.currPage, it, voteLsAdapter)
                if (!it.datas.isNullOrEmpty()) {
                    voteLsAdapter.add(it.datas!!)
                } else {
                    if (mModel.currPage == 1) {
                        voteLsAdapter.emptyViewShow = true
                    }
                }
            }
        })
    }

    override fun initData() {
        mModel.currPage = 1
        mModel.getVoteLs()
    }
}