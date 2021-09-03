package com.daqsoft.usermodule.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.bean.MineFocusMassBean
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.FocusItemActivityBinding
import com.daqsoft.usermodule.databinding.ItemActivityTagBinding
import com.daqsoft.usermodule.databinding.ItemFocusMassBinding
import com.daqsoft.usermodule.databinding.MineFocusListBinding
import com.jakewharton.rxbinding2.view.RxView
import org.jetbrains.anko.textColor
import java.util.concurrent.TimeUnit

/**
 *@package:com.daqsoft.usermodule.ui.fragment
 *@date:2020/5/28 14:04
 *@author: caihj
 *@des:TODO
 **/
class FocusSocialTeamFragment: BaseFragment<MineFocusListBinding,FocusSocialTeamVm>(){
    override fun getLayout(): Int = R.layout.mine_focus_list

    override fun injectVm(): Class<FocusSocialTeamVm> = FocusSocialTeamVm::class.java

    override fun initView() {
       mBinding.recyclerFocus.apply {
           layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
           adapter = adapters
       }
        mBinding.refreshFocus.setOnRefreshListener {
            mModel.currPage = 1
            mModel.getMineFocusMass()
        }
        adapters.setOnLoadMoreListener {
            mModel.currPage ++
            mModel.getMineFocusMass()
        }
        mModel.focusMassDatas.observe(this, Observer {
            dissMissLoadingDialog()
            if(it!=null){
                pageDeal(it.datas)
            }
        })
    }


    var adapters = object : RecyclerViewAdapter<ItemFocusMassBinding, MineFocusMassBean>(
        R.layout.item_focus_mass
    ) {
        override fun setVariable(
            mBinding: ItemFocusMassBinding,
            position: Int,
            item: MineFocusMassBean
        ) {
            mBinding.item = item

            Glide.with(context!!)
                .load(item.getImage())
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .into(mBinding.ivImg)

            // 取消关注
            mBinding.tvCancelFocus.setOnClickListener {
                mModel.attentionResourceCancle(item.id.toString())
                getData().removeAt(position)
                notifyDataSetChanged()
            }
            // 跳转详情
            mBinding.root.setOnClickListener {
                ARouter.getInstance().build(MainARouterPath.MAIN_CLUB_INFO)
                    .withInt("id", item.id)
                    .navigation()
            }
        }

    }

    override fun initData() {
        showLoadingDialog()
        mModel.getMineFocusMass()
    }


    private fun pageDeal(it: MutableList<MineFocusMassBean>?) {
        mBinding.refreshFocus.isRefreshing = false
        if (mModel.currPage == 1) {
            mBinding.recyclerFocus.smoothScrollToPosition(0)
            adapters!!.clear()
        }
        if (!it.isNullOrEmpty()) {
            adapters!!.add(it!!)
        }
        if (it.isNullOrEmpty() || it.size < mModel.pageSize) {
            adapters?.loadEnd()
        } else {
            adapters?.loadComplete()
        }
    }

}

class FocusSocialTeamVm:BaseViewModel(){
    /**
     * 关注社团的数据
     */
    var focusMassDatas = MutableLiveData<BaseResponse<MineFocusMassBean>>()

    var currPage = 1
    val pageSize = 20

    /**
     * 获取关注社团的数据
     */
    fun getMineFocusMass() {
        UserRepository().userService.getMineFocusMass(currPage)
            .excute(object : BaseObserver<MineFocusMassBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<MineFocusMassBean>) {
                    focusMassDatas.postValue(response)
                }

            })
    }


    //关注资源
    fun attentionResource(resourceId: String, resourceType: String = ResourceType.CONTENT_TYPE_ASSOCIATION) {
        mPresenter.value?.loading = true
        CommentRepository.service.attentionResource(resourceId, resourceType).excute(object : BaseObserver<Any>(mPresenter) {
            override fun onSuccess(response: BaseResponse<Any>) {
            }
        })
    }

    //取消关注资源
    fun attentionResourceCancle(resourceId: String, resourceType: String = ResourceType.CONTENT_TYPE_ASSOCIATION) {
        mPresenter.value?.loading = true
        CommentRepository.service.attentionResourceCancle(resourceId, resourceType).excute(object : BaseObserver<Any>(mPresenter) {
            override fun onSuccess(response: BaseResponse<Any>) {
            }
        })
    }
}