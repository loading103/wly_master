package com.daqsoft.travelCultureModule.resource

import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityScenicLivesBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.travelCultureModule.resource.adapter.ScenicSpotLiveAdapter
import com.daqsoft.travelCultureModule.resource.viewmodel.ScenicLivesViewModel

/**
 * @Description 景区直播列表
 * @ClassName   ScenicLivesActivity
 * @Author      luoyi
 * @Time        2020/4/26 9:35
 */
@Route(path = MainARouterPath.MAIN_SCENIC_SPOTS_LIVES)
class ScenicLivesActivity : TitleBarActivity<ActivityScenicLivesBinding, ScenicLivesViewModel>() {

    @JvmField
    @Autowired
    var spotId: String = ""

    var adapter: ScenicSpotLiveAdapter? = null
    override fun getLayout(): Int {
        return R.layout.activity_scenic_lives
    }

    override fun setTitle(): String {
        return "实景直播列表"
    }

    override fun injectVm(): Class<ScenicLivesViewModel> {
        return ScenicLivesViewModel::class.java
    }

    override fun initView() {
        adapter = ScenicSpotLiveAdapter()
        mBinding?.recyScenicLives.adapter = adapter
        mBinding?.recyScenicLives.layoutManager = GridLayoutManager(this@ScenicLivesActivity, 2, GridLayoutManager.VERTICAL, false)
        initViewModel()
    }

    private fun initViewModel() {
        mModel.scenicId=spotId
        mModel?.spotLivesLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding?.recyScenicLives.visibility = View.VISIBLE
            if (!it.isNullOrEmpty()) {
                adapter?.clear()
                adapter?.add(it)
            }
        })
        mModel?.mError.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding?.recyScenicLives.visibility = View.VISIBLE
        })
    }

    override fun initData() {
        showLoadingDialog()
        mModel?.getSpotLives()
    }
}