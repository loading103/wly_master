package com.daqsoft.travelCultureModule.questionnaire
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.KeyboardUtils
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityQuestionTxBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.QuestionSubmitRoot
import com.daqsoft.provider.businessview.event.QuestSubmitEvent
import com.daqsoft.provider.event.UpdateTokenEvent
import com.daqsoft.travelCultureModule.questionnaire.viewmodel.QuestionNaureViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 调查问卷列表（填写问卷界面）
 */
@Route(path = MainARouterPath.MINE_QUESTION_TX_LIST)
class QuestionLsSubmitActivity : TitleBarActivity<ActivityQuestionTxBinding, QuestionNaureViewModel>() {

    @JvmField
    @Autowired
    var id: String = "0"

    @JvmField
    @Autowired
    var groupCode: String = ""

    @JvmField
    @Autowired
    var title1: String = ""

    @JvmField
    @Autowired
    var guideBody: String = ""
    @JvmField
    @Autowired
    var databean: QuestionSubmitRoot?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState)
    }

    override fun getLayout(): Int {
        return R.layout.activity_question_tx
    }

    override fun setTitle(): String {
        return "问卷调查"
    }

    override fun initPageParams() {
        isInitImmerBar = false
    }


    override fun injectVm(): Class<QuestionNaureViewModel> {
        return QuestionNaureViewModel::class.java
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        initViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


    override fun initData() {
        // databean为null,代表第一次提交，不为null。代表编辑界面携带数据过来的
        mBinding.recycleView.setShowSubject(true)
        if(databean==null){
            showLoadingDialog()
            mModel.getListDetailDatas(id)
        }else{
            // 先给选中状态赋值
            databean?.questionList?.let {
                it.forEach {it1->
                    it1.chooseList?.forEach {it2->
                        it2.haveChoosed=it2.userCheck
                    }
                }
                mBinding.recycleView.setNewData(it)
                if(!title1.isNullOrBlank()){
                    mBinding.recycleView.setGuideBody(title1,guideBody?:"")
                }
            }
        }
    }




    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refresh(event: QuestSubmitEvent) {
        submitData()
    }
    /**
     * 提交数据
     */
    private fun submitData() {
        val chooseDatas = mBinding.recycleView.getChooseDatas() ?: return
        if(groupCode==null){
            groupCode=""
        }
        mModel.submitQuestListDatas(groupCode,id,chooseDatas)
    }

    private fun initViewModel() {
        //  数据加载失败
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
            ToastUtils.showMessage(it.message)
            if(it.message.toString().contains("登录")){
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY).navigation()
            }
        })
        //  数据加载成功
        mModel.detailsBean.observe(this, Observer {
            dissMissLoadingDialog()
            if(it?.questionList?.isEmpty()!!){
                return@Observer
            }
            mBinding.recycleView.setNewData(it?.questionList)
            mBinding.recycleView.setGuideBody(it?.guideBody,it?.title)

        })
        mModel.submitBean.observe(this, Observer {
            if(it.publishResult){
                ARouter.getInstance().build(MainARouterPath.MINE_QUESTION_RESULT_LIST)
                    .withString("groupCode",it.groupCode)
                    .withString("title1", mModel.detailsBean?.value?.title)
                    .withString("guideBody", mModel.detailsBean?.value?.guideBody)
                    .withString("id",id)
                    .navigation()
                finish()
            }else{
                ToastUtils.showMessage("您的答卷已经提交，感谢您的参与")
                finish()
            }
        })
    }
}