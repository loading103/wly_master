package com.daqsoft.usermodule.ui.collection

import android.app.Activity
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.*
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.bean.MineFocusMassBean
import com.daqsoft.provider.databinding.LayoutDialogNoticeBinding
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.rxCommand.ReplyCommand
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityMineFocusBinding
import com.daqsoft.usermodule.databinding.ItemFocusMassBinding
import com.daqsoft.usermodule.ui.fragment.FocusActFragment
import com.daqsoft.usermodule.ui.fragment.FocusLegacyFragment
import com.daqsoft.usermodule.ui.fragment.FocusMassFragment

/**
 * @Description 关注页面
 * @ClassName   CollectionActivity
 * @Author      Huangxi
 * @Time        2020/2/28
 */
@Route(path = ARouterPath.UserModule.USER_FOCUS_ACTIVITY)
class MineFocusActivity : TitleBarActivity<ActivityMineFocusBinding, MineFocusViewModel>(),
    View.OnClickListener {
    /**
     * 当前页
     */
    var currpage: Int = 1

    var tabPos = 0

    private val focusPageAdapter: FocusPageAdapter by lazy {
        FocusPageAdapter(supportFragmentManager)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_focus_mass -> {
                // 活动
                changeSelectTab(0)
                mBinding.refreshFocus.currentItem = 0
            }
            R.id.tv_mas_trends -> {
                // 社团
                changeSelectTab(1)
                mBinding.refreshFocus.currentItem = 1
            }
            mBinding.heir.id ->{
                // 传承人
                changeSelectTab(2)
                mBinding.refreshFocus.currentItem = 2
            }
        }
    }

    private fun changeSelectTab(pos: Int) {
        when (pos) {
            0 -> {
                mBinding.tvFocusMass.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                mBinding.tvMasTrends.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                mBinding.tvFocusMass.isSelected = true
                mBinding.tvMasTrends.isSelected = false
                tabPos = pos

                mBinding.heir.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                mBinding.heir.isSelected = false

            }
            1 -> {
                mBinding.tvFocusMass.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                mBinding.tvMasTrends.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                mBinding.tvFocusMass.isSelected = false
                mBinding.tvMasTrends.isSelected = true
                tabPos = pos

                mBinding.heir.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                mBinding.heir.isSelected = false
            }
            2->{
                mBinding.heir.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                mBinding.heir.isSelected = true

                mBinding.tvFocusMass.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                mBinding.tvMasTrends.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                mBinding.tvFocusMass.isSelected = false
                mBinding.tvMasTrends.isSelected = false
                tabPos = pos
            }
        }

    }

    override fun getLayout(): Int = R.layout.activity_mine_focus

    override fun setTitle(): String = "关注"

    override fun injectVm(): Class<MineFocusViewModel> = MineFocusViewModel::class.java

    override fun initView() {
        focusPageAdapter.apply {
            fragments.clear()
            fragments.add(FocusActFragment())
            fragments.add(FocusMassFragment())
            fragments.add(FocusLegacyFragment())
        }

        mBinding.refreshFocus.apply {
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    changeSelectTab(position)
                }

            })
            adapter = focusPageAdapter
            currentItem = 0
            changeSelectTab(0)
        }
        mBinding.tvFocusMass.setOnClickListener(this)
        mBinding.tvMasTrends.setOnClickListener(this)

        mBinding.heir.setOnClickListener(this)
    }

    override fun initData() {
    }

    override fun notifyData() {
        super.notifyData()

    }


    /**
     * 弹出确认框
     * @param sureCommand 动作
     */
    fun noticeConfirm(context: Activity) {
        val dialog = AlertDialog.Builder(AppManager.instance.currentActivity()).create()
        dialog.show()
        val window = dialog.window
        val binding = DataBindingUtil.inflate<LayoutDialogNoticeBinding>(
            context.layoutInflater,
            R.layout.layout_dialog_notice, null, false
        )

        window!!.setContentView(binding.root)
        binding.label = ""
        binding.notice = "很抱歉，您累计失约{25}次，诚信分为350分，诚信等级较差，{2019.10.10-2019.10.16}期间，您无法预订活动。"
        binding.cancel = "知道了"
        binding.sure = "我的诚信"

        binding.cancelSubmit = object : ReplyCommand {
            override fun run() {
                dialog.dismiss()
            }
        }
        binding.sureSubmit = object : ReplyCommand {
            override fun run() {
                dialog.dismiss()
            }
        }
    }


}


/**
 * 关注页面的ViewModel
 */
class MineFocusViewModel : BaseViewModel() {

}