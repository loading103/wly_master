package com.daqsoft.usermodule.ui.fragment

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
import com.daqsoft.provider.bean.MineFocusMassBean
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.FragFocusMassBinding
import com.daqsoft.usermodule.databinding.ItemFocusMassBinding
import java.lang.Exception

/**
 * @Description 社团关注
 * @ClassName   FocusMassFragment
 * @Author      luoyi
 * @Time        2020/9/10 19:41
 */
class FocusMassFragment : BaseFragment<FragFocusMassBinding, FocusMassViewModel>() {
    var currpage: Int = 1

    override fun getLayout(): Int {
        return R.layout.frag_focus_mass
    }

    override fun injectVm(): Class<FocusMassViewModel> {
        return FocusMassViewModel::class.java
    }

    override fun initView() {
        focusMassAdapter.run {
            setOnLoadMoreListener {
                currpage += 1
            }
        }
        mBinding.rvFocusMass.apply {
            adapter = focusMassAdapter
            layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun initData() {
        mModel.getMineFocusMass(currpage)
    }

    override fun notifyData() {
        super.notifyData()
        mModel.focusMassDatas.observe(this, Observer {
            mBinding.swFocusMass.finishRefresh()
            pageDeal(currpage, it, focusMassAdapter)
            if (it.datas != null && it.datas!!.isNotEmpty()) {
                focusMassAdapter.add(it.datas!!)
                focusMassAdapter.notifyDataSetChanged()
            }
        })
        mModel.mError.observe(this, Observer {
            mBinding.swFocusMass.finishRefresh()
        })
        mModel.canceFouceLd.observe(this, Observer {
            try {
                if (it in focusMassAdapter.getData().indices) {
                    focusMassAdapter.removeItem(it)
                }
            } catch (e: Exception) {
            }

        })
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

    var focusMassAdapter = object : RecyclerViewAdapter<ItemFocusMassBinding, MineFocusMassBean>(
        R.layout.item_focus_mass
    ) {
        override fun setVariable(
            mBinding: ItemFocusMassBinding,
            position: Int,
            item: MineFocusMassBean
        ) {
            mBinding.item = item

            Glide.with(contentView!!)
                .load(item.getImage())
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .into(mBinding.ivImg)

            // 取消关注
            mBinding.tvCancelFocus.setOnClickListener {
                mModel.attentionResourceCancle(
                    item.id.toString(),
                    ResourceType.CONTENT_TYPE_ASSOCIATION,
                    position
                )
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
}

class FocusMassViewModel : BaseViewModel() {
    /**
     * 关注社团的数据
     */
    var focusMassDatas = MutableLiveData<BaseResponse<MineFocusMassBean>>()

    var canceFouceLd = MutableLiveData<Int>()

    /**
     * 获取关注社团的数据
     */
    fun getMineFocusMass(currPage: Int) {
        UserRepository().userService.getMineFocusMass(currPage)
            .excute(object : BaseObserver<MineFocusMassBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<MineFocusMassBean>) {
                    focusMassDatas.postValue(response)
                }

                override fun onFailed(response: BaseResponse<MineFocusMassBean>) {
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
                    canceFouceLd.postValue(position)
                }
            })
    }
}