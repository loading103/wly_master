package com.daqsoft.baselib.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LevelListDrawable
import android.os.Build
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.baselib.utils.GlideRoundTransform
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.Utils
import daqsoft.com.baselib.R

/**
 * 自定义binding方法
 * @author Ramon
 * @date 2019/5/26
 */

/**
 * 给ImageView设置背景图片
 */
@BindingAdapter("url", "placeholder")
fun setImageUrl(imageView: ImageView, url: String?, placeholder: Drawable?) {
    GlideModuleUtil.loadDqImage(url, imageView)
}

/**
 * 给ImageView设置背景图片
 */
@BindingAdapter("waterMarkerUrl")
fun setImageWaterMarkUrl(imageView: ImageView, waterMarkerUrl: String?) {
    GlideModuleUtil.loadDqImageWaterMark(waterMarkerUrl, imageView)
}

/**
 * 给ImageView设置背景图片
 */
@BindingAdapter("url", "placeholder")
fun setImageUrl(imageView: ImageView, url: Int?, placeholder: Drawable?) {
    GlideModuleUtil.loadLocalDqImage(url, imageView)
}

/**
 * 给ImageView设置圆角背景图片
 */
@BindingAdapter("url", "placeholder", "cornerRadius")
fun setImageUrl(imageView: ImageView, url: String?, placeholder: Drawable?, cornerRadius: Int) {

    val option = RequestOptions
        .bitmapTransform(
            RoundedCorners(
                Utils.dip2px(imageView.context, cornerRadius.toFloat()).toInt()
            )
        )
        .placeholder(placeholder)
        .error(placeholder)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .skipMemoryCache(true)
        .fallback(placeholder)


    Glide.with(imageView)
        .load(url)
        .apply(option)
        .into(imageView)
}

fun setImageUrlqwx(imageView: ImageView, url: String?, placeholder: Drawable?, cornerRadius: Int) {
    var options = RequestOptions()
        .centerCrop()
        .placeholder(placeholder) //预加载图片
//                    .error(R.drawable.ic_launcher_foreground) //加载失败图片
        .priority(Priority.HIGH) //优先级
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE) //缓存
        .skipMemoryCache(true)
        .transform(GlideRoundTransform(cornerRadius)); //圆角
    GlideModuleUtil.loadDqImageWaterMark(
        url,
        600,
        700,
        imageView.context,
        imageView,
        R.mipmap.placeholder_img_fail_240_180,
        R.mipmap.placeholder_img_fail_240_180,
        options
    )
//    Glide.with(imageView).load(url?.let { setImageUrlqwxSmall(it) }).apply(options).into(imageView);
}

/**
 * 获取图片url略缩图
 */
private fun setImageUrlqwxSmall(url: String): String {
    return "https://www.daqctc.com/api/config/ued/image?imageUrl=$url&width=600&height=700"
}

/**
 * 设置圆形图标，多用于头像设置
 */
@BindingAdapter("url", "placeholder", "circleCrop")
fun setCenterCropImage(
    imageView: ImageView,
    url: String?,
    placeholder: Drawable?,
    circleCrop: Boolean
) {
    val options = RequestOptions()
        .placeholder(placeholder)
        .error(placeholder)
        .circleCrop()
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .skipMemoryCache(true)
        .fallback(placeholder)

    Glide.with(imageView)
        .load(url)
        .apply(options)
        .into(imageView)
}

/**
 * 设置圆形图标，多用于头像设置
 */
@BindingAdapter("url", "circleCrop")
fun setCenterCropImage(imageView: ImageView, url: String?, circleCrop: Boolean) {
    val options = RequestOptions()
        .circleCrop()
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .skipMemoryCache(true)


    Glide.with(imageView)
        .load(url)
        .apply(options)
        .into(imageView)
}

/**
 * 给TextView设置Html样式的text
 */
@BindingAdapter("html")
fun setHtmlText(textView: TextView, html: String) {
    textView.text = Html.fromHtml(html)
}

/**
 * 给ImageView设置图片，不包含占位图
 */
@BindingAdapter("url")
fun setImageUrl(imageView: ImageView, url: String?) {
    val option = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .skipMemoryCache(true)

    Glide.with(imageView)
        .load(url)
        .apply(option)
        .into(imageView)
}

/**
 * 给ImageView设置图片，不包含占位图
 */
@BindingAdapter("dqurl", "width", "height")
fun setImageDqUrl(imageView: ImageView, dqurl: String?, width: String, height: String) {
    val option = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .skipMemoryCache(true)

    Glide.with(imageView)
        .load(
            StringUtil.getImageUrl(dqurl, width.toInt(), height.toInt())
        )
        .apply(option)
        .placeholder(R.mipmap.placeholder_img_fail_240_180)
        .into(imageView)
}

@BindingAdapter(value = ["imageUrl", "raduis"], requireAll = true)
fun setDefaultImageDqUrl(imageView: ImageView, url: String?, raduis: String?) {
    var corRaduis = 1
    if (!raduis.isNullOrEmpty()) {
        corRaduis = raduis.toInt()
    }
    var options: RequestOptions? = null
    if (corRaduis != 0) {
        options = RequestOptions().transform(CenterCrop(), RoundedCorners(corRaduis))
    } else {
        options = RequestOptions().transform(CenterCrop())
    }

    Glide.with(imageView).load(url)
        .apply(options)
        .placeholder(R.mipmap.placeholder_img_fail_240_180)
        .into(imageView)
}


