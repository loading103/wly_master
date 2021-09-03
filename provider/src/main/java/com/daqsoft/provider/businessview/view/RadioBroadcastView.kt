package com.daqsoft.provider.businessview.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.ContentVoBean
import com.daqsoft.provider.databinding.LayoutRadioBroadcastViewBinding
import com.daqsoft.thetravelcloudwithculture.home.bean.HomeContentBean
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

/**
 * @Description
 * @ClassName   RadioBroadcastView
 * @Author      luoyi
 * @Time        2020/10/20 9:49
 */
class RadioBroadcastView : FrameLayout {
    var mContext: Context? = null
    var mBinding: LayoutRadioBroadcastViewBinding? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.mContext = context
        initView()
    }

    private fun initView() {
        mBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.layout_radio_broadcast_view,
            this,
            false
        )
        addView(mBinding?.root)
    }

    fun setData(datas: MutableList<HomeContentBean>) {
        mBinding?.vfRadioBradcast?.removeAllViews()
        if (!datas.isNullOrEmpty()) {
            datas.forEach { bean ->
                var view =
                    LayoutInflater.from(mContext!!).inflate(R.layout.item_radio_broadcast_txt, null)
                var tvName = view.findViewById<TextView>(R.id.tv_radio_broad_cast)
                tvName.text = "" + bean.title
                mBinding?.vfRadioBradcast?.addView(view)
                RxView.clicks(tvName)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        if (bean.contentType.equals("IMAGE")) {
                            ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_IMG)
                                .withString("id", bean.id.toString())
                                .navigation()
                        } else {
                            ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_INFO)
                                .withString("id", bean.id.toString())
                                .withString("contentTitle", "资讯详情")
                                .navigation()
                        }
                    }
            }
            if (datas.size > 1) {
                mBinding?.vfRadioBradcast?.setFlipInterval(3000)
                mBinding?.vfRadioBradcast?.isAutoStart = true
            }
        }

    }

}