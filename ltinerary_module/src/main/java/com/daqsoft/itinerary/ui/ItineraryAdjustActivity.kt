package com.daqsoft.itinerary.ui

import android.app.Activity
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AlertDialog
import androidx.collection.ArrayMap
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.itinerary.R
import com.daqsoft.itinerary.adapter.ItineraryAdjustAdapter
import com.daqsoft.itinerary.bean.ItineraryDetailBean
import com.daqsoft.itinerary.databinding.ActivityItineraryAdjustBinding
import com.daqsoft.itinerary.util.ItemCustomTouchHelper
import com.daqsoft.itinerary.util.ItemTouchHelperCallback
import com.daqsoft.provider.ItineraryRouter
import com.daqsoft.itinerary.vm.ItineraryViewModel

/**
 * @Author：      邓益千
 * @Create by：   2020/5/19 13:41
 * @Description： 行程调整
 */
@Route(path = ItineraryRouter.ITINERARY_ADJUST)
class ItineraryAdjustActivity : TitleBarActivity<ActivityItineraryAdjustBinding, ItineraryViewModel>() {

    /**行程详情*/
    @JvmField
    @Autowired(name = "detail")
    var detail: ItineraryDetailBean? = null
    var itemTouchHelper: ItemCustomTouchHelper? = null
    private val adapter: ItineraryAdjustAdapter by lazy {
        ItineraryAdjustAdapter()
    }

    override fun getLayout(): Int = R.layout.activity_itinerary_adjust

    override fun setTitle(): String = getString(R.string.itinerary_adjust)

    override fun injectVm(): Class<ItineraryViewModel> = ItineraryViewModel::class.java

    override fun initView() {
        mBinding.itineraryName.text = detail?.name
        itemTouchHelper = ItemCustomTouchHelper(ItemTouchHelperCallback(adapter))
        itemTouchHelper?.attachToRecyclerView(mBinding.recyclerView)
        mBinding.recyclerView.adapter = adapter
        detail?.let {
            var temps: MutableList<ItineraryDetailBean.AgendaBean.PlansBean> = mutableListOf()
            for (index in it.dayList.indices) {
                var day = it.dayList[index]
                if (!day.sourceList.isNullOrEmpty()) {
                    var planDay: ItineraryDetailBean.AgendaBean.PlansBean = ItineraryDetailBean.AgendaBean.PlansBean()
                    planDay.type = 2
                    planDay.day = index + 1
                    planDay.dayId = day.id
                    planDay.time = day.time
                    temps.add(planDay)
                    for (plan in day.sourceList) {
                        plan.dayId = day.id
                    }
                    temps.addAll(day.sourceList)
                } else {
                    // 处理空白天""
                }
            }
            adapter.add(temps)
        }

        mBinding.confirmView.setOnClickListener {
            var index = 0
            val paramsList = ArrayList<ArrayMap<String, Any>>()
            for (item in adapter.getData()) {
                if (item.type != 2) {
                    val params = ArrayMap<String, Any>()
                    index = index + 1
                    params["sort"] = index
                    params["dayId"] = item.dayId
                    params["id"] = item.id
                    params["sourceId"] = item.id
                    params["resourceType"] = item.resourceType
                    params["type"] = "JOURNEY_SOURCE"
                    paramsList.add(params)
                }
            }
            val jsonParams = ArrayMap<String, Any>()
            jsonParams["journeyId"] = detail?.id
            jsonParams["operateType"] = 2
            jsonParams["params"] = paramsList
            mModel.operateSource(jsonParams)
        }
    }

    override fun initData() {
        mModel.refresh.observe(this, Observer {
            AlertDialog.Builder(this)
                .setTitle("温馨提示！")
                .setMessage("调整完成")
                .setPositiveButton("确定") { dialog, _ ->
                    dialog.dismiss()
                    setResult(Activity.RESULT_OK)
                    finish()
                }.create().show()
        })
    }
}