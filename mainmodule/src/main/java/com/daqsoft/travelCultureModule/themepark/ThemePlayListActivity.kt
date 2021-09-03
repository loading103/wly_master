package com.daqsoft.travelCultureModule.themepark

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.dp
import com.daqsoft.baselib.utils.ScaleTransitionPagerTitleView
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityThemePlayListBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.PlayChooseType
import com.daqsoft.travelCultureModule.country.bean.ResourceTypeLabel
import com.daqsoft.travelCultureModule.country.view.PlayChoosePopupWindow
import com.daqsoft.travelCultureModule.country.view.TypeSelectPopupWindow
import com.daqsoft.travelCultureModule.themepark.adapter.ThemePlayListAdapter
import com.daqsoft.travelCultureModule.themepark.viewmodel.ThemePlayListViewModel
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView
import timber.log.Timber

/**
 * @Description 游玩项目
 */
@Route(path = MainARouterPath.THEME_PLAY_LIST)
class ThemePlayListActivity : TitleBarActivity<ActivityThemePlayListBinding, ThemePlayListViewModel>() {

    @JvmField
    @Autowired
    var Id: String = ""

    private var typeListPopWindow: PlayChoosePopupWindow? = null

    private val mAdapter: ThemePlayListAdapter by lazy {
        ThemePlayListAdapter().apply { emptyViewShow=false }
    }

    override fun getLayout(): Int {
        return R.layout.activity_theme_play_list
    }

    override fun setTitle(): String {
        return "游玩项目"
    }
    override fun injectVm(): Class<ThemePlayListViewModel> {
        return ThemePlayListViewModel::class.java
    }

    override fun initView() {
        initViewModel()
        initListener()

        mBinding.magicIndicator.navigator=setNaviga(this@ThemePlayListActivity, mModel.topdatas)

        mModel.pageManager.initPageIndex()

        mBinding.recyclerView.apply {
            adapter = mAdapter
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    val position = parent.getChildAdapterPosition(view)
                    val count = state.itemCount - 1
                    outRect.bottom = 20.dp
                }
            })
        }
        mBinding.smartRefreshLayout.apply {
            setEnableLoadMore(true)
            setRefreshFooter(ClassicsFooter(context).setDrawableSize(20f))
            setOnRefreshListener {
                mModel.pageManager.initPageIndex()
                mModel.getListData()
            }
            setOnLoadMoreListener{
                mModel.pageManager.nexPageIndex
                mModel.getListData()
            }
        }

    }

    private fun initListener() {
        mBinding.tvFilter.setOnClickListener {
            showChooseTopPopup()
        }
    }

    override fun initData() {
        showLoadingDialog()
        mModel.getListData()
    }



    private fun initViewModel() {
        mModel.datas?.observe(this, Observer {
            dissMissLoadingDialog()
            mAdapter.emptyViewShow=true
            mBinding.smartRefreshLayout.finishRefresh()
            mBinding.smartRefreshLayout.finishLoadMore()
            if(it==null){
                return@Observer
            }
            if (mModel.pageManager.isFirstIndex) {
                mAdapter.setNewData(it)
                if( mAdapter.getData().size==mModel.totleNumber){
                    mBinding.smartRefreshLayout.setNoMoreData(true)
                }else{
                    mBinding.smartRefreshLayout.setNoMoreData(false)
                }
            }else{
                if( mAdapter.getData().size==mModel.totleNumber){
                    mBinding.smartRefreshLayout.setNoMoreData(true)
                }else{
                    mBinding.smartRefreshLayout.setNoMoreData(false)
                }
                mAdapter.add(it)
            }
            mAdapter.notifyDataSetChanged()
        })
    }



    fun setNaviga(context: Context, datas: List<String>) : CommonNavigator {
        val commonNavigator = CommonNavigator(context)
        commonNavigator.isAdjustMode=true
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return datas?.size ?: 0
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val colorTransitionPagerTitleView: SimplePagerTitleView = ScaleTransitionPagerTitleView(context)
                colorTransitionPagerTitleView.textSize = 16f
                colorTransitionPagerTitleView.normalColor =  context.resources.getColor(R.color.color_666)
                colorTransitionPagerTitleView.selectedColor = context.resources.getColor(R.color.color_333333)
                colorTransitionPagerTitleView.text = datas[index]
                colorTransitionPagerTitleView.setOnClickListener{
                    mBinding.magicIndicator.onPageSelected(index);
                    mBinding.magicIndicator.onPageScrolled(index, 0.0F, 0);
                    ToastUtils.showMessage(datas[index])
                }
                return colorTransitionPagerTitleView
            }
            override fun getIndicator(context: Context): IPagerIndicator {
                return LinePagerIndicator(context).apply {
                    mode = LinePagerIndicator.MODE_EXACTLY
                    lineHeight = UIUtil.dip2px(context, 3.0).toFloat()
                    lineWidth = UIUtil.dip2px(context, 20.0).toFloat()
                    roundRadius = UIUtil.dip2px(context, 6.0).toFloat()
                    startInterpolator = AccelerateInterpolator()
                    endInterpolator = DecelerateInterpolator(2.0f)
                    setColors(context.resources.getColor(R.color.colorPrimary))
                }
            }
        }
        return commonNavigator;
    }


    /**
     * 筛选缓存的选项
     */
    fun showChooseTopPopup(){
        if (typeListPopWindow == null) {
            typeListPopWindow = PlayChoosePopupWindow.getInstance(this, object : PlayChoosePopupWindow.WindowDataBack {
                override fun select(bean1: ResourceTypeLabel?, bean2: ResourceTypeLabel?, bean3: ResourceTypeLabel?, bean4: ResourceTypeLabel?) {
                    Timber.e(bean1?.labelName+"--"+bean2?.labelName+"--"+bean3?.labelName+"--"+bean4?.labelName)
                }

                override fun reset() {
                    Timber.e("----------reset")
                }
            })
            typeListPopWindow!!.setdata (mModel.types1,null,mModel.types3,null)
        }
        typeListPopWindow?.show(mtitleBar)
    }
}