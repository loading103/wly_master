package com.daqsoft.legacyModule.media

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.databinding.LegacyModuleActivityMediaBinding
import com.daqsoft.legacyModule.media.fgt.LegacyMediaAudioFragment
import com.daqsoft.legacyModule.media.fgt.LegacyMediaVideoFragment
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.event.UpdateAudioPlayerEvent
import com.daqsoft.provider.event.UpdateScenicVideoPlayerEvent
import org.greenrobot.eventbus.EventBus

/**
 * @des     视听非遗 Activity
 * @class   LegacyMediaActivity
 * @author  Wongxd
 * @date    2020-4-21 15:22
 */
@Route(path = ARouterPath.LegacyModule.LEGACY_MEDIA_ACTIVITY)
internal class LegacyMediaActivity : TitleBarActivity<LegacyModuleActivityMediaBinding, LegacyMediaViewModel>() {

    override fun getLayout(): Int = R.layout.legacy_module_activity_media

    override fun setTitle(): String = getString(R.string.legacy_module_media)

    override fun injectVm(): Class<LegacyMediaViewModel> = LegacyMediaViewModel::class.java


    val legacyMediaVideoFragment = LegacyMediaVideoFragment()
    private var mPos = 0

    override fun initView() {

        mBinding.vp.apply {
            adapter = VpAdapter(listOf(legacyMediaVideoFragment, LegacyMediaAudioFragment()))
            currentItem = mPos
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                    mPos = position
                    refreshTabStatus()
                    EventBus.getDefault().post(UpdateScenicVideoPlayerEvent(1))
                    EventBus.getDefault().post(UpdateAudioPlayerEvent(1))
                }
            })
        }

        initTab()
    }

    private fun refreshTabStatus() {

        mBinding.vp.currentItem = mPos

        if (mPos == 0) {
            mBinding.tvVideo.apply {
                setTextColor(resources.getColor(R.color.legacy_module_34ac9e))
                textSize = 16f
            }
            mBinding.vVideo.visibility = View.VISIBLE
            mBinding.tvAudio.apply {
                setTextColor(resources.getColor(R.color.color_666))
                textSize = 14f
            }
            mBinding.vAudio.visibility = View.INVISIBLE

        } else {
            mBinding.tvAudio.apply {
                setTextColor(resources.getColor(R.color.legacy_module_34ac9e))
                textSize = 16f
            }
            mBinding.vAudio.visibility = View.VISIBLE

            mBinding.tvVideo.apply {
                setTextColor(resources.getColor(R.color.color_666))
                textSize = 14f
            }
            mBinding.vVideo.visibility = View.INVISIBLE
        }
    }

    private fun initTab() {


        mBinding.llVideo.onNoDoubleClick {
            mPos = 0
            refreshTabStatus()
        }

        mBinding.llAudio.onNoDoubleClick {
            mPos = 1
            refreshTabStatus()
        }

        refreshTabStatus()
    }


    override fun initData() {

    }


    inner class VpAdapter(val fgts: List<Fragment>) : FragmentStatePagerAdapter(supportFragmentManager) {
        override fun getItem(position: Int): Fragment = fgts[position]

        override fun getCount(): Int = fgts.size

    }

    override fun onBackPressed() {
        if(!legacyMediaVideoFragment.backPress())
          super.onBackPressed()
    }
}