package com.daqsoft.baselib.base

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.config.Config
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.scwang.smart.refresh.layout.constant.RefreshState
import daqsoft.com.baselib.R
import daqsoft.com.baselib.databinding.ActivityListBinding
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.activity_title_bar.*

/**
 * 列表Activity
 *
 * （只适用于一个标题一个RecyclerView，且RecyclerView为纵向布局的情况，若列表页有其他布局请不要使用该Activity）
 *
 * @param VB itemDataBinding
 * @param VM Activity ViewModel
 * @param D 列表实体类
 *
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-6-11
 * @since JDK 1.8.0_191
 */
abstract class ListActivity<VB : ViewDataBinding, VM : BaseViewModel, D : Any> :
    TitleBarActivity<ActivityListBinding, VM>() {

    override fun getLayout(): Int = R.layout.activity_list
    /**
     * item布局
     */
    protected val item_layout by lazy { getItemLayout() }
    /**
     * 数据集合
     */
    protected val data = arrayListOf<D>()
    protected var page = 1
    protected var pageSize = 10
    protected val adapter = object : RecyclerViewAdapter<VB, D>(item_layout, data) {
        override fun setVariable(mBinding: VB, position: Int, item: D) {
            this@ListActivity.setVariable(mBinding, position, item)
        }
    }

    override fun initView() {
        notifyDatas()
        mBinding.mSwipeRefreshLayout.setOnRefreshListener {
            page = 1
            adapter.clear()
            reloadData()
//            mSwipeRefreshLayout.isRefreshing = false
        }
        mRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        mRecyclerView.adapter = adapter
        adapter.setOnLoadMoreListener {
            page++
            reloadData()
        }
    }


    /**
     * 获取item布局
     * @author 周俊蒙
     * @return 返回item布局文件
     * @date 2019/6/11
     */
    abstract fun getItemLayout(): Int

    /**
     * 为item设置databingidng
     * @author 周俊蒙
     * @date 2019/6/11
     */
    abstract fun setVariable(mBinding: VB, position: Int, item: D)

    /**
     * 处理请求的数据回调
     * @author 周俊蒙
     * @date 2019/6/11
     */
    abstract fun notifyDatas()

    /**
     * 统一处理ViewModel传过来的回调
     */
    override fun presenter() {

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
                // 开启网络加载框
                mContentView.showLoadingView(Config.loadingView)
            } else {
                // 关闭网络加载框
                if (mBinding.mSwipeRefreshLayout != null && mBinding.mSwipeRefreshLayout.state == RefreshState.Refreshing) {
//                    mBinding.mSwipeRefreshLayout.isRefreshing = false
                    mBinding.mSwipeRefreshLayout.finishRefresh()
                }
                if (it.error) {
                    mContentView.showErrorView()
                } else {
                    mContentView.showContentView()
                }
            }
            if (it.login) {
                // 需要登录时跳转登录界面
                // TODO:因为Baselib不依赖provicer模块，因此此处地址用写死的LoginActivity的路径字符串，不能用ARouterPath
                // 修改方案为在BaseApplication里定义loginPath作为跳转登录页
                // 清空保存的token
                SPUtils.getInstance().put(SPUtils.Config.TOKEN, "")
                ARouter.getInstance()
                    .build(BaseApplication.loginPath)
                    .navigation()
                it.login = false
            }
            // 视图显示完毕后，将加载状态置为初始状态
//            it.loading = true
        })
    }


}