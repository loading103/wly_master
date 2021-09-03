package com.daqsoft.legacyModule.mine

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.bean.FansBean
import com.daqsoft.legacyModule.databinding.ActivityMineFansListBinding
import com.daqsoft.legacyModule.mine.adpter.LegacyFansAdapter
import com.daqsoft.legacyModule.net.LegacyRepository
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.network.comment.CommentRepository

/**
 *@package:com.daqsoft.legacyModule.mine
 *@date:2020/5/18 18:52
 *@author: caihj
 *@des:粉丝列表
 **/
@Route(path = ARouterPath.LegacyModule.LEGACY_MINE_FANS)
class MineFansListActivity : TitleBarActivity<ActivityMineFansListBinding, MineFansViewModel>() {

    @Autowired
    @JvmField
    var type = ""

    override fun getLayout(): Int = R.layout.activity_mine_fans_list

    override fun setTitle(): String = getTitleName()

    override fun injectVm(): Class<MineFansViewModel> = MineFansViewModel::class.java

    var adapter: LegacyFansAdapter? = null

    fun getTitleName(): String = when (type) {
        "fans" -> getString(R.string.legacy_module_mine_fans)
        "watch" -> getString(R.string.legacy_module_mine_focus)
        else -> getString(R.string.legacy_module_pk_count)
    }

    override fun initView() {
        adapter = LegacyFansAdapter(this, type)
        adapter?.setOnLoadMoreListener {
            mModel.mCurrPage++
            if (type != "pk") {
                mModel.getFansList()
            } else {
                mModel.getPkList()
            }
        }
        mBinding.mSwipeRefreshLayout.setOnRefreshListener {
//            mBinding.mSwipeRefreshLayout.isRefreshing = false
            mModel.mCurrPage = 1
            if (type != "pk") {
                mModel.getFansList()
            } else {
                mModel.getPkList()
            }
        }
        mBinding.rvFans.layoutManager = LinearLayoutManager(this)
        mBinding.rvFans.adapter = adapter
        mModel.fansList.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.mSwipeRefreshLayout.finishRefresh()
            if(it.isNotEmpty()){
                pageDeal(mModel.mCurrPage,it,adapter!!)
                adapter?.clear()
                adapter!!.add(it)
                adapter?.notifyDataSetChanged()
            }
        })
        adapter!!.onItemClick = object : LegacyFansAdapter.OnItemClickListener {
            override fun onItemClick(id: String, position: Int, status: Boolean) {
                if (!status) {
                    mModel.focusCancelHeritagePeople(id)
                } else {
                    mModel.focusHeritagePeople(id, position)

                }
            }
        }
        mModel.focusStatus.observe(this, Observer {
            if (it != -1) {
                adapter!!.clear()
                if (type != "pk") {
                    mModel.getFansList()
                } else {
                    mModel.getPkList()
                }
            }
        })
        mModel.cancelFocusStatus.observe(this, Observer {
            if (it) {
                adapter!!.clear()
                if (type != "pk") {
                    mModel.getFansList()
                } else {
                    mModel.getPkList()
                }
            }
        })
    }

    override fun initData() {
        mModel.type = type
        showLoadingDialog()
        if (type != "pk") {
            mModel.getFansList()
        } else {
            mModel.getPkList()
        }
    }

    private fun pageDeal(
        page: Int?, response: MutableList<FansBean>, adapter:
        RecyclerViewAdapter<*, *>
    ) {
        if (page == null) {
            return
        }
        if (page == 1) {
            adapter.clear()
        }
        if (response == null) {
            adapter.loadEnd()
            return
        }
        if (response.size >= mModel.pageSize) {
            adapter.loadComplete()
        } else {
            adapter.loadEnd()
        }
    }
}

class MineFansViewModel : BaseViewModel() {
    var fansList = MutableLiveData<MutableList<FansBean>>()
    var type = ""
    var mCurrPage = 1
    var pageSize = 10
    fun getFansList() {
        LegacyRepository.service.getFansList(pageSize, mCurrPage, type)
            .excute(object : BaseObserver<FansBean>() {
                override fun onSuccess(response: BaseResponse<FansBean>) {
                    fansList.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<FansBean>) {
                    super.onFailed(response)
                    fansList.postValue(response.datas)
                }

            })
    }

    var focusStatus = MutableLiveData<Int>()

    fun focusHeritagePeople(id: String, position: Int) {
        CommentRepository.service.attentionResource(id, ResourceType.CONTENT_TYPE_HERITAGE_PEOPLE).excute(object : BaseObserver<Any>(mPresenter) {
            override fun onSuccess(response: BaseResponse<Any>) {
                if (response.code == 0) {
                    ToastUtils.showMessage("关注成功!")
                    focusStatus.postValue(position)
                } else {
                    ToastUtils.showMessage(response.message)
                    focusStatus.postValue(-1)
                }
            }
        })
    }

    var cancelFocusStatus = MutableLiveData<Boolean>()

    /**
     * 取消关注
     */
    fun focusCancelHeritagePeople(id: String) {
        CommentRepository.service.attentionResourceCancle(id, ResourceType.CONTENT_TYPE_HERITAGE_PEOPLE).excute(object : BaseObserver<Any>(mPresenter) {
            override fun onSuccess(response: BaseResponse<Any>) {
                if (response.code == 0) {
                    ToastUtils.showMessage("取消成功!")
                    cancelFocusStatus.postValue(true)
                } else {
                    ToastUtils.showMessage(response.message)
                }
            }
        })
    }

    fun getPkList() {
        LegacyRepository.service.getPkPeopleList(type = "watch", currPage = mCurrPage, pageSize = pageSize)
            .excute(object : BaseObserver<FansBean>() {
                override fun onSuccess(response: BaseResponse<FansBean>) {
                    fansList.postValue(response.datas)
                }

            })
    }
}