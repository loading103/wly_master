package com.daqsoft.itinerary.ui

import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.ResourceUtils
import com.daqsoft.provider.Constants
import com.daqsoft.itinerary.R
import com.daqsoft.itinerary.adapter.TrafficAdapter
import com.daqsoft.itinerary.bean.TrafficItem
import com.daqsoft.itinerary.databinding.ActivityItineraryTrafficBinding
import com.daqsoft.itinerary.vm.ItineraryViewModel
import com.daqsoft.itinerary.widget.DividerItemDecoration
import com.daqsoft.provider.ItineraryRouter

/**
 * @Author：      邓益千
 * @Create by：   2020/5/23 10:15
 * @Description： 交通列表
 */
@Route(path = ItineraryRouter.TRAFFIC_LIST)
class ItineraryTrafficActivity : TitleBarActivity<ActivityItineraryTrafficBinding,ItineraryViewModel>() {

    @JvmField
    @Autowired
    var startSite: Int = 0

    @JvmField
    @Autowired
    var endSite: Int = 0

    /**
     * 出行方式
     */
    @JvmField
    @Autowired
    var travelMode: String = ""


    private val trafficAdapter: TrafficAdapter by lazy {
        TrafficAdapter()
    }

    override fun getLayout(): Int = R.layout.activity_itinerary_traffic

    override fun setTitle(): String  = getString(R.string.traffic_assistant)

    override fun injectVm(): Class<ItineraryViewModel> = ItineraryViewModel::class.java

    override fun initView() {
        mBinding.recyclerView.apply {
            adapter = trafficAdapter
            addItemDecoration(DividerItemDecoration(1,ResourceUtils.getDimension(this,R.dimen.dp_20)))
        }
    }

    override fun initData() {
        showLoadingDialog()
        mModel.trafficDetail.observe(this, Observer {
            dissMissLoadingDialog()
            for (i in it.trafficList.size - 1 downTo 0) {
                if (it.trafficList[i].type == Constants.selfDrive){
                    it.trafficList.removeAt(i)
                    break
                }
            }
            trafficAdapter.add(it.trafficList)
        })

        mModel.queryTraffic(startSite,endSite)
    }
}