package com.daqsoft.travelCultureModule.lecturehall

import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityLectureHallLsBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.LectureHall
import com.daqsoft.provider.bean.LectureType
import com.daqsoft.travelCultureModule.lecturehall.adapter.LectureHallLsAdapter
import com.daqsoft.travelCultureModule.lecturehall.adapter.LectureHallTypeAdapter
import com.daqsoft.travelCultureModule.lecturehall.viewmodel.LectureHallsViewModel
import org.jetbrains.anko.textColor

/**
 * @Description 大讲堂列表
 * @ClassName   LectureHallLsActivity
 * @Author      luoyi
 * @Time        2020/6/15 10:54
 */
@Route(path = MainARouterPath.LECTURE_HALL_LS)
class LectureHallLsActivity : TitleBarActivity<ActivityLectureHallLsBinding, LectureHallsViewModel>() {


    var lectureHallTypeAdapter: LectureHallTypeAdapter? = null

    var lectureHallLsAdapter: LectureHallLsAdapter? = null

    var currPage: Int = 1
    override fun getLayout(): Int {
        return R.layout.activity_lecture_hall_ls
    }

    override fun setTitle(): String {
        return "大讲堂"
    }

    override fun injectVm(): Class<LectureHallsViewModel> {
        return LectureHallsViewModel::class.java
    }

    override fun initView() {
        initViewModel()
        lectureHallTypeAdapter = LectureHallTypeAdapter(this@LectureHallLsActivity)
        lectureHallTypeAdapter?.emptyViewShow = false
        mBinding.rvLetureLsTabs.adapter = lectureHallTypeAdapter
        mBinding.rvLetureLsTabs.layoutManager = LinearLayoutManager(
            this@LectureHallLsActivity,
            LinearLayoutManager.HORIZONTAL, false
        )

        lectureHallLsAdapter = LectureHallLsAdapter(this@LectureHallLsActivity)
        mBinding.recyLectureHallList.adapter = lectureHallLsAdapter
        mBinding.recyLectureHallList.layoutManager = LinearLayoutManager(
            this@LectureHallLsActivity, LinearLayoutManager.VERTICAL,
            false
        )
        mBinding.swLectureHallLs.setOnRefreshListener {
            currPage = 1
            mModel.getLectureHallList(currPage)
        }
        lectureHallLsAdapter?.setOnLoadMoreListener {
            currPage = +1
            mModel.getLectureHallList(currPage)
        }
        mBinding.swhLectureHallStudyStatus.setOnCheckedChangeListener { buttonView, isChecked ->
            mModel.onlyNotStudy = isChecked
            mBinding.recyLectureHallList.smoothScrollToPosition(0)
            mBinding.recyLectureHallList.visibility = View.GONE
            showLoadingDialog()
            currPage = 1
            lectureHallLsAdapter?.clear()
            mModel.getLectureHallList(currPage)
        }
        mBinding.edtSearchLectureHallLs.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_ALL_SEARCH)
                .navigation()
        }
        lectureHallTypeAdapter?.onLectureHallItemClickListener = object : LectureHallTypeAdapter.OnLectureHallItemClickListener {
            override fun onItemCLick(position: Int, data: LectureType) {
                mBinding.rvLetureLsTabs.smoothScrollToPosition(position)
                mBinding.recyLectureHallList.smoothScrollToPosition(0)
                mBinding.recyLectureHallList.visibility = View.GONE
                showLoadingDialog()
                currPage = 1
                lectureHallLsAdapter?.clear()
                mModel.typeId = data.id
                mModel.getLectureHallList(currPage)
            }
        }
        mBinding.tvSortOrders.onNoDoubleClick {
            showLoadingDialog()
            mBinding.recyLectureHallList.smoothScrollToPosition(0)
            mBinding.recyLectureHallList.visibility = View.GONE
            currPage = 1
            lectureHallLsAdapter?.clear()
            mModel.isAsc = !mModel.isAsc
            mModel.getLectureHallList(currPage)
            if (mModel.isAsc) {
                mBinding.tvSortOrders.textColor = resources.getColor(R.color.colorPrimary)
                var drawable = resources.getDrawable(R.mipmap.main_arrow_up)
                drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                mBinding.tvSortOrders.setCompoundDrawables(null, null, drawable, null)
            } else {
                mBinding.tvSortOrders.textColor = resources.getColor(R.color.color_666)
                var drawable = resources.getDrawable(R.mipmap.main_arrow_down)
                drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                mBinding.tvSortOrders.setCompoundDrawables(null, null, drawable, null)
            }
        }
    }

    private fun initViewModel() {
        mModel.lectureTypesLd.observe(this, Observer {
            if (it.isNullOrEmpty()) {
                mBinding.rvLetureLsTabs.visibility = View.GONE
            } else {
                mBinding.rvLetureLsTabs.visibility = View.VISIBLE
                lectureHallTypeAdapter?.clear()
                var datas: MutableList<LectureType> = mutableListOf()
                datas.add(LectureType("", "", "", "", "不限", 0))
                datas.addAll(it)
                lectureHallTypeAdapter?.add(datas)
            }
        })
        mModel.lectureHallLsLd.observe(this, Observer {
            dealPage(it)
        })
    }

    private fun dealPage(data: MutableList<LectureHall>?) {
        dissMissLoadingDialog()
        if (!mBinding.recyLectureHallList.isVisible) {
            mBinding.recyLectureHallList.visibility = View.VISIBLE
        }
        mBinding.swLectureHallLs.isRefreshing = false
        if (currPage == 1) {
            mBinding.recyLectureHallList.smoothScrollToPosition(0)
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

    override fun initData() {
        showLoadingDialog()
        mModel?.getLectureHallType()
        mModel?.getLectureHallList(currPage)
    }
}