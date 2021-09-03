package com.daqsoft.volunteer.brand

import android.annotation.SuppressLint
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.bean.VolunteerBrandBean
import com.daqsoft.volunteer.databinding.ActivityVolunteerBrandListBinding
import com.daqsoft.volunteer.databinding.VolunteerBrandItemBinding
import com.daqsoft.volunteer.net.VolunteerRepository
import com.daqsoft.volunteer.utils.JumpUtils

/**
 *@package:com.daqsoft.volunteer.brand
 *@date:2020/6/8 15:15
 *@author: caihj
 *@des:志愿品牌列表
 **/
@Route(path = ARouterPath.VolunteerModule.VOLUNTEER_BRAND_LIST)
class VolunteerBrandListActivity:TitleBarActivity<ActivityVolunteerBrandListBinding,VolunteerBandListVM>() {
    override fun getLayout(): Int = R.layout.activity_volunteer_brand_list

    override fun setTitle(): String = getString(R.string.volunteer_module_volunteer_brand_title)

    override fun injectVm(): Class<VolunteerBandListVM> = VolunteerBandListVM::class.java

    override fun initView() {
        mBinding.sfBrand.setOnRefreshListener {
            mModel.mCurrentPage = 1
            brandAdapter.clear()
            mModel.getBrandList()
        }
        mModel.brands.observe(this, Observer {
            pageDel(it)
        })
        mBinding.rvBrand.layoutManager = LinearLayoutManager(this)
        mBinding.rvBrand.adapter = brandAdapter
        brandAdapter.setOnLoadMoreListener {
            mModel.mCurrentPage++
            mModel.getBrandList()
        }
    }

    private fun pageDel(datas:MutableList<VolunteerBrandBean>){
        if (mModel.mCurrentPage == 1) {
            mBinding.rvBrand.smoothScrollToPosition(0)
            mBinding.rvBrand.visibility = View.VISIBLE
            brandAdapter.clear()
            brandAdapter.emptyViewShow = datas.isNullOrEmpty()
        }
        if (!datas.isNullOrEmpty()) {
            brandAdapter.add(datas)
        }
        if (datas.isNullOrEmpty() || datas.size < mModel.pageSize) {
            brandAdapter.loadEnd()
        } else {
            brandAdapter.loadComplete()
        }
        dissMissLoadingDialog()
    }

    override fun initData() {
        mModel.getBrandList()
    }

    private val brandAdapter = object :RecyclerViewAdapter<VolunteerBrandItemBinding,VolunteerBrandBean>(R.layout.volunteer_brand_item){
        @SuppressLint("SetTextI18n")
        override fun setVariable(
            mBinding: VolunteerBrandItemBinding,
            position: Int,
            item: VolunteerBrandBean
        ) {
            Glide.with(this@VolunteerBrandListActivity).load(item.image).placeholder(R.mipmap.placeholder_img_fail_h158).into(mBinding.ivCover)
            mBinding.tvTitle.text = item.name
            mBinding.tvDeclaration.text = item.declaration
            if(item.activityNum > 0){
                mBinding.tvActivity.text = "${item.activityNum}个志愿服务活动"
            }
            mBinding.root.onNoDoubleClick {
                JumpUtils.gotoBrandDetail(item.id.toString())
            }
        }

    }
}

class VolunteerBandListVM:BaseViewModel(){

    var brands = MutableLiveData<MutableList<VolunteerBrandBean>>()
    var mCurrentPage = 1
    var pageSize = 10


    fun getBrandList(){
        VolunteerRepository.service.getVolunteerBrandList(pageSize,mCurrentPage).excute(object :BaseObserver<VolunteerBrandBean>(){
            override fun onSuccess(response: BaseResponse<VolunteerBrandBean>) {
                brands.postValue(response.datas)
            }

        })
    }

}