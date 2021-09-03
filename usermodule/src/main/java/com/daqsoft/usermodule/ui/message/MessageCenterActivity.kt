package com.daqsoft.usermodule.ui.message

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.NotifityUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.MessageRootBean
import com.daqsoft.provider.bean.MineMessageBean
import com.daqsoft.provider.event.UpdateMessageNumberEvent
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityMessageCenterBinding
import com.daqsoft.usermodule.ui.message.adapter.MessageCenterAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * @Description 消息中心 列表
 * @ClassName   MesageCenterActivity
 * @Time        2020/12/16 11:14
 */
@Route(path = ARouterPath.UserModule.USER_MEASSAGE_CENTER_ACTIVITY)
class MessageCenterActivity : TitleBarActivity<ActivityMessageCenterBinding, MessageCenterViewModel>() {

    private val msgAdapter: MessageCenterAdapter by lazy {
        MessageCenterAdapter()
    }

    override fun getLayout(): Int {
        return R.layout.activity_message_center
    }

    override fun setTitle(): String {
        return getString(R.string.mine_message_center)
    }

    override fun injectVm(): Class<MessageCenterViewModel> {
        return MessageCenterViewModel::class.java
    }

    override fun initView() {
        EventBus.getDefault().register(this)


        if(NotifityUtils.isNotificationEnabled()){
            mBinding.rlTopMessageCenter.visibility=View.GONE
        }else{
            mBinding.rlTopMessageCenter.visibility=View.VISIBLE
        }

        mBinding.recyMsgCenters.layoutManager=MsgLayoutManager
        mBinding.recyMsgCenters.adapter = msgAdapter
        msgAdapter.setNewData(mModel.messagelists)
        mBinding.srlMsgCenter.apply {
            setOnRefreshListener {
                mModel.getListDatas()
                mModel.getVotDatas()
            }
        }
        // 全部已读
        mBinding.tvRead.onNoDoubleClick {
            AlertDialog.Builder(this)
                .setTitle("提示！")
                .setMessage("确认清空全部新消息提醒")
                .setPositiveButton("确定") { dialog, _ ->
                    dialog.dismiss()
                    showLoadingDialog()
                    mModel.setAllReaded()
                }
                .setNegativeButton("取消") { dialog, _ ->
                    dialog.dismiss()
                }.create().show()
        }
        mBinding.tvTipMessage.onNoDoubleClick {
            mBinding.rlTopMessageCenter.visibility=View.GONE
            NotifityUtils.openNotice()
        }
    }


    val MsgLayoutManager = LinearLayoutManager(
        this, LinearLayoutManager.VERTICAL,
        false
    )
    override fun initData() {
        initViewModel()
        showLoadingDialog()
        mModel.getListDatas()
        mModel.getVotDatas()
    }

    private fun initViewModel() {
        mModel.datas.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.srlMsgCenter.finishRefresh()
            msgAdapter.notifyDataSetChanged()
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(NotifityUtils.isNotificationEnabled()){
            mBinding.rlTopMessageCenter.visibility=View.GONE
        }else{
            mBinding.rlTopMessageCenter.visibility=View.VISIBLE
        }

    }


    /**
     * 刷新未读数
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refresh(event: UpdateMessageNumberEvent) {
        mModel.getListDatas()
        mModel.getVotDatas()
    }

}


class MessageCenterViewModel : BaseViewModel() {
    val datas = MutableLiveData<MutableList<MessageRootBean>>()
    var messagelists = mutableListOf<MessageRootBean>(
        MessageRootBean(R.mipmap.mine_notification_icon_system, "系统消息", "1",  "0", "暂无消息"),
        MessageRootBean(R.mipmap.mine_notification_icon_hudong, "互动消息", "2", "0","暂无消息"),
        MessageRootBean(R.mipmap.mine_notification_icon_activity, "活动变更通知", "3", "0","暂无消息"),
        MessageRootBean(R.mipmap.mine_notification_icon_volunteer, "志愿服务消息", "4", "0","暂无消息"),
        MessageRootBean(R.mipmap.mine_notification_icon_complain, "投诉进度", "5", "0","暂无消息")
    )

    /**
     * 获取数据
     */
    fun getListDatas() {
        UserRepository.userService.getMessageList()
            .excute(object : BaseObserver<MessageRootBean>() {
                override fun onSuccess(response: BaseResponse<MessageRootBean>) {
                    setData(response.datas)
                }

                override fun onFailed(response: BaseResponse<MessageRootBean>) {
                    super.onFailed(response)
                    datas.postValue(null)
                }
            })
    }

    /**
     * 获取志愿者数据
     */
    fun getVotDatas() {
        UserRepository.userService.getVotMessageList()
            .excute(object : BaseObserver<MessageRootBean>() {
                override fun onSuccess(response: BaseResponse<MessageRootBean>) {
                    setVotData(response.datas)
                }
            })
    }

    private fun setData(data: MutableList<MessageRootBean>?) {
        messagelists?.forEachIndexed { i, list ->
            data?.forEachIndexed { j, bean ->
                if(bean.classify==list.classify){
                    list.createTime=bean.createTime
                    list.title=bean.title
                    if(!TextUtils.isEmpty(bean.type)){
                        list.selected=(bean.type.toInt()-1).toString()
                    }
                    list.messageNum=bean.messageNum
                }
            }
            datas.postValue(messagelists)
        }
    }

    /**
     * 志愿消息单独出来的
     */
    private fun setVotData(data: MutableList<MessageRootBean>?) {
        messagelists?.forEachIndexed { i, list ->
            data?.forEachIndexed { j, bean ->
                if(list.classify=="4"){
                    if(bean.createTime!=null){
                        list.createTime=bean.createTime.split(" ")[0]
                    }
                    list.title=bean.getVotMessage()

                }
            }
        }
        getVotedNumber()
        datas.postValue(messagelists)
    }
    /**
     * 全部已读
     */
    fun setAllReaded() {
        UserRepository.userService.ReadAllMessage()
            .excute(object : BaseObserver<String>(mPresenter) {
                override fun onSuccess(response: BaseResponse<String>) {
                    messagelists.forEach{
                        it.messageNum="0"
                    }
                    datas.postValue(messagelists)
                }

                override fun onFailed(response: BaseResponse<String>) {
                    super.onFailed(response)
                    datas.postValue(messagelists)
                }
            })
    }

    /**
     * 志愿消息未读书单独出来的
     */
    fun getVotedNumber() {
        UserRepository.userService.getNoReadMessage()
            .excute(object : BaseObserver<MineMessageBean>() {
                override fun onSuccess(response: BaseResponse<MineMessageBean>) {
                    if (response.code == 0 && response.data != null && !TextUtils.isEmpty(response?.data?.num)) {
                        if(response?.data?.num?.toInt()!! >0){
                            messagelists.forEach{
                                if(it.name=="志愿服务消息"){
                                    it.messageNum=response?.data?.num!!
                                }
                            }
                            datas.postValue(messagelists)
                        }
                    }
                }
            })
    }

}