fun setImageVideo(context: Context, imageView: ImageView, url: String?, placeholder: Drawable?) {
    val option = RequestOptions()
        .placeholder(placeholder)
        .error(placeholder)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .skipMemoryCache(true)
        .fallback(placeholder)
    // 异步加载视频缩略图
    Glide.with(context)
        .setDefaultRequestOptions(
            RequestOptions()
                .frame(1000000)
                .centerCrop()
        )
        .load(url)
        .apply(option)
        .into(imageView)

}

/**
 * 给WebView设置html内容
 */
@BindingAdapter("html")
fun setHtmlText(webView: WebView, html: String?) {
    webView.loadDataWithBaseURL(
        null,
        Utils.getNewContent(html ?: ""),
        "text/html",
        "utf-8",
        null
    )
}

/**
 * 给TextView设置Html字体，实现图文混排
 */
@BindingAdapter("html")
fun setHtmlTextForTextView(textView: TextView, html: String?) {
//    val covert = Utils.toDBC(html ?: "")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        textView.text = Html.fromHtml(
            html ?: "",
            Html.FROM_HTML_MODE_COMPACT,
            Html.ImageGetter {
                val levelListDrawable = LevelListDrawable()
                Glide.with(textView)
                    .asBitmap()
                    .load(it)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onLoadCleared(placeholder: Drawable?) {

                        }

                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            val bitmapDrawable = BitmapDrawable(null, resource)
                            levelListDrawable.addLevel(1, 1, bitmapDrawable)
                            levelListDrawable.setBounds(0, 0, resource.width, resource.height)
                            levelListDrawable.level = 1
                            textView.invalidate()
                            textView.text = textView.text
                        }

                    })
                return@ImageGetter levelListDrawable
            }, null
        )
    }
}

/**
 * 设置文字的伸缩
 * 用于有简介有详情的情况
 * 若详情是含有html标签的字段，依然可以正常显示且能正常显示图片
 * @param html 详情
 * @param summary 简介
 * @param expandTextColor 收缩字体的颜色
 * TODO:本想用Int类型通过资源文件引用color，但是跨模块会出现资源文件找不到的现象，故此处传字符串String
 */
@BindingAdapter("html", "summary", "expandTextColor")
fun setExpandTextView(
    textView: TextView?,
    html: String?,
    summary: String?,
    expandTextColor: String?
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        // 折叠的文字
        val collapsingText = "[查看全部]"
        // 展开的文字
        val expandText = "[收起]"

        val collapsingTextSs = SpannableString(collapsingText)
        val expandTextSs = SpannableString(expandText)
        if (textView != null) {
            // 为收缩状态则展开，并设置详细内容的文字
            val clickableSpan1 = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    textView.maxLines = Int.MAX_VALUE
                    textView.text = Html.fromHtml(
                        html ?: "",
                        Html.FROM_HTML_MODE_COMPACT,
                        Html.ImageGetter {
                            val levelListDrawable = LevelListDrawable()
                            Glide.with(textView)
                                .asBitmap()
                                .load(it)
                                .into(object : CustomTarget<Bitmap>() {
                                    override fun onLoadCleared(placeholder: Drawable?) {

                                    }

                                    override fun onResourceReady(
                                        resource: Bitmap,
                                        transition: Transition<in Bitmap>?
                                    ) {
                                        val bitmapDrawable = BitmapDrawable(null, resource)
                                        levelListDrawable.addLevel(1, 1, bitmapDrawable)
                                        levelListDrawable.setBounds(
                                            0,
                                            0,
                                            resource.width,
                                            resource.height
                                        )
                                        levelListDrawable.level = 1
                                        textView.invalidate()
                                        textView.text = textView.text
                                    }

                                })
                            return@ImageGetter levelListDrawable
                        }, null
                    )
                    textView.append(expandTextSs)
                    textView.movementMethod = LinkMovementMethod.getInstance()
                }

                override fun updateDrawState(ds: TextPaint) {
//                    ds.color = ContextCompat.getColor(textView.context, expandTextColor)
                    ds.color = Color.parseColor(expandTextColor.toString())
                    ds.isUnderlineText = false
                }
            }
            // 若展开则点击收缩
            val clickableSpan2 = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    textView.text = summary
                    textView.append(collapsingTextSs)
                    textView.movementMethod = LinkMovementMethod.getInstance()
                }

                override fun updateDrawState(ds: TextPaint) {
//                    ds.color = ContextCompat.getColor(textView.context, expandTextColor)
                    ds.color = Color.parseColor(expandTextColor)
                    ds.isUnderlineText = false
                }
            }
            collapsingTextSs.setSpan(
                clickableSpan1,
                0,
                collapsingText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            expandTextSs.setSpan(
                clickableSpan2,
                0,
                expandText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // 默认收缩状态
            textView.text = summary
            textView.append(collapsingTextSs)
            textView.movementMethod = LinkMovementMethod.getInstance()
        }
    }

}

/**
 * 机器人字体设置
 * @param keyWord 搜索关键词
 * @param keyWordColor 搜索关键词颜色
 */
@BindingAdapter("keyWord", "keyWordColor")
fun robotMessageText(textView: TextView, keyWord: String, keyWordColor: String) {
    val content = "为你查到关于\"$keyWord\"资源如下:"
    val convert = SpannableString(content)
    convert.setSpan(
        ForegroundColorSpan(Color.parseColor(keyWordColor)),
        6,
        6 + keyWord.length + 2,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    textView.text = convert
}
