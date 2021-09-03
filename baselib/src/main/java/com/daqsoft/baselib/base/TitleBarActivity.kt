package com.daqsoft.baselib.base

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.config.Config
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.NetWorkUtils
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.baselib.widgets.dialog.CommonLoadingDialog
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity
import com.umeng.analytics.MobclickAgent
import daqsoft.com.baselib.R
import kotlinx.android.synthetic.main.activity_title_bar.*
import kotlinx.android.synthetic.main.title_bar.view.*

/**
 * 带有TitleBar的Activity
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019/4/21
 * @since JDK 1.8
 */
abstract class TitleBarActivity<VB : ViewDataBinding, VM : BaseViewModel> : RxAppCompatActivity() {
    var mLoadingDialog: CommonLoadingDialog? = null

   lateinit var mtitleBar: TitleBar
    protected val mModel: VM by lazy {
        ViewModelProvider.NewInstanceFactory().create(injectVm())
    }
    protected val mBinding: VB by lazy { setBindingContentView() }
    /**
     * 标题栏返回按钮是否显示
     */
    protected var isBackShow = true
        set(value) {
            field = value
            mTitleBar.isBackShow = field
        }

    /**
     * 用于标记是否初始化immerBaR
     */
    open var isInitImmerBar: Boolean = true
    /**
     * 标题
     */
    protected var title: String = ""
        set(value) {
            field = value
            mTitleBar.title = field
        }

    protected fun getContext(): Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 注册ARouter 方便 路由传参
        ARouter.getInstance().inject(this)
        initPageParams()
        AppManager.instance.addActivity(this)
        ViewModelProvider.NewInstanceFactory().create(injectVm())
        if (isInitImmerBar) {
            immersionBar()
        }
        setContentView(R.layout.activity_title_bar)
        setBindingContentView()
        initTitleBar()
        initView()
        notifyData()
        presenter()

