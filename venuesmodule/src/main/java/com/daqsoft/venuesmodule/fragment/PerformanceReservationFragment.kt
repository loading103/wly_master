package com.daqsoft.venuesmodule.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.extend.dp
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.provider.bean.OderAddressInfoBean
import com.daqsoft.provider.businessview.fragment.AppointmentFragment
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.adapter.PerformanceReservationAdapter
import com.daqsoft.venuesmodule.databinding.FragmentPerformanceReservationBinding
import com.daqsoft.venuesmodule.viewmodel.PerformanceReservationViewModel

/**
 * @package name：com.daqsoft.venuesmodule.activity.widgets
 * @date 2020/9/15 16:26
 * @author zp
 * @describe 演出预定
 */
class PerformanceReservationFragment : BaseFragment<FragmentPerformanceReservationBinding, PerformanceReservationViewModel>() {

    companion object {
        @JvmStatic
        fun newInstance(data:ArrayList<OderAddressInfoBean>) = PerformanceReservationFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList("data",data)
                }
            }
    }

    var data:ArrayList<OderAddressInfoBean>? = null
    /**
     * 获取参数
     */
    private fun initParam() {
        arguments?.let {
            data = it.getParcelableArrayList("data")
        }
    }

    private val performanceReservationAdapter by lazy { PerformanceReservationAdapter() }

    override fun getLayout(): Int {
        return R.layout.fragment_performance_reservation
    }

    override fun injectVm(): Class<PerformanceReservationViewModel> {
        return PerformanceReservationViewModel::class.java
    }

    override fun initView() {
        initParam()
        initRecycleView()

        data?.let {
            mBinding.tvProviderMore.visibility = if (it.size > 3) View.VISIBLE else View.GONE
        }
    }

    /**
     * 初始化recycleView
     */
    private fun initRecycleView(){
        with(mBinding.rvBooking){
            layoutManager = LinearLayoutManager(context)
            performanceReservationAdapter.add(data as MutableList<OderAddressInfoBean>)
            adapter = performanceReservationAdapter
            addItemDecoration(object : RecyclerView.ItemDecoration(){
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val position = parent.getChildAdapterPosition(view)
                    outRect.top = if (position == 0) 28.dp else 31.dp
                }
            })
        }
    }

    override fun initData() {
    }

}