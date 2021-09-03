package com.daqsoft.servicemodule.ui

import android.annotation.SuppressLint
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.daqsoft.android.scenic.servicemodule.R
import com.daqsoft.android.scenic.servicemodule.databinding.ActivityServicePlaneBinding
import com.daqsoft.android.scenic.servicemodule.databinding.ItemPlaneDateBinding
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.UIHelperUtils
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.servicemodule.adapter.PlaneListAdapter
import com.daqsoft.servicemodule.bean.PlaneTimeBean
import com.daqsoft.servicemodule.model.PlaneListViewModel
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

/**
 * desc :机票列表
 * @author 江云仙
 * @date 2020/4/9 14:12
 */
@Route(path = ARouterPath.ServiceModule.SERVICE_PLANE_LIST_ACTIVITY)
class PlaneListActivity : TitleBarActivity<ActivityServicePlaneBinding, PlaneListViewModel>() {
    @JvmField
    @Autowired
    var startCityName = ""//开始时间
    @JvmField
    @Autowired
    var endCityName = ""//结束时间
    @JvmField
    @Autowired
    var time = ""//选择时间

    override fun getLayout(): Int {
        return R.layout.activity_service_plane
    }

    override fun setTitle(): String {
        return "$startCityName-$endCityName"
    }

    override fun injectVm(): Class<PlaneListViewModel> = PlaneListViewModel::class.java

    override fun initView() {
        mModel.endcity = endCityName
        mModel.city = startCityName
        mModel.date = time
        mBinding.recyTrainTime.visibility = View.GONE
        setAdapter()
        setClick()
        setHeaderAdapter()
    }

    private fun setHeaderAdapter() {
        //车站列表
        mBinding.recyHeaderTime.apply {
            layoutManager = GridLayoutManager(this@PlaneListActivity,5)
            adapter = dateAdapter
        }
        mModel.planeTimeBeans.observe(this, Observer {
            //设置当前默认位置
            for (i in 0 until  it.size){
                if (it[i].isChoose){
                    currentPlaneTime=it[i]
                    break
                }
            }
            dateAdapter.clear()
            dateAdapter.add(it)
            dateAdapter.notifyDataSetChanged()

        })

    }
    private var currentPlaneTime: PlaneTimeBean?=null
    private val dateAdapter = object : RecyclerViewAdapter<ItemPlaneDateBinding, PlaneTimeBean>(R.layout.item_plane_date) {
        @SuppressLint("CheckResult", "SetTextI18n")
        override fun setVariable(mBinding: ItemPlaneDateBinding, position: Int, item: PlaneTimeBean) {
            mBinding.tvPlaneDate.text = item.date
            mBinding.tvPlanePrice.text = "--"
            mBinding.tvPlaneWeek.text = item.week
            mBinding.rvPalneDate.isSelected = item.isChoose
            RxView.clicks(mBinding.root)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    mModel.date = item.chooseDate
                    this@PlaneListActivity.mBinding.recyTrainTime.visibility = View.GONE
                    if (currentPlaneTime != item) {
                        item.isChoose = true
                        if (currentPlaneTime != null) {
                            currentPlaneTime!!.isChoose = false
                        }
                        currentPlaneTime = item
                        notifyDataSetChanged()
                    }
                    planeListAdapter?.clear()
                    mModel.getPlaneList()
                }
        }
    }
    /**
     *事件点击
     */
    private fun setClick() {
        //选择时间
        mBinding.imgChooseTime.onNoDoubleClick {
            UIHelperUtils.showOptionsPicker(this, timePickerView)
        }
    }
    /**
     * 时间选择器
     */
    private val timePickerView by lazy {
        TimePickerBuilder(this, OnTimeSelectListener { date, v ->
            // 选中事件回调
            time = Utils.getDateTime(Utils.dateYMD, date)
            mModel.date=time
            mModel.getDates(time)
            planeListAdapter?.clear()
            mBinding.recyTrainTime.visibility = View.GONE
            mModel.getPlaneList()
        }).build()
    }

    /**
     *设置适配器
     */
    var planeListAdapter:PlaneListAdapter?=null
    private fun setAdapter() {
        val tagLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.recyTrainTime.layoutManager = tagLayoutManager
        planeListAdapter = PlaneListAdapter(startCityName, endCityName)
        mBinding.recyTrainTime.adapter = planeListAdapter
        mModel.result.observe(this, Observer {
            mBinding.recyTrainTime.visibility = View.VISIBLE
            planeListAdapter?.clear()
            val data = it
            planeListAdapter?.add(data)
            planeListAdapter?.notifyDataSetChanged()
        })
    }

    override fun initData() {
        mModel.getPlaneList()
        mModel.getDates(time)
    }
}
