package com.daqsoft.usermodule.ui.message.fragment

import android.view.View
import androidx.lifecycle.Observer
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.FragMessageListBinding
import com.daqsoft.usermodule.ui.message.MessageHdListActivity
import com.daqsoft.usermodule.ui.message.MessageListActivity
import com.daqsoft.usermodule.ui.message.adapter.MessageListAdapter
import com.daqsoft.usermodule.ui.message.viewmodel.MessageTypeListViewModel
import com.scwang.smartrefresh.layout.footer.ClassicsFooter

/**
 * @Description 回复
 * @ClassName   MessageListFragment
 */
class MessageReplyFragment: BaseFragment<FragMessageListBinding, MessageTypeListViewModel>() {


    private val type="4"
    private var classify ="0"
    private val msgLsAdapter: MessageListAdapter by lazy {
        MessageListAdapter()
    }

    override fun getLayout(): Int {
        return R.layout.frag_message_list
    }

    override fun injectVm(): Class<MessageTypeListViewModel> {
        return MessageTypeListViewModel::class.java
    }

    override fun initView() {
        initViewModel()
        mBinding.recyMsgCenters.apply {
            adapter = msgLsAdapter
        }
        mBinding.srlMsgCenter.apply {
            setEnableLoadMore(true)
            setRefreshFooter(ClassicsFooter(context).setDrawableSize(20f))
            setOnRefreshListener {
                mModel.pageManager.initPageIndex()
                mModel.getListData(classify,type)
            }
            setOnLoadMoreListener{
                mModel.pageManager.nexPageIndex
                mModel.getListData(classify,type)
            }
        }
    }

    override fun initData() {
        classify = (activity as MessageListActivity).classify.toString()
        msgLsAdapter?.setLayoutType(classify,type)
        mModel.getListData(classify,type)
    }

    private fun initViewModel() {
        mModel.datas?.observe(this, Observer {
            if (mModel.pageManager.isFirstIndex) {
                msgLsAdapter.datas.clear()
                msgLsAdapter.datas.addAll(it)
                if( msgLsAdapter.datas.size==mModel.totleNumber){
                    mBinding.srlMsgCenter.finishRefreshWithNoMoreData()
                }else{
                    mBinding.srlMsgCenter.finishRefresh()
                }
            }else{
                msgLsAdapter.datas.addAll(it)
                if( msgLsAdapter.datas.size==mModel.totleNumber){
                    mBinding.srlMsgCenter.finishLoadMoreWithNoMoreData()
                }else{
                    mBinding.srlMsgCenter.finishLoadMore()
                }
            }
            if(msgLsAdapter.datas.isEmpty()){
                mBinding.llEmpty.llRppt.visibility=View.VISIBLE
            }else{
                mBinding.llEmpty.llRppt.visibility=View.GONE
            }
            msgLsAdapter.notifyDataSetChanged()
        })
    }


     fun refreshData(classify: String, type: String) {
        msgLsAdapter?.setLayoutType(classify,type)
        mModel.getListData(classify,type)
    }


}
