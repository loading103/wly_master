package com.daqsoft.itinerary.ui

import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.AppManager
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.ResourceUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.itinerary.R
import com.daqsoft.itinerary.adapter.CustomCityAdapter
import com.daqsoft.itinerary.bean.CityBean
import com.daqsoft.itinerary.bean.ItineraryLabelBean
import com.daqsoft.itinerary.databinding.ActivityCustomDestinationBinding
import com.daqsoft.itinerary.interfa.OnItemSelectedListener
import com.daqsoft.itinerary.util.ItineraryManager
import com.daqsoft.provider.ItineraryRouter
import com.daqsoft.itinerary.vm.ItineraryCustomViewModel
import com.daqsoft.baselib.widgets.DividerItemDecoration

/**
 * @Author：      邓益千
 * @Create by：   2020/5/19 20:16
 * @Description：自定义目的地
 */
@Route(path = ItineraryRouter.CUSTOM_DESTINATION)
class CustomDestinationActivity : TitleBarActivity<ActivityCustomDestinationBinding, ItineraryCustomViewModel>(){

    /**共计时间*/
    @JvmField
    @Autowired(name = "hours")
    var hours = 0

    /**行程标签*/
    @JvmField
    @Autowired(name = "itineraryLabel")
    var itineraryLabel : ItineraryLabelBean? = null

    /**选中的城市*/
    private val citys: ArrayList<CityBean> by lazy {
        ArrayList<CityBean>()
    }

    private val cityAdapter: CustomCityAdapter by lazy {
        CustomCityAdapter()
    }

    override fun getLayout(): Int = R.layout.activity_custom_destination

    override fun setTitle(): String = getString(R.string.itinerary_settings)

    override fun injectVm(): Class<ItineraryCustomViewModel> = ItineraryCustomViewModel::class.java

    override fun initView() {
        AppManager.instance.removeActivity(this)
        ItineraryManager.instance.addActivity(this)

        mBinding.recyclerView.let {
            it.adapter = cityAdapter.apply {
                emptyViewShow = false
                setOnItemSelectedListener(itemListener)
                setItemHeight(ResourceUtils.getDimension(it,R.dimen.dp_101))
            }
            it.addItemDecoration(DividerItemDecoration(0, ResourceUtils.getDimension(it, R.dimen.dp_12)))
        }

        //下一步
        mBinding.nextView.setOnClickListener {
            if (citys.isNullOrEmpty()){
                ToastUtils.showMessage("请加添想去的城市！")
                return@setOnClickListener
            }
            ARouter.getInstance()
                .build(ItineraryRouter.ASSIGN_DAY)
                .withInt("hours",hours)
                .withParcelable("itineraryLabel",itineraryLabel)
                .withParcelableArrayList("citys",citys)
                .navigation()
        }
    }

    override fun initData() {
        mModel.cityList.observe(this, Observer {
            cityAdapter.add(it)
        })

        mModel.getDestination()
    }

    private val itemListener = object : OnItemSelectedListener<CityBean>{
        override fun onSelected(position: Int,item: CityBean) {
            if (item.isSelected) {
                citys.add(item)
            } else {
                citys.remove(item)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ItineraryManager.instance.removeActivity(this)
    }
}