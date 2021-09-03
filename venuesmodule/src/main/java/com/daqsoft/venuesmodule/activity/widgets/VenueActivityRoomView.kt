package com.daqsoft.venuesmodule.activity.widgets

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.bean.ActivityRoomBean
import com.daqsoft.provider.view.GridDecoration
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.adapter.ThemePavilionAdapter
import com.daqsoft.venuesmodule.adapter.VenuesActivityRoomAdapter
import com.daqsoft.venuesmodule.databinding.IncludeVenueDetailsActivityRoomBinding

/**
 * @Description  活动室
 * @ClassName   VenueActivitesView
 * @Author      luoyi
 * @Time        2020/3/27 14:54
 */
class VenueActivityRoomView : FrameLayout {

    var mContext: Context? = null

    var binding: IncludeVenueDetailsActivityRoomBinding? = null
    /**
     * 活动数据集
     */
    var datas: MutableList<ActivityRoomBean> = mutableListOf()

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        initView()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.include_venue_details_activity_room,
            this,
            false
        )
        addView(binding!!.root)
    }

    /**
     * 设置数据
     */
    fun setData(data: MutableList<ActivityRoomBean>) {
        datas.clear()
        datas.addAll(data)
        updateUi()
    }


    fun updateUi() {
//        var adapater = VenuesActivityRoomAdapter(mContext!!)
//        binding?.recyclerVenuesDetailsRoom?.layoutManager = LinearLayoutManager(
//            mContext!!, LinearLayoutManager.VERTICAL,
//            false
//        )
//        binding?.recyclerVenuesDetailsRoom?.adapter = adapater
//        adapater?.isNeedShowMore = datas.size > 3
//        if (adapater!!.isNeedShowMore) {
//            binding?.vActivityRoomShowMore?.visibility = View.VISIBLE
//        } else {
//            binding?.vActivityRoomShowMore?.visibility = View.GONE
//        }
//        adapater.clear()
//        adapater.add(datas)
//        binding?.vActivityRoomShowMore?.onNoDoubleClick {
//            if (adapater!!.isNeedShowMore) {
//                adapater!!.isNeedShowMore = false
//                adapater?.notifyDataSetChanged()
//                val drawable2 = mContext!!.getDrawable(com.daqsoft.provider.R.mipmap.provider_arrow_up)
//                drawable2.setBounds(0, 0, drawable2.minimumWidth, drawable2.minimumHeight)
//                binding?.txtActivityRoomTip?.setCompoundDrawables(null, null, drawable2, null)
//                binding?.txtActivityRoomTip?.text = "收起全部活动室"
//            } else {
//                adapater!!.isNeedShowMore = true
//                adapater?.notifyDataSetChanged()
//                val drawable2 = mContext!!.getDrawable(com.daqsoft.provider.R.mipmap.provider_arrow_down)
//                drawable2.setBounds(0, 0, drawable2.minimumWidth, drawable2.minimumHeight)
//                binding?.txtActivityRoomTip?.setCompoundDrawables(null, null, drawable2, null)
//                binding?.txtActivityRoomTip?.text = "查看全部活动室"
//            }
//        }


        val themePavilionAdapter = ThemePavilionAdapter()
        themePavilionAdapter.isNeedShowMore = datas.size > 4
        themePavilionAdapter.add(datas)
        binding?.run {

            with(recyclerVenuesDetailsRoom){
                layoutManager = GridLayoutManager(context,2)
                addItemDecoration(GridDecoration(2,12, includeEdge = true, isRemoveBoth = true))
                adapter = themePavilionAdapter
            }

            with(vActivityRoomShowMore){
                visibility = if (themePavilionAdapter.isNeedShowMore) View.VISIBLE else View.GONE

                onNoDoubleClick {
                    if (themePavilionAdapter.isNeedShowMore) {
                        themePavilionAdapter.isNeedShowMore = false
                        themePavilionAdapter.notifyDataSetChanged()
                        val drawable2 =
                            mContext!!.getDrawable(com.daqsoft.provider.R.mipmap.provider_arrow_up)
                        drawable2.setBounds(0, 0, drawable2.minimumWidth, drawable2.minimumHeight)
                        binding?.txtActivityRoomTip?.setCompoundDrawables(
                            null,
                            null,
                            drawable2,
                            null
                        )
                        binding?.txtActivityRoomTip?.text = "收起全部活动室"
                    } else {
                        themePavilionAdapter.isNeedShowMore = true
                        themePavilionAdapter.notifyDataSetChanged()
                        val drawable2 =
                            mContext!!.getDrawable(com.daqsoft.provider.R.mipmap.provider_arrow_down)
                        drawable2.setBounds(0, 0, drawable2.minimumWidth, drawable2.minimumHeight)
                        binding?.txtActivityRoomTip?.setCompoundDrawables(
                            null,
                            null,
                            drawable2,
                            null
                        )
                        binding?.txtActivityRoomTip?.text = "查看全部活动室"
                    }
                }
            }
        }

    }


}