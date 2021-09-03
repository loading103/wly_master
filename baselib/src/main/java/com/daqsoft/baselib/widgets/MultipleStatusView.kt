package com.daqsoft.baselib.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.ToastUtils
import daqsoft.com.baselib.R
import kotlinx.android.synthetic.main.layout_loading.view.*
import java.lang.Exception
import kotlin.random.Random

/**
 * 多状态视图布局
 * @author Ramon
 * @date 2019/5/4
 */
class MultipleStatusView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    /**
     * 普通视图状态
     */
    val CONTENTVIEW_STATUS = 0
    /**
     * 网络请求错误状态
     */
    val ERROR_STATUS = 1
    /**
     * 网络错误状态
     */
    val NET_ERROR_STATUS = 2
    /**
     * 网络加载中状态
     */
    val LOADING_STATUS = 3
    /**
     * 当前视图状态（默认为正常视图状态）
     */
    var current_status = CONTENTVIEW_STATUS
    /**
     * 自定义网络请求错误状态下的自定义布局
     */
    val CUSTOM_ERROR: View? = null

    /**
     * 自定义网络错误状态下的自定义布局
     */
    val CUSTOM_NET_ERROR: View? = null

    /**
     * 正常状态视图
     */
    var layout_content_view: View? = null
    /**
     * 加载中视图
     */
    var layout_loading_view: View? = null
    /**
     * 错误状态视图
     */
    var layout_error_view: View? = null
    /**
     * 网络状态错误视图
     */
    var layout_net_error_view: View? = null

    var layout_current: View? = null

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MultipleStatusView)

        for (i in 0 until typedArray.indexCount) {
            val attr = typedArray.getIndex(i)
            when (attr) {
                R.styleable.MultipleStatusView_status -> current_status = typedArray.getInt(attr, 0)
            }
        }

        initView()

        typedArray.recycle()
    }

    /**
     * 初始化视图
     */
    fun initView() {
        when (current_status) {
            CONTENTVIEW_STATUS -> showContentView()
            ERROR_STATUS -> showErrorView()
            NET_ERROR_STATUS -> showNetErrorView()
            LOADING_STATUS -> showLoadingView()
        }
    }


    /**
     * 添加并显示正常状态下的视图布局文件
     * @param view 需要添加的布局
     */
    fun showContentView(view: View? = null) {
        removeAllViews()
        if (view == null) {
            if (layout_content_view == null) {
//                throw IllegalArgumentException("请添加视图布局")
                return
            }
            addView(layout_content_view)
        } else {
            layout_content_view = view
            addView(layout_content_view)
        }

    }

    /**
     *  只显示加载视图
     */
    fun onlyShowLoadingView() {
        hideAllViews()
        if (layout_loading_view == null) {
            val inflate = View.inflate(context, R.layout.layout_loading, null)
            layout_loading_view = inflate
        } else {
            if (!checkIsAddView(layout_loading_view!!)) {
                addView(layout_loading_view)
            }
            layout_content_view!!.visibility = View.VISIBLE
        }
    }

    /**
     * 只显示内容
     */
    fun onlyShowContentView() {
        hideAllViews()
        if (layout_content_view == null) {
            return
        } else {
            if (!checkIsAddView(layout_content_view!!)) {
                addView(layout_content_view)
            }
            layout_content_view!!.visibility = View.VISIBLE
        }
    }

    private fun checkIsAddView(view: View): Boolean {
        if (childCount > 0) {
            val count = childCount - 1
            for (i in 0..count) {
                if (getChildAt(i) == view) {
                    return true
                    break
                }
            }
        }
        return false

    }

    /**
     * 隐藏所有views
     */
    private fun hideAllViews() {
        layout_loading_view?.let {
            it.visibility = View.GONE
        }
        layout_content_view?.let {
            it.visibility = View.GONE
        }
        layout_error_view?.let {
            it.visibility = View.GONE
        }
        layout_net_error_view?.let {
            it.visibility = View.GONE
        }
    }

    /**
     * 添加网络请求错误状态下的视图布局
     * @param view 自定义错误视图布局
     * 报错去掉加载中
     */
    fun showErrorView(view: View? = null) {
        try {
            if(layout_loading_view!=null ){
                removeView(layout_loading_view)
            }
        }catch (e: Exception){
        }
//        if (view == null) {
//            // 若没有传自定义的错误视图，则用默认的自定义视图
//            if (layout_error_view == null) {
//                val inflate = View.inflate(context, R.layout.multiple_status_error, null)
//                if (BaseApplication.appArea == "sc"){
//                    createDrawable(inflate)
//                }
//                layout_error_view = inflate
//            }
//            addView(layout_error_view)
//        } else {
//            layout_error_view = view
//            addView(layout_error_view)
//        }
    }

    /**
     * 添加网络错误状态下的视图布局
     * @param view 自定义网络错误视图布局
     */
    fun showNetErrorView(view: View? = null) {
//        removeAllViews()
//        if (view == null) {
//            // 若没有传自定义的错误视图，则用默认的自定义视图
//            if (layout_net_error_view == null) {
//                val inflate = View.inflate(context, R.layout.multiple_status_net_error, null)
//                if (BaseApplication.appArea == "sc"){
//                    createDrawable(inflate)
//                }
//                layout_net_error_view = inflate
//            }
//            addView(layout_net_error_view)
//        } else {
//            layout_net_error_view = view
//            addView(layout_net_error_view)
//        }
    }

    /**
     * 显示加载中视图
     * @author 周俊蒙
     * @param view 自定义加载中视图布局
     * @date 2019/6.9
     */
   private var lav_loading: LottieAnimationView? = null
    fun showLoadingView(view: View? = null) {
//        removeAllViews()
        if (view == null) {
            // 若没有传自定义的加载中视图，则用默认的自定义加载视图
            if (layout_loading_view == null) {
                val inflate = View.inflate(context, R.layout.layout_loading, null)
                 lav_loading = inflate.findViewById<LottieAnimationView>(R.id.lav_loading);
                layout_loading_view = inflate
            }
            if(BaseApplication.appArea=="ws"){
                if((0..1).random()==0){
                    lav_loading?.setAnimation("loading.json")
                }else{
                    lav_loading?.setAnimation("wsloading2.json")
                }
            }
            removeView(layout_loading_view)
            addView(layout_loading_view)
        } else {
            if(BaseApplication.appArea=="ws"){
                if((0..1).random()==0){
                    lav_loading?.setAnimation("loading.json")
                }else{
                    lav_loading?.setAnimation("wsloading2.json")
                }
            }
            layout_loading_view = view
            addView(layout_loading_view)
        }
    }

    /**
     * 创建图片
     * @param inflate View
     */
    private fun createDrawable(inflate:View){
        val textview  = inflate.findViewById<TextView>(R.id.textView)
        val params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT)
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
        params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
        params.verticalBias = 0.4f
        val drawable = context.getDrawable(R.mipmap.common_application_failed).apply {
            this.setBounds(0, 0, this.minimumWidth, this.minimumHeight)
        }
        textview.setCompoundDrawables(null,drawable,null,null)
        textview.layoutParams = params
    }
}