package com.daqsoft.travelCultureModule.complaint

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
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
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityChooseComplaintResourceBinding
import com.daqsoft.mainmodule.databinding.ItemComplaintResourceBinding
import com.daqsoft.mainmodule.databinding.ItemComplaintResourceTypeBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.CollectionTypeBean
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.network.home.bean.ComplaintResourceBean
import com.daqsoft.provider.utils.HtmlUtils

/**
 * @Description 旅游投诉选择被投诉方
 * @ClassName   ComplaintActivity
 * @Author      Huangxi
 * @Time        2020/2/28
 */
@Route(path = MainARouterPath.MAIN_COMPLAINT_RESOURCE_ACTIVITY)
class ChooseComplaintResourceActivity : TitleBarActivity<ActivityChooseComplaintResourceBinding,
        ComplaintResourceViewModel>() {
    /**
     * 地区编码
     */
    @JvmField
    @Autowired
    var region = ""
    /**
     * 类型
     */
    var resourceType = ResourceType.CONTENT_TYPE_SCENERY
    /**
     * 当前页码
     */
    var currPage = 1
    /**
     * 关键字
     */
    var keyword: String = ""
    /**
     * 类型筛选的条件
     */
    var typeList = ArrayList<CollectionTypeBean>()

    /**
     * 选择类型适配器
     */
    var typeAdapter =
        object : RecyclerViewAdapter<ItemComplaintResourceTypeBinding, CollectionTypeBean>(
            R.layout.item_complaint_resource_type
        ) {
            override fun setVariable(
                mBinding: ItemComplaintResourceTypeBinding,
                position: Int,
                item: CollectionTypeBean
            ) {

                mBinding.item = item
                mBinding.tvName.isSelected = false
                mBinding.tvName.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                if (item.checked) {
                    mBinding.tvName.isSelected = true
                    mBinding.tvName.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                    resourceType = item.value
                }

                mBinding.root.setOnClickListener {
                    for (type in typeList) {
                        type.checked = false
                        if (type.value.equals(item.value)) {
                            type.checked = true
                        }
                    }
                    resourceType = item.value
                    notifyDataSetChanged()
                    currPage = 1
                    mModel.getComplaintResource(currPage, keyword, resourceType, region)
                }
            }
        }
    /**
     * 选择类型适配器
     */
    var dataAdapter =
        object : RecyclerViewAdapter<ItemComplaintResourceBinding, ComplaintResourceBean>(
            R.layout.item_complaint_resource
        ) {
            override fun setVariable(
                mBinding: ItemComplaintResourceBinding,
                position: Int,
                item: ComplaintResourceBean
            ) {
                mBinding.item = item
                val nameStr = HtmlUtils.html2Str(item.name)
                val spannableString = SpannableString(nameStr)
                if (keyword.isNotEmpty() && item.name.contains(keyword)) {
                    var data = nameStr.split(keyword)
                    var content = ""
                    if (data != null && data.isNotEmpty()) {
                        for (value in data) {
                            content += value
                            if (content.length + keyword.length <= nameStr.length) {
                                spannableString.setSpan(
                                    ForegroundColorSpan(resources.getColor(R.color.colorPrimary)),
                                    content.length,
                                    content.length + keyword.length,
                                    Spanned.SPAN_INCLUSIVE_INCLUSIVE
                                )
                                content += keyword
                            }
                        }
                    }
                }
                mBinding.tvName.text = spannableString
                mBinding.root.setOnClickListener {
                    var intent = Intent()
                    intent.putExtra("resourceId", item.resourceId)
                    intent.putExtra("resourceType", item.resourceType)
                    intent.putExtra("respondent", item.name)
                    setResult(0x0002, intent)
                    finish()
                }
            }
        }

    override fun getLayout(): Int = R.layout.activity_choose_complaint_resource

    override fun setTitle(): String = "被投诉方"

    override fun injectVm(): Class<ComplaintResourceViewModel> =
        ComplaintResourceViewModel::class.java

    override fun initView() {
        mBinding.recyclerType.apply {
            layoutManager = LinearLayoutManager(
                this@ChooseComplaintResourceActivity,
                LinearLayoutManager.HORIZONTAL, false
            )
            adapter = typeAdapter
        }
        mBinding.recyclerList.apply {
            layoutManager = LinearLayoutManager(
                this@ChooseComplaintResourceActivity,
                LinearLayoutManager.VERTICAL, false
            )
            adapter = dataAdapter
        }
        mBinding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                keyword = mBinding.etSearch.text.toString().trim()
                currPage = 1
                mModel.getComplaintResource(currPage, keyword, resourceType, region)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
        mBinding.refreshList.setOnRefreshListener {
            currPage = 1
            mModel.getComplaintResource(currPage, keyword, resourceType, region)
        }
        dataAdapter.setOnLoadMoreListener {
            currPage++
            mModel.getComplaintResource(currPage, keyword, resourceType, region)
        }
        mBinding.tvCancel.setOnClickListener {
            mBinding.etSearch.setText("")
            keyword = ""
            currPage = 1
            mModel.getComplaintResource(currPage, keyword, resourceType, region)
        }
        mBinding.tvComplaint.setOnClickListener {
            var intent = Intent()
            intent.putExtra("resourceId", "")
            intent.putExtra("resourceType", "")
            intent.putExtra("respondent", mBinding.etSearch.text.toString().trim())
            setResult(0x0002, intent)
            finish()
        }
    }

    override fun initData() {
        var types = resources.getStringArray(R.array.complaint_resource)
        for (type in types) {
            var value = ""
            var check = false
            when (type) {
                "景区" -> value = ResourceType.CONTENT_TYPE_SCENERY
                "酒店" -> value = ResourceType.CONTENT_TYPE_HOTEL
                "场馆" -> value = ResourceType.CONTENT_TYPE_VENUE
                "餐饮" -> value = ResourceType.CONTENT_TYPE_RESTAURANT
                "农家乐" -> value = ResourceType.CONTENT_TYPE_AGRITAINMENT
                "研学基地" -> value = ResourceType.CONTENT_TYPE_RESEARCH_BASE
            }
            if (type == "景区") {
                check = true
            }
            var bean = CollectionTypeBean(type, value, check)
            typeList.add(bean)
        }
        typeAdapter.add(typeList)
        typeAdapter.notifyDataSetChanged()
        mModel.getComplaintResource(currPage, keyword, resourceType, region)
    }

    override fun notifyData() {
        super.notifyData()
        mModel.resource.observe(this, Observer {
            if (currPage == 1) {
//                mBinding.refreshList.isRefreshing = false
                mBinding.refreshList.finishRefresh()
            }
            if (it?.datas != null && it.datas!!.isNotEmpty()) {
                mBinding.llNoData.visibility = View.GONE
                mBinding.recyclerList.visibility = View.VISIBLE
                pageDeal(currPage, it, dataAdapter)
                dataAdapter.add(it.datas!!)
                dataAdapter.notifyDataSetChanged()
            } else {
                if (mBinding.etSearch.text.toString().trim().isNotEmpty()) {
                    mBinding.llNoData.visibility = View.VISIBLE
                    mBinding.recyclerList.visibility = View.GONE
                    mBinding.tvSearchName.text = mBinding.etSearch.text.toString().trim()
                } else {
                    mBinding.llNoData.visibility = View.GONE
                    mBinding.recyclerList.visibility = View.VISIBLE
                    dataAdapter.clear()
                    dataAdapter.add(it.datas!!)
                    dataAdapter.notifyDataSetChanged()
                }
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


    @SuppressLint("ServiceCast")
    override fun finish() {
        val imm =  getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(mBinding.root.windowToken,0)
        super.finish()
    }
}

/**
 * @Description 旅游投诉选择被投诉方ViewModel
 * @ClassName   ComplaintActivity
 * @Author      Huangxi
 * @Time        2020/2/28
 */
class ComplaintResourceViewModel : BaseViewModel() {
    /**
     * 资源数据
     */
    var resource = MutableLiveData<BaseResponse<ComplaintResourceBean>>()

    /**
     * 获取被投诉方资源
     */
    fun getComplaintResource(currPage: Int, keyword: String, resourceType: String, region: String) {
        mPresenter.value?.loading = true
        HomeRepository.service.complaintResourceSearch(currPage, keyword, resourceType, region)
            .excute(object : BaseObserver<ComplaintResourceBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ComplaintResourceBean>) {
                    resource.postValue(response)
                }

            })
    }
}
