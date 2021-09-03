package com.daqsoft.servicemodule.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.android.scenic.servicemodule.R
import com.daqsoft.android.scenic.servicemodule.databinding.ServiceSosActBinding
import com.daqsoft.baselib.base.BaseApplication.Companion.context
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.SystemHelper
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.provider.view.BaseDialog
import com.daqsoft.servicemodule.model.ServiceSosModel
import com.tbruyelle.rxpermissions2.RxPermissions


@Route(path = ARouterPath.ServiceModule.SERVICE_SOS_ACTIVITY)
class ServiceSosActivity : TitleBarActivity<ServiceSosActBinding, ServiceSosModel>() {
    /**
     * 当前经纬度
     */
    var lat: String = ""
    var lon: String = ""
    var address: String? = ""

    var phoneDialog: BaseDialog? = null

    // rx权限对象
    private var permissions: RxPermissions? = null
    override fun getLayout(): Int {
        return R.layout.service_sos_act
    }

    override fun setTitle(): String {
        return resources.getString(R.string.service_sos)
    }

    override fun injectVm(): Class<ServiceSosModel> {
        return ServiceSosModel::class.java
    }

    override fun initView() {
        permissions = RxPermissions(this)
        mBinding.rvCall.setOnClickListener {
            mModel.contact_phone.observe(this, Observer {
                if (!it.isNullOrEmpty()) {
                    showCallPhoneTip(it)
                }
            })

        }
        mBinding.serviceSosUpload.setOnClickListener {
            var intent = Intent(this, ServiceSosDetailActivity::class.java)
            intent.putExtra("lat", lat)
            intent.putExtra("lon", lon)
            intent.putExtra("address", address)
            startActivity(intent)
            finish()
        }
    }

    override fun initData() {
        initMap()
        mModel.getSiteInfo()
    }

    @SuppressLint("CheckResult")
    private fun initMap() {
        if (
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            //未开启定位权限
            permissions?.request(Manifest.permission.ACCESS_FINE_LOCATION)!!.subscribe {
                if (it) {
                    doLocation()
                } else {
                    mModel?.toast.postValue("非常抱歉，正常授权才能使用地图模式")
                }
            }
        } else {
            doLocation()
        }


    }

    private fun doLocation() {
        GaoDeLocation.getInstance().init(this, object : GaoDeLocation.OnGetCurrentLocationLisner {

            override fun onResult(
                adCode: String?,
                result: String?,
                lat_: Double,
                lon_: Double,
                adcode: String?
            ) {
                mBinding.serviceSosLocation.text = result
                address = result
                lat = lat_.toString()
                lon = lon_.toString()
            }

            override fun onError(errormsg: String) {
            }
        })
    }

    fun showCallPhoneTip(phone: String) {
        if (phoneDialog == null) {
            phoneDialog = BaseDialog(this@ServiceSosActivity)
            phoneDialog!!.contentView(R.layout.call_phone_base_dialog)
                .layoutParams(
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                )
                .gravity(Gravity.CENTER)
                .animType(BaseDialog.AnimInType.BOTTOM)
                .canceledOnTouchOutside(false)
            phoneDialog!!.findViewById<TextView>(R.id.tv_title).text = "拨打电话"
            phoneDialog!!.findViewById<TextView>(R.id.tv_content).text = "是否立即拨打电话？"
            phoneDialog!!.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
                phoneDialog!!.dismiss()
            }
            phoneDialog!!.findViewById<TextView>(R.id.tv_query).setOnClickListener {
                phoneDialog!!.dismiss()
                SystemHelper.callPhone(context!!, phone)
            }
        }
        phoneDialog?.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        permissions = null
        phoneDialog = null
        GaoDeLocation.getInstance().release()
    }
}