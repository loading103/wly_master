package com.daqsoft.itinerary.ui

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.col.sln3.it
import com.daqsoft.baselib.base.AppManager
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.ResourceUtils
import com.daqsoft.itinerary.R
import com.daqsoft.itinerary.adapter.CustomCityAdapter
import com.daqsoft.itinerary.bean.CityBean
import com.daqsoft.itinerary.bean.ItineraryLabelBean
import com.daqsoft.itinerary.databinding.ActivityAssignDayBinding
import com.daqsoft.itinerary.interfa.OnItemSelectedListener
import com.daqsoft.itinerary.util.ItineraryManager
import com.daqsoft.provider.ItineraryRouter
import com.daqsoft.itinerary.vm.ItineraryCustomViewModel
import com.daqsoft.baselib.widgets.DividerItemDecoration

/**
 * @Author：      邓益千
 * @Create by：   2020/5/20 11:01
 * @Description： 天数分配
 */
@Route(path = ItineraryRouter.ASSIGN_DAY)
class AssignDayActivity : TitleBarActivity<ActivityAssignDayBinding, ItineraryCustomViewModel>(){

    /**行程标签*/
    @JvmField
    @Autowired(name = "itineraryLabel")
    var itineraryLabel : ItineraryLabelBean? = null

    /**城市列表*/
    @JvmField
    @Autowired(name = "citys")
    var citys : ArrayList<CityBean>? = null

    /**剩余小时*/
    @JvmField
    @Autowired(name = "hours")
    var remainHour = 0

    private var maxDay = 0

    private val cityAdapter: CustomCityAdapter by lazy {
        CustomCityAdapter()
    }

    override fun getLayout(): Int = R.layout.activity_assign_day

    override fun setTitle(): String = getString(R.string.itinerary_settings)

    override fun injectVm(): Class<ItineraryCustomViewModel> = ItineraryCustomViewModel::class.java

    override fun initView() {
        remainHour = (remainHour / 8)

        AppManager.instance.removeActivity(this)
        ItineraryManager.instance.addActivity(this)

        mBinding.recyclerView.let {
            it.adapter = cityAdapter.apply {
                emptyViewShow = false
                setOnItemSelectedListener(itemListener)
                setItemHeight(ResourceUtils.getDimension(it,R.dimen.dp_160))
                setShowDataType(1)
                if (!citys.isNullOrEmpty()){
                    add(citys!!)
                }
            }
            it.addItemDecoration(DividerItemDecoration(0, ResourceUtils.getDimension(it, R.dimen.dp_12)))
        }

        //跳转智能推荐
        mBinding.nextRecomm.setOnClickListener {
            for (cy in citys!!){
                if (maxDay < cy.suggestedDay){
                    maxDay = cy.suggestedDay
                }
            }

            ARouter.getInstance()
                .build(ItineraryRouter.SMART_RECOMM)
                .withInt("maxDay",(maxDay * 2))
                .withParcelableArrayList("citys",citys)
                .withParcelable("itineraryLabel",itineraryLabel)
                .navigation()
        }
    }

    override fun initData() {
        var totalDay = 0
        citys?.let {
            for (city in it){
                if (maxDay < city.suggestedDay){
                    maxDay = city.suggestedDay
                }
                //行程总天数
                totalDay += city.suggestedDay
            }
        }
        val remain = remainHour - totalDay
        val dayText = if (remain <= 0){
            "行程共${totalDay}天"
        } else {
            "行程共${totalDay}天,剩余${remain}天"
        }
        mBinding.dayView.text = dayText
    }

    private val itemListener = object : OnItemSelectedListener<CityBean> {
        override fun onSelected(position: Int,item: CityBean) {
            initData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ItineraryManager.instance.removeActivity(this)
    }
}