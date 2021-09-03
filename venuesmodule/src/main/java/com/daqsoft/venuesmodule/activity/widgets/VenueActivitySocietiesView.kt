package com.daqsoft.venuesmodule.activity.widgets

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.bean.ActivityRoomBean
import com.daqsoft.provider.bean.SocietiesBean
import com.daqsoft.provider.view.GridDecoration
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.adapter.SocietiesAdapter
import com.daqsoft.venuesmodule.adapter.VenuesActivityRoomAdapter
import com.daqsoft.venuesmodule.databinding.IncludeVenueDetailsActivityRoomBinding
import com.daqsoft.venuesmodule.databinding.IncludeVenueDetailsActivitySocietiesBinding

/**
 * @Description  社团
 * @ClassName   VenueActivitesView
 * @Author      luoyi
 * @Time        2020/3/27 14:54
 */
class VenueActivitySocietiesView : FrameLayout {

    var mContext: Context? = null

    var binding: IncludeVenueDetailsActivitySocietiesBinding? = null
    /**
     * 活动数据集
     */
    var datas: MutableList<SocietiesBean> = mutableListOf()

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        initView()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.include_venue_details_activity_societies,
            this,
            false
        )
        addView(binding!!.root)
    }

    /**
     * 设置数据
     */
    fun setData(data: MutableList<SocietiesBean>) {
        datas.clear()
        datas.addAll(data)
        updateUi()
    }


    fun updateUi() {


        val societiesAdapter = SocietiesAdapter()
        societiesAdapter.add(datas)
        binding?.run {
            with(rvSocieties){
                layoutManager = GridLayoutManager(context,2)
                addItemDecoration(GridDecoration(2,12, includeEdge = true, isRemoveBoth = true))
                adapter = societiesAdapter
            }

            tvProviderMore.visibility = if (datas.size > 4) View.VISIBLE else View.GONE

            tvProviderMore.onNoDoubleClick {
                ARouter.getInstance().build(MainARouterPath.MAIN_CLUB).navigation()
            }
        }
    }


}