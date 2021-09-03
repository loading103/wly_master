package com.daqsoft.travelCultureModule.venuecollect.adapter
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemPlaygroundLsBinding
import com.daqsoft.mainmodule.databinding.MainItemCheliTopBinding
import com.daqsoft.mainmodule.databinding.MainItemHotActivityClassifyBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ActivityMethod
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.bean.ExhibitionTagBean
import com.daqsoft.provider.bean.LineTagBean
import com.daqsoft.provider.bean.PlayGroundBean
import com.daqsoft.provider.utils.AddressUtil
import com.daqsoft.travelCultureModule.itrobot.adapter.ItRobotKeyAdapter
import com.daqsoft.travelCultureModule.resource.adapter.ScenicSpotLiveAdapter
import com.jakewharton.rxbinding2.view.RxView
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.textColor
import java.lang.Exception
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit

class TopTagExbihitionAdapter : RecyclerViewAdapter<MainItemCheliTopBinding, ExhibitionTagBean> {

    var context: Context

    constructor(context: Context) : super(R.layout.main_item_cheli_top) {
        this.context = context
    }

    override fun setVariable(mBinding: MainItemCheliTopBinding, position: Int, item: ExhibitionTagBean) {
        mBinding.label.text = item.name
        mBinding.label.isSelected = item.select

        var d = RxView.clicks(mBinding.root)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                run {
                    if (item.select) {
                        return@run
                    }
                    if (onItemCLickListener != null) {
                        onItemCLickListener?.onItemClick(item, position)
                    }

                }
            }
    }




    interface OnItemCLickListener {
        fun onItemClick(item: ExhibitionTagBean,position: Int)
    }
    var onItemCLickListener :OnItemCLickListener ?= null

}