package com.daqsoft.travelCultureModule.sidetour.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Poi
import com.amap.api.navi.AmapNaviPage
import com.amap.api.navi.AmapNaviParams
import com.amap.api.navi.AmapNaviType
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.utils.ResourceUtils
import com.daqsoft.baselib.utils.SpannUtils
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemGasstationInfoBinding
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.travelCultureModule.sidetour.SideTourClosePageEvent
import com.daqsoft.travelCultureModule.sidetour.adapter.ImageRecyAdapter
import com.daqsoft.travelCultureModule.sidetour.viewmodel.SideTourViewModel
import kotlinx.android.synthetic.main.item_side_tour_parking.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception


/**
 * @Author：      邓益千
 * @Create by：   2020/6/24 10:54
 * @Description： 加油站
 */
class GasstationFragment : BaseFragment<ItemGasstationInfoBinding, SideTourViewModel>() {

    private var currentIndex = 0

    companion object {
        const val SITE_NAME = "siteName"
        const val ADDRESS = "address"
        const val CURRENT = "current"
        const val TOTAL = "total"
        const val ISERVICE = "isService"

        fun getInstance(args: Bundle): GasstationFragment {
            val fragmentL = GasstationFragment()
            fragmentL.arguments = args
            return fragmentL
        }
    }

    /**开始坐标*/
    private lateinit var startLatLng: LatLng

    /**结束坐标*/
    private lateinit var endLatLng: LatLng

    private var isService: Boolean = false
    private val mImageRecyAdapter: ImageRecyAdapter by lazy {
        ImageRecyAdapter(context!!).apply {
            emptyViewShow = false
        }
    }

    override fun getLayout(): Int = R.layout.item_gasstation_info

    override fun injectVm(): Class<SideTourViewModel> = SideTourViewModel::class.java

    override fun initView() {
        mBinding.imgControlGaasstation.onNoDoubleClick {
            if (mBinding.typeLayout.isShown) {
                mBinding.typeLayout.visibility = View.GONE
                mBinding.imgControlGaasstation.setImageResource(R.mipmap.map_arrow_up)
            } else {
                mBinding.typeLayout.visibility = View.VISIBLE
                mBinding.imgControlGaasstation.setImageResource(R.mipmap.main_arrow_down)
            }
        }
        mBinding?.rvAddressImg.layoutManager =
            GridLayoutManager(context, 3)
        mBinding?.rvAddressImg.adapter = mImageRecyAdapter
    }

    override fun initData() {
        arguments?.let {
            startLatLng = LatLng(it.getDouble("startLat"), it.getDouble("startLon"))
            endLatLng = LatLng(it.getDouble("lat"), it.getDouble("lon"))
            isService = it.getBoolean(ISERVICE)

            mModel.getGasstationDetails(it.getInt("id"), it.getDouble("lon"), it.getDouble("lat"))
            currentIndex = it.getInt(CURRENT)
            SpannUtils.getBuilder(mBinding.countView)
                .append("${currentIndex}/")
                .append("${it.getInt(TOTAL)}")
                .setTextColor(ResourceUtils.getColor(context!!, R.color.color_666))
                .build()

            mBinding.naviView.onNoDoubleClick {
                val start = Poi("我的位置", startLatLng, "")
                val end = Poi(it.getString(SITE_NAME), endLatLng, "")
                AmapNaviPage.getInstance().showRouteActivity(
                    context,
                    AmapNaviParams(start, null, end, AmapNaviType.DRIVER),
                    null
                )
            }

            val distance = AMapUtils.calculateLineDistance(startLatLng, endLatLng)
            var tip = if (isService) {
                "您"
            } else {
                "该场所"
            }
            mBinding.distanceView.text =
                "距离${tip}${Utils.toKm(distance)}km | ${it.getString(ADDRESS)}"
            mBinding.name = it.getString(SITE_NAME)

        }

        mModel.gasStationDetail.observe(this, Observer {
            var temp: MutableList<String> = mutableListOf()
            if (it != null) {
                if (!it.oil0.isNullOrEmpty()) {
                    temp.add(it.oil0)
                }
                if (!it.oil92.isNullOrEmpty()) {
                    temp.add(it.oil92)
                }
                if (!it.oil95.isNullOrEmpty()) {
                    temp.add(it.oil95)
                }
                if (!it.oil98.isNullOrEmpty()) {
                    temp.add(it.oil98)
                }

                mBinding.bean = it
                if (currentIndex == 1) {
                    if (it?.images.isNullOrEmpty()) {
                        mBinding.txtGasstationName.maxWidth =
                            resources.getDimension(R.dimen.dp_210).toInt()
                    } else {
                        mBinding.txtGasstationName.maxWidth =
                            resources.getDimension(R.dimen.dp_164).toInt()
                    }
                    mBinding.txtGasstationTag.visibility = View.VISIBLE
                } else {
                    mBinding.txtGasstationTag.visibility = View.GONE
                }

                if (!it?.openTime.isNullOrEmpty() || !it?.phone.isNullOrEmpty() ||
                    !it?.images.isNullOrEmpty() || !it?.oil0.isNullOrEmpty() || !it?.oil92.isNullOrEmpty()
                    || !it?.oil95.isNullOrEmpty() || !it?.oil98.isNullOrEmpty()
                ) {
                    mBinding.imgControlGaasstation.visibility = View.VISIBLE
                } else {
                    mBinding.imgControlGaasstation.visibility = View.GONE
                }
                for (i in temp.indices) {
                    when (i) {
                        0 -> {
                            SpannUtils.getBuilder(mBinding.labelView)
                                .append("0#\t\t\t")
                                .append("¥${temp[i]}")
                                .setTextColor(ResourceUtils.getColor(context!!, R.color.color_666))
                                .build()
                            mBinding.labelView.visibility = View.VISIBLE
                        }
                        1 -> {
                            SpannUtils.getBuilder(mBinding.labelView2)
                                .append("92#\t\t\t")
                                .append("¥${temp[i]}")
                                .setTextColor(ResourceUtils.getColor(context!!, R.color.color_666))
                                .build()
                            mBinding.labelView2.visibility = View.VISIBLE
                        }
                        2 -> {
                            SpannUtils.getBuilder(mBinding.tvLabelThree)
                                .append("95#\t\t\t")
                                .append("¥${temp[i]}")
                                .setTextColor(ResourceUtils.getColor(context!!, R.color.color_666))
                                .build()
                            mBinding.tvLabelThree.visibility = View.VISIBLE
                        }
                        3 -> {
                            SpannUtils.getBuilder(mBinding.tvLabelFour)
                                .append("98#\t\t\t")
                                .append("¥${temp[i]}")
                                .setTextColor(ResourceUtils.getColor(context!!, R.color.color_666))
                                .build()
                            mBinding.tvLabelFour.visibility = View.VISIBLE
                        }
                    }
                }
                if (temp.isNullOrEmpty()) {
                    mBinding.typeLayout.visibility = View.GONE
                }
                if (!it.images.isNullOrEmpty()) {
                    var temps = it.images.split(",")
                    if (!temp.isNullOrEmpty()) {
                        mImageRecyAdapter.clear()
                        mImageRecyAdapter.add(temps as MutableList<String>)
                    }
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun closePage(event: SideTourClosePageEvent) {
        //  关闭弹出页面
        if (event.type == ResourceType.TYPE_GAS_STATION)
            if (mBinding.typeLayout.isShown) {
                mBinding.typeLayout.visibility = View.GONE
                mBinding.imgControlGaasstation.setImageResource(R.mipmap.map_arrow_up)
            }
    }
}