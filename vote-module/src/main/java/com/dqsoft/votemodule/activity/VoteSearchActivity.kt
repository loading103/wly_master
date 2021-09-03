package com.dqsoft.votemodule.activity

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextSwitcher
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.PageDealUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.VoteSubTypeBean
import com.daqsoft.provider.bean.VoteTypeBean
import com.daqsoft.provider.bean.VoteWorkBean
import com.dqsoft.votemodule.R
import com.dqsoft.votemodule.adapter.GridVoteLsAdapter
import com.dqsoft.votemodule.adapter.VoteChildTypeAdapter
import com.dqsoft.votemodule.adapter.VoteTypeAdapter
import com.dqsoft.votemodule.databinding.ActivityVoteSearchBinding
import com.dqsoft.votemodule.vm.VoteSearchViewModel

/**
 * @Description
 * @ClassName   VoteSearchActivity
 * @Author      luoyi
 * @Time        2020/11/9 9:52
 */
@Route(path = ARouterPath.VoteModule.VOTE_SEARCH)
class VoteSearchActivity : TitleBarActivity<ActivityVoteSearchBinding, VoteSearchViewModel>() {

    @Autowired
    @JvmField
    var voteId: String? = ""

    private val voteTypeAdapter: VoteTypeAdapter by lazy {
        VoteTypeAdapter(this@VoteSearchActivity)
            .apply {
                onItemClickListener = object : VoteTypeAdapter.OnItemClickListener {
                    override fun onItemClick(pos: Int, item: VoteTypeBean) {
                        if (item.id.toString() != mModel.type) {
                            hideEmptyView()
                            mModel.type = item.id.toString()
                            mModel.typeChild = ""
                            hideEmptyView()
                            mModel.currPage = 1
                            mModel.getVoteWorkList(voteId ?: "")
                        }
                        if (!item.child.isNullOrEmpty()) {
                            mBinding.rvVoteChildTypes.visibility = View.VISIBLE
                            voteChildTypeAdapter.clear()
                            voteChildTypeAdapter.selectPos = 0
                            voteChildTypeAdapter.getData().add(VoteSubTypeBean().apply {
                                name = "全部"
                                id = -1
                            })
                            voteChildTypeAdapter.add(item.child!!)
                        } else {
                            mBinding.rvVoteChildTypes.visibility = View.GONE
                        }
                    }

                }
            }
    }
    private val voteChildTypeAdapter: VoteChildTypeAdapter by lazy {
        VoteChildTypeAdapter(this@VoteSearchActivity).apply {
            onItemClickListener = object : VoteChildTypeAdapter.OnItemClickListener {
                override fun onItemClick(pos: Int, typeId: String) {
                    if (typeId != mModel.typeChild) {
                        if (typeId == "-1") {
                            mModel.typeChild = ""
                        } else {
                            mModel.typeChild = typeId
                        }
                        hideEmptyView()
                        mModel.currPage = 1
                        mModel.getVoteWorkList(voteId ?: "")
                    }
                }

            }
        }
    }
    private val defaultWorkAdapter: GridVoteLsAdapter by lazy {
        GridVoteLsAdapter().apply {
            emptyViewShow = false
            setItemFooterTypeIsShow(false)
            setOnLoadMoreListener {
                mModel.currPage = mModel.currPage + 1
                mModel.getVoteWorkList(voteId ?: "")
            }
            onVotoLsItemClickListener = object : GridVoteLsAdapter.OnVoteLsItemClickListener {
                override fun onVoteItem(position: Int, item: VoteWorkBean) {
                    mModel.voteWork(item.id.toString(), position)
                }

            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_vote_search
    }

    override fun setTitle(): String {
        return "搜索"
    }

    override fun injectVm(): Class<VoteSearchViewModel> {
        return VoteSearchViewModel::class.java
    }

    override fun initView() {
        initViewModel()
        mBinding.rvVoteSubTypes.run {
            adapter = voteTypeAdapter
            layoutManager = LinearLayoutManager(this@VoteSearchActivity, LinearLayoutManager.HORIZONTAL, false)
        }
        mBinding.rvVoteChildTypes.run {
            adapter = voteChildTypeAdapter
            layoutManager = LinearLayoutManager(this@VoteSearchActivity, LinearLayoutManager.HORIZONTAL, false)
        }
        mBinding.rvSearchContents.run {
            adapter = defaultWorkAdapter
            layoutManager = GridLayoutManager(this@VoteSearchActivity, 2, GridLayoutManager.VERTICAL, false)
        }
        mBinding.tvCanceSearch.onNoDoubleClick {
            mBinding.edtSearchVote.setText("")
            mModel.currPage = 1
            mModel.name = ""
            mBinding.rvSearchContents.smoothScrollToPosition(0)
            hideEmptyView()
            mModel.getVoteWorkList(voteId ?: "")
        }

        mBinding.edtSearchVote.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
//                mModel.name = ""
//                mModel.currPage = 1
//                mBinding.rvSearchContents.smoothScrollToPosition(0)
//
//                mModel.getVoteWorkList(voteId ?: "")
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var content: String? = mBinding.edtSearchVote.text.toString()
                if (!content.isNullOrEmpty() && content != mModel.name) {
                    mModel.currPage = 1
                    mModel.name = content
                    mBinding.rvSearchContents.smoothScrollToPosition(0)
                    hideEmptyView()
                    mModel.getVoteWorkList(voteId ?: "")
                } else {
                    if (!mModel.name.isNullOrEmpty()) {
                        mModel.name = ""
                        mModel.currPage = 1
                        hideEmptyView()
                        mBinding.rvSearchContents.smoothScrollToPosition(0)
                        mModel.getVoteWorkList(voteId ?: "")
                    }
                }
            }

        })
    }

    private fun hideEmptyView() {
        mBinding.vVoteEmpty.visibility = View.GONE
        mBinding.tvVoteNoMore.visibility = View.GONE
    }

    private fun initViewModel() {
        mModel.voteTypeLd.observe(this, Observer {
            voteTypeAdapter.clear()
            if (!it.isNullOrEmpty()) {
                it.add(0, VoteTypeBean().apply {
                    id = -1
                    name = "全部"
                })
                voteTypeAdapter.add(it)
                mBinding.rvVoteSubTypes.visibility = View.VISIBLE
            } else {
                mBinding.rvVoteSubTypes.visibility = View.GONE
            }
        })

        mModel.voteWorkLd.observe(this, Observer {
            if (it != null) {
                if (it != null) {
                    mModel.currPage = 1
                    mBinding.rvSearchContents.smoothScrollToPosition(0)
                    hideEmptyView()
                    mModel.getVoteWorkList(voteId ?: "")
                    if (!it.continueFlag) {
                        if (it.voteLimitStatus == 2) {
                            ToastUtils.showMessage("您今天的投票次数已用完")
                        }
                        if (it.voteLimitStatus == 3) {
                            ToastUtils.showMessage("该作品的投票次数已用完")
                        } else {
                            ToastUtils.showMessage("您的投票次数已用完")
                        }
                    } else {
                        if (it.voteLimitStatus == 0 || it.surplusCount == -1) {
                            ToastUtils.showMessage("投票成功")
                        } else {
                            ToastUtils.showMessage("投票成功，还可投${it.surplusCount}次")
                        }
                    }
                }
            }
        })

        mModel.voteWorksLd.observe(this, Observer {
            PageDealUtils().apply {
                onPageListener = object : PageDealUtils.OnPageListener {
                    override fun onEmpty() {
                        mBinding.rvSearchContents.visibility = View.GONE
                        mBinding.vVoteEmpty.visibility = View.VISIBLE
                    }

                    override fun onNoMoreData() {
                        mBinding.tvVoteNoMore.visibility = View.VISIBLE

                    }

                }
            }.pageDeal(mModel.currPage, it, defaultWorkAdapter)
            if (it != null && !it.datas.isNullOrEmpty()) {
                if (!mBinding.rvSearchContents.isVisible) {
                    mBinding.rvSearchContents.visibility = View.VISIBLE
                }
                defaultWorkAdapter.add(it.datas!!)
                defaultWorkAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun initData() {
        mModel.getVoteTypes(voteId ?: "")
        mModel.getVoteWorkList(voteId ?: "")
    }
}