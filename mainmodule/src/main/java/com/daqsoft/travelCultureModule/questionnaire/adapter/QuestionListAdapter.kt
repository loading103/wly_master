package com.daqsoft.travelCultureModule.questionnaire.adapter

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.SM4Util
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemQuestionListBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.QuestionListBean
import com.daqsoft.provider.bean.QuestionSubmitRoot
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.view.dialog.ProviderInputDialog
import com.daqsoft.travelCultureModule.questionnaire.QuestionNaureLsActivity
import com.daqsoft.travelCultureModule.questionnaire.viewmodel.QuestionNaureViewModel


internal class QuestionListAdapter(
    questionNaureLsActivity: QuestionNaureLsActivity
) : RecyclerViewAdapter<ItemQuestionListBinding, QuestionListBean>(R.layout.item_question_list) {

    var  activity: QuestionNaureLsActivity = questionNaureLsActivity

    @SuppressLint("CheckResult")
    override fun setVariable(mBinding: ItemQuestionListBinding, position: Int, item: QuestionListBean) {
        mBinding.datas=item

        //  未开始的调查问卷，需要显示调查问卷开始与结束时间。已开始和已结束的调查问卷，只需要显示调查问卷结束时间即可
        if(item.processStatus=="0" ||  TextUtils.isEmpty(item.getStartTime1())){
            mBinding.tvStartTime.visibility=View.VISIBLE
        }else{
            mBinding.tvStartTime.visibility=View.GONE
        }


        mBinding.root.onNoDoubleClick {
            onItemClickListener?.onItemClick(item, position, mBinding)
        }
    }


    interface OnItemClickListener {
        fun onItemClick(
            pos: QuestionListBean,
            position: Int,
            mBinding: ItemQuestionListBinding
        )
    }
    var onItemClickListener:OnItemClickListener? = null




}