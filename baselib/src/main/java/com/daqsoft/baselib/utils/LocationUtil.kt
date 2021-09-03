package com.daqsoft.baselib.utils

import android.widget.FrameLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.view.OptionsPickerView
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.bean.LocationData
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.net.CityRepository
import com.daqsoft.baselib.net.NetStatus
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity

/**
 * @Description 地区选择工具
 * @ClassName   LocationUtil
 * @Author      PuHua
 * @Time        2019/11/6 10:10
 */
class LocationUtil {
    /**
     * 地区选择器
     */
    private var locationPv: OptionsPickerView<LocationData>? = null
    val locations = MutableLiveData<MutableList<LocationData>>()
    /**
     * 省级选择条
     */

    val province = mutableListOf<LocationData>()
    /**
     * 市级选择条
     */
    val mCity = mutableListOf<MutableList<LocationData>>()
    /**
     * 县级选择条
     */
    val mDirect = mutableListOf<MutableList<MutableList<LocationData>>>()

    /**
     * 网络状态变量
     */
    var mPresenter = MutableLiveData<NetStatus>(NetStatus())

    /**
     * 构造
     */
    constructor(
        context: RxAppCompatActivity,
        onLocationSelectListener: OnLocationSelectListener,
        presenter: MutableLiveData<NetStatus>
    ) {
        if (locationPv == null) {
            mPresenter = presenter
            locationPv = OptionsPickerBuilder(context, OnOptionsSelectListener { s1, s2, s3, v ->
                if (s3 != 0) {
                    province[s1].sub[s2].sub[s3].memo =
                        province[s1].name + province[s1].sub[s2].name + province[s1].sub[s2].sub[s3].name
                    onLocationSelectListener.onLocationSelect(province[s1].sub[s2].sub[s3])
                } else {
                    if (s2 != 0) {
                        province[s1].sub[s2].memo = province[s1].name + province[s1].sub[s2].name
                        onLocationSelectListener.onLocationSelect(province[s1].sub[s2])
                    } else {
                        province[s1].memo = province[s1].name
                        onLocationSelectListener.onLocationSelect(province[s1])
                    }
                }

            }).build<LocationData>()
            // 省级数据监听
            locations.observe(context, Observer {
                locationPv?.setPicker(province, mCity, mDirect!!)
                var layoutParams: FrameLayout.LayoutParams =
                    locationPv!!.dialogContainerLayout.layoutParams as FrameLayout.LayoutParams
                layoutParams.bottomMargin = UIHelperUtils.getNavigationBarHeight(context)
                locationPv!!.dialogContainerLayout.layoutParams = layoutParams
                if (!locationPv!!.isShowing) {
                    showPic()
                }
            })
            if (province == null || province.size <= 0 || mCity == null || mCity.size <= 0 || mDirect == null && mDirect.size <= 0
            ) {
                getLocation("")
            }

        } else {
            if (!locationPv!!.isShowing) {
                showPic()
            }
        }
    }

    /**
     * 显示控件
     */
    fun showPic() {
        locationPv!!.show()
    }

    /**
     * 获取省级
     */
    private fun getLocation(region: String) {
        mPresenter.value?.loading = true
        CityRepository().service.getLocations()
            .excute(object : BaseObserver<LocationData>(mPresenter) {
                override fun onSuccess(response: BaseResponse<LocationData>) {
                    val data: ArrayList<LocationData> =
                        response.datas as ArrayList<LocationData>
                    dealData(data)
                }
            })
    }

    /**
     * 数据处理
     */
    fun dealData(data: ArrayList<LocationData>) {
        province.addAll(data!!)
        province.removeAt(0)

        for (i in 0 until (province?.size)) {
            val list2 = mutableListOf<MutableList<LocationData>>()
            val list1 = province[i].sub
            mCity.add(list1)
            if (list1.size > 0) {
                for (j in 0 until list1.size) {
                    val list3 = list1[j].sub
                    list2.add(list3)
                }
            }
            mDirect.add(list2)
        }
        locations.postValue(data)
    }

    interface OnLocationSelectListener {
        fun onLocationSelect(location: LocationData)
    }
}