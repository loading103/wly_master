package com.daqsoft.travelCultureModule.venuecollect.fragment

import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.FragExhibitionListBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.travelCultureModule.venuecollect.adapter.ExhibitionListAdapter
import com.daqsoft.travelCultureModule.venuecollect.viewmodel.ExhibitionLsViewModel
import com.scwang.smartrefresh.layout.footer.ClassicsFooter

/**
 * @Description 常设展览
 * @ClassName   MessageListFragment
 */
class ExhibitionOnShowFragment: BaseFragment<FragExhibitionListBinding, ExhibitionLsViewModel>() {
    // List适配器
    private val mAdapter by lazy { ExhibitionListAdapter() }

    override fun getLayout(): Int {
        return R.layout.frag_exhibition_list
    }

    override fun injectVm(): Class<ExhibitionLsViewModel> {
        return ExhibitionLsViewModel::class.java
    }

    override fun initView() {
        initViewModel()
        initEvent()
        mBinding.recyMsgCenters.apply {
            adapter = mAdapter
        }
        mBinding.srlMsgCenter.apply {
            setEnableLoadMore(true)
            setRefreshFooter(ClassicsFooter(context).setDrawableSize(20f))
            setOnRefreshListener {
                mModel.pageManager.initPageIndex()
                mModel.getTopListDatas()
            }
            setOnLoadMoreListener{
                mModel.pageManager.nexPageIndex
                mModel.getTopListDatas()
            }
        }
    }

    override fun initData() {
        mModel.pageManager.initPageIndex()
        mModel.getTopListDatas()
    }

    private fun initViewModel() {
     mModel.listdatas.observe(this, androidx.lifecycle.Observer {
         mBinding.srlMsgCenter.finishRefresh()
         dissMissLoadingDialog()
         if (mModel.pageManager.isFirstIndex) {
             mAdapter.clear()
             mAdapter.setNewData(it)
             if( mAdapter.getData().size==mModel.totleNumber){
                 mBinding.srlMsgCenter.finishRefreshWithNoMoreData()
             }else{
                 mBinding.srlMsgCenter.finishRefresh()
             }
         }else{
             mAdapter.getData().addAll(it)
             if( mAdapter.getData().size==mModel.totleNumber){
                 mBinding.srlMsgCenter.finishLoadMoreWithNoMoreData()
             }else{
                 mBinding.srlMsgCenter.finishLoadMore()
             }
         }
         // 框架已经实现了空界面
         if(mAdapter.getData().isEmpty()){
             mBinding.llEmpty.llRppt.visibility= View.GONE
         }else{
             mBinding.llEmpty.llRppt.visibility= View.GONE
         }
         mAdapter.notifyDataSetChanged()
     })
        // 收藏
        mModel.collectLiveData.observe(this, androidx.lifecycle.Observer {
            dissMissLoadingDialog()
            mAdapter?.notifyCollectStatus(it, true)
        })


        // 取消收藏
        mModel.canceCollectLiveData.observe(this, androidx.lifecycle.Observer {
            dissMissLoadingDialog()
            mAdapter?.notifyCollectStatus(it, false)
        })
    }

    private fun initEvent() {
        // 点击收藏
        mAdapter?.onCollectClickListener = object : ExhibitionListAdapter.OnCollectClickListener {
            override fun onCollectClick(id: String, postion: Int, status: Boolean, type: String) {
                if (AppUtils.isLogin()) {
//                    showLoadingDialog()
                    if (status) {
                        mModel.collectionCancelScenic(id, postion, type)
                    } else {
                        mModel.collectionScenic(id, postion, type)
                    }
                } else {
                    ToastUtils.showMessage("该操作需要登录，请先登录")
                    ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                        .navigation()
                }
            }

        }
    }
}
