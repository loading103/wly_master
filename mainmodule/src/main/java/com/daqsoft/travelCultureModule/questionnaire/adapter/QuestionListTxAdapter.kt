package com.daqsoft.travelCultureModule.questionnaire.adapter

import android.annotation.SuppressLint
import android.text.TextUtils
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.SM4Util
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemQuestionListBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.QuestionListBean
import com.daqsoft.provider.view.dialog.ProviderInputDialog
import com.daqsoft.travelCultureModule.questionnaire.QuestionNaureLsActivity


internal class QuestionListTxAdapter() : RecyclerViewAdapter<ItemQuestionListBinding, QuestionListBean>(R.layout.item_question_list) {


    @SuppressLint("CheckResult")
    override fun setVariable(mBinding: ItemQuestionListBinding, position: Int, item: QuestionListBean) {
        mBinding.datas=item
        mBinding.root.onNoDoubleClick {

                ARouter.getInstance()
                    .build(ARouterPath.Provider.WEB_ACTIVITY)
                    .withString("mTitle", "shopName")
                    .withString("html", "url")
                    .navigation()
        }
    }



}