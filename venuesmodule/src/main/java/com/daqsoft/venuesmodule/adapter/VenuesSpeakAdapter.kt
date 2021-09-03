package com.daqsoft.venuesmodule.adapter

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.network.venues.bean.AudioInfo
import com.daqsoft.provider.utils.GaosiUtils
import com.daqsoft.provider.view.web.WebActivity
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ItemVenuesSpeakingBinding
import java.lang.Exception

/**
 * @Description 听解说适配器
 * @ClassName   VenuesSpeakAdapter
 * @Author      luoyi
 * @Time        2020/3/26 18:22
 */
class VenuesSpeakAdapter : RecyclerViewAdapter<ItemVenuesSpeakingBinding, AudioInfo> {
    var playingPos: Int = -1
    var mContext: Context? = null
    /**
     *  缓存动画执行对象
     */
    var mCacheAnimator: HashMap<Int, ObjectAnimator> = hashMapOf()


    var listener: MeidaPlayerListener? = null

    constructor(context: Context) : super(R.layout.item_venues_speaking) {
        this.mContext = context
    }

    var isNeedMore: Boolean = false
    override fun getItemCount(): Int {
        if (isNeedMore) {
            // 需要判断
            if (getData().size > 2) {
                return 2
            } else {
                return getData().size
            }
        } else {
            return getData().size
        }
    }

    @SuppressLint("WrongConstant")
    override fun setVariable(mBinding: ItemVenuesSpeakingBinding, position: Int, item: AudioInfo) {
        mBinding.audioInfo = item
        if (!item.linkUrl.isNullOrEmpty()) {
            mBinding.vVenueCommentaryMore.visibility = View.VISIBLE
            mBinding.vVenueCommentaryMore.onNoDoubleClick {
                ARouter.getInstance()
                    .build(ARouterPath.Provider.WEB_ACTIVITY)
                    .withString("mTitle", "金牌解说")
                    .withString("html", item.linkUrl)
                    .navigation()
            }
        } else {
            mBinding.vVenueCommentaryMore.visibility = View.GONE
        }
        if (item.cover.isNullOrEmpty()) {
            Glide.with(mContext!!)
                .asBitmap()
                .load(R.mipmap.placeholder_img_fail_240_180)
                .centerCrop()
                .override(48, 48)
                .into(object : CustomTarget<Bitmap>() {

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

                        var b = GaosiUtils.rsBlur(BaseApplication.context, resource, 25)

                        mBinding.imgCommentBg.setImageBitmap(b)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
        } else {
            Glide.with(mContext!!)
                .asBitmap()
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .load(item.cover)
                .centerCrop()
                .override(48, 48)
                .into(object : CustomTarget<Bitmap>() {

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

                        var b = GaosiUtils.rsBlur(BaseApplication.context, resource, 25)

                        mBinding.imgCommentBg.setImageBitmap(b)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
        }
        if (!item.audio.isNullOrEmpty()) {
            try {
                if (item.name.isNullOrEmpty()) {
                    // 裁剪文件路径 获取名称
                    var title = item.audio!!.substring(
                        item.audio!!.lastIndexOf("/") + 1,
                        item.audio!!.lastIndexOf(".")
                    )
                    if (title.contains(".")) {
                        mBinding.name = title.substring(0, title.lastIndexOf("."))
                    } else {
                        mBinding.name = title
                    }

                } else {
                    mBinding.name = item.name
                }
            } catch (e: Exception) {

            }
        }

        mBinding?.vCommentaryImg.onNoDoubleClick {
            if (item.audio.isNullOrEmpty()) {
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

    override fun payloadUpdateUi(mBinding: ItemVenuesSpeakingBinding, position: Int, payloads: MutableList<Any>) {
        var audioInfo = getData()[position]
        when (payloads[0]) {
            "updatePlayStatus" -> {
                // 更改所有Item播放状态
                if (audioInfo.state == 1) {
                    listener?.changeAudioPlayer(audioInfo.audio!!)
                    mBinding?.imgCommentaryOpeartion.setBackgroundResource(R.mipmap.venue_details_commentary_pause)
                    startAnimation(mBinding.vCommentaryImg, position)
                    mBinding.root.setBackgroundResource(R.drawable.shape_venues_fff5e6_5)
                } else {
                    endAnimation(position)
                    mBinding?.imgCommentaryOpeartion.setBackgroundResource(R.mipmap.venue_details_commentary_play)
                    mBinding?.probarCommentary.progress = audioInfo.progress
                    mBinding?.txtVenueCommentaryTimer.text = "00.00"
                    mBinding.root.setBackgroundResource(R.color.white)
                }
            }
            "updatePlayPos" -> {
                // 更改当前播放状态
                when (audioInfo.state) {
                    1 -> {
                        // 播放
                        startAnimation(mBinding.vCommentaryImg, position)
                        listener?.startAudioPlayer(audioInfo.audio!!)
                        mBinding?.imgCommentaryOpeartion.setBackgroundResource(R.mipmap.venue_details_commentary_pause)
                        mBinding.root.setBackgroundResource(R.drawable.shape_venues_fff5e6_5)
                    }
                    2 -> {
                        // 暂停
                        mBinding?.imgCommentaryOpeartion.setBackgroundResource(R.mipmap.venue_details_commentary_play)
                        pauseAnimation(position)
                        listener?.pauseAudioPlayer()
                        mBinding.root.setBackgroundResource(R.drawable.shape_venues_fff5e6_5)
                    }
                    else -> {
                        // 结束动画
                        endAnimation(position)
                        listener?.stopAudioPlayer()
                        mBinding?.txtVenueCommentaryTimer.text = "00.00"
                        mBinding?.imgCommentaryOpeartion.setBackgroundResource(R.mipmap.venue_details_commentary_play)
                        mBinding.root.setBackgroundResource(R.color.white)
                    }
                }
            }
            "update_progress" -> {
                // 更新播放进度
                mBinding?.probarCommentary.max = audioInfo.duration
                mBinding?.probarCommentary.progress = audioInfo.progress
                mBinding?.txtVenueCommentaryTimer.text = getTime(audioInfo.progress)
            }
            "updatePlayComplation" -> {
                // 播放完成
                endAnimation(position)
                mBinding?.imgCommentaryOpeartion.setBackgroundResource(R.mipmap.venue_details_commentary_play)
                mBinding.root.setBackgroundResource(R.color.white)
            }
        }
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
        );
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
            getData()[playingPos].state = 2
            notifyItemChanged(playingPos, "updatePlayPos")
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