package com.daqsoft.travelCultureModule.venuecollect
import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityExhibitionLsBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.ExhibitionTagBean
import com.daqsoft.travelCultureModule.venuecollect.viewmodel.ExhibitionLsViewModel
import com.daqsoft.travelCultureModule.venuecollect.adapter.ExhibitionListAdapter
import com.daqsoft.travelCultureModule.venuecollect.adapter.MsgPageAdapter
import com.daqsoft.travelCultureModule.venuecollect.adapter.TopTagAdapter
import com.daqsoft.travelCultureModule.venuecollect.adapter.TopTagExbihitionAdapter
import com.daqsoft.travelCultureModule.venuecollect.fragment.ExhibitionActivityFragment
import com.daqsoft.travelCultureModule.venuecollect.fragment.ExhibitionOnShowFragment
import com.daqsoft.travelCultureModule.venuecollect.fragment.ExhibitionOnlineFragment

/**
 * 精品文物列表界面
 */
@Route(path = MainARouterPath.COLLECT_SHOW_LIST)
class ExhibitionLsActivity : TitleBarActivity<ActivityExhibitionLsBinding, ExhibitionLsViewModel>() {


    val fragmentList : MutableList<Fragment> = mutableListOf()

   lateinit var  msgPageAdpater: MsgPageAdapter
    // 头部适配器
    private val topAdapter: TopTagExbihitionAdapter by lazy {
        TopTagExbihitionAdapter(this).apply {
            emptyViewShow=false
           onItemCLickListener =  object : TopTagExbihitionAdapter.OnItemCLickListener {
               override fun onItemClick(item: ExhibitionTagBean, position: Int) {
                   mBinding.vpMsgList.currentItem = position
               }
           }
       }
   }


    override fun getLayout(): Int {
        return R.layout.activity_exhibition_ls
    }

    override fun setTitle(): String {
        return "陈列展览"
    }

    override fun injectVm(): Class<ExhibitionLsViewModel> {
        return ExhibitionLsViewModel::class.java
    }

    override fun initView() {
        initViewModel()
        mBinding.rvLineType.adapter = topAdapter
        fragmentList.add(ExhibitionActivityFragment())
        fragmentList.add(ExhibitionOnShowFragment())
        fragmentList.add(ExhibitionOnlineFragment())
        msgPageAdpater= MsgPageAdapter(supportFragmentManager,fragmentList)
        mBinding.vpMsgList.adapter = msgPageAdpater
        mBinding.vpMsgList.currentItem = 0
        mBinding.vpMsgList.isCanScrollble=true
        mBinding.vpMsgList.offscreenPageLimit=3
        mBinding.vpMsgList.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            @SuppressLint("MissingSuperCall")
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                mModel.topdatas.forEachIndexed { index, bean ->
                    bean.select=index==position
                }
                topAdapter.notifyDataSetChanged()
            }

        })
    }


    override fun initData() {
        topAdapter.setNewData(mModel.topdatas)
    }

    private fun initViewModel() {
    }

}