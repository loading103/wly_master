package com.daqsoft.provider.businessview.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.businessview.adapter.ProviderCommentNewAdapter
import com.daqsoft.provider.databinding.LayoutProviderCommentsNewBinding
import com.daqsoft.provider.network.comment.beans.CommentBean

/**
 * @Description 用户评价
 * @ClassName   ProviderCommentsView
 * @Author      luoyi
 * @Time        2020/4/2 15:15
 */
class ProviderNewCommentsView : FrameLayout {

    var mContext: Context? = null

    var binding: LayoutProviderCommentsNewBinding? = null

    var type: String = ""
    /**
     * 评论数据集
     */
    var datas: MutableList<CommentBean> = mutableListOf()

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        initView()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.layout_provider_comments_new,
            this,
            false
        )

        addView(binding!!.root)
    }

    /**
     * 设置数据
     */
    fun setData(data: MutableList<CommentBean>, commentNum: Int) {
        setData(data)
    }

    fun setData(data: MutableList<CommentBean>) {
        datas.clear()
        datas.addAll(data)
        updateUi()
    }

    /**
     *  修改评论数目
     */
    fun updateCommentNum(num: Int, resourceId: String, type: String, contentTitle: String = "") {
        if (num > 0) {
//            binding?.tvProviderDetailsCommentCount?.text = mContext?.getString(R.string.provider_comment_num, num.toString())
            binding?.tvProviderDetailsCommentCount?.visibility = View.VISIBLE
            binding?.tvProviderDetailsCommentCount?.onNoDoubleClick {
                ARouter.getInstance()
                    .build(ARouterPath.Provider.PROVIDER_COMMENT_LS)
                    .withString("id", resourceId)
                    .withString("type", type)
                    .withString("contentTitle", contentTitle)
                    .navigation()
            }
        }
    }

    fun updateUi() {
        var adapater = ProviderCommentNewAdapter(mContext!!, false, type)
        adapater?.emptyViewShow = false
        binding?.recyclerProviderDetailsComment?.layoutManager = LinearLayoutManager(mContext!!, LinearLayoutManager.VERTICAL, false)
        binding?.recyclerProviderDetailsComment?.adapter = adapater
        adapater.add(datas)
    }

    fun setVisible(isShow: Boolean) {
        visibility = if (isShow) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}