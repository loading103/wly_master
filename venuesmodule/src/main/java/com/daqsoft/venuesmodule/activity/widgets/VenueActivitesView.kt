package com.daqsoft.venuesmodule.activity.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.adapter.SCPopularActivitiesAdapter
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.IncludeVenueDetailsActivityNewBinding

/**
 * @Description 场馆活动
 * @ClassName   VenueActivitesView
 * @Author      luoyi
 * @Time        2020/3/27 11:22
 */
class VenueActivitesView : FrameLayout {

    var mContext: Context? = null

    var binding: IncludeVenueDetailsActivityNewBinding? = null
    /**
     * 活动数据集
     */
    var datas: MutableList<ActivityBean> = mutableListOf()


    private val adapter by lazy { SCPopularActivitiesAdapter() }

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        initView()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.include_venue_details_activity_new,
            this,
            false
        )

        binding?.run {
            xBranchActivity.adapter = adapter
            xBranchActivity.pageMargin = resources.getDimensionPixelSize(R.dimen.dp_8)
        }


        addView(binding!!.root)
    }

    /**
     * 设置数据
     */
    fun setData(data: MutableList<ActivityBean>, id: String, type: String) {
        datas.clear()
        datas.addAll(data)
        updateUi(id, type)
    }


    fun updateUi(id: String, type: String) {
//        var adapater = VenueActivityAdapter(mContext!!)
//        binding?.recyclerVenuesDetailsActivity?.layoutManager = LinearLayoutManager(
//            mContext!!, LinearLayoutManager.VERTICAL,
//            false
//        )
//        binding?.recyclerVenuesDetailsActivity?.adapter = adapater
//        binding?.tvVenuesDetailsActivityCount?.text = "共${datas.size}个活动"
//        adapater.clear()
//        if (datas.size > 2) {
//            adapater.add(datas.subList(0, 2))
//        } else {
//            adapater.add(datas)
//        }

        // 2020/9/15 修改

        adapter.menus.clear()
        adapter.menus.addAll(datas)
        adapter.notifyDataSetChanged()

        binding?.tvVenuesDetailsActivityCount?.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.Provider.PROVIDER_ACTIVITIES)
                .withString("id", id)
                .withString("type", type)
                .navigation()
        }
    }


}