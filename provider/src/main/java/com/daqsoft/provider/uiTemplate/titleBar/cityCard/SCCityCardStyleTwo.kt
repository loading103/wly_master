package com.daqsoft.provider.uiTemplate.titleBar.cityCard

import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.daqsoft.baselib.extend.dp
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.BrandMDD
import com.daqsoft.provider.databinding.ScCityCardStyleTwoBinding
import com.daqsoft.provider.databinding.ScCityCardStyleTwoViewPager2ItemBinding
import com.daqsoft.provider.getRealImageUrl
import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.listener.OnPageChangeListener
import kotlinx.android.synthetic.main.sc_city_card_style_one.view.*
import kotlin.math.abs

/**
 * @package name：com.daqsoft.provider.uiTemplate.titleBar.cityCard
 * @date 16/10/2020 下午 5:40
 * @author zp
 * @describe
 */
class SCCityCardStyleTwo(context: Context) : SCCityCardStyleBase(context){

    private lateinit var viewPager2Adapter : SCCityCardStyleTwoViewPager2Adapter

    override fun initView() {
        val mBinding = DataBindingUtil.inflate<ScCityCardStyleTwoBinding>(
            LayoutInflater.from(context),
            R.layout.sc_city_card_style_two,
            this,
            false
        )
        bindDataToView(mBinding)
        this.addView(mBinding.root)
    }

    override fun cityCardDataChanged(data: MutableList<BrandMDD>) {
        viewPager2Adapter.setDatas(data)
        viewPager2Adapter.notifyDataSetChanged()
    }

    /**
     * 绑定数据到视图
     * @param mBinding ScInformationStyleOneBinding
     */
    fun bindDataToView(viewBinding: ScCityCardStyleTwoBinding) {
        viewPager2Adapter = SCCityCardStyleTwoViewPager2Adapter()

        viewBinding.banner.apply{
            with(viewPager2) {
                offscreenPageLimit = 3
                val recyclerView = getChildAt(0) as RecyclerView
                recyclerView.run {
                    val dm: DisplayMetrics = resources.displayMetrics
                    val width: Int = dm.widthPixels
                    val margin = this@apply.marginLeft + this@apply.marginRight
                    val padding = (width - margin) / 5 * 3
                    setPadding(0, 0, padding, 0)
                    clipToPadding = false
                }
                val compositePageTransformer = CompositePageTransformer()
                compositePageTransformer.addTransformer(object : ViewPager2.PageTransformer {
                    val MAX_SCALE = 1.0f
                    val MIN_SCALE = 0.9f
                    override fun transformPage(page: View, position: Float) {
                        var pos = position
                        if (position < -1) {
                            pos = -1f
                        } else if (position > 1) {
                            pos = 1f
                        }
                        val tempScale = if (pos < 0) 1 + pos else 1 - pos
                        val slope: Float = (MAX_SCALE - MIN_SCALE) / 1
                        val scaleValue: Float = MIN_SCALE + tempScale * slope
                        val pageWidth = page.width
                        val deviation = pageWidth - (pageWidth * scaleValue)
                        if(position != 0f){
                            page.translationX = -deviation/2 * Math.abs(1-position)
                        }

                        page.scaleX = scaleValue
                        page.scaleY = scaleValue
                    }
                })
                setPageTransformer(compositePageTransformer)
            }
            setAdapter(viewPager2Adapter)

            addOnPageChangeListener(object : OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    val item = viewPager2Adapter.getData(position)
                    viewBinding.title.text = item.name
                    viewBinding.content.text = item.slogan
                }

                override fun onPageScrollStateChanged(state: Int) {
                }

            })

            currentItem = 1
        }


    }


    inner class SCCityCardStyleTwoViewPager2Adapter : BannerAdapter<BrandMDD, SCCityCardStyleTwoViewPager2Adapter.ViewPager2ItemHolder>(
        arrayListOf()
    ){


        inner class ViewPager2ItemHolder(val binding: ScCityCardStyleTwoViewPager2ItemBinding):RecyclerView.ViewHolder(
            binding.root
        )

        override fun onCreateHolder(parent: ViewGroup?, viewType: Int): ViewPager2ItemHolder {
            val mBinding = DataBindingUtil.inflate<ScCityCardStyleTwoViewPager2ItemBinding>(
                LayoutInflater.from(context),
                R.layout.sc_city_card_style_two_view_pager_2_item,
                parent,
                false
            )
            return ViewPager2ItemHolder(mBinding)
        }

        override fun onBindView(
            holder: ViewPager2ItemHolder,
            data: BrandMDD,
            position: Int,
            size: Int
        ) {
            holder.binding.apply {

                    // 图
                    Glide
                        .with(root.context)
                        .load(data.images.getRealImageUrl())
                        .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(5)))
                        .placeholder(R.mipmap.placeholder_img_fail_240_180)
                        .into(image)

                root.onNoDoubleClick {
                    ARouter.getInstance().build(MainARouterPath.MAIN_MDD_CITY_INFO)
                        .withString("id", data.id.toString())
                        .withString("region", data.region)
                        .navigation()
                }
            }
        }
    }

}