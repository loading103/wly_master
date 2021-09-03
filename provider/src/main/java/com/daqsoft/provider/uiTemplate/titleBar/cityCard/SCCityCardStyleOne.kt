package com.daqsoft.provider.uiTemplate.titleBar.cityCard

import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.BrandMDD
import com.daqsoft.provider.databinding.ScCityCardStyleOneBinding
import com.daqsoft.provider.databinding.ScCityCardStyleOneViewPager2ItemBinding
import com.daqsoft.provider.getRealImageUrl
import com.daqsoft.provider.getRealImages
import com.youth.banner.adapter.BannerAdapter
import kotlinx.android.synthetic.main.sc_city_card_style_one.view.*

/**
 * @package name：com.daqsoft.provider.uiTemplate.titleBar.cityCard
 * @date 16/10/2020 下午 5:40
 * @author zp
 * @describe
 */
class SCCityCardStyleOne(context: Context) : SCCityCardStyleBase(context){

    private lateinit var viewPager2Adapter : SCCityCardStyleOneViewPager2Adapter

    override fun initView() {
        val mBinding = DataBindingUtil.inflate<ScCityCardStyleOneBinding>(
            LayoutInflater.from(context),
            R.layout.sc_city_card_style_one,
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
    fun bindDataToView(viewBinding: ScCityCardStyleOneBinding) {
        viewPager2Adapter = SCCityCardStyleOneViewPager2Adapter()

        viewBinding.banner.apply{
            with(viewPager2) {
                offscreenPageLimit = 2
                val recyclerView = getChildAt(0) as RecyclerView
                recyclerView.run {

                    val dm: DisplayMetrics = resources.displayMetrics
                    val width: Int = dm.widthPixels
                    val margin = this@apply.marginLeft + this@apply.marginRight
                    val padding = (width - margin) / 2 + resources.getDimensionPixelOffset(R.dimen.dp_20)

                    setPadding(0, 0, padding, 0)
                    clipToPadding = false
                }
                val compositePageTransformer = CompositePageTransformer()
                compositePageTransformer.addTransformer(MarginPageTransformer(resources.getDimension(R.dimen.dp_10).toInt()))
                setPageTransformer(compositePageTransformer)
            }
            setAdapter(viewPager2Adapter)
            currentItem = 1
        }


    }


    inner class SCCityCardStyleOneViewPager2Adapter : BannerAdapter<BrandMDD,SCCityCardStyleOneViewPager2Adapter.ViewPager2ItemHolder>(
        arrayListOf()){

        inner class ViewPager2ItemHolder(val binding: ScCityCardStyleOneViewPager2ItemBinding):RecyclerView.ViewHolder(binding.root)

        override fun onCreateHolder(parent: ViewGroup?, viewType: Int): ViewPager2ItemHolder {
            val mBinding = DataBindingUtil.inflate<ScCityCardStyleOneViewPager2ItemBinding>(
                LayoutInflater.from(context),
                R.layout.sc_city_card_style_one_view_pager_2_item,
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
                // title
                title.text = data.name
                // slogan
                content.text = data.slogan
                // 图
                Glide
                    .with(root.context)
                    .load(data.images.getRealImageUrl())
                    .placeholder(R.mipmap.placeholder_img_fail_240_180)
                    .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(5)))
                    .into(image)
                root.onNoDoubleClick {
                    ARouter.getInstance().build(MainARouterPath.MAIN_MDD_CITY_INFO)
                        .withString("id", data.id.toString())
                        .withString("region", data.region)
                        .withString("name",data.name)
                        .navigation()
                }
            }
        }
    }

}