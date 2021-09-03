package com.daqsoft.travelCultureModule.questionnaire
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.KeyboardUtils
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.SM4Util
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityQuestionLsBinding
import com.daqsoft.mainmodule.databinding.ItemQuestionListBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.QuestionListBean
import com.daqsoft.provider.utils.ToastUtil
import com.daqsoft.provider.view.dialog.ProviderInputDialog
import com.daqsoft.travelCultureModule.questionnaire.adapter.QuestionListAdapter
import com.daqsoft.travelCultureModule.questionnaire.viewmodel.QuestionNaureViewModel
import com.scwang.smartrefresh.layout.footer.ClassicsFooter

/**
 * 调查问卷列表
 */
@Route(path = MainARouterPath.MINE_QUESTION_REQ)
class QuestionNaureLsActivity : TitleBarActivity<ActivityQuestionLsBinding, QuestionNaureViewModel>(), TextWatcher, TextView.OnEditorActionListener {
    var item: QuestionListBean?=null
    // List适配器
    private val mAdapter by lazy {
        QuestionListAdapter(this).
        apply {
            emptyViewShow=false
            onItemClickListener = object : QuestionListAdapter.OnItemClickListener {
                override fun onItemClick(item1: QuestionListBean, position: Int, mBinding: ItemQuestionListBinding) {
                    if(!AppUtils.isLogin() && item1.login=="1"){
                        ToastUtils.showUnLoginMsg()
                        ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY).navigation()
                        return
                    }
                    item=item1
                    // 0 未开始
                    if(item1.getState()=="0"){
//                        ToastUtils.showMessage("该问卷调查未开始")
                        ToastUtil.show("该问卷调查未开始")
                    }
                    //1 进行中
                    if(item1.getState()=="1"){
                        onGoingClick(item1)
                    }
                    // 2 结束
                    if(item1.getState()=="2"){
                        // 点击已经结素，如果显示结果才能跳转
                        // 如果加密了，要输入密码在进入详情

                        if(item1.publicResult!="1"){
//                            ToastUtils.showMessage("该问卷调查已结束")
                            ToastUtil.show("该问卷调查已结束")
                            return
                        }
                        if(!TextUtils.isEmpty(item1.inputPass)){
                            var key = SM4Util.decryptByEcb(item1.inputPass)
                            showInPutDialog(key, "")
                        }else {
                            ARouter.getInstance().build(MainARouterPath.MINE_QUESTION_RESULT_LIST)
                                .withString("id", item1.id)
                                .withBoolean("isend", true)
                                .withString("groupCode",item1.groupCode?:"")
//                                .withString("title1",item1.title)
//                                .withString("guideBody",item1.guideBody)
                                .navigation()

                        }
                    }
                }

            }
        }

    }

    // 搜索内容
    private  var sreachText=""

    override fun getLayout(): Int {
        return R.layout.activity_question_ls
    }

    override fun setTitle(): String {
        return "问卷调查"
    }

    override fun injectVm(): Class<QuestionNaureViewModel> {
        return QuestionNaureViewModel::class.java
    }

    override fun initView() {
        initViewModel()
        initEvent()
        mBinding.swprefreshFoods.apply {
            setEnableLoadMore(true)
            setRefreshFooter(ClassicsFooter(context).setDrawableSize(20f))
            setOnLoadMoreListener {
                mModel.pageManager.nexPageIndex
                mModel.getListDatas(sreachText)
            }
            setOnRefreshListener {
                mModel.pageManager.initPageIndex()
                mModel.getListDatas(sreachText)
            }
        }
        mBinding.recycleView.apply {
            this.adapter = mAdapter
        }
    }

    private fun initEvent() {
        mBinding.edtSearch.addTextChangedListener(this)
        mBinding.edtSearch.setOnEditorActionListener(this)
        mBinding.tvMine.onNoDoubleClick {
            if(AppUtils.isLogin()){
                ARouter.getInstance().build(MainARouterPath.MINE_QUESTION_REQ_LIST)
                    .navigation()
            }else{
                ToastUtils.showUnLoginMsg()
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY).navigation()
            }
        }
    }

    override fun initData() {
        showLoadingDialog()
        mModel.pageManager.initPageIndex()
        mModel.getListDatas()
    }



    /**
     * 监听搜索按钮
     */
    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?) :Boolean{
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            KeyboardUtils.hideSoftInput(this)
            return true;
        }
        return false;
    }


    private fun initViewModel() {
        //  数据加载失败
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
            ToastUtils.showMessage(it.message)
            mBinding.swprefreshFoods.finishRefresh()
        })
        //  数据加载成功
        mModel.listdatas.observe(this, Observer {
            dissMissLoadingDialog()
            if(it==null){
                return@Observer
            }
            if (mModel.pageManager.isFirstIndex) {
                mAdapter.clear()
                mAdapter.setNewData(it)
                if( mAdapter.getData().size==mModel.totleNumber){
                    mBinding.swprefreshFoods.finishRefreshWithNoMoreData()
                }else{
                    mBinding.swprefreshFoods.finishRefresh()
                }
            }else{
                mAdapter.getData().addAll(it)
                if( mAdapter.getData().size==mModel.totleNumber){
                    mBinding.swprefreshFoods.finishLoadMoreWithNoMoreData()
                }else{
                    mBinding.swprefreshFoods.finishLoadMore()
                }
            }
            mAdapter.emptyViewShow=true
            mAdapter.notifyDataSetChanged()

        })
        // 获取详情成功
        mModel.detailsBean.observe(this, Observer {
            dissMissLoadingDialog()
            if(TextUtils.isEmpty(it?.groupCode)){
                // 如果加密了，要输入密码在进入详情
                if(!TextUtils.isEmpty(item?.inputPass)){
                    var key = SM4Util.decryptByEcb(item?.inputPass)
                    showInPutDialog(key,null)
                }else{
                    ARouter.getInstance().build(MainARouterPath.MINE_QUESTION_TX_LIST)
                        .withString("id",item?.id.toString())
                        .navigation()
                }
            }else{
                // 如果加密了，要输入密码在进入详情
                if(!TextUtils.isEmpty(item?.inputPass)){
                    var key = SM4Util.decryptByEcb(item?.inputPass)
                    showInPutDialog(key,it?.groupCode)
                }else{
                    ARouter.getInstance().build(MainARouterPath.MINE_QUESTION_RESULT_LIST)
                        .withString("id",item?.id.toString())
                        .withString("groupCode",it?.groupCode)
                        .withString("title1",it?.title)
                        .withString("guideBody",it?.guideBody)
                        .navigation()
                }
            }
        })

    }
    override fun afterTextChanged(s: Editable?) {
        var searech=s.toString()
        sreachText=searech
        mModel.pageManager.initPageIndex()
        mModel.getListDatas( searech)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    /**
     * 进行中点击先请求详情接口判断是否返回了groupCode，有就去结果列表，没有就去提交界面
     */

    private fun onGoingClick(items: QuestionListBean) {
        showLoadingDialog()
        mModel.getListDetailDatas(items.id)
    }
    /**
     * 输入密码弹窗
     */
    private fun showInPutDialog(key: String?, groupCode: String?) {
        ProviderInputDialog.Builder().setOnTipConfirmListener(object :
            ProviderInputDialog.OnTipConfirmListener {
            override fun onConfirm(content: String, providerInputDialog: ProviderInputDialog) {
                if(content==key){
                    providerInputDialog.dismiss()
                    item?.inputPass=""
                    // 进行中 填写了就去完成界面，没有就去提交界面
                    if(item?.getState()=="1"){
                        if(TextUtils.isEmpty(groupCode)){
                            ARouter.getInstance().build(MainARouterPath.MINE_QUESTION_TX_LIST)
                                .withString("id",item?.id)
                                .navigation()
                        }else{
                            ARouter.getInstance().build(MainARouterPath.MINE_QUESTION_RESULT_LIST)
                                .withString("id",item?.id)
                                .withString("title1",item?.title)
                                .withBoolean("isend", false)
                                .withString("guideBody",item?.guideBody)
                                .withString("groupCode",groupCode)
                                .navigation()
                        }

                    }else{
                        // 已结束 填写了就去完成界面，没有就去提交界面
                        ARouter.getInstance().build(MainARouterPath.MINE_QUESTION_RESULT_LIST)
                            .withString("id",item?.id)
                            .withBoolean("isend", true)
                            .withString("groupCode",groupCode)
//                            .withString("title1",item?.title)
//                            .withString("guideBody",item?.guideBody)
                            .navigation()
                    }

                }else{
//                    ToastUtils.showMessage("密码错误,请重试")
                    ToastUtil.show("密码错误,请重试")
                }
            }
        })
            .create(this).show()
    }

}