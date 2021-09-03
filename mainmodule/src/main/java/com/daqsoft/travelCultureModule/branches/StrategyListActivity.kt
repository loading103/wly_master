package com.daqsoft.travelCultureModule.branches

import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityStrategyListBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.travelCultureModule.branches.adapter.StrategyAdapter

/**
 * @Author：      邓益千
 * @Create by：   2020/5/8 17:12
 * @Description：攻略列表
 */
@Route(path = MainARouterPath.MAIN_STRATEGY_LIST)
class StrategyListActivity : TitleBarActivity<ActivityStrategyListBinding, BranchDetailActivityViewModel>() {

    @JvmField
    @Autowired
    var id: String = ""

    private var total = 0

    private var currPage = 1

    private val strategyAdapter: StrategyAdapter by lazy {
        StrategyAdapter()
    }

    override fun getLayout(): Int = R.layout.activity_strategy_list

    override fun setTitle(): String = "攻略"

    override fun injectVm(): Class<BranchDetailActivityViewModel> = BranchDetailActivityViewModel::class.java

    override fun initView() {
        strategyAdapter.emptyViewShow = false
        strategyAdapter.setOnLoadMoreListener {
            currPage++
            mModel.getZixunList(id, ResourceType.CONTENT_TYPE_BRAND, "", "10", currPage.toString(), "", "ywgl")
        }
        mBinding.recyclerView.adapter = strategyAdapter
    }

    override fun initData() {
        mModel.zixunList.observe(this, Observer {
            if (it != null) {
                strategyAdapter.add(it)
                if (mModel.total == strategyAdapter.itemCount - 1) {
                    strategyAdapter.loadEnd()
                } else {
                    strategyAdapter.loadComplete()
                }
            }
        })

        mModel.getZixunList(id, ResourceType.CONTENT_TYPE_BRAND, "", "10", currPage.toString(), "", "ywgl")
    }

}