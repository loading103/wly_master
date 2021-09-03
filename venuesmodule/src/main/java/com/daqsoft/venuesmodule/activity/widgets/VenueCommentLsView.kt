package com.daqsoft.venuesmodule.activity.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.businessview.adapter.ProviderCommentNewAdapter
import com.daqsoft.provider.network.comment.beans.CommentBean
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.IncludeVenueDetailsCommentBinding

/**
 * @Description
 * @ClassName   VenueCommentLsView
 * @Author      luoyi
 * @Time        2020/3/27 17:03
 */
class VenueCommentLsView : FrameLayout {

    var mContext: Context? = null

    var binding: IncludeVenueDetailsCommentBinding? = null

    /**
     * 活动数据集
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
            R.layout.include_venue_details_comment,
            this,
            false
        )
        addView(binding!!.root)
    }

    /**
     * 设置数据
     */
    fun setData(data: MutableList<CommentBean>) {
        datas.clear()
        datas.addAll(data)
        updateUi()
    }

    /**
     *  修改评论数目
     */
    fun updateCommentNum(num: Int, resourceId: String, type: String) {
        if (num > 2) {
            binding?.tvVenuesDetailsCommentCount?.text = mContext?.getString(R.string.venue_detail_comment_num, num.toString())
            binding?.tvVenuesDetailsCommentCount?.visibility = View.VISIBLE
            binding?.tvVenuesDetailsCommentCount?.onNoDoubleClick {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_ACTIVITY_COMMENT)
                    .withString("id", resourceId)
                    .withString("type", type)
                    .navigation()
            }
        }
    }

    fun updateUi() {
        var adapater = ProviderCommentNewAdapter(mContext!!,false,"")
        binding?.recyclerVenuesDetailsComment?.layoutManager = LinearLayoutManager(
            mContext!!, LinearLayoutManager.VERTICAL,
            false
        )
        binding?.recyclerVenuesDetailsComment?.adapter = adapater
        adapater.add(datas)
    }


}