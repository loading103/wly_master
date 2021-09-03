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
     * 是否第一次初始化
     * true:是
     * false:否
     */
    var init = true

    /**
     * 统一处理ViewModel传过来的回调
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
                // 显示加载状态视图
                mRootView.showLoadingView(Config.loadingView)
            } else {
                // 显示正常状态视图
                if (it.isNeedRecyleView || it.error) {
                    if (it.error) {
                        mRootView.showErrorView()
                    } else {
                        mRootView.showContentView()
                    }
                }
            }
            if (it.login) {
                // 需要登录时跳转登录界面
                // TODO:因为Baselib不依赖provicer模块，因此此处地址用写死的LoginActivity的路径字符串，不能用ARouterPath
                ARouter.getInstance()
                    .build(BaseApplication.loginPath)
                    .navigation()
                it.login = false
            }
            // 视图显示完毕后，将加载状态置为初始状态
//            it.loading = true
        })
        mModel.finish.observe(this, Observer {
            activity!!.finish()
        })
    }

    /**
     * 设置需要加载的视图布局
     * @return 视图布局的ViewDataBinding对象
     */
    fun setBindingView(): VB {
        val inflate = DataBindingUtil.inflate<VB>(layoutInflater, getLayout(), mRootView, false)
        inflate.setLifecycleOwner(this)
        mRootView.showContentView(inflate.root)
        return inflate
    }

    /**
     * 重新加载数据
     *
     * 用于上一次网络请求失败，数据请求错误或需要重新刷新数据时候的情况
     *
     * @date 2019/6/5
     * @author 周俊蒙
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
     * 处理需要观察ViewModel中的数据
     */
    protected open fun notifyData() {

    }

    /**
     * 添加Fragment视图布局
     */
    abstract fun getLayout(): Int

    /**
     * 注册ViewModel
     */
    abstract fun injectVm(): Class<VM>


    /**
     * 初始化视图
     */
    abstract fun initView()

    /**
     * 初始化数据
     */
    abstract fun initData()

    /**
     * fragment列表
     */
    var fragments = mutableListOf<Fragment>()

    /**
     * 判断是否应当隐藏其他fragment
     */
    var isHideAnother = true

    /**
     * fragment控制方法
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
     * 显示加载框
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
     *隐藏加载框
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