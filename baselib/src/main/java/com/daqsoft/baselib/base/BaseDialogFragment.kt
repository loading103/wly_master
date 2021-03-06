package com.daqsoft.baselib.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.config.Config
import com.daqsoft.baselib.utils.NetWorkUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.dialog.CommonLoadingDialog
import com.trello.rxlifecycle3.components.support.RxFragment
import daqsoft.com.baselib.R
import kotlinx.android.synthetic.main.fragment_base.*

/**
 * @Description
 * @ClassName   BaseDialogFragment
 * @Author      luoyi
 * @Time        2020/8/4 14:23
 */
abstract class BaseDialogFragment<VB : ViewDataBinding, VM : BaseViewModel> : RxDialogFragment() {

    var contentView: View? = null
    protected val mBinding: VB by lazy { setBindingView() }
    protected val mModel: VM by lazy {
        ViewModelProvider.NewInstanceFactory().create(injectVm())
    }
    private var mLoadingDialog: CommonLoadingDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.fragment_base, container, false)
        } else {
            val parent = contentView!!.parent
            if (parent != null) {
                (parent as ViewGroup).removeView(contentView)
            }
        }
        return contentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewModelProvider.NewInstanceFactory().create(injectVm())
        setBindingView()
        initView()
        presenter()
        if (context != null) {
            if (NetWorkUtils.isNetWorkAvailable(context!!)) {
                initData()
            } else {
                ToastUtils.showMessage(resources.getString(R.string.net_work_unavailable))
                mRootView.showNetErrorView(Config.errorView)
            }
        }
        notifyData()
    }

    /**
     * ????????????????????????
     * true:???
     * false:???
     */
    var init = true

    /**
     * ????????????ViewModel??????????????????
     */
    fun presenter() {
        mModel.mPresenter.observe(this, Observer {
            if (it.isOnlyShowContent) {
                mRootView.onlyShowContentView()
                it.isNeedRecyleView = false
                return@Observer
            }

            if (it.isOnlyShowLoading) {
                mRootView.onlyShowLoadingView()
                it.isOnlyShowLoading = false
                return@Observer
            }
            if (init) {
                init = false
                return@Observer
            }
            if (it.errorMessage.isNotEmpty() && it.isNeedToastMessage) {
                ToastUtils.showMessage(it.errorMessage)
                it.errorMessage = ""
            }
            if (it.loading) {
                // ????????????????????????
                mRootView.showLoadingView(Config.loadingView)
            } else {
                // ????????????????????????
                if (it.isNeedRecyleView || it.error) {
                    if (it.error) {
                        mRootView.showErrorView()
                    } else {
                        mRootView.showContentView()
                    }
                }
            }
            if (it.login) {
                // ?????????????????????????????????
                // TODO:??????Baselib?????????provicer???????????????????????????????????????LoginActivity??????????????????????????????ARouterPath
                ARouter.getInstance()
                    .build(BaseApplication.loginPath)
                    .navigation()
                it.login = false
            }
            // ?????????????????????????????????????????????????????????
//            it.loading = true
        })
        mModel.finish.observe(this, Observer {
            activity!!.finish()
        })
    }

    /**
     * ?????????????????????????????????
     * @return ???????????????ViewDataBinding??????
     */
    fun setBindingView(): VB {
        val inflate = DataBindingUtil.inflate<VB>(layoutInflater, getLayout(), mRootView, false)
        inflate.setLifecycleOwner(this)
        mRootView.showContentView(inflate.root)
        return inflate
    }

    /**
     * ??????????????????
     *
     * ????????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @date 2019/6/5
     * @author ?????????
     */
    protected fun reloadData() {
        if (context != null) {
            if (NetWorkUtils.isNetWorkAvailable(context!!)) {
                initData()
            } else {
                ToastUtils.showMessage(resources.getString(R.string.net_work_unavailable))
                mRootView.showNetErrorView(Config.errorView)
            }
        }

    }

    /**
     * ??????????????????ViewModel????????????
     */
    protected open fun notifyData() {

    }

    /**
     * ??????Fragment????????????
     */
    abstract fun getLayout(): Int

    /**
     * ??????ViewModel
     */
    abstract fun injectVm(): Class<VM>


    /**
     * ???????????????
     */
    abstract fun initView()

    /**
     * ???????????????
     */
    abstract fun initData()

    /**
     * fragment??????
     */
    var fragments = mutableListOf<Fragment>()

    /**
     * ??????????????????????????????fragment
     */
    var isHideAnother = true

    /**
     * fragment????????????
     */
    public fun transactFragment(id: Int, fragment: Fragment) {
        var transient = childFragmentManager.beginTransaction()
        if (fragment.isAdded) {
            transient.show(fragment)
            if (isHideAnother)
                for (i in 0 until fragments.size) {
                    if (fragment != fragments[i]) {
                        transient.hide(fragments[i])
                    }
                }
        } else {
            transient.add(id, fragment)
            if (isHideAnother)
                for (i in 0 until fragments.size) {
                    if (fragment != fragments[i]) {
                        transient.hide(fragments[i])
                    }
                }
            fragments.add(fragment)
            transient.show(fragment)

        }
        transient.commit()
    }

    /**
     * ???????????????
     */
    public fun showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = CommonLoadingDialog(activity)
        }
        if (!mLoadingDialog!!.isShowing) {
            mLoadingDialog?.show()
        }
    }

    override fun onPause() {
        super.onPause()
        dissMissLoadingDialog()
    }

    /**
     *???????????????
     */
    public fun dissMissLoadingDialog() {
        if (mLoadingDialog == null || !mLoadingDialog!!.isShowing) {
            return
        }
        try {
            mLoadingDialog?.dismiss()
        } catch (e: Exception) {

        }

    }


}