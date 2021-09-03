package com.daqsoft.volunteer.volunteer

import android.content.Intent
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.network.home.bean.ItemAddressBean
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.databinding.ActivityAddressChoiceBinding
import com.daqsoft.volunteer.databinding.ItemAdressBinding

/**
 * @Description 时光的添加地点页面
 * @ClassName   StoryAddressActivity
 * @Author      HuangXi
 * @Time        2020/2/19 13:47
 */
@Route(path = ARouterPath.VolunteerModule.ADDRESS_SEARCH)
class AddressActivity : TitleBarActivity<ActivityAddressChoiceBinding, StoryAddressModel>() {


    @JvmField
    @Autowired
    var resourceId = ""

    /**
     * 每页数量
     */
    var pageSize = 6

    /**
     * 关键字
     */
    var keyword = ""

    /**
     * 经纬度
     */
    var longitude: Double = 0.0
    /**
     * 纬度
     */
    var latitude: Double = 0.0

    /**
     * 距离
     */
    var distance = 1000000
    /**
     * 当前页码
     */
    var currPage = 1

    override fun getLayout(): Int = R.layout.activity_address_choice

    override fun setTitle(): String = "搜索地点"

    override fun injectVm(): Class<StoryAddressModel> = StoryAddressModel::class.java

    var mAdapter = object : RecyclerViewAdapter<ItemAdressBinding, ItemAddressBean>(
        R.layout.item_adress
    ) {
        override fun setVariable(
            mBinding: ItemAdressBinding,
            position: Int,
            item: ItemAddressBean
        ) {
            mBinding.item = item
            if(!item.name.isNullOrEmpty()){
                mBinding.name=item.name.replace("<em>", "").replace("</em>", "")
            }
            if (item.status) {
                mBinding.ivItemAddressStatus.visibility = View.VISIBLE
            } else {
                mBinding.ivItemAddressStatus.visibility = View.GONE
            }
            mBinding.root.setOnClickListener {
                var intent = Intent()
                intent.putExtra("address", item)
                setResult(0x0004, intent)
                finish()
            }
        }

    }

    override fun initView() {
        mBinding.recyclerList.apply {
            layoutManager = LinearLayoutManager(
                this@AddressActivity, LinearLayoutManager
                    .VERTICAL, false
            )
            adapter = mAdapter
        }
        mBinding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                keyword = mBinding.etSearch.text.toString().trim()
                currPage = 1
                mModel.getSearchAround(pageSize, keyword, longitude, latitude, distance, currPage)
            }

        })
        mAdapter.setOnLoadMoreListener {
            currPage++
            mModel.getSearchAround(pageSize, keyword, longitude, latitude, distance, currPage)
        }
        initSelfLocation()
        mBinding.tvShowSelfAddress.setOnClickListener {
            if (!mBinding.tvShowSelfAddress.isSelected) {
                mBinding.tvSelfAddress.visibility = View.GONE
                mBinding.tvShowSelfAddress.setText("显示所在位置")
                mBinding.tvShowSelfAddress.isSelected = true
            } else {
                mBinding.tvSelfAddress.visibility = View.VISIBLE
                mBinding.tvShowSelfAddress.setText("不显示所在位置")
                mBinding.tvShowSelfAddress.isSelected = false
            }

        }
        mBinding.tvSelfAddress.setOnClickListener {
            initSelfLocation()
        }
    }

    override fun initData() {
    }

    /**
     * 获取当前位置
     */
    private fun initSelfLocation() {
        GaoDeLocation().init(this, object : GaoDeLocation.OnGetCurrentLocationLisner {
            override fun onResult(
                adCode: String?,
                result: String?,
                lat: Double,
                lon: Double,
                adcode: String?
            ) {
                latitude = lat
                longitude = lon
                mBinding.tvSelfAddress.setText("我的位置：" + result.toString())
                mModel.getSearchAround(pageSize, keyword, longitude, latitude, distance, currPage)
            }

            override fun onError(errorMsg: String?) {
                latitude = 0.0
                longitude = 0.0
                mBinding.tvSelfAddress.setText("未能获取当前位置，请开启定位")
                mModel.getSearchAround(pageSize, keyword, longitude, latitude, distance, currPage)
            }

        })
    }

    override fun notifyData() {
        super.notifyData()
        mModel.datas.observe(this, Observer {
            pageDeal(currPage, it, mAdapter)
            if (it != null && it.code == 0 && it.datas != null && it.datas!!.size > 0) {
                for (item in it.datas!!) {
                    item.status = false
                    if (item.resourceId.toString().equals(resourceId)) {
                        item.status = true
                    }
                }
                mAdapter.add(it.datas!!)
                mAdapter.notifyDataSetChanged()
            }

        })
    }

    /**
     * 列表数据页码处理
     *
     * @param page     当前页
     * @param response 返回数据体
     * @param adapter  适配器
     */
    private fun pageDeal(
        page: Int?,
        response: BaseResponse<*>,
        adapter: RecyclerViewAdapter<*, *>
    ) {
        if (page == null) {
            return
        }
        if (page == 1) {
            adapter.clear()
        }
        if (response.page == null) {
            adapter.loadEnd()
            return
        }
        if (response.page!!.currPage < response.page!!.totalPage) {
            adapter.loadComplete()
        } else {
            adapter.loadEnd()
        }
    }
}

/**
 * @Description 时光的添加地点页面ViewModel
 * @ClassName   StoryAddressActivity
 * @Author      HuangXi
 * @Time        2020/2/19 13:47
 */
class StoryAddressModel : BaseViewModel() {

    /**
     * 数据集
     */
    var datas = MutableLiveData<BaseResponse<ItemAddressBean>>()


    /**
     * 获取添加地点（搜索周边）
     * @param pageSize 页面大小
     * @param keyword 关键字
     * @param longitude 经度
     * @param latitude 纬度
     * @param distance 距离
     * @param currPage 当前页
     */
    fun getSearchAround(
        pageSize: Int, keyword: String,
        longitude: Double, latitude: Double,
        distance: Int, currPage: Int
    ) {
        mPresenter.value?.loading = true
        HomeRepository.service.getSearchAround(
            pageSize,
            keyword,
            longitude,
            latitude,
            distance,
            currPage
        )
            .excute(object : BaseObserver<ItemAddressBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ItemAddressBean>) {
                    datas.postValue(response)
                }

            })
    }


}