package com.daqsoft.itinerary.ui.fragment

import android.content.Context
import android.view.View
import androidx.lifecycle.Observer
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.baselib.utils.*
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.itinerary.R
import com.daqsoft.itinerary.bean.ProvinceBean
import com.daqsoft.itinerary.databinding.FragmentSettingItineraryBinding
import com.daqsoft.itinerary.ui.ItineraryCustomActivity
import com.daqsoft.itinerary.vm.ItineraryCustomViewModel
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.view.popupwindow.AreaSelectPopupWindow
import java.util.*
import kotlin.collections.ArrayList

/**
 * @Author：      邓益千
 * @Create by：   2020/4/23 19:37
 * @Description： 步骤一，行程设置
 */
class ItinerarySettingFragment : BaseFragment<FragmentSettingItineraryBinding, ItineraryCustomViewModel>() {

    /**用于判断开始时间 or 结束时间*/
    private var tagTime = 0

    private var cityName:String? = ""

    private val cityList: MutableList<ArrayList<ProvinceBean>> by lazy {
        ArrayList<ArrayList<ProvinceBean>>()
    }

    private lateinit var parentActivity: ItineraryCustomActivity

    override fun getLayout(): Int = R.layout.fragment_setting_itinerary

    override fun injectVm(): Class<ItineraryCustomViewModel> = ItineraryCustomViewModel::class.java

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentActivity = context as ItineraryCustomActivity
    }

    override fun initView() {
        cityName = getString(R.string.default_city)
        mBinding.viewCity.text = cityName
        parentActivity.customLabel.value!!.cityName = cityName

        //开始日期View
        mBinding.startDate.apply {
            val startDate = DateUtil.getNextHotelDateString(Date())
            text = startDate.replace("-", "月").substring(5, startDate.length) + "日"
            parentActivity.customLabel.value?.travelStartTime = startDate
            onNoDoubleClick {
                tagTime = 0
                UIHelperUtils.showOptionsPicker(activity!!, timePickerView)
            }
        }

        //结束日期View
        mBinding.endDate.apply {
            val endDate = DateUtil.getNextHotelDateString(Date(), 7)
            text = endDate.replace("-", "月").substring(5, endDate.length) + "日"
            parentActivity.customLabel.value?.travelEndTime = endDate
            onNoDoubleClick {
                tagTime = 1
                UIHelperUtils.showOptionsPicker(activity!!, timePickerView)
            }
        }


        mBinding.viewCity.onNoDoubleClick {
            UIHelperUtils.showOptionsPicker(activity!!, cityPickerView)
        }
    }

    override fun initData() {
        showLoadingDialog()
        mModel.siteInfo.observe(this, Observer {
            parentActivity.customLabel.value!!.siteCode = it.siteCode
        })

        mModel.provinceList.observe(this, Observer {
            val adCode: String? = SPUtils.getInstance().getString(SPKey.SITE_REGION)
            //ToastUtils.showMessage("adcode=" + adCode)
            it.removeAt(0)
            //循环省
            for (province in it) {
                province.sub.removeAt(0)
                val cityDate = ArrayList<ProvinceBean>()
                if (adCode.isNullOrEmpty()) {
                    if (province.name == cityName) {
                        parentActivity.customLabel.value!!.cityRegion = province.region
                    }
                } else {
                    if (province.region == adCode) {
                        if(!province.sub.isNullOrEmpty()){
                            val city = province.sub[0]
                            cityName = city.name
                            mBinding.viewCity.text = cityName
                            parentActivity.customLabel.value!!.cityName = cityName
                            parentActivity.customLabel.value!!.cityRegion = city.region
                        }

                    }
                }
                //循环市
                for (city in province.sub) {
                    if (city.child) {
                        city.sub.removeAt(0)
                    }

                    cityDate.add(city)
                }
                cityList.add(cityDate)
            }
            cityPickerView.setPicker(it, cityList as List<MutableList<ProvinceBean>>)
            dissMissLoadingDialog()
        })

        // 地区
//        mModel.areas.observe(this, Observer {
//            if (!it.isNullOrEmpty()) {
//                var adCode: String? = SPUtils.getInstance().getString(SPKey.SITE_REGION)
//                if (adCode.isNullOrEmpty()) {
//                    cityName = getString(R.string.default_city)
//                } else {
//                    for (item in it) {
//                        if (item.name == adCode) {
//                            cityName = item.name
//                            break
//                        }
//                        if (!item.subList.isNullOrEmpty()) {
//                            for (item2 in item.subList!!) {
//                                cityName = item2.name
//                            }
//                        }
//                    }
//                }
//            }
//        })

        mModel.getSiteCode()
        mModel.getProvince()
//        mModel.getChildRegions()
    }

    /**初始化日期控件*/
    private val timePickerView by lazy {
        TimePickerBuilder(context, OnTimeSelectListener { date, view ->
            if (tagTime == 0) {
                val startDate = Utils.getDateTime(Utils.MD, date)
                mBinding.startDate.text = startDate
                parentActivity.customLabel.value?.travelStartTime = Utils.getDateTime(Utils.dateYMD, date)
            } else {
                val endDate = Utils.getDateTime(Utils.MD, date)
                mBinding.endDate.text = endDate
                parentActivity.customLabel.value?.travelEndTime = Utils.getDateTime(Utils.dateYMD, date)
            }
        }).build()
    }

    /**城市选择器*/
    private val cityPickerView by lazy {
        OptionsPickerBuilder(context, OnOptionsSelectListener { options1, options2, _, _ ->
            val bean = cityList[options1][options2]
            mBinding.viewCity.text = bean.name
            parentActivity.customLabel.value!!.cityName = bean.name
            parentActivity.customLabel.value!!.cityRegion = bean.region
        }).build<ProvinceBean>()
    }
}