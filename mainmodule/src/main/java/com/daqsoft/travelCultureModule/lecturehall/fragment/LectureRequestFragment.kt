package com.daqsoft.travelCultureModule.lecturehall.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.FragLectureRequestionBinding
import com.daqsoft.provider.bean.LectureHall
import com.daqsoft.provider.bean.LectureRequestion
import com.daqsoft.travelCultureModule.lecturehall.adapter.LectureReuqestionAdapter
import com.daqsoft.travelCultureModule.lecturehall.viewmodel.LectureRequestionViewModel

/**
 * @Description 课程问答
 * @ClassName   LectureRequestFragment
 * @Author      luoyi
 * @Time        2020/6/16 11:42
 */
class LectureRequestFragment :
    BaseFragment<FragLectureRequestionBinding, LectureRequestionViewModel>() {


    var requestionAdapter: LectureReuqestionAdapter? = null

    var currPage: Int = 1
    var id: String? = ""

    companion object {
        const val ID: String = "id"
        fun newInstance(id: String): LectureRequestFragment {
            var frag = LectureRequestFragment()
            var bundle = Bundle()
            bundle.putString(ID, id)
            frag.arguments = bundle
            return frag
        }
    }

    override fun getLayout(): Int {
        return R.layout.frag_lecture_requestion
    }

    override fun injectVm(): Class<LectureRequestionViewModel> {
        return LectureRequestionViewModel::class.java
    }

    override fun initView() {
        initViewModel()
        requestionAdapter = LectureReuqestionAdapter(context!!)
        mBinding.rvLectureRequestions.adapter = requestionAdapter
        mBinding.rvLectureRequestions.layoutManager =
            LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
        requestionAdapter?.clear()
        requestionAdapter?.emptyViewShow = false
        requestionAdapter?.setOnLoadMoreListener {
            currPage = 1
            mModel.getLectureRequestion(id!!, currPage)
        }
    }

    private fun initViewModel() {
        mModel.lectureHallRequestLsLd.observe(this, Observer {
            dealPage(it)
        })
    }

    override fun initData() {
        id = arguments?.getString(ID)
        if (!id.isNullOrEmpty()) {
            mModel.getLectureRequestion(id!!, currPage)
        }
    }

    private fun dealPage(data: MutableList<LectureRequestion>?) {
        dissMissLoadingDialog()
        if (!mBinding.rvLectureRequestions.isVisible) {
            mBinding.rvLectureRequestions.visibility = View.VISIBLE
        }
        if (currPage == 1) {
            mBinding.rvLectureRequestions.smoothScrollToPosition(0)
            requestionAdapter?.clear()
            if (data.isNullOrEmpty()) {
                mBinding.vLectureEmpty.visibility = View.VISIBLE
                mBinding.rvLectureRequestions.visibility=View.GONE
            } else {
                mBinding.vLectureEmpty.visibility = View.GONE
                mBinding.rvLectureRequestions.visibility=View.VISIBLE
            }
        }
        if (!data.isNullOrEmpty()) {
            requestionAdapter?.add(data!!)
        }
        if (data.isNullOrEmpty() || data.size < 10) {
            requestionAdapter?.loadEnd()
        } else {
            requestionAdapter?.loadComplete()
        }
    }
}