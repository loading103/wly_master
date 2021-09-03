package com.daqsoft.thetravelcloudwithculture.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.alibaba.android.vlayout.layout.SingleLayoutHelper
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.provider.bean.StyleTemplate
import com.daqsoft.provider.net.TemplateApi
import com.daqsoft.provider.uiTemplate.banner.AdsBannerAdapter
import com.daqsoft.provider.uiTemplate.component.ComponentTemplateAdapter
import com.daqsoft.provider.uiTemplate.menu.topMenu.TopMenuAdapter
import com.daqsoft.provider.uiTemplate.operation.OperationTemplateAdapter
import com.daqsoft.provider.uiTemplate.titleBar.activity.SCPopularActivitiesStyle
import com.daqsoft.provider.uiTemplate.titleBar.found.FoundTemplateAdapter
import com.daqsoft.provider.uiTemplate.titleBar.tourguide.TourGuideTitleAdapter
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.FragmentServiceTemplateBinding
import com.daqsoft.thetravelcloudwithculture.ui.vm.ServiceTemplateVm
import java.lang.ref.SoftReference

/**
 * @Description 公共服务模板页面
 * @ClassName   ServiceTemplateFragment
 * @Author      luoyi
 * @Time        2020/10/16 14:47
 */
class ServiceTemplateFragment : BaseFragment<FragmentServiceTemplateBinding, ServiceTemplateVm>() {

    lateinit var delegateAdapter: DelegateAdapter
    var isShowTitle: Boolean = false

    companion object {

        const val IS_SHOW_TITLE: String = "is_show_title"
        fun newInstance(isShowTitleBar: Boolean): ServiceTemplateFragment {
            var frag = ServiceTemplateFragment()
            var bundle = Bundle()
            bundle.putBoolean(IS_SHOW_TITLE, isShowTitleBar)
            frag.arguments = bundle
            return frag
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_service_template
    }

    override fun injectVm(): Class<ServiceTemplateVm> {
        return ServiceTemplateVm::class.java
    }

    override fun initView() {
        arguments?.let {
            isShowTitle = it.getBoolean(IS_SHOW_TITLE, false)
            mBinding.tvTitleService.visibility = if (isShowTitle) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        initViewModel()
        initUi()
    }

    private fun initUi() {
        mBinding.rvSeviceTemplate?.run {
            val viewPool = RecyclerView.RecycledViewPool()
            setRecycledViewPool(viewPool)
            val virtualLayoutManager = VirtualLayoutManager(context)
            layoutManager = virtualLayoutManager
            delegateAdapter = DelegateAdapter(virtualLayoutManager, true)
            adapter = delegateAdapter
        }
        mBinding.rflService.setOnRefreshListener {
            mModel.getServiceTemplate()
        }
    }

    private fun initViewModel() {
        mModel.serviceTemplateLd.observe(this, Observer {
            mBinding.rflService.finishRefresh()
            if (!it.isNullOrEmpty()) {
                delegateAdapter.clear()
                delegateAdapter.notifyDataSetChanged()
                for (index in it.indices) {
                    var template = it[index]
                    template?.let { obj ->
                        if (!obj.moduleType.isNullOrEmpty()) {
                            buildIndexTemplateView(obj, index)
                        }
                    }
                }
            } else {
                // 没有配置页面模板信息
//                ToastUtils.showMessage("")
            }
        })
    }

    private fun buildIndexTemplateView(obj: StyleTemplate, index: Int) {
        when (obj.moduleType) {
            // 运营专区
            TemplateApi.MoudleType.operation -> {
                if (obj.layoutDetail != null) {
                    var adapter: OperationTemplateAdapter =
                        OperationTemplateAdapter(SingleLayoutHelper())
                            .apply {
                                commonTemlate = obj.layoutDetail!!
                            }
                    delegateAdapter.addAdapter(adapter)
                }
            }
            // 菜单
            TemplateApi.MoudleType.menu, TemplateApi.MoudleType.bottomMenu -> {

            }
            // 顶部导航菜单
            TemplateApi.MoudleType.topMenu -> {
                if (!obj.layoutDetails.isNullOrEmpty()) {
                    var adapter: TopMenuAdapter = TopMenuAdapter(SingleLayoutHelper()).apply {
                        fragmentManger = SoftReference(childFragmentManager)
                    }
                    delegateAdapter.addAdapter(adapter)
                    adapter.addAll(obj.layoutDetails)
                }
            }
            // 轮播图
            TemplateApi.MoudleType.carousel -> {
                if (!obj.layoutDetails.isNullOrEmpty()) {
                    var adapter: AdsBannerAdapter = AdsBannerAdapter(LinearLayoutHelper()).apply {
                        adses.clear()
                        adses.addAll(obj.layoutDetails)
                    }
                    delegateAdapter.addAdapter(adapter)
                }
            }
            // 功能组件
            TemplateApi.MoudleType.component -> {
                if (obj.layoutDetail != null) {
                    var adapter: ComponentTemplateAdapter =
                        ComponentTemplateAdapter(SingleLayoutHelper()).apply {
                            commonTemlate = obj.layoutDetail
                            fragmentManger = SoftReference(childFragmentManager)
                        }
                    delegateAdapter.addAdapter(adapter)
                }
            }
            // 栏目标题
            TemplateApi.MoudleType.channelTitle -> {
                obj.layoutDetail?.run {
                    when (menuCode) {
                        // 活动
                        TemplateApi.BusinessType.HOT_ACTIVITY -> {
                        }
                        // 导游导览
                        TemplateApi.BusinessType.TOUR_GUIDE -> {
                        }
                        // 发现身边
                        TemplateApi.BusinessType.FOUND_AROUND -> {
                        }
                        else -> {

                        }
                    }
                }
            }
        }
    }

    override fun initData() {
        mModel.getServiceTemplate()
    }
}