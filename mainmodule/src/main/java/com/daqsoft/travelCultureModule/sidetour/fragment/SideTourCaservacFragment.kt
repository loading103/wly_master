package com.daqsoft.travelCultureModule.sidetour.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.SystemHelper
import com.daqsoft.baselib.utils.UIHelperUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemCaservacInfoBinding
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.CaservacBean
import com.daqsoft.provider.bean.MapResBean
import com.daqsoft.provider.utils.MapNaviUtils
import com.daqsoft.travelCultureModule.net.MainRepository
import com.daqsoft.travelCultureModule.sidetour.SideTourClosePageEvent
import com.daqsoft.travelCultureModule.sidetour.adapter.ImageRecyAdapter
import com.jakewharton.rxbinding2.view.RxView
import com.tbruyelle.rxpermissions2.RxPermissions
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

/**
 * @Description
 * @ClassName   SideTourCaservacFragment
 * @Author      luoyi
 * @Time        2020/8/28 17:11
 */
class SideTourCaservacFragment :
    BaseFragment<ItemCaservacInfoBinding, SideTourCaservacViewModel>() {
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
    /**
     *权限
     */
    private var permissions: RxPermissions? = null
    private val mImageRecyAdapter: ImageRecyAdapter by lazy {
        ImageRecyAdapter(context!!).apply {
            emptyViewShow = false
        }
    }
    var isService = false

    companion object {
        fun newInstance(
            bean: MapResBean,
            index: Int,
            total: Int,
            lat: String,
            lng: String,
            isService: Boolean = false
        ): SideTourCaservacFragment {
            var frag = SideTourCaservacFragment()
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

    override fun getLayout(): Int {
        return R.layout.item_caservac_info
    }

    override fun injectVm(): Class<SideTourCaservacViewModel> {
        return SideTourCaservacViewModel::class.java
    }


    override fun initData() {
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        permissions = RxPermissions(this)
        mBinding?.rvAddressImg.layoutManager =
            GridLayoutManager(context, 3)
        mBinding?.rvAddressImg.adapter = mImageRecyAdapter
        mBinding.imgControlCaservac.onNoDoubleClick {
            if (mBinding.vBottomCaservacInfo.isShown) {
                mBinding.vBottomCaservacInfo.visibility = View.GONE
                mBinding.imgControlCaservac.setImageResource(R.mipmap.map_arrow_up)
            } else {
                mBinding.vBottomCaservacInfo.visibility = View.VISIBLE
                mBinding.imgControlCaservac.setImageResource(R.mipmap.main_arrow_down)
            }
        }
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
            mModel.getCaservacDetail(params)

        }
        initViewModel()
        initViewEvent()
    }

    @SuppressLint("CheckResult")
    private fun initViewEvent() {

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

    override fun onDestroy() {
        super.onDestroy()
        permissions = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }
    private fun initViewModel() {
        mModel.caservacInfo.observe(this, Observer {
            bindData(it)
        })
    }

    /**
     * param data 车位详情对象
     */
    private fun bindData(data: CaservacBean?) {
        data?.let {
            mBinding.aidNumber = it.phone
            mBinding.level = it.level
            mBinding.type = it.type
            if (it.phone.isNullOrEmpty() && it.level.isNullOrEmpty() && it.type.isNullOrEmpty() && it.images.isNullOrEmpty()) {
                mBinding.imgControlCaservac.visibility = View.GONE
            } else {
                mBinding.imgControlCaservac.visibility = View.VISIBLE
            }
            mBinding.vBottomCaservacInfo.visibility = View.GONE
            mBinding.tvAidNumberValue.onNoDoubleClick {
                if (!it.phone.isNullOrEmpty()) {
                    SystemHelper.callPhone(context!!,it.phone!!)
                }
            }
            mBinding.bean = it
            if (!it.images.isNullOrEmpty()) {
                var temps = it.images!!.split(",")
                if (!temps.isNullOrEmpty()) {
                    mImageRecyAdapter.clear()
                    mImageRecyAdapter.add(temps as MutableList<String>)
                }
            }
            if (currentIndex == 0) {
                if (it.images.isNullOrEmpty()) {
                    mBinding.txtCaservacName.maxWidth =
                        resources.getDimension(R.dimen.dp_210).toInt()
                } else {
                    mBinding.txtCaservacName.maxWidth =
                        resources.getDimension(R.dimen.dp_164).toInt()
                }
                mBinding.txtCaservacTag.visibility = View.VISIBLE
            } else {
                mBinding.txtCaservacTag.visibility = View.GONE
            }
            if (!it?.phone.isNullOrEmpty() || !it?.images.isNullOrEmpty() ||
                !it?.type.isNullOrEmpty() || !it?.level.isNullOrEmpty()
            ) {
                mBinding.imgControlCaservac.visibility = View.VISIBLE
            } else {
                mBinding.imgControlCaservac.visibility = View.GONE
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun closePage(event: SideTourClosePageEvent) {
        //  关闭弹出页面
        if (event.type == ResourceType.CONTENT_TYPE_MEDICAL)
            try {
                mBinding.vBottomCaservacInfo?.let {
                    if (it.visibility == View.VISIBLE) {
                        it.visibility = View.GONE
                        mBinding.imgControlCaservac?.setImageResource(R.mipmap.main_arrow_up)
                    }
                }
            } catch (e: java.lang.Exception) {
            }

    }
}


class SideTourCaservacViewModel : BaseViewModel() {
    /**
     * 医疗点详情
     */
    var caservacInfo = MutableLiveData<CaservacBean>()

    /**
     * 获取停车位详情
     */
    fun getCaservacDetail(p: HashMap<String, String>) {
        mPresenter?.value?.loading = true
//        mPresenter?.value?.isNeedRecyleView = false
        MainRepository.service.getCaservacDetail(p)
            .excute(object : BaseObserver<CaservacBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<CaservacBean>) {
                    caservacInfo.postValue(response.data)
                }
            })
    }
}