package com.daqsoft.travelCultureModule.sidetour.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.utils.SystemHelper
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemSideTourParkingBinding
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.MapResBean
import com.daqsoft.provider.bean.ParkingBean
import com.daqsoft.provider.utils.MapNaviUtils
import com.daqsoft.travelCultureModule.sidetour.SideTourClosePageEvent
import com.daqsoft.travelCultureModule.sidetour.adapter.ImageRecyAdapter
import com.daqsoft.travelCultureModule.sidetour.viewmodel.SideTourPakingViewModel
import com.jakewharton.rxbinding2.view.RxView
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.item_side_tour_parking.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

/**
 * @Description 找车位适配器
 * @ClassName   SideTourFragment
 * @Author      luoyi
 * @Time        2020/3/19 10:52
 */
class SideTourParkingFragment() :
    BaseFragment<ItemSideTourParkingBinding, SideTourPakingViewModel>() {

    /**
     *  厕所地图实体
     */
    var bean: MapResBean? = null
    /**
     *  集合大小
     */
    var totalSize: Int = 0
    /**
     * 当前页码
     */
    var currentIndex: Int = 0
    /**
     * 纬度
     */
    var lat: String = ""
    /**
     * 经度
     */
    var lng: String = ""
    var isService = false

    /**
     *
     */
    private var permissions: RxPermissions? = null

    private var mImageRecyAdapter: ImageRecyAdapter? = null

    companion object {
        fun newInstance(
            bean: MapResBean,
            index: Int,
            total: Int,
            lat: String,
            lng: String,
            isService: Boolean = false
        ): SideTourParkingFragment {
            var frag = SideTourParkingFragment()
            var bundle = Bundle()
            bundle.putParcelable("mapRes", bean)
            bundle.putInt("index", index)
            bundle.putInt("totalSize", total)
            bundle.putString("lat", lat)
            bundle.putString("lng", lng)
            bundle.putBoolean("isService", isService)
            frag.arguments = bundle
            return frag
        }
    }

    private fun getPageParams() {
        arguments?.run {
            bean = getParcelable("mapRes")
            currentIndex = getInt("index", 0)
            totalSize = getInt("totalSize", 0)
            lat = getString("lat")
            lng = getString("lng")
            isService = getBoolean("isService")
        }

    }

    override fun getLayout(): Int = R.layout.item_side_tour_parking

    override fun injectVm(): Class<SideTourPakingViewModel> {
        return SideTourPakingViewModel::class.java
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        EventBus.getDefault().register(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            EventBus.getDefault().unregister(this)
            permissions = null
        } catch (e: java.lang.Exception) {
        }


    }

    override fun initView() {
        permissions = RxPermissions(this)
        getPageParams()
        bean?.let {
            mBinding.name = it?.name

            mBinding.address = it?.address
            mBinding.currentIndex = "${currentIndex + 1}"
            mBinding.totalSize = "/$totalSize"
            val disStr = if (isService) "您" else "该场所"

            if (!it.latitude.isNullOrEmpty() && !it.longitude.isNullOrEmpty() && !lat.isNullOrEmpty() && !lng.isNullOrEmpty()) {
                var latLng = LatLng(lat.toDouble(), lng.toDouble())
                var end = LatLng(it.latitude!!.toDouble(), it.longitude!!.toDouble())
                var dis = AMapUtils.calculateLineDistance(latLng, end)
                var disDistance = if (dis > 1000) {
                    val df = DecimalFormat("0.00")
                    df.format(dis / 1000) + "KM | "
                } else {
                    dis.toInt().toString() + "M | "

                }
                mBinding.distance = "距离${disStr}" + disDistance + it?.address
            } else {
                mBinding.distance = "距离${disStr}：暂无," + it?.address
            }

            if (!it.images.isNullOrEmpty()) {
                val images = it.images!!.split(",")
                var imgUrl = ""
                if (!images.isNullOrEmpty()) {
                    imgUrl = images[0]
                }
                mBinding?.imageUrl = imgUrl
                if (imgUrl.isNullOrEmpty()) {
                    mBinding.imgParkingHead.visibility = View.GONE
                } else {
                    mBinding.imgParkingHead.visibility = View.VISIBLE
                }
            } else {
                mBinding.imgParkingHead.visibility = View.GONE
            }
            mBinding?.placeholder = context!!.getDrawable(R.mipmap.placeholder_img_fail_240_180)
            var params: HashMap<String, String> = HashMap()
            params["id"] = it.id.toString()
            params["lat"] = lat
            params["lng"] = lng
            mModel.getParkingDetail(params)

        }
        mImageRecyAdapter = ImageRecyAdapter(context!!)
        mBinding?.vItemSideTourParkingOther.recyParkImages.layoutManager =
            GridLayoutManager(context, 3)
        mBinding?.vItemSideTourParkingOther.recyParkImages.adapter = mImageRecyAdapter
        initViewModel()
        initViewEvent()
    }

    @SuppressLint("CheckResult")
    private fun initViewEvent() {
        mBinding?.vControlParkingMoreInfo.setOnClickListener {
            if (v_item_side_tour_parking_other.visibility == View.VISIBLE) {
                v_item_side_tour_parking_other.visibility = View.GONE
                mBinding.imgControlParking.setImageResource(R.mipmap.main_arrow_up)
            } else {
                v_item_side_tour_parking_other.visibility = View.VISIBLE
                mBinding.imgControlParking.setImageResource(R.mipmap.main_arrow_down)
            }
        }
        RxView.clicks(mBinding.vItemSideTourParkingOther.txtParkPhoneNum)
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                val phoneNum = mBinding.vItemSideTourParkingOther.txtParkPhoneNum.text
                if (!phoneNum.toString().isNullOrEmpty()) {
                    SystemHelper.callPhone(context!!,phoneNum.toString())
                }
            }

        RxView.clicks(mBinding.vParkingToNav)
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                if (bean != null && !bean?.latitude.isNullOrEmpty() && !bean?.longitude.isNullOrEmpty()) {
                    if (MapNaviUtils.isGdMapInstalled()) {
                        MapNaviUtils.openGaoDeNavi(
                            context, 0.0, 0.0, null,
                            bean!!.latitude!!.toDouble(), bean!!.longitude!!.toDouble(),
                            bean?.address
                        )
                    } else {
                        mModel.toast.postValue("非常抱歉，系统未安装地图软件")
                    }
                } else {
                    mModel.toast.postValue("非常抱歉，暂无位置信息")
                }
            }
    }

    private fun initViewModel() {
        mModel.parkingInfo.observe(this, Observer {
            if (it != null) {
                if (currentIndex == 0) {
                    mBinding.txtParkName.maxWidth =
                        if(it.images.isNullOrEmpty()) {
                            resources.getDimension(R.dimen.dp_210).toInt()
                        }else{
                            resources.getDimension(R.dimen.dp_164).toInt()
                        }
                    mBinding.txtParkTag.visibility = View.VISIBLE
                } else {
                    mBinding.txtParkTag.visibility = View.GONE
                }
                try {
                    if ((!it.total.isNullOrEmpty() && it.total.toInt() > 0)||!it.images.isNullOrEmpty()||!it.charges.isNullOrEmpty()||!it.openTime.isNullOrEmpty()||!it.phone?.isNullOrEmpty()) {
                        mBinding?.vControlParkingMoreInfo.visibility = View.VISIBLE
                        v_item_side_tour_parking_other.visibility = View.GONE
                        bindData(it)
                    } else {
                        mBinding?.vControlParkingMoreInfo.visibility = View.GONE
                        v_item_side_tour_parking_other.visibility = View.GONE
                    }
                } catch (e: Exception) {
                    mBinding?.vControlParkingMoreInfo.visibility = View.GONE
                    mBinding?.vControlParkingMoreInfo.visibility = View.GONE
                }

            } else {
                mBinding?.vControlParkingMoreInfo.visibility = View.GONE
                v_item_side_tour_parking_other.visibility = View.GONE
            }
        })
    }

    /**
     * param data 车位详情对象
     */
    private fun bindData(data: ParkingBean) {
        mBinding.vItemSideTourParkingOther.park = data
        mBinding?.vItemSideTourParkingOther.totalInfo = "共${data.total}个"
        if (!data.scaleSize.isNullOrEmpty()) {
            mBinding?.vItemSideTourParkingOther.scaleSize = "${data.scaleSize}㎡"
            mBinding?.vItemSideTourParkingOther.txtParkScale.visibility = View.VISIBLE
        } else {
            mBinding?.vItemSideTourParkingOther.txtParkScale.visibility = View.GONE
        }
        if (!data.images.isNullOrEmpty()) {
            val images = data.images.split(",")
            var imgUrl = ""
            if (images.isNotEmpty()) {
                imgUrl = images[0]
            }
            mBinding?.imageUrl = imgUrl
            mImageRecyAdapter!!.clear()
            mImageRecyAdapter!!.add(images as MutableList<String>)
            mBinding?.vItemSideTourParkingOther?.vParkImages.visibility = View.VISIBLE
        } else {
            mBinding?.vItemSideTourParkingOther?.vParkImages.visibility = View.GONE
        }

    }

    override fun initData() {
    }

    override fun onDestroy() {
        super.onDestroy()
        permissions = null
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun closePage(event: SideTourClosePageEvent) {
        //  关闭弹出页面
        if (event.type == ResourceType.CONTENT_TYPE_PARKING)
            try {
                v_item_side_tour_parking_other?.let {
                    if (it.visibility == View.VISIBLE) {
                        it.visibility = View.GONE
                        mBinding.imgControlParking?.setImageResource(R.mipmap.main_arrow_up)
                    }
                }
            } catch (e: java.lang.Exception) {
            }

    }
}