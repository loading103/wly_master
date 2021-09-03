package com.daqsoft.travelCultureModule.lecturehall.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.FragLectureChapterBinding
import com.daqsoft.provider.bean.LectureHallChapter
import com.daqsoft.travelCultureModule.lecturehall.adapter.LectureHallChapterAdapter
import com.daqsoft.travelCultureModule.lecturehall.event.UpdateLectureInfoEvent
import com.daqsoft.travelCultureModule.lecturehall.viewmodel.LectureChapterViewModel
import org.greenrobot.eventbus.EventBus

/**
 * @Description 课程章节
 * @ClassName   LectureChapterFragment
 * @Author      luoyi
 * @Time        2020/6/16 11:42
 */
class LectureChapterFragment : BaseFragment<FragLectureChapterBinding, LectureChapterViewModel>() {

    var id: String? = ""

    var lectureChapterAdapter: LectureHallChapterAdapter? = null

    companion object {
        const val ID = "id"
        fun newInstance(id: String): LectureChapterFragment {
            var frag = LectureChapterFragment()
            var bundle: Bundle = Bundle()
            bundle.putString(ID, id)
            frag.arguments = bundle
            return frag
        }
    }

    override fun getLayout(): Int {
        return R.layout.frag_lecture_chapter
    }

    override fun injectVm(): Class<LectureChapterViewModel> {
        return LectureChapterViewModel::class.java
    }

    override fun initView() {
        lectureChapterAdapter = LectureHallChapterAdapter(context!!)
        mBinding.rvLectureChapters.adapter = lectureChapterAdapter
        mBinding.rvLectureChapters.layoutManager =
            LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
        mModel?.lectureHalChapterLd.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                lectureChapterAdapter?.clear()
                lectureChapterAdapter?.add(it)
                if (it[0] != null) {
                    var item = it[0]
                    if (!item.ctcSchoolChapterVOS.isNullOrEmpty()) {
                        var child = item.ctcSchoolChapterVOS[0]
                        if (child != null && !child.address.isNullOrEmpty()) {
                            EventBus.getDefault().post(
                                UpdateLectureInfoEvent(
                                    child.address,
                                    child.id,
                                    child.userDuration,
                                    child.duration
                                ).apply {
                                    isAutoPlay=false
                                }
                            )
                        }
                    }
                }
            }
        })
        lectureChapterAdapter?.onChapterItemCLickListener =
            object : LectureHallChapterAdapter.OnChapterItemCLickListener {
                override fun onItemClick(item: LectureHallChapter) {
                    item?.let {
                        EventBus.getDefault().post(
                            UpdateLectureInfoEvent(
                                it.address,
                                item.id,
                                item.userDuration,
                                item.duration
                            ).apply {
                                isAutoPlay=true
                            }
                        )
                    }

                }

            }
    }

    override fun initData() {
        getParams()
        if (AppUtils.isLogin()) {
            id?.let {
                mModel?.getLectureChapterLs(it)
            }
        }
    }

    private fun getParams() {
        id = arguments?.getString(ID)
    }
}