        if (NetWorkUtils.isNetWorkAvailable(this)) {
            initData()
        } else {
            ToastUtils.showMessage(resources.getString(R.string.net_work_unavailable))
            mContentView.showNetErrorView(Config.errorView)
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    /**
     * 新增方法，用于屏蔽，以前的开发，写的一些骚代码
     *  例如：isInitImmerBar 用于屏蔽，全屏状态设置
     */
    open fun initPageParams() {

    }

    /**
     * 自定义titleBar
     */
    open fun initCustomTitleBar(mTitleBar: TitleBar) {

    }

    /**
     * 初始化TitleBar
     */
    fun initTitleBar() {
        mtitleBar=mTitleBar
        mTitleBar.title = setTitle()
        if (Config.titleBarColor != null) {
            mTitleBar.titleBackground = ContextCompat.getColor(this, Config.titleBarColor!!)
            mTitleBar.background = ContextCompat.getDrawable(this, Config.titleBarColor!!)
            mTitleBarIg.background = ContextCompat.getDrawable(this, Config.titleBarColor!!)
        } else {
            mTitleBar.titleBackground = ContextCompat.getColor(this, BaseApplication.titleBarColor)
            mTitleBar.background = ContextCompat.getDrawable(this, BaseApplication.titleBarColor)
            mTitleBarIg.background = ContextCompat.getDrawable(this, BaseApplication.titleBarColor)
        }

        if (Config.titleBarTextColor != null) {
            mTitleBar.titleColor = ContextCompat.getColor(this, Config.titleBarTextColor!!)
        } else {
            mTitleBar.titleColor = ContextCompat.getColor(this, BaseApplication.titleBarTextColor)
        }

        if (Config.titleBarBackImg != null) {
            mTitleBar.mBackIv.setImageResource(Config.titleBarBackImg!!)
        } else {
            mTitleBar.mBackIv.setImageResource(BaseApplication.titleBarBackImg)
        }
        // 保证文字过长，跑马灯效果能正常运行
        mTitleBar.isFocusable = true
        mTitleBar.isSelected = true
        mTitleBar.isFocusableInTouchMode = true

        mTitleBar.isBackShow = isBackShow
        mTitleBar.mMenu_1.setImageResource(setTitleRightIcon())
        menuIconClick = setMenuIconClickListener()
        mTitleBar.mMenu_1.setOnClickListener { menuIconClick?.invoke() }
        initCustomTitleBar(mTitleBar)
    }


    /**
     * 设置左上角返回图标
     */
    fun setTitleBarLeftBackImg(resId: Int) {
        mTitleBar.mBackIv.setImageResource(resId)
    }

    fun setTitleContent(title: String) {
        mTitleBar.title = title
    }

    /**
     * 目前只有新疆端有分享
     * 四川端的appid是错地
     * 乌市端没得appid
     */
    fun showShareButton(res: Int) {
//        if(BaseApplication.appArea=="xj" ||BaseApplication.appArea=="sc"){
//            mTitleBar.showShareIcon(res)
//        }
        if(BaseApplication.appArea=="xj"){
            mTitleBar.showShareIcon(res)
        }
    }
    fun setShareClick(click:View.OnClickListener){
        mTitleBar.setShareClickListener(click)
    }



    /**
     * 右上角图标点击事件
     */
    private var menuIconClick: (() -> Unit)? = null

    protected open fun setMenuIconClickListener(): (() -> Unit)? = null

    /**
     * 设置
     */
    protected open fun setTitleRightIcon(): Int = 0

    /**
     * 设置需要加载的视图布局
     * @return 视图布局的ViewDataBinding对象
     */
    private fun setBindingContentView(): VB {
        val inflate = DataBindingUtil.inflate<VB>(layoutInflater, getLayout(), mContentView, false)
        inflate.setLifecycleOwner(this)

        val frameLayout = FrameLayout(applicationContext)
        val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT)
        layoutParams.gravity = Gravity.CENTER
        frameLayout.addView(inflate.root)

        mContentView.showContentView(frameLayout)
        return inflate
    }

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
    public fun transactFragment(id: Int, fragment: Fragment, fragmentTag: String? = null) {
        var transient = getSupportFragmentManager().beginTransaction()
        if (fragment.isAdded) {
            transient.show(fragment)
            if (isHideAnother)
                for (i in 0 until fragments.size) {
                    if (fragment != fragments[i]) {
                        transient.hide(fragments[i])
                    }
                }
        } else {
            transient.add(id, fragment, fragmentTag)
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


    fun addFragment(id: Int, fragment: Fragment) {
        var transient = supportFragmentManager.beginTransaction()
        transient.add(id, fragment)
    }

    fun clearFragment() {
        var transient = supportFragmentManager.beginTransaction()
        try {
            for (i in fragments) {
                transient.remove(i).commitNow()
            }
            fragments.clear()
            supportFragmentManager.executePendingTransactions()
        } catch (e: java.lang.Exception) {
            Log.e("clear_fragment", "" + e.message)
        }


    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
        dissMissLoadingDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            AppManager.instance.finishActivity(this)
            mLoadingDialog = null
        } catch (e: Exception) {

        }

    }

    /**
     * 重新加载数据
     *
     * 用于上一次网络请求失败，数据请求错误或需要重新刷新数据时候的情况
     * @date 2019/6/5
     * @author 周俊蒙
     */
    protected fun reloadData() {
        if (NetWorkUtils.isNetWorkAvailable(this)) {
            initData()
        } else {
            ToastUtils.showMessage(resources.getString(R.string.net_work_unavailable))
            mContentView.showNetErrorView(Config.errorView)
        }
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
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                }
                else -> window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
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
    open fun presenter() {
        mModel.mPresenter.observe(this, Observer {
            if (init) {
                init = false
                return@Observer
            }

            if(!it.isNeed){
                return@Observer
            }
            if (it.loading) {
                // 开启网络加载框
                mContentView.showLoadingView(Config.loadingView)
            } else {
                // 关闭网络加载框
                if (it.isNeedRecyleView) {
                    if (it.error) {
                        dissMissLoadingDialog()
                        mContentView.showErrorView()
                    } else {
                        mContentView.showContentView()
                    }
                } else {

                }
            }
            if (it.login) {
                // 需要登录时跳转登录界面
                // TODO:因为Baselib不依赖provicer模块，因此此处地址用写死的LoginActivity的路径字符串，不能用ARouterPath
                // 修改方案为在BaseApplication里定义loginPath作为跳转登录页
                // 清空保存的token
                if (AppUtils.isLogin()) {
                    ToastUtils.showMessage("登录状态失效，请重新登录~")
                    SPUtils.getInstance().put(SPUtils.Config.TOKEN, "")
                } else {
                    ToastUtils.showMessage("非常抱歉，你还未登录~")
                }
                ARouter.getInstance()
                    .build(BaseApplication.loginPath)
                    .navigation()
                it.login = false
//                finish()
            } else {
                if (!it.errorMessage.isNullOrEmpty() && it.isNeedToastMessage) {
                    // 系统错误不显示给用户
//                    if (it.errorMessage == "系统错误!") {
//                        if (BaseApplication.isDebug) {
//                            ToastUtils.showMessage(it.errorMessage)
//                        }
//                    } else {
                    if (!it.isNeedToastMessage.equals("数据不存在")){
                        ToastUtils.showMessage(it.errorMessage)
                    }
//                    }

                    it.errorMessage = ""
                }
            }
            // 视图显示完毕后，将加载状态置为初始状态
//            it.loading = true
        })
    }

    /**
     * 处理需要观察ViewModel中的数据
     */
    protected open fun notifyData() {
        mModel.finish.observe(this, Observer {
            if (it) {
                finish()
            }
        })
        mModel.toast.observe(this, Observer {
            ToastUtils.showMessage(it)
        })
    }

    /**
     * 获取布局文件
     */
    abstract fun getLayout(): Int

    /**
     * 初始化标题
     */
    abstract fun setTitle(): String

    /**
     * 注册ViewModel
     */
    abstract fun injectVm(): Class<VM>

    /**
     * 初始化视图布局
     */
    abstract fun initView()

    /**
     * 初始化数据
     */
    abstract fun initData()

    /**
     * 显示加载框
     */
    public fun showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = CommonLoadingDialog(this)
            mLoadingDialog!!.setCanceledOnTouchOutside(false)
        }
        if (!mLoadingDialog!!.isShowing) {
            mLoadingDialog?.show()
        }
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