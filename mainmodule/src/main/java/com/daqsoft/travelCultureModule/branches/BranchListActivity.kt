package com.daqsoft.travelCultureModule.branches

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.adapter.setImageUrlqwx
import com.daqsoft.baselib.base.*
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.integralmodule.repository.IntegralRepository
import com.daqsoft.integralmodule.repository.bean.SiteInfoBean
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemBrandBinding
import com.daqsoft.mainmodule.databinding.MainActivityBranchListBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.HomeBranchBean
import com.daqsoft.provider.utils.GaosiUtils
import com.daqsoft.thetravelcloudwithculture.home.bean.CityCardDetail
import com.daqsoft.travelCultureModule.net.MainRepository
import com.scwang.smart.refresh.layout.util.SmartUtil.dp2px
import java.util.*


/**
 * @Description 文化旅游品牌列表
 * @ClassName   BranchListActivity
 * @Author      PuHua
 * @Time        2019/12/20 17:28
 */
@Route(path = MainARouterPath.MAIN_BRANCH_LIST)
class BranchListActivity : TitleBarActivity<MainActivityBranchListBinding, BranchListActivityViewModel>() {

    @JvmField
    @Autowired
    var siteId: String = "1"

    /**
     * 品牌展播的适配器
     */
    private val xBranchGalleryAdapter = XHBranchGalleryAdapter()
    private var isFirstTab = true //切换切换
    override fun getLayout(): Int = R.layout.main_activity_branch_list

    override fun setTitle(): String = getString(R.string.culture_and_tourism_brand)

    override fun injectVm(): Class<BranchListActivityViewModel> = BranchListActivityViewModel::class.java
    var adapter = object : RecyclerViewAdapter<ItemBrandBinding, HomeBranchBean>(R.layout.item_brand) {
        override fun setVariable(mBinding: ItemBrandBinding, position: Int, item: HomeBranchBean) {
            mBinding.tvItemBrandName.text = item.name
            mBinding.tvItemBrandIntroduce.text = item.slogan

            if(BaseApplication.appArea=="sc"){
                mBinding.ivClubHead.visibility=View.VISIBLE
            }else{
                mBinding.ivClubHead.visibility=View.INVISIBLE
            }
            if (!item.relationResourceNameStr.isNullOrEmpty()) {
                var ss = item.relationResourceNameStr.split(",")
                if (ss.size >= 3) {
                    ss = ss.subList(0, 3)
                }
//                mBinding.tvItemBrandOther.text = DividerTextUtils.convertDotString(ss)
            }
            setImageUrlqwx(
                mBinding.ivItemBrandBackground, item.brandImage, AppCompatResources.getDrawable(
                    BaseApplication.context, R.drawable.placeholder_img_fail_h300
                ), 5
            )
            var data_color = item.mainColor.split(",")
            if (data_color.size == 3) {
                var color = Color.rgb(data_color[0].toInt(), data_color[1].toInt(), data_color[2].toInt())
                val colors = intArrayOf(color, -0x00) //分别为开始颜色，中间夜色，结束颜色
                val gd = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors) //创建drawable
                // gd.alpha=240
                gd.cornerRadius = dp2px(5F).toFloat()
                mBinding.rlItemBrand.background = gd
            }
            mBinding.llItemBrand.setOnClickListener {
                ARouter.getInstance().build(MainARouterPath.MAIN_BRANCH_DETAIL)
                    .withString("id", item.id)
                    .navigation()
            }
        }

    }

    override fun initView() {
        val tagLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.llSecond.rvBrand.layoutManager = tagLayoutManager
        mBinding.llSecond.rvBrand.adapter = adapter
        mBinding.ivBrandChange.setOnClickListener {
            mBinding.llSecond.clBrandSecond.visibility = View.VISIBLE
            mBinding.clBrandFirst.visibility = View.GONE
        }
        mBinding.llSecond.ivBrandChangeSecond.setOnClickListener {
            mBinding.llSecond.clBrandSecond.visibility = View.GONE
            mBinding.clBrandFirst.visibility = View.VISIBLE
        }
        mModel.siteBean.observe(this, Observer {
            mBinding.tvBranchName.text = it.name + "文旅品牌"
            mBinding.llSecond.tvBrand2Name.text = it.name + "文旅品牌"
            if (it.siteLogo != "") {
//                setCenterCropImage(mBinding.llSecond.ivBrand2Logo, it.siteLogo, true)
                val options = RequestOptions()
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .skipMemoryCache(true)
                Glide.with(this)
                    .load(it.siteLogo)
                    .apply(options)
                    .into(mBinding.llSecond.ivBrand2Logo)
            } else {
                mBinding.llSecond.ivBrand2Logo.visibility = View.GONE
            }
        })
        mModel.homeBranchBeanList.observe(this, Observer {
            adapter.add(it)
            xBranchGalleryAdapter.menus.addAll(it)
            xBranchGalleryAdapter.notifyDataSetChanged()
            if (it.size > 0) {
                Glide.with(this)
                    .asBitmap()
                    .load(StringUtil.getImageUrl(it[0].brandImage, 335, 187))
                    .skipMemoryCache(true)
                    .centerCrop()
                    .override(335, 187)
                    .into(object : CustomTarget<Bitmap>() {

                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

                            var b = GaosiUtils.rsBlur(applicationContext, resource, 15)

                            mBinding.gaoSiImage.setImageBitmap(b)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                    })
                //景点数量
                val scenicNumber = it[0].relationResourceCount
                //城市数量
                val cityNumber = it[0].siteCount

                mBinding.tvBrandJpNum.text = scenicNumber.toString()
                mBinding.tvBrandCityNum.text = cityNumber
                if (scenicNumber == 0 && cityNumber == "0") {
                    mBinding.wayLayout.visibility = View.GONE
                    mBinding.viewLine.visibility = View.GONE
                } else {
                    mBinding.wayLayout.visibility = View.VISIBLE
                    mBinding.viewLine.visibility = View.VISIBLE
                }
            }
        })

        mModel.visitingCard.observe(this, Observer {
            mBinding.viewProfile.text = it.summary
            if (!it.summary?.isNullOrEmpty()) {
                mBinding.llSecond.tvBrand2Content.text = it.summary
            }
            Glide.with(mBinding.gaoSiImage)
                .load(it.logo)
                .into(mBinding.viewLogo)
        })

        mBinding.xBranchGallery.setAdapter(xBranchGalleryAdapter)
        mBinding.xBranchGallery.setPageTransformer(BranchScaleTransformer())
        mBinding.xBranchGallery.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                //景点数量
                val scenicNumber = mModel.homeBranchBeanList.value!![position].relationResourceCount
                //城市数量
                val cityNumber = mModel.homeBranchBeanList.value!![position].siteCount
                mBinding.tvBrandJpNum.text = scenicNumber.toString()
                mBinding.tvBrandCityNum.text = cityNumber
                if (scenicNumber == 0 && cityNumber == "0") {
                    mBinding.wayLayout.visibility = View.GONE
                    mBinding.viewLine.visibility = View.GONE
                } else {
                    mBinding.wayLayout.visibility = View.VISIBLE
                    mBinding.viewLine.visibility = View.VISIBLE
                }

                Glide.with(mBinding.gaoSiImage)
                    .asBitmap()
                    .load(StringUtil.getImageUrl(mModel.homeBranchBeanList.value!![position].brandImage, 335, 187))
                    .skipMemoryCache(true)
                    .centerCrop()
                    .override(335, 187)
                    .into(object : CustomTarget<Bitmap>() {

                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

                            var b = GaosiUtils.rsBlur(applicationContext, resource, 15)
                            mBinding.gaoSiImage.setImageBitmap(b)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                    })
            }
        })


    }

    override fun initData() {

        mModel.getBranchList(siteId)
        mModel.siteInfo()
        mModel.getVisitingCard()
    }
}

