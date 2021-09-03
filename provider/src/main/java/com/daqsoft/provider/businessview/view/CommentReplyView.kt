package com.daqsoft.provider.businessview.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.R
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.businessview.adapter.ProviderReplyAdapter
import com.daqsoft.provider.businessview.event.UpdateCommentEvent
import com.daqsoft.provider.businessview.fragment.ProviderNewCommentFragment
import com.daqsoft.provider.businessview.fragment.ProviderReplyFragment
import com.daqsoft.provider.network.comment.beans.CommentBean
import com.daqsoft.provider.network.comment.beans.ReplyBean
import org.greenrobot.eventbus.EventBus

class CommentReplyView :LinearLayout {


    var mContext: Context? = null

    var showNumber: Int= 4

    var item: CommentBean? = null

    var totleNumber: Int? = null
    //评论回复弹窗
    var  commentFragment: ProviderNewCommentFragment?=null

    private var addCommentPopFragment: ProviderReplyFragment? = null

   var recyComments: RecyclerView?=null

    private var replyCommentAdapter: ProviderReplyAdapter? = null

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context!!, attrs, defStyleAttr) {
        this.mContext = context
        initView()
    }

    private fun initView() {

        replyCommentAdapter= mContext?.let { it -> ProviderReplyAdapter(it) }
        replyCommentAdapter?.onLisitener=object : ProviderReplyAdapter.OnLastItemLisitener{
            override fun onLastItemClick(islast: Boolean, item1: ReplyBean) {
                if(islast){
                    if (commentFragment == null) {
                        commentFragment = item?.let { ProviderNewCommentFragment.newInstance(item!!, "") }
                        commentFragment?.onReplySuccessLisitener=object :ProviderNewCommentFragment.OnReplySuccessLisitener{
                            override fun onReplysuccess() {
                                initRefreshData()
                                EventBus.getDefault().post(UpdateCommentEvent())
                            }
                        }
                    }
                    if (!commentFragment!!.isAdded) {
                        commentFragment?.show((mContext as FragmentActivity).supportFragmentManager, "new_comment")
                    }
                }else{
                    val name = SPUtils.getInstance().getString(SPKey.NICK_NAME)
                    if(item?.vipNickName!=name){
                        return
                    }
                    if (addCommentPopFragment == null) {
                        addCommentPopFragment = item?.let { ProviderReplyFragment.newInstance(item1.commentId ?: "", item1.id ?: "") }
                        //  回复成成功刷新数据
                        addCommentPopFragment?.onReplySuccessLisitener=object : ProviderReplyFragment.OnReplySuccessLisitener{
                            override fun onReplysuccess() {
                                initRefreshData()
                                EventBus.getDefault().post(UpdateCommentEvent())
                            }
                        }
                    }
                    if (!addCommentPopFragment!!.isAdded) {
                        addCommentPopFragment?.show((context as FragmentActivity).supportFragmentManager, "story_add_comment")
                    }
                }
            }
        }


        var view = LayoutInflater.from(mContext).inflate(R.layout.frag_new_reply, this@CommentReplyView)
        recyComments=view.findViewById(R.id.recy_comments)
        recyComments?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyComments?.adapter = replyCommentAdapter
    }

    /**
     * 别处回复成功之后当前item刷新界面
     */
    fun initRefreshData() {
    }


    fun setData(
        datas: MutableList<ReplyBean>,
        item: CommentBean){
        this.item=item
        if(datas.isNullOrEmpty()){
            return
        }
        replyCommentAdapter?.clear()
        replyCommentAdapter?.totleSize=item.commentReplyPageData.totalCount
        if(datas.size>showNumber){
            replyCommentAdapter?.add(datas.subList(0,4))
        }else{
            replyCommentAdapter?.add(datas)
        }
        replyCommentAdapter?.notifyDataSetChanged()

    }

}
