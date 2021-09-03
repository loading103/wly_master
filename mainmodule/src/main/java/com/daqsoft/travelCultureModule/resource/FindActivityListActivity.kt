package com.daqsoft.travelCultureModule.resource

import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityFindActicityBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.businessview.adapter.ProviderActivityAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 * @Description 找活动
 */
@Route(path = MainARouterPath.MAIN_FIND_ACTIVITY)
class FindActivityListActivity : TitleBarActivity<ActivityFindActicityBinding, BaseViewModel>() {

    @JvmField
    @Autowired
    var toJson : String? = null

    var datas : ArrayList<ActivityBean>? = null
    var mAdapter: ProviderActivityAdapter? = null
    override fun getLayout(): Int {
        return R.layout.activity_find_acticity
    }

    override fun setTitle(): String {
        return "全部活动"
    }

    override fun injectVm(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun initView() {
        mAdapter = ProviderActivityAdapter(this@FindActivityListActivity)
        mBinding.recyScenicLives.apply {
            layoutManager=LinearLayoutManager(this@FindActivityListActivity,  LinearLayoutManager.VERTICAL, false)
            adapter=mAdapter
        }

    }

    override fun initData() {
        datas = Gson().fromJson(toJson, object : TypeToken<ArrayList<ActivityBean>>() {}.type)
        mAdapter?.add(datas!!)
    }
}