class BranchListActivityViewModel : BaseViewModel() {

    /**
     * 品牌展播
     */
    var homeBranchBeanList = MutableLiveData<MutableList<HomeBranchBean>>()

    /**
     * 品牌展播列表
     */
    fun getBranchList(siteId: String?) {
        mPresenter.value?.loading = true
        val param = HashMap<String, String>()
        // queryType  【选填】默认为1 1 首页/品牌名片 2 城市名片 3 随机推荐
        if(!siteId.isNullOrBlank()){
            param["siteId"] = siteId
        }
        param["queryType"] = "1"
        // 活动类型id
        param["pageSize"] = "100"
        param["currPage"] = "1"
        MainRepository.service.getBranchList(param).excute(object : BaseObserver<HomeBranchBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<HomeBranchBean>) {
                homeBranchBeanList.postValue(response.datas)
            }
        })
    }

    /**
     * 站点详情
     */
    var siteBean = MutableLiveData<SiteInfoBean>()

    fun siteInfo() {
        IntegralRepository().siteInfo()
            .excute(object : BaseObserver<SiteInfoBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<SiteInfoBean>) {
                    siteBean.postValue(response.data)
                }
            })
    }

    /**城市名片*/
    val visitingCard = MutableLiveData<CityCardDetail>()

    fun getVisitingCard() {
        IntegralRepository().getVisitingCard().excute(object : BaseObserver<CityCardDetail>() {
            override fun onSuccess(response: BaseResponse<CityCardDetail>) {
                visitingCard.postValue(response.data)
            }
        })
    }
}