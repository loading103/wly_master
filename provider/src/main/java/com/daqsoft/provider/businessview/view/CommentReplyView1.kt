package com.daqsoft.provider.businessview.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.R
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.businessview.adapter.CommentEmoticonsAdapter
import com.daqsoft.provider.businessview.fragment.ProviderNewCommentFragment
import com.daqsoft.provider.businessview.viewmodel.ProviderAddCommentLsViewModel
import com.daqsoft.provider.databinding.ItemReplyNewBinding
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.comment.beans.CommentBean
import com.daqsoft.provider.network.comment.beans.ReplyBean
import java.util.Observer

class CommentReplyView1 :LinearLayout {

    var showDatas:MutableList<ReplyBean> = mutableListOf()

    var mContext: Context? = null

    var showNumber: Int= 4

    var mBinding: ItemReplyNewBinding? = null

    var item: CommentBean? = null

    var totleNumber: Int? = null
    //评论回复弹窗
    var  commentFragment: ProviderNewCommentFragment?=null

    private lateinit var mModel: ProviderAddCommentLsViewModel

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context!!, attrs, defStyleAttr) {
        this.mContext = context

    }

    /**
     * 别处回复成功之后当前item刷新界面
     */
    public fun initRefreshData() {
        item?.id?.let {
            CommentRepository.service.getReplyList(it,"1", "10")
                .excute(object : BaseObserver<ReplyBean>() {
                    override fun onSuccess(response: BaseResponse<ReplyBean>) {
                        if (response.code == 0) {
                            item?.commentReplyPageData?.totalCount= response.page?.total!!
                            response.datas?.let { it1 -> setData(it1, item!!) }
                        }
                    }
                    override fun onFailed(response: BaseResponse<ReplyBean>) {
                    }
                })
        }
    }


    fun setData(
        datas: MutableList<ReplyBean>,
        item: CommentBean
    ){
        this.item=item
        if(datas.isNullOrEmpty()){
            return
        }
        removeAllViews()
        showDatas.clear()
        if(datas.size>showNumber){
            showDatas.addAll(datas.subList(0,4))
        }else{
            showDatas.addAll(datas)
        }
        showDatas.forEachIndexed { index, bean ->

            var binding: ItemReplyNewBinding?   =  DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_reply_new, this, false)
            if(index==3){
                binding?.tvName?.text="查看${item.commentReplyPageData.totalCount}条回复"
                val drawableLeft: Drawable = resources.getDrawable(R.mipmap.activity_details_right)
                binding?.tvName?.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableLeft, null)
                binding?.tvName?.compoundDrawablePadding = 15
                binding?.root?.setOnClickListener {
                    if (commentFragment == null) {
                        commentFragment = ProviderNewCommentFragment.newInstance(item, "")
                        commentFragment?.onReplySuccessLisitener=object :ProviderNewCommentFragment.OnReplySuccessLisitener{
                            override fun onReplysuccess() {
                                initRefreshData()
                            }
                        }

                    }
                    if (!commentFragment!!.isAdded) {
                        commentFragment?.show((mContext as FragmentActivity).supportFragmentManager, "new_comment")
                    }
                }
            }else{
                binding?.tvName?.text=bean.getContent()
                // 评论表情
                if (bean.emoticonsUrl.isNullOrEmpty()) {
                    mBinding?.rvEmoticons?.visibility = View.GONE
                } else {
                    var emoticonsCommentAdapter: CommentEmoticonsAdapter = CommentEmoticonsAdapter().apply { emptyViewShow = false }
                    mBinding?.rvEmoticons?.visibility = View.VISIBLE
                    mBinding?.rvEmoticons?.adapter = emoticonsCommentAdapter
                    mBinding?.rvEmoticons?.layoutManager = GridLayoutManager(mContext!!, 5, GridLayoutManager.VERTICAL, false)
                    emoticonsCommentAdapter.clear()
                    emoticonsCommentAdapter.add(item.emoticonsUrl!!)
                }
            }

            addView(binding?.root)
        }
    }




    /**
     * 添加一个item
     */
    fun addOneView(reply: String){
        var binding: ItemReplyNewBinding?   =  DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_reply_new, this, false)
        binding?.tvName?.text=reply
        addView(binding?.root,0)
    }
    interface OnReplySuccessLisitener {
        fun  onReplysuccess()
    }

    var onReplySuccessLisitener: OnReplySuccessLisitener? = null
}
