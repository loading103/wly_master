package com.daqsoft.usermodule.ui.message

import android.text.TextUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.MessageTopBean
import com.daqsoft.provider.bean.MessageTopNumberBean
import com.daqsoft.provider.event.UpdateMessageNumberEvent
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityMessageListBinding
import com.daqsoft.usermodule.ui.message.adapter.MessageTabAdapter
import com.daqsoft.usermodule.ui.message.adapter.MsgPageAdapter
import com.daqsoft.usermodule.ui.message.fragment.*
import org.greenrobot.eventbus.EventBus

/**
 * @Description 系统消息
 * @ClassName   MessageListActivity
 * @Author      luoyi
 * @Time        2020/12/16 14:09
 */
@Route(path = ARouterPath.UserModule.USER_MEASSAGE_LIST_DETAIL)
class MessageListActivity : TitleBarActivity<ActivityMessageListBinding, MessageListViewModel>() {

    @JvmField
    @Autowired
    var classify: String = ""


    @JvmField
    @Autowired
    var selected: String = ""


    var type: String = "1"

    lateinit var  msgPageAdpater: MsgPageAdapter

    var fragmentList : MutableList<Fragment> = mutableListOf()

    private val msgTabAdapter: MessageTabAdapter by lazy {
        MessageTabAdapter().apply {
            emptyViewShow = false
            onItemClickListener = object : MessageTabAdapter.OnItemClickListener {
                override fun onItemClick(position:Int, item: MessageTopBean) {
                    mBinding.vpMsgList.currentItem=position
                    if(!TextUtils.isEmpty( item.messageNum) && item.messageNum!="0"){
                        item.messageNum="0"
                        notifyDataSetChanged()
                    }
                    mModel.setMessageReaded(classify.toInt(),item.id.toInt())
                }
            }
        }
    }


    override fun getLayout(): Int {
        return R.layout.activity_message_list
    }

    override fun setTitle(): String {
        return "系统消息"
    }

    override fun injectVm(): Class<MessageListViewModel> {
        return MessageListViewModel::class.java
    }

    override fun initView() {
        mBinding.recyMsgTabs.apply {
            adapter = msgTabAdapter
            msgTabAdapter.setNewData(mModel.messagelists)
        }
        fragmentList.add(MessageNoticeFragment())
        fragmentList.add(MessageActivityFragment())
        fragmentList.add(MessageLevelFragment())
        fragmentList.add(MessageReplyFragment())
        fragmentList.add(MessageTaskFragment())
        msgPageAdpater= MsgPageAdapter(supportFragmentManager,fragmentList)
        mBinding.vpMsgList.offscreenPageLimit=5
        mBinding.vpMsgList.adapter = msgPageAdpater
        mBinding.vpMsgList.currentItem = 0
        initViewModel()
    }


    override fun initData() {
        mModel.getTopListDatas(classify)
    }

    /**
     * 默认到选中页界面
     */
    private var  chooseIndex=0
    private fun initViewModel() {
        mModel.number.observe(this, Observer {
            mBinding.vpMsgList.currentItem = selected.toInt()
            // 默认跳转到选中页面
            mModel.messagelists.forEachIndexed { index, messageTopBean ->
                messageTopBean.choosed = index==selected.toInt()
                if(index==selected.toInt()){
                    chooseIndex=index
                    messageTopBean.messageNum="0"
                    mModel.setMessageReaded(classify.toInt(),messageTopBean.id.toInt())
                }
            }
            msgTabAdapter?.notifyDataSetChanged()
            mBinding.recyMsgTabs?.scrollToPosition(chooseIndex)
        })
    }
}



class MessageListViewModel : BaseViewModel() {
    val number = MutableLiveData<Int>()

    val unDataRead = MutableLiveData<Boolean>()
    /**
     * 顶部列表
     */
    var messagelists = mutableListOf<MessageTopBean>(
        MessageTopBean(R.drawable.user_selector_top_1, "通知", "0", true,"1"),
        MessageTopBean(R.drawable.user_selector_top_2, "活动与邀请", "0", false,"2"),
        MessageTopBean(R.drawable.user_selector_top_3, "等级变动", "0", false,"3"),
        MessageTopBean(R.drawable.user_selector_top_4, "回复", "0", false,"4"),
        MessageTopBean(R.drawable.user_selector_top_5, "任务提醒", "0", false,"5")
    )

    /**
     * 获取顶部未读数数据
     */
    fun getTopListDatas(classify: String) {
        mPresenter?.value?.loading = false
        UserRepository.userService.getMessageTopNumber(classify)
            .excute(object : BaseObserver<MessageTopNumberBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<MessageTopNumberBean>) {
                    setData(response.datas)
                }
            })
    }


    private fun setData(data: MutableList<MessageTopNumberBean>?) {
        messagelists?.forEachIndexed { i, list ->
            data?.forEachIndexed { j, bean ->
                if(bean.type==list.id){
                    list.messageNum=bean.num
                }
            }
            number.postValue(0)
        }
    }
    /**
     * 获取顶部未读数数据
     */
    fun setMessageReaded(classify: Int,type: Int) {
        UserRepository.userService.ReadMessage(classify,type)
            .excute(object : BaseObserver<String>() {
                override fun onSuccess(response: BaseResponse<String>) {
                    unDataRead.postValue(true)
                    EventBus.getDefault().post(UpdateMessageNumberEvent())
                }

                override fun onFailed(response: BaseResponse<String>) {
                    unDataRead.postValue(false)
                    super.onFailed(response)
                }
            })
    }


}