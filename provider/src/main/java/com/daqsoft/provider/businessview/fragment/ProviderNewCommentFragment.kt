package com.daqsoft.provider.businessview.fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.provider.R
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.businessview.adapter.*
import com.daqsoft.provider.businessview.event.UpdateCommentEvent
import com.daqsoft.provider.businessview.viewmodel.ProviderAddCommentLsViewModel
import com.daqsoft.provider.databinding.FragNewCommentPopBinding
import com.daqsoft.provider.network.comment.beans.CommentBean
import com.daqsoft.provider.network.comment.beans.ReplyBean
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.umeng.commonsdk.stateless.UMSLEnvelopeBuild.mContext
import org.greenrobot.eventbus.EventBus

/**
 * 评论回复
 */
class ProviderNewCommentFragment : DialogFragment() {

    private lateinit var mBinding: FragNewCommentPopBinding

    private lateinit var mModel: ProviderAddCommentLsViewModel

    private var topAdapter: ProviderCommentAdapter1? = null

    private var replyCommentAdapter: ProviderCommenReplyAdapter? = null

    private var topdate:MutableList<CommentBean> = mutableListOf()


    private var resouceType: String? = ""

    private var itembean: CommentBean? = null


    companion object {
        const val ITEM = "item"
        const val RESOURCE_TYPE = "resource_type"
        fun newInstance(item: CommentBean, resouceType: String): ProviderNewCommentFragment {
            var frag = ProviderNewCommentFragment()
            var bundle: Bundle = Bundle()
            bundle.putParcelable(ITEM, item)
            bundle.putString(RESOURCE_TYPE, resouceType)
            frag.arguments = bundle
            return frag
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.frag_new_comment_pop, container, false)
        initView()
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itembean = arguments?.getParcelable<CommentBean>(ProviderNewCommentFragment.ITEM)
        resouceType = arguments?.getString(ProviderNewCommentFragment.RESOURCE_TYPE, "")
        itembean?.let {
            mBinding.data=itembean

            mBinding.tvTitle.text="全部评论("+it.commentReplyPageData.totalCount+")"
            mBinding.tvTitle.setText("")
            topdate.clear()
            topdate.add(it)
            topAdapter?.clear()
            topAdapter?.setNewData(topdate)

            val name = SPUtils.getInstance().getString(SPKey.NICK_NAME)
            if(itembean?.vipNickName!=name){
                mBinding.llRoot1.visibility=View.GONE
            }else{
                mBinding.llRoot1.visibility=View.VISIBLE
            }
        }
    }


    fun initView() {


        mBinding.smartlayout.apply {
            setEnableRefresh(false)
            setEnableLoadMore(true)
            setRefreshFooter(ClassicsFooter(context).setDrawableSize(20f))
            setOnLoadMoreListener {
                mModel.pageManager.nexPageIndex
                itembean?.id?.let { it1 -> mModel.getReplyList(it1) }
            }
        }

        topAdapter = context?.let { ProviderCommentAdapter1(it, true) }
        mBinding.recyComments.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mBinding.recyComments.adapter = topAdapter

        replyCommentAdapter= context?.let { ProviderCommenReplyAdapter(it, true).apply {
            onLisitener=object : ProviderCommenReplyAdapter.OnItemLisitener{
                override fun onItemClick(item: ReplyBean) {
                    val name = SPUtils.getInstance().getString(SPKey.NICK_NAME)
                    if(itembean?.vipNickName!=name){
//                        ToastUtils.showMessage("不是自己哦")
                        return
                    }
                    if (addCommentPopFragment == null) {
                        addCommentPopFragment = ProviderReplyFragment.newInstance(item.commentId ?: "", item.id)
                        //  回复成成功刷新数据
                        addCommentPopFragment?.onReplySuccessLisitener=object :ProviderReplyFragment.OnReplySuccessLisitener{
                            override fun onReplysuccess() {
                                onReplySuccessLisitener?.onReplysuccess()
                                mModel?.pageManager.initPageIndex()
                                item?.commentId?.let { it1 -> mModel.getReplyList(it1) }
                            }
                        }
                    }
                    if (!addCommentPopFragment!!.isAdded) {
                        addCommentPopFragment?.show((context as FragmentActivity).supportFragmentManager, "story_add_comment")
                        Handler().postDelayed({
                            addCommentPopFragment?.pid=item.id
                        },200)
                    }
                }
            }
        }}
        replyCommentAdapter?.emptyViewShow=false
        mBinding.recyContent.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mBinding.recyContent.adapter = replyCommentAdapter

        mBinding.ivClose.setOnClickListener {
            dismiss()
        }
        mBinding.topView.setOnClickListener {
            dismiss()
        }
        mBinding.llRoot.setOnClickListener {
            itembean?.let { it1 -> showCommentPopup(it1) }
        }
    }

    fun initData() {
        mModel = ProviderAddCommentLsViewModel()
        mModel.pageManager.initPageIndex()
        itembean?.id?.let { it1 -> mModel.getReplyList(it1) }


        mModel.dataList.observe(this, Observer {
            if(it.isNullOrEmpty()){
                mBinding.smartlayout.finishLoadMore()
                return@Observer
            }

            mBinding.tvTitle.text="全部评论("+mModel.totleNumber+")"
            if (mModel.pageManager.isFirstIndex) {
                replyCommentAdapter?.clear()
                replyCommentAdapter?.setNewData(it)
                if( replyCommentAdapter?.getData()?.size==mModel.totleNumber){
                    mBinding.smartlayout.setNoMoreData(true)
                }else{
                    mBinding.smartlayout.setNoMoreData(false)
                }
            }else{
                mBinding.smartlayout.finishLoadMore()
                replyCommentAdapter?.add(it)
                if( replyCommentAdapter?.getData()?.size==mModel.totleNumber){
                    mBinding.smartlayout.setNoMoreData(true)
                }else{
                    mBinding.smartlayout.setNoMoreData(false)
                }
            }
        })

    }
    override fun onStart() {
        super.onStart()
        initDialogWindow()
        initData()
    }

    fun initDialogWindow() {
        val win = dialog!!.window
        // 一定要设置Background，如果不设置，window属性设置无效
        win.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.transparent)))
        val dm = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(dm)
        val params = win.attributes
        params.gravity = Gravity.BOTTOM
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        win.attributes = params
        win.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    /**
     * 显示弹窗
     */
    private var addCommentPopFragment: ProviderReplyFragment? = null
    private fun showCommentPopup(item: CommentBean) {
        if (addCommentPopFragment == null) {
            addCommentPopFragment = ProviderReplyFragment.newInstance(item.id ?: "", "")
            //  回复成成功刷新数据
            addCommentPopFragment?.onReplySuccessLisitener=object :ProviderReplyFragment.OnReplySuccessLisitener{
                override fun onReplysuccess() {
                    onReplySuccessLisitener?.onReplysuccess()
                    mModel?.pageManager.initPageIndex()

                    item?.id?.let { it1 -> mModel.getReplyList(it1) }
                }
            }
        }
        if (!addCommentPopFragment!!.isAdded) {
            addCommentPopFragment?.show((context as FragmentActivity).supportFragmentManager, "story_add_comment")
            Handler().postDelayed({ addCommentPopFragment?.pid=""},200)
        }
    }


    interface OnReplySuccessLisitener {
        fun  onReplysuccess()
    }

    var onReplySuccessLisitener: OnReplySuccessLisitener? = null
}