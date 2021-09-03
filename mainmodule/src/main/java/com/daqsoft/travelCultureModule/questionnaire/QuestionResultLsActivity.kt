package com.daqsoft.travelCultureModule.questionnaire
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityQuestionLsBinding
import com.daqsoft.mainmodule.databinding.ActivityQuestionResultBinding
import com.daqsoft.mainmodule.databinding.ActivityQuestionTxBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.QuestionSubmitBean
import com.daqsoft.provider.bean.QuestionSubmitRoot
import com.daqsoft.travelCultureModule.questionnaire.adapter.QuestionListAdapter
import com.daqsoft.travelCultureModule.questionnaire.adapter.QuestionListTxAdapter
import com.daqsoft.travelCultureModule.questionnaire.viewmodel.QuestionNaureViewModel
import com.scwang.smartrefresh.layout.footer.ClassicsFooter

/**
 * 调查结果列表
 */
@Route(path = MainARouterPath.MINE_QUESTION_RESULT_LIST)
class QuestionResultLsActivity : TitleBarActivity<ActivityQuestionResultBinding, QuestionNaureViewModel>() {
    @JvmField
    @Autowired
    var id: String = "0"
    @JvmField
    @Autowired
    var groupCode: String = ""
    @JvmField
    @Autowired
    var isend: Boolean =false

    @JvmField
    @Autowired
    var title1: String = ""

    @JvmField
    @Autowired
    var guideBody: String = ""

    private  var databean: QuestionSubmitRoot? =QuestionSubmitRoot()
    override fun onCreate(savedInstanceState: Bundle?) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState)
    }

    override fun getLayout(): Int {
        return R.layout.activity_question_result
    }

    override fun setTitle(): String {
        return "调查结果"
    }

    override fun injectVm(): Class<QuestionNaureViewModel> {
        return QuestionNaureViewModel::class.java
    }

    override fun initView() {
        initViewModel()
        initEvent()

        mBinding.swprefreshFoods.apply {
            setEnableLoadMore(false)
            setEnableRefresh(false)
        }
        //如果没有登录 不能编辑
//        if(AppUtils.isLogin()){
//            mBinding.tvChange.visibility=View.VISIBLE
//        }else{
//            mBinding.tvChange.visibility=View.GONE
//        }

        // 如果时间结束了，就不能编辑
        if(isend || !AppUtils.isLogin()){
            mBinding.tvChange.visibility=View.GONE
        }else{
            mBinding.tvChange.visibility=View.VISIBLE
        }

    }

    private fun initEvent() {
        mBinding.tvChange.onNoDoubleClick {
            if(AppUtils.isLogin()){
                submitData()
            }else{
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
    }


    override fun initData() {
        showLoadingDialog()
        mModel.pageManager.initPageIndex()
        if(groupCode.isNullOrBlank()){
            mModel.getResultListDatas("",id)
        }else{
            mModel.getResultListDatas(groupCode,"")
        }
    }

    /**
     * 编辑
     */
    private fun submitData() {
        ARouter.getInstance().build( MainARouterPath.MINE_QUESTION_TX_LIST)
            .withString("groupCode",groupCode)
            .withString("title1",title1)
            .withString("guideBody",guideBody)
            .withString("id",id)
            .withSerializable("databean",databean)
            .navigation()
        finish()
    }

    private fun initViewModel() {
        //  数据加载失败
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.swprefreshFoods.finishRefresh()
        })
        //  数据加载成功
        mModel.resultDatas.observe(this, Observer {
            dissMissLoadingDialog()
            if(it?.isEmpty()!!){
                return@Observer
            }
            databean?.questionList?.addAll(it)
            mBinding.recycleView.setNewData(it)
            if(!title1.isNullOrBlank()){
                mBinding.recycleView.setGuideBody(title1,guideBody?:"")
            }

        })
        mModel.submitBean.observe(this, Observer {
            dissMissLoadingDialog()
            ToastUtils.showMessage("提交成功")
            finish()

        })

        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
            ToastUtils.showMessage(it.message)
            mBinding.swprefreshFoods.finishRefresh()
        })
    }
}