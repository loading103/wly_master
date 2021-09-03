package com.daqsoft.provider.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.daqsoft.provider.R
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


/**
 * @package name：com.daqsoft.provider.view
 * @date 2020/9/9 14:12
 * @author zp
 * @describe
 */
class BottomNavigationItemView(context: Context) : LinearLayout(context) {


    private var imageView: ImageView? = null
    private var textView: TextView? = null

    /**
     * 是否选中
     */
    private var isSelect = true

    /**
     * 未选中颜色
     */
    private var unselectedColor: Int = R.color.gray_999999

    /**
     * 选中颜色
     */
    private var selectedColor: Int = R.color.color_333

    /**
     * 未选中图片
     */
    private var unselectedImage: String? = null

    /**
     * 选中图片
     */
    private var selectedImage: String? = null

    /**
     * 文案
     */
    private var text: String? = null

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        this.setBackgroundResource(R.color.transparent)
        this.orientation = VERTICAL
        val view = LayoutInflater.from(context).inflate(
            R.layout.item_bottom_navigation_view,
            this,
            false
        )
        imageView = view.findViewById(R.id.image)
        textView = view.findViewById(R.id.title)

        addView(view)
    }

    /**
     * 设置选中状态
     * @param select Boolean
     */
    fun setSelectFlag(select: Boolean) {
        this.isSelect = select
        if (isSelect) {
            textView?.setTextColor(context.resources.getColor(selectedColor))
            loadPicture(selectedImage)
        } else {
            textView?.setTextColor(context.resources.getColor(unselectedColor))
            loadPicture(unselectedImage)
        }
    }

    /**
     * 获取选中状态
     * @return Boolean
     */
    fun getSelectFlag() = isSelect


    /**
     * 设置未选中颜色
     * @param conlor Int
     */
    fun setUnselectedColor(unselectedColor: Int) {
        this.unselectedColor = unselectedColor
    }

    /**
     * 设置选中颜色
     * @param conlor Int
     */
    fun setSelectedColor(selectedColor: Int) {
        this.selectedColor = selectedColor
    }

    /**
     * 设置未选中图片
     * @param conlor Int
     */
    fun setUnselectedImage(unselectedImage: String) {
        this.unselectedImage = unselectedImage
    }

    /**
     * 设置未选中图片
     * @param conlor Int
     */
    fun setSelectedImage(selectedImage: String) {
        this.selectedImage = selectedImage
    }

    /**
     * 设置文案
     * @param text String
     */
    fun setText(text: String) {
        this.text = text
        textView?.text = text
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    /**
     * 加载图片
     */
    private fun loadPicture(url: String?) {
        imageView?.let {
            Glide
                .with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        if (resource is GifDrawable) {
                            //设置只播放一次
                            resource.setLoopCount(1)
                        }
                        return false
                    }
                })
                .into(it)
        }
    }

    @Throws(Exception::class)
    fun getValue(`object`: Any?, fieldName: String?): Any? {
        if (`object` == null) {
            return null
        }
        if (TextUtils.isEmpty(fieldName)) {
            return null
        }
        var field: Field? = null
        var clazz: Class<*> = `object`.javaClass
        while (clazz != Any::class.java) {
            try {
                field = clazz.getDeclaredField(fieldName)
                field.setAccessible(true)
                return field.get(`object`)
            } catch (e: Exception) { //这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
//如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了
            }
            clazz = clazz.superclass
        }
        return null
    }

    /**
     * Gif播放完毕回调
     */
    interface GifListener {
        fun gifPlayComplete()
    }
}