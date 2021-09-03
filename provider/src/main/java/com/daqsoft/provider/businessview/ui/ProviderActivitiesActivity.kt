package com.daqsoft.provider.businessview.ui

import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.businessview.adapter.ProviderActivityAdapter
import com.daqsoft.provider.businessview.viewmodel.ProviderActivitiesViewModel
import com.daqsoft.provider.databinding.ActivityProviderActivitiesBinding
import com.daqsoft.provider.network.comment.beans.CommentBean

/**
 * @Description
 * @ClassName   ProviderActivityActivity
 * @Author      luoyi
 * @Time        2020/5/8 14:50
 */
@Route(path = ARouterPath.Provider.PROVIDER_ACTIVITIES)
class ProviderActivitiesActivity : TitleBarActivity<ActivityProviderActivitiesBinding, ProviderActivitiesViewModel>() {
    @JvmField
    @Autowired
    var id: String = ""

    @JvmField
    @Autowired
    var type: String = ""

    var pageIndex: Int = 1

    private var adapter: ProviderActivityAdapter? = null

    override fun getLayout(): Int {
        return R.layout.activity_provider_activities
    }

    override fun setTitle(): String {
        return "活动列表"
    }

    override fun injectVm(): Class<ProviderActivitiesViewModel> {
        return ProviderActivitiesViewModel::class.java
    }

    override fun initView() {
        adapter = ProviderActivityAdapter(this@ProviderActivitiesActivity)
        adapter?.emptyViewShow = false
        mBinding.recyProviderActivities.layoutManager = LinearLayoutManager(
            this@ProviderActivitiesActivity, LinearLayoutManager.VERTICAL,
            false
        )
        mBinding.recyProviderActivities.adapter = adapter

        initViewModel()
    }

    private fun initViewModel() {
        mModel.activitiesLiveData.observe(this, Observer {
            pageDealed(it)
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.swiperActivities.finishRefresh()
            adapter?.loadComplete()
        })
        adapter?.setOnLoadMoreListener {
            pageIndex += 1
            mModel.getProviderActivities(pageIndex, id, type)
        }
        mBinding.swiperActivities.setOnRefreshListener {
            pageIndex = 1
            mModel.getProviderActivities(pageIndex, id, type)
        }
    }

    override fun initData() {
        showLoadingDialog()
        mModel.getProviderActivities(pageIndex, id, type)
    }

    /**
     * 处理评论列表分页
     */
    private fun pageDealed(it: MutableList<ActivityBean>?) {
        dissMissLoadingDialog()
        if (!mBinding.recyProviderActivities.isVisible) {
            mBinding.recyProviderActivities.visibility = View.VISIBLE
        }
//        mBinding.swiperActivities.isRefreshing = false
        mBinding.swiperActivities.finishRefresh()
        if (pageIndex == 1) {
            adapter?.clear()
        }
        if (!it.isNullOrEmpty()) {
            adapter?.add(it!!)
        }
        if (it.isNullOrEmpty() || it.size < mModel.pageSize) {
            if (it.isNullOrEmpty() && pageIndex == 1) {
                adapter?.emptyViewShow = true
            } else {
                adapter?.loadEnd()
            }
        } else {
            adapter?.loadComplete()
        }

    }
}