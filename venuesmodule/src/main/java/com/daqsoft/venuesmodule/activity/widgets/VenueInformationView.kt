package com.daqsoft.venuesmodule.activity.widgets

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.ContentBean
import com.daqsoft.provider.databinding.LayoutProviderContentBinding
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.adapter.VenueInformationAdapter
import com.daqsoft.venuesmodule.databinding.LayoutVenueInformationBinding

/**
 * @package name：com.daqsoft.venuesmodule.activity.widgets
 * @date 2020/9/15 16:26
 * @author zp
 * @describe 场馆资讯
 */
class VenueInformationView : FrameLayout {

    var mContext: Context? = null

    var binding: LayoutVenueInformationBinding? = null

    var title: String = "场馆资讯"

    var mDatas: MutableList<ContentBean> = mutableListOf()

    val  adapter by lazy { VenueInformationAdapter() }

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        initView()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.layout_venue_information,
            this,
            false
        )
        binding?.run {
            title = title
            rvProvideContents.adapter = adapter
            rvProvideContents.layoutManager = LinearLayoutManager(mContext!!, LinearLayoutManager.VERTICAL, false)
            rvProvideContents.addItemDecoration(object : RecyclerView.ItemDecoration(){
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val position = parent.getChildAdapterPosition(view)
                    outRect.top = Utils.dip2px(context,if (position ==0) 27f else 32f).toInt()
                }
            })
        }
        addView(binding!!.root)
    }

    fun setData(resId: String, resTpye: String, datas: MutableList<ContentBean>) {
        if (!datas.isNullOrEmpty()) {
            adapter.emptyViewShow = false
            adapter.clear()
            adapter.add(datas)
            if (datas.size >= 2) {
                binding?.tvProviderMore?.visibility = View.VISIBLE
            } else {
                binding?.tvProviderMore?.visibility = View.GONE
            }
        }

        binding?.tvProviderMore?.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_CONTENT)
                .withString("channelCode", "")
                .withString("linksResourceType", resTpye)
                .withString("linksResourceId", resId)
                .navigation()
        }

    }
}