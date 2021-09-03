package com.daqsoft.travelCultureModule.lecturehall

import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.PageDealUtils
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityMineLectureReqBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.LectureHall
import com.daqsoft.provider.bean.LectureRequestion
import com.daqsoft.travelCultureModule.lecturehall.adapter.LectureReuqestionAdapter
import com.daqsoft.travelCultureModule.lecturehall.viewmodel.MineLectureReqViewModel

/**
 * @Description 我的提问
 * @ClassName   MineLectureReqActivity
 * @Author      luoyi
 * @Time        2020/6/18 11:36
 */
@Route(path = MainARouterPath.MINE_LECTURE_REQ)
class MineLectureReqActivity : TitleBarActivity<ActivityMineLectureReqBinding, MineLectureReqViewModel>() {
    var requestionAdapter: LectureReuqestionAdapter? = null
    var currPage: Int = 1
    override fun getLayout(): Int {
        return R.layout.activity_mine_lecture_req
    }

    override fun setTitle(): String {
        return "我的问答"
    }

    override fun injectVm(): Class<MineLectureReqViewModel> {
        return MineLectureReqViewModel::class.java
    }

    override fun initView() {
        requestionAdapter = LectureReuqestionAdapter(this@MineLectureReqActivity).apply {
            setOnLoadMoreListener {
                currPage = currPage + 1
                mModel.getMineLectureReqLs(currPage)
            }
        }
        mBinding.rvMineLectureReqs.adapter = requestionAdapter
        mBinding.rvMineLectureReqs.layoutManager = LinearLayoutManager(this@MineLectureReqActivity, LinearLayoutManager.VERTICAL, false)
        initViewModel()
    }

    private fun initViewModel() {
        mModel.lectureReqLd.observe(this, Observer {
            dissMissLoadingDialog()
            PageDealUtils().pageDeal(currPage, it, requestionAdapter)
            if (it != null && !it.datas.isNullOrEmpty()) {
                requestionAdapter?.add(it.datas!!)
            }
            if (!mBinding.rvMineLectureReqs.isVisible) {
                mBinding.rvMineLectureReqs.visibility = View.VISIBLE
            }
        })
    }

    override fun initData() {
        showLoadingDialog()
        mModel.getMineLectureReqLs(currPage)
    }

}