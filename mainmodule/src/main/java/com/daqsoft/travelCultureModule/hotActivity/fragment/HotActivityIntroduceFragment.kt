package com.daqsoft.travelCultureModule.hotActivity.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.utils.*
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.MainActivityIntroduceFragmentBinding
import com.daqsoft.mainmodule.databinding.MainItemHotActivityTagBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.bean.HotActivityDetailBean
import com.daqsoft.provider.utils.AddressUtil
import com.daqsoft.provider.utils.MapNaviUtils
import com.jakewharton.rxbinding2.view.RxView
import java.lang.Exception
import java.util.concurrent.TimeUnit

/**
 * @des 首页第一部分菜单
 * @author PuHua
 * @Date 2019/12/5 17:52
 */
class HotActivityIntroduceFragment :
    BaseFragment<MainActivityIntroduceFragmentBinding,
            BaseViewModel>() {

    companion object {
        const val HOTACITIVTY = "HOT_ACTIVITY"
        fun newInstance(hotActivity: HotActivityDetailBean): HotActivityIntroduceFragment {
            var frag = HotActivityIntroduceFragment()
            var bundle = Bundle()
            bundle.putParcelable(HOTACITIVTY, hotActivity)
            frag.arguments = bundle
            return frag
        }
    }


    private var mHotActivity: HotActivityDetailBean? = null

    override fun getLayout(): Int = R.layout.main_activity_introduce_fragment

    override fun initData() {}

    override fun initView() {
        try {
            mHotActivity = arguments?.getParcelable(HOTACITIVTY)
        } catch (e: Exception) {

        }
        mBinding.rlHonesty.onNoDoubleClick {
            // 跳转我的诚信
            if (AppUtils.isLogin()) {
                ARouter.getInstance()
                    .build(ARouterPath.UserModule.USER_CREDIT_ACTIVITY)
                    .navigation()
            } else {
                ToastUtils.showUnLoginMsg()
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
        // 名称
        mBinding.name = mHotActivity?.name

        // 显示价格/积分/免费
        // 根据类型判断是否显示积分/价格
        if (mHotActivity?.type == ActivityType.ACTIVITY_TYPE_RESERVE || mHotActivity?.type == ActivityType.ACTIVITY_TYPE_ENROLL) {
            // 预订活动||报名活动
            if (mHotActivity?.integral == "0") {
                if (mHotActivity!!.type == ActivityType.ACTIVITY_TYPE_RESERVE) {
                    mBinding.price = getString(R.string.order_free_reserve)
                } else {
                    mBinding.price = getString(R.string.order_free)
                }
                mBinding.tvPrice.visibility = View.VISIBLE
                mBinding.tvIntegral.visibility = View.GONE
            } else {
                mBinding.price = mHotActivity?.integral
                mBinding.tvIntegral.visibility = View.VISIBLE
                mBinding.tvPrice.visibility = View.VISIBLE
            }
            // 剩余
            var left = getString(R.string.home_activity_rest_numbers, mHotActivity?.stock)
            mBinding.left = left
            mBinding.ivTagImage.visibility = View.VISIBLE
            mBinding.tvLeftNumber.visibility = View.VISIBLE
            if (mHotActivity?.type == ActivityType.ACTIVITY_TYPE_RESERVE || mHotActivity?.type == ActivityType.ACTIVITY_TYPE_ENROLL) {
                // 预订活动显示报名时间 测试加的
                if (!mHotActivity?.signStartTime.isNullOrEmpty() && !mHotActivity?.signEndTime.isNullOrEmpty()) {
                    mBinding.tvBmTimeLabel.visibility = View.VISIBLE
                    mBinding.tvBmTime.visibility = View.VISIBLE
                    mBinding.bmtime = DateUtil.getTwoDateStrs(mHotActivity?.signStartTime, mHotActivity?.signEndTime)
                }
            } else {
                mBinding.tvBmTimeLabel.visibility = View.GONE
                mBinding.tvBmTime.visibility = View.GONE
            }

        } else if (mHotActivity?.type == ActivityType.ACTIVITY_TYPE_PLAIN) {
            // 普通活动
            mBinding.tvPrice.visibility = View.GONE
            mBinding.tvIntegral.visibility = View.GONE
            mBinding.ivTagImage.visibility = View.GONE
            mBinding.tvLeftNumber.visibility = View.GONE
        }

        var tags: String = ""
        if (!mHotActivity?.classifyName.isNullOrEmpty()) {
            tags = mHotActivity!!.classifyName
        }
        if (!mHotActivity?.remark.isNullOrEmpty()) {
            if (tags != null) {
                tags = "$tags · "
            }
            tags += mHotActivity?.remark
        }
        if (!tags.isNullOrEmpty()) {
            mBinding.tvTags.visibility = View.VISIBLE
            mBinding.tvTags.text = tags
        } else {
            mBinding.tvTags.visibility = View.GONE
        }

        // 开启诚信优享
        if (mHotActivity?.faithUseStatus == "1") {
            mBinding.rlHonesty.visibility = View.VISIBLE
            mBinding.tvHonesty.text = getString(R.string.honeysty_integrity_score, mHotActivity?.faithUseValue)
        } else {
            mBinding.rlHonesty.visibility = View.GONE
        }

        // 副标题 1.5新加
        if (!mHotActivity?.subhead.isNullOrEmpty()) {
            mBinding.tvSubhead.visibility = View.VISIBLE
            mBinding.tvSubhead.text = mHotActivity?.subhead
        } else {
            mBinding.tvSubhead.visibility = View.GONE
        }
        // 可参与人数 1.5新加  xxxxx 2020/6/12 测试-颜彬 让屏蔽
        if (!mHotActivity?.totalStock.isNullOrEmpty() && mHotActivity?.totalStock != "0") {
            mBinding.tvCanInPersonNumLabel.visibility = View.VISIBLE
            mBinding.tvCanInPersonNum.visibility = View.VISIBLE
            mBinding.tvCanInPersonNum.text = mHotActivity?.totalStock + "人"
        } else {
            mBinding.tvCanInPersonNumLabel.visibility = View.GONE
            mBinding.tvCanInPersonNum.visibility = View.GONE
        }

        // 时间
        var startTime = mHotActivity?.useStartTime
        var endTime = mHotActivity?.useEndTime
        if (!startTime.isNullOrEmpty() && !endTime.isNullOrEmpty()) {
            mBinding.time = DateUtil.getTwoDateStrs(startTime, endTime)
            mBinding.tvTimeLabel.visibility = View.VISIBLE
            mBinding.tvTime.visibility = View.VISIBLE
        } else {
            mBinding.tvTimeLabel.visibility = View.GONE
            mBinding.tvTime.visibility = View.GONE
        }


        // 电话
        mBinding.phone = mHotActivity?.phone

        if (mHotActivity?.phone.isNullOrEmpty()) {
            mBinding.tvPhone.visibility = View.GONE
            mBinding.tvPhoneLabel.visibility = View.GONE
        } else {
            RxView.clicks(mBinding.tvPhone)
                .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                    SystemHelper.callPhone(context!!,mHotActivity!!.phone)
                }
        }
        if (mHotActivity?.address.isNullOrEmpty()) {
            mBinding.tvAddress.visibility = View.GONE
            mBinding.tvAddressLabel.visibility = View.GONE
        } else {
            // 地址

            var address: String? = mHotActivity?.address
            val lat = SPUtils.getInstance().getString(SPUtils.Config.LATITUDE)
            val lng = SPUtils.getInstance().getString(SPUtils.Config.LONGITUDE)
            if (!mHotActivity?.address.isNullOrEmpty()) {
                if (!mHotActivity?.latitude.isNullOrEmpty() && !mHotActivity?.longitude.isNullOrEmpty()&&!lat.isNullOrEmpty()&&!lng.isNullOrEmpty()) {
                    address = "$address,距您" + AddressUtil.getLocationDistanceEn(
                        LatLng(mHotActivity?.latitude!!.toDouble(), mHotActivity?.longitude!!.toDouble()),
                        LatLng(lat.toDouble(), lng.toDouble())
                    )
                }
            }
            if (!address.isNullOrEmpty()) {
                mBinding.address = mHotActivity?.address
                mBinding.tvAddress.text = address
                mBinding.tvAddressLabel.visibility = View.VISIBLE
                mBinding.tvAddress.visibility = View.VISIBLE
            } else {
                mBinding.tvAddressLabel.visibility = View.GONE
                mBinding.tvAddress.visibility = View.GONE
            }
            RxView.clicks(mBinding.tvAddress)
                .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                    if (mHotActivity != null && !mHotActivity?.latitude.isNullOrEmpty() && !mHotActivity?.longitude.isNullOrEmpty()) {
                        if (MapNaviUtils.isGdMapInstalled()) {
                            MapNaviUtils.openGaoDeNavi(
                                context, 0.0, 0.0, null,
                                mHotActivity!!.latitude!!.toDouble(), mHotActivity!!.longitude!!.toDouble(),
                                mHotActivity?.address
                            )
                        } else {
                            ToastUtils.showMessage("非常抱歉，系统未安装地图软件")
                        }
                    } else {
                        ToastUtils.showMessage("非常抱歉，暂无位置信息")
                    }
                }
            mBinding?.rlHonesty.onNoDoubleClick {
                if (AppUtils.isLogin()) {
                    ARouter.getInstance()
                        .build(ARouterPath.UserModule.USER_CREDIT_ACTIVITY)
                        .navigation()
                } else {
                    ToastUtils.showUnLoginMsg()
                    ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                        .navigation()
                }
            }
        }

    }

    /**
     * 标签适配器
     */
    val tagAdapter = object :
        RecyclerViewAdapter<MainItemHotActivityTagBinding, String>(
            R.layout.main_item_hot_activity_tag
        ) {
        override fun setVariable(mBinding: MainItemHotActivityTagBinding, position: Int, item: String) {
            mBinding.name = item
        }

    }

    override fun injectVm(): Class<BaseViewModel> = BaseViewModel::class.java

}
