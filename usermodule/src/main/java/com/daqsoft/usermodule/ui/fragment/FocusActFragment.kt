package com.daqsoft.usermodule.ui.fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.bean.MineFocusMassBean
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.FragFocusActBinding
import com.daqsoft.usermodule.ui.collection.MineFocusActAdapter
import kotlin.math.min

/**
 * @Description 关注活动
 * @ClassName   FocusActFragment
 * @Author      luoyi
 * @Time        2020/9/10 19:40
 */
class FocusActFragment : BaseFragment<FragFocusActBinding, FocusActViewModel>() {

    private var currPage: Int = 1

    private val mineActFocusAdapter: MineFocusActAdapter by lazy {
        MineFocusActAdapter(context!!)
    }

    override fun getLayout(): Int {
        return R.layout.frag_focus_act
    }

    override fun initView() {
        initViewModel()
        mBinding.rvFocusAct.apply {
            layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
            adapter = mineActFocusAdapter
        }
        mBinding.swFocusAct.setOnRefreshListener {
            currPage = 1
            mModel.getMineActivityLs(currPage)
        }
        mineActFocusAdapter.run {
            onMineFocusActListener = object : MineFocusActAdapter.OnMineFocusActListener {
                override fun onCanceFocus(item: ActivityBean, position: Int) {
                    showLoadingDialog()
                    mModel.attentionResourceCancle(
                        item.id,
                        ResourceType.CONTENT_TYPE_ACTIVITY,
                        position
                    )
                    setOnLoadMoreListener {
                        currPage += 1
                        mModel.getMineActivityLs(currPage)
                    }
                }

            }
        }
    }

    private fun initViewModel() {
        mModel.canceAttentLd.observe(this, Observer {
            dissMissLoadingDialog()
            if (it >= 0) {
                mineActFocusAdapter.removeItem(it)
            }
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()

        })
        mModel.activities.observe(this, Observer {
            mBinding.swFocusAct.finishRefresh()
            pageDeal(currPage, it, mineActFocusAdapter)
            if (it != null && it.datas!!.isNotEmpty()) {
                mineActFocusAdapter.add(it.datas!!)
            }

        })
    }

    override fun initData() {
        mModel.getMineActivityLs(currPage)
    }

    override fun injectVm(): Class<FocusActViewModel> {
        return FocusActViewModel::class.java
    }


    /**
     * 列表数据页码处理
     *
     * @param page     当前页
     * @param response 返回数据体
     * @param adapter  适配器
     */
    private fun pageDeal(
        page: Int?,
        response: BaseResponse<*>,
        adapter: RecyclerViewAdapter<*, *>
    ) {
        if (page == null) {
            return
        }
        if (page == 1) {
            adapter.clear()
        }
        if (response.page == null) {
            adapter.loadEnd()
            return
        }
        if (response.page!!.currPage < response.page!!.totalPage) {
            adapter.loadComplete()
        } else {
            adapter.loadEnd()
        }
    }
}


class FocusActViewModel : BaseViewModel() {

    /**
     * 活动列表
     */
    val activities = MutableLiveData<BaseResponse<ActivityBean>>()
    var canceAttentLd = MutableLiveData<Int>()

    fun getMineActivityLs(currPage: Int) {
        val param = HashMap<String, String>()
        param["queryType"] = "1"
        param["currPage"] = currPage.toString()
        param["pageSize"] = "10"
        UserRepository().userService.getActivityList(param)
            .excute(object : BaseObserver<ActivityBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ActivityBean>) {
                    activities.postValue(response)
                }

                override fun onFailed(response: BaseResponse<ActivityBean>) {
                    mError.postValue(response)
                }

            })
    }


    //关注资源
    fun attentionResource(
        resourceId: String,
        resourceType: String = ResourceType.CONTENT_TYPE_ASSOCIATION
    ) {
        mPresenter.value?.loading = true
        CommentRepository.service.attentionResource(resourceId, resourceType)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                }
            })
    }

    //取消关注资源
    fun attentionResourceCancle(
        resourceId: String,
        resourceType: String = ResourceType.CONTENT_TYPE_ASSOCIATION, position: Int
    ) {
        mPresenter.value?.loading = true
        CommentRepository.service.attentionResourceCancle(resourceId, resourceType)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    canceAttentLd?.postValue(position)
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    mError?.postValue(response)
                }
            })
    }
}