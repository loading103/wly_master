package com.daqsoft.legacyModule.media.adapter

import android.view.View
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.BindingViewHolder
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.databinding.LegacyModuleItemMediaVideoBinding
import com.daqsoft.legacyModule.media.bean.LegacyMediaListBean
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.base.ResourceType
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerDqUI
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard


/**
 * @des    视听非遗  视频 adapter
 * @class  LegacyMediaVideoAdapter
 * @author Wongxd
 * @date   2020-4-21 14:22
 *
 */
internal class LegacyMediaVideoAdapter : RecyclerViewAdapter<LegacyModuleItemMediaVideoBinding, LegacyMediaListBean>
    (R.layout.legacy_module_item_media_video) {


    private var jcVideo: JCVideoPlayerDqUI? = null
    override fun setVariable(mBinding: LegacyModuleItemMediaVideoBinding, position: Int, item: LegacyMediaListBean) {
        mBinding.item = item


        mBinding.tvTag.onNoDoubleClick {
            var path = ""
            when(item.resourceType){
                ResourceType.CONTENT_TYPE_HERITAGE_PROTECT_BASE,
                ResourceType.CONTENT_TYPE_HERITAGE_TEACHING_BASE,
                ResourceType.CONTENT_TYPE_HERITAGE_EXPERIENCE_BASE ->{
                    path = ARouterPath.LegacyModule.LEGACY_EXPERIENCE_DETAIL_ACTIVITY
                }
                ResourceType.CONTENT_TYPE_HERITAGE_ITEM ->{
                    path = ARouterPath.LegacyModule.LEGACY_Smrity_DETAIL_ACTIVITY
                }
                ResourceType.CONTENT_TYPE_HERITAGE_PEOPLE ->{
                    path = ARouterPath.LegacyModule.LEGACY_PEOPLE_DETAIL_ACTIVITY
                }
            }
            ARouter.getInstance().build(path)
                .withString("id", item.resourceId)
                .withString("type",item.resourceType)
                .navigation()
        }

        val imgUrl = item.getVideoInfo().cover
        val videoUrl = item.getVideoInfo().url
        jcVideo = mBinding.jcVideo

        jcVideo?.let { jcVideo ->

            jcVideo.mStateChangeListener = JCVideoPlayerDqUI.StateChangeListener { state ->
                mBinding.tvTime.isVisible = state
            }

            jcVideo.backButton.visibility = View.GONE
            if (videoUrl.isNotBlank()) {
                jcVideo.setUp(videoUrl, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "")
                Glide.with(mBinding.root).load(imgUrl).centerCrop().into(jcVideo.thumbImageView)
                jcVideo.totalTimeTextView.text = item.getVideoInfo().time
                val layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    Utils.dip2px(mBinding.root.context, 40f).toInt()
                )
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                layoutParams.bottomMargin = mBinding.root.context.resources.getDimensionPixelSize(R.dimen.dp_27)
                jcVideo.layoutBottom.layoutParams = layoutParams
            }
        }
    }


    override fun onViewRecycled(holder: BindingViewHolder<*>) {
        super.onViewRecycled(holder)
        try {
            jcVideo?.release()
        } catch (e: Exception) {
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        try {
            jcVideo?.release()
        } catch (e: Exception) {
        }
    }

    override fun onViewDetachedFromWindow(holder: BindingViewHolder<*>) {
        super.onViewDetachedFromWindow(holder)
        try {
            jcVideo?.release()
        } catch (e: Exception) {
        }
    }

    /**
     * 暂停播放
     */
    fun pausePlayer(){
        try {
            jcVideo?.pasuePlayer()
        } catch (e: Exception) {
        }
    }

    /**
     * 退出全屏
     */
    fun exitFullScreen():Boolean{
        if(jcVideo?.currentState == JCVideoPlayerStandard.SCREEN_WINDOW_FULLSCREEN){
            JCVideoPlayer.backPress()
            return true
        }
        return false
    }
}