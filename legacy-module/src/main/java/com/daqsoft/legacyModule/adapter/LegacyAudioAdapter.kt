package com.daqsoft.legacyModule.adapter

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.databinding.ItemMediaAudioBinding
import com.daqsoft.legacyModule.media.bean.LegacyAudioBean
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.base.ResourceType

/**
 *@package:com.daqsoft.legacyModule.adapter
 *@date:2020/5/25 17:14
 *@author: caihj
 *@des:TODO
 **/
class LegacyAudioAdapter : RecyclerViewAdapter<ItemMediaAudioBinding, LegacyAudioBean> {
    var playingPos: Int = -1
    var mContext: Context? = null
    /**
     *  缓存动画执行对象
     */
    var mCacheAnimator: HashMap<Int, ObjectAnimator> = hashMapOf()

    var listener: MeidaPlayerListener? = null

    constructor(context: Context) : super(R.layout.item_media_audio) {
        this.mContext = context
    }

    override fun setVariable(mBinding: ItemMediaAudioBinding, position: Int, item: LegacyAudioBean) {
        mBinding.audioInfo = item
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
        if (item.audioInfo!=null) {
            try {
                    mBinding.name = item.audioInfo!!.getFileName()
            } catch (e: Exception) {

            }
        }
        if(!item.audioInfo!!.time.isNullOrEmpty()){
            mBinding.txtProviderCommentaryTotalTime.text=""+item.audioInfo!!.time
            mBinding.txtProviderCommentaryTotalTime.visibility= View.VISIBLE
        }else{
            mBinding.txtProviderCommentaryTotalTime.visibility= View.GONE
        }
        mBinding?.vProviderCommentaryImg.onNoDoubleClick {
            if (item.audioInfo == null) {
                ToastUtils.showMessage("非常抱歉，音频文件地址错误~")
            } else {
                if (position != playingPos && playingPos != -1) {
                    // 如果需要播放位置不等于当前选中位置
                    playingPos = position
                    updatePlayingStatus(playingPos)
                    notifyItemRangeChanged(0, getData().size, "updatePlayStatus")
                } else {
                    // 切换选中位置
                    playingPos = position
                    if (item.state == 0) {
                        item.state = 1
                    } else if (item.state == 1) {
                        item.state = 2
                    } else if (item.state == 2) {
                        item.state = 1
                    }
                    notifyItemChanged(position, "updatePlayPos")
                }
            }
        }
        clearPlayStatus(position,mBinding)
    }

    private fun updatePlayingStatus(playingPos: Int) {
        listener?.pauseAudioPlayer()
        for (i in getData().indices) {
            if (i == playingPos) {
                getData()[i].state = 1
            } else {
                getData()[i].state = 0
                getData()[i].progress = 0
            }
        }

    }

    private fun clearPlayStatus(position: Int, mBinding: ItemMediaAudioBinding){
//        endAnimation(position)
        if(playingPos == position){
            startAnimation(mBinding.vProviderCommentaryImg)
        }else{
            pauseAnimation(mBinding.vProviderCommentaryImg)
        }
        mBinding?.imgCommentaryOpeartion.setBackgroundResource(R.mipmap.provider_details_commentary_play)
        mBinding?.txtProviderCommentaryTimer.text = "00.00"
        mBinding.root.setBackgroundResource(R.color.white)
        mBinding?.probarProviderCommentary.progress = 0
    }

    override fun payloadUpdateUi(mBinding: ItemMediaAudioBinding, position: Int, payloads: MutableList<Any>) {
        var audioInfo = getData()[position]
        when (payloads[0]) {
            "updatePlayStatus" -> {
                // 更改所有Item播放状态
                if (audioInfo.state == 1) {
                    listener?.changeAudioPlayer(audioInfo.audioInfo!!.getAudioUrl())
                    mBinding?.imgCommentaryOpeartion.setBackgroundResource(R.mipmap.provider_details_commentary_pause)
//                    startAnimation(mBinding.vProviderCommentaryImg, position)
                    startAnimation(mBinding.vProviderCommentaryImg)
                    mBinding.root.setBackgroundResource(R.drawable.shape_provider_fff5e6_5)
                } else {
//                    endAnimation(position)
                    pauseAnimation(mBinding.vProviderCommentaryImg)
                    mBinding?.imgCommentaryOpeartion.setBackgroundResource(R.mipmap.provider_details_commentary_play)
                    mBinding?.probarProviderCommentary.progress = audioInfo.progress
                    mBinding?.txtProviderCommentaryTimer.text = "00.00"
                    mBinding.root.setBackgroundResource(R.color.white)
                }
            }
            "updatePlayPos" -> {
                // 更改当前播放状态
                when (audioInfo.state) {
                    1 -> {
                        // 播放
                        startAnimation(mBinding.vProviderCommentaryImg)
//                        startAnimation(mBinding.vProviderCommentaryImg, position)
                        listener?.startAudioPlayer(audioInfo.audioInfo!!.getAudioUrl())
                        mBinding?.imgCommentaryOpeartion.setBackgroundResource(R.mipmap.provider_details_commentary_pause)
                        mBinding.root.setBackgroundResource(R.drawable.shape_provider_fff5e6_5)
                    }
                    2 -> {
                        // 暂停
                        mBinding?.imgCommentaryOpeartion.setBackgroundResource(R.mipmap.provider_details_commentary_play)
//                        pauseAnimation(position)
                        pauseAnimation(mBinding.vProviderCommentaryImg)
                        listener?.pauseAudioPlayer()
                        mBinding.root.setBackgroundResource(R.drawable.shape_provider_fff5e6_5)
                    }
                    else -> {
                        // 结束动画
//                        endAnimation(position)
                        pauseAnimation(mBinding.vProviderCommentaryImg)
                        listener?.stopAudioPlayer()
                        mBinding?.txtProviderCommentaryTimer.text = "00.00"
                        mBinding?.imgCommentaryOpeartion.setBackgroundResource(R.mipmap.provider_details_commentary_play)
                        mBinding.root.setBackgroundResource(R.color.white)
                    }
                }
            }
            "update_progress" -> {
                // 更新播放进度
                mBinding?.imgCommentaryOpeartion.setBackgroundResource(R.mipmap.provider_details_commentary_pause)
                mBinding?.probarProviderCommentary.max =audioInfo.duration
                mBinding?.probarProviderCommentary.progress = audioInfo.progress
                mBinding?.txtProviderCommentaryTimer.text = getTime(audioInfo.progress)
            }
            "updatePlayComplation" -> {
                // 播放完成
//                endAnimation(position)
                pauseAnimation(mBinding.vProviderCommentaryImg)
                mBinding?.imgCommentaryOpeartion.setBackgroundResource(R.mipmap.provider_details_commentary_play)
                mBinding.root.setBackgroundResource(R.color.white)
            }
        }
    }

    fun startAnimation(view:View){
        val rotationAnimator = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotationAnimator.repeatCount = -1
        rotationAnimator.duration = 5000
        rotationAnimator.interpolator = LinearInterpolator()
        view.startAnimation(rotationAnimator)
    }

    fun pauseAnimation(view:View){
        view.animation?.cancel()
    }

    private fun pauseAnimation(position: Int) {
        try {
            if (mCacheAnimator.containsKey(position)) {
                mCacheAnimator[position]?.pause()
            }
        } catch (e: Exception) {
        }
    }


    /**
     * 开启动画
     */
    private fun startAnimation(view: View, position: Int) {
        var objectAnimator: ObjectAnimator = ObjectAnimator.ofFloat(
            view,
            "rotation", 0f, 360f
        )
        // 旋转的角度可有多个
        objectAnimator.duration = 5000;
        objectAnimator.repeatCount = ValueAnimator.INFINITE
        objectAnimator.interpolator = LinearInterpolator()
        objectAnimator.start();//开始（重新开始）
        mCacheAnimator[position] = objectAnimator
    }

    /**
     * 暂停播放
     */
    fun pausePlaying() {
        try {
            if (playingPos > -1 && getData()[playingPos].state == 1) {
                getData()[playingPos].state = 2
                notifyItemChanged(playingPos, "updatePlayPos")
            }
        } catch (e: Exception) {
        }
    }

    /**
     * 结束动画
     */
    private fun endAnimation(position: Int) {
        try {

            if (mCacheAnimator.containsKey(position)) {
                mCacheAnimator[position]?.end()
                mCacheAnimator.remove(position)
            }
        } catch (e: Exception) {

        }
    }

    interface MeidaPlayerListener {
        /**
         * 启动播放
         */
        fun startAudioPlayer(url: String)

        /**
         * 改变资源文件
         */
        fun changeAudioPlayer(url: String)

        /**
         *  暂停播放
         */
        fun pauseAudioPlayer()

        /**
         * 停止播放
         */
        fun stopAudioPlayer()


    }

    /**
     *  修改进度条
     */
    fun updateProgress(progress: Int, duration: Int) {
        getData()[playingPos].progress = progress
        getData()[playingPos].duration = duration
        notifyItemChanged(playingPos, "update_progress")
    }

    fun getTime(ms: Int): String {
        var s = (ms / 1000) % 60
        var m = ms / 1000 / 60
        var result = ""
        result = if (m < 10) {
            "0$m"
        } else {
            m.toString()
        }
        result = if (s < 10) {
            "$result:0$s"
        } else {
            "$result:$s"
        }
        return result
    }

    /**
     * 播放完成
     */
    fun updatePlayCompletion() {
        getData()[playingPos].state = 0
        notifyItemChanged(playingPos, "updatePlayComplation")
    }

}