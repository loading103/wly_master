package com.daqsoft.itinerary.adapter

import android.graphics.Color
import android.os.Build
import android.util.DisplayMetrics
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.collection.ArrayMap
import androidx.constraintlayout.widget.ConstraintLayout
import com.amap.api.col.sln3.it
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.extend.dp
import com.daqsoft.baselib.utils.ResourceUtils
import com.daqsoft.baselib.widgets.ArcImageView
import com.daqsoft.itinerary.R
import com.daqsoft.itinerary.bean.ItineraryBean
import com.daqsoft.itinerary.databinding.ItemItineraryBinding
import com.daqsoft.itinerary.interfa.OnItemSelectedListener
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import org.jetbrains.anko.padding
import org.jetbrains.anko.windowManager
import java.lang.StringBuilder

/**
 * @Author：      邓益千
 * @Create by：   2020/4/21 20:17
 * @Description：
 */
class ItineraryListAdapter(private val tag: String) :
    RecyclerViewAdapter<ItemItineraryBinding, ItineraryBean>(R.layout.item_itinerary) {

    private var listener: OnItemClick<Map<String, String>>? = null

    private val labelParams: LinearLayout.LayoutParams by lazy {
        LinearLayout.LayoutParams(-2, -2)
    }

    fun setOnItemListener(listener: OnItemClick<Map<String, String>>?) {
        this.listener = listener
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun setVariable(mBinding: ItemItineraryBinding, position: Int, item: ItineraryBean) {
        mBinding.itinerary = item
        if (tag == "SYSTEM") {
            mBinding.viewRenamed.visibility = View.GONE
        }

        mBinding.root.setOnClickListener {
            val map = ArrayMap<String, String>()
            map["tag"] = tag
            map["id"] = item.id.toString()
            listener?.onSelected(position, map)
        }
        mBinding.viewRenamed.setOnClickListener {
            listener?.onRenamed(position, item.id.toString())
        }

        val sb = StringBuilder()
        sb.append("行程历时%d天".format(item.processDay))
        if (!item.processStart.isNullOrEmpty() && !item.processEnd.isNullOrEmpty()) {
            if (item.processStart == item.processEnd) {
                sb.append(",${item.processStart}".replace("-", "."))
            } else {
                sb.append(
                    ",${item.processStart.replace("-", ".")}-${item.processEnd.replace(
                        "-",
                        "."
                    )}"
                )
            }
        }
        mBinding.itineraryPeriods.text = sb.toString()

        mBinding.tagLayout.removeAllViews()
        //个性标签有值就循环
        if (!item.fitTagsNames.isNullOrEmpty() && tag == "SYSTEM") {
            val tags = item.fitTagsNames.split(",")
            for (tag in tags) {
                val tagView = TextView(
                    ContextThemeWrapper(
                        mBinding.root.context,
                        R.style.ItineraryLabel
                    )
                ).apply {
                    text = tag
                    setTextColor(ResourceUtils.getColor(this,R.color.color_36CD64))
                    setBackgroundResource(R.drawable.itinerary_shape_solid_green_3)
                }
                mBinding.tagLayout.addView(tagView)
            }
        }

        //人群分类有值就循环
        if (!item.personalTagsNames.isNullOrEmpty() && tag == "SYSTEM") {
            val tags = item.personalTagsNames.split(",")
            for (tag in tags) {
                val tagView = TextView(ContextThemeWrapper(mBinding.root.context, R.style.ItineraryLabel)).apply {
                    text = tag
                    setBackgroundResource(R.drawable.itinerary_shape_solid_orange)
                }
                mBinding.tagLayout.addView(tagView)
            }
        }

        val ab = StringBuffer()
        if (item.regionCount > 0) {
            ab.append("${item.regionCount}个城市")
        }
        if (item.scenicCount > 0) {
            ab.append(" · ${item.scenicCount}个景区")
        }
        if (item.venueCount > 0) {
            ab.append(" · ${item.venueCount}个文化场所")
        }
        if (item.hotelCount > 0) {
            ab.append(" · ${item.hotelCount}个酒店")
        }
//        if (item.diningCount > 0) {
//            ab.append(" · ${item.diningCount}个餐馆")
//        }
        mBinding.viewLabels.text = ab.toString()
        mBinding.posterLayout.removeAllViews()
        var params = ConstraintLayout.LayoutParams(-1, -1)
        if (item.coverImages.isNullOrEmpty()) {
            mBinding.posterLayout.visibility = View.GONE
        } else {
            mBinding.posterLayout.visibility = View.VISIBLE
            if (!item.coverImages.contains(",")) {
                //动态添加一个View
//                val posterView = ArcImageView(mBinding.root.context)
//                posterView.setCornerRadius(
//                    ResourceUtils.getDimension(posterView, R.dimen.dp_5).toFloat()
//                )
                val posterView = ImageView(mBinding.root.context)
                posterView.id = R.id.view_poster
                params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                posterView.layoutParams = params
                Glide
                    .with(mBinding.root.context)
                    .load(item.coverImages)
                    .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(5.dp)))
                    .placeholder(R.drawable.grid_placeholde)
                    .into(posterView)
                mBinding.posterLayout.addView(posterView)

            } else {
                val dm = DisplayMetrics()
                val urls = item.coverImages.split(",")

                //获取屏幕分辨率进行View排版
                mBinding.root.context.windowManager.defaultDisplay.getRealMetrics(dm)

                //添加第一个View
                params.width = (dm.widthPixels * 0.6).toInt()
//                val posterView = ArcImageView(mBinding.root.context)
//                posterView.setCornerTopLeftRadius(
//                    ResourceUtils.getDimension(posterView, R.dimen.dp_5).toFloat()
//                )
//                posterView.setCornerBottomLeftRadius(
//                    ResourceUtils.getDimension(
//                        posterView,
//                        R.dimen.dp_5
//                    ).toFloat()
//                )
                val posterView = ImageView(mBinding.root.context)
                posterView.id = R.id.view_poster
                params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                posterView.layoutParams = params
                mBinding.posterLayout.addView(posterView)

                val topLeft = RoundedCornersTransformation(
                    5.dp,
                    0,
                    RoundedCornersTransformation.CornerType.TOP_LEFT
                )
                val bottomLeft = RoundedCornersTransformation(
                    5.dp,
                    0,
                    RoundedCornersTransformation.CornerType.BOTTOM_LEFT
                )
                val transform = MultiTransformation(CenterCrop(), topLeft, bottomLeft)

                Glide
                    .with(mBinding.root.context)
                    .load(urls[0])
                    .apply(RequestOptions().transform(transform))
                    .placeholder(R.drawable.grid_placeholde)
                    .into(posterView)

                val width = (dm.widthPixels * 0.3).toInt()
                val height = mBinding.root.context.resources.getDimensionPixelSize(R.dimen.dp_80)
                if (urls.size == 2) {
                    //添加第二个View
                    params = ConstraintLayout.LayoutParams(0, -1)
//                    val posterViewTwo = ArcImageView(mBinding.root.context)
//                    posterViewTwo.setCornerTopRightRadius(
//                        ResourceUtils.getDimension(
//                            posterView,
//                            R.dimen.dp_5
//                        ).toFloat()
//                    )
//                    posterViewTwo.setCornerBottomRightRadius(
//                        ResourceUtils.getDimension(
//                            posterView,
//                            R.dimen.dp_5
//                        ).toFloat()
//                    )

                    val posterViewTwo = ImageView(mBinding.root.context)
                    posterViewTwo.id = R.id.view_postertwo
                    params.leftToRight = posterView.id
                    params.marginStart =
                        mBinding.root.context.resources.getDimensionPixelSize(R.dimen.dp_5)
                    params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
                    posterViewTwo.layoutParams = params
                    mBinding.posterLayout.addView(posterViewTwo)

                    val topRight = RoundedCornersTransformation(
                        5.dp,
                        0,
                        RoundedCornersTransformation.CornerType.TOP_RIGHT
                    )
                    val bottomRight = RoundedCornersTransformation(
                        5.dp,
                        0,
                        RoundedCornersTransformation.CornerType.BOTTOM_RIGHT
                    )
                    val transform = MultiTransformation(CenterCrop(), topRight, bottomRight)
                    Glide
                        .with(mBinding.root.context)
                        .load(urls[1])
                        .apply(RequestOptions().transform(transform))
                        .placeholder(R.drawable.grid_placeholde)
                        .into(posterViewTwo)

                } else if (urls.size > 2) {
                    //添加第二个View
                    params = ConstraintLayout.LayoutParams(0, height)
//                    val posterViewTwo = ArcImageView(mBinding.root.context)
//                    posterViewTwo.setCornerTopRightRadius(
//                        ResourceUtils.getDimension(
//                            posterView,
//                            R.dimen.dp_5
//                        ).toFloat()
//                    )
                    val posterViewTwo = ImageView(mBinding.root.context)

                    posterViewTwo.id = R.id.view_postertwo
                    params.leftToRight = posterView.id
                    params.marginStart =
                        mBinding.root.context.resources.getDimensionPixelSize(R.dimen.dp_5)
                    params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
                    posterViewTwo.layoutParams = params
                    mBinding.posterLayout.addView(posterViewTwo)

                    val topRight = RoundedCornersTransformation(
                        20,
                        0,
                        RoundedCornersTransformation.CornerType.TOP_RIGHT
                    )
                    val transformTwo = MultiTransformation(CenterCrop(), topRight)

                    Glide
                        .with(mBinding.root.context)
                        .load(urls[1])
                        .apply(RequestOptions().transform(transformTwo))
                        .placeholder(R.drawable.grid_placeholde)
                        .into(posterViewTwo)

                    //第三个View
                    params = ConstraintLayout.LayoutParams(0, height)
//                    val posterViewThree = ArcImageView(mBinding.root.context)
//                    posterViewThree.setCornerBottomRightRadius(
//                        ResourceUtils.getDimension(
//                            posterView,
//                            R.dimen.dp_5
//                        ).toFloat()
//                    )

                    val posterViewThree = ImageView(mBinding.root.context)
                    params.leftToRight = posterView.id
                    params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
                    params.bottomToBottom = 0
                    params.marginStart =
                        mBinding.root.context.resources.getDimensionPixelSize(R.dimen.dp_5)
                    posterViewThree.layoutParams = params
                    mBinding.posterLayout.addView(posterViewThree)

                    val bottomRight = RoundedCornersTransformation(
                        20,
                        0,
                        RoundedCornersTransformation.CornerType.BOTTOM_RIGHT
                    )
                    val transformThree = MultiTransformation(CenterCrop(), bottomRight)
                    Glide
                        .with(mBinding.root.context)
                        .load(urls[2])
                        .apply(RequestOptions().transform(transformThree))
                        .placeholder(R.drawable.grid_placeholde)
                        .into(posterViewThree)

                    //显示剩余图片数量
                    if (urls.size > 3) {
                        params = ConstraintLayout.LayoutParams(-2, -2)
                        params.bottomToBottom = 0
                        params.rightToRight = 0
                        params.marginEnd =
                            mBinding.root.context.resources.getDimensionPixelSize(R.dimen.dp_10)
                        params.bottomMargin =
                            mBinding.root.context.resources.getDimensionPixelSize(R.dimen.dp_10)
                        val picNumberView = TextView(mBinding.root.context)
                        picNumberView.layoutParams = params
                        picNumberView.padding =
                            mBinding.root.context.resources.getDimensionPixelSize(R.dimen.dp_5)
                        picNumberView.setTextColor(Color.parseColor("#ffffff"))
                        picNumberView.setBackgroundResource(R.drawable.itinerary_bg_pic_number)
                        picNumberView.text = "${urls.size}图"
                        mBinding.posterLayout.addView(picNumberView)
                    }
                }
            }

        }

    }

    interface OnItemClick<T> : OnItemSelectedListener<T> {
        fun onRenamed(position: Int, id: String)
    }
}