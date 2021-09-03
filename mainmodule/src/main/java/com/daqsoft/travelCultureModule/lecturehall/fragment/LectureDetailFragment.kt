package com.daqsoft.travelCultureModule.lecturehall.fragment

import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.FragLectureDetailBinding
import com.daqsoft.provider.bean.LectureHallDetailBean
import com.daqsoft.travelCultureModule.lecturehall.event.UpdateLectureInfoEvent
import com.daqsoft.travelCultureModule.lecturehall.viewmodel.LectureHallDetailViewModel
import org.greenrobot.eventbus.EventBus

/**
 * @Description 课程简介
 * @ClassName   LectureDetailFragment
 * @Author      luoyi
 * @Time        2020/6/16 11:30
 */
class LectureDetailFragment : BaseFragment<FragLectureDetailBinding, LectureHallDetailViewModel>() {


    var id: String? = ""

    companion object {
        const val ID = "id"
        fun newInstance(id: String): LectureDetailFragment {
            var frag = LectureDetailFragment()
            var bundle = Bundle()
            bundle.putString(ID, id)
            frag.arguments = bundle
            return frag
        }
    }

    override fun getLayout(): Int {
        return R.layout.frag_lecture_detail
    }

    override fun injectVm(): Class<LectureHallDetailViewModel> {
        return LectureHallDetailViewModel::class.java
    }


    override fun initView() {
        initViewModel()
    }

    private fun initViewModel() {
        mModel?.lectureHallDetailLd.observe(this, Observer {
            dissMissLoadingDialog()
            if (it != null) {
                EventBus.getDefault().post(UpdateLectureInfoEvent(it,true))
                bindUi(it)
            }
        })
    }

    private fun bindUi(data: LectureHallDetailBean) {
        // 讲师数据
        Glide.with(context!!)
            .load(data.lecturerImage)
            .placeholder(R.mipmap.mine_profile_photo_default)
            .into(mBinding.imgLectureHead)
        mBinding.tvLectureTeacName.text = "" + data.lecturerName
        mBinding.tvLectureTeacInfo.text = "" + data.lecturerOverview

        //  课程简介
        if (data.introduction.isNullOrEmpty()) {
            mBinding.tvLectureDetail.visibility = View.GONE
            mBinding.tvLectureDetailValue.visibility = View.GONE
        } else {
            mBinding.tvLectureDetail.visibility = View.VISIBLE
            mBinding.tvLectureDetailValue.visibility = View.VISIBLE
            mBinding.tvLectureDetailValue.text = "" + Html.fromHtml(data.introduction)
        }
        // 课程目标
        if (data.courseAims.isNullOrEmpty()) {
            mBinding.tvMainTarget.visibility = View.GONE
            mBinding.tvMainTargetValue.visibility = View.GONE
        } else {
            mBinding.tvMainTarget.visibility = View.VISIBLE
            mBinding.tvMainTargetValue.visibility = View.VISIBLE
            mBinding.tvMainTargetValue.text = "" + Html.fromHtml(data.courseAims)
        }
        // 主要内容
        if (data.content.isNullOrEmpty()) {
            mBinding.tvMainDetail.visibility = View.GONE
            mBinding.tvMainDetailValue.visibility = View.GONE
        } else {
            mBinding.tvMainDetail.visibility = View.VISIBLE
            mBinding.tvMainDetailValue.visibility = View.VISIBLE
            mBinding.tvMainDetailValue.text = "" + Html.fromHtml(data.content)
        }
        // 思考问题
        if (data.thinkingProblem.isNullOrEmpty()) {
            mBinding.tvThinkDetail.visibility = View.GONE
            mBinding.tvThinkDetailValue.visibility = View.GONE
        } else {
            mBinding.tvThinkDetail.visibility = View.VISIBLE
            mBinding.tvThinkDetailValue.visibility = View.VISIBLE
            mBinding.tvThinkDetailValue.text = "" + Html.fromHtml(data.thinkingProblem)
        }
    }

    override fun initData() {
        showLoadingDialog()
        getParams()
        id?.let {
            mModel?.getLectureHallDetail(it)
        }

    }

    private fun getParams() {
        id = arguments?.getString(ID)
    }
}