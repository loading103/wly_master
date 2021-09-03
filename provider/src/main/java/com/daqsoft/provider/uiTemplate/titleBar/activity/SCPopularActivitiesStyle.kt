package com.daqsoft.provider.uiTemplate.titleBar.activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.base.ActivityMethod
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.databinding.ScPopularActivitiesStyleBinding
import com.daqsoft.provider.uiTemplate.TemplateType
import com.daqsoft.provider.uiTemplate.titleBar.view.TitleBarFactoryView

/**
 * @package name：com.daqsoft.provider.activitymodule
 * @date 2020/10/9 9:56
 * @author zp
 * @describe 四川热门活动
 */

class SCPopularActivitiesStyle(
    private val helper: LayoutHelper,
    private val commonTemlate: CommonTemlate
) : DelegateAdapter.Adapter<SCPopularActivitiesStyle.RecyclerViewItemHolder>() {

    /**
     * 适配器
     */
    val viewPager2Adapter by lazy {
        SCPopularActivitiesStyleAdapterFactory.getAdapter(commonTemlate.moduleCode ?: "1").create()
    }

    /**
     * 标题栏
     */
    val titleBarView by lazy {
        TitleBarFactoryView(BaseApplication.context).apply {
            setData(commonTemlate)
            onTitileBarClickListener =
                object : TitleBarFactoryView.OnTitleBarClickListener {
                    override fun toMoreInfo(commonTemlate: CommonTemlate) {
                        ARouter.getInstance().build(MainARouterPath.MAIN_ACTIVITY_LS).navigation()
                    }
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewItemHolder {
        val mBinding = DataBindingUtil.inflate<ScPopularActivitiesStyleBinding>(
            LayoutInflater.from(parent.context),
            R.layout.sc_popular_activities_style,
            parent,
            false
        )
        return RecyclerViewItemHolder(mBinding)
    }

    override fun onBindViewHolder(holder: RecyclerViewItemHolder, position: Int) {

        if (viewPager2Adapter.menus.isEmpty()) {
            return
        }

        holder.binding.run {

            // 标题栏
            this.titleBar.removeAllViews()
            this.titleBar.addView(titleBarView)
            holder.binding.llHomeActivityStyleFive.llHomePopularActFive.visibility = View.GONE
            with(viewPager2) {
                offscreenPageLimit = 1
                when (commonTemlate.moduleCode) {
                    "5", "8" -> {
                        viewPager2.visibility  = View.VISIBLE
                        val recyclerView = getChildAt(0) as RecyclerView
                        recyclerView.apply {
                            val padding = resources.getDimensionPixelOffset(R.dimen.dp_20)
                            setPadding(0, 0, padding, 0)
                            clipToPadding = false
                        }
                        val compositePageTransformer = CompositePageTransformer()
                        compositePageTransformer.addTransformer(
                            MarginPageTransformer(
                                resources.getDimension(
                                    R.dimen.dp_10
                                ).toInt()
                            )
                        )
                        setPageTransformer(compositePageTransformer)
                    }
                    "7" -> {
                        viewPager2.visibility = View.VISIBLE
                        setPageTransformer(object : ViewPager2.PageTransformer {
                            override fun transformPage(view: View, position: Float) {
                                val pagerWidth: Int = viewPager2.width

                                if (position >= offscreenPageLimit || position <= -1) {
                                    view.visibility = View.GONE
                                } else {
                                    view.visibility = View.VISIBLE
                                }

                                if (position >= 0) {
                                    view.translationX = pagerWidth * -position
                                }
                                if (position > -1 && position < 0) {
                                    val rotation = position * 30
                                    view.rotation = rotation
                                    view.alpha = position * position * position + 1
                                } else if (position > offscreenPageLimit - 1) {
                                    view.alpha = (1 - position + Math.floor(position.toDouble())).toFloat()
                                } else {
                                    view.rotation = 0f
                                    view.alpha = 1f
                                }

                                ViewCompat.setElevation(view, (offscreenPageLimit - position) * 5)

                            }
                        })
                    }
                    "6" -> {
                        viewPager2.visibility = View.VISIBLE
                        val recyclerView = getChildAt(0) as RecyclerView
                        recyclerView.apply {
                            val padding = resources.getDimensionPixelOffset(R.dimen.dp_42)
                            setPadding(0, 0, padding, 0)
                            clipToPadding = false
                        }
                        val compositePageTransformer = CompositePageTransformer()
                        compositePageTransformer.addTransformer(
                            MarginPageTransformer(
                                resources.getDimension(
                                    R.dimen.dp_12
                                ).toInt()
                            )
                        )
                        setPageTransformer(compositePageTransformer)
                    }
                    "1","2","4","3" -> {
                        viewPager2.visibility  = View.GONE
                        if (!viewPager2Adapter.menus.isNullOrEmpty()) {
                            holder.binding.llHomeActivityStyleFive.llHomePopularActFive.visibility = View.VISIBLE
                            var item = viewPager2Adapter.menus[0]
                            holder.binding.llHomeActivityStyleFive.data = item
                            var activityAdapter = ActivitiesStyleFiveAdapter().apply {
                                emptyViewShow = false
                            }
                            // 设置 活动列表
                            holder.binding.llHomeActivityStyleFive.recyHomePopularActFives.run {
                                adapter = activityAdapter
                                activityAdapter.add(viewPager2Adapter.menus)
                                activityAdapter.onActivityItemClickListener = object : ActivitiesStyleFiveAdapter.OnActivityItemClickListener {
                                    override fun OnItemCLick(item: ActivityBean,position: Int) {
                                        holder.binding.llHomeActivityStyleFive.data = item
                                        holder.binding.llHomeActivityStyleFive.llHomePopularActCurrent.onNoDoubleClick {
                                            toActivityDetail(item)
                                        }
                                    }
                                }
                            }

                            // 设置价格显示

                        }
                    }
                }
                adapter = viewPager2Adapter
            }

        }


    }

    private fun toActivityDetail(item: ActivityBean) {
        if (!item.jumpUrl.isNullOrEmpty()) {
            ARouter.getInstance()
                .build(ARouterPath.Provider.WEB_ACTIVITY)
                .withString("mTitle", item.jumpName)
                .withString("html", item.jumpUrl)
                .navigation()
        } else {
            when (item.type) {
                // 志愿活动
                ActivityType.ACTIVITY_TYPE_VOLUNT -> {
                    ARouter.getInstance()
                        .build(MainARouterPath.MAIN_VOLUNTEER_ACTIVITY)
//                                    .build( MainARouterPath.MAIN_SEAT_SELECT_ACTIVITY)
                        .withString("id", item.id)
                        .withString("classifyId", item.classifyId)
                        .navigation()
                }
                // 预订活动
                ActivityType.ACTIVITY_TYPE_RESERVE -> {
                    // 预订
                    when (item.method) {
                        // 付费预订活动
                        ActivityMethod.ACTIVITY_MODE_INTEGRAL_PAY -> {

                            ARouter.getInstance()
                                .build(MainARouterPath.MAIN_ACTIVITY_PAY_ORDER)
                                .withString("jumpUrl", item.jumpUrl)
                                .navigation()
                        }
                        else -> {
                            ARouter.getInstance()
                                .build(MainARouterPath.MAIN_HOT_ACITITY)
                                .withString("id", item.id)
                                .withString("classifyId", item.classifyId)
                                .withString("region", item.region)
                                .navigation()
                        }
                    }
                }
                else -> {
                    ARouter.getInstance()
                        .build(MainARouterPath.MAIN_HOT_ACITITY)
                        .withString("id", item.id)
                        .withString("classifyId", item.classifyId)
                        .navigation()
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun getItemViewType(position: Int): Int {
        return TemplateType.POPULAR_ACTIVITIES + (commonTemlate.moduleCode ?: "1").toInt()
    }

    override fun onCreateLayoutHelper(): LayoutHelper {
        return helper
    }

    inner class RecyclerViewItemHolder(val binding: ScPopularActivitiesStyleBinding) :
        RecyclerView.ViewHolder(binding.root)
}

