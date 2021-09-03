package com.daqsoft.travelCultureModule.itrobot.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.LayoutScItRobotWindowBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.ItRobotGreeting
import com.daqsoft.provider.utils.MenuJumpUtils

/**
 * @Description
 * @ClassName   ItRobotWindowView
 * @Author      luoyi
 * @Time        2020/5/18 20:47
 */
class ScItRobotWindowView : FrameLayout {

    var mContext: Context? = null
    var binding: LayoutScItRobotWindowBinding? = null
    var onClickHideListener: OnClickHideListener? = null
    var isCanVisable:Boolean=false
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        initView()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.layout_sc_it_robot_window,
            null,
            false
        )
        addView(binding?.root)
        initViewEvent()
    }

    private fun initViewEvent() {
        binding?.imgHideRobot?.onNoDoubleClick {
            //            binding?.vRobotInfo?.visibility = View.GONE
//            binding?.imgHideRobot?.visibility = View.GONE
            onClickHideListener?.onClickHide()
//            binding?.imgShowRobot?.visibility = View.VISIBLE
        }
//        binding?.imgShowRobot?.onNoDoubleClick {
//            binding?.vRobotInfo?.visibility = View.VISIBLE
//            binding?.imgHideRobot?.visibility = View.VISIBLE
//            binding?.imgShowRobot?.visibility = View.GONE
//        }
        binding?.root?.onNoDoubleClick {
            MainARouterPath.toItRobotPage()
        }
    }

    fun setData(itRobotGreeting: ItRobotGreeting) {
        if (itRobotGreeting != null) {
            if (!itRobotGreeting.greetings.isNullOrEmpty()) {
                isCanVisable=true
                binding?.tvRobotTitle?.text = "" + itRobotGreeting.greetings
                if (itRobotGreeting.appUrl.isNullOrEmpty() && itRobotGreeting.resourceType.isNullOrEmpty() &&
                    itRobotGreeting.resourceId.isNullOrEmpty()
                ) {
                    binding?.imgRobotGo?.visibility = View.GONE
                } else {
                    binding?.imgRobotGo?.visibility = View.VISIBLE
                    binding?.imgRobotGo?.onNoDoubleClick {
                        var uuid = SPUtils.getInstance().getString(SPKey.UUID)
                        if (!itRobotGreeting.appUrl.isNullOrEmpty()) {
                            ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
                                .withString(
                                    "html",
                                    StringUtil.getDqUrl(itRobotGreeting.appUrl, uuid)
                                )
                                .navigation()
                        } else {
                            MenuJumpUtils.jumpResourceTypePage(
                                itRobotGreeting.resourceType,
                                itRobotGreeting.resourceId, ""
                            )
                        }
                    }
                }
            }
        }
    }

    interface OnClickHideListener {
        fun onClickHide()
    }

}