package com.daqsoft.travelCultureModule.sidetour.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.utils.SystemHelper
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemSideTourToilentBinding
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.MapResBean
import com.daqsoft.provider.bean.ToilentBean
import com.daqsoft.provider.utils.MapNaviUtils
import com.daqsoft.travelCultureModule.sidetour.SideTourClosePageEvent
import com.daqsoft.travelCultureModule.sidetour.adapter.ImageRecyAdapter
import com.daqsoft.travelCultureModule.sidetour.viewmodel.SideTourToilentViewModel
import com.jakewharton.rxbinding2.view.RxView
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.item_side_tour_toilent.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

/**
 * @Description
 * @ClassName   SideTourFragment
 * @Author      luoyi
 * @Time        2020/3/19 10:52
 */
class SideTourToilentFragment() :
    BaseFragment<ItemSideTourToilentBinding, SideTourToilentViewModel>() {

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
     * 是否从享服务进来
     */
    var isService = false
    /**
     *
     */
    private var permissions: RxPermissions? = null
    private val mImageRecyAdapter: ImageRecyAdapter by lazy {
        ImageRecyAdapter(context!!).apply {
            emptyViewShow = false
        }
    }

    companion object {
        fun newInstance(
            bean: MapResBean,
            index: Int,
            total: Int,
            lat: String,
            lng: String,
            isService: Boolean = false
        ): SideTourToilentFragment {
            var frag = SideTourToilentFragment()
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

    override fun getLayout(): Int = R.layout.item_side_tour_toilent

    override fun injectVm(): Class<SideTourToilentViewModel> {
        return SideTourToilentViewModel::class.java
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 注册eventBus
        EventBus.getDefault().register(this)
        permissions = RxPermissions(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun initView() {
        getPageParams()
        mBinding?.vItemSideTourOther.recyParkImages.layoutManager =
            GridLayoutManager(context, 3)
        mBinding?.vItemSideTourOther.recyParkImages.adapter = mImageRecyAdapter
        bean?.let {
            // 初始化数据
            mBinding.txtToilentTag.visibility = View.VISIBLE

            mBinding.name = it?.name
            mBinding.address = it?.address
            mBinding.currentIndex = "${currentIndex + 1}"
            mBinding.totalSize = "/$totalSize"
            val disStr = if (isService) "您:" else "该场所"
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
                mBinding.distance = "距离${disStr}:暂无," + it?.address
            }
            var params: HashMap<String, String> = HashMap()
            params["id"] = it.id.toString()
            params["lat"] = lat
            params["lng"] = lng
            mModel.getToilentDetail(params)

        }
        initViewModel()
        initViewEvent()
    }


    @SuppressLint("CheckResult")
    private fun initViewEvent() {
        mBinding?.vControlToilentMoreInfo.setOnClickListener {
            if (v_item_side_tour_other.visibility == View.VISIBLE) {
                v_item_side_tour_other.visibility = View.GONE
                mBinding.imgControlToilent.setImageResource(R.mipmap.main_arrow_up)
            } else {
                v_item_side_tour_other.visibility = View.VISIBLE
                mBinding.imgControlToilent.setImageResource(R.mipmap.main_arrow_down)
            }
        }
        RxView.clicks(mBinding.vItemSideTourOther.txtToilentContactUsValue)
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                val phoneNum = mBinding.vItemSideTourOther.txtToilentContactUsValue.text
                if (!phoneNum.toString().isNullOrEmpty()) {
                    SystemHelper.callPhone(context!!,phoneNum.toString())
                }
            }

        RxView.clicks(mBinding.vToilentToNav)
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
        mModel.toilentInfo.observe(this, Observer {
            if (it != null) {
                if (currentIndex == 0) {
                    mBinding.txtToilentName.maxWidth = if (it.images.isNullOrEmpty()) {
                        resources.getDimension(R.dimen.dp_210).toInt()
                    } else {
                        resources.getDimension(R.dimen.dp_164).toInt()
                    }
                    mBinding.txtToilentTag.visibility = View.VISIBLE
                } else {
                    mBinding.txtToilentTag.visibility = View.GONE
                }
                if (it.manNum.isNullOrEmpty() || it.manNum.isBlank()) {
                    mBinding?.vControlToilentMoreInfo.visibility = View.GONE
                } else {
                    mBinding?.vControlToilentMoreInfo.visibility = View.VISIBLE
                    bindData(it)
                }
            } else {
                mBinding?.vControlToilentMoreInfo.visibility = View.GONE
            }
        })
    }

    /**
     * param data 厕所详情对象
     */
    private fun bindData(data: ToilentBean) {
        mBinding?.vItemSideTourOther.toilent = data
        mBinding?.toilent = data
        if (!data.type.isNullOrEmpty() || !data.scale.isNullOrEmpty() || !data.disableNum.isNullOrEmpty()
            || !data.womanNum.isNullOrEmpty() || !data.manNum.isNullOrEmpty()
            || !data.charges.isNullOrEmpty() || !data.images.isNullOrEmpty()
        ) {
            mBinding.vControlToilentMoreInfo.visibility = View.VISIBLE
        } else {
            mBinding.vControlToilentMoreInfo.visibility = View.GONE
        }
        if (!data.scale.isNullOrEmpty()) {
            mBinding.vItemSideTourOther.toilent!!.scale = data.scale + "㎡"
        }
        if (!data.disableNum.isNullOrEmpty()) {
            mBinding.vItemSideTourOther.lvToilentSpecial.visibility = View.GONE
        } else {
            mBinding.vItemSideTourOther.lvToilentSpecial.visibility = View.VISIBLE
            if (data.disableNum.isNullOrEmpty()) {
                mBinding.vItemSideTourOther.vToilentDisable.visibility = View.GONE
            } else if (data.infantNum.isNullOrEmpty()) {
                mBinding.vItemSideTourOther.vToilentBaby.visibility = View.GONE
            }
        }
        if (data.womanNum.isNullOrEmpty() && data.manNum.isNullOrEmpty()) {
            mBinding.vItemSideTourOther.lvToilentNormal.visibility = View.GONE
        } else {
            mBinding.vItemSideTourOther.lvToilentNormal.visibility = View.VISIBLE
            if (data.womanNum.isNullOrEmpty()) {
                mBinding.vItemSideTourOther.vToilentWoman.visibility = View.GONE
            } else if (data.manNum.isNullOrEmpty()) {
                mBinding.vItemSideTourOther.vToilentMan.visibility = View.GONE
            }
        }
        if (!data.images.isNullOrEmpty()) {
            var temps = data.images.split(",")
            if (!temps.isNullOrEmpty()) {
                mImageRecyAdapter.clear()
                mImageRecyAdapter.add(temps as MutableList<String>)
            }
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
        if (event.type == ResourceType.CONTENT_TYPE_TOILET) {
            try {
                v_item_side_tour_other?.let {
                    if (it.visibility == View.VISIBLE) {
                        it.visibility = View.GONE
                        mBinding.imgControlToilent.setImageResource(R.mipmap.main_arrow_up)
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            EventBus.getDefault().unregister(this)
        } catch (e: Exception) {
        }

    }
}