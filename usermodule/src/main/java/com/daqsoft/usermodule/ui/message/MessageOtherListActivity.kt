package com.daqsoft.usermodule.ui.message

import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.provider.ARouterPath
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityOtherMessageListBinding
import com.daqsoft.usermodule.ui.message.adapter.MessageOtherListAdapter
import com.daqsoft.usermodule.ui.message.viewmodel.MessageTypeListViewModel
import com.scwang.smartrefresh.layout.footer.ClassicsFooter

/**
 * @Description 活动变更 志愿服务消息 投诉进度
 */
@Route(path = ARouterPath.UserModule.USER_MEASSAGE_QT_LIST_DETAIL)
class MessageOtherListActivity : TitleBarActivity<ActivityOtherMessageListBinding, MessageTypeListViewModel>() {

    @JvmField
    @Autowired
    var classify: String = ""

    private val msgAdapter: MessageOtherListAdapter by lazy {
        MessageOtherListAdapter()
    }

    override fun getLayout(): Int {
        return R.layout.activity_other_message_list
    }

    override fun setTitle(): String {
        if(classify=="3"){
            return "活动变更"
        }else  if(classify=="4"){
            return "志愿服务消息"
        }else  if(classify=="5"){
            return "投诉进度"
        }
        return "消息中心"
    }
    override fun injectVm(): Class<MessageTypeListViewModel> {
        return MessageTypeListViewModel::class.java
    }

    override fun initView() {
        initViewModel()
        mModel.pageManager.initPageIndex()
        msgAdapter?.setLayoutType(classify)

        mBinding.recyMsgCenters.apply {
            adapter = msgAdapter
        }
        mBinding.srlMsgCenter.apply {
            setEnableLoadMore(true)
            setRefreshFooter(ClassicsFooter(context).setDrawableSize(20f))
            setOnRefreshListener {
                mModel.pageManager.initPageIndex()
                getListData()
            }
            setOnLoadMoreListener{
                mModel.pageManager.nexPageIndex
                getListData()
            }
        }
    }

    override fun initData() {
        showLoadingDialog()
        getListData()
    }

    private fun getListData() {
        if(classify=="4"){
            mModel.setVotedNumberReaded()
            // 设置消息已读
            mModel.getVotDatas()
        }else{
            mModel.getListData(classify,"")
            // 设置消息已读
            mModel.setMessageReaded(classify.toInt(),0)
        }

    }

    private fun initViewModel() {
        mModel.datas?.observe(this, Observer {
            dissMissLoadingDialog()
            if (mModel.pageManager.isFirstIndex) {
                msgAdapter.datas.clear()
                msgAdapter.datas.addAll(it)
                if( msgAdapter.datas.size==mModel.totleNumber){
                    mBinding.srlMsgCenter.finishRefreshWithNoMoreData()
                }else{
                    mBinding.srlMsgCenter.finishRefresh()
                }
            }else{
                msgAdapter.datas.addAll(it)
                if( msgAdapter.datas.size==mModel.totleNumber){
                    mBinding.srlMsgCenter.finishLoadMoreWithNoMoreData()
                }else{
                    mBinding.srlMsgCenter.finishLoadMore()
                }
            }
            if(msgAdapter.datas.isEmpty()){
                mBinding.llEmpty.llRppt.visibility=View.VISIBLE
            }else{
                mBinding.llEmpty.llRppt.visibility=View.GONE
            }
            msgAdapter.notifyDataSetChanged()
        })
    }

}