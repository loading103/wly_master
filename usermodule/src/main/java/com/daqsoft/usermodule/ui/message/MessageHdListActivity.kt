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
import com.daqsoft.usermodule.ui.message.adapter.MessageHdTabAdapter
import com.daqsoft.usermodule.ui.message.adapter.MessageTabAdapter
import com.daqsoft.usermodule.ui.message.adapter.MsgPageAdapter
import com.daqsoft.usermodule.ui.message.fragment.*
import org.greenrobot.eventbus.EventBus

/**
 * @Description 互动消息
 */
@Route(path = ARouterPath.UserModule.USER_MEASSAGE_HD_LIST_DETAIL)
class MessageHdListActivity : TitleBarActivity<ActivityMessageListBinding, MessageHdistViewModel>() {

    @JvmField
    @Autowired
    var classify: String = ""

    @JvmField
    @Autowired
    var selected: String = ""

    var type: String = "1"

    lateinit var  msgPageAdpater: MsgPageAdapter

    var fragmentList : MutableList<Fragment> = mutableListOf()

    private val msgTabAdapter: MessageHdTabAdapter by lazy {
        MessageHdTabAdapter().apply {
            emptyViewShow = false
            onItemClickListener = object : MessageHdTabAdapter.OnItemClickListener {
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
        return "互动消息"
    }

    override fun injectVm(): Class<MessageHdistViewModel> {
        return MessageHdistViewModel::class.java
    }

    override fun initView() {
        mBinding.recyMsgTabs.apply {
            adapter = msgTabAdapter
            msgTabAdapter.setNewData(mModel.messagelists)
        }
        fragmentList.add(MessageGoodFragment())
        fragmentList.add(MessageCollectFragment())
        fragmentList.add(MessageFollowFragment())
        fragmentList.add(MessageAskFragment())
        fragmentList.add(MessagePkFragment())
        msgPageAdpater= MsgPageAdapter(supportFragmentManager,fragmentList)
        mBinding.vpMsgList.offscreenPageLimit=5
        mBinding.vpMsgList.adapter = msgPageAdpater
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



class MessageHdistViewModel : BaseViewModel() {
    val number = MutableLiveData<Int>()
    /**
     * 顶部列表
     */
    var messagelists = mutableListOf<MessageTopBean>(
        MessageTopBean(R.drawable.user_selector_top_1, "点赞", "0", true,"1"),
        MessageTopBean(R.drawable.user_selector_top_2, "收藏", "0", false,"2"),
        MessageTopBean(R.drawable.user_selector_top_3, "关注", "0", false,"3"),
        MessageTopBean(R.drawable.user_selector_top_4, "问答", "0", false,"4"),
        MessageTopBean(R.drawable.user_selector_top_5, "作品PK", "0", false,"5")
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
                    EventBus.getDefault().post(UpdateMessageNumberEvent())
                }

                override fun onFailed(response: BaseResponse<String>) {
                    super.onFailed(response)
                }
            })
    }
}