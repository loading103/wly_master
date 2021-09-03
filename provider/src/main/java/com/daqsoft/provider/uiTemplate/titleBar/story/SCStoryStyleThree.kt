package com.daqsoft.provider.uiTemplate.titleBar.story

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.extend.dp
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.adapter.ReadStoryAdapter
import com.daqsoft.provider.bean.HomeStoryTagBean
import com.daqsoft.provider.bean.StoreBean
import com.daqsoft.provider.databinding.ScStoryStyleThreeBinding
import com.daqsoft.provider.databinding.ScStoryStyleThreeRecycleViewItemBinding
import com.daqsoft.provider.databinding.ScStoryStyleThreeTypeRecycleViewItemBinding
import com.daqsoft.provider.net.ProviderApi
import com.daqsoft.provider.view.GridDecoration
import com.daqsoft.provider.view.extend.onModuleNoDoubleClick
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import org.jetbrains.anko.padding
import org.jetbrains.anko.windowManager
import java.util.logging.SimpleFormatter

/**
 * @package name：com.daqsoft.provider.uiTemplate.titleBar.story
 * @date 12/10/2020 上午 10:06
 * @author zp
 * @describe 故事样式3
 */

class SCStoryStyleThree(context: Context) : SCStoryStyleBase(context){

    private lateinit var storyAdapter : StyleThreeRecyclerViewAdapter

    private lateinit var typeAdapter: StyleThreeTypeRecyclerViewAdapter

    lateinit var mBinding : ScStoryStyleThreeBinding

    override fun initView() {
        mBinding = DataBindingUtil.inflate<ScStoryStyleThreeBinding>(
            LayoutInflater.from(context),
            R.layout.sc_story_style_three,
            this,
            false
        )
        mBinding.recycleView.visibility = View.GONE
        bindDataToView(mBinding)
        this.setBackgroundColor(resources.getColor(R.color.white))
        this.addView(mBinding.root)
    }

    override fun storyDataChanged(data: MutableList<StoreBean>) {
        mBinding.recycleView.visibility = View.VISIBLE
        storyAdapter.clear()
        storyAdapter.add(data)
        storyAdapter.notifyDataSetChanged()
    }

    override fun storyTypeDataChanged(data: MutableList<HomeStoryTagBean>) {
        typeAdapter.clear()
        typeAdapter.add(data)
        typeAdapter.notifyDataSetChanged()
    }

    /**
     * 绑定数据到视图
     * @param mBinding ScInformationStyleOneBinding
     */
    fun bindDataToView(viewBinding: ScStoryStyleThreeBinding) {
        storyAdapter = StyleThreeRecyclerViewAdapter()
        typeAdapter = StyleThreeTypeRecyclerViewAdapter()

        // 类型
        with(viewBinding.recycleViewType){
            adapter = typeAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    val position = getChildAdapterPosition(view)
                    val count = state.itemCount - 1
                    if (position != count){
                        outRect.right = 12.dp
                    }
                }
            })
        }

        // 故事
        with(viewBinding.recycleView){
            adapter = storyAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(object : RecyclerView.ItemDecoration(){
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    outRect.bottom = 30.dp
                }
            })
        }
    }


    inner class StyleThreeTypeRecyclerViewAdapter : RecyclerViewAdapter<ScStoryStyleThreeTypeRecycleViewItemBinding, HomeStoryTagBean>(
        R.layout.sc_story_style_three_type_recycle_view_item) {
        @SuppressLint("CheckResult", "SetTextI18n")
        override fun setVariable(
            mBinding: ScStoryStyleThreeTypeRecycleViewItemBinding,
            position: Int,
            item: HomeStoryTagBean
        ) {
            // 标题
            mBinding.title.text = "#${item.name}#"

            // 图
            Glide.with(mBinding.root.context)
                .load(item.cover)
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .into(mBinding.image)

            // item 点击
            mBinding.root.onNoDoubleClick {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_STORY_TAG_DETAIL)
                    .withString("id", item.id)
                    .navigation()
            }
        }

    }


    inner class StyleThreeRecyclerViewAdapter : RecyclerViewAdapter<ScStoryStyleThreeRecycleViewItemBinding, StoreBean>(
        R.layout.sc_story_style_three_recycle_view_item) {

        override fun setVariable(mBinding: ScStoryStyleThreeRecycleViewItemBinding, position: Int, item: StoreBean) {

            mBinding.root.onModuleNoDoubleClick(
                context!!.resources.getString(R.string.venue_story),
                ProviderApi.REGION_MAIN_COLUMNS
            ) {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_STORY_DETAIL)
                    .withString("id", item.id)
                    .withInt("type", 1)
                    .navigation()
            }
            // 点赞
            if (item.likeNum == "0"){
                mBinding.like.visibility = View.GONE
            }
            mBinding.like.text = item.likeNum
            // 浏览
            if (item.showNum == "0"){
                mBinding.browse.visibility = View.GONE
            }
            mBinding.browse.text = item.showNum
            // 名称
            mBinding.title.text = item.vipNickName

            // 内容
            mBinding.content.text = item.content

            // 时间
            mBinding.time.text = DateUtil.formatDateByString(
                "yyyy-MM-dd",
                "yyyy年MM月dd日",
                item.createDate
            )

            // 图
            if (!item.images.isNullOrEmpty()) {
                mBinding.amount.text = context.getString(R.string.venue_story_image_number, item.images.size.toString())
                mBinding.amount.visibility = View.VISIBLE
            } else {
                mBinding.amount.visibility = View.GONE
            }
            mBinding.posterLayout.removeAllViews()
            var params = ConstraintLayout.LayoutParams(-1, -1)
            if (item.images.isNullOrEmpty()) {
                if (!item.cover.isNullOrEmpty()) {
                    mBinding.posterLayout.visibility = View.VISIBLE
                    val posterView = ImageView(mBinding.root.context)
                    posterView.id = R.id.view_poster
                    params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                    params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    posterView.layoutParams = params
                    Glide
                        .with(mBinding.root.context)
                        .load(item.cover)
                        .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(5.dp)))
                        .placeholder(R.mipmap.placeholder_img_fail_240_180)
                        .into(posterView)
                    mBinding.posterLayout.addView(posterView)
                } else {
                    mBinding.posterLayout.visibility = View.GONE
                }
            } else {
                mBinding.posterLayout.visibility = View.VISIBLE
                if (item.images.size == 1){
                    val posterView = ImageView(mBinding.root.context)
                    posterView.id = R.id.view_poster
                    params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                    params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    posterView.layoutParams = params
                    Glide
                        .with(mBinding.root.context)
                        .load(item.images[0])
                        .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(5.dp)))
                        .placeholder(R.mipmap.placeholder_img_fail_240_180)
                        .into(posterView)
                    mBinding.posterLayout.addView(posterView)
                }else {
                    val dm = DisplayMetrics()
                    //获取屏幕分辨率进行View排版
                    mBinding.root.context.windowManager.defaultDisplay.getRealMetrics(dm)
                    //添加第一个View
                    params.width = (dm.widthPixels * 0.6).toInt()
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
                        .load(item.images[0])
                        .apply(RequestOptions().transform(transform))
                        .placeholder(R.mipmap.placeholder_img_fail_240_180)
                        .into(posterView)

                    val width = (dm.widthPixels * 0.3).toInt()
                    val height = mBinding.root.context.resources.getDimensionPixelSize(R.dimen.dp_86)
                    if (item.images.size == 2) {
                        //添加第二个View
                        params = ConstraintLayout.LayoutParams(0, -1)
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
                            .load(item.images[1])
                            .apply(RequestOptions().transform(transform))
                            .placeholder(R.mipmap.placeholder_img_fail_240_180)
                            .into(posterViewTwo)

                    } else if (item.images.size > 2) {
                        //添加第二个View
                        params = ConstraintLayout.LayoutParams(0, height)
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
                            .load(item.images[1])
                            .apply(RequestOptions().transform(transformTwo))
                            .placeholder(R.mipmap.placeholder_img_fail_240_180)
                            .into(posterViewTwo)

                        //第三个View
                        params = ConstraintLayout.LayoutParams(0, height)
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
                            .load(item.images[2])
                            .apply(RequestOptions().transform(transformThree))
                            .placeholder(R.mipmap.placeholder_img_fail_240_180)
                            .into(posterViewThree)
                    }
                }
            }
        }
    }
}

