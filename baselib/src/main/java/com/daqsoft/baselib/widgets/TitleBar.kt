package com.daqsoft.baselib.widgets

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.daqsoft.baselib.utils.Utils
import daqsoft.com.baselib.R
import kotlinx.android.synthetic.main.title_bar.view.*

/**
 * 自定义顶部标题栏
 *
 * 使用的时候一定要注意设置字体大小
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019/4/17
 * @since JDK 1.8
 */
class TitleBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    /**
     * 返回键按钮是否隐藏
     */
    var isBackShow: Boolean = true
        set(value) {
            field = value
            mBackIv.visibility = if (field) View.VISIBLE else View.INVISIBLE
        }
    /**
     * 标题text
     */
    var title: CharSequence = ""
        set(value) {
            field = value
            mTitleTv.text = field
        }
    /**
     * 右标题
     */
    var titleRight: CharSequence = ""
        set(value) {
            field = value
            mTitleRightTv.text = field
        }
    /**
     * 标题颜色
     */
    var titleColor: Int = 0
        set(value) {
            field = value
            mTitleTv.setTextColor(field)
        }
    /**
     * 右标题颜色
     */
    var titleRightColor: Int = 0
        set(value) {
            field = value
            mTitleRightTv.setTextColor(field)
        }
    /**
     * 标题字体大小
     */
    var titleSize: Float = 16f
        set(value) {
            field = value
            mTitleTv.textSize = field
        }
    /**
     * 右标题字体大小
     */
    var titleRightSize: Float = 14f
        set(value) {
            field = value
            mTitleRightTv.textSize = field
        }
    /**
     * 标题背景
     */
    var titleBackground: Int = 0
        set(value) {
            field = value
            mTitleBarFl.setBackgroundColor(field)
        }

    var itemClickListener: ((View) -> Unit)? = null

    fun showShareIcon(shareRes: Int) {
        v_title_share.visibility = View.VISIBLE
        img_titel_share.setImageResource(shareRes)
    }

    /**
     * 设置分享点击事件
     */
    fun setShareClickListener(click: View.OnClickListener) {
        img_titel_share.setOnClickListener(click)
    }


    init {
        View.inflate(context, R.layout.title_bar, this)
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.TitleBar)
//        title = typeArray.getString(R.styleable.TitleBar_title)
//        titleRight = typeArray.getString(R.styleable.TitleBar_titleRight)
        for (i in 0 until typeArray.indexCount) {
            val attr = typeArray.getIndex(i)
            when (attr) {
                R.styleable.TitleBar_titleRight -> titleRight = typeArray.getText(attr)
                R.styleable.TitleBar_title -> title = typeArray.getText(attr)
            }
        }
        titleColor = typeArray.getColor(
            R.styleable.TitleBar_titleTextColor,
            ContextCompat.getColor(context, R.color.white)
        )
        titleRightColor = typeArray.getColor(
            R.styleable.TitleBar_titleRightTextColor,
            ContextCompat.getColor(context, R.color.white)
        )
        isBackShow = typeArray.getBoolean(R.styleable.TitleBar_isBackShow, true)
        titleSize = Utils.px2dip(
            context,
            typeArray.getDimension(R.styleable.TitleBar_titleTextSize, 0f)
        )
        titleRightSize = Utils.px2dip(
            context,
            typeArray.getDimension(R.styleable.TitleBar_titleTextRightSize, 0f)
        )
        titleBackground = typeArray.getColor(
            R.styleable.TitleBar_titleBackground,
            ContextCompat.getColor(context, R.color.white)
        )
        initView()
        typeArray.recycle()
    }

    private fun initView() {
        mTitleTv.apply {
            this.text = title
            this.textSize = titleSize
            this.setTextColor(titleColor)
        }
        mTitleRightTv.apply {
            this.text = titleRight
            this.textSize = titleRightSize
            this.setTextColor(titleRightColor)
        }
        mTitleBarFl.setBackgroundColor(titleBackground)
        mBackIv.visibility = if (isBackShow) View.VISIBLE else View.INVISIBLE
        // 设置返回键监听事件
        mBackIv.setOnClickListener {
            (context as Activity).onBackPressed()
        }
        mTitleRightTv.setOnClickListener {
            if (itemClickListener != null) {
                itemClickListener?.invoke(it)
            }
        }
    }

    /**
     * 菜单点击事件
     * @param itemClickListener 点击事件回调
     */
    fun menuItemClick(itemClickListener: (v: View) -> Unit) {
        this.itemClickListener = itemClickListener
    }


}
