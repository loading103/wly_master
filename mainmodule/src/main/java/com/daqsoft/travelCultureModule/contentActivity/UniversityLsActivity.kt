package com.daqsoft.travelCultureModule.contentActivity

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityUniversityLsBinding
import com.daqsoft.mainmodule.databinding.ItemWatchShowBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.travelCultureModule.contentActivity.bean.ShowBean
import com.daqsoft.travelCultureModule.contentActivity.vm.UniversityLsViewModel

/**
 * @Description
 * @ClassName   UniversityLsActivity
 * @Author      luoyi
 * @Time        2020/6/1 11:35
 */
@Route(path = MainARouterPath.UNIVERSITY_LS)
class UniversityLsActivity : TitleBarActivity<ActivityUniversityLsBinding, UniversityLsViewModel>() {
    override fun getLayout(): Int {
        return R.layout.activity_university_ls
    }

    override fun setTitle(): String {
        return "大讲堂"
    }

    override fun injectVm(): Class<UniversityLsViewModel> {
        return UniversityLsViewModel::class.java
    }

    override fun initView() {
        var linerLayoutManager = LinearLayoutManager(this)
        mBinding.recyUniversityLs.layoutManager = linerLayoutManager
        mBinding.recyUniversityLs.adapter = showAdapter
        showAdapter.emptyViewShow = false
        mModel.zixunList.observe(this, Observer {
            dissMissLoadingDialog()
            if (it != null && !it.subset.isNullOrEmpty()) {
                showAdapter.add(it.subset as MutableList<ShowBean>)
            } else {
                showAdapter.emptyViewShow = true
            }
        })
    }

    override fun initData() {
        showLoadingDialog()
        mModel.getZixunList()
//        mModel.getWatchShows()
    }

    var showAdapter = object : RecyclerViewAdapter<ItemWatchShowBinding, ShowBean>(R.layout.item_watch_show) {
        override fun setVariable(mBinding: ItemWatchShowBinding, position: Int, item: ShowBean) {
            mBinding.tvTitle.text = item.name
            Glide.with(this@UniversityLsActivity)
                .load(item.backgroundImg)
                .into(mBinding.avCover)
            mBinding.root.onNoDoubleClick {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_CONTENT)
                    .withString("channelCode", item.channelCode)
                    .navigation()
            }
        }

    }
}