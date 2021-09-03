package com.daqsoft.legacyModule.news

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.bean.NewsCategoryBean
import com.daqsoft.legacyModule.bean.Subset
import com.daqsoft.legacyModule.databinding.ActivityLegacyNewsBinding
import com.daqsoft.legacyModule.net.LegacyRepository
import com.daqsoft.legacyModule.smriti.adapter.MyViewPagerAdapter
import com.daqsoft.provider.ARouterPath
import java.util.HashMap

/**
 *@package:com.daqsoft.legacyModule.home
 *@date:2020/5/14 9:04
 *@author: caihj
 *@des:非遗资讯列表
 **/
@Route(path = ARouterPath.LegacyModule.LEGACY_NEWS_ACTIVITY)
class LegacyNewsActivity : TitleBarActivity<ActivityLegacyNewsBinding, LegacyNewsViewModel>() {


    private var mAdapter = MyViewPagerAdapter(supportFragmentManager)
    var mutableList = mutableListOf<Subset>()

    override fun getLayout(): Int = R.layout.activity_legacy_news

    override fun setTitle(): String = getString(R.string.legacy_module_news)

    override fun injectVm(): Class<LegacyNewsViewModel> = LegacyNewsViewModel::class.java

    override fun initView() {
        mModel.newsCategory.observe(this, Observer {
            if(it==null){
                mBinding.llRmpty.llRppt.visibility= View.VISIBLE
                return@Observer
            }
            mBinding.llRmpty.llRppt.visibility= View.GONE
            it.map { item ->
                mAdapter.addFragment(NewsListFragment.newInstance(item.channelCode),item.name)
            }
            mBinding.vp.adapter = mAdapter
            mBinding.slidingTabLayout.setViewPager(mBinding.vp)
            mAdapter.notifyDataSetChanged()

        })
        mAdapter.addFragment(NewsListFragment.newInstance("fyzx"),"全部")
        mBinding.vp.offscreenPageLimit = 2

    }

    override fun initData() {
        mModel.getNewsCategory()
    }
}

class LegacyNewsViewModel: BaseViewModel() {

    var newsCategory = MutableLiveData<MutableList<Subset>>()

    fun getNewsCategory() {
        LegacyRepository.service.getNewsCategory()
            .excute(object : BaseObserver<NewsCategoryBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<NewsCategoryBean>) {
                    newsCategory.postValue(response.data?.subset)
                }

                override fun onFailed(response: BaseResponse<NewsCategoryBean>) {
                    super.onFailed(response)
                    newsCategory.postValue(null)
                }
            })
    }

}