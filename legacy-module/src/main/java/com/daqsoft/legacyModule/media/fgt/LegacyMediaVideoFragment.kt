package com.daqsoft.legacyModule.media.fgt

import android.content.Context
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.databinding.LegacyModuleFragmentMediaBinding
import com.daqsoft.legacyModule.media.adapter.LegacyMediaVideoAdapter
import com.daqsoft.legacyModule.media.LegacyMediaViewModel
import com.daqsoft.provider.event.UpdateScenicVideoPlayerEvent
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @des     非遗视频
 * @class   LegacyMediaVideoFragment
 * @author  Wongxd
 * @date    2020-4-21 17:45
 */
internal class LegacyMediaVideoFragment : BaseFragment<LegacyModuleFragmentMediaBinding, LegacyMediaViewModel>() {

    override fun getLayout(): Int = R.layout.legacy_module_fragment_media

    override fun injectVm(): Class<LegacyMediaViewModel> = LegacyMediaViewModel::class.java

    private val mMediaType = "video"

    private val videoAdapter by lazy { LegacyMediaVideoAdapter() }

    override fun initView() {


        mBinding.srl.setOnRefreshListener {
            mBinding.srl.finishRefresh()
            refreshData()
        }

        mBinding.srl.setOnLoadMoreListener {
            mModel.pageManager.nexPageIndex
            mModel.getMediaList(mMediaType)
            mBinding.srl.finishLoadMore()
        }


        mBinding.rv.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = videoAdapter
        }


        initViewModel()
    }

    private fun initViewModel() {
        mModel.mediaList.observe(this, Observer { list ->


            mBinding.rv.visibility = View.VISIBLE

            if (list.isNullOrEmpty()) return@Observer

                if (mModel.pageManager.isFirstIndex)
                    videoAdapter.clearNotify()

                videoAdapter.add(list)

        })
    }

    private fun refreshData() {
        mModel.pageManager.initPageIndex()
        mModel.getMediaList(mMediaType)
    }

    override fun initData() {
        refreshData()
    }

    override fun onAttach(context: Context) {
        EventBus.getDefault().register(this)
        super.onAttach(context)
    }

    override fun onDestroyView() {
        JCVideoPlayer.releaseAllVideos()
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updatePlayerEvent(event: UpdateScenicVideoPlayerEvent) {
        when (event.type) {
            1 -> {
                // 暂停
                videoAdapter.pausePlayer()
            }
        }
    }

    fun backPress():Boolean{
        return videoAdapter.exitFullScreen()
    }
}