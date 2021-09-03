package com.daqsoft.travelCultureModule.lecturehall.fragment

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.FragLectureCommentBinding
import com.daqsoft.provider.bean.LectureRequestion
import com.daqsoft.provider.businessview.event.UpdateCommentEvent
import com.daqsoft.provider.network.comment.beans.CommentBean
import com.daqsoft.travelCultureModule.lecturehall.adapter.LectureCommentAdapter
import com.daqsoft.travelCultureModule.lecturehall.viewmodel.LectureCommentViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @Description 课程评论
 * @ClassName   LectureCommentFragment
 * @Author      luoyi
 * @Time        2020/6/16 11:41
 */
class LectureCommentFragment : BaseFragment<FragLectureCommentBinding, LectureCommentViewModel>() {


    var id: String? = ""
    var minHeight: Float? = 0F
    var currPage: Int = 1

    var commentAdapter: LectureCommentAdapter? = null

    companion object {
        const val ID = "id"
        const val MINE_HEIGHT = "min_height"
        fun newInstance(id: String, minHeight: Float): LectureCommentFragment {
            var frag = LectureCommentFragment()
            var bundle: Bundle = Bundle()
            bundle.putString(ID, id)
            bundle.putFloat(MINE_HEIGHT, minHeight)
            frag.arguments = bundle
            return frag
        }
    }

    override fun getLayout(): Int {
        return R.layout.frag_lecture_comment
    }

    override fun injectVm(): Class<LectureCommentViewModel> {
        return LectureCommentViewModel::class.java
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        commentAdapter = LectureCommentAdapter(context!!)
        mBinding.rvLectureComments.adapter = commentAdapter
        mBinding.rvLectureComments.layoutManager =
            LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
        commentAdapter?.setOnLoadMoreListener {
            currPage = currPage + 1
            mModel.getLectureCommentList(id ?: "", currPage)
        }
        initViewModel()
    }

    private fun initViewModel() {
        mModel.commentBeans.observe(this, Observer {
            dealPage(it)
        })
    }

    override fun initData() {
        id = arguments?.getString(ID)
        minHeight = arguments?.getFloat(MINE_HEIGHT)
        if (!id.isNullOrEmpty()) {
            mModel.getLectureCommentList(id!!, currPage)
        }

    }

    private fun dealPage(data: MutableList<CommentBean>?) {
        dissMissLoadingDialog()
        if (!mBinding.rvLectureComments.isVisible) {
            mBinding.rvLectureComments.visibility = View.VISIBLE
        }
        if (currPage == 1) {
            mBinding.rvLectureComments.smoothScrollToPosition(0)
            commentAdapter?.clear()
            if (data.isNullOrEmpty()) {
                if (minHeight != null && minHeight!! > 0F) {
                    var param = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        minHeight!!.toInt()
                    )
                    mBinding.rvLectureComments.layoutParams = param
                }
            }
        }
        if (!data.isNullOrEmpty()) {
            commentAdapter?.add(data!!)
        }
        if (data.isNullOrEmpty() || data.size < 10) {
            commentAdapter?.loadEnd()
        } else {
            commentAdapter?.loadComplete()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRecordEvent(event: UpdateCommentEvent) {
        currPage=1
        mModel.getLectureCommentList(id ?: "", currPage)
    }
}