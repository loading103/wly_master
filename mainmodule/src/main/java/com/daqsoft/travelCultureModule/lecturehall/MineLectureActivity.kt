package com.daqsoft.travelCultureModule.lecturehall

import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityMineLectureBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.LectureHall
import com.daqsoft.travelCultureModule.lecturehall.adapter.LectureHallLsAdapter
import com.daqsoft.travelCultureModule.lecturehall.viewmodel.MineLectureViewModel

/**
 * @Description 我的学习
 * @ClassName   MineLectureActivity
 * @Author      luoyi
 * @Time        2020/6/18 11:36
 */
@Route(path = MainARouterPath.MINE_LECTURE_LIST)
class MineLectureActivity : TitleBarActivity<ActivityMineLectureBinding, MineLectureViewModel>() {

    var lectureHallLsAdapter: LectureHallLsAdapter? = null

    var currPage: Int = 1

    override fun getLayout(): Int {
        return R.layout.activity_mine_lecture
    }

    override fun setTitle(): String {
        return "我的学习"
    }

    override fun injectVm(): Class<MineLectureViewModel> {
        return MineLectureViewModel::class.java
    }

    override fun initView() {
        lectureHallLsAdapter = LectureHallLsAdapter(this@MineLectureActivity)
        mBinding.rvStudyHistories.adapter = lectureHallLsAdapter
        mBinding.rvStudyHistories.layoutManager = LinearLayoutManager(
            this@MineLectureActivity,
            LinearLayoutManager.VERTICAL, false
        )
        lectureHallLsAdapter?.setOnLoadMoreListener {
            currPage + 1
            mModel.getMineLectureLs(currPage)
        }
        initViewModel()
    }

    private fun initViewModel() {
        mModel.mineLectureLd.observe(this, Observer {
            dealPage(it)
        })
        mModel.mineLectureRecordLd.observe(this, Observer {
            if (it != null) {
                mBinding.tvStudyClass.text = "${it.chapterNum}节课"
                mBinding.tvStudyHour.text = "${DateUtil.getFormatedTime(it.totalDuration * 1000L)}"
            } else {
                mBinding.tvStudyClass.text = "0"
                mBinding.tvStudyHour.text = "0"
            }
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })
    }

    override fun initData() {
        mModel.getMineLectureLs(currPage)
        mModel.getMineLectureRecorder()
    }

    private fun dealPage(data: MutableList<LectureHall>?) {
        dissMissLoadingDialog()
        if (!mBinding.rvStudyHistories.isVisible) {
            mBinding.rvStudyHistories.visibility = View.VISIBLE
        }
        if (currPage == 1) {
            mBinding.rvStudyHistories.smoothScrollToPosition(0)
            lectureHallLsAdapter?.clear()
        }
        if (!data.isNullOrEmpty()) {
            lectureHallLsAdapter?.add(data!!)
        }
        if (data.isNullOrEmpty() || data.size < 10) {
            lectureHallLsAdapter?.loadEnd()
        } else {
            lectureHallLsAdapter?.loadComplete()
        }
    }
}