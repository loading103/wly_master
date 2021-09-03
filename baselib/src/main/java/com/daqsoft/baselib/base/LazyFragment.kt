package com.daqsoft.baselib.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.config.Config
import com.daqsoft.baselib.utils.NetWorkUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.trello.rxlifecycle3.components.support.RxFragment
import daqsoft.com.baselib.R
import kotlinx.android.synthetic.main.fragment_base.*

/**
 * 懒加载fragment基类（不结合ViewPager使用）
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-6-12
 * @since JDK 1.8.0_191
 */
abstract class LazyFragment<VB : ViewDataBinding, VM : BaseViewModel> : RxFragment() {

    protected val mBinding: VB by lazy { setBindingView() }
    protected val mModel: VM by lazy { injectVm() }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val inflate = inflater.inflate(R.layout.fragment_base, container, false)
        return inflate
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBindingView()
        injectVm()
        setViewModel()
        presenter()
        initView()

        notifyData()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            // 可见状态
            if (context != null) {
                if (NetWorkUtils.isNetWorkAvailable(context!!)) {
                    initData()
                } else {
                    ToastUtils.showMessage(resources.getString(R.string.net_work_unavailable))
                    mRootView.showNetErrorView(Config.errorView)
                }
            }
        }
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
            if (init) {
                init = false
                return@Observer
            }
            if (!it.errorMessage.isNullOrEmpty()) {
                ToastUtils.showMessage(it.errorMessage)
                it.errorMessage = ""
            }
            if (it.loading) {
                // 显示加载状态视图
                mRootView.showLoadingView(Config.loadingView)
            } else {
                // 显示正常状态视图
                if (it.error) {
                    mRootView.showErrorView()
                } else {
                    mRootView.showContentView()
                }
            }
            if (it.login) {
                // 需要登录时跳转登录界面
                // TODO:因为Baselib不依赖provicer模块，因此此处地址用写死的LoginActivity的路径字符串，不能用ARouterPath
                ARouter.getInstance()
                        .build("/usermodule/LoginActivity")
                        .navigation()
                it.login = false
            }
            // 视图显示完毕后，将加载状态置为初始状态
//            it.loading = true
        })
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
     * 初始化视图
     */
    abstract fun initView()

    /**
     * 初始化数据
     */
    abstract fun initData()

    /**
     * 注册ViewModel
     */
    abstract fun injectVm(): VM

    /**
     * 给DataBindingUI 设置ViewModel
     */
    abstract fun setViewModel()

}