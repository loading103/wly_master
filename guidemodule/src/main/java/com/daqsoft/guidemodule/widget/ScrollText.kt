package com.daqsoft.guidemodule.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Display
import android.view.View
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatTextView

/**
 *
 * @Description  ScrollText
 * @ClassName   ScrollText
 * @Author      Wongxd
 * @Time        2020/4/14 9:17
 */
internal class ScrollText : AppCompatTextView, View.OnClickListener {

    private val TAG: String = MarqueeTextView::class.java.simpleName

    private var textLength = 0f // 文本长度

    private var viewWidth = 0f
    private var step = 0f // 文字的横坐标

    private var textY = 0f // 文字的纵坐标

    private var temp_view_plus_text_length = 0.0f // 用于计算的临时变量

    private var temp_view_plus_two_text_length = 0.0f // 用于计算的临时变量

    var isStarting = false // 是否开始滚动

    private lateinit var paint: Paint// 绘图样式

    private var text = "" // 文本内容

    private val currentScrollX // 当前滚动的位置
            = 0
    private val isStop = false
    private val textWidth = 0

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        try {
            initView()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 初始化控件
     */
    private fun initView() {
        setOnClickListener(this)
    }


    /**
     * 文本初始化，每次更改文本内容或者文本效果等之后都需要重新初始化一下
     */
    fun init(windowManager: WindowManager?) {
        try {
            paint = getPaint()
            paint.color = textColors.defaultColor
            text = getText().toString()
            textLength = paint.measureText(text)
            viewWidth = width.toFloat()
            if (viewWidth == 0f) {
                if (windowManager != null) {
                    val display: Display = windowManager.defaultDisplay
                    viewWidth = display.width.toFloat()
                }
            }
            step = textLength
            temp_view_plus_text_length = viewWidth + textLength
            temp_view_plus_two_text_length = viewWidth + textLength * 2
            y = textSize + paddingTop
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * 开始滚动
     */
    fun startScroll() {
        try {
            isStarting = true
            invalidate()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 停止滚动
     */
    fun stopScroll() {
        try {
            isStarting = false
            invalidate()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDraw(canvas: Canvas) {
        try {
            canvas.drawText(text, temp_view_plus_text_length - step, y, paint)
            if (!isStarting) {
                return
            }
            step += 2f // 速度
            if (step > temp_view_plus_two_text_length) step = textLength
            invalidate()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 控制
    override fun onClick(v: View?) {
        try {
            if (isStarting) stopScroll() else startScroll()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}