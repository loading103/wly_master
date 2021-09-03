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
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.BrandMDD
import com.daqsoft.provider.databinding.ScCityCardStyleFourBinding
import com.daqsoft.provider.databinding.ScCityCardStyleFourViewPager2ItemBinding
import com.daqsoft.provider.dp
import com.daqsoft.provider.getRealImageUrl
import com.youth.banner.adapter.BannerAdapter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * @package name：com.daqsoft.provider.uiTemplate.titleBar.cityCard
 * @date 16/10/2020 下午 5:40
 * @author zp
 * @describe
 */
class SCCityCardStyleFour(context: Context) : SCCityCardStyleBase(context){

    private lateinit var bannerAdapter : SCCityCardStyleFourBannerAdapter

    private lateinit var mBinding : ScCityCardStyleFourBinding

    override fun initView() {
        mBinding = DataBindingUtil.inflate<ScCityCardStyleFourBinding>(
            LayoutInflater.from(context),
            R.layout.sc_city_card_style_four,
            this,
            false
        )
        bindDataToView(mBinding)
        this.addView(mBinding.root)
    }

    override fun cityCardDataChanged(data: MutableList<BrandMDD>) {
        bannerAdapter.setDatas(data)
        bannerAdapter.notifyDataSetChanged()
        mBinding.banner.currentItem = data.size / 2
        startLoop()
    }

    fun startLoop(initialDelay: Long = 0, period: Long = 3L){
        val count: Int = bannerAdapter.itemCount
        if (count == 0) {
            return
        }
        Observable.interval(initialDelay, period, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : io.reactivex.Observer<Long> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: Long) {
                    val next: Int = (mBinding.banner.currentItem + 1) % count
                    mBinding.banner.setCurrentItem(next,true)
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {
                }

            })
    }


    /**
     * 绑定数据到视图
     * @param mBinding ScInformationStyleOneBinding
     */
    fun bindDataToView(viewBinding: ScCityCardStyleFourBinding) {
        bannerAdapter = SCCityCardStyleFourBannerAdapter()

        viewBinding.banner.apply{

                offscreenPageLimit = 2
                val recyclerView = getChildAt(0) as RecyclerView
                recyclerView.run {
                    val padding = resources.getDimensionPixelOffset(R.dimen.dp_20)
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
                        if (position != 0f) {
                            page.translationX = (pageWidth * scaleValue) * -position
                            page.translationZ = -Math.abs(position)
                        }
                        page.scaleX = scaleValue
                        page.scaleY = scaleValue

                        val alphaValue = Math.min(1 - Math.abs(position) * 0.2f, 1f)
                        if (position < -1 || position > 1) {
                            page.alpha = 0f
                        } else {
                            page.alpha = alphaValue
                        }
                    }
                })
                setPageTransformer(compositePageTransformer)

                adapter = bannerAdapter
                currentItem = 1
        }

    }

    inner class SCCityCardStyleFourBannerAdapter : BannerAdapter<BrandMDD, SCCityCardStyleFourBannerAdapter.BannerItemHolder>(
        arrayListOf()
    ){

        inner class BannerItemHolder(val binding: ScCityCardStyleFourViewPager2ItemBinding):RecyclerView.ViewHolder(
            binding.root
        )

        override fun onCreateHolder(parent: ViewGroup?, viewType: Int): BannerItemHolder {
            val mBinding = DataBindingUtil.inflate<ScCityCardStyleFourViewPager2ItemBinding>(
                LayoutInflater.from(context),
                R.layout.sc_city_card_style_four_view_pager_2_item,
                parent,
                false
            )
            return BannerItemHolder(mBinding)
        }

        override fun onBindView(
            holder: BannerItemHolder,
            data: BrandMDD,
            position: Int,
            size: Int
        ) {
            Timber.e("size ${size}")
            holder.binding.apply {

                    // 图
                    Glide
                        .with(root.context)
                        .load(data.images.getRealImageUrl())
                        .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(5)))
                        .placeholder(R.mipmap.placeholder_img_fail_240_180)
                        .into(image)



                title.text = data.name

                content.text = data.slogan

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

