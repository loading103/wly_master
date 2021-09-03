package com.daqsoft.legacyModule.smriti.ui

import android.annotation.SuppressLint
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.databinding.ActivityLegacySmritiBinding
import com.daqsoft.legacyModule.smriti.fragment.LegacyBehalfFragment
import com.daqsoft.legacyModule.smriti.adapter.MyViewPagerAdapter
import com.daqsoft.legacyModule.smriti.fragment.HeritageExperienceBaseFragment
import com.daqsoft.legacyModule.smriti.fragment.HeritagePeopleFragment
import com.daqsoft.legacyModule.smriti.fragment.HeritageTeachingBaseFragment
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.flyco.tablayout.listener.OnTabSelectListener
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

/**
 * desc :非遗传承列表
 * @author 江云仙
 * @date 2020/4/21 15:15
 */
@Route(path = ARouterPath.LegacyModule.LEGACY_Smrity_ACTIVITY)
class LegacySmritiActivity : TitleBarActivity<ActivityLegacySmritiBinding, BaseViewModel>() {


    @Autowired
    @JvmField
    var tabIndex: Int = 0

    private var mAdapter = MyViewPagerAdapter(supportFragmentManager)
    override fun getLayout(): Int {
        return R.layout.activity_legacy_smriti
    }

    override fun setTitle(): String {
        return "非遗项目"
    }

    override fun injectVm(): Class<BaseViewModel> = BaseViewModel::class.java

    override fun initView() {
        setViewPager()
        setClick()
    }

    /**
     *点击事件
     */
    @SuppressLint("CheckResult")
    private fun setClick() {
        RxView.clicks(mBinding.tvSearch)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                //跳转到搜索页面
                ARouter.getInstance().build(MainARouterPath.MAIN_ALL_SEARCH)
                    .navigation()
            }
    }

    /**
     *设置viewpager联动
     */
    private fun setViewPager() {
//        val mTitles = mutableListOf("非遗项目", "非遗传承人", "项目体验基地", "传习保护示范基地")
////        val mTitles = mutableListOf("非遗项目", "非遗传承人")
//        mAdapter.addFragment(LegacyBehalfFragment(), mTitles[0])//添加Fragment
//        mAdapter.addFragment(HeritagePeopleFragment.newInstance(), mTitles[1])//添加Fragment
//        mAdapter.addFragment(HeritageExperienceBaseFragment.newInstance(), mTitles[2])//添加Fragment
//        mAdapter.addFragment(HeritageTeachingBaseFragment.newInstance(), mTitles[3])//添加Fragment

        var mTitles= mutableListOf<String>()
        mTitles.add("非遗项目")
        mTitles.add("非遗传承人")
        mAdapter.addFragment(LegacyBehalfFragment(), mTitles[0])//添加Fragment
        mAdapter.addFragment(HeritagePeopleFragment.newInstance(), mTitles[1])//添加Fragment
        if(BaseApplication.appArea!="ws"){
            mTitles.add("项目体验基地")
            mTitles.add("传习保护示范基地")
            mAdapter.addFragment(HeritageExperienceBaseFragment.newInstance(), mTitles[2])//添加Fragment
            mAdapter.addFragment(HeritageTeachingBaseFragment.newInstance(), mTitles[3])//添加Fragment
        }


        mBinding.vp.adapter = mAdapter
        mBinding.slidingTabLayout.setViewPager(mBinding.vp)
        mBinding.vp.offscreenPageLimit = 4
        mBinding.slidingTabLayout.currentTab = tabIndex
        // 设置已选中的Tab点击的监听
        mBinding.slidingTabLayout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                title = mTitles[position]
            }

            override fun onTabReselect(position: Int) {
            }
        })

//        mBinding.vp.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
//            override fun onPageScrollStateChanged(state: Int) {
//            }
//
//            override fun onPageScrolled(
//                position: Int,
//                positionOffset: Float,
//                positionOffsetPixels: Int
//            ) {
//            }
//
//            override fun onPageSelected(position: Int) {
//            }
//
//        })

    }

    override fun initData() {

    }
}
