package com.daqsoft.travelCultureModule.hotActivity.fragment

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.MainItemHotActivityTagBinding
import com.daqsoft.mainmodule.databinding.MainItemVolunteerAvatarBinding
import com.daqsoft.mainmodule.databinding.MainVolunteerIntroduceFragmentBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.HotActivityDetailBean
import com.daqsoft.provider.bean.SignUser
import com.daqsoft.provider.mapview.impl.GaoDeMapManager
import com.daqsoft.provider.utils.MapNaviUtils
import java.text.DecimalFormat


/**
 * @des 志愿活动的详情部分
 * @author PuHua
 * @Date 2019/12/5 17:52
 */
class VolunteerIntroduceFragment(hotActivity: HotActivityDetailBean) :
    BaseFragment<MainVolunteerIntroduceFragmentBinding,
            BaseViewModel>() {

    private val mHotActivity = hotActivity

    override fun getLayout(): Int = R.layout.main_volunteer_introduce_fragment

    override fun initData() {


    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        // 名称
        mBinding.name = mHotActivity.name
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
        // 开启诚信优享
        if (mHotActivity.faithAuditStatus == "1") {
            mBinding.rlHonesty.visibility = View.VISIBLE
            mBinding.tvHonesty.text = getString(R.string.home_honesty, mHotActivity.faithAuditValue)
        } else {
            mBinding.rlHonesty.visibility = View.GONE
        }
        // 剩余
//        var left =getString(R.string.home_activity_rest_numbers, mHotActivity.stock)


        // 标签
        val tagLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvTags.layoutManager = tagLayoutManager
        tagAdapter.emptyViewShow = false
        mBinding.rvTags.adapter = tagAdapter
        val tags = mHotActivity.tagNames.split(",")
        var datas: MutableList<String> = mutableListOf()
        if (!tags.isNullOrEmpty()) {
            for (item in tags) {
                if (!item.isNullOrEmpty()) {
                    datas.add(item)
                }
            }
        }
        if (datas.isNullOrEmpty()) {
            mBinding.rvTags.visibility = View.GONE
        } else {
            tagAdapter.add(datas)
        }

        // 招募时间
        if (!mHotActivity.signStartTime.isNullOrEmpty() && !mHotActivity.signEndTime.isNullOrEmpty()) {
            var signStartTime = mHotActivity.signStartTime.substring(0, mHotActivity.signStartTime.length - 3)
            var signEndTime = mHotActivity.signEndTime.substring(0, mHotActivity.signEndTime.length - 3)
            mBinding.recruitTime = getString(R.string.order_activity_room_time_stamp_, signStartTime, signEndTime)
            mBinding?.tvTimeLabel.visibility = View.VISIBLE
            mBinding?.tvTime.visibility = View.VISIBLE
        } else {
            mBinding?.tvTimeLabel.visibility = View.GONE
            mBinding?.tvTime.visibility = View.GONE
        }
        if (!mHotActivity.useStartTime.isNullOrEmpty() && !mHotActivity.useEndTime.isNullOrEmpty()) {
            // 服务时间
            var startTime = mHotActivity.useStartTime.substring(0, mHotActivity.useStartTime.length - 3)
            var endTime = mHotActivity.useEndTime.substring(0, mHotActivity.useEndTime.length - 3)
            mBinding.time = getString(R.string.order_activity_room_time_stamp_, startTime, endTime)
            mBinding.tvServiceTimeLabel.visibility = View.VISIBLE
            mBinding.tvServiceTime.visibility = View.VISIBLE
        } else {
            mBinding.tvServiceTimeLabel.visibility = View.GONE
            mBinding.tvServiceTime.visibility = View.GONE
        }
        if (!mHotActivity.stock.isNullOrEmpty() || !mHotActivity.alreadySignCount.isNullOrEmpty() || !mHotActivity.signCount.isNullOrEmpty()
            || !mHotActivity.serviceTime.isNullOrEmpty()
        ) {
            mBinding.stock = mHotActivity.stock
            mBinding.alreadySignCount = mHotActivity.alreadySignCount
            mBinding.signCount = mHotActivity.signCount
            mBinding.serviceTime = mHotActivity.serviceTime
            mBinding.clRecruit.visibility = View.VISIBLE
        } else {
            mBinding.clRecruit.visibility = View.GONE
        }

        // 地址
        if (!mHotActivity.address.isNullOrEmpty()) {
            mBinding?.tvAddressLabel.visibility = View.VISIBLE
            mBinding?.tvAddress.visibility = View.VISIBLE
            mBinding?.address = mHotActivity.address
            mBinding?.tvAddress.onNoDoubleClick {
                if (mHotActivity?.latitude.isNullOrEmpty() && mHotActivity?.longitude.isNullOrEmpty()) {
                    if (MapNaviUtils.isGdMapInstalled()) {
                        MapNaviUtils.openGaoDeNavi(
                            context, 0.0, 0.0, null,
                            mHotActivity!!.latitude.toDouble(), mHotActivity!!.longitude.toDouble(),
                            mHotActivity?.address
                        )
                    } else {
                        mModel.toast.postValue("非常抱歉，系统未安装地图软件")
                    }
                } else {
                    mModel.toast.postValue("非常抱歉，暂无位置信息")
                }
            }
        } else {
            mBinding?.tvAddress.visibility = View.GONE
            mBinding?.tvAddressLabel.visibility = View.GONE
        }
        mBinding.address = mHotActivity.address
        mBinding.pbProgress.max = mHotActivity.totalStock.toInt()

        mBinding.pbProgress.progress = mHotActivity.alreadySignCount.toInt()

        val vto = mBinding.pbProgress.viewTreeObserver
        var isInitView: Boolean = false
        vto.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                if (!isInitView) {
                    val width = mBinding.pbProgress.measuredWidth.toFloat()
                    val txW = mBinding.tvProgress.measuredWidth
                    val dis = (width - txW) * mBinding.pbProgress.progress / mBinding.pbProgress.max
                    mBinding.tvProgress.x = mBinding.pbProgress.x + dis
                    isInitView = true
                } else {
                    vto.removeOnPreDrawListener(this)
                }
                return true
            }
        })
        val df = DecimalFormat("0.00")

        mBinding.tvProgress.text = df.format(mBinding.pbProgress.progress * 100.2f / mBinding.pbProgress.max) + "%"

        // 已招募人员
        if (mHotActivity.signUser.isNullOrEmpty()) {
            mBinding.tvMembers.visibility = View.GONE
            mBinding.rvMembers.visibility = View.GONE
        } else {
            mBinding.tvMembers.visibility = View.VISIBLE
            mBinding.rvMembers.visibility = View.VISIBLE
        }
        val avatarLayout = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvMembers.layoutManager = avatarLayout
        mBinding.rvMembers.adapter = avatarAdapter
        avatarAdapter.add(mHotActivity.signUser as MutableList<SignUser>)

        // 活动场地
        //
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
    /**
     * 招募人头像适配器
     */
    val avatarAdapter = object :
        RecyclerViewAdapter<MainItemVolunteerAvatarBinding, SignUser>(
            R.layout.main_item_volunteer_avatar
        ) {
        override fun setVariable(mBinding: MainItemVolunteerAvatarBinding, position: Int, item: SignUser) {
            mBinding.url = item.headUrl
        }

    }

    override fun injectVm(): Class<BaseViewModel> = BaseViewModel::class.java

}
