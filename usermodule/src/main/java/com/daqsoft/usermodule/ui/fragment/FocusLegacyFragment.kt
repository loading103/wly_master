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
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.bean.LegacyPeopleFansBean
import com.daqsoft.provider.bean.MineFocusMassBean
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.*
import com.jakewharton.rxbinding2.view.RxView
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.textColor
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit

/**
 *@package:com.daqsoft.usermodule.ui.fragment
 *@date:2020/5/28 14:04
 *@author: caihj
 *@des:TODO
 **/
class FocusLegacyFragment: BaseFragment<MineFocusLegacyListBinding,FocusLegacyVm>(){

    override fun getLayout(): Int = R.layout.mine_focus_legacy_list

    override fun injectVm(): Class<FocusLegacyVm> = FocusLegacyVm::class.java

    override fun initView() {
        mBinding.rvLegacy.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        mBinding.rvLegacy.adapter = adapters
        mModel.focusPeopleDatas.observe(this, Observer {

            mBinding.smartRefreshLayout.finishRefresh()
            dissMissLoadingDialog()
            if(it!=null){
                pageDeal(it)
            }
        })

        mBinding.smartRefreshLayout.setOnRefreshListener {
           mModel.getFocusPeople()
        }

    }


    var adapters = object : RecyclerViewAdapter<FocusItemLegacyBinding, LegacyPeopleFansBean>(
        R.layout.focus_item_legacy
    ) {
        override fun setVariable(
            mBinding: FocusItemLegacyBinding,
            position: Int,
            item: LegacyPeopleFansBean
        ) {
            Glide.with(context!!)
                .load(item.headUrl)
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .into(mBinding.ivImg)
            mBinding.tvTitle.text = item.nickName
            mBinding.tvType.text = item.heritagePeopleType
            val numstr = DividerTextUtils.convertDotString(StringBuilder(),"${item.storyNum} 个作品","${item.fans} 个粉丝","${item.showNum} 次浏览")
            mBinding.tvNum.text = numstr
            // 取消关注
            mBinding.tvCancelFocus.setOnClickListener {
                mModel.attentionResourceCancle(item.heritagePeopleId)
                getData().removeAt(position)
                notifyDataSetChanged()
            }
            // 跳转详情
            mBinding.root.setOnClickListener {
                ARouter.getInstance().build(ARouterPath.LegacyModule.LEGACY_PEOPLE_DETAIL_ACTIVITY)
                    .withString("id", item.heritagePeopleId)
                    .navigation()
            }
        }

    }

    override fun initData() {
        showLoadingDialog()
        mModel.getFocusPeople()
    }


    private fun pageDeal(it: MutableList<LegacyPeopleFansBean>?) {
        if (mModel.currPage == 1) {
            mBinding.rvLegacy.smoothScrollToPosition(0)
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

class FocusLegacyVm:BaseViewModel(){
    /**
     * 关注社团的数据
     */
    var focusPeopleDatas = MutableLiveData<MutableList<LegacyPeopleFansBean>>()

    var currPage = 1
    val pageSize = 20


    fun getFocusPeople(){
        UserRepository.userService.getLegacyPeopleList().excute(object :BaseObserver<LegacyPeopleFansBean>(){
            override fun onSuccess(response: BaseResponse<LegacyPeopleFansBean>) {
                focusPeopleDatas.postValue(response.datas)
            }

        })
    }

    //取消关注资源
    fun attentionResourceCancle(resourceId: String, resourceType: String = ResourceType.CONTENT_TYPE_HERITAGE_PEOPLE) {
        mPresenter.value?.loading = true
        CommentRepository.service.attentionResourceCancle(resourceId, resourceType).excute(object : BaseObserver<Any>(mPresenter) {
            override fun onSuccess(response: BaseResponse<Any>) {
                getFocusPeople()
            }
        })
    }
}