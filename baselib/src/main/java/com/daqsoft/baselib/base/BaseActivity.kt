package com.daqsoft.baselib.base

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.config.Config
import com.daqsoft.baselib.utils.NetWorkUtils
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity
import com.umeng.analytics.MobclickAgent
import daqsoft.com.baselib.R
import kotlinx.android.synthetic.main.activity_base.*

/**
 * Activity基类
 *
 * 对Activity做一些封装处理，如不需要TitleBar，可直接继承该Activity
 *
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019/4/15
 * @since v0.0.1
 */
abstract class BaseActivity<VB : ViewDataBinding, VM : BaseViewModel> : RxAppCompatActivity() {

    protected val mModel: VM by lazy { injectVm() }
    protected val mBinding: VB by lazy { setBindingContentView() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 注册ARouter 方便 路由传参
        ARouter.getInstance().inject(this)
        AppManager.instance.addActivity(this)
        injectVm()
        immersionBar()
        setContentView(R.layout.activity_base)
        setBindingContentView()
        setViewModel()
        initView()
        presenter()
        if (NetWorkUtils.isNetWorkAvailable(this)) {
            initData()
        } else {
            ToastUtils.showMessage(resources.getString(R.string.net_work_unavailable))
            mRootView.showNetErrorView(Config.errorView)
        }
        notifyData()
        //requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    /**
     * 设置需要加载的视图布局
     * @return 视图布局的ViewDataBinding对象
     */
    fun setBindingContentView(): VB {
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
        if (NetWorkUtils.isNetWorkAvailable(this)) {
            initData()
        } else {
            ToastUtils.showMessage(resources.getString(R.string.net_work_unavailable))
            mRootView.showNetErrorView(Config.errorView)
        }
    }


    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
    }

    /**
     * 5.0以上沉浸式状态栏适配
     */
    private fun immersionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                BaseApplication.theme == R.string.theme_dark -> {
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
                BaseApplication.theme == R.string.theme_light -> {
                    window.decorView.systemUiVisibility = View
                        .SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                }
                else -> window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
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
                SPUtils.getInstance().put(SPUtils.Config.TOKEN, "")
                // 需要登录时跳转登录界面
                ARouter.getInstance()
                    .build(BaseApplication.loginPath)
                    .navigation()
                it.login = false
            }
            // 视图显示完毕后，将加载状态置为初始状态
//            it.loading = true
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        AppManager.instance.finishActivity(this)
    }

    /**
     * 处理需要观察ViewModel中的数据
     */
    protected open fun notifyData() {

    }

    /**
     * 获取布局文件
     */
    abstract fun getLayout(): Int

    /**
     * 注册ViewModel
     */
    abstract fun injectVm(): VM

    /**
     * 给DataBindingUI 设置ViewModel
     */
    abstract fun setViewModel()

    /**
     * 初始化布局
     */
    abstract fun initView()

    /**
     * 初始化数据
     */
    abstract fun initData()

}