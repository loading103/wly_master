package com.daqsoft.travelCultureModule.story

import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityTopicBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.HomeTopicBean
import com.daqsoft.travelCultureModule.story.story.TopicLsAdapter
import com.daqsoft.travelCultureModule.story.vm.TopicViewModel

/**
 * 话题列表页面
 * @author caihj
 * @date 2020/4/2 0020
 * @version 1.0.0
 * @since JDK 1.8
 */
@Route(path = MainARouterPath.MAIN_TOPIC_LIST)
class TopicActivity : TitleBarActivity<ActivityTopicBinding, TopicViewModel>() {
    @JvmField
    @Autowired
    var name: String = ""

    val topicAdapter by lazy { TopicLsAdapter(this@TopicActivity) }

    override fun getLayout(): Int = R.layout.activity_topic

    override fun setTitle(): String = resources.getString(R.string.home_topic_list_titles)

    override fun injectVm(): Class<TopicViewModel> = TopicViewModel::class.java

    override fun initView() {

        if(!name.isNullOrEmpty()){
            mModel.name=name
        }
        mBinding.rvTopic.visibility = View.GONE
        mBinding.rvTopic.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        mBinding.rvTopic.adapter = topicAdapter
        // 加载更多控件
        topicAdapter!!.setOnLoadMoreListener {
            mModel?.currPage = mModel?.currPage + 1
            mModel?.getTopicList()
        }
        // 刷新控件
        mBinding?.sfTopic.setOnRefreshListener {
//            mBinding?.sfTopic.isRefreshing = true
            mModel?.currPage = 1
            mModel?.getTopicList()
        }
        // 收藏
        mModel.collectVenueLiveData.observe(this, Observer {
            if (topicAdapter != null) {
                topicAdapter?.notifyCollectStatus(it)
            }
        })

        // 取消收藏
        mModel.canceCollectLiveData.observe(this, Observer {
            if (topicAdapter != null) {
                topicAdapter?.notifyCollectStatus(it)
            }
        })

        mBinding.etSearchTopic!!.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo
                    .IME_ACTION_UNSPECIFIED || actionId == EditorInfo.IME_ACTION_DONE
            ) {
                var content = mBinding.etSearchTopic.text.toString()
//                mBinding?.sfTopic.isRefreshing = true
                mModel?.currPage = 1
                topicAdapter!!.clear()
                mModel.getTopicList(content)
            }
            false
        })
        initEvent()
    }


    override fun initData() {
        showLoadingDialog()
        mModel.getTopicList()
        mModel.topicList.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding?.rvTopic.visibility = View.VISIBLE
//            mBinding?.sfTopic.isRefreshing = false
            mBinding.sfTopic.finishRefresh()
            pageDeal(it)
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding?.rvTopic.visibility = View.VISIBLE
        })

    }

    private fun initEvent() {
        topicAdapter?.onItemClick = object : TopicLsAdapter.OnItemClickListener {
            override fun onItemClick(id: String, position: Int, status: Boolean) {
                if (AppUtils.isLogin()) {
                    if (status) {
                        mModel?.canceCollect(id, position)
                    } else {
                        mModel?.collect(id, position)
                    }
                } else {
                    ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                    ARouter.getInstance()
                        .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                        .navigation()
                }
            }

        }
    }


    /**
     * 列表数据页码处理
     *
     * @param page     当前页
     * @param response 返回数据体
     * @param adapter  适配器
     */
    private fun pageDeal(data: MutableList<HomeTopicBean>) {
        if (mModel.currPage == 1) {
            mBinding?.rvTopic.smoothScrollToPosition(0)
            mBinding?.rvTopic.visibility = View.VISIBLE
            topicAdapter!!.clear()
            topicAdapter!!.emptyViewShow = data.isNullOrEmpty()

        }
        if (!data.isNullOrEmpty()) {
            topicAdapter!!.add(data)
        }
        if (data.isNullOrEmpty() || data.size < mModel.pageSize) {
            topicAdapter!!.loadEnd()
        } else {
            topicAdapter!!.loadComplete()
        }
    }
}