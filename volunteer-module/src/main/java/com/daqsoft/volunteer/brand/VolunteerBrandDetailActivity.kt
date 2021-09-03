package com.daqsoft.volunteer.brand

import android.content.Intent
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.view.convenientbanner.holder.BaseBannerImageHolder
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.bean.VolunteerActivityBean
import com.daqsoft.volunteer.bean.VolunteerBrandBean
import com.daqsoft.volunteer.bean.VolunteerBrandDetailBean
import com.daqsoft.volunteer.databinding.VolunteerBrandDetailBinding
import com.daqsoft.volunteer.main.adapter.VolunteerActivityAdapter
import com.daqsoft.volunteer.net.VolunteerRepository
import me.nereo.multi_image_selector.BigImgActivity

/**
 *@package:com.daqsoft.volunteer.brand
 *@date:2020/6/8 16:09
 *@author: caihj
 *@des:品牌详情
 **/
@Route(path = ARouterPath.VolunteerModule.VOLUNTEER_BRAND_DETAIL)
class VolunteerBrandDetailActivity:TitleBarActivity<VolunteerBrandDetailBinding,VolunteerBrandDetailVM>() {

    @Autowired
    @JvmField
    var id = ""

    private val activityAdapter = VolunteerActivityAdapter(this,1)


    override fun getLayout(): Int = R.layout.volunteer_brand_detail

    override fun setTitle(): String = getString(R.string.volunteer_module_volunteer_brand_detail_title)

    override fun injectVm(): Class<VolunteerBrandDetailVM> = VolunteerBrandDetailVM::class.java

    override fun initView() {
        mModel.brandBean.observe(this, Observer {
            // 图片相关
            if (!it.image.isNullOrEmpty()) {
                val images = it.image.split(",")
                if (!images.isNullOrEmpty()) {
                    mBinding?.txtCurrentIndex.text = "1"
                    mBinding?.txtTotalSize.text = "/${images.size}"
                    mBinding?.vpBrand.setPages(object : CBViewHolderCreator {
                        override fun createHolder(itemView: View?): Holder<*> {
                            return BaseBannerImageHolder(itemView!!)
                        }

                        override fun getLayoutId(): Int {
                            return R.layout.holder_img_base
                        }
                    }, images).setCanLoop(false).setPointViewVisible(false).setOnItemClickListener {
                        val intent =
                            Intent(this@VolunteerBrandDetailActivity, BigImgActivity::class.java)
                        intent.putExtra("position", it)
                        intent.putStringArrayListExtra(
                            "imgList",
                            ArrayList( images)
                        )
                        startActivity(intent)
                    }
                    if (images.size > 1) {
                        mBinding?.vpBrand.setCanLoop(true)
                    } else {
                        mBinding?.vpBrand.setCanLoop(false)
                    }
                    mBinding?.vpBrand?.onPageChangeListener = object : OnPageChangeListener {
                        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                        }

                        override fun onPageSelected(index: Int) {
                            mBinding?.txtCurrentIndex.text = ((index + 1).toString())
                            mBinding?.txtTotalSize.text = "/${images.size}"
                        }

                        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                        }

                    }
                }

            }
            mBinding.tvTitle.text = it.name
            mBinding.tvDeclaration.text = it.declaration
            mBinding.tvUnit.text = it.manageUnit
            mBinding.tvAddress.text = it.serviceRegionName
            mBinding.tvContent.text = it.introduce
        })
        mBinding.rvActivities.layoutManager = LinearLayoutManager(this)
        mBinding.rvActivities.adapter = activityAdapter
        mModel.activitys.observe(this, Observer {
            if(it.size > 0){
                mBinding.tvNum.text = "(${it.size})"
                activityAdapter.add(it)
            }else{
                mBinding.tvServiceActivityTitle.visibility = View.GONE
                mBinding.tvNum.visibility = View.GONE
                mBinding.tvYear.visibility = View.GONE
                mBinding.rvActivities.visibility = View.GONE
            }
        })
    }

    override fun initData() {
        mModel.getBrandDetail(id)
        mModel.getVolunteerActivity(id)
    }
}


class VolunteerBrandDetailVM:BaseViewModel(){

    var brandBean = MutableLiveData<VolunteerBrandDetailBean>()


    fun getBrandDetail(id:String){
        VolunteerRepository.service.getVolunteerBrandDetail(id).excute(object :BaseObserver<VolunteerBrandDetailBean>(){
            override fun onSuccess(response: BaseResponse<VolunteerBrandDetailBean>) {
                brandBean.postValue(response.data)
            }
        })
    }

    var activitys = MutableLiveData<MutableList<VolunteerActivityBean>>()
    var year = "2020"
    fun getVolunteerActivity(id:String){
        val params = HashMap<String,String>()
        params["belongBrand"] = id
        params["year"] = year
        params["pageSize"] = "10"
        params["currPage"] = "1"
        VolunteerRepository.service.getVolunteerActivityList(params).excute(object :BaseObserver<VolunteerActivityBean>(){
            override fun onSuccess(response: BaseResponse<VolunteerActivityBean>) {
                activitys.postValue(response.datas)
            }

        })
    }

}