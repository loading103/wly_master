package com.daqsoft.travelCultureModule.questionnaire.adapter

import android.annotation.SuppressLint
import android.text.TextUtils
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.SM4Util
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemQuestionListBinding
import com.daqsoft.mainmodule.databinding.ItemQuestionMineListBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.QuestionListBean
import com.daqsoft.provider.bean.QuestionSubmitRoot
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.view.dialog.ProviderInputDialog
import com.daqsoft.travelCultureModule.questionnaire.QuestionNaureLsActivity
import com.daqsoft.travelCultureModule.questionnaire.QuestionNaureMineLsActivity


internal class QuestionListMineAdapter(questionNaureLsActivity: QuestionNaureMineLsActivity) : RecyclerViewAdapter<ItemQuestionMineListBinding, QuestionListBean>(R.layout.item_question_mine_list) {

    var  activity: QuestionNaureMineLsActivity = questionNaureLsActivity

    @SuppressLint("CheckResult")
    override fun setVariable(mBinding: ItemQuestionMineListBinding, position: Int, item: QuestionListBean) {
        mBinding.datas=item
        mBinding.root.onNoDoubleClick {
            onItemClickListener?.onItemClick(item, position, mBinding)
        }
    }


    interface OnItemClickListener {
        fun onItemClick(
            pos: QuestionListBean,
            position: Int,
            mBinding: ItemQuestionMineListBinding
        )
    }
    var onItemClickListener:OnItemClickListener? = null

}