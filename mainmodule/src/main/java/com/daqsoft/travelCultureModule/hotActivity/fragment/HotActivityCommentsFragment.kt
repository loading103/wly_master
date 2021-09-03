package com.daqsoft.travelCultureModule.hotActivity.fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.*
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.commentModule.CommentAdapter
import com.daqsoft.provider.databinding.ItemCommentBinding
import com.daqsoft.provider.network.comment.beans.CommentBean
import java.lang.StringBuilder

/**
 * @des 活动精彩评论页面
 * @author PuHua
 * @Date 2019/12/5 17:52
 */
class HotActivityCommentsFragment(beans: MutableList<CommentBean>) :
    BaseFragment<MainActivityCommentsFragmentBinding,
            BaseViewModel>() {
    /**
     * 评论列表
     */
    private val comments = beans

    /**
     * 评论适配器
     */
    private var commentAdapter: CommentAdapter? = null

    override fun getLayout(): Int = R.layout.main_activity_comments_fragment

    override fun initData() {


    }

    override fun initView() {

        val tagLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mBinding.rvComments.layoutManager = tagLayoutManager

        commentAdapter = CommentAdapter(context!!)
        mBinding.rvComments.adapter = commentAdapter

        commentAdapter!!.add(comments)

    }


    override fun injectVm(): Class<BaseViewModel> =
        BaseViewModel::class.java

}
