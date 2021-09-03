package com.daqsoft.usermodule.ui.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.FeedBackBean
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.network.net.UserService
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityFeedBackLsBinding
import com.daqsoft.usermodule.ui.activity.adapter.FeedBackAdapter

/**
 * @Description 意见反馈
 * @ClassName   FeedBackLsActivity
 * @Author      luoyi
 * @Time        2020/5/21 16:51
 */
@Route(path = ARouterPath.UserModule.USER_FEED_BACK_LS_ACTIVITY)
class FeedBackLsActivity : TitleBarActivity<ActivityFeedBackLsBinding, FeedBackLsViewModel>() {

    var currPage: Int = 1

    var adapter: FeedBackAdapter? = null

    override fun getLayout(): Int {
        return R.layout.activity_feed_back_ls
    }

    override fun setTitle(): String {
        return "反馈记录"
    }

    override fun injectVm(): Class<FeedBackLsViewModel> {
        return FeedBackLsViewModel::class.java
    }

    override fun initView() {
        adapter = FeedBackAdapter(this@FeedBackLsActivity)
        mBinding.recyFeedBackLs.layoutManager = LinearLayoutManager(this@FeedBackLsActivity, LinearLayoutManager.VERTICAL, false)
        mBinding.recyFeedBackLs.adapter = adapter
        adapter?.emptyViewShow = false

        mBinding.swflFeedBack.setOnRefreshListener {
//            mBinding.swflFeedBack.isRefreshing = true
            currPage = 1
            mModel.getFeedBackLs(currPage)
        }

        adapter?.setOnLoadMoreListener {
            currPage += 1
            mModel.getFeedBackLs(currPage)
        }
        mModel.feedBasksLiveData.observe(this, Observer {
            dissMissLoadingDialog()
//            mBinding.swflFeedBack.isRefreshing = false
            mBinding.swflFeedBack.finishRefresh()
            if (currPage == 1) {
//                mBinding.recyFeedBackLs.smoothScrollToPosition(0)
                adapter!!.clear()
            }
            if (!it.isNullOrEmpty()) {
                adapter!!.add(it!!)
            } else {
                adapter!!.emptyViewShow = true
            }

            if (it.isNullOrEmpty() || it.size < mModel.pageSize) {
                adapter?.loadEnd()
            } else {
                adapter?.loadComplete()
            }
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.swflFeedBack.finishRefresh()
        })
    }

    override fun initData() {
        showLoadingDialog()
        mModel.getFeedBackLs(currPage)
    }
}


/**
 * @Description 意见反馈 viewModel
 * @ClassName    FeedBackLsViewModel
 * @Author      luoyi
 * @Time        2020/5/21 16:51
 */
class FeedBackLsViewModel : BaseViewModel() {

    var pageSize: Int = 10

    var feedBasksLiveData: MutableLiveData<MutableList<FeedBackBean>> = MutableLiveData()


    /**
     * 获取意见反馈
     */
    fun getFeedBackLs(currPage: Int) {
        mPresenter.value?.loading = false
        UserRepository().userService?.getFeedBackLs(currPage, pageSize)
            ?.excute(object : BaseObserver<FeedBackBean>() {
                override fun onSuccess(response: BaseResponse<FeedBackBean>) {
                    feedBasksLiveData.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<FeedBackBean>) {
                    mError.postValue(response)
                }

            })
    }
}