package com.daqsoft.baselib.widgets

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieCompositionFactory
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.SPUtils
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshKernel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.constant.SpinnerStyle
import daqsoft.com.baselib.R


/**
 * @package name：com.daqsoft.baselib.widgets
 * @date 2020/9/4 17:31
 * @author zp
 * @describe SmartRefreshLayout 自定义头部刷新 LottieAnimation
 */
class PandaRefreshHeader : LinearLayout, RefreshHeader {

    private var lottie: LottieAnimationView? = null
    private var tips:TextView? = null
    private var animatorUtil:AnimatorUtil? = null
    private var ivloading: ImageView? = null
    constructor(context: Context?) : super(context){
        initView(context!!)
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        initView(context!!)
    }


    private fun initView(context: Context){
        var view : View? = null
        // 乌市用的帧动画 另外两个用的json
        if(BaseApplication.appArea=="sc" || BaseApplication.appArea=="xj"){
            view = LayoutInflater.from(context).inflate(R.layout.layout_refresh_head_panda, this, false)
            lottie  = view.findViewById(R.id.lottie)
            if (BaseApplication.appArea === "sc") {
                lottie?.setAnimation("lottie_animation_refresh_header_panda.json")
                lottie?.setImageAssetsFolder("refresh")
            } else {
                lottie?.setAnimation("load_more.json")
                lottie?.setImageAssetsFolder("images")
            }
        }else{
            view = LayoutInflater.from(context).inflate(R.layout.layout_refresh_head_ws, this, false)
            ivloading  = view.findViewById(R.id.im_loading)
            animatorUtil= AnimatorUtil(context)
            animatorUtil?.FrameAnimation(ivloading)

        }
        tips = view.findViewById(R.id.tips)
        tips?.visibility=View.VISIBLE
        val tipsText = SPUtils.getInstance().getString("main_tips", "加载中...")
        tips?.text=tipsText
        addView(view)
    }

    /**
     * 设置动画
     * @param animation String
     */
    fun setAnimationViewJson(animationJson: String) {
        lottie?.setAnimationFromJson(animationJson, null)
    }

    /**
     * 设置动画
     * @param animation String
     */
    fun setAnimationFromUrl(url: String) {
        LottieCompositionFactory.fromUrl(context, url)
            .addFailureListener {
                //加载失败
            }.addListener { result ->
                lottie?.setComposition(result)
            }
    }

    /**
     * 色设置提示文字
     * @param hint String
     */
    fun setTips(hint: String){
        tips?.text = hint
    }

    override fun onStateChanged(
        refreshLayout: RefreshLayout,
        oldState: RefreshState,
        newState: RefreshState
    ) {
        Log.e("newState-----",newState.toString())
        when (newState) {
            RefreshState.None -> {
            }
            RefreshState.PullDownToRefresh -> {
            }
            RefreshState.PullDownCanceled -> {
            }
            RefreshState.ReleaseToRefresh -> {
            }
            RefreshState.ReleaseToTwoLevel -> {
            }
            RefreshState.RefreshReleased -> {
                lottie?.playAnimation()
            }
            RefreshState.Refreshing -> {
            }
            RefreshState.RefreshFinish -> {
                lottie?.cancelAnimation()
                lottie?.clearAnimation()
                ivloading?.clearAnimation()
            }
            else->{
            }
        }
    }

    override fun getView(): View {
        return this
    }

    override fun getSpinnerStyle(): SpinnerStyle {
        return SpinnerStyle.Translate
    }

    override fun setPrimaryColors(vararg colors: Int) {
    }

    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {

    }

    override fun onMoving(
        isDragging: Boolean,
        percent: Float,
        offset: Int,
        height: Int,
        maxDragHeight: Int
    ) {
    }


    override fun onReleased(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
    }

    override fun onStartAnimator(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
//        lottie?.playAnimation()
    }

    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
        lottie?.cancelAnimation()
        lottie?.clearAnimation()
        return 500
    }

    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {
    }

    override fun isSupportHorizontalDrag(): Boolean {
        return false
    